package org.helico.service;

import org.helico.dao.DictWordDAO;
import org.helico.dao.WordDAO;
import org.helico.domain.Word;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

}
