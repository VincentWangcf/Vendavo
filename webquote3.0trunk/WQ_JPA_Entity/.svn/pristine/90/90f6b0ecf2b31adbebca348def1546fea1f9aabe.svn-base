package com.avnet.emasia.webquote.entity;

import java.io.Serializable;
import java.util.List;

import javax.persistence.*;


/**
 * The persistent class for the PLANT database table.
 * 
 */
@Entity
@Table(name="PLANT")
public class Plant implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@Column(unique=true, nullable=false, length=4)
	private String name;

	@ManyToMany
	@JoinTable(
		name="PLANT_SALEORG_MAPPING"
		, joinColumns={
			@JoinColumn(name="PLANT_ID", nullable=false)
			}
		, inverseJoinColumns={
			@JoinColumn(name="SALES_ORG_ID", nullable=false)
			}
	)
	private List<SalesOrg> salesOrgs;	
	
	public Plant() {
	}

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<SalesOrg> getSalesOrgs() {
		return salesOrgs;
	}

	public void setSalesOrgs(List<SalesOrg> salesOrgs) {
		this.salesOrgs = salesOrgs;
	}
	


}