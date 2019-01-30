package com.avnet.emasia.webquote.entity;

import java.io.Serializable;
import javax.persistence.*;


/**
 * The persistent class for the COST_INDICATOR_RULE database table.
 * 
 */
@Entity
@Table(name="COST_INDICATOR_RULE")
public class CostIndicatorRule implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name="COST_INDICATOR", unique=true, nullable=false, length=30)
	private String costIndicator;

	@Column(name="LEAD_TIME", nullable=false)
	private Integer leadTime;


	@Column(nullable=false)
	private Integer moq;

	@Column(name="MOQ_VALUE", length=20)
	private String moqValue;

	@Column(nullable=false)
	private Integer mov;

	@Column(name="MOV_VALUE", length=20)
	private String movValue;

	@Column(nullable=false)
	private Integer mpq;

	@Column(name="MPQ_VALUE", length=20)
	private String mpqValue;
	
	@Column(name="PRICE_VALIDITY")
	private Integer priceValidity;
	
	@Column(name="SHIPMENT_VALIDITY")
	private Integer shipmentValidity;	
	
	@Column(name="PRICE_LIST_REMARKS")
	private Integer priceListRemarks;	
	
	@Column(name="PART_DESCRIPTION")
	private Integer partDescription;	

	//uni-directional one-to-one association to CostIndicator
	@OneToOne
	@JoinColumn(name="COST_INDICATOR", nullable=false, insertable=false, updatable=false)
	private CostIndicator costIndicatorBean;

	public CostIndicatorRule() {
	}

	public String getCostIndicator() {
		return this.costIndicator;
	}

	public void setCostIndicator(String costIndicator) {
		this.costIndicator = costIndicator;
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

	public String getMoqValue() {
		return this.moqValue;
	}

	public void setMoqValue(String moqValue) {
		this.moqValue = moqValue;
	}

	public Integer getMov() {
		return this.mov;
	}

	public void setMov(Integer mov) {
		this.mov = mov;
	}

	public String getMovValue() {
		return this.movValue;
	}

	public void setMovValue(String movValue) {
		this.movValue = movValue;
	}

	public Integer getMpq() {
		return this.mpq;
	}

	public void setMpq(Integer mpq) {
		this.mpq = mpq;
	}

	public String getMpqValue() {
		return this.mpqValue;
	}

	public void setMpqValue(String mpqValue) {
		this.mpqValue = mpqValue;
	}

	public CostIndicator getCostIndicatorBean() {
		return this.costIndicatorBean;
	}

	public void setCostIndicatorBean(CostIndicator costIndicatorBean) {
		this.costIndicatorBean = costIndicatorBean;
	}

	public Integer getPriceValidity() {
		return priceValidity;
	}

	public void setPriceValidity(Integer priceValidity) {
		this.priceValidity = priceValidity;
	}

	public Integer getShipmentValidity() {
		return shipmentValidity;
	}

	public void setShipmentValidity(Integer shipmentValidity) {
		this.shipmentValidity = shipmentValidity;
	}

	public Integer getPriceListRemarks() {
		return priceListRemarks;
	}

	public void setPriceListRemarks(Integer priceListRemarks) {
		this.priceListRemarks = priceListRemarks;
	}

	public Integer getPartDescription() {
		return partDescription;
	}

	public void setPartDescription(Integer partDescription) {
		this.partDescription = partDescription;
	}

	@Override
	public String toString() {
		return "CostIndicatorRule [costIndicator=" + costIndicator
				+ ", leadTime=" + leadTime + ", moq=" + moq + ", moqValue="
				+ moqValue + ", mov=" + mov + ", movValue=" + movValue
				+ ", mpq=" + mpq + ", mpqValue=" + mpqValue
				+ ", priceValidity=" + priceValidity + ", shipmentValidity="
				+ shipmentValidity + ", priceListRemarks=" + priceListRemarks
				+ ", partDescription=" + partDescription
				+ ", costIndicatorBean=" + costIndicatorBean + "]";
	}

}