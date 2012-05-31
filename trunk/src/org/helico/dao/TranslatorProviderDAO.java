package org.helico.dao;

import org.helico.domain.Translator;
import org.helico.domain.TranslatorProvider;

import java.util.List;

public interface TranslatorProviderDAO {

    public Translator getTranslator(Long id);

    public TranslatorProvider getProvider(Long id);

    public List<TranslatorProvider> listProviders();

    public List<TranslatorProvider> listProviders(String langId);

    public List<Translator> listTranslators(String langId);

}
