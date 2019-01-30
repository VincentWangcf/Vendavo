package com.avnet.emasia.webquote.entity;

import java.io.Serializable;
import javax.persistence.*;


/**
 * The persistent class for the CITY database table.
 * 
 */
@Entity
@Table(name="CITY")
public class City implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@Column(unique=true, nullable=false, length=5)
	private String id;

	@Column(length=255)
	private String name;

	//uni-directional many-to-one association to Country
	@ManyToOne
	@JoinColumn(name="COUNTRY_ID")
	private Country country;

	public City() {
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

	public Country getCountry() {
		return this.country;
	}

	public void setCountry(Country country) {
		this.country = country;
	}

}