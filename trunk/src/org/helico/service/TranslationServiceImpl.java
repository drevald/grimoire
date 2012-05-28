package org.helico.service;

import org.apache.log4j.Logger;
import org.helico.dao.TranslationDAO;
import org.helico.dao.TranslatorProviderDAO;
import org.helico.domain.Translation;
import org.helico.domain.TranslatorProvider;
import org.helico.sm.StateMachine;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class TranslationServiceImpl implements TranslationService {

    private static final Logger LOG = Logger.getLogger(TranslationServiceImpl.class);

    @Autowired
	private StateMachine stateMachine;

    @Autowired
    TranslationDAO translationDao;

    @Autowired
    TranslatorProviderDAO translatorProviderDAO;

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
        return translatorProviderDAO.listProviders();  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Transactional
    public List<TranslatorProvider> listProviders(String langId) {
        return translatorProviderDAO.listProviders(langId);  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Transactional
    public TranslatorProvider getProvider(Long transProvId) {
        return translatorProviderDAO.getProvider(transProvId);  //To change body of implemented methods use File | Settings | File Templates.
    }

    public void translateText(Long dictId, Long transId) {
        stateMachine.sendEvent(StateMachine.Event.TRANSLATE, transId, dictId);
    }

    public void translateText(Long dictId) {
        stateMachine.sendEvent(StateMachine.Event.TRANSLATE, null, dictId);
    }

}
