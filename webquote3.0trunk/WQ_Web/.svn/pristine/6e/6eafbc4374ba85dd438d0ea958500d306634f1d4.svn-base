package com.avnet.emasia.webquote.web.quote;

import java.io.Serializable;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
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
import javax.faces.event.AjaxBehaviorEvent;
import javax.faces.model.SelectItem;

import org.primefaces.context.RequestContext;

import com.avnet.emasia.webquote.constants.ActionEnum;
import com.avnet.emasia.webquote.constants.BmtFlagEnum;
import com.avnet.emasia.webquote.entity.Attachment;
import com.avnet.emasia.webquote.entity.QuoteItem;
import com.avnet.emasia.webquote.entity.QuoteItemDesign;
import com.avnet.emasia.webquote.entity.User;
import com.avnet.emasia.webquote.exception.AppException;
import com.avnet.emasia.webquote.exception.CommonConstants;
import com.avnet.emasia.webquote.quote.ejb.CopyRefreshQuoteSB;
import com.avnet.emasia.webquote.quote.ejb.ExchangeRateSB;
import com.avnet.emasia.webquote.quote.ejb.QuoteSB;
import com.avnet.emasia.webquote.quote.ejb.SystemCodeMaintenanceSB;
import com.avnet.emasia.webquote.quote.ejb.constant.QuoteSBConstant;
import com.avnet.emasia.webquote.quote.vo.QuoteItemVo;
import com.avnet.emasia.webquote.utilites.resources.ResourceMB;
import com.avnet.emasia.webquote.utilites.web.common.CacheUtil;
import com.avnet.emasia.webquote.utilities.MessageFormatorUtil;
import com.avnet.emasia.webquote.web.quote.cache.QuoteTypeCacheManager;
import com.avnet.emasia.webquote.web.quote.constant.QuoteConstant;
import com.avnet.emasia.webquote.web.user.UserInfo;

@ManagedBean
@SessionScoped
public class BmtInfoMB extends AttachmentEditMB implements Serializable {
	

	/**
	 * 
	 */
	private static final long serialVersionUID = 4143805354393052384L;

	private static final Logger LOG =Logger.getLogger(BmtInfoMB.class.getName());
	
	@EJB
	CopyRefreshQuoteSB copyRefreshQuoteSB;
	
	@EJB 
	QuoteSB quoteSB;
	
	@EJB
	ExchangeRateSB exchangeRateSB;	
	
	@EJB
	private SystemCodeMaintenanceSB codeMaintenanceSB;
	
	@EJB
	private CacheUtil cacheUtil;
	

	private boolean finished;
	private List<String> quoteTypeList = null;
	
	private SelectItem[] pageBmtFlags;
	private SelectItem[] bmtCurrencyList;
	
	public BmtInfoMB(){		
		super.attachmentType = QuoteSBConstant.ATTACHMENT_TYPE_BMT;
		super.showExistingAttachment = false;	
		
		int bmtFlagSize =5;
		pageBmtFlags = new SelectItem[bmtFlagSize];

		pageBmtFlags[0] = new SelectItem("99", QuoteConstant.DEFAULT_SELECT);
		pageBmtFlags[1] = new SelectItem("A", "Non-BMT DR");
		pageBmtFlags[2] = new SelectItem("B", "BMT DR");
		pageBmtFlags[3] = new SelectItem("C", "MRS");
		pageBmtFlags[4] = new SelectItem("D", "BMT-DR Incomplete");

	}
	
	@PostConstruct
	public void initialize() {
		//Bryan Start
		//setQuoteTypeList(QuoteTypeCacheManager.getQuoteTypeList());
		setQuoteTypeList(cacheUtil.getQuoteTypeList());
		//Bryan End
		List<String> bmtCurrencyLst = codeMaintenanceSB.findCurrency();
		bmtCurrencyList = com.avnet.emasia.webquote.utilites.web.util.QuoteUtil.createFilterOptions(bmtCurrencyLst.toArray(new String[bmtCurrencyLst.size()]),bmtCurrencyLst.toArray(new String[bmtCurrencyLst.size()]),  false, false);
		
	}
	
