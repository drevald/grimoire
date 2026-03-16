package org.helico.sm;

import org.helico.service.DictService;
import org.helico.service.JobService;
import org.helico.service.TranslationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.helico.domain.Dict;
import org.helico.domain.Job;

import java.util.ArrayList;
import java.util.List;

@Component
public class StartupInitializer {

    private static final Logger LOG = LoggerFactory.getLogger(StartupInitializer.class);

    @Autowired
    @Lazy
    JobService jobService;

    @Autowired
    @Lazy
    DictService dictService;

    @Autowired
    @Lazy
    TranslationService translationService;

    @EventListener(ApplicationReadyEvent.class)
    public void onApplicationStart() {
        LOG.info("Application fully started! All services are available.");

        // Collect dicts that were translating before restart, with their translatorId from job details
        List<long[]> toResume = new ArrayList<>();
        for (Dict dict : dictService.listDicts()) {
            if ("TRANSLATING".equals(dict.getStatus())) {
                Job job = jobService.getLastOrActive(dict.getId());
                if (job != null && job.getDetails() != null) {
                    try {
                        long translatorId = Long.parseLong(job.getDetails());
                        toResume.add(new long[]{dict.getId(), translatorId});
                        LOG.info("Startup: will resume dict#{} with translator#{}", dict.getId(), translatorId);
                    } catch (NumberFormatException e) {
                        LOG.warn("Startup: dict#{} TRANSLATING but job details '{}' is not a translatorId — skipping resume",
                            dict.getId(), job.getDetails());
                    }
                } else {
                    LOG.warn("Startup: dict#{} TRANSLATING but no job found — will just reset to PARSED", dict.getId());
                }
            }
        }

        // Reset stuck dicts (STORING→PERSISTED, PARSING→STORED, TRANSLATING→PARSED)
        dictService.fixStatus();

        // Resume translations — fixStatus() TX is now committed, so dicts are PARSED and safe to re-translate
        for (long[] entry : toResume) {
            long dictId = entry[0];
            long translatorId = entry[1];
            LOG.info("Startup: resuming translation for dict#{} with translator#{}", dictId, translatorId);
            translationService.translateText(dictId, translatorId);
        }
    }

}
