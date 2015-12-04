package org.helico.dao;

import java.util.List;

import org.helico.dao.UserDAO;
import org.helico.domain.User;
import org.helico.domain.UserLang;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class UserLangDAOImpl implements UserLangDAO {

    @Autowired
    private SessionFactory sessionFactory;

    public long addUserLang(UserLang userLang) {
        sessionFactory.getCurrentSession().save(userLang);
        return userLang.getId();
    }

}
