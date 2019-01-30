package com.avnet.emasia.webquote.entity;

import java.io.Serializable;

import javax.persistence.*;

import java.sql.Timestamp;
import java.util.Date;


/**
 * The persistent class for the DR_PROJECT_CUSTOMER database table.
 * 
 */
@Entity
@Table(name="DR_PROJECT_CUSTOMER")
public class DrProjectCustomer implements Serializable {
	private static final long serialVersionUID = 1L;

	@EmbeddedId
	private DrProjectCustomerPK id;
	
	@Column(name="CUSTOMER_TYPE", length=1)
	private String customerType;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="CREATED_ON", nullable=false)
	private Date createdOn;

	@Column(name="CUSTOMER_NAME", length=35)
	private String customerName;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="LAST_UPDATED_ON")
	private Date lastUpdatedOn;

	@Column(name="SALES_ORG_DESCRIPTION", length=20)
	private String salesOrgDescription;

	//bi-directional many-to-one association to DrProjectHeader
	@ManyToOne
	@JoinColumn(name="PROJECT_ID", nullable=false, insertable=false, updatable=false)
	private DrProjectHeader drProjectHeader;

	
	//uni-directional many-to-one association to Customer
	@ManyToOne
	@JoinColumn(name="CUSTOMER_NUMBER", nullable=false, insertable=false, updatable=false)
	private Customer customer;
	
	
	@ManyToOne
	@JoinColumn(name="SALES_ORG", nullable=false, insertable=false, updatable=false)
	private SalesOrg salesOrgBean;
	
	@Transient 
	private String originalSapData;

	
	public DrProjectCustomer() {
	}

	public DrProjectCustomerPK getId() {
		return this.id;
	}

	public void setId(DrProjectCustomerPK id) {
		this.id = id;
	}
	
	public String getCustomerType() {
		return this.customerType;
	}
	public void setCustomerType(String customerType) {
		this.customerType = customerType;
	}

	public Date getCreatedOn() {
		return this.createdOn;
	}

	public void setCreatedOn(Date createdOn) {
		this.createdOn = createdOn;
	}

	public String getCustomerName() {
		return this.customerName;
	}

	public void setCustomerName(String customerName) {
		this.customerName = customerName;
	}

	public Date getLastUpdatedOn() {
		return this.lastUpdatedOn;
	}

	public void setLastUpdatedOn(Date lastUpdatedOn) {
		this.lastUpdatedOn = lastUpdatedOn;
	}

	public String getSalesOrgDescription() {
		return this.salesOrgDescription;
	}

	public void setSalesOrgDescription(String salesOrgDescription) {
		this.salesOrgDescription = salesOrgDescription;
	}

	public DrProjectHeader getDrProjectHeader() {
		return this.drProjectHeader;
	}
	
	

	public SalesOrg getSalesOrgBean() {
		return salesOrgBean;
	}

	public void setSalesOrgBean(SalesOrg salesOrgBean) {
		this.salesOrgBean = salesOrgBean;
	}

	public Customer getCustomer() {
		return customer;
	}

	public void setCustomer(Customer customer) {
		this.customer = customer;
	}

	public void setDrProjectHeader(DrProjectHeader drProjectHeader) {
		this.drProjectHeader = drProjectHeader;
	}

	@Override
	public String toString() {
		return "DrProjectCustomer [id=" + id + ", createdOn=" + createdOn
				+ ", customerName=" + customerName + ", lastUpdatedOn="
				+ lastUpdatedOn + ", salesOrgDescription="
				+ salesOrgDescription + ", drProjectHeader=" + drProjectHeader
				+ "]";
	}


	public String getOriginalSapData() {
		return originalSapData;
	}
	@Transient
	public void setOriginalSapData(String originalSapData) {
		this.originalSapData = originalSapData;
	}

	
}