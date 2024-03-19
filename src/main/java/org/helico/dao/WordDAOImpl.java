package org.helico.dao;

import org.apache.log4j.Logger;
import org.helico.domain.DictWord;
import org.helico.domain.Word;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class WordDAOImpl implements WordDAO {

    private static final Logger LOG = Logger.getLogger(WordDAOImpl.class);

    @Autowired
    SessionFactory sessionFactory;

    public synchronized Word store(String value, String langId) {

        Session session = sessionFactory.getCurrentSession();

        LOG.debug(">>>>saving value:"+value+" lang:"+langId);
            Word result = (Word)session.createQuery("from Word where value=?1 and langId=?2")
                    .setParameter(1, value).setParameter(2, langId).uniqueResult();

        if (result==null) {
            Word word = new Word();
            word.setValue(value);
            word.setLangId(langId);
            try {
                session.saveOrUpdate(word);
                return word;
            } catch (Exception e) {
                session.clear();
                LOG.warn(e, e);
                return null;
            }
        } else {
            return result;
        }

    }

    public synchronized void batchStore(List<Word> words, Long dictId) {

        Session session = sessionFactory.getCurrentSession();

        try {

            session.beginTransaction();

            for (Word word : words) {

                LOG.debug(">>>>saving value:"+word.getValue()+" lang:"+word.getLangId());
                    Word storedWord = (Word)session.createQuery("from Word where value=?1 and langId=?2")
                            .setParameter(1, word.getValue()).setParameter(2, word.getLangId()).uniqueResult();

                if (storedWord == null) {
                    Word newWord = new Word();
                    newWord.setValue(word.getValue());
                    newWord.setLangId(word.getLangId());
                    try {
                        session.saveOrUpdate(newWord);
                        storedWord = newWord;
                    } catch (Exception e) {
                        session.clear();
                        LOG.warn(e, e);
                    }
                }

                DictWord dictWord = (DictWord)session.createQuery("from DictWord where dictId=?1 and word.id=?2")
                    .setParameter(1, dictId).setParameter(2, word.getId()).uniqueResult();

                if (dictWord == null) {
                    dictWord = new DictWord();
                    dictWord.setDictId(dictId);
                    dictWord.setWord(word);
                }

                dictWord.setCounter(dictWord.getCounter() + 1);

                session.saveOrUpdate(dictWord);

            }
            session.getTransaction().commit();
        } catch (Exception e) {
            LOG.error("Batch insert failed", e);
            session.getTransaction().rollback();
        }
    }


    public synchronized Word get(String langId, String value) {

        Session session = sessionFactory.getCurrentSession();

        LOG.debug(">>>>saving value:"+value+" lang:"+langId);
        Word result = (Word)session.createQuery("from Word where value=?1 and langId=?2")
                .setParameter(1, value).setParameter(2, langId).uniqueResult();
        return result;

    }

//    LOG.debug(">>>>saving value:"+word.getValue()+" lang:"+word.getLangId());
//    Word storedWord = (Word)session.createQuery("from Word where value=?1 and langId=?1")

}