package org.helico.service;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.io.PushbackInputStream;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.helico.bean.TextFileLoader;
import org.helico.dao.DictDAO;
import org.helico.domain.Dict;
import org.helico.domain.Dict.Status;
import org.helico.sm.StateMachine;
import org.helico.sm.StateMachine.Event;

@Service
public class DictServiceImpl implements DictService {
	
    private static final Logger LOG = Logger.getLogger(DictServiceImpl.class);
	
    private static final int PREVIEW_SIZE = 1000;
    
    @Autowired
	private DictDAO dictDao;
	
    @Autowired
	private TextFileLoader textFileLoader;

    @Autowired
	private StateMachine stateMachine;


	@Transactional
	public void saveDict(Dict dict) {
	    LOG.info(">>>saveDict start");	    
	    dictDao.saveDict(dict);
	    LOG.info("<<<loadPreview end");
	}

	@Transactional
	public List<Dict> listDicts() {
	    List<Dict> result = dictDao.listDicts();
	    LOG.info("Number of results is " + result.size());
	    return result;
	}

	@Transactional
	public void removeDict(Long id) {
	    dictDao.removeDict(id);
	}

    @Transactional
	public Dict findDict(Long id) {
	    LOG.info(">>>findDict start");
	    Dict dict = dictDao.findDict(id);
	    LOG.info("<<<findDict end");
	    return dict;
    }

	@Transactional
	public void loadFile() {
		
	}

	@Transactional
	public void loadFile(Long dictId, InputStream is) {
		textFileLoader.load(dictId, is);
	}

	@Transactional
	    public Dict loadPreviewFile(Long userId, InputStream is, String name) {
	    LOG.info(">>>loadPreview start");
	    PushbackInputStream pis = new PushbackInputStream(is, PREVIEW_SIZE);
	    byte[] data = new byte[PREVIEW_SIZE];
	    try {
		pis.read(data);
		pis.unread(data);
	    } catch (Exception e) {
		LOG.error(e, e);
	    }
	    Dict dict = new Dict();
	    dict.setUserId(userId);
	    dict.setPreview(data);
	    dict.setName(name);
	    dictDao.saveDict(dict);
	    stateMachine.sendEvent(StateMachine.Event.LOAD, pis, dict.getId());
	    //textFileLoader.load(dict.getId(), is);
	    LOG.info(">>>loadPreview ends");
	    return dict;
	}

    @Transactional
	    public Dict createDict(Long userId, String name) {
	    LOG.info(">>>createDict start");
		Dict dict = new Dict();
		dict.setUserId(userId);
		dict.setName(name);
		LOG.debug("Status persisted = " + Status.PERSISTED);
		dict.setStatus("PERSISTED");
		dictDao.saveDict(dict);
	        LOG.info("<<<createDict ends");
		return dict;
	}
	
    @Transactional
    public void setStatus(Long id, Status status) {
		Dict dict = findDict(id);
		dict.setStatus(status.name());
	    LOG.info("setStatus(" + dict + ")");
		dictDao.saveDict(dict);	
	}

    @Transactional
	public void setPreview(Long id, byte[] data) {
	Dict dict = findDict(id);
	dict.setPreview(data);
    LOG.info("setPreview(" + dict + ")");
	dictDao.saveDict(dict);
    }
    
}
