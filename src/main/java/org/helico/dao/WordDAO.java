package org.helico.dao;

import org.helico.domain.Word;

import java.util.List;

public interface WordDAO {

    public Word store(String word, String langId);

    public void batchStore(List<Word> words, Long dictId);

    public Word get(String langId, String word);

}
