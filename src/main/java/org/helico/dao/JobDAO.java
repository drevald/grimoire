package org.helico.dao;

import org.helico.domain.Dict;
import org.helico.domain.Job;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface JobDAO extends JpaRepository<Job, Long> {

    @Query("FROM Job WHERE dictId=?1 and active=true")
    public List<Job> findActive(Long id);

    public Job findFirstByDictIdOrderByIdDesc(Long id);

}
