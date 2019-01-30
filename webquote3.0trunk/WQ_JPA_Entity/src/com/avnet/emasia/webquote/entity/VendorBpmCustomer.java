package com.avnet.emasia.webquote.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Transient;

@Entity
@Table(name="VENDOR_BPM_CUSTOMER")
public class VendorBpmCustomer implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@SequenceGenerator(name="BPM_CUSTOMER_ID_GENERATOR", sequenceName="WQ_SEQ",allocationSize=1)
	@GeneratedValue(strategy=GenerationType.SEQUENCE, generator="BPM_CUSTOMER_ID_GENERATOR")
	@Column(unique=true, nullable=false, precision=19)
	private long id;

	@Column(name="DISTRIBUTOR_CODE", length=20)
	private String distributorCode;

	@Column(name="DISTRIBUTOR_NAME", length=100)
	private String distributorName;

	@Column(name="BPM_CODE", length=20)
	private String bpmCode;

	@Column(name="SHORTNAME", length=50)
	private String shortname;

	@Column(name="BPM_NAME1", length=50)
	private String bpmName1;

	@Column(name="BPM_NAME2", length=50)
	private String bpmName2;

	@Column(name="CTRY", length=3)
	private String ctry;
	
	@Transient
	private int xlsLineSeq;

	public VendorBpmCustomer() {
	}

	public long getId() {
		return this.id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getDistributorCode() {
		return distributorCode;
	}

	public void setDistributorCode(String distributorCode) {
		this.distributorCode = distributorCode;
	}

	public String getDistributorName() {
		return distributorName;
	}

	public void setDistributorName(String distributorName) {
		this.distributorName = distributorName;
	}

	public String getBpmCode() {
		return bpmCode;
	}

	public void setBpmCode(String bpmCode) {
		this.bpmCode = bpmCode;
	}

	public String getShortname() {
		return shortname;
	}

	public void setShortname(String shortname) {
		this.shortname = shortname;
	}

	public String getBpmName1() {
		return bpmName1;
	}

	public void setBpmName1(String bpmName1) {
		this.bpmName1 = bpmName1;
	}

	public String getBpmName2() {
		return bpmName2;
	}

	public void setBpmName2(String bpmName2) {
		this.bpmName2 = bpmName2;
	}

	public String getCtry() {
		return ctry;
	}

	public void setCtry(String ctry) {
		this.ctry = ctry;
	}

	public int getXlsLineSeq() {
		return xlsLineSeq;
	}

	public void setXlsLineSeq(int xlsLineSeq) {
		this.xlsLineSeq = xlsLineSeq;
	}

}