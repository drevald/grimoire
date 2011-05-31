package org.helico.dao;

import org.helico.domain.Word;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.apache.log4j.Logger;

import java.util.List;

@Repository
public class WordDAOImpl implements WordDAO {

    private static final Logger LOG = Logger.getLogger(WordDAOImpl.class);

    @Autowired
    SessionFactory sessionFactory;

    public synchronized Word store(String value, String langId) {

        Session session = sessionFactory.getCurrentSession();

        LOG.debug(">>>>saving value:"+value+" lang:"+langId);
            Word result = (Word)session.createQuery("from Word where value=? and langId=?")
                    .setString(0, value).setString(1, langId).uniqueResult();

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
}
