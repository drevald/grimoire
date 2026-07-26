package org.helico.dao;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.helico.domain.Dict;
import org.helico.domain.Text;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public class DictDaoImpl implements DictDao {

    private static final Logger LOG = LoggerFactory.getLogger(DictDaoImpl.class);

    @PersistenceContext
    private EntityManager entityManager;

    @Transactional
    public void saveText(Text text) {
        LOG.info("save sess#"+entityManager.hashCode()+" " + text.toString());
        if (text.getId() == null) {
            entityManager.persist(text);
        } else {
            entityManager.merge(text);
        }
        entityManager.flush();
    }

    @Transactional
    public long saveDict(Dict dict) {
        LOG.info("save sess#"+entityManager.hashCode()+" " + dict.toString());
        if (dict.getId() == null) {
            entityManager.persist(dict);
        } else {
            entityManager.merge(dict);
        }
        entityManager.flush();
        return dict.getId();
    }

    @SuppressWarnings("unchecked")
    @Transactional(readOnly = true)
    public List<Dict> listDicts() {
        return entityManager.createQuery("from Dict").getResultList();
    }

    @SuppressWarnings("unchecked")
    @Transactional(readOnly = true)
    public List<Dict> listDicts(Long accountId) {
        return entityManager.createQuery("from Dict where accountId=?1").setParameter(1, accountId).getResultList();
    }

    @Transactional
    public void removeDict(Long id) {
        Dict dict = entityManager.getReference(Dict.class, id);
        if (null != dict) {
            Text text = dict.getText();
            if(text != null) {
                entityManager.remove(text);
            }
            entityManager.remove(dict);
        }
    }

    @Transactional(readOnly = true)
    public Dict findDict(Long id, Long accountId) {
        Dict dict = null;
        try {
            dict = (Dict)entityManager.createQuery("from Dict where id=?1 and accountId=?2")
            .setParameter(1,id).setParameter(2,accountId).getSingleResult();
        } catch (jakarta.persistence.NoResultException e) {
            // No result found
        }
        LOG.info("get sess#"+entityManager.hashCode()+" =  " + dict);
        return dict;
    }

    @Transactional(readOnly = true)
    public Dict findDict(Long id) {
        Dict dict = null;
        try {
            dict = (Dict)entityManager.createQuery("from Dict where id=?1")
            .setParameter(1,id).getSingleResult();
        } catch (jakarta.persistence.NoResultException e) {
            // No result found
        }
        LOG.info("get sess#"+entityManager.hashCode()+" =  " + dict);
        return dict;
    }

    @SuppressWarnings("unchecked")
    @Transactional(readOnly = true)
    public List<Dict> findDictByStatus(String status) {
        return entityManager.createQuery("from Dict where status=?1").setParameter(1, status).getResultList();
    }

}
