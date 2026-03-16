package org.helico.dao;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.helico.domain.Translation;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class TranslationDAOImpl implements TranslationDAO {

    private static Logger LOG = LoggerFactory.getLogger(TranslationDAOImpl.class);

    @PersistenceContext
    private EntityManager entityManager;

    public void saveOrUpdate(Translation translation) {
        LOG.info(">>>>save translation sess#" + entityManager.hashCode() + " " + translation.toString());
        if (translation.getId() == null) {
            entityManager.persist(translation);
        } else {
            entityManager.merge(translation);
        }
        entityManager.flush();
        LOG.info("<<<<saved translation sess#" + entityManager.hashCode() + " " + translation.toString());
    }

    public String findValue(Long wordId, Long translatorId) {
        List<Translation> result = entityManager.createQuery("from Translation where wordId=?1 and translatorId=?2", Translation.class)
            .setParameter(1, wordId).setParameter(2, translatorId).getResultList();
        return result.isEmpty() ? null : result.get(0).getValue();
    }

    public boolean isTranslated(Long wordId, Long translatorId) {
        List result = entityManager.createQuery("from Translation where wordId=?1 and translatorId=?2")
        .setParameter(1, wordId)
        .setParameter(2, translatorId)
        .getResultList();
        return result != null && result.size() > 0;
    }
}
