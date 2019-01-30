package com.avnet.emasia.webquote.entity;

import java.io.Serializable;
import javax.persistence.*;

import java.util.Date;


/**
 * The persistent class for the CUSTOMER_PARTNER database table.
 * 
 */
@Entity
@Table(name="CUSTOMER_PARTNER")
public class CustomerPartner implements Serializable {
	

	private static final long serialVersionUID = 1L;

	@EmbeddedId
	private CustomerPartnerPK id;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="CREATED_ON", nullable=false)
	private Date createdOn;

	@Column(name="EMPLOYEE_NUMBER", precision=10)
	private Integer employeeNumber;


	@Column(name="PARTNER_NAME")
	private String partnerName;
	
	@Column(name="PARTNER_CUSTOMER_CODE")
	private String partnerCustomerCode;

	@Column(name="CUSTOMER_NUMBER", nullable=false, insertable=false, updatable=false)
	private String customerNumber;
	
	@Column(name="PARTNER_FUNCTION", insertable=false, updatable=false)
	private String partnerFunction;
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="LAST_UPDATED_ON")
	private Date lastUpdatedOn;

	//uni-directional many-to-one association to CustomerSale
	@ManyToOne
	@JoinColumns({
		@JoinColumn(name="CUSTOMER_NUMBER", referencedColumnName="CUSTOMER_NUMBER", nullable=false, insertable=false, updatable=false),
		@JoinColumn(name="DISTRIBUTION_CHANNEL", referencedColumnName="DISTRIBUTION_CHANNEL", nullable=false, insertable=false, updatable=false),
		@JoinColumn(name="DIVISION", referencedColumnName="DIVISION", nullable=false, insertable=false, updatable=false),
		@JoinColumn(name="SALES_ORG", referencedColumnName="SALES_ORG", nullable=false, insertable=false, updatable=false)
		})
	private CustomerSale customerSale;

	//uni-directional many-to-one association to SalesOrg
	@ManyToOne
	@JoinColumn(name="SALES_ORG", nullable=false, insertable=false, updatable=false)
	private SalesOrg salesOrgBean;

	public CustomerPartner() {
	}

	public CustomerPartnerPK getId() {
		return this.id;
	}

	public void setId(CustomerPartnerPK id) {
		this.id = id;
	}

	public Date getCreatedOn() {
		return this.createdOn;
	}

	public void setCreatedOn(Date createdOn) {
		this.createdOn = createdOn;
	}

	public Integer getEmployeeNumber() {
		return this.employeeNumber;
	}

	public void setEmployeeNumber(Integer employeeNumber) {
		this.employeeNumber = employeeNumber;
	}

	public Date getLastUpdatedOn() {
		return this.lastUpdatedOn;
	}

	public void setLastUpdatedOn(Date lastUpdatedOn) {
		this.lastUpdatedOn = lastUpdatedOn;
	}

	public CustomerSale getCustomerSale() {
		return this.customerSale;
	}

	public void setCustomerSale(CustomerSale customerSale) {
		this.customerSale = customerSale;
	}

	public SalesOrg getSalesOrgBean() {
		return this.salesOrgBean;
	}

	public void setSalesOrgBean(SalesOrg salesOrgBean) {
		this.salesOrgBean = salesOrgBean;
	}

	public String getPartnerName() {
		return partnerName;
	}

	public void setPartnerName(String partnerName) {
		this.partnerName = partnerName;
	}

	public String getPartnerCustomerCode() {
		return partnerCustomerCode;
	}

	public String getCustomerNumber() {
		return customerNumber;
	}

	public void setCustomerNumber(String customerNumber) {
		this.customerNumber = customerNumber;
	}

	public String getPartnerFunction() {
		return partnerFunction;
	}

	public void setPartnerFunction(String partnerFunction) {
		this.partnerFunction = partnerFunction;
	}

	public void setPartnerCustomerCode(String partnerCustomerCode) {
		this.partnerCustomerCode = partnerCustomerCode;
	}


}