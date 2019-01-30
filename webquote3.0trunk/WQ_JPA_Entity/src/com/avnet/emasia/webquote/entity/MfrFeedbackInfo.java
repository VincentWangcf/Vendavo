package com.avnet.emasia.webquote.entity;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQuery;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Entity
@Table(name="Mfr_Feedback_Info")
@NamedQuery(name="MfrFeedbackInfo.findAll", query="SELECT r FROM MfrFeedbackInfo r")
public class MfrFeedbackInfo implements Serializable {
	private static final long serialVersionUID = 1L;
	@Id
	@SequenceGenerator(name="MfrFeedbackInfo_ID_GENERATOR", sequenceName="WQ_SEQ",allocationSize=1)
	@GeneratedValue(strategy=GenerationType.SEQUENCE, generator="MfrFeedbackInfo_ID_GENERATOR")
	@Column(unique=true, nullable=false, precision=15)
	private long id;
	
	private String mfr;

	@Column(name="SQ_NUMBER")
	private String sqNumber;

	@Column(name="DEBIT_NUMBER")
	private String debitNumber;

	@Column(name="QUOTE_TYPE")
	private String type;
	
	@Column(name="AUTH_QTY")
	private Integer authQty;

	@Column(name="AVNET_QUOTE_NUMBER")
	private String avnetQuoteNumber;

	private String buyer;

	@Column(name="CONTACT_EMAIL")
	private String contactEmail;

	@Column(name="CONTACT_NAME")
	private String contactName;

	private Double cost;

	private String currency;

	private String customer;

	@Temporal(TemporalType.DATE)
	@Column(name="DEBIT_CREATE_DATE")
	private Date debitCreateDate;

	@Temporal(TemporalType.DATE)
	@Column(name="DEBIT_EXPIRY_DATE")
	private Date debitExpiryDate;

	@Column(name="END_CUSTOMER")
	private String endCustomer;

	@Column(name="PART_NUMBER")
	private String partNumber;

	@Column(name="SHIP_TO")
	private String shipTo;

	private String status;

	@Column(name="SUGGESTED_RESALE")
	private Double suggestedResale;


	@Column(name="VENDOR_REMARKS")
	private String vendorRemarks;
	
	@ManyToOne
	@JoinColumn(name="BIZ_UNIT", nullable=false)
	private BizUnit bizUnit;

	public BizUnit getBizUnit() {
		return bizUnit;
	}

	public void setBizUnit(BizUnit bizUnit) {
		this.bizUnit = bizUnit;
	}

	public Integer getAuthQty() {
		return this.authQty;
	}

	public void setAuthQty(Integer authQty) {
		this.authQty = authQty;
	}

	public String getAvnetQuoteNumber() {
		return this.avnetQuoteNumber;
	}

	public void setAvnetQuoteNumber(String avnetQuoteNumber) {
		this.avnetQuoteNumber = avnetQuoteNumber;
	}

	public String getBuyer() {
		return this.buyer;
	}

	public void setBuyer(String buyer) {
		this.buyer = buyer;
	}

	public String getContactEmail() {
		return this.contactEmail;
	}

	public void setContactEmail(String contactEmail) {
		this.contactEmail = contactEmail;
	}

	public String getContactName() {
		return this.contactName;
	}

	public void setContactName(String contactName) {
		this.contactName = contactName;
	}

	public Double getCost() {
		return this.cost;
	}

	public void setCost(Double cost) {
		this.cost = cost;
	}

	public String getCurrency() {
		return this.currency;
	}

	public void setCurrency(String currency) {
		this.currency = currency;
	}

	public String getCustomer() {
		return this.customer;
	}

	public void setCustomer(String customer) {
		this.customer = customer;
	}

	public Date getDebitCreateDate() {
		return this.debitCreateDate;
	}

	public void setDebitCreateDate(Date debitCreateDate) {
		this.debitCreateDate = debitCreateDate;
	}

	public Date getDebitExpiryDate() {
		return this.debitExpiryDate;
	}

	public void setDebitExpiryDate(Date debitExpiryDate) {
		this.debitExpiryDate = debitExpiryDate;
	}

	public String getDebitNumber() {
		return this.debitNumber;
	}

	public void setDebitNumber(String debitNumber) {
		this.debitNumber = debitNumber;
	}

	public String getEndCustomer() {
		return this.endCustomer;
	}

	public void setEndCustomer(String endCustomer) {
		this.endCustomer = endCustomer;
	}

	public long getId() {
		return this.id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getMfr() {
		return this.mfr;
	}

	public void setMfr(String mfr) {
		this.mfr = mfr;
	}

	public String getPartNumber() {
		return this.partNumber;
	}

	public void setPartNumber(String partNumber) {
		this.partNumber = partNumber;
	}

	public String getShipTo() {
		return this.shipTo;
	}

	public void setShipTo(String shipTo) {
		this.shipTo = shipTo;
	}

	public String getSqNumber() {
		return this.sqNumber;
	}

	public void setSqNumber(String sqNumber) {
		this.sqNumber = sqNumber;
	}

	public String getStatus() {
		return this.status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public Double getSuggestedResale() {
		return this.suggestedResale;
	}

	public void setSuggestedResale(Double suggestedResale) {
		this.suggestedResale = suggestedResale;
	}

	public String getType() {
		return this.type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getVendorRemarks() {
		return this.vendorRemarks;
	}

	public void setVendorRemarks(String vendorRemarks) {
		this.vendorRemarks = vendorRemarks;
	}

}