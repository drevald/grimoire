package org.helico.dao;

import org.helico.domain.Job;
import java.util.List;

/**
 * Manages Job entity
 */
public interface JobDAO {

    public Job find(Long id);

    public void saveOrUpdate(Job job);

    public List<Job> findActive(Long id);

    public Job findLastOrActive(Long id);

}
