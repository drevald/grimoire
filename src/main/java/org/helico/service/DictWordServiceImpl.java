package org.helico.service;

import org.helico.dao.DictWordDAO;
import org.helico.dao.WordDAO;
import org.helico.domain.DictWord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.Pageable;

import java.util.List;

@Service
public class DictWordServiceImpl implements DictWordService {

    @Autowired
    WordDAO wordDAO;

    @Autowired
    DictWordDAO dictWordDao;

    
    public List<DictWord> getWords(Long dictId) {
	    return dictWordDao.getWords(dictId);
    }

    
	public List<DictWord> getWords(Long dictId, Integer offset, Integer num) {
        Pageable page = PageRequest.of(offset, num);
	    return dictWordDao.getAllByDictId(dictId, page);
    }

    
    public Long countWords(Long dictId) {
	    return (long)dictWordDao.getWords(dictId).size();
    }

}
