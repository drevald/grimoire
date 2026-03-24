package org.helico.sm.handler;

import com.sun.net.httpserver.HttpServer;
import org.helico.domain.*;
import org.helico.service.DictService;
import org.helico.service.DictWordService;
import org.helico.service.JobService;
import org.helico.service.TranslationService;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.Method;
import java.net.InetSocketAddress;
import java.text.MessageFormat;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Tests for the Scriptorium translation provider.
 *
 * Scriptorium uses the standard HTTP provider mechanism:
 *   req_pattern = http://scriptorium.dobby/api/v1/translate/{1}/{0}/{2}
 *   res_pattern = "translations":[{"word":"{0}",{4}]
 *
 * Where {0}=word, {1}=srcLang, {2}=destLang.
 *
 * Test case: Swedish word "inte" (not) → English "not".
 */
@ExtendWith(MockitoExtension.class)
public class ScriptoriumProviderTest {

    static final String REQ_PATTERN = "http://scriptorium.dobby/api/v1/translate/{1}/{0}/{2}";
    // {5} captures everything before "translations" so parse() can anchor at position 0
    static final String RES_PATTERN = "{5}\"translations\":[{\"word\":\"{0}\",{4}]";

    @Mock DictWordService dictWordService;
    @Mock JobService jobService;
    @Mock TranslationService transService;
    @Mock DictService dictService;

    @InjectMocks
    TranslateHandler handler;

    // ─── Pattern unit tests (no HTTP) ─────────────────────────────────────────

    @Test
    @DisplayName("req_pattern builds correct Scriptorium URL: sv/inte/en")
    void testReqPattern_BuildsCorrectUrl() throws Exception {
        String escaped = callEscape(REQ_PATTERN);
        MessageFormat fmt = new MessageFormat(escaped);
        String url = fmt.format(new String[]{"inte", "sv", "en"});
        assertEquals("http://scriptorium.dobby/api/v1/translate/sv/inte/en", url);
    }

    @Test
    @DisplayName("res_pattern parses 'not' from Scriptorium JSON for 'inte'")
    void testResPattern_ParsesTranslatedWord() throws Exception {
        String escaped = callEscape(RES_PATTERN);
        MessageFormat fmt = new MessageFormat(escaped);
        Object[] parsed = fmt.parse(scriptorium("inte", "sv", "en", "not"));
        assertEquals("not", parsed[0]);
    }

    @Test
    @DisplayName("res_pattern extracts first translation when multiple exist")
    void testResPattern_MultipleTranslations_TakesFirst() throws Exception {
        String escaped = callEscape(RES_PATTERN);
        MessageFormat fmt = new MessageFormat(escaped);

        String response =
            "{\"word\":\"inte\",\"srcLang\":\"sv\",\"destLang\":\"en\"," +
            "\"translations\":[{\"word\":\"not\",\"romanized\":null,\"senseRef\":null}," +
            "{\"word\":\"no\",\"romanized\":null,\"senseRef\":null}]}";

        Object[] parsed = fmt.parse(response);
        assertEquals("not", parsed[0], "Should extract the first translation");
    }

    @Test
    @DisplayName("escape() preserves {n} placeholders")
    void testEscape_PreservesPlaceholders() throws Exception {
        String escaped = callEscape(RES_PATTERN);
        assertTrue(escaped.contains("{0}"), "Placeholder {0} must survive escaping");
        assertTrue(escaped.contains("{4}"), "Placeholder {4} must survive escaping");
        // The literal { in [{ must be escaped as '{'
        assertTrue(escaped.contains("'{'"), "Literal brace must be escaped as '{'");
    }

    @Test
    @DisplayName("escape() does not break MessageFormat construction")
    void testEscape_DoesNotBreakMessageFormat() throws Exception {
        assertDoesNotThrow(() -> new MessageFormat(callEscape(REQ_PATTERN)));
        assertDoesNotThrow(() -> new MessageFormat(callEscape(RES_PATTERN)));
    }

    // ─── End-to-end tests with a minimal Java HTTP server ────────────────────

    private HttpServer httpServer;
    private int port;
    private AtomicReference<String> lastRequestPath;

    @BeforeEach
    void startHttpServer() throws Exception {
        lastRequestPath = new AtomicReference<>("");
        httpServer = HttpServer.create(new InetSocketAddress(0), 0);
        port = httpServer.getAddress().getPort();

        // Stub: GET /api/v1/translate/sv/inte/en → Scriptorium JSON
        httpServer.createContext("/api/v1/translate/sv/inte/en", exchange -> {
            lastRequestPath.set(exchange.getRequestURI().getPath());
            byte[] body = scriptorium("inte", "sv", "en", "not").getBytes("UTF-8");
            exchange.getResponseHeaders().add("Content-Type", "application/json");
            exchange.sendResponseHeaders(200, body.length);
            exchange.getResponseBody().write(body);
            exchange.getResponseBody().close();
        });

        // Stub: everything else → 404
        httpServer.createContext("/", exchange -> {
            lastRequestPath.set(exchange.getRequestURI().getPath());
            exchange.sendResponseHeaders(404, -1);
            exchange.getResponseBody().close();
        });

        httpServer.start();
    }

    @AfterEach
    void stopHttpServer() {
        if (httpServer != null) httpServer.stop(0);
    }

    @Test
    @DisplayName("process() translates sv 'inte' to en 'not' via Scriptorium pattern")
    void testProcess_Inte_TranslatesTo_Not() throws Exception {
        TranslatorProvider provider = scriptoriumProvider("http://localhost:" + port);
        Translator translator = translator(provider, "sv", "en");
        Dict dict = dict(1L, "sv");
        Job job = job(1L, 1L);
        Word word = word(10L, "inte", "sv");

        when(transService.getTranslator(99L)).thenReturn(translator);
        when(dictService.findDict(1L)).thenReturn(dict);
        when(dictWordService.countWords(1L)).thenReturn(1L);
        when(dictWordService.getWords(eq(1L), eq(0), anyInt())).thenReturn(List.of(dictWord(word)));
        when(transService.isTranslated(eq(10L), eq(1L))).thenReturn(false);

        handler.process(99L, job);

        verify(transService).storeTranslation(eq(10L), eq(1L), eq("not"));
        assertEquals("/api/v1/translate/sv/inte/en", lastRequestPath.get(),
            "Handler must call the correct Scriptorium endpoint");
    }

    @Test
    @DisplayName("process() skips 'inte' when it is already translated")
    void testProcess_SkipsAlreadyTranslated() throws Exception {
        TranslatorProvider provider = scriptoriumProvider("http://localhost:" + port);
        Translator translator = translator(provider, "sv", "en");
        Dict dict = dict(1L, "sv");
        Job job = job(1L, 1L);
        Word word = word(10L, "inte", "sv");

        when(transService.getTranslator(99L)).thenReturn(translator);
        when(dictService.findDict(1L)).thenReturn(dict);
        when(dictWordService.countWords(1L)).thenReturn(1L);
        when(dictWordService.getWords(eq(1L), eq(0), anyInt())).thenReturn(List.of(dictWord(word)));
        when(transService.isTranslated(eq(10L), eq(1L))).thenReturn(true);

        handler.process(99L, job);

        verify(transService, never()).storeTranslation(anyLong(), anyLong(), anyString());
        assertEquals("", lastRequestPath.get(), "No HTTP request should be made for already-translated words");
    }

    @Test
    @DisplayName("process() handles 404 from Scriptorium — word stays untranslated")
    void testProcess_ScriptoriumReturns404_WordSkipped() throws Exception {
        // Use a path the server doesn't know → falls through to the 404 handler
        TranslatorProvider provider = scriptoriumProvider("http://localhost:" + port);
        provider.setReqPattern("http://localhost:" + port + "/no-such-path/{1}/{0}/{2}");

        Translator translator = translator(provider, "sv", "en");
        Dict dict = dict(1L, "sv");
        Job job = job(1L, 1L);
        Word word = word(10L, "inte", "sv");

        when(transService.getTranslator(99L)).thenReturn(translator);
        when(dictService.findDict(1L)).thenReturn(dict);
        when(dictWordService.countWords(1L)).thenReturn(1L);
        when(dictWordService.getWords(eq(1L), eq(0), anyInt())).thenReturn(List.of(dictWord(word)));
        when(transService.isTranslated(anyLong(), anyLong())).thenReturn(false);

        handler.process(99L, job);

        verify(transService, never()).storeTranslation(anyLong(), anyLong(), anyString());
    }

    // ─── Helpers ─────────────────────────────────────────────────────────────

    /** Calls the private escape() method via reflection. */
    private String callEscape(String pattern) throws Exception {
        Method m = TranslateHandler.class.getDeclaredMethod("escape", String.class);
        m.setAccessible(true);
        return (String) m.invoke(handler, pattern);
    }

    /** Builds a minimal Scriptorium-style JSON response. */
    private static String scriptorium(String word, String src, String dest, String translation) {
        return "{\"word\":\"" + word + "\",\"srcLang\":\"" + src + "\",\"destLang\":\"" + dest + "\"," +
               "\"translations\":[{\"word\":\"" + translation + "\",\"romanized\":null,\"senseRef\":null}]}";
    }

    private TranslatorProvider scriptoriumProvider(String baseUrl) {
        TranslatorProvider p = new TranslatorProvider();
        p.setId(5L);
        p.setTitle("Scriptorium");
        p.setHost("scriptorium.dobby");
        p.setMethod("GET");
        p.setReqPattern(baseUrl + "/api/v1/translate/{1}/{0}/{2}");
        p.setResPattern(RES_PATTERN);
        return p;
    }

    private Translator translator(TranslatorProvider provider, String src, String dest) {
        Translator t = new Translator();
        t.setId(1L);
        t.setProvider(provider);
        t.setSrcLangId(src);
        t.setDestLangId(dest);
        return t;
    }

    private Dict dict(Long id, String langId) {
        Dict d = new Dict();
        d.setId(id);
        d.setLangId(langId);
        return d;
    }

    private Job job(Long id, Long dictId) {
        Job j = new Job();
        j.setId(id);
        j.setDictId(dictId);
        return j;
    }

    private Word word(Long id, String value, String langId) {
        Word w = new Word();
        w.setId(id);
        w.setValue(value);
        w.setLangId(langId);
        return w;
    }

    private DictWord dictWord(Word word) {
        DictWord dw = new DictWord();
        dw.setWord(word);
        return dw;
    }
}
