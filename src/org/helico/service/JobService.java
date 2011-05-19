package org.helico.service;

import java.util.List;
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

    public void setDetails(Long id, String details);

    public List<Job> getActiveJobs(Long dictId);

    public Job getLastOrActive(Long dictId);


}
