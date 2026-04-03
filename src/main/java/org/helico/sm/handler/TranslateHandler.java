package org.helico.sm.handler;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.StringRequestEntity;
import org.apache.commons.httpclient.protocol.Protocol;
import org.apache.commons.httpclient.protocol.SecureProtocolSocketFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.helico.domain.*;
import org.helico.service.DictWordService;
import org.helico.service.JobService;
import org.helico.service.TranslationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.net.InetAddress;
import java.net.Socket;
import java.net.URLEncoder;
import java.security.cert.X509Certificate;
import java.text.MessageFormat;
import java.util.List;

@Component("translateHandler")
public class TranslateHandler extends AbstractHandler {

    private static final Logger LOG = LoggerFactory.getLogger(TranslateHandler.class);

    private static final int WORDS_PORTION = 32;

    @Autowired
    DictWordService dictWordService;

    @Autowired
    JobService jobService;

    @Autowired
    TranslationService transService;

    HttpClient httpClient;

    private String escape(String pattern) {
        String result = pattern;
        // Protect {n} MessageFormat placeholders from brace-escaping
        for (int i = 0; i < 10; i++) {
            result = result.replace("{" + i + "}", "\u0001" + i + "\u0002");
        }
        result = result.replace("{", "'{'").replace("}", "'}'");
        for (int i = 0; i < 10; i++) {
            result = result.replace("\u0001" + i + "\u0002", "{" + i + "}");
        }
        return result;
    }

    /**
     * Translates a single word using the given translator and stores the result.
     * Returns the stored translation value, or null if translation failed.
     */
    /**
     * Returns the raw JSON from Scriptorium for the given word, or null on failure.
     */
    public String fetchRaw(String wordValue, Long translatorId) {
        Translator translator = transService.getTranslator(translatorId);
        TranslatorProvider provider = translator.getProvider();
        try {
            if (httpClient == null) {
                httpClient = new HttpClient();
                initTrustAllSsl();
            }
            String encodedText = URLEncoder.encode(wordValue, "utf-8");
            MessageFormat req = new MessageFormat(escape(provider.getReqPattern()));
            String url = req.format(new String[]{encodedText, translator.getSrcLangId(), translator.getDestLangId()});
            for (int redirects = 0; redirects < 5; redirects++) {
                GetMethod get = new GetMethod(url);
                addCustomHeaders(get, provider);
                httpClient.executeMethod(get);
                int status = get.getStatusCode();
                if (status == 200) {
                    String body = new String(get.getResponseBody(), java.nio.charset.StandardCharsets.UTF_8);
                    get.releaseConnection();
                    return body;
                } else if (status >= 300 && status < 400) {
                    org.apache.commons.httpclient.Header loc = get.getResponseHeader("Location");
                    get.releaseConnection();
                    if (loc != null) url = loc.getValue(); else break;
                } else {
                    get.releaseConnection();
                    break;
                }
            }
        } catch (Exception e) {
            LOG.warn("fetchRaw failed for '{}': {}", wordValue, e.getMessage());
        }
        return null;
    }

    public String translatePhrase(String text, Long translatorId) {
        Translator translator = transService.getTranslator(translatorId);
        TranslatorProvider provider = translator.getProvider();
        MessageFormat req = new MessageFormat(escape(provider.getReqPattern()));
        MessageFormat res = new MessageFormat(escape(provider.getResPattern()));
        return fetchTranslation(text, provider, translator.getSrcLangId(), translator.getDestLangId(), req, res);
    }

    public String translateSingleWord(String wordValue, Long translatorId) {
        Translator translator = transService.getTranslator(translatorId);
        TranslatorProvider provider = translator.getProvider();
        MessageFormat req = new MessageFormat(escape(provider.getReqPattern()));
        MessageFormat res = new MessageFormat(escape(provider.getResPattern()));
        String result = fetchTranslation(wordValue, provider, translator.getSrcLangId(), translator.getDestLangId(), req, res);
        return cleanTranslation(result);
    }

