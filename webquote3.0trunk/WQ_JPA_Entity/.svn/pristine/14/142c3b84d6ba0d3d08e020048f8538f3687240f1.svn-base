package com.avnet.emasia.webquote.entity;

import java.io.Serializable;
import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;


/**
 * The persistent class for the AUDIT_EXCHANGE_RATE database table.
 * 
 */
@Entity
@Table(name="AUDIT_EXCHANGE_RATE")
public class AuditExchangeRate implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@SequenceGenerator(name="AUDIT_EXCHANGE_RATE_ID_GENERATOR", sequenceName="WQ_SEQ")
	@GeneratedValue(strategy=GenerationType.SEQUENCE, generator="AUDIT_EXCHANGE_RATE_ID_GENERATOR")
	private long id;

	@Column(name="BIZ_UNIT_NEW")
	private String bizUnitNew;

	@Column(name="BIZ_UNIT_OLD")
	private String bizUnitOld;

	@Column(name="CURR_FROM_NEW")
	private String currFromNew;

	@Column(name="CURR_FROM_OLD")
	private String currFromOld;

	@Column(name="CURR_TO_NEW")
	private String currToNew;

	@Column(name="CURR_TO_OLD")
	private String currToOld;

	@Column(name="EX_RATE_FROM_NEW")
	private BigDecimal exRateFromNew;

	@Column(name="EX_RATE_FROM_OLD")
	private BigDecimal exRateFromOld;

	@Column(name="EX_RATE_TO_NEW")
	private BigDecimal exRateToNew;

	@Column(name="EX_RATE_TO_OLD")
	private BigDecimal exRateToOld;

	@Column(name="HANDLING_NEW")
	private BigDecimal handlingNew;

	@Column(name="HANDLING_OLD")
	private BigDecimal handlingOld;

	@Column(name="ID_NEW")
	private BigDecimal idNew;

	@Column(name="ID_OLD")
	private BigDecimal idOld;

	@Column(name="LAST_UPDATED_BY")
	private String lastUpdatedBy;

	@Column(name="LAST_UPDATED_NAME")
	private String lastUpdatedName;

	@Column(name="SOLD_TO_CODE_NEW")
	private String soldToCodeNew;

	@Column(name="SOLD_TO_CODE_OLD")
	private String soldToCodeOld;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="UPDATE_DATE")
	private Date updateDate;

	@Temporal(TemporalType.DATE)
	@Column(name="VALID_FROM_NEW")
	private Date validFromNew;

	@Temporal(TemporalType.DATE)
	@Column(name="VALID_FROM_OLD")
	private Date validFromOld;

	@Column(name="VAT_NEW")
	private BigDecimal vatNew;

	@Column(name="VAT_OLD")
	private BigDecimal vatOld;
	
	@Column(name="ACTION")
	private String action;

	public AuditExchangeRate() {
	}

	public long getId() {
		return this.id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getBizUnitNew() {
		return this.bizUnitNew;
	}

	public void setBizUnitNew(String bizUnitNew) {
		this.bizUnitNew = bizUnitNew;
	}

	public String getBizUnitOld() {
		return this.bizUnitOld;
	}

	public void setBizUnitOld(String bizUnitOld) {
		this.bizUnitOld = bizUnitOld;
	}

	public String getCurrFromNew() {
		return this.currFromNew;
	}

	public void setCurrFromNew(String currFromNew) {
		this.currFromNew = currFromNew;
	}

	public String getCurrFromOld() {
		return this.currFromOld;
	}

	public void setCurrFromOld(String currFromOld) {
		this.currFromOld = currFromOld;
	}

	public String getCurrToNew() {
		return this.currToNew;
	}

	public void setCurrToNew(String currToNew) {
		this.currToNew = currToNew;
	}

	public String getCurrToOld() {
		return this.currToOld;
	}

	public void setCurrToOld(String currToOld) {
		this.currToOld = currToOld;
	}

	public BigDecimal getExRateFromNew() {
		return this.exRateFromNew;
	}

	public void setExRateFromNew(BigDecimal exRateFromNew) {
		this.exRateFromNew = exRateFromNew;
	}

	public BigDecimal getExRateFromOld() {
		return this.exRateFromOld;
	}

	public void setExRateFromOld(BigDecimal exRateFromOld) {
		this.exRateFromOld = exRateFromOld;
	}

	public BigDecimal getExRateToNew() {
		return this.exRateToNew;
	}

	public void setExRateToNew(BigDecimal exRateToNew) {
		this.exRateToNew = exRateToNew;
	}

	public BigDecimal getExRateToOld() {
		return this.exRateToOld;
	}

	public void setExRateToOld(BigDecimal exRateToOld) {
		this.exRateToOld = exRateToOld;
	}

	public BigDecimal getHandlingNew() {
		return this.handlingNew;
	}

	public void setHandlingNew(BigDecimal handlingNew) {
		this.handlingNew = handlingNew;
	}

	public BigDecimal getHandlingOld() {
		return this.handlingOld;
	}

	public void setHandlingOld(BigDecimal handlingOld) {
		this.handlingOld = handlingOld;
	}

	public BigDecimal getIdNew() {
		return this.idNew;
	}

	public void setIdNew(BigDecimal idNew) {
		this.idNew = idNew;
	}

	public BigDecimal getIdOld() {
		return this.idOld;
	}

	public void setIdOld(BigDecimal idOld) {
		this.idOld = idOld;
	}

	public String getLastUpdatedBy() {
		return this.lastUpdatedBy;
	}

	public void setLastUpdatedBy(String lastUpdatedBy) {
		this.lastUpdatedBy = lastUpdatedBy;
	}

	public String getLastUpdatedName() {
		return this.lastUpdatedName;
	}

	public void setLastUpdatedName(String lastUpdatedName) {
		this.lastUpdatedName = lastUpdatedName;
	}

	public String getSoldToCodeNew() {
		return this.soldToCodeNew;
	}

	public void setSoldToCodeNew(String soldToCodeNew) {
		this.soldToCodeNew = soldToCodeNew;
	}

	public String getSoldToCodeOld() {
		return this.soldToCodeOld;
	}

	public void setSoldToCodeOld(String soldToCodeOld) {
		this.soldToCodeOld = soldToCodeOld;
	}

	public Date getUpdateDate() {
		return this.updateDate;
	}

	public void setUpdateDate(Date updateDate) {
		this.updateDate = updateDate;
	}

	public Date getValidFromNew() {
		return this.validFromNew;
	}

	public void setValidFromNew(Date validFromNew) {
		this.validFromNew = validFromNew;
	}

	public Date getValidFromOld() {
		return this.validFromOld;
	}

	public void setValidFromOld(Date validFromOld) {
		this.validFromOld = validFromOld;
	}

	public BigDecimal getVatNew() {
		return this.vatNew;
	}

	public void setVatNew(BigDecimal vatNew) {
		this.vatNew = vatNew;
	}

	public BigDecimal getVatOld() {
		return this.vatOld;
	}

	public void setVatOld(BigDecimal vatOld) {
		this.vatOld = vatOld;
	}

	public String getAction() {
		return action;
	}

	public void setAction(String action) {
		this.action = action;
	}

}