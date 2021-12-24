package org.helico.dao;

import org.helico.domain.Translation;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface TranslationDAO extends CrudRepository<Translation, Long> {

    @Query("FROM Translation WHERE wordId=?1 AND translationServiceId=?2")
    List<Translation> find(Long wordId, Long translationServiceId);

//    public boolean isTranslated(Long wordId, Long translationServiceId);
//
//    public void saveOrUpdate(Translation translation);

}
