package org.helico.sm.handler;

import org.apache.log4j.Logger;
import org.helico.service.JobService;
import org.helico.sm.Handler;
import org.helico.sm.StateMachine;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Reads UTF-8 encoded text and detects words. By words it understands continuous
 * sets of characters separated by white space. Allowed symbols within word can be dash or new line symbol.
 * Adds word to database if not present yet.
 */

@Component("parseHandler")
public class ParseHandler implements Handler {

    private static final Logger LOG = Logger.getLogger(ParseHandler.class);

    @Autowired
    StateMachine stateMachine;

    @Autowired
    JobService jobService;

    public void process(Object data, Long id) {
        LOG.info("processing dict# " + id);
    }

}
