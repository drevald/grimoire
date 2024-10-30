package org.helico.service;

import org.helico.domain.DictWord;
import org.helico.domain.Word;

import java.util.List;

public interface WordService {

    public void batchStore(List<Word> words, Long dictId);

    public void store(String word, String langId, Long dictId);

    public List<DictWord> getWords(Long dictId);

    public Word getWord(String langId, String word);

}
