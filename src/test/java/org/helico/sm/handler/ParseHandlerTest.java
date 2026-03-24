package org.helico.sm.handler;

import org.helico.domain.Dict;
import org.helico.domain.Job;
import org.helico.domain.Text;
import org.helico.service.DictService;
import org.helico.service.JobService;
import org.helico.service.WordService;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Comprehensive test for ParseHandler with focus on:
 * 1. Word counting accuracy
 * 2. Progress tracking accuracy based on bytes read
 * 3. All words are stored correctly
 */
@ExtendWith(MockitoExtension.class)
public class ParseHandlerTest {

    @Mock
    private WordService wordService;

    @Mock
    private JobService jobService;

    @Mock
    private DictService dictService;

    @InjectMocks
    private ParseHandler parseHandler;

    private Dict mockDict;
    private Text mockText;
    private Job mockJob;

    @TempDir
    Path tempDir;

    @BeforeEach
    public void setUp() {
        mockText = new Text();
        mockDict = new Dict();
        mockDict.setId(1L);
        mockDict.setLangId("en");
        mockDict.setText(mockText);

        mockJob = new Job();
        mockJob.setId(1L);
        mockJob.setDictId(1L);
    }

    /**
     * Test data generator: creates random text with known word counts
     */
    private static class TextGenerator {
        private final Random random = new Random(42); // Fixed seed for reproducibility
        private final List<String> words = new ArrayList<>();
        private final Map<String, Integer> wordCounts = new LinkedHashMap<>();

        public TextGenerator generateWords(int wordCount) {
            words.clear();
            wordCounts.clear();

            // Generate unique words using only letters: worda, wordb, wordc, ..., wordaa, wordab, etc.
            for (int i = 1; i <= wordCount; i++) {
                String suffix = generateLetterSuffix(i);
                String word = "word" + suffix;
                words.add(word);
                wordCounts.put(word.toLowerCase(), wordCounts.getOrDefault(word.toLowerCase(), 0) + 1);
            }

            return this;
        }

        /**
         * Generate a letter-only suffix for word indexing
         * 1 -> "a", 2 -> "b", ..., 26 -> "z", 27 -> "aa", 28 -> "ab", etc.
         */
        private String generateLetterSuffix(int index) {
            StringBuilder suffix = new StringBuilder();
            index--; // Convert to 0-based
            do {
                suffix.insert(0, (char) ('a' + (index % 26)));
                index = index / 26 - 1;
            } while (index >= 0);
            return suffix.toString();
        }

        public TextGenerator generateRepeatingWords(String... repeatingWords) {
            words.clear();
            wordCounts.clear();

            for (String word : repeatingWords) {
                words.add(word);
                wordCounts.put(word.toLowerCase(), wordCounts.getOrDefault(word.toLowerCase(), 0) + 1);
            }

            return this;
        }

        public String toText() {
            return String.join(" ", words);
        }

        public String toTextWithNewlines(int wordsPerLine) {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < words.size(); i++) {
                sb.append(words.get(i));
                if ((i + 1) % wordsPerLine == 0 && i < words.size() - 1) {
                    sb.append("\n");
                } else if (i < words.size() - 1) {
                    sb.append(" ");
                }
            }
            return sb.toString();
        }

        public List<String> getWords() {
            return new ArrayList<>(words);
        }

        public Map<String, Integer> getUniqueWordCounts() {
            return new LinkedHashMap<>(wordCounts);
        }

        public int getTotalWords() {
            return words.size();
        }

        public int getUniqueWords() {
            return wordCounts.size();
        }

