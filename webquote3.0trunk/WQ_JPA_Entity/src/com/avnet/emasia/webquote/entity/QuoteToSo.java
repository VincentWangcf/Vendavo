package com.avnet.emasia.webquote.entity;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.*;

/**
 * The persistent class for the POS database table.
 * 
 */
@Entity
@Table(name="QUOTE_TO_SO")
public class QuoteToSo implements Serializable {
	private static final long serialVersionUID = 1L;

	@EmbeddedId
	private QuoteToSoPK id;
	

	@Column(name="STATUS")
	private Boolean status;

	public QuoteToSo() {
	}

	public QuoteToSoPK getId() {
		return id;
	}

	public void setId(QuoteToSoPK id) {
		this.id = id;
	}

	public Boolean getStatus() {
		return status;
	}

	public void setStatus(Boolean status) {
		this.status = status;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}
	
	public static QuoteToSo create(long id, Date date) {
		QuoteToSo toSo = new QuoteToSo();
		QuoteToSoPK toSoPK = new QuoteToSoPK();
		toSoPK.setQuoteItemId(String.valueOf(id));
		toSoPK.setUpdateDate(date);
		toSo.setId(toSoPK);
		toSo.setStatus(true);
		return toSo;
	}
}