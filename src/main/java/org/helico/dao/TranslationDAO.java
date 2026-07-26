package org.helico.dao;

import org.helico.domain.Translation;

public interface TranslationDao {

    public boolean isTranslated(Long wordId, Long translationServiceId);

    public String findValue(Long wordId, Long translatorId);

    public void saveOrUpdate(Translation translation);

}
