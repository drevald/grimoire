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
        try {
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