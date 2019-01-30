package com.avnet.emasia.webquote.web.reports;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.jboss.logmanager.Level;

import com.avnet.emasia.webquote.dp.EJBCommonSB;
import com.avnet.emasia.webquote.entity.Manufacturer;
import com.avnet.emasia.webquote.entity.User;
import com.avnet.emasia.webquote.quote.ejb.ManufacturerSB;
import com.avnet.emasia.webquote.quote.ejb.QuoteSB;
import com.avnet.emasia.webquote.reports.ejb.QuoteCycleReportSB;
import com.avnet.emasia.webquote.reports.vo.QuoteReportCriteria;
import com.avnet.emasia.webquote.reports.vo.RFQBacklogReportVo;
import com.avnet.emasia.webquote.user.ejb.RoleSB;
import com.avnet.emasia.webquote.user.ejb.TeamSB;
import com.avnet.emasia.webquote.user.ejb.UserSB;
import com.avnet.emasia.webquote.utilites.resources.ResourceMB;
import com.avnet.emasia.webquote.utilites.web.util.QuoteUtil;
import com.avnet.emasia.webquote.utilities.common.Constants;

@ManagedBean
@SessionScoped
public class QuoteCycleReportMB implements Serializable
{

	/**
	 * 
	 */
	private static final long serialVersionUID = 5877626476954014881L;
	private static final Logger LOG = Logger.getLogger(QuoteCycleReportMB.class
			.getName());
	// private static final String ROLE_QC_PRICING = "ROLE_QC_PRICING";
	private String exportFileName;
	private RFQBacklogReportVo reportCriteria;
	private List<RFQBacklogReportVo> rfqBacklogReportLst;
	private List<RFQBacklogReportVo> filteredRfqBacklogReportLst;

	@EJB
	private QuoteCycleReportSB quoteCycleReportSB;

	@EJB
	private RoleSB roleSB;

	@EJB
	private TeamSB teamSB;

	@EJB
	private UserSB userSB;

	@EJB
	private QuoteSB quoteSB;

	@EJB
	private ManufacturerSB manufacturerSB;

	@EJB
	protected EJBCommonSB ejbCommonSB;
	
	public List<RFQBacklogReportVo> getFilteredRfqBacklogReportLst() {
		return filteredRfqBacklogReportLst;
	}

	public void setFilteredRfqBacklogReportLst(
			List<RFQBacklogReportVo> filteredRfqBacklogReportLst) {
		this.filteredRfqBacklogReportLst = filteredRfqBacklogReportLst;
	}

	public RFQBacklogReportVo getReportCriteria()
	{
		return reportCriteria;
	}
	
	public String getExportFileName() {
		Date time = new Date();
		return ResourceMB.getDefaultText("wq.label.quoteCyc")+"_"+time.toString();
	}

	public void setExportFileName(String exportFileName) {
		this.exportFileName = exportFileName;
	}
	
	public void setReportCriteria(RFQBacklogReportVo reportCriteria)
	{
		this.reportCriteria = reportCriteria;
	}

	public List<RFQBacklogReportVo> getRfqBacklogReportLst()
	{
		return rfqBacklogReportLst;
	}

	public void setRfqBacklogReportLst(
			List<RFQBacklogReportVo> rfqBacklogReportLst)
	{
		this.rfqBacklogReportLst = rfqBacklogReportLst;
	}


	public void resetCriteria()
	{
		reportCriteria.setTeam("");
		reportCriteria.setSalemanName("");
		reportCriteria.setFormNo("");
		reportCriteria.setQcPricer("");
		reportCriteria.setPendingTime("");
		Date date = new Date();
		reportCriteria.setCreateFrom((ejbCommonSB.getDate(Calendar.DATE, -7)));
		reportCriteria.setCreateTo(date);
	}

	@PostConstruct
	public void constructCriteria()
	{
		reportCriteria = new RFQBacklogReportVo();
		reportCriteria = ejbCommonSB.constructCriteria(reportCriteria, 7, this);
	}

