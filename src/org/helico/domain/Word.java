package org.helico.domain;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "word")
public class Word {

    @Id
	@Column(name = "id")
	@GeneratedValue
	private Long id;

    @Column(name = "lang_id")
	private String langId;

    @Column(name = "value")
	private String value;

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
