package org.helico.domain;

import javax.persistence.*;

@Entity
@Table(name = "text")
public class Text {

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

    @Column(name = "orig_doc", columnDefinition="LONGBLOB")
    @Basic(fetch = FetchType.LAZY)
    private byte[] origDoc;

    @Column(name = "utf8_text", columnDefinition="LONGBLOB")
    @Basic(fetch = FetchType.LAZY)
    private byte[] utfText;

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


    public byte[] getOrigDoc() {
        return origDoc;
    }

    public void setOrigDoc(byte[] origDoc) {
        this.origDoc = origDoc;
    }

    public byte[] getUtfText() {
        return utfText;
    }

    public void setUtfText(byte[] utfText) {
        this.utfText = utfText;
    }

}
