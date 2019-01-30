package com.avnet.emasia.webquote.entity;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Entity
@Table(name="PRICER_UPLOAD_SUMMARY")
public class PricerUploadSummary  implements Serializable{

	private static final long serialVersionUID = 6111844982386031294L;

	@Id
	@SequenceGenerator(name="PRICER_SUMMARYE_ID_GENERATOR", sequenceName="WQ_SEQ",allocationSize=1)
	@GeneratedValue(strategy=GenerationType.SEQUENCE, generator="PRICER_SUMMARYE_ID_GENERATOR")
	@Column(name="ID", unique=true, nullable=false)
	private long id;
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="CREATED_ON", nullable=false)
	private Date createdOn;
	
	@Column(name="CONTENT", length=500)
	private String content;
	@Column(name="EMPLOYEE_ID", length=10)
	private String employeeId;

	@Column(name="FILE_NAME", length=200)
	private String fileName;
	
	@Column(name="BIZ_UNIT")
	private String bizUnit;
	
	public String getBizUnit() {
		return bizUnit;
	}

	public void setBizUnit(String bizUnit) {
		this.bizUnit = bizUnit;
	}

	public String getEmployeeId() {
		return employeeId;
	}

	public void setEmployeeId(String employeeId) {
		this.employeeId = employeeId;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public Date getCreatedOn() {
		return createdOn;
	}

	public void setCreatedOn(Date createdOn) {
		this.createdOn = createdOn;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}
}
