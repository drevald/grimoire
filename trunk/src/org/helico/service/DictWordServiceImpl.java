package org.helico.service;

import org.helico.dao.DictWordDAO;
import org.helico.dao.WordDAO;
import org.helico.domain.DictWord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class DictWordServiceImpl implements DictWordService {

    @Autowired
    WordDAO wordDAO;

    @Autowired
    DictWordDAO dictWordDao;

    @Transactional
    public List<DictWord> getWords(Long dictId) {
	    return dictWordDao.getWords(dictId);
    }

    @Transactional
	public List<DictWord> getWords(Long dictId, Integer offset, Integer num) {
	    return dictWordDao.getWords(dictId, offset, num);
    }

    @Transactional
    public Long countWords(Long dictId) {
	    return dictWordDao.countWords(dictId);
    }

}
