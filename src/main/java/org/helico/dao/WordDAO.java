package org.helico.dao;

import org.helico.domain.Word;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;


public interface WordDAO extends CrudRepository<Word, Long> {

//    public Word store(String word, String langId);
//
//    public void batchStore(List<Word> words, Long dictId);

    @Query("FROM Word WHERE langId=?1 AND value=?2")
    public List<Word> get(String langId, String word);

}
