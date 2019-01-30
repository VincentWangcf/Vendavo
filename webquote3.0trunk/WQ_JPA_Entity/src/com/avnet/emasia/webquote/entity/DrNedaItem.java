package com.avnet.emasia.webquote.entity;

import java.io.Serializable;
import javax.persistence.*;

import java.util.Date;


/**
 * The persistent class for the DR_NEDA_ITEM database table.
 * 
 */
@Entity
@Table(name="DR_NEDA_ITEM")
public class DrNedaItem implements Serializable {
	private static final long serialVersionUID = 1L;

	@EmbeddedId
	private DrNedaItemPK id;

	@Column(name="ADDITIONAL_INFO1", length=80)
	private String additionalInfo1;

	@Column(name="ADDITIONAL_INFO2", length=80)
	private String additionalInfo2;

	@Column(name="ADDITIONAL_INFO3", length=80)
	private String additionalInfo3;

	@Column(name="ANNUAL_QTY", precision=13)
	private Integer annualQty;

	@Column(name="CORE_ID", length=48)
	private String coreId;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="CREATED_ON", nullable=false)
	private Date createdOn;

	@Column(length=5)
	private String currency;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="LAST_UPDATED_ON")
	private Date lastUpdatedOn;

	@Column(name="M_A", length=1)
	private String mA;

	@Column(name="PART_MASK", length=40)
	private String partMask;

	@Column(name="QTY_PER_BOARD", precision=13)
	private Integer qtyPerBoard;

	@Column(name="SUGGESTED_PRICE", precision=14, scale=5)
	private Double suggestedPrice;

	//bi-directional many-to-one association to DrNedaHeader
	@ManyToOne
	@JoinColumns({
		@JoinColumn(name="NEDA_ID", referencedColumnName="NEDA_ID", nullable=false, insertable=false, updatable=false),
		@JoinColumn(name="PROJECT_ID", referencedColumnName="PROJECT_ID", nullable=false, insertable=false, updatable=false)
		})
	private DrNedaHeader drNedaHeader;

	@Transient 
	private String originalSapData;
	
	@Column(name="SUPPLIER_CODE", length=3)
	private String supplierCode;

	@Column(name="SUPPLIER_NAME", length=30)
	private String supplierName;

	@Column(length=10)
	private String application;

	@Column(name="APPLICATION_DESCRIPTION", length=80)
	private String applicationDescription;

	@Column(name="DR_NUMBER", length=30)
	private String drNumber;

	@Column(name="FAE_ID", length=12)
	private String faeId;

	@Column(name="FAE_NAME", length=30)
	private String faeName;
	
	@Column(name="STATUS", length=30)
	private String status;
	
	@Temporal(TemporalType.DATE)
	@Column(name="CREATION_DATE", nullable=false)
	private Date creationDate;
	
	
	public DrNedaItem() {
	}

	public DrNedaItemPK getId() {
		return this.id;
	}

	public void setId(DrNedaItemPK id) {
		this.id = id;
	}

	public String getAdditionalInfo1() {
		return this.additionalInfo1;
	}

	public void setAdditionalInfo1(String additionalInfo1) {
		this.additionalInfo1 = additionalInfo1;
	}

	public String getAdditionalInfo2() {
		return this.additionalInfo2;
	}

	public void setAdditionalInfo2(String additionalInfo2) {
		this.additionalInfo2 = additionalInfo2;
	}

	public String getAdditionalInfo3() {
		return this.additionalInfo3;
	}

	public void setAdditionalInfo3(String additionalInfo3) {
		this.additionalInfo3 = additionalInfo3;
	}

	public Integer getAnnualQty() {
		return this.annualQty;
	}

	public void setAnnualQty(Integer annualQty) {
		this.annualQty = annualQty;
	}

	public String getCoreId() {
		return this.coreId;
	}

	public void setCoreId(String coreId) {
		this.coreId = coreId;
	}

	public Date getCreatedOn() {
		return this.createdOn;
	}

	public void setCreatedOn(Date createdOn) {
		this.createdOn = createdOn;
	}

	public String getCurrency() {
		return this.currency;
	}

	public void setCurrency(String currency) {
		this.currency = currency;
	}

	public Date getLastUpdatedOn() {
		return this.lastUpdatedOn;
	}

	public void setLastUpdatedOn(Date lastUpdatedOn) {
		this.lastUpdatedOn = lastUpdatedOn;
	}

	public String getMA() {
		return this.mA;
	}

	public void setMA(String mA) {
		this.mA = mA;
	}

	public String getPartMask() {
		return this.partMask;
	}

	public void setPartMask(String partMask) {
		this.partMask = partMask;
	}

	public Integer getQtyPerBoard() {
		return this.qtyPerBoard;
	}

	public void setQtyPerBoard(Integer qtyPerBoard) {
		this.qtyPerBoard = qtyPerBoard;
	}

	public Double getSuggestedPrice() {
		return this.suggestedPrice;
	}

	public void setSuggestedPrice(Double suggestedPrice) {
		this.suggestedPrice = suggestedPrice;
	}

	public DrNedaHeader getDrNedaHeader() {
		return this.drNedaHeader;
	}

	public void setDrNedaHeader(DrNedaHeader drNedaHeader) {
		this.drNedaHeader = drNedaHeader;
	}

	@Override
	public String toString() {
		return "DrNedaItem [id=" + id + ", additionalInfo1=" + additionalInfo1
				+ ", additionalInfo2=" + additionalInfo2 + ", additionalInfo3="
				+ additionalInfo3 + ", annualQty=" + annualQty + ", coreId="
				+ coreId + ", createdOn=" + createdOn + ", currency="
				+ currency + ", lastUpdatedOn=" + lastUpdatedOn + ", mA=" + mA
				+ ", partMask=" + partMask + ", qtyPerBoard=" + qtyPerBoard
				+ ", suggestedPrice=" + suggestedPrice + ", drNedaHeader="
				+ drNedaHeader + "]";
	}


	public String getOriginalSapData() {
		return originalSapData;
	}
	@Transient
	public void setOriginalSapData(String originalSapData) {
		this.originalSapData = originalSapData;
	}

	public String getSupplierCode() {
		return this.supplierCode;
	}

	public void setSupplierCode(String supplierCode) {
		this.supplierCode = supplierCode;
	}

	public String getSupplierName() {
		return this.supplierName;
	}

	public void setSupplierName(String supplierName) {
		this.supplierName = supplierName;
	}
	

	public String getApplication() {
		return this.application;
	}

	public void setApplication(String application) {
		this.application = application;
	}

	public String getApplicationDescription() {
		return this.applicationDescription;
	}

	public void setApplicationDescription(String applicationDescription) {
		this.applicationDescription = applicationDescription;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	
	public String getDrNumber() {
		return this.drNumber;
	}

	public void setDrNumber(String drNumber) {
		this.drNumber = drNumber;
	}

	public String getFaeId() {
		return this.faeId;
	}

	public void setFaeId(String faeId) {
		this.faeId = faeId;
	}

	public String getFaeName() {
		return this.faeName;
	}

	public void setFaeName(String faeName) {
		this.faeName = faeName;
	}
	
	public Date getCreationDate() {
		return this.creationDate;
	}

	public void setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
	}

}