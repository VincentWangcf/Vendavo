package com.avnet.emasia.webquote.web.reports;

import java.io.Serializable;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;

import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.primefaces.model.StreamedContent;

import com.avnet.emasia.webquote.dp.EJBCommonSB;
import com.avnet.emasia.webquote.entity.Customer;
import com.avnet.emasia.webquote.entity.Manufacturer;
import com.avnet.emasia.webquote.entity.QuoteItem;
import com.avnet.emasia.webquote.entity.User;
import com.avnet.emasia.webquote.quote.ejb.ManufacturerSB;
import com.avnet.emasia.webquote.quote.ejb.constant.QuoteSBConstant;
import com.avnet.emasia.webquote.reports.ejb.RORReportSB;
import com.avnet.emasia.webquote.reports.vo.RORReportSearchCriteria;
import com.avnet.emasia.webquote.reports.vo.RORReportVo;
import com.avnet.emasia.webquote.user.ejb.TeamSB;
import com.avnet.emasia.webquote.user.ejb.UserSB;
import com.avnet.emasia.webquote.utilites.resources.ResourceMB;
import com.avnet.emasia.webquote.utilites.web.util.DownloadUtil;
import com.avnet.emasia.webquote.utilites.web.util.QuoteUtil;
import com.avnet.emasia.webquote.utilities.DateUtils;
import com.avnet.emasia.webquote.web.user.UserInfo;

@ManagedBean
@SessionScoped
public class RorReportMB implements Serializable
{

	/**
	 * 
	 */
	private static final long serialVersionUID = -3610507651540539263L;
	private static final Logger LOGGER = Logger.getLogger(RorReportMB.class.getName());

	private User user;
	private String exportFileName;
	private final int DEFAULT_DATE_RANGE = 7;

	private RORReportSearchCriteria criteria;
	private List<QuoteItem> quoteItemList;
	private List<RORReportVo> rorReportList;
	private List<RORReportVo> filteredRorReportList;
	private DecimalFormat df = new DecimalFormat("#");


	@EJB
	private RORReportSB rorReportSB;

	@EJB
	private UserSB userSB;

	@EJB
	private TeamSB teamSB;

	@EJB
	private ManufacturerSB manufacturerSB;
	
	@EJB
	private EJBCommonSB ejbCommonSB;
	
	public RorReportMB(){
		user = UserInfo.getUser();		
		resetCriteria();
		df.setMaximumFractionDigits(5);
		df.setMinimumIntegerDigits(1);

	}

	public String getExportFileName() {
		Date time = new Date();
		return "ROR Report_"+time.toString();
	}

	public void setExportFileName(String exportFileName) {
		this.exportFileName = exportFileName;
	}

	@PostConstruct
	public void postContruct()
	{
	}

	public void resetCriteria()
	{
		criteria = new RORReportSearchCriteria();

		List<String> stage = new ArrayList<String>();
		stage.add(QuoteSBConstant.QUOTE_STAGE_FINISH);
		stage.add(QuoteSBConstant.QUOTE_STAGE_INVALID);
		stage.add(QuoteSBConstant.QUOTE_STAGE_PENDING);

		criteria.setPageStage(stage);	
		criteria.setStage(stage);	

		Calendar cal = Calendar.getInstance();
		Date endDate = cal.getTime();
		criteria.setEndDate(endDate);
		cal.add(Calendar.DATE, -1*DEFAULT_DATE_RANGE+1);
		Date startDate = cal.getTime();
		criteria.setStartDate(startDate);
	}

	public void searchRORReport(){
		//fix incident INC0097122 June Guo 2015/2/4
		if(null==criteria.getStartDate() || null==criteria.getEndDate()) {
			FacesContext.getCurrentInstance().addMessage("messages", new FacesMessage(FacesMessage.SEVERITY_ERROR,ResourceMB.getText("wq.message.inputRfqCreatedForm")+".", ""));
			return;
		}
		int diffDays = DateUtils.getDayDiff(criteria.getStartDate(), criteria.getEndDate());
		if(diffDays<0 || diffDays>7) {
			FacesContext.getCurrentInstance().addMessage("messages", new FacesMessage(FacesMessage.SEVERITY_ERROR,ResourceMB.getText("wq.message.inputRfqCreatedForm")+".", ""));
			return;
		}
		rorReportList = new ArrayList<RORReportVo>();

		criteria.setStartDate(QuoteUtil.getStartOfDay(criteria.getStartDate()));
		criteria.setEndDate(QuoteUtil.getEndOfDay(criteria.getEndDate()));
		criteria.setBizUnits(user.getBizUnits());
		LOGGER.log(Level.INFO, criteria.toString());

		quoteItemList = rorReportSB.findRORReport(criteria, user);
		if(quoteItemList!=null)
		{
			LOGGER.log(Level.INFO, "RORReport Search : " + quoteItemList.size() + " is found");

			for(QuoteItem quoteItem : quoteItemList){
				RORReportVo vo = new RORReportVo();
				if(quoteItem.getQuote() != null){
					vo.setCopyToCs(quoteItem.getQuote().getCopyToCS());
					vo.setFormNumber(quoteItem.getQuote().getFormNumber());
					vo.setUploadTime(quoteItem.getSubmissionDate());
					vo.setYourReference(quoteItem.getQuote().getYourReference());

					if(quoteItem.getQuote().getSales() != null){
						vo.setEmployeeName(quoteItem.getQuote().getSales().getName());
						vo.setSalesmanCode(quoteItem.getQuote().getSales().getEmployeeId());

						if(quoteItem.getQuote().getSales().getTeam() != null){
							vo.setTeam(quoteItem.getQuote().getSales().getTeam().getName());
						}	
					}
				}

				vo.setCustomer(quoteItem.getSoldToCustomerFullName());
				vo.setEndCustomer(quoteItem.getEndCustomerFullName());
				vo.setItemRemarks(quoteItem.getRemarks());
				vo.setStage(quoteItem.getStage());

				if(quoteItem.getRequestedQty() != null)
					vo.setRequiredQty(String.valueOf(quoteItem.getRequestedQty()));
				if(quoteItem.getTargetResale() != null){
					vo.setTargetResales(df.format(quoteItem.getTargetResale()));
				}

				//			if(quoteItem.getRequestedMaterial() != null){
				//				vo.setRequestedPartNumber(quoteItem.getRequestedMaterial().getFullMfrPartNumber());
				//				
				//				if(quoteItem.getRequestedMaterial().getManufacturer() != null)
				//					vo.setMfr(quoteItem.getRequestedMaterial().getManufacturer().getName());
				//			}

				if(quoteItem.getRequestedPartNumber() != null){
					vo.setRequestedPartNumber(quoteItem.getRequestedPartNumber());

					if(quoteItem.getRequestedMfr() != null)
						vo.setMfr(quoteItem.getRequestedMfr().getName());
				}			

				if(quoteItem.getQuotedMaterial() != null)
					vo.setQuotedPartNumber(quoteItem.getQuotedMaterial().getFullMfrPartNumber());

				rorReportList.add(vo);
			}
		}
		else
		{
			LOGGER.log(Level.INFO, "RORReport Search returns 0 result");
		}
	}

