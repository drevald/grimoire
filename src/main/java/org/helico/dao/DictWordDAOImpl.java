package org.helico.dao;

import org.helico.domain.DictWord;
import org.helico.domain.Word;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class DictWordDAOImpl implements DictWordDAO {

    @Autowired
    SessionFactory sessionFactory;

    public void addWord(Word word, Long dictId) {

        Session session = sessionFactory.getCurrentSession();

        DictWord dictWord = (DictWord)session.createQuery("from DictWord where dictId=?1 and word.id=?2")
                .setParameter(1, dictId).setParameter(2, word.getId()).uniqueResult();

        if (dictWord == null) {
            dictWord = new DictWord();
            dictWord.setDictId(dictId);
            dictWord.setWord(word);
        }

        dictWord.setCounter(dictWord.getCounter() + 1);

        session.saveOrUpdate(dictWord);

    }

    public List<DictWord> getWords(Long dictId) {
        return getWords(dictId, 0, 32);
    }

    @SuppressWarnings("unchecked")
    public List<DictWord> getWords(Long dictId, Integer offset, Integer num) {
        Session session = sessionFactory.getCurrentSession();
            List<DictWord> words = (List<DictWord>)session
            .createQuery("from DictWord where dictId=?1 order by counter desc")
                .setParameter(1, dictId)
            .setFirstResult(offset)
            .setMaxResults(num)
            .list();
        return words;
    }

    public Long countWords(Long dictId) {
        Session session = sessionFactory.getCurrentSession();
            Long count = (Long)session
            .createQuery("select count(*) from DictWord where dictId=?1")
                .setParameter(1, dictId)
            .uniqueResult();
        return count;
    }

    @Override
    public Map<Integer, Integer> getHistogram(Long dictId) {
        Session session = sessionFactory.getCurrentSession();
        Map<Integer, Integer> result = new HashMap<>();
        List resultList = session
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
                        ")\n" +
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
