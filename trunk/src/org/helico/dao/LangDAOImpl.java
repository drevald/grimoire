package org.helico.dao;

import java.util.List;

import org.apache.log4j.Logger;
import org.helico.domain.Lang;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class LangDAOImpl implements LangDAO {

    private static final Logger LOG = Logger.getLogger(LangDAOImpl.class);

	@Autowired
	private SessionFactory sessionFactory;

    @SuppressWarnings("unchecked")
    public List<Lang> list() {
        return sessionFactory.getCurrentSession().createQuery("from Lang").list();
    }

    public Lang find(String id) {
        return (Lang)sessionFactory.getCurrentSession().load(Lang.class, id);
    }


}
