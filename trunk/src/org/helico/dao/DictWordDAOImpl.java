package org.helico.dao;

import java.util.List;
import org.helico.domain.DictWord;
import org.helico.domain.Word;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class DictWordDAOImpl implements DictWordDAO {

    @Autowired
    SessionFactory sessionFactory;

    public void addWord(Word word, Long dictId) {

        Session session = sessionFactory.getCurrentSession();

        DictWord dictWord = (DictWord)session.createQuery("from DictWord where dictId=? and word.id=?")
                .setLong(0, dictId).setLong(1, word.getId()).uniqueResult();

        if (dictWord == null) {
            dictWord = new DictWord();
            dictWord.setDictId(dictId);
            dictWord.setWord(word);
        }

        dictWord.setCounter(dictWord.getCounter() + 1);

        session.saveOrUpdate(dictWord);

    }

    public List<DictWord> getWords(Long dictId) {

	Session session = sessionFactory.getCurrentSession();
        List<DictWord> words = (List<DictWord>)session
	    .createQuery("from DictWord where dictId=? order by counter desc")
            .setLong(0, dictId)
	    .setMaxResults(200)
	    .list();

	return words;
	
    }

}
