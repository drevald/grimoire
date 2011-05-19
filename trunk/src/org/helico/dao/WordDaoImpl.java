package org.helico.dao;

import org.helico.domain.Transition;
import org.helico.domain.Word;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.apache.log4j.Logger;

import java.util.List;

@Repository
public class WordDaoImpl implements WordDao {

    private static final Logger LOG = Logger.getLogger(WordDaoImpl.class);

    @Autowired
    SessionFactory sessionFactory;

    public synchronized void store(String value, Long langId) {
        Session session = sessionFactory.getCurrentSession();
        LOG.debug(">>>>saving value:"+value+" lang:"+langId);
            List check = session.createQuery("from Word where value=? and langId=?")
                    .setString(0, value).setLong(1, langId).list();
        if (check==null || check.size()==0) {
            Word word = new Word();
            word.setValue(value);
            word.setLangId(langId);
            try {
                session.saveOrUpdate(word);
            } catch (Exception e) {
                session.clear();
                LOG.warn(e, e);
            }
        }
            //todo implement
    }
}
