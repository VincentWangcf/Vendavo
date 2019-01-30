package com.avnet.emasia.webquote.entity;

import java.io.Serializable;

import javax.persistence.*;


/**
 * The persistent class for the PROGRAM_TYPE database table.
 * 
 */
@Entity
@Table(name="PROGRAM_TYPE")
public class ProgramType implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@SequenceGenerator(name="PROGRAMTYPE_ID_GENERATOR", sequenceName="WQ_SEQ",allocationSize=1)
	@GeneratedValue(strategy=GenerationType.SEQUENCE, generator="PROGRAMTYPE_ID_GENERATOR")
	@Column(unique=true, nullable=false, length=20)
	private long id;
	
	@Column(name="NAME", length=20)
	private String name;

	public ProgramType() {
	}

	public long getId() {
		return this.id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

}