package org.helico.service;

import java.io.InputStream;
import java.util.List;
import org.helico.domain.User;

public interface UserService {

	public void addUser(User user);
	
	public List<User> listUsers();
	
	public void removeUser(Long id);
	
	public User findUser(String name);

}