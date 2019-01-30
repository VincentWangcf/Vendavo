package com.avnet.emasia.webquote.entity;

import java.io.Serializable;
import javax.persistence.*;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;


/**
 * The persistent class for the SALES_ORG database table.
 * 
 */
@Entity
@Table(name="SALES_ORG")
public class SalesOrg implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@SequenceGenerator(name="SALES_ORG_SALESORG_GENERATOR", sequenceName="WQ_SEQ",allocationSize=1)
	@GeneratedValue(strategy=GenerationType.SEQUENCE, generator="SALES_ORG_SALESORG_GENERATOR")
	@Column(name="NAME", unique=true, nullable=false, length=4)
	private String name;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="CREATED_ON", nullable=false)
	private Date createdOn;

	@Column(length=20)
	private String description;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="LAST_UPDATED_ON")
	private Date lastUpdatedOn;

	@ManyToMany
	@JoinTable(
		name="SALEORG_BIZUNIT_MAPPING"
		, joinColumns={
			@JoinColumn(name="SALES_ORG_ID", nullable=false)
			}
		, inverseJoinColumns={
			@JoinColumn(name="BIZ_UNIT_ID", nullable=false)
			}
	)
	private List<BizUnit> bizUnits;

	public SalesOrg() {
	}

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Date getCreatedOn() {
		return this.createdOn;
	}

	public void setCreatedOn(Date createdOn) {
		this.createdOn = createdOn;
	}

	public String getDescription() {
		return this.description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Date getLastUpdatedOn() {
		return this.lastUpdatedOn;
	}

	public void setLastUpdatedOn(Date lastUpdatedOn) {
		this.lastUpdatedOn = lastUpdatedOn;
	}

	public List<BizUnit> getBizUnits() {
		return bizUnits;
	}

	public void setBizUnits(List<BizUnit> bizUnits) {
		this.bizUnits = bizUnits;
	}

	@Override
	public String toString() {
		return "SalesOrg [name=" + name + ", createdOn=" + createdOn
				+ ", description=" + description + ", lastUpdatedOn="
				+ lastUpdatedOn + ", bizUnit=" + bizUnits + "]";
	}

}