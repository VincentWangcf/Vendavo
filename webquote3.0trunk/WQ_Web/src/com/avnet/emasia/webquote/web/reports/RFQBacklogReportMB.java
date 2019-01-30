package com.avnet.emasia.webquote.web.reports;

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

import com.avnet.emasia.webquote.dp.EJBCommonSB;
import com.avnet.emasia.webquote.entity.Role;
import com.avnet.emasia.webquote.entity.User;
import com.avnet.emasia.webquote.quote.ejb.QuoteSB;
import com.avnet.emasia.webquote.quote.ejb.constant.QuoteSBConstant;
import com.avnet.emasia.webquote.reports.ejb.RFQBacklogReportSB;
import com.avnet.emasia.webquote.reports.vo.RFQBacklogReportVo;
import com.avnet.emasia.webquote.user.ejb.RoleSB;
import com.avnet.emasia.webquote.user.ejb.TeamSB;
import com.avnet.emasia.webquote.user.ejb.UserSB;
import com.avnet.emasia.webquote.utilites.resources.ResourceMB;
import com.avnet.emasia.webquote.utilities.common.Constants;

@ManagedBean
@SessionScoped
public class RFQBacklogReportMB implements Serializable
{

	/** 
	 * 
	 */
	private static final long serialVersionUID = -4118885974834735643L;
	private static final Logger LOG = Logger.getLogger(RFQBacklogReportMB.class
			.getName());
	// private static final String ROLE_QC_PRICING = "ROLE_QC_PRICING";
	private String exportFileName;
	private RFQBacklogReportVo reportCriteria;
	private List<RFQBacklogReportVo> rfqBacklogReportLst;
	private List<RFQBacklogReportVo> filteredRfqBacklogReportLst;

	@EJB
	private RFQBacklogReportSB rfqBacklogReportSB;

	@EJB
	private RoleSB roleSB;

	@EJB
	private TeamSB teamSB;

	@EJB
	private UserSB userSB;

	@EJB
	private QuoteSB quoteSB;
	
	@EJB
	protected EJBCommonSB ejbCommonSB;

	
	public List<RFQBacklogReportVo> getFilteredRfqBacklogReportLst() {
		return filteredRfqBacklogReportLst;
	}

	public void setFilteredRfqBacklogReportLst(
			List<RFQBacklogReportVo> filteredRfqBacklogReportLst) {
		this.filteredRfqBacklogReportLst = filteredRfqBacklogReportLst;
	}

	public String getExportFileName() {
		Date time = new Date();
		return ResourceMB.getDefaultText("wq.header.RFQReport")+"_"+time.toString();
	}

	public void setExportFileName(String exportFileName) {
		this.exportFileName = exportFileName;
	}

