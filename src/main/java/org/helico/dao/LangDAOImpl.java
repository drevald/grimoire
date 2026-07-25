package org.helico.dao;

import java.util.List;

import org.helico.domain.Lang;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public class LangDAOImpl implements LangDAO {

    @PersistenceContext
    private EntityManager entityManager;

    @SuppressWarnings("unchecked")
    @Transactional(readOnly = true)
    public List<Lang> list() {
        return entityManager.createQuery("from Lang").getResultList();
    }

    @Transactional(readOnly = true)
    public Lang find(String id) {
        return entityManager.getReference(Lang.class, id);
    }


}
