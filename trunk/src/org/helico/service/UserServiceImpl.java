package org.helico.service;

import java.io.InputStream;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import org.apache.log4j.Logger;
import org.helico.bean.Counter;
import org.helico.dao.UserDAO;
import org.helico.domain.User;

@Service
public class UserServiceImpl implements UserService {
	
	private static final Logger LOG = Logger.getLogger(UserServiceImpl.class);
	
	@Autowired
	private UserDAO userDao;
	
	@Autowired
	private Counter counter;
	
	@Transactional
	public void addFile(InputStream is) {
		counter.load(is);
	}	

	@Transactional
	public void addUser(User user) {
		LOG.info("Starting counter from thread #" +  Thread.currentThread().toString());
		counter.count();
		LOG.info("Count finished from thread #" +  Thread.currentThread().toString());
		userDao.addUser(user);
		LOG.info("Business method done in thread #" +  Thread.currentThread().toString());
	}

	@Transactional
	public List<User> listUsers() {
	    List<User> result = userDao.listUsers();
	    LOG.info("Number of results is " + result.size());
	    return result;
	}

	@Transactional
	public void removeUser(Long id) {
		userDao.removeUser(id);
	}

	@Transactional
	public User findUser(String name) {
		User user = userDao.findUser(name);
		return user;
	}

}
