package org.helico.sm.handler;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.helico.domain.Dict;
import org.helico.domain.Job;
import org.helico.domain.Dict.Status;
import org.helico.service.DictService;
import org.helico.service.JobService;
import org.helico.sm.Handler;

import org.helico.sm.StateMachine;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;


@Component("uploadHandler")
public class UploadHandler extends AbstractHandler {

    private static final Logger LOG = Logger.getLogger(UploadHandler.class);
    
    public void process(Object object, Job job) throws Exception {
          jobService.setActive(job.getId(), true);
          InputStream is = (InputStream)object;
          jobService.setActive(job.getId(), true);
          ByteArrayOutputStream baos = new ByteArrayOutputStream();
          IOUtils.copy(is, baos);
          baos.flush();
	  Long dictId = job.getDictId();
	  Dict dict = dictService.findDict(dictId);
          dict.setOrigDoc(baos.toByteArray());
          dictService.saveDict(dict);
          baos.close();
          is.close();
          LOG.info("<<< done dict#" + dictId);
    }

}