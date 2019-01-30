package com.avnet.emasia.webquote.stm.dto;

import java.io.Serializable;
import java.util.Date;

import com.avnet.emasia.webquote.entity.MfrRequestInfo;
import com.avnet.emasia.webquote.entity.QuoteItem;
import com.avnet.emasia.webquote.entity.User;
import com.avnet.emasia.webquote.stm.annotation.TransAlias;
import com.avnet.emasia.webquote.stm.constant.FieldTypeEnum;

public class OutBoundVo implements Serializable{

	public String getUnitOfQty() {
		return unitOfQty;
	}
	public void setUnitOfQty(String unitOfQty) {
		this.unitOfQty = unitOfQty;
	}
	public String getUnitOfResale() {
		return unitOfResale;
	}
	public void setUnitOfResale(String unitOfResale) {
		this.unitOfResale = unitOfResale;
	}
	public String getUnitOfCost() {
		return unitOfCost;
	}
	public void setUnitOfCost(String unitOfCost) {
		this.unitOfCost = unitOfCost;
	}
	@Override
	public String toString() {
		return "OutBoundVo [seq=" + seq + ", rfqCode=" + rfqCode + ", action=" + action + ", mfr=" + mfr + ", fullMPN=" + fullMPN + ", quoteType=" + quoteType + ", reqQty=" + reqQty + ", custCode=" + custCode + ", shipToCode=" + shipToCode + ", contactName=" + contactName + ", contactChannel=" + contactChannel + ", headRemark=" + headRemark + ", bpmCode=" + bpmCode + ", bpmName=" + bpmName + ", bpmCntCode=" + bpmCntCode + ", ecBpmCode=" + ecBpmCode + ", ecBPMName=" + ecBPMName + ", ecBPMCntCode=" + ecBPMCntCode + ", tgtResale=" + tgtResale + ", tgtCost=" + tgtCost + ", itemRemark=" + itemRemark + ", deliveryStart=" + deliveryStart + ", deliveryStop=" + deliveryStop + ", sentAt=" + sentAt + ", currency=" + currency + ", projectRegistrationNumber=" + projectRegistrationNumber + ", bqNumber=" + bqNumber + ", mpSchedule=" + mpSchedule + ", ppSchedule=" + ppSchedule + ", eau=" + eau + ", application=" + application + ", project=" + project + ", b2bStatus=" + b2bStatus + "]";
	}
	private static final long serialVersionUID = -7345570227057380237L;

	private String formNumber;
	
	//Used Vendor Quote Maintenance page
	private int seq;
	
	//Use in other page related to Vendor Quote Maintenance, like send sga quote,send sada quote etc
	private int seq2;

	@TransAlias(sgaMandatory=true,sadaMandatory=true,fieldType =FieldTypeEnum.TEXT,fieldLength="9" ,xmlFieldName="RFQCode",uiFieldName="wq.label.rfqCode")
	private String rfqCode;

	@TransAlias(sgaMandatory=true,sadaMandatory=true,fieldType=FieldTypeEnum.TEXT,fieldLength="1" ,xmlFieldName="Action",uiFieldName="wq.label.actn")
	private String action;

