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
import javax.persistence.OneToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Entity
@Table(name="MFR_REQUEST_INFO")
@NamedQuery(name="MfrRequestInfo.findAll", query="SELECT s FROM MfrRequestInfo s")
public class MfrRequestInfo implements Serializable {
	private static final long serialVersionUID = 1L;
	@Id
	@SequenceGenerator(name="MfrRequestInfo_ID_GENERATOR", sequenceName="WQ_SEQ",allocationSize=1)
	@GeneratedValue(strategy=GenerationType.SEQUENCE, generator="MfrRequestInfo_ID_GENERATOR")
	@Column(unique=true, nullable=false, precision=15)
	private long id;
	
	@Column(name="BPM_CODE")
	private String bpmCode;
	
	@Column(name="BPM_NAME")
	private String bpmName;
	
	@Column(name="BPM_CNT_CODE")
	private String bpmCntCode;
	
	@Column(name="EC_BPM_CODE")
	private String ecBpmCode;
	
	@Column(name="EC_BPM_NAME")
	private String ecBPMName;
	
	@Column(name="ECBPM_CNT_CODE")
	private String ecBPMCntCode;

	
	@Column(name="BQ_NUMBER")
	private String bqNumber;

	@Column(name="PROJECT_REGISTRATION_NUMBER")
	private String projectRegistrationNumber;

	private String status;

	@Column(name="VENDOR_SHIP_TO")
	private String vendorShipTo;
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="SENT_OUT_TIME")
	private Date sentOutTime;
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="CREATE_TIME")
	private Date createTime;
	
	@Column(name="REQ_QTY")
	private Integer reqQty;
	
	@Column(name="CONTACT_NAME")
	private String contactName;
	
	@Column(name="CONTACT_CHANNEL")
	private String contactChannel;
	
	@Column(name="HEAD_REMARK")
	private String headRemark;
	
	@Column(name="tgt_Resale")
	private Double tgtResale;
	
	@Column(name="tgt_Cost")
	private Double tgtCost;
		
	@OneToOne
	@JoinColumn(name="quote_Item_id", nullable=false)
	private QuoteItem quoteItem;
	
	public QuoteItem getQuoteItem() {
		return quoteItem;
	}

	public void setQuoteItem(QuoteItem quoteItem) {
		this.quoteItem = quoteItem;
	}

	public Integer getReqQty() {
		return reqQty;
	}

	public void setReqQty(Integer reqQty) {
		this.reqQty = reqQty;
	}

	public String getContactName() {
		return contactName;
	}

	public void setContactName(String contactName) {
		this.contactName = contactName;
	}

	public String getContactChannel() {
		return contactChannel;
	}

	public void setContactChannel(String contactChannel) {
		this.contactChannel = contactChannel;
	}

	public String getHeadRemark() {
		return headRemark;
	}

	public void setHeadRemark(String headRemark) {
		this.headRemark = headRemark;
	}

	public Double getTgtResale() {
		return tgtResale;
	}

	public void setTgtResale(Double tgtResale) {
		this.tgtResale = tgtResale;
	}

	public Double getTgtCost() {
		return tgtCost;
	}

	public void setTgtCost(Double tgtCost) {
		this.tgtCost = tgtCost;
	}
	
	public Date getSentOutTime() {
		return sentOutTime;
	}

	public void setSentOutTime(Date sentOutTime) {
		this.sentOutTime = sentOutTime;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public String getBpmCode() {
		return this.bpmCode;
	}

	public void setBpmCode(String bpmCode) {
		this.bpmCode = bpmCode;
	}

	public String getBqNumber() {
		return this.bqNumber;
	}

	public void setBqNumber(String bqNumber) {
		this.bqNumber = bqNumber;
	}

	public String getEcBpmCode() {
		return this.ecBpmCode;
	}

	public void setEcBpmCode(String ecBpmCode) {
		this.ecBpmCode = ecBpmCode;
	}

	public long getId() {
		return this.id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getProjectRegistrationNumber() {
		return this.projectRegistrationNumber;
	}

	public void setProjectRegistrationNumber(String projectRegistrationNumber) {
		this.projectRegistrationNumber = projectRegistrationNumber;
	}

	public String getStatus() {
		return this.status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getVendorShipTo() {
		return this.vendorShipTo;
	}

	public void setVendorShipTo(String vendorShipTo) {
		this.vendorShipTo = vendorShipTo;
	}
	public String getBpmName() {
		return bpmName;
	}

	public void setBpmName(String bpmName) {
		this.bpmName = bpmName;
	}

	public String getBpmCntCode() {
		return bpmCntCode;
	}

	public void setBpmCntCode(String bpmCntCode) {
		this.bpmCntCode = bpmCntCode;
	}

	public String getEcBPMName() {
		return ecBPMName;
	}

	public void setEcBPMName(String ecBPMName) {
		this.ecBPMName = ecBPMName;
	}

	public String getEcBPMCntCode() {
		return ecBPMCntCode;
	}

	public void setEcBPMCntCode(String ecBPMCntCode) {
		this.ecBPMCntCode = ecBPMCntCode;
	}


}