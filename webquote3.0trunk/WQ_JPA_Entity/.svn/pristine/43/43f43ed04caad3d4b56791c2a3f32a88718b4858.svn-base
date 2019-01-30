package com.avnet.emasia.webquote.entity;

import java.io.Serializable;
import javax.persistence.*;

/**
 * The primary key class for the CUSTOMER_PARTNER database table.
 * 
 */
@Embeddable
public class CustomerPartnerPK implements Serializable {
	//default serial version id, required for serializable classes.
	private static final long serialVersionUID = 1L;

	@Column(name="CUSTOMER_NUMBER", unique=true, nullable=false, length=10)
	private String customerNumber;

	@Column(name="PARTNER_FUNCTION", unique=true, nullable=false, length=2)
	private String partnerFunction;

	@Column(name="SALES_ORG", unique=true, nullable=false, length=4)
	private String salesOrg;

	@Column(name="DISTRIBUTION_CHANNEL", unique=true, nullable=false, length=2)
	private String distributionChannel;

	@Column(name="DIVISION",unique=true, nullable=false, length=2)
	private String division;

	@Column(name="PARTNER_COUNTER", unique=true, nullable=false, precision=3)
	private long partnerCounter;

	public CustomerPartnerPK() {
	}
	public String getCustomerNumber() {
		return this.customerNumber;
	}
	public void setCustomerNumber(String customerNumber) {
		this.customerNumber = customerNumber;
	}
	public String getPartnerFunction() {
		return this.partnerFunction;
	}
	public void setPartnerFunction(String partnerFunction) {
		this.partnerFunction = partnerFunction;
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
	public long getPartnerCounter() {
		return this.partnerCounter;
	}
	public void setPartnerCounter(long partnerCounter) {
		this.partnerCounter = partnerCounter;
	}

	public boolean equals(Object other) {
		if (this == other) {
			return true;
		}
		if (!(other instanceof CustomerPartnerPK)) {
			return false;
		}
		CustomerPartnerPK castOther = (CustomerPartnerPK)other;
		return 
			this.customerNumber.equals(castOther.customerNumber)
			&& (this.partnerFunction.equals(castOther.partnerFunction)) //Using == to compare 2 Strings, is this the intent? (Chris Lam, 2013/08/01)
			&& this.salesOrg.equals(castOther.salesOrg)
			&& this.distributionChannel.equals(castOther.distributionChannel)
			&& this.division.equals(castOther.division)
			&& (this.partnerCounter == castOther.partnerCounter);
	}

	public int hashCode() {
		final int prime = 31;
		int hash = 17;
		hash = hash * prime + this.customerNumber.hashCode();
		hash = hash * prime + this.partnerFunction.hashCode();
		hash = hash * prime + this.salesOrg.hashCode();
		hash = hash * prime + this.distributionChannel.hashCode();
		hash = hash * prime + this.division.hashCode();
		hash = hash * prime + ((int) (this.partnerCounter ^ (this.partnerCounter >>> 32)));
		
		return hash;
	}
}