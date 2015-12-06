package org.helico.service;

import org.helico.domain.User;

import java.util.List;
import java.util.Set;

public interface UserService {

	public void addUser(User user);
	
	public List<User> listUsers();
	
	public void removeUser(Long id);
	
	public User findUser(String name);

    public Long registerUser(String username, String password);

	public Long registerUser(String username, String password, String bativeLangId, Set<String> userLangIds);

	public Long updateUser(Long id, String username, String password, String bativeLangId, Set<String> userLangIds);

}