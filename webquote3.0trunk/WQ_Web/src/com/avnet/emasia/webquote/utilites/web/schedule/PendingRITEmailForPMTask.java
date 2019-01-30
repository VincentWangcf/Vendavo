package com.avnet.emasia.webquote.utilites.web.schedule;


import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ejb.Asynchronous;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.Timer;

import org.jboss.as.server.CurrentServiceContainer;
import org.jboss.msc.service.ServiceController;

import com.avnet.emasia.webquote.commodity.helper.CommodityConstant;
import com.avnet.emasia.webquote.dp.EJBCommonSB;
import com.avnet.emasia.webquote.entity.BizUnit;
import com.avnet.emasia.webquote.entity.User;
import com.avnet.emasia.webquote.exception.CommonConstants;
import com.avnet.emasia.webquote.exception.WebQuoteException;
import com.avnet.emasia.webquote.masterData.exception.CheckedException;
import com.avnet.emasia.webquote.masterData.util.StringUtils;
import com.avnet.emasia.webquote.quote.ejb.MyQuoteSearchSB;
import com.avnet.emasia.webquote.quote.ejb.QuoteSB;
import com.avnet.emasia.webquote.quote.ejb.SystemCodeMaintenanceSB;
import com.avnet.emasia.webquote.quote.ejb.constant.QuoteSBConstant;
import com.avnet.emasia.webquote.quote.vo.MyQuoteSearchCriteria;
import com.avnet.emasia.webquote.quote.vo.QuoteItemVo;
import com.avnet.emasia.webquote.user.ejb.BizUnitSB;
import com.avnet.emasia.webquote.user.ejb.UserSB;
import com.avnet.emasia.webquote.utilites.resources.ResourceMB;
import com.avnet.emasia.webquote.utilities.DateUtils;
import com.avnet.emasia.webquote.utilities.MessageFormatorUtil;
import com.avnet.emasia.webquote.utilities.bean.MailInfoBean;
import com.avnet.emasia.webquote.utilities.common.SysConfigSB;
import com.avnet.emasia.webquote.utilities.ejb.MailUtilsSB;
import com.avnet.emasia.webquote.utilities.schedule.HATimerService;
import com.avnet.emasia.webquote.utilities.schedule.IScheduleTask;
import com.avnet.emasia.webquote.web.quote.job.EnvironmentService;
/**
 * @author 906893
 * @createdOn 20130702
 */
