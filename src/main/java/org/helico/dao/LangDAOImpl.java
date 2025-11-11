package org.helico.dao;

import java.util.List;

import org.helico.domain.Lang;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;

@Repository
public class LangDAOImpl implements LangDAO {

    @PersistenceContext
    private EntityManager entityManager;

    @SuppressWarnings("unchecked")
    public List<Lang> list() {
        return entityManager.createQuery("from Lang").getResultList();
    }

    public Lang find(String id) {
        return entityManager.getReference(Lang.class, id);
    }


}
