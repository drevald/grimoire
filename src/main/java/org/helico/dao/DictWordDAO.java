package org.helico.dao;

import java.util.List;
import java.util.Map;
import org.helico.domain.DictWord;
import org.helico.domain.Word;

/**
 * Created by IntelliJ IDEA.
 * Account: Administrator
 * Date: 5/20/11
 * Time: 7:31 AM
 * To change this template use File | Settings | File Templates.
 */
public interface DictWordDAO {

    public void addWord(Word word, Long dictId);

    public List<DictWord> getWords(Long dictId);

    public List<DictWord> getWords(Long dictId, Integer offset, Integer num);

    public Long countWords(Long dictId);

    public Long totalWords(Long dictId);

        // Returns words distribution per hundreds
    // First number is a number of hundred in descending sort
    // Second is number of words from nth hundred in text, etc
    public Map<Integer, Integer> getHistogram(Long dictId);

}
