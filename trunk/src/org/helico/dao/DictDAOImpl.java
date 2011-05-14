package org.helico.dao;

import java.util.List;
import org.apache.log4j.Logger;

import org.helico.dao.DictDAO;
import org.helico.domain.Dict;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class DictDAOImpl implements DictDAO {
	
    private static final Logger LOG = Logger.getLogger(DictDAOImpl.class);

    @Autowired
	private SessionFactory sessionFactory;

	public synchronized long saveDict(Dict dict) {
	LOG.info("save sess#"+sessionFactory.getCurrentSession().hashCode()+" " + dict.toString());
		sessionFactory.getCurrentSession().saveOrUpdate(dict);
		//		sessionFactory.getCurrentSession().merge(dict);
	    return dict.getId();
	}

	@SuppressWarnings("unchecked")
	public List<Dict> listDicts() {
		return sessionFactory.getCurrentSession().createQuery("from Dict").list();
	}

	public void removeDict(Long id) {
		Dict dict = (Dict) sessionFactory.getCurrentSession().load(Dict.class, id);
	    if (null != dict) {
		sessionFactory.getCurrentSession().delete(dict);
	    }		
	}

    public synchronized Dict findDict(Long id) {
    	Dict dict = (Dict)sessionFactory.getCurrentSession().createQuery("from Dict where id=?")
	    .setLong(0,id).uniqueResult();
		LOG.info("get sess#"+sessionFactory.getCurrentSession().hashCode()+" =  " + dict.toString());
		return dict;

    }



}
