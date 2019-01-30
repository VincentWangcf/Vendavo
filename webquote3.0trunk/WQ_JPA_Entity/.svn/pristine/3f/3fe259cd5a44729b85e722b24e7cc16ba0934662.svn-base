package com.avnet.emasia.webquote.edi;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Entity
@Table(name="TI_3A1")
public class ThreeAMesgOne {
	@Id
	@SequenceGenerator(name="TI_3A1_ID_GENERATOR", sequenceName="WQ_SEQ",allocationSize=1)
	@GeneratedValue(strategy=GenerationType.SEQUENCE, generator="TI_3A1_ID_GENERATOR")
	@Column(unique=true, nullable=false)
	private long id;

	@Column(name="FILE_NAME", length=100, nullable=false)
	private String fileName;
	
	@Column(name="USER_NAME", length=50)
	private String userName;
	
	@Column(name="USER_EMAIL", length=50)
	private String userEmail;
	
	@Column(name="USER_TEL", length=20)
	private String userTel;
	
	@Column(name="PO_NUM", length=35, nullable=false)
	private String poNum;
	
	@Column(name="PO_LINE_ITEM", length=5)
	private String poLineItem;
	
	@Column(name="CONTRACT_QUOTE", length=10)
	private String contractQuote;
	
	@Column(name="CM_NAME", length=50)
	private String cmName;
	
	@Column(name="CM_AVNET_CUST_CODE", length=10)
	private String cmAvnetCustCode;
	
	@Column(name="TI_EC_NUM", length=10)
	private String tiEcNum;
	
	@Column(name="TI_SOLD_TO_NUM", length=10)
	private String tiSoldToNum;
	
	@Column(name="CM_CITY", length=20)
	private String cmCity;
	
	@Column(name="CM_COUNTRY_CODE", length=5)
	private String cmCountryCode;
	
	@Column(name="CM_POSTAL_CODE", length=10)
	private String cmPostalCode;
	
	@Column(name="UC_NAME", length=50)
	private String ucName;
	
	@Column(name="UC_AVNET_CUST_NO", length=10)
	private String ucAvnetCustNo;
	
	@Column(name="EMSI_NUM", length=10)
	private String emsiNum;
	
	@Column(name="UC_CITY", length=20)
	private String ucCity;
	
	@Column(name="UC_COUNTRY_CODE", length=5)
	private String ucCountryCode;
	
	@Column(name="UC_POSTAL_CODE", length=10)
	private String ucPostalCode;
	
	@Column(name="TI_PN", length=50)
	private String tiPartNum;
	
	@Column(name="PN_CONTROLLED_ITEM", length=10)
	private String pnControlledItem;
	
	@Column(name="COMPETITOR_NAME", length=50)
	private String competitorName;
	
	@Column(name="COMPETITOR_MATERIAL", length=20)
	private String competitorMaterial;
	
	@Column(name="COMPETITOR_CURRENCY", length=10)
	private String competitorCurrency;
	
	@Column(name="COMPETITOR_AMOUNT", length=10)
	private String competitorAmount;
	
	@Column(name="REQUESTED_QUANTITY", length=10)
	private String requestedQuantity;
	
	@Column(name="UNIT_MEASUREMENT", length=10)
	private String unitMeasurement;
	
	@Column(name="ZCPR_CURRENCY", length=10)
	private String zcprCurrency;
	
	@Column(name="ZCPR_PRICE", length=10)
	private String zcprPrice;
	
	@Column(name="ZCPC_CURRENCY", length=10)
	private String zcpcCurrency;
	
