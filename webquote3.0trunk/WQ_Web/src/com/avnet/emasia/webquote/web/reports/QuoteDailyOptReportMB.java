package com.avnet.emasia.webquote.web.reports;

import java.io.Serializable;
import java.text.ParseException;
import com.avnet.emasia.webquote.utilities.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.persistence.Tuple;

import org.apache.commons.lang.time.DateUtils;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.springframework.security.core.context.SecurityContextHolder;
import com.avnet.emasia.webquote.entity.DataAccess;
import com.avnet.emasia.webquote.entity.User;
import com.avnet.emasia.webquote.exception.WebQuoteRuntimeException;
import com.avnet.emasia.webquote.quote.ejb.ManufacturerSB;
import com.avnet.emasia.webquote.quote.ejb.QuoteSB;
import com.avnet.emasia.webquote.quote.ejb.constant.QuoteSBConstant;
import com.avnet.emasia.webquote.quote.vo.QuoteItemVo;
import com.avnet.emasia.webquote.reports.ejb.QuoteDailyOptReportSB;
import com.avnet.emasia.webquote.reports.vo.QuoteDailyOptVo;
import com.avnet.emasia.webquote.reports.vo.QuoteReportCriteria;
import com.avnet.emasia.webquote.user.ejb.RoleSB;
import com.avnet.emasia.webquote.user.ejb.TeamSB;
import com.avnet.emasia.webquote.user.ejb.UserSB;
import com.avnet.emasia.webquote.utilites.resources.ResourceMB;
import com.avnet.emasia.webquote.utilites.web.util.QuoteUtil;
import com.avnet.emasia.webquote.utilities.common.Constants;
import com.avnet.emasia.webquote.web.quote.constant.QuoteConstant;
import com.avnet.emasia.webquote.web.reports.vo.QuoteDailyReportVo;
import com.avnet.emasia.webquote.web.security.WQUserDetails;

@ManagedBean
@SessionScoped
public class QuoteDailyOptReportMB implements Serializable
{

	/**
	 * 
	 */
	private static final long serialVersionUID = 4178125382322419108L;
	private static final Logger LOG = Logger
			.getLogger(QuoteDailyOptReportMB.class.getName());
	private static User user;
	private QuoteDailyOptVo reportCriteria;
	private List<QuoteDailyOptVo> quoteDetailLst;
	private List<QuoteDailyOptVo> filteredQuoteDetailLst;
	
	private List<QuoteDailyReportVo> quoteDailyReportVos;
	private List<QuoteDailyReportVo> filteredQuoteDailyReportVos;
	
	private String exportFileName;
	
	private String col1;
	private String col2;
	private String col3;
	private String col4;
	private String col5;
	private String col6;
	private String col7;

	private String total;	

	@EJB
	private QuoteDailyOptReportSB quoteDetailOptReportSB;

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

	@ManagedProperty(value="#{resourceMB}")
	/** To Inject ResourceMB instance  */
    private ResourceMB resourceMB;
	
	public List<QuoteDailyOptVo> getFilteredQuoteDetailLst() {
		return filteredQuoteDetailLst;
	}

	public void setFilteredQuoteDetailLst(
			List<QuoteDailyOptVo> filteredQuoteDetailLst) {
		this.filteredQuoteDetailLst = filteredQuoteDetailLst;
	}

	public String getExportFileName() {
		Date time = new Date();
		return ResourceMB.getDefaultText("wq.label.dailyQuoteReport")+"_"+time.toString();
	}

	public void setExportFileName(String exportFileName) {
		this.exportFileName = exportFileName;
	}
	
	public List<QuoteDailyOptVo> getQuoteDetailLst()
	{
		return quoteDetailLst;
	}

	public void setQuoteDetailLst(List<QuoteDailyOptVo> quoteDetailLst)
	{
		this.quoteDetailLst = quoteDetailLst;
	}

	public QuoteDailyOptVo getReportCriteria()
	{
		return reportCriteria;
	}

	public void setReportCriteria(QuoteDailyOptVo reportCriteria)
	{
		this.reportCriteria = reportCriteria;
	}

	public static User getUser()
	{
		WQUserDetails userDetails = (WQUserDetails) SecurityContextHolder
				.getContext().getAuthentication().getPrincipal();
		user = userDetails.getUser();
		return user;
	}

