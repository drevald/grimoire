package org.helico.dao;

import org.helico.domain.Transition;

import java.util.List;

public interface TransitionDAO {

    public String getHandlerName(String event, String status);

    public Transition find(String event, String status);

    public List<Transition> list();

}