package org.helico.dao;

import java.util.List;

import org.helico.domain.Account;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;

@Repository
public class AccountDAOImpl implements AccountDAO {

    @PersistenceContext
    private EntityManager entityManager;

    public long saveAccount(Account account) {
        entityManager.merge(account);
        return account.getId();
    }

    public long addAccount(Account account) {
        entityManager.persist(account);
        return account.getId();
    }

    @SuppressWarnings("unchecked")
    public List<Account> listAccounts() {
        return entityManager.createQuery("from Account").getResultList();
    }

    public Account findAccount(String name) {
        return entityManager.createQuery("from Account where name=?1", Account.class)
            .setParameter(1, name)
            .getSingleResult();
    }

    public void removeAccount(Long id) {
        Account account = entityManager.getReference(Account.class, id);
        if (null != account) {
            entityManager.remove(account);
        }
    }

}
