package org.helico.service;

import org.apache.log4j.Logger;
import org.helico.dao.TranslationDAO;
import org.helico.domain.Translation;
import org.helico.sm.StateMachine;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class TranslationServiceImpl implements TranslationService {

    private static final Logger LOG = Logger.getLogger(TranslationServiceImpl.class);

    @Autowired
	private StateMachine stateMachine;

    @Autowired
    TranslationDAO translationDao;

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

    public void translateText(Long dictId) {
        stateMachine.sendEvent(StateMachine.Event.TRANSLATE, null, dictId);
    }

}
