package org.helico.dao;

import org.helico.domain.TranslatorProvider;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class TranslatorProviderDAOImpl implements TranslatorProviderDAO {

    @Autowired
    private SessionFactory sessionFactory;

    public List<TranslatorProvider> listProviders() {
        return sessionFactory.getCurrentSession().createQuery("from TranslatorProvider").list();
    }

}
