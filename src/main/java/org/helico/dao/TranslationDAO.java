package org.helico.dao;

import org.helico.domain.Translation;

public interface TranslationDAO {

    public boolean isTranslated(Long wordId, Long translationServiceId);

    public void saveOrUpdate(Translation translation);

}
