package com.avnet.emasia.webquote.entity;

import java.io.Serializable;
import javax.persistence.*;

@Entity
@Table(name="MANUFACTURER_BIZUNIT_MAPPING")
@IdClass(value=ManufacturerMappingPK.class)
public class ManufacturerMapping implements Serializable {

	private static final long serialVersionUID = 1L;
	
	//consider to remove below primitive type to entity in later phase;and remove the "insertable=false, updatable=false" on manufacturer and bizUnit; 
	@Id
	@Column(name="MANUFACTURER_ID")
    private long manufacturerID;
    @Id
    @Column(name="BIZ_UNIT_ID")
    private String bizUnitID;
//	@Column(name="FACTOR", precision=15, scale=5)
//	private Integer factor;
//	@Column(name="FLOOR", precision=15, scale=5)
//	private Integer floor;
//	@Column(name="CAP", precision=15, scale=5)
//	private Integer cap;
	
    @JoinColumn(name="MANUFACTURER_ID", insertable=false, updatable=false)
    @ManyToOne
    private Manufacturer manufacturer;
    
    @JoinColumn(name="BIZ_UNIT_ID", insertable=false, updatable=false)
    @ManyToOne
    private BizUnit bizUnit;
    
    
	@Column(name="PRICER_FLAG")
	private Boolean pricer;
	
	public long getManufacturerID() {
		return manufacturerID;
	}
	public void setManufacturerID(long manufacturerID) {
		this.manufacturerID = manufacturerID;
	}
	public String getBizUnitID() {
		return bizUnitID;
	}
	public void setBizUnitID(String bizUnitID) {
		this.bizUnitID = bizUnitID;
	}
//	public Integer getFactor() {
//		return factor;
//	}
//	public void setFactor(Integer factor) {
//		this.factor = factor;
//	}
//	public Integer getFloor() {
//		return floor;
//	}
//	public void setFloor(Integer floor) {
//		this.floor = floor;
//	}
//	public Integer getCap() {
//		return cap;
//	}
//	public void setCap(Integer cap) {
//		this.cap = cap;
//	}
	public Boolean getPricer()
	{
		return pricer;
	}
	public void setPricer(Boolean pricer)
	{
		this.pricer = pricer;
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
	
	
	
}