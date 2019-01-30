package com.avnet.emasia.webquote.web.quote;

import java.io.Serializable;
import java.util.ArrayList;

import java.util.Date;

import java.util.List;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import com.avnet.emasia.webquote.entity.AuditQuoteItem;
import com.avnet.emasia.webquote.entity.User;
import com.avnet.emasia.webquote.reports.ejb.OfflineReportSB;
import com.avnet.emasia.webquote.quote.ejb.QuoteSB;
import com.avnet.emasia.webquote.quote.vo.AuditReportSearchCriteria;
import com.avnet.emasia.webquote.utilites.web.util.QuoteUtil;
import com.avnet.emasia.webquote.web.user.UserInfo;

@ManagedBean
@SessionScoped
public class AuditReportMB implements Serializable {


	private static final long serialVersionUID = 4947932561511214150L;

	private static final Logger LOG =Logger.getLogger(AuditReportMB.class.getName());

	@EJB
	QuoteSB quoteSB;

	@EJB
	OfflineReportSB offlineReprtSB;

	private List<AuditQuoteItem> auditQuoteItems;
	private List<AuditQuoteItem> filteredAuditQuoteItems;
	
	private String formNumber;
	private String quoteNumber;
	private String quotedMfr;
	private String quotedPartNumber;
	private Date startDate;
	private Date endDate;

	public AuditReportMB(){

	}

	@PostConstruct
	public void postContruct(){

	}
	
	public void postProcessXLS(Object document)
	{
		String[] columns = {"No.","Update Date & Time","Create/Update By","Create/Update By (Employee Id)","Form Number","Quote Number","Stage Old","Stage New",
				"Status Old","Status New","Quoted MFR Old","Quoted MFR New","Quoted Part Number Old","Quoted Part Number New","Quoted Qty Old","Quoted Qty New",
				"PMOQ Old","PMOQ New","Qty Indicator Old","Qty Indicator New","Cost Indicator Old","Cost Indicator New","Cost Old","Cost New",
				"Quoted Price Old","Quoted Price New","Lead Time Old","Lead Time New","MPQ Old","MPQ New","MOQ Old","MOQ New",
				"MOV Old","MOV New","Price Validity Old","Price Validity New","Shipment Validity Old","Shipment Validity New","Resale Indicator Old","Resale Indicator New"};
		HSSFWorkbook wb = (HSSFWorkbook) document;
		HSSFSheet sheet = wb.getSheetAt(0);
		HSSFRow header = sheet.getRow(0);
		int i = 0;
		for (String column : columns)
		{
			header.getCell(i++).setCellValue(column);
		}
	}

	public void search(){

		AuditReportSearchCriteria criteria = new AuditReportSearchCriteria();
		
		User user = UserInfo.getUser();

		criteria.setBizUnit(user.getDefaultBizUnit());
		criteria.setFormNumber(formNumber);
		criteria.setMfr(quotedMfr);
		criteria.setQuotedPartNumber(quotedPartNumber);
		criteria.setQuoteNumber(quoteNumber);
		criteria.setUpdateDateFrom(startDate);
		criteria.setUpdateDateTo(endDate);
	
		auditQuoteItems = quoteSB.findAuditQuoteItem(criteria);
		
		LOG.log(Level.INFO, auditQuoteItems.size() + " records is found for audit Quote");

	}

	public void reset(){

		formNumber = null;
		quoteNumber = null;
		
		startDate = null;
		endDate = null;
		
		quotedMfr = null;
		quotedPartNumber = null;

	}
	
	public List<AuditQuoteItem> getAuditQuoteItems() {
		return auditQuoteItems;
	}

	public void setAuditQuoteItems(List<AuditQuoteItem> auditQuoteItems) {
		this.auditQuoteItems = auditQuoteItems;
	}

	public List<AuditQuoteItem> getFilteredAuditQuoteItems() {
		return filteredAuditQuoteItems;
	}

	public void setFilteredAuditQuoteItems(
			List<AuditQuoteItem> filteredAuditQuoteItems) {
		this.filteredAuditQuoteItems = filteredAuditQuoteItems;
	}

	public String getFormNumber() {
		return formNumber;
	}

	public void setFormNumber(String formNumber) {
		this.formNumber = formNumber;
	}

	public String getQuoteNumber() {
		return quoteNumber;
	}

	public void setQuoteNumber(String quoteNumber) {
		this.quoteNumber = quoteNumber;
	}

	public String getQuotedMfr() {
		return quotedMfr;
	}

	public void setQuotedMfr(String quotedMfr) {
		this.quotedMfr = quotedMfr;
	}

	public String getQuotedPartNumber() {
		return quotedPartNumber;
	}

	public void setQuotedPartNumber(String quotedPartNumber) {
		this.quotedPartNumber = quotedPartNumber;
	}

	public Date getStartDate() {
		return startDate;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	public Date getEndDate() {
		return endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}



}

