package org.helico.service;

import org.helico.domain.Transition;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.apache.log4j.Logger;

import org.helico.dao.TransitionDAO;

import java.util.List;

@Service
public class TransitionServiceImpl implements TransitionService {

    private static final Logger LOG = Logger.getLogger(TransitionServiceImpl.class);

    @Autowired
    TransitionDAO transitionDao;

    @Transactional
    public String getHandlerName(String event, String status) {
	    return transitionDao.getHandlerName(event, status);
    }

    @Transactional
    public Transition find(String event, String status) {
        return transitionDao.find(event, status);
    }

    @Transactional
    public List<Transition> list() {
        return transitionDao.list();
    }

}