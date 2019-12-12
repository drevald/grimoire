package org.helico.dao;

import org.apache.log4j.Logger;
import org.helico.domain.Translation;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class TranslationDAOImpl implements TranslationDAO {

    private static Logger LOG = Logger.getLogger(TranslationDAOImpl.class);

    @Autowired
    SessionFactory sessionFactory;

    public void saveOrUpdate(Translation translation) {
        Session session = sessionFactory.getCurrentSession();
	    LOG.info(">>>>save translation sess#" + sessionFactory.getCurrentSession().hashCode() + " " + translation.toString());
		session.saveOrUpdate(translation);
        session.flush();
	    LOG.info("<<<<saved translation sess#" + sessionFactory.getCurrentSession().hashCode() + " " + translation.toString());
    }

    public boolean isTranslated(Long wordId, Long translatorId) {
        Session session = sessionFactory.getCurrentSession();
    	List result = session.createQuery("from Translation where wordId=?1 and translatorId=?2")
        .setLong(1, wordId)
	    .setLong(2, translatorId)
        .list();
		return result != null && result.size() > 0;
    }
}
