package org.helico.dao;

import java.util.List;

import org.helico.domain.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

public interface AccountDAO extends JpaRepository<Account, Long> {

	@Query(value = "FROM Account")
	public List<Account> listAccounts();
	
	@Query(value = "FROM Account WHERE accountname=?1")
	Account findAccount(String name);

}