	public RFQBacklogReportVo getReportCriteria()
	{
		return reportCriteria;
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

	public void getReport()
	{
		rfqBacklogReportLst = rfqBacklogReportSB.searchReport(reportCriteria);
	}

	public void resetCriteria()
	{
		reportCriteria.setTeam("");
		reportCriteria.setSalemanName("");
		reportCriteria.setFormNo("");
		reportCriteria.setQcPricer("");
		reportCriteria.setPendingTime("");
	}

	@PostConstruct
	public void constructCriteria()
	{
		reportCriteria = new RFQBacklogReportVo();
		reportCriteria = ejbCommonSB.constructCriteria(reportCriteria, 6, this);
	}

	public void reportOfflineSearch()
	{

	}


	public void reportSearch()
	{
		String searchRoleType="";
		String roleName = null;
		try
		{
			List<Role> roleLst = ejbCommonSB.getUser().getRoles();
			roleName = roleLst.get(0).getName();

			if (roleName.contains("ROLE_SALES")
					|| roleName.contains("ROLE_INSIDE_SALES")
					|| roleName.contains("ROLE_SALES_GM")
					|| roleName.contains("ROLE_SALES_DIRECTOR")
					|| roleName.contains("ROLE_PM"))
			{
				searchRoleType=Constants.BACKLOG_SEARCH_RPT_ROLE_TYPE_SALES;
				//searchForSale();
			}
			else if (roleName.contains("ROLE_SALES_MANAGER"))
			{
				searchRoleType=Constants.BACKLOG_SEARCH_RPT_ROLE_TYPE_MGR;
				//rfqBacklogReportLst = searchForManager(reportCriteria);
			}
			else if (roleName.contains("ROLE_QC_PRICING")
					|| (roleName.contains("ROLE_QC_OPERATION")
					|| roleName.contains("ROLE_QCP_MANAGER")
					|| roleName.contains("ROLE_QCO_MANAGER") 
					|| roleName.contains("ROLE_QC_Director")))
			{
				searchRoleType=Constants.BACKLOG_SEARCH_RPT_ROLE_TYPE_QC;
				//rfqBacklogReportLst = searchForQc(reportCriteria);
			}
			rfqBacklogReportLst=searchCriteriaReportByRole(reportCriteria, searchRoleType);
		}
		catch (Exception e)
		{
			LOG.log(Level.SEVERE, "Report search failed for role name : "+roleName+" , error message"+e.getMessage(), e);
		}
	}

	/*public void searchForSale()
	{
		try
		{
			LOG.info("search sales' report... ");
			rfqBacklogReportLst = rfqBacklogReportSB
					.searchReportSale(reportCriteria);
			LOG.info("search sales'report ended.");
		}
		catch (Exception e)
		{
			
			LOG.severe("search sales' report failed!");
		}
	}*/

	/*Search Gateway*/
	public void searchCriteriaForReport()
	{
		String searchRoleType="";
		StringBuffer buffer = new StringBuffer();
		try
		{
			List<Role> roleLst = ejbCommonSB.getUser().getRoles();
			List<String> roleList = new ArrayList<String>();
			for(int i = 0 ; i < ejbCommonSB.getUser().getRoles().size() ; i++)
			{
				roleList.add(roleLst.get(i).getName());
				String roleName = roleLst.get(i).getName();
				buffer.append(roleName);
				buffer.append(" , ");
				LOG.info("Search sales report with role " + roleName);
			}
			
			reportCriteria.setCurrentUser(ejbCommonSB.getUser());
			reportCriteria.setBizUnits(ejbCommonSB.getUser().getBizUnits());
			
			if (roleList.contains("ROLE_QC_PRICING")
					|| (roleList.contains("ROLE_QC_OPERATION")
							|| roleList.contains("ROLE_QCO_MANAGER") 
							|| roleList.contains("ROLE_QC_Director")))
			{
				searchRoleType=Constants.BACKLOG_SEARCH_RPT_ROLE_TYPE_QC;
				//rfqBacklogReportLst = searchForQc(reportCriteria);
			}
			else if (roleList.contains("ROLE_SALES")
					|| roleList.contains("ROLE_INSIDE_CS")
					|| roleList.contains("ROLE_INSIDE_SALES")
					|| roleList.contains("ROLE_SALES_GM")
					|| roleList.contains("ROLE_SALES_DIRECTOR")
					|| roleList.contains("ROLE_PM"))
			{
				searchRoleType=Constants.BACKLOG_SEARCH_RPT_ROLE_TYPE_SALES;
				//rfqBacklogReportLst = searchForSale(reportCriteria);
			}
			else if (roleList.contains("ROLE_SALES_MANAGER")) 
			{
				searchRoleType=Constants.BACKLOG_SEARCH_RPT_ROLE_TYPE_MGR;
				//rfqBacklogReportLst = searchForManager(reportCriteria);
			}
			else 
				filteredRfqBacklogReportLst = null;
			rfqBacklogReportLst = searchCriteriaReportByRole(reportCriteria,searchRoleType);
			LOG.info("search sales' report with criteria  ended.");
		}
		catch (Exception e)
		{
			LOG.log(Level.SEVERE,"search sales' report with criteria  failed! for following roles :"+buffer.toString()+" , Error Message"+ e.getMessage(),e);
		}
	}

	/*
	public List<RFQBacklogReportVo> searchForSale(RFQBacklogReportVo rtCriteria)
			throws Exception
			{
		List<RFQBacklogReportVo> resultLst = null;
		resultLst = rfqBacklogReportSB.searchCriteriaReportSale(rtCriteria);
		return resultLst;
			}

	public List<RFQBacklogReportVo> searchForManager(
			RFQBacklogReportVo rtCriteria) throws Exception
			{
		List<RFQBacklogReportVo> resultLst = null;
		resultLst = rfqBacklogReportSB.searchCriteriaReportManager(rtCriteria);
		return resultLst;
			}

	public List<RFQBacklogReportVo> searchForQc(RFQBacklogReportVo rtCriteria)
			throws Exception
			{
		List<RFQBacklogReportVo> resultLst = null;
		resultLst = rfqBacklogReportSB.searchCriteriaReportQc(rtCriteria);
		return resultLst;
			}
	*/
	
	// by Alex Xu on 22 Oct 2014 for INC0040301
	public List<RFQBacklogReportVo> searchCriteriaReportByRole(RFQBacklogReportVo rtCriteria,String roleType)
				{
		return rfqBacklogReportSB.searchCriteriaReportByRole(rtCriteria,roleType);
	}

	public List<String> autoCompleteTeam(String key)
	{
		return ejbCommonSB.autoCompleteTeam(key,this.teamSB);
	}

	public List<String> autoCompleteSale(String key)
	{
		return ejbCommonSB.autoCompleteSale(key,this.userSB);
	}

	public List<String> autoCompleteQCP(String key)
	{
		List<User> userList = userSB.findUserWithKeywordAndRoles(key, new String[]{QuoteSBConstant.ROLE_QC_PRICING}, reportCriteria.getCurrentUser().getDefaultBizUnit());
		List<String> resultLst = new ArrayList<String>();

		for (User user : userList)
		{
			resultLst.add(user.getName());
		}

		return resultLst;
	}

	public List<String> autoCompleteFormNo(String key)
	{
		return ejbCommonSB.autoCompleteFormNo(key,this.quoteSB);
	}
	
	public void postProcessXLS(Object document)
	{
		String[] columns = { "No.", "Team", "Salesman Name", "Form No",
				"Your Reference", "Customer Name", "End Customer Name", "OOH",
				"MFR", "Full MFR Part", "QC Handler", "Pending Time (In Days)",
				"Pending",  "Pending For",
				"Pending Internal Transferred Time (In Days)", "BUM", "PM" };
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
	 * @param args
	 */
	public static void main(String[] args)
	{
		// TODO Auto-generated method stub

	}

	
	
}