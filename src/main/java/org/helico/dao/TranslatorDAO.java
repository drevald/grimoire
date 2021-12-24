package org.helico.dao;


import org.helico.domain.Translator;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface TranslatorDAO extends CrudRepository<Translator, Long> {

    @Query("FROM Translator WHERE langId=?1")
    List<Translator> listTranslators(String langId);

}
