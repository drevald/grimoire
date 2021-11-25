package org.helico.sm.handler;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import junit.framework.Assert;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.log4j.Logger;
import org.helico.sm.handler.TranslateHandler;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Ignore;
import org.junit.Test;

import org.apache.commons.httpclient.HttpClient;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

public class TranslateHandlerTest {

    private static final Logger LOG = Logger.getLogger(TranslateHandlerTest.class);
//    private static final HttpClient HTTP_CLIENT = new HttpClient();

//    @Test
//    public void TestBingTranslator() {
//        TranslateHandler translateHandler = new TranslateHandler();
//        String translation = translateHandler.fetchTranslation("dog", null, "en", "ru");
//        assertEquals("собака", translation);
//    }

//    @Ignore
//    @Test
//    public void testYandex() throws Exception {
//        JSONObject request = new JSONObject();
//        JSONArray texts = new JSONArray();
//        texts.put("I saw a dream");
//        texts.put("That was not all a dream");
//        request.put("sourceLanguageCode", "en");
//        request.put("targetLanguageCode", "ru");
//        request.put("texts", texts);
//        System.out.println(request.toString());
//        PostMethod postMethod = new PostMethod("https://translate.api.cloud.yandex.net/translate/v2/translate\n");
//        postMethod.setRequestBody(request.toString());
//        postMethod.addRequestHeader("Authorization", "Api-Key " + System.getenv("API_KEY"));
//        HTTP_CLIENT.executeMethod(postMethod);
//        String output = postMethod.getResponseBodyAsString();
//        JSONObject obj = new JSONObject(output);
//        JSONArray translations = obj.getJSONArray("translations");
//        for (int i=0; i < translations.length(); i++) {
//            String firstResult = translations.getJSONObject(i).getString("text");
//            System.out.println(i + "\t" + new String (firstResult.getBytes(StandardCharsets.ISO_8859_1), StandardCharsets.UTF_8));
//        }
//
//    }

//    @Test
//    public void testLibre() throws Exception {
//        JSONObject request = new JSONObject();
//        request.put("q", "dog");
//        request.put("source", "en");
//        request.put("target", "ru");
//        PostMethod postMethod = new PostMethod("https://translate.argosopentech.com/translate\n");
//        postMethod.setRequestBody(request.toString());
//        postMethod.addRequestHeader("Content-Type", "application/json");
//        HTTP_CLIENT.executeMethod(postMethod);
//        String response = postMethod.getResponseBodyAsString();
//        JSONObject obj = new JSONObject(response);
//        assertEquals(obj.getString("translatedText"), "собака");
//    }
//
//    @Ignore
//    @Test
//    public void testContDict() throws Exception {
//
//        PostMethod postMethod = new PostMethod("https://www.contdict.com/");
//        NameValuePair[] parameters = new NameValuePair[] {
//                new NameValuePair("from", "en"),
//                new NameValuePair("to", "ru"),
//                new NameValuePair("text", "dog")
//        };
//        postMethod.setRequestBody(parameters);
//        HTTP_CLIENT.executeMethod(postMethod);
//        String response = postMethod.getResponseBodyAsString();
//    }

}
