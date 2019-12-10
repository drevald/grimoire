package org.helico.dao;

import org.apache.log4j.Logger;
import org.helico.domain.Dict;
import org.helico.domain.Text;
import org.hibernate.LockMode;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.QueryProducer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class DictDAOImpl implements DictDAO {
	
    private static final Logger LOG = Logger.getLogger(DictDAOImpl.class);

    @Autowired
	private SessionFactory sessionFactory;

	public synchronized long saveText(Text text) {
	    Session session = sessionFactory.getCurrentSession();
	    LOG.info("save sess#"+sessionFactory.getCurrentSession().hashCode()+" " + text.toString());
	    session.saveOrUpdate(text);
	    session.flush();
	    return text.getId();
	}

	public synchronized long saveDict(Dict dict) {
	    Session session = sessionFactory.getCurrentSession();
	    LOG.info("save sess#"+sessionFactory.getCurrentSession().hashCode()+" " + dict.toString());
	    session.saveOrUpdate(dict);
	    session.flush();
	    return dict.getId();
	}

	@SuppressWarnings("unchecked")
	public List<Dict> listDicts() {
		return sessionFactory.getCurrentSession().createQuery("from Dict").list();
	}

	@SuppressWarnings("unchecked")
	public List<Dict> listDicts(Long userId) {
	    return sessionFactory.getCurrentSession().createQuery("from Dict where userId=?").setLong(0, userId).list();
	}

	public void removeDict(Long id) {
	    Dict dict = (Dict) sessionFactory.getCurrentSession().load(Dict.class, id, LockMode.READ);
	    if (null != dict) {
		    sessionFactory.getCurrentSession().delete(dict);
	    }		
	}

    public synchronized Dict findDict(Long id, Long userId) {
	Session session = sessionFactory.getCurrentSession();
    	Dict dict = (Dict)session.createQuery("from Dict where id=? and userId=?")
	    .setLong(0,id).setLong(1,userId).uniqueResult();
		LOG.info("get sess#"+sessionFactory.getCurrentSession().hashCode()+" =  " + dict);
		return dict;
    }

    public synchronized Dict findDict(Long id) {
	Session session = sessionFactory.getCurrentSession();
    	Dict dict = (Dict)session.createQuery("from Dict where id=?")
	    .setLong(0,id).uniqueResult();
		LOG.info("get sess#"+sessionFactory.getCurrentSession().hashCode()+" =  " + dict);
		return dict;
    }

    @SuppressWarnings("unchecked")
	public List<Dict> findDictByStatus(String status) {
	    return ((QueryProducer)(sessionFactory.getCurrentSession())).createQuery("from Dict where status=?1").setString(1, status).list();
	}
}
