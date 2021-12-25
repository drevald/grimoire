package org.helico.dao;

import java.util.List;
import org.helico.domain.DictWord;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

/**
 * Created by IntelliJ IDEA.
 * Account: Administrator
 * Date: 5/20/11
 * Time: 7:31 AM
 * To change this template use File | Settings | File Templates.
 */
//public interface DictWordDAO extends CrudRepository<DictWord, Long> {
public interface DictWordDAO extends PagingAndSortingRepository<DictWord, Long> {

    @Query("FROM DictWord WHERE dictId=?1")
    List<DictWord> getWords(Long dictId);

    List<DictWord> getAllByDictId(Long dictId, Pageable pageable);

//    @Query("SELECT dw FROM DictWord as dw WHERE dw.dictId=?1 AND dw.word.id=?2")
//    @Query(value="SELECT * FROM DictWord dw INNER JOIN Word w ON w.id=dw.word_id WHERE dw.dictId=?1 AND word.id=?2",
//            nativeQuery = true)
    @Query("FROM DictWord WHERE dictId=?1 AND dictId=?2")
    DictWord findFirstByDictIdWord(Long dictId, Long wordId);

}
