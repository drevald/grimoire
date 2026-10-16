package org.helico.service;

import org.helico.domain.DictWord;
import org.helico.domain.Word;

import java.util.List;
import java.util.Map;
import java.util.Set;

public interface WordService {

    public void store(String word, String langId, Long dictId, int count);

    public Word getWord(String langId, String word);

    public Map<String, Word> getWords(String langId, Set<String> words);

}
