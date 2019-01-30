package com.avnet.emasia.webquote.entity;

import java.io.Serializable;
import javax.persistence.*;

/**
 * The primary key class for the SALES_ORDER database table.
 * 
 */
@Embeddable
public class SalesOrderPK implements Serializable {
	//default serial version id, required for serializable classes.
	private static final long serialVersionUID = 1L;

	@Column(name="SALES_ORDER_NUMBER", unique=true, nullable=false, length=10)
	private String salesOrderNumber;

	@Column(name="SALES_ORDER_ITEM_NUMBER", unique=true, nullable=false, precision=6)
	private long salesOrderItemNumber;

	@Column(name="QUOTE_NUMBER", unique=true, nullable=false, length=20)
	private String quoteNumber;

	public SalesOrderPK() {
	}
	public String getSalesOrderNumber() {
		return this.salesOrderNumber;
	}
	public void setSalesOrderNumber(String salesOrderNumber) {
		this.salesOrderNumber = salesOrderNumber;
	}
	public long getSalesOrderItemNumber() {
		return this.salesOrderItemNumber;
	}
	public void setSalesOrderItemNumber(long salesOrderItemNumber) {
		this.salesOrderItemNumber = salesOrderItemNumber;
	}
	public String getQuoteNumber() {
		return this.quoteNumber;
	}
	public void setQuoteNumber(String quoteNumber) {
		this.quoteNumber = quoteNumber;
	}

	public boolean equals(Object other) {
		if (this == other) {
			return true;
		}
		if (!(other instanceof SalesOrderPK)) {
			return false;
		}
		SalesOrderPK castOther = (SalesOrderPK)other;
		return 
			this.salesOrderNumber.equals(castOther.salesOrderNumber)
			&& (this.salesOrderItemNumber == castOther.salesOrderItemNumber)
			&& this.quoteNumber.equals(castOther.quoteNumber);
	}

	public int hashCode() {
		final int prime = 31;
		int hash = 17;
		hash = hash * prime + this.salesOrderNumber.hashCode();
		hash = hash * prime + ((int) (this.salesOrderItemNumber ^ (this.salesOrderItemNumber >>> 32)));
		hash = hash * prime + this.quoteNumber.hashCode();
		
		return hash;
	}
}