	public void reportOfflineSearch()
	{

	}

	public void searchCriteriaForReport()
	{
		try
		{
			LOG.info("search cycle time report with criteria... ");
			QuoteReportCriteria rptCriteria = new QuoteReportCriteria();
			rptCriteria.setQcPricer(reportCriteria.getQcPricer());
			rptCriteria.setsTeam(reportCriteria.getTeam());
			rptCriteria.setsMfr(reportCriteria.getMfrName());
			rptCriteria.setSentOutDateFrom(reportCriteria.getCreateFrom());
			rptCriteria.setSentOutDateTo(reportCriteria.getCreateTo());
			List<String> stage = new ArrayList<String>();
			stage.add(Constants.QUOTE_STAGE_INVALID);
			stage.add(Constants.QUOTE_STAGE_FINISH);
			rptCriteria.setStage(stage);

			rfqBacklogReportLst = quoteCycleReportSB
					.quoteCycleTmeSearch(rptCriteria,ejbCommonSB.getUser().getBizUnits());
			
			filteredRfqBacklogReportLst = null;
			LOG.info("search cycle time report with criteria  ended.");
		}
		catch (Exception e)
		{
			LOG.log(Level.SEVERE, "search cycle time report with criteria  failed! for QC owner : "+reportCriteria.getQcPricer()+" , MFR : "+reportCriteria.getMfr()
			+" , Sent out from "+QuoteUtil.convertDateToString(reportCriteria.getCreateFrom())+" , sent out TO : "+QuoteUtil.convertDateToString(reportCriteria.getCreateTo())+" , error message"+ e.getMessage(), e);
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
	
	public List<String> autoCompleteFormNo(String key)
	{
		return ejbCommonSB.autoCompleteFormNo(key,this.quoteSB);
	}

	public List<String> autoCompleteQcp(String key)
	{
		List<String> rltLst = new ArrayList<String>();
		//List<User> usrLst = userSB.wfindUserByEmployeeNameAndRole(key,"ROLE_QC_PRICING", user.getBizUnits().get(0));

		String [] roles = {"ROLE_QC_PRICING","ROLE_QCP_MANAGER"};
		List<User> usrLst = userSB.findUserWithKeywordAndRoles(key, roles, ejbCommonSB.getUser().getBizUnits().get(0));
		for (User usr : usrLst)
		{
			rltLst.add(usr.getName());
		}

		return rltLst;
	}

	public List<String> autoCompleteMfr(String key)
	{
		List<String> rltLst = new ArrayList<String>();
		List<Manufacturer> mLst = manufacturerSB.mFindManufacturerByName(key,
				ejbCommonSB.getUser().getBizUnits());

		for (Manufacturer m : mLst)
		{
			rltLst.add(m.getName());
		}

		return rltLst;
	}
	
	public void postProcessXLS(Object document)
	{
		String[] columns = { "No", "RFQ Code", "Finished", "Quoted",
				"Invalid", "Internal Transfer", "System"+"Quote", "Quoted Price",
				"Cost", "Avnet"+"Quoted Qty", "Upload Time", "Sent Out Time",
				"Employee Name", "Team","quoteType", "MFR",
				"Required Qty", "Target Resale", "Cost Indicator",
				"RFQ Status", "Customer","Avnet Quoted P/N", "Enquiry Segment",
				"First RFQ Code", "Revert Version","BU", "QC Owner", "Sent Out Date", "FiscalWeek", "QuotedMonth", 
				"MFR2","BUM","Sales GM","MFR Group","WithTarget","MeetTarget","QuotedAmount", "ResponseTime",
				"ResponseTimeby Day", "ResponseTimeGrouping in Days", 
				"Count Hit Order","Hit Order Amount","Adjusted OrderAmount","With TP Quote Amount","Met TP Quote Amount","Met TP Hit","Met TP Hit Amount","AQ", "1st RFQ"};
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