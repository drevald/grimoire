package org.helico.service;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.helico.dao.DictDAO;
import org.helico.domain.Dict;
import org.helico.domain.Dict.Status;
import org.helico.domain.Text;
import org.helico.sm.StateMachine;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service to work with dictionaries.
 */
@Service
public class DictServiceImpl implements DictService {

    private static final Logger LOG = Logger.getLogger(DictServiceImpl.class);

    private static final int PREVIEW_SIZE = 256;

    @Autowired
    private DictDAO dictDao;

    @Autowired
    private StateMachine stateMachine;

    @Transactional
    public long saveOriginal(Long accountId, String langId, InputStream is, String name, String storage){
        LOG.info(">>>saveOriginal start");
        Dict dict = new Dict();
        dict.setStatus(Status.PERSISTED);
        dict.setAccountId(accountId);
        dict.setLangId(langId);
        dict.setName(name.substring(0, name.indexOf(".")).toUpperCase());
        Text text = new Text();
        long stamp = System.currentTimeMillis();
        text.setOrigPath(storage + "/" + name + "." + stamp);
        text.setUtfPath(storage + "/" + name + ".utf" + "." + stamp);
        dictDao.saveText(text);
        dict.setText(text);
        long dictId = dictDao.saveDict(dict);
        try (OutputStream os = Files.newOutputStream(new File(text.getOrigPath()).toPath())) {
            IOUtils.copy(is, os);
            os.flush();
            os.close();
            is.close();
            LOG.info("<<< done dict#" + dictId);
        } catch (Exception e) {
            LOG.error(e);
        }
        if (text.getOrigPath().contains(".pdf")) {
            dict.setEncoding(StandardCharsets.UTF_8.name());
            dictDao.saveDict(dict);
            stateMachine.sendEvent(StateMachine.Event.STORE, is, dict.getId());
        }
        return dictId;
    }

    @Transactional
    public String getPreview(Long id, String encoding) {
        Text text = findDict(id).getText();
        if(text.getUtfPath().contains(".pdf")) {
            return getPdfPreview(text, encoding);
        } else {
            return getTextPreview(text, encoding);
        }
    }

    private String getPdfPreview(Text text, String encoding) {
        try {
            PDDocument document = Loader.loadPDF(new File(text.getOrigPath()));
            PDFTextStripper stripper = new PDFTextStripper();
            String pdfText = stripper.getText(document);
            return pdfText.substring(0, PREVIEW_SIZE);
        } catch (Exception e) {
            LOG.error(e);
        }
        return null;
    }

    private String getTextPreview(Text text, String encoding) {
        String origDocument = text.getOrigPath();
        try (FileInputStream fis = new FileInputStream(origDocument);) {
            byte[] sample = new byte[PREVIEW_SIZE];
            int i = fis.read(sample);
            if (i > 0) {
                return new String(sample, encoding);
            }
        } catch (Exception e) {
            LOG.error(e);
        }
        return null;
    }

    @Transactional
    public void saveDict(Dict dict) {
        LOG.info(">>>saveDict start");
        dictDao.saveDict(dict);
        LOG.info("<<<saveDict end");
    }

