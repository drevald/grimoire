package org.helico.dao;

import org.helico.aop.Logged;
import org.helico.domain.TranslatorProvider;
import org.helico.domain.Translator;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Logged
@Repository
public class TranslatorProviderDAOImpl implements TranslatorProviderDAO {

    @PersistenceContext
    private EntityManager entityManager;

    public Translator getTranslator(Long id) {
        return entityManager.find(Translator.class, id);
    }

    public TranslatorProvider getProvider(Long id) {
        return entityManager.find(TranslatorProvider.class, id);
    }

    @SuppressWarnings("unchecked")
    public List<TranslatorProvider> listProviders() {
        return entityManager.createQuery("from TranslatorProvider").getResultList();
    }

    @SuppressWarnings("unchecked")
    public List<TranslatorProvider> listProviders(String langId) {
        List<TranslatorProvider> result = new ArrayList<TranslatorProvider>();
        List objResult = entityManager.createQuery(
                "from TranslatorProvider tp inner join tp.translators as translator where translator.srcLangId=?1 and tp.enabled = true")
                .setParameter(1, langId)
                .getResultList();
        for (Object obj : objResult) {
            TranslatorProvider provider = (TranslatorProvider)((Object[])obj)[0];
            result.add(provider);
        }
        return result;
    }

    @SuppressWarnings("unchecked")
    public List<Translator> listTranslators(String langId) {
        return (List<Translator>)entityManager.createQuery(
                "from Translator where srcLangId=?1 and provider.enabled = true")
                .setParameter(1, langId)
                .getResultList();
    }

    @SuppressWarnings("unchecked")
    public List<Translator> listTranslators(String srcLangId, String destLangId) {
        return (List<Translator>) entityManager.createQuery(
                "from Translator where srcLangId=?1 and destLangId=?2 and provider.enabled = true")
                .setParameter(1, srcLangId)
                .setParameter(2, destLangId)
                .getResultList();
    }

    public void saveProvider(TranslatorProvider provider) {
        if (provider.getId() == null) {
            entityManager.persist(provider);
        } else {
            entityManager.merge(provider);
        }
    }

    public void deleteProvider(Long id) {
        TranslatorProvider provider = entityManager.find(TranslatorProvider.class, id);
        if (provider != null) {
            entityManager.remove(provider);
        }
    }

}
