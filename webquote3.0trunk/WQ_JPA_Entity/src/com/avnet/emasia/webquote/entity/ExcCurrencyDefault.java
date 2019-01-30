package com.avnet.emasia.webquote.entity;

import java.io.Serializable;
import javax.persistence.*;
import java.util.Date;


/**
 * The persistent class for the EXC_CURRENCY_DEFAULT database table.
 * 
 */
@Entity
@Table(name="EXC_CURRENCY_DEFAULT")
@NamedQuery(name="ExcCurrencyDefault.findAll", query="SELECT e FROM ExcCurrencyDefault e")
public class ExcCurrencyDefault implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name="BIZ_UNIT")
	private String bizUnit;

	private String currency;

	@Column(name="UPDATED_BY")
	private String updatedBy;

	@Temporal(TemporalType.DATE)
	@Column(name="UPDATED_ON")
	private Date updatedOn;

	public ExcCurrencyDefault() {
	}

	public String getBizUnit() {
		return this.bizUnit;
	}

	public void setBizUnit(String bizUnit) {
		this.bizUnit = bizUnit;
	}

	public String getCurrency() {
		return this.currency;
	}

	public void setCurrency(String currency) {
		this.currency = currency;
	}

	public String getUpdatedBy() {
		return this.updatedBy;
	}

	public void setUpdatedBy(String updatedBy) {
		this.updatedBy = updatedBy;
	}

	public Date getUpdatedOn() {
		return this.updatedOn;
	}

	public void setUpdatedOn(Date updatedOn) {
		this.updatedOn = updatedOn;
	}

}