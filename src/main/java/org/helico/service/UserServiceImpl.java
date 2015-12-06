package org.helico.service;

import org.apache.log4j.Logger;
import org.helico.dao.LangDAO;
import org.helico.dao.UserDAO;
import org.helico.domain.Lang;
import org.helico.domain.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class UserServiceImpl implements UserService {
	
	private static final Logger LOG = Logger.getLogger(UserServiceImpl.class);
	
	@Autowired
	private UserDAO userDao;

	@Autowired
	private LangDAO langDao;

	@Transactional
	public void addUser(User user) {
		LOG.info("Starting counter from thread #" +  Thread.currentThread().toString());
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

    @Transactional
    public Long registerUser(String username, String password) {
        User user = new User(username, password);
        Long userId = userDao.addUser(user);
        return userId;
    }

	@Transactional
	public Long registerUser(String username, String password, String nativeLangId, Set<String> userLangIds) {
		User user = new User(username, password);
		Long userId = userDao.addUser(user);
		Set<Lang> userLangs = new HashSet<Lang>();
		for (String langId : userLangIds) {
			userLangs.add(langDao.find(langId));
		}
		user.setUserLangs(userLangs);
		user.setNativeLangId(nativeLangId);
		userDao.saveUser(user);
		return userId;
	}

	@Transactional
	public Long updateUser(Long userId, String username, String password, String nativeLangId, Set<String> userLangIds) {
		User user = userDao.findUser(username);
		Set<Lang> userLangs = new HashSet<Lang>();
		for (String langId : userLangIds) {
			userLangs.add(langDao.find(langId));
		}
		user.setUserLangs(userLangs);
		user.setNativeLangId(nativeLangId);
		user.setPassword(password);
		userDao.saveUser(user);
		return userId;
	}

}
