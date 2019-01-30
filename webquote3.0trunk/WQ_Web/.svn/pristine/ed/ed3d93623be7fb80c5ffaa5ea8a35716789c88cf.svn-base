package com.avnet.emasia.webquote.web.reports;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;

import org.jboss.logmanager.Level;
import org.primefaces.context.RequestContext;
import org.primefaces.event.RowEditEvent;
import org.springframework.security.core.context.SecurityContextHolder;
import com.avnet.emasia.webquote.entity.BizUnit;
import com.avnet.emasia.webquote.entity.ReportRecipient;
import com.avnet.emasia.webquote.entity.User;
import com.avnet.emasia.webquote.reports.ejb.DailyRITReportSB;
import com.avnet.emasia.webquote.reports.vo.DailyRITVo;
import com.avnet.emasia.webquote.user.ejb.UserSB;
import com.avnet.emasia.webquote.utilites.resources.ResourceMB;
import com.avnet.emasia.webquote.utilites.web.common.RecipientMaintenanceModel;
import com.avnet.emasia.webquote.web.quote.constant.QuoteConstant;
import com.avnet.emasia.webquote.web.security.WQUserDetails;

@ManagedBean
@SessionScoped
public class RecipientMaintenanceMB implements Serializable
{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static final Logger LOG = Logger
			.getLogger(RecipientMaintenanceMB.class.getName());

	private final static String BACK_HOME_PATH = "/Report/RecipientMaintenance?faces-redirect=true";

	private ReportRecipient[] selectedRecipient;
	private RecipientMaintenanceModel pModel;
	private SelectItem[] reportTypeSelectList;
	private ReportRecipient newRecipient;
	private String userNameStr;
	private List<ReportRecipient> recipientsInfoLst = new ArrayList<ReportRecipient>();
	private User user = ((WQUserDetails) SecurityContextHolder.getContext()
			.getAuthentication().getPrincipal()).getUser();

	@EJB
	private DailyRITReportSB dailyRITReportSB;

	@EJB
	private UserSB userSB;

	private List<User> searchUserLst;
	private List<User> selectedUser;

	public ReportRecipient[] getSelectedRecipient()
	{
		return selectedRecipient;
	}

	public void setSelectedRecipient(ReportRecipient[] selectedRecipient)
	{
		this.selectedRecipient = selectedRecipient;
	}

	public String getUserNameStr()
	{
		return userNameStr;
	}

	public void setUserNameStr(String userNameStr)
	{
		this.userNameStr = userNameStr;
	}

	public List<User> getSelectedUser()
	{
		return selectedUser;
	}

	public void setSelectedUser(List<User> selectedUser)
	{
		this.selectedUser = selectedUser;
	}

	public List<User> getSearchUserLst()
	{
		return searchUserLst;
	}

	public void setSearchUserLst(List<User> searchUserLst)
	{
		this.searchUserLst = searchUserLst;
	}

	public SelectItem[] getReportTypeSelectList()
	{
		return reportTypeSelectList;
	}

	public void setReportTypeSelectList(SelectItem[] reportTypeSelectList)
	{
		this.reportTypeSelectList = reportTypeSelectList;
	}

	public ReportRecipient getNewRecipient()
	{
		return newRecipient;
	}

	public void setNewRecipient(ReportRecipient newRecipient)
	{
		this.newRecipient = newRecipient;
	}

	public RecipientMaintenanceModel getpModel()
	{
		return pModel;
	}

	public void setpModel(RecipientMaintenanceModel pModel)
	{
		this.pModel = pModel;
	}

	@PostConstruct
	public void initRcptMB()
	{
		LOG.info("initializing recipient been | RecipientMaintenanceMB");
		try
		{
			String regionStr = user.getDefaultBizUnit().getName();
			recipientsInfoLst = dailyRITReportSB
					.findRecipientsInfo(QuoteConstant.DAILY_RIT_REPORT, regionStr);
			recipientsInfoLst.addAll(dailyRITReportSB
					.findRecipientsInfo(QuoteConstant.OUTSTANDING_IT_REPORT, regionStr));
			recipientsInfoLst
					.addAll(dailyRITReportSB
							.findRecipientsInfo(QuoteConstant.SALES_QUOTATION_PERFORMANCE, regionStr));
		}
		catch (Exception e)
		{
			LOG.log(Level.SEVERE, "get recipients info failed for user :  "+user.getDefaultBizUnit().getName()+", Exception message" + e.getMessage(), e);
		}
		pModel = new RecipientMaintenanceModel(recipientsInfoLst);
		initNewRecipient();
		reportTypeSelectList = initReportType();
		newRecipient.setReportType("");
		selectedUser = null;
		selectedRecipient = null;
		userNameStr = "";
		searchUser();
	}

	private SelectItem[] initReportType()
	{
		SelectItem[] options = new SelectItem[QuoteConstant.REPORT_TYPE_ARR.length + 1];
		options[0] = new SelectItem(QuoteConstant.DEFAULT_SELECT_VALUE,
				QuoteConstant.DEFAULT_SELECT);
		for (int i = 0; i < QuoteConstant.REPORT_TYPE_ARR.length; i++)
		{
			options[i + 1] = new SelectItem(QuoteConstant.REPORT_TYPE_ARR[i],
					QuoteConstant.REPORT_TYPE_ARR[i]);
		}
		if (options.length > 0)
			options[0].setNoSelectionOption(true);
		return options;
	}