	@TransAlias(sgaMandatory=true,sadaMandatory=true,fieldType=FieldTypeEnum.TEXT,fieldLength="3" ,xmlFieldName="MFR",uiFieldName="wq.label.mfr")
	private String mfr;
	@TransAlias(sgaMandatory=true,sadaMandatory=true,fieldType=FieldTypeEnum.TEXT,fieldLength="35" ,xmlFieldName="FullMPN",uiFieldName="wq.label.mfrPN")
	private String fullMPN;
	@TransAlias(sgaMandatory=true,sadaMandatory=true,fieldType=FieldTypeEnum.TEXT,fieldLength="5" ,xmlFieldName="QuoteType",uiFieldName="wq.label.QuoteType")
	private String quoteType;
	@TransAlias(sgaMandatory=true,sadaMandatory=true,fieldType=FieldTypeEnum.INTEGER,fieldLength="15" ,xmlFieldName="ReqQty",uiFieldName="wq.label.reqQty")
	private String reqQty;
	@TransAlias(sgaMandatory=true,sadaMandatory=true,fieldType=FieldTypeEnum.TEXT,fieldLength="3" ,xmlFieldName="UnitOfQty",uiFieldName="wq.label.UnitOfQty")
	private String unitOfQty;
	@TransAlias(sgaMandatory=true,sadaMandatory=true,fieldType=FieldTypeEnum.TEXT,fieldLength="35" ,xmlFieldName="CustCode",uiFieldName="wq.label.custmrCode")
	private String custCode;
	@TransAlias(sgaMandatory=true,sadaMandatory=true,fieldType=FieldTypeEnum.TEXT,fieldLength="35" ,xmlFieldName="ShipToCode",uiFieldName="wq.label.ShipToCode")
	private String shipToCode;
	@TransAlias(sgaMandatory=true,sadaMandatory=true,fieldType=FieldTypeEnum.TEXT,fieldLength="17" ,xmlFieldName="ContactName",uiFieldName="wq.label.contactName")
	private String contactName;
	@TransAlias(sgaMandatory=false,sadaMandatory=false,fieldType=FieldTypeEnum.TEXT,fieldLength="512" ,xmlFieldName="ContactChannel",uiFieldName="wq.label.ContactChannel")
	private String contactChannel;
	@TransAlias(sgaMandatory=false,sadaMandatory=true,fieldType=FieldTypeEnum.TEXT,fieldLength="140" ,xmlFieldName="HeadRemark",uiFieldName="wq.label.headerRemarks")
	private String headRemark;
	@TransAlias(sgaMandatory=true,sadaMandatory=true,fieldType=FieldTypeEnum.TEXT,fieldLength="35" ,xmlFieldName="BPMCode",uiFieldName="wq.label.bpmCode")
	private String bpmCode;
	@TransAlias(sgaMandatory=true,sadaMandatory=true,fieldType=FieldTypeEnum.TEXT,fieldLength="35" ,xmlFieldName="BPMName",uiFieldName="wq.label.bpmNam")
	private String bpmName;
	@TransAlias(sgaMandatory=true,sadaMandatory=true,fieldType=FieldTypeEnum.TEXT,fieldLength="3" ,xmlFieldName="BPMCntCode",uiFieldName="wq.label.bpmContCode")
	private String bpmCntCode;
	@TransAlias(sgaMandatory=false,sadaMandatory=true,fieldType=FieldTypeEnum.TEXT,fieldLength="35" ,xmlFieldName="ECBPM",uiFieldName="wq.label.ec")
	private String ecBpmCode;
	@TransAlias(sgaMandatory=false,sadaMandatory=true,fieldType=FieldTypeEnum.TEXT,fieldLength="35" ,xmlFieldName="ECBPMName",uiFieldName="wq.label.ecBPMName")
	private String ecBPMName;
	@TransAlias(sgaMandatory=false,sadaMandatory=true,fieldType=FieldTypeEnum.TEXT,fieldLength="3" ,xmlFieldName="ECBPMCntCode",uiFieldName="wq.label.ecCountry")
	private String ecBPMCntCode;
	@TransAlias(sgaMandatory=true,sadaMandatory=true,fieldType=FieldTypeEnum.NUMBER,fieldLength="15" ,xmlFieldName="TgtResale",uiFieldName="wq.label.trgResale")
	private String tgtResale;
	@TransAlias(sgaMandatory=true,sadaMandatory=true,fieldType=FieldTypeEnum.INTEGER,fieldLength="9" ,xmlFieldName="UnitOfResale",uiFieldName="wq.label.UnitOfResale")
	private String unitOfResale;
	@TransAlias(sgaMandatory=true,sadaMandatory=true,fieldType=FieldTypeEnum.NUMBER,fieldLength="15" ,xmlFieldName="TgtCost",uiFieldName="wq.label.TargetCost")
	private String tgtCost;
	@TransAlias(sgaMandatory=true,sadaMandatory=true,fieldType=FieldTypeEnum.INTEGER,fieldLength="9" ,xmlFieldName="UnitOfCost",uiFieldName="wq.label.UnitOfCost")
	private String unitOfCost;
	@TransAlias(sgaMandatory=true,sadaMandatory=true,fieldType=FieldTypeEnum.TEXT,fieldLength="350" ,xmlFieldName="ItemRemark",uiFieldName="wq.label.itemRemark")
	private String itemRemark;
	@TransAlias(sgaMandatory=false,sadaMandatory=false,fieldType=FieldTypeEnum.INTEGER,fieldLength="8" ,xmlFieldName="DeliveryStart",uiFieldName="wq.label.DeliveryStart")
	private String deliveryStart;
	@TransAlias(sgaMandatory=false,sadaMandatory=false,fieldType=FieldTypeEnum.INTEGER,fieldLength="8" ,xmlFieldName="DeliveryStop",uiFieldName="wq.label.DeliveryStop")
	private String deliveryStop;

	@TransAlias(sgaMandatory=false,sadaMandatory=false,fieldType=FieldTypeEnum.INTEGER,fieldLength="8" ,xmlFieldName="SentAt",uiFieldName="wq.label.SentAt")
	private String sentAt;
	
	@TransAlias(sgaMandatory=true,sadaMandatory=true,fieldType=FieldTypeEnum.TEXT,fieldLength="3" ,xmlFieldName="Currency",uiFieldName="wq.label.curr")
	private String currency;
	
	private String projectRegistrationNumber;

	private String bqNumber;

	private String mpSchedule;

	private String ppSchedule;

	private Integer eau;

	private String application;

	private String project;

	private String b2bStatus;

	private QuoteItem quoteItem;

	private MfrRequestInfo mfrRequestInfo;
	
	private String fileName;
	
	private User currentUser;
	
	private Date sentOutTime;