	public void resetCriteria()
	{
		reportCriteria.setUserPmName("");
		Date date = new Date();
		reportCriteria.setSentOutFrom((getDate(Calendar.DATE, -6)));
		reportCriteria.setSentOutTo(date);
		List<String> stage = new ArrayList<String>();
		stage.add(QuoteSBConstant.QUOTE_STAGE_FINISH);

		List<String> pageStage = new ArrayList<String>();
		pageStage.add(QuoteSBConstant.QUOTE_STAGE_FINISH);
		pageStage.add(QuoteSBConstant.QUOTE_STAGE_INVALID);
		reportCriteria.setStage(stage);		
		reportCriteria.setPageStage(pageStage);
	}

	@PostConstruct
	public void constructCriteria()
	{
		try
		{
			LOG.info("Initalization Daily Quote Output Report");
			reportCriteria = new QuoteDailyOptVo();
			Date date = new Date();
			reportCriteria.setSentOutFrom((getDate(Calendar.DATE, -6)));
			reportCriteria.setSentOutTo(date);
			List<String> stage = new ArrayList<String>();
			stage.add(QuoteSBConstant.QUOTE_STAGE_FINISH);
			
			List<String> pageStage = new ArrayList<String>();
			pageStage.add(QuoteSBConstant.QUOTE_STAGE_FINISH);
			pageStage.add(QuoteSBConstant.QUOTE_STAGE_INVALID);
			reportCriteria.setStage(stage);
			reportCriteria.setPageStage(pageStage);
		
			getUser();
		}
		catch (Exception e)
		{
			LOG.log(Level.SEVERE, "Initalization Daily Quote Output report failed! Exception message: "+e.getMessage(), e);
			
		}
	}

	private Date getDate(int field, int amount)
	{
		Date date = new Date();
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		cal.add(field, amount);

		return cal.getTime();
	}

	private List<QuoteDailyOptVo> converReportBean()
	{
		LOG.info("Convert to quote detail time report.");
		List<QuoteDailyOptVo> qvoLst = new ArrayList<QuoteDailyOptVo>();
		return qvoLst;
	}

