package org.helico.domain;

import org.apache.log4j.Logger;

import javax.persistence.*;

@Entity
@Table(name = "text")
public class Text {

    private static final Logger LOG = Logger.getLogger(Text.class);

    @Id
	@Column(name = "id")
	@GeneratedValue
	private Long id;

	@Column(name = "orig_path")
	private String origPath;

	@Column(name = "utf8_path")
	private String utfPath;

	@Column(name = "encoding")
	private String encoding;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getOrigPath() {
        return origPath;
    }

    public void setOrigPath(String origPath) {
        this.origPath = origPath;
    }

    public String getUtfPath() {
        return utfPath;
    }

    public void setUtfPath(String utfPath) {
        this.utfPath = utfPath;
    }

    public String getEncoding() {
        return encoding;
    }

    public void setEncoding(String encoding) {
        this.encoding = encoding;
    }
}
