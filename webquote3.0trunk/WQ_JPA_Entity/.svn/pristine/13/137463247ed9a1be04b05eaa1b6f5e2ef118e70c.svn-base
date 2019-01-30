package com.avnet.emasia.webquote.entity;

import java.io.Serializable;
import javax.persistence.*;

/**
 * The primary key class for the POS database table.
 * 
 */
@Embeddable
public class PosSummaryPK implements Serializable {
	//default serial version id, required for serializable classes.
	private static final long serialVersionUID = 1L;

	@Column(name="MFR")
	private String mfr;

	@Column(name="PART_NUMBER")
	private String partNumber;

	@Column(name="CUSTOMER_NAME")
	private String customerName;

	@Column(name="SOLD_TO_CUSTOMER_NUMBER", nullable=false)
	private String soldToCustomerNumber;
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((customerName == null) ? 0 : customerName.hashCode());
		result = prime * result + ((mfr == null) ? 0 : mfr.hashCode());
		result = prime * result
				+ ((partNumber == null) ? 0 : partNumber.hashCode());
		result = prime
				* result
				+ ((soldToCustomerNumber == null) ? 0 : soldToCustomerNumber
						.hashCode());
		return result;
	}


	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		PosSummaryPK other = (PosSummaryPK) obj;
		if (customerName == null) {
			if (other.customerName != null)
				return false;
		} else if (!customerName.equals(other.customerName))
			return false;
		if (mfr == null) {
			if (other.mfr != null)
				return false;
		} else if (!mfr.equals(other.mfr))
			return false;
		if (partNumber == null) {
			if (other.partNumber != null)
				return false;
		} else if (!partNumber.equals(other.partNumber))
			return false;
		if (soldToCustomerNumber == null) {
			if (other.soldToCustomerNumber != null)
				return false;
		} else if (!soldToCustomerNumber.equals(other.soldToCustomerNumber))
			return false;
		return true;
	}


	public PosSummaryPK() {
	}


	public String getSoldToCustomerNumber() {
		return soldToCustomerNumber;
	}


	public void setSoldToCustomerNumber(String soldToCustomerNumber) {
		this.soldToCustomerNumber = soldToCustomerNumber;
	}


	public String getMfr() {
		return mfr;
	}


	public void setMfr(String mfr) {
		this.mfr = mfr;
	}


	public String getPartNumber() {
		return partNumber;
	}


	public void setPartNumber(String partNumber) {
		this.partNumber = partNumber;
	}


	public String getCustomerName() {
		return customerName;
	}


	public void setCustomerName(String customerName) {
		this.customerName = customerName;
	}

	
}