package com.avnet.emasia.webquote.entity;

import java.io.Serializable;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import java.util.Date;


/**
 * The persistent class for the AUDIT_TRAIL database table.
 * 
 */
@Entity
@Table(name="AUDIT_TRAIL")
public class AuditTrail implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@SequenceGenerator(name="AUDIT_TRAIL_ID_GENERATOR", sequenceName="WQ_SEQ",allocationSize=1)
	@GeneratedValue(strategy=GenerationType.SEQUENCE, generator="AUDIT_TRAIL_ID_GENERATOR")
	@Column(unique=true, nullable=false, precision=19)
	private long id;

	@Column(name="\"ACTION\"", length=50)
	private String action;

	@Column(name="CREATED_BY", length=20)
	private String createdBy;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="CREATED_ON")
	private Date createdOn;

	@Column(length=100)
	private String description;

	@Column(name="EMPLOYEE_NAME", length=30)
	private String employeeName;

	@Column(length=255)
	private String region;

	@Column(name="TARGET_CLASS", length=50)
	private String targetClass;

	@Column(name="TARGET_ID", length=50)
	private String targetId;

	//bi-directional many-to-one association to AuditTrailRecord
	@OneToMany(cascade=CascadeType.ALL,fetch=FetchType.EAGER, mappedBy="auditTrail")
	private List<AuditTrailRecord> auditTrailRecords = new ArrayList<AuditTrailRecord>();
	
	public AuditTrail() {
	}

	public long getId() {
		return this.id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getAction() {
		return this.action;
	}

	public void setAction(String action) {
		this.action = action;
	}

	public String getCreatedBy() {
		return this.createdBy;
	}

	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}

	public Date getCreatedOn() {
		return this.createdOn;
	}

	public void setCreatedOn(Date createdOn) {
		this.createdOn = createdOn;
	}

	public String getDescription() {
		return this.description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getEmployeeName() {
		return this.employeeName;
	}

	public void setEmployeeName(String employeeName) {
		this.employeeName = employeeName;
	}

	public String getRegion() {
		return this.region;
	}

	public void setRegion(String region) {
		this.region = region;
	}

	public String getTargetClass() {
		return this.targetClass;
	}

	public void setTargetClass(String targetClass) {
		this.targetClass = targetClass;
	}

	public String getTargetId() {
		return this.targetId;
	}

	public void setTargetId(String targetId) {
		this.targetId = targetId;
	}
	
	public List<AuditTrailRecord> getAuditTrailRecords() {

		return this.auditTrailRecords;
	}

	public void setAuditTrailRecords(List<AuditTrailRecord> auditTrailRecords) {
		this.auditTrailRecords = auditTrailRecords;
	}

}