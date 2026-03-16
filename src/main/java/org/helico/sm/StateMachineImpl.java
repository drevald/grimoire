package org.helico.sm;

import org.helico.domain.Dict;
import org.helico.domain.Job;
import org.helico.domain.Transition;
import org.helico.service.JobService;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ApplicationContext;
import org.springframework.beans.factory.annotation.Autowired;

import org.helico.service.DictService;
import org.helico.service.TransitionService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component
public class StateMachineImpl implements StateMachine, ApplicationContextAware {

    private static final Logger LOG = LoggerFactory.getLogger(StateMachineImpl.class);

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
        String oldStatus = dict.getStatus();
        Transition transition = transitionService.find(event.toString(), dict.getStatus());
        if (transition != null) {
            LOG.debug("handler found: " +  transition);
            Job job = new Job();
            job.setTransId(transition.getId());
            job.setActive(false);
            job.setDictId(dictId);
            if (event == Event.TRANSLATE && data instanceof Long) {
                job.setDetails(String.valueOf(data));
            }
            jobService.save(job);
            Handler handler = (Handler)appContext.getBean(transition.getHandlerName());
            String newStatus = transition.getDestStatus();
            LOG.info(">>> dict#{} STATUS CHANGE: {} → {} (event={}, handler={})",
                dictId, oldStatus, newStatus, event.label, transition.getHandlerName());
            dict.setStatus(newStatus);
            dictService.saveDict(dict);
            LOG.info("<<< dict#{} status saved as {}", dictId, newStatus);
            handler.process(data, job.getId());
            LOG.debug("handler called");
        } else {
            LOG.warn("handler for event = " + event.label + " and status = " + dict.getStatus() + " is not found");
        }
    }

    public void setApplicationContext(ApplicationContext appContext) {
        LOG.debug("setting application context: " + appContext);
        this.appContext = appContext;
    }

    public void onApplicationEvent(ApplicationEvent applicationEvent) {
        LOG.info("Event received: " + applicationEvent);
        if (applicationEvent instanceof ContextRefreshedEvent) {
            dictService.fixStatus();
        }
    }

}