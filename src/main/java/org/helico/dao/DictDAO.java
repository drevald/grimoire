package org.helico.dao;

import org.helico.domain.Dict;
import org.helico.domain.Text;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;


/**
 * written by ${name}
 */

@Repository
public interface DictDAO extends CrudRepository<Dict, Long> {

    @Query("FROM Dict WHERE id =?1 AND accountId =?2")
    Dict findDict(Long id, Long accountId);

    @Query("FROM Dict WHERE status =?1")
    List<Dict> findDictByStatus(String status);

    @Query(value = "SELECT d FROM Dict d")
    List<Dict> findDict(Long accountId);

    @Query(value = "SELECT d FROM Dict d")
    List<Dict> findDict();

}
