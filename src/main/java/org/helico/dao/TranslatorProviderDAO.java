package org.helico.dao;

import org.helico.domain.Translator;
import org.helico.domain.TranslatorProvider;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface TranslatorProviderDAO extends CrudRepository<TranslatorProvider, Long> {

//    Translator getTranslator(Long id);
//
//    TranslatorProvider getProvider(Long id);

    @Query("FROM TranslatorProvider")
    List<TranslatorProvider> listProviders();

    @Query("FROM TranslatorProvider tp INNER JOIN tp.translators AS translator WHERE translator.srcLangId=?1")
    List<TranslatorProvider> listProviders(String langId);

//    @Query("FROM TranslatorProvider WHERE langId=?1")
//    List<Translator> listTranslators(String langId);

}
