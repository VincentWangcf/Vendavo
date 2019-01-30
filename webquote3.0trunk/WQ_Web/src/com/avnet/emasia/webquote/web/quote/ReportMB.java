package com.avnet.emasia.webquote.web.quote;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.text.DateFormat;
import java.text.ParseException;
import com.avnet.emasia.webquote.utilities.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.mail.MessagingException;
import javax.naming.NamingException;

import org.apache.poi.xssf.streaming.SXSSFWorkbook;

import com.avnet.emasia.webquote.entity.Manufacturer;
import com.avnet.emasia.webquote.entity.Team;
import com.avnet.emasia.webquote.entity.User;
import com.avnet.emasia.webquote.quote.ejb.FiscalMappingSB;
import com.avnet.emasia.webquote.quote.ejb.SystemCodeMaintenanceSB;
import com.avnet.emasia.webquote.quote.ejb.constant.QuoteSBConstant;
import com.avnet.emasia.webquote.quote.vo.ReportSearchCriteria;
import com.avnet.emasia.webquote.reports.vo.ColumnModelVo;
import com.avnet.emasia.webquote.reports.vo.ReportGroupVo;
import com.avnet.emasia.webquote.user.ejb.TeamSB;
import com.avnet.emasia.webquote.user.ejb.UserSB;
import com.avnet.emasia.webquote.utilites.resources.ResourceMB;
import com.avnet.emasia.webquote.utilites.web.common.CacheUtil;
import com.avnet.emasia.webquote.utilites.web.util.ExcelUtil;
import com.avnet.emasia.webquote.utilites.web.util.QuoteUtil;
import com.avnet.emasia.webquote.utilities.bean.MailInfoBean;
import com.avnet.emasia.webquote.utilities.common.OSTimeZone;
import com.avnet.emasia.webquote.utilities.common.SysConfigSB;
import com.avnet.emasia.webquote.utilities.ejb.MailUtilsSB;
import com.avnet.emasia.webquote.web.quote.cache.MfrCacheManager;
import com.avnet.emasia.webquote.web.quote.constant.QuoteConstant;
import com.avnet.emasia.webquote.web.user.UserInfo;

@ManagedBean
@SessionScoped
public class ReportMB implements Serializable {

	/**
	 * 
	 */
	private static final Logger LOG = Logger.getLogger(ReportMB.class.getName());
	private static final long serialVersionUID = 642360478380712067L;
	
	@EJB
	UserSB userSB;
	
	@EJB
	TeamSB teamSB;
	
	@EJB
	SysConfigSB sysConfigSB;
	
	@EJB
	FiscalMappingSB fiscalMappingSB;
	
	@EJB
	protected MailUtilsSB mailUtilsSB;
	
	@EJB
	private SystemCodeMaintenanceSB sysCodeMaintSB;
	
	@EJB
	private CacheUtil cacheUtil;

	SelectItem[] salesMgrSelectList;
	SelectItem[] salesTeamSelectList;
	SelectItem[] salesUserSelectList;
	SelectItem[] mfrSelectList;
	SelectItem[] quoteHandlerSelectList;
	SelectItem[] bizUnitSelectList;
	SelectItem[] quotePeriodSelectList;
	
	
	public final static String QUOTE_TYPE_YEAR = "Fiscal Year";
	public final static String QUOTE_TYPE_QTR = "Fiscal Qtr";
	public final static String QUOTE_TYPE_MONTH = "Fiscal Month";

	public final static String HEADER_QUOTE_PERIOD = "Quote Period";
	public final static String HEADER_SALES_MGR = "Sales Mgr";
	public final static String HEADER_SALES_TEAM = "Sales Team (Sales Office)";
	public final static String HEADER_SALES_USER = "Sales";
	public final static String HEADER_CUSTOMER = "Customer";
	public final static String HEADER_CUSTOMER_NUM = "Sold To code";
	public final static String HEADER_CUSTOMER_NAME = "Sold to party";
	public final static String HEADER_MFR = "MFR";
	public final static String HEADER_PRODUCT_GRP_1 = "Product Group 1";
	public final static String HEADER_PRODUCT_GRP_2 = "Product Group 2";
	public final static String HEADER_PRODUCT_GRP_3 = "Product Group 3";
	public final static String HEADER_PRODUCT_GRP_4 = "Product Group 4";
	public final static String HEADER_QUOTE_HANDLER = "Quote Handler";
	public final static String HEADER_BU = "BU (Region)";
	public final static String HEADER_PN = "PN";

