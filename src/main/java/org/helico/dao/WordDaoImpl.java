package org.helico.dao;

import org.helico.domain.Word;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class WordDaoImpl implements WordDao {

    @PersistenceContext
    private EntityManager entityManager;

    public Word store(String langId, String value) {
        return find(langId, value)
                .orElseGet(() -> {
                    Word newWord = new Word();
                    newWord.setValue(value);
                    newWord.setLangId(langId);
                    entityManager.persist(newWord);
                    return newWord;
                });
    }

    public Word get(String langId, String value) {
        return find(langId, value).orElse(null);
    }

    private Optional<Word> find(String langId, String value) {
        return entityManager
                .createQuery("from Word where value = :value and langId = :langId", Word.class)
                .setParameter("value", value)
                .setParameter("langId", langId)
                .getResultList()
                .stream()
                .findFirst();
    }

}