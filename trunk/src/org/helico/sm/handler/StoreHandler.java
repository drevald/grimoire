package org.helico.sm.handler;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.helico.domain.Dict;
import org.helico.service.DictService;
import org.helico.sm.Handler;
import org.helico.sm.StateMachine;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.io.ByteArrayInputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.nio.charset.Charset;

@Component("storeHandler")
public class StoreHandler implements Handler {

    private static final Logger LOG = Logger.getLogger(StoreHandler.class);

    @Autowired
    DictService dictService;

    @Autowired
    StateMachine stateMachine;

    @Async
    public void process(Object data, Long id) {
        Dict dict = dictService.findDict(id);
        try {
            InputStreamReader reader = new InputStreamReader(
                new ByteArrayInputStream(dict.getOrigDoc()), dict.getEncoding());
            StringWriter writer = new StringWriter();
            IOUtils.copyLarge(reader, writer);
            IOUtils.closeQuietly(reader);
            IOUtils.closeQuietly(writer);
            dict.setUtfText(writer.toString().getBytes("UTF-8"));
            dictService.saveDict(dict);
            stateMachine.sendEvent(StateMachine.Event.OK, null, id);
        } catch (Exception e) {
            LOG.error(e, e);
        }
    }

}
