package org.helico.sm.handler;

import org.apache.log4j.Logger;
import org.helico.domain.*;
import org.helico.service.JobService;
import org.helico.service.TranslationService;
import org.helico.service.WordService;
import org.helico.sm.StateMachine;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;


@Component("translateHandler")
public class TranslateHandler extends AbstractHandler {

    private static final Logger LOG = Logger.getLogger(TranslateHandler.class);

    @Autowired
    StateMachine stateMachine;

    @Autowired
    WordService wordService;

    @Autowired
    JobService jobService;

    @Autowired
    TranslationService transService;

    public void process(Object data, Job job) throws Exception {
        Dict dict = dictService.findDict(job.getDictId());
        LOG.debug("dict=" + dict);
        List<DictWord> dictWords = wordService.getWords(dict.getId());
        for (DictWord dictWord : dictWords) {
            Word word = dictWord.getWord();
            if (!transService.isTranslated(word.getId(), 0L)) {
                String translation = word.getValue() + "_translated"; //todo get real translation here
                transService.storeTranslation(word.getId(), 0L, translation);
            }
        }
    }

}