	public void searchCriteriaForReport()
	{
		try
		{
			LOG.info("search Daily Quote Output Report with criteria... ");

			Date startDate = reportCriteria.getSentOutFrom();
			Date endDate = reportCriteria.getSentOutTo();
			Calendar cStart = Calendar.getInstance();
			Calendar cEnd = Calendar.getInstance();
			cStart.setTime(startDate);
			cEnd.setTime(endDate);
			
			reportCriteria.setSentOutTo(DateUtils.addDays(reportCriteria.getSentOutTo(), 1));
			
			int sart = cStart.get(Calendar.DAY_OF_YEAR);
			int end = cEnd.get(Calendar.DAY_OF_YEAR);
			
			int diffInDays = end - sart;
			
			if(startDate.getTime()>endDate.getTime())
			{
				String errorMessage = ResourceMB.getText("wq.message.startDate");
				FacesContext.getCurrentInstance().addMessage("growl", new FacesMessage(FacesMessage.SEVERITY_ERROR,errorMessage, ""));
				return;
			}
			
			if(diffInDays > 6)
			{
				String errorMessage = ResourceMB.getText("wq.message.quoteOutputLimit");
				FacesContext.getCurrentInstance().addMessage("growl", new FacesMessage(FacesMessage.SEVERITY_ERROR,errorMessage, ""));
				return;
			}
			
			List<String> status = new ArrayList<String>();
			status.add(QuoteSBConstant.RFQ_STATUS_AQ);
			status.add(QuoteSBConstant.RFQ_STATUS_IT);
			status.add(QuoteSBConstant.RFQ_STATUS_QC);
			status.add(QuoteSBConstant.RFQ_STATUS_RIT);
			status.add(QuoteSBConstant.RFQ_STATUS_SQ);

			reportCriteria.setStatus(status);
			
			if (reportCriteria.getStage().contains(
					QuoteSBConstant.QUOTE_STAGE_DRAFT))
			{
				if (!reportCriteria.getStatus().contains(
						QuoteSBConstant.RFQ_STATUS_DQ))
				{
					reportCriteria.getStatus().add(
							QuoteSBConstant.RFQ_STATUS_DQ);
				}
			}
			else
			{
				for (int i = 0; i < reportCriteria.getStatus().size(); i++)
				{
					if (reportCriteria.getStatus().get(i)
							.equalsIgnoreCase(QuoteSBConstant.RFQ_STATUS_DQ))
					{
						reportCriteria.getStatus().remove(i);
						break;
					}
				}
			}
			
			quoteDetailLst = new ArrayList<QuoteDailyOptVo>();
			QuoteReportCriteria qcriteria = new QuoteReportCriteria();
			qcriteria.setSentOutDateFrom(reportCriteria.getSentOutFrom());
			qcriteria.setSentOutDateTo(reportCriteria.getSentOutTo());
			qcriteria.setStage(reportCriteria.getStage());
			qcriteria.setStatus(reportCriteria.getStatus());
			qcriteria.setQcPricer(reportCriteria.getUserPmName());
			qcriteria.setBizUnits(getUser().getBizUnits());
			
			List<Tuple> tuples = new ArrayList<Tuple>();
			tuples = quoteDetailOptReportSB.quoteDailyOutputSearch(
					qcriteria);
			
			//START PROCESSING TUPLES
			Set<String> qcPricers = new HashSet<String>();
			for(int i = 0 ; i < tuples.size() ; i++)
			{
				qcPricers.add((String) tuples.get(i).get(0));
			}
			List<String> qcList = new ArrayList(qcPricers);

			quoteDailyReportVos = new ArrayList<QuoteDailyReportVo>();
			for (String qc : qcList)
			{
				QuoteDailyReportVo quoteDailyReportVo = new QuoteDailyReportVo();
				quoteDailyReportVo.setQcPricer(qc);
				
				SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
				String firstDate = sdf.format(reportCriteria.getSentOutFrom());
				quoteDailyReportVo.setFirstDate(sdf.parse(firstDate));
				quoteDailyReportVos.add(quoteDailyReportVo);
			}
			
			Set<String> dateSet = new HashSet<String>();
			for(Tuple t : tuples)
			{
				String name = (String) t.get(0);
				Date date = (Date) t.get(1);
				SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
				String dateString = sdf.format(date);
				dateSet.add(dateString);
				
				for(QuoteDailyReportVo vo : quoteDailyReportVos)
				{
					if (vo.getQcPricer().equals(name)) {
						
						if(vo.getDateMap()!=null)
						{
							if(!vo.getDateMap().containsKey(dateString))
							{
								Map<String,Integer> dateMap = vo.getDateMap();
								dateMap.put(dateString, 1);
								vo.setDateMap(dateMap);
							}
							else
							{
								Map<String,Integer> dateMap = vo.getDateMap();
								dateMap.put(dateString, dateMap.get(dateString) + 1);
								vo.setDateMap(dateMap);
							}
						}
						else
						{
							Map<String,Integer> dateMap = new HashMap<String,Integer>();
							dateMap.put(dateString, 1);
							vo.setDateMap(dateMap);
						}
			        }
				}
			}
			
			for(QuoteDailyReportVo vo : quoteDailyReportVos)
			{
				Map sortedMap = new TreeMap();
			    sortedMap.putAll(vo.getDateMap());
			    vo.setDateMap(sortedMap);
			}
			
			Date day1 = reportCriteria.getSentOutFrom();
			
			//SimpleDateFormat sdf = new SimpleDateFormat(resourceMB.getLocalizedDatePattern("dd/MM/yy"));
			SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
			
			String day1String = sdf.format(day1);
			String day2String = sdf.format(DateUtils.addDays(day1, 1));
			String day3String = sdf.format(DateUtils.addDays(day1, 2));
			String day4String = sdf.format(DateUtils.addDays(day1, 3));
			String day5String = sdf.format(DateUtils.addDays(day1, 4));
			String day6String = sdf.format(DateUtils.addDays(day1, 5));
			String day7String = sdf.format(DateUtils.addDays(day1, 6));
	
			col1 = day1String;
			col2 = day2String;
			col3 = day3String;
			col4 = day4String;
			col5 = day5String;
			col6 = day6String;
			col7 = day7String;
			total = "Total";
			
			///
			if ( quoteDailyReportVos != null && quoteDailyReportVos.size() > 0)
			{
				QuoteDailyReportVo vo = new QuoteDailyReportVo();
				vo.setQcPricer("Total");
				int c1 = 0, c2 = 0, c3 = 0, c4 = 0, c5 = 0, c6 = 0, c7 = 0, tlt = 0;
				for (QuoteDailyReportVo v : quoteDailyReportVos)
				{
					if (v != null)
					{
						c1 += v.getCol1();
						c2 += v.getCol2();
						c3 += v.getCol3();
						c4 += v.getCol4();
						c5 += v.getCol5();
						c6 += v.getCol6();
						c7 += v.getCol7();
						tlt += v.getTotal();
					}
				}
				vo.setCol1(c1);
				vo.setCol2(c2);
				vo.setCol3(c3);
				vo.setCol4(c4);
				vo.setCol5(c5);
				vo.setCol6(c6);
				vo.setCol7(c7);
				vo.setTotal(tlt);
				vo.setIsTotalRow(true);
				quoteDailyReportVos.add(vo);
			}
			///
			LOG.info("search daily quote output report criteria ended.");
		}
		catch (Exception e)
		{
			LOG.log(Level.SEVERE,"search Daily Quote Output Report with criteria failed! User Name : "+reportCriteria.getUserPmName()+" , Sent out from : "+reportCriteria.getSentOutFrom()+" , Sent out from  : "+reportCriteria.getSentOutTo()+ e.getMessage(),e);
			
		}
	}

	
	public List<QuoteDailyReportVo> getQuoteDailyReportVos() {		
		return quoteDailyReportVos;
	}

