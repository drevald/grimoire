package org.helico.dao;

import org.helico.domain.Transition;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class TransitionDAOImpl implements TransitionDAO {

    @PersistenceContext
    private EntityManager entityManager;


    public String getHandlerName(String event, String status) {
        Transition trans = null;
        try {
            trans = (Transition)entityManager.createQuery("from Transition where event=?1 and sourceStatus=?2")
            .setParameter(1, event).setParameter(2, status).getSingleResult();
        } catch (jakarta.persistence.NoResultException e) {
            // No result found
        }
        return (trans == null ? null : trans.getHandlerName());
    }

    public Transition find(String event, String status) {
        Transition trans = null;
        try {
            trans = (Transition)entityManager.createQuery("from Transition where event=?1 and sourceStatus=?2")
            .setParameter(1, event).setParameter(2, status).getSingleResult();
        } catch (jakarta.persistence.NoResultException e) {
            // No result found
        }
        return trans;
    }

    @SuppressWarnings("unchecked")
    public List<Transition> list() {
        return entityManager.createQuery("from Transition").getResultList();
    }

}