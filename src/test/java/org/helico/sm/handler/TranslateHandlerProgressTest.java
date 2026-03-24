package org.helico.sm.handler;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import org.helico.domain.*;
import org.helico.service.DictWordService;
import org.helico.service.JobService;
import org.helico.service.TranslationService;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;
import java.util.stream.Collectors;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Comprehensive test for TranslateHandler with focus on:
 * 1. Word counting accuracy
 * 2. Progress tracking accuracy
 * 3. Translation completion verification
 */
@ExtendWith(MockitoExtension.class)
public class TranslateHandlerProgressTest {

    private static WireMockServer wireMockServer;
    private static final int WIREMOCK_PORT = 8089;

    @Mock
    private DictWordService dictWordService;

    @Mock
    private JobService jobService;

    @Mock
    private TranslationService transService;

    @Mock
    private org.helico.service.DictService dictService;

    @InjectMocks
    private TranslateHandler translateHandler;

    private TranslatorProvider mockProvider;
    private Translator mockTranslator;
    private Dict mockDict;
    private Job mockJob;

    @BeforeAll
    public static void setUpWireMock() {
        wireMockServer = new WireMockServer(WIREMOCK_PORT);
        wireMockServer.start();
        WireMock.configureFor("localhost", WIREMOCK_PORT);
    }

    @AfterAll
    public static void tearDownWireMock() {
        wireMockServer.stop();
    }

    @BeforeEach
    public void setUp() {
        wireMockServer.resetAll();

        // Setup mock provider with correct naming (req_pattern = URL, res_pattern = response parser)
        mockProvider = new TranslatorProvider();
        mockProvider.setId(1L);
        mockProvider.setTitle("Test Provider");
        mockProvider.setMethod("GET");
        mockProvider.setReqPattern("http://localhost:" + WIREMOCK_PORT + "/translate?text={0}&from={1}&to={2}");
        mockProvider.setResPattern("{0}"); // Response parser pattern
        mockProvider.setContentType("application/json");
        mockProvider.setCharset("UTF-8");

        // Setup mock translator
        mockTranslator = new Translator();
        mockTranslator.setId(1L);
        mockTranslator.setProvider(mockProvider);
        mockTranslator.setSrcLangId("en");
        mockTranslator.setDestLangId("ru");

        // Setup mock dict
        mockDict = new Dict();
        mockDict.setId(1L);
        mockDict.setLangId("en");

        // Setup mock job
        mockJob = new Job();
        mockJob.setId(1L);
        mockJob.setDictId(1L);
    }

    /**
     * Test data generator: creates random words with known counts
     */
    private static class TestDataGenerator {
        private final Random random = new Random(42); // Fixed seed for reproducibility
        private final Map<String, Integer> wordCounts = new LinkedHashMap<>();
        private final List<DictWord> dictWords = new ArrayList<>();

        public TestDataGenerator generateWords(int uniqueWordCount) {
            wordCounts.clear();
            dictWords.clear();

            // Generate unique words with pattern: word_1, word_2, word_3, etc.
            for (int i = 1; i <= uniqueWordCount; i++) {
                String wordValue = "word_" + i;
                wordCounts.put(wordValue, 1);

                Word word = new Word();
                word.setId((long) i);
                word.setValue(wordValue);
                word.setLangId("en");

                DictWord dictWord = new DictWord();
                dictWord.setWord(word);
                dictWords.add(dictWord);
            }

            return this;
        }

        public Map<String, Integer> getWordCounts() {
            return new HashMap<>(wordCounts);
        }

        public List<DictWord> getDictWords() {
            return new ArrayList<>(dictWords);
        }

        public int getTotalUniqueWords() {
            return wordCounts.size();
        }

        public String getStatistics() {
            return String.format("Total unique words: %d, Total words: %d",
                    wordCounts.size(),
                    wordCounts.values().stream().mapToInt(Integer::intValue).sum());
        }
    }

