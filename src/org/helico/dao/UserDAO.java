package org.helico.dao;

import java.util.List;
import org.helico.domain.User;

public interface UserDAO {
	
	public long addUser(User user);
	
	public List<User> listUsers();
	
	public void removeUser(Long id);

	public User findUser(String name);
}
