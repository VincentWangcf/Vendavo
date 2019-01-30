package com.avnet.emasia.webquote.vo;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;

import com.avnet.emasia.webquote.entity.DrNedaItem;

public class DrProjectVO implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private DrNedaItem drNedaItem;
	private int itemNumber;
	private int refItemNumber;
	private Date drExpiryDate;	
	private String salesEmployeeId;
	private Date displayDrExpiryDate;  // fix defect 220 by June display condition dr expired date >= current date
	
	public DrNedaItem getDrNedaItem() {
		return drNedaItem;
	}
	public void setDrNedaItem(DrNedaItem drNedaItem) {
		this.drNedaItem = drNedaItem;
	}
	public int getItemNumber() {
		return itemNumber;
	}
	public void setItemNumber(int itemNumber) {
		this.itemNumber = itemNumber;
	}
	public int getRefItemNumber() {
		return refItemNumber;
	}
	public void setRefItemNumber(int refItemNumber) {
		this.refItemNumber = refItemNumber;
	}
	public Date getDrExpiryDate() {
		return drExpiryDate;
	}
	public void setDrExpiryDate(Date drExpiryDate) {
		this.drExpiryDate = drExpiryDate;
	}
	public String getSalesEmployeeId() {
		return salesEmployeeId;
	}
	public void setSalesEmployeeId(String salesEmployeeId) {
		this.salesEmployeeId = salesEmployeeId;
	}
	public Date getDisplayDrExpiryDate() {
		return displayDrExpiryDate;
	}
	public void setDisplayDrExpiryDate(Date today) {
		//display condition dr expired date >= current date
		if(this.drExpiryDate!=null && null!=today) {
			Calendar drExpiryCal = Calendar.getInstance();
			Calendar currentCal = Calendar.getInstance();
			drExpiryCal.setTime(drExpiryDate);
			drExpiryCal.set(Calendar.HOUR_OF_DAY, 0);
			drExpiryCal.set(Calendar.MINUTE, 0);
			drExpiryCal.set(Calendar.SECOND, 0);
			drExpiryCal.set(Calendar.MILLISECOND, 0);

			currentCal.setTime(today);
			currentCal.set(Calendar.HOUR_OF_DAY, 0);
			currentCal.set(Calendar.MINUTE, 0);
			currentCal.set(Calendar.SECOND, 0);
			currentCal.set(Calendar.MILLISECOND, 0);
			if(drExpiryCal.compareTo(currentCal)<0) {
				this.displayDrExpiryDate = null;
			}else {
				this.displayDrExpiryDate = this.drExpiryDate;
			}
		}else {
			// modified by Lenon(043044) 2015.01.20 note:Remove the check of DR expiry date = blank 
			//this.displayDrExpiryDate = this.drExpiryDate;
			this.displayDrExpiryDate = today;
		}
	}
	
	
		
}
