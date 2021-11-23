package org.helico.sm.handler;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.log4j.Logger;
import org.helico.sm.handler.TranslateHandler;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Test;

import org.apache.commons.httpclient.HttpClient;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

public class TranslateHandlerTest {

    private static final Logger LOG = Logger.getLogger(TranslateHandlerTest.class);

//    @Test
//    public void TestBingTranslator() {
//        TranslateHandler translateHandler = new TranslateHandler();
//        String translation = translateHandler.fetchTranslation("dog", null, "en", "ru");
//        assertEquals("собака", translation);
//    }
//
//    Authorization: Bearer <IAM-TOKEN>

    @Test
    public void testYandex() throws Exception {
        JSONObject request = new JSONObject();
        JSONArray texts = new JSONArray();
        texts.put("I saw a dream");
        texts.put("That was not all a dream");
        request.put("sourceLanguageCode", "en");
        request.put("targetLanguageCode", "bl");
        request.put("texts", texts);
        System.out.println(request.toString());
        HttpClient httpClient = new HttpClient();
        PostMethod postMethod = new PostMethod("https://translate.api.cloud.yandex.net/translate/v2/translate\n");
        postMethod.setRequestBody(request.toString());
        postMethod.addRequestHeader("Authorization", "Api-Key " + System.getenv("API_KEY"));
        httpClient.executeMethod(postMethod);
        String output = postMethod.getResponseBodyAsString();JSONObject obj = new JSONObject(output);
        JSONArray translations = obj.getJSONArray("translations");
        for (int i=0; i < translations.length(); i++) {
            String firstResult = translations.getJSONObject(i).getString("text");
            System.out.println(i + "\t" + new String (firstResult.getBytes(StandardCharsets.ISO_8859_1), StandardCharsets.UTF_8));
        }

    }

//    {
//        "sourceLanguageCode": "string",
//            "targetLanguageCode": "string",
//            "format": "string",
//            "texts": [
//        "string"
//  ],
//        "folderId": "string",
//            "model": "string",
//            "glossaryConfig": {
//        "glossaryData": {
//            "glossaryPairs": [
//            {
//                "sourceText": "string",
//                    "translatedText": "string"
//            }
//      ]
//        }
//    }
//    }


}
