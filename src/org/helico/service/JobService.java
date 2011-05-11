package org.helico.service;

/**
 * Encapsulates Handler processing
 */

public interface JobService {

    public void setProgress(Long id, Integer progress);

    public void setActive(Long id, Boolean active);

}
