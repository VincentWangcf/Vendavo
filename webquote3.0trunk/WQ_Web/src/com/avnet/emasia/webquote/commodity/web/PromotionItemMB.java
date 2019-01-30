package com.avnet.emasia.webquote.commodity.web;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;

import org.primefaces.context.RequestContext;
import org.springframework.security.core.context.SecurityContextHolder;

import com.avnet.emasia.webquote.commodity.dto.PISearchBean;
import com.avnet.emasia.webquote.commodity.ejb.ProgramMaterialSB;
import com.avnet.emasia.webquote.commodity.ejb.PromotionItemSB;
import com.avnet.emasia.webquote.commodity.util.Constants;
import com.avnet.emasia.webquote.commodity.vo.QtyBreakPricerModel;
import com.avnet.emasia.webquote.entity.BizUnit;
import com.avnet.emasia.webquote.entity.Pricer;
import com.avnet.emasia.webquote.entity.ProgramPricer;
import com.avnet.emasia.webquote.entity.PromotionItem;
import com.avnet.emasia.webquote.entity.QtyBreakPricer;
import com.avnet.emasia.webquote.entity.SalesCostPricer;
import com.avnet.emasia.webquote.entity.User;
import com.avnet.emasia.webquote.utilites.resources.ResourceMB;
import com.avnet.emasia.webquote.utilities.DateUtils;
import com.avnet.emasia.webquote.utilities.MessageFormatorUtil;
import com.avnet.emasia.webquote.web.security.WQUserDetails;

/**
 * @author Tonmy,Li(906893)
 * @Created on 2013-02-04
 *  Webpromo promotion item module .
 */

@ManagedBean(name = "promotionItemMB")
@SessionScoped
public class PromotionItemMB implements java.io.Serializable{


	private static final long serialVersionUID = 979611845924281312L;

	private static Logger logger = Logger.getLogger(PromotionItemMB.class.getName());

	@EJB
	private PromotionItemSB promotionItemSB;
	@EJB
	private ProgramMaterialSB programMaterialSB;
	
	private List<PromotionItem> promotionItems;
	
	private PromotionItem selectedPromotionItem;
    
	private PromotionItem newPromotionItem;
	
    private User user;
    
    
    private QtyBreakPricerModel searchAddResultModel;  
    private Pricer selectedAddProgramMaterial;
    private String searchType;
    private String searchContent;
	
    private String searchType2;
    private String searchContent2;
    
    private QtyBreakPricerModel searchEditResultModel;  
    private Pricer selectedEditProgramMaterial;
    
    private BizUnit currentBizUnit;
    
