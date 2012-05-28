package org.helico.dao;

import org.apache.log4j.Logger;
import org.helico.domain.TranslatorProvider;
import org.helico.domain.Translator;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class TranslatorProviderDAOImpl implements TranslatorProviderDAO {

    private static Logger LOG = Logger.getLogger(TranslatorProviderDAOImpl.class);

    @Autowired
    private SessionFactory sessionFactory;

    public TranslatorProvider getProvider(Long id) {
        LOG.info(">>>>find translator provider#" + id);
    	TranslatorProvider provider = (TranslatorProvider)sessionFactory.getCurrentSession()
                .get(TranslatorProvider.class, id);
        LOG.info("<<<<find translator provider");
        return provider;
    }

    public List<TranslatorProvider> listProviders() {
        return sessionFactory.getCurrentSession().createQuery("from TranslatorProvider").list();
    }

    public List<TranslatorProvider> listProviders(String langId) {
        LOG.info(">>>>find translator providers for lang #" + langId);
        List<TranslatorProvider> result = sessionFactory.getCurrentSession().createQuery(
                "from TranslatorProvider tp inner join tp.translators as translator where translator.destLangId=?")
                .setString(0, langId)
                .list();
        LOG.info("<<<<find translator providers for lang #" + langId);
        return result;
    }

}
