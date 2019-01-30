package com.avnet.emasia.webquote.entity;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Entity
@Table(name="SAP_DPA_CUST")
public class SapDpaCust implements Serializable{

	private static final long serialVersionUID = 1L;

	@Id
	@SequenceGenerator(name="CUSTOMER_GROUP_ID_GENERATOR", sequenceName="WQ_SEQ",allocationSize=1)
	@GeneratedValue(strategy=GenerationType.SEQUENCE, generator="CUSTOMER_GROUP_ID_GENERATOR")
	@Column(name="ID")
	private long id;
	
	@Column(name="CUST_GROUP_ID")
	private String custGroupId;
	
	@Column(name="NAME")
	private String name;
	
	@Column(name="SALES_ORG")
	private String salesOrg;
	
	@ManyToOne
	@JoinColumn(name = "BIZ_UNIT")
	private BizUnit bizUnit;
	
	@Column(name="SOLD_TO_CUST_NUMBER")
	private String soldToCustNumber;
	
	@Column(name="SHIP_TO_CUST_NUMBER")
	private String shipToCustNumber;
	
	@Column(name="END_CUST_NUMBER")
	private String endCustNumber;
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="CREATED_ON", nullable=false)
	private Date createdOn;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="LAST_UPDATED_ON")
	private Date lastUpdatedOn;


	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}
	
	public String getCustGroupId() {
		return custGroupId;
	}

	public void setCustGroupId(String custGroupId) {
		this.custGroupId = custGroupId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getSalesOrg() {
		return salesOrg;
	}

	public void setSalesOrg(String salesOrg) {
		this.salesOrg = salesOrg;
	}

	public BizUnit getBizUnit() {
		return bizUnit;
	}

	public void setBizUnit(BizUnit bizUnit) {
		this.bizUnit = bizUnit;
	}

	public String getSoldToCustNumber() {
		return soldToCustNumber;
	}

	public void setSoldToCustNumber(String soldToCustNumber) {
		this.soldToCustNumber = soldToCustNumber;
	}

	public String getShipToCustNumber() {
		return shipToCustNumber;
	}

	public void setShipToCustNumber(String shipToCustNumber) {
		this.shipToCustNumber = shipToCustNumber;
	}

	public String getEndCustNumber() {
		return endCustNumber;
	}

	public void setEndCustNumber(String endCustNumber) {
		this.endCustNumber = endCustNumber;
	}

	public Date getLastUpdatedOn() {
		return lastUpdatedOn;
	}

	public void setLastUpdatedOn(Date lastUpdatedOn) {
		this.lastUpdatedOn = lastUpdatedOn;
	}

	public Date getCreatedOn() {
		return createdOn;
	}

	public void setCreatedOn(Date createdOn) {
		this.createdOn = createdOn;
	}

	
	
	

}
