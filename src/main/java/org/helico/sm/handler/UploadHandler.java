package org.helico.sm.handler;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.helico.domain.Dict;
import org.helico.domain.Job;
import org.helico.domain.Text;
import org.springframework.stereotype.Component;

import java.io.*;


@Component("uploadHandler")
public class UploadHandler extends AbstractHandler {

    private static final Logger LOG = Logger.getLogger(UploadHandler.class);
    
    public void process(Object object, Job job) throws Exception {
          jobService.setActive(job.getId(), true);
//          InputStream is = (InputStream)object;
            Long dictId = job.getDictId();
//	        Dict dict = dictService.findDict(dictId);
//            Text text = dict.getText();
//        //OutputStream os = new FileOutputStream(new File(text.getOrigPath()));
//        OutputStream os = new FileOutputStream(new File(text.getOrigPath()));
//          IOUtils.copy(is, os);
//          os.flush();
//          dictService.saveDict(dict);
//          os.close();
//          is.close();
          LOG.info("<<< done dict#" + dictId);
    }

}