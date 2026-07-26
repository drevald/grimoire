package org.helico.service;

import org.helico.dao.DictWordDao;
import org.helico.dao.WordDao;
import org.helico.domain.DictWord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class DictWordServiceImpl implements DictWordService {

    @Autowired
    WordDao wordDao;

    @Autowired
    DictWordDao dictWordDao;

    public List<DictWord> getWords(Long dictId) {
        return dictWordDao.getWords(dictId);
    }

    public List<DictWord> getWords(Long dictId, Integer offset, Integer num) {
        return dictWordDao.getWords(dictId, offset, num);
    }

    public Long countWords(Long dictId) {
        return dictWordDao.countWords(dictId);
    }

    public Long totalWords(Long dictId) {
        return dictWordDao.totalWords(dictId);
    }

    public Map<Integer, Integer> getHistogram(Long dictId) {
        return dictWordDao.getHistogram(dictId);
    }
}
