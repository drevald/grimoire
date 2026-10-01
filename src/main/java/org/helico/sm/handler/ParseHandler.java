package org.helico.sm.handler;

import org.apache.commons.io.input.CountingInputStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
import java.util.HashMap;
import java.util.Map;

/**
 * Reads UTF-8 encoded text and detects words. By words it understands continuous
 * sets of characters separated by white space. Allowed symbols within word can be dash or new line symbol.
 * Adds word to database if not present yet.
 */

@Component("parseHandler")
public class ParseHandler extends AbstractHandler {

    private static final Logger LOG = LoggerFactory.getLogger(ParseHandler.class);

    private static final Long PROGRESS_GRANULARITY = 100L;

    private final WordService wordService;
    private final JobService jobService;
    private final DictService dictService;

    ParseHandler (WordService wordService, JobService jobService, DictService dictService) {
        this.wordService = wordService;
        this.jobService = jobService;
        this.dictService = dictService;
    }

    public void process(Object data, Job job) throws Exception {
        Dict dict = dictService.findDict(job.getDictId());
        Text text = dict.getText();
        LOG.debug("dict=" + dict);

        File utfFile = new File(text.getUtfPath());
        long textLength = utfFile.length();
        CountingInputStream is = new CountingInputStream(
                Files.newInputStream(Paths.get(text.getUtfPath())));

        Map<String, Integer> countWords = new HashMap<>();
        WordReader reader = new WordReader(is);
        while (reader.ready()) {
            WordReaderResult result = reader.readWord();
            if (result != null && result.isWord()) {
                String word = result.getResult();
                if (word.length() > 32) {
                    LOG.warn("The word \"" + word + "\" is too long.");
                } else {
                    countWords.merge(word.toLowerCase(), 1, Integer::sum);
                }
            }

        }

        countWords.entrySet().stream().forEach(
                x -> {
                    wordService.store(x.getKey(), dict.getLangId(), dict.getId(), x.getValue());
                }
        );

        jobService.setProgress(job.getId(), (int) ((is.getByteCount() * 100) / textLength));
        reader.close();

    }


}
