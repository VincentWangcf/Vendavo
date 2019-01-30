package com.avnet.emasia.webquote.entity;

import java.io.Serializable;
import javax.persistence.*;

import java.sql.Timestamp;
import java.util.Date;


/**
 * The persistent class for the CUSTOMER_SALE database table.
 * 
 */
@Entity
@Table(name="CUSTOMER_SALE")
public class CustomerSale implements Serializable {
	private static final long serialVersionUID = 1L;

	@EmbeddedId
	private CustomerSalePK id;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="CREATED_ON", nullable=false)
	private Date createdOn;

	@Column(name="CREDIT_CONTROL_AREA", length=4)
	private String creditControlArea;

	@Column(length=5)
	private String currency;

	@Column(name="CUSTOMER_GROUP", length=2)
	private String customerGroup;

	@Column(name="DELIVERY_PLANT", length=4)
	private String deliveryPlant;

	@Column(name="INCO_TERM1", length=3)
	private String incoTerm1;

	@Column(name="INCO_TERM2", length=28)
	private String incoTerm2;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="LAST_UPDATED_ON")
	private Date lastUpdatedOn;

	@Column(name="PAYMENT_TERM", length=4)
	private String paymentTerm;

	@Column(name="SALES_GROUP", length=3)
	private String salesGroup;

	@Column(name="SALES_OFFICE", length=4)
	private String salesOffice;

	//uni-directional many-to-one association to Customer
	@ManyToOne
	@JoinColumn(name="CUSTOMER_NUMBER", nullable=false, insertable=false, updatable=false)
	private Customer customer;

	//uni-directional many-to-one association to SalesOrg
	@ManyToOne
	@JoinColumn(name="SALES_ORG", nullable=false, insertable=false, updatable=false)
	private SalesOrg salesOrgBean;

	public CustomerSale() {
	}

	public CustomerSalePK getId() {
		return this.id;
	}

	public void setId(CustomerSalePK id) {
		this.id = id;
	}

	public Date getCreatedOn() {
		return this.createdOn;
	}

	public void setCreatedOn(Date createdOn) {
		this.createdOn = createdOn;
	}

	public String getCreditControlArea() {
		return this.creditControlArea;
	}

	public void setCreditControlArea(String creditControlArea) {
		this.creditControlArea = creditControlArea;
	}

	public String getCurrency() {
		return this.currency;
	}

	public void setCurrency(String currency) {
		this.currency = currency;
	}

	public String getCustomerGroup() {
		return this.customerGroup;
	}

	public void setCustomerGroup(String customerGroup) {
		this.customerGroup = customerGroup;
	}

	public String getDeliveryPlant() {
		return this.deliveryPlant;
	}

	public void setDeliveryPlant(String deliveryPlant) {
		this.deliveryPlant = deliveryPlant;
	}

	public String getIncoTerm1() {
		return this.incoTerm1;
	}

	public void setIncoTerm1(String incoTerm1) {
		this.incoTerm1 = incoTerm1;
	}

	public String getIncoTerm2() {
		return this.incoTerm2;
	}

	public void setIncoTerm2(String incoTerm2) {
		this.incoTerm2 = incoTerm2;
	}

	public Date getLastUpdatedOn() {
		return this.lastUpdatedOn;
	}

	public void setLastUpdatedOn(Date lastUpdatedOn) {
		this.lastUpdatedOn = lastUpdatedOn;
	}

	public String getPaymentTerm() {
		return this.paymentTerm;
	}

	public void setPaymentTerm(String paymentTerm) {
		this.paymentTerm = paymentTerm;
	}

	public String getSalesGroup() {
		return this.salesGroup;
	}

	public void setSalesGroup(String salesGroup) {
		this.salesGroup = salesGroup;
	}

	public String getSalesOffice() {
		return this.salesOffice;
	}

	public void setSalesOffice(String salesOffice) {
		this.salesOffice = salesOffice;
	}

	public Customer getCustomer() {
		return this.customer;
	}

	public void setCustomer(Customer customer) {
		this.customer = customer;
	}

	public SalesOrg getSalesOrgBean() {
		return this.salesOrgBean;
	}

	public void setSalesOrgBean(SalesOrg salesOrgBean) {
		this.salesOrgBean = salesOrgBean;
	}

}