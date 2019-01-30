package com.avnet.emasia.webquote.entity;

import java.io.Serializable;
import javax.persistence.*;

import java.util.Date;
import java.util.logging.Logger;


/**
 * The persistent class for the QUOTE database table.
 * 
 */
@Entity
@Table(name="QUOTE_TO_SO_PENDING")
public class QuoteToSoPending implements Serializable {
	private static final long serialVersionUID = 1L;
	static Logger logger = Logger.getLogger("QuoteToSoPending");
	
	@Column(name="CUSTOMER_NUMBER")
	private String customerNumber;
	
	@Column(name="MFR")
	private String mfr;

	@Column(name="FULL_MFR_PART_NUMBER")
	private String fullMfrPartNumber;

	@Id
	@Column(name="QUOTE_NUMBER", unique=true, nullable=false)	
	private String quoteNumber;

	@ManyToOne
	@JoinColumn(name="BIZ_UNIT_ID", nullable=false)
	private BizUnit bizUnit;

	@Column(name="STATUS", length=1)
	private Boolean status;

	@Column(name="MESSAGE")
	private String message;
		
	@Column(name="TYPE")
	private String type;
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="CREATE_DATE", nullable=false)
	private Date createDate;
	
	public QuoteToSoPending() {
	}

	public String getCustomerNumber() {
		return customerNumber;
	}

	public void setCustomerNumber(String customerNumber) {
		this.customerNumber = customerNumber;
	}

	public String getMfr() {
		return mfr;
	}

	public void setMfr(String mfr) {
		this.mfr = mfr;
	}

	public String getFullMfrPartNumber() {
		return fullMfrPartNumber;
	}

	public void setFullMfrPartNumber(String fullMfrPartNumber) {
		this.fullMfrPartNumber = fullMfrPartNumber;
	}

	public String getQuoteNumber() {
		return quoteNumber;
	}

	public void setQuoteNumber(String quoteNumber) {
		this.quoteNumber = quoteNumber;
	}

	public BizUnit getBizUnit() {
		return bizUnit;
	}

	public void setBizUnit(BizUnit bizUnit) {
		this.bizUnit = bizUnit;
	}

	public Boolean getStatus() {
		return status;
	}

	public void setStatus(Boolean status) {
		this.status = status;
	}

	public Date getCreateDate() {
		return createDate;
	}

	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}
	
}