package org.helico.dao;

import org.helico.domain.DictWord;
import org.helico.domain.Word;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

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

}
