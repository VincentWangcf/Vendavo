package com.avnet.emasia.webquote.entity;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.*;

/**
 * The primary key class for the POS database table.
 * 
 */
@Embeddable
public class QuotationEmailPK implements Serializable {
	//default serial version id, required for serializable classes.
	private static final long serialVersionUID = 1L;

	@Temporal(TemporalType.DATE)
	@Column(name="UPDATE_DATE")
	private Date updateDate;

	@Column(name="FROM_EMPLOYEE_ID")
	private String fromEmployeeId;

	@Column(name="FORM_ID")
	private String formId;
	
	public QuotationEmailPK() {
	}

	public Date getUpdateDate() {
		return updateDate;
	}

	public void setUpdateDate(Date updateDate) {
		this.updateDate = updateDate;
	}

	public String getFromEmployeeId() {
		return fromEmployeeId;
	}

	public void setFromEmployeeId(String fromEmployeeId) {
		this.fromEmployeeId = fromEmployeeId;
	}

	public String getFormId() {
		return formId;
	}

	public void setFormId(String formId) {
		this.formId = formId;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((formId == null) ? 0 : formId.hashCode());
		result = prime * result
				+ ((fromEmployeeId == null) ? 0 : fromEmployeeId.hashCode());
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
		QuotationEmailPK other = (QuotationEmailPK) obj;
		if (formId == null) {
			if (other.formId != null)
				return false;
		} else if (!formId.equals(other.formId))
			return false;
		if (fromEmployeeId == null) {
			if (other.fromEmployeeId != null)
				return false;
		} else if (!fromEmployeeId.equals(other.fromEmployeeId))
			return false;
		if (updateDate == null) {
			if (other.updateDate != null)
				return false;
		} else if (!updateDate.equals(other.updateDate))
			return false;
		return true;
	}

	
}