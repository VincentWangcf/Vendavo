package com.avnet.emasia.webquote.web.quote;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import com.avnet.emasia.webquote.utilities.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.TimeZone;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.faces.model.SelectItem;
import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.MapMessage;
import javax.jms.MessageProducer;
import javax.jms.QueueConnectionFactory;
import javax.jms.Session;
import javax.naming.InitialContext;
import javax.servlet.http.HttpServletRequest;

import org.apache.cxf.common.util.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.eclipse.jetty.util.log.Log;
import org.primefaces.component.datatable.DataTable;
import org.primefaces.context.RequestContext;
import org.primefaces.event.SelectEvent;
import org.primefaces.event.UnselectEvent;
import org.primefaces.event.data.PageEvent;
import org.primefaces.model.UploadedFile;

import com.avnet.emasia.webquote.component.show.config.ComponetConfigService;
import com.avnet.emasia.webquote.constants.ActionEnum;
import com.avnet.emasia.webquote.constants.BmtFlagEnum;
import com.avnet.emasia.webquote.constants.QuoteSourceEnum;
import com.avnet.emasia.webquote.constants.RemoteEjbClassEnum;
import com.avnet.emasia.webquote.constants.StageEnum;
import com.avnet.emasia.webquote.constants.StatusEnum;
import com.avnet.emasia.webquote.entity.BizUnit;
import com.avnet.emasia.webquote.entity.Currency;
import com.avnet.emasia.webquote.entity.Customer;
import com.avnet.emasia.webquote.entity.ExcCurrencyDefault;
import com.avnet.emasia.webquote.entity.ExcelReport;
import com.avnet.emasia.webquote.entity.ExchangeRate;
import com.avnet.emasia.webquote.entity.Manufacturer;
import com.avnet.emasia.webquote.entity.ManufacturerMapping;
import com.avnet.emasia.webquote.entity.Material;
import com.avnet.emasia.webquote.entity.Money;
import com.avnet.emasia.webquote.entity.Quote;
import com.avnet.emasia.webquote.entity.QuoteItem;
import com.avnet.emasia.webquote.entity.Role;
import com.avnet.emasia.webquote.entity.Team;
import com.avnet.emasia.webquote.entity.User;
import com.avnet.emasia.webquote.exception.WebQuoteException;
import com.avnet.emasia.webquote.quote.ejb.BalanceUnconsumedQtySB;
import com.avnet.emasia.webquote.quote.ejb.CopyRefreshQuoteSB;
import com.avnet.emasia.webquote.quote.ejb.CustomerSB;
import com.avnet.emasia.webquote.quote.ejb.ExchangeRateSB;
import com.avnet.emasia.webquote.quote.ejb.ManufacturerSB;
import com.avnet.emasia.webquote.quote.ejb.MaterialSB;
import com.avnet.emasia.webquote.quote.ejb.MyQuoteSearchSB;
import com.avnet.emasia.webquote.quote.ejb.QuoteSB;
import com.avnet.emasia.webquote.quote.ejb.SapMaterialSB;
import com.avnet.emasia.webquote.quote.ejb.constant.QuoteSBConstant;
import com.avnet.emasia.webquote.quote.vo.MyQuoteSearchCriteria;
import com.avnet.emasia.webquote.quote.vo.QuoteItemVo;
import com.avnet.emasia.webquote.reports.ejb.ExcelReportSB;
import com.avnet.emasia.webquote.reports.ejb.OfflineReportSB;
import com.avnet.emasia.webquote.reports.util.ReportConvertor;
import com.avnet.emasia.webquote.strategy.StrategyFactory;
import com.avnet.emasia.webquote.user.ejb.TeamSB;
import com.avnet.emasia.webquote.user.ejb.UserSB;
import com.avnet.emasia.webquote.utilites.resources.ResourceMB;
import com.avnet.emasia.webquote.utilites.web.common.CacheUtil;
import com.avnet.emasia.webquote.utilites.web.common.Constants;
import com.avnet.emasia.webquote.utilites.web.util.DownloadUtil;
import com.avnet.emasia.webquote.utilities.MessageFormatorUtil;
import com.avnet.emasia.webquote.utilities.common.SysConfigSB;
import com.avnet.emasia.webquote.utilities.util.QuoteUtil;
import com.avnet.emasia.webquote.vo.OfflineReportParam;
import com.avnet.emasia.webquote.web.datatable.SelectableLazyDataModel;
import com.avnet.emasia.webquote.web.quote.constant.QuoteConstant;
import com.avnet.emasia.webquote.web.quote.strategy.SendQuotationEmailStrategy;
import com.avnet.emasia.webquote.web.quote.vo.QuotationEmailVO;
import com.avnet.emasia.webquote.web.user.UserInfo;

@ManagedBean
@SessionScoped
//NEC Pagination Changes: Class modified for implementing lazy loading in pagination
public class MyQuoteSearchMB extends CommonBean  implements Serializable {

	private static final long serialVersionUID = 4947932561511214150L;

	private static final Logger LOG = Logger.getLogger(MyQuoteSearchMB.class
			.getName());
	@EJB
	private CacheUtil cacheUtil;
	
	@EJB
	ExcelReportSB excelReportSB;
	
	@EJB
	SysConfigSB sysConfigSB;

	@EJB
	MyQuoteSearchSB myQuoteSearchSB;

	@EJB
	MaterialSB materialSB;
	
	@EJB
	CopyRefreshQuoteSB copyQuoteSB;
	
	@EJB 
	QuoteSB quoteSB;

	@EJB
	CustomerSB customerSB;

	@EJB
	UserSB userSB;

	@EJB
	ManufacturerSB manufacturerSB;

	@EJB
	TeamSB teamSB;

	@EJB
	OfflineReportSB offlineReprtSB;
	
	@EJB
	ExchangeRateSB exchangeRateSB;		

	@EJB
	SapMaterialSB sapMaterialSB;

	@EJB
	BalanceUnconsumedQtySB balanceUnconsumedQtySB;

	@ManagedProperty("#{copyQuoteMB}")
	CopyQuoteMB copyQuoteMB;
	
	@ManagedProperty("#{bmtInfoMB}")
	BmtInfoMB bmtInfoMB;

	@ManagedProperty("#{refreshQuoteMB}")
	RefreshQuoteMB refreshQuoteMB;

	@ManagedProperty("#{reviseFinalQuotedPriceMB}")
	ReviseFinalQuotedPriceMB reviseFinalQuotedPriceMB;
	
	@ManagedProperty("#{dLinkMB}")
	DLinkMB dLinkMB;
	
	@EJB
	AsynReviseFinalQuotedPriceBean asynReviseFinalQuotedPriceBean;
	
	private MyQuoteSearchCriteria criteria;

	//private List<QuoteItemVo> quoteItemVos = new ArrayList<QuoteItemVo>();
	//NEC Pagination changes: initialize the Lazy data  model for supporting the lazy loading in pagination
	private LazyDataModelForMyQuoteSearch quoteItemVos = new LazyDataModelForMyQuoteSearch();

	private List<QuoteItemVo> selectedQuoteItemVos = new ArrayList<QuoteItemVo>();
	
	private List<QuoteItemVo> proceedQuoteItemVos = new ArrayList<QuoteItemVo>();

	private List<QuoteItemVo> filteredQuoteItemVos;
	
	private QuoteItemVo currentQuoteItemVo = new QuoteItemVo();

	private List<String> sapMaterialList;

	private boolean recordsExceedMaxAllowed = false;
	
	
	private String pageRoleName="QC";
	

	// In My Form Search for Sales, if user is jumped from My Form Search, need
	// show Form Info in the page
	private boolean showFormForSales;

	// QC Operation and QC pricing share same search page
	private boolean qcOprSearchButton = true;
	private boolean qcPriSearchButton = false;

	// Sales and Inside Sales share same search page
	private boolean insideSalesSearchButton = false;
	private boolean csSearchButton = false;
	private boolean salesSearchButton = false;

	private long copyHeaderQuoteId;
	
	private SelectItem[] exCurrencyList;
	
	private String exCurrency;
	
	@ManagedProperty(value="#{resourceMB}")
	/** To Inject ResourceMB instance  */
    private ResourceMB resourceMB;

	//Multi Currency Project 201810 (whwong)
	private boolean showDLink = false;
	private boolean showQRR = true;
	
	
	public boolean isShowDLink() {
		return showDLink;
	}

	public void setShowDLink(boolean showDLink) {
		this.showDLink = showDLink;
	}

	public boolean isShowQRR() {
		return showQRR;
	}

	public void setShowQRR(boolean showQRR) {
		this.showQRR = showQRR;
	}

	/*	
	private boolean japanRegion =false;
	
	public boolean isJapanRegion() {
		return japanRegion;
	}

	public void setJapanRegion(boolean japanRegion) {
		this.japanRegion = japanRegion;
	}
*/
	public boolean isCsSearchButton() {
		return csSearchButton;
	}

	public void setCsSearchButton(boolean csSearchButton) {
		this.csSearchButton = csSearchButton;
	}

	public boolean isSalesSearchButton() {
		return salesSearchButton;
	}

	public void setSalesSearchButton(boolean salesSearchButton) {
		this.salesSearchButton = salesSearchButton;
	}

	private String myQuotePage;

	private transient Method lastSearchMethod;

	// When user click return to MyQuote page at refresh/copy quote, revise
	// final quote, we
	// let user come back to the page number that he is from in my quote
	private boolean keepFirstRow = false;
	private int firstRow = 0;
	
	private String defaultBmtFlag="All Types";

	// User may click the link in the email and will search this form number
	private String quoteId;

	private User currentUser = null;
	
	private transient UploadedFile quoteExcel;
   


	@PostConstruct
	public void postContruct() {
		
		

		currentUser = UserInfo.getUser();
		criteria = new MyQuoteSearchCriteria();
		showFormForSales = false;

		//
		List<StatusEnum> statuss = Arrays.asList(StatusEnum.values());
		List<String> strStatuss = new ArrayList<String> ();
		for(StatusEnum stt:statuss) {
			strStatuss.add(stt.toString());
		}
		criteria.setStatus(strStatuss);
		criteria.setPageStatus(strStatuss);

		//
		List<StageEnum> stages = Arrays.asList(StageEnum.values());
		List<String> strStages = new ArrayList<String>();
		List<String> bmtStages = new ArrayList<String>();
		for(StageEnum stg:stages) {
			strStages.add(stg.toString());
			if(!StageEnum.DRAFT.toString().equalsIgnoreCase(stg.toString())) {
				bmtStages.add(stg.toString());
			}
		}
		criteria.setStage(strStages);
		criteria.setPageStage(strStages);
		criteria.setPageSalesCSStage(strStages);
		criteria.setPageBMTStage(bmtStages);
		
		List<String> pendings = new ArrayList<String>();
		pendings.add("QC");
		pendings.add("PM");
		pendings.add("BMT");
		pendings.add("SQ");
		criteria.setPagePendings(pendings);
		criteria.setPending(pendings);
		
		List<String> flagList = new ArrayList<String>();
		flagList.add(defaultBmtFlag);
		List<BmtFlagEnum> flags = Arrays.asList(BmtFlagEnum.values());
		
		for(BmtFlagEnum flagEnum : flags) {
			if(!"99".equalsIgnoreCase(flagEnum.code())) {
				flagList.add(flagEnum.description());
			}
			
		}
		criteria.setPageBMTFlags(flagList);
		criteria.setSelectBmtFlag(flagList);
		


		User user = UserInfo.getUser();
        //Added for Sales Cost project by DamonChen
		//criteria.setSalsCostAccessFlag(user.isSalsCostAccessFlag());
		//don't need to be controlled by user's sales cost access flag ,by DamonChen@21071127
		criteria.setSalsCostAccessFlag(null);
		List<Role> roles = user.getRoles();
		
		Date date = new Date();
		int dateInterval = -3;

		for (Role role : roles) {
			if (role.getName().contains("ROLE_QC_OPERATION")
					|| role.getName().contains("ROLE_QCO_MANAGER")
					|| role.getName().contains("ROLE_QC_Director")) {
				qcOprSearchButton = true;
				qcPriSearchButton = false;
				criteria.setPageStage(strStages);
				break;
			}
			if (role.getName().contains("ROLE_QC_PRICING")
					|| role.getName().contains("ROLE_QCP_MANAGER")
					|| role.getName().contains("ROLE_CM")
					|| role.getName().contains("ROLE_CM_MANAGER")
					|| role.getName().contains("ROLE_BOM")) {
				qcOprSearchButton = false;
				qcPriSearchButton = true;
				criteria.setPageStage(strStages);
				break;
			}
			if (role.getName().contains("ROLE_INSIDE_SALES")) {
				insideSalesSearchButton = true;
				break;
			}
		}
			
		for (Role role : roles) {
			if (role.getName().contains("ROLE_SALES")
					|| role.getName().contains("ROLE_INSIDE_SALES")
					|| role.getName().contains("ROLE_SALES_MANAGER")
					|| role.getName().contains("ROLE_SALES_GM")
					|| role.getName().contains("ROLE_SALES_DIRECTOR")
					|| role.getName().contains("ROLE_CS")
					|| role.getName().contains("ROLE_CS_MANAGER")) {

				criteria.setPageUploadDateFrom(getDate(Calendar.MONTH, dateInterval));
				criteria.setPageUploadDateTo(date);

				criteria.setPageSentOutDateFrom(getDate(Calendar.MONTH, dateInterval));
				criteria.setPageSentOutDateTo(date);

			} else if (role.getName().contains("ROLE_QC_PRICING")
					|| role.getName().contains("ROLE_QCP_MANAGER")
					|| role.getName().contains("ROLE_CM")
					|| role.getName().contains("ROLE_CM_MANAGER")
					|| role.getName().contains("ROLE_QC_OPERATION")
					|| role.getName().contains("ROLE_QCO_MANAGER")
					|| role.getName().contains("ROLE_QC_Director")) {

				criteria.setPageUploadDateFrom(getDate(Calendar.MONTH, dateInterval));
				criteria.setPageUploadDateTo(date);

				criteria.setPageSentOutDateFrom(getDate(Calendar.MONTH, dateInterval));
				criteria.setPageSentOutDateTo(date);
			} else if (role.getName().contains("ROLE_PM")
					|| role.getName().contains("ROLE_PM_SPM")
					|| role.getName().contains("ROLE_PM_BUM")
					|| role.getName().contains("ROLE_PM_MD")
					|| role.getName().contains("ROLE_MM")
					|| role.getName().contains("ROLE_MM_MANAGER")
					|| role.getName().contains("ROLE_MM_DIRECTOR")) {
				criteria.setPageSentOutDateFrom(getDate(Calendar.MONTH, dateInterval));
				criteria.setPageSentOutDateTo(date);
			}  else {
				//dAVID Changed for BMT AND OTHER ROLE should release limit 
				//when page has not display criteria[rfq submittion date] 
				/*criteria.setPageUploadDateFrom(getDate(Calendar.MONTH, dateInterval));
				criteria.setPageUploadDateTo(date);*/

				criteria.setPageSentOutDateFrom(getDate(Calendar.MONTH, dateInterval));
				criteria.setPageSentOutDateTo(date);

			}
		
			
			//added by June for Cur RMB project 2014/07/03 --- start 
			if (role.getName().contains("ROLE_SALES")
					|| role.getName().contains("ROLE_SALES_DIRECTOR")
					|| role.getName().contains("ROLE_SALES_GM")
					|| role.getName().contains("ROLE_SALES_MANAGER")) {
				salesSearchButton = true;
				break;
			}
			if (role.getName().contains("ROLE_CS")
					|| role.getName().contains("ROLE_CS_MANAGER")) {
				this.csSearchButton = true;
				break;
			}
			//added by June for Cur RMB project 2014/07/03 --- end 
			
		}
		
		//if (currentUser.getDefaultBizUnit().getName() == "JAPAN") japanRegion = true; else japanRegion=false;
		//japanRegion = true;
		this.showDLink = ComponetConfigService.showByRegion("MyQuoteList", "showDLink", Arrays.asList(currentUser.getDefaultBizUnit().getName()));
		this.showQRR = ComponetConfigService.showByRegion("MyQuoteList", "showQRR", Arrays.asList(currentUser.getDefaultBizUnit().getName()));
		
		List<String> currencyLst = exchangeRateSB.findAllExCurrencyByBu(currentUser.getDefaultBizUnit().getName());
		exCurrencyList =  com.avnet.emasia.webquote.utilites.web.util.QuoteUtil.createFilterOptions(currencyLst.toArray(new String[currencyLst.size()]),currencyLst.toArray(new String[currencyLst.size()]),  false, false);
		
		//NEC Pagination Changes: Clear filter and sort field when reset is clicked
		final DataTable d = (DataTable) FacesContext.getCurrentInstance()
				.getViewRoot().findComponent(":form:datatable_myquotelist");
		if (d != null) {
			int first = 0;
			d.reset();
			d.setFirst(first);
		}
		//NEC Pagination changes: initialize the Lazy data  model for supporting the pagination lazy loading
		quoteItemVos = new LazyDataModelForMyQuoteSearch();
		
		
	}

