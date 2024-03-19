package org.helico.service;

import org.helico.domain.Transition;

import java.util.List;

public interface TransitionService {

    public String getHandlerName(String event, String state);

    public Transition find(String event, String state);

    public List<Transition> list();

}