package org.helico.dao;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.helico.domain.TranslatorProvider;
import org.helico.domain.Translator;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
public class TranslatorProviderDAOImpl implements TranslatorProviderDAO {

    private static Logger LOG = LoggerFactory.getLogger(TranslatorProviderDAOImpl.class);

    @PersistenceContext
    private EntityManager entityManager;

    public Translator getTranslator(Long id) {
        LOG.info(">>>>find translator#" + id);
        Translator translator = entityManager.find(Translator.class, id);
        LOG.info("<<<<find translator");
        return translator;
    }

    public TranslatorProvider getProvider(Long id) {
        LOG.info(">>>>find translator provider#" + id);
        TranslatorProvider provider = entityManager.find(TranslatorProvider.class, id);
        LOG.info("<<<<find translator provider");
        return provider;
    }

    @SuppressWarnings("unchecked")
    public List<TranslatorProvider> listProviders() {
        return entityManager.createQuery("from TranslatorProvider").getResultList();
    }

    @SuppressWarnings("unchecked")
    public List<TranslatorProvider> listProviders(String langId) {
        LOG.info(">>>>find translator providers for lang #" + langId);
        List<TranslatorProvider> result = new ArrayList<TranslatorProvider>();
        List objResult = entityManager.createQuery(
                "from TranslatorProvider tp inner join tp.translators as translator where translator.srcLangId=?1")
                .setParameter(1, langId)
                .getResultList();
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
        List<Translator> result = (List<Translator>)entityManager.createQuery(
                "from Translator where srcLangId=?1")
                .setParameter(1, langId)
                .getResultList();
        LOG.info("<<<<find translators for lang #" + langId);
        return result;
    }

    @SuppressWarnings("unchecked")
    public List<Translator> listTranslators(String srcLangId, String destLangId) {
        return (List<Translator>) entityManager.createQuery(
                "from Translator where srcLangId=?1 and destLangId=?2")
                .setParameter(1, srcLangId)
                .setParameter(2, destLangId)
                .getResultList();
    }

    public void saveProvider(TranslatorProvider provider) {
        LOG.info(">>>>save translator provider #" + provider.getId());
        if (provider.getId() == null) {
            entityManager.persist(provider);
        } else {
            entityManager.merge(provider);
        }
        LOG.info("<<<<save translator provider");
    }

    public void deleteProvider(Long id) {
        LOG.info(">>>>delete translator provider #" + id);
        TranslatorProvider provider = entityManager.find(TranslatorProvider.class, id);
        if (provider != null) {
            entityManager.remove(provider);
        }
        LOG.info("<<<<delete translator provider");
    }

}
