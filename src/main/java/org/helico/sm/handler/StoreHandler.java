package org.helico.sm.handler;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.helico.domain.Dict;
import org.helico.domain.Job;
import org.helico.domain.Text;
import org.helico.service.DictService;
import org.helico.service.JobService;
import org.helico.service.TextService;
import org.helico.sm.StateMachine;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.*;

@Component("storeHandler")
public class StoreHandler extends AbstractHandler {

    private static final Logger LOG = Logger.getLogger(StoreHandler.class);

    @Autowired
    DictService dictService;

    @Autowired
    StateMachine stateMachine;

    @Autowired
    JobService jobService;

    @Override
    protected void process(Object object, Job job) throws Exception {
        Dict dict = dictService.findDict(job.getDictId());
        Text text = dict.getText();
        ByteArrayInputStream bais = new ByteArrayInputStream(text.getOrigDoc());
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        IOUtils.copyLarge(bais, baos);
        IOUtils.closeQuietly(bais);
        IOUtils.closeQuietly(baos);
        text.setUtfText(baos.toByteArray());
        dictService.saveText(text);
        dictService.saveDict(dict);
    }

}
