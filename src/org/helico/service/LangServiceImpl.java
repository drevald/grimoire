package org.helico.service;

import org.helico.dao.JobDAO;
import org.helico.domain.Job;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class LangServiceImpl implements LangService {

    @Autowired
    LangDAO langDao;

    @Transactional
	public List<Lang> list() {
	return langDao.list();
    }
}
