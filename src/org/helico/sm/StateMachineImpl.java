package org.helico.sm;

import org.springframework.stereotype.Component;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ApplicationContext;
import org.helico.sm.handler.UploadHandler;
import org.helico.sm.Handler;
import org.apache.log4j.Logger;

@Component
public class StateMachineImpl implements StateMachine, ApplicationContextAware {

    private static final Logger LOG = Logger.getLogger(StateMachineImpl.class);

    private ApplicationContext appContext;

    public void sendEvent(Event event, Object data) {
	LOG.debug("processing event..");
	Handler handler = (Handler)appContext.getBean("uploadHandler");
	LOG.debug("handler found: " + handler);
	handler.process(data);
	LOG.debug("handler called");
    }

    public void setApplicationContext(ApplicationContext appContext) {
	LOG.debug("setting application context: " + appContext);
	this.appContext = appContext;
    }

}