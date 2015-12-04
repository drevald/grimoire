package org.helico.dao;

import java.util.List;

import org.helico.dao.UserDAO;
import org.helico.domain.User;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class UserDAOImpl implements UserDAO {
	
	@Autowired
	private SessionFactory sessionFactory;

	public long saveUser(User user) {
		sessionFactory.getCurrentSession().merge(user);
		return user.getId();
	}

	public long addUser(User user) {
		sessionFactory.getCurrentSession().save(user);
		return user.getId();
	}

	@SuppressWarnings("unchecked")
	public List<User> listUsers() {
		return sessionFactory.getCurrentSession().createQuery("from User").list();
	}
	
	public User findUser(String name) {
		return (User)sessionFactory.getCurrentSession().createQuery("from User where name=?")
		.setString(0,name).uniqueResult();
	}

	public void removeUser(Long id) {
		User user = (User) sessionFactory.getCurrentSession().load(User.class, id);
		if (null != user) {
			sessionFactory.getCurrentSession().delete(user);
		}		
	}

}
