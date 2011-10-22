package org.helico.domain;

import javax.persistence.*;

@Entity
@Table(name = "translation")
public class Translation {

//CREATE TABLE translation (
//  id BIGINT NOT NULL AUTO_INCREMENT,
//  translators_service_id BIGINT NOT NULL,
//  word_id BIGINT NOT NULL,
//  translator_id BIGINT NOT NULL,
//  value VARCHAR(64) NULL,
//  PRIMARY KEY(id),
//  INDEX translations_FKIndex1(translator_id, translators_service_id),
//  INDEX translations_FKIndex2(word_id)
//);

    @Id
    @Column(name = "id")
    @GeneratedValue
    private Long id;

    @Column(name = "word_id")
    private Long wordId;

    @Column(name = "translators_service_id")
    private Long translatorServiceId;

    @Column(name = "translator_id")
    private Long translatorId;

    @Column(name = "value")
    private String value;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name="word_id", insertable=false, updatable=false)
    private Word word;

    public Long getId() {
        return id;
    }

    public Long getWordId() {
        return wordId;
    }

    public Word getWord() {
        return word;
    }

    public void setWord(Word word) {
        this.word = word;
    }

    public void setWordId(Long wordId) {
        this.wordId = wordId;
    }

    public Long getTranslatorServiceId() {
        return translatorServiceId;
    }

    public void setTranslatorServiceId(Long translatorServiceId) {
        this.translatorServiceId = translatorServiceId;
    }

    public Long getTranslatorId() {
        return translatorId;
    }

    public void setTranslatorId(Long translatorId) {
        this.translatorId = translatorId;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

}
