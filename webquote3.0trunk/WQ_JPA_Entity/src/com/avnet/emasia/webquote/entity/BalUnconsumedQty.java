package com.avnet.emasia.webquote.entity;

// Generated 2014-5-9 16:54:06 by Hibernate Tools 3.4.0.CR1

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

/**
 * BalUnconsumedQty generated by hbm2java
 */
@Entity
@Table(name = "BAL_UNCONSUMED_QTY", uniqueConstraints = @UniqueConstraint(columnNames = {
		"QUOTE_NUMBER", "MFR", "QUOTED_PART_NUMBER", "SUPPLIER_QUOTE_NUMBER",
		"BIZ_UNIT" }))
public class BalUnconsumedQty implements java.io.Serializable
{
	@Id
	@SequenceGenerator(name="BalUnconsumedQty_ID_GENERATOR", sequenceName="WQ_SEQ",allocationSize=1)
	@GeneratedValue(strategy=GenerationType.SEQUENCE, generator="BalUnconsumedQty_ID_GENERATOR")
	@Column(name="ID", unique=true, nullable=false)
	private long id;

	@Column(name = "QUOTE_NUMBER", length = 20)
	private String quoteNumber;
	@Column(name = "MFR", nullable = false, length = 10)
	private String mfr;
	@Column(name = "QUOTED_PART_NUMBER", length = 80)
	private String quotedPartNumber;
	@Column(name = "SUPPLIER_QUOTE_NUMBER", nullable = false, length = 50)
	private String supplierQuoteNumber;
	@Column(name = "BAL_UNCONSUMED_QTY", nullable = false, precision = 22, scale = 0)
	private Integer buQty;
	@Column(name = "BIZ_UNIT", nullable = false, length = 10)
	private String bizUnit;

	public BalUnconsumedQty()
	{
	}

	public long getId()
	{
		return this.id;
	}

	public void setId(long id)
	{
		this.id = id;
	}


	public String getQuoteNumber()
	{
		return this.quoteNumber;
	}

	public void setQuoteNumber(String quoteNumber)
	{
		this.quoteNumber = quoteNumber;
	}


	public String getMfr()
	{
		return this.mfr;
	}

	public void setMfr(String mfr)
	{
		this.mfr = mfr;
	}


	public String getQuotedPartNumber()
	{
		return this.quotedPartNumber;
	}

	public void setQuotedPartNumber(String quotedPartNumber)
	{
		this.quotedPartNumber = quotedPartNumber;
	}


	public String getSupplierQuoteNumber()
	{
		return this.supplierQuoteNumber;
	}

	public void setSupplierQuoteNumber(String supplierQuoteNumber)
	{
		this.supplierQuoteNumber = supplierQuoteNumber;
	}


	public Integer getBuQty()
	{
		return this.buQty;
	}

	public void setBuQty(Integer buQty)
	{
		this.buQty = buQty;
	}


	public String getBizUnit()
	{
		return this.bizUnit;
	}

	public void setBizUnit(String bizUnit)
	{
		this.bizUnit = bizUnit;
	}

}
