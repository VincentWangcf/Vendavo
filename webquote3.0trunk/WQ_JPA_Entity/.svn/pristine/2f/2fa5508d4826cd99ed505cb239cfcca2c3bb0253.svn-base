package com.avnet.emasia.webquote.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@Embeddable
public class ManufacturerMappingPK implements Serializable {
	private static final long serialVersionUID = 1L;
	@Column(name="MANUFACTURER_ID")
	private long manufacturerID;
	@Column(name="BIZ_UNIT_ID")
	private String bizUnitID;

    public ManufacturerMappingPK() {}

    public Long getManufacturerId() {
		return manufacturerID;
	}

	public void setManufacturerId(Long manufacturerID) {
		this.manufacturerID = manufacturerID;
	}

	public String getBizUnitID() {
		return bizUnitID;
	}

	public void setBizUnit(String bizUnitID) {
		this.bizUnitID = bizUnitID;
	}
	
    @Override
    public boolean equals(Object o) {
        if(o instanceof Country){
        	ManufacturerMappingPK other = (ManufacturerMappingPK) o;
        	
        	if(!other.getManufacturerId().equals(manufacturerID)){
                return false;
            }
 
            if(!other.getBizUnitID().equals(bizUnitID)){
                return false;
            }
            
            return true;
        }

        return false;
    }

    @Override
    public int hashCode() {
    	return bizUnitID.hashCode();
    }
}