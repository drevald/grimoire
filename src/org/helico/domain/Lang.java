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
	@GeneratedValue
	private Long id;
    
    @Column(name = "name")
	private String name;

    @Column(name = "code")
	private String code;

    @Column(name = "encodings")
	private String encodings;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getEncodings() {
        return encodings;
    }

    public void setEncodings(String encodings) {
        this.encodings = encodings;
    }
}