    public void process(Object data, Job job) throws Exception {
        Translator translator = transService.getTranslator((Long) data);
        TranslatorProvider provider = translator.getProvider();
        Dict dict = dictService.findDict(job.getDictId());
        LOG.debug("dict={}", dict);

        jobService.setProgress(job.getId(), 0);

        MessageFormat req = new MessageFormat(escape(provider.getReqPattern()));
        MessageFormat res = new MessageFormat(escape(provider.getResPattern()));

        Long wordsNum = dictWordService.countWords(dict.getId());
        int offset = 0;
        while (wordsNum > offset) {
            // Check for cancellation before each batch
            if (!jobService.find(job.getId()).getActive()) {
                LOG.info("Job#{} cancelled — resetting dict#{} to PARSED", job.getId(), dict.getId());
                dictService.setStatus(dict.getId(), org.helico.domain.Dict.Status.PARSED);
                return;
            }
            List<DictWord> dictWords = dictWordService.getWords(dict.getId(), offset, WORDS_PORTION);
            for (DictWord dictWord : dictWords) {
                Word word = dictWord.getWord();
                if (!transService.isTranslated(word.getId(), translator.getId())) {
                    String translation = fetchTranslation(word.getValue(), provider, dict.getLangId(), translator.getDestLangId(), req, res);
                    if (translation != null) {
                        try {
                            transService.storeTranslation(word.getId(), translator.getId(), cleanTranslation(translation));
                        } catch (Exception e) {
                            LOG.error("Can not save translation for " + word.getValue() + " : " + translation, e);
                        }
                    }
                }
            }
            offset += WORDS_PORTION;
            jobService.setProgress(job.getId(), (int) ((offset * 100) / wordsNum));
        }
    }

    private void initTrustAllSsl() {
        try {
            SSLContext ctx = SSLContext.getInstance("TLS");
            ctx.init(null, new TrustManager[]{new X509TrustManager() {
                public X509Certificate[] getAcceptedIssuers() { return new X509Certificate[0]; }
                public void checkClientTrusted(X509Certificate[] c, String a) {}
                public void checkServerTrusted(X509Certificate[] c, String a) {}
            }}, null);
            final javax.net.ssl.SSLSocketFactory sf = ctx.getSocketFactory();
            Protocol.registerProtocol("https", new Protocol("https",
                new SecureProtocolSocketFactory() {
                    public Socket createSocket(String h, int p, InetAddress la, int lp) throws java.io.IOException {
                        return sf.createSocket(h, p, la, lp);
                    }
                    public Socket createSocket(String h, int p, InetAddress la, int lp,
                            org.apache.commons.httpclient.params.HttpConnectionParams params) throws java.io.IOException {
                        return sf.createSocket(h, p, la, lp);
                    }
                    public Socket createSocket(String h, int p) throws java.io.IOException {
                        return sf.createSocket(h, p);
                    }
                    public Socket createSocket(Socket s, String h, int p, boolean ac) throws java.io.IOException {
                        return sf.createSocket(s, h, p, ac);
                    }
                }, 443));
        } catch (Exception e) {
            LOG.warn("Could not register trust-all SSL factory: {}", e.getMessage());
        }
    }

