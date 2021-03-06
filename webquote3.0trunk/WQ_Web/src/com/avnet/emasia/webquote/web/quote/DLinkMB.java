package com.avnet.emasia.webquote.web.quote;

import java.util.HashMap;
import java.util.Map;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;

import com.avnet.emasia.webquote.constants.ActionEnum;
import com.avnet.emasia.webquote.constants.StageEnum;
import com.avnet.emasia.webquote.dp.EJBCommonSB;
import com.avnet.emasia.webquote.entity.Currency;
import com.avnet.emasia.webquote.entity.Customer;
import com.avnet.emasia.webquote.entity.ExchangeRate;
import com.avnet.emasia.webquote.entity.Money;
import com.avnet.emasia.webquote.entity.Quote;
import com.avnet.emasia.webquote.entity.QuoteItem;
import com.avnet.emasia.webquote.entity.SalesCostType;
import com.avnet.emasia.webquote.entity.User;
import com.avnet.emasia.webquote.exception.AppException;
import com.avnet.emasia.webquote.exception.CommonConstants;
import com.avnet.emasia.webquote.quote.ejb.CopyRefreshQuoteSB;
import com.avnet.emasia.webquote.quote.ejb.QuoteSB;
import com.avnet.emasia.webquote.quote.ejb.SAPWebServiceSB;
import com.avnet.emasia.webquote.quote.ejb.SystemCodeMaintenanceSB;
import com.avnet.emasia.webquote.quote.ejb.constant.QuoteSBConstant;
import com.avnet.emasia.webquote.quote.vo.QuoteItemVo;
import com.avnet.emasia.webquote.strategy.StrategyFactory;
import com.avnet.emasia.webquote.utilites.resources.ResourceMB;
import com.avnet.emasia.webquote.utilites.web.common.Constants;
import com.avnet.emasia.webquote.utilites.web.util.QuoteUtil;
import com.avnet.emasia.webquote.utilities.MessageFormatorUtil;
import com.avnet.emasia.webquote.web.user.UserInfo;
import com.sap.document.sap.soap.functions.mc_style.TableOfZquoteMsg;
import com.sap.document.sap.soap.functions.mc_style.ZquoteMsg;
import com.avnet.emasia.webquote.web.quote.strategy.SendQuotationEmailStrategy;
//import com.avnet.emasia.webquote.web.quote.strategy.SendQuotationEmailWithAttachStrategy;
import com.avnet.emasia.webquote.web.quote.vo.QuotationEmailVO;

@ManagedBean
@SessionScoped
public class DLinkMB implements Serializable {
	
	private static final long serialVersionUID = -2115128979336099333L;

	//private static final Logger LOG =Logger.getLogger(DLinkMB.class.getName());
	
	@EJB
	QuoteSB quoteSB;
	
	@EJB
	SAPWebServiceSB webServiceSB;
	
	@EJB
	private SystemCodeMaintenanceSB sysMaintSB;

	@EJB
	protected EJBCommonSB ejbCommonSB;

	private static final Logger logger = Logger.getLogger(DLinkMB.class.getName());
	
	private List<QuoteItemVo> quoteItemVos;
	
	private List<QuoteItemVo> selectedQuoteItemVos;
	
	private List<QuoteItemVo> filteredQuoteItemVos;

	private Currency selectedCurrency;
	
	private boolean finished;
	
	@ManagedProperty(value="#{resourceMB}")
	/** To Inject ResourceMB instance  */
    private ResourceMB resourceMB;
	
	
	
	public Currency getSelectedCurrency() {
		return selectedCurrency;
	}

	public void setSelectedCurrency(Currency selectedCurrency) {
		this.selectedCurrency = selectedCurrency;
	}

	public void checkCopyQuoteItem(List<QuoteItemVo> quoteItemVos){
		this.quoteItemVos = quoteItemVos;
		int i = 1;
		for(QuoteItemVo vo : quoteItemVos){
			vo.setSeq2(i++);
		}
		selectedQuoteItemVos = quoteItemVos.stream().filter(item -> item.isErrRow()!=true).collect(Collectors.toList());
		if (selectedQuoteItemVos.size()>0){
			selectedCurrency = selectedQuoteItemVos.get(0).getQuoteItem().getQuote().getBizUnit().getDefaultCurrency();
		}
		else {
			selectedCurrency = quoteItemVos.get(0).getQuoteItem().getQuote().getBizUnit().getDefaultCurrency();
		}

		for(QuoteItemVo vo : quoteItemVos){
			if (!vo.isErrRow()) {
				ExchangeRate exRates[] = Money.getExchangeRateForDlink(vo.getQuoteItem().getBuyCurr(), Currency.JPY, vo.getQuoteItem().getQuote().getBizUnit(), vo.getQuoteItem().getSoldToCustomer(), new Date());
				if (exRates.length>1){
					if (exRates[0]!=null && exRates[1] != null){
						vo.getQuoteItem().setRateRemarks(exRates[1].getRemarks());
					} else {
						vo.getQuoteItem().setRateRemarks(null);
					}
				} else {
					vo.getQuoteItem().setRateRemarks(null);
				}
			} else vo.getQuoteItem().setRateRemarks(null);		//BA request hide the remark if is error
		}
	}

