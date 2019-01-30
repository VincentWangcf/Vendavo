package com.avnet.emasia.webquote.commodity.bean;

import java.io.Serializable;
import java.util.Date;

public class VerifyEffectiveDateResult extends QedCheckBean  implements Serializable{

	private static final long serialVersionUID = 7303873088706870832L;
	
	private String pricerType;
	
	private long materialId;
	private long materialDetailId;
	private String priceValidity;
	
	private String costIndicator;
	
	private String fullMfrPartNumber;
	
	private String bizUnit;
	
	private String mfr;
	
	private String soldToCode;
	
	private String programType;

	private String currency;
	
	public String getPricerType() {
		return pricerType;
	}

	public void setPricerType(String pricerType) {
		this.pricerType = pricerType;
	}

	public long getMaterialId() {
		return materialId;
	}

	public void setMaterialId(long materialId) {
		this.materialId = materialId;
	}

	public long getMaterialDetailId() {
		return materialDetailId;
	}

	public void setMaterialDetailId(long materialDetailId) {
		this.materialDetailId = materialDetailId;
	}

	public String getPriceValidity() {
		return priceValidity;
	}

	public void setPriceValidity(String priceValidity) {
		this.priceValidity = priceValidity;
	}

	public String getCostIndicator() {
		return costIndicator;
	}

	public void setCostIndicator(String costIndicator) {
		this.costIndicator = costIndicator;
	}

	public String getFullMfrPartNumber() {
		return fullMfrPartNumber;
	}

	public void setFullMfrPartNumber(String fullMfrPartNumber) {
		this.fullMfrPartNumber = fullMfrPartNumber;
	}

	public String getBizUnit() {
		return bizUnit;
	}

	public void setBizUnit(String bizUnit) {
		this.bizUnit = bizUnit;
	}

	public String getMfr() {
		return mfr;
	}

	public void setMfr(String mfr) {
		this.mfr = mfr;
	}

	public String getSoldToCode() {
		return soldToCode;
	}

	public void setSoldToCode(String soldToCode) {
		this.soldToCode = soldToCode;
	}

	public String getProgramType() {
		return programType;
	}

	public void setProgramType(String programType) {
		this.programType = programType;
	}

	public String getCurrency() {
		return currency;
	}

	public void setCurrency(String currency) {
		this.currency = currency;
	}
}
