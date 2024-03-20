package org.helico.service;

import org.helico.dao.DictWordDAO;
import org.helico.dao.WordDAO;
import org.helico.domain.DictWord;
import org.helico.domain.Word;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class WordServiceImpl implements WordService {

    @Autowired
    WordDAO wordDAO;

    @Autowired
    DictWordDAO dictWordDAO;

    @Transactional
    public void store(String word, String langId, Long dictId) {

        Word newWord = wordDAO.store(word, langId);

        if(newWord != null) {
            dictWordDAO.addWord(newWord, dictId);
        }

    }

    @Transactional
    public void batchStore(List<Word> words, Long dictId) {
        wordDAO.batchStore(words, dictId);
    }

    @Transactional
    public List<DictWord> getWords(Long dictId) {
        return dictWordDAO.getWords(dictId);
    }

    @Transactional
    public Word getWord(String langId, String word) {
        return wordDAO.get(langId, word);
    }

}
