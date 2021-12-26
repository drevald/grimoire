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

	public void store(String value, String langId, Long dictId) {

        Word word;
        List<Word> words = wordDAO.get(langId, value);
        DictWord dictWord = null;
        if (words.isEmpty()) {
            word = new Word(value, langId);
            wordDAO.save(word);
        } else {
            word = words.get(0);
            dictWord = dictWordDAO.findFirstByDictIdWord(dictId, word.getId());
        }

        if(dictWord == null) {
            dictWord = new DictWord(word, dictId);
        }

        dictWord.setCounter(dictWord.getCounter() + 1);
        dictWordDAO.save(dictWord);
    }

    public void batchStore(List<Word> words, Long dictId) {
        //todo add transaction
        for (Word word : words) {
            wordDAO.save(word);
        }
    }

    public List<DictWord> getWords(Long dictId) {
        return dictWordDAO.getWords(dictId);
    }

    public Word getWord(String langId, String word) {
        //todo get unique
        List<Word> words = wordDAO.get(langId, word);
        return words.get(0);
    }

}
