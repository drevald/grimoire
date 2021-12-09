package org.helico.dao;

import org.helico.domain.Transition;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class TransitionDAOImpl implements TransitionDAO {

    @Autowired
    private SessionFactory sessionFactory;


    public String getHandlerName(String event, String status) {
    	Transition trans = (Transition)sessionFactory.getCurrentSession().createQuery("from Transition where event=? and sourceStatus=?")
	    .setParameter(0, event).setParameter(1, status).uniqueResult();
	    return (trans == null ? null : trans.getHandlerName());
    }

    public Transition find(String event, String status) {
        Transition trans = (Transition)sessionFactory.getCurrentSession().createQuery("from Transition where event=? and sourceStatus=?")
	    .setParameter(0, event).setParameter(1, status).uniqueResult();
	    return trans;
    }

    @SuppressWarnings("unchecked") 
    public List<Transition> list() {
        return sessionFactory.getCurrentSession().createQuery("from Transition").list();
    }

}