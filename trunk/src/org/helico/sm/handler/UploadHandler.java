package org.helico.sm.handler;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;

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
public class UploadHandler implements Handler {

    private static final Logger LOG = Logger.getLogger(UploadHandler.class);

    @Autowired
	DictService dictService;

    @Autowired
	private StateMachine stateMachine;

    @Autowired
    JobService jobService;

    @Async
    public void process(Object object, Long id) {
        LOG.info(">>start>>>dict#" + id );
        Job job = jobService.find(id);
        LOG.info(">>> 0");
        try {
        LOG.info(">>> 1");
            jobService.setActive(job.getId(), true);
        LOG.info(">>> 2");
            InputStream is = (InputStream)object;
        LOG.info(">>> 3");
            jobService.setActive(job.getId(), true);
        LOG.info(">>> 4");
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
        LOG.info(">>> 5");
            IOUtils.copy(is, baos);
        LOG.info(">>> 6");
            baos.flush();
        LOG.info(">>> 7");
	Long dictId = job.getDictId();
	Dict dict = dictService.findDict(dictId);
        LOG.info(">>> 8");
            dict.setOrigDoc(baos.toByteArray());
        LOG.info(">>> 9");
            dictService.saveDict(dict);
        LOG.info(">>> 10");
            baos.close();
        LOG.info(">>>11");
            is.close();
            LOG.info("<<< done dict#" + dictId);
            stateMachine.sendEvent(StateMachine.Event.OK, null, dictId);
        } catch (Exception e) {
	    LOG.info("<<<<<<<<<<<");
            LOG.error(e, e);
            stateMachine.sendEvent(StateMachine.Event.FAIL, e.getMessage(), job.getDictId());
            LOG.info("<<< failed dict#" + job.getDictId() + " with error " +  e.getMessage());
        } finally {
	    LOG.info("<><><><><>");
            jobService.setActive(job.getId(), false);
        }

    }

}