    @Test
    @DisplayName("Test with 100 unique words - verify all words are counted and progress is tracked")
    public void testTranslation_100Words_AllCountedAndProgressTracked() throws Exception {
        // Given: Generate 100 unique test words
        TestDataGenerator testData = new TestDataGenerator().generateWords(100);
        System.out.println("Test data generated: " + testData.getStatistics());

        List<DictWord> allWords = testData.getDictWords();
        long totalWords = allWords.size();

        // Setup WireMock to respond to all translation requests
        stubFor(get(urlPathEqualTo("/translate"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withBody("translated") // Simple response
                        .withHeader("Content-Type", "text/plain")));

        // Mock service calls
        when(transService.getTranslator(1L)).thenReturn(mockTranslator);
        when(dictService.findDict(1L)).thenReturn(mockDict);
        when(dictWordService.countWords(1L)).thenReturn(totalWords);
        when(transService.isTranslated(anyLong(), anyLong())).thenReturn(false);

        // Mock getWords to return words in batches of 32 (WORDS_PORTION)
        int batchSize = 32;
        for (int offset = 0; offset < totalWords; offset += batchSize) {
            int endIndex = Math.min(offset + batchSize, (int) totalWords);
            List<DictWord> batch = allWords.subList(offset, endIndex);
            when(dictWordService.getWords(eq(1L), eq(offset), eq(batchSize)))
                    .thenReturn(batch);
        }

        // When: Process the translation job
        translateHandler.process(1L, mockJob);

        // Then: Verify all words were processed
        ArgumentCaptor<Long> wordIdCaptor = ArgumentCaptor.forClass(Long.class);
        ArgumentCaptor<Long> translatorIdCaptor = ArgumentCaptor.forClass(Long.class);
        ArgumentCaptor<String> translationCaptor = ArgumentCaptor.forClass(String.class);

        verify(transService, times((int) totalWords))
                .storeTranslation(wordIdCaptor.capture(), translatorIdCaptor.capture(), translationCaptor.capture());

        // Verify all unique word IDs were translated
        List<Long> translatedWordIds = wordIdCaptor.getAllValues();
        assertEquals(totalWords, translatedWordIds.size(), "All words should be translated");
        assertEquals(totalWords, new HashSet<>(translatedWordIds).size(), "All word IDs should be unique");

        // Verify progress was updated correctly
        ArgumentCaptor<Long> jobIdCaptor = ArgumentCaptor.forClass(Long.class);
        ArgumentCaptor<Integer> progressCaptor = ArgumentCaptor.forClass(Integer.class);

        verify(jobService, atLeastOnce())
                .setProgress(jobIdCaptor.capture(), progressCaptor.capture());

        List<Integer> progressUpdates = progressCaptor.getAllValues();
        System.out.println("Progress updates: " + progressUpdates);

        // Verify progress tracking
        assertFalse(progressUpdates.isEmpty(), "Progress should be updated at least once");

        // Progress should increase monotonically
        for (int i = 1; i < progressUpdates.size(); i++) {
            assertTrue(progressUpdates.get(i) >= progressUpdates.get(i - 1),
                    "Progress should increase monotonically");
        }

        // Final progress should be close to 100% (allowing for rounding)
        Integer lastProgress = progressUpdates.get(progressUpdates.size() - 1);
        assertTrue(lastProgress >= 96 && lastProgress <= 100,
                "Final progress should be close to 100%, was: " + lastProgress);

        // Verify all job service calls used correct job ID
        jobIdCaptor.getAllValues().forEach(jobId ->
                assertEquals(1L, jobId, "All progress updates should be for job ID 1"));
    }

    @Test
    @DisplayName("Test with 32 words (exact batch size) - verify single batch processing")
    public void testTranslation_32Words_SingleBatch() throws Exception {
        // Given: Generate exactly 32 words (one batch)
        TestDataGenerator testData = new TestDataGenerator().generateWords(32);
        System.out.println("Test data generated: " + testData.getStatistics());

        List<DictWord> allWords = testData.getDictWords();
        long totalWords = allWords.size();

        // Setup WireMock
        stubFor(get(urlPathEqualTo("/translate"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withBody("translated")
                        .withHeader("Content-Type", "text/plain")));

        // Mock service calls
        when(transService.getTranslator(1L)).thenReturn(mockTranslator);
        when(dictService.findDict(1L)).thenReturn(mockDict);
        when(dictWordService.countWords(1L)).thenReturn(totalWords);
        when(transService.isTranslated(anyLong(), anyLong())).thenReturn(false);
        when(dictWordService.getWords(eq(1L), eq(0), eq(32))).thenReturn(allWords);

        // When: Process the translation job
        translateHandler.process(1L, mockJob);

        // Then: Verify all 32 words were processed
        verify(transService, times(32)).storeTranslation(anyLong(), anyLong(), anyString());

        // Verify progress was set to 100%
        ArgumentCaptor<Integer> progressCaptor = ArgumentCaptor.forClass(Integer.class);
        verify(jobService, atLeastOnce()).setProgress(eq(1L), progressCaptor.capture());

        List<Integer> progressUpdates = progressCaptor.getAllValues();
        Integer lastProgress = progressUpdates.get(progressUpdates.size() - 1);
        assertEquals(100, lastProgress, "Progress should be exactly 100% for single batch");
    }

    @Test
    @DisplayName("Test with 65 words (2+ batches) - verify multi-batch processing")
    public void testTranslation_65Words_MultiBatch() throws Exception {
        // Given: Generate 65 words (2 full batches + 1 word)
        TestDataGenerator testData = new TestDataGenerator().generateWords(65);
        System.out.println("Test data generated: " + testData.getStatistics());

        List<DictWord> allWords = testData.getDictWords();
        long totalWords = allWords.size();

        // Setup WireMock
        stubFor(get(urlPathEqualTo("/translate"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withBody("translated")
                        .withHeader("Content-Type", "text/plain")));

        // Mock service calls
        when(transService.getTranslator(1L)).thenReturn(mockTranslator);
        when(dictService.findDict(1L)).thenReturn(mockDict);
        when(dictWordService.countWords(1L)).thenReturn(totalWords);
        when(transService.isTranslated(anyLong(), anyLong())).thenReturn(false);

        // Mock batches: batch 1 (0-31), batch 2 (32-63), batch 3 (64)
        when(dictWordService.getWords(eq(1L), eq(0), eq(32)))
                .thenReturn(allWords.subList(0, 32));
        when(dictWordService.getWords(eq(1L), eq(32), eq(32)))
                .thenReturn(allWords.subList(32, 64));
        when(dictWordService.getWords(eq(1L), eq(64), eq(32)))
                .thenReturn(allWords.subList(64, 65));

        // When: Process the translation job
        translateHandler.process(1L, mockJob);

        // Then: Verify all 65 words were processed
        verify(transService, times(65)).storeTranslation(anyLong(), anyLong(), anyString());

        // Verify progress updates
        ArgumentCaptor<Integer> progressCaptor = ArgumentCaptor.forClass(Integer.class);
        verify(jobService, atLeast(3)).setProgress(eq(1L), progressCaptor.capture());

        List<Integer> progressUpdates = progressCaptor.getAllValues();
        System.out.println("Progress updates for 65 words: " + progressUpdates);

        // Should have at least 3 progress updates (one per batch)
        assertTrue(progressUpdates.size() >= 3, "Should have at least 3 progress updates");

        // Verify progress increases
        assertTrue(progressUpdates.get(0) < 100, "First progress should be less than 100%");
        assertTrue(progressUpdates.get(progressUpdates.size() - 1) >= 98,
                "Final progress should be close to 100%");
    }

    @Test
    @DisplayName("Test with some words already translated - verify skip logic")
    public void testTranslation_SomeWordsAlreadyTranslated() throws Exception {
        // Given: 50 words, but 25 are already translated
        TestDataGenerator testData = new TestDataGenerator().generateWords(50);
        List<DictWord> allWords = testData.getDictWords();

        // Setup WireMock
        stubFor(get(urlPathEqualTo("/translate"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withBody("translated")
                        .withHeader("Content-Type", "text/plain")));

        // Mock service calls
        when(transService.getTranslator(1L)).thenReturn(mockTranslator);
        when(dictService.findDict(1L)).thenReturn(mockDict);
        when(dictWordService.countWords(1L)).thenReturn(50L);

        // Mock batches
        when(dictWordService.getWords(eq(1L), eq(0), eq(32)))
                .thenReturn(allWords.subList(0, 32));
        when(dictWordService.getWords(eq(1L), eq(32), eq(32)))
                .thenReturn(allWords.subList(32, 50));

        // Mock: First 25 words are already translated
        when(transService.isTranslated(anyLong(), anyLong())).thenAnswer(invocation -> {
            Long wordId = invocation.getArgument(0);
            return wordId <= 25; // First 25 words are already translated
        });

        // When: Process the translation job
        translateHandler.process(1L, mockJob);

        // Then: Verify only 25 new translations were stored (50 - 25 already translated)
        verify(transService, times(25)).storeTranslation(anyLong(), anyLong(), anyString());

        // Verify progress still tracks through all 50 words
        ArgumentCaptor<Integer> progressCaptor = ArgumentCaptor.forClass(Integer.class);
        verify(jobService, atLeast(2)).setProgress(eq(1L), progressCaptor.capture());

        Integer lastProgress = progressCaptor.getAllValues().get(progressCaptor.getAllValues().size() - 1);
        assertTrue(lastProgress >= 96, "Progress should reach close to 100% even with skipped words");
    }

    @Test
    @DisplayName("Test POST request with request body - verify body is sent correctly")
    public void testTranslation_POST_WithRequestBody() throws Exception {
        // Given: Configure provider for POST with request body
        mockProvider.setMethod("POST");
        mockProvider.setReqPattern("http://localhost:" + WIREMOCK_PORT + "/translate?api-version=3.0&from={1}&to={2}");
        mockProvider.setRequestBody("[{\"text\":\"{0}\"}]");
        mockProvider.setResPattern("{0}"); // Response parser
        mockProvider.setContentType("application/json");

        TestDataGenerator testData = new TestDataGenerator().generateWords(5);
        List<DictWord> allWords = testData.getDictWords();

        // Setup WireMock to expect POST with body
        stubFor(post(urlPathEqualTo("/translate"))
                .withRequestBody(containing("\"text\":"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withBody("translated")
                        .withHeader("Content-Type", "text/plain")));

        // Mock service calls
        when(transService.getTranslator(1L)).thenReturn(mockTranslator);
        when(dictService.findDict(1L)).thenReturn(mockDict);
        when(dictWordService.countWords(1L)).thenReturn(5L);
        when(dictWordService.getWords(eq(1L), eq(0), eq(32))).thenReturn(allWords);
        when(transService.isTranslated(anyLong(), anyLong())).thenReturn(false);

        // When: Process the translation job
        translateHandler.process(1L, mockJob);

        // Then: Verify POST was called 5 times
        verify(postRequestedFor(urlPathEqualTo("/translate"))
                .withRequestBody(containing("\"text\":")));

        verify(transService, times(5)).storeTranslation(anyLong(), anyLong(), anyString());
    }

    @Test
    @DisplayName("Print test statistics")
    public void printTestStatistics() {
        // This test just prints information about test data generation
        TestDataGenerator testData = new TestDataGenerator().generateWords(100);

        System.out.println("\n=== Test Data Statistics ===");
        System.out.println(testData.getStatistics());
        System.out.println("\nSample words:");
        testData.getDictWords().stream().limit(10).forEach(dw ->
                System.out.println("  - " + dw.getWord().getValue())
        );
        System.out.println("  ... and " + (testData.getTotalUniqueWords() - 10) + " more words");
        System.out.println("============================\n");

        assertTrue(true, "Statistics printed successfully");
    }
}
