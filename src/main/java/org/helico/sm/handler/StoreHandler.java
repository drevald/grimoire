package org.helico.sm.handler;

import org.apache.commons.io.IOUtils;
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

        if (dict.getText().getOrigPath().toLowerCase().endsWith("pdf")) {
            PDDocument document = PDDocument.load(new ByteArrayInputStream(text.getOrigDoc()));
            PDFTextStripper stripper = new PDFTextStripper();
            stripper.setLineSeparator(" ");
            String pdfText = stripper.getText(document);
            reader = new StringReader(pdfText);
        } else if (dict.getEncoding() == null) {
            reader = new InputStreamReader(new ByteArrayInputStream(text.getOrigDoc()));
        } else {
            reader = new InputStreamReader(new ByteArrayInputStream(text.getOrigDoc()), dict.getEncoding());
        }

        ByteArrayOutputStream os = new ByteArrayOutputStream();
        Writer writer = new OutputStreamWriter(os, StandardCharsets.UTF_8);

        IOUtils.copyLarge(reader, writer);
        IOUtils.closeQuietly(reader);
        IOUtils.closeQuietly(writer);
        text.setUtfText(os.toByteArray());
        dictService.saveText(text);
        dictService.saveDict(dict);
    }

}
