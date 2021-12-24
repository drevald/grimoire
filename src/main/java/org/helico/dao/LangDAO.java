package org.helico.dao;

import java.util.List;
import org.helico.domain.Lang;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

public interface LangDAO extends CrudRepository<Lang, Long> {

    @Query("FROM Lang")
    public List<Lang> list();

    @Query("FROM Lang WHERE id=?1")
    public Lang find(String id);

}