	public void setUpdateQuoteItem(QuoteItemVo item) {
		List<QuoteItemVo> quoteItemVos = new ArrayList<QuoteItemVo>();
		quoteItemVos.add(item);
		
		super.selectedQuoteItemVos = quoteItemVos;
		super.selectedQuoteItemId = item.getQuoteItem().getId();
		super.quoteItemVos = quoteItemVos;
		
		super.currentQuoteItemVo = item;
		super.showExistingAttachment = false;
		
		this.setFinished(false);
	}
	
	public void setCopyBmtQuoteItems(List<QuoteItemVo> itemVos) {
		this.setFinished(false);
		super.quoteItemVos = itemVos; 
		super.showExistingAttachment = false;
		int i = 1;
		String buName = null;
		
		//Bryan Start
		//String bidtobuy =QuoteTypeCacheManager.getQuoteType(QuoteConstant.BID_TO_BUY);
		String bidtobuy =cacheUtil.getQuoteType(QuoteConstant.BID_TO_BUY);
		//Bryan End
		List<String> attType = new ArrayList<String>();//RFQ Form Attachment,RFQ Item Attachment,PM Attachment,QC Attachment,BMT Attachment,Refresh Attachment
		attType.add("RFQ Form Attachment");
		attType.add("RFQ Item Attachment");
		attType.add("PM Attachment");
		attType.add("QC Attachment");
		attType.add("BMT Attachment");
		attType.add("Refresh Attachment");
		for(QuoteItemVo vo : quoteItemVos){
			vo.setSeq2(i++);
			vo.setQuoteType(bidtobuy);
			vo.setEau(vo.getQuoteItem().getEau());
			List<Attachment> atts = quoteSB.findAttachments(vo.getQuoteItem().getId(), vo.getQuoteItem().getQuote().getId(), attType);
			if(null!=atts && atts.size()>0) {
				vo.getQuoteItem().setAttachments(atts); 
			}
			List<Attachment> oldAttachments = vo.getQuoteItem().getAttachments();
			if(null!=oldAttachments && oldAttachments.size()>0) {
				for(Attachment attachment : oldAttachments){
					LOG.info("isNew  "+attachment.isNewAttachment()+"------==>>>"+ attachment.getType()+ "====>>>+++++"+attachment.getFileName());
				}
			}
			
			vo.getQuoteItem().setReasonForRefresh(null);
			buName =vo.getQuoteItem().getQuote().getBizUnit().getName();
			
			
		}

		selectedQuoteItemVos = itemVos;
		
	}
	
	public void updateDrMargin(AjaxBehaviorEvent event){	
		
	    QuoteItemVo item = (QuoteItemVo)event.getComponent().getAttributes().get("targetQuoteItem");
	   
	}
	
	public void setQuoteItem(QuoteItemVo quoteItemVo){
		List<QuoteItemVo> quoteItemVos = new ArrayList<QuoteItemVo>();
		quoteItemVos.add(quoteItemVo);
		
		super.selectedQuoteItemVos = quoteItemVos;
		
		super.selectedQuoteItemId = quoteItemVo.getQuoteItem().getId();

		int i = 1;
		//Bryan Start
		//String bidtobuy =QuoteTypeCacheManager.getQuoteType(QuoteConstant.BID_TO_BUY);
		String bidtobuy =cacheUtil.getQuoteType(QuoteConstant.BID_TO_BUY);
		//Bryan End
		for(QuoteItemVo vo : quoteItemVos){
			vo.setSeq2(i++);
			vo.setQuoteType(bidtobuy);
			vo.getQuoteItem().setAttachments(null);
			vo.getQuoteItem().setReasonForRefresh(null);
		}
		selectedQuoteItemVos = quoteItemVos;
	}
	
	public String confirmCopyBMTQuote() {
		
		if(this.selectedQuoteItemVos.size() == 0){
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN,ResourceMB.getText("wq.message.noRecordSelected")+" !", "")); 
			return null;
		}
		
		boolean valid = true;
		