	public List<String> autoCompleteTeam(String key)
	{
		return ejbCommonSB.autoCompleteTeam(key,this.teamSB);
	}

	public List<String> autoCompleteSale(String key)
	{
		return ejbCommonSB.autoCompleteSale(key,this.userSB);
	}

	public List<String> autoCompleteForCustomer(String key)
	{
		List<String> rltLst = new ArrayList<String>();
		List<Customer> custrLst = userSB.findAutoCompleteCustomers(key);

		for (Customer ct : custrLst)
		{
			/*String name1 = ct.getCustomerName1();
			String name2 = ct.getCustomerName2();

			if (name1 == null)
			{
				name1 = "";
			}
			if (name2 == null)
			{
				name2 = "";
			}*/
			//updated  function getcustomerName1 to getCustomerFulname by damon
			rltLst.add(ct.getCustomerFullName());
		}

		return rltLst;
	}

	public List<String> autoCompleteMfr(String key)
	{
		List<String> rltLst = new ArrayList<String>();
		List<Manufacturer> mLst = manufacturerSB.mFindManufacturerByName(key,
				user.getBizUnits());

		for (Manufacturer m : mLst)
		{
			rltLst.add(m.getName());
		}

		return rltLst;
	}

	public StreamedContent downloadRorReport(){

		//logger.log(Level.INFO, "PERFORMANCE START - downloadWorkingPlatform()");

		List<List<String>> data = null;
		if(this.filteredRorReportList != null){
			data = DownloadUtil.convertRorReportResult(this.filteredRorReportList);
		} else {
			data = DownloadUtil.convertRorReportResult(this.rorReportList);			
		}

		List<String> header = new ArrayList<String>();
		header.add("Customer Name");	
		header.add("End Customer");
		header.add("Salesman Name");
		header.add("Salesman Employee Code");
		header.add("Team");
		header.add("Item Remarks");
		header.add("MFR");
		header.add("Requested P/N");
		header.add("Avnet Quoted P/N");
		header.add("Required Qty");
		header.add("Target Resale");
		header.add("Requester Reference");
		header.add("Quote Stage");
		header.add("RFQ Submission Date");
		header.add("Form No");
		header.add("Copy To CS");
		StreamedContent rorReport = DownloadUtil.generateExcel(header, data, "Download.xlsx");
		return rorReport;

		//logger.log(Level.INFO, "PERFORMANCE END - downloadWorkingPlatform()");

	}  

	public RORReportSearchCriteria getCriteria() {
		return criteria;
	}

	public void setCriteria(RORReportSearchCriteria criteria) {
		this.criteria = criteria;
	}

	public List<QuoteItem> getQuoteItemList() {
		return quoteItemList;
	}

	public void setQuoteItemList(List<QuoteItem> quoteItemList) {
		this.quoteItemList = quoteItemList;
	}

	public List<RORReportVo> getRorReportList() {
		return rorReportList;
	}

	public void setRorReportList(List<RORReportVo> rorReportList) {
		this.rorReportList = rorReportList;
	}

	public List<RORReportVo> getFilteredRorReportList() {
		return filteredRorReportList;
	}

	public void setFilteredRorReportList(List<RORReportVo> filteredRorReportList) {
		this.filteredRorReportList = filteredRorReportList;
	}

	public StreamedContent getRorReport() {
		return downloadRorReport();		
	}

	public void setRorReport(StreamedContent rorReport) {
	}

	public void postProcessXLS(Object document)
	{
		String[] columns = { "No.", "Customer Name", "End Customer", "Salesman Name",
				"Salesman Employee Code", "Team", "Item Remarks", "MFR",
				"Requested P/N", "Avnet Quoted P/N", "Requested Qty", "Target Resale",
				"Requester Reference", "Quote Stage" };
		HSSFWorkbook wb = (HSSFWorkbook) document;
		HSSFSheet sheet = wb.getSheetAt(0);
		HSSFRow header = sheet.getRow(0);
		int i = 0;
		for (String column : columns)
		{
			header.getCell(i++).setCellValue(column);
		}
	}

	
	
	
}