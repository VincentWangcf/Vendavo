package com.avnet.emasia.webquote.entity;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.*;

/**
 * The primary key class for the POS database table.
 * 
 */
@Embeddable
public class QuoteToSoPK implements Serializable {
	//default serial version id, required for serializable classes.
	private static final long serialVersionUID = 1L;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="UPDATE_DATE")
	private Date updateDate;

	@Column(name="QUOTE_ITEM_ID")
	private String quoteItemId;
	
	public QuoteToSoPK() {
	}

	public Date getUpdateDate() {
		return updateDate;
	}

	public void setUpdateDate(Date updateDate) {
		this.updateDate = updateDate;
	}

	public String getQuoteItemId() {
		return quoteItemId;
	}

	public void setQuoteItemId(String quoteItemId) {
		this.quoteItemId = quoteItemId;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((quoteItemId == null) ? 0 : quoteItemId.hashCode());
		result = prime * result
				+ ((updateDate == null) ? 0 : updateDate.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		QuoteToSoPK other = (QuoteToSoPK) obj;
		if (quoteItemId == null) {
			if (other.quoteItemId != null)
				return false;
		} else if (!quoteItemId.equals(other.quoteItemId))
			return false;
		if (updateDate == null) {
			if (other.updateDate != null)
				return false;
		} else if (!updateDate.equals(other.updateDate))
			return false;
		return true;
	}


	
}