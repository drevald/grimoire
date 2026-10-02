package org.helico.dao;

import org.helico.domain.Word;

import java.util.List;

public interface WordDao {

    public Word store(String word, String langId);

    public Word get(String langId, String word);

}
