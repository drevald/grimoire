package org.helico.sm.handler;

import org.helico.sm.Handler;
import org.springframework.stereotype.Component;

@Component("dummyHandler")
public class DummyHandler implements Handler {

    public void process(Object data, Long id) {
        return;
    }

}
