package com.avnet.emasia.webquote.entity;

import java.io.Serializable;
import javax.persistence.*;

import java.sql.Timestamp;
import java.util.Date;


/**
 * The persistent class for the FREE_STOCK database table.
 * 
 */
@Entity
@Table(name="FREE_STOCK")
public class FreeStock implements Serializable {
	private static final long serialVersionUID = 1L;

	@EmbeddedId
	private FreeStockPK id;
	
	@ManyToOne
	@JoinColumn(name="MATERIAL_ID")
	private Material material;

	@Column(name="ABC_INDICATOR", length=1)
	private String abcIndicator;

	@Column(name="ALLOCATED_QTY", precision=16)
	private Integer allocatedQty;

	@Column(name="AVAILABLE_PO_QTY", precision=16)
	private Integer availablePoQty;

	@Column(name="AVERAGE_COST", precision=12, scale=2)
	private Double averageCost;

	@Column(name="COMMISSION_GROUP", length=2)
	private String commissionGroup;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="CREATED_ON", nullable=false)
	private Date createdOn;

	@Column(length=40)
	private String description;

	@Column(name="FREE_STOCK", precision=16)
	private Integer freeStock;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="LAST_UPDATED_ON")
	private Date lastUpdatedOn;

	@Column(name="LEAD_TIME", precision=3)
	private Integer leadTime;

	@Column(precision=16)
	private Integer moq;

	@Column(precision=16)
	private Integer mpq;

	@Temporal(TemporalType.DATE)
	@Column(name="OLDEST_DATE")
	private Date oldestDate;

	@Column(name="OPEN_PO_QTY", precision=16)
	private Integer openPoQty;

	@Column(name="PO_COST", precision=12, scale=2)
	private Double poCost;

	@Column(name="QTY_ON_HAND", precision=16)
	private Integer qtyOnHand;

	@Column(name="SO_NOT_SUPP_BY_PO", precision=16)
	private Integer soNotSuppByPo;

	@Column(name="STANDARD_PRICE", precision=12, scale=2)
	private Double standardPrice;

	@Transient 
	private String originalSapData;
	
	public FreeStock() {
	}

	public FreeStockPK getId() {
		return this.id;
	}

	public void setId(FreeStockPK id) {
		this.id = id;
	}

	public String getAbcIndicator() {
		return this.abcIndicator;
	}

	public void setAbcIndicator(String abcIndicator) {
		this.abcIndicator = abcIndicator;
	}

	public Integer getAllocatedQty() {
		return this.allocatedQty;
	}

	public void setAllocatedQty(Integer allocatedQty) {
		this.allocatedQty = allocatedQty;
	}

	public Integer getAvailablePoQty() {
		return this.availablePoQty;
	}

	public void setAvailablePoQty(Integer availablePoQty) {
		this.availablePoQty = availablePoQty;
	}

	public Double getAverageCost() {
		return this.averageCost;
	}

	public void setAverageCost(Double averageCost) {
		this.averageCost = averageCost;
	}

	public String getCommissionGroup() {
		return this.commissionGroup;
	}

	public void setCommissionGroup(String commissionGroup) {
		this.commissionGroup = commissionGroup;
	}

	public Date getCreatedOn() {
		return this.createdOn;
	}

	public void setCreatedOn(Date createdOn) {
		this.createdOn = createdOn;
	}

	public String getDescription() {
		return this.description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Integer getFreeStock() {
		return this.freeStock;
	}

	public void setFreeStock(Integer freeStock) {
		this.freeStock = freeStock;
	}

	public Date getLastUpdatedOn() {
		return this.lastUpdatedOn;
	}

	public void setLastUpdatedOn(Date lastUpdatedOn) {
		this.lastUpdatedOn = lastUpdatedOn;
	}

	public Integer getLeadTime() {
		return this.leadTime;
	}

	public void setLeadTime(Integer leadTime) {
		this.leadTime = leadTime;
	}

	public Integer getMoq() {
		return this.moq;
	}

	public void setMoq(Integer moq) {
		this.moq = moq;
	}

	public Integer getMpq() {
		return this.mpq;
	}

	public void setMpq(Integer mpq) {
		this.mpq = mpq;
	}

	public Date getOldestDate() {
		return this.oldestDate;
	}

	public void setOldestDate(Date oldestDate) {
		this.oldestDate = oldestDate;
	}

	public Integer getOpenPoQty() {
		return this.openPoQty;
	}

	public void setOpenPoQty(Integer openPoQty) {
		this.openPoQty = openPoQty;
	}

	public Double getPoCost() {
		return this.poCost;
	}

	public void setPoCost(Double poCost) {
		this.poCost = poCost;
	}

	public Integer getQtyOnHand() {
		return this.qtyOnHand;
	}

	public void setQtyOnHand(Integer qtyOnHand) {
		this.qtyOnHand = qtyOnHand;
	}

	public Integer getSoNotSuppByPo() {
		return this.soNotSuppByPo;
	}

	public void setSoNotSuppByPo(Integer soNotSuppByPo) {
		this.soNotSuppByPo = soNotSuppByPo;
	}

	public Double getStandardPrice() {
		return this.standardPrice;
	}

	public void setStandardPrice(Double standardPrice) {
		this.standardPrice = standardPrice;
	}

	public Material getMaterial() {
		return material;
	}

	public void setMaterial(Material material) {
		this.material = material;
	}
	public String getOriginalSapData() {
		return originalSapData;
	}
	
	@Transient
	public void setOriginalSapData(String originalSapData) {
		this.originalSapData = originalSapData;
	}
}