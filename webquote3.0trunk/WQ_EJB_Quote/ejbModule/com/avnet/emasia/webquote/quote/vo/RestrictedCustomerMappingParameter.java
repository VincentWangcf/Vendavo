package com.avnet.emasia.webquote.quote.vo;

import java.io.Serializable;

public class RestrictedCustomerMappingParameter  implements Serializable {


	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String mfr;
	private String endCustomerCode;
	private String requiredPartNumber;
	private String soldToCustomerNumber;
	private String productGroup1Name;
	private String productGroup2Name;
	private String productGroup3Name;
	private String productGroup4Name;
	
	public String getMfr() {
		return mfr;
	}
	public void setMfr(String mfr) {
		this.mfr = mfr;
	}
	public String getEndCustomerCode() {
		return endCustomerCode;
	}
	public void setEndCustomerCode(String costIndicator) {
		this.endCustomerCode = costIndicator;
	}
	public String getRequiredPartNumber() {
		return requiredPartNumber;
	}
	public void setRequiredPartNumber(String requiredPartNumber) {
		this.requiredPartNumber = requiredPartNumber;
	}
	public String getSoldToCustomerNumber() {
		return soldToCustomerNumber;
	}
	public void setSoldToCustomerNumber(String soldToCustomerNumber) {
		this.soldToCustomerNumber = soldToCustomerNumber;
	}
	
	public String getProductGroup1Name() {
		return productGroup1Name;
	}
	public void setProductGroup1Name(String productGroup1Name) {
		this.productGroup1Name = productGroup1Name;
	}
	public String getProductGroup2Name() {
		return productGroup2Name;
	}
	public void setProductGroup2Name(String productGroup2Name) {
		this.productGroup2Name = productGroup2Name;
	}
	public String getProductGroup3Name() {
		return productGroup3Name;
	}
	public void setProductGroup3Name(String productGroup3Name) {
		this.productGroup3Name = productGroup3Name;
	}
	public String getProductGroup4Name() {
		return productGroup4Name;
	}
	public void setProductGroup4Name(String productGroup4Name) {
		this.productGroup4Name = productGroup4Name;
	}
	
	private String bizUnit;
	
	
	public String getBizUnit() {
		return bizUnit;
	}
	public void setBizUnit(String bizUnit) {
		this.bizUnit = bizUnit;
	}
	public String getMandatoryKey()
	{
		StringBuffer sb = new StringBuffer();
		sb.append(getBizUnit()).append("|").append(getMfr());
		return sb.toString();
	}
}