    @PostConstruct
    public void initialize()
    {
//    	logger.fine("call initialize");
    	
    	FacesContext.getCurrentInstance().getExternalContext().getSession(true);
    	if(user==null)
    	{
    		user =((WQUserDetails)SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUser();
    		currentBizUnit = user.getDefaultBizUnit();
    	}
    	
    	promotionItems = promotionItemSB.getAllPromotionItem(currentBizUnit);
    	newPromotionItem = new PromotionItem();
    	selectedPromotionItem = new PromotionItem();
    }
	
    public void switchBizUnit(BizUnit bizUnit)
    {
    	currentBizUnit = bizUnit;
    	
    	promotionItems = promotionItemSB.getAllPromotionItem(bizUnit);
    	newPromotionItem = new PromotionItem();
    	selectedPromotionItem = new PromotionItem();
    	
    }
    
	public List<PromotionItem> getPromotionItems() {
		return promotionItems;
	}

	public void setPromotionItems(List<PromotionItem> promotionItems) {
		this.promotionItems = promotionItems;
	}

	public PromotionItem getSelectedPromotionItem() {
		logger.fine("call getSelectedPromotionItem");
		return selectedPromotionItem;
	}

	public void setSelectedPromotionItem(PromotionItem selectedPromotionItem) {
		logger.fine("call setSelectedPromotionItem");
		this.selectedPromotionItem = selectedPromotionItem;
	}
	
	
	
	public void resetNewPromotionItem()
	{
		logger.fine("call resetNewPromotionItem"); 
		newPromotionItem = new PromotionItem();
		selectedPromotionItem = new PromotionItem();
		searchAddResultModel = new QtyBreakPricerModel(new ArrayList<QtyBreakPricer>());
		resetPISearch();
	}
	
	public void refreshPromotionItems()
	{
		logger.fine("call refreshPromotionItems"); 
		promotionItems = promotionItemSB.getAllPromotionItem(currentBizUnit);
	}
	
	public void resetPISearch()
	{
		logger.fine("call refreshPISearch"); 
		searchContent=null;
		searchType=Constants.PARA_SEARCH_TYPE_PART;
	}

	/* 
	 * create promotion item.
	 */
	public void createPromotionItem()
    {
    	logger.fine("call createPromotionItem"); 
    	RequestContext context = RequestContext.getCurrentInstance();
		FacesMessage msg = null;
		boolean isSuccess = false; 
		String errorMsg = null ;
		if(newPromotionItem.getDescription() ==null || newPromotionItem.getDescription().equals(""))
		{			
			errorMsg = ResourceMB.getText("wq.message.entrDescr");
		}
		    
		if(newPromotionItem.getDescription().length()>500)
		{
			errorMsg = ResourceMB.getText("wq.message.descrLenError");
		}
		if(promotionItems.size()>=50)
		{
			errorMsg = ResourceMB.getText("wq.message.recordLimtError");
		}
			
		if(getSelectedAddProgramMaterial()==null)
		{
			errorMsg = ResourceMB.getText("wq.message.chosePrgmMatrl");
		}
		String message=null;
		if(errorMsg ==null)
		{
			try
			{

				//TO-DO set user and bussiness units
			   //newPromotionItem.setMaterial(selectedAddProgramMaterial);
			   Date d = DateUtils.getCurrentAsiaDateObj();
			   newPromotionItem.setMaterial(getSelectedAddProgramMaterial().getMaterial());
			   newPromotionItem.setCreatedOn(d);
			   newPromotionItem.setLastUpdatedOn(d);
			   newPromotionItem.setVersion(1);
			   newPromotionItem.setCreatedBy(user);
			   newPromotionItem.setLastUpdatedBy(user);
			   Integer newSeq = promotionItemSB.getMaxSeqNum(currentBizUnit);
			   newPromotionItem.setSequence(newSeq);
			   newPromotionItem.setBizUnit(currentBizUnit);
			   if(selectedAddProgramMaterial instanceof ProgramPricer) {
				   ProgramPricer p = (ProgramPricer)selectedAddProgramMaterial;
				   newPromotionItem.setProgramCode(p.getProgramType().getName());
			   }else if(selectedAddProgramMaterial instanceof SalesCostPricer) {
				   SalesCostPricer p = (SalesCostPricer)selectedAddProgramMaterial;
				   newPromotionItem.setProgramCode(p.getProgramType().getName());
			   }
			   
			   promotionItemSB.createPromoItem(newPromotionItem);
			   isSuccess = true;
			   message=ResourceMB.getText("wq.message.modifidPromoItem");
			   msg = new FacesMessage(FacesMessage.SEVERITY_INFO, message+"!",null);
			   

			}
			catch(Exception e)
			{
				logger.log(Level.SEVERE, "Error occured while creating PromotionItem with Description: "+newPromotionItem.getDescription()+", "
						+ "Reason for failure: "+MessageFormatorUtil.getParameterizedStringFromException(e), e);
				 message=ResourceMB.getText("wq.exception.createFail");
				 msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, message ,null);
			}
			
		}
		else
		{
			msg = new FacesMessage(FacesMessage.SEVERITY_WARN, errorMsg , errorMsg);
		}
		refreshPromotionItems();
		resetNewPromotionItem();
		FacesContext.getCurrentInstance().addMessage(null, msg);
		//context.addCallbackParam("isSuccess", isSuccess);
						
    }

	/*
	 * update promotion item.
	 */
    public void updatePromotionItem()
    {
    	 
    	logger.fine("call updatePromotionItem"); 
    	RequestContext context = RequestContext.getCurrentInstance();
		FacesMessage msg = null;
		boolean isSuccess = false; 
		String errorMsg = null ;
		if(selectedPromotionItem.getDescription() ==null || selectedPromotionItem.getDescription().equals(""))
		{
			errorMsg =ResourceMB.getText("wq.message.entrDescr");
		}
		if(selectedPromotionItem.getDescription().length()>500)
		{
			errorMsg = ResourceMB.getText("wq.message.descrLenError");
		}
		
		if(selectedEditProgramMaterial==null)
		{
			errorMsg = ResourceMB.getText("wq.message.chosePrgmMatrl");
		}
		String message=null;
		if(errorMsg ==null)
		{
			try
			{
				//selectedPromotionItem.setMaterial(selectedEditProgramMaterial);
				promotionItemSB.updatePromoItem(selectedPromotionItem);
				isSuccess = true;
				message=ResourceMB.getText("wq.message.modifidPromoItem");
				msg = new FacesMessage(FacesMessage.SEVERITY_INFO, message,null);
			}
			catch(Exception e)
			{
				logger.log(Level.SEVERE, "Error occured in updating promotion item: "+selectedPromotionItem.getDescription()+", "
						+ "Reason for failure: "+MessageFormatorUtil.getParameterizedStringFromException(e), e);
				 message=ResourceMB.getText("wq.exception.editFail");
				 msg = new FacesMessage(FacesMessage.SEVERITY_INFO, message ,null);
			}
			
		}
		else
		{
			msg = new FacesMessage(FacesMessage.SEVERITY_WARN, errorMsg , errorMsg);
		}
		refreshPromotionItems();
		FacesContext.getCurrentInstance().addMessage(null, msg);
		context.addCallbackParam("isSuccess", isSuccess);
						
    }
    
