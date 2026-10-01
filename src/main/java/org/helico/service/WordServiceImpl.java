package org.helico.service;

import org.helico.dao.DictWordDao;
import org.helico.dao.WordDao;
import org.helico.domain.DictWord;
import org.helico.domain.Word;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class WordServiceImpl implements WordService {

    @Autowired
    WordDao wordDao;

    @Autowired
    DictWordDao dictWordDao;

    @Transactional
    public void store(String word, String langId, Long dictId, int count) {
        Word newWord = wordDao.store(word, langId);
        if(newWord != null) {
            dictWordDao.addWord(newWord, dictId, count);
        }
    }

    @Transactional
    public Word getWord(String langId, String word) {
        return wordDao.get(langId, word);
    }

}