	public void copyQuote(){
		logger.info("DLinkMB.copyQuote: Begin");		
		
		if(selectedQuoteItemVos.size() == 0){  
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,ResourceMB.getText("wq.message.noRecordSelected")+" !", "")); 
			return;
		}
		
		User user  = UserInfo.getUser();
		
		logger.info("DLinkMB: Create $Link Qoute. User:" + user.getEmployeeId() + " , QuoteNumber:" + selectedQuoteItemVos);		
		
		List<Integer> errorRows = new ArrayList<Integer>();
		
		List<String> errorMsgs = new ArrayList<String>();
		
		//DecimalFormat df = (DecimalFormat)DecimalFormat.getNumberInstance(resourceMB.getResourceLocale());
		//df.setMaximumFractionDigits(5);
		DecimalFormat df = new DecimalFormat("#,###,###,##0.00000"); 

		//1) group selected Quote by FormNumber
		//Map<String, List<QuoteItem>> map = new HashMap<String, List<QuoteItem>>();
		Map<String, List<QuoteItemVo>> map = new HashMap<String, List<QuoteItemVo>>();

		for (QuoteItemVo vo : selectedQuoteItemVos) {
			QuoteItem item = vo.getQuoteItem();
			Quote quote = item.getQuote();

			List<QuoteItemVo> list = map.get(quote.getFormNumber());

			//QuoteItem newItem = QuoteItem.newInstance(vo.getQuoteItem());
			
			if (list == null) {
				list = new ArrayList<QuoteItemVo>();
				list.add(vo);
				map.put(quote.getFormNumber(), list);
			} else {
				list.add(vo);
			}

		}
		
		
		//2) Loop selected QuoteItem according to FormNumber for populateQuoteNumber //xxx, and create new quote with new FormNumber and QuoteNumber accordingly
		for (String formNumber : map.keySet()) {

			List<QuoteItemVo> vosInSameQuote = map.get(formNumber);

			Quote oldQuote = vosInSameQuote.get(0).getQuoteItem().getQuote();

			Quote newQuote = Quote.newInstance(oldQuote); //copyQuoteSpecific(oldQuote, currentUser);
			
			//newQuote.setStage(StageEnum.PENDING.toString());
			//newQuote.setStage(StageEnum.FINISH.toString());
			int seq = 1;

			//3) Update new Quote info
			for (QuoteItemVo vo : vosInSameQuote) {

				QuoteItem oldItem = vo.getQuoteItem();
//				QuoteItemDesign oldDesign = oldItem.getQuoteItemDesign();
				
				QuoteItem newItem = QuoteItem.newInstance(oldItem); //copyQuoteItem(oldItem, UserInfo.getUser()); //copyQuoteItem(oldItem, currentUser);
				
				newItem.setRfqCurr(oldItem.getFinalCurr());
				newItem.setFinalCurr(selectedCurrency);
				newItem.setQuotedPrice(oldItem.getFinalQuotationPrice());
				newItem.setExRateRfq(oldItem.getExRateFnl());
				
				//Money money = currConvert(oldItem);
				
				//money.getExchangeRate(newItem.getBuyCurr(), newItem.getFinalCurr(), newItem.getQuote().getBizUnit(), newItem.getSoldToCustomer(), new Date());
				
				//newItem.setExRateFnl(money.getExchangeRate(newItem.getBuyCurr(), newItem.getFinalCurr(), newItem.getQuote().getBizUnit(), newItem.getSoldToCustomer(), new Date())[0]);
				//ExchangeRate exRate = Money.getExchangeRate(newItem.getBuyCurr(), newItem.getFinalCurr(), newItem.getQuote().getBizUnit(), newItem.getSoldToCustomer(), new Date())[0];
				ExchangeRate exRates[] = Money.getExchangeRateForDlink(newItem.getBuyCurr(), newItem.getFinalCurr(), newItem.getQuote().getBizUnit(), newItem.getSoldToCustomer(), new Date());
				if (exRates.length>1){
					if (exRates[0]!=null && exRates[1] != null){
						newItem.setExRateFnl(exRates[1].getExRateTo());
		    	        newItem.setRateRemarks(exRates[1].getRemarks());
		    	        newItem.setExRateBuy(exRates[0].getExRateTo());
					} else {
						newItem.setExRateFnl(null);
		    	        newItem.setRateRemarks(null);
						
					}
				} else {
					newItem.setExRateFnl(null);
	    	        newItem.setRateRemarks(null);
					
				}
				
				//newItem.setFinalQuotationPrice(currConvert(newItem).getAmount().doubleValue());
				newItem.setFinalQuotationPrice(currConvert(newItem).getAmount().doubleValue());

				//newItem.setExRateRfq(oldItem.getFina);
    	        
    	        //newItem.setRemarks(newItem.findExchangeRate().getRemarks());

    	        newItem.setResaleIndicator("0000");
    	        newItem.setStatus(QuoteSBConstant.RFQ_STATUS_AQ);
    	        newItem.setStage(StageEnum.FINISH.toString());
    	        
				if (newItem.getQuoteItemDesign() != null) {
					newItem.getQuoteItemDesign().setCreatedBy(user.getEmployeeId());
					newItem.getQuoteItemDesign().setLastUpdatedBy(user.getEmployeeId());
					newItem.getQuoteItemDesign().setCreatedTime(new Date());
					newItem.getQuoteItemDesign().setLastUpdatedTime(new Date());
				}
				
				//20181218: request to reset the sentOutTime and submission Date
				newItem.setSentOutTime(QuoteUtil.getCurrentTime());
				newItem.setSubmissionDate(QuoteUtil.getCurrentTime());
				
				newItem.setVersion(0);
				newItem.setId(0L);
				newItem.setItemSeq(seq++);
				newItem.setQuote(newQuote);

				newQuote.addItem(newItem);
				//quoteSB.applyDefaultCostIndicatorLogic(newItem, true);
			}
			
			quoteSB.populateQuoteNumber(newQuote);
			String quoteNumber = "";
			for (QuoteItem item : newQuote.getQuoteItems()) {
				if (item.getFirstRfqCode() == null) {
					item.setFirstRfqCode(item.getQuoteNumber());
				}
				quoteNumber = quoteNumber + "," + item.getQuoteNumber();
			}

			//4) save Quote
			//quoteSB.saveQuote(newQuote);
			Quote quote = quoteSB.saveQuote(newQuote);
			
			//5) send email
			//sendEmail(quote, user);
			String strategyKey=user.getDefaultBizUnit().getName();
			if(quote.isdLinkFlag()){
				strategyKey=Constants.DEFAULT;
			}

			SendQuotationEmailStrategy strategy =(SendQuotationEmailStrategy) StrategyFactory.getSingletonFactory()
					.getStrategy(SendQuotationEmailStrategy.class, strategyKey,
							this.getClass().getClassLoader());
			strategy.sendQuotationEmail(strategy.genQuotationEmailVO(quote, user));

			// send to sap
			if (null != quote && quote.getQuoteItems()!=null && quote.getQuoteItems().size() > 0) {
				logger.info( "DLinkMB.copyQuote>createSAPQuote " + "ID=" + quote.getId() + "; " + quote.getFormNumber() + "==QuoteNumber " + quoteNumber + " Begin." );			
				
				try {
					webServiceSB.createSAPQuote(quote.getQuoteItems());
				} catch (AppException e) {
					Date date = new Date();
					String timeStamp = String.valueOf(date.getTime());
					String s = ResourceMB.getText(e.getErrorCode());
					logger.log(Level.WARNING, "Exception in creating SAP Quote : "+timeStamp + " > " + s);
					//FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN,s,""));
				}
			}
			
			logger.info( "copyQuote " + "ID=" + quote.getId() + "; " + quote.getFormNumber() + "==QuoteNumber " + quoteNumber + " succcessfully." );			
		}
		
		finished = true;					
		quoteItemVos = selectedQuoteItemVos;		

		String doneMsg = selectedQuoteItemVos.size() + " " + ResourceMB.getText("wq.message.dLinkQuoteGenerated")+ " " 
						+ ResourceMB.getText("wq.message.emailSent");
		FacesContext.getCurrentInstance().addMessage(
				"growl",
				new FacesMessage(FacesMessage.SEVERITY_INFO, doneMsg,""));
	}

