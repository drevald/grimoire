package org.helico.dao;

import java.util.List;
import org.apache.log4j.Logger;

import org.helico.dao.DictDAO;
import org.helico.domain.Dict;
import org.hibernate.SessionFactory;
import org.hibernate.Session;
import org.hibernate.LockMode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class DictDAOImpl implements DictDAO {
	
    private static final Logger LOG = Logger.getLogger(DictDAOImpl.class);

    @Autowired
	private SessionFactory sessionFactory;

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

	public void removeDict(Long id) {
	    Dict dict = (Dict) sessionFactory.getCurrentSession().load(Dict.class, id, LockMode.READ);
	    if (null != dict) {
		sessionFactory.getCurrentSession().delete(dict);
	    }		
	}

    public synchronized Dict findDict(Long id) {
	Session session = sessionFactory.getCurrentSession();
    	Dict dict = (Dict)session.createQuery("from Dict where id=?")
	    .setLong(0,id).uniqueResult();
		LOG.info("get sess#"+sessionFactory.getCurrentSession().hashCode()+" =  " + dict);
		return dict;

    }



}