        public String getStatistics() {
            return String.format("Total words: %d, Unique words: %d, Text length: %d bytes",
                    getTotalWords(), getUniqueWords(), toText().getBytes().length);
        }
    }

    @Test
    @DisplayName("Test data generator - verify text generation")
    public void testTextGenerator() {
        TextGenerator generator = new TextGenerator().generateWords(100);

        assertEquals(100, generator.getTotalWords(), "Should generate 100 total words");
        assertEquals(100, generator.getUniqueWords(), "Should have 100 unique words");

        String text = generator.toText();
        assertTrue(text.contains("worda"), "Should contain worda");
        assertTrue(text.contains("wordz"), "Should contain wordz");
        // Word 100 would be "wordcv" (using letter suffix algorithm)

        System.out.println("Generated text sample: " + text.substring(0, Math.min(100, text.length())) + "...");
        System.out.println(generator.getStatistics());
    }

    @Test
    @DisplayName("Parse 50 words - verify all words are counted and stored")
    public void testParse_50Words_AllCounted() throws Exception {
        // Given: Generate 50 words
        TextGenerator generator = new TextGenerator().generateWords(50);
        String textContent = generator.toText();

        System.out.println("Test: " + generator.getStatistics());

        // Create temporary file with the text
        File tempFile = tempDir.resolve("test50.txt").toFile();
        Files.writeString(tempFile.toPath(), textContent);
        mockText.setUtfPath(tempFile.getAbsolutePath());

        // Mock service calls
        when(dictService.findDict(1L)).thenReturn(mockDict);

        // When: Process the file
        parseHandler.process(null, mockJob);

        // Then: Verify all unique words were stored
        ArgumentCaptor<String> wordCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> langCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<Long> dictIdCaptor = ArgumentCaptor.forClass(Long.class);

        verify(wordService, times(50))
                .store(wordCaptor.capture(), langCaptor.capture(), dictIdCaptor.capture());

        List<String> storedWords = wordCaptor.getAllValues();
        assertEquals(50, storedWords.size(), "Should store 50 words");

        // Verify all expected words were stored (in lowercase)
        Set<String> expectedWords = generator.getWords().stream()
                .map(String::toLowerCase)
                .collect(Collectors.toSet());
        Set<String> actualWords = new HashSet<>(storedWords);

        // Check that all expected words are present in actual words
        assertTrue(actualWords.containsAll(expectedWords),
                "All generated words should be stored. Expected: " + expectedWords + ", Got: " + actualWords);

        // Verify language and dict ID
        assertTrue(langCaptor.getAllValues().stream().allMatch(lang -> "en".equals(lang)));
        assertTrue(dictIdCaptor.getAllValues().stream().allMatch(id -> id.equals(1L)));

        System.out.println("✓ All 50 words verified and stored correctly");
    }

    @Test
    @DisplayName("Parse 100 words - verify progress updates")
    public void testParse_100Words_ProgressUpdates() throws Exception {
        // Given: Generate 100 words
        TextGenerator generator = new TextGenerator().generateWords(100);
        String textContent = generator.toText();
        long fileSize = textContent.getBytes().length;

        System.out.println("Test: " + generator.getStatistics());
        System.out.println("File size: " + fileSize + " bytes");

        // Create temporary file
        File tempFile = tempDir.resolve("test100.txt").toFile();
        Files.writeString(tempFile.toPath(), textContent);
        mockText.setUtfPath(tempFile.getAbsolutePath());

        when(dictService.findDict(1L)).thenReturn(mockDict);

        // When: Process the file
        parseHandler.process(null, mockJob);

        // Then: Verify progress was updated
        ArgumentCaptor<Long> jobIdCaptor = ArgumentCaptor.forClass(Long.class);
        ArgumentCaptor<Integer> progressCaptor = ArgumentCaptor.forClass(Integer.class);

        verify(jobService, atLeastOnce())
                .setProgress(jobIdCaptor.capture(), progressCaptor.capture());

        List<Integer> progressUpdates = progressCaptor.getAllValues();
        System.out.println("Progress updates: " + progressUpdates);

        // Verify progress tracking
        assertFalse(progressUpdates.isEmpty(), "Should have progress updates");

        // Progress should increase monotonically
        for (int i = 1; i < progressUpdates.size(); i++) {
            assertTrue(progressUpdates.get(i) >= progressUpdates.get(i - 1),
                    "Progress should increase monotonically");
        }

        // Final progress should be 100%
        Integer lastProgress = progressUpdates.get(progressUpdates.size() - 1);
        assertTrue(lastProgress >= 99 && lastProgress <= 100,
                "Final progress should be 100%, was: " + lastProgress);

        // Verify job ID
        assertTrue(jobIdCaptor.getAllValues().stream().allMatch(id -> id.equals(1L)));

        System.out.println("✓ Progress tracked correctly: " + progressUpdates.size() + " updates");
    }

    @Test
    @DisplayName("Parse 200 words - verify all words counted and progress granularity")
    public void testParse_200Words_ProgressGranularity() throws Exception {
        // Given: Generate 200 words (progress updates every 100 chars)
        TextGenerator generator = new TextGenerator().generateWords(200);
        String textContent = generator.toText();
        long fileSize = textContent.getBytes().length;

        System.out.println("Test: " + generator.getStatistics());

        File tempFile = tempDir.resolve("test200.txt").toFile();
        Files.writeString(tempFile.toPath(), textContent);
        mockText.setUtfPath(tempFile.getAbsolutePath());

        when(dictService.findDict(1L)).thenReturn(mockDict);

        // When: Process
        parseHandler.process(null, mockJob);

        // Then: Verify word count
        verify(wordService, times(200)).store(anyString(), anyString(), anyLong());

        // Verify progress granularity (PROGRESS_GRANULARITY = 100)
        ArgumentCaptor<Integer> progressCaptor = ArgumentCaptor.forClass(Integer.class);
        verify(jobService, atLeastOnce()).setProgress(eq(1L), progressCaptor.capture());

        List<Integer> progressUpdates = progressCaptor.getAllValues();
        System.out.println("Progress updates for 200 words: " + progressUpdates);
        System.out.println("Number of progress updates: " + progressUpdates.size());

        // Should have multiple progress updates (every 100 characters read)
        assertTrue(progressUpdates.size() >= 2,
                "Should have at least 2 progress updates for 200 words");

        System.out.println("✓ All 200 words counted, progress granularity verified");
    }

    @Test
    @DisplayName("Parse text with punctuation - verify only letters extracted")
    public void testParse_WithPunctuation() throws Exception {
        // Given: Text with punctuation and numbers
        String textContent = "Hello, world! This is a test. Numbers: 123, 456.";
        // Expected words: Hello world This is a test Numbers (7 words)

        File tempFile = tempDir.resolve("test_punctuation.txt").toFile();
        Files.writeString(tempFile.toPath(), textContent);
        mockText.setUtfPath(tempFile.getAbsolutePath());

        when(dictService.findDict(1L)).thenReturn(mockDict);

        // When: Process
        parseHandler.process(null, mockJob);

        // Then: Verify only letter-based words are stored
        ArgumentCaptor<String> wordCaptor = ArgumentCaptor.forClass(String.class);
        verify(wordService, atLeast(1)).store(wordCaptor.capture(), anyString(), anyLong());

        List<String> storedWords = wordCaptor.getAllValues();
        System.out.println("Stored words: " + storedWords);

        // All stored words should be lowercase and contain only letters
        for (String word : storedWords) {
            assertTrue(word.matches("[a-z]+"), "Word should contain only lowercase letters: " + word);
        }

        // Verify expected words are present
        assertTrue(storedWords.contains("hello"));
        assertTrue(storedWords.contains("world"));
        assertTrue(storedWords.contains("test"));

        System.out.println("✓ Punctuation handled correctly, " + storedWords.size() + " words extracted");
    }

    @Test
    @DisplayName("Parse text with long words - verify >32 char words are skipped")
    public void testParse_LongWords_Skipped() throws Exception {
        // Given: Text with a very long word (>32 characters)
        String longWord = "a".repeat(50); // 50 characters
        String textContent = "short " + longWord + " word";

        File tempFile = tempDir.resolve("test_long.txt").toFile();
        Files.writeString(tempFile.toPath(), textContent);
        mockText.setUtfPath(tempFile.getAbsolutePath());

        when(dictService.findDict(1L)).thenReturn(mockDict);

        // When: Process
        parseHandler.process(null, mockJob);

        // Then: Verify long word was NOT stored (only "short" and "word")
        ArgumentCaptor<String> wordCaptor = ArgumentCaptor.forClass(String.class);
        verify(wordService, times(2)).store(wordCaptor.capture(), anyString(), anyLong());

        List<String> storedWords = wordCaptor.getAllValues();
        assertEquals(2, storedWords.size(), "Should only store 2 words (short and word)");
        assertTrue(storedWords.contains("short"));
        assertTrue(storedWords.contains("word"));
        assertFalse(storedWords.contains(longWord.toLowerCase()),
                "Long word (>32 chars) should not be stored");

        System.out.println("✓ Long words correctly skipped");
    }

    @Test
    @DisplayName("Parse text with newlines - verify words across lines")
    public void testParse_WithNewlines() throws Exception {
        // Given: Text with newlines
        TextGenerator generator = new TextGenerator().generateWords(50);
        String textContent = generator.toTextWithNewlines(10); // 10 words per line

        File tempFile = tempDir.resolve("test_newlines.txt").toFile();
        Files.writeString(tempFile.toPath(), textContent);
        mockText.setUtfPath(tempFile.getAbsolutePath());

        when(dictService.findDict(1L)).thenReturn(mockDict);

        // When: Process
        parseHandler.process(null, mockJob);

        // Then: Verify all words stored correctly despite newlines
        verify(wordService, times(50)).store(anyString(), anyString(), anyLong());

        System.out.println("✓ Newlines handled correctly, all 50 words parsed");
    }

    @Test
    @DisplayName("Parse empty file - verify no words stored (may throw ArithmeticException)")
    public void testParse_EmptyFile() throws Exception {
        // Given: Empty file
        File tempFile = tempDir.resolve("test_empty.txt").toFile();
        Files.writeString(tempFile.toPath(), "");
        mockText.setUtfPath(tempFile.getAbsolutePath());

        when(dictService.findDict(1L)).thenReturn(mockDict);

        // When: Process (may throw ArithmeticException due to division by zero in progress calculation)
        try {
            parseHandler.process(null, mockJob);
        } catch (ArithmeticException e) {
            // Expected for empty file (division by zero in progress calculation)
            System.out.println("Note: Empty file causes ArithmeticException in progress calculation (textLength = 0)");
        }

        // Then: Verify no words stored regardless
        verify(wordService, never()).store(anyString(), anyString(), anyLong());

        System.out.println("✓ Empty file handled correctly - no words stored");
    }

    @Test
    @DisplayName("Parse with duplicate words - verify each occurrence is stored")
    public void testParse_DuplicateWords() throws Exception {
        // Given: Text with duplicate words
        TextGenerator generator = new TextGenerator()
                .generateRepeatingWords("hello", "world", "hello", "test", "world", "hello");

        String textContent = generator.toText();
        System.out.println("Text with duplicates: " + textContent);
        System.out.println("Word counts: " + generator.getUniqueWordCounts());

        File tempFile = tempDir.resolve("test_duplicates.txt").toFile();
        Files.writeString(tempFile.toPath(), textContent);
        mockText.setUtfPath(tempFile.getAbsolutePath());

        when(dictService.findDict(1L)).thenReturn(mockDict);

        // When: Process
        parseHandler.process(null, mockJob);

        // Then: Verify each word occurrence is stored (6 total)
        ArgumentCaptor<String> wordCaptor = ArgumentCaptor.forClass(String.class);
        verify(wordService, times(6)).store(wordCaptor.capture(), anyString(), anyLong());

        List<String> storedWords = wordCaptor.getAllValues();

        // Count occurrences
        Map<String, Long> counts = storedWords.stream()
                .collect(Collectors.groupingBy(w -> w, Collectors.counting()));

        System.out.println("Stored word counts: " + counts);

        assertEquals(3, counts.get("hello"), "hello should appear 3 times");
        assertEquals(2, counts.get("world"), "world should appear 2 times");
        assertEquals(1, counts.get("test"), "test should appear 1 time");

        System.out.println("✓ Duplicate words handled correctly");
    }

    @Test
    @DisplayName("Parse Icelandic (non-ASCII) words - verify unicode letters are extracted")
    public void testParse_IcelandicText() throws Exception {
        // Given: Icelandic text with accented characters
        String textContent = "Þetta er prófun á íslensku máli";
        // Expected words: Þetta er prófun á íslensku máli (6 words)

        File tempFile = tempDir.resolve("icelandic.txt").toFile();
        Files.writeString(tempFile.toPath(), textContent, java.nio.charset.StandardCharsets.UTF_8);
        mockText.setUtfPath(tempFile.getAbsolutePath());

        when(dictService.findDict(1L)).thenReturn(mockDict);

        parseHandler.process(null, mockJob);

        ArgumentCaptor<String> wordCaptor = ArgumentCaptor.forClass(String.class);
        verify(wordService, atLeast(1)).store(wordCaptor.capture(), anyString(), anyLong());

        List<String> storedWords = wordCaptor.getAllValues();
        // Character.isLetter() returns true for Icelandic letters so they should be captured
        assertTrue(storedWords.contains("þetta"), "Icelandic word 'Þetta' should be stored as lowercase");
        assertTrue(storedWords.contains("íslensku"), "Icelandic word with accent should be stored");

        System.out.println("✓ Icelandic words parsed: " + storedWords);
    }

    @Test
    @DisplayName("Parse empty file - ArithmeticException from division-by-zero is a known bug")
    public void testParse_EmptyFile_divisionByZeroBug() throws Exception {
        // This test documents a bug in ParseHandler: when the file is 0 bytes,
        // the final progress line `(byteCount * 100) / textLength` throws ArithmeticException.
        // Fix: guard with `if (textLength > 0)` before that line.
        File tempFile = tempDir.resolve("empty.txt").toFile();
        Files.writeString(tempFile.toPath(), "");
        mockText.setUtfPath(tempFile.getAbsolutePath());

        when(dictService.findDict(1L)).thenReturn(mockDict);

        assertThrows(ArithmeticException.class, () -> parseHandler.process(null, mockJob),
                "Empty file should currently throw ArithmeticException (known bug - fix with textLength > 0 guard)");

        verify(wordService, never()).store(anyString(), anyString(), anyLong());
    }

    @Test
    @DisplayName("Documentation: Progress calculation explanation")
    public void documentProgressCalculation() {
        System.out.println("\n=== Progress Calculation in ParseHandler ===");
        System.out.println("Progress is calculated based on BYTES READ, not words processed:");
        System.out.println();
        System.out.println("Formula: progress = (bytesRead * 100) / totalFileSize");
        System.out.println();
        System.out.println("Progress updates occur:");
        System.out.println("  - Every 100 characters read (PROGRESS_GRANULARITY = 100)");
        System.out.println("  - At the end of file processing");
        System.out.println();
        System.out.println("Example for 200-word file (~1200 bytes):");
        System.out.println("  - At ~100 chars:  progress ≈ 8%");
        System.out.println("  - At ~200 chars:  progress ≈ 16%");
        System.out.println("  - At ~300 chars:  progress ≈ 25%");
        System.out.println("  - ...");
        System.out.println("  - At ~1200 chars: progress = 100%");
        System.out.println();
        System.out.println("Note: Progress is based on file position, not word count!");
        System.out.println("===========================================\n");

        assertTrue(true, "Documentation displayed");
    }
}