	public List<ReportRecipient> getRecipientsInfoLst()
	{
		return recipientsInfoLst;
	}

	public void setRecipientsInfoLst(List<ReportRecipient> recipientsInfoLst)
	{
		this.recipientsInfoLst = recipientsInfoLst;
	}

	/**
	 * initializate add property bean.
	 */
	public void initNewRecipient()
	{
		newRecipient = new ReportRecipient();
		User user = new User();
		newRecipient.setUser(user);
	}

	/**
	 * Deletes recipient from db.
	 * 
	 * @return
	 */
	public void deleteRecipient()
	{
		try
		{
			if (selectedRecipient == null || selectedRecipient.length == 0)
			{
				FacesMessage msg = new FacesMessage(
						FacesMessage.SEVERITY_ERROR,
						ResourceMB.getText("wq.message.selRecepient")+"!", ResourceMB.getText("wq.message.selRecepient")+"!");
				FacesContext.getCurrentInstance().addMessage("messageGrowl",
						msg);
				return;
			}
			dailyRITReportSB.deleteRecipient(selectedRecipient);
			LOG.info("delete recipient successfully!");
			initRcptMB();
			backToHome(BACK_HOME_PATH);
		}
		catch (Exception e)
		{
			LOG.log(Level.SEVERE, "delete recipient failed! Exception message: "+ e.getMessage(), e);
			FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR,
					ResourceMB.getText("wq.message.deleteRecepientFailed")+"!", ResourceMB.getText("wq.message.innerErrors")+"!");
			FacesContext.getCurrentInstance().addMessage("messageGrowl", msg);
			
		}
	}

	/**
	 * cancel edit property operation.
	 * 
	 * @param event
	 */
	public void onCancel(RowEditEvent event)
	{
		FacesMessage msg = new FacesMessage(ResourceMB.getText("wq.message.propertyCancelled"),
				((ReportRecipient) event.getObject()).getReportType());
		FacesContext.getCurrentInstance().addMessage(null, msg);
	}

	public void backToHome(String homePth)
	{
		FacesContext f = FacesContext.getCurrentInstance();
		f.getApplication().getNavigationHandler()
				.handleNavigation(f, null, homePth);
	}

	public void searchUserByName()
	{
		BizUnit bizUnit = user.getDefaultBizUnit();
		List<BizUnit> bizUnitLst = new ArrayList<BizUnit>();
		bizUnitLst.add(bizUnit);
		if (userNameStr == null || userNameStr.equals(""))
		{
			userNameStr = "_";
		}
		searchUserLst = userSB.findAutoCompleteUser(userNameStr, bizUnitLst);
	}

	public void searchUser()
	{
		BizUnit bizUnit = user.getDefaultBizUnit();
		List<BizUnit> bizUnitLst = new ArrayList<BizUnit>();
		bizUnitLst.add(bizUnit);
		searchUserLst = userSB.findAutoCompleteUser("_", bizUnitLst);
	}

	public void addRecipientInNewPanel()
	{
		RequestContext.getCurrentInstance().execute(
				"PF('recipient_dialog_widgetVar').show()");
	}

	/**
	 * 
	 */
	public void addUserToRecipient()
	{
		List<String> duplicateUserLst = new ArrayList<String>();
		BizUnit bz = user.getDefaultBizUnit();
		if (newRecipient.getReportType() == null
				|| newRecipient.getReportType().equals(
						QuoteConstant.DEFAULT_SELECT_VALUE)
				|| selectedUser == null || selectedUser.size() == 0)
		{
			FacesMessage msg = new FacesMessage(
					FacesMessage.SEVERITY_ERROR,
					ResourceMB.getText("wq.message.mandatoryFields")+"!",
					ResourceMB.getText("wq.message.repTypeUserNotNull")+".");
			FacesContext.getCurrentInstance()
					.addMessage("userDialogMsgId", msg);
			return;
		}

		if (recipientsInfoLst != null)
		{
			if (selectedUser != null)
			{
				String str = "-1";
				String str2 = "-2";
				for (User u : selectedUser)
				{
					str = u.getEmployeeId() + bz.getName()
							+ newRecipient.getReportType();
					for (ReportRecipient rp : recipientsInfoLst)
					{
						str2 = rp.getUser().getEmployeeId()
								+ rp.getBzUnit().getName() + rp.getReportType();
						if (str.equalsIgnoreCase(str2))
						{
							duplicateUserLst.add(u.getName());
						}
					}
				}
			}
		}

		if (duplicateUserLst.size() > 0)
		{
			FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR,
					ResourceMB.getText("wq.message.duplicateUser")+"!<br/>"
							+ duplicateUserLst.toString(),
					duplicateUserLst.toString());
			FacesContext.getCurrentInstance()
					.addMessage("userDialogMsgId", msg);
			return;
		}

		DailyRITVo d = new DailyRITVo();
		d.setUserLst(selectedUser);
		d.setReportType(newRecipient.getReportType());
		d.setBizUnit(bz);
		try
		{
			dailyRITReportSB.addUserToRecipient(d);
		}
		catch (Exception e)
		{
			LOG.log(Level.SEVERE,"Save recipients failed ! for report type : "+newRecipient.getReportType()+" and selected users , Exception message" + e.getMessage(),e);
			
		}

		initRcptMB();
		backToHome(BACK_HOME_PATH);
	}

	public void cancelAdd()
	{
		initRcptMB();
		backToHome(BACK_HOME_PATH);
	}

	public static void main(String[] args)
	{
	}
}