	public String getProject() {
		return project;
	}
	public void setProject(String project) {
		this.project = project;
	}
	public String getApplication() {
		return application;
	}
	public void setApplication(String application) {
		this.application = application;
	}
	public Integer getEau() {
		return eau;
	}
	public void setEau(Integer eau) {
		this.eau = eau;
	}
	public String getPpSchedule() {
		return ppSchedule;
	}
	public void setPpSchedule(String ppSchedule) {
		this.ppSchedule = ppSchedule;
	}
	public String getMpSchedule() {
		return mpSchedule;
	}
	public void setMpSchedule(String mpSchedule) {
		this.mpSchedule = mpSchedule;
	}
	public String getBqNumber() {
		return bqNumber;
	}
	public void setBqNumber(String bqNumber) {
		this.bqNumber = bqNumber;
	}
	public String getProjectRegistrationNumber() {
		return projectRegistrationNumber;
	}
	public void setProjectRegistrationNumber(String projectRegistrationNumber) {
		this.projectRegistrationNumber = projectRegistrationNumber;
	}

	public String getRfqCode() {
		return rfqCode;
	}
	public void setRfqCode(String rfqCode) {
		this.rfqCode = rfqCode;
	}
	public String getAction() {
		return action;
	}
	public void setAction(String action) {
		this.action = action;
	}
	public String getMfr() {
		return mfr;
	}
	public void setMfr(String mfr) {
		this.mfr = mfr;
	}
	public String getFullMPN() {
		return fullMPN;
	}
	public void setFullMPN(String fullMPN) {
		this.fullMPN = fullMPN;
	}
	public String getQuoteType() {
		return quoteType;
	}
	public void setQuoteType(String quoteType) {
		this.quoteType = quoteType;
	}
	public String getReqQty() {
		return reqQty;
	}
	public void setReqQty(String reqQty) {
		this.reqQty = reqQty;
	}
	public String getCustCode() {
		return custCode;
	}
	public void setCustCode(String custCode) {
		this.custCode = custCode;
	}
	public String getShipToCode() {
		return shipToCode;
	}
	public void setShipToCode(String shipToCode) {
		this.shipToCode = shipToCode;
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
	public String getBpmCode() {
		return bpmCode;
	}
	public void setBpmCode(String bpmCode) {
		this.bpmCode = bpmCode;
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
	public String getEcBpmCode() {
		return ecBpmCode;
	}
	public void setEcBpmCode(String ecBpmCode) {
		this.ecBpmCode = ecBpmCode;
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
	public String getTgtResale() {
		return tgtResale;
	}
	public void setTgtResale(String tgtResale) {
		this.tgtResale = tgtResale;
	}
	public String getTgtCost() {
		return tgtCost;
	}
	public void setTgtCost(String tgtCost) {
		this.tgtCost = tgtCost;
	}
	public String getItemRemark() {
		return itemRemark;
	}
	public void setItemRemark(String itemRemark) {
		this.itemRemark = itemRemark;
	}
	public String getDeliveryStart() {
		return deliveryStart;
	}
	public void setDeliveryStart(String deliveryStart) {
		this.deliveryStart = deliveryStart;
	}
	public String getDeliveryStop() {
		return deliveryStop;
	}
	public void setDeliveryStop(String deliveryStop) {
		this.deliveryStop = deliveryStop;
	}
	public String getSentAt() {
		return sentAt;
	}
	public void setSentAt(String sentAt) {
		this.sentAt = sentAt;
	}
	public String getCurrency() {
		return currency;
	}
	public void setCurrency(String currency) {
		this.currency = currency;
	}
	public int getSeq() {
		return seq;
	}
	public String getFormNumber() {
		return formNumber;
	}
	public void setFormNumber(String formNumber) {
		this.formNumber = formNumber;
	}
	public void setSeq(int seq) {
		this.seq = seq;
	}
	public String getB2bStatus() {
		return b2bStatus;
	}
	public void setB2bStatus(String b2bStatus) {
		this.b2bStatus = b2bStatus;
	}

	public QuoteItem getQuoteItem() {
		return quoteItem;
	}
	public void setQuoteItem(QuoteItem quoteItem) {
		this.quoteItem = quoteItem;
	}

	public MfrRequestInfo getMfrRequestInfo() {
		return mfrRequestInfo;
	}
	public void setMfrRequestInfo(MfrRequestInfo mfrRequestInfo) {
		this.mfrRequestInfo = mfrRequestInfo;
	}

	public int getSeq2() {
		return seq2;
	}
	public void setSeq2(int seq2) {
		this.seq2 = seq2;
	}
	public String getFileName() {
		return fileName;
	}
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	public User getCurrentUser() {
		return currentUser;
	}
	public void setCurrentUser(User currentUser) {
		this.currentUser = currentUser;
	}
	public Date getSentOutTime() {
		return sentOutTime;
	}
	public void setSentOutTime(Date sentOutTime) {
		this.sentOutTime = sentOutTime;
	}
	
}
