package org.helico.sm;

import org.helico.domain.Dict;
import org.helico.domain.Job;
import org.helico.domain.Transition;
import org.helico.service.JobService;
import org.springframework.stereotype.Component;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ApplicationContext;
import org.springframework.beans.factory.annotation.Autowired;

import org.helico.service.DictService;
import org.helico.service.TransitionService;

import org.apache.log4j.Logger;

@Component
public class StateMachineImpl implements StateMachine, ApplicationContextAware {

    private static final Logger LOG = Logger.getLogger(StateMachineImpl.class);

    private ApplicationContext appContext;

    @Autowired
    private TransitionService transitionService;

    @Autowired
    private DictService dictService;

    @Autowired
    private JobService jobService;

    public void sendEvent(Event event, Object data, Long dictId) {
        LOG.debug("processing event..");
        Dict dict = dictService.findDict(dictId);
        Transition transition = transitionService.find(event.toString(), dict.getStatus());
        if (transition != null) {
            LOG.debug("handler found: " +  transition);
            Job job = new Job();
            job.setTransId(transition.getId());
            job.setActive(false);
            job.setDictId(dictId);
            jobService.save(job);
            Handler handler = (Handler)appContext.getBean(transition.getHandlerName());
            dict.setStatus(transition.getDestStatus());
            dictService.saveDict(dict);
            handler.process(data, job.getId());
            LOG.debug("handler called");
        } else {
            LOG.warn("handler for event = " + event + " and status = " + dict.getStatus() + " is not found");
        }
    }

    public void setApplicationContext(ApplicationContext appContext) {
        LOG.debug("setting application context: " + appContext);
        this.appContext = appContext;
    }



}