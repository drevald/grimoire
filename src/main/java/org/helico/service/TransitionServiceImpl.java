package org.helico.service;

import org.helico.domain.Transition;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.helico.dao.TransitionDao;

import java.util.List;

@Service
public class TransitionServiceImpl implements TransitionService {

    private final TransitionDao transitionDao;

    TransitionServiceImpl(TransitionDao transitionDao) {
        this.transitionDao = transitionDao;
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