    private String fetchTranslation(String text, TranslatorProvider provider, String srcLangId, String destLangId,
                                    MessageFormat reqFormat, MessageFormat resFormat) {
        String result = null;
        try {
            if (httpClient == null) {
                httpClient = new HttpClient();
                initTrustAllSsl();
            }
            String method = provider.getMethod();
            if (method == null || method.isEmpty()) {
                method = "GET"; // Default to GET for backward compatibility
            }

            String encodedText = URLEncoder.encode(text, "utf-8");

            if ("POST".equalsIgnoreCase(method)) {
                // POST: reqPattern = URL, requestBody = request body template, resPattern = response parser
                String url = reqFormat.format(new String[] {encodedText, srcLangId, destLangId});
                PostMethod postMethod = new PostMethod(url);

                // Add custom headers if configured
                addCustomHeaders(postMethod, provider);

                // Format the request body using requestBody field (dedicated field for POST body template)
                String bodyTemplate = provider.getRequestBody();
                if (bodyTemplate != null && !bodyTemplate.isEmpty()) {
                    MessageFormat bodyFormat = new MessageFormat(escape(bodyTemplate));
                    // POST body needs raw text (JSON-escaped), not URL-encoded
                    String jsonText = text.replace("\\", "\\\\").replace("\"", "\\\"");
                    String requestBody = bodyFormat.format(new String[] {jsonText, srcLangId, destLangId});

                    // Get content type, default to application/json if not specified
                    String contentType = provider.getContentType();
                    if (contentType == null || contentType.isEmpty()) {
                        contentType = "application/json";
                    }

                    // Get charset, default to UTF-8 if not specified
                    String charset = provider.getCharset();
                    if (charset == null || charset.isEmpty()) {
                        charset = "UTF-8";
                    }

                    postMethod.setRequestEntity(new StringRequestEntity(requestBody, contentType, charset));
                    LOG.debug("POST request to: " + url + " with Content-Type: " + contentType + "; charset=" + charset + " and body: " + requestBody);
                } else {
                    LOG.debug("POST request to: " + url + " (no body)");
                }

                httpClient.executeMethod(postMethod);

                if (postMethod.getStatusCode() == 200) {
                    String output = compactJson(postMethod.getResponseBodyAsString());
                    LOG.debug("POST response: " + output);
                    result = (String) resFormat.parse(output)[0];
                } else {
                    throw new Exception("Code " + postMethod.getStatusCode() + " " + postMethod.getResponseBodyAsString());
                }
                postMethod.releaseConnection();

            } else {
                // GET: reqPattern = URL template, resPattern = response parser
                // Follow redirects manually (commons-httpclient 3.x does not handle 308)
                String getUrl = reqFormat.format(new String[] {encodedText, srcLangId, destLangId});
                for (int redirects = 0; redirects < 5; redirects++) {
                    GetMethod getMethod = new GetMethod(getUrl);
                    addCustomHeaders(getMethod, provider);
                    LOG.debug("GET request to: " + getUrl);
                    httpClient.executeMethod(getMethod);
                    int status = getMethod.getStatusCode();
                    if (status == 200) {
                        String output = compactJson(new String(getMethod.getResponseBody(), java.nio.charset.StandardCharsets.UTF_8));
                        LOG.debug("GET response: " + output);
                        result = (String) resFormat.parse(output)[0];
                        getMethod.releaseConnection();
                        break;
                    } else if (status >= 300 && status < 400) {
                        org.apache.commons.httpclient.Header location = getMethod.getResponseHeader("Location");
                        getMethod.releaseConnection();
                        if (location != null) {
                            getUrl = location.getValue();
                            LOG.debug("Redirect {} → {}", status, getUrl);
                        } else {
                            throw new Exception("Redirect (Code " + status + ") without Location header");
                        }
                    } else {
                        String body = getMethod.getResponseBodyAsString();
                        getMethod.releaseConnection();
                        throw new Exception("Code " + status + " " + body);
                    }
                }
            }

        } catch (Exception e) {
            LOG.warn("No translation for '{}' via {} ({}): {}", text, provider.getTitle(), provider.getMethod(), e.getMessage());
        }
        return result;
    }

    /**
     * Strips wiki template markup and truncates the translation to a clean, short value.
     * Scriptorium returns Wiktionary article text verbatim, so the first sense may contain
     * {{template}} junk after the actual word(s). We keep only the text before the first
     * occurrence of "{{", "|}}", or a sentence-ending sequence, then trim and lowercase.
     */
    private String cleanTranslation(String raw) {
        if (raw == null) return null;
        String s = raw;
        // Strip everything from the first wiki template marker onward
        int junk = s.indexOf("{{");
        if (junk < 0) junk = s.indexOf("}}");
        if (junk < 0) junk = s.indexOf(".|");
        if (junk > 0) s = s.substring(0, junk);
        s = s.trim().toLowerCase();
        // Remove trailing punctuation left after stripping
        s = s.replaceAll("[,;.\\s]+$", "");
        return s.isEmpty() ? null : s;
    }

    /**
     * Removes pretty-print whitespace from JSON so MessageFormat patterns work
     * regardless of whether the API returns compact or indented JSON.
     * Only strips whitespace adjacent to JSON structural characters, leaving
     * string values untouched.
     */
    private String compactJson(String json) {
        if (json == null) return null;
        return json
            .replaceAll(":\\s+", ":")
            .replaceAll(",\\s+", ",")
            .replaceAll("\\[\\s+", "[")
            .replaceAll("\\{\\s+", "{")
            .replaceAll("\\s+}", "}")
            .replaceAll("\\s+]", "]");
    }