	private Date getDate(int field, int amount) {
		Date date = new Date();
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		cal.add(field, amount);

		return cal.getTime();
	}
	
	public String validationRecallQuotes(List<QuoteItemVo> quoteItems) {
		clearErrorRow();
		StringBuffer sb = new StringBuffer();
		
		if(null==quoteItems || 0==quoteItems.size()) {
			sb.append(ResourceMB.getText("wq.message.selRecord")+" !<br>");
		}
		
		for (QuoteItemVo vo : quoteItems) {
			if(!StatusEnum.RBQ.toString().equalsIgnoreCase(vo.getQuoteItem().getStatus())) {
				vo.setErrRow(true);
				sb.append(ResourceMB.getText("wq.label.rbqReCalled")+".");
				break;
			}
			if(null!=vo.getQuoteItem().getQuoteItemDesign()) {
				if(!BmtFlagEnum.BMT_DR_Incomplete.code().toString().equalsIgnoreCase(vo.getQuoteItem().getQuoteItemDesign().getBmtFlag().getBmtFlagCode())) {
					vo.setErrRow(true);
					sb.append(ResourceMB.getText("wq.label.bmtDRQuote")+".");
					break;
				}
			}else {//if no quote item design, then look the bmt flag is null , not permission to be recalled
				vo.setErrRow(true);
				sb.append(ResourceMB.getText("wq.label.bmtDRQuote")+".");
				break;
			}
			if(!StageEnum.PENDING.toString().equalsIgnoreCase(vo.getQuoteItem().getStage())) {
				vo.setErrRow(true);
				sb.append(ResourceMB.getText("wq.label.pendingQuoteRecall")+".");
				break;
			}
			
		}
		
		if(null!=sb && sb.length()>0) {
			return sb.toString();
		}else {
			return "";
		}
		
	}
	public void recallQuote() {
		LOG.info("Start call recallQuote");
		
		// Changes for pagination done by NTI
		Set<QuoteItemVo> cacheItems = getCacheSelectionItems();
		selectedQuoteItemVos =  new ArrayList<>();
		selectedQuoteItemVos.addAll(cacheItems);
		
		String msg = validationRecallQuotes(this.selectedQuoteItemVos);
		if (!QuoteUtil.isEmpty(msg)) {
			FacesContext.getCurrentInstance().addMessage("growl",new FacesMessage(FacesMessage.SEVERITY_WARN,msg, ""));
			return;
		} else {
			String employeeId = UserInfo.getUser().getEmployeeId();
			String employeeName =  UserInfo.getUser().getName();
			for (QuoteItemVo vo : selectedQuoteItemVos) {
				QuoteItem item = vo.getQuoteItem();
				item.setStatus(StatusEnum .BIT.toString());
				item.setLastBmtUpdateTime(new Date());
				item.setLastUpdatedBmt(employeeId);
				item.setLastUpdatedBy(employeeId);
				item.setLastUpdatedName(employeeName);
				item.setAction(ActionEnum.MYQUOTE_RECALL_QUOTE.name()); // add audit action 
				QuoteItem newItem = quoteSB.updateQuoteItem(vo.getQuoteItem());
				
				vo.setQuoteItem(newItem);
			}
			
			FacesContext.getCurrentInstance().addMessage("growl",new FacesMessage(FacesMessage.SEVERITY_INFO,ResourceMB.getText("wq.message.recordQuoteSucc")+".", ""));
		}
		LOG.info("End call recallQuote");
	}
	
	
	

	public String copyQuote() {

		clearErrorRow();

		// Changes for pagination done by NTI
		Set<QuoteItemVo> cacheItems = getCacheSelectionItems();
		selectedQuoteItemVos =  new ArrayList<>();
		selectedQuoteItemVos.addAll(cacheItems);
				
		if (selectedQuoteItemVos.size() == 0) {
			FacesContext.getCurrentInstance().addMessage(
					"growl",
					new FacesMessage(FacesMessage.SEVERITY_WARN,
							ResourceMB.getText("wq.message.selRecord") +"!", ""));
			return null;
		}

		boolean valid = true, isDLink = false;
		for (QuoteItemVo vo : selectedQuoteItemVos) {
			QuoteItem item = vo.getQuoteItem();
			if (item.getStage() == null
					|| !item.getStage().equals(
							QuoteSBConstant.QUOTE_STAGE_FINISH)) {
				valid = false;
			}
			else if (vo.getQuoteItem().getQuote().isdLinkFlag() && vo.getQuoteItem().getRfqCurr().name()!=vo.getQuoteItem().getFinalCurr().name())
				isDLink=true;
		}

		if (valid == false) {
			FacesContext.getCurrentInstance().addMessage(
					"growl",
					new FacesMessage(FacesMessage.SEVERITY_WARN,
							ResourceMB.getText("wq.message.onlyFinishQuote")+".", ""));
			return null;
		}
		else if (isDLink == true) {
			String errMessage = ResourceMB.getText("wq.message.notAllowChildDLink_CopyQuote");
			FacesContext.getCurrentInstance().addMessage(
					"growl",
					new FacesMessage(FacesMessage.SEVERITY_WARN, errMessage,
							""));
			return null;
		}
		
		//DP Quote is not allowed to copy add by Lenon.Yang(043044) 2016-04-25
		for (QuoteItemVo vo : selectedQuoteItemVos) {
			QuoteItem item = vo.getQuoteItem();
			if (QuoteSourceEnum.DP.toString().equals(item.getQuote().getQuoteSource())) {
				valid = false;
				break;
			}
		}

		if (!valid) {
			FacesContext.getCurrentInstance().addMessage(
					"growl",new FacesMessage(FacesMessage.SEVERITY_WARN,ResourceMB.getText("wq.message.dpQuoteNotAllowedToCopy")+".", ""));
			return null;
		}

		doKeepFirstRow();

		copyQuoteMB.checkCopyQuoteItem(selectedQuoteItemVos);
		copyQuoteMB.setFinished(false);
		return "CopyQuote.xhtml?faces-redirect=true";
	}

	public String copyHeader() {

		// Changes for pagination done by NTI
		Set<QuoteItemVo> cacheItems = getCacheSelectionItems();
		selectedQuoteItemVos =  new ArrayList<>();
		selectedQuoteItemVos.addAll(cacheItems);
		
		if (selectedQuoteItemVos.size() != 1) {
			FacesContext.getCurrentInstance().addMessage(
					"growl",
					new FacesMessage(FacesMessage.SEVERITY_WARN,
							ResourceMB.getText("wq.message.selRecord") +"!", ""));
			return null;
		}

		copyHeaderQuoteId = selectedQuoteItemVos.get(0).getQuoteItem().getQuote().getId();
		return "RFQSubmissionLayout.jsf?faces-redirect=true&action="
				+ QuoteConstant.ACTION_COPY_QUOTE_HEADER + "&quoteId="
				+ selectedQuoteItemVos.get(0).getQuoteItem().getQuote().getId();

	}

	public String continueSubmission() {

		// Changes for pagination done by NTI
		Set<QuoteItemVo> cacheDatas = getCacheSelectionItems();
		selectedQuoteItemVos =  new ArrayList<>();
		selectedQuoteItemVos.addAll(cacheDatas);
		
		if (selectedQuoteItemVos.size() == 0) {
			FacesContext.getCurrentInstance().addMessage(
					"growl",
					new FacesMessage(FacesMessage.SEVERITY_WARN,
							ResourceMB.getText("wq.message.noRecordSelected")+" !", ""));
			return null;
		}

		long id = selectedQuoteItemVos.get(0).getQuoteItem().getQuote().getId();
		for (QuoteItemVo vo : selectedQuoteItemVos) {
			if (vo.getQuoteItem().getQuote().getId() != id) {
				FacesContext
						.getCurrentInstance()
						.addMessage(
								"growl",
								new FacesMessage(
										FacesMessage.SEVERITY_WARN,
										ResourceMB.getText("wq.message.rfqsub")+".",
										""));
				return null;
			} else {
				id = vo.getQuoteItem().getQuote().getId();
			}
		}

		QuoteItem item = selectedQuoteItemVos.get(0).getQuoteItem();

		if (item.getStage() != null
				&& !item.getStage().equals(QuoteSBConstant.QUOTE_STAGE_DRAFT)) {
			FacesContext.getCurrentInstance().addMessage(
					"growl",
					new FacesMessage(FacesMessage.SEVERITY_WARN,
							ResourceMB.getText("wq.message.onlyDraftRFQContinuedToSubmit")+"!", ""));
			return null;
		}
		LOG.info("the continue submission quote id: "+selectedQuoteItemVos.get(0).getQuoteItem().getQuote().getId());
		return "RFQSubmissionLayout.xhtml?faces-redirect=true&action="
				+ QuoteConstant.ACTION_CONTINUE_RFQ + "&quoteId="
				+ selectedQuoteItemVos.get(0).getQuoteItem().getQuote().getId();
	}

	public String resubmitInvalidQuote() {

		// Changes for pagination done by NTI
		Set<QuoteItemVo> cacheDatas = getCacheSelectionItems();
		selectedQuoteItemVos =  new ArrayList<>();
		selectedQuoteItemVos.addAll(cacheDatas);
		
		if (selectedQuoteItemVos.size() != 1) {
			FacesContext.getCurrentInstance().addMessage(
					"growl",
					new FacesMessage(FacesMessage.SEVERITY_WARN,
							ResourceMB.getText("wq.message.selRecord") +"!", ""));
			return null;
		}

		QuoteItem item = selectedQuoteItemVos.get(0).getQuoteItem();

		boolean isDLink = false;
		if (item.getStage() != null
				&& !item.getStage().equals(QuoteSBConstant.QUOTE_STAGE_INVALID)) {
			FacesContext
					.getCurrentInstance()
					.addMessage(
							"growl",
							new FacesMessage(
									FacesMessage.SEVERITY_WARN,
									ResourceMB.getText("wq.message.onlyInvalidRFQContinuedToSubmit")+"!",
									""));
			return null;
		}
		else if (item.getQuote().isdLinkFlag() && item.getRfqCurr().name()!=item.getFinalCurr().name()){
			isDLink=true;
		}

		if (isDLink == true) {
			String errMessage = ResourceMB.getText("wq.message.notAllowChildDLink_InvalidQuote");
			FacesContext.getCurrentInstance().addMessage(
					"growl",
					new FacesMessage(FacesMessage.SEVERITY_WARN, errMessage,
							""));
			return null;
		}
		
		return "RFQSubmissionLayout.xhtml?faces-redirect=true&action="
				+ QuoteConstant.ACTION_RESUBMIT_INVALID_RFQ + "&quoteId="
				+ selectedQuoteItemVos.get(0).getQuoteItem().getQuote().getId()
				+ "&quoteItemId="
				+ selectedQuoteItemVos.get(0).getQuoteItem().getId();
	}

	public String refreshQuote() {

		clearErrorRow();
		
		// Changes for pagination done by NTI
		Set<QuoteItemVo> cacheDatas = getCacheSelectionItems();
		selectedQuoteItemVos =  new ArrayList<>();
		selectedQuoteItemVos.addAll(cacheDatas);
		
		boolean valid = true, isDLink = false;
		if (selectedQuoteItemVos.size() == 0) {
			FacesContext.getCurrentInstance().addMessage(
					"growl",
					new FacesMessage(FacesMessage.SEVERITY_WARN,
							ResourceMB.getText("wq.message.noRecordSelected")+" !", ""));
			return null;
		}

		for (QuoteItemVo vo : selectedQuoteItemVos) {
			if (vo.getQuoteItem().getStage() == null
					|| !vo.getQuoteItem().getStage()
							.equals(QuoteSBConstant.QUOTE_STAGE_FINISH)) {
				vo.setErrRow(true);
				valid = false;
				break;
			}
			else if (vo.getQuoteItem().getQuote().isdLinkFlag() && vo.getQuoteItem().getRfqCurr().name()!=vo.getQuoteItem().getFinalCurr().name())
				isDLink=true;
		}

		if (valid == false) {
			FacesContext.getCurrentInstance().addMessage(
					"growl",
					new FacesMessage(FacesMessage.SEVERITY_WARN,
							ResourceMB.getText("wq.message.onlyFinishQuoteCanBeRefreshed")+".", ""));
			return null;
		}
		else if (isDLink == true) {
			String errMessage = ResourceMB.getText("wq.message.notAllowChildDLink_RefreshQuote");
			FacesContext.getCurrentInstance().addMessage(
					"growl",
					new FacesMessage(FacesMessage.SEVERITY_WARN, errMessage,
							""));
			return null;
		}
		
		// DP Quote is not allowed to refresh add by Lenon.Yang(043044) 2016-04-25
		for (QuoteItemVo vo : selectedQuoteItemVos) {
			QuoteItem item = vo.getQuoteItem();
			if (QuoteSourceEnum.DP.toString().equals(
					item.getQuote().getQuoteSource())) {
				valid = false;
				break;
			}
		}

		if (!valid) {
			FacesContext.getCurrentInstance().addMessage(
					"growl",
					new FacesMessage(FacesMessage.SEVERITY_WARN,ResourceMB.getText("wq.message.dpQuoteNotAllowedToRefresh")+".", ""));
			return null;
		}
		
		// check manufacturer_bizunit_mapping add by Lenon.Yang(043044) 2016-07-04
		for (QuoteItemVo vo : selectedQuoteItemVos) {
			QuoteItem item = vo.getQuoteItem();
			BizUnit bizUnit = null;
			String mfr = "";
			if(item != null && item.getQuote() != null ) {
				bizUnit = item.getQuote().getBizUnit();
			}
			if(item != null && item.getQuotedMfr() != null) {
				mfr = item.getQuotedMfr().getName();
			}
			ManufacturerMapping mfrMapping = manufacturerSB.findManufacturerMappingByBizUnit(bizUnit, mfr);
			if(mfrMapping == null) {
    			valid = false;
				break;
    		}
		}

		if (!valid) {
			FacesContext.getCurrentInstance().addMessage(
					"growl",
					new FacesMessage(FacesMessage.SEVERITY_WARN,ResourceMB.getText("wq.message.mrfQuoteDoesNotExistInBussinessUnit")+".", ""));
			return null;
		}
		
		

		doKeepFirstRow();

		Collections.sort(selectedQuoteItemVos ,new Comparator<QuoteItemVo>() {
			public int compare(QuoteItemVo o1, QuoteItemVo o2){
			      return o1.getSeq() - o2.getSeq();
			   }
		});
		
		refreshQuoteMB.checkRrefreshQuoteItem(selectedQuoteItemVos);

		return "RefreshQuote.xhtml?faces-redirect=true";
	}

	public String markAsUserd() {

		clearErrorRow();
		
		// Changes for pagination done by NTI
		Set<QuoteItemVo> cacheDatas = getCacheSelectionItems();
		selectedQuoteItemVos =  new ArrayList<>();
		selectedQuoteItemVos.addAll(cacheDatas);
		
		for(QuoteItemVo vo:selectedQuoteItemVos){
			
			/**1.	Add error message if select non-FINISH items to Mark As Used, and don��t allow proceeding.*/
			if(!QuoteSBConstant.QUOTE_STAGE_FINISH.equalsIgnoreCase(vo.getQuoteItem().getStage())){
				FacesContext.getCurrentInstance().addMessage(
						"growl",
						new FacesMessage(FacesMessage.SEVERITY_WARN,
								ResourceMB.getText("wq.message.onlyFinishQuoteCanBeMarkedAsUsed")+".", ""));
				return null;
			}
		}

		if (selectedQuoteItemVos.size() == 0) {
			FacesContext.getCurrentInstance().addMessage(
					"growl",
					new FacesMessage(FacesMessage.SEVERITY_WARN,
							ResourceMB.getText("wq.message.selRecord") +"!", ""));
			return null;
		}

		doKeepFirstRow();
		
		Collections.sort(selectedQuoteItemVos ,new Comparator<QuoteItemVo>() {
			public int compare(QuoteItemVo o1, QuoteItemVo o2){
			      return o1.getSeq() - o2.getSeq();
			   }
		});

		copyQuoteMB.checkCopyQuoteItem(selectedQuoteItemVos);
		copyQuoteMB.setFinished(false);
		return "MarkAsUserd.xhtml?faces-redirect=true";
	}

	public String buqUpload() {
		return "BalUnconsumedQtyUpload.xhtml?faces-redirect=true";
	}

