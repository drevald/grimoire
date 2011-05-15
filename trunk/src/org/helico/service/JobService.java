package org.helico.service;

import org.helico.domain.Dict;
import org.helico.domain.Job;

/**
 * Encapsulates Handler processing
 */

public interface JobService {

    public Job createJob(Long tranId, Long dictId);

    public Job find(Long id);

    public void save(Job job);

    public void setProgress(Long id, Integer progress);

    public void setActive(Long id, Boolean active);

}
