package com.avnet.emasia.webquote.entity;

import java.io.Serializable;
import javax.persistence.*;
import java.math.BigDecimal;


/**
 * The persistent class for the DESIGN_LOCATION database table.
 * 
 */
@Entity
@Table(name="DESIGN_LOCATION")
public class DesignLocation implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	private String code;

	@Column(name="DESIGN_REGION")
	private String designRegion;

	private String name;

	@Version
	@Column(name="\"VERSION\"")
	private Integer version;

	public DesignLocation() {
	}

	public String getCode() {
		return this.code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getDesignRegion() {
		return this.designRegion;
	}

	public void setDesignRegion(String designRegion) {
		this.designRegion = designRegion;
	}

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Integer getVersion() {
		return this.version;
	}

	public void setVersion(Integer version) {
		this.version = version;
	}

}