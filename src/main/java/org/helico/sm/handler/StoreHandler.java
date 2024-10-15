package org.helico.sm.handler;

import org.apache.commons.io.IOUtils;
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

    @Autowired
    DictService dictService;

    @Autowired
    StateMachine stateMachine;

    @Autowired
    JobService jobService;

    @Override
    protected void process(Object object, Job job) throws Exception {
        Dict dict = dictService.findDict(job.getDictId());
        Text text = dict.getText();
        Reader reader;

        if (dict.getText().getOrigPath().toLowerCase().contains(".pdf")) {
            PDDocument document = Loader.loadPDF(new File(text.getOrigPath()));
            PDPage page = document.getPage(1);
            PDFTextStripper stripper = new PDFTextStripper();
            //todo - find out why this does not work
            stripper.setWordSeparator(" ");
            stripper.setLineSeparator("");
            stripper.setParagraphEnd("\n");
            String pdfText = stripper.getText(document);
            pdfText = pdfText.replaceAll("\t", " ");
            reader = new StringReader(pdfText);
        } else if (dict.getEncoding() == null) {
            reader = new FileReader(text.getOrigPath());
        } else {
            reader = new InputStreamReader(Files.newInputStream(Paths.get(text.getOrigPath())), dict.getEncoding());
        }

        Writer writer = new OutputStreamWriter(
                Files.newOutputStream(Paths.get(text.getUtfPath())), StandardCharsets.UTF_8);

        IOUtils.copyLarge(reader, writer);
        IOUtils.closeQuietly(reader);
        IOUtils.closeQuietly(writer);

        stateMachine.sendEvent(StateMachine.Event.OK, null, dict.getId());

    }

}
