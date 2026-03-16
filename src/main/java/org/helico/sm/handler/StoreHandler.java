package org.helico.sm.handler;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.helico.domain.Dict;
import org.helico.domain.Job;
import org.helico.domain.Text;
import org.helico.service.DictService;
import org.helico.service.JobService;
import org.helico.sm.StateMachine;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

@Component("storeHandler")
public class StoreHandler extends AbstractHandler {

    private static final Logger LOG = LoggerFactory.getLogger(StoreHandler.class);

    @Autowired
    DictService dictService;

    @Autowired
    StateMachine stateMachine;

    @Autowired
    JobService jobService;

    /**
     * Cleans PDF text by merging lines that were split for formatting purposes.
     * Preserves paragraph breaks but removes unwanted line breaks within sentences.
     */
    private String cleanPdfText(String text) {
        if (text == null || text.isEmpty()) {
            return text;
        }

        // Replace tabs with spaces
        text = text.replaceAll("\t", " ");

        // Replace Windows line endings with Unix
        text = text.replaceAll("\r\n", "\n");

        String[] lines = text.split("\n");
        StringBuilder result = new StringBuilder();

        for (int i = 0; i < lines.length; i++) {
            String line = lines[i].trim();

            // Skip empty lines (preserve as paragraph breaks)
            if (line.isEmpty()) {
                result.append("\n");
                continue;
            }

            // Add the current line
            result.append(line);

            // Check if we should merge with next line
            if (i < lines.length - 1) {
                String nextLine = lines[i + 1].trim();

                // Don't merge if next line is empty (paragraph break)
                if (nextLine.isEmpty()) {
                    result.append("\n");
                    continue;
                }

                // Handle hyphenation: if line ends with hyphen, merge without space
                if (line.endsWith("-")) {
                    // Remove the hyphen and merge
                    result.setLength(result.length() - 1);
                    continue;
                }

                // Check if line ends with sentence-ending punctuation
                char lastChar = line.charAt(line.length() - 1);
                boolean endsWithPunctuation = lastChar == '.' || lastChar == '!' ||
                                             lastChar == '?' || lastChar == ':' ||
                                             lastChar == ';';

                // Check if next line starts with capital letter (might be new sentence)
                boolean nextStartsWithCapital = !nextLine.isEmpty() &&
                                               Character.isUpperCase(nextLine.charAt(0));

                // Merge lines if current doesn't end with punctuation
                // OR if it ends with punctuation but next doesn't start with capital
                if (!endsWithPunctuation || !nextStartsWithCapital) {
                    result.append(" ");
                } else {
                    result.append("\n");
                }
            } else {
                // Last line
                result.append("\n");
            }
        }

        // Clean up multiple consecutive spaces
        String cleaned = result.toString().replaceAll(" +", " ");

        // Clean up multiple consecutive newlines (max 2 = paragraph break)
        cleaned = cleaned.replaceAll("\n{3,}", "\n\n");

        return cleaned;
    }

    @Override
    protected void process(Object object, Job job) throws Exception {
        Dict dict = dictService.findDict(job.getDictId());
        Text text = dict.getText();
        Reader reader;

        if (dict.getText().getOrigPath().toLowerCase().contains(".pdf")) {
            try (PDDocument document = Loader.loadPDF(new File(text.getOrigPath()))) {
                PDFTextStripper stripper = new PDFTextStripper();
                stripper.setSortByPosition(true);
                stripper.setAddMoreFormatting(false);
                String pdfText = stripper.getText(document);
                pdfText = cleanPdfText(pdfText);
                reader = new StringReader(pdfText);
            }

        } else if (dict.getEncoding() == null) {
            reader = new FileReader(text.getOrigPath());
        } else {
            reader = new InputStreamReader(Files.newInputStream(Paths.get(text.getOrigPath())), dict.getEncoding());
        }

        File utfTextFile = new File(text.getUtfPath());
        LOG.info("UTF Text file created = " + utfTextFile.createNewFile());
        Writer writer = new OutputStreamWriter(new FileOutputStream(utfTextFile), StandardCharsets.UTF_8);
        LOG.info("UTF Text file exist = " + utfTextFile.exists());
        IOUtils.copyLarge(reader, writer);
        IOUtils.closeQuietly(reader);
        IOUtils.closeQuietly(writer);
        LOG.info("UTF Text file size = " + utfTextFile.length());

    }

}
