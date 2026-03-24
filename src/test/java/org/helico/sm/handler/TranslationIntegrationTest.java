package org.helico.sm.handler;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import org.helico.domain.TranslatorProvider;
import org.junit.jupiter.api.*;

import java.lang.reflect.Method;
import java.text.MessageFormat;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration test for translation functionality.
 * Tests actual translation using a mock HTTP server (WireMock).
 *
 * This test verifies that "dog" translates to "собака" (Russian for "dog").
 */
public class TranslationIntegrationTest {

    private static WireMockServer wireMockServer;
    private TranslateHandler translateHandler;

    @BeforeAll
    public static void setupWireMock() {
        // Start WireMock server on port 8089
        wireMockServer = new WireMockServer(8089);
        wireMockServer.start();
        WireMock.configureFor("localhost", 8089);
    }

    @AfterAll
    public static void tearDown() {
        if (wireMockServer != null) {
            wireMockServer.stop();
        }
    }

    @BeforeEach
    public void setup() {
        wireMockServer.resetAll();
        translateHandler = new TranslateHandler();
    }

    @Test
    @DisplayName("Test GET request: 'dog' translates to 'собака'")
    public void testTranslation_Dog_To_Sobaka_GET() throws Exception {
        // Given: Mock translation API that returns "собака" for "dog"
        stubFor(get(urlPathEqualTo("/translate"))
            .withQueryParam("text", equalTo("dog"))
            .withQueryParam("from", equalTo("en"))
            .withQueryParam("to", equalTo("ru"))
            .willReturn(aResponse()
                .withBody("собака")
                .withStatus(200)));

        // And: A GET provider configured to use the mock server
        TranslatorProvider provider = new TranslatorProvider();
        provider.setId(1L);
        provider.setTitle("Test GET Provider");
        provider.setMethod("GET");
        provider.setHost("http://localhost:8089");
        provider.setReqPattern("{0}"); // Response parser - extract first element
        provider.setResPattern("http://localhost:8089/translate?text={0}&from={1}&to={2}"); // URL template

        // When: We call the translation method directly
        String translation = callFetchTranslation("dog", provider, "en", "ru");

        // Then: The translation should be "собака"
        assertEquals("собака", translation, "Translation of 'dog' should be 'собака'");

        // Verify the API was called
        verify(getRequestedFor(urlPathEqualTo("/translate"))
            .withQueryParam("text", equalTo("dog"))
            .withQueryParam("from", equalTo("en"))
            .withQueryParam("to", equalTo("ru")));
    }

    @Test
    @DisplayName("Test POST JSON request: 'dog' translates to 'собака'")
    public void testTranslation_Dog_To_Sobaka_POST_JSON() throws Exception {
        // Given: Mock translation API that accepts POST with JSON body
        stubFor(post(urlPathEqualTo("/translate"))
            .withRequestBody(containing("\"text\":\"dog\""))
            .withRequestBody(containing("\"source\":\"en\""))
            .withRequestBody(containing("\"target\":\"ru\""))
            .withHeader("Content-Type", containing("application/json"))
            .willReturn(aResponse()
                .withBody("собака")
                .withStatus(200)));

        // And: A POST provider configured to use the mock server
        TranslatorProvider provider = new TranslatorProvider();
        provider.setId(2L);
        provider.setTitle("Test POST JSON Provider");
        provider.setMethod("POST");
        provider.setHost("http://localhost:8089/translate");
        provider.setReqPattern("{0}"); // Response parser
        provider.setResPattern("{\"text\":\"{0}\",\"source\":\"{1}\",\"target\":\"{2}\"}"); // Request body template
        provider.setContentType("application/json");
        provider.setCharset("UTF-8");

        // When: We call the translation method
        String translation = callFetchTranslation("dog", provider, "en", "ru");

        // Then: The translation should be "собака"
        assertEquals("собака", translation, "Translation of 'dog' should be 'собака'");

        // Verify the API was called with correct POST body
        verify(postRequestedFor(urlPathEqualTo("/translate"))
            .withRequestBody(containing("dog"))
            .withRequestBody(containing("en"))
            .withRequestBody(containing("ru"))
            .withHeader("Content-Type", containing("application/json")));
    }

