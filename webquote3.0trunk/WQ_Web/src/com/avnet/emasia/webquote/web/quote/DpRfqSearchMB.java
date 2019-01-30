package com.avnet.emasia.webquote.web.quote;

import java.io.Serializable;
import java.util.ArrayList;
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

import org.apache.commons.lang.StringUtils;

import com.avnet.emasia.webquote.constants.DpStatusEnum;
import com.avnet.emasia.webquote.dp.DpRfqProcessSB;
import com.avnet.emasia.webquote.dp.DpRfqSubmissionSB;
import com.avnet.emasia.webquote.entity.DpRfqItem;
import com.avnet.emasia.webquote.entity.QuoteItem;
import com.avnet.emasia.webquote.entity.User;
import com.avnet.emasia.webquote.exception.AppException;
import com.avnet.emasia.webquote.quote.ejb.QuoteSB;
import com.avnet.emasia.webquote.quote.ejb.SAPWebServiceSB;
import com.avnet.emasia.webquote.quote.ejb.constant.QuoteSBConstant;
import com.avnet.emasia.webquote.quote.vo.DpRfqSearchCriteria;
import com.avnet.emasia.webquote.utilites.resources.ResourceMB;
import com.avnet.emasia.webquote.utilities.MessageFormatorUtil;
import com.avnet.emasia.webquote.web.user.UserInfo;
import com.sap.document.sap.soap.functions.mc_style.TableOfZquoteMsg;
import com.sap.document.sap.soap.functions.mc_style.ZquoteMsg;

