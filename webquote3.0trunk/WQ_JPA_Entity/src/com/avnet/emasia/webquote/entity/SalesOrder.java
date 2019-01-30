package com.avnet.emasia.webquote.entity;

import java.io.Serializable;

import javax.persistence.*;

import com.avnet.emasia.webquote.entity.util.SalesOrderListener;

import java.sql.Timestamp;
import java.util.Date;


/**
 * The persistent class for the SALES_ORDER database table.
 * 
 */
@Entity
@Table(name="SALES_ORDER")
@EntityListeners(SalesOrderListener.class)
public class SalesOrder implements Serializable {
	private static final long serialVersionUID = 1L;

	@EmbeddedId
	private SalesOrderPK id;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="CREATED_ON", nullable=false)
	private Date createdOn;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="LAST_UPDATED_ON")
	private Date lastUpdatedOn;

	@Temporal(TemporalType.DATE)
	@Column(name="LAST_UPDATING_DATE")
	private Date lastUpdatingDate;
	
	@Column(name="SALES_ORDER_RESALE", precision=9, scale=5)
	private Double salesOrderResale;
	
	
	@Column(name="SALES_ORDER_QTY")
	private Integer salesOrderQty;

	//uni-directional many-to-one association to QuoteItem
	@ManyToOne
	@JoinColumn(name="QUOTE_NUMBER", referencedColumnName="QUOTE_NUMBER", nullable=false, insertable=false, updatable=false)
	private QuoteItem quoteItem;

	// operation indicator 
	// D for Delete, U for update( if exist then update, otherwise , insert.) , if it is null. no action. 
	@Transient 
	private String indicator;
	
	@Column(name="CURR")
	private String curr;
	
	@Column(name="PRICE_UNIT")
	private Integer priceUnit;
	
	@Column(name="D_PRICE")
	private Double dPrice;
	
	@Column(name="USD_PRICE")
	private Double usdPrice;
	
	public SalesOrder() {
	}

	public SalesOrderPK getId() {
		return this.id;
	}

	public void setId(SalesOrderPK id) {
		this.id = id;
	}

	public Date getCreatedOn() {
		return this.createdOn;
	}

	public void setCreatedOn(Date createdOn) {
		this.createdOn = createdOn;
	}

	public Date getLastUpdatedOn() {
		return this.lastUpdatedOn;
	}

	public void setLastUpdatedOn(Date lastUpdatedOn) {
		this.lastUpdatedOn = lastUpdatedOn;
	}

	public Date getLastUpdatingDate() {
		return this.lastUpdatingDate;
	}

	public void setLastUpdatingDate(Date lastUpdatingDate) {
		this.lastUpdatingDate = lastUpdatingDate;
	}

	public QuoteItem getQuoteItem() {
		return this.quoteItem;
	}

	public void setQuoteItem(QuoteItem quoteItem) {
		this.quoteItem = quoteItem;
	}

	public String getIndicator() {
		return indicator;
	}
	@Transient
	public void setIndicator(String indicator) {
		this.indicator = indicator;
	}

	public Double getSalesOrderResale() {
		return salesOrderResale;
	}

	public void setSalesOrderResale(Double salesOrderResale) {
		this.salesOrderResale = salesOrderResale;
	}

	public Integer getSalesOrderQty() {
		return salesOrderQty;
	}

	public void setSalesOrderQty(Integer salesOrderQty) {
		this.salesOrderQty = salesOrderQty;
	}

	public String getCurr() {
		return curr;
	}

	public void setCurr(String curr) {
		this.curr = curr;
	}

	public Integer getPriceUnit() {
		return priceUnit;
	}

	public void setPriceUnit(Integer priceUnit) {
		this.priceUnit = priceUnit;
	}

	public Double getdPrice() {
		return dPrice;
	}

	public void setdPrice(Double dPrice) {
		this.dPrice = dPrice;
	}

	public Double getUsdPrice() {
		return usdPrice;
	}

	public void setUsdPrice(Double usdPrice) {
		this.usdPrice = usdPrice;
	}


	
	
}