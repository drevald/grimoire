package org.helico.bean;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.helico.domain.Dict;
import org.helico.domain.Dict.Status;
import org.helico.service.DictService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
public class TextFileLoaderImpl implements TextFileLoader {
	
	private static final Logger LOG = Logger.getLogger(TextFileLoaderImpl.class);
	
	@Autowired
	DictService dictService;

	@Async
	public void load(Long id, InputStream is) {
		try {
			Dict dict = dictService.findDict(id);
			dict.setStatus(Status.UPLOADING);
			dictService.saveDict(dict);
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			IOUtils.copy(is, baos);
			baos.flush();
			dict = dictService.findDict(id);
			dict.setOrigDoc(baos.toByteArray());
			//dict.setUtfText(baos.toByteArray());
			dict.setStatus(Status.UPLOADED);
			dictService.saveDict(dict);
			baos.close();
			is.close();
		} catch (Exception e) {
			LOG.error(e, e);
			dictService.setStatus(id, Status.UPLOAD_FAILED);
		}
	}

}
