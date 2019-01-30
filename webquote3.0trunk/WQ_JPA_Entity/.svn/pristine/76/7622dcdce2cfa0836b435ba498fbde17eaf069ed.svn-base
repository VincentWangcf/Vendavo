package com.avnet.emasia.webquote.entity;

import java.io.Serializable;
import javax.persistence.*;

/**
 * The persistent class for the VALIDATION_RULE database table.
 * 
 */
@Entity
@Table(name="VALIDATION_RULE")
public class ValidationRule implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@SequenceGenerator(name="VALIDATION_RULE_ID_GENERATOR", sequenceName="WQ_SEQ",allocationSize=1)
	@GeneratedValue(strategy=GenerationType.SEQUENCE, generator="VALIDATION_RULE_ID_GENERATOR")
	@Column(unique=true, nullable=false, precision=18)
	private long id;

	@Column(nullable=false, precision=1)
	private Boolean application;

	@Column(name="COMPETITOR_INFORMATION", nullable=false, precision=1)
	private Boolean competitorInformation;

	@Column(name="DESIGN_LOCATION", nullable=false, precision=1)
	private Boolean designLocation;

	@Column(nullable=false, precision=1)
	private Boolean eau;

	@Column(name="END_CUSTOMER", nullable=false, precision=1)
	private Boolean endCustomer;

	@Column(name="MP_SCHEDULE", nullable=false, precision=1)
	private Boolean mpSchedule;

	@Column(name="PP_SCHEDULE", nullable=false, precision=1)
	private Boolean ppSchedule;

	@Column(nullable=false, precision=1)
	private Boolean project;

	@Column(name="SHIP_TO_CODE", nullable=false, precision=1)
	private Boolean shipToCode;

	@Column(name="SUPPLIER_DR_NO", nullable=false, precision=1)
	private Boolean supplierDrNo;

	@Column(name="TARGET_RESALE", nullable=false, precision=1)
	private Boolean targetResale;

	//uni-directional many-to-one association to Manufacturer
	@ManyToOne
	@JoinColumn(name="MANUFACTURER_ID", nullable=false)
	private Manufacturer manufacturer;

	@ManyToOne
	@JoinColumn(name="BIZ_UNIT_ID", nullable=false)
	private BizUnit bizUnit;

	
	public ValidationRule() {
	}


	public long getId() {
		return id;
	}


	public void setId(long id) {
		this.id = id;
	}


	public Boolean getApplication() {
		return application;
	}


	public void setApplication(Boolean application) {
		this.application = application;
	}


	public Boolean getCompetitorInformation() {
		return competitorInformation;
	}


	public void setCompetitorInformation(Boolean competitorInformation) {
		this.competitorInformation = competitorInformation;
	}


	public Boolean getDesignLocation() {
		return designLocation;
	}


	public void setDesignLocation(Boolean designLocation) {
		this.designLocation = designLocation;
	}


	public Boolean getEau() {
		return eau;
	}


	public void setEau(Boolean eau) {
		this.eau = eau;
	}


	public Boolean getEndCustomer() {
		return endCustomer;
	}


	public void setEndCustomer(Boolean endCustomer) {
		this.endCustomer = endCustomer;
	}


	public Boolean getMpSchedule() {
		return mpSchedule;
	}


	public void setMpSchedule(Boolean mpSchedule) {
		this.mpSchedule = mpSchedule;
	}


	public Boolean getPpSchedule() {
		return ppSchedule;
	}


	public void setPpSchedule(Boolean ppSchedule) {
		this.ppSchedule = ppSchedule;
	}


	public Boolean getProject() {
		return project;
	}


	public void setProject(Boolean project) {
		this.project = project;
	}


	public Boolean getShipToCode() {
		return shipToCode;
	}


	public void setShipToCode(Boolean shipToCode) {
		this.shipToCode = shipToCode;
	}


	public Boolean getSupplierDrNo() {
		return supplierDrNo;
	}


	public void setSupplierDrNo(Boolean supplierDrNo) {
		this.supplierDrNo = supplierDrNo;
	}


	public Boolean getTargetResale() {
		return targetResale;
	}


	public void setTargetResale(Boolean targetResale) {
		this.targetResale = targetResale;
	}


	public Manufacturer getManufacturer() {
		return manufacturer;
	}


	public void setManufacturer(Manufacturer manufacturer) {
		this.manufacturer = manufacturer;
	}


	public BizUnit getBizUnit() {
		return bizUnit;
	}


	public void setBizUnit(BizUnit bizUnit) {
		this.bizUnit = bizUnit;
	}


	public static long getSerialversionuid() {
		return serialVersionUID;
	}


}