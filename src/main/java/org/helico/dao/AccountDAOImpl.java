package org.helico.dao;

import java.util.List;

import org.helico.domain.Account;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class AccountDAOImpl implements AccountDAO {
	
	@Autowired
	private SessionFactory sessionFactory;

	public long saveAccount(Account account) {
		sessionFactory.getCurrentSession().merge(account);
		return account.getId();
	}

	public long addAccount(Account account) {
		sessionFactory.getCurrentSession().save(account);
		return account.getId();
	}

	@SuppressWarnings("unchecked")
	public List<Account> listAccounts() {
		return sessionFactory.getCurrentSession().createQuery("from Account").list();
	}
	
	public Account findAccount(String name) {
		return (Account)sessionFactory.getCurrentSession().createQuery("from Account where name=?")
		.setParameter(0,name).uniqueResult();
	}

	public void removeAccount(Long id) {
		Account account = (Account) sessionFactory.getCurrentSession().load(Account.class, id);
		if (null != account) {
			sessionFactory.getCurrentSession().delete(account);
		}		
	}

}
