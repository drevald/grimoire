package org.helico.service;

import org.helico.domain.Account;

import java.util.List;
import java.util.Set;

public interface AccountService {

	public void addAccount(Account account);
	
	public List<Account> listAccounts();
	
	public void removeAccount(Long id);
	
	public Account findAccount(String name);

    public Long registerAccount(String accountname, String password);

	public Long registerAccount(String accountname, String password, String bativeLangId, Set<String> accountLangIds);

	public Long updateAccount(Long id, String accountname, String password, String bativeLangId, Set<String> accountLangIds);

}