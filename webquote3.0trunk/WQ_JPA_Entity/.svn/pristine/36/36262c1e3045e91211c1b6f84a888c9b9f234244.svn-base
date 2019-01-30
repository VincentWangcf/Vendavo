package com.avnet.emasia.webquote.entity;

import java.io.Serializable;
import javax.persistence.*;

/**
 * @author Tonmy,Li(906893)
 * @Created on 2012-12-25
 * The persistent class for the SYSTEM_CODE_MAINTENANCE database table.
 * 
 */
@Entity
@Table(name="SYSTEM_CODE_MAINTENANCE")
@NamedQueries(
{
   @NamedQuery(name= "SYSTEM_CODE_MAINTENANCE.findByCategory", query ="select a from SystemCodeMaintenance a where a.category=:category")
})
public class SystemCodeMaintenance implements Serializable {
	private static final long serialVersionUID = 1L;

	@Column(length=20)
	private String category;

	@Column(length=500)
	private String description;
	
	@Id
	private long id;

	@Column(name="\"VALUE\"", length=100)
	private String value;
	
	@Column(name="REGION")
	private String region;
	

	public SystemCodeMaintenance() {
	}

	public String getCategory() {
		return this.category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public String getDescription() {
		return this.description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public long getId() {
		return this.id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getValue() {
		return this.value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public String getRegion() {
		return region;
	}

	public void setRegion(String region) {
		this.region = region;
	}

}