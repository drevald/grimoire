package org.helico.integration;

import org.helico.domain.Translator;
import org.helico.domain.TranslatorProvider;
import org.helico.dao.TranslatorProviderDAO;
import org.helico.sm.handler.TranslateHandler;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.Method;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Real integration test that connects to the actual database
 * and tests translation providers configured in the system.
 *
 * This test iterates through all en->ru translators in the database
 * and verifies that "dog" translates to "собака".
 *
 * To run this test, ensure:
 * 1. Database is running
 * 2. Translation providers are configured
 * 3. Set environment variable: ENABLE_REAL_TRANSLATION_TESTS=true
 */
@SpringBootTest
@ActiveProfiles("test")
@EnabledIfEnvironmentVariable(named = "ENABLE_REAL_TRANSLATION_TESTS", matches = "true")
public class RealTranslationProviderTest {

    @Autowired
    private TranslatorProviderDAO translatorProviderDAO;

    @Autowired
    private TranslateHandler translateHandler;

    @Test
    @DisplayName("Test real database providers: 'dog' -> 'собака' for en->ru translators")
    @Transactional(readOnly = true)
    public void testRealProviders_Dog_To_Sobaka() throws Exception {
        // Given: Get all en->ru translators from database
        List<Translator> translators = translatorProviderDAO.listTranslators("en");

        assertNotNull(translators, "Translators list should not be null");
        assertFalse(translators.isEmpty(), "At least one translator should be configured in the database");

        System.out.println("Found " + translators.size() + " translator(s) for 'en' language");

        // Filter for Russian target language
        List<Translator> enToRuTranslators = translators.stream()
            .filter(t -> "ru".equals(t.getDestLangId()))
            .toList();

        assertTrue(!enToRuTranslators.isEmpty(),
            "At least one en->ru translator should be configured. Please add a translator in the database.");

        System.out.println("Found " + enToRuTranslators.size() + " en->ru translator(s)");

        // When/Then: Test each en->ru translator
        int successCount = 0;
        int failCount = 0;
        StringBuilder results = new StringBuilder();
        results.append("\n=== Translation Test Results ===\n");

        for (Translator translator : enToRuTranslators) {
            TranslatorProvider provider = translator.getProvider();

            System.out.println("\n--- Testing Provider: " + provider.getTitle() + " (ID: " + provider.getId() + ") ---");
            System.out.println("Method: " + provider.getMethod());
            System.out.println("Host: " + provider.getHost());
            System.out.println("Request Pattern: " + provider.getReqPattern());
            System.out.println("Response Pattern: " + provider.getResPattern());
            System.out.println("Content-Type: " + provider.getContentType());
            System.out.println("Charset: " + provider.getCharset());

            try {
                // Call the fetchTranslation method using reflection
                String translation = callFetchTranslation("dog", provider, "en", "ru");

                System.out.println("Translation result: '" + translation + "'");

                results.append(String.format("Provider '%s' (ID:%d, Method:%s): ",
                    provider.getTitle(), provider.getId(), provider.getMethod()));

                if (translation != null && translation.trim().toLowerCase().equals("собака")) {
                    results.append("✅ SUCCESS - 'dog' -> '").append(translation).append("'\n");
                    successCount++;
                    System.out.println("✅ TEST PASSED");
                } else if (translation != null) {
                    results.append("⚠️  UNEXPECTED - 'dog' -> '").append(translation)
                           .append("' (expected 'собака')\n");
                    failCount++;
                    System.out.println("⚠️  TEST FAILED - Expected 'собака', got: '" + translation + "'");
                } else {
                    results.append("❌ FAILED - Translation returned null\n");
                    failCount++;
                    System.out.println("❌ TEST FAILED - Translation returned null");
                }

            } catch (Exception e) {
                results.append("❌ ERROR - ").append(e.getMessage()).append("\n");
                failCount++;
                System.err.println("❌ TEST ERROR: " + e.getMessage());
                e.printStackTrace();
            }
        }

        results.append("\n=== Summary ===\n");
        results.append("Total providers tested: ").append(enToRuTranslators.size()).append("\n");
        results.append("Successful: ").append(successCount).append("\n");
        results.append("Failed: ").append(failCount).append("\n");

        System.out.println(results.toString());

        // Assert that at least one provider successfully translated
        assertTrue(successCount > 0,
            "At least one provider should successfully translate 'dog' to 'собака'. " +
            "Results:\n" + results);
    }

    @Test
    @DisplayName("List all providers in database")
    @Transactional(readOnly = true)
    public void testListAllProviders() {
        // Given: Get all providers from database
        List<TranslatorProvider> providers = translatorProviderDAO.listProviders();

        System.out.println("\n=== All Translation Providers ===");
        System.out.println("Total providers: " + providers.size());
        System.out.println();

        if (providers.isEmpty()) {
            System.out.println("⚠️  No providers configured in database!");
            System.out.println("Please add at least one provider using:");
            System.out.println("1. Web UI: http://localhost:8081/admin/providers/new");
            System.out.println("2. SQL: INSERT INTO translator_provider ...");
        } else {
            for (TranslatorProvider provider : providers) {
                System.out.println("Provider ID: " + provider.getId());
                System.out.println("  Title: " + provider.getTitle());
                System.out.println("  Method: " + provider.getMethod());
                System.out.println("  Host: " + provider.getHost());
                System.out.println("  Content-Type: " + provider.getContentType());
                System.out.println("  Charset: " + provider.getCharset());
                System.out.println();
            }
        }

        // This test always passes, it's just for information
        assertTrue(true);
    }

    @Test
    @DisplayName("List all en->ru translators in database")
    @Transactional(readOnly = true)
    public void testListEnToRuTranslators() {
        // Given: Get all en->ru translators
        List<Translator> translators = translatorProviderDAO.listTranslators("en");

        List<Translator> enToRuTranslators = translators.stream()
            .filter(t -> "ru".equals(t.getDestLangId()))
            .toList();

        System.out.println("\n=== English to Russian Translators ===");
        System.out.println("Total en->ru translators: " + enToRuTranslators.size());
        System.out.println();

        if (enToRuTranslators.isEmpty()) {
            System.out.println("⚠️  No en->ru translators configured!");
            System.out.println("Please add at least one translator:");
            System.out.println("INSERT INTO translator (id, service_id, src_lang_id, dest_lang_id)");
            System.out.println("VALUES (1, 1, 'en', 'ru');");
        } else {
            for (Translator translator : enToRuTranslators) {
                System.out.println("Translator ID: " + translator.getId());
                System.out.println("  Provider: " + translator.getProvider().getTitle());
                System.out.println("  Source Lang: " + translator.getSrcLangId());
                System.out.println("  Target Lang: " + translator.getDestLangId());
                System.out.println();
            }
        }

        // This test always passes, it's just for information
        assertTrue(true);
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
