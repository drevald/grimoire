package org.helico.dao;

import org.helico.domain.Job;

/**
 * Manages Job entity
 */
public interface JobDAO {

    public Job find(Long id);

    public void saveOrUpdate(Job job);

}
