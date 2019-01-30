package com.avnet.emasia.webquote.entity;

import java.io.Serializable;
import javax.persistence.*;

/**
 * The primary key class for the DR_NEDA_ITEM database table.
 * 
 */
@Embeddable
public class DrNedaItemPK implements Serializable {
	//default serial version id, required for serializable classes.
	private static final long serialVersionUID = 1L;

	@Column(name="PROJECT_ID", unique=true, nullable=false, precision=7)
	private long projectId;

	@Column(name="NEDA_ID", unique=true, nullable=false, precision=3)
	private long nedaId;

	@Column(name="NEDA_LINE_NUMBER", unique=true, nullable=false, precision=3)
	private long nedaLineNumber;

	public DrNedaItemPK() {
	}
	public long getProjectId() {
		return this.projectId;
	}
	public void setProjectId(long projectId) {
		this.projectId = projectId;
	}
	public long getNedaId() {
		return this.nedaId;
	}
	public void setNedaId(long nedaId) {
		this.nedaId = nedaId;
	}
	public long getNedaLineNumber() {
		return this.nedaLineNumber;
	}
	public void setNedaLineNumber(long nedaLineNumber) {
		this.nedaLineNumber = nedaLineNumber;
	}

	public boolean equals(Object other) {
		if (this == other) {
			return true;
		}
		if (!(other instanceof DrNedaItemPK)) {
			return false;
		}
		DrNedaItemPK castOther = (DrNedaItemPK)other;
		return 
			(this.projectId == castOther.projectId)
			&& (this.nedaId == castOther.nedaId)
			&& (this.nedaLineNumber == castOther.nedaLineNumber);
	}

	public int hashCode() {
		final int prime = 31;
		int hash = 17;
		hash = hash * prime + ((int) (this.projectId ^ (this.projectId >>> 32)));
		hash = hash * prime + ((int) (this.nedaId ^ (this.nedaId >>> 32)));
		hash = hash * prime + ((int) (this.nedaLineNumber ^ (this.nedaLineNumber >>> 32)));
		
		return hash;
	}
}