package org.helico.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.helico.dao.DictDAO;
import org.helico.domain.Dict;
import org.helico.domain.Text;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * Created with IntelliJ IDEA.
 * Account: ddreval
 * Date: 05.06.14
 * Time: 17:40
 * To change this template use File | Settings | File Templates.
 */
@Service
public class TextServiceImpl implements TextService {

    private static final Logger LOG = LoggerFactory.getLogger(TextServiceImpl.class);

    @Autowired
    private DictDAO dictDao;

    public Reader getTextReader(Long id) throws Exception {
        Dict dict = dictDao.findDict(id);
        String utfPath = dict.getText().getUtfPath();
        Reader fr = new InputStreamReader(new FileInputStream(utfPath), "UTF-8");
        return fr;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public Reader getTextReader(Long id, int offset, int len) throws Exception {
        LOG.debug("Getting text reader for dict #" + id + " offset:" + offset + " len:" + len);
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

    public String getFullText(Long id) throws Exception {
        Dict dict = dictDao.findDict(id);
        return new String(Files.readAllBytes(Paths.get(dict.getText().getUtfPath())), StandardCharsets.UTF_8);
    }

    public void saveFullText(Long id, String content) throws Exception {
        Dict dict = dictDao.findDict(id);
        Files.write(Paths.get(dict.getText().getUtfPath()), content.getBytes(StandardCharsets.UTF_8));
    }

}