    /*
     * delete promotino item.
     */
    public void deletePromotionItem()
    {
    	
    	logger.fine("call deletePromotionItem"); 
		RequestContext context = RequestContext.getCurrentInstance();
		FacesMessage msg = null;
		boolean isSuccess = false;
		String message=null;

		try
		{
			promotionItemSB.deletePromoItem(selectedPromotionItem);
			isSuccess = true;
			message=ResourceMB.getText("wq.message.success");
			msg = new FacesMessage(FacesMessage.SEVERITY_INFO, message,null);
		}
		catch(Exception e)
		{
			logger.log(Level.SEVERE, "Error occured while deleting promotionItem: "+selectedPromotionItem.getDescription()+", "
					+ "Reason for failure: "+MessageFormatorUtil.getParameterizedStringFromException(e), e);
			 message=ResourceMB.getText("wq.exception.delFail");
			 msg = new FacesMessage(FacesMessage.SEVERITY_INFO, message ,null);
			 context.addCallbackParam(null, msg);
		}
		
		refreshPromotionItems();
		FacesContext.getCurrentInstance().addMessage(null, msg);
		context.addCallbackParam("isSuccess", isSuccess);
						
    }
    
    /*
     * ascend promotion item.
     */
    public void ascendPromotionItem()
    {
    	 
    	logger.fine("call ascendPromotionItem"); 
    	RequestContext context = RequestContext.getCurrentInstance();
		FacesMessage msg = null;
		boolean isSuccess = false; 
		String errorMsg = null ;
		String message=null;

		try
		{
		   promotionItemSB.upPromoItem(selectedPromotionItem.getSequence(),currentBizUnit);
		   isSuccess = true;
		   message=ResourceMB.getText("wq.message.success");
		   msg = new FacesMessage(FacesMessage.SEVERITY_INFO, message,null);
		}
		catch(Exception e)
		{
			 isSuccess = false;
			 message=ResourceMB.getText("wq.message.oprtnFailed");			 
			 msg = new FacesMessage(FacesMessage.SEVERITY_WARN, message, errorMsg);
			 logger.severe(MessageFormatorUtil.getParameterizedString(Locale.ENGLISH, "wq.message.oprtnFailed", new Object[]{})+e.getMessage());
		}	
		
		refreshPromotionItems();
		FacesContext.getCurrentInstance().addMessage(null, msg);
		context.addCallbackParam("isSuccess", isSuccess);
						
    }
    
    /*
     * decline promotion item.
     */
    public void declinePromotionItem()
    {
    	 
    	logger.fine("call declinePromotionItem"); 
    	RequestContext context = RequestContext.getCurrentInstance();
		FacesMessage msg = null;
		boolean isSuccess = false; 
		String errorMsg = null ;
		String message=null;

		try
		{
		   promotionItemSB.downPromoItem(selectedPromotionItem.getSequence(),user.getCurrenBizUnit());
		   isSuccess = true;
		   message=ResourceMB.getText("wq.message.success");
		   msg = new FacesMessage(FacesMessage.SEVERITY_INFO,message,null);
		}
		catch(Exception e)
		{
			 isSuccess = false;
			 message=ResourceMB.getText("wq.message.oprtnFailed");	
			 msg = new FacesMessage(FacesMessage.SEVERITY_WARN, message , errorMsg);
			 logger.severe(MessageFormatorUtil.getParameterizedString(Locale.ENGLISH, "wq.message.oprtnFailed", new Object[]{})+e.getMessage());
		}
			
		
		refreshPromotionItems();
		FacesContext.getCurrentInstance().addMessage(null, msg);
		context.addCallbackParam("isSuccess", isSuccess);
    }


