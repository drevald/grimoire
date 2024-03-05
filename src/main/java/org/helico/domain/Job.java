package org.helico.domain;

import org.apache.log4j.Logger;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * Entity which encapsulates specific transition being applied to given dictionary
 */

@Entity
@Table(name = "job")
public class Job {

    @Id
	@Column(name = "id")
	@GeneratedValue
	private Long id;
    
    @Column(name = "transition_id")
	private Long transId;

    @Column(name = "dict_id")
	private Long dictId;

    @Column(name = "progress")
	private Integer progress = 0;

    @Column(name = "active")
	private Boolean active = false;

    @Column(name = "details")
	private String details;


    public Long getId() {
	return id;
    }

    public Integer getProgress() {
	return progress;
    }

    public void setProgress(Integer progress) {
	this.progress = progress;
    }

    public Long getTransId() {
        return transId;
    }

    public void setTransId(Long transId) {
        this.transId = transId;
    }

    public Long getDictId() {
        return dictId;
    }

    public void setDictId(Long dictId) {
        this.dictId = dictId;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public String getDetails() {
	return details;
    }

    public void setDetails (String details) {
	this.details = details;
    }

    public String toString() {
	return "job#" + id + "#"+  this.hashCode() + ", progr:" + progress + ", active:" + active + ", dict:" + dictId;
    }

}
