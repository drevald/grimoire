package org.helico.sm.handler;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.font.Standard14Fonts;
import org.helico.domain.Dict;
import org.helico.domain.Job;
import org.helico.domain.Text;
import org.helico.service.DictService;
import org.helico.service.JobService;
import org.helico.sm.StateMachine;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.File;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class StoreHandlerTest {

    @Mock DictService dictService;
    @Mock StateMachine stateMachine;
    @Mock JobService jobService;

    @InjectMocks StoreHandler storeHandler;

    @TempDir Path tempDir;

    Dict mockDict;
    Text mockText;
    Job mockJob;

    @BeforeEach
    void setUp() {
        mockText = new Text();
        mockDict = new Dict();
        mockDict.setId(1L);
        mockDict.setLangId("en");
        mockDict.setText(mockText);

        mockJob = new Job();
        mockJob.setId(1L);
        mockJob.setDictId(1L);

        when(dictService.findDict(1L)).thenReturn(mockDict);
    }

    // -------------------------------------------------------------------------
    // Plain text
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("Plain text file is copied as UTF-8 to the utfPath")
    void testPlainTextFile_copiedToUtfFile() throws Exception {
        String content = "Hello world this is a test";
        Path origFile = tempDir.resolve("book.txt");
        Path utfFile  = tempDir.resolve("book.utf.txt");
        Files.writeString(origFile, content, StandardCharsets.UTF_8);

        mockText.setOrigPath(origFile.toString());
        mockText.setUtfPath(utfFile.toString());
        mockDict.setEncoding(null);

        storeHandler.process(null, mockJob);

        assertTrue(Files.exists(utfFile), "UTF file must be created");
        assertEquals(content, Files.readString(utfFile, StandardCharsets.UTF_8));
    }

    @Test
    @DisplayName("Plain text file with Windows-1251 encoding is re-encoded to UTF-8")
    void testPlainTextFile_windows1251_reEncodedToUtf8() throws Exception {
        String content = "Hello world test";
        Path origFile = tempDir.resolve("book.txt");
        Path utfFile  = tempDir.resolve("book.utf.txt");
        Files.write(origFile, content.getBytes(Charset.forName("Windows-1251")));

        mockText.setOrigPath(origFile.toString());
        mockText.setUtfPath(utfFile.toString());
        mockDict.setEncoding("Windows-1251");

        storeHandler.process(null, mockJob);

        assertTrue(Files.exists(utfFile));
        assertEquals(content, Files.readString(utfFile, StandardCharsets.UTF_8));
    }

    // -------------------------------------------------------------------------
    // PDF — page coverage (regression for the page-1-only bug)
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("PDF: text from ALL pages is extracted, not just page 1")
    void testPdfFile_allPagesExtracted() throws Exception {
        Path origFile = tempDir.resolve("book.pdf");
        Path utfFile  = tempDir.resolve("book.utf.pdf");

        // Build a 3-page PDF, each page with distinct text
        try (PDDocument doc = new PDDocument()) {
            String[] pageTexts = {"FirstPageContent", "SecondPageContent", "ThirdPageContent"};
            for (String pageText : pageTexts) {
                PDPage page = new PDPage();
                doc.addPage(page);
                try (PDPageContentStream cs = new PDPageContentStream(doc, page)) {
                    cs.beginText();
                    cs.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA), 12);
                    cs.newLineAtOffset(100, 700);
                    cs.showText(pageText);
                    cs.endText();
                }
            }
            doc.save(origFile.toFile());
        }

        mockText.setOrigPath(origFile.toString());
        mockText.setUtfPath(utfFile.toString());

        storeHandler.process(null, mockJob);

        assertTrue(Files.exists(utfFile));
        String extracted = Files.readString(utfFile, StandardCharsets.UTF_8);
        assertTrue(extracted.contains("FirstPageContent"),  "Page 1 must be present");
        assertTrue(extracted.contains("SecondPageContent"), "Page 2 must be present");
        assertTrue(extracted.contains("ThirdPageContent"),  "Page 3 must be present");
    }

    @Test
    @DisplayName("PDF: single-page PDF is extracted correctly")
    void testPdfFile_singlePage_extracted() throws Exception {
        Path origFile = tempDir.resolve("single.pdf");
        Path utfFile  = tempDir.resolve("single.utf.pdf");

        try (PDDocument doc = new PDDocument()) {
            PDPage page = new PDPage();
            doc.addPage(page);
            try (PDPageContentStream cs = new PDPageContentStream(doc, page)) {
                cs.beginText();
                cs.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA), 12);
                cs.newLineAtOffset(100, 700);
                cs.showText("OnlyPageContent");
                cs.endText();
            }
            doc.save(origFile.toFile());
        }

        mockText.setOrigPath(origFile.toString());
        mockText.setUtfPath(utfFile.toString());

        storeHandler.process(null, mockJob);

        assertTrue(Files.exists(utfFile));
        assertTrue(Files.readString(utfFile, StandardCharsets.UTF_8).contains("OnlyPageContent"));
    }

    // -------------------------------------------------------------------------
    // Regression: StoreHandler must NOT call stateMachine.sendEvent directly
    // (AbstractHandler handles the OK transition after process() returns)
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("StoreHandler does not call StateMachine directly (AbstractHandler owns that)")
    void testStoreHandler_doesNotCallStateMachine() throws Exception {
        Path origFile = tempDir.resolve("book.txt");
        Path utfFile  = tempDir.resolve("book.utf.txt");
        Files.writeString(origFile, "some content");

        mockText.setOrigPath(origFile.toString());
        mockText.setUtfPath(utfFile.toString());
        mockDict.setEncoding(null);

        storeHandler.process(null, mockJob);

        verifyNoInteractions(stateMachine);
    }

    // -------------------------------------------------------------------------
    // cleanPdfText behaviour (visible through full round-trip)
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("PDF: hyphenated line-break is merged without inserting a hyphen")
    void testPdfCleanText_hyphenatedWordsMerged() throws Exception {
        Path origFile = tempDir.resolve("hyph.pdf");
        Path utfFile  = tempDir.resolve("hyph.utf.pdf");

        // Simulate PDFBox output where a word is split with a trailing hyphen
        // We embed two lines: "auto-" and "mobile"  → should become "automobile"
        try (PDDocument doc = new PDDocument()) {
            PDPage page = new PDPage();
            doc.addPage(page);
            try (PDPageContentStream cs = new PDPageContentStream(doc, page)) {
                cs.beginText();
                cs.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA), 12);
                cs.newLineAtOffset(100, 700);
                cs.showText("auto-");
                cs.newLineAtOffset(0, -20);
                cs.showText("mobile");
                cs.endText();
            }
            doc.save(origFile.toFile());
        }

        mockText.setOrigPath(origFile.toString());
        mockText.setUtfPath(utfFile.toString());

        storeHandler.process(null, mockJob);

        String extracted = Files.readString(utfFile, StandardCharsets.UTF_8);
        assertTrue(extracted.contains("automobile"),
                "Hyphenated line break should be merged: got [" + extracted.trim() + "]");
    }

    @Test
    @DisplayName("UTF file has non-zero size after processing a non-empty input")
    void testUtfFile_nonZeroSizeAfterProcessing() throws Exception {
        String content = "word1 word2 word3";
        Path origFile = tempDir.resolve("book.txt");
        Path utfFile  = tempDir.resolve("book.utf.txt");
        Files.writeString(origFile, content);

        mockText.setOrigPath(origFile.toString());
        mockText.setUtfPath(utfFile.toString());
        mockDict.setEncoding(null);

        storeHandler.process(null, mockJob);

        assertTrue(new File(utfFile.toString()).length() > 0,
                "UTF file must not be empty after processing");
    }
}
