package com.avnet.emasia.webquote.entity;

import java.io.Serializable;
import javax.persistence.*;

/**
 * The primary key class for the FREE_STOCK database table.
 * 
 */
@Embeddable
public class FreeStockPK implements Serializable {
	//default serial version id, required for serializable classes.
	private static final long serialVersionUID = 1L;

	@Column(name="COMPANY_CODE", unique=true, nullable=false, length=4)
	private String companyCode;

	@Column(unique=true, nullable=false, length=4)
	private String plant;

	@Column(name="MATERIAL_PREFIX", unique=true, nullable=false, length=3)
	private String materialPrefix;

	@Column(name="SAP_PART_NUMBER", unique=true, nullable=false, length=18)
	private String sapPartNumber;

	public FreeStockPK() {
	}
	public String getCompanyCode() {
		return this.companyCode;
	}
	public void setCompanyCode(String companyCode) {
		this.companyCode = companyCode;
	}
	public String getPlant() {
		return this.plant;
	}
	public void setPlant(String plant) {
		this.plant = plant;
	}
	public String getMaterialPrefix() {
		return this.materialPrefix;
	}
	public void setMaterialPrefix(String materialPrefix) {
		this.materialPrefix = materialPrefix;
	}
	public String getSapPartNumber() {
		return this.sapPartNumber;
	}
	public void setSapPartNumber(String sapPartNumber) {
		this.sapPartNumber = sapPartNumber;
	}

	public boolean equals(Object other) {
		if (this == other) {
			return true;
		}
		if (!(other instanceof FreeStockPK)) {
			return false;
		}
		FreeStockPK castOther = (FreeStockPK)other;
		return 
			this.companyCode.equals(castOther.companyCode)
			&& this.plant.equals(castOther.plant)
			&& this.materialPrefix.equals(castOther.materialPrefix)
			&& this.sapPartNumber.equals(castOther.sapPartNumber);
	}

	public int hashCode() {
		final int prime = 31;
		int hash = 17;
		hash = hash * prime + this.companyCode.hashCode();
		hash = hash * prime + this.plant.hashCode();
		hash = hash * prime + this.materialPrefix.hashCode();
		hash = hash * prime + this.sapPartNumber.hashCode();
		
		return hash;
	}
}