    /**
     * Parses and adds custom HTTP headers to the request.
     * Headers are stored in format: "Header-Name: value\nAnother-Header: value"
     *
     * @param httpMethod The HTTP method to add headers to
     * @param provider The translator provider containing header configuration
     */
    /**
     * Fetches audio bytes for a word from a Scriptorium-style provider.
     * Calls /api/v1/entries/{srcLang}/{word} to get the filename, then /api/v1/audio/{filename}.
     * Returns null if no audio is available.
     */
    public byte[] fetchAudio(String wordValue, Translator translator) {
        String reqPattern = translator.getProvider().getReqPattern();
        if (reqPattern == null || reqPattern.isEmpty()) return null;
        // Derive entries base URL from req_pattern: take everything up to /api/... path
        int apiIdx = reqPattern.indexOf("/api/");
        if (apiIdx < 0) return null;
        String baseUrl = reqPattern.substring(0, apiIdx);
        try {
            if (httpClient == null) { httpClient = new HttpClient(); initTrustAllSsl(); }
            String encodedWord = URLEncoder.encode(wordValue, "utf-8");
            GetMethod entryGet = new GetMethod(baseUrl + "/api/v1/entries/" + translator.getSrcLangId() + "/" + encodedWord);
            httpClient.executeMethod(entryGet);
            if (entryGet.getStatusCode() != 200) { entryGet.releaseConnection(); return null; }
            String body = new String(entryGet.getResponseBody(), java.nio.charset.StandardCharsets.UTF_8);
            entryGet.releaseConnection();
            String filename = extractAudioFilename(body);
            if (filename == null) return null;
            GetMethod audioGet = new GetMethod(baseUrl + "/api/v1/audio/" + URLEncoder.encode(filename, "utf-8"));
            httpClient.executeMethod(audioGet);
            if (audioGet.getStatusCode() != 200) { audioGet.releaseConnection(); return null; }
            byte[] data = audioGet.getResponseBody();
            audioGet.releaseConnection();
            return data;
        } catch (Exception e) {
            LOG.warn("fetchAudio failed for '{}': {}", wordValue, e.getMessage());
        }
        return null;
    }

    private String extractAudioFilename(String json) {
        int idx = json.indexOf("\"audio\":[");
        if (idx < 0) return null;
        int filenameIdx = json.indexOf("\"filename\":\"", idx);
        if (filenameIdx < 0) return null;
        int start = filenameIdx + 12;
        int end = json.indexOf("\"", start);
        return end > start ? json.substring(start, end) : null;
    }

    /** Replaces ${VAR_NAME} placeholders with environment variable values. */
    private String resolveEnvVars(String value) {
        if (value == null || !value.contains("${")) return value;
        java.util.regex.Matcher m = java.util.regex.Pattern.compile("\\$\\{([^}]+)\\}").matcher(value);
        StringBuffer sb = new StringBuffer();
        while (m.find()) {
            String envVal = System.getenv(m.group(1));
            m.appendReplacement(sb, envVal != null ? envVal : m.group(0));
        }
        m.appendTail(sb);
        return sb.toString();
    }

    private void addCustomHeaders(org.apache.commons.httpclient.HttpMethod httpMethod, TranslatorProvider provider) {
        String headers = provider.getHeaders();
        if (headers == null || headers.trim().isEmpty()) {
            return;
        }

        // Parse headers (format: "Header-Name: value\nAnother-Header: value")
        String[] headerLines = headers.split("\\r?\\n");
        for (String headerLine : headerLines) {
            headerLine = headerLine.trim();

            // Skip empty lines and comments
            if (headerLine.isEmpty() || headerLine.startsWith("#")) {
                continue;
            }

            // Parse "Header-Name: value"
            int colonIndex = headerLine.indexOf(':');
            if (colonIndex > 0) {
                String headerName = headerLine.substring(0, colonIndex).trim();
                String headerValue = resolveEnvVars(headerLine.substring(colonIndex + 1).trim());

                if (!headerName.isEmpty() && !headerValue.isEmpty()) {
                    httpMethod.addRequestHeader(headerName, headerValue);
                    LOG.debug("Added custom header: " + headerName);
                }
            }
        }
    }

}
