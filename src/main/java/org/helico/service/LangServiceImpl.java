package org.helico.service;

import org.helico.dao.LangDAO;
import org.helico.domain.Lang;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
public class LangServiceImpl implements LangService {

    private static final String ENC_SEPARATOR = ",";

    @Autowired
    LangDAO langDao;

    @Transactional
    public List<Lang> list() {
        return langDao.list();
    }

    @Transactional
    public String[] getEncodings(String id) {
        Lang lang = langDao.find(id);
        String str = lang.getEncodings();
        if (str == null) {
            return null;
        } else {
            return str.split(ENC_SEPARATOR);
        }
    }

}
