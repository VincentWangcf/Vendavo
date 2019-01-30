package com.avnet.emasia.webquote.entity;

import java.io.Serializable;
import javax.persistence.*;

/**
 * The primary key class for the DR_PROJECT_CUSTOMER database table.
 * 
 */
@Embeddable
public class DrProjectCustomerPK implements Serializable {
	//default serial version id, required for serializable classes.
	private static final long serialVersionUID = 1L;

	@Column(name="PROJECT_ID", unique=true, nullable=false, precision=7)
	private long projectId;

	@Column(name="CUSTOMER_NUMBER", unique=true, nullable=false, length=10)
	private String customerNumber;

	@Column(name="SALES_ORG", unique=true, length=4)
	private String salesOrg;

	
	public DrProjectCustomerPK() {
	}
	public long getProjectId() {
		return this.projectId;
	}
	public void setProjectId(long projectId) {
		this.projectId = projectId;
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
	
	public boolean equals(Object other) {
		if (this == other) {
			return true;
		}
		if (!(other instanceof DrProjectCustomerPK)) {
			return false;
		}
		DrProjectCustomerPK castOther = (DrProjectCustomerPK)other;
		return 
			(this.projectId == castOther.projectId)
			&& this.customerNumber.equals(castOther.customerNumber)
			&& this.salesOrg.equals(castOther.salesOrg);
	}

	public int hashCode() {
		final int prime = 31;
		int hash = 17;
		hash = hash * prime + ((int) (this.projectId ^ (this.projectId >>> 32)));
		hash = hash * prime + this.customerNumber.hashCode();
		hash = hash * prime + this.salesOrg.hashCode();

		
		return hash;
	}
	@Override
	public String toString() {
		return "DrProjectCustomerPK [projectId=" + projectId
				+ ", customerNumber=" + customerNumber + ", salesOrg="
				+ salesOrg + "]";
	}
	
	
}