	public final static String GRP_COL_QUOTE_PERIOD = "quotedPeriod";
	public final static String GRP_COL_SALES_MGR = "salesMgr";
	public final static String GRP_COL_SALES_TEAM = "salesTeam";
	public final static String GRP_COL_SALES_USER = "salesUser";
	public final static String GRP_COL_CUSTOMER_NUM = "customer";
	public final static String GRP_COL_CUSTOMER_NAME = "customerName";
	public final static String GRP_COL_MFR = "mfr";
	public final static String GRP_COL_PRODUCT_GRP_1 = "productGrp1";
	public final static String GRP_COL_PRODUCT_GRP_2 = "productGrp2";
	public final static String GRP_COL_PRODUCT_GRP_3 = "productGrp3";
	public final static String GRP_COL_PRODUCT_GRP_4 = "productGrp4";
	public final static String GRP_COL_QUOTE_HANDLER = "quoteHandler";
	public final static String GRP_COL_BU = "bizUnit";
	public final static String GRP_COL_PN = "quotedPartNumber";
	
	public void changePullDownMenu(String bu) {
		// get sales manager list information by role is sales managers
		//For Sales Mgr, only users in ��Role_Sales_Mgr�� and accessible to current selected BU(Region) could be selected. Default value = ��ALL��
		List<String> roleMgr = new ArrayList<String>();
		roleMgr.add( QuoteSBConstant.ROLE_SALES_MANAGER);
		List<User> users = userSB.findUserByRoleAndBu(roleMgr, bu);
		List<String> mgrs = new ArrayList<String>();
		if (users != null) {
			for (User mgr : users)
				mgrs.add(mgr.getName());
		}
		this.salesMgrSelectList = QuoteUtil.createFilterOptions(
				mgrs.toArray(new String[mgrs.size()]), false, true,false);

		// get sales Team list information by role is sales managers
		List<Team> teamLst = teamSB.findByBizUnits(bu);
		List<String> teams = new ArrayList<String>();
		if (teamLst != null) {
			for (Team team : teamLst) {
				teams.add(team.getName());
			}
		}
		this.salesTeamSelectList = QuoteUtil.createFilterOptions(
				teams.toArray(new String[teams.size()]), false, true,false);


		// get manuafactor list information by biz unit
		//Bryan Start
//		List<Manufacturer> manufacturers = MfrCacheManager
//				.getMfrListByBizUnit(bu);
		List<Manufacturer> manufacturers = cacheUtil
				.getMfrListByBizUnit(bu);
		//Bryan End
		List<String> mfrCodes = new ArrayList<String>();
		if (manufacturers != null) {
			for (Manufacturer manufacturer : manufacturers)
				mfrCodes.add(manufacturer.getName());
		}
		this.mfrSelectList = QuoteUtil.createFilterOptions(
				mfrCodes.toArray(new String[mfrCodes.size()]),
				mfrCodes.toArray(new String[mfrCodes.size()]), false, true,false);

		// get quoteHandlerList information
		//Quote Handler: only users in (��Role_QCO�� or ��Role_QCP��) and with same default biz_unit as could be selected could be selected. Default value = ��ALL��. 
		List<String> role = new ArrayList<String>();
		role.add(QuoteSBConstant.ROLE_QC_PRICING);
		role.add(QuoteSBConstant.ROLE_QC_OPERATION);
		List<User> qhLst = userSB.findUserByRoleAndBu(role, bu);
		List<String> handlersLbl = new ArrayList<String>();
		List<String> handlersValue = new ArrayList<String>();
		if (qhLst != null) {
			for (User qh : qhLst){
				String id = String.valueOf(qh.getId());
				handlersLbl.add(qh.getName());
				handlersValue.add(id);
			}
		}
		this.quoteHandlerSelectList = QuoteUtil.createFilterOptions(
				handlersLbl.toArray(new String[qhLst.size()]),handlersValue.toArray(new String[qhLst.size()]), false, true,false);
		
		 this.changeSalesByTeamAndBu(bu,"All");

	}

