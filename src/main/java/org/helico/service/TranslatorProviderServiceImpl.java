package org.helico.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.helico.dao.TranslatorProviderDAO;
import org.helico.domain.TranslatorProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class TranslatorProviderServiceImpl implements TranslatorProviderService {

    private static final Logger LOG = LoggerFactory.getLogger(TranslatorProviderServiceImpl.class);

    @Autowired
    private TranslatorProviderDAO translatorProdiverDAO;

    @Transactional
    public List<TranslatorProvider> listProviders() {
        return translatorProdiverDAO.listProviders();
    }

}
