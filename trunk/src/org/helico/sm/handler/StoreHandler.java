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
        Dict dict = dictService.findDict(job.getDictId());
        InputStreamReader reader = new InputStreamReader(
                new ByteArrayInputStream(dict.getOrigDoc()), dict.getEncoding());
         StringWriter writer = new StringWriter();
         IOUtils.copyLarge(reader, writer);
         IOUtils.closeQuietly(reader);
         IOUtils.closeQuietly(writer);
         dict.setUtfText(writer.toString().getBytes("UTF-8"));
         dictService.saveDict(dict);
    }

}
