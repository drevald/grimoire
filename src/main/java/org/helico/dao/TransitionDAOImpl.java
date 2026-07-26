package org.helico.dao;

import org.helico.domain.Transition;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public class TransitionDaoImpl implements TransitionDao {

    @PersistenceContext
    private EntityManager entityManager;

    @Transactional(readOnly = true)
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

    @Transactional(readOnly = true)
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
    @Transactional(readOnly = true)
    public List<Transition> list() {
        return entityManager.createQuery("from Transition").getResultList();
    }

}