	public String buqdowmLoad() {

		clearErrorRow();

		// Changes for pagination done by NTI
		Set<QuoteItemVo> cacheDatas = getCacheSelectionItems();
		selectedQuoteItemVos =  new ArrayList<>();
		selectedQuoteItemVos.addAll(cacheDatas);
		
		if (selectedQuoteItemVos.size() == 0) {
			FacesContext.getCurrentInstance().addMessage(
					"growl",
					new FacesMessage(FacesMessage.SEVERITY_WARN,
							ResourceMB.getText("wq.message.selRecord") +"!", ""));
			return null;
		}

		doKeepFirstRow();

		copyQuoteMB.checkCopyQuoteItem(selectedQuoteItemVos);
		copyQuoteMB.setFinished(false);
		return "MarkAsUserd.xhtml?faces-redirect=true";
	}

	private String convertErrorRows(List<Integer> items) {
		Collections.sort(items);

		StringBuffer sb = new StringBuffer("");
		int i = 1;
		for (Integer item : items) {
			sb.append("[");
			sb.append(item);
			sb.append("]");
			if (i != items.size()) {
				sb.append(",");
			}
			i++;
		}

		return sb.toString();
	}

	public String reviseFinalQuotedPrice() {

		clearErrorRow();

		List<Integer> errorRows = new ArrayList<Integer>();

		// Changes for pagination done by NTI
		Set<QuoteItemVo> cacheDatas = getCacheSelectionItems();
		selectedQuoteItemVos =  new ArrayList<>();
		selectedQuoteItemVos.addAll(cacheDatas);
		
		if (selectedQuoteItemVos.size() == 0) {
			FacesContext.getCurrentInstance().addMessage(
					"growl",
					new FacesMessage(FacesMessage.SEVERITY_WARN,
							ResourceMB.getText("wq.message.noRecordSelected")+" !", ""));
			return null;
		}

		boolean valid = true, isDLink = false;
		for (QuoteItemVo vo : selectedQuoteItemVos) {
			if (vo.getQuoteItem().getStage() == null
					|| !vo.getQuoteItem().getStage()
							.equals(QuoteSBConstant.QUOTE_STAGE_FINISH)) {
				valid = false;
				vo.setErrRow(true);
				errorRows.add(vo.getSeq());
			}
			else if (vo.getQuoteItem().getQuote().isdLinkFlag() && vo.getQuoteItem().getRfqCurr().name()!=vo.getQuoteItem().getFinalCurr().name())
				isDLink=true;
		}

		if (valid == false) {
			String errMessage = ResourceMB.getText("wq.message.onlyFinishQuoteCanBeRevised")+ ".<br/>"
					+ convertErrorRows(errorRows);
			FacesContext.getCurrentInstance().addMessage(
					"growl",
					new FacesMessage(FacesMessage.SEVERITY_WARN, errMessage,
							""));
			return null;
		}
		else if (isDLink == true) {
			String errMessage = ResourceMB.getText("wq.message.notAllowChildDLink_FinalQuote");
			FacesContext.getCurrentInstance().addMessage(
					"growl",
					new FacesMessage(FacesMessage.SEVERITY_WARN, errMessage,
							""));
			return null;
		}

		doKeepFirstRow();
		Collections.sort(selectedQuoteItemVos ,new Comparator<QuoteItemVo>() {
			public int compare(QuoteItemVo o1, QuoteItemVo o2){
			      return o1.getSeq() - o2.getSeq();
			   }
		});
		reviseFinalQuotedPriceMB.checkCopyQuoteItem(selectedQuoteItemVos);
		reviseFinalQuotedPriceMB.setFinished(false);
		return "ReviseFinalQuotedPrice.xhtml?faces-redirect=true";

	}

	public void search() {
		long start = System.currentTimeMillis();
		//LOG.finer(criteria.toString());
		criteria.setNoNeedAttachement(false);
		criteria.setAction(QuoteSBConstant.MYQUOTE_SEARCH_ACTION_PAGE);

		if (criteria.getsQuotedPartNumber() != null
				&& criteria.getsQuotedPartNumber().length() > 0
				&& criteria.getsQuotedPartNumber().length() < 3) {
			FacesMessage msg = new FacesMessage(
					FacesMessage.SEVERITY_WARN,
					ResourceMB.getText("wq.message.minLenError"),
					"");
			FacesContext.getCurrentInstance().addMessage("growl", msg);
			return;
		}

		if (criteria.getsCustomerName() != null
				&& criteria.getsCustomerName().length() > 0
				&& criteria.getsCustomerName().length() < 3) {
			FacesMessage msg = new FacesMessage(
					FacesMessage.SEVERITY_WARN,
					ResourceMB.getText("wq.message.cutmrLenError"),
					"");
			FacesContext.getCurrentInstance().addMessage("growl", msg);
			return;
		}
		
		// Changes for pagination done by NTI
		
/*		quoteItemVos = myQuoteSearchSB.search(criteria,resourceMB.getResourceLocale());

		int count = quoteItemVos.size();

		if (count > 500) {

			recordsExceedMaxAllowed = true;

			for (int i = quoteItemVos.size() - 1; i >= 500; i--) {
				quoteItemVos.remove(i);
			}

		} else {
			recordsExceedMaxAllowed = false;
		}
*/
		filteredQuoteItemVos = null;
		selectedQuoteItemVos = null;
		quoteItemVos.clearCacheSelectionItems();

		final DataTable d = (DataTable) FacesContext.getCurrentInstance()
				.getViewRoot().findComponent(":form:datatable_myquotelist");
		if (d != null) {
			int first = 0;
			d.setFirst(first);
		}

		long end = System.currentTimeMillis();
		//LOG.info("size: " + quoteItemVos.size() + ", takes11 " + (end - start)+ " ms");
	}

	public void csSearch() {

		String errMsg = checkDate();
		if (errMsg != null) {
			FacesContext.getCurrentInstance().addMessage("growl",
					new FacesMessage(FacesMessage.SEVERITY_WARN, errMsg, ""));
			return;
		}

		// asean -- find all active sales( not including sale mgr and director)
		// in this region
		// other region, find distinct sales in sale_cs_mapping ( subordinates,
		// region)
		criteria.setupUIInCriteria();

		User user = UserInfo.getUser();

		List<User> sales = userSB.findAllSalesForCs(user);

		criteria.setRestrictedSales(sales);

		setupLastUpdatedBy();

		setupQuoteExpiryDateInCriteria();

		setupDataAccessInCriteria();

		setupBizUnit();

		List<String> attachmentTypes = new ArrayList<String>();
		attachmentTypes.add(QuoteSBConstant.ATTACHMENT_TYPE_QUOTE);
		attachmentTypes.add(QuoteSBConstant.ATTACHMENT_TYPE_QUOTE_ITEM);
		attachmentTypes.add(QuoteSBConstant.ATTACHMENT_TYPE_REFRESH);
		criteria.setAttachmentType(attachmentTypes);

		search();

		quoteItemVos.startCsSearch();
	
		try {
			lastSearchMethod = this.getClass().getDeclaredMethod("csSearch",null);
		} catch ( Exception e) {
			LOG.log(Level.SEVERE,"Error occured when get search Method with reflection in MyQuoteSearchMB for Avnet Quote No. "+criteria.getsQuoteNumber()+""
					+ ", Form no. "+criteria.getsFormNumber()+", MFR: "+criteria.getsMfr()+", Customer Name: "+criteria.getsCustomerName()+""
							+ ", Sold To Code: "+criteria.getsSoldToCode()+", Employee Name: "+criteria.getsSalesName()+", Reason to failed : "+MessageFormatorUtil.getParameterizedStringFromException(e),e);
		}
		
	}

	public void salesSearch(boolean... myFormSearchMBInvoke) {
		
		String errMsg = checkDate();
		if (errMsg != null) {
			FacesContext.getCurrentInstance().addMessage("growl",new FacesMessage(FacesMessage.SEVERITY_WARN, errMsg, ""));
			return;
		}

		criteria.setupUIInCriteria();
		if(myFormSearchMBInvoke==null||myFormSearchMBInvoke.length<1 ||!myFormSearchMBInvoke[0]) {
			criteria.setQuoteId(null);
		}
		/*if(!formSearchInvoke) {
			criteria.setQuoteId(null);
		}*/
		showFormForSales = false;

		User user = UserInfo.getUser();

		List<User> sales = new ArrayList<User>();

		List<User> subordinates = userSB.findAllSubordinates(user, 10);

		sales.add(user);
		sales.addAll(subordinates);

		criteria.setRestrictedSales(sales);

		setupQuoteExpiryDateInCriteria();

		setupDataAccessInCriteria();

		setupBizUnit();

		List<String> attachmentTypes = new ArrayList<String>();
		attachmentTypes.add(QuoteSBConstant.ATTACHMENT_TYPE_QUOTE);
		attachmentTypes.add(QuoteSBConstant.ATTACHMENT_TYPE_QUOTE_ITEM);
		attachmentTypes.add(QuoteSBConstant.ATTACHMENT_TYPE_REFRESH);
		criteria.setAttachmentType(attachmentTypes);

		search();

		quoteItemVos.startSalesSearch();
		try {
			lastSearchMethod = this.getClass().getDeclaredMethod("salesSearch",
					boolean[].class);
		} catch (Exception e) {
			LOG.log(Level.SEVERE,
					"Error occured when get search Method with reflection in MyQuoteSearchMB for form no. "+criteria.getsFormNumber()+", Reason to failed : "+MessageFormatorUtil.getParameterizedStringFromException(e),
					e);
		}

		
	}

	public void insideSalesSearch() {

		String errMsg = checkDate();
		if (errMsg != null) {
			FacesContext.getCurrentInstance().addMessage("growl",new FacesMessage(FacesMessage.SEVERITY_WARN, errMsg, ""));
			return;
		}

		criteria.setupUIInCriteria();

		showFormForSales = false;

		User user = UserInfo.getUser();

		List<User> sales = userSB.findAllSalesForCs(user);

		sales.add(user);

		criteria.setRestrictedSales(sales);

		setupQuoteExpiryDateInCriteria();

		setupDataAccessInCriteria();

		setupBizUnit();

		List<String> attachmentTypes = new ArrayList<String>();
		attachmentTypes.add(QuoteSBConstant.ATTACHMENT_TYPE_QUOTE);
		attachmentTypes.add(QuoteSBConstant.ATTACHMENT_TYPE_QUOTE_ITEM);
		attachmentTypes.add(QuoteSBConstant.ATTACHMENT_TYPE_REFRESH);
		criteria.setAttachmentType(attachmentTypes);

		search();
		//Fixed for defect#297 that salesman can check the avnet quote# on draft stage by Damonchen@20180424
		//quoteItemVos.startPagination();
		quoteItemVos.startSalesSearch();
		

		try {
			lastSearchMethod = this.getClass().getDeclaredMethod("insideSalesSearch", null);
		} catch (Exception e) {
			LOG.log(Level.SEVERE,"Error occured when get search Method with reflection in MyQuoteSearchMB for Avnet Quote No. "+criteria.getsQuoteNumber()+""
					+ ", Form no. "+criteria.getsFormNumber()+", MFR: "+criteria.getsMfr()+", Customer Name: "+criteria.getsCustomerName()+""
							+ ", Sold To Code: "+criteria.getsSoldToCode()+", Employee Name: "+criteria.getsSalesName()+", Reason to failed : "+MessageFormatorUtil.getParameterizedStringFromException(e),e);
		}



		// quoteItemVos =
		// myQuoteSearchSB.updateReferenceMarginForSalesAndCs(quoteItemVos);
	}

	public void qcPricingSearch() {

		String errMsg = checkDate();
		if (errMsg != null) {
			FacesContext.getCurrentInstance().addMessage("growl",new FacesMessage(FacesMessage.SEVERITY_WARN, errMsg, ""));
			return;
		}

		// set mfr list ( from [MfrGroupAssignment] table)
		// when qc == aemc, set mfr2 list (ProgramQCAssignment (empty table))
		// and
		// set qc role = yes
		criteria.setupUIInCriteria();

		setupDataAccessInCriteria();

		setupLastUpdatedBy();

		setupBizUnit();

		List<String> attachmentTypes = new ArrayList<String>();
		attachmentTypes.add(QuoteSBConstant.ATTACHMENT_TYPE_QUOTE);
		attachmentTypes.add(QuoteSBConstant.ATTACHMENT_TYPE_QUOTE_ITEM);
		attachmentTypes.add(QuoteSBConstant.ATTACHMENT_TYPE_REFRESH);
		attachmentTypes.add(QuoteSBConstant.ATTACHMENT_TYPE_INTERNAL_TRANSFER);
		criteria.setAttachmentType(attachmentTypes);

		search();
		quoteItemVos.startQcSearch();
		try {
			lastSearchMethod = this.getClass().getDeclaredMethod(
					"qcPricingSearch", null);
		} catch (Exception e) {
			LOG.log(Level.SEVERE,"Error occured when get search Method with reflection in MyQuoteSearchMB for Avnet Quote No. "+criteria.getsQuoteNumber()+""
					+ ", Form no. "+criteria.getsFormNumber()+", MFR: "+criteria.getsMfr()+", Customer Name: "+criteria.getsCustomerName()+""
							+ ", Sold To Code: "+criteria.getsSoldToCode()+", Employee Name: "+criteria.getsSalesName()+", Reason to failed : "+MessageFormatorUtil.getParameterizedStringFromException(e),e);
		}
	}

	public void qcOperationSearch() {

		String errMsg = checkDate();
		if (errMsg != null) {
			FacesContext.getCurrentInstance().addMessage("growl", new FacesMessage(FacesMessage.SEVERITY_WARN, errMsg, ""));
			return;
		}

		User user = UserInfo.getUser();

		List<User> users = userSB.findAllSubordinates(user, 10);

		List<User> css = new ArrayList<User>();

		for (User cs : users) {
			css.add(cs);
		}

		css.add(user);

		criteria.setupUIInCriteria();

		setupLastUpdatedBy();

		setupDataAccessInCriteria();

		setupBizUnit();

		List<String> attachmentTypes = new ArrayList<String>();
		attachmentTypes.add(QuoteSBConstant.ATTACHMENT_TYPE_QUOTE);
		attachmentTypes.add(QuoteSBConstant.ATTACHMENT_TYPE_QUOTE_ITEM);
		attachmentTypes.add(QuoteSBConstant.ATTACHMENT_TYPE_REFRESH);
		attachmentTypes.add(QuoteSBConstant.ATTACHMENT_TYPE_INTERNAL_TRANSFER);
		criteria.setAttachmentType(attachmentTypes);

		search();
		//changed to startQcSearch , by DamonChan@20171219
		quoteItemVos.startQcSearch();
		try {
			lastSearchMethod = this.getClass().getDeclaredMethod(
					"qcOperationSearch", null);
		} catch (Exception e) {
			LOG.log(Level.SEVERE,"Error occured when get search Method with reflection in MyQuoteSearchMB for Avnet Quote No. "+criteria.getsQuoteNumber()+""
					+ ", MFR: "+criteria.getsMfr()
							+ ", Sold To Code: "+criteria.getsSoldToCode()+", Employee Name: "+criteria.getsSalesName()+", Reason to failed : "+MessageFormatorUtil.getParameterizedStringFromException(e),e);
		}
	}

