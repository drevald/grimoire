package org.helico.sm;

import org.helico.dao.DictDAO;
import org.helico.domain.Dict;
import org.helico.domain.Job;
import org.helico.domain.Transition;
import org.helico.service.JobService;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextStartedEvent;
import org.springframework.beans.factory.annotation.Autowired;

import org.helico.service.DictService;
import org.helico.service.TransitionService;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import java.util.List;

@Component
public class StateMachineImpl implements StateMachine, ApplicationContextAware, ApplicationListener {

    protected static final Logger LOG = LogManager.getLogger(StateMachineImpl.class);

    private ApplicationContext appContext;

    @Autowired
    private TransitionService transitionService;

    @Autowired
    private JobService jobService;

    @Autowired
    private DictDAO dictDao;

    public void sendEvent(Event event, Object data, Long dictId) {
        LOG.debug("processing event..");
        Dict dict = dictDao.findById(dictId).get();
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
            dictDao.save(dict);
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
            List<Dict> parsing = dictDao.findDictByStatus(Dict.Status.PARSING.name());
            for (Dict dict : parsing) {
                dict.setStatus(Dict.Status.STORED.name());
                dictDao.save(dict);
            }
            List<Dict> translating = dictDao.findDictByStatus(Dict.Status.TRANSLATING.name());
            for (Dict dict : translating) {
                dict.setStatus(Dict.Status.PARSED.name());
                dictDao.save(dict);
            }
        }
    }

}