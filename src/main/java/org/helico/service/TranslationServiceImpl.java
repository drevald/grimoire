package org.helico.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.helico.dao.TranslationDao;
import org.helico.dao.TranslatorProviderDao;
import org.helico.domain.Translation;
import org.helico.domain.Translator;
import org.helico.domain.TranslatorProvider;
import org.helico.sm.StateMachine;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TranslationServiceImpl implements TranslationService {

    private static final Logger LOG = LoggerFactory.getLogger(TranslationServiceImpl.class);

    private final StateMachine stateMachine;
    private final TranslationDao translationDao;
    private final TranslatorProviderDao translatorProviderDao;

    public TranslationServiceImpl(
            StateMachine stateMachine,
            TranslationDao translationDao,
            TranslatorProviderDao translatorProviderDao)
    {
        this.stateMachine = stateMachine;
        this.translationDao = translationDao;
        this.translatorProviderDao = translatorProviderDao;
    }

    public String findTranslation(Long wordId, Long translatorId) {
        return translationDao.findValue(wordId, translatorId);
    }

    public boolean isTranslated(Long wordId, Long translationServiceId) {
        boolean result = translationDao.isTranslated(wordId, translationServiceId);
        return result;
    }

    public void storeTranslation(Long wordId, Long translatorId, String value) {
        LOG.info("storeTranslation wordId={} translatorId={} valueLength={} value={}", wordId, translatorId, value != null ? value.length() : null, value);
        Translation translation = new Translation();
        translation.setTranslatorId(translatorId);
        translation.setValue(value);
        translation.setWordId(wordId);
        translationDao.saveOrUpdate(translation);
    }

    public List<TranslatorProvider> listProviders() {
        return translatorProviderDao.listProviders();
    }

    public List<TranslatorProvider> listProviders(String langId) {
        return translatorProviderDao.listProviders(langId);
    }

    public List<Translator> listTranslators(String langId) {
        return translatorProviderDao.listTranslators(langId);
    }

    public List<Translator> listTranslators(String srcLangId, String destLangId) {
        return translatorProviderDao.listTranslators(srcLangId, destLangId);
    }

    public TranslatorProvider getProvider(Long transProvId) {
        return translatorProviderDao.getProvider(transProvId);
    }

    public Translator getTranslator(Long transId) {
        return translatorProviderDao.getTranslator(transId);
    }

    public void translateText(Long dictId, Long translatorId) {
        stateMachine.sendEvent(StateMachine.Event.TRANSLATE, translatorId, dictId);
    }

    public void translateText(Long dictId) {
        stateMachine.sendEvent(StateMachine.Event.TRANSLATE, null, dictId);
    }

}
