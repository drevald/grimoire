package org.helico.dao;

import java.util.List;
import org.helico.domain.DictWord;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class DictWordDAOImpl implements DictWordDAO {

    @Autowired
    SessionFactory sessionFactory;

    public void addWord(Long wordId, Long dictId) {

        Session session = sessionFactory.getCurrentSession();

        DictWord dictWord = (DictWord)session.createQuery("from DictWord where dictId=? and wordId=?")
                .setLong(0, dictId).setLong(1, wordId).uniqueResult();

        if (dictWord == null) {
            dictWord = new DictWord();
            dictWord.setDictId(dictId);
            dictWord.setWordId(wordId);
        }

        dictWord.setCounter(dictWord.getCounter() + 1);

        session.saveOrUpdate(dictWord);

    }

    public List<DictWord> getWords(Long dictId) {

	Session session = sessionFactory.getCurrentSession();
        List<DictWord> words = (List<DictWord>)session
	    .createQuery("from DictWord where dictId=?")
            .setLong(0, dictId)
	    .setMaxResults(10)
	    .list();

	return words;
	
    }

}
