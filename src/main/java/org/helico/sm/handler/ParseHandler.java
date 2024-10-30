package org.helico.sm.handler;

import org.apache.commons.io.input.CountingInputStream;
import org.apache.log4j.Logger;
import org.helico.domain.Dict;
import org.helico.domain.Job;
import org.helico.domain.Text;
import org.helico.service.DictService;
import org.helico.service.JobService;
import org.helico.service.WordService;
import org.helico.util.WordReader;
import org.helico.util.WordReaderResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * Reads UTF-8 encoded text and detects words. By words it understands continuous
 * sets of characters separated by white space. Allowed symbols within word can be dash or new line symbol.
 * Adds word to database if not present yet.
 */

@Component("parseHandler")
public class ParseHandler extends AbstractHandler {

    private static final Logger LOG = Logger.getLogger(ParseHandler.class);

    private static final Long PROGRESS_GRANULARITY = 100L;

    @Autowired
    WordService wordService;

    @Autowired
    JobService jobService;

    @Autowired
    DictService dictService;

    public void process(Object data, Job job) throws Exception {
        Dict dict = dictService.findDict(job.getDictId());
        Text text = dict.getText();
        LOG.debug("dict=" + dict);

        File utfFile = new File(text.getUtfPath());
        long textLength = utfFile.length();
        CountingInputStream is = new CountingInputStream(
                Files.newInputStream(Paths.get(text.getUtfPath())));

        WordReader reader = new WordReader(is);
        while (reader.ready()) {
            WordReaderResult result = reader.readWord();
            if (result != null && result.isWord()) {
                String word = result.getResult();
                if (word.length() > 32) {
                    LOG.warn("The word \"" + word + "\" is too long.");
                } else {
                    wordService.store(result.getResult().toLowerCase(), dict.getLangId(), dict.getId());
                }
            }
            if (reader.getCounter() % PROGRESS_GRANULARITY == 0) {
                LOG.debug("is.getByteCount()=" + is.getByteCount() + " total=" + textLength);
                LOG.debug("!!! percentage = " + (int) ((is.getByteCount() * 100) / textLength));
                jobService.setProgress(job.getId(), (int) ((is.getByteCount() * 100) / textLength));
            }
        }

        LOG.debug("is.getByteCount()=" + is.getByteCount() + " total=" + textLength);
        LOG.debug("!!! percentage = " + (int) ((is.getByteCount() * 100) / textLength));
        jobService.setProgress(job.getId(), (int) ((is.getByteCount() * 100) / textLength));

        LOG.debug("rawUtfBytes.size=" + textLength);

        reader.close();

    }

}
