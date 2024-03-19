package org.helico.dao;

import java.util.List;

import org.helico.domain.Account;

public interface AccountDAO {
	
	public long addAccount(Account account);

	public long saveAccount(Account account);

	public List<Account> listAccounts();
	
	public void removeAccount(Long id);

	public Account findAccount(String name);
}
