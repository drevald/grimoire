package org.helico.dao;

import java.util.List;

import org.helico.domain.Account;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

public interface AccountDAO extends CrudRepository<Account, Long> {
	
//	public long addAccount(Account account);
//
//	public long saveAccount(Account account);

	@Query(value = "FROM Account")
	public List<Account> listAccounts();
	
//	public void removeAccount(Long id);
//

	@Query(value = "FROM Account WHERE accountname=?1")
	Account findAccount(String name);
}
