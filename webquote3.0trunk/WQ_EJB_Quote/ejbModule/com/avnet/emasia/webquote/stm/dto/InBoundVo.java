package com.avnet.emasia.webquote.stm.dto;

import java.io.Serializable;

import com.avnet.emasia.webquote.entity.User;
import com.avnet.emasia.webquote.stm.annotation.TransAlias;
import com.avnet.emasia.webquote.stm.constant.FieldTypeEnum;

public class InBoundVo implements Serializable{

	public String getUnitOfMeasure() {
		return unitOfMeasure;
	}
	public void setUnitOfMeasure(String unitOfMeasure) {
		this.unitOfMeasure = unitOfMeasure;
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
	private static final long serialVersionUID = -5267960914554386518L;

	private int seq;

	@TransAlias(sgaMandatory=true,sadaMandatory=true,fieldType=FieldTypeEnum.TEXT,fieldLength="3" ,xmlFieldName="MFR",uiFieldName="MFR")
	private String mfr;
	@TransAlias(sgaMandatory=true,sadaMandatory=true,fieldType=FieldTypeEnum.TEXT,fieldLength="35" ,xmlFieldName="SQCode",uiFieldName="SQCode")
	private String sqCode;
	@TransAlias(sgaMandatory=false,sadaMandatory=true,fieldType=FieldTypeEnum.TEXT,fieldLength="35" ,xmlFieldName="VendorDebit",uiFieldName="VendorDebit")
	private String  vendorDebit;
	@TransAlias(sgaMandatory=true,sadaMandatory=true,fieldType=FieldTypeEnum.TEXT,fieldLength="5" ,xmlFieldName="QuoteType",uiFieldName="QuoteType")
	private String  quoteType;
	@TransAlias(sgaMandatory=false,sadaMandatory=false,fieldType=FieldTypeEnum.TEXT,fieldLength="30" ,xmlFieldName="SQStatus",uiFieldName="SQStatus")
	private String  sqStatus;
	@TransAlias(sgaMandatory=false,sadaMandatory=false,fieldType=FieldTypeEnum.TEXT,fieldLength="35" ,xmlFieldName="QuotedMPN",uiFieldName="MFR P/N")
	private String  quotedMPN;
	@TransAlias(sgaMandatory=true,sadaMandatory=true,fieldType=FieldTypeEnum.TEXT,fieldLength="9" ,xmlFieldName="RFQCode",uiFieldName="Avnet Quote#")
	private String rfqCode;
	@TransAlias(sgaMandatory=false,sadaMandatory=false,fieldType=FieldTypeEnum.TEXT,fieldLength="35" ,xmlFieldName="CustCode",uiFieldName="CustCode")
	private String custCode;
	@TransAlias(sgaMandatory=false,sadaMandatory=false,fieldType=FieldTypeEnum.TEXT,fieldLength="35" ,xmlFieldName="ShipToCode",uiFieldName="ShipToCode")
	private String shipToCode;
	@TransAlias(sgaMandatory=false,sadaMandatory=false,fieldType=FieldTypeEnum.TEXT,fieldLength="17" ,xmlFieldName="ContactName",uiFieldName="ContactName")
	private String contactName;
	@TransAlias(sgaMandatory=false,sadaMandatory=false,fieldType=FieldTypeEnum.TEXT,fieldLength="512" ,xmlFieldName="ContactChannel",uiFieldName="ContactChannel")
	private String contactChannel;
	@TransAlias(sgaMandatory=false,sadaMandatory=false,fieldType=FieldTypeEnum.TEXT,fieldLength="35" ,xmlFieldName="BPMCode",uiFieldName="BPM Code")
	private String bpmCode;
	@TransAlias(sgaMandatory=false,sadaMandatory=false,fieldType=FieldTypeEnum.TEXT,fieldLength="35" ,xmlFieldName="BPMName",uiFieldName="BPM Name")
	private String bpmName;
	@TransAlias(sgaMandatory=false,sadaMandatory=false,fieldType=FieldTypeEnum.TEXT,fieldLength="3" ,xmlFieldName="BPMCntCode",uiFieldName="BPM Country Code")
	private String bpmCntCode;
	@TransAlias(sgaMandatory=false,sadaMandatory=false,fieldType=FieldTypeEnum.TEXT,fieldLength="35" ,xmlFieldName="ECBPM",uiFieldName="EC BPM Code")
	private String ecBpmCode;
	@TransAlias(sgaMandatory=false,sadaMandatory=false,fieldType=FieldTypeEnum.TEXT,fieldLength="35" ,xmlFieldName="ECBPMName",uiFieldName="EC BPM Name")
	private String ecBPMName;
	@TransAlias(sgaMandatory=false,sadaMandatory=false,fieldType=FieldTypeEnum.TEXT,fieldLength="3" ,xmlFieldName="ECBPMCntCode",uiFieldName="EC BPM Country Code")
	private String ecBPMCntCode;
	@TransAlias(sgaMandatory=true,sadaMandatory=true,fieldType=FieldTypeEnum.TEXT,fieldLength="3" ,xmlFieldName="Currency",uiFieldName="Currency")
	private String currency;
	@TransAlias(sgaMandatory=true,sadaMandatory=true,fieldType=FieldTypeEnum.INTEGER,fieldLength="15" ,xmlFieldName="AuthQty",uiFieldName="AuthQty")
	private String authQty;
	@TransAlias(sgaMandatory=false,sadaMandatory=false,fieldType=FieldTypeEnum.TEXT,fieldLength="3" ,xmlFieldName="UnitOfMeasure",uiFieldName="UnitOfMeasure")
	private String unitOfMeasure;
	@TransAlias(sgaMandatory=false,sadaMandatory=true,fieldType=FieldTypeEnum.NUMBER,fieldLength="15" ,xmlFieldName="VendorResale",uiFieldName="VendorResale")
	private String vendorResale;
	@TransAlias(sgaMandatory=false,sadaMandatory=false,fieldType=FieldTypeEnum.INTEGER,fieldLength="9" ,xmlFieldName="UnitOfResale",uiFieldName="UnitOfResale")
	private String unitOfResale;
	@TransAlias(sgaMandatory=true,sadaMandatory=true,fieldType=FieldTypeEnum.NUMBER,fieldLength="15" ,xmlFieldName="VendorCost",uiFieldName="VendorCost")
	private String vendorCost;
	@TransAlias(sgaMandatory=false,sadaMandatory=false,fieldType=FieldTypeEnum.INTEGER,fieldLength="9" ,xmlFieldName="UnitOfCost",uiFieldName="UnitOfCost")
	private String unitOfCost;
	@TransAlias(sgaMandatory=false,sadaMandatory=false,fieldType=FieldTypeEnum.TEXT,fieldLength="350" ,xmlFieldName="HeadRemark",uiFieldName="HeadRemark")
	private String headRemark;
	@TransAlias(sgaMandatory=false,sadaMandatory=false,fieldType=FieldTypeEnum.TEXT,fieldLength="350" ,xmlFieldName="ItemRemark",uiFieldName="ItemRemark")
	private String itemRemark;;
	@TransAlias(sgaMandatory=true,sadaMandatory=true,fieldType=FieldTypeEnum.INTEGER,fieldLength="8" ,xmlFieldName="CreatedAt",uiFieldName="CreatedAt")
	private String createdAt;
	@TransAlias(sgaMandatory=false,sadaMandatory=true,fieldType=FieldTypeEnum.INTEGER,fieldLength="8" ,xmlFieldName="DebitExpiryAt",uiFieldName="DebitExpiryAt")
	private String debitExpiryAt;
	@TransAlias(sgaMandatory=true,sadaMandatory=false,fieldType=FieldTypeEnum.INTEGER,fieldLength="8" ,xmlFieldName="RequestedAt",uiFieldName="RequestedAt")
	private String requestedAt;
	@TransAlias(sgaMandatory=true,sadaMandatory=true,fieldType=FieldTypeEnum.INTEGER,fieldLength="8" ,xmlFieldName="QuoteExpiryAt",uiFieldName="QuoteExpiryAt")
	private String quoteExpiryAt;
	@TransAlias(sgaMandatory=false,sadaMandatory=false,fieldType=FieldTypeEnum.INTEGER,fieldLength="8" ,xmlFieldName="ReceivedAt",uiFieldName="ReceivedAt")
	private String receivedAt;
	
	private String fileName;
	
	private User currentUser;
	
	public String getMfr() {
		return mfr;
	}
	public void setMfr(String mfr) {
		this.mfr = mfr;
	}
	public String getSqCode() {
		return sqCode;
	}
	public void setSqCode(String sqCode) {
		this.sqCode = sqCode;
	}
	public String getVendorDebit() {
		return vendorDebit;
	}
	public void setVendorDebit(String vendorDebit) {
		this.vendorDebit = vendorDebit;
	}
	public String getQuoteType() {
		return quoteType;
	}
	public void setQuoteType(String quoteType) {
		this.quoteType = quoteType;
	}
	public String getSqStatus() {
		return sqStatus;
	}
	public void setSqStatus(String sqStatus) {
		this.sqStatus = sqStatus;
	}
	public String getQuotedMPN() {
		return quotedMPN;
	}
	public void setQuotedMPN(String quotedMPN) {
		this.quotedMPN = quotedMPN;
	}
	public String getRfqCode() {
		return rfqCode;
	}
	public void setRfqCode(String rfqCode) {
		this.rfqCode = rfqCode;
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
	public String getCurrency() {
		return currency;
	}
	public void setCurrency(String currency) {
		this.currency = currency;
	}
	public String getAuthQty() {
		return authQty;
	}
	public void setAuthQty(String authQty) {
		this.authQty = authQty;
	}
	public String getVendorResale() {
		return vendorResale;
	}
	public void setVendorResale(String vendorResale) {
		this.vendorResale = vendorResale;
	}
	public String getVendorCost() {
		return vendorCost;
	}
	public void setVendorCost(String vendorCost) {
		this.vendorCost = vendorCost;
	}
	public String getHeadRemark() {
		return headRemark;
	}
	public void setHeadRemark(String headRemark) {
		this.headRemark = headRemark;
	}
	public String getItemRemark() {
		return itemRemark;
	}
	public void setItemRemark(String itemRemark) {
		this.itemRemark = itemRemark;
	}
	public String getCreatedAt() {
		return createdAt;
	}
	public void setCreatedAt(String createdAt) {
		this.createdAt = createdAt;
	}
	public String getDebitExpiryAt() {
		return debitExpiryAt;
	}
	public void setDebitExpiryAt(String debitExpiryAt) {
		this.debitExpiryAt = debitExpiryAt;
	}
	public String getRequestedAt() {
		return requestedAt;
	}
	public void setRequestedAt(String requestedAt) {
		this.requestedAt = requestedAt;
	}
	public String getQuoteExpiryAt() {
		return quoteExpiryAt;
	}
	public void setQuoteExpiryAt(String quoteExpiryAt) {
		this.quoteExpiryAt = quoteExpiryAt;
	}
	public String getReceivedAt() {
		return receivedAt;
	}
	public void setReceivedAt(String receivedAt) {
		this.receivedAt = receivedAt;
	}

	@Override
	public String toString() {
		return "InBoundVo [seq=" + seq + ", mfr=" + mfr + ", sqCode=" + sqCode + ", vendorDebit=" + vendorDebit + ", quoteType=" + quoteType + ", sqStatus=" + sqStatus + ", quotedMPN=" + quotedMPN + ", rfqCode=" + rfqCode + ", custCode=" + custCode + ", shipToCode=" + shipToCode + ", contactName=" + contactName + ", contactChannel=" + contactChannel + ", bpmCode=" + bpmCode + ", bpmName=" + bpmName + ", bpmCntCode=" + bpmCntCode + ", ecBpmCode=" + ecBpmCode + ", ecBPMName=" + ecBPMName + ", ecBPMCntCode=" + ecBPMCntCode + ", currency=" + currency + ", authQty=" + authQty + ", vendorResale=" + vendorResale + ", vendorCost=" + vendorCost + ", headRemark=" + headRemark + ", itemRemark=" + itemRemark + ", createdAt=" + createdAt + ", debitExpiryAt=" + debitExpiryAt + ", requestedAt=" + requestedAt + ", quoteExpiryAt=" + quoteExpiryAt + ", receivedAt=" + receivedAt + "]";
	}
	public int getSeq() {
		return seq;
	}
	public void setSeq(int seq) {
		this.seq = seq;
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
	
}
