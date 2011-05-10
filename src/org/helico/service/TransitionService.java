package org.helico.service;

import org.helico.domain.Transition;

public interface TransitionService {

    public String getHandlerName(String event, String state);

    public Transition find(String event, String state);

}