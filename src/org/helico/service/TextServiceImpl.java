package org.helico.service;

import org.apache.log4j.Logger;
import org.helico.dao.DictDAO;
import org.helico.domain.Dict;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.*;

/**
 * Created with IntelliJ IDEA.
 * User: ddreval
 * Date: 05.06.14
 * Time: 17:40
 * To change this template use File | Settings | File Templates.
 */
@Service
public class TextServiceImpl implements TextService {

    private static final Logger LOG = Logger.getLogger(TextServiceImpl.class);

    @Autowired
    private DictDAO dictDao;

    @Transactional
    public Reader getTextReader(Long id) throws Exception {
        Dict dict = dictDao.findDict(id);
        String utfPath = dict.getText().getUtfPath();
        Reader fr = new InputStreamReader(new FileInputStream(utfPath), "UTF-8");
        return fr;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Transactional
    public Reader getTextReader(Long id, int offset, int len) throws Exception {
        Dict dict = dictDao.findDict(id);
        String utfPath = dict.getText().getUtfPath();
        FileInputStream fis = new FileInputStream(utfPath);
        Reader fr = new InputStreamReader(fis, "UTF-8");
        char[] buffer = new char[len];
        fr.read(buffer, offset, len);
        StringReader sr = new StringReader(new String(buffer));
        return sr;  //To change body of implemented methods use File | Settings | File Templates.
    }

}
