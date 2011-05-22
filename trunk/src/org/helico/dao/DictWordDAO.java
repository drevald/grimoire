package org.helico.dao;

import java.util.List;
import org.helico.domain.DictWord;
import org.helico.domain.Word;

/**
 * Created by IntelliJ IDEA.
 * User: Administrator
 * Date: 5/20/11
 * Time: 7:31 AM
 * To change this template use File | Settings | File Templates.
 */
public interface DictWordDAO {

    public void addWord(Word word, Long dictId);

    public List<DictWord> getWords(Long dictId);

    public List<DictWord> getWords(Long dictId, Integer offset, Integer num);

    public Long countWords(Long dictId);

}
