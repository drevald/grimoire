package org.helico.sm;

import org.helico.service.DictService;
import org.helico.service.JobService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.helico.domain.Dict;
import org.helico.domain.Job;

@Component
public class StartupInitializer {

    private static final Logger LOG = LoggerFactory.getLogger(StartupInitializer.class);

    @Autowired
    @Lazy
    JobService jobService;

    @Autowired
    @Lazy
    DictService dictService;

    @EventListener(ApplicationReadyEvent.class)
    public void onApplicationStart() {
        LOG.info("Application fully started! All services are available.");

        for (Dict dict : dictService.listDicts()) {
            for (Job job : jobService.getActiveJobs(dict.getId())) {
                LOG.info("START {}", job);
            }
        }

    }

}