	protected void setReportPeriod(ReportSearchCriteria criteria,int range) {
		try {
			Date now = new Date();
			DateFormat sdf = new SimpleDateFormat("yyyyMMdd HH:mm:ss");
			sdf.setTimeZone(OSTimeZone.getOsTimeZone());
			String[] period = fiscalMappingSB.getPeriodByDate(sdf.format(now),range);
			criteria.setQuoteFrm(sdf.parse(period[0] + " 00:00:00"));
			criteria.setQuoteTo(sdf.parse(period[1] + " 23:59:59"));
		} catch (ParseException e) {
			LOG.log(Level.SEVERE,"Error occuring during formatting the Date Biz Unit: "+criteria.getBizUnit() +" :: Date From "+criteria.getQuoteFrm()+" :: Date To "+criteria.getQuoteTo()+" :: Period Type "+criteria.getQuoteTypeFormat()+"" +e.getMessage(), e);
		}
		
		
	}

	
	public void changeSalesByTeamAndBu(String bu, String team) {
		// get sales user list information by role is sales managers
		List<String> roleSale = new ArrayList<String>();
		roleSale.add( QuoteSBConstant.ROLE_SALES);
		List<User> salesUserLst = null;
		if(null==team || "All".equals(team)) {
			salesUserLst = userSB.findUserByRoleAndBu(roleSale, bu);
		}else {
			salesUserLst = userSB.findUserByRoleAndBuTeam(roleSale, bu,team);
		}
		List<String> salesUsers = new ArrayList<String>();
		if (salesUserLst != null) {
			for (User su : salesUserLst)
				salesUsers.add(su.getName());
		}
		this.salesUserSelectList = QuoteUtil.createFilterOptions(
				salesUsers.toArray(new String[salesUsers.size()]), false, true,false);
		

	}
	
	void generateXlSAndSend(String attachedName,List<ColumnModelVo> columnLst,int grandColspanSize,List<?> objs, Object grandTotal,String reportTitle,String region) {

		String[] columnNameArr = new String[columnLst.size()];
		int i = 0;
		for (ColumnModelVo vo : columnLst) {
			columnNameArr[i] = (vo.getHeader());
			i++;
		}
		List<SXSSFWorkbook> workbooks = ExcelUtil.generateExcelFileLst(objs, attachedName,
						columnNameArr, columnLst,grandColspanSize,grandTotal);
		
		try {
			User user = UserInfo.getUser();
			sendEmail(workbooks, attachedName,user,reportTitle,region);
			Object[] objArr = {user.getName()};
			String errMsg =ResourceMB.getParameterizedString("wq.message.mailSent",objArr);
			FacesContext.getCurrentInstance().addMessage(
					null,
					new FacesMessage(FacesMessage.SEVERITY_INFO, errMsg,
							""));
		} catch (Exception e) {
			String errMsg = e.getMessage();
			FacesContext.getCurrentInstance().addMessage(
					null,
					new FacesMessage(FacesMessage.SEVERITY_ERROR, errMsg,
							""));
			LOG.log(Level.SEVERE,""+reportTitle+" Send email failed !!"  + e.getMessage(), e);
		}
		
	}
	
	


	void sendEmail(List<SXSSFWorkbook> workbooks, String fileName,User user,String reportTitle,String region)
			throws IOException {

		LOG.info("call e Report sendEmail process");
		File xlsFile = null;
		File zipFile = null;
		FileOutputStream fileOut = null;
		MailInfoBean mib = new MailInfoBean();
		List<String> toList = new ArrayList<String>();
		
		//toList.add("rashmi.singh@nectechnologies.in"); // ;user.getEmailAddress()
		toList.add(user.getEmailAddress());
		String fromEmailAddr = sysCodeMaintSB.getEmailAddress(region);
		String signature = sysCodeMaintSB.getEmailSignName(region);
		
		String tempPath = sysConfigSB.getProperyValue(QuoteConstant.TEMP_DIR);

		if (toList.size() > 0) {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmssSSS");
			String subject = reportTitle;
			mib.setMailSubject(subject);
			mib.setMailTo(toList);
			String content = "";
			content += "";
			content += "Best Regards" + "<br/>";
			content += signature + "<br/>";
			content += sysCodeMaintSB.getEmailHotLine(region) + "<br/>";
			content += "Email Box"+":" +sysCodeMaintSB.getEmailSignContent(region);
			
			
			
			mib.setMailContent(content);
			mib.setMailFrom(fromEmailAddr);
			
			
			fileName = fileName+"_"+sdf.format(new Date());
			

			try {
				for(int i=0;i<workbooks.size();i++) {
					String zipFileName = tempPath+fileName+"__"+(i+1)+"_.zip";
					String xlsFileName = tempPath+fileName+"__"+(i+1)+"_.xlsx";
					xlsFile = new File(xlsFileName);
					fileOut = new FileOutputStream(xlsFile);
					workbooks.get(i).write(fileOut);
					fileOut.close();
					zipFile = (File) QuoteUtil.doZipFile(zipFileName, xlsFileName);
					LOG.log(Level.INFO, " attached xlsx file => Zip size ==>> " + zipFile.length());
					mib.setZipFile(xlsFile);
					mailUtilsSB.sendAttachedMail(mib);
				}
				
			} catch (Exception e) {
				LOG.log(Level.SEVERE,""+reportTitle+" Send email failed!" + e.getMessage(), e);
				
			} finally {

				if (fileOut != null)
					fileOut = null;
				if (xlsFile != null) {
					if (xlsFile.exists())
						xlsFile.delete();
				}
				if (zipFile != null) {
					if (zipFile.exists())
						zipFile.delete();
				}
			}

			LOG.info("call sendEmail end");
		} else {
			LOG.info("call sendEmail end, because receiptor is empty!");
		}
		

	}
	
