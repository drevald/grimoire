package org.helico.dao;

import java.util.List;
import org.helico.domain.DictWord;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface DictWordDAO extends PagingAndSortingRepository<DictWord, Long> {

    @Query("FROM DictWord WHERE dictId=?1")
    List<DictWord> getWords(Long dictId);

    List<DictWord> getAllByDictId(Long dictId, Pageable pageable);

    @Query("SELECT dw FROM DictWord as dw WHERE dw.dictId=?1 AND dw.word.id=?2")
    DictWord findFirstByDictIdWord(Long dictId, Long wordId);

}
