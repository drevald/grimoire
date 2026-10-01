package org.helico.service;

import org.helico.domain.DictWord;
import org.helico.domain.Word;

import java.util.List;

public interface WordService {

    public void store(String word, String langId, Long dictId, int count);

    public Word getWord(String langId, String word);

}
