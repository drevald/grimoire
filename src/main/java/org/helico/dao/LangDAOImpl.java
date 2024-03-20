package org.helico.dao;

import java.util.List;

import org.helico.domain.Lang;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class LangDAOImpl implements LangDAO {

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
