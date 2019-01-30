package com.avnet.emasia.webquote.quote.vo; 

import java.io.Serializable;

/**
 * @author Lin, Tough(901518) Created on 2013-2-27
 */

public class FuturePriceCriteria implements Serializable {


	private static final long serialVersionUID = -5562311023152260517L;
	private String mfr;
	private String partNumber;
	private String bizUnit;
	
	
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
	public String getBizUnit() {
		return bizUnit;
	}
	public void setBizUnit(String bizUnit) {
		this.bizUnit = bizUnit;
	}

}
