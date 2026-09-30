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
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class TranslationServiceImpl implements TranslationService {

    private static final Logger LOG = LoggerFactory.getLogger(TranslationServiceImpl.class);

    @Autowired
    private StateMachine stateMachine;

    @Autowired
    TranslationDao translationDao;

    @Autowired
    TranslatorProviderDao translatorProviderDao;

    @Transactional
    public String findTranslation(Long wordId, Long translatorId) {
        return translationDao.findValue(wordId, translatorId);
    }

    @Transactional
    public boolean isTranslated(Long wordId, Long translationServiceId) {
        boolean result = translationDao.isTranslated(wordId, translationServiceId);
        return result;
    }

    @Transactional
    public void storeTranslation(Long wordId, Long translatorId, String value) {
        Translation translation = new Translation();
        translation.setTranslatorId(translatorId);
        translation.setValue(value);
        translation.setWordId(wordId);
        translationDao.saveOrUpdate(translation);
    }

    @Transactional
    public List<TranslatorProvider> listProviders() {
        return translatorProviderDao.listProviders();
    }

    @Transactional
    public List<TranslatorProvider> listProviders(String langId) {
        return translatorProviderDao.listProviders(langId);
    }

    @Transactional
    public List<Translator> listTranslators(String langId) {
        return translatorProviderDao.listTranslators(langId);
    }

    @Transactional
    public List<Translator> listTranslators(String srcLangId, String destLangId) {
        return translatorProviderDao.listTranslators(srcLangId, destLangId);
    }

    @Transactional
    public TranslatorProvider getProvider(Long transProvId) {
        return translatorProviderDao.getProvider(transProvId);
    }

    @Transactional
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
