package org.helico.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.helico.dao.TranslatorProviderDAO;
import org.helico.domain.TranslatorProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TranslatorProviderServiceImpl implements TranslatorProviderService {

    private static final Logger LOG = LoggerFactory.getLogger(TranslatorProviderServiceImpl.class);

    @Autowired
    private TranslatorProviderDAO translatorProdiverDAO;

    public List<TranslatorProvider> listProviders() {
        return translatorProdiverDAO.listProviders();
    }

    public TranslatorProvider getProvider(Long id) {
        return translatorProdiverDAO.getProvider(id);
    }

    public void saveProvider(TranslatorProvider provider) {
        LOG.info(">>>saveProvider start");
        translatorProdiverDAO.saveProvider(provider);
        LOG.info("<<<saveProvider end");
    }

    public void deleteProvider(Long id) {
        LOG.info(">>>deleteProvider start");
        translatorProdiverDAO.deleteProvider(id);
        LOG.info("<<<deleteProvider end");
    }

}