	public void setQuoteDailyReportVos(List<QuoteDailyReportVo> quoteDailyReportVos) {
		this.quoteDailyReportVos = quoteDailyReportVos;
	}

	public List<QuoteDailyReportVo> getFilteredQuoteDailyReportVos() {
		return filteredQuoteDailyReportVos;
	}

	public void setFilteredQuoteDailyReportVos(
			List<QuoteDailyReportVo> filteredQuoteDailyReportVos) {
		this.filteredQuoteDailyReportVos = filteredQuoteDailyReportVos;
	}

	public String getCol1() {
		return col1;
	}

	public void setCol1(String col1) {
		this.col1 = col1;
	}

	public String getCol2() {
		return col2;
	}

	public void setCol2(String col2) {
		this.col2 = col2;
	}

	public String getCol3() {
		return col3;
	}

	public void setCol3(String col3) {
		this.col3 = col3;
	}

	public String getCol4() {
		return col4;
	}

	public void setCol4(String col4) {
		this.col4 = col4;
	}

	public String getCol5() {
		return col5;
	}

	public void setCol5(String col5) {
		this.col5 = col5;
	}

	public String getCol6() {
		return col6;
	}

	public void setCol6(String col6) {
		this.col6 = col6;
	}

	public String getCol7() {
		return col7;
	}
	
	public void setCol7(String col7) {
		this.col7 = col7;
	}

	public String getTotal()
	{
		return total;
	}

	public void setTotal(String total)
	{
		this.total = total;
	}
	
	public List<String> autoCompletePM(String key)
	{
		List<String> rltLst = new ArrayList<String>();
		List<User> userList = userSB.findUserWithKeywordAndRoles(key, new String[]{"ROLE_QC_PRICING","ROLE_QC_OPERATION"}, user.getDefaultBizUnit());
		for (User usr : userList)
		{
			rltLst.add(usr.getName());
		}

		return rltLst;
	}
	
	public void postProcessXLS(Object document)
	{
		String[] columns = { "No.", "Quoted By", col1,col2,col3,col4,col5,col6,col7,total};
		HSSFWorkbook wb = (HSSFWorkbook) document;
		HSSFSheet sheet = wb.getSheetAt(0);
		HSSFRow header = sheet.getRow(0);
		int i = 0;
		for (String column : columns)
		{
			header.getCell(i++).setCellValue(column);
		}
	}
	/**
	 * Setter for resourceMB
	 * @param resourceMB ResourceMB
	 * */
	public void setResourceMB(final ResourceMB resourceMB) {
		this.resourceMB = resourceMB;
	}
}