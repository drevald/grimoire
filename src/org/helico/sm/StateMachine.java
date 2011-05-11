package org.helico.sm;

public interface StateMachine {

    public static enum Event {
        FAIL,
	    OK,
	    LOAD,
	    STORE,
	    PARSE,
	    TRANSLATE
    };

    public void sendEvent(Event event, Object data, Long dictId);

}