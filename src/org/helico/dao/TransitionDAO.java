package org.helico.dao;

import org.helico.domain.Transition;

public interface TransitionDAO {

    public String getHandlerName(String event, String status);

    public Transition find(String event, String status);

}