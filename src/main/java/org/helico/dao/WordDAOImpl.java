package org.helico.dao;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.helico.domain.DictWord;
import org.helico.domain.Word;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public class WordDaoImpl implements WordDao {

    private static final Logger LOG = LoggerFactory.getLogger(WordDaoImpl.class);

    @PersistenceContext
    private EntityManager entityManager;

    @Transactional
    public Word store(String value, String langId) {

        LOG.debug(">>>>saving value:"+value+" lang:"+langId);
        Word result = null;
        try {
            result = (Word)entityManager.createQuery("from Word where value=?1 and langId=?2")
                    .setParameter(1, value).setParameter(2, langId).getSingleResult();
        } catch (jakarta.persistence.NoResultException e) {
            // No result found
        }

        if (result==null) {
            Word word = new Word();
            word.setValue(value);
            word.setLangId(langId);
            try {
                if (word.getId() == null) {
                    entityManager.persist(word);
                } else {
                    entityManager.merge(word);
                }
                return word;
            } catch (Exception e) {
                entityManager.clear();
                LOG.warn("Warning occurred", e);
                return null;
            }
        } else {
            return result;
        }

    }

    @Transactional
    public void batchStore(List<Word> words, Long dictId) {

        try {

            for (Word word : words) {

                LOG.debug(">>>>saving value:"+word.getValue()+" lang:"+word.getLangId());
                Word storedWord = null;
                try {
                    storedWord = (Word)entityManager.createQuery("from Word where value=?1 and langId=?2")
                            .setParameter(1, word.getValue()).setParameter(2, word.getLangId()).getSingleResult();
                } catch (jakarta.persistence.NoResultException e) {
                    // No result found
                }

                if (storedWord == null) {
                    Word newWord = new Word();
                    newWord.setValue(word.getValue());
                    newWord.setLangId(word.getLangId());
                    try {
                        if (newWord.getId() == null) {
                            entityManager.persist(newWord);
                        } else {
                            entityManager.merge(newWord);
                        }
                        storedWord = newWord;
                    } catch (Exception e) {
                        entityManager.clear();
                        LOG.warn("Warning occurred", e);
                    }
                }

                DictWord dictWord = null;
                try {
                    dictWord = (DictWord)entityManager.createQuery("from DictWord where dictId=?1 and word.id=?2")
                        .setParameter(1, dictId).setParameter(2, word.getId()).getSingleResult();
                } catch (jakarta.persistence.NoResultException e) {
                    // No result found
                }

                if (dictWord == null) {
                    dictWord = new DictWord();
                    dictWord.setDictId(dictId);
                    dictWord.setWord(word);
                }

                dictWord.setCounter(dictWord.getCounter() + 1);

                if (dictWord.getId() == null) {
                    entityManager.persist(dictWord);
                } else {
                    entityManager.merge(dictWord);
                }

            }
        } catch (Exception e) {
            LOG.error("Batch insert failed", e);
        }
    }


    @Transactional(readOnly = true)
    public Word get(String langId, String value) {

        LOG.debug(">>>>saving value:"+value+" lang:"+langId);
        Word result = null;
        try {
            result = (Word)entityManager.createQuery("from Word where value=?1 and langId=?2")
                    .setParameter(1, value).setParameter(2, langId).getSingleResult();
        } catch (jakarta.persistence.NoResultException e) {
            // No result found
        }
        return result;

    }

//    LOG.debug(">>>>saving value:"+word.getValue()+" lang:"+word.getLangId());
//    Word storedWord = (Word)session.createQuery("from Word where value=?1 and langId=?1")

}