	public boolean validationSearchCriteria(int periodRange,ReportSearchCriteria criteria,List<ReportGroupVo> lst) {

		// validation the quote from and quote to date
		String errMsg = "";
//		Fix ticket INC0204125 to remove check date
		/*if (errMsg != null) {
			FacesContext.getCurrentInstance().addMessage(null,
					new FacesMessage(FacesMessage.SEVERITY_ERROR, errMsg, ""));
			return false;
		}*/

		// validation the quotedPeriod is existed or not
		boolean isContainPeriod = false;
		for (ReportGroupVo vo : lst) {
			if (vo.getGroupByName().equals(HEADER_QUOTE_PERIOD)) {
				isContainPeriod = true;
			}
		}
		if (!isContainPeriod) {
			errMsg = ResourceMB.getText("wq.message.quotedPeriod");
			FacesContext.getCurrentInstance().addMessage(null,
					new FacesMessage(FacesMessage.SEVERITY_ERROR, errMsg, ""));
			return false;
		}
		return true;
	}

	
	public static List<ReportGroupVo> getGroupsObj() {
		List<ReportGroupVo> source = new ArrayList<ReportGroupVo>();

		source.add(new ReportGroupVo(HEADER_QUOTE_PERIOD, 1,
				GRP_COL_QUOTE_PERIOD));
		source.add(new ReportGroupVo(HEADER_SALES_MGR, 2, GRP_COL_SALES_MGR));
		source.add(new ReportGroupVo(HEADER_SALES_TEAM, 3,
				GRP_COL_SALES_TEAM));
		source.add(new ReportGroupVo(HEADER_SALES_USER, 4,
				GRP_COL_SALES_USER));
		source.add(new ReportGroupVo(HEADER_CUSTOMER, 5,
				GRP_COL_CUSTOMER_NUM));
		source.add(new ReportGroupVo(HEADER_MFR, 6, GRP_COL_MFR));
		source.add(new ReportGroupVo(HEADER_PRODUCT_GRP_1, 7,
				GRP_COL_PRODUCT_GRP_1));
		source.add(new ReportGroupVo(HEADER_PRODUCT_GRP_2, 8,
				GRP_COL_PRODUCT_GRP_2));
		source.add(new ReportGroupVo(HEADER_PRODUCT_GRP_3, 9,
				GRP_COL_PRODUCT_GRP_3));
		source.add(new ReportGroupVo(HEADER_PRODUCT_GRP_4, 10,
				GRP_COL_PRODUCT_GRP_4));
		source.add(new ReportGroupVo(HEADER_QUOTE_HANDLER, 11,
				GRP_COL_QUOTE_HANDLER));
		source.add(new ReportGroupVo(HEADER_BU, 12, GRP_COL_BU));
		source.add(new ReportGroupVo(HEADER_PN, 13, GRP_COL_PN));
		return source;
	}

	
	/**
	 * 
	 * @param grpVos
	 */
	void setSearchGroupShowValue(List<ReportGroupVo> grpVos,ReportSearchCriteria criteria) {
		
		//step 1, set the display to false 
		criteria.setShowQuotePeriod(false);
		criteria.setShowSalesMgr(false);
		criteria.setShowSalesTeam(false);
		criteria.setShowSalesUser(false);
		criteria.setShowCustomer(false);
		criteria.setShowmfr(false);
		criteria.setShowProductGrp1(false);
		criteria.setShowProductGrp2(false);
		criteria.setShowProductGrp3(false);
		criteria.setShowProductGrp4(false);
		criteria.setShowQuoteHandler(false);
		criteria.setShowBizUnit(false);
		criteria.setShowPN(false);
		
		//setp 2 set the display according to pick list
		for (ReportGroupVo vo : grpVos) {
			if (vo.getGroupByName().equals(HEADER_QUOTE_PERIOD)) {
				criteria.setShowQuotePeriod(true);
			} else if (vo.getGroupByName().equals(HEADER_SALES_MGR)) {
				criteria.setShowSalesMgr(true);
			} else if (vo.getGroupByName().equals(HEADER_SALES_TEAM)) {
				criteria.setShowSalesTeam(true);
			} else if (vo.getGroupByName().equals(HEADER_SALES_USER)) {
				criteria.setShowSalesUser(true);
			} else if (vo.getGroupByName().equals(HEADER_CUSTOMER)) {
				criteria.setShowCustomer(true);
			} else if (vo.getGroupByName().equals(HEADER_MFR)) {
				criteria.setShowmfr(true);
			} else if (vo.getGroupByName().equals(HEADER_PRODUCT_GRP_1)) {
				criteria.setShowProductGrp1(true);
			} else if (vo.getGroupByName().equals(HEADER_PRODUCT_GRP_2)) {
				criteria.setShowProductGrp2(true);
			} else if (vo.getGroupByName().equals(HEADER_PRODUCT_GRP_3)) {
				criteria.setShowProductGrp3(true);
			} else if (vo.getGroupByName().equals(HEADER_PRODUCT_GRP_4)) {
				criteria.setShowProductGrp4(true);
			} else if (vo.getGroupByName().equals(HEADER_QUOTE_HANDLER)) {
				criteria.setShowQuoteHandler(true);
			} else if (vo.getGroupByName().equals(HEADER_BU)) {
				criteria.setShowBizUnit(true);
			} else if (vo.getGroupByName().equals(HEADER_PN)) {
				criteria.setShowPN(true);
			}

		}

	}




