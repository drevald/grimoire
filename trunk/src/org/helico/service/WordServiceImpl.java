package org.helico.service;

import org.helico.dao.WordDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class WordServiceImpl implements WordService {

    @Autowired
    WordDao wordDao;

    @Transactional
	public void store(String word, Long langId) {
        wordDao.store(word, langId);
    }

}