	public void mmSearch() {

		String errMsg = checkDate();
		if (errMsg != null) {
			FacesContext.getCurrentInstance().addMessage("growl",new FacesMessage(FacesMessage.SEVERITY_WARN, errMsg, ""));
			return;
		}
		criteria.setupUIInCriteria();

		setupDataAccessInCriteria();

		setupQuoteExpiryDateInCriteria();

		setupBizUnit();

		List<String> attachmentTypes = new ArrayList<String>();
		attachmentTypes.add(QuoteSBConstant.ATTACHMENT_TYPE_QUOTE);
		attachmentTypes.add(QuoteSBConstant.ATTACHMENT_TYPE_QUOTE_ITEM);
		attachmentTypes.add(QuoteSBConstant.ATTACHMENT_TYPE_REFRESH);
		attachmentTypes.add(QuoteSBConstant.ATTACHMENT_TYPE_INTERNAL_TRANSFER);
		criteria.setAttachmentType(attachmentTypes);

		search();
		quoteItemVos.startMmSearch();
		try {
			lastSearchMethod = this.getClass().getDeclaredMethod("mmSearch",
					null);
		} catch (Exception e) {
			LOG.log(Level.SEVERE,
					"Error occured when get search Method with reflection in MyQuoteSearchMB for Avnet no. "+criteria.getsQuoteNumber()
					+", Employee name: "+criteria.getSalesName()+", MFR: "+criteria.getsMfr()+", Reason for failure: "+MessageFormatorUtil.getParameterizedStringFromException(e),e);
		}
	}
	public void bmtSearchOffline() {

		LOG.info("call bmtSearchOffline");
		String errMsg = checkDate();
		if (errMsg != null) {
			FacesContext.getCurrentInstance().addMessage("growl",
					new FacesMessage(FacesMessage.SEVERITY_WARN, errMsg, ""));
			return;
		}

		// set mfr list ( from mfrpmassignment table)
		criteria.setupUIInCriteria();

		setupDataAccessInCriteria();

		setupQuoteExpiryDateInCriteria();

		setupBizUnit();
		setupBmtFlag();
		searchOffline(QuoteConstant.MYQUOTE_BMT);
	
	}
	public void bmtSearch() {

//		this.currentQuoteItemVo= new QuoteItemVo();
		String errMsg = checkDate();
		if (errMsg != null) {
			FacesContext.getCurrentInstance().addMessage("growl",new FacesMessage(FacesMessage.SEVERITY_WARN, errMsg, ""));
			return;
		}

		// set mfr list ( from mfrpmassignment table)
		criteria.setupUIInCriteria();

		setupDataAccessInCriteria();

		setupQuoteExpiryDateInCriteria();

		setupBizUnit();
		
		setupBmtFlag();

		List<String> attachmentTypes = new ArrayList<String>();
		attachmentTypes.add(QuoteSBConstant.ATTACHMENT_TYPE_QUOTE);
		attachmentTypes.add(QuoteSBConstant.ATTACHMENT_TYPE_QUOTE_ITEM);
		attachmentTypes.add(QuoteSBConstant.ATTACHMENT_TYPE_QC);
		attachmentTypes.add(QuoteSBConstant.ATTACHMENT_TYPE_INTERNAL_TRANSFER);
		attachmentTypes.add(QuoteSBConstant.ATTACHMENT_TYPE_BMT);
		criteria.setAttachmentType(attachmentTypes);

		search();

		
		quoteItemVos.startBmtSearch();
		
		try {
			lastSearchMethod = this.getClass().getDeclaredMethod("bmtSearch",null);
		} catch (Exception e) {
			LOG.log(Level.SEVERE,"Error occured when get search Method with reflection in MyQuoteSearchMB for Avnet Quote No. "+criteria.getsQuoteNumber()+""
					+ ", Form no. "+criteria.getsFormNumber()+", MFR: "+criteria.getsMfr()+", Customer Name: "+criteria.getsCustomerName()+""
							+ ", Employee Name: "+criteria.getsSalesName()+", Reason to failed : "+MessageFormatorUtil.getParameterizedStringFromException(e),e);
		}
	
	}
	
	/**
	 * this method is be used for set up bmt flag only for the bmt user
	 * when the user select all type, should extract all bmt flag except for the null 
	 * when the user did not select any bmt flag, then set up the bmt flag as 99(empty)
	 */
	private void setupBmtFlag() {
		List<String> selectFlag = criteria.getSelectBmtFlag();
		List<String> bmtFlag = new ArrayList<String>();
		if(null==selectFlag || 0==selectFlag.size()) {
			bmtFlag.add(BmtFlagEnum.EMPTY.code());
		}else {
			if(selectFlag.contains(defaultBmtFlag)) {
				List<BmtFlagEnum> flags = Arrays.asList(BmtFlagEnum.values());
				for(BmtFlagEnum flag:flags) {
					bmtFlag.add(flag.code());
				}
			}else {
				for(String flagDesc:selectFlag) {
					String code = QuoteUtil.getflagCodeByDesc(flagDesc);
					bmtFlag.add(code);
				}
			}
		}
		
		criteria.setBmtFlag(bmtFlag);
		
		
	}

	public void pmSearch() {

		String errMsg = checkDate();
		if (errMsg != null) {
			FacesContext.getCurrentInstance().addMessage("growl",new FacesMessage(FacesMessage.SEVERITY_WARN, errMsg, ""));
			return;
		}

		// set mfr list ( from mfrpmassignment table)
		criteria.setupUIInCriteria();

		setupDataAccessInCriteria();

		setupQuoteExpiryDateInCriteria();

		setupBizUnit();

		List<String> attachmentTypes = new ArrayList<String>();
		attachmentTypes.add(QuoteSBConstant.ATTACHMENT_TYPE_QUOTE);
		attachmentTypes.add(QuoteSBConstant.ATTACHMENT_TYPE_QUOTE_ITEM);
		attachmentTypes.add(QuoteSBConstant.ATTACHMENT_TYPE_REFRESH);
		attachmentTypes.add(QuoteSBConstant.ATTACHMENT_TYPE_INTERNAL_TRANSFER);
		criteria.setAttachmentType(attachmentTypes);

		search();

		
		quoteItemVos.startPmSearch();
		try {
			lastSearchMethod = this.getClass().getDeclaredMethod("pmSearch",
					null);
		} catch (Exception e) {
			LOG.log(Level.SEVERE,
					"Error occured when get search Method with reflection in MyQuoteSearchMB for Avnet no. "+criteria.getsQuoteNumber()
					+", Employee name: "+criteria.getSalesName()+", MFR: "+criteria.getsMfr()+", Reason for failure: "+MessageFormatorUtil.getParameterizedStringFromException(e),e);
		}
	}

	private void setupBizUnit() {
		User user = UserInfo.getUser();
		criteria.setBizUnits(user.getBizUnits());
	}

	public void setupDataAccessInCriteria() {
		User user = UserInfo.getUser();
		criteria.setDataAccesses(userSB.findAllDataAccesses(user));
	}

	private void setupLastUpdatedBy() {
		User user = UserInfo.getUser();

		List<User> subordinated = userSB.findAllSubordinates(user, 10);

		List<String> lastUpdatedBy = new ArrayList<String>();

		for (User cs : subordinated) {
			lastUpdatedBy.add(cs.getEmployeeId());
		}

		lastUpdatedBy.add(user.getEmployeeId());

		criteria.setLastUpdatedBy(lastUpdatedBy);
	}

	private void setupQuoteExpiryDateInCriteria() {

		// Some users can only search the records whose Quote Expiry Date is
		// within 2 years
		Calendar securityFrom = Calendar.getInstance();

		securityFrom.add(Calendar.YEAR, -2);

		securityFrom.set(Calendar.HOUR_OF_DAY, 0);
		securityFrom.set(Calendar.MINUTE, 0);
		securityFrom.set(Calendar.SECOND, 0);
		securityFrom.set(Calendar.MILLISECOND, 0);

		Calendar securityTo = new GregorianCalendar(9999, 11, 31);

		if (criteria.getQuoteExpiryDateFrom() == null) {
			criteria.setQuoteExpiryDateFrom(securityFrom.getTime());
		} else {
			if (criteria.getQuoteExpiryDateFrom()
					.before(securityFrom.getTime())) {
				criteria.setQuoteExpiryDateFrom(securityFrom.getTime());
			}
		}

		if (criteria.getQuoteExpiryDateTo() == null) {
			criteria.setQuoteExpiryDateTo(securityTo.getTime());
		} else {
			if (criteria.getQuoteExpiryDateTo().after(securityTo.getTime())) {
				criteria.setQuoteExpiryDateTo(securityTo.getTime());
			}
		}
	}

	private void clearErrorRow() {
		// Changes for pagination done by NTI
	
		for (QuoteItemVo vo : quoteItemVos.getCacheSelectedItems()) {
			vo.setErrRow(false);
		}
	}

	// auto complete method

	public List<String> autoCompleteForPartNumber(String key) {

		User user = UserInfo.getUser();

		List<Material> materials = materialSB
				.wFindMaterialByMfrPartNumberWithPage(key, user.getBizUnits(),
						0, 30);

		List<String> result = new ArrayList<String>();

		for (Material material : materials) {
			result.add(material.getFullMfrPartNumber());
		}

		Set<String> resultSet = new LinkedHashSet<String>(result);
		result.clear();
		result.addAll(resultSet);

		return result;

	}

	public List<String> autoCompleteForCustomerName(String key) {

		User user = UserInfo.getUser();

		List<Customer> customers = customerSB.wFindCustomerByNameWithPage(key,
				user.getBizUnits(), 0, 30);

		List<String> result = new ArrayList<String>();

		for (Customer customer : customers) {
			result.add(customer.getCustomerFullName());
		}

		return result;

	}

	public List<String> autoCompleteForEmployeeName(String key) {

		User user = UserInfo.getUser();
		List<BizUnit> bizUnits = user.getBizUnits();

		List<User> users = userSB.wFindUserByNameAndRoleWithPage(key, null,
				bizUnits, 0, 30);

		List<String> result = new ArrayList<String>();

		for (User user1 : users) {
			result.add(user1.getName());
		}

		return result;

	}

	public List<String> autoCompleteForEmployeeNameForCS(String key) {

		User user = UserInfo.getUser();

		List<User> sales = userSB.findAllSalesForCs(user);

		List<String> result = new ArrayList<String>();

		int i = 0;
		for (User user1 : sales) {
			if (user1.getName().toUpperCase().contains(key.toUpperCase())) {
				result.add(user1.getName());
				i++;
			}

			if (i == 30) {
				break;
			}
		}

		return result;

	}

	public List<String> autoCompleteForEmployeeNameForSales(String key) {

		User user = UserInfo.getUser();

		List<User> sales = new ArrayList<User>();

		List<User> subordinates = userSB.findAllSubordinates(user, 10);

		sales.add(user);
		sales.addAll(subordinates);

		List<String> result = new ArrayList<String>();

		int i = 0;
		for (User user1 : sales) {
			if (user1.getName().toUpperCase().contains(key.toUpperCase())) {
				result.add(user1.getName());
				i++;
			}

			if (i == 30) {
				break;
			}

		}
		return result;
	}

	public List<String> autoCompleteForManufacturer(String key) {

		User user = UserInfo.getUser();

		List<Manufacturer> mfrs = manufacturerSB.mFindManufacturerByName(key,
				user.getBizUnits());

		List<String> result = new ArrayList<String>();

		int i = 1;
		for (Manufacturer mfr : mfrs) {
			result.add(mfr.getName());
			if (i == 30) {
				break;
			}
			i++;
		}

		return result;

	}

	public List<String> autoCompleteForTeam(String key) {

		User user = UserInfo.getUser();

		List<Team> teams = teamSB.findByName(key, user.getBizUnits());

		List<String> result = new ArrayList<String>();

		int i = 1;
		for (Team team : teams) {
			result.add(team.getName());
			if (i == 30) {
				break;
			}
			i++;
		}

		return result;
	}

	public void clearFields() {
		criteria.clear();
	}

	private String checkDate() {
		String errMsg = ResourceMB.getText("wq.message.date")+".";
		if (criteria.getPageUploadDateFrom() != null
				&& criteria.getPageUploadDateTo() != null) {
			if (criteria.getPageUploadDateFrom().after(
					criteria.getPageUploadDateTo())) {
				return errMsg;
			}
		}

		if (criteria.getPageSentOutDateFrom() != null
				&& criteria.getPageSentOutDateTo() != null) {
			if (criteria.getPageSentOutDateFrom().after(
					criteria.getPageSentOutDateTo())) {
				return errMsg;
			}
		}

		if (criteria.getPagePoExpiryDateFrom() != null
				&& criteria.getPagePoExpiryDateTo() != null) {
			if (criteria.getPagePoExpiryDateFrom().after(
					criteria.getPagePoExpiryDateTo())) {
				return errMsg;
			}
		}

		if (criteria.getPageQuoteExpiryDateFrom() != null
				&& criteria.getPageQuoteExpiryDateTo() != null) {
			if (criteria.getPageQuoteExpiryDateFrom().after(
					criteria.getPageQuoteExpiryDateTo())) {
				return errMsg;
			}
		}

		return null;
	}
	
	public void exportSelected(String reportName) {
		try {
			Set<QuoteItemVo> cacheDatas = getCacheSelectionItems();
			selectedQuoteItemVos =  new ArrayList<>();
			selectedQuoteItemVos.addAll(cacheDatas);
			if (selectedQuoteItemVos.size() == 0) {
				FacesContext.getCurrentInstance().addMessage(
						"growl",
						new FacesMessage(FacesMessage.SEVERITY_WARN,
								ResourceMB.getText("wq.message.noRecordSelected")+" !", ""));
				return;
			}
			ExcelReport excelReport = excelReportSB.getExcelReportByReportName(reportName);
			List resultList = ReportConvertor.convert2ResultBean(selectedQuoteItemVos);
			DownloadUtil.outputExcelFile(excelReport, "", resultList, FacesContext.getCurrentInstance());
		} catch (WebQuoteException e) {
			
			LOG.log(Level.SEVERE, "Download Selected Quote Error,Report Name: "+reportName+", "
					+ "Reason for failure: "+MessageFormatorUtil.getParameterizedStringFromException(e),e);
			Log.info("Download Selected Quote Error,Report Name:" + reportName);
		}
		
	}

	/*public void postProcessXLSForCS(Object document) {
		String[] columns = { "Used","Avnet Quote#", "Form No", "Valid For Ordering",
				"Invalid For Ordering (Reason)", "MFR", "Avnet Quoted P/N",
				"Requested P/N", "SAP Material Number",
				"Customer Name(Sold To Party)",
				"Chinese Customer Name(Sold To Party)", "Sold To Code",
				"SAP Sold To Type", "End Customer", "End Customer Code",
				"Avnet Quoted Price", "Final Quotation Price",
				"Quote Reference Margin", "PMOQ", "Cost Indicator",
				"Quotation Effective Date", "Quote Expiry Date",
				"Shipment Validity", "MPQ", "MOQ", "Lead Time (wks)",
				"Multi-Usage", "MFR Debit #", "MFR Quote #", "MFR Quote Qty",
				"Resale Indicator", "Cancellation Window",
				"Rescheduling Window", "Quotation T&C", "Allocation Part",
				"Avnet Quote Centre Comment",
				"QC Comment (Avnet Internal Only)", "Quote Stage",
				"RFQ Status","BMT Flag","Pending at", "Salesman Name", "Sales Employee Code", "Team",
				"Ship To Party", "Ship To Code", "Customer Type",
				"Material Type", "Business Program Type", "Segment",
				"Project Name", "Application", "Design Region","Design Location",
				"Design In Cat", "DRMS Project ID", "MP Schedule",
				"PP Schedule", "LOA", "Quote Type","Reference Exchange Rate","Reference Exchange Currency", "Remarks",
				"Requester Reference", "Copy to CS", "SPR", "Product Group 2",
				"Required Qty", "EAU", "Target Resale", "MFR DR#",
				"Competitor Information", "Item Remarks", "First RFQ Code",
				"Revert Version", "RFQ Submission Date", "Quote Release Date",
				"Quoted By", "RFQ Created By","DP Reference ID","DP Reference Line ID" };
		postProcessXLS(document, columns);
	}*/

	/*public void postProcessXLSForQC(Object document) {
		String[] columns = { "Used", "Bal. Unconsumed Qty", "Avnet Quote#",
				"Form No", "Quote Stage", "RFQ Status","BMT Flag","Pending at", "MFR",
				"Avnet Quoted P/N", "SAP Material Number",
				"Customer Name(Sold To Party)",
				"Chinese Customer Name(Sold To Party)", "Sold To Code",
				"SAP Sold To Type", "End Customer", "End Customer Code",
				"Avnet Quoted Price", "Final Quotation Price", "Cost",
				"Quote Margin", "Quote Reference Margin", "Cost Indicator",
				"Price Validity", "Quotation Effective Date",
				"Quote Expiry Date", "PO Expiry Date", "Shipment Validity",
				"PMOQ", "MPQ", "MOQ", "MOV", "Lead Time (wks)", "Multi-Usage",
				"Qty Indicator", "MFR Debit #", "MFR Quote #", "MFR Quote Qty",
				"Avnet Quoted Qty", "Resale Indicator", "Resale Max%",
				"Resale Min%", "Cancellation Window", "Rescheduling Window",
				"Quotation T&C", "Allocation Part",
				"Avnet Quote Centre Comment",
				"QC Comment (Avnet Internal Only)", "PM Comment",
				"Valid For Ordering", "Invalid For Ordering (Reason)",
				"Salesman Name", "Sales Employee Code", "Team",
				"Ship To Party", "Ship To Code", "Customer Type",
				"Material Type", "Business Program Type", "Segment",
				"Project Name", "Application", "Design Region","Design Location",
				"Design In Cat", "DRMS Project ID", "DRMS Authorized GP%",
				"Reasons for Authorized GP% Change", "Remarks of Reason",
				"DR Expiry Date", "MP Schedule", "PP Schedule", "LOA",
				"Quote Type","Reference Exchange Rate","Reference Exchange Currency", "Remarks", "Requester Reference", "Copy to CS",
				"Requested P/N", "SPR", "Product Group 2", "Required Qty",
				"EAU", "Target Resale", "Target Price Margin", "MFR DR#",
				"Competitor Information", "Item Remarks", "Reason For Refresh",
				"First RFQ Code", "Revert Version", "RFQ Submission Date",
				"Quote Release Date", "Quoted By", "RFQ Created By",
				"BMT MFR DR#","BMT Suggest Cost","BMT Suggest Resale","BMT Suggest Margin","BMT MFR Quote #",
				 "BMT SQ Effective Date","BMT SQ Expiry Date","BMT Comments","BMT Quote Qty","BMT Suggest Cost (Non-USD)",
				 "BMT Suggest Resale(Non-USD)","BMT Currency","Exchange rate(Non-USD)" ,"Last Update BMT", "Last Update By","DP Reference ID","DP Reference Line ID"};
		postProcessXLS(document, columns);
	}*/

