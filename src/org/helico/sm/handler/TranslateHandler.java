package org.helico.sm.handler;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.log4j.Logger;
import org.helico.domain.*;
import org.helico.service.DictWordService;
import org.helico.service.JobService;
import org.helico.service.TranslationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.net.URLEncoder;
import java.text.MessageFormat;
import java.util.List;


@Component("translateHandler")
public class TranslateHandler extends AbstractHandler {

    private static final Logger LOG = Logger.getLogger(TranslateHandler.class);

    private MessageFormat reqFormat;

    private MessageFormat resFormat;

    private static final int WORDS_PORTION = 32;

    @Autowired
    DictWordService dictWordService;

    @Autowired
    JobService jobService;

    @Autowired
    TranslationService transService;

    HttpClient httpClient;

    public void process(Object data, Job job) throws Exception {
        Long transProviderId = data==null ? 1 : (Long)data;
        TranslatorProvider provider = transService.getProvider(transProviderId);
        reqFormat=new MessageFormat(provider.getReqPattern());
        resFormat=new MessageFormat(provider.getResPattern());
        Dict dict = dictService.findDict(job.getDictId());
        LOG.debug("dict=" + dict);
        Long wordsNum = dictWordService.countWords(dict.getId());
        int offset = 0;
        while (wordsNum > offset) {
            List<DictWord> dictWords = dictWordService.getWords(dict.getId(), offset, WORDS_PORTION);
            for (DictWord dictWord : dictWords) {
                Word word = dictWord.getWord();
                if (!transService.isTranslated(word.getId(), 0L)) {
                    String translation = fetchTranslation(word.getValue(), provider, dict.getLangId(), "ru");
                    if (translation != null) {
						try {
	                        transService.storeTranslation(word.getId(), 0L, translation.toLowerCase());
						} catch (Exception e) {
							LOG.error("Can not save translation for " + word.getValue() + " : " + translation, e);
						}
                    }
                }
            }
            offset += WORDS_PORTION;
            jobService.setProgress(job.getId(), (int)((offset * 100)/wordsNum));
        }

    }

    private String fetchTranslation(String text, TranslatorProvider provider, String srcLangId, String destLangId) {
        String result = null;
        try {
            if (httpClient == null) {
                httpClient = new HttpClient();
            }
//            GetMethod getMethod = new GetMethod(
//                    String.format(provider.getReqPattern(), new String[] {text, srcLangId, destLangId}));
            GetMethod getMethod = new GetMethod(resFormat.format(new String[] {URLEncoder.encode(text), srcLangId, destLangId}));
//            getMethod.addRequestHeader("User-Agent","Opera/9.80 (Windows NT 6.1; WOW64; U; ru) Presto/2.10.229 Version/11.64");
            httpClient.executeMethod(getMethod);
            String output = getMethod.getResponseBodyAsString();
            result = (String)reqFormat.parse(output)[0];
        } catch (Exception e) {
            LOG.error("Can not get translation", e);
        }
        return result;
    }

//    private String fetchTranslation(String text, String srcLangId, String destLangId) {
//        String result = null;
//        try {
//            if (httpClient == null) {
//                httpClient = new HttpClient();
//            }
//            GetMethod getMethod = new GetMethod(BING_OUTPUT_FORMAT.format(new String[] {text, srcLangId, destLangId}));
//            httpClient.executeMethod(getMethod);
//            String output = getMethod.getResponseBodyAsString();
//            result = (String)BING_FORMAT.parse(output)[0];
//        } catch (Exception e) {
//            LOG.error("Can not get translation", e);
//        }
//        return result;
//    }

}
