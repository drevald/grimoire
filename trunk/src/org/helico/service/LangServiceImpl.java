package org.helico.service;

import org.helico.dao.LangDAO;
import org.helico.domain.Lang;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
public class LangServiceImpl implements LangService {

    @Autowired
    LangDAO langDao;

    @Transactional
	public List<Lang> list() {
	return langDao.list();
    }

}
