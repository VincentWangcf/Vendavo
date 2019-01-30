package com.avnet.emasia.webquote.commodity.web;

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
import javax.faces.event.ActionEvent;

import org.primefaces.context.RequestContext;
import org.springframework.security.core.context.SecurityContextHolder;

import com.avnet.emasia.webquote.commodity.ejb.AnnouncementSB;
import com.avnet.emasia.webquote.entity.Announcement;
import com.avnet.emasia.webquote.entity.BizUnit;
import com.avnet.emasia.webquote.entity.User;
import com.avnet.emasia.webquote.utilites.resources.ResourceMB;
import com.avnet.emasia.webquote.utilities.DateUtils;
import com.avnet.emasia.webquote.utilities.MessageFormatorUtil;
import com.avnet.emasia.webquote.web.security.WQUserDetails;

/**
 * @author Tonmy,Li(906893)
 * @Created on 2013-02-05
 *  Webpromo maintenance module .
 */

@ManagedBean(name = "announcementMB")
@SessionScoped
public class AnnouncementMB implements java.io.Serializable{


	private static final long serialVersionUID = 5039741930049246241L;

	private static final Logger LOGGER = Logger.getLogger(AnnouncementMB.class.getName());
	
	@EJB
    private AnnouncementSB announcementSB;
	
	private List<Announcement> announcements;
	
	private Announcement selectedAnnouncement;
	
	private Announcement newAnnouncement;
	
	private User user;

	private BizUnit currentBizUnit; 
	
	
	public AnnouncementMB()
	{
    	
	}
	