@Stateless
public class PendingRITEmailForPMTask implements IScheduleTask ,java.io.Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -6028215791707231146L;
	private static final Logger LOGGER = Logger.getLogger(PendingRITEmailForPMTask.class
			.getName());
	
	private static final String FROM = "PM_EMAIL_FROM_";
	private static final String TEXT = "PM_EMAIL_TEXT_";
	private static final String SUBJECT = "PM_EMAIL_SUBJECT";
	private static final String RECEIVER_EMAIL = "RECEIVERS_FOR_PM_EMAIL_FAILURE";
	private static final int RECORD_NUMBER = 30; 
	
	private static final String FAIL_EMAIL = "PM_RIT_EMAIL_FAIL_NOTIFY_EMAIL";
	
	private static final String DEAR_PM = "Dear pmName,\n";
	private static final String TEXT_IT = "The following quote(s) has/have been pending in IT. Please check and response asap.\n#more#\nWR#         MFR   Full Part No             Customer                 No. of Pending Days\n";
	private static final String TEXT_SQ = "The following quote(s) has/have been pending in SQ for your reference.\n#more#\nWR#         MFR   Full Part No             Customer                 No. of Pending Days\n";
    private static final String BOTTOM_TEXT_IT = "\nYour RFQs Pending List:\nYour RFQ: DOMAIN/RFQ/ResponseInternalTransfer.jsf\n";
    private static final String BOTTOM_TEXT_SQ = "\nYour RFQs Pending List(Pending SQ):\nYour RFQ: DOMAIN/RFQ/MyQuoteListForPM.jsf\n"; 
   
    private static final String BLANK_SPACE_3 = "   ";
    private static final String BLANK_SPACE_2 = "  ";
    
	private static final String REGION_SIGN = "REGION_SIGN";

    
	@EJB
	private transient UserSB userSB;
    @EJB
    private transient BizUnitSB bizUnitSB;
    @EJB
    private transient QuoteSB quoteSB;
    @EJB
    private transient SysConfigSB sysConfigSB;
    @EJB
    private transient MailUtilsSB mailUtilsSB;
    @EJB
    private transient MyQuoteSearchSB myQuoteSearchSB;
    
	@EJB
	private transient SystemCodeMaintenanceSB sysCodeMaintSB;
	
	@EJB
	protected EJBCommonSB ejbCommonSB;
    
    @Override
	@Asynchronous
	public void executeTask(Timer timer) 
	{
		LOGGER.info("PendingRitEmailForPMTask job beginning...");
		//logger.info("start proceedPendingRITQuote");
		try
		{
			            	
			            	proceedPendingRITQuote();
			            	
		}
		catch(Exception e)
		{
			LOGGER.log(Level.WARNING,"PendingRitEmailForPMTask error:"+ MessageFormatorUtil.getParameterizedStringFromException(e));
		}
        
		LOGGER.info("PendingRitEmailForPMTask job ended");
	}

	public void proceedPendingRITQuote() throws WebQuoteException
	{
		
		LOGGER.info("start proceedPendingRITQuote");
		List<BizUnit> bizUnitList = bizUnitSB.findAll();
		if(bizUnitList==null)
			return;
		if(bizUnitList.size()==0)
			return;
		BizUnit bu = null;
		

		//logger.info("start try");
		for(int i=0 ; i < bizUnitList.size() ; i ++)
		{
			bu = (BizUnit)bizUnitList.get(i);
			LOGGER.info("BizUnit : "+ bu.getName());
			if(org.apache.commons.lang.StringUtils.isNotBlank(bu.getName()))
			{
				List<User> pmList = userSB.findAllPMByBizUnit(bu);
				
				String failEmail = sysCodeMaintSB.getBuzinessProperty(FAIL_EMAIL,bu.getName());
				LOGGER.info("failEmail : "+ failEmail);
				List<String> failEmailList = StringUtils.splitStringToList(failEmail, "|");
				List<String> errorRecipents = new ArrayList<String>();
				if(pmList!=null && pmList.size()>0)
				for(User pmUser : pmList)
				{
					LOGGER.info("proceedPendingRITQuote | PM user name : "+ pmUser.getName());
					MyQuoteSearchCriteria criteria = new MyQuoteSearchCriteria();
					List<BizUnit> buList = new ArrayList<BizUnit>();
					buList.add(bu);
					criteria.setBizUnits(buList);
					//logger.info("added bu : "+ bu.getName());
					List<String> statusList = new ArrayList<String>();
					statusList.add(QuoteSBConstant.RFQ_STATUS_IT);
					//enhancement : added the SQ status quote
					statusList.add(QuoteSBConstant.RFQ_STATUS_SQ);
					criteria.setStatus(statusList);
					//logger.info("added status : "+ QuoteSBConstant.RFQ_STATUS_IT);
					List<String> stageList = new ArrayList<String>();
					stageList.add(QuoteSBConstant.QUOTE_STAGE_PENDING);
					criteria.setStage(stageList);
					//logger.info("added stage : "+ QuoteSBConstant.QUOTE_STAGE_PENDING);
					criteria.setDataAccesses(pmUser.getDataAccesses());
					//logger.info("added data access : ");
					//List<QuoteItemVo> quoteItemList = myQuoteSearchSB.search(criteria,new ResourceMB().getResourceLocale());
					List<QuoteItemVo> quoteItemList = myQuoteSearchSB.search(criteria, ResourceMB.getLocale());
					//logger.info("complete search : ");
					if(quoteItemList!=null && quoteItemList.size()>0)
					{
						//logger.info("Have more than one pending quote for PM "+ pmUser.getName());
						try
						{	
							sendEmail(bu,quoteItemList, pmUser);
						}
						catch(CheckedException e)
						{
							errorRecipents.add(pmUser.getId() + " " + pmUser.getName());
							LOGGER.log(Level.WARNING,"Failed to send email to PM:" + pmUser.getId()+", Exception message: "+MessageFormatorUtil.getParameterizedStringFromException(e));
						}

					}

				}
				
				if(errorRecipents!=null && errorRecipents.size()>0)
				{
					LOGGER.info("errorRecipents is not empty ");
					sendFailMsgToBusiness(errorRecipents,failEmailList);
					sendFailureNotificationEmail("email send failed", bu);
				}
				//logger.info("BizUnit : end ");
			}
			
		}
			
		
		LOGGER.info("end proceedPendingRITQuote");
		
	}
	
	public void sendEmail(BizUnit bu, List<QuoteItemVo> quoteItemList, User pmUser) throws CheckedException
	{
		LOGGER.info("start proceedPendingRITQuote | sendEmail");
		try
        {
			List<QuoteItemVo> itList = new ArrayList<QuoteItemVo>();
			List<QuoteItemVo> sqList = new ArrayList<QuoteItemVo>();
			String fromStr = sysCodeMaintSB.getBuzinessProperty(FROM+bu.getName(),bu.getName());
			String subjectStr = sysCodeMaintSB.getBuzinessProperty(SUBJECT,bu.getName());
			StringBuffer contentSb = new StringBuffer();
			contentSb.append(DEAR_PM.replaceFirst("pmName", pmUser.getName()));
			
    	
			for(QuoteItemVo item : quoteItemList)
			{
				if(QuoteSBConstant.RFQ_STATUS_IT.equalsIgnoreCase(item.getQuoteItem().getStatus()))
				{
					itList.add(item);
				}
				else if(QuoteSBConstant.RFQ_STATUS_SQ.equalsIgnoreCase(item.getQuoteItem().getStatus()))
				{
					sqList.add(item);
				}
			}
			
    		
            if(itList!=null && itList.size()>0)
            {
            	Collections.reverse(itList);
            	contentSb.append("\n\n");
            	if (itList.size() > RECORD_NUMBER)
        		{
            		contentSb.append(TEXT_IT.replaceFirst("#more#", "Only 30 earliest records are shown."));
        		}
        		else
        		{
        			contentSb.append(TEXT_IT.replaceFirst("#more#", ""));
        		}
            	int listSize = itList.size();
            	if(listSize>RECORD_NUMBER)
            	{
            		listSize = RECORD_NUMBER;
            	}
            	
            	for(int i =0 ; i<listSize ; i ++)
        		{
            		QuoteItemVo item = itList.get(i);
        			int noOfPendingDays = DateUtils.getDayDiffExcludeSatAndSun(getCalendar(item.getQuoteItem().getSubmissionDate()), DateUtils.getCurrentAsiaCal());
        			contentSb.append(item.getQuoteItem().getQuoteNumber()).append(BLANK_SPACE_3);
        			contentSb.append(item.getQuoteItem().getRequestedMfr().getName()).append(BLANK_SPACE_3);
        			contentSb.append(item.getQuoteItem().getRequestedPartNumber()).append(BLANK_SPACE_3);
        			contentSb.append(item.getQuoteItem().getSoldToCustomer().getCustomerFullName()).append(BLANK_SPACE_3);
        			contentSb.append("                 " + noOfPendingDays).append(BLANK_SPACE_2);
        			contentSb.append("\n");
        		}

    			contentSb.append(BOTTOM_TEXT_IT.replaceAll("DOMAIN", (String)sysConfigSB.getProperyValue(CommodityConstant.WEBQUOTE2_DOMAIN)));

            }

            if(sqList!=null && sqList.size()>0)
            {
            	Collections.reverse(sqList);
            	contentSb.append("\n\n");
            	if (sqList.size() > RECORD_NUMBER)
        		{
            		contentSb.append(TEXT_SQ.replaceFirst("#more#", "Only 30 earliest records are shown."));
        		}
        		else
        		{
        			contentSb.append(TEXT_SQ.replaceFirst("#more#", ""));
        		}
            	int sqlistSize = sqList.size();
            	if(sqlistSize>RECORD_NUMBER)
            	{
            		sqlistSize = RECORD_NUMBER;
            	}
            	
            	for(int i =0 ; i<sqlistSize ; i ++)
        		{
            		QuoteItemVo item = sqList.get(i);
	    			int noOfPendingDays = DateUtils.getDayDiffExcludeSatAndSun(getCalendar(item.getQuoteItem().getSubmissionDate()), DateUtils.getCurrentAsiaCal());
	    			contentSb.append(item.getQuoteItem().getQuoteNumber()).append(BLANK_SPACE_3);
	    			contentSb.append(item.getQuoteItem().getRequestedMfr().getName()).append(BLANK_SPACE_3);
	    			contentSb.append(item.getQuoteItem().getRequestedPartNumber()).append(BLANK_SPACE_3);
	    			contentSb.append(item.getQuoteItem().getSoldToCustomer().getCustomerFullName()).append(BLANK_SPACE_3);
	    			contentSb.append("                 " + noOfPendingDays).append(BLANK_SPACE_2);
	    			contentSb.append("\n");
	    		}
	    	
        		contentSb.append(BOTTOM_TEXT_SQ.replaceAll("DOMAIN", (String)sysConfigSB.getProperyValue(CommodityConstant.WEBQUOTE2_DOMAIN)));
    	
            }
            
            //modified by Lenon 2016-08-31
	    	String regionText = sysCodeMaintSB.getBuzinessProperty(REGION_SIGN, bu.getName());
			if (org.apache.commons.lang.StringUtils.isNotBlank(regionText)) {
				String bestRegardText = regionText.split(",")[0];
				String regionQuoteCenter = regionText.split(",")[1];
				contentSb.append("\n\n").append(bestRegardText).append("\n").append(regionQuoteCenter);
			}

        	MailInfoBean mailInfoBean = new MailInfoBean();
    		mailInfoBean.setMailSubject(subjectStr);
            
    		mailInfoBean.setMailFrom(fromStr);
    		List<String> toList = new ArrayList<String>();
    		toList.add(pmUser.getEmailAddress());
    		//toList.add("tonmy.li@avnet.com");
    		mailInfoBean.setMailTo(toList);
    		mailInfoBean.setMailContent(contentSb.toString());
    		mailUtilsSB.sendTextMail(mailInfoBean);    
        }   
    	catch(Exception ex2)
    	{
    		throw new CheckedException(CommonConstants.WQ_WEB_SEND_EMAIL_FAILED_ERROR_MSG_+ex2.getMessage());
    	}
    	//logger.info("end proceedPendingRITQuote | sendEmail");
	}
	
	public void sendFailureNotificationEmail(String msg, BizUnit bizUnit)
	{
		//logger.info("start proceedPendingRITQuote | sendFailureNotificationEmail");
		
		MailInfoBean mib = new MailInfoBean();
		//add by Lenon.Yang(043044) 2016/05/23
		String jbossNodeName = System.getProperty(HATimerService.JBOSS_NODE_NAME);
		String subject = "Sent Pending quoto notification email for PM failed";
		if(org.apache.commons.lang.StringUtils.isNotBlank(jbossNodeName)) {
			subject += "(Jboss Node:" + jbossNodeName + ")";
		}
		mib.setMailSubject(subject);
		mib.setMailContent(msg);
		String emails = sysCodeMaintSB.getBuzinessProperty(RECEIVER_EMAIL, bizUnit.getName());
		List<String> toList = StringUtils.splitStringToList(emails, "|");
		mib.setMailTo(toList);
		try
	    {
			mailUtilsSB.sendTextMail(mib);
	    }
		catch (Exception e)
		{
			LOGGER.log(Level.SEVERE,"Send email failed! For message: " +msg+", biz unit: "+bizUnit.getName()+", Exception message: "+ MessageFormatorUtil.getParameterizedStringFromException(e), e);
			
		}
		//logger.info("end proceedPendingRITQuote | sendFailureNotificationEmail");
	}
	 
	 public void sendFailMsgToBusiness(List<String> errorRecipents,List<String> failEmailList)
	 {
			MailInfoBean mib = new MailInfoBean();
			//add by Lenon.Yang(043044) 2016/05/23
			String jbossNodeName = System.getProperty(HATimerService.JBOSS_NODE_NAME);
			String subject = "PM Notification Email Failed To Delivery";
			if(org.apache.commons.lang.StringUtils.isNotBlank(jbossNodeName)) {
				subject += "(Jboss Node:" + jbossNodeName + ")";
			}
			mib.setMailSubject(subject);
			
			
			StringBuffer sb = new StringBuffer();
			sb.append("Notification Email cannot be deliveried to below PM. Plese check. ");
			sb.append("\n");
			sb.append("\n");
			for(String errorRecipent : errorRecipents){
				sb.append(errorRecipent);
				sb.append("\n");
			}
			mib.setMailContent(sb.toString());
			mib.setMailTo(failEmailList);
			try
		    {
				mailUtilsSB.sendTextMail(mib);
		    }
			catch (Exception e)
			{
				LOGGER.log(Level.SEVERE, "send Fail Msg To Business email failed! , exception message : "+e.getMessage(), e);
			}
	 }
	 
	 public Calendar getCalendar(Date uploadTime)
	 {
		 Calendar cal=Calendar.getInstance();
		 cal.setTime(uploadTime);
		 return cal;
	 }
	 
}
