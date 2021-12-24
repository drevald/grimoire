package org.helico.dao;

import org.helico.domain.Transition;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface TransitionDAO extends CrudRepository<Transition, Long> {

    @Query("SELECT handlerName FROM Transition where event=?1 and sourceStatus=?2")
    public String getHandlerName(String event, String status);

    @Query("FROM Transition where event=?1 and sourceStatus=?2")
    public Transition find(String event, String status);

    @Query("FROM Transition")
    public List<Transition> list();

}