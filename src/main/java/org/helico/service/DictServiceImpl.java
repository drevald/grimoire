package org.helico.service;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.helico.dao.DictDAO;
import org.helico.domain.Dict;
import org.helico.domain.Dict.Status;
import org.helico.domain.Text;
import org.helico.sm.StateMachine;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.*;
import java.util.List;

@Service
public class DictServiceImpl implements DictService {

	protected static final Logger LOG = LogManager.getLogger(DictServiceImpl.class);

	private static final int PREVIEW_SIZE = 1000;

	@Autowired
	private DictDAO dictDao;

	@Autowired
	private StateMachine stateMachine;

	
	public void saveDict(Dict dict) {
		LOG.info(">>>saveDict start");
		dictDao.save(dict);
		LOG.info("<<<saveDict end");
	}

	
	public void saveText(Text text) {
		LOG.info(">>>saveDict start");
		//dictDao.save(text);
		LOG.info("<<<saveDict end");
	}

	public void storeDict(Dict dict) {
		LOG.info(">>>storeDict start");
		stateMachine.sendEvent(StateMachine.Event.STORE, null, dict.getId());
		LOG.info("<<<storeDict end");
	}

	
	public List<Dict> listDicts() {
		List<Dict> result = dictDao.findDict();
		LOG.info("Number of results is " + result.size());
		return result;
	}

	
	public List<Dict> listDicts(Long accountId) {
		//List<Dict> result = dictDao.listDicts(accountId);
		List<Dict> result = dictDao.findDict(accountId);
		LOG.info("Number of results is " + result.size());
		return result;
	}

	
	public void removeDict(Long id) {
		dictDao.deleteById(id);
	}

	
	public Dict findDict(Long id, Long accountId) {
		LOG.info(">>>findDict start");
		Dict dict = dictDao.findDict(id, accountId);
		LOG.info("<<<findDict end");
		return dict;
	}

	
	public Dict findDict(Long id) {
		LOG.info(">>>findDict start");
		Dict dict = dictDao.findById(id).get();
		LOG.info("<<<findDict end");
		return dict;
	}

	
	public void loadFile() {

	}

	
	public Dict loadPreviewFile(Long accountId, String langId, InputStream is, String name, String storage) {
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
		dict.setAccountId(accountId);
		dict.setPreview(data);
		dict.setName(name.substring(0, name.indexOf(".")).toUpperCase());
		dict.setStatus(Status.PERSISTED);
		Text text = new Text();
		long stamp = System.currentTimeMillis();
		text.setOrigPath(storage + "/" + name + "." + stamp);
		text.setUtfPath(storage + "/" + name + ".utf" + "." + stamp);
		text.setEncoding("UTF-8");

		ByteArrayOutputStream baos = new ByteArrayOutputStream();

		try {
			IOUtils.copy(pis, baos);
			IOUtils.closeQuietly(pis);
			try {
				IOUtils.copy(is, baos);
				IOUtils.closeQuietly(is);
			} catch (Exception ee) {
				;
			}
			IOUtils.closeQuietly(baos);
		} catch (Exception e) {
			e.printStackTrace();
		}

		text.setOrigDoc(baos.toByteArray());

		//dictDao.saveText(text); //DD
		dict.setText(text);
		dictDao.save(dict);
		stateMachine.sendEvent(StateMachine.Event.LOAD, pis, dict.getId());
//		textFileLoader.load(dict.getId(), is);
		LOG.info(">>>loadPreview ends");
		return dict;
	}

	
	public Dict createDict(Long accountId, String name) {
		LOG.info(">>>createDict start");
		Dict dict = new Dict();
		dict.setAccountId(accountId);
		dict.setName(name);
		LOG.debug("Status persisted = " + Status.PERSISTED);
		dict.setStatus("PERSISTED");
		dictDao.save(dict);
		LOG.info("<<<createDict ends");
		return dict;
	}

	
	public void setStatus(Long id, Status status) {
		Dict dict = dictDao.findById(id).get();
		dict.setStatus(status.name());
		LOG.info("setStatus(" + dict + ")");
		dictDao.save(dict);
	}

	
	public void setPreview(Long id, byte[] data) {
		Dict dict = dictDao.findById(id).get();
		dict.setPreview(data);
		LOG.info("setPreview(" + dict + ")");
		dictDao.save(dict);
	}

	public void parseText(Long dictId) {
		stateMachine.sendEvent(StateMachine.Event.PARSE, null, dictId);
	}

//	
	public void fixStatus() {
		List<Dict> parsing = dictDao.findDictByStatus(Status.PARSING.name());
		for (Dict dict : parsing) {
			dict.setStatus(Status.STORED.name());
			dictDao.save(dict);
		}
		List<Dict> translating = dictDao.findDictByStatus(Status.TRANSLATING.name());
		for (Dict dict : translating) {
			dict.setStatus(Status.PARSED.name());
			dictDao.save(dict);
		}
	}

}
