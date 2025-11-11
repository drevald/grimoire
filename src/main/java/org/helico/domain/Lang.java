package org.helico.domain;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "lang")
public class Lang {

    private static final Logger LOG = LoggerFactory.getLogger(Lang.class);

    @Id
    @Column(name = "id")
    private String id;

    @Column(name = "name")
    private String name;

    @Column(name = "encodings")
    private String encodings;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEncodings() {
        return encodings;
    }

    public void setEncodings(String encodings) {
        this.encodings = encodings;
    }

    @Override
    public boolean equals(Object obj) {
        return (obj instanceof Lang)&&(((Lang) obj).getId().equals(id));
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }

}