	/*public void postProcessXLSForPM(Object document) {
		String[] columns = { "Avnet Quote#", "Form No", "Quote Stage",
				"RFQ Status","BMT Flag","Pending at", "MFR", "Requested P/N", "Avnet Quoted P/N",
				"SAP Material Number", "Customer Name(Sold To Party)",
				"Chinese Customer Name(Sold To Party)", "Sold To Code",
				"SAP Sold To Type", "Customer Type", "End Customer",
				"End Customer Code", "Avnet Quoted Price",
				"Final Quotation Price", "Cost", "Quote Margin",
				"Quote Reference Margin", "Cost Indicator", "Price Validity",
				"Quotation Effective Date", "Quote Expiry Date", "PO Expiry Date",
				"Shipment Validity", "DRMS Project ID", "MFR DR#",
				"DRMS Authorized GP%", "Reasons for Authorized GP% Change",
				"Remarks of Reason", "DR Expiry Date", "Segment",
				"Project Name", "Application","Design Region", "Design Location",
				"Design In Cat", "MFR Debit #", "MFR Quote #", "MFR Quote Qty",
				"Avnet Quoted Qty","Resale Indicator", "Avnet Quote Centre Comment",
				"QC Comment (Avnet Internal Only)", "PM Comment", "PMOQ",
				"MPQ", "MOQ", "MOV", "Lead Time (wks)", "Multi-Usage",
				"Qty Indicator", "Resale Max%", "Resale Min%",
				"Cancellation Window", "Rescheduling Window", "Quotation T&C",
				"Allocation Part", "Salesman Name", "Sales Employee Code",
				"Team", "Ship To Party", "Ship To Code", "Material Type",
				"Business Program Type", "PP Schedule", "MP Schedule", "LOA",
				"Quote Type","Reference Exchange Rate", "Reference Exchange Currency","Remarks", "Requester Reference", "Copy to CS",
				"SPR", "Product Group 2", "Required Qty", "EAU",
				"Target Resale", "Competitor Information", "Item Remarks",
				"Reason For Refresh", "First RFQ Code","Revert Version", "RFQ Submission Date",
				"Quote Release Date", "Quoted By", "RFQ Created By" ,"BMT MFR DR#","BMT Suggest Cost","BMT Suggest Resale","BMT Suggest Margin","BMT MFR Quote #",
				 "BMT SQ Effective Date","BMT SQ Expiry Date","BMT Comments","BMT Quote Qty","BMT Suggest Cost (Non-USD)",
				 "BMT Suggest Resale(Non-USD)","BMT Currency","Exchange rate(Non-USD)","DP Reference ID","DP Reference Line ID"};
		postProcessXLS(document, columns);
	}*/
	
	/*public void postProcessXLSForBMT(Object document) {
		String[] columns = { "Avnet Quote#", "Form No", "Quote Stage",
				"RFQ Status","BMT Flag","Pending at", "MFR", "Requested P/N", "Avnet Quoted P/N",
				"SAP Material Number", "Customer Name(Sold To Party)",
				"Chinese Customer Name(Sold To Party)", "Sold To Code",
				"SAP Sold To Type", "Customer Type", "End Customer",
				"End Customer Code", "Avnet Quoted Price",
				"Final Quotation Price", "Cost", "Quote Margin",
				"Quote Reference Margin", "Cost Indicator", "Price Validity",
				"Quotation Effective Date", "Quote Expiry Date", "PO Expiry Date",
				"Shipment Validity", "DRMS Project ID", "MFR DR#",
				"DRMS Authorized GP%", "Reasons for Authorized GP% Change",
				"Remarks of Reason", "DR Expiry Date", "Segment",
				"Project Name", "Application", "Design Region","Design Location",
				"Design In Cat", "MFR Debit #", "MFR Quote #", "MFR Quote Qty",
				"Avnet Quoted Qty", "Resale Indicator","Avnet Quote Centre Comment",
				"QC Comment (Avnet Internal Only)", "PM Comment", "PMOQ",
				"MPQ", "MOQ", "MOV", "Lead Time (wks)", "Multi-Usage",
				"Qty Indicator", "Resale Max%", "Resale Min%",
				"Cancellation Window", "Rescheduling Window", "Quotation T&C",
				"Allocation Part", "Salesman Name", "Sales Employee Code",
				"Team", "Ship To Party", "Ship To Code", "Material Type",
				"Business Program Type", "PP Schedule", "MP Schedule", "LOA",
				"Quote Type", "Reference Exchange Rate","Reference Exchange Currency","Remarks", "Requester Reference", "Copy to CS",
				"SPR", "Product Group 2", "Required Qty", "EAU",
				"Target Resale", "Competitor Information", "Item Remarks",
				"Reason For Refresh","First RFQ Code","Revert Version", "RFQ Submission Date",
				"Quote Release Date", "Quoted By", "RFQ Created By", 
				"Last Update BMT", "Last Update By",
				"BMT MFR DR#","BMT Suggest Cost","BMT Suggest Resale","BMT Suggest Margin","BMT MFR Quote #",
				 "BMT SQ Effective Date","BMT SQ Expiry Date","BMT Comments","BMT Quote Qty","BMT Suggest Cost (Non-USD)",
				 "BMT Suggest Resale(Non-USD)","BMT Currency","Exchange rate(Non-USD)" ,"DP Reference ID","DP Reference Line ID"};
		postProcessXLS(document, columns);
	}*/

	/*public void postProcessXLSForMM(Object document) {
		String[] columns = { "Used", "Bal. Unconsumed Qty", "Avnet Quote#",
				"Quote Stage", "RFQ Status","BMT Flag","Pending at", "MFR", "Avnet Quoted P/N",
				"Requested P/N", "SAP Material Number",
				"Customer Name(Sold To Party)",
				"Chinese Customer Name(Sold To Party)", "Sold To Code",
				"Avnet Quoted Price", "Final Quotation Price", "Cost",
				"Cost Indicator", "Quotation Effective Date","PO Expiry Date", "Shipment Validity",
				"PMOQ", "MPQ", "MOQ", "MOV", "Lead Time (wks)", "Multi-Usage",
				"MFR Debit #", "MFR Quote #", "MFR Quote Qty",
				"Avnet Quoted Qty","Resale Indicator", "Cancellation Window",
				"Rescheduling Window", "Quotation T&C", "Allocation Part",
				"Avnet Quote Centre Comment",
				"QC Comment (Avnet Internal Only)", "Quote Expiry Date",
				"PM Comment", "Form No", "Salesman Name",
				"Sales Employee Code", "Team", "Quoted By", "End Customer",
				"End Customer Code", "Ship To Party", "Ship To Code",
				"Material Type", "Business Program Type", "Design In Cat",
				"Quote Type","Reference Exchange Rate","Reference Exchange Currency",
				"Product Group 2", "Item Remarks", "RFQ Submission Date",
				"Quote Release Date", "RFQ Created By","DP Reference ID","DP Reference Line ID" };
		postProcessXLS(document, columns);
	}*/

	/*public void postProcessXLSForSales(Object document) {
		String[] columns = { "Used","Avnet Quote#", "Form No", "Valid For Ordering",
				"Invalid For Ordering (Reason)", "MFR", "Avnet Quoted P/N",
				"Requested P/N", "SAP Material Number",
				"Customer Name(Sold To Party)",
				"Chinese Customer Name(Sold To Party)", "Sold To Code",
				"SAP Sold To Type", "End Customer", "End Customer Code",
				"Avnet Quoted Price", "Final Quotation Price",
				"Quote Reference Margin", "PMOQ", "Cost Indicator",
				"Quotation Effective Date", "Quote Expiry Date",
				"Shipment Validity", "MPQ", "MOQ", "Lead Time (wks)",
				"Multi-Usage", "MFR Debit #", "MFR Quote #", "MFR Quote Qty",
				"Resale Indicator", "Cancellation Window",
				"Rescheduling Window", "Quotation T&C", "Allocation Part",
				"Avnet Quote Centre Comment",
				"QC Comment (Avnet Internal Only)", "Quote Stage",
				"RFQ Status","BMT Flag","Pending at", "Salesman Name", "Sales Employee Code", "Team",
				"Ship To Party", "Ship To Code", "Customer Type",
				"Material Type", "Business Program Type", "Segment",
				"Project Name", "Application", "Design Region","Design Location",
				"Design In Cat", "DRMS Project ID", "DR Expiry Date",
				"MP Schedule", "PP Schedule", "LOA", "Quote Type","Reference Exchange Rate","Reference Exchange Currency", "Remarks",
				"Requester Reference", "Copy to CS", "SPR", "Product Group 2",
				"Required Qty", "EAU", "Target Resale", "MFR DR#",
				"Competitor Information", "Item Remarks", "First RFQ Code",
				"Revert Version", "RFQ Submission Date", "Quote Release Date",
				"Quoted By", "RFQ Created By" ,"BMT MFR DR#","BMT Suggest Resale","BMT MFR Quote #",
				 "BMT SQ Effective Date","BMT SQ Expiry Date","BMT Comments","BMT Quote Qty",
				 "BMT Suggest Resale(Non-USD)","BMT Currency","Exchange rate(Non-USD)","DP Reference ID","DP Reference Line ID"};
		postProcessXLS(document, columns);
	}*/

	/***
	 * To set up criteria for showing DQ status records
	 * @param document
	 */
	public void preProcessXLSForQuote(Object document){
		criteria.setupUIInCriteria();
	}
	/**
	 * This method is used to sort quoteItem based on seqNo.
	 * @param document
	 */
	public void preProcessXLSQuoteSearch(Object document){
		List<QuoteItemVo>  sortedQuoteItem = quoteItemVos.getSelectionItems();
		Collections.sort(sortedQuoteItem ,new Comparator<QuoteItemVo>() {
			public int compare(QuoteItemVo o1, QuoteItemVo o2){
			      return o1.getSeq() - o2.getSeq();
			   }
		});
		quoteItemVos.setSelectionItems(sortedQuoteItem);
	}
	
	public void postProcessXLS(Object document, String[] columns) {
		HSSFWorkbook wb = (HSSFWorkbook) document;
		HSSFSheet sheet = wb.getSheetAt(0);
		HSSFRow header = sheet.getRow(0);
		int i = 0;
		for (String column : columns) {
			header.getCell(i++).setCellValue(column);
		}
	}

	private void doKeepFirstRow() {
		final DataTable d = (DataTable) FacesContext.getCurrentInstance()
				.getViewRoot().findComponent(":form:datatable_myquotelist");
		if (d != null) {
			firstRow = d.getFirst();
		} else {
			firstRow = 0;
		}
		keepFirstRow = true;
	}

	public void restoreFirstRow() {
		final DataTable d = (DataTable) FacesContext.getCurrentInstance()
				.getViewRoot().findComponent(":form:datatable_myquotelist");
		if (d != null) {
			d.setFirst(firstRow);
		}

		keepFirstRow = false;
	}

	public void setCopyQuoteMB(CopyQuoteMB copyQuoteMB) {
		this.copyQuoteMB = copyQuoteMB;
	}

	public void setBmtInfoMB(BmtInfoMB bmtInfoMB) {
		this.bmtInfoMB = bmtInfoMB;
	}

	public void setRefreshQuoteMB(RefreshQuoteMB refreshQuoteMB) {
		this.refreshQuoteMB = refreshQuoteMB;
	}

	public boolean isRecordsExceedMaxAllowed() {
		return recordsExceedMaxAllowed;
	}

	public void setRecordsExceedMaxAllowed(boolean recordsExceedMaxAllowed) {
		this.recordsExceedMaxAllowed = recordsExceedMaxAllowed;
	}

	/*public List<QuoteItemVo> getQuoteItemVos() {
		HttpServletRequest origRequest = (HttpServletRequest)FacesContext.getCurrentInstance().getExternalContext().getRequest();
		String url = origRequest.getRequestURL().toString();
		
		if(url.contains("MyQuoteListForSales")) {
			this.pageRoleName ="Sales";
		} else if( url.contains("MyQuoteListForCS")) {
			this.pageRoleName="CS";
		} else if( url.contains("MyQuoteListForBMT")) {
			this.pageRoleName="BMT";
		} else if( url.contains("MyQuoteListForQC")) {
			this.pageRoleName="QC";
		} else if( url.contains("MyQuoteListForPM")) {
			this.pageRoleName="PM";
		}else if( url.contains("MyQuoteListForMM")) {
			this.pageRoleName="MM";
		}
		return quoteItemVos.getCurrentPageDatas();
	}

	public void setQuoteItemVos(List<QuoteItemVo> quoteItemVos) {
		this.quoteItemVos = quoteItemVos;
	}
	*/

	public List<QuoteItemVo> getSelectedQuoteItemVos() {
		Set<QuoteItemVo> cacheItems = getCacheSelectionItems();
		selectedQuoteItemVos =  new ArrayList<>();
		selectedQuoteItemVos.addAll(cacheItems);
		Collections.sort(selectedQuoteItemVos ,new Comparator<QuoteItemVo>() {
			public int compare(QuoteItemVo o1, QuoteItemVo o2){
			      return o1.getSeq() - o2.getSeq();
			   }
		});
		return selectedQuoteItemVos;
	}

	public void setSelectedQuoteItemVos(List<QuoteItemVo> selectedQuoteItemVos) {
		this.selectedQuoteItemVos = selectedQuoteItemVos;
	}

	public MyQuoteSearchCriteria getCriteria() {
		return criteria;
	}

	public void setCriteria(MyQuoteSearchCriteria criteria) {
		this.criteria = criteria;
	}

	public boolean isShowFormForSales() {
		return showFormForSales;
	}

	public void setShowFormForSales(boolean showFormForSales) {
		this.showFormForSales = showFormForSales;
	}

	public List<QuoteItemVo> getFilteredQuoteItemVos() {
		return filteredQuoteItemVos;
	}

	public void setFilteredQuoteItemVos(List<QuoteItemVo> filteredQuoteItemVos) {
		this.filteredQuoteItemVos = filteredQuoteItemVos;
	}

	public void setReviseFinalQuotedPriceMB(
			ReviseFinalQuotedPriceMB reviseFinalQuotedPriceMB) {
		this.reviseFinalQuotedPriceMB = reviseFinalQuotedPriceMB;
	}

	public void setdLinkMB(DLinkMB dLinkMB) {
		this.dLinkMB = dLinkMB;
	}

	public boolean isQcOprSearchButton() {
		return qcOprSearchButton;
	}

	public void setQcOprSearchButton(boolean qcOprSearchButton) {
		this.qcOprSearchButton = qcOprSearchButton;
	}

	public boolean isQcPriSearchButton() {
		return qcPriSearchButton;
	}

	public void setQcPriSearchButton(boolean qcPriSearchButton) {
		this.qcPriSearchButton = qcPriSearchButton;
	}

	public long getCopyHeaderQuoteId() {
		return copyHeaderQuoteId;
	}

	public void setCopyHeaderQuoteId(long copyHeaderQuoteId) {
		this.copyHeaderQuoteId = copyHeaderQuoteId;
	}

	public String getMyQuotePage() {
		// this.selectedQuoteItemVos = null;
		this.filteredQuoteItemVos = null;
		//this.quoteItemVos = null;
		quoteItemVos.stopPagination();
		if (lastSearchMethod != null) {
			try {
				//fixed for David add a prameter to salesSearch method, by damonChenc
				if ("salesSearch".equalsIgnoreCase(lastSearchMethod.getName())) {
					lastSearchMethod.invoke(this, (Object) new boolean[] {});
				} else {
					lastSearchMethod.invoke(this, null);
				}
			} catch (Exception e) {
				LOG.log(Level.SEVERE,
						"Error occured when get search Method with reflection in MyQuoteSearchMB for Avnet no. "+criteria.getsQuoteNumber()
						+", Employee name: "+criteria.getSalesName()+", MFR: "+criteria.getsMfr()+", Reason for failure: "+e.getMessage(),e);
			}
		}

		return myQuotePage + "?faces-redirect=true";
	}

	public void setMyQuotePage(String myQuotePage) {
		this.myQuotePage = myQuotePage;
	}

	public void goBackToFirstRow() {
		final DataTable d = (DataTable) FacesContext.getCurrentInstance()
				.getViewRoot().findComponent(":form:datatable_myquotelist");
		if (d != null) {
			d.setFirst(firstRow);
		}
		keepFirstRow = false;
	}

