package com.avnet.emasia.webquote.entity;

import java.io.Serializable;
import javax.persistence.*;

import java.util.Date;


/**
 * The persistent class for the POS database table.
 * 
 */
@Entity
@Table(name="POS")
public class Pos implements Serializable {
	private static final long serialVersionUID = 1L;
	private static final String SEPARATOR ="|";
	@EmbeddedId
	private PosPK id;
	
	@Temporal(TemporalType.DATE)
	@Column(name="BILLING_DATE", nullable=false)
	private Date billingDate;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="CREATED_ON", nullable=false)
	private Date createdOn;

	@Column(name="CUSTOMER_NAME", nullable=false, length=35)
	private String customerName;

	@Column(name="FULL_MFR_PART_NUMBER", nullable=false, length=40)
	private String fullMfrPartNumber;

	@Column(name="INVOICE_AMOUNT", nullable=false, length=20)
	private String invoiceAmount;

	@Column(name="INVOICE_QTY", nullable=false, precision=15)
	private Integer invoiceQty;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="LAST_UPDATED_ON")
	private Date lastUpdatedOn;

	@Column(nullable=false, length=10)
	private String mfr;

	@Column(name="SAP_PART_NUMBER", nullable=false, length=18)
	private String sapPartNumber;

	@Column(name="SIGN", nullable=false, length=1)
	private String sign;

	@Column(name="CUSTOMER_NUMBER", nullable=false)
	private String customerNumber;

	@Column(name="SALES_ORG", nullable=false)
	private String salesOrg;

	@Transient 
	private String originalSapData;
	
	public Pos() {
	}

	public PosPK getId() {
		return id;
	}

	public void setId(PosPK id) {
		this.id = id;
	}

	public Date getBillingDate() {
		return billingDate;
	}

	public void setBillingDate(Date billingDate) {
		this.billingDate = billingDate;
	}

	public Date getCreatedOn() {
		return createdOn;
	}

	public void setCreatedOn(Date createdOn) {
		this.createdOn = createdOn;
	}

	public String getCustomerName() {
		return customerName;
	}

	public void setCustomerName(String customerName) {
		this.customerName = customerName;
	}

	public String getFullMfrPartNumber() {
		return fullMfrPartNumber;
	}

	public void setFullMfrPartNumber(String fullMfrPartNumber) {
		this.fullMfrPartNumber = fullMfrPartNumber;
	}

	public String getInvoiceAmount() {
		return invoiceAmount;
	}

	public void setInvoiceAmount(String invoiceAmount) {
		this.invoiceAmount = invoiceAmount;
	}

	public Integer getInvoiceQty() {
		return invoiceQty;
	}

	public void setInvoiceQty(Integer invoiceQty) {
		this.invoiceQty = invoiceQty;
	}

	public Date getLastUpdatedOn() {
		return lastUpdatedOn;
	}

	public void setLastUpdatedOn(Date lastUpdatedOn) {
		this.lastUpdatedOn = lastUpdatedOn;
	}

	public String getMfr() {
		return mfr;
	}

	public void setMfr(String mfr) {
		this.mfr = mfr;
	}

	public String getSapPartNumber() {
		return sapPartNumber;
	}

	public void setSapPartNumber(String sapPartNumber) {
		this.sapPartNumber = sapPartNumber;
	}

	public String getSign() {
		return sign;
	}

	public void setSign(String sign) {
		this.sign = sign;
	}

	public String getCustomerNumber() {
		return customerNumber;
	}

	public void setCustomerNumber(String customerNumber) {
		this.customerNumber = customerNumber;
	}

	public String getSalesOrg() {
		return salesOrg;
	}

	public void setSalesOrg(String salesOrg) {
		this.salesOrg = salesOrg;
	}

	public String getOriginalSapData() {
		return originalSapData;
	}

	public void setOriginalSapData(String originalSapData) {
		this.originalSapData = originalSapData;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((billingDate == null) ? 0 : billingDate.hashCode());
		result = prime * result
				+ ((createdOn == null) ? 0 : createdOn.hashCode());
		result = prime * result
				+ ((customerName == null) ? 0 : customerName.hashCode());
		result = prime * result
				+ ((customerNumber == null) ? 0 : customerNumber.hashCode());
		result = prime
				* result
				+ ((fullMfrPartNumber == null) ? 0 : fullMfrPartNumber
						.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result
				+ ((invoiceAmount == null) ? 0 : invoiceAmount.hashCode());
		result = prime * result
				+ ((invoiceQty == null) ? 0 : invoiceQty.hashCode());
		result = prime * result
				+ ((lastUpdatedOn == null) ? 0 : lastUpdatedOn.hashCode());
		result = prime * result + ((mfr == null) ? 0 : mfr.hashCode());
		result = prime * result
				+ ((originalSapData == null) ? 0 : originalSapData.hashCode());
		result = prime * result
				+ ((salesOrg == null) ? 0 : salesOrg.hashCode());
		result = prime * result
				+ ((sapPartNumber == null) ? 0 : sapPartNumber.hashCode());
		result = prime * result + ((sign == null) ? 0 : sign.hashCode());
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
		Pos other = (Pos) obj;
		if (billingDate == null) {
			if (other.billingDate != null)
				return false;
		} else if (!billingDate.equals(other.billingDate))
			return false;
		if (createdOn == null) {
			if (other.createdOn != null)
				return false;
		} else if (!createdOn.equals(other.createdOn))
			return false;
		if (customerName == null) {
			if (other.customerName != null)
				return false;
		} else if (!customerName.equals(other.customerName))
			return false;
		if (customerNumber == null) {
			if (other.customerNumber != null)
				return false;
		} else if (!customerNumber.equals(other.customerNumber))
			return false;
		if (fullMfrPartNumber == null) {
			if (other.fullMfrPartNumber != null)
				return false;
		} else if (!fullMfrPartNumber.equals(other.fullMfrPartNumber))
			return false;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (invoiceAmount == null) {
			if (other.invoiceAmount != null)
				return false;
		} else if (!invoiceAmount.equals(other.invoiceAmount))
			return false;
		if (invoiceQty == null) {
			if (other.invoiceQty != null)
				return false;
		} else if (!invoiceQty.equals(other.invoiceQty))
			return false;
		if (lastUpdatedOn == null) {
			if (other.lastUpdatedOn != null)
				return false;
		} else if (!lastUpdatedOn.equals(other.lastUpdatedOn))
			return false;
		if (mfr == null) {
			if (other.mfr != null)
				return false;
		} else if (!mfr.equals(other.mfr))
			return false;
		if (originalSapData == null) {
			if (other.originalSapData != null)
				return false;
		} else if (!originalSapData.equals(other.originalSapData))
			return false;
		if (salesOrg == null) {
			if (other.salesOrg != null)
				return false;
		} else if (!salesOrg.equals(other.salesOrg))
			return false;
		if (sapPartNumber == null) {
			if (other.sapPartNumber != null)
				return false;
		} else if (!sapPartNumber.equals(other.sapPartNumber))
			return false;
		if (sign == null) {
			if (other.sign != null)
				return false;
		} else if (!sign.equals(other.sign))
			return false;
		return true;
	}


	
}