/**
 * 
 */
package com.avnet.emasia.webquote.reports.vo;

import java.io.Serializable;

/**
 * @author 916138
 *
 */
public class OrderOnHandReportVo extends ReportDisplayVo implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 8531185997346029860L;
	/**
	 * 
	 */
	
	
	private double nonOohTP;
	private double oohNoTP;
	private double oohWithTP;
	private double oohAmt;
	private double totalAmt;
	
	public double getNonOohTP() {
		return nonOohTP;
	}
	public void setNonOohTP(double nonOohTP) {
		this.nonOohTP = nonOohTP;
	}
	public double getOohNoTP() {
		return oohNoTP;
	}
	public void setOohNoTP(double oohNoTP) {
		this.oohNoTP = oohNoTP;
	}
	public double getOohWithTP() {
		return oohWithTP;
	}
	public void setOohWithTP(double oohWithTP) {
		this.oohWithTP = oohWithTP;
	}
	public double getOohAmt() {
		return oohAmt;
	}
	public void setOohAmt(double oohAmt) {
		this.oohAmt = oohAmt;
	}
	public double getTotalAmt() {
		return totalAmt;
	}
	public void setTotalAmt(double totalAmt) {
		this.totalAmt = totalAmt;
	}
	
	
	
	
	
	
	

}
