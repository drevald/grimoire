package org.helico.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.helico.dao.TranslationDAO;
import org.helico.dao.TranslatorProviderDAO;
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

    @Autowired
    private StateMachine stateMachine;

    @Autowired
    TranslationDAO translationDao;

    @Autowired
    TranslatorProviderDAO translatorProviderDAO;

    public String findTranslation(Long wordId, Long translatorId) {
        return translationDao.findValue(wordId, translatorId);
    }

    public boolean isTranslated(Long wordId, Long translationServiceId) {
        boolean result = translationDao.isTranslated(wordId, translationServiceId);
        return result;
    }

    public void storeTranslation(Long wordId, Long translatorId, String value) {
        Translation translation = new Translation();
        translation.setTranslatorId(translatorId);
        translation.setValue(value);
        translation.setWordId(wordId);
        translationDao.saveOrUpdate(translation);
    }

    public List<TranslatorProvider> listProviders() {
        return translatorProviderDAO.listProviders();
    }

    public List<TranslatorProvider> listProviders(String langId) {
        return translatorProviderDAO.listProviders(langId);
    }

    public List<Translator> listTranslators(String langId) {
        return translatorProviderDAO.listTranslators(langId);
    }

    public List<Translator> listTranslators(String srcLangId, String destLangId) {
        return translatorProviderDAO.listTranslators(srcLangId, destLangId);
    }

    public TranslatorProvider getProvider(Long transProvId) {
        return translatorProviderDAO.getProvider(transProvId);
    }

    public Translator getTranslator(Long transId) {
        return translatorProviderDAO.getTranslator(transId);
    }

    public void translateText(Long dictId, Long translatorId) {
        stateMachine.sendEvent(StateMachine.Event.TRANSLATE, translatorId, dictId);
    }

    public void translateText(Long dictId) {
        stateMachine.sendEvent(StateMachine.Event.TRANSLATE, null, dictId);
    }

}