	public String getQuoteId() {
		return quoteId;
	}

	public void setQuoteId(String quoteId) {
		this.quoteId = quoteId;

		List<Long> quoteIds = new ArrayList<Long>();
		quoteIds.add(Long.parseLong(quoteId));
		criteria.setQuoteId(quoteIds);

		showFormForSales = false;

		User user = UserInfo.getUser();

		List<Role> roles = user.getRoles();

		for (Role role : roles) {
			if (role.getName().contains("ROLE_SALES")
					|| role.getName().contains("ROLE_SALES_MANAGER")
					|| role.getName().contains("ROLE_SALES_GM")
					|| role.getName().contains("ROLE_SALES_DIRECTOR")) {
				List<User> sales = userSB.findAllSubordinates(user, 10);

				sales.add(user);

				criteria.setRestrictedSales(sales);

				setupQuoteExpiryDateInCriteria();

				setupDataAccessInCriteria();

				setupBizUnit();

				List<String> attachmentTypes = new ArrayList<String>();
				attachmentTypes.add(QuoteSBConstant.ATTACHMENT_TYPE_QUOTE);
				attachmentTypes.add(QuoteSBConstant.ATTACHMENT_TYPE_QUOTE_ITEM);
				attachmentTypes.add(QuoteSBConstant.ATTACHMENT_TYPE_REFRESH);
				criteria.setAttachmentType(attachmentTypes);

				search();

				quoteItemVos.startSalesSearch();
				
				

				break;

			} else if (role.getName().contains("ROLE_CS")
					|| role.getName().contains("ROLE_CS_MANAGER")
					|| role.getName().contains("ROLE_INSIDE_SALES")) {

				List<User> sales = userSB.findAllSalesForCs(user);

				criteria.setRestrictedSales(sales);

				setupQuoteExpiryDateInCriteria();

				setupDataAccessInCriteria();

				setupBizUnit();

				List<String> attachmentTypes = new ArrayList<String>();
				attachmentTypes.add(QuoteSBConstant.ATTACHMENT_TYPE_QUOTE);
				attachmentTypes.add(QuoteSBConstant.ATTACHMENT_TYPE_QUOTE_ITEM);
				attachmentTypes.add(QuoteSBConstant.ATTACHMENT_TYPE_REFRESH);
				criteria.setAttachmentType(attachmentTypes);

				search();

				quoteItemVos.startCsSearch();
				

				break;

			}
		}
		//criteria.setQuoteId(null);
	}

	// Search Offline
	public void csSearchOffline() {
		LOG.info("call csSearchOffline");
		String errMsg = checkDate();
		if (errMsg != null) {
			FacesContext.getCurrentInstance().addMessage("growl",new FacesMessage(FacesMessage.SEVERITY_WARN, errMsg, ""));
			return;
		}
		criteria.setupUIInCriteria();

		User user = UserInfo.getUser();

		List<User> sales = userSB.findAllSalesForCs(user);

		criteria.setRestrictedSales(sales);

		setupLastUpdatedBy();

		setupQuoteExpiryDateInCriteria();

		setupDataAccessInCriteria();

		setupBizUnit();
		searchOffline(QuoteConstant.MYQUOTE_CS);

	}

	public void salesSearchOffline() {
		LOG.info("call salesSearchOffline");
		String errMsg = checkDate();
		if (errMsg != null) {
			FacesContext.getCurrentInstance().addMessage("growl",new FacesMessage(FacesMessage.SEVERITY_WARN, errMsg, ""));
			return;
		}

		criteria.setupUIInCriteria();

		showFormForSales = false;

		User user = UserInfo.getUser();

		List<User> sales = new ArrayList<User>();

		List<User> subordinates = userSB.findAllSubordinates(user, 10);

		sales.add(user);
		sales.addAll(subordinates);

		criteria.setRestrictedSales(sales);

		setupQuoteExpiryDateInCriteria();

		setupDataAccessInCriteria();

		setupBizUnit();
		searchOffline(QuoteConstant.MYQUOTE_SALE);

	}

	public void insideSalesSearchOffline() {

		LOG.info("call insideSalesSearchOffline");

		String errMsg = checkDate();
		if (errMsg != null) {
			FacesContext.getCurrentInstance().addMessage("growl",new FacesMessage(FacesMessage.SEVERITY_WARN, errMsg, ""));
			return;
		}

		criteria.setupUIInCriteria();

		showFormForSales = false;

		User user = UserInfo.getUser();

		List<User> sales = userSB.findAllSalesForCs(user);

		sales.add(user);

		criteria.setRestrictedSales(sales);

		setupQuoteExpiryDateInCriteria();

		setupDataAccessInCriteria();

		setupBizUnit();
		searchOffline(QuoteConstant.MYQUOTE_SALE);

	}

	public void qcPricingSearchOffline() {
		LOG.info("call qcPricingSearchOffline");
		String errMsg = checkDate();
		if (errMsg != null) {
			FacesContext.getCurrentInstance().addMessage("growl",new FacesMessage(FacesMessage.SEVERITY_WARN, errMsg, ""));
			return;
		}
		criteria.setupUIInCriteria();

		setupDataAccessInCriteria();

		setupLastUpdatedBy();

		setupBizUnit();
		searchOffline(QuoteConstant.MYQUOTE_QC);
	}

	public void qcOperationSearchOffline() {
		LOG.info("call qcOperationSearchOffline");
		String errMsg = checkDate();
		if (errMsg != null) {//
			FacesContext.getCurrentInstance().addMessage("growl",new FacesMessage(FacesMessage.SEVERITY_WARN, errMsg, ""));
			return;
		}
		criteria.setupUIInCriteria();

		setupLastUpdatedBy();

		setupDataAccessInCriteria();

		setupBizUnit();
		searchOffline(QuoteConstant.MYQUOTE_QC);
	}

	public void mmSearchOffline() {
		LOG.info("call mmSearchOffline");
		String errMsg = checkDate();
		if (errMsg != null) {
			FacesContext.getCurrentInstance().addMessage("growl",new FacesMessage(FacesMessage.SEVERITY_WARN, errMsg, ""));
			return;
		}

		// set mfr list ( from mfrmmassignment table)

		criteria.setupUIInCriteria();

		setupDataAccessInCriteria();

		setupQuoteExpiryDateInCriteria();

		setupBizUnit();
		searchOffline(QuoteConstant.MYQUOTE_MM);
	}
	
	public void pmSearchOffline() {
		LOG.info("call pmSearchOffline");
		String errMsg = checkDate();
		if (errMsg != null) {
			FacesContext.getCurrentInstance().addMessage("growl",new FacesMessage(FacesMessage.SEVERITY_WARN, errMsg, ""));
			return;
		}

		// set mfr list ( from mfrpmassignment table)
		criteria.setupUIInCriteria();

		setupDataAccessInCriteria();

		setupQuoteExpiryDateInCriteria();

		setupBizUnit();
		searchOffline(QuoteConstant.MYQUOTE_PM);
	}

	public void searchOffline(String reportName) {
		LOG.info("call searchOffline");
		DataTable dataTable = (DataTable) FacesContext.getCurrentInstance().getViewRoot().findComponent(":form:datatable_myquotelist");
		Map<String,Object> filtersMap = dataTable.getFilters();
		LOG.info("call searchOffline-fied:"+dataTable.getSortField());
		LOG.info("call searchOffline-sorder:"+dataTable.getSortOrder());
		int i=quoteItemVos.findLazyDataCount(dataTable.getSortField(), dataTable.getSortOrder(), filtersMap);
		LOG.info("call searchOffline-iiiii:"+i);
		if (i > QuoteSBConstant.EXCEL_MAX_ROWS) {
			FacesContext.getCurrentInstance().addMessage("growl",
					new FacesMessage(FacesMessage.SEVERITY_WARN, "",  ResourceMB.getText("wq.error.excelmaxnumlimit")));
			return;
		}
		criteria.setFiltersMap(filtersMap);
		//int pageSize = 10000;
		User tempUser = UserInfo.getUser();
		criteria.setLimitResult(true);
		criteria.setNoNeedAttachement(true);
		criteria.setupUIInCriteria();
		criteria.setAction(QuoteSBConstant.MYQUOTE_SEARCH_ACTION_OFFLINE);
		OfflineReportParam param = new OfflineReportParam();
		param.setCriteriaBeanValue(criteria);
		param.setReportName(reportName);
		param.setEmployeeId(String.valueOf(tempUser.getEmployeeId()));
		param.setRemoteEjbClass(RemoteEjbClassEnum.MYQUOTE_REMOTE_EJB.classSimpleName());
		offlineReprtSB.sendOffLineReportRemote(param);
		 
		criteria.setFiltersMap(null);
		// LOG.info(criteria.toString());
		/*int totalRecords = myQuoteSearchSB.searchCount(criteria).intValue();
		LOG.info("search result count:" + totalRecords);

		List<OfflineReport> reportBeanList = new ArrayList<OfflineReport>();
		Date curDate = DateUtils.getCurrentAsiaDateObj();
		SimpleDateFormat dateTimeFormat = new SimpleDateFormat(
				"yyyy/MM/ddHH:mm:ss");
		String batchKey = dateTimeFormat.format(curDate);

		int count = totalRecords / pageSize;
		LOG.info("count:" + count);
		for (int i = 0; i <= count; i++) {
			criteria.setStart(i * pageSize);

			if (pageSize > (totalRecords - (i * pageSize))) {
				criteria.setEnd(totalRecords - (i * pageSize));
			} else {
				criteria.setEnd(pageSize);
			}

			OfflineReport reportBean = new OfflineReport();
			reportBean.setServiceBeanId(batchKey);
			reportBean
					.setServiceBeanClass("com.avnet.emasia.webquote.quote.job.MyQuoteExcelFileGenerator");
			reportBean.setServiceBeanMethod(reportName);
			reportBean
					.setSearchBeanClass(MyQuoteSearchCriteria.class.getName());
			reportBean.setSearchBeanValue(transfer(criteria));
			reportBean.setCreatedOn(curDate);
			reportBean.setLastUpdatedOn(curDate);
			reportBean.setEmployeeId(String.valueOf(tempUser.getEmployeeId()));
			reportBean.setEmployeeName(tempUser.getName());
			reportBean.setSendFlag(false);
			reportBean.setBizUnit(tempUser.getCurrenBizUnit().getName());
			reportBeanList.add(reportBean);
		}
		//offlineReprtSB.createOfflineReportRequest(reportBeanList);
		// LOG.info("searchOffline end" );
		long returnRequestId = offlineReprtSB.createOfflineReportRequest(reportBeanList);
		LOG.info("user is :"+tempUser.getEmployeeId()+" searchOffline returnRequestId:" + returnRequestId	);
		sendQueue(returnRequestId);*/
		
		FacesContext
				.getCurrentInstance()
				.addMessage(
						"growl",
						new FacesMessage(
								FacesMessage.SEVERITY_INFO,
								"",
								ResourceMB.getText("wq.message.requestSubmitted")+ ". <br />"+ResourceMB.getText("wq.message.reportSentYourEmail")+ ". <br />"+ResourceMB.getText("wq.message.withInOneH")+ "."));
		return;
	}
	
	/**
	 * this method are trige by click the confirm button in the dialog of exchange rate
	 * @author 916138 
	 * @param quoteItems
	 * @param currentUser
	 */
	public void confirmQuoteRate() {
		LOG.info("confirm quote Rate");
		LOG.info("proceedQuoteVos Exchange Rate===>>> "+ proceedQuoteItemVos.size());
		Date date= new Date();
		Calendar cal = Calendar.getInstance();
		cal.setTimeZone(TimeZone.getTimeZone("GMT+8"));
		cal.setTime(date);
		date = cal.getTime();	
		try {
			
		if (proceedQuoteItemVos.size() == 0) {
			FacesContext.getCurrentInstance().addMessage("growl",new FacesMessage(FacesMessage.SEVERITY_WARN,ResourceMB.getText("wq.message.noRecordSelected")+" !", ""));
			return;
		}
		
		boolean isValid = checkExchangeRate(proceedQuoteItemVos);
		if(!isValid) {
			FacesContext.getCurrentInstance().addMessage("growl",new FacesMessage(FacesMessage.SEVERITY_WARN,ResourceMB.getText("wq.message.someProceedQuoteNotHaveExchangeRate"),""));
			return;
		}
		
		List<QuoteItem> emailQuoteItems = quoteSB.quoteRateRequest(proceedQuoteItemVos,UserInfo.getUser());
			
		//And the quotation form is sent out to sales again. 
		emailQuotationToSales(emailQuoteItems,currentUser);
			
		reSearchByRole();
			
			
		} catch (Exception e)
		{
			String timeStamp = String.valueOf(date.getTime());
			LOG.log(Level.WARNING, timeStamp);
			LOG.log(Level.SEVERE,"Error occured in MyQuoteSearchMB for Avnet Quote No. "+criteria.getsQuoteNumber()+""
					+", Reason to failed : "+MessageFormatorUtil.getParameterizedStringFromException(e),e);
			String s = MessageFormatorUtil.getParameterizedStringFromException(e);
			FacesContext.getCurrentInstance().addMessage(null,new FacesMessage(FacesMessage.SEVERITY_WARN, s, ""));
			return;
		}

	}

	
	
	/**
	 * check the selected proceed quote item have exchange rate or not, if it is not 
	 * system will not proceed the confirmation
	 * @author 916138
	 * @param proceedQuoteItemVos2
	 * @return
	 */
	private boolean checkExchangeRate(List<QuoteItemVo> itemVos) {
		boolean isValid = true;
		for (QuoteItemVo vo : itemVos) {
			if (vo.getExChangeRate()==null || (vo.getExChangeRate().getExRateTo() == null|| vo.getExChangeRate().getExRateTo().equals(new BigDecimal(0)))) {
				vo.setErrRow(true);
				isValid = false;
			}
		}
		return isValid;
	}

	/**
	 * after change the exchange rate, call search method to refresh local object
	 */
	private void reSearchByRole() {
		//refresh the table 
		if(this.isQcOprSearchButton()) {
			qcOperationSearch();
		}else if(this.isQcPriSearchButton()) {
			qcPricingSearch();
		}else if(this.isInsideSalesSearchButton()) {
			insideSalesSearch();
		}else if(this.isCsSearchButton()) {
			csSearch();
		}else if(this.isSalesSearchButton()) {
			salesSearch();
		}
		
	}

	/**
	 * email the quotation form to sales when exchange rate was be update
	 * @author 916138 
	 * @param quoteItems
	 * @param currentUser
	 */
	private void emailQuotationToSales(List<QuoteItem> quoteItems,
			User currentUser) {
		List<String> quoteEmailedLst = new ArrayList<String>();
		boolean isEmailed = false; // if the quote has been emailed, in this cycle, then don't email again.
		for(QuoteItem qi:quoteItems) {
			String formNumber = qi.getQuote().getFormNumber();
			if(null!=quoteEmailedLst && quoteEmailedLst.size()>0) {
				isEmailed = quoteEmailedLst.contains(formNumber); // to determine if email this quotation form
			}
			if(!isEmailed) {
				quoteEmailedLst.add(formNumber);
				emailQuotationFormToSales(qi,currentUser);
			
			}
			
		}
		
	}
	
	
	private void emailQuotationFormToSales(QuoteItem qi,User currentUser) {

		Quote quote = qi.getQuote();
		QuotationEmailVO vo = new QuotationEmailVO();
		vo.setFormNumber(quote.getFormNumber());
		vo.setQuoteId(quote.getId());
		Customer customer = qi.getSoldToCustomer();
		
		LOG.info("quote item's size ===>>> " +quote.getQuoteItems().size() );
		
		
	//	vo.setHssfWorkbook(getQuoteTemplateBySoldTo(customer,quote));
		vo.setQuote(quote);
		vo.setSoldToCustomer(customer);
		vo.setSubmissionDateFromQuote(true);
		vo.setRecipient(quote.getSales().getName());
		
		vo.setSender(currentUser.getName());
		vo.setFromEmail(currentUser.getEmailAddress());
		

		String subject = "Quotation - "+ quote.getFormNumber();

		if (getCustomerFullName(customer) != null)
		{
			subject += " - "+ getCustomerFullName(customer);
		}
		
		vo.setFileName(subject);
		
		if(quote != null){
			String yourReference = "";
			if(!QuoteUtil.isEmpty(quote.getYourReference())){
				if(quote.getYourReference().length() > 50)
    				yourReference = quote.getYourReference().substring(0, 50);
				else
					yourReference = quote.getYourReference();
    			subject += " - Reference :  " + yourReference;	    			
			}
		}						
		vo.setSubject(subject);

		List<String> toEmails = new ArrayList<String>();
		toEmails.add(quote.getSales().getEmployeeId());
		vo.setToEmails(toEmails);
		List<String> ccEmails = new ArrayList<String>();
		if (quote.getCopyToCS() != null)
		{
			String[] csIds = quote.getCopyToCS().split(
					";");
			for (String csId : csIds)
			{
				if (!QuoteUtil.isEmpty(csId))
				{
					ccEmails.add(csId);
				}
			}
		}
		vo.setCcEmails(ccEmails);
		//sendQuotationEmail(vo);
		//SendQuotationEmailStrategy strategy = (SendQuotationEmailStrategy) cacheUtil.getStrategy(SendQuotationEmailStrategy.class,
			//	Constants.DEFAULT);
		
	
		SendQuotationEmailStrategy strategy =(SendQuotationEmailStrategy) StrategyFactory.getSingletonFactory()
		.getStrategy(SendQuotationEmailStrategy.class,currentUser.getDefaultBizUnit().getName(),
				this.getClass().getClassLoader());
		
		strategy.sendQuotationEmail(vo);
	
	}
		
