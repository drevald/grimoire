package org.helico.service;

import org.helico.domain.Transition;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.helico.dao.TransitionDAO;

import java.util.List;

@Service
public class TransitionServiceImpl implements TransitionService {

    private static final Logger LOG = LoggerFactory.getLogger(TransitionServiceImpl.class);

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
