package org.helico.sm.handler;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.helico.domain.Dict;
import org.helico.domain.Job;
import org.helico.service.DictService;
import org.helico.service.JobService;
import org.helico.sm.Handler;
import org.helico.sm.StateMachine;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.io.ByteArrayInputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.nio.charset.Charset;

@Component("storeHandler")
public class StoreHandler implements Handler {

    private static final Logger LOG = Logger.getLogger(StoreHandler.class);

    @Autowired
    DictService dictService;

    @Autowired
    StateMachine stateMachine;

    @Autowired
    JobService jobService;

    @Async
    public void process(Object data, Long id) {
        Job job = jobService.find(id);
        jobService.setActive(job.getId(), true);
        Dict dict = dictService.findDict(job.getDictId());
        LOG.info(">>> dict#" + dict.getId() + " with data " +  data);
        if (dict.getEncoding() == null) {
	    LOG.info(">>> dict#" + id + " has no encoding");
            stateMachine.sendEvent(StateMachine.Event.WAIT, null, dict.getId());
            jobService.setActive(job.getId(), false);
            return;
        }
        try {
	    LOG.info(">>> 1");
           InputStreamReader reader = new InputStreamReader(
                new ByteArrayInputStream(dict.getOrigDoc()), dict.getEncoding());
	    LOG.info(">>> 2");
            StringWriter writer = new StringWriter();
	    LOG.info(">>> 3");
            IOUtils.copyLarge(reader, writer);
	    LOG.info(">>> 4");
            IOUtils.closeQuietly(reader);
	    LOG.info(">>> 5");
            IOUtils.closeQuietly(writer);
	    LOG.info(">>> 6");
            dict.setUtfText(writer.toString().getBytes("UTF-8"));
	    LOG.info(">>> 7");
            dictService.saveDict(dict);
            LOG.info("<<< dict#" + dict.getId() + " with data " +  data);
            stateMachine.sendEvent(StateMachine.Event.OK, null, dict.getId());
	    LOG.info(">>> 8");
        } catch (Exception e) {
            stateMachine.sendEvent(StateMachine.Event.OK, e.getMessage(), dict.getId());
            LOG.info("<<< failed dict#" + dict.getId() + " with error " +  e.getMessage());
        } finally {
            jobService.setActive(job.getId(), false);
        }
    }

}
