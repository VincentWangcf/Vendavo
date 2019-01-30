package com.avnet.emasia.webquote.entity;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.*;


/**
 * The persistent class for the OFFLINE_REPORT database table.
 * 
 */
@Entity
@Table(name="PRICER_OFFLINE")
public class PricerOffline implements Serializable {

	private static final long serialVersionUID = 8277529715693013755L;

	@Id
	@SequenceGenerator(name="PRICER_OFFLINE_ID_GENERATOR", sequenceName="WQ_SEQ",allocationSize=1)
	@GeneratedValue(strategy=GenerationType.SEQUENCE, generator="PRICER_OFFLINE_ID_GENERATOR")
	@Column(name="ID", unique=true, nullable=false)
	private long id;
	
	@Column(name="action", length=100)
	private String action;
	
	@Column(name="EMPLOYEE_ID", length=10)
	private String employeeId;
	
	@Column(name="EMPLOYEE_NAME", length=50)
	private String employeeName;
	@Column(name="FILE_NAME", length=200)
	private String fileName;

	@Column(name="PRICER_TYPE", length=20)
	private String pricerType;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="CREATED_ON", nullable=false)
	private Date createdOn;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="LAST_UPDATED_ON")
	private Date lastUpdatedOn;
	
	@Column(name="BIZ_UNIT")
	private String bizUnit;

	@Column(name="SEND_FLAG", length=1)
	private Boolean sendFlag;
	
	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public Boolean getSendFlag() {
		return sendFlag;
	}

	public void setSendFlag(Boolean sendFlag) {
		this.sendFlag = sendFlag;
	}

	public String getEmployeeId() {
		return employeeId;
	}

	public void setEmployeeId(String employeeId) {
		this.employeeId = employeeId;
	}

	public String getEmployeeName() {
		return employeeName;
	}

	public void setEmployeeName(String employeeName) {
		this.employeeName = employeeName;
	}

	public Date getCreatedOn() {
		return createdOn;
	}

	public void setCreatedOn(Date createdOn) {
		this.createdOn = createdOn;
	}

	public Date getLastUpdatedOn() {
		return lastUpdatedOn;
	}

	public void setLastUpdatedOn(Date lastUpdatedOn) {
		this.lastUpdatedOn = lastUpdatedOn;
	}

	public String getBizUnit() {
		return bizUnit;
	}

	public void setBizUnit(String bizUnit) {
		this.bizUnit = bizUnit;
	}
	
	public String getAction() {
		return action;
	}

	public void setAction(String action) {
		this.action = action;
	}

	public String getPricerType() {
		return pricerType;
	}

	public void setPricerType(String pricerType) {
		this.pricerType = pricerType;
	}
	
	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	
	@Override
	public String toString() {
		return "OfflineReport [action=" + action + ", sendFlag="
				+ sendFlag + ", fileName="+fileName+",employeeId=" + employeeId + ", employeeName="
				+ employeeName + ", pricerType=" + pricerType + ", createdOn=" + createdOn
				+ ", lastUpdatedOn=" + lastUpdatedOn + ", bizUnit=" + bizUnit
				+ "]";
	}
}