    @PostConstruct
    public void initialize()
    {
    	LOGGER.setLevel(Level.FINE);
//    	logger.fine("call initialize");
    	FacesContext.getCurrentInstance().getExternalContext().getSession(true);
    	if(null==user)
    	{
    		user =((WQUserDetails)SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUser();
    		currentBizUnit = user.getDefaultBizUnit();
    	}
    	announcements = announcementSB.getAllAnnouncement(currentBizUnit);
    	
    }
	
    public void switchBizUnit(BizUnit bizUnit)
    {
    	currentBizUnit = bizUnit;
    	announcements = announcementSB.getAllAnnouncement(bizUnit);
    }
	public List<Announcement> getAnnouncements() {
		return announcements;
	}

	public void setAnnouncements(List<Announcement> announcements) {
		this.announcements = announcements;
	}

	public Announcement getSelectedAnnouncement() {
		return selectedAnnouncement;
	}

	public void setSelectedAnnouncement(Announcement selectedAnnouncement) {
		this.selectedAnnouncement = selectedAnnouncement;
	}

	public Announcement getNewAnnouncement() {
		return newAnnouncement;
	}

	public void setNewAnnouncement(Announcement newAnnouncement) {
		this.newAnnouncement = newAnnouncement;
	}

	public void resetNewAnnouncement()
	{
		LOGGER.fine("call resetNewAnnouncement"); 
		newAnnouncement = new Announcement();
		newAnnouncement.setExpirationDate(DateUtils.getHalfYearBeforeCur());
	}
	
	public void refreshAnnouncements()
	{ 
		announcements = announcementSB.getAllAnnouncement(user.getCurrenBizUnit());
	}
	
	/*
	 * create annoucncement
	 */
	
	public void createAnnouncement(ActionEvent actionEvent)
    {
    	LOGGER.fine("call createAnnouncement"); 
		RequestContext context = RequestContext.getCurrentInstance();
		FacesMessage msg = null;
		boolean isSuccess = false; 
		String errorMsg = null ;
		if(newAnnouncement.getDescription() ==null || newAnnouncement.getDescription().equals(""))
		{
			errorMsg = ResourceMB.getText("wq.message.entrDescr");
		}
		    
		if(newAnnouncement.getDescription().length()>500)
		{
			errorMsg = ResourceMB.getText("wq.message.descrLenError");
		}
		if(announcements.size()>=50)
		{
			errorMsg = ResourceMB.getText("wq.message.recordLimtError");
		}
			
		
		if(errorMsg ==null)
		{ 
			try
			{
				//TO-DO set user and bussiness units
			   Date d = DateUtils.getCurrentAsiaDateObj();
			   newAnnouncement.setCreatedOn(d);
			   newAnnouncement.setLastUpdatedOn(d);
			   newAnnouncement.setVersion(1);
			   newAnnouncement.setCreatedBy(user);
			   newAnnouncement.setLastUpdatedBy(user);
			   newAnnouncement.setSequence(announcementSB.getMaxSeqNum(currentBizUnit));
			   newAnnouncement.setBizUnit(currentBizUnit);
			   announcementSB.createAnnoun(newAnnouncement);
			   isSuccess = true;
			   msg = new FacesMessage(FacesMessage.SEVERITY_INFO, ResourceMB.getText("wq.message.annoucmntAddedSuccess")+"!",null);
			   announcements = announcementSB.getAllAnnouncement(user.getCurrenBizUnit());
			}
			catch(Exception e)
			{
				LOGGER.log(Level.SEVERE, "Error occured in creating announcement: "+selectedAnnouncement.getDescription()+", "
				 		+ "Reason for failure: "+MessageFormatorUtil.getParameterizedStringFromException(e), e);
				 errorMsg = ResourceMB.getText("wq.exception.createFail");
			}
			
		}
		else
		{
			msg = new FacesMessage(FacesMessage.SEVERITY_WARN, errorMsg , errorMsg);
		}
		refreshAnnouncements();
		FacesContext.getCurrentInstance().addMessage(null, msg);
		context.addCallbackParam("isSuccess", isSuccess);
						
    }

	/*
	 * update announcement
	 */
    public void updateAnnouncement(ActionEvent actionEvent)
    {
    	 
    	LOGGER.fine("call updateAnnouncement"); 
    	RequestContext context = RequestContext.getCurrentInstance();
		FacesMessage msg = null;
		boolean isSuccess = false; 
		String errorMsg = null ;
		if(selectedAnnouncement.getDescription() ==null || selectedAnnouncement.getDescription().equals(""))
		{
			errorMsg = ResourceMB.getText("wq.message.entrDescr");
		}
		if(selectedAnnouncement.getDescription().length()>500)
		{
			errorMsg = ResourceMB.getText("wq.message.descrLenError");
		}
		if(errorMsg ==null)
		{
			try
			{
				selectedAnnouncement.setLastUpdatedOn(DateUtils.getCurrentAsiaDateObj());
				selectedAnnouncement.setLastUpdatedBy(user);
				announcementSB.updateAnnoun(selectedAnnouncement);
				isSuccess = true;
				msg = new FacesMessage(FacesMessage.SEVERITY_INFO, ResourceMB.getText("wq.message.annoucmntAddedSuccess")+"!",null);
			}
			catch(Exception e)
			{
				LOGGER.log(Level.SEVERE, "Error occured in editing announcement: "+selectedAnnouncement.getDescription()+", "
				 		+ "Reason for failure: "+MessageFormatorUtil.getParameterizedStringFromException(e), e);
				 errorMsg = ResourceMB.getText("wq.exception.editFail");
			}
			
		}
		else
		{
			msg = new FacesMessage(FacesMessage.SEVERITY_WARN, errorMsg , errorMsg);
		}
		refreshAnnouncements();
		FacesContext.getCurrentInstance().addMessage(null, msg);
		context.addCallbackParam("isSuccess", isSuccess);
						
    }
    
    /*
     * Delete announcement.
     */
    public void deleteAnnouncement()
    {
    	
    	LOGGER.fine("call deleteAnnouncement"); 
		RequestContext context = RequestContext.getCurrentInstance();
		FacesMessage msg = null;
		boolean isSuccess = false;
		String errorMsg = null ;
		String message=null;
		try
		{
			announcementSB.deleteAnnoun(selectedAnnouncement);
			isSuccess = true;
			message=ResourceMB.getText("wq.message.success");
			msg = new FacesMessage(FacesMessage.SEVERITY_INFO, message,null);
		}
		catch(Exception e)
		{
			LOGGER.log(Level.SEVERE, "Error occured in deleting announcement: "+selectedAnnouncement.getDescription()+", "
			 		+ "Reason for failure: "+MessageFormatorUtil.getParameterizedStringFromException(e), e);
			 isSuccess = false;
			 message=ResourceMB.getText("wq.message.oprtnFailed");
			 msg = new FacesMessage(FacesMessage.SEVERITY_WARN, message, errorMsg);
		}
		
		refreshAnnouncements();
		FacesContext.getCurrentInstance().addMessage(null, msg);
		context.addCallbackParam("isSuccess", isSuccess);
						
    }
    
    /*
     * ascend the Announcement
     */
    public void ascendAnnouncement()
    {
    	 
    	LOGGER.fine("call ascendAnnouncement"); 
    	RequestContext context = RequestContext.getCurrentInstance();
		FacesMessage msg = null;
		boolean isSuccess = false; 
		String errorMsg = null ;
		String message = null ;
		

		try
		{
		   announcementSB.upAnnouncement(selectedAnnouncement.getSequence(),user.getCurrenBizUnit());
		   isSuccess = true;
		   message=ResourceMB.getText("wq.message.success");
		   msg = new FacesMessage(FacesMessage.SEVERITY_INFO,message,null);
		}
		catch(Exception e)
		{	
			 LOGGER.log(Level.SEVERE, "Error occured in moving up announcement: "+selectedAnnouncement.getDescription()+", "
			 		+ "Reason for failure: "+MessageFormatorUtil.getParameterizedStringFromException(e), e);
			 isSuccess = false;
			 message=ResourceMB.getText("wq.message.oprtnFailed");
			 msg = new FacesMessage(FacesMessage.SEVERITY_WARN, message, errorMsg);
		}	

		refreshAnnouncements();
		FacesContext.getCurrentInstance().addMessage(null, msg);
		context.addCallbackParam("isSuccess", isSuccess);
						
    }
    
    /*
     * decline the announcement.
     */
    public void declineAnnouncement()
    {
    	 
    	LOGGER.fine("call declineAnnouncement"); 
    	RequestContext context = RequestContext.getCurrentInstance();
		FacesMessage msg = null;
		boolean isSuccess = false; 
		String errorMsg = null ;
		String message = null ;

		try
		{
		   announcementSB.downAnnouncement(selectedAnnouncement.getSequence(),user.getCurrenBizUnit());
		   isSuccess = true;
		   message=ResourceMB.getText("wq.message.success");
		   msg = new FacesMessage(FacesMessage.SEVERITY_INFO, message,null);
		}
		catch(Exception e)
		{
			LOGGER.log(Level.SEVERE, "Error occured in moving down announcement: "+selectedAnnouncement.getDescription()+", "
			 		+ "Reason for failure: "+MessageFormatorUtil.getParameterizedStringFromException(e), e);
			 isSuccess = false;
			 message=ResourceMB.getText("wq.message.oprtnFailed");
			 msg = new FacesMessage(FacesMessage.SEVERITY_WARN, message, errorMsg);
		}
			
		
		refreshAnnouncements();
		FacesContext.getCurrentInstance().addMessage(null, msg);
		context.addCallbackParam("isSuccess", isSuccess);
    }


	public BizUnit getCurrentBizUnit() {
		return currentBizUnit;
	}

	public void setCurrentBizUnit(BizUnit currentBizUnit) {
		this.currentBizUnit = currentBizUnit;
	}
	
	
}
