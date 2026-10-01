package org.helico.dao;

import org.helico.domain.DictWord;
import org.helico.domain.Word;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class DictWordDaoImpl implements DictWordDao {

    @PersistenceContext
    private EntityManager entityManager;

    public void addWord(Word word, Long dictId) {
        addWord(word, dictId, 1);
    }

    public void addWord(Word word, Long dictId, int count) {
    DictWord dictWord = entityManager
            .createQuery("from DictWord  where dictId = :dictId and word.id = :wordId", DictWord.class)
            .setParameter("dictId", dictId)
            .setParameter("wordId", word.getId())
            .getResultList()
            .stream()
            .findFirst()
            .orElseGet(() -> {
                        DictWord newDictWord = new DictWord();
                        newDictWord.setDictId(dictId);
                        newDictWord.setWord(word);
                        entityManager.persist(newDictWord);
                        return newDictWord;
                    }
            );
        dictWord.setCounter(dictWord.getCounter() + count);
    }

    public List<DictWord> getWords(Long dictId) {
        return getWords(dictId, 0, 32);
    }

    @SuppressWarnings("unchecked")
    public List<DictWord> getWords(Long dictId, Integer offset, Integer num) {
        List<DictWord> words = (List<DictWord>)entityManager
            .createQuery("from DictWord where dictId=?1 order by counter desc")
                .setParameter(1, dictId)
            .setFirstResult(offset)
            .setMaxResults(num)
            .getResultList();
        return words;
    }

    public Long countWords(Long dictId) {
        Long count = (Long)entityManager
            .createQuery("select count(*) from DictWord where dictId=?1")
                .setParameter(1, dictId)
            .getSingleResult();
        return count;
    }

    public Long totalWords(Long dictId) {
        Long count = (Long)entityManager
                .createQuery("select sum(counter) from DictWord where dictId=?1")
                .setParameter(1, dictId)
                .getSingleResult();
        return count;
    }

    @Override
    public Map<Integer, Integer> getHistogram(Long dictId) {
        Map<Integer, Integer> result = new HashMap<>();
        List resultList = entityManager
                .createNativeQuery("WITH ranked_words AS (\n" +
                        "    SELECT \n" +
                        "        word_id,\n" +
                        "        counter,\n" +
                        "        ROW_NUMBER() OVER (ORDER BY counter DESC) AS row_num\n" +
                        "    FROM dict_word\n" +
                        "    where dict_id=" + dictId + "\n" +
                        "),\n" +
                        "histogram AS (\n" +
                        "    SELECT \n" +
                        "        CEIL(row_num / 100) AS bucket,\n" +
                        "        COUNT(*) AS word_count,              \n" +
                        "        SUM(counter) AS total_occurrences\n" +
                        "    FROM ranked_words\n" +
                        "    GROUP BY CEIL(row_num / 100)\n" +
                        "    ORDER BY bucket\n" +
                        "),\n" +
                        "total_sum AS (" +
                        "    SELECT SUM(counter) AS overall_total FROM ranked_words" +
                        ") " +
                        "SELECT \n" +
                        "    bucket,\n" +
                        "    total_occurrences\n" +
                        "FROM histogram;")
                .getResultList();
        for (Object obj : resultList) {
            Object[] pair = (Object[]) obj;
            result.put(((Double) pair[0]).intValue(), ((BigDecimal) pair[1]).intValue());
        }
        return result;
    }
}