//		Bmt flag, mfr dr no, bmt suggest cost, bmt suggest resale, bmt suggest margin
		StringBuffer errorMsg = new StringBuffer();
		for(QuoteItemVo vo : selectedQuoteItemVos){
			QuoteItemDesign design = vo.getQuoteItem().getQuoteItemDesign();
			
			String bmtFlag = design.getBmtFlag().getBmtFlagCode().toString();
			StringBuffer rowErrorMsg = new StringBuffer();
			
			if(BmtFlagEnum.BMT_DR.code().toString().equalsIgnoreCase(bmtFlag)) {
				if(design.getDrMfrNo() == null || "".equalsIgnoreCase(design.getDrMfrNo().trim())){
					vo.setErrRow(true);
					valid = false;
					rowErrorMsg.append("-"+ResourceMB.getText("wq.message.bmtMfrMandatory")+",<br/>");
				}
				if(design.getDrCost() == null|| design.getDrCost().doubleValue() == 0){
					vo.setErrRow(true);
					valid = false;
					rowErrorMsg.append("-"+ResourceMB.getText("wq.message.bmtCostMandatory")+",<br/>");
				}
				
				if(design.getDrResale() == null|| design.getDrResale().doubleValue() == 0){
					vo.setErrRow(true);
					valid = false;
					rowErrorMsg.append("-"+ResourceMB.getText("wq.message.bmtResaleMandatory")+",<br/>");
				}
				
				if (rowErrorMsg.length() > 0) {    
					rowErrorMsg.insert(0, ResourceMB.getText("wq.message.forRowQuote")+"# "+vo.getQuoteItem().getQuoteNumber()+":<br/>");
					rowErrorMsg.insert(0, ResourceMB.getText("wq.message.bmtFlageDR")+",<br/>");
					errorMsg.append(rowErrorMsg+"<br>");
				}
				
				
			}
		}
		if(!valid){
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN,errorMsg.toString(),"")); 
			return null;
		}	
		
		for(QuoteItemVo vo : this.selectedQuoteItemVos){
			long count = 0L;
			try {
				count = copyRefreshQuoteSB.getRevertVersionByFirstRFQCode(vo.getQuoteItem().getFirstRfqCode());
			
				if(count >= 99){  
					Object[] dynamicParameters={vo.getQuoteItem().getFirstRfqCode(), vo.getQuoteItem().getQuoteNumber()};
					String s = ResourceMB.getParameterizedString("wq.message.rfQCodeUsedCount", dynamicParameters)+".";
					s = s + "<br/>" + ResourceMB.getText("wq.error.inputNewRFQ")+".";
					FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, ResourceMB.getText(CommonConstants.WQ_JPA_ENTITY_MSG_CHECK_MSG)+"<br/>" + s, ""));
					return null;
				}
			}catch(Exception e){
				LOG.log(Level.SEVERE, "Exception occured for Quoted P/N: "+vo.getQuotedPartNumber()+", Exception Message: " +MessageFormatorUtil.getParameterizedStringFromException(e), e);
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, ResourceMB.getText(CommonConstants.WQ_JPA_ENTITY_MSG_CHECK_MSG)+"<br/>" + e.getMessage(), ""));
			}
		}

		try{			
			List<String> quoteNumbers = new ArrayList<String>();
			
			for(QuoteItemVo vo : selectedQuoteItemVos ){
				quoteNumbers.add(vo.getQuoteItem().getQuoteNumber());
				List<Attachment> oldAttachments = vo.getQuoteItem().getAttachments();
				
				if(null!=oldAttachments && oldAttachments.size()>0) {
					LOG.info("total attachment size"+oldAttachments.size());
					for(Attachment att:oldAttachments) {
						LOG.info("isNew"+att.isNewAttachment()+"======>>>"+ att.getType()+ "====>>>+++++"+att.getFileName());
					}
				}
				
				vo.getQuoteItem().setEau(vo.getEau());
				
			}
			this.setAttachmentContent(selectedQuoteItemVos);
			
			LOG.info("Copy BMT Quote begin: " + quoteNumbers.toString());
			
			selectedQuoteItemVos = copyRefreshQuoteSB.refreshQuote(selectedQuoteItemVos, UserInfo.getUser(),"copyBMT");
			
			quoteNumbers.clear();
			for(QuoteItemVo vo : selectedQuoteItemVos ){
				quoteNumbers.add(vo.getQuoteItem().getQuoteNumber());
			}
			
			LOG.info("Copy BMT Quote finish: " + quoteNumbers.toString());			
		}catch(Exception e){
			Date date = new Date();
			String timeStamp = String.valueOf(date.getTime());
			LOG.log(Level.WARNING, "exception in confirm Copy BMT Quote ,Exception message"+MessageFormatorUtil.getParameterizedStringFromException(e)+" time stamp  "+timeStamp);
			ResourceMB resourceMB=(ResourceMB)FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("resourceMB");
			String s = MessageFormatorUtil.getParameterizedStringFromException(e,resourceMB.getResourceLocale());
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN,s,""));
			return null;			
		}

		Collections.sort(quoteItemVos, new Comparator<QuoteItemVo>(){
			public int compare(QuoteItemVo arg0, QuoteItemVo arg1) {
				return arg0.getSeq2() - arg1.getSeq2();
			}
		});
						
		finished = true;		
		return "/RFQ/CopyBMTQuoteResult.xhtml";
	}

	private boolean validationBMTCopyQuote(List<QuoteItemVo> selectedQuoteItemVos) {
		boolean errorFound = false;
		return errorFound;
		
	}
	


	/**
	 * 
	 * @return
	 */
	public String confirmUpdateBMTInfo() {
		if(null!=this.currentQuoteItemVo) {
			String bmtFlagCode = currentQuoteItemVo.getQuoteItem().getQuoteItemDesign().getBmtFlag().getBmtFlagCode();
			if(null==bmtFlagCode || bmtFlagCode.equalsIgnoreCase("99")){
				FacesContext.getCurrentInstance().addMessage("growl",new FacesMessage(FacesMessage.SEVERITY_WARN,"",ResourceMB.getText("wq.error.bmtFlagRequired")+"."));
				return null;
			} else {
				
				String employeeId = UserInfo.getUser().getEmployeeId();
				String employeeName =  UserInfo.getUser().getName();
				
				QuoteItem item = this.currentQuoteItemVo.getQuoteItem();
				item.setLastBmtUpdateTime(new Date());
				item.setLastUpdatedBmt(employeeId);
				item.setLastUpdatedBy(employeeId);
				item.setLastUpdatedName(employeeName);
				
				QuoteItemDesign design = item.getQuoteItemDesign();
				User currentUser = UserInfo.getUser();
				design.setLastUpdatedBy(currentUser.getEmployeeId());
				design.setLastUpdatedTime(new Date());
//				design = quoteSB.updateQuoteItemDesign(design);
//				item.setQuoteItemDesign(design); // set new design back to item 
				item.setAction(ActionEnum.MYQUOTE_UPDATE_BMT_QUOTE.name()); // add audit action 
				QuoteItem newItem = quoteSB.updateQuoteItem(item);
				
				this.currentQuoteItemVo.setQuoteItem(newItem);
				
				
				LOG.info("confirmUpdateBMTInfo finish ");			
				
				RequestContext context = RequestContext.getCurrentInstance();
				
				context.addCallbackParam("saveValidationSucceed", "1");
				context.update("form");
			}
		}
		return null;
	}

	public boolean isFinished() {
		return finished;
	}

	public void setFinished(boolean finished) {
		this.finished = finished;
	}

	public List<String> getQuoteTypeList() {
		return quoteTypeList;
	}

	public void setQuoteTypeList(List<String> quoteTypeList) {
		this.quoteTypeList = quoteTypeList;
	}

	public SelectItem[] getPageBmtFlags() {
		return pageBmtFlags;
	}

	public void setPageBmtFlags(SelectItem[] pageBmtFlags) {
		this.pageBmtFlags = pageBmtFlags;
	}

	public SelectItem[] getBmtCurrencyList() {
		return bmtCurrencyList;
	}

	public void setBmtCurrencyList(SelectItem[] bmtCurrencyList) {
		this.bmtCurrencyList = bmtCurrencyList;
	}
	
	
}
