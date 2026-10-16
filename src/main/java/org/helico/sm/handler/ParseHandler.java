package org.helico.sm.handler;

import org.helico.sm.StateMachine;
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
import org.springframework.stereotype.Component;

import java.nio.file.Files;
import java.nio.file.Path;
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

    final WordService wordService;

    ParseHandler (StateMachine stateMachine, WordService wordService, JobService jobService, DictService dictService) {
        super(stateMachine, jobService, dictService);
        this.wordService = wordService;
    }

    public void doProcess(Object data, Job job, Dict dict) throws Exception {

        Text text = dict.getText();
        LOG.debug("dict=" + dict);

        Map<String, Integer> wordsCount = new HashMap<>();
        Path path = Path.of(text.getUtfPath());
        try (WordReader reader = new WordReader(Files.newBufferedReader(path))) {
            while (reader.ready()) {
                WordReaderResult result = reader.readWord();
                if (result != null && result.isWord()) {
                    String word = result.getResult();
                    if (word.length() > 32) {
                        LOG.warn("The word \"" + word + "\" is too long.");
                    } else {
                        wordsCount.merge(word.toLowerCase(), 1, Integer::sum);
                    }
                }
            }
        }

        int total = wordsCount.size();
        int done = 0;
        int lastPercent = -1;
        for (Map.Entry<String, Integer> entry : wordsCount.entrySet()) {
            wordService.store(entry.getKey(), dict.getLangId(), dict.getId(), entry.getValue());
            int percent = (100 * done++) / total;
            if (percent != lastPercent) {
                lastPercent = percent;
                jobService.setProgress(job.getId(), lastPercent);
            }
        }
        jobService.setProgress(job.getId(), 100);

    }
}
