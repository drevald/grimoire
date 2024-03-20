package org.helico.sm;

public interface StateMachine {

    enum Event {

        FAIL("FAIL"),
        WAIT("FAIL"),
        OK("FAIL"),
        LOAD("FAIL"),
        STORE("FAIL"),
        PARSE("FAIL"),
        TRANSLATE("FAIL");

        public final String label;

        private Event(String label) {
            this.label = label;
        }

    };


    public void sendEvent(Event event, Object data, Long dictId);

}