/*	
	public void sendEmail(Quote quote, User user){
		QuotationEmailVO vo = new QuotationEmailVO();
		String soldToCode = quote.getQuoteItems().get(0).getSoldToCustomer().getCustomerNumber();
		String formNumbers ="";
		
		vo.setFormNumber(quote.getFormNumber());
		vo.setQuoteId(quote.getId());
		//Customer customer = customerSB.findByPK(soldToCode);
		//vo.setHssfWorkbook(getQuoteTemplateBySoldTo(customer, submitQuote));
		vo.setQuote(quote);
		vo.setSoldToCustomer(quote.getSoldToCustomer());
		//vo.setSoldToCustomer(customer);
		vo.setSubmissionDateFromQuote(true);
		vo.setRecipient(user.getName());
		//vo.setRecipient(rfqHeader.getSalesEmployeeName());
		
		String regionCodeName = quote.getBizUnit().getName();
		vo.setSender(sysMaintSB.getEmailSignName(regionCodeName) + "<br/>"
				+ sysMaintSB.getEmailHotLine(regionCodeName) + "<br/>Email Box: "
				+ sysMaintSB.getEmailSignContent(regionCodeName) + "<br/>");
		vo.setFromEmail(sysMaintSB.getEmailAddress(regionCodeName));

		//String fullCustomerName = getCustomerFullName(quote.getSoldToCustomer());
		String fullCustomerName = quote.getSoldToCustomer().getCustomerFullName();

		vo = ejbCommonSB.commonLogicForDpRFQandRFQ(quote, vo, quote.getSoldToCustomer(), user,
				fullCustomerName);

		formNumbers += quote.getFormNumber() + ";";
		//sendQuotationEmail(vo);
		String strategyKey=user.getDefaultBizUnit().getName();
		if(quote.isdLinkFlag()){
			strategyKey=Constants.DEFAULT;
		}

		SendQuotationEmailWithAttachStrategy strategy =(SendQuotationEmailWithAttachStrategy) StrategyFactory.getSingletonFactory()
				.getStrategy(SendQuotationEmailWithAttachStrategy.class, strategyKey,
						this.getClass().getClassLoader());

		SendQuotationEmailStrategy strategy =(SendQuotationEmailStrategy) StrategyFactory.getSingletonFactory()
				.getStrategy(SendQuotationEmailStrategy.class, strategyKey,
						this.getClass().getClassLoader());
		strategy.sendQuotationEmail(vo);

			SendQuotationEmailStrategy strategy = (SendQuotationEmailStrategy) cacheUtil.getStrategy(SendQuotationEmailStrategy.class,
				strategyKey);
		
		SendQuotationEmailStrategy strategy =(SendQuotationEmailStrategy) StrategyFactory.getSingletonFactory()
				.getStrategy(SendQuotationEmailStrategy.class,strategyKey,
						this.getClass().getClassLoader());
		strategy.sendQuotationEmail(vo);
		
	}
*/	

	public Money currConvert(QuoteItem item) {
		//Money money = Money.of(this.minSellPrice, Currency.USD);
		Money money = Money.of(item.getQuotedPrice(), Currency.USD);
		
		if(money ==null){
			return null;
		}
		//Money.getExchangeRate(item.getBuyCurr(), item.getFinalCurr(), item.getQuote().getBizUnit(), item.getSoldToCustomer(), new Date());
		//money.convertTo(Currency.getByCurrencyName(this.rfqCurr), new BizUnit(this.getRfqHeaderVO().getBizUnit()), this.soldToCustomer, new Date());
		return money.convertToForDlink(Currency.JPY, item.getQuote().getBizUnit(), item.getSoldToCustomer(), new Date());
		//return money.convertToForDlink(item.getRfqCurr(), item.getQuote().getBizUnit(), item.getSoldToCustomer(), new Date());
	}

	public String defaultCurr() {
		//this.selectedQuoteItemVos.get(0).quoteItem.quote.bizUnit.defaultCurrency
		Currency currReturn = this.getSelectedQuoteItemVos().get(0).getQuoteItem().getQuote().getBizUnit().getDefaultCurrency();
		return currReturn==null? "USD": currReturn.name();
	}

	//Getters, Setters	
	public List<QuoteItemVo> getQuoteItemVos() {
		return quoteItemVos;
	}

	public void setQuoteItemVos(List<QuoteItemVo> quoteItemVos) {
		this.quoteItemVos = quoteItemVos;
	}

	public List<QuoteItemVo> getSelectedQuoteItemVos() {
		return selectedQuoteItemVos;
	}

	public void setSelectedQuoteItemVos(List<QuoteItemVo> selectedQuoteItemVos) {
		this.selectedQuoteItemVos = selectedQuoteItemVos;
	}
	
	public boolean isFinished() {
		return finished;
	}

	public void setFinished(boolean finished) {
		this.finished = finished;
	}

	public List<QuoteItemVo> getFilteredQuoteItemVos() {
		return filteredQuoteItemVos;
	}

	public void setFilteredQuoteItemVos(List<QuoteItemVo> filteredQuoteItemVos) {
		this.filteredQuoteItemVos = filteredQuoteItemVos;
	}

	/**
	 * Setter for resourceMB
	 * @param resourceMB ResourceMB
	 * */
	public void setResourceMB(final ResourceMB resourceMB) {
		this.resourceMB = resourceMB;
	}
}
                    
