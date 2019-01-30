package com.avnet.emasia.webquote.entity;

import java.io.Serializable;
import javax.persistence.*;

/**
 * The primary key class for the CUSTOMER_SALE database table.
 * 
 */
@Embeddable
public class CustomerSalePK implements Serializable {
	//default serial version id, required for serializable classes.
	private static final long serialVersionUID = 1L;

	@Column(name="CUSTOMER_NUMBER", unique=true, nullable=false, length=10)
	private String customerNumber;

	@Column(name="SALES_ORG", unique=true, nullable=false, length=4)
	private String salesOrg;

	@Column(name="DISTRIBUTION_CHANNEL", unique=true, nullable=false, length=2)
	private String distributionChannel;

	@Column(name="DIVISION", unique=true, nullable=false, length=2)
	private String division;

	public CustomerSalePK() {
	}
	public String getCustomerNumber() {
		return this.customerNumber;
	}
	public void setCustomerNumber(String customerNumber) {
		this.customerNumber = customerNumber;
	}
	public String getSalesOrg() {
		return this.salesOrg;
	}
	public void setSalesOrg(String salesOrg) {
		this.salesOrg = salesOrg;
	}
	public String getDistributionChannel() {
		return this.distributionChannel;
	}
	public void setDistributionChannel(String distributionChannel) {
		this.distributionChannel = distributionChannel;
	}
	public String getDivision() {
		return this.division;
	}
	public void setDivision(String division) {
		this.division = division;
	}

	public boolean equals(Object other) {
		if (this == other) {
			return true;
		}
		if (!(other instanceof CustomerSalePK)) {
			return false;
		}
		CustomerSalePK castOther = (CustomerSalePK)other;
		return 
			this.customerNumber.equals(castOther.customerNumber)
			&& this.salesOrg.equals(castOther.salesOrg)
			&& this.distributionChannel.equals(castOther.distributionChannel)
			&& this.division.equals(castOther.division);
	}

	public int hashCode() {
		final int prime = 31;
		int hash = 17;
		hash = hash * prime + this.customerNumber.hashCode();
		hash = hash * prime + this.salesOrg.hashCode();
		hash = hash * prime + this.distributionChannel.hashCode();
		hash = hash * prime + this.division.hashCode();
		
		return hash;
	}
}