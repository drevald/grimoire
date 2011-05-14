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
        LOG.info(">>> dict#" + id + " with data " +  data);
        Job job = jobService.find(id);
        jobService.setActive(job.getId(), true);
        Dict dict = dictService.findDict(job.getDictId());
        if (dict.getEncoding() == null) {
            stateMachine.sendEvent(StateMachine.Event.WAIT, null, id);
            jobService.setActive(job.getId(), false);
            return;
        }
        try {
            InputStreamReader reader = new InputStreamReader(
                new ByteArrayInputStream(dict.getOrigDoc()), dict.getEncoding());
            StringWriter writer = new StringWriter();
            IOUtils.copyLarge(reader, writer);
            IOUtils.closeQuietly(reader);
            IOUtils.closeQuietly(writer);
            dict.setUtfText(writer.toString().getBytes("UTF-8"));
            dictService.saveDict(dict);
            LOG.info("<<< dict#" + id + " with data " +  data);
            stateMachine.sendEvent(StateMachine.Event.OK, null, id);
        } catch (Exception e) {
            stateMachine.sendEvent(StateMachine.Event.OK, e.getMessage(), id);
            LOG.info("<<< failed dict#" + id + " with error " +  e.getMessage());
        } finally {
            jobService.setActive(job.getId(), false);
        }
    }

}
