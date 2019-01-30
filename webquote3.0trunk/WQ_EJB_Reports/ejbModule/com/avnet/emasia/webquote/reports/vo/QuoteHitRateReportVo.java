/**
 * 
 */
package com.avnet.emasia.webquote.reports.vo;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @author 916138
 *
 */
public class QuoteHitRateReportVo extends ReportDisplayVo implements Serializable{

	
	/**
	 * 
	 */
	private static final long serialVersionUID = 4403780697773267679L;
	//count report
	private long quotedCnt;
	private long hitOrderCnt;
	private long metTpHitItemCnt;
	
	//amount report
	private double quotedAmt;
	private double hitOrderAmt;
	
	//new columns
	private long metTpItemCnt;
	private double metTpHitItemAmt;
	
	
	private double hitOrderRate;
	
	

	public long getQuotedCnt() {
		return quotedCnt;
	}

	public void setQuotedCnt(long quotedCnt) {
		this.quotedCnt = quotedCnt;
	}

	public long getHitOrderCnt() {
		return hitOrderCnt;
	}

	public void setHitOrderCnt(long hitOrderCnt) {
		this.hitOrderCnt = hitOrderCnt;
	}

	public long getMetTpHitItemCnt() {
		return metTpHitItemCnt;
	}

	public void setMetTpHitItemCnt(long metTpHitItemCnt) {
		this.metTpHitItemCnt = metTpHitItemCnt;
	}

	public double getQuotedAmt() {
		return quotedAmt;
	}

	public void setQuotedAmt(double quotedAmt) {
		this.quotedAmt = quotedAmt;
	}

	public double getHitOrderAmt() {
		return hitOrderAmt;
	}

	public void setHitOrderAmt(double hitOrderAmt) {
		this.hitOrderAmt = hitOrderAmt;
	}

	public double getHitOrderRate() {
		return hitOrderRate;
	}

	public void setHitOrderRate(double hitOrderRate) {
		this.hitOrderRate = hitOrderRate;
	}
	
	public void setHitOrderRate(long quote,long hitOrder) {
		 BigDecimal qt =new BigDecimal(quote);
		 BigDecimal ho = new BigDecimal(hitOrder);
		 if(null !=qt && qt.intValue()>0)
		    	this.hitOrderRate = ho.divide(qt,4, BigDecimal.ROUND_HALF_UP).multiply(this.rateMultiplier).doubleValue();
	}
	
	public void setHitOrderRate(double quote,double hitOrder) {
		 BigDecimal qt =new BigDecimal(quote);
		 BigDecimal ho = new BigDecimal(hitOrder);
		 if(null !=qt && qt.intValue()>0)
		    	this.hitOrderRate = ho.divide(qt,4, BigDecimal.ROUND_HALF_UP).multiply(this.rateMultiplier).doubleValue();
	}

	public long getMetTpItemCnt() {
		return metTpItemCnt;
	}

	public void setMetTpItemCnt(long metTpItemCnt) {
		this.metTpItemCnt = metTpItemCnt;
	}

	public double getMetTpHitItemAmt() {
		return metTpHitItemAmt;
	}

	public void setMetTpHitItemAmt(double metTpHitItemAmt) {
		this.metTpHitItemAmt = metTpHitItemAmt;
	}


	
	
	
	
	

}
