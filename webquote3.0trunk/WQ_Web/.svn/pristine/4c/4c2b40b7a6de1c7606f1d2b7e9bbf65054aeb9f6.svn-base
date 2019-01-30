package com.avnet.emasia.webquote.commodity.vo;

import java.util.Map;

public class ValidateError implements java.io.Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1713489380266767151L;
	String headerMsg;
	String bodyMsg;
	String erroeMsg;
	boolean hasError;
	Map<Long,Integer> issueRows ;
	public String getHeaderMsg() {
		return headerMsg;
	}
	public void setHeaderMsg(String headerMsg) {
		this.headerMsg = headerMsg;
	}
	public String getBodyMsg() {
		return bodyMsg;
	}
	public void setBodyMsg(String bodyMsg) {
		this.bodyMsg = bodyMsg;
	}
	public String getErroeMsg() {
		return erroeMsg;
	}
	public void setErroeMsg(String erroeMsg) {
		if(erroeMsg==null || "".equalsIgnoreCase(erroeMsg))
			hasError= false;
		else
			hasError= true;
		this.erroeMsg = erroeMsg;
	}
	public Map<Long,Integer> getIssueRows() {
		return issueRows;
	}
	public void setIssueRows(Map<Long,Integer> issueRows) {
		this.issueRows = issueRows;
	}
	public boolean isHasError() {
		return hasError;
	}
	public void setHasError(boolean hasError) {
		this.hasError = hasError;
	} 


	

}
