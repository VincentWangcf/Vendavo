package com.avnet.emasia.webquote.quote.vo;

import java.io.Serializable;

public class RestrictedCustomerMappingSearchCriteria  implements Serializable {

	private static final long serialVersionUID = -9079969053053600924L;
	private String mfrKeyword;
	private String productGroup1Keyword;
	private String productGroup2Keyword;
	private String productGroup3Keyword;
	private String productGroup4Keyword;
	private String partNumberKeyword;
	private String costIndicatorKeyword;
	private String endCustomerCodeKeyword;
	
	public String getEndCustomerCodeKeyword() {
		return endCustomerCodeKeyword;
	}

	public void setEndCustomerCodeKeyword(String endCustomerCodeKeyword) {
		this.endCustomerCodeKeyword = endCustomerCodeKeyword;
	}

	private String soldToCodeKeyword;
	private String bizUnit;
	
	public String getMfrKeyword() {
		return mfrKeyword;
	}

	public void setMfrKeyword(String mfrKeyword) {
		this.mfrKeyword = mfrKeyword;
	}

	public String getProductGroup1Keyword() {
		return productGroup1Keyword;
	}

	public void setProductGroup1Keyword(String productGroup1Keyword) {
		this.productGroup1Keyword = productGroup1Keyword;
	}

	public String getProductGroup2Keyword() {
		return productGroup2Keyword;
	}

	public void setProductGroup2Keyword(String productGroup2Keyword) {
		this.productGroup2Keyword = productGroup2Keyword;
	}

	public String getProductGroup3Keyword() {
		return productGroup3Keyword;
	}

	public void setProductGroup3Keyword(String productGroup3Keyword) {
		this.productGroup3Keyword = productGroup3Keyword;
	}

	public String getProductGroup4Keyword() {
		return productGroup4Keyword;
	}

	public void setProductGroup4Keyword(String productGroup4Keyword) {
		this.productGroup4Keyword = productGroup4Keyword;
	}

	public String getPartNumberKeyword() {
		return partNumberKeyword;
	}

	public void setPartNumberKeyword(String partNumberKeyword) {
		this.partNumberKeyword = partNumberKeyword;
	}

	public String getCostIndicatorKeyword() {
		return costIndicatorKeyword;
	}

	public void setCostIndicatorKeyword(String costIndicatorKeyword) {
		this.costIndicatorKeyword = costIndicatorKeyword;
	}

	public String getSoldToCodeKeyword() {
		return soldToCodeKeyword;
	}

	public void setSoldToCodeKeyword(String soldToCodeKeyword) {
		this.soldToCodeKeyword = soldToCodeKeyword;
	}

	public String getBizUnit() {
		return bizUnit;
	}

	public void setBizUnit(String bizUnit) {
		this.bizUnit = bizUnit;
	}
}