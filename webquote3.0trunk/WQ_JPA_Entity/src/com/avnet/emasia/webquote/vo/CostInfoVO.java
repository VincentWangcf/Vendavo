package com.avnet.emasia.webquote.vo;

import java.io.Serializable;
import java.util.Date;

public class CostInfoVO implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String region;

	private String saleCostPart;

	private String mfr;

	private String partNumber;

	private Double cost;

	private String costSource;

	private String costIndicator;

	private Date priceValidity;

	private Integer mpq;

	private Integer moq;

	private String productGroup1;

	private String productGroup2;

	private String productGroup3;

	private String productGroup4;

	private String soldToCode;

	private String soldToName;

	private Integer qty;

	public String getRegion() {
		return region;
	}

	public void setRegion(String region) {
		this.region = region;
	}

	public String getSaleCostPart() {
		return saleCostPart;
	}

	public void setSaleCostPart(String saleCostPart) {
		this.saleCostPart = saleCostPart;
	}

	public String getMfr() {
		return mfr;
	}

	public void setMfr(String mfr) {
		this.mfr = mfr;
	}

	public String getPartNumber() {
		return partNumber;
	}

	public void setPartNumber(String partNumber) {
		this.partNumber = partNumber;
	}

	public Double getCost() {
		return cost;
	}

	public void setCost(Double cost) {
		this.cost = cost;
	}

	public String getCostSource() {
		return costSource;
	}

	public void setCostSource(String costSource) {
		this.costSource = costSource;
	}

	public String getCostIndicator() {
		return costIndicator;
	}

	public void setCostIndicator(String costIndicator) {
		this.costIndicator = costIndicator;
	}

	public Date getPriceValidity() {
		return priceValidity;
	}

	public void setPriceValidity(Date priceValidity) {
		this.priceValidity = priceValidity;
	}

	public Integer getMpq() {
		return mpq;
	}

	public void setMpq(Integer mpq) {
		this.mpq = mpq;
	}

	public Integer getMoq() {
		return moq;
	}

	public void setMoq(Integer moq) {
		this.moq = moq;
	}

	public String getProductGroup1() {
		return productGroup1;
	}

	public void setProductGroup1(String productGroup1) {
		this.productGroup1 = productGroup1;
	}

	public String getProductGroup2() {
		return productGroup2;
	}

	public void setProductGroup2(String productGroup2) {
		this.productGroup2 = productGroup2;
	}

	public String getProductGroup3() {
		return productGroup3;
	}

	public void setProductGroup3(String productGroup3) {
		this.productGroup3 = productGroup3;
	}

	public String getProductGroup4() {
		return productGroup4;
	}

	public void setProductGroup4(String productGroup4) {
		this.productGroup4 = productGroup4;
	}

	public String getSoldToCode() {
		return soldToCode;
	}

	public void setSoldToCode(String soldToCode) {
		this.soldToCode = soldToCode;
	}

	public String getSoldToName() {
		return soldToName;
	}

	public void setSoldToName(String soldToName) {
		this.soldToName = soldToName;
	}

	public Integer getQty() {
		return qty;
	}

	public void setQty(Integer qty) {
		this.qty = qty;
	}

}
