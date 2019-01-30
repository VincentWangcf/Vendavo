package com.avnet.emasia.webquote.reports.vo;

import java.io.Serializable;
import com.avnet.emasia.webquote.utilities.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.avnet.emasia.webquote.entity.Manufacturer;
import com.avnet.emasia.webquote.entity.MaterialType;
import com.avnet.emasia.webquote.entity.ProductGroup;
import com.avnet.emasia.webquote.entity.ProgramType;
import com.avnet.emasia.webquote.entity.Team;
import com.avnet.emasia.webquote.entity.User;

public class RORReportVo implements Serializable
{
	/**
	 * 
	 */
	private static Logger log = Logger.getLogger(RORReportVo.class.getName());

	private static final long serialVersionUID = 1446276722825246312L;
	private String customer;
	private String endCustomer;
	private String employeeName;
	private String salesmanCode;
	private String team;
	private String itemRemarks;
	private String mfr;
	private String requestedPartNumber;
	private String quotedPartNumber;
	private String requiredQty;
	private String targetResales;
	private String yourReference;
	private String stage;
	private Date uploadTime;
	private String formNumber;
	private String copyToCs;
	
	private SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
	
	public String getCustomer() {
		return customer;
	}
	public void setCustomer(String customer) {
		this.customer = customer;
	}
	public String getEndCustomer() {
		return endCustomer;
	}
	public void setEndCustomer(String endCustomer) {
		this.endCustomer = endCustomer;
	}
	public String getEmployeeName() {
		return employeeName;
	}
	public void setEmployeeName(String employeeName) {
		this.employeeName = employeeName;
	}
	public String getSalesmanCode() {
		return salesmanCode;
	}
	public void setSalesmanCode(String salesmanCode) {
		this.salesmanCode = salesmanCode;
	}
	public String getTeam() {
		return team;
	}
	public void setTeam(String team) {
		this.team = team;
	}
	public String getItemRemarks() {
		return itemRemarks;
	}
	public void setItemRemarks(String itemRemarks) {
		this.itemRemarks = itemRemarks;
	}
	public String getMfr() {
		return mfr;
	}
	public void setMfr(String mfr) {
		this.mfr = mfr;
	}
	public String getRequestedPartNumber() {
		return requestedPartNumber;
	}
	public void setRequestedPartNumber(String requestedPartNumber) {
		this.requestedPartNumber = requestedPartNumber;
	}
	public String getQuotedPartNumber() {
		return quotedPartNumber;
	}
	public void setQuotedPartNumber(String quotedPartNumber) {
		this.quotedPartNumber = quotedPartNumber;
	}
	public String getRequiredQty() {
		return requiredQty;
	}
	public void setRequiredQty(String requiredQty) {
		this.requiredQty = requiredQty;
	}
	public String getTargetResales() {
		return targetResales;
	}
	public void setTargetResales(String targetResales) {
		this.targetResales = targetResales;
	}
	public String getYourReference() {
		return yourReference;
	}
	public void setYourReference(String yourReference) {
		this.yourReference = yourReference;
	}
	public String getStage() {
		return stage;
	}
	public void setStage(String stage) {
		this.stage = stage;
	}
	public Date getUploadTime() {
		return uploadTime;
	}
	public void setUploadTime(Date uploadTime) {
		this.uploadTime = uploadTime;
	}
	public String getFormNumber() {
		return formNumber;
	}
	public void setFormNumber(String formNumber) {
		this.formNumber = formNumber;
	}
	public String getCopyToCs() {
		return copyToCs;
	}
	public void setCopyToCs(String copyToCs) {
		this.copyToCs = copyToCs;
	}

	public String getUploadTimeStr(){
		try{
			if(this.uploadTime != null)
				return sdf.format(this.uploadTime);
		} catch (Exception ex){
			log.log(Level.SEVERE,"Exception occuring in uploadTime format ::"+this.uploadTime+" : "+ ex.getMessage(), ex);
		}
		return null;
	}
	
}
