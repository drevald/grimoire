package org.helico.bean;

import java.io.FileOutputStream;
import java.io.InputStream;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Async
@Component
public class CounterImpl implements Counter {
	
	private static final Logger LOG = Logger.getLogger(CounterImpl.class);

	@Async
	public void count() {
		for (int i=0; i<10; i++) {
			try {
				Thread.sleep(1000);
				LOG.info("Counter prints " + i + " in thread #" +  Thread.currentThread().toString());
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	@Async
	public void load(InputStream is) {
		try {
			FileOutputStream fos = new FileOutputStream("D:\\out");
			IOUtils.copyLarge(is, fos);
			while(is.available() > 0) {
				int i = is.read();
				fos.write(i);
			}
			fos.close();
			is.close();
		} catch (Exception e) {
			LOG.error(e, e);
		}
	}	
	
}
