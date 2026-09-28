package org.helico.service;

import org.helico.aop.Logged;
import org.helico.dao.TranslatorProviderDAO;
import org.helico.domain.TranslatorProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class TranslatorProviderServiceImpl implements TranslatorProviderService {

    @Autowired
    private TranslatorProviderDAO translatorProdiverDAO;

    @Transactional
    public List<TranslatorProvider> listProviders() {
        return translatorProdiverDAO.listProviders();
    }

    @Transactional
    public TranslatorProvider getProvider(Long id) {
        return translatorProdiverDAO.getProvider(id);
    }

    @Logged
    @Transactional
    public void saveProvider(TranslatorProvider provider) {
        translatorProdiverDAO.saveProvider(provider);
    }

    @Logged
    @Transactional
    public void deleteProvider(Long id) {
        translatorProdiverDAO.deleteProvider(id);
    }

}
