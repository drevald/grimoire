package org.helico.sm.handler;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.helico.domain.Dict;
import org.helico.domain.Dict.Status;
import org.helico.service.DictService;
import org.helico.sm.Handler;

import org.helico.sm.StateMachine;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;


@Component("uploadHandler")
public class UploadHandler implements Handler {

    private static final Logger LOG = Logger.getLogger(UploadHandler.class);

    @Autowired
	DictService dictService;

    @Autowired
	private StateMachine stateMachine;

    @Async
	public void process(Object object, Long id) {
	LOG.info("processing...");
	try {
	    InputStream is = (InputStream)object;
	    Dict dict = dictService.findDict(id);
	    dictService.saveDict(dict);
	    ByteArrayOutputStream baos = new ByteArrayOutputStream();
	    IOUtils.copy(is, baos);
	    baos.flush();
	    dict = dictService.findDict(id);
	    dict.setOrigDoc(baos.toByteArray());
	    //dict.setUtfText(baos.toByteArray());
	    dictService.saveDict(dict);
	    baos.close();
	    is.close();
        stateMachine.sendEvent(StateMachine.Event.OK, null, id);
	} catch (Exception e) {
	    LOG.error(e, e);
	    dictService.setStatus(id, Status.UPLOAD_FAILED);
	}

    }

}