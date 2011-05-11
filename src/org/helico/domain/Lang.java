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

}