	public void quoteRateRequest() {
		exCurrency = null;
		LOG.info("Start call validation Quotation");
		boolean isValid = true;
		boolean isEffectValid = true;
		clearErrorRow();
		
		// Changes for pagination done by NTI
		Set<QuoteItemVo> cacheItems = getCacheSelectionItems();
		selectedQuoteItemVos =  new ArrayList<>();
		selectedQuoteItemVos.addAll(cacheItems);
		
		if (selectedQuoteItemVos.size() == 0) {
			FacesContext.getCurrentInstance().addMessage("growl",new FacesMessage(FacesMessage.SEVERITY_WARN,ResourceMB.getText("wq.message.noRecordSelected")+" !", ""));
			return;
		}
		
		for (QuoteItemVo vo : selectedQuoteItemVos) {
			if (vo.getQuoteItem().getStage() == null
					|| !vo.getQuoteItem().getStage()
							.equals(QuoteSBConstant.QUOTE_STAGE_FINISH)) {
				vo.setErrRow(true);
				isValid = false;
			}
			
			if (vo.getQuoteItem().getQuotationEffectiveDate() == null) {
				vo.setErrRow(true);
				isEffectValid = false;
			}
		}
		
		if(!isValid) {
			FacesContext.getCurrentInstance().addMessage("growl",new FacesMessage(FacesMessage.SEVERITY_WARN,ResourceMB.getText("wq.message.canUpdateFinishQuoteExchageRate")+".",""));
			return;
		}
		
		if(!isEffectValid) {
			isValid = false;
			FacesContext.getCurrentInstance().addMessage("growl",new FacesMessage(FacesMessage.SEVERITY_WARN,ResourceMB.getText("wq.message.quoteEffectiveDateNotPermit")+".",""));
			return;
		}
		
		if (isValid) {
			
			RequestContext context = RequestContext.getCurrentInstance();
			context.addCallbackParam("saveValidationSucceed", "1");
			//proceedQuoteItemVos = generateProceedQuoteItems(selectedQuoteItemVos);
		} 

		refreshExchangeRate();
		return;
	
	}
	
	public void refreshExchangeRate() {
/****Multi Currency Project 201810 (whwong) had remove Currency option
		if(org.apache.commons.lang.StringUtils.isBlank(exCurrency)) {
			FacesContext.getCurrentInstance().addMessage("growl",new FacesMessage(FacesMessage.SEVERITY_WARN,ResourceMB.getText("wq.message.noExchangeCurrencySelected")+" !", ""));
			return;
		}
*/		
		// Changes for pagination done by NTI
		Set<QuoteItemVo> cacheItems = getCacheSelectionItems();
		selectedQuoteItemVos =  new ArrayList<>();
		selectedQuoteItemVos.addAll(cacheItems);
		
/****Multi Currency Project 201810 (whwong) had remove Currency option
		for(QuoteItemVo vo : selectedQuoteItemVos){
			vo.getQuoteItem().setRfqCurr(Currency.getByCurrencyName(exCurrency));
		}
*/
		proceedQuoteItemVos = generateProceedQuoteItems(selectedQuoteItemVos);
	}
	
	/**
	 * generate and find out the valid proceedQuoteItem
	 * @param selectedQuoteItemVos
	 * @return
	 */
	private List<QuoteItemVo> generateProceedQuoteItems(
			List<QuoteItemVo> vos) {
		List<QuoteItemVo> returnLst = new ArrayList<QuoteItemVo>();
		ExchangeRate latestRate = null;
		for(QuoteItemVo vo : vos){
			QuoteItem qi = vo.getQuoteItem();
			if(StringUtils.isEmpty(qi.getRfqCurr().name())){
				ExcCurrencyDefault ecd = exchangeRateSB.findDefaultCurrencyByBu(qi.getQuote().getBizUnit().getName());
				if(ecd != null){
					qi.setRfqCurr(Currency.getByCurrencyName(ecd.getCurrency()));
					qi.setBuyCurr(Currency.USD);
				}
			}			
			//latestRate = quoteSB.findLatestExchangeRate(qi);
			ExchangeRate exRates[];
			exRates = Money.getExchangeRate(qi.getBuyCurr(), qi.getRfqCurr(), qi.getQuote().getBizUnit(), qi.getSoldToCustomer(), new Date());
			//if(null!=latestRate) {
			if(null!=exRates[1] && exRates[0]!=null) {
				vo.setExChangeRate(exRates[1]);
				vo.setExChangeRateBuy(exRates[0]);
				//BigDecimal calculateRate = latestRate.getExRateTo().multiply(latestRate.getHandling()).multiply(latestRate.getVat()).setScale(5,BigDecimal.ROUND_HALF_UP);
				//vo.setExRateTo(calculateRate);
				returnLst.add(vo);
			}
			
		}
		return returnLst;
	}
	
	public byte[] transfer(Object obj) {
		try {
			ByteArrayOutputStream bo = new ByteArrayOutputStream();
			ObjectOutputStream oo = new ObjectOutputStream(bo);
			oo.writeObject(obj);
			return bo.toByteArray();
		} catch (IOException e) {
			LOG.log(Level.SEVERE, "exception in converting object to raw data , Exception message : "+e.getMessage(), e);
		}
		return new byte[0];
	}

	private void addbalanceUnconsumedQtyInquoteItemVo(
			List<QuoteItemVo> quoteItemVos) {
		String bizUnitName = currentUser.getDefaultBizUnit().getName();
		for (QuoteItemVo vo : quoteItemVos) {
			QuoteItem quoteItem = vo.getQuoteItem();
			if (quoteItem == null) {
				vo.setBalanceUnconsumedQty(null);
			} else {
				
				//fixed by Damonchen@20180411
				if (quoteItem.getQuotedMfr() != null) {
				Integer balanceUnconsumedQty = balanceUnconsumedQtySB
						.getBalUnconsumedQtyByCriteria(quoteItem.getQuotedMfr()
								.getName(), quoteItem.getQuoteNumber(),
								quoteItem.getQuotedPartNumber(), quoteItem
										.getVendorQuoteNumber(), bizUnitName);
				vo.setBalanceUnconsumedQty(balanceUnconsumedQty);
				}
			}
		}
	}

	public void updatePartNumberSearchDialogBox(ActionEvent event) {
		QuoteItemVo vo = (QuoteItemVo) event.getComponent().getAttributes()
				.get("targetQuoteItemVo");
		long materialId = vo.getQuoteItem().getQuotedMaterial().getId();
		List<String> sapMaterialTuples = sapMaterialSB
				.findSapPartNumbersByMaterialID(materialId);
		String mfrName = vo.getQuoteItem().getQuotedMaterial()
				.getManufacturer().getName();

		sapMaterialList = new ArrayList<String>();
		for (int i = 0; i < sapMaterialTuples.size(); i++) {
			sapMaterialList.add(mfrName + " | " + sapMaterialTuples.get(i));
		}
		RequestContext.getCurrentInstance().execute(
				"PF('search_Parts_dialog').show()");
	}

	public boolean isInsideSalesSearchButton() {
		return insideSalesSearchButton;
	}

	public void setInsideSalesSearchButton(boolean insideSalesSearchButton) {
		this.insideSalesSearchButton = insideSalesSearchButton;
	}

	public boolean isKeepFirstRow() {
		return keepFirstRow;
	}

	public void setKeepFirstRow(boolean keepFirstRow) {
		this.keepFirstRow = keepFirstRow;
	}

	public List<String> getSapMaterialList() {
		return sapMaterialList;
	}

	public void setSapMaterialList(List<String> sapMaterialList) {
		this.sapMaterialList = sapMaterialList;
	}

	public QuoteItemVo getCurrentQuoteItemVo() {
		return currentQuoteItemVo;
	}

	public void setCurrentQuoteItemVo(QuoteItemVo currentQuoteItemVo) {
		this.currentQuoteItemVo = currentQuoteItemVo;
	}

	public List<QuoteItemVo> getProceedQuoteItemVos() {
		return proceedQuoteItemVos;
	}

	public void setProceedQuoteItemVos(List<QuoteItemVo> proceedQuoteItemVos) {
		this.proceedQuoteItemVos = proceedQuoteItemVos;
	}
	

	public void rowSelectupdate(SelectEvent event){
		RequestContext context = RequestContext.getCurrentInstance();				
		QuoteItemVo item = (QuoteItemVo) event.getObject();
		this.currentQuoteItemVo= item;
		context.update("form:myQuoteSearchDetailInfo");		
		
	}
	
	public void onRowUnselect(UnselectEvent event) 
	{
		RequestContext context = RequestContext.getCurrentInstance();				
		QuoteItemVo item = (QuoteItemVo) event.getObject();
		this.currentQuoteItemVo= item;
		context.update("form:myQuoteSearchDetailInfo");	
	}
	
	public void onPageChange(PageEvent event) {  
		 doKeepFirstRow();

    }  
	
	public void updateBMTInfo() {

		LOG.info("Start call validation updateBMTInfo");
		clearErrorRow();
		
		// Changes for pagination done by NTI
		Set<QuoteItemVo> cacheItems = getCacheSelectionItems();
		selectedQuoteItemVos =  new ArrayList<>();
		selectedQuoteItemVos.addAll(cacheItems);
		
		if (selectedQuoteItemVos.size() == 0) {
			FacesContext.getCurrentInstance().addMessage("growl",new FacesMessage(FacesMessage.SEVERITY_WARN,ResourceMB.getText("wq.message.noRecordSelected")+" !", ""));
			return ;
		} else {
			if (selectedQuoteItemVos.size() >1) {
				FacesContext.getCurrentInstance().addMessage("growl",new FacesMessage(FacesMessage.SEVERITY_WARN,ResourceMB.getText("wq.message.moreThanOneRecordSeleced") +"!", ""));
				return ;
			}else {
				
				QuoteItem item = this.getSelectedQuoteItemVos().get(0).getQuoteItem();
				
				if(null==item.getQuoteItemDesign()) {
					FacesContext.getCurrentInstance().addMessage("growl",new FacesMessage(FacesMessage.SEVERITY_WARN,ResourceMB.getText("wq.message.doNotHaveBMTInfo")+".", ""));
					return;
				} else {
					String stage = item.getStage();
					String bmtFlagCode = item.getQuoteItemDesign().getBmtFlag().getBmtFlagCode();
					
					if(QuoteSBConstant.QUOTE_STAGE_FINISH.equalsIgnoreCase(stage) && bmtFlagCode.equalsIgnoreCase(BmtFlagEnum.BMT_DR_Incomplete.code().toString())){
						RequestContext context = RequestContext.getCurrentInstance();
						context.addCallbackParam("saveValidationSucceed", "1");
						this.currentQuoteItemVo = this.selectedQuoteItemVos.get(0);
						
						doKeepFirstRow();
						bmtInfoMB.setUpdateQuoteItem(currentQuoteItemVo);
						LOG.info("End call validation updateBMTInfo");
						return ;
					} else {
						FacesContext.getCurrentInstance().addMessage("growl",new FacesMessage(FacesMessage.SEVERITY_WARN,ResourceMB.getText("wq.message.onlyUpdateCanBMTInfoForFinishAndDRIncompleate")+".",""));
						return ;
					} //QuoteSBConstant.QUOTE_STAGE_FINISH.equalsIgnoreCase(stage) &&
					
				}//null==item.getQuoteItemDesign()


			}//selectedQuoteItemVos.size() >1
			
		}//selectedQuoteItemVos.size() == 0

	}
	
	
	public String copyBMTQuote() {
		LOG.info("Start call copyBMTQuote");
		clearErrorRow();

		// Changes for pagination done by NTI
		Set<QuoteItemVo> cacheDatas = getCacheSelectionItems();
		selectedQuoteItemVos =  new ArrayList<>();
		selectedQuoteItemVos.addAll(cacheDatas);
		
		if (selectedQuoteItemVos.size() == 0) {
			FacesContext.getCurrentInstance().addMessage(
					"growl",
					new FacesMessage(FacesMessage.SEVERITY_WARN,
							ResourceMB.getText("wq.message.selRecord") +"!", ""));
			return null;
		}
		
		if (selectedQuoteItemVos.size() >10) {
			FacesContext.getCurrentInstance().addMessage(
					"growl",
					new FacesMessage(FacesMessage.SEVERITY_WARN,
							ResourceMB.getText("wq.message.selRecordMaXNumberTen") +"!", ""));
			return null;
		}

		boolean valid = true;
		boolean validDesign = true;
		for (QuoteItemVo vo : selectedQuoteItemVos) {
			QuoteItem item = vo.getQuoteItem();
			if (item.getStage() == null
					|| !item.getStage().equals(
							QuoteSBConstant.QUOTE_STAGE_FINISH)) {
				valid = false;
			}
			if(null==item.getQuoteItemDesign()) {
				validDesign= false;
			}
		}

		if (!valid) {
			FacesContext.getCurrentInstance().addMessage(
					"growl",
					new FacesMessage(FacesMessage.SEVERITY_WARN,
							ResourceMB.getText("wq.message.onlyFinishQuote")+".", ""));
			return null;
		}
		
		if (!validDesign) {
			FacesContext.getCurrentInstance().addMessage(
					"growl",
					new FacesMessage(FacesMessage.SEVERITY_WARN,
							ResourceMB.getText("wq.message.doNotHaveBMTInfo")+".", ""));
			return null;
		}

		doKeepFirstRow();

		bmtInfoMB.setCopyBmtQuoteItems(selectedQuoteItemVos);
		LOG.info("End call copyBMTQuote");
		return "CopyBMTQuote.xhtml?faces-redirect=true";
	}
	
	public void changePendingAt() {
		LOG.info("pending...."+this.criteria.getPending());
		RequestContext context = RequestContext.getCurrentInstance();		
		List<String> pendings = this.criteria.getPending();
		List<String> statuss = new ArrayList<String>();//this.criteria.getStatus();
		for(String pending:pendings) {
			List<String> pStatuss = QuoteUtil.pendingAtStatusLaw(pending);
			statuss.addAll(pStatuss);
		}
		
    	this.criteria.setStatus(statuss);
    	LOG.info("statuss...."+statuss);
    	context.update("form:panelSrcCtr:pageStatuss");	
	}

	public void sendQueue(long requestId)
	{
		LOG.info("sendQueue start requestId:" + requestId	);
		InitialContext ini;
		try
		{
			ini = new InitialContext();
			final ConnectionFactory factory = (QueueConnectionFactory) ini.lookup("/ConnectionFactory");
			final Destination destination =(Destination) ini.lookup("/queue/offlineRptQueue");
	        final Connection connection = factory.createConnection();
	        final Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
	        MapMessage message = session.createMapMessage();
	        message.setLong("requestId", requestId);
	        final MessageProducer sender = session.createProducer(destination);
	        sender.send(message);
	        Thread.sleep(1000);
	        session.close();
	        connection.close();
	        LOG.info("sendQueue end requestId:" + requestId);
		}
		catch (Exception e)
		{
			LOG.log(Level.SEVERE,"Error occured when get search Method with reflection in MyQuoteSearchMB for Avnet Quote No. "+criteria.getsQuoteNumber()+""
					+ ", MFR: "+criteria.getsMfr()+""
							+ ", Employee Name: "+criteria.getsSalesName()+", Reason to failed : "+MessageFormatorUtil.getParameterizedStringFromException(e),e);
		
		}
		

	}

	public SelectItem[] getExCurrencyList() {
		return exCurrencyList;
	}

	public void setExCurrencyList(SelectItem[] exCurrencyList) {
		this.exCurrencyList = exCurrencyList;
	}

	public String getExCurrency() {
		return exCurrency;
	}

	public void setExCurrency(String exCurrency) {
		this.exCurrency = exCurrency;
	}

	public String getPageRoleName() {
		return pageRoleName;
	}

	public void setPageRoleName(String pageRoleName) {
		this.pageRoleName = pageRoleName;
	}
	
	
	public UploadedFile getQuoteExcel() {
		return quoteExcel;
	}

