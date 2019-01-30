package com.avnet.emasia.webquote.entity;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.*;


/**
 * The persistent class for the OFFLINE_REPORT database table.
 * 
 */
@Entity
@Table(name="OFFLINE_REPORT")
public class OfflineReport implements Serializable {

	

	/**
	 * 
	 */
	private static final long serialVersionUID = 7854624691416767706L;


	@Id
	@SequenceGenerator(name="OFFLINE_REPORT_ID_GENERATOR", sequenceName="WQ_SEQ",allocationSize=1)
	@GeneratedValue(strategy=GenerationType.SEQUENCE, generator="OFFLINE_REPORT_ID_GENERATOR")
	@Column(name="ID", unique=true, nullable=false)
	private long id;
	
	
	@Column(name="SERVICE_BEAN_CLASS",length=200)
	private String serviceBeanClass;

	
	@Column(name="SERVICE_BEAN_ID", length=200)
	private String serviceBeanId;

	@Column(name="SERVICE_BEAN_METHOD", length=50)
	private String serviceBeanMethod;
	

	@Column(name="SEARCH_BEAN_VALUE", length=20)
	private byte[] searchBeanValue;
	
	
	@Column(name="SEARCH_BEAN_CLASS", length=200)
	private String searchBeanClass;
	
	@Column(name="SEND_FLAG", length=1)
	private Boolean sendFlag;
	
	
	@Column(name="EMPLOYEE_ID", length=6)
	private String employeeId;
	
	@Column(name="EMPLOYEE_NAME", length=50)
	private String employeeName;
	
	@Column(name="TYPE", length=10)
	private String type;
	

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="CREATED_ON", nullable=false)
	private Date createdOn;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="LAST_UPDATED_ON")
	private Date lastUpdatedOn;
	
	@Column(name="BIZ_UNIT")
	private String bizUnit;
	

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getServiceBeanClass() {
		return serviceBeanClass;
	}

	public void setServiceBeanClass(String serviceBeanClass) {
		this.serviceBeanClass = serviceBeanClass;
	}

	public String getServiceBeanId() {
		return serviceBeanId;
	}

	public void setServiceBeanId(String serviceBeanId) {
		this.serviceBeanId = serviceBeanId;
	}

	public String getServiceBeanMethod() {
		return serviceBeanMethod;
	}

	public void setServiceBeanMethod(String serviceBeanMethod) {
		this.serviceBeanMethod = serviceBeanMethod;
	}

	public  byte[]  getSearchBeanValue() {
		return searchBeanValue;
	}

	public void setSearchBeanValue( byte[]  searchBeanValue) {
		this.searchBeanValue = searchBeanValue;
	}

	public String getSearchBeanClass() {
		return searchBeanClass;
	}

	public void setSearchBeanClass(String searchBeanClass) {
		this.searchBeanClass = searchBeanClass;
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

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
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

	@Override
	public String toString() {
		return "OfflineReport [serviceBeanId=" + serviceBeanId + ", sendFlag="
				+ sendFlag + ", employeeId=" + employeeId + ", employeeName="
				+ employeeName + ", type=" + type + ", createdOn=" + createdOn
				+ ", lastUpdatedOn=" + lastUpdatedOn + ", bizUnit=" + bizUnit
				+ "]";
	}

	
	

}