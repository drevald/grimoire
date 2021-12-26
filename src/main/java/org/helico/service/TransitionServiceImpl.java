package org.helico.service;

import org.helico.domain.Transition;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import org.helico.dao.TransitionDAO;

import java.util.List;

@Service
public class TransitionServiceImpl implements TransitionService {

    protected static final Logger LOG = LogManager.getLogger(TransitionServiceImpl.class);

    @Autowired
    TransitionDAO transitionDao;

    
    public String getHandlerName(String event, String status) {
	    return transitionDao.getHandlerName(event, status);
    }

    
    public Transition find(String event, String status) {
        return transitionDao.find(event, status);
    }

    
    public List<Transition> list() {
        return transitionDao.list();
    }

}