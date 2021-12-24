package org.helico.service;

import org.apache.log4j.Logger;
import org.helico.dao.LangDAO;
import org.helico.dao.AccountDAO;
import org.helico.domain.Lang;
import org.helico.domain.Account;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class AccountServiceImpl implements AccountService {
	
	private static final Logger LOG = Logger.getLogger(AccountServiceImpl.class);
	
	@Autowired
	private AccountDAO accountDao;

	@Autowired
	private LangDAO langDao;

	@Transactional
	public void addAccount(Account account) {
		LOG.info("Starting counter from thread #" +  Thread.currentThread().toString());
		LOG.info("Count finished from thread #" +  Thread.currentThread().toString());
		accountDao.save(account);
		LOG.info("Business method done in thread #" +  Thread.currentThread().toString());
	}

	@Transactional
	public List<Account> listAccounts() {
		List<Account> result = new ArrayList<Account>();
		accountDao.findAll().forEach(result::add);
	    LOG.info("Number of results is " + result.size());
	    return result;
	}

	@Transactional
	public void removeAccount(Long id) {
		accountDao.deleteById(id);
	}

	@Transactional
	public Account findAccount(String name) {
		Account account = accountDao.findAccount(name);
		return account;
	}

    @Transactional
    public Long registerAccount(String accountname, String password) {
        Account account = new Account(accountname, password);
        Long accountId = accountDao.save(account).getId();
        return accountId;
    }

	@Transactional
	public Long registerAccount(String accountname, String password, String nativeLangId, Set<String> accountLangIds) {
		Account account = new Account(accountname, password);
		Long accountId = accountDao.save(account).getId();
		Set<Lang> accountLangs = new HashSet<Lang>();
		for (String langId : accountLangIds) {
			accountLangs.add(langDao.find(langId));
		}
		account.setAccountLangs(accountLangs);
		account.setNativeLangId(nativeLangId);
		accountDao.save(account);
		return accountId;
	}

	@Transactional
	public Long updateAccount(Long accountId, String accountname, String password, String nativeLangId, Set<String> accountLangIds) {
		Account account = accountDao.findAccount(accountname);
		Set<Lang> accountLangs = new HashSet<Lang>();
		for (String langId : accountLangIds) {
			accountLangs.add(langDao.find(langId));
		}
		account.setAccountLangs(accountLangs);
		account.setNativeLangId(nativeLangId);
		account.setPassword(password);
		accountDao.save(account);
		return accountId;
	}

}
