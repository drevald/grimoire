package org.helico.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.helico.dao.TranslatorProviderDao;
import org.helico.domain.TranslatorProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TranslatorProviderServiceImpl implements TranslatorProviderService {

    private static final Logger LOG = LoggerFactory.getLogger(TranslatorProviderServiceImpl.class);

    private final TranslatorProviderDao translatorProdiverDao;

    public TranslatorProviderServiceImpl(TranslatorProviderDao translatorProdiverDao) {
        this.translatorProdiverDao = translatorProdiverDao;
    }

    public List<TranslatorProvider> listProviders() {
        return translatorProdiverDao.listProviders();
    }

    public TranslatorProvider getProvider(Long id) {
        return translatorProdiverDao.getProvider(id);
    }

    public void saveProvider(TranslatorProvider provider) {
        LOG.info(">>>saveProvider start");
        translatorProdiverDao.saveProvider(provider);
        LOG.info("<<<saveProvider end");
    }

    public void deleteProvider(Long id) {
        LOG.info(">>>deleteProvider start");
        translatorProdiverDao.deleteProvider(id);
        LOG.info("<<<deleteProvider end");
    }

}
