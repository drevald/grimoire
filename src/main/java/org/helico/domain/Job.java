package org.helico.domain;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * Entity which encapsulates specific transition being applied to given dictionary
 */

@Entity
@Table(name = "job")
public class Job {

    private static final Logger LOG = LoggerFactory.getLogger(Job.class);

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
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

    //progress in percents
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

    public void setId(Long id) {
        this.id = id;
    }
}
