package org.helico.dao;

import java.util.List;

import org.helico.domain.Account;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public class AccountDaoImpl implements AccountDao {

    @PersistenceContext
    private EntityManager entityManager;

    @Transactional
    public long saveAccount(Account account) {
        entityManager.merge(account);
        return account.getId();
    }

    @Transactional
    public long addAccount(Account account) {
        entityManager.persist(account);
        return account.getId();
    }

    @SuppressWarnings("unchecked")
    @Transactional(readOnly = true)
    public List<Account> listAccounts() {
        return entityManager.createQuery("from Account").getResultList();
    }

    @Transactional(readOnly = true)
    public Account findAccount(String name) {
        return entityManager.createQuery("from Account where name=?1", Account.class)
            .setParameter(1, name)
            .getSingleResult();
    }

    @Transactional
    public void removeAccount(Long id) {
        Account account = entityManager.getReference(Account.class, id);
        if (null != account) {
            entityManager.remove(account);
        }
    }

}
