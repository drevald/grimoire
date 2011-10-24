package org.helico.service;

import org.helico.domain.TranslatorProvider;

import java.util.List;

public interface TranslationService {

    public void translateText(Long dictId);

    public boolean isTranslated(Long wordId, Long translationServiceId);

    public void storeTranslation(Long wordId, Long translatorId, String values);

    public List<TranslatorProvider> listProviders();

}
