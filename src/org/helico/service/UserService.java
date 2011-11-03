package org.helico.service;

import org.helico.domain.User;

import java.util.List;

public interface UserService {

	public void addUser(User user);
	
	public List<User> listUsers();
	
	public void removeUser(Long id);
	
	public User findUser(String name);

    public Long registerUser(String username, String password);
}