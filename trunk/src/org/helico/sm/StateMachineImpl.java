package org.helico.sm;

import org.helico.domain.Transition;
import org.springframework.stereotype.Component;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ApplicationContext;
import org.springframework.beans.factory.annotation.Autowired;

import org.helico.sm.handler.UploadHandler;
import org.helico.sm.Handler;
import org.helico.domain.Dict;
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

    public void sendEvent(Event event, Object data, Long dictId) {
        LOG.debug("processing event..");
        Dict dict = dictService.findDict(dictId);
        Transition transition = transitionService.find(event.toString(), dict.getStatus());
        LOG.debug("handler found: " +  transition.getHandlerName());
        if (transition.getHandlerName() != null) {
            Handler handler = (Handler)appContext.getBean(transition.getHandlerName());
            dict.setStatus(transition.getDestStatus());
            dictService.saveDict(dict);
            LOG.debug("handler found: " + transition.getHandlerName());
            handler.process(data, dictId);
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