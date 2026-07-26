package org.helico.dao;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.helico.domain.Translation;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public class TranslationDaoImpl implements TranslationDao {

    private static Logger LOG = LoggerFactory.getLogger(TranslationDaoImpl.class);

    @PersistenceContext
    private EntityManager entityManager;

    @Transactional
    public void saveOrUpdate(Translation translation) {
        LOG.info(">>>>save translation sess#" + entityManager.hashCode() + " " + translation.toString());
        if (translation.getId() == null && translation.getWordId() != null) {
            // Check if a translation already exists for this word (unique constraint on word_id)
            List<Translation> existing = entityManager
                .createQuery("from Translation where wordId=?1", Translation.class)
                .setParameter(1, translation.getWordId())
                .getResultList();
            if (!existing.isEmpty()) {
                Translation t = existing.get(0);
                t.setValue(translation.getValue());
                t.setTranslatorId(translation.getTranslatorId());
                entityManager.merge(t);
                entityManager.flush();
                LOG.info("<<<<updated translation sess#" + entityManager.hashCode() + " " + t.toString());
                return;
            }
        }
        if (translation.getId() == null) {
            entityManager.persist(translation);
        } else {
            entityManager.merge(translation);
        }
        entityManager.flush();
        LOG.info("<<<<saved translation sess#" + entityManager.hashCode() + " " + translation.toString());
    }

    @Transactional(readOnly = true)
    public String findValue(Long wordId, Long translatorId) {
        List<Translation> result = entityManager.createQuery("from Translation where wordId=?1 and translatorId=?2", Translation.class)
            .setParameter(1, wordId).setParameter(2, translatorId).getResultList();
        return result.isEmpty() ? null : result.get(0).getValue();
    }

    @Transactional(readOnly = true)
    public boolean isTranslated(Long wordId, Long translatorId) {
        List result = entityManager.createQuery("from Translation where wordId=?1 and translatorId=?2")
        .setParameter(1, wordId)
        .setParameter(2, translatorId)
        .getResultList();
        return result != null && result.size() > 0;
    }
}
