package com.avnet.emasia.webquote.web.quote.vo;

import java.io.Serializable;
import java.util.List;

import com.avnet.emasia.webquote.web.quote.constant.QuoteConstant;

public class CustomerVO implements Serializable {

	private String customerNumber;
	private String customerType;
	private String customerName;
	private List<String> shipToCustomerList;
	private List<String> endCustmerList;
	private List<String> salesOrgList;
	private String customerNameLabel;
	private String customerNumberLabel;
	
	public String getCustomerNumber() {
		return customerNumber;
	}
	public void setCustomerNumber(String customerNumber) {
		this.customerNumber = customerNumber;
	}
	public String getCustomerType() {
		return customerType;
	}
	public void setCustomerType(String customerType) {
		this.customerType = customerType;
	}
	public String getCustomerName() {
		return customerName;
	}
	public void setCustomerName(String customerName) {
		this.customerName = customerName;
	}
	public List<String> getShipToCustomerList() {
		return shipToCustomerList;
	}
	public void setShipToCustomerList(List<String> shipToCustomerList) {
		this.shipToCustomerList = shipToCustomerList;
	}
	public List<String> getEndCustmerList() {
		return endCustmerList;
	}
	public void setEndCustmerList(List<String> endCustmerList) {
		this.endCustmerList = endCustmerList;
	}
	public String getCustomerNameLabel() {
		this.customerNameLabel = customerName + QuoteConstant.AUTOCOMPLETE_SEPARATOR + customerNumber + QuoteConstant.AUTOCOMPLETE_SEPARATOR;
		for(String salesOrg : salesOrgList){
			this.customerNameLabel += salesOrg + "/";
		}
		int len = this.customerNameLabel.length();
		this.customerNameLabel = this.customerNameLabel.substring(0, len-1);
		return customerNameLabel;
	}
	public void setCustomerNameLabel(String customerNameLabel) {
		this.customerNameLabel = customerNameLabel;
	}
	public String getCustomerNumberLabel() {
		this.customerNumberLabel = customerNumber + QuoteConstant.AUTOCOMPLETE_SEPARATOR + customerName + QuoteConstant.AUTOCOMPLETE_SEPARATOR;
		for(String salesOrg : salesOrgList){
			this.customerNumberLabel += salesOrg + "/";
		}
		int len = this.customerNumberLabel.length();
		this.customerNumberLabel = this.customerNumberLabel.substring(0, len-1);		
		return customerNumberLabel;
	}
	public void setCustomerNumberLabel(String customerNumberLabel) {
		this.customerNumberLabel = customerNumberLabel;
	}
	public List<String> getSalesOrgList() {
		return salesOrgList;
	}
	public void setSalesOrgList(List<String> salesOrgList) {
		this.salesOrgList = salesOrgList;
	}
	public String getSalesOrgListInString() {
		String label = "";
		for(String salesOrg : salesOrgList)
			label += salesOrg + "/";
		int len = label.length();
		label = label.substring(0, len-1);		
		return label;
	}

	

	
}
