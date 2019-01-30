package com.avnet.emasia.webquote.entity;

import java.io.Serializable;
import javax.persistence.*;

import java.sql.Timestamp;
import java.util.Date;


/**
 * The persistent class for the CUSTOMER_ADDRESS database table.
 * 
 */
@Entity
@Table(name="CUSTOMER_ADDRESS")
public class CustomerAddress implements Serializable {
	private static final long serialVersionUID = 1L;

	@EmbeddedId
	private CustomerAddressPK id;

	@Column(name="CUSTOMER_NAME3", length=40)
	private String customerName3;

	@Column(name="CUSTOMER_NAME4", length=40)
	private String customerName4;

	@Column(length=40)
	private String city;

	@Column(length=3)
	private String country;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="CREATED_ON", nullable=false)
	private Date createdOn;

	@Column(name="CUSTOMER_NAME1", length=40)
	private String customerName1;

	@Column(name="CUSTOMER_NAME2", length=40)
	private String customerName2;

	@Column(name="HOUSE_NUMBER", length=10)
	private String houseNumber;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="LAST_UPDATED_ON")
	private Date lastUpdatedOn;

	@Column(name="POSTAL_CODE", length=10)
	private String postalCode;

	@Column(name="SEARCH_TERM1", length=255)
	private String searchTerm1;

	@Column(name="SEARCH_TERM2", length=255)
	private String searchTerm2;

	@Column(length=60)
	private String street;
	
	@Column(length=60)
	private String street2;
	
	@Column(length=60)
	private String street3;
	
	@Transient
	private String customerAddress;

	//uni-directional many-to-one association to Customer
	@ManyToOne
	@JoinColumn(name="CUSTOMER_NUMBER", nullable=false, insertable=false, updatable=false)
	private Customer customer;

	public CustomerAddress() {
	}

	public CustomerAddressPK getId() {
		return this.id;
	}

	public void setId(CustomerAddressPK id) {
		this.id = id;
	}

	public String getCustomerName3() {
		return customerName3;
	}

	public void setCustomerName3(String customerName3) {
		this.customerName3 = customerName3;
	}

	public String getCustomerName4() {
		return customerName4;
	}

	public void setCustomerName4(String customerName4) {
		this.customerName4 = customerName4;
	}

	public String getCity() {
		return this.city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getCountry() {
		return this.country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public Date getCreatedOn() {
		return this.createdOn;
	}

	public void setCreatedOn(Date createdOn) {
		this.createdOn = createdOn;
	}

	public String getCustomerName1() {
		return this.customerName1;
	}

	public void setCustomerName1(String customerName1) {
		this.customerName1 = customerName1;
	}

	public String getCustomerName2() {
		return this.customerName2;
	}

	public void setCustomerName2(String customerName2) {
		this.customerName2 = customerName2;
	}

	public String getHouseNumber() {
		return this.houseNumber;
	}

	public void setHouseNumber(String houseNumber) {
		this.houseNumber = houseNumber;
	}

	public Date getLastUpdatedOn() {
		return this.lastUpdatedOn;
	}

	public void setLastUpdatedOn(Date lastUpdatedOn) {
		this.lastUpdatedOn = lastUpdatedOn;
	}

	public String getPostalCode() {
		return this.postalCode;
	}

	public void setPostalCode(String postalCode) {
		this.postalCode = postalCode;
	}

	public String getSearchTerm1() {
		return this.searchTerm1;
	}

	public void setSearchTerm1(String searchTerm1) {
		this.searchTerm1 = searchTerm1;
	}

	public String getSearchTerm2() {
		return this.searchTerm2;
	}

	public void setSearchTerm2(String searchTerm2) {
		this.searchTerm2 = searchTerm2;
	}

	public String getStreet() {
		return this.street;
	}

	public void setStreet(String street) {
		this.street = street;
	}

	public String getStreet2() {
		return street2;
	}

	public void setStreet2(String street2) {
		this.street2 = street2;
	}

	public String getStreet3() {
		return street3;
	}

	public void setStreet3(String street3) {
		this.street3 = street3;
	}

	public Customer getCustomer() {
		return this.customer;
	}

	public void setCustomer(Customer customer) {
		this.customer = customer;
	}

	public String getCustomerAddress() {
		return customerAddress;
	}
	
	@Transient
	public void setCustomerAddress(String customerAddress) {
		this.customerAddress = customerAddress;
	}


}