	@Column(name="ZCPC_PRICE", length=10)
	private String zcpcPrice;
	
	
	@Column(name="COMMENTS", length=500)
	private String comments;
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="CREATED_DATE", nullable=false)
	private Date createdOn;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getUserEmail() {
		return userEmail;
	}

	public void setUserEmail(String userEmail) {
		this.userEmail = userEmail;
	}

	public String getUserTel() {
		return userTel;
	}

	public void setUserTel(String userTel) {
		this.userTel = userTel;
	}

	public String getPoNum() {
		return poNum;
	}

	public void setPoNum(String poNum) {
		this.poNum = poNum;
	}

	public String getPoLineItem() {
		return poLineItem;
	}

	public void setPoLineItem(String poLineItem) {
		this.poLineItem = poLineItem;
	}

	public String getContractQuote() {
		return contractQuote;
	}

	public void setContractQuote(String contractQuote) {
		this.contractQuote = contractQuote;
	}

	public String getCmName() {
		return cmName;
	}

	public void setCmName(String cmName) {
		this.cmName = cmName;
	}

	public String getCmAvnetCustCode() {
		return cmAvnetCustCode;
	}

	public void setCmAvnetCustCode(String cmAvnetCustCode) {
		this.cmAvnetCustCode = cmAvnetCustCode;
	}

	public String getTiEcNum() {
		return tiEcNum;
	}

	public void setTiEcNum(String tiEcNum) {
		this.tiEcNum = tiEcNum;
	}

	public String getTiSoldToNum() {
		return tiSoldToNum;
	}

	public void setTiSoldToNum(String tiSoldToNum) {
		this.tiSoldToNum = tiSoldToNum;
	}

	public String getCmCity() {
		return cmCity;
	}

	public void setCmCity(String cmCity) {
		this.cmCity = cmCity;
	}

	public String getCmCountryCode() {
		return cmCountryCode;
	}

	public void setCmCountryCode(String cmCountryCode) {
		this.cmCountryCode = cmCountryCode;
	}

	public String getCmPostalCode() {
		return cmPostalCode;
	}

	public void setCmPostalCode(String cmPostalCode) {
		this.cmPostalCode = cmPostalCode;
	}

	public String getUcName() {
		return ucName;
	}

	public void setUcName(String ucName) {
		this.ucName = ucName;
	}

	public String getUcAvnetCustNo() {
		return ucAvnetCustNo;
	}

	public void setUcAvnetCustNo(String ucAvnetCustNo) {
		this.ucAvnetCustNo = ucAvnetCustNo;
	}

	public String getEmsiNum() {
		return emsiNum;
	}

	public void setEmsiNum(String emsiNum) {
		this.emsiNum = emsiNum;
	}

	public String getUcCity() {
		return ucCity;
	}

	public void setUcCity(String ucCity) {
		this.ucCity = ucCity;
	}

	public String getUcCountryCode() {
		return ucCountryCode;
	}

	public void setUcCountryCode(String ucCountryCode) {
		this.ucCountryCode = ucCountryCode;
	}

	public String getUcPostalCode() {
		return ucPostalCode;
	}

	public void setUcPostalCode(String ucPostalCode) {
		this.ucPostalCode = ucPostalCode;
	}

	public String getTiPartNum() {
		return tiPartNum;
	}

	public void setTiPartNum(String tiPn) {
		this.tiPartNum = tiPn;
	}

	public String getPnControlledItem() {
		return pnControlledItem;
	}

	public void setPnControlledItem(String pnControlledItem) {
		this.pnControlledItem = pnControlledItem;
	}

	public String getCompetitorName() {
		return competitorName;
	}

	public void setCompetitorName(String competitorName) {
		this.competitorName = competitorName;
	}

	public String getCompetitorMaterial() {
		return competitorMaterial;
	}

	public void setCompetitorMaterial(String competitorMaterial) {
		this.competitorMaterial = competitorMaterial;
	}

	public String getCompetitorCurrency() {
		return competitorCurrency;
	}

	public void setCompetitorCurrency(String competitorCurrency) {
		this.competitorCurrency = competitorCurrency;
	}

	public String getCompetitorAmount() {
		return competitorAmount;
	}

	public void setCompetitorAmount(String competitorAmount) {
		this.competitorAmount = competitorAmount;
	}

	public String getRequestedQuantity() {
		return requestedQuantity;
	}

	public void setRequestedQuantity(String requestedQuantity) {
		this.requestedQuantity = requestedQuantity;
	}

	public String getUnitMeasurement() {
		return unitMeasurement;
	}

	public void setUnitMeasurement(String unitMeasurement) {
		this.unitMeasurement = unitMeasurement;
	}

	public String getZcprCurrency() {
		return zcprCurrency;
	}

	public void setZcprCurrency(String zcprCurrency) {
		this.zcprCurrency = zcprCurrency;
	}

	public String getZcprPrice() {
		return zcprPrice;
	}

	public void setZcprPrice(String zcprPrice) {
		this.zcprPrice = zcprPrice;
	}

	public String getZcpcCurrency() {
		return zcpcCurrency;
	}

	public void setZcpcCurrency(String zcpcCurrency) {
		this.zcpcCurrency = zcpcCurrency;
	}

	public String getZcpcPrice() {
		return zcpcPrice;
	}

	public void setZcpcPrice(String zcpcPrice) {
		this.zcpcPrice = zcpcPrice;
	}

	public String getComments() {
		return comments;
	}

	public void setComments(String comment) {
		this.comments = comment;
	}

	public Date getCreatedOn() {
		return createdOn;
	}

	public void setCreatedOn(Date createdOn) {
		this.createdOn = createdOn;
	}

	@Override
	public String toString() {
		return "ThreeAMesgOne [fileName=" + fileName + ", userName=" + userName + ", userEmail=" + userEmail
				+ ", userTel=" + userTel + ", poNum=" + poNum + ", poLineItem=" + poLineItem + ", contractQuote="
				+ contractQuote + "]";
	}
	
}
