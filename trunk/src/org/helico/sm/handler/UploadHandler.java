package org.helico.sm.handler;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.apache.log4j.Logger;

import org.helico.sm.Handler;

@Component("uploadHandler")
public class UploadHandler implements Handler {

    private static final Logger LOG = Logger.getLogger(UploadHandler.class);

    @Async
    public void process(Object object) {
	LOG.info("processing...");
    }

}