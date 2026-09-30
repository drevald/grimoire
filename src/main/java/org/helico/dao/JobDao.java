package org.helico.dao;

import org.helico.domain.Job;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * Manages Job entity
 */
public interface JobDao {

    public Job find(Long id);

    void saveOrUpdate(Job job);

    List<Job> findActive(Long id);

    Job findLastOrActive(Long id);

    Map<Long, Job> findLastOrActiveByDictIds(Collection<Long> dictIds);

}