    @Test
    @DisplayName("Test POST form-encoded request: 'cat' translates to 'кошка'")
    public void testTranslation_Cat_To_Koshka_POST_FormEncoded() throws Exception {
        // Given: Mock API accepting form-encoded POST
        stubFor(post(urlPathEqualTo("/translate"))
            .withRequestBody(equalTo("text=cat&from=en&to=ru"))
            .withHeader("Content-Type", containing("application/x-www-form-urlencoded"))
            .willReturn(aResponse()
                .withBody("кошка")
                .withStatus(200)));

        // And: A POST form-encoded provider
        TranslatorProvider provider = new TranslatorProvider();
        provider.setId(3L);
        provider.setTitle("Test POST Form Provider");
        provider.setMethod("POST");
        provider.setHost("http://localhost:8089/translate");
        provider.setReqPattern("{0}");
        provider.setResPattern("text={0}&from={1}&to={2}");
        provider.setContentType("application/x-www-form-urlencoded");
        provider.setCharset("UTF-8");

        // When: We call the translation method
        String translation = callFetchTranslation("cat", provider, "en", "ru");

        // Then: The translation should be "кошка"
        assertEquals("кошка", translation, "Translation of 'cat' should be 'кошка'");
    }

    @Test
    @DisplayName("Test multiple translations in sequence")
    public void testMultipleTranslations() throws Exception {
        // Given: Mock API that returns different translations
        stubFor(get(urlPathMatching("/translate.*"))
            .withQueryParam("text", equalTo("dog"))
            .willReturn(aResponse().withBody("собака").withStatus(200)));

        stubFor(get(urlPathMatching("/translate.*"))
            .withQueryParam("text", equalTo("cat"))
            .willReturn(aResponse().withBody("кошка").withStatus(200)));

        stubFor(get(urlPathMatching("/translate.*"))
            .withQueryParam("text", equalTo("hello"))
            .willReturn(aResponse().withBody("привет").withStatus(200)));

        TranslatorProvider provider = new TranslatorProvider();
        provider.setMethod("GET");
        provider.setReqPattern("{0}");
        provider.setResPattern("http://localhost:8089/translate?text={0}&from={1}&to={2}");

        // When: We translate multiple words
        String dog = callFetchTranslation("dog", provider, "en", "ru");
        String cat = callFetchTranslation("cat", provider, "en", "ru");
        String hello = callFetchTranslation("hello", provider, "en", "ru");

        // Then: All translations should be correct
        assertEquals("собака", dog);
        assertEquals("кошка", cat);
        assertEquals("привет", hello);
    }

    @Test
    @DisplayName("Test API returns 404 - translation should be null")
    public void testTranslation_APIError_ReturnsNull() throws Exception {
        // Given: Mock API returns 404
        stubFor(get(urlPathEqualTo("/translate"))
            .willReturn(aResponse()
                .withStatus(404)
                .withBody("Not found")));

        TranslatorProvider provider = new TranslatorProvider();
        provider.setMethod("GET");
        provider.setReqPattern("{0}");
        provider.setResPattern("http://localhost:8089/translate?text={0}&from={1}&to={2}");

        // When: We try to translate
        String translation = callFetchTranslation("dog", provider, "en", "ru");

        // Then: Translation should be null due to error
        assertNull(translation, "Translation should be null when API returns error");
    }

    /**
     * Helper method to call the private fetchTranslation method via reflection
     */
    private String callFetchTranslation(String text, TranslatorProvider provider,
                                        String srcLang, String destLang) throws Exception {
        Method method = TranslateHandler.class.getDeclaredMethod(
            "fetchTranslation", String.class, TranslatorProvider.class, String.class, String.class);
        method.setAccessible(true);
        return (String) method.invoke(translateHandler, text, provider, srcLang, destLang);
    }
}
