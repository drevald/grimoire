package org.helico.sm.handler;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
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

    private static final Logger LOG = Logger.getLogger(StoreHandler.class);

    @Autowired
    DictService dictService;

    @Autowired
    StateMachine stateMachine;

    @Autowired
    JobService jobService;

    @Override
    protected void process(Object object, Job job) throws Exception {
        LOG.info("1");
        Dict dict = dictService.findDict(job.getDictId());
        LOG.info("2");
        Text text = dict.getText();
        LOG.info("3");
        Reader reader;

        if (dict.getText().getOrigPath().toLowerCase().contains(".pdf")) {
            LOG.info("4");
            PDDocument document = Loader.loadPDF(new File(text.getOrigPath()));
            PDPage page = document.getPage(1);
            PDFTextStripper stripper = new PDFTextStripper();
            LOG.info("5");
            stripper.setWordSeparator(" ");
            stripper.setLineSeparator("");
            stripper.setParagraphEnd("\n");
            String pdfText = stripper.getText(document);
            pdfText = pdfText.replaceAll("\t", " ");
            LOG.info("6");
            reader = new StringReader(pdfText);
        } else if (dict.getEncoding() == null) {
            LOG.info("7");
            reader = new FileReader(text.getOrigPath());
            LOG.info("8");
        } else {
            LOG.info("9");
            reader = new InputStreamReader(Files.newInputStream(Paths.get(text.getOrigPath())), dict.getEncoding());
            LOG.info("10");
        }

        LOG.info("11");
        File utfTextFile = new File(text.getUtfPath());
        LOG.info("UTF Text file created = " + utfTextFile.createNewFile());
        Writer writer = new OutputStreamWriter(new FileOutputStream(utfTextFile), StandardCharsets.UTF_8);
        LOG.info("UTF Text file exist = " + utfTextFile.exists());
        IOUtils.copyLarge(reader, writer);
        IOUtils.closeQuietly(reader);
        IOUtils.closeQuietly(writer);
        LOG.info("UTF Text file size = " + utfTextFile.length());

        stateMachine.sendEvent(StateMachine.Event.OK, null, dict.getId());

    }

}
