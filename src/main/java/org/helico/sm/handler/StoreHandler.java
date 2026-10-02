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
import java.util.regex.Pattern;

@Component("storeHandler")
public class StoreHandler extends AbstractHandler {

    private static final Logger LOG = LoggerFactory.getLogger(StoreHandler.class);
    private static final String SENTENCE_END = ".!?:;";
    private static final Pattern SPACES = Pattern.compile(" +");
    private static final Pattern EXTRA_BREAKS = Pattern.compile("\n{3,}");

    StoreHandler(DictService dictService, StateMachine stateMachine, JobService jobService) {
        super(stateMachine, jobService, dictService);
    }

    String cleanPdfText(String text) {
        if (text.isEmpty()) {
            return text;
        }
        String[] lines = text.replace('\t', ' ').replace("\r\n", "\n").split("\n");
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < lines.length; i++) {
            String line = lines[i].trim();
            String next = i + 1 < lines.length ? lines[i + 1].trim() : "";
            if (line.endsWith("-") && !next.isEmpty()) {
                result.append(line, 0, line.length() - 1);   // перенос слова: склеиваем без дефиса
            } else {
                result.append(line).append(separator(line, next));
            }
        }
        String cleaned = SPACES.matcher(result).replaceAll(" ");
        return EXTRA_BREAKS.matcher(cleaned).replaceAll("\n\n");
    }

    /** Что поставить между строкой PDF и следующей: PDF рвёт строки по ширине страницы, а не по смыслу. */
    private static String separator(String line, String next) {
        if (line.isEmpty() || next.isEmpty()) {
            return "\n";                                      // абзац или конец текста
        }
        boolean sentenceEnd = SENTENCE_END.indexOf(line.charAt(line.length() - 1)) >= 0;
        boolean nextIsCapital = Character.isUpperCase(next.charAt(0));
        return sentenceEnd && nextIsCapital ? "\n" : " ";
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
