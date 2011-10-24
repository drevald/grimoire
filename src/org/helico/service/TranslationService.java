package org.helico.service;

public interface TranslationService {

    public void translateText(Long dictId);

    public boolean isTranslated(Long wordId, Long translationServiceId);

    public void storeTranslation(Long wordId, Long translatorId, String values);

}
