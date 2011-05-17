package org.helico.sm.handler;

import org.apache.commons.io.input.CountingInputStream;
import org.apache.log4j.Logger;
import org.helico.domain.Dict;
import org.helico.service.JobService;
import org.helico.service.WordService;
import org.helico.sm.Handler;
import org.helico.sm.StateMachine;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import org.helico.domain.Job;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;

/**
 * Reads UTF-8 encoded text and detects words. By words it understands continuous
 * sets of characters separated by white space. Allowed symbols within word can be dash or new line symbol.
 * Adds word to database if not present yet.
 */

@Component("parseHandler")
public class ParseHandler extends AbstractHandler {

    private static final Logger LOG = Logger.getLogger(ParseHandler.class);

    @Autowired
    WordService wordService;

    public void process(Object data, Job job) throws Exception {
        Dict dict = dictService.findDict(job.getDictId());
        LOG.debug("dict="+dict);
        byte[] rawUtfBytes = dict.getUtfText();
        CountingInputStream is = new CountingInputStream(new ByteArrayInputStream(rawUtfBytes));
        InputStreamReader reader = new InputStreamReader(is, "UTF-8");
        StringBuilder sb = new StringBuilder();

        boolean readingWord = false;
        while(reader.ready()) {
            int ch = reader.read();

            if (!Character.isLetter(ch) && readingWord) {
                wordService.store(sb.toString());
                LOG.debug("WORD:"+sb.toString());
                sb.delete(0, sb.length());
                readingWord = false;
            } else if (!Character.isLetter(ch) && !readingWord) {
                continue;
            } else if (Character.isLetter(ch) && readingWord) {
                sb.append(Character.toChars(ch));
            } else if (Character.isLetter(ch) && !readingWord) {
                sb.append(Character.toChars(ch));
                readingWord = true;
            }


        }

        LOG.debug("rawUtfBytes.size="+rawUtfBytes.length);
        LOG.debug("is.getByteCount()="+is.getByteCount());
        LOG.debug("is.getCount()="+is.getCount());

        reader.close();

    }

}