    @Transactional
    public void saveText(Text text) {
        LOG.info(">>>saveDict start");
        dictDao.saveText(text);
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
    public List<Dict> listDicts(Long accountId) {

        List<Dict> result = dictDao.listDicts(accountId);
        LOG.info("Number of results is " + result.size());
        return result;
    }

    @Transactional
    public void removeDict(Long id) {
        Dict dict = dictDao.findDict(id);
        Text text = dict.getText();
        if(text != null && text.getUtfPath() != null && new File(text.getUtfPath()).exists()) {
            if (new File(text.getUtfPath()).delete()) {
                LOG.debug("Converted file at " + text.getUtfPath() + " is deleted");
            }
        }
        if(text != null && text.getOrigPath() != null && new File(text.getOrigPath()).exists()) {
            if (new File(text.getOrigPath()).delete()) {
                LOG.debug("Original file at " + text.getOrigPath() + " is deleted");
            }
        }
        dictDao.removeDict(id);
    }

    @Transactional
    public Dict findDict(Long id, Long accountId) {
        LOG.info(">>>findDict start");
        Dict dict = dictDao.findDict(id, accountId);
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

//    @Transactional
//    @Deprecated
//    public Dict loadPreviewPdfFile(Long accountId, String langId, InputStream is, String name, String storage) {
//        LOG.info(">>>loadPreview start");
//        try {
//
//            ByteArrayOutputStream buffer = new ByteArrayOutputStream();
//            IOUtils.copy(is, buffer);
//            IOUtils.closeQuietly(is);
//            IOUtils.closeQuietly(buffer);
//
//            PDDocument document = PDDocument.load(buffer.toByteArray());
//            PDFTextStripper stripper = new PDFTextStripper();
//            String pdfText = stripper.getText(document);
//
//            Dict dict = new Dict();
//            dict.setLangId(langId);
//            dict.setAccountId(accountId);
//            dict.setPreview(pdfText.substring(0, PREVIEW_SIZE).getBytes());
//            dict.setName(name.substring(0, name.indexOf(".")).toUpperCase());
//            dict.setStatus(Status.PERSISTED);
//            Text text = new Text();
//            long stamp = System.currentTimeMillis();
//            text.setOrigPath(storage + "/" + name);
//            text.setUtfPath(storage + "/" + name + ".utf");
//            text.setEncoding("UTF-8");
//
//            text.setOrigDoc(buffer.toByteArray());
//            text.setUtfText(pdfText.getBytes());
//            PushbackInputStream pis = new PushbackInputStream(is);
//
//            dictDao.saveText(text);
//            dict.setText(text);
//            dictDao.saveDict(dict);
//            stateMachine.sendEvent(StateMachine.Event.LOAD, pis, dict.getId());
//            LOG.info(">>>loadPreview ends");
//            return dict;
//
//        } catch (Exception e) {
//            LOG.error(e);
//        }
//
//        return null;
//    }

//    @Transactional
//    public Dict loadPreviewFile(Long accountId, String langId, InputStream is, String name, String storage)  {
//        LOG.info(">>>loadPreview start");
//        PushbackInputStream pis = null;
//        byte[] data = new byte[PREVIEW_SIZE];
//        try {
//            pis = new PushbackInputStream(is, PREVIEW_SIZE);
//            if(pis.read(data) > 0) {
//                pis.unread(data);
//            }
//            Dict dict = new Dict();
//            dict.setLangId(langId);
//            dict.setAccountId(accountId);
//            dict.setPreview(data);
//            dict.setName(name.substring(0, name.indexOf(".")).toUpperCase());
//            dict.setStatus(Status.PERSISTED);
//            Text text = new Text();
//            long stamp = System.currentTimeMillis();
//            text.setOrigPath(storage + "/" + name + "." + stamp);
//            text.setUtfPath(storage + "/" + name + ".utf" + "." + stamp);
//            text.setEncoding("UTF-8");
//
//            ByteArrayOutputStream baos = new ByteArrayOutputStream();
//
//            IOUtils.copy(pis, baos);
//            IOUtils.closeQuietly(pis);
//            IOUtils.copy(is, baos);
//            IOUtils.closeQuietly(is);
//            IOUtils.closeQuietly(baos);
//
//            text.setOrigDoc(baos.toByteArray());
//            dictDao.saveText(text);
//            dict.setText(text);
//            dictDao.saveDict(dict);
//            stateMachine.sendEvent(StateMachine.Event.LOAD, pis, dict.getId());
//            LOG.info(">>>loadPreview ends");
//            return dict;
//        } catch (Exception e) {
//            LOG.error(e, e);
//        }
//        return null;
//    }

    @Transactional
    public Dict createDict(Long accountId, String name) {
        LOG.info(">>>createDict start");
        Dict dict = new Dict();
        dict.setAccountId(accountId);
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

    @Transactional
    public void fixStatus() {
        List<Dict> parsing = dictDao.findDictByStatus(Status.PARSING.name());
        for (Dict dict : parsing) {
            dict.setStatus(Status.STORED.name());
            dictDao.saveDict(dict);
        }
        List<Dict> translating = dictDao.findDictByStatus(Status.TRANSLATING.name());
        for (Dict dict : translating) {
            dict.setStatus(Status.PARSED.name());
            dictDao.saveDict(dict);
        }
    }

}
