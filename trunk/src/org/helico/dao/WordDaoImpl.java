package org.helico.dao;

import org.helico.domain.Transition;
import org.helico.domain.Word;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * Created by Wiley Europe, Training & Educational Systems Russia,
 * Created by Wiley Europe, Training & Educational Systems Russia,
 *
 * @author <a href="mailto:dedreval@wiley.com">ddreval</a>
 * @version 17.05.11
 */
public class WordDaoImpl implements WordDao {

    @Autowired
    SessionFactory sessionFactory;

    public void store(String word) {
        List check = sessionFactory.getCurrentSession()
                .createQuery("from Word where value=?").setString(0, word).list();
        //todo implement

    }
}
