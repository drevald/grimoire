package org.helico.sm.handler;

import org.helico.sm.Handler;
import org.helico.domain.Job;
import org.helico.service.JobService;

import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Autowired;

@Component("dummyHandler")
public class DummyHandler implements Handler {

    @Autowired
	JobService jobService;


    public void process(Object data, Long id) {
	jobService.setDetails(id, (String)data);
        return;
    }

}
