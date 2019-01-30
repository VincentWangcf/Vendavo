package com.avnet.emasia.webquote.entity;

import java.io.Serializable;

import javax.persistence.*;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;


/**
 * The persistent class for the QUOTE_ITEM_DESIGN_HIS database table.
 * 
 */
@Entity
@Table(name="QUOTE_ITEM_DESIGN_HIS")
	
	//private static final long serialVersionUID = 1L;
public class QuoteItemDesignHis implements Serializable {
	

	@Transient
	private Map<String,Boolean> highLightMap = new HashMap<String,Boolean>();
	
	@EmbeddedId
	private QuoteItemDesignHisPK id;

	@Column(name="BMT_FLAG_CODE")
	private String bmtFlagCode;

	@Column(name="CREATED_BY")
	private String createdBy;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="CREATED_TIME")
	private Date createdTime;

	@Column(name="DR_ALT_CURRENCY")
	private String drAltCurrency;

	@Column(name="DR_COMMENTS")
	private String drComments;

	@Column(name="DR_COST")
	private Double drCost;

	@Column(name="DR_COST_ALT_CURRENCY")
	private Double drCostAltCurrency;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="DR_EFFECTIVE_DATE")
	private Date drEffectiveDate;

	@Column(name="DR_EXCHANGE_RATE")
	private Integer drExchangeRate;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="DR_EXPIRY_DATE")
	private Date drExpiryDate;

	@Column(name="DR_MFR_NO")
	private String drMfrNo;

	@Column(name="DR_MFR_QUOTE_NO")
	private String drMfrQuoteNo;

	@Column(name="DR_NO")
	private String drNo;

	@Column(name="DR_QUOTE_QTY")
	private Integer drQuoteQty;

	@Column(name="DR_RESALE")
	private Integer drResale;

	@Column(name="DR_RESALE_ALT_CURRENCY")
	private Integer drResaleAltCurrency;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="END_DATE")
	private Date endDate;


	@Column(name="LAST_UPDATED_BY")
	private String lastUpdatedBy;

	@Temporal(TemporalType.DATE)
	@Column(name="LAST_UPDATED_TIME")
	private Date lastUpdatedTime;

	@Column(name="\"VERSION\"")
	private Integer version;

	public QuoteItemDesignHis() {
	}

	public QuoteItemDesignHisPK getId() {
		return id;
	}

	public void setId(QuoteItemDesignHisPK id) {
		this.id = id;
	}

	public String getBmtFlagCode() {
		return bmtFlagCode;
	}

	public void setBmtFlagCode(String bmtFlagCode) {
		this.bmtFlagCode = bmtFlagCode;
	}

	public String getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}

	public Date getCreatedTime() {
		return createdTime;
	}

	public void setCreatedTime(Date createdTime) {
		this.createdTime = createdTime;
	}

	public String getDrAltCurrency() {
		return drAltCurrency;
	}

	public void setDrAltCurrency(String drAltCurrency) {
		this.drAltCurrency = drAltCurrency;
	}

	public String getDrComments() {
		return drComments;
	}

	public void setDrComments(String drComments) {
		this.drComments = drComments;
	}

	public Double getDrCost() {
		return drCost;
	}

	public void setDrCost(Double drCost) {
		this.drCost = drCost;
	}

	public Double getDrCostAltCurrency() {
		return drCostAltCurrency;
	}

	public void setDrCostAltCurrency(Double drCostAltCurrency) {
		this.drCostAltCurrency = drCostAltCurrency;
	}

	public Date getDrEffectiveDate() {
		return drEffectiveDate;
	}

	public void setDrEffectiveDate(Date drEffectiveDate) {
		this.drEffectiveDate = drEffectiveDate;
	}

	public Integer getDrExchangeRate() {
		return drExchangeRate;
	}

	public void setDrExchangeRate(Integer drExchangeRate) {
		this.drExchangeRate = drExchangeRate;
	}

	public Date getDrExpiryDate() {
		return drExpiryDate;
	}

	public void setDrExpiryDate(Date drExpiryDate) {
		this.drExpiryDate = drExpiryDate;
	}

	public String getDrMfrNo() {
		return drMfrNo;
	}

	public void setDrMfrNo(String drMfrNo) {
		this.drMfrNo = drMfrNo;
	}

	public String getDrMfrQuoteNo() {
		return drMfrQuoteNo;
	}

	public void setDrMfrQuoteNo(String drMfrQuoteNo) {
		this.drMfrQuoteNo = drMfrQuoteNo;
	}

	public String getDrNo() {
		return drNo;
	}

	public void setDrNo(String drNo) {
		this.drNo = drNo;
	}

	public Integer getDrQuoteQty() {
		return drQuoteQty;
	}

	public void setDrQuoteQty(Integer drQuoteQty) {
		this.drQuoteQty = drQuoteQty;
	}

	public Integer getDrResale() {
		return drResale;
	}

	public void setDrResale(Integer drResale) {
		this.drResale = drResale;
	}

	public Integer getDrResaleAltCurrency() {
		return drResaleAltCurrency;
	}

	public void setDrResaleAltCurrency(Integer drResaleAltCurrency) {
		this.drResaleAltCurrency = drResaleAltCurrency;
	}

	public Date getEndDate() {
		return endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	public String getLastUpdatedBy() {
		return lastUpdatedBy;
	}

	public void setLastUpdatedBy(String lastUpdatedBy) {
		this.lastUpdatedBy = lastUpdatedBy;
	}

	public Date getLastUpdatedTime() {
		return lastUpdatedTime;
	}

	public void setLastUpdatedTime(Date lastUpdatedTime) {
		this.lastUpdatedTime = lastUpdatedTime;
	}

	public Integer getVersion() {
		return version;
	}

	public void setVersion(Integer version) {
		this.version = version;
	}
	
	public Map<String, Boolean> getHighLightMap() {
		return highLightMap;
	}

	public void setHighLightMap(Map<String, Boolean> highLightMap) {
		this.highLightMap = highLightMap;
	}
	

}