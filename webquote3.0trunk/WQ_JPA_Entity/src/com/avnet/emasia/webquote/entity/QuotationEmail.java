package com.avnet.emasia.webquote.entity;

import java.io.Serializable;
import javax.persistence.*;

import java.util.Date;


/**
 * The persistent class for the POS database table.
 * 
 */
@Entity
@Table(name="QUOTATION_EMAIL")
public class QuotationEmail implements Serializable {
	private static final long serialVersionUID = 1L;
	private static final String SEPARATOR ="|";
	@EmbeddedId
	private QuotationEmailPK id;
	

	@Column(name="STATUS")
	private Boolean status;

	@Column(name="TO_EMPLOYEE_ID")
	private String toEmployeeId;

	@Column(name="QUOTE_ITEM_ID")
	private String quoteItemId;

	@Column(name="REMARK")
	private String remark;

	@Column(name="CC_EMPLOYEE_ID")
	private String ccEmployeeId;

	@Column(name="TO_EMPLOYEE_NAME")
	private String toEmployeeName;

	@Column(name="SUBJECT")
	private String subject;

	public QuotationEmail() {
	}

	public QuotationEmailPK getId() {
		return id;
	}

	public void setId(QuotationEmailPK id) {
		this.id = id;
	}

	public Boolean getStatus() {
		return status;
	}

	public void setStatus(Boolean status) {
		this.status = status;
	}

	public String getToEmployeeId() {
		return toEmployeeId;
	}

	public void setToEmployeeId(String toEmployeeId) {
		this.toEmployeeId = toEmployeeId;
	}

	public String getQuoteItemId() {
		return quoteItemId;
	}

	public void setQuoteItemId(String quoteItemId) {
		this.quoteItemId = quoteItemId;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public String getCcEmployeeId() {
		return ccEmployeeId;
	}

	public void setCcEmployeeId(String ccEmployeeId) {
		this.ccEmployeeId = ccEmployeeId;
	}

	public String getToEmployeeName() {
		return toEmployeeName;
	}

	public void setToEmployeeName(String toEmployeeName) {
		this.toEmployeeName = toEmployeeName;
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((ccEmployeeId == null) ? 0 : ccEmployeeId.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result
				+ ((quoteItemId == null) ? 0 : quoteItemId.hashCode());
		result = prime * result + ((remark == null) ? 0 : remark.hashCode());
		result = prime * result + ((status == null) ? 0 : status.hashCode());
		result = prime * result + ((subject == null) ? 0 : subject.hashCode());
		result = prime * result
				+ ((toEmployeeId == null) ? 0 : toEmployeeId.hashCode());
		result = prime * result
				+ ((toEmployeeName == null) ? 0 : toEmployeeName.hashCode());
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
		QuotationEmail other = (QuotationEmail) obj;
		if (ccEmployeeId == null) {
			if (other.ccEmployeeId != null)
				return false;
		} else if (!ccEmployeeId.equals(other.ccEmployeeId))
			return false;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (quoteItemId == null) {
			if (other.quoteItemId != null)
				return false;
		} else if (!quoteItemId.equals(other.quoteItemId))
			return false;
		if (remark == null) {
			if (other.remark != null)
				return false;
		} else if (!remark.equals(other.remark))
			return false;
		if (status == null) {
			if (other.status != null)
				return false;
		} else if (!status.equals(other.status))
			return false;
		if (subject == null) {
			if (other.subject != null)
				return false;
		} else if (!subject.equals(other.subject))
			return false;
		if (toEmployeeId == null) {
			if (other.toEmployeeId != null)
				return false;
		} else if (!toEmployeeId.equals(other.toEmployeeId))
			return false;
		if (toEmployeeName == null) {
			if (other.toEmployeeName != null)
				return false;
		} else if (!toEmployeeName.equals(other.toEmployeeName))
			return false;
		return true;
	}

	
}