package org.helico.service;

import org.helico.domain.Transition;

import java.util.List;

public interface TransitionService {

    public Transition find(String event, String state);

    public List<Transition> list();

}