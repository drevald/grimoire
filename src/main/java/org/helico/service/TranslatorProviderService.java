package org.helico.service;

import org.helico.domain.TranslatorProvider;

import java.util.List;

public interface TranslatorProviderService {

    public List<TranslatorProvider> listProviders();

    public TranslatorProvider getProvider(Long id);

    public void saveProvider(TranslatorProvider provider);

    public void deleteProvider(Long id);

}
