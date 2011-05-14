package org.helico.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "transition")
public class Transition {

    @Id
    @Column(name = "id")
    private Long id;

    @Column(name = "event")
	private String event;

    @Column(name = "src_status")
	private String sourceStatus;

    @Column(name = "dest_status")
	private String destStatus;

    @Column(name = "handler_name")
	private String handlerName;

    public String getEvent() {
	return event;
    }

    public void setEvent(String event) {
	this.event = event;
    }

    public String getSourceStatus() {
	return sourceStatus;
    }

    public void setSourceStatus(String sourceStatus) {
	this.sourceStatus = sourceStatus;
    }

    public String getDestStatus() {
	return destStatus;
    }

    public void setDestStatus(String destStatus) {
	this.destStatus = destStatus;
    }

    public String getHandlerName() {
	return handlerName;
    }

    public void setHandlerName(String handlerName) {
	this.handlerName = handlerName;
    }

    public String toString() {
        return "Transition #" + id + " for :" + event + " " + sourceStatus + "->" + destStatus + " by: " + handlerName;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}