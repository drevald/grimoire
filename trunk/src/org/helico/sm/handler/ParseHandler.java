package org.helico.sm.handler;

import org.apache.commons.io.input.CountingInputStream;
import org.apache.log4j.Logger;
import org.helico.domain.Dict;
import org.helico.domain.Job;
import org.helico.service.JobService;
import org.helico.service.WordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;

/**
 * Reads UTF-8 encoded text and detects words. By words it understands continuous
 * sets of characters separated by white space. Allowed symbols within word can be dash or new line symbol.
 * Adds word to database if not present yet.
 */

@Component("parseHandler")
public class ParseHandler extends AbstractHandler {

    private static final Logger LOG = Logger.getLogger(ParseHandler.class);

    private static final Long PROGRESS_GRANULARITY = 100L;

    private static final Long WAIT_FOR_TEXT_PARSED = 1000L;

    @Autowired
    WordService wordService;

    @Autowired
    JobService jobService;

    public void process(Object data, Job job) throws Exception {
        Dict dict = dictService.findDict(job.getDictId());
        LOG.debug("dict="+dict);
//        byte[] rawUtfBytes = dict.getUtfText();
//        do {
//            rawUtfBytes = dict.getUtfText();
//            try {
//                Thread.currentThread().sleep(WAIT_FOR_TEXT_PARSED);
//            } catch (InterruptedException ie) {
//                LOG.warn(ie, ie);
//            }
//        } while (rawUtfBytes == null);
//        CountingInputStream is = new CountingInputStream(new ByteArrayInputStream(rawUtfBytes));
        File utfFile = new File(dict.getText().getUtfPath());
        CountingInputStream is = new CountingInputStream(
                new FileInputStream(utfFile));
        InputStreamReader reader = new InputStreamReader(is, "UTF-8");
        StringBuilder sb = new StringBuilder();
	    long counter = 0L;
        boolean readingWord = false;
        //List<Word> words = new ArrayList<Word>();

        while(reader.ready()) {

            int ch = reader.read();

            if (!Character.isLetter(ch) && readingWord) {
                wordService.store(sb.toString().toLowerCase(), dict.getLangId(), dict.getId());
                //words.add(new Word(sb.toString().toLowerCase(), dict.getLangId()));
                LOG.trace("WORD:"+sb.toString());
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

            if (++counter % PROGRESS_GRANULARITY == 0) {
                LOG.debug("is.getByteCount()="+is.getByteCount()+" total=" + utfFile.length());
                LOG.debug("!!! percentage = "+(int)((is.getByteCount() * 100) / utfFile.length()));
                jobService.setProgress(job.getId(), (int)((is.getByteCount() * 100) / utfFile.length()));
//                wordService.batchStore(words, dict.getId());
//                words.clear();
            }

        }

        LOG.debug("is.getByteCount()="+is.getByteCount()+" total=" + utfFile.length());
        LOG.debug("!!! percentage = "+(int)((is.getByteCount() * 100) / utfFile.length()));
        jobService.setProgress(job.getId(), (int)((is.getByteCount() * 100) / utfFile.length()));
//        wordService.batchStore(words, dict.getId());
//        words.clear();

        LOG.debug("rawUtfBytes.size=" + utfFile.length());

        reader.close();

    }

}
