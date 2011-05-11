package org.helico.sm.handler;

import org.apache.log4j.Logger;
import org.helico.service.JobService;
import org.helico.sm.Handler;
import org.helico.sm.StateMachine;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


@Component("translateHandler")
public class TranslateHandler implements Handler {

    private static final Logger LOG = Logger.getLogger(TranslateHandler.class);

    @Autowired
    StateMachine stateMachine;

    @Autowired
    JobService jobService;

    public void process(Object data, Long id) {
        LOG.info("processing dict# " + id);
    }

}
