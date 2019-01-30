package com.avnet.emasia.webquote.entity;

import java.io.Serializable;
import javax.persistence.*;

/**
 * The primary key class for the POS database table.
 * 
 */
@Embeddable
public class PosPK implements Serializable {
	//default serial version id, required for serializable classes.
	private static final long serialVersionUID = 1L;

	@Column(name="INVOICE_NUMBER", unique=true, nullable=false, length=10)
	private String invoiceNumber;

	@Column(name="INVOICE_ITEM_NUMBER", unique=true, nullable=false, precision=6)
	private long invoiceItemNumber;

	public PosPK() {
	}
	public String getInvoiceNumber() {
		return this.invoiceNumber;
	}
	public void setInvoiceNumber(String invoiceNumber) {
		this.invoiceNumber = invoiceNumber;
	}
	public long getInvoiceItemNumber() {
		return this.invoiceItemNumber;
	}
	public void setInvoiceItemNumber(long invoiceItemNumber) {
		this.invoiceItemNumber = invoiceItemNumber;
	}

	public boolean equals(Object other) {
		if (this == other) {
			return true;
		}
		if (!(other instanceof PosPK)) {
			return false;
		}
		PosPK castOther = (PosPK)other;
		return 
			this.invoiceNumber.equals(castOther.invoiceNumber)
			&& (this.invoiceItemNumber == castOther.invoiceItemNumber);
	}

	public int hashCode() {
		final int prime = 31;
		int hash = 17;
		hash = hash * prime + this.invoiceNumber.hashCode();
		hash = hash * prime + ((int) (this.invoiceItemNumber ^ (this.invoiceItemNumber >>> 32)));
		
		return hash;
	}
}