	public UserSB getUserSB() {
		return userSB;
	}




	public void setUserSB(UserSB userSB) {
		this.userSB = userSB;
	}




	public TeamSB getTeamSB() {
		return teamSB;
	}




	public void setTeamSB(TeamSB teamSB) {
		this.teamSB = teamSB;
	}




	public MailUtilsSB getMailUtilsSB() {
		return mailUtilsSB;
	}




	public void setMailUtilsSB(MailUtilsSB mailUtilsSB) {
		this.mailUtilsSB = mailUtilsSB;
	}




	public SystemCodeMaintenanceSB getSysCodeMaintSB() {
		return sysCodeMaintSB;
	}




	public void setSysCodeMaintSB(SystemCodeMaintenanceSB sysCodeMaintSB) {
		this.sysCodeMaintSB = sysCodeMaintSB;
	}




	public SelectItem[] getSalesMgrSelectList() {
		return salesMgrSelectList;
	}




	public void setSalesMgrSelectList(SelectItem[] salesMgrSelectList) {
		this.salesMgrSelectList = salesMgrSelectList;
	}




	public SelectItem[] getSalesTeamSelectList() {
		return salesTeamSelectList;
	}




	public void setSalesTeamSelectList(SelectItem[] salesTeamSelectList) {
		this.salesTeamSelectList = salesTeamSelectList;
	}




	public SelectItem[] getSalesUserSelectList() {
		return salesUserSelectList;
	}




	public void setSalesUserSelectList(SelectItem[] salesUserSelectList) {
		this.salesUserSelectList = salesUserSelectList;
	}




	public SelectItem[] getMfrSelectList() {
		return mfrSelectList;
	}




	public void setMfrSelectList(SelectItem[] mfrSelectList) {
		this.mfrSelectList = mfrSelectList;
	}




	public SelectItem[] getQuoteHandlerSelectList() {
		return quoteHandlerSelectList;
	}




	public void setQuoteHandlerSelectList(SelectItem[] quoteHandlerSelectList) {
		this.quoteHandlerSelectList = quoteHandlerSelectList;
	}




	public SelectItem[] getBizUnitSelectList() {
		return bizUnitSelectList;
	}




	public void setBizUnitSelectList(SelectItem[] bizUnitSelectList) {
		this.bizUnitSelectList = bizUnitSelectList;
	}




	public SelectItem[] getQuotePeriodSelectList() {
		return quotePeriodSelectList;
	}




	public void setQuotePeriodSelectList(SelectItem[] quotePeriodSelectList) {
		this.quotePeriodSelectList = quotePeriodSelectList;
	}
	

}