	public void setQuoteExcel(UploadedFile quoteExcel) {
		this.quoteExcel = quoteExcel;
	}

	
	
	public void uploadQuoteFile(){
		LOG.info("uploadQuoteFile begin.........");
		if (quoteExcel != null) {
			Future<String> myFutureResult =null;
			String fileName = quoteExcel.getFileName();
			String extensionName=fileName.substring(fileName.lastIndexOf(".")+1);
			LOG.info("extensionName:"+extensionName);
			if((!"xls".equals(extensionName.toLowerCase())) &&(!"xlsx".equals(extensionName.toLowerCase()))){
				String s = ResourceMB.getText("wq.message.uploeadedQuoteIsInWrongFormat")+".";
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN,s,""));
				return ;
			}
			User user = UserInfo.getUser();
			myFutureResult=asynReviseFinalQuotedPriceBean.massRevise(quoteExcel,user,insideSalesSearchButton,resourceMB.getResourceLocale());
			
			//StringBuffer sb=new StringBuffer();
			//sb.append("Your request has been submitted.").append("<br/>").append("A summary of the upload result will be sent to your email within 1 hour.");
			String returnMsg="";
			try {
				returnMsg = myFutureResult.get();
			} catch (InterruptedException e) {
				LOG.log(Level.SEVERE,"Error occured when get search Method with reflection in MyQuoteSearchMB for Form No. "+criteria.getsFormNumber()+""
						+ ", MFR: "+criteria.getsMfr()+""
								+ ", Employee Name: "+criteria.getsSalesName()+",Customer name: "+criteria.getsCustomerName()+", Sold to Code: "+criteria.getsSoldToCode()+", Reason to failed : "+MessageFormatorUtil.getParameterizedStringFromException(e),e);
			} catch (ExecutionException e) {
				LOG.log(Level.SEVERE,"Error occured when get search Method with reflection in MyQuoteSearchMB for Form No. "+criteria.getsFormNumber()+""
						+ ", MFR: "+criteria.getsMfr()+""
								+ ", Employee Name: "+criteria.getsSalesName()+",Customer name: "+criteria.getsCustomerName()+", Sold to Code: "+criteria.getsSoldToCode()+", Reason to failed : "+MessageFormatorUtil.getParameterizedStringFromException(e),e);
			}
			LOG.info("the return MSG form massRevise is: "+returnMsg);
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO,returnMsg,""));
			
		}else{
			LOG.info("quoteExcel is null");
		}
		
		LOG.log(Level.INFO, "begin to search again,the insideSalesSearch is: "+this.insideSalesSearchButton);
		if(this.insideSalesSearchButton	){
			insideSalesSearch();
		}else{
			salesSearch();
		}
		LOG.log(Level.INFO, "end to search again,the insideSalesSearch is: "+this.insideSalesSearchButton);
		
		
	}
	
	/**
	 * Setter for resourceMB
	 * @param resourceMB ResourceMB
	 * */
	public void setResourceMB(final ResourceMB resourceMB) {
		this.resourceMB = resourceMB;
	}

	
	public LazyDataModelForMyQuoteSearch getQuoteItemVos() {
		
		HttpServletRequest origRequest = (HttpServletRequest)FacesContext.getCurrentInstance().getExternalContext().getRequest();
		String url = origRequest.getRequestURL().toString();
		
		if(url.contains("MyQuoteListForSales")) {
			this.pageRoleName ="Sales";
		} else if( url.contains("MyQuoteListForCS")) {
			this.pageRoleName="CS";
		} else if( url.contains("MyQuoteListForBMT")) {
			this.pageRoleName="BMT";
		} else if( url.contains("MyQuoteListForQC")) {
			this.pageRoleName="QC";
		} else if( url.contains("MyQuoteListForPM")) {
			this.pageRoleName="PM";
		}else if( url.contains("MyQuoteListForMM")) {
			this.pageRoleName="MM";
		}
		
		return quoteItemVos;
	}

	public void setQuoteItemVos(LazyDataModelForMyQuoteSearch quoteItemVos) {
		this.quoteItemVos = quoteItemVos;
	}
	
	
	public Set<QuoteItemVo> getCacheSelectionItems() {
		if(quoteItemVos != null){
			return quoteItemVos.getCacheSelectedItems() ;
		}else{
			return new HashSet<QuoteItemVo>() ;
		}
		
	}
	
	//NEC Pagination Changes: Inner Class created for implementing Pagination with Lazy Loading
	public class LazyDataModelForMyQuoteSearch extends SelectableLazyDataModel<QuoteItemVo>{
		
		
		private Boolean csSearch;
		private Boolean salesSearch;
		private Boolean qcSearch;
		private Boolean mmSearch;
		private Boolean pmSearch;
		private Boolean bmtSearch;
		
		
		public void startCsSearch(){
			startPagination();
			csSearch = true;
			salesSearch = false;
			qcSearch = false;
			mmSearch = false;
			pmSearch = false;
			bmtSearch = false;
		}
		
		public void startSalesSearch(){
			startPagination();
			csSearch = false;
			salesSearch = true;
			qcSearch = false;
			mmSearch = false;
			pmSearch = false;
			bmtSearch = false;
		}
		
		public void startQcSearch(){
			startPagination();
			csSearch = false;
			salesSearch = false;
			qcSearch = true;
			mmSearch = false;
			pmSearch = false;
			bmtSearch = false;
		}
		
		public void startMmSearch(){
			startPagination();
			csSearch = false;
			salesSearch = false;
			qcSearch = false;
			mmSearch = true;
			pmSearch = false;
			bmtSearch = false;
		}
		
		public void startPmSearch(){
			startPagination();
			csSearch = false;
			salesSearch = false;
			qcSearch = false;
			mmSearch = false;
			pmSearch = true;
			bmtSearch = false;
		}
		
		public void startBmtSearch(){
			startPagination();
			csSearch = false;
			salesSearch = false;
			qcSearch = false;
			mmSearch = false;
			pmSearch = false;
			bmtSearch = true;
		}
		
		//NEC Pagination Changes: sets maximum no. of pages that can be stored in cache at a time  
		@Override
		public void startPagination(){
			super.startPagination();
			String cachePageSizeVal = sysConfigSB.getProperyValue(SelectableLazyDataModel.CACHE_PAGE_SIZE);
			try{
				setCachePageSize(Integer.parseInt(cachePageSizeVal));
			}catch(Exception e){
				LOG.log(Level.SEVERE,
						"Error occured while setting size of cache, Exception message: "+e.getMessage(),
						e);
			}
			
		}
		
		@Override
		public void stopPagination(){
			super.stopPagination();
			csSearch = false;
			salesSearch = false;
			qcSearch = false;
			mmSearch = false;
			pmSearch = false;
			bmtSearch = false;
		}
		//NEC Pagination Changes: for calculation/logic after fetching records from EJB layer
		@Override
		public List<QuoteItemVo> populateData(List<QuoteItemVo> outcome) {
			
			if(csSearch != null && csSearch){
				myQuoteSearchSB.hideInfoForUnfinishedQuote(outcome);
			}else if(salesSearch != null && salesSearch){
				myQuoteSearchSB.hideInfoForUnfinishedQuote(outcome);
			}else if(qcSearch != null && qcSearch){
				addbalanceUnconsumedQtyInquoteItemVo(outcome);
			}else if(mmSearch != null && mmSearch){
				addbalanceUnconsumedQtyInquoteItemVo(outcome);
			}else if(pmSearch != null && pmSearch){
				
			}else if(bmtSearch != null && bmtSearch){
				
			}
			
			criteria.setQuoteId(null);//clear quoteid after search data
			
			return outcome;
		}
		
		//NEC Pagination Changes: finds data for previous page through asynchronous call to database
		@Override
		public void findLazyPreviousPageData(int first, int pageSize, int pageNumber, String sortField, String sort, Map<String, Object> filters,
				ConcurrentHashMap<Integer, List<QuoteItemVo>> map, Queue<Integer> queue, int cachePageSize) {
			myQuoteSearchSB.asyncSearch(criteria, resourceMB.getResourceLocale(), first, pageSize, pageNumber, sortField, sort, filters, map, queue,cachePageSize,false);
		}
		
		//NEC Pagination Changes: finds data for next page through asynchronous call to database
		@Override
		public void findLazyNextPageData(int first, int pageSize, int pageNumber, String sortField, String sort, Map<String, Object> filters,
				ConcurrentHashMap<Integer, List<QuoteItemVo>> map, Queue<Integer> queue, int cachePageSize) {
			myQuoteSearchSB.asyncSearch(criteria, resourceMB.getResourceLocale(), first, pageSize, pageNumber, sortField, sort, filters, map, queue,cachePageSize,false);
		}



		/***
	     * To fetch record for one page
	     * 
	      * @param first
	     * @param pageSize
	     * @param sortField
	     * @param sortOrder
	     * @param filters
	     * @return
	     */
		@Override
	     public List<QuoteItemVo> findLazyData(int first, int pageSize, String sortField, String sortOrder,
	                                     Map<String, Object> filters) {
			
			List<QuoteItemVo> outCome =  myQuoteSearchSB.search(criteria,resourceMB.getResourceLocale(),first,pageSize,sortField,sortOrder,filters,false);
			
			return outCome;

	     }

	     /***
	     * To find out no of record present in DB
	     * 
	      * @param sortField
	     * @param sort
	     * @param filters
	     * @return
	     */
		@Override
	     public int findLazyDataCount(String sortField, String sortOrder, Map<String, Object> filters) {
	                     return myQuoteSearchSB.count(criteria,resourceMB.getResourceLocale(),sortField,sortOrder,filters);
	     }
		
		
		
		@Override
		public Object getRowKey(QuoteItemVo object) {
			
				return ((QuoteItemVo) object).getQuoteItem().getId();
	
		}
		
		
		/* (non-Javadoc)
		 * @see org.primefaces.model.LazyDataModel#getRowData(java.lang.String)
		 */
		@Override
		public QuoteItemVo getRowData(String rowKey) {
			List<QuoteItemVo> list = (List<QuoteItemVo>) getWrappedData();
			if (!list.isEmpty()) {
				for (QuoteItemVo t : list) {
					
						String key =(String.valueOf(((QuoteItemVo) t).getQuoteItem().getId()));
						if( rowKey.equals(key))
							return t;
					
				}
			}

			return null;
		}

		
		@Override
		public void cellChangeListener(String id) {
			quoteItemVos.setCacheModifyData(id);
			FacesUtil.updateUI("form:datatable_myquotelist");
			
		}

		

		@Override
		protected SelectableLazyDataModel<QuoteItemVo> getLazyData() {
			return quoteItemVos;
		}

		@Override
		public void onRowSelectCheckbox(SelectEvent event) {
			super.onRowSelectCheckbox(event);
			onRowSelect(event);
		}

		@Override
		public void onRowUnselectCheckbox(UnselectEvent event) {
			super.onRowUnselectCheckbox(event);
			onRowUnselect(event);
		}

		


		
		
		/**
		 * This method will be called on Row select
		 */
		@Override
		public void onRowSelect(SelectEvent event) {
			super.onRowSelect(event);
			rowSelectupdate(event);
		}
		
		
		public List<QuoteItemVo> getSelectionItems() {
			
			
			Set<QuoteItemVo> cacheItems = getCacheSelectionItems();
			selectionItems =  new ArrayList<>();
			selectionItems.addAll(cacheItems);
			
			Collections.sort(selectionItems ,new Comparator<QuoteItemVo>() {
				public int compare(QuoteItemVo o1, QuoteItemVo o2){
					if (o1 == null && o2 == null) return (int) 0;
					else if (o1 == null && o2 !=null) return 0 - o2.getSeq();
					else if (o1 != null && o2 ==null) return o1.getSeq();
					else return o1.getSeq() - o2.getSeq();
				   }
			});
			
			return selectionItems;
		}

	}

	/**
	 * Project:	JP multi currency
	 * Project Duration:	201809 - 201901
	 * Module Spec (refer SDD):		5.6
	 * Function Spec (refer SDD):	5.6.2.4
	 * Develop By:	P03318 (winghong.wong@avnet.com)
	 * Created At: 	17 Sep 2018
	 * */
	public String dLinkQuoteCreation() {
		LOG.info("dLinkQuoteCreation: Begin");
		clearErrorRow();

		List<Integer> errorRows = new ArrayList<Integer>();

		// Changes for pagination done by NTI
		Set<QuoteItemVo> cacheDatas = getCacheSelectionItems();
		selectedQuoteItemVos =  new ArrayList<>();
		selectedQuoteItemVos.addAll(cacheDatas);
		
		if (selectedQuoteItemVos.size() == 0) {
			FacesContext.getCurrentInstance().addMessage(
					"growl",
					new FacesMessage(FacesMessage.SEVERITY_WARN,
							ResourceMB.getText("wq.message.noRecordSelected")+" !", ""));
			return null;
		}

		LOG.info("dLinkQuoteCreation: Rules Check");
		//selectedQuoteItemVos.get(firstRow).getQuoteItem().getQuote().getBizUnit()
		boolean valid = true;
		for (QuoteItemVo vo : selectedQuoteItemVos) {
			if (vo.getQuoteItem().getStage() == null
					|| !vo.getQuoteItem().getStage()
							.equals(QuoteSBConstant.QUOTE_STAGE_FINISH)) {
				valid = false;
				vo.setErrRow(true);
				vo.setMessage(ResourceMB.getText("wq.error.dLink002"));	//002
				errorRows.add(vo.getSeq());
			}
			
			if (!vo.getQuoteItem().getQuote().isdLinkFlag()){
				vo.setErrRow(true);
				//String sMsg = "1";
				//vo.setMsg(sMsg);
				//vo.setMessage(sMsg);
				//vo.setMessage("001");
				vo.setMessage(ResourceMB.getText("wq.error.dLink001"));
			} else if (vo.getQuoteItem().getStage() == null
					|| !vo.getQuoteItem().getStage()
					.equals(QuoteSBConstant.QUOTE_STAGE_FINISH)){
				vo.setErrRow(true);
				//vo.setMessage("002");
				vo.setMessage(ResourceMB.getText("wq.error.dLink002"));	//002
				
			}
			else if (((vo.getQuoteItem().getFinalQuotationPrice()==null)? 0.00 : vo.getQuoteItem().getFinalQuotationPrice())==0.00){
				vo.setErrRow(true);
				vo.setMessage(ResourceMB.getText("wq.error.dLink003"));	//003
			}
			else if (dLinkMB.currConvert(vo.getQuoteItem())==null){	//CUSTOMER_FIXED_RATE
				vo.setErrRow(true);
				vo.setMessage(ResourceMB.getText("wq.error.dLink004"));	//004
			} 
			else if (vo.getQuoteItem().getRfqCurr().name()!=vo.getQuoteItem().getFinalCurr().name()) {// (!vo.getQuoteItem().getFirstRfqCode().equals(vo.getQuoteItem().getQuoteNumber())){
				vo.setErrRow(true);
				vo.setMessage(ResourceMB.getText("wq.error.dLink005"));	//005
			}

		}

		
/*		if (valid == false) {
			String errMessage = ResourceMB.getText("wq.error.dLink002")+ ".<br/>"	//002
					+ convertErrorRows(errorRows);
			FacesContext.getCurrentInstance().addMessage(
					"growl",
					new FacesMessage(FacesMessage.SEVERITY_WARN, errMessage,
							""));
			return null;
		}
*/
		LOG.info("dLinkQuoteCreation: Rules Check Completed");
		doKeepFirstRow();
		Collections.sort(selectedQuoteItemVos ,new Comparator<QuoteItemVo>() {
			public int compare(QuoteItemVo o1, QuoteItemVo o2){
			      return o1.getSeq() - o2.getSeq();
			   }
		});
		//.quoteItem.quote.bizUnit.allowCurrencies
		Log.info("dLinkQuoteCreation: bizUnit=" + selectedQuoteItemVos.get(0).getQuoteItem().getQuote().getBizUnit().getName()
						+ "; AllowCurr=" + selectedQuoteItemVos.get(0).getQuoteItem().getQuote().getBizUnit().getAllowCurrencies().toString());
		LOG.info("dLinkQuoteCreation: Copy Selected Quote to dLinkMB");
		dLinkMB.checkCopyQuoteItem(selectedQuoteItemVos);
		dLinkMB.setFinished(false);
		//reviseFinalQuotedPriceMB.checkCopyQuoteItem(selectedQuoteItemVos);
		//reviseFinalQuotedPriceMB.setFinished(false);

		return "dLink.xhtml?faces-redirect=true";

	}

/*	public String defaultCurr() {
		//this.selectedQuoteItemVos.get(0).quoteItem.quote.bizUnit.defaultCurrency
		Currency currReturn = this.getSelectedQuoteItemVos().get(0).getQuoteItem().getQuote().getBizUnit().getDefaultCurrency();
		return currReturn==null? "": currReturn.name();
	}
*/
}