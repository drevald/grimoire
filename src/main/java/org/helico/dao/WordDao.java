package org.helico.dao;

import org.helico.domain.Word;

import java.util.Map;
import java.util.Set;

public interface WordDao {

    Word store(String langId, String word);

    Word get(String langId, String word);

    Map<String, Word> getWords(String langId, Set<String> words);

}
