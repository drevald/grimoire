package org.helico.domain;

import org.apache.log4j.Logger;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "dict")
public class Dict {

	private static final Logger LOG = Logger.getLogger(Dict.class);
	
	public enum Status {
	    PERSISTED,
		UPLOADING,
		UPLOADED,
		UPLOAD_FAILED,
		SAVED,
		READING,
		PARSED,
		TRANSLATING,
		TRANSLATED
	};

    @Id
	@Column(name = "id")
	@GeneratedValue
	private Long id;
    
    @Column(name = "user_id")
	private Long userId;
    
    @Column(name = "name")
	private String name;

    @Column(name = "orig_doc", columnDefinition="LONGBLOB")
	private byte[] origDoc;

    @Column(name = "utf8_text", columnDefinition="LONGTEXT")
	private byte[] utfText;
    
    @Column(name = "status")
    private String status;
    
    @Column(name = "preview", columnDefinition="BLOB")
    private byte[] preview;

    @Column(name = "encoding")
    private String encoding;

    public String getEncoding() {
	return encoding;
    }

    public void setEncoding(String encoding) {
	this.encoding = encoding;
    }
 
    public byte[] getPreview() {
	return preview;
    }

    public void setPreview(byte[] preview) {
	this.preview = preview;
    }

    public String getStatus() {
	return status;
    }

    public void setStatus(Status status) {
	this.status = status.toString();
    }    
    
    public void setStatus(String status) {
	this.status = status;
    }

    public Long getId() {
	return id;
    }

    public void setId(Long id) {
	this.id = id;
    }

    public byte[] getUtfText() {
	return utfText;
    }

    public Long getUserId() {
	return userId;
    }

    public void setUserId(Long userId) {
	this.userId = userId;
    }

    public String getName() {
	return name;
    }

    public void setName(String name) {
	this.name = name;
    }


	public byte[] getOrigDoc() {
		return origDoc;
	}

	public void setOrigDoc(byte[] origDoc) {
		this.origDoc = origDoc;
	}

	public void setUtfText(byte[] utfText) {
		this.utfText = utfText;
	}
	
	public String toString() {
		return "dict#" + id + "#"+  this.hashCode() + ", prev:" + preview + ", data:" + origDoc + ", status:" + status;
	}
	
}

