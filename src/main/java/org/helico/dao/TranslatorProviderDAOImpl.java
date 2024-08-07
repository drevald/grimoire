package org.helico.dao;

import org.apache.log4j.Logger;
import org.helico.domain.TranslatorProvider;
import org.helico.domain.Translator;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
public class TranslatorProviderDAOImpl implements TranslatorProviderDAO {

    private static Logger LOG = Logger.getLogger(TranslatorProviderDAOImpl.class);

    @Autowired
    private SessionFactory sessionFactory;

    public Translator getTranslator(Long id) {
        LOG.info(">>>>find translator#" + id);
        Translator translator = (Translator)sessionFactory.getCurrentSession()
                .get(Translator.class, id);
        LOG.info("<<<<find translator");
        return translator;
    }

    public TranslatorProvider getProvider(Long id) {
        LOG.info(">>>>find translator provider#" + id);
        TranslatorProvider provider = (TranslatorProvider)sessionFactory.getCurrentSession()
                .get(TranslatorProvider.class, id);
        LOG.info("<<<<find translator provider");
        return provider;
    }

    @SuppressWarnings("unchecked")
    public List<TranslatorProvider> listProviders() {
        return sessionFactory.getCurrentSession().createQuery("from TranslatorProvider").list();
    }

    @SuppressWarnings("unchecked")
    public List<TranslatorProvider> listProviders(String langId) {
        LOG.info(">>>>find translator providers for lang #" + langId);
        List<TranslatorProvider> result = new ArrayList<TranslatorProvider>();
        List objResult = sessionFactory.getCurrentSession().createQuery(
                "from TranslatorProvider tp inner join tp.translators as translator where translator.srcLangId=?1")
                .setParameter(1, langId)
                .list();
        for (Object obj : objResult) {
            TranslatorProvider provider = (TranslatorProvider)((Object[])obj)[0];
            result.add(provider);
        }

        LOG.info("<<<<find translator providers for lang #" + langId);
        return result;
    }

    @SuppressWarnings("unchecked")
    public List<Translator> listTranslators(String langId) {
        LOG.info(">>>>find translators for lang #" + langId);
        List<Translator> result = (List<Translator>)sessionFactory.getCurrentSession().createQuery(
                "from Translator where srcLangId=?1")
                .setParameter(1, langId)
                .list();
        LOG.info("<<<<find translators for lang #" + langId);
        return result;
    }

}
