package org.helico.sm.handler;

import org.apache.commons.io.input.CountingInputStream;
import org.apache.log4j.Logger;
import org.helico.domain.Dict;
import org.helico.domain.Job;
import org.helico.domain.Text;
import org.helico.service.JobService;
import org.helico.service.WordService;
import org.helico.util.WordReader;
import org.helico.util.WordReaderResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.ByteArrayInputStream;
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
        Text text = dict.getText();
        LOG.debug("dict="+dict);
//        File utfFile = new File(dict.getText().getUtfPath());
//        CountingInputStream is = new CountingInputStream(
//                new FileInputStream(utfFile));

        byte[] utfText = text.getUtfText();
        CountingInputStream is = new CountingInputStream(
                new ByteArrayInputStream(utfText));

        //        InputStreamReader reader = new InputStreamReader(is, "UTF-8");
//        StringBuilder sb = new StringBuilder();
//	    long counter = 0L;
//        boolean readingWord = false;
//        //List<Word> words = new ArrayList<Word>();
//
//        while(reader.ready()) {
//
//            int ch = reader.read();
//
//            if (!Character.isLetter(ch) && readingWord) {
//                wordService.store(sb.toString().toLowerCase(), dict.getLangId(), dict.getId());
//                LOG.trace("WORD:"+sb.toString());
//                sb.delete(0, sb.length());
//                readingWord = false;
//            } else if (!Character.isLetter(ch) && !readingWord) {
//                continue;
//            } else if (Character.isLetter(ch) && readingWord) {
//                sb.append(Character.toChars(ch));
//            } else if (Character.isLetter(ch) && !readingWord) {
//                sb.append(Character.toChars(ch));
//                readingWord = true;
//            }
//
//            if (++counter % PROGRESS_GRANULARITY == 0) {
//                LOG.debug("is.getByteCount()="+is.getByteCount()+" total=" + utfFile.length());
//                LOG.debug("!!! percentage = "+(int)((is.getByteCount() * 100) / utfFile.length()));
//                jobService.setProgress(job.getId(), (int)((is.getByteCount() * 100) / utfFile.length()));
////                wordService.batchStore(words, dict.getId());
////                words.clear();
//            }
//
//        }


        WordReader reader = new WordReader(is);
        while(reader.ready()) {
            WordReaderResult result = reader.readWord();
            if (result != null && result.isWord()) {
                wordService.store(result.getResult().toLowerCase(), dict.getLangId(), dict.getId());
            }
            if (reader.getCounter() % PROGRESS_GRANULARITY == 0) {
                LOG.debug("is.getByteCount()="+is.getByteCount()+" total=" + utfText.length);
                LOG.debug("!!! percentage = "+(int)((is.getByteCount() * 100) / utfText.length));
                jobService.setProgress(job.getId(), (int)((is.getByteCount() * 100) / utfText.length));
            }
        }


        LOG.debug("is.getByteCount()="+is.getByteCount()+" total=" + utfText.length);
        LOG.debug("!!! percentage = "+(int)((is.getByteCount() * 100) / utfText.length));
        jobService.setProgress(job.getId(), (int)((is.getByteCount() * 100) / utfText.length));
//        wordService.batchStore(words, dict.getId());
//        words.clear();

        LOG.debug("rawUtfBytes.size=" + utfText.length);

        reader.close();

    }

}