@ManagedBean
@SessionScoped
public class DpRfqSearchMB implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	
	private static final Logger LOG =Logger.getLogger(DpRfqSearchMB.class.getName());

	@EJB
	private DpRfqSubmissionSB dpRfqSubmissionSB;
	
	@EJB
	private DpRfqProcessSB dpRfqProcessSB;

	private List<DpRfqItem> dpRfqItems;
	
	private List<DpRfqItem> filtereddpRfqItem;
	
	
	private DpRfqSearchCriteria criteria;
	
	private List<DpRfqItem> selectedDpRfqItems = new ArrayList<DpRfqItem>();
	
	@EJB
	private QuoteSB quoteSB;
	
	@EJB
	private SAPWebServiceSB sapWebServiceSB;
	

	

	public DpRfqSearchMB(){

	}

	@PostConstruct
	public void postContruct(){
		criteria = new DpRfqSearchCriteria();
	}

	public void search(){

		try {
			criteria.setupUIInCriteria();
			User user = UserInfo.getUser();
			criteria.setBizUnits(user.getBizUnits());
			dpRfqItems = dpRfqSubmissionSB.search(criteria);
			int seq = 1;
			if(dpRfqItems != null && dpRfqItems.size() > 0) {
				for(DpRfqItem item : dpRfqItems) {
					item.setSeq(seq++);
				}
			}
			LOG.log(Level.INFO, dpRfqItems.size() + " records is found for Dp Rfq");
		} catch (Exception e) {
			LOG.log(Level.SEVERE, "Exception in search , Exception message : "+e.getMessage(), e);
		}

	}
	
	public void resendToDP() {
		if (selectedDpRfqItems.size() == 0) {
			FacesContext.getCurrentInstance().addMessage(
					"growl",
					new FacesMessage(FacesMessage.SEVERITY_WARN,ResourceMB.getText("wq.message.selRecord")+" !", ""));
			return;
		}
		boolean canGo = true;
		DpRfqItem tempDpRfqItem = null;
		for(DpRfqItem item : selectedDpRfqItems) {
			if(StringUtils.isBlank(item.getQuoteLineStatus())) {
				canGo = false;
				tempDpRfqItem = item;
				break;
			}
		}
		if(!canGo && tempDpRfqItem != null ) {
			FacesContext.getCurrentInstance().addMessage(
					"growl",
					new FacesMessage(FacesMessage.SEVERITY_WARN,ResourceMB.getText("wq.message.dprfq.oneRecord")+":" 
					+ tempDpRfqItem.getReferenceLineId(), ""));
			return;
		}
		
		dpRfqProcessSB.resendMessage(selectedDpRfqItems);
		
			
	}
	
	public void invalidQuote() {
		if (selectedDpRfqItems.size() == 0) {
			FacesContext.getCurrentInstance().addMessage(
					"growl",
					new FacesMessage(FacesMessage.SEVERITY_WARN,ResourceMB.getText("wq.message.selRecord")+" !", ""));
			return;
		}
		
		boolean canGo = true;
		DpRfqItem tempDpRfqItem = null;
		for(DpRfqItem item : selectedDpRfqItems) {
			if(!StringUtils.equals(DpStatusEnum.QUOTED.code(), item.getQuoteLineStatus())) {
				canGo = false;
				tempDpRfqItem = item;
				break;
			}
		}
		if(!canGo && tempDpRfqItem != null ) {
			FacesContext.getCurrentInstance().addMessage(
					"growl",
					new FacesMessage(FacesMessage.SEVERITY_WARN,ResourceMB.getText("wq.message.dprfq.oneRecordQuoted")+":" 
					+ tempDpRfqItem.getReferenceLineId(), ""));
			return;
		}
		
		List<QuoteItem> sapQuoteItems = new ArrayList<QuoteItem>();
		try {
			for(DpRfqItem dpRfqItem: selectedDpRfqItems) {
				QuoteItem quoteItem = dpRfqItem.getQuoteItem();
				if(quoteItem != null) {
					quoteItem.setStage(QuoteSBConstant.QUOTE_STAGE_INVALID);
					LOG.info("Invalidating " + quoteItem.getQuoteNumber());
					quoteItem.setLastUpdatedBy(UserInfo.getUser().getEmployeeId());
					quoteItem.setLastUpdatedName(UserInfo.getUser().getName());
					quoteItem.setLastUpdatedOn(new Date());
					quoteItem = quoteSB.updateQuoteItem(quoteItem);
					sapQuoteItems.add(quoteItem);
					LOG.info(quoteItem.getQuoteNumber() + " is now invalid");
				}
			}
			
			if (sapQuoteItems != null && sapQuoteItems.size() > 0) {
				LOG.info("<<<<<<<<<<< Call SAP WebService  >>>>> ");
				TableOfZquoteMsg tableMsg = sapWebServiceSB.createSAPQuote(sapQuoteItems);
				List<ZquoteMsg> msgs = tableMsg.getItem();
				List<String> errorMsgs = new ArrayList<String>();
				for (ZquoteMsg msg : msgs) {
					if (!msg.getType().equalsIgnoreCase("S")) {
						errorMsgs.add(msg.getMessage());
					}
				}

				if (errorMsgs.size() != 0) {
					LOG.info(errorMsgs.toString());
				}
			}
			dpRfqProcessSB.resendMessage(selectedDpRfqItems);
			
		} catch (AppException e) {
			LOG.log(Level.SEVERE, MessageFormatorUtil.getParameterizedStringFromException(e),e);
		}
			
	}
	

	
	
	public List<DpRfqItem> getDpRfqItems() {
		return dpRfqItems;
	}

	public void setDpRfqItems(List<DpRfqItem> dpRfqItems) {
		this.dpRfqItems = dpRfqItems;
	}

	public List<DpRfqItem> getFiltereddpRfqItem() {
		return filtereddpRfqItem;
	}

	public void setFiltereddpRfqItem(List<DpRfqItem> filtereddpRfqItem) {
		this.filtereddpRfqItem = filtereddpRfqItem;
	}

	public DpRfqSearchCriteria getCriteria() {
		return criteria;
	}

	public void setCriteria(DpRfqSearchCriteria criteria) {
		this.criteria = criteria;
	}

	public List<DpRfqItem> getSelectedDpRfqItems() {
		return selectedDpRfqItems;
	}

	public void setSelectedDpRfqItems(List<DpRfqItem> selectedDpRfqItems) {
		this.selectedDpRfqItems = selectedDpRfqItems;
	}
	
}

