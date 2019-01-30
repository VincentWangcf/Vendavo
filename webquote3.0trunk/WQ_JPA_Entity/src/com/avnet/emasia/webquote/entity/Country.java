package com.avnet.emasia.webquote.entity;

import java.io.Serializable;
import javax.persistence.*;


/**
 * The persistent class for the COUNTRY database table.
 * 
 */
@Entity
@Table(name="COUNTRY")
public class Country implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@Column(unique=true, nullable=false, length=5)
	private String id;

	@Column(nullable=false, length=255)
	private String name;

	public Country() {
	}

	public String getId() {
		return this.id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

}