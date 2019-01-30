package com.avnet.emasia.webquote.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

@Entity
@Table(name="PG_MASTER_MAPPING")
public class PgMasterMapping implements Serializable {

	private static final long serialVersionUID = -3513370471226382957L;

	@Id
	@SequenceGenerator(name="PG_MASTER_MAPPING_ID_GENERATOR", sequenceName="WQ_SEQ",allocationSize=1)
	@GeneratedValue(strategy=GenerationType.SEQUENCE, generator="PG_MASTER_MAPPING_ID_GENERATOR")
	@Column(unique=true, nullable=false, precision=19)
	private long id;
	
	@Column(name="BIZ_UNIT", length=10)
	private String bizUnit;
	
	@ManyToOne
	@JoinColumn(name="MANUFACTURER_ID")	
	private Manufacturer manufacturer;
	

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getBizUnit() {
		return bizUnit;
	}

	public void setBizUnit(String bizUnit) {
		this.bizUnit = bizUnit;
	}

	public Manufacturer getManufacturer() {
		return manufacturer;
	}

	public void setManufacturer(Manufacturer manufacturer) {
		this.manufacturer = manufacturer;
	}

}
