package org.helico.service;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.helico.dao.DictDAO;
import org.helico.domain.Dict;
import org.helico.domain.Dict.Status;
import org.helico.domain.Text;
import org.helico.sm.StateMachine;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.io.InputStream;
import java.io.PushbackInputStream;
import java.util.List;

@Service
public class DictServiceImpl implements DictService {
	
    private static final Logger LOG = Logger.getLogger(DictServiceImpl.class);
	
    private static final int PREVIEW_SIZE = 1000;
    
    @Autowired
	private DictDAO dictDao;

    @Autowired
	private StateMachine stateMachine;

	@Transactional
	public void saveDict(Dict dict) {
	    LOG.info(">>>saveDict start");	    
	    dictDao.saveDict(dict);
	    LOG.info("<<<saveDict end");
	}

    public void storeDict(Dict dict) {
        LOG.info(">>>storeDict start");
        stateMachine.sendEvent(StateMachine.Event.STORE, null, dict.getId());
        LOG.info("<<<storeDict end");
    }

    @Transactional
	public List<Dict> listDicts() {
	    List<Dict> result = dictDao.listDicts();
	    LOG.info("Number of results is " + result.size());
	    return result;
	}

    @Transactional
	public List<Dict> listDicts(Long userId) {
	    List<Dict> result = dictDao.listDicts(userId);
	    LOG.info("Number of results is " + result.size());
	    return result;
	}


	@Transactional
	public void removeDict(Long id) {
        Dict dict = dictDao.findDict(id);
        if (dict != null && dict.getText() != null) {
            try {
                FileUtils.deleteQuietly(new File(dict.getText().getOrigPath()));
                FileUtils.deleteQuietly(new File(dict.getText().getUtfPath()));
            } catch (Exception e) {
                LOG.error("Can not delete text files", e);
            }
        }
	    dictDao.removeDict(id);
	}

    @Transactional
	public Dict findDict(Long id, Long userId) {
	    LOG.info(">>>findDict start");
	    Dict dict = dictDao.findDict(id, userId);
	    LOG.info("<<<findDict end");
	    return dict;
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
	    public Dict loadPreviewFile(Long userId, String langId, InputStream is, String name, String storage) {
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
        dict.setLangId(langId);
	    dict.setUserId(userId);
	    dict.setPreview(data);
	    dict.setName(name.substring(0, name.indexOf(".")).toUpperCase());
	    dict.setStatus(Status.PERSISTED);
        Text text = new Text();
        long stamp = System.currentTimeMillis();
        text.setOrigPath(storage + "/" + name + "." + stamp);
        text.setUtfPath(storage + "/" + name + ".utf" + "." + stamp);
        text.setEncoding("UTF-8");
        dictDao.saveText(text);
        dict.setText(text);
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
        Dict dict = dictDao.findDict(id);
		dict.setStatus(status.name());
	    LOG.info("setStatus(" + dict + ")");
		dictDao.saveDict(dict);	
	}

    @Transactional
	public void setPreview(Long id, byte[] data) {
        Dict dict = dictDao.findDict(id);
        dict.setPreview(data);
        LOG.info("setPreview(" + dict + ")");
        dictDao.saveDict(dict);
    }

    public void parseText(Long dictId) {
        stateMachine.sendEvent(StateMachine.Event.PARSE, null, dictId);
    }

    public void fixStatus() {
        dictDao.fixStatus();
    }

}
