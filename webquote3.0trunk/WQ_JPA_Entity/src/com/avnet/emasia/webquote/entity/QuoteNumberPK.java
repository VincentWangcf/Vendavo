package com.avnet.emasia.webquote.entity;

import java.io.Serializable;
import javax.persistence.*;

/**
 * The primary key class for the QUOTE_NUMBER database table.
 * 
 */
@Embeddable
public class QuoteNumberPK implements Serializable {
	//default serial version id, required for serializable classes.
	private static final long serialVersionUID = 1L;

	@Column(name="DOC_TYPE", unique=true, nullable=false, length=3)
	private String docType;

	@Column(name="BIZ_UNIT", unique=true, nullable=false, length=10)
	private String bizUnit;

	public QuoteNumberPK() {
	}
	public String getDocType() {
		return this.docType;
	}
	public void setDocType(String docType) {
		this.docType = docType;
	}
	public String getBizUnit() {
		return this.bizUnit;
	}
	public void setBizUnit(String bizUnit) {
		this.bizUnit = bizUnit;
	}

	public boolean equals(Object other) {
		if (this == other) {
			return true;
		}
		if (!(other instanceof QuoteNumberPK)) {
			return false;
		}
		QuoteNumberPK castOther = (QuoteNumberPK)other;
		return 
			this.docType.equals(castOther.docType)
			&& this.bizUnit.equals(castOther.bizUnit);
	}

	public int hashCode() {
		final int prime = 31;
		int hash = 17;
		hash = hash * prime + this.docType.hashCode();
		hash = hash * prime + this.bizUnit.hashCode();
		
		return hash;
	}
}