package com.avnet.emasia.webquote.web.quote;

import java.io.Serializable;
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

import com.avnet.emasia.webquote.entity.AuditExchangeRate;
import com.avnet.emasia.webquote.entity.User;
import com.avnet.emasia.webquote.quote.ejb.QuoteSB;
import com.avnet.emasia.webquote.quote.vo.AuditExchangeReportSearchCriteria;
import com.avnet.emasia.webquote.web.user.UserInfo;

@ManagedBean
@SessionScoped
public class AuditExchangeRateReportMB implements Serializable {


	private static final long serialVersionUID = 4947932561511214150L;

	private static final Logger LOG =Logger.getLogger(AuditExchangeRateReportMB.class.getName());

	@EJB
	QuoteSB quoteSB;

	private List<AuditExchangeRate> auditExchangeRates;
	private List<AuditExchangeRate> filteredAuditExchangeRates;
	
	private String currencyFrom;
	private String currencyTo;
	private String soldToCode;
	private Date startDate;
	private Date endDate;

	public AuditExchangeRateReportMB(){

	}

	@PostConstruct
	public void postContruct(){

	}
	
	public void postProcessXLS(Object document)
	{
		String[] columns = { "No.", "Update Date& Time", "Action", "Create/Update By",
				"Create/Update By (Employee Id)", "Currency From", "Currency To", "Sold To Code",
				"Exchange Rate From Old", "Exchange Rate From New", "Exchange Rate To Old", "Exchange Rate To New",
				"VAT Old", "VAT New","Handling Old", "Handling New"};
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

		AuditExchangeReportSearchCriteria criteria = new AuditExchangeReportSearchCriteria();
		
		User user = UserInfo.getUser();

		criteria.setBizUnit(user.getDefaultBizUnit());
		criteria.setCurrencyFrom(currencyFrom);
		criteria.setCurrencyTo(currencyTo);
		criteria.setSoldToCode(soldToCode);
		criteria.setUpdateDateFrom(startDate);
		criteria.setUpdateDateTo(endDate);
	
		auditExchangeRates = quoteSB.findAuditExchangeRate(criteria);
		
		LOG.log(Level.INFO, auditExchangeRates.size() + " records is found for audit Exchange Rate");

	}

	public void reset(){

		auditExchangeRates = null;
		currencyTo = null;
		currencyFrom = null;
		startDate = null;
		endDate = null;
		
		soldToCode = null;

	}

	public List<AuditExchangeRate> getAuditExchangeRates() {
		return auditExchangeRates;
	}

	public void setAuditExchangeRates(List<AuditExchangeRate> auditExchangeRates) {
		this.auditExchangeRates = auditExchangeRates;
	}

	public List<AuditExchangeRate> getFilteredAuditExchangeRates() {
		return filteredAuditExchangeRates;
	}

	public void setFilteredAuditExchangeRates(
			List<AuditExchangeRate> filteredAuditExchangeRates) {
		this.filteredAuditExchangeRates = filteredAuditExchangeRates;
	}

	public String getCurrencyFrom() {
		return currencyFrom;
	}

	public void setCurrencyFrom(String currencyFrom) {
		this.currencyFrom = currencyFrom;
	}

	public String getCurrencyTo() {
		return currencyTo;
	}

	public void setCurrencyTo(String currencyTo) {
		this.currencyTo = currencyTo;
	}

	public String getSoldToCode() {
		return soldToCode;
	}

	public void setSoldToCode(String soldToCode) {
		this.soldToCode = soldToCode;
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

