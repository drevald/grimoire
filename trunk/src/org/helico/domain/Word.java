package org.helico.domain;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "word")
public class Word {

    private static final String NOT_TRANSLATED = "Not translated yet";

    @Id
	@Column(name = "id")
	@GeneratedValue
	private Long id;

    @Column(name = "lang_id")
	private String langId;

    @Column(name = "value")
	private String value;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER, mappedBy = "word")
    private Set<Translation> translations;

    @Transient
    private String translation;

    public Word() {
        translations = new HashSet<Translation>();
        translation = NOT_TRANSLATED;
    }

    public Word(String value, String langId) {
        this.value = value;
        this.langId = langId;
    }

//    @OneToMany(cascade=ALL, mappedBy="translation")
//    public Set<Translation> getTranslations() {
//        return orders;
//    }

    public String getTranslation() {
        if (translations != null && translations.size() > 0) {
            return translations.iterator().next().getValue();
        }
        return NOT_TRANSLATED;
    }

    public Set<Translation> getTranslations() {
        return this.translations;
    }

    public void setTranslations(Set<Translation> translations) {
        this.translations = translations;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getLangId() {
        return langId;
    }

    public void setLangId(String langId) {
        this.langId = langId;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

}
