package org.helico.dao;

import org.helico.domain.Dict;
import org.helico.domain.Job;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

/**
 * Manages Job entity
 */
public interface JobDAO extends CrudRepository<Job, Long> {

//    public Job find(Long id);
//
//    public void saveOrUpdate(Job job);

//    "from Job where dictId=?1 and active=1"
    @Query("FROM Job WHERE dictId=?1 and active=true")
    public List<Job> findActive(Long id);

}