	/*
	 * search program material on new promotion item panel
	 */
	public void searchAdd()
	{
		
    	PISearchBean pis = new PISearchBean();
		pis.setActive(Constants.YES);	
		pis.setBizUnit(currentBizUnit.getName());
		if(Constants.PARA_SEARCH_TYPE_MFR.equalsIgnoreCase(searchType))
		{ 
			pis.setMfr(searchContent);
		}
		else if(Constants.PARA_SEARCH_TYPE_PART.equalsIgnoreCase(searchType))
		{
			pis.setMfrPart(searchContent);
		}
		List<QtyBreakPricer> results = programMaterialSB.searchQtyBreakPricerByPage(pis);

		searchAddResultModel = new QtyBreakPricerModel(results); 
		// search the default selection:
		if(results!=null && results.size()>0)
		{
			selectedAddProgramMaterial = results.get(0);
		}

	}
	
	/*
	 * search program material on edit promotion item panel
	 */
	public void searchEdit()
	{
		
    	logger.fine("call searchEdit : searchType2 "+ searchType2 + " search content2: "+ searchContent2); 
    	PISearchBean pis = new PISearchBean();
		pis.setActive(Constants.YES);	
		pis.setBizUnit(currentBizUnit.getName());
		if(Constants.PARA_SEARCH_TYPE_MFR.equalsIgnoreCase(searchType2))
		{
			pis.setMfr(searchContent2);
		}
		else if(Constants.PARA_SEARCH_TYPE_PART.equalsIgnoreCase(searchType2))
		{
		    pis.setMfrPart(searchContent2);
		}
		List<QtyBreakPricer> results = programMaterialSB.searchSpecialPricer(pis);

		searchEditResultModel = new QtyBreakPricerModel(results); 
		// search the default selection:
		if(results!=null && results.size()>0)
		{
			selectedEditProgramMaterial = results.get(0);
		}

	}
	
	/*
	 * default data loading action.
	 */
	public void defaultLoading()
	{
		logger.fine("call defaultLoading"); 
		PISearchBean pis = new PISearchBean();
		pis.setActive(Constants.YES);
		pis.setBizUnit(currentBizUnit.getName());
		//pis.setMfr(selectedPromotionItem.getMaterial().getManufacturer().getName());
		pis.setMfrPart(selectedPromotionItem.getMaterial().getFullMfrPartNumber());
		List<QtyBreakPricer> results = programMaterialSB.searchSpecialPricer(pis);
		searchEditResultModel = new QtyBreakPricerModel(results); 
		if(results!=null && results.size()>0)
		{
			selectedEditProgramMaterial = results.get(0);
		}
		resetPISearch();

	}

    
	public QtyBreakPricerModel getSearchAddResultModel() {
		return searchAddResultModel;
	}


	public void setSearchAddResultModel(QtyBreakPricerModel searchAddResultModel) {
		this.searchAddResultModel = searchAddResultModel;
	}


	public Pricer getSelectedAddProgramMaterial() {
		return selectedAddProgramMaterial;
	}


	public void setSelectedAddProgramMaterial(
			Pricer selectedAddProgramMaterial) {
		this.selectedAddProgramMaterial = selectedAddProgramMaterial;
	}


	public QtyBreakPricerModel getSearchEditResultModel() {
		return searchEditResultModel;
	}


	public void setSearchEditResultModel(QtyBreakPricerModel searchEditResultModel) {
		this.searchEditResultModel = searchEditResultModel;
	}


	public Pricer getSelectedEditProgramMaterial() {
		return selectedEditProgramMaterial;
	}


	public void setSelectedEditProgramMaterial(
			Pricer selectedEditProgramMaterial) {
		this.selectedEditProgramMaterial = selectedEditProgramMaterial;
	}

	
	public String getSearchType() {
		return searchType;
	}


	public void setSearchType(String searchType) {
		this.searchType = searchType;
	}


	public String getSearchContent() {
		return searchContent;
	}


	public void setSearchContent(String searchContent) {
		this.searchContent = searchContent;
	}


	public PromotionItem getNewPromotionItem() {
		return newPromotionItem;
	}


	public void setNewPromotionItem(PromotionItem newPromotionItem) {
		this.newPromotionItem = newPromotionItem;
	}


	public String getSearchType2() {
		return searchType2;
	}


	public void setSearchType2(String searchType2) {
		this.searchType2 = searchType2;
	}


	public String getSearchContent2() {
		return searchContent2;
	}


	public void setSearchContent2(String searchContent2) {
		this.searchContent2 = searchContent2;
	}

	public BizUnit getCurrentBizUnit() {
		return currentBizUnit;
	}

	public void setCurrentBizUnit(BizUnit currentBizUnit) {
		this.currentBizUnit = currentBizUnit;
	}

	
	
}
