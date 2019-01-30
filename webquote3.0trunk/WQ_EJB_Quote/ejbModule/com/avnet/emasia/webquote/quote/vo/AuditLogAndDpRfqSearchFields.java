package com.avnet.emasia.webquote.quote.vo;

import java.io.Serializable;
import java.util.List;

import com.avnet.emasia.webquote.entity.BizUnit;

public class AuditLogAndDpRfqSearchFields implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = -6154256365544132957L;
	private List<String> quoteNumber;
	private BizUnit bizUnit;
	private String sQuoteNumber;
	
	public List<String> getQuoteNumber() {
		return quoteNumber;
	}

	public void setQuoteNumber(List<String> quoteNumber) {
		this.quoteNumber = quoteNumber;
	}

	public BizUnit getBizUnit() {
		return bizUnit;
	}

	public void setBizUnit(BizUnit bizUnit) {
		this.bizUnit = bizUnit;
	}

	public String getsQuoteNumber() {
		return sQuoteNumber;
	}

	public void setsQuoteNumber(String sQuoteNumber) {
		this.sQuoteNumber = sQuoteNumber;
	}

}
