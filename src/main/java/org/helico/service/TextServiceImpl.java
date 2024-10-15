package org.helico.service;

import org.apache.log4j.Logger;
import org.helico.dao.DictDAO;
import org.helico.domain.Dict;
import org.helico.domain.Text;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.*;
import java.nio.charset.StandardCharsets;

/**
 * Created with IntelliJ IDEA.
 * Account: ddreval
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
        Text text = dict.getText();
        Reader reader = new FileReader(text.getUtfPath(), StandardCharsets.UTF_8);
        char[] buffer = new char[len];
        reader.skip(offset);
        reader.read(buffer);
        StringReader sr = new StringReader(new String(buffer));
        LOG.trace(String.format("NOT MARKED STRING Dict #%d Offset %d \n ++++++++++ \n %s \n ----------\n"
                ,dict.getId(), offset, new String(buffer)));
        return sr;
    }

}
