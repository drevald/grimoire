package org.helico.sm.handler;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.helico.domain.Job;
import org.helico.domain.Text;
import org.springframework.stereotype.Component;

import org.helico.domain.Dict;

@Component("uploadHandler")
public class UploadHandler extends AbstractHandler {

    private static final Logger LOG = Logger.getLogger(UploadHandler.class);

    public void process(Object object, Job job) throws Exception {
        jobService.setActive(job.getId(), true);
        InputStream is = (InputStream)object;
        Long dictId = job.getDictId();
        Dict dict = dictService.findDict(dictId);
        Text text = dict.getText();
        try (OutputStream os = Files.newOutputStream(new File(text.getOrigPath()).toPath())) {
            IOUtils.copy(is, os);
            os.flush();
            dictService.saveDict(dict);
            os.close();
            is.close();
            LOG.info("<<< done dict#" + dictId);
        } catch (Exception e) {
            LOG.error(e);
        }

    }

}