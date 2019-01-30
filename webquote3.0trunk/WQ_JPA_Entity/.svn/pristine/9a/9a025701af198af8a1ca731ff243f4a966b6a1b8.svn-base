package com.avnet.emasia.webquote.entity;

import java.io.Serializable;
import javax.persistence.*;


/**
 * The persistent class for the AUDIT_TRAIL_RECORD database table.
 * 
 */
@Entity
@Table(name="AUDIT_TRAIL_RECORD")
public class AuditTrailRecord implements Serializable {
	private static final long serialVersionUID = 1L;

	
	@Id
	@Column(name="FROM_VALUE", length=500)
	private String fromValue;

	private Long id;

	@Column(name="TARGET_COLUMN", length=20)
	private String targetColumn;

	@Column(name="TO_VALUE", length=500)
	private String toValue;

	//bi-directional many-to-one association to AuditTrail
	@ManyToOne(cascade=CascadeType.ALL,fetch=FetchType.EAGER)
	@JoinColumn(name="AUDIT_TRAIL_ID")
	private AuditTrail auditTrail;


	public AuditTrailRecord() {
	}

	public String getFromValue() {
		return this.fromValue;
	}

	public void setFromValue(String fromValue) {
		this.fromValue = fromValue;
	}

	public Long getId() {
		return this.id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getTargetColumn() {
		return this.targetColumn;
	}

	public void setTargetColumn(String targetColumn) {
		this.targetColumn = targetColumn;
	}

	public String getToValue() {
		return this.toValue;
	}

	public void setToValue(String toValue) {
		this.toValue = toValue;
	}

	public AuditTrail getAuditTrail() {
		return this.auditTrail;
	}

	public void setAuditTrail(AuditTrail auditTrail) {
		this.auditTrail = auditTrail;
	}

}