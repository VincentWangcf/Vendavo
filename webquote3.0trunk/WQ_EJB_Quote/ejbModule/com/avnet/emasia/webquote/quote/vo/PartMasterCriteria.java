package com.avnet.emasia.webquote.quote.vo;

import java.io.Serializable;


public class PartMasterCriteria implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -7598698506517901517L;
	public PartMasterCriteria() {
		// TODO Auto-generated constructor stub
	}
    
	private String mfr ; 
	private String partNumber;
	private String region;
	private String salesCostPart;
	private String productGroup1;
	private String productGroup2;
	private String productGroup3;
	private String productGroup4;
	
	
	private int start;
	private int end;
	
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
	
	public String getRegion() {
		return region;
	}
	public void setRegion(String region) {
		this.region = region;
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
	public String getSalesCostPart() {
		return salesCostPart;
	}
	public void setSalesCostPart(String salesCostPart) {
		this.salesCostPart = salesCostPart;
	}
	public int getStart() {
		return start;
	}
	public void setStart(int start) {
		this.start = start;
	}
	public int getEnd() {
		return end;
	}
	public void setEnd(int end) {
		this.end = end;
	}
	
}
