package org.helico.service;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.helico.dao.TranslationDAO;
import org.helico.dao.TranslatorDAO;
import org.helico.dao.TranslatorProviderDAO;
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

    protected static final Logger LOG = LogManager.getLogger(TranslationServiceImpl.class);

    @Autowired
	private StateMachine stateMachine;

    @Autowired
    TranslationDAO translationDao;

    @Autowired
    TranslatorDAO translatorDAO;

    @Autowired
    TranslatorProviderDAO translatorProviderDAO;

    
    public boolean isTranslated(Long wordId, Long translationServiceId) {
        List<Translation> result = translationDao.find(wordId, translationServiceId);
        return !result.isEmpty();
    }

    
    public void storeTranslation(Long wordId, Long translatorId, String value) {
        Translation translation = new Translation();
        translation.setTranslatorId(translatorId);
        translation.setValue(value);
        translation.setWordId(wordId);
        translationDao.save(translation);
    }

    
    public List<TranslatorProvider> listProviders() {
        return translatorProviderDAO.listProviders();
    }

    
    public List<TranslatorProvider> listProviders(String langId) {
        return translatorProviderDAO.listProviders(langId);
    }

    
    public List<Translator> listTranslators(String langId) {
        return translatorDAO.listTranslators(langId);
    }

    
    public TranslatorProvider getProvider(Long transProvId) {
        return translatorProviderDAO.findById(transProvId).get();
    }

    
    public Translator getTranslator(Long transId) {
        return translatorDAO.findById(transId).get();
    }

    public void translateText(Long dictId, Long translatorId) {
        stateMachine.sendEvent(StateMachine.Event.TRANSLATE, translatorId, dictId);
    }

    public void translateText(Long dictId) {
        stateMachine.sendEvent(StateMachine.Event.TRANSLATE, null, dictId);
    }

}
