package org.helico.domain;

import org.apache.log4j.Logger;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "lang")
public class Lang {

	private static final Logger LOG = Logger.getLogger(Lang.class);
	
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
}
