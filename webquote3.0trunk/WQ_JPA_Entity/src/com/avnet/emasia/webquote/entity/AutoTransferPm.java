package com.avnet.emasia.webquote.entity;

import java.io.Serializable;

import javax.persistence.*;




/**
 * The persistent class for the AUTO_TRANSFER_PM database table.
 * 
 */
@Entity
@Table(name="AUTO_TRANSFER_PM")
public class AutoTransferPm implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@SequenceGenerator(name="AUTO_TRANSFER_PM_ID_GENERATOR", sequenceName="WQ_SEQ",allocationSize=1)
	@GeneratedValue(strategy=GenerationType.SEQUENCE, generator="AUTO_TRANSFER_PM_ID_GENERATOR")
	@Column(unique=true, nullable=false, precision=15)
	private long id;

	@Column(name="MANUFACTURER", length=10)
	private String manufacturer;
		
	@Column(name="FULL_MFR_PART_NUMBER", length=40)
	private String fullMfrPartNumber;
	
	@Column(name="SOLD_TO_CUSTOMER_NUMBER", length=10)
	private String soldToCustomerNumber;	
	
	@Column(name="PM_EMPLOYEE_NUMBEr", length=10)
	private String pmEmployeeNumber;

	@Column(name="BIZ_UNIT", length=10)
	private String bizUnit;
	
	
	public AutoTransferPm() {
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getManufacturer() {
		return manufacturer;
	}

	public void setManufacturer(String manufacturer) {
		this.manufacturer = manufacturer;
	}

	public String getFullMfrPartNumber() {
		return fullMfrPartNumber;
	}

	public void setFullMfrPartNumber(String fullMfrPartNumber) {
		this.fullMfrPartNumber = fullMfrPartNumber;
	}

	public String getSoldToCustomerNumber() {
		return soldToCustomerNumber;
	}

	public void setSoldToCustomerNumber(String soldToCustomerNumber) {
		this.soldToCustomerNumber = soldToCustomerNumber;
	}

	public String getPmEmployeeNumber() {
		return pmEmployeeNumber;
	}

	public void setPmEmployeeNumber(String pmEmployeeNumber) {
		this.pmEmployeeNumber = pmEmployeeNumber;
	}


	public String getBizUnit()
	{
		return bizUnit;
	}

	public void setBizUnit(String bizUnit)
	{
		this.bizUnit = bizUnit;
	}

}