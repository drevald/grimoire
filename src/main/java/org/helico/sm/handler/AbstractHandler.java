package org.helico.sm.handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    private static final Logger LOG = LoggerFactory.getLogger(AbstractHandler.class);

    private static final String JOB_DONE = "DONE";

    @Autowired
    private StateMachine stateMachine;

    @Autowired
    JobService jobService;

    @Autowired
    DictService dictService;

    @Async
    public void process(Object object, Long id) {
        LOG.info(">>> start job#" + id);
        Job job = null;
        Dict dict = null;
        try {
            job = jobService.find(id);
            if (job == null) {
                LOG.error("Job #{} not found — cannot process", id);
                return;
            }
            dict = dictService.findDict(job.getDictId());
            jobService.setActive(job.getId(), true);
            process(object, job);
            LOG.info("<<< done dict#{}", dict.getId());
            jobService.setActive(job.getId(), false);
            stateMachine.sendEvent(StateMachine.Event.OK, JOB_DONE, dict.getId());
        } catch (Throwable e) {
            LOG.error("Handler failed for job#{} dict#{}", id, dict != null ? dict.getId() : "?", e);
            if (job != null) jobService.setActive(job.getId(), false);
            if (dict != null) stateMachine.sendEvent(StateMachine.Event.FAIL, e.getMessage(), dict.getId());
        }
    }

    protected abstract void process(Object object, Job job) throws Exception;

}