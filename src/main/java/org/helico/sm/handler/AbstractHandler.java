package org.helico.sm.handler;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.helico.domain.Dict;
import org.helico.domain.Job;
import org.helico.service.JobService;
import org.helico.service.DictService;
import org.helico.sm.Handler;

import org.helico.sm.StateMachine;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
public abstract class AbstractHandler implements Handler {

    protected static final Logger LOG = LogManager.getLogger(AbstractHandler.class);

    private static final String JOB_DONE = "DONE";

    @Autowired
    private StateMachine stateMachine;

    @Autowired
    JobService jobService;

    @Autowired
    DictService dictService;

    @Async
	public void process(Object object, Long id) {
        LOG.info(">>> start dict#" + id );
        Job job = jobService.find(id);
	    Dict dict = dictService.findDict(job.getDictId());
        try {
            jobService.setActive(job.getId(), true);
	        process(object, job);
            LOG.info("<<< done dict#" + dict.getId());
            stateMachine.sendEvent(StateMachine.Event.OK, JOB_DONE, dict.getId());
        } catch (Error e) {
            LOG.error(e, e);
            stateMachine.sendEvent(StateMachine.Event.FAIL, e.getMessage(), dict.getId());
            LOG.info("<<< failed dict#" + dict.getId() + " with error " +  e.getMessage());
        } catch (Exception e) {
            LOG.error(e, e);
            stateMachine.sendEvent(StateMachine.Event.FAIL, e.getMessage(), dict.getId());
            LOG.info("<<< failed dict#" + dict.getId() + " with exception " +  e.getMessage());
        } finally {
            jobService.setActive(job.getId(), false);
        }
    }

    protected abstract void process(Object object, Job job) throws Exception;

}