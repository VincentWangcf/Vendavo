package com.avnet.emasia.webquote.entity;

import java.io.Serializable;
import javax.persistence.*;

/**
 * The primary key class for the CUSTOMER_ADDRESS database table.
 * 
 */
@Embeddable
public class CustomerAddressPK implements Serializable {
	//default serial version id, required for serializable classes.
	private static final long serialVersionUID = 1L;

	@Column(name="CUSTOMER_NUMBER", unique=true, nullable=false, length=10)
	private String customerNumber;

	@Column(name="LANGUAGE_CODE", unique=true, nullable=false, length=1)
	private String languageCode;

	public CustomerAddressPK() {
	}
	public String getCustomerNumber() {
		return this.customerNumber;
	}
	public void setCustomerNumber(String customerNumber) {
		this.customerNumber = customerNumber;
	}
	public String getLanguageCode() {
		return this.languageCode;
	}
	public void setLanguageCode(String languageCode) {
		this.languageCode = languageCode;
	}

	public boolean equals(Object other) {
		if (this == other) {
			return true;
		}
		if (!(other instanceof CustomerAddressPK)) {
			return false;
		}
		CustomerAddressPK castOther = (CustomerAddressPK)other;
		return 
			this.customerNumber.equals(castOther.customerNumber)
			&& this.languageCode.equals(castOther.languageCode);
	}

	public int hashCode() {
		final int prime = 31;
		int hash = 17;
		hash = hash * prime + this.customerNumber.hashCode();
		hash = hash * prime + this.languageCode.hashCode();
		
		return hash;
	}
}