package com.avnet.emasia.webquote.entity;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import javax.persistence.*;

/**
 * The primary key class for the AUDIT_QUOTE_ITEM database table.
 * 
 */
@Embeddable
public class QuoteItemDesignHisPK implements Serializable {
	//default serial version id, required for serializable classes.
	private static final long serialVersionUID = 1L;

	@Column(name="QUOTE_ITEM_ID", unique=true, nullable=false)
	private long quoteItemId;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="START_DATE", unique=true, nullable=false)
	private Date startDate;

	public QuoteItemDesignHisPK() {
	}


	public long getQuoteItemId() {
		return quoteItemId;
	}

	public void setQuoteItemId(long quoteItemId) {
		this.quoteItemId = quoteItemId;
	}

	public Date getStartDate() {
		return startDate;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (int) (quoteItemId ^ (quoteItemId >>> 32));
		result = prime * result
				+ ((startDate == null) ? 0 : startDate.hashCode());
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
		QuoteItemDesignHisPK other = (QuoteItemDesignHisPK) obj;
		if (quoteItemId != other.quoteItemId)
			return false;
		if (startDate == null) {
			if (other.startDate != null)
				return false;
		} else if (!startDate.equals(other.startDate))
			return false;
		return true;
	}


	
	
	
}