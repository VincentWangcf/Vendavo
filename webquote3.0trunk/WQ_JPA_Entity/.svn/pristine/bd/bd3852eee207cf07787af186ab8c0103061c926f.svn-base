package com.avnet.emasia.webquote.entity;

import java.io.Serializable;
import java.math.BigDecimal;

import javax.persistence.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;


/**
 * The persistent class for the POS database table.
 * 
 */
@Entity
@Table(name="POS_SUMMARY")
public class PosSummary implements Serializable {
	private static final long serialVersionUID = 1L;

	@EmbeddedId
	private PosSummaryPK id;

	@Column(name="LAST_12_MONTH_QTY")
	private Long last12MonthQty;

	@Column(name="LAST_0_MIN")
	private Double last0Min = null;

	@Column(name="LAST_0_MEAN")
	private Double last0Mean = null;

	@Column(name="LAST_0_MAX")
	private Double last0Max = null;

	@Column(name="LAST_0_QTY")
	private Integer last0Qty = null;

	@Column(name="LAST_1_MIN")
	private Double last1Min = null;

	@Column(name="LAST_1_MEAN")
	private Double last1Mean = null;

	@Column(name="LAST_1_MAX")
	private Double last1Max = null;

	@Column(name="LAST_1_QTY")
	private Integer last1Qty = null;

	@Column(name="LAST_2_MIN")
	private Double last2Min = null;

	@Column(name="LAST_2_MEAN")
	private Double last2Mean = null;

	@Column(name="LAST_2_MAX")
	private Double last2Max = null;

	@Column(name="LAST_2_QTY")
	private Integer last2Qty = null;
	
	@Column(name="LAST_3_MIN")
	private Double last3Min = null;

	@Column(name="LAST_3_MEAN")
	private Double last3Mean = null;

	@Column(name="LAST_3_MAX")
	private Double last3Max = null;

	@Column(name="LAST_3_QTY")
	private Integer last3Qty = null;	

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="LAST_UPDATE_DATE")
	private Date lastUpdateDate;

	public PosSummary() {
	}

	public PosSummaryPK getId() {
		return this.id;
	}

	public void setId(PosSummaryPK id) {
		this.id = id;
	}

	public Long getLast12MonthQty() {
		return last12MonthQty;
	}

	public void setLast12MonthQty(Long last12MonthQty) {
		this.last12MonthQty = last12MonthQty;
	}

	public Double getLast0Min() {
		return last0Min;
	}

	public void setLast0Min(Double last0Min) {
		this.last0Min = last0Min;
	}

	public Double getLast0Mean() {
		return last0Mean;
	}

	public void setLast0Mean(Double last0Mean) {
		this.last0Mean = last0Mean;
	}

	public Double getLast0Max() {
		return last0Max;
	}

	public void setLast0Max(Double last0Max) {
		this.last0Max = last0Max;
	}

	public Integer getLast0Qty() {
		return last0Qty;
	}

	public void setLast0Qty(Integer last0Qty) {
		this.last0Qty = last0Qty;
	}

	public Double getLast1Min() {
		return last1Min;
	}

	public void setLast1Min(Double last1Min) {
		this.last1Min = last1Min;
	}

	public Double getLast1Mean() {
		return last1Mean;
	}

	public void setLast1Mean(Double last1Mean) {
		this.last1Mean = last1Mean;
	}

	public Double getLast1Max() {
		return last1Max;
	}

	public void setLast1Max(Double last1Max) {
		this.last1Max = last1Max;
	}

	public Integer getLast1Qty() {
		return last1Qty;
	}

	public void setLast1Qty(Integer last1Qty) {
		this.last1Qty = last1Qty;
	}

	public Double getLast2Min() {
		return last2Min;
	}

	public void setLast2Min(Double last2Min) {
		this.last2Min = last2Min;
	}

	public Double getLast2Mean() {
		return last2Mean;
	}

	public void setLast2Mean(Double last2Mean) {
		this.last2Mean = last2Mean;
	}

	public Double getLast2Max() {
		return last2Max;
	}

	public void setLast2Max(Double last2Max) {
		this.last2Max = last2Max;
	}

	public Integer getLast2Qty() {
		return last2Qty;
	}

	public void setLast2Qty(Integer last2Qty) {
		this.last2Qty = last2Qty;
	}

	public Double getLast3Min() {
		return last3Min;
	}

	public void setLast3Min(Double last3Min) {
		this.last3Min = last3Min;
	}

	public Double getLast3Mean() {
		return last3Mean;
	}

	public void setLast3Mean(Double last3Mean) {
		this.last3Mean = last3Mean;
	}

	public Double getLast3Max() {
		return last3Max;
	}

	public void setLast3Max(Double last3Max) {
		this.last3Max = last3Max;
	}

	public Integer getLast3Qty() {
		return last3Qty;
	}

	public void setLast3Qty(Integer last3Qty) {
		this.last3Qty = last3Qty;
	}

	public Date getLastUpdateDate() {
		return lastUpdateDate;
	}

	public void setLastUpdateDate(Date lastUpdateDate) {
		this.lastUpdateDate = lastUpdateDate;
	}
	
	@Transient
	public List<Double> sales0Amount = new ArrayList<Double>(); 
	
	@Transient
	public List<Double> sales1Amount = new ArrayList<Double>();  
	
	@Transient
	public List<Double> sales2Amount = new ArrayList<Double>();  
	
	@Transient
	public List<Double> sales3Amount = new ArrayList<Double>(); 

	public List<Double> getSales0Amount() {
		return sales0Amount;
	}

	public void setSales0Amount(List<Double> sales0Amount) {
		this.sales0Amount = sales0Amount;
	}

	public List<Double> getSales1Amount() {
		return sales1Amount;
	}

	public void setSales1Amount(List<Double> sales1Amount) {
		this.sales1Amount = sales1Amount;
	}

	public List<Double> getSales2Amount() {
		return sales2Amount;
	}

	public void setSales2Amount(List<Double> sales2Amount) {
		this.sales2Amount = sales2Amount;
	}

	public List<Double> getSales3Amount() {
		return sales3Amount;
	}

	public void setSales3Amount(List<Double> sales3Amount) {
		this.sales3Amount = sales3Amount;
	}
}