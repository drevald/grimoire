package org.helico.sm.handler;

import org.apache.log4j.Logger;
import org.helico.domain.Dict;
import org.helico.domain.DictWord;
import org.helico.domain.Job;
import org.helico.domain.Word;
import org.helico.service.DictWordService;
import org.helico.service.JobService;
import org.helico.service.TranslationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;


@Component("translateHandler")
public class TranslateHandler extends AbstractHandler {

    private static final Logger LOG = Logger.getLogger(TranslateHandler.class);

    private static final int WORDS_PORTION = 32;

    @Autowired
    DictWordService dictWordService;

    @Autowired
    JobService jobService;

    @Autowired
    TranslationService transService;

    public void process(Object data, Job job) throws Exception {
        Dict dict = dictService.findDict(job.getDictId());
        LOG.debug("dict=" + dict);
        Long wordsNum = dictWordService.countWords(dict.getId());
        int offset = 0;
        while (wordsNum > offset) {
            List<DictWord> dictWords = dictWordService.getWords(dict.getId(), offset, WORDS_PORTION);
            for (DictWord dictWord : dictWords) {
                Word word = dictWord.getWord();
                if (!transService.isTranslated(word.getId(), 0L)) {
                    String translation = word.getValue() + "_translated"; //todo get real translation here
                    transService.storeTranslation(word.getId(), 0L, translation);
                }
            }
            offset += WORDS_PORTION;
            jobService.setProgress(job.getId(), (int)((offset * 100)/wordsNum));
        }

    }

}
