package org.helico.dao;

import java.util.List;
import org.helico.domain.DictWord;
import org.helico.domain.Word;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

/**
 * Created by IntelliJ IDEA.
 * Account: Administrator
 * Date: 5/20/11
 * Time: 7:31 AM
 * To change this template use File | Settings | File Templates.
 */
public interface DictWordDAO extends CrudRepository<DictWord, Long> {

//    public void addWord(Word word, Long dictId);

    @Query("FROM DictWord WHERE dictId=?1")
    public List<DictWord> getWords(Long dictId);

    @Query("FROM DictWord WHERE dictId=?1 AND offset=?2 AND num=?3")
    public List<DictWord> getWords(Long dictId, Integer offset, Integer num);

}
