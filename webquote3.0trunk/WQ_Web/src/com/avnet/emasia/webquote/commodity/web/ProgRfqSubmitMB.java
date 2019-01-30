package com.avnet.emasia.webquote.commodity.web;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.nio.charset.Charset;
import java.text.MessageFormat;
import com.avnet.emasia.webquote.utilities.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.ejb.TransactionManagement;
import javax.ejb.TransactionManagementType;
import javax.faces.application.FacesMessage;
import javax.faces.application.FacesMessage.Severity;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.faces.model.SelectItem;
import javax.persistence.Column;
import javax.transaction.Status;
import javax.transaction.UserTransaction;

import org.apache.commons.lang.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.CellStyle;
import org.eclipse.jetty.util.log.Log;
import org.primefaces.component.datatable.DataTable;
import org.primefaces.context.RequestContext;
import org.primefaces.event.CellEditEvent;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;
import org.primefaces.model.UploadedFile;
import org.springframework.security.core.context.SecurityContextHolder;

import com.avnet.emasia.webquote.commodity.constant.ShoppingCartConstant;
import com.avnet.emasia.webquote.commodity.ejb.ProgramMaterialSB;
import com.avnet.emasia.webquote.commodity.helper.ProgRfqSubmitHelper;
import com.avnet.emasia.webquote.commodity.helper.SessionHelper;
import com.avnet.emasia.webquote.commodity.helper.ValidateHelper;
import com.avnet.emasia.webquote.commodity.util.PricerUtils;
import com.avnet.emasia.webquote.commodity.vo.ValidateError;
import com.avnet.emasia.webquote.constants.ActionEnum;
import com.avnet.emasia.webquote.dp.EJBCommonSB;
import com.avnet.emasia.webquote.entity.Attachment;
import com.avnet.emasia.webquote.entity.BizUnit;
import com.avnet.emasia.webquote.entity.City;
import com.avnet.emasia.webquote.entity.Country;
import com.avnet.emasia.webquote.entity.Currency;
import com.avnet.emasia.webquote.entity.Customer;
import com.avnet.emasia.webquote.entity.CustomerAddress;
import com.avnet.emasia.webquote.entity.CustomerPartner;
import com.avnet.emasia.webquote.entity.CustomerSale;
import com.avnet.emasia.webquote.entity.CustomerSalePK;
import com.avnet.emasia.webquote.entity.ExcCurrencyDefault;
import com.avnet.emasia.webquote.entity.Material;
import com.avnet.emasia.webquote.entity.Money;
import com.avnet.emasia.webquote.entity.Pricer;
import com.avnet.emasia.webquote.entity.ProgramPricer;
import com.avnet.emasia.webquote.entity.QuantityBreakPrice;
import com.avnet.emasia.webquote.entity.Quote;
import com.avnet.emasia.webquote.entity.QuoteItem;
import com.avnet.emasia.webquote.entity.QuoteResponseTimeHistory;
import com.avnet.emasia.webquote.entity.RFQSubmissionTypeEnum;
import com.avnet.emasia.webquote.entity.Role;
import com.avnet.emasia.webquote.entity.SalesOrg;
import com.avnet.emasia.webquote.entity.Team;
import com.avnet.emasia.webquote.entity.User;
import com.avnet.emasia.webquote.exception.AppException;
import com.avnet.emasia.webquote.exception.WebQuoteException;
import com.avnet.emasia.webquote.helper.TransferHelper;
import com.avnet.emasia.webquote.masterData.exception.CheckedException;
import com.avnet.emasia.webquote.quote.ejb.CitySB;
import com.avnet.emasia.webquote.quote.ejb.CustomerSB;
import com.avnet.emasia.webquote.quote.ejb.ExchangeRateSB;
import com.avnet.emasia.webquote.quote.ejb.ManufacturerSB;
import com.avnet.emasia.webquote.quote.ejb.MaterialSB;
import com.avnet.emasia.webquote.quote.ejb.MyQuoteSearchSB;
import com.avnet.emasia.webquote.quote.ejb.QuoteSB;
import com.avnet.emasia.webquote.quote.ejb.RestrictedCustomerMappingSB;
import com.avnet.emasia.webquote.quote.ejb.SAPWebServiceSB;
import com.avnet.emasia.webquote.quote.ejb.SystemCodeMaintenanceSB;
import com.avnet.emasia.webquote.quote.ejb.constant.QuoteSBConstant;
import com.avnet.emasia.webquote.quote.vo.ShoppingCartLoadItemBean;
import com.avnet.emasia.webquote.strategy.StrategyFactory;
import com.avnet.emasia.webquote.user.ejb.ApplicationSB;
import com.avnet.emasia.webquote.user.ejb.BizUnitSB;
import com.avnet.emasia.webquote.user.ejb.UserSB;
import com.avnet.emasia.webquote.utilites.resources.ResourceMB;
import com.avnet.emasia.webquote.utilites.web.common.CacheUtil;
import com.avnet.emasia.webquote.utilites.web.common.Constants;
import com.avnet.emasia.webquote.utilites.web.util.DeploymentConfiguration;
import com.avnet.emasia.webquote.utilites.web.util.DownloadUtil;
import com.avnet.emasia.webquote.utilites.web.util.Excel20007Reader;
import com.avnet.emasia.webquote.utilites.web.util.PricerUploadHelper;
import com.avnet.emasia.webquote.utilites.web.util.QuoteUtil;
import com.avnet.emasia.webquote.utilites.web.util.ShoppingCartProcessSheet;
import com.avnet.emasia.webquote.utilities.DateUtils;
import com.avnet.emasia.webquote.utilities.MessageFormatorUtil;
import com.avnet.emasia.webquote.utilities.bean.MailInfoBean;
import com.avnet.emasia.webquote.utilities.common.SysConfigSB;
import com.avnet.emasia.webquote.utilities.ejb.MailUtilsSB;
import com.avnet.emasia.webquote.vo.CatalogMainVO;
import com.avnet.emasia.webquote.vo.PricerInfo;
import com.avnet.emasia.webquote.web.quote.FacesUtil;
import com.avnet.emasia.webquote.web.quote.constant.QuoteConstant;
import com.avnet.emasia.webquote.web.quote.job.FileUtil;
import com.avnet.emasia.webquote.web.quote.strategy.SendQuotationEmailStrategy;
import com.avnet.emasia.webquote.web.quote.vo.QuotationEmailVO;
import com.avnet.emasia.webquote.web.security.WQUserDetails;
import com.avnet.emasia.webquote.web.shoppingcart.helper.ShopCartSubmitResultDownLoadStrategy;
import com.avnet.emasia.webquote.web.shoppingcart.helper.ShoppingCartDownLoadStrategy;
import com.avnet.emasia.webquote.web.shoppingcart.helper.ShoppingCartUploadStrategy;
//import com.sap.document.sap.soap.functions.mc_style.ZwqCustomer;
import com.avnet.emasia.webquote.webservice.customer.ZwqCustomer;

@ManagedBean(name = "progRfqSubmitMB")
@SessionScoped
@TransactionManagement(TransactionManagementType.BEAN)
public class ProgRfqSubmitMB extends ProgRfqSubmitHelper implements java.io.Serializable {

	@EJB
	CacheUtil cacheUtil;
	@EJB
	RestrictedCustomerMappingSB restrictedCustomerSB;
	
	private static final Logger LOG = Logger.getLogger(ProgRfqSubmitMB.class.getName());
	/**
	 * 
	 */
	private static final long serialVersionUID = 4086791792133327708L;
	private static final String EDIT_CELL_HEADER_TEXT_REQ_QTY = "Required Qty*";
	private static final String EDIT_CELL_HEADER_TEXT_TARGET_RESALE = "Target Resale";
	private static final String EMPTY_STR = "";
	private Quote quote;
	private QuoteItem selectedQuoteItem;
	private Integer selectedRowIndex;
	// added by June for Project RMB cur 20140704
	private SelectItem[] exCurrencyList;
	private String rfqCurr;
	private String systemInfo;
	private String newCustomerTitle;
	private String newProspectiveCustomerType;
	

	private User user;
	private boolean salesmanListForCsPanelDisplay;
	private boolean salesmanListForQcPanelDisplay;
	private boolean salesmanListForInsideSalesDisplay;
	private boolean rfqOperationPanelDisplay;

	private List<User> salesList;
	private Map<String, User> salesMap;
	private List<Attachment> attachments;
	private int itemNumberForAttachment;

	//private Quote submittedQuote;

	private Quote quoteInitialCopy;

	private int seletedDeletedAttachementRowId;

	private int submitType;
	private List<String> tempSTList;

	private String shoppingCartInfo;

	private List<QuoteItem> selectedSubmitResultItems = null;
	/*
	 * For customer search and creation
	 */
	private String customerTypeSearch;
	private String customerNameSearch;
	private SelectItem[] customerTypeSearchSelectList;
	private SelectItem[] countrySelectList;
	private SelectItem[] citySelectList;
	private SelectItem[] salesOrgSelectList;
	private List<Customer> searchCustomers;
	private List<Customer> selectedSearchCustomers;
	private Customer selectedSearchCustomer;
	private boolean deleteCustomer = false;
	
	private boolean needSave = false ;
	// New prospective part
	private String newProspectiveCustomerName1;
	private String newProspectiveCustomerName2;
	private String newProspectiveCustomerCountry;
	private String newProspectiveCustomerCity;
	private String newProspectiveCustomerAddress3;
	private String newProspectiveCustomerAddress4;
	private String newProspectiveCustomerSalesOrg;

	private boolean newProspectiveCustomerDuplicated;

	private String duplicatedCustomerCode;
	private String duplicatedCustomerName;
	private String duplicatedCustomerType;
	private String duplicatedCountry;
	private String duplicatedCity;
	private String duplicatedSalesOrg;

	private int searchCustomersCount = 0;

	private BizUnit bizUnit;
	private BizUnit salesBizUnit;

	private SelectItem[] regionList;

	private String region;

	// for result page.
	private Quote resultQuote;
	private List<String> standTermsAndCons;
	private List<String> specTermsAndCons;

	// private transient StreamedContent file;
	private transient StreamedContent downloadAttachment;

	private boolean createProspectiveCustomerButtonDisplay = true;

	private transient UploadedFile uploadFile;

	private transient StreamedContent downloadAll;
	private ShoppingCartDownLoadStrategy downloadSrategy = new ShoppingCartDownLoadStrategy();
	// private CatalogSearchResultDownLoadStrategy downloadSrategy = new
	// CatalogSearchResultDownLoadStrategy();

	private String uploadFileName;

	private final static String END_CUST = "END_CUST";

	private final static String SOLD_TO_CUST = "SOLD_TO_CUST";

	private String selectForWhichCustomer = SOLD_TO_CUST;

	private SubmitResultInfo submitResultInfo;

	private boolean salesRole = false;
	
	private boolean qco =false;
	// private

	public void changeToEndCustomerCodeSearch() {

		// logger.log(Level.INFO,
		// "PERFORMANCE START - changeToEndCustomerCodeSearch()");
		this.selectForWhichCustomer = END_CUST;
		clearCustomerSearchResult();

		// logger.log(Level.INFO,
		// "PERFORMANCE END - changeToEndCustomerCodeSearch()");
	}

	public void changeToSoldToCodeSearch() {

		// logger.log(Level.INFO,
		// "PERFORMANCE START - changeToSoldToCodeSearch()");

		this.selectForWhichCustomer = SOLD_TO_CUST;
		clearCustomerSearchResult();

		// logger.log(Level.INFO,
		// "PERFORMANCE END - changeToSoldToCodeSearch()");
	}
	/*
	 * private void ChangeToEndCust() { this.selectForWhichCustomer=END_CUST; }
	 * 
	 * private void ChangeToSoldToCust() {
	 * this.selectForWhichCustomer=SOLD_TO_CUST; }
	 */

	public ProgRfqSubmitMB() {
		// TODO Auto-generated constructor stub
	}

	@ManagedProperty(value = "#{resourceMB}")
	/** To Inject ResourceMB instance */
	private ResourceMB resourceMB;
	@EJB
	private UserSB userSB;
	@EJB
	private QuoteSB quoteSB;
	@EJB
	private CustomerSB customerSB;
	@EJB
	private ApplicationSB applicationSB;
	@EJB
	private SAPWebServiceSB sapWebServiceSB;
	@EJB
	private MailUtilsSB mailUtilsSB;

	@EJB
	private ExchangeRateSB exChangeRateSB;

	@EJB
	private CitySB citySB;
	@EJB
	private ManufacturerSB manufacturerSB;
	@EJB
	private SysConfigSB sysConfigSB;
	@EJB
	private ProgramMaterialSB programMaterialSB;
	@EJB
	private MyQuoteSearchSB myQuoteSearchSB;

	@EJB
	transient SystemCodeMaintenanceSB sysMaintSB;

	@Resource
	private transient UserTransaction ut;

	@EJB
	private MaterialSB materialSB;

	@EJB
	protected EJBCommonSB ejbCommonSB;

	@EJB
	private BizUnitSB bizUnitSB;

	@PostConstruct
	public void initialize() {
		LOG.fine("call ProgRfqSumbitMB initialize");
		if (null == user) {
			user = ((WQUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUser();
		}
		bizUnit = user.getDefaultBizUnit();

		// user = UserInfo.getUser();
		this.regionList = QuoteUtil.getUserRegionDownListByOrder(user.getAllBizUnits(),
				bizUnitSB.getAllBizUnitsByOrder());
		this.region = user.getDefaultBizUnit().getName();
		// applicationSB.isActionAccessibleByUser(user,
		// "RfqSubmission.roleProspective.Sales");
		// isInsideSalesRole = applicationSB.isActionAccessibleByUser(user,
		// "RfqSubmission.roleProspective.InsideSales");
		/*
		 * isSalesRole = applicationSB.isActionAccessibleByUser(user,
		 * "RfqSubmission.roleProspective.Sales"); isCSRole =
		 * applicationSB.isActionAccessibleByUser(user,
		 * "RfqSubmission.roleProspective.CS"); isQCOperation =
		 * applicationSB.isActionAccessibleByUser(user,
		 * "RfqSubmission.roleProspective.QCOperation"); isQCPricing =
		 * applicationSB.isActionAccessibleByUser(user,
		 * "RfqSubmission.roleProspective.QCPricing");
		 */
		qco = applicationSB.isActionAccessibleByUser(user, "RfqSubmission.roleProspective.QCOperation");
		salesmanListForCsPanelDisplay = applicationSB.isActionAccessibleByUser(user,
				"ProgRfqSubmit.salesmanPanel.panel");
		salesmanListForQcPanelDisplay = applicationSB.isActionAccessibleByUser(user,
				"ProgRfqSubmit.salesmanPanelForQCO.panel");
		salesmanListForInsideSalesDisplay = applicationSB.isActionAccessibleByUser(user,
				"ProgRfqSubmit.salesmanPanelForInsideSales.panel");
		rfqOperationPanelDisplay = applicationSB.isActionAccessibleByUser(user,
				"CommodityMgt.RfqOperationPanel.buttons");
		salesRole = applicationSB.isActionAccessibleByUser(user, "RfqSubmission.roleProspective.Sales");

		quote = initialQuote();
		if (salesRole) {
			quote.setSales(user);
		}
		if (quote.getSales() != null) {
			salesBizUnit = quote.getSales().getDefaultBizUnit();
		}

		// Loading initial data.

		if (salesmanListForCsPanelDisplay) {
			salesList = userSB.findAllSalesForCs(user);
			salesMap = new HashMap<String, User>();
			if (salesList != null && salesList.size() > 0) {
				for (User tempUser : salesList) {
					salesMap.put(tempUser.getName(), tempUser);
				}
			}
		} else if (salesmanListForQcPanelDisplay) {
			// salesList=
			// userSB.findAllSalesByBizUnit(QuoteSBConstant.ROLE_SALES,
			// bizUnit);
			salesList = userSB.wFindAllSalesByBizUnit(null, null,
					new String[] { QuoteSBConstant.ROLE_SALES, QuoteSBConstant.ROLE_INSIDE_SALES,
							QuoteSBConstant.ROLE_SALES_MANAGER, QuoteSBConstant.ROLE_SALES_DIRECTOR,
							QuoteSBConstant.ROLE_SALES_GM },
					bizUnit);

			salesMap = new HashMap<String, User>();
			if (salesList != null && salesList.size() > 0) {
				for (User tempUser : salesList) {
					salesMap.put(tempUser.getName(), tempUser);
				}
			}
		} else if (salesmanListForInsideSalesDisplay) {
			LOG.fine("call ProgRfqSumbitMB initialize | it is inside sales");
			salesList = userSB.wFindAllSalesForCsForInsideSales(null, null, user);
			salesMap = new HashMap<String, User>();
			if (salesList != null && salesList.size() > 0) {
				for (User tempUser : salesList) {
					salesMap.put(tempUser.getName(), tempUser);
				}
			}
		}
		if(salesRole) {
			if(salesMap == null) {
				salesMap = new HashMap<String, User>();
			}
			salesMap.put(user.getName(), user);
		}

		customerTypeSearchSelectList = QuoteUtil.createFilterOptions(QuoteConstant.CUSTOMER_TYPE, false, true);

		List<String> countryCodes = new ArrayList<String>();
		List<String> countryNames = new ArrayList<String>();
		//Bryan Start
		//List<Country> countries = CountryCacheManager.getCountries();
		List<Country> countries = cacheUtil.getCountries();
		//Bryan End
		if (countries != null) {
			for (Country country : countries) {
				countryCodes.add(country.getId());
				countryNames.add(country.getName());
			}
		}
		countrySelectList = QuoteUtil.createFilterOptions(countryNames.toArray(new String[countryNames.size()]),
				countryCodes.toArray(new String[countryCodes.size()]), false, false);

		List<String> cityCodes = new ArrayList<String>();
		List<String> cityNames = new ArrayList<String>();
		citySelectList = QuoteUtil.createFilterOptions(cityCodes.toArray(new String[cityCodes.size()]),
				cityNames.toArray(new String[cityNames.size()]), false, false);

		updateSystemInfoForSalesChange();

		// webquote japan not need to show create prospective customer
		// button. added by Lenon.Yang(043044) 2016/07/16
		this.createProspectiveCustomerButtonDisplay = applicationSB.isActionAccessibleByUser(user,
				QuoteConstant.WEBPROMO_ALLOW_CREATE_PROSPECTIVE_CUSTOMER);
		this.updateSystemInfoForBizUnitChange();
	}

	/**   
	* @Description: TODO
	* @author 042659
	* @param quote2       
	* @return void    
	* @throws  
	*/  
	private void initRfqcurr(Quote quote2) {
		if (quote2 != null && quote2.getQuoteItems() != null && !quote2.getQuoteItems().isEmpty()
				&& quote2.getQuoteItems().get(0).getRfqCurr() != null) {
			this.setRfqCurr(quote2.getQuoteItems().get(0).getRfqCurr().name());
		}
	}

	private void updateSystemInfoForSalesChange() {
		if (quote != null && quote.getSales() != null) {
			BizUnit currsalesBizUnit = quote.getSales().getDefaultBizUnit();
			this.salesBizUnit = currsalesBizUnit;
			if (salesBizUnit != null) {
				//Bryan Start
				//List<String> salesOrgCodes = SalesOrgCacheManager.getSalesOrgCodesByBizUnit(currsalesBizUnit.getName());
				List<String> salesOrgCodes = cacheUtil.getSalesOrgCodesByBizUnit(salesBizUnit.getName());
				//Bryan End
				salesOrgSelectList = QuoteUtil.createFilterOptions(
						salesOrgCodes.toArray(new String[salesOrgCodes.size()]),
						salesOrgCodes.toArray(new String[salesOrgCodes.size()]), false, false);
				// logger.log(Level.INFO, "PERFORMANCE END - updateSalesOrg()");
			}else {
				salesOrgSelectList = new SelectItem[0]; 
			}
			
		}
	}

	/**
	 * @param resourceMB
	 *            the resourceMB to set
	 */
	public void setResourceMB(ResourceMB resourceMB) {
		this.resourceMB = resourceMB;
	}

	public boolean emptySalesMan() {
		return !(this.quote != null && this.quote.getSales() != null && this.quote.getSales().getId() > 0);
	}

	// TODO
	public void regionChanged() {
		// this.mfrDownListInit(this.criteria.getRegion());
		bizUnit = bizUnitSB.findByPk(this.getRegion());
		// salesBizUnit = bizUnit;
		// this.quote.setBizUnit(bizUnit);
		quote = initialQuote();
		this.updateSystemInfoForBizUnitChange();
	}

	/** first clear and fill with quoteIn db when exist */
	public Quote initialQuote() {
		//for
		clearPageQuote();
		Quote qInDB = quoteSB.getProgDraftRfq(user, bizUnit);
		if (qInDB != null) {
			fillDispalyQuoteWithDraftQuoteInDB(this.quote, qInDB);
		}
		updateSystemInfoForSalesChange();
		this.setNeedSave(false);
		return this.quote;
	}

	/** for clear **/
	private void clearPageQuote() {
		Quote returnQuote = new Quote();
		returnQuote.setQuoteItems(new ArrayList<QuoteItem>());
		returnQuote.setAttachments(new ArrayList<Attachment>());
		returnQuote.setSoldToCustomer(this.getEmptyCustomer());
		returnQuote.setEndCustomer(this.getEmptyCustomer());
		// if (salesmanListForCsPanelDisplay || salesmanListForQcPanelDisplay ||
		// salesmanListForInsideSalesDisplay) {
		if (!this.isSalesRole()) {
			User u = new User();
			u.setEmployeeId(EMPTY_STR);
			u.setName(EMPTY_STR);
			// WebQuote 2.2 enhancement. 6.1.1 part 2.
			Team tempTeam = new Team();
			tempTeam.setName(EMPTY_STR);
			u.setTeam(tempTeam);
			returnQuote.setSales(new User());
		} else {
			 
			returnQuote.setSales(user);
			// WebQuote 2.2 enhancement. 6.1.1 part 2.
			returnQuote.setTeam(user.getTeam());
		}
		returnQuote.setBizUnit(bizUnit);
		this.quote = returnQuote;
	}

	public List<Attachment> getAttachments() {
		return attachments;
	}

	public void setAttachments(List<Attachment> attachments) {
		this.attachments = attachments;
	}

	public List<User> getSalesList() {
		return salesList;
	}

	public void setSalesList(List<User> salesList) {
		this.salesList = salesList;
	}

	public Quote getResultQuote() {
		return resultQuote;
	}

	public void setResultQuote(Quote resultQuote) {
		this.resultQuote = resultQuote;
	}

	public boolean isSalesmanListForCsPanelDisplay() {
		return salesmanListForCsPanelDisplay;
	}

	public void setSalesmanListForCsPanelDisplay(boolean salesmanListForCsPanelDisplay) {
		this.salesmanListForCsPanelDisplay = salesmanListForCsPanelDisplay;
	}

	public boolean isSalesmanListForQcPanelDisplay() {
		return salesmanListForQcPanelDisplay;
	}

	public void setSalesmanListForQcPanelDisplay(boolean salesmanListForQcPanelDisplay) {
		this.salesmanListForQcPanelDisplay = salesmanListForQcPanelDisplay;
	}

	public Quote getQuote() {
		return quote;
	}

	public void setQuote(Quote quote) {
		this.quote = quote;
	}

	public QuoteItem getSelectedQuoteItem() {
		return selectedQuoteItem;
	}

	public void setSelectedQuoteItem(QuoteItem selectedQuoteItem) {
		this.selectedQuoteItem = selectedQuoteItem;
	}

	public StreamedContent getDownloadAttachment() {
		return downloadAttachment;
	}

	public void setDownloadAttachment(StreamedContent downloadAttachment) {
		this.downloadAttachment = downloadAttachment;
	}

	public long getItemNumberForAttachment() {
		return itemNumberForAttachment;
	}

	public void setItemNumberForAttachment(int itemNumberForAttachment) {
		this.itemNumberForAttachment = itemNumberForAttachment;
	}

	public String getCustomerTypeSearch() {
		return customerTypeSearch;
	}

	public void setCustomerTypeSearch(String customerTypeSearch) {
		this.customerTypeSearch = customerTypeSearch;
	}

	public SelectItem[] getCustomerTypeSearchSelectList() {
		return customerTypeSearchSelectList;
	}

	public int getSeletedDeletedAttachementRowId() {
		return seletedDeletedAttachementRowId;
	}

	public void setSeletedDeletedAttachementRowId(int seletedDeletedAttachementRowId) {
		this.seletedDeletedAttachementRowId = seletedDeletedAttachementRowId;
	}

	/*public Quote getSubmittedQuote() {
		return submittedQuote;
	}

	public void setSubmittedQuote(Quote submittedQuote) {
		this.submittedQuote = submittedQuote;
	}*/

	public void setCustomerTypeSearchSelectList(SelectItem[] customerTypeSearchSelectList) {
		this.customerTypeSearchSelectList = customerTypeSearchSelectList;
	}

	public String getNewProspectiveCustomerName1() {
		return newProspectiveCustomerName1;
	}

	public void setNewProspectiveCustomerName1(String newProspectiveCustomerName1) {
		this.newProspectiveCustomerName1 = newProspectiveCustomerName1;
	}

	public String getNewProspectiveCustomerName2() {
		return newProspectiveCustomerName2;
	}

	public void setNewProspectiveCustomerName2(String newProspectiveCustomerName2) {
		this.newProspectiveCustomerName2 = newProspectiveCustomerName2;
	}

	public String getNewProspectiveCustomerCountry() {
		return newProspectiveCustomerCountry;
	}

	public void setNewProspectiveCustomerCountry(String newProspectiveCustomerCountry) {
		this.newProspectiveCustomerCountry = newProspectiveCustomerCountry;
	}

	public String getNewProspectiveCustomerCity() {
		return newProspectiveCustomerCity;
	}

	public void setNewProspectiveCustomerCity(String newProspectiveCustomerCity) {
		this.newProspectiveCustomerCity = newProspectiveCustomerCity;
	}

	public String getNewProspectiveCustomerAddress3() {
		return newProspectiveCustomerAddress3;
	}

	public void setNewProspectiveCustomerAddress3(String newProspectiveCustomerAddress3) {
		this.newProspectiveCustomerAddress3 = newProspectiveCustomerAddress3;
	}

	public String getNewProspectiveCustomerAddress4() {
		return newProspectiveCustomerAddress4;
	}

	public void setNewProspectiveCustomerAddress4(String newProspectiveCustomerAddress4) {
		this.newProspectiveCustomerAddress4 = newProspectiveCustomerAddress4;
	}

	public String getNewProspectiveCustomerSalesOrg() {
		return newProspectiveCustomerSalesOrg;
	}

	public void setNewProspectiveCustomerSalesOrg(String newProspectiveCustomerSalesOrg) {
		this.newProspectiveCustomerSalesOrg = newProspectiveCustomerSalesOrg;
	}

	public String getCustomerNameSearch() {
		return customerNameSearch;
	}

	public void setCustomerNameSearch(String customerNameSearch) {
		this.customerNameSearch = customerNameSearch;
	}

	public SelectItem[] getCountrySelectList() {
		return countrySelectList;
	}

	public void setCountrySelectList(SelectItem[] countrySelectList) {
		this.countrySelectList = countrySelectList;
	}

	public SelectItem[] getCitySelectList() {
		return citySelectList;
	}

	public void setCitySelectList(SelectItem[] citySelectList) {
		this.citySelectList = citySelectList;
	}

	public SelectItem[] getSalesOrgSelectList() {
		return salesOrgSelectList;
	}

	public void setSalesOrgSelectList(SelectItem[] salesOrgSelectList) {
		this.salesOrgSelectList = salesOrgSelectList;
	}

	public int getSearchCustomersCount() {
		return searchCustomersCount;
	}

	public void setSearchCustomersCount(int searchCustomersCount) {
		this.searchCustomersCount = searchCustomersCount;
	}

	public List<Customer> getSearchCustomers() {
		return searchCustomers;
	}

	public void setSearchCustomers(List<Customer> searchCustomers) {
		this.searchCustomers = searchCustomers;
	}

	public List<Customer> getSelectedSearchCustomers() {
		return selectedSearchCustomers;
	}

	public void setSelectedSearchCustomers(List<Customer> selectedSearchCustomers) {
		this.selectedSearchCustomers = selectedSearchCustomers;
	}

	public Customer getSelectedSearchCustomer() {
		return selectedSearchCustomer;
	}

	public void setSelectedSearchCustomer(Customer selectedSearchCustomer) {
		this.selectedSearchCustomer = selectedSearchCustomer;
	}

	public List<String> getStandTermsAndCons() {
		return standTermsAndCons;
	}

	public void setStandTermsAndCons(List<String> standTermsAndCons) {
		this.standTermsAndCons = standTermsAndCons;
	}

	public List<String> getSpecTermsAndCons() {
		return specTermsAndCons;
	}

	public void setSpecTermsAndCons(List<String> specTermsAndCons) {
		this.specTermsAndCons = specTermsAndCons;
	}

	public boolean isDeleteCustomer() {
		return deleteCustomer;
	}

	public void setDeleteCustomer(boolean deleteCustomer) {
		this.deleteCustomer = deleteCustomer;
	}

	public boolean isNewProspectiveCustomerDuplicated() {
		return newProspectiveCustomerDuplicated;
	}

	public void setNewProspectiveCustomerDuplicated(boolean newProspectiveCustomerDuplicated) {
		this.newProspectiveCustomerDuplicated = newProspectiveCustomerDuplicated;
	}

	public String getDuplicatedCustomerCode() {
		return duplicatedCustomerCode;
	}

	public void setDuplicatedCustomerCode(String duplicatedCustomerCode) {
		this.duplicatedCustomerCode = duplicatedCustomerCode;
	}

	public String getDuplicatedCustomerName() {
		return duplicatedCustomerName;
	}

	public void setDuplicatedCustomerName(String duplicatedCustomerName) {
		this.duplicatedCustomerName = duplicatedCustomerName;
	}

	public String getDuplicatedCustomerType() {
		return duplicatedCustomerType;
	}

	public void setDuplicatedCustomerType(String duplicatedCustomerType) {
		this.duplicatedCustomerType = duplicatedCustomerType;
	}

	public String getDuplicatedCountry() {
		return duplicatedCountry;
	}

	public void setDuplicatedCountry(String duplicatedCountry) {
		this.duplicatedCountry = duplicatedCountry;
	}

	public String getDuplicatedCity() {
		return duplicatedCity;
	}

	public void setDuplicatedCity(String duplicatedCity) {
		this.duplicatedCity = duplicatedCity;
	}

	public String getDuplicatedSalesOrg() {
		return duplicatedSalesOrg;
	}

	public void setDuplicatedSalesOrg(String duplicatedSalesOrg) {
		this.duplicatedSalesOrg = duplicatedSalesOrg;
	}

	/*
	 * when edit request qty, target resales , and item remarks will trigger it.
	 * 
	 * @Param: event, the cellEditEvent .
	 */
	public void onCellEdit(CellEditEvent event) {
		LOG.info("call | ProgRfqSubmitMB |  onCellEdit row index : " + event.getRowIndex()
				+ " quote.getQuoteItems() size : " + quote.getQuoteItems().size());
		RequestContext requestContext = RequestContext.getCurrentInstance();
		Object newValue = event.getNewValue();
		Object oldValue = event.getOldValue();
		QuoteItem tempQ = quote.getQuoteItems().get(event.getRowIndex());
		String message = null;
		if (newValue != null && !newValue.equals(oldValue)) {
			this.setNeedSave(true);
			String columnName = event.getColumn().getHeaderText();
			LOG.fine("call | ProgRfqSubmitMB columnName |" + columnName);
			if (EDIT_CELL_HEADER_TEXT_REQ_QTY.equalsIgnoreCase(columnName)) {

				Integer tempInt = tempQ.getRequestedQty();
				if (tempInt != null && tempInt > 0) {
					//remove the logic 
					/*if (tempQ.getMpq() != null && tempInt % tempQ.getMpq() != 0) {
						message = ResourceMB.getText("wq.message.reqQtyError");
						FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, EMPTY_STR, message);
						FacesContext.getCurrentInstance().addMessage("growl_submit_target", msg);
						requestContext.update("growl_submit");
					}*/
				} else {
					message = ResourceMB.getText("wq.message.reqQtyNumrcError");
					FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, EMPTY_STR, message);
					FacesContext.getCurrentInstance().addMessage("growl_submit_target", msg);
					requestContext.update("growl_submit");
				}
				// }
				// else
				// {
				// if(!StringUtils.isNumeric((String)newValue))
				// {
				// FacesMessage msg = new
				// FacesMessage(FacesMessage.SEVERITY_ERROR, "", "Requested Qty
				// is required to be numeric!");
				// FacesContext.getCurrentInstance().addMessage("growl_submit_target",
				// msg);
				// requestContext.update("growl_submit");
				// }
				// else
				// {
				// Integer tempInt = (Integer)newValue;
				// if(tempInt%tempQ.getMpq()!=0)
				// {
				// FacesMessage msg = new
				// FacesMessage(FacesMessage.SEVERITY_ERROR, "", "Requested Qty
				// must meet MPQ!");
				// FacesContext.getCurrentInstance().addMessage("requiredQtyInput",
				// msg);
				// requestContext.update("form_submit:requiredQtyInputMsg");
				// }
				// }
				//
				//
				//
				// }
			} else if (EDIT_CELL_HEADER_TEXT_TARGET_RESALE.equalsIgnoreCase(columnName)) {
				if (!(newValue instanceof Double)) {
					message = ResourceMB.getText("wq.message.reqQtyNumrcError");
					FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, EMPTY_STR, message);
					FacesContext.getCurrentInstance().addMessage("growl_submit_target", msg);
					requestContext.update("growl_submit");
				}
			}

		}
	}

	/*
	 * Get the draft quote object . which will be stored into db.
	 * when set quoteDisplay ==null , this mean no quote in DB
	 */
	public Quote getDrafQuoteForIntoDB(Quote quoteDisplay) {

		LOG.fine("call | getQuoteForIntoDB");

		Quote draftQuote = null;
		try {
			draftQuote = new Quote();
			Date date = new Date();
			//Mandatory fields
			draftQuote.setStage(QuoteSBConstant.QUOTE_STATE_PROG_DRAFT);
			draftQuote.setFormNumber("FM_" + user.getEmployeeId());
			draftQuote.setBizUnit(bizUnit);
			draftQuote.setCreatedBy(user.getEmployeeId());
			draftQuote.setCreatedName(user.getName());
			draftQuote.setCreatedOn(date);
			draftQuote.setLastUpdatedOn(date);
			draftQuote.setLastUpdatedName(user.getName());
			draftQuote.setLastUpdatedBy(user.getEmployeeId());
			draftQuote.setQuoteItems(new ArrayList<QuoteItem>());
			
			if(quoteDisplay !=null) {
				draftQuote.setRemarks(quoteDisplay.getRemarks());
				if (quoteDisplay.getSoldToCustomer() != null) {
					Customer customerT = customerSB.findByPK(quoteDisplay.getSoldToCustomer().getCustomerNumber().trim());
					draftQuote.setSoldToCustomer(customerT);
				}
				if (quoteDisplay.getEndCustomer() != null ) {
					Customer customerT = customerSB.findByPK(quoteDisplay.getEndCustomer().getCustomerNumber());
					draftQuote.setEndCustomer(customerT);
				}
				if (quoteDisplay.getSales() != null) {
					User userT = null;
					if (!QuoteUtil.isEmpty(quoteDisplay.getSales().getEmployeeId())) {
						userT = userSB.findByEmployeeIdLazily(quoteDisplay.getSales().getEmployeeId());
					}
					if (userT != null) {
						draftQuote.setSales(userT);
						draftQuote.setTeam(userT.getTeam());
					}
	
				}
				if (quoteDisplay.getQuoteItems() != null && quoteDisplay.getQuoteItems().size() > 0)
					for (int i = 0; i < quoteDisplay.getQuoteItems().size(); i++) {
						QuoteItem item = quoteDisplay.getQuoteItems().get(i);
						//for use QuoteItem.newInstance(item);
						if(item.getAttachments() ==null) {
							item.setAttachments(new ArrayList<Attachment>());
						}
						if(item.getQuoteResponseTimeHistorys() ==null) {
							item.setQuoteResponseTimeHistorys(new ArrayList<QuoteResponseTimeHistory>());
						}
						QuoteItem newItem = QuoteItem.newInstance(item);
						newItem.setLastUpdatedBy(user.getEmployeeId());
						newItem.setLastUpdatedName(user.getName());
						newItem.setLastUpdatedOn(date);
						item.setStage(QuoteSBConstant.QUOTE_STATE_PROG_DRAFT);
						/*newItem.setPricerId(item.getPricerId());
						newItem.setMinSellPrice(item.getMinSellPrice());
						newItem.setFinalQuotationPrice(item.getFinalQuotationPrice());
						newItem.setSoldToCustomer(item.getSoldToCustomer());
						newItem.setEndCustomer(item.getEndCustomer());
						
						//newItem.getLastUpdatedBy(item);
						// Delinkage change on Oct 29 , 2013 by Tonmy
						// newItem.setRequestedMaterial(item.getRequestedMaterial());
						newItem.setRequestedMfr(item.getRequestedMfr());*/
						newItem.setRequestedPartNumber(item.getRequestedPartNumber());
						newItem.setRequestedMaterialForProgram(item.getQuotedMaterial());
						//newItem.setQuotedMfr(item.getRequestedMfr());
						//newItem.setQuotedPartNumber(item.getRequestedPartNumber());
						newItem.setQuotedMaterial(item.getQuotedMaterial());
						// PROGRM PRICER ENHANCEMENT
						newItem.setCostIndicator(item.getCostIndicator());
						if (item.getRequestedProgramMaterialForProgram() != null
								&& item.getRequestedProgramMaterialForProgram().getCostIndicator() != null)
							newItem.setCostIndicator(
									item.getRequestedProgramMaterialForProgram().getCostIndicator().getName());
	
						//newItem.setMpq(item.getMpq());
						//newItem.setMoq(item.getMoq());
						//newItem.setRequestedQty(item.getRequestedQty());
						//newItem.setTargetResale(item.getTargetResale());
						//newItem.setRemarks(item.getRemarks());
						//newItem.setAttachments(item.getAttachments());
						newItem.setStage(QuoteSBConstant.QUOTE_STATE_PROG_DRAFT);
						newItem.setQuote(draftQuote);
						 
						if (item.getAttachments() == null || item.getAttachments().isEmpty()) {
							newItem.setAttachments(null);
						}
						newItem.setQuote(draftQuote);
						draftQuote.getQuoteItems().add(newItem);
					}
				this.handleQuoItemForInsertToDB(draftQuote.getQuoteItems());
			}
		}
		catch (Exception e) {
			LOG.log(Level.SEVERE,
					"Exception in Quote : " + draftQuote.getId() + " , Exception Message : " + e.getMessage(), e);
			throw e;
		}
		return draftQuote;
	}

	/*
	 * Compare the data
	 */
	/*public boolean compareCurrentPageAndDraft() {
		LOG.fine("call | compareCurrentPageAndDraft");
		Quote quo = getProgDraftRfqFromCurretnPage();
		Quote draftQuote = getSelectedRfqDraftQuote();
		if (quo.equals(draftQuote)) {
			return true;
		}
		return false;
	}*/

	public List<Material> getSelectedRfqDraft() {
		LOG.fine("call | getSelectedRfqDraft");
		List<Material> draftMList = new ArrayList<Material>();

		Quote tempQuote = getSelectedRfqDraftQuote();

		if (tempQuote != null && tempQuote.getQuoteItems() != null && tempQuote.getQuoteItems().size() > 0) {
			List<QuoteItem> progDraftList = tempQuote.getQuoteItems();
			if (null != progDraftList) {
				// transfer quote item to program materials
				for (QuoteItem item : progDraftList) {
					// Delinkage change on Oct 29 , 2013 by Tonmy
					// draftMList.add(item.getRequestedMaterial());
					draftMList.add(programMaterialSB.findExactMaterialByQuoteItem(item, bizUnit));
				}
			}
		}
		return draftMList;
	}

	public Quote getSelectedRfqDraftQuote() {
		LOG.fine("call getSelectedRfqDraftQuote");
		Quote tempQuote = quoteSB.getProgDraftRfq(user, bizUnit);
		// Delinkage change on Oct 30 , 2013 by Tonmy
		if (tempQuote != null && tempQuote.getQuoteItems() != null && tempQuote.getQuoteItems().size() > 0) {
			List<QuoteItem> tempQuoteItem = new ArrayList<QuoteItem>();
			for (QuoteItem qi : tempQuote.getQuoteItems()) {
				qi.setRequestedMaterialForProgram(getMaterial(qi));
				ProgramPricer pm = getProgramMaterialByCostIndicatorAndBizUnit(qi.getRequestedMaterialForProgram(), qi);
				if (pm != null) {
					qi.setRequestedProgramMaterialForProgram(pm);
					tempQuoteItem.add(qi);
				}
			}
			tempQuote.setQuoteItems(tempQuoteItem);
		}
		if (tempQuote != null) {
			if (tempQuote.getSoldToCustomer() == null) {
				Customer cust = new Customer();
				cust.setCustomerNumber(EMPTY_STR);
				cust.setCustomerName1(EMPTY_STR);
				tempQuote.setSoldToCustomer(cust);
			}
			if (tempQuote.getSales() == null) {
				User u = new User();
				u.setEmployeeId(EMPTY_STR);
				u.setName(EMPTY_STR);
				tempQuote.setSales(u);
				// WebQuote 2.2 enhancement. 6.1.1 part 2.
				Team tempTeam = new Team();
				tempTeam.setName(EMPTY_STR);
				tempQuote.setTeam(tempTeam);
			}

		}

		return tempQuote;
	}

	/*
	 * remove program rfq draft
	 */
	/*
	 * public void removeProgRfqDraft() { quoteSB.removeProgDraftRfq(user,
	 * bizUnit); }
	 */

	protected void copyQuote() {

		quoteInitialCopy = new Quote();
		// Mandatory fields
		if (quote.getSoldToCustomer() != null) {
			quoteInitialCopy.setSoldToCustomer(quote.getSoldToCustomer());
			Customer customerT = customerSB.findByPK(quote.getSoldToCustomer().getCustomerNumber());
			if (customerT == null) {
				Customer cust = new Customer();
				cust.setCustomerNumber(EMPTY_STR);
				cust.setCustomerName1(EMPTY_STR);
				quoteInitialCopy.setSoldToCustomer(cust);
			} else {
				quoteInitialCopy.setSoldToCustomer(customerT);
			}
		}

		if (quote.getSales() != null) {
			quoteInitialCopy.setSales(quote.getSales());
			// WebQuote 2.2 enhancement. 6.1.1 part 2.
			quoteInitialCopy.setTeam(quote.getTeam());
		}

		if (quote.getRemarks() != null)
			quoteInitialCopy.setRemarks(quote.getRemarks());
		quoteInitialCopy.setQuoteItems(new ArrayList<QuoteItem>());
		if (quote.getQuoteItems() != null && quote.getQuoteItems().size() > 0)
			for (int i = 0; i < quote.getQuoteItems().size(); i++) {
				QuoteItem item = quote.getQuoteItems().get(i);
				QuoteItem newItem = new QuoteItem();

				// Delinkage change on Oct 29 , 2013 by Tonmy
				// newItem.setRequestedMaterial(item.getRequestedMaterial());
				newItem.setRequestedMfr(item.getRequestedMfr());
				newItem.setRequestedPartNumber(item.getRequestedPartNumber());
				newItem.setRequestedMaterialForProgram(item.getRequestedMaterialForProgram());
				// PROGRM PRICER ENHANCEMENT
				newItem.setRequestedProgramMaterialForProgram(item.getRequestedProgramMaterialForProgram());
				newItem.setQuotedMfr(item.getRequestedMfr());
				newItem.setQuotedPartNumber(item.getRequestedPartNumber());
				newItem.setQuotedMaterial(item.getRequestedMaterialForProgram());

				newItem.setMpq(item.getMpq());
				newItem.setMoq(item.getMoq());
				newItem.setRequestedQty(item.getRequestedQty());
				newItem.setTargetResale(item.getTargetResale());
				newItem.setRemarks(item.getRemarks());
				newItem.setAttachments(item.getAttachments());
				newItem.setQuote(quoteInitialCopy);
				if (newItem.getAttachments() == null)
					item.setAttachments(new ArrayList<Attachment>());
				quoteInitialCopy.getQuoteItems().add(newItem);
			}
	}

	protected void restoredQuote() {

		quote = new Quote();
		// Mandatory fields
		if (quoteInitialCopy.getSoldToCustomer() != null) {
			quote.setSoldToCustomer(quoteInitialCopy.getSoldToCustomer());
			Customer customerT = customerSB.findByPK(quoteInitialCopy.getSoldToCustomer().getCustomerNumber());
			if (customerT == null) {
				Customer cust = new Customer();
				cust.setCustomerNumber(EMPTY_STR);
				cust.setCustomerName1(EMPTY_STR);
				quote.setSoldToCustomer(cust);
			} else {
				quote.setSoldToCustomer(customerT);
			}
		}

		if (quoteInitialCopy.getSales() != null) {
			quote.setSales(quoteInitialCopy.getSales());
			// WebQuote 2.2 enhancement. 6.1.1 part 2.
			quote.setTeam(quoteInitialCopy.getTeam());
		}
		if (quoteInitialCopy.getRemarks() != null)
			quote.setRemarks(quoteInitialCopy.getRemarks());
		quote.setQuoteItems(new ArrayList<QuoteItem>());
		if (quoteInitialCopy.getQuoteItems() != null && quoteInitialCopy.getQuoteItems().size() > 0)
			for (int i = 0; i < quoteInitialCopy.getQuoteItems().size(); i++) {
				QuoteItem item = quoteInitialCopy.getQuoteItems().get(i);
				QuoteItem newItem = new QuoteItem();

				// Delinkage change on Oct 29 , 2013 by Tonmy
				// newItem.setRequestedMaterial(item.getRequestedMaterial());
				newItem.setRequestedMfr(item.getRequestedMfr());
				newItem.setRequestedPartNumber(item.getRequestedPartNumber());
				newItem.setRequestedMaterialForProgram(item.getRequestedMaterialForProgram());
				newItem.setQuotedMfr(item.getRequestedMfr());
				newItem.setQuotedPartNumber(item.getRequestedPartNumber());
				newItem.setQuotedMaterial(item.getRequestedMaterialForProgram());

				newItem.setMpq(item.getMpq());
				newItem.setMoq(item.getMoq());
				newItem.setRequestedQty(item.getRequestedQty());
				newItem.setTargetResale(item.getTargetResale());
				newItem.setRemarks(item.getRemarks());
				newItem.setAttachments(item.getAttachments());
				newItem.setQuote(quote);
				if (newItem.getAttachments() == null)
					item.setAttachments(new ArrayList<Attachment>());
				quote.getQuoteItems().add(newItem);
			}

	}

	/*******************************************************
	 * 
	 * Below are actions , which will be call by UI directly .
	 * 
	 * *****************************************************
	 */

	/*
	 * Quote the selected item.
	 */
	public void quoteNowAction(ActionEvent event) {
		LOG.fine("call |  quoteNowAction");
		ProgramPricer selectedProgramMaterial = (ProgramPricer) event.getComponent().getAttributes()
				.get("selectedProgramMaterial");
		QuoteItem qi = new QuoteItem();
		qi.setMpq(selectedProgramMaterial.getMpq());
		qi.setMoq(selectedProgramMaterial.getMoq());
		// Delinkage change on Oct 29 , 2013 by Tonmy
		// qi.setRequestedMaterial(selectedProgramMaterial.getMaterial());
		qi.setRequestedMfr(selectedProgramMaterial.getMaterial().getManufacturer());
		qi.setRequestedPartNumber(selectedProgramMaterial.getMaterial().getFullMfrPartNumber());
		qi.setRequestedMaterialForProgram(getMaterial(qi));
		qi.setRequestedProgramMaterialForProgram(selectedProgramMaterial);
		qi.setQuotedMfr(selectedProgramMaterial.getMaterial().getManufacturer());
		qi.setQuotedPartNumber(selectedProgramMaterial.getMaterial().getFullMfrPartNumber());
		qi.setQuotedMaterial(getMaterial(qi));

		quote = initialQuote();
		qi.setQuote(quote);
		quote.getQuoteItems().add(qi);

		copyQuote();

		submitType = 1;
		geneExChangeCurrencyLst(); // added by June for RMB project 20140707
		RequestContext.getCurrentInstance().execute("PF('submitRfqDialog').show()");

	}

	private void geneExChangeCurrencyLst() {
		// added by June for project RMB cur to initilise exchange currency pull
		// downmenu
		List<String> currencyLst = exChangeRateSB.findAllExCurrencyByBu(this.bizUnit.getName());
		exCurrencyList = QuoteUtil.createFilterOptions(currencyLst.toArray(new String[currencyLst.size()]),
				currencyLst.toArray(new String[currencyLst.size()]), false, false);

		ExcCurrencyDefault defaultCurrency = exChangeRateSB.findDefaultCurrencyByBu(bizUnit.getName());
		
		if (null != defaultCurrency && !QuoteUtil.isEmpty(defaultCurrency.getCurrency()))
			rfqCurr = defaultCurrency.getCurrency();
		initRfqcurr(this.quote);

	}


	/*
	 * When user click on the delete button on the rfq item . it will trigger
	 * this method.
	 * 
	 * @param: itemNumber. it is program material id.
	 */
	public void removeRfqItemAction() {
		LOG.fine("call | removeRfqItem itemNumber");
		// List<QuoteItem> quoteItems = quote.getQuoteItems();
		// List<QuoteItem> removeList = new ArrayList<QuoteItem>();
		// removeList.add(selectedQuoteItem);
		// quoteItems.removeAll(removeList);
		try {
			if(this.getSelectedRowIndex() !=null) {
				/****
				 * remove(int) remove(Object) , remove(Integer) use remove(Object),
				 * so need cast by self to use remove(int) maybe can change to removeAt(int)
				 */
				quote.getQuoteItems().remove((int)this.getSelectedRowIndex());
				this.setNeedSave(true);
			}
		} catch (Exception ex) {

		}

		// quote.setQuoteItems(quoteItems);
		// quote.getQuoteItems().removeAll(selectedQuoteItem);
	}

	public void updateAttachmentAction(ActionEvent event) {
		LOG.fine("call | updateAttachment");
		int rowIndex = (int) event.getComponent().getAttributes().get("selectedPm");
		itemNumberForAttachment = rowIndex;
		List<QuoteItem> quoteItems = quote.getQuoteItems();
		attachments = quoteItems.get(itemNumberForAttachment).getAttachments();
	}

	public void updateDownloadAttachment(int rowIndex) throws WebQuoteException {
		Attachment attachment = attachments.get(rowIndex);
		byte[] bytes = attachment.getFileImage();
		if (bytes == null || !(bytes.length > 0)) {
			// modified by Yang,Lenon for read attachments from host
			// server(2015.12.31)
			String fileRootPath = sysConfigSB.getProperyValue(QuoteSBConstant.ATTACHMENT_ROOT_PATH);
			String realFilePath = fileRootPath + File.separator + attachment.getFilePath() + File.separator
					+ attachment.getFileNameActual();
			bytes = FileUtil.file2Byte(realFilePath);
		}
		// stream = new ByteArrayInputStream(bytes);
		String mimeType = DownloadUtil.getMimeType(attachment.getFileName());
		downloadAttachment = new DefaultStreamedContent(new ByteArrayInputStream(bytes), mimeType,
				attachment.getFileName());
	}

	/*
	 * update attachments
	 */
	public void uploadAttachmentAction(FileUploadEvent event) {
		LOG.fine("call | uploadAttachment");
		String message = null;
		try {
			// Check file size. Can't excess 2 M
			long fileSize = event.getFile().getSize();
			if (fileSize > 5 * 1024 * 1024) {
				message = ResourceMB.getText("wq.message.uplodAttchmntError");
				FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, EMPTY_STR, message);
				FacesContext.getCurrentInstance().addMessage("messages_att", msg);
			} else {
				Attachment attachment = new Attachment();
				String fullFileName = event.getFile().getFileName();
				String fileName = fullFileName.substring(fullFileName.lastIndexOf("\\") + 1, fullFileName.length());
				attachment.setFileName(fileName);
				attachment.setFileImage(QuoteUtil.getUploadFileContent(event.getFile()));
				attachment.setType("RFQ Item Attachment");
				List<QuoteItem> quoteItems = quote.getQuoteItems();
				QuoteItem item = quoteItems.get(this.itemNumberForAttachment);
				List<Attachment> attachmentsa = item.getAttachments();
				if (attachmentsa == null)
					attachmentsa = new ArrayList<Attachment>();
				attachmentsa.add(attachment);
				attachment.setQuoteItem(item);
				item.setAttachments(attachmentsa);
				this.setNeedSave(true);
				//quote.setQuoteItems(quoteItems);
				message = ResourceMB.getText("wq.message.uploadMsg");
				FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_INFO, EMPTY_STR,
						event.getFile().getFileName() + message);
				FacesContext.getCurrentInstance().addMessage("growl_submit_target", msg);
			}

		} catch (Exception e) {
			LOG.log(Level.SEVERE,
					"Error occured while uploading attcahment with file name: " + event.getFile().getFileName()
							+ ", Reason for failure: " + MessageFormatorUtil.getParameterizedStringFromException(e),
					e);
			message = ResourceMB.getText("wq.exception.uploadAttchFail");
			FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, EMPTY_STR, message);
			FacesContext.getCurrentInstance().addMessage("growl_submit_target", msg);
		}
	}

	public void resetAction() {
		LOG.fine("call | resetAction");
		this.initialQuote();
		/*restoredQuote();
		final String message = ResourceMB.getText("wq.message.resetSuccess");
		FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_INFO, EMPTY_STR, message);
		FacesContext.getCurrentInstance().addMessage("growl_submit_target", msg);*/
	}

	/*public void closeAction() {
		LOG.fine("call |  closeAction");
		RequestContext requestContext = RequestContext.getCurrentInstance();
		FacesMessage msg = null;
		// check whether have draft
		Quote draftQuote = getSelectedRfqDraftQuote();
		if (draftQuote == null) {
			requestContext.execute("PF('closeConfirmation').show()");
		} else {
			if (compareCurrentPageAndDraft()) {
				requestContext.execute("PF('submitRfqDialog').hide()");
				FacesContext ctx = FacesContext.getCurrentInstance();
				
				 * Object value =
				 * ctx.getApplication().getELResolver().getValue(ctx.
				 * getELContext(), null, "commodityMB"); CommodityMB tempObj =
				 * ((CommodityMB) value);
				 
				this.updateSystemInfoForBizUnitChange();
			} else {
				requestContext.execute("PF('closeConfirmation').show()");
			}
		}

	}*/
 
	public void saveAsCartAction() {
		LOG.fine("call | saveAsDraftAction");
		if (quote != null) {
			String message = null;
			List<QuoteItem> tempQis = quote.getQuoteItems();
			for (int i = 0; i < tempQis.size(); i++) {
				quote.getQuoteItems().get(i).setError(false);
			}
			try {
				Quote quo = getDrafQuoteForIntoDB(this.quote);
				List<QuoteItem> tempItems = quo.getQuoteItems();
				
				for (QuoteItem item : tempItems) {
					item.setAction(ActionEnum.PROGRAM_RFQ_SUBMISSION_SAVE.name());
					item.setRfqCurr(QuoteUtil.isEmpty(this.getRfqCurr())? null:Currency.getByCurrencyName(this.getRfqCurr()));
				
				}
				
				quoteSB.saveProgDraftQuote(quo, user, bizUnit);
				this.updateSystemInfoForBizUnitChange();
				//if(!(notShowMessage !=null && notShowMessage.length>0 && notShowMessage[0])) {
					message = ResourceMB.getText("wq.message.savedDraftSuccess");
					FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_INFO, EMPTY_STR, message);
					FacesContext.getCurrentInstance().addMessage("growl_submit_target", msg);
				//}

			} catch (Exception e) {
				LOG.log(Level.SEVERE, "Error occured while saving as draft, Reason for failure: "
						+ MessageFormatorUtil.getParameterizedStringFromException(e), e);
				message = ResourceMB.getText("wq.message.errorSaveDraft");
				handleErrorMsg(message);
			}
			//this.updateSystemInfoForBizUnitChange();
		}
	}

	public void saveAsCartActionForClose() {

		RequestContext requestContext = RequestContext.getCurrentInstance();
		saveAsCartAction();
		requestContext.execute("PF('submitRfqDialog').hide()");

	}

	private void handleQuoItemForDisplay(List<QuoteItem> quoteItems) {
		for (QuoteItem quoteItem : quoteItems) {
			if (quoteItem.getSoldToCustomer() == null) {
				quoteItem.setSoldToCustomer(getEmptyCustomer());
				//form DB shold deep copy for avoid reference error when edit in page;
			}else if(!QuoteUtil.isEmpty(quoteItem.getSoldToCustomer().getCustomerName1())){
				try {
					quoteItem.setSoldToCustomer((Customer)quoteItem.getSoldToCustomer().deepClone());
				} catch (Exception e) {
					e.printStackTrace();
				} 
			}
			// for end customer
			// quoteItem.setEndCustomer(item.getEndCustomer());
			if (quoteItem.getEndCustomer() == null) {
				quoteItem.setEndCustomer(getEmptyCustomer());
			}else if(!QuoteUtil.isEmpty(quoteItem.getEndCustomer().getCustomerName1())){
				try {
					quoteItem.setEndCustomer((Customer)quoteItem.getEndCustomer().deepClone());
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}

	private void handleQuoItemForInsertToDB(List<QuoteItem> quoteItems) {
		for (QuoteItem quoteItem : quoteItems) {
			if (quoteItem.getSoldToCustomer() != null
					&& !QuoteUtil.isEmpty(quoteItem.getSoldToCustomer().getCustomerNumber())) {
				Customer customerT = getCustomerInfomationByCode(quoteItem.getSoldToCustomer().getCustomerNumber().trim());
				quoteItem.setSoldToCustomer(customerT);
			} else {
				quoteItem.setSoldToCustomer(null);
			}
			if (quoteItem.getEndCustomer() != null
					&& !QuoteUtil.isEmpty(quoteItem.getEndCustomer().getCustomerNumber())) {
				String soldToNum = (quoteItem.getSoldToCustomer()==null || 
						quoteItem.getSoldToCustomer().getCustomerNumber()==null) ? 
						null:quoteItem.getSoldToCustomer().getCustomerNumber().trim();
				Customer customerT =this.getEndCustomer(quoteItem.getEndCustomer().getCustomerNumber().trim(), soldToNum);
				quoteItem.setEndCustomer(customerT);
			} else {
				quoteItem.setEndCustomer(null);
			}
			
			//quoteItem.setBuyCurr(Currency.USD);
			//quoteItem.setRfqCurr(QuoteUtil.isEmpty(this.getRfqCurr())? null:Currency.getByCurrencyName(this.getRfqCurr()));
		}
	}

	private void handleQuoteForInsertToDB(Quote quote) {
		if (quote.getSoldToCustomer() != null && !QuoteUtil.isEmpty(quote.getSoldToCustomer().getCustomerNumber())) {
			Customer customerT = customerSB.findByPK(quote.getSoldToCustomer().getCustomerNumber().trim());
			quote.setSoldToCustomer(customerT);
		} else {
			quote.setSoldToCustomer(null);
		}

		if (quote.getEndCustomer() != null && !QuoteUtil.isEmpty(quote.getEndCustomer().getCustomerNumber())) {
			Customer customerT = customerSB.findByPK(quote.getEndCustomer().getCustomerNumber().trim());
			quote.setEndCustomer(customerT);
		} else {
			quote.setEndCustomer(null);
		}
	}

	public void submitAction() {

		LOG.fine("call | submitAction quote: ");
		//RequestContext requestContext = RequestContext.getCurrentInstance();
		boolean isCheckedSale = false;
		boolean isContinue = true;

		if (!this.isSalesRole()) {
			isCheckedSale = true;
		}
		LOG.info("copy the page quote begin ");
		Quote quoteCopyFromPage = Quote.newInstance(this.quote);
		// for quoteItem
		for(QuoteItem qItem :this.quote.getQuoteItems()) {
			QuoteItem qItemCopy = QuoteItem.newInstance(qItem);
			quoteCopyFromPage.addItem(qItemCopy);
			qItemCopy.setQuote(quoteCopyFromPage);
		}
		LOG.info("copy the page quote end ");
		//quoteCopyFromPage.setAttachments(this.quote.getAttachments());
		this.handleQuoteForInsertToDB(quoteCopyFromPage);
		this.handleQuoItemForInsertToDB(quoteCopyFromPage.getQuoteItems());
		ValidateError error = ValidateHelper.validate(quoteCopyFromPage, isCheckedSale);
		if (error.isHasError()) {
			Map<Long, Integer> issueRows = error.getIssueRows();
			List<QuoteItem> tempQis = quoteCopyFromPage.getQuoteItems();
			for (int i = 0; i < tempQis.size(); i++) {
				QuoteItem tempQi = tempQis.get(i);
				if (issueRows.get(new Long(tempQi.getPricerId())) != null) {
					quoteCopyFromPage.getQuoteItems().get(i).setError(true);
				} else {
					quoteCopyFromPage.getQuoteItems().get(i).setError(false);
				}
			}
			isContinue = false;
			FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, EMPTY_STR, error.getErroeMsg());
			FacesContext.getCurrentInstance().addMessage("messages_submit_target", msg);
		} else {
			if (isCheckedSale) {
				User userTemp = userSB.findByEmployeeIdLazily(quoteCopyFromPage.getSales().getEmployeeId());
				if (userTemp == null) {
					isContinue = false;
					FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR,
							ResourceMB.getText("wq.message.invalidSalesmanError"),
							ResourceMB.getText("wq.message.invalidSalesNumError") + quoteCopyFromPage.getSales().getEmployeeId());
					FacesContext.getCurrentInstance().addMessage("messages_submit_target", msg);
				} else if (!QuoteUtil.isEmpty(quoteCopyFromPage.getSales().getName())
						&& !quoteCopyFromPage.getSales().getName().equals(userTemp.getName())) {
					isContinue = false;
					FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR,
							ResourceMB.getText("wq.message.invalidSalesmanError"),
							ResourceMB.getText("wq.message.salesmanNumbrError") + quoteCopyFromPage.getSales().getName()
									+ ResourceMB.getText("wq.message.salesmanNotMatchError"));
					FacesContext.getCurrentInstance().addMessage("messages_submit_target", msg);
				}
			}

			if (isContinue) {

				List<QuoteItem> tempQis = quoteCopyFromPage.getQuoteItems();
				for (int i = 0; i < tempQis.size(); i++) {
					quoteCopyFromPage.getQuoteItems().get(i).setError(false);
				}
				Quote submitedQuote = null;
				try {

					// ut.begin();
					submitedQuote = quoteCopyFromPage;
					// it is CS. will fill up the copyToCS.
					if (salesmanListForCsPanelDisplay) {
						// changed on 18 Apr 2014.
						submitedQuote.setCopyToCS(user.getEmployeeId() + ";");
						submitedQuote.setCopyToCsName(user.getName() + ";");
					}
					if (this.isSalesRole()) {
						submitedQuote.setSales(user);
					}
					// Default value
					submitedQuote.setBizUnit(bizUnit);
					submitedQuote.setCreatedBy(user.getEmployeeId());
					submitedQuote.setCreatedName(user.getName());
					submitedQuote.setCreatedOn(DateUtils.getCurrentAsiaDateObj());
					submitedQuote.setSubmissionDate(DateUtils.getCurrentAsiaDateObj());
					submitedQuote.setLastUpdatedOn(DateUtils.getCurrentAsiaDateObj());
					submitedQuote.setLastUpdatedBy(user.getEmployeeId());
					submitedQuote.setLastUpdatedName(user.getName());
					if (submitedQuote.getSoldToCustomer() != null) {
						submitedQuote.setSoldToCustomerName(getCustomerFullName(submitedQuote.getSoldToCustomer()));
						submitedQuote.setSoldToCustomerNameChinese(
								retrieveCustomerChineseName(submitedQuote.getSoldToCustomer()));
					}
					if (submitedQuote.getEndCustomer() != null) {
						submitedQuote.setEndCustomerName(getCustomerFullName(submitedQuote.getEndCustomer()));
					}
					// AQ LOGIC
					materialSB.applyAqLogic(submitedQuote.getQuoteItems(),
							RFQSubmissionTypeEnum.ShoppingCartSubmission);
					
					restrictedCustomerSB.validateRestrictedCustomerWithQuoteItems(submitedQuote.getQuoteItems(),FacesContext.getCurrentInstance().getViewRoot().getLocale(), bizUnit);
					
					// group by soldTo
					Map<String, List<QuoteItem>> mapGroupBySoldToCode = new HashMap<String, List<QuoteItem>>();
					for (QuoteItem item : submitedQuote.getQuoteItems()) {
						if (mapGroupBySoldToCode.containsKey(item.getSoldToCustomer().getCustomerNumber())) {
							mapGroupBySoldToCode.get(item.getSoldToCustomer().getCustomerNumber()).add(item);
						} else {
							List<QuoteItem> list = new ArrayList<QuoteItem>();
							list.add(item);
							mapGroupBySoldToCode.put(item.getSoldToCustomer().getCustomerNumber(), list);
						}
					}
					List<Quote> aqQuoteList = new ArrayList<Quote>();
					try {
						ut.begin();
						/*** find aq submit and save quotes **/
						quoteSB.removeProgDraftRfq(user, bizUnit);

						for (Entry<String, List<QuoteItem>> entry : mapGroupBySoldToCode.entrySet()) {
							Quote needSubmitQuote = Quote.newInstance(submitedQuote);
							boolean hasAq = false;
							needSubmitQuote.setStage(null);
							needSubmitQuote.setFormNumber(null);
							//needSubmitQuote.set(new Date());
							needSubmitQuote.setQuoteItems(entry.getValue());
							// reset soldto from the first grouped item
							needSubmitQuote.setSoldToCustomer(entry.getValue().get(0).getSoldToCustomer());
							//Fixed for defect101 by Damonchen@20181207
							needSubmitQuote.setSoldToCustomerName(getCustomerFullName(entry.getValue().get(0).getSoldToCustomer()));
							needSubmitQuote.setSoldToCustomerNameChinese(
									retrieveCustomerChineseName(entry.getValue().get(0).getSoldToCustomer()));
							for (QuoteItem item : entry.getValue()) {
								item.setQuote(needSubmitQuote);
								
								Date date = new Date();
								item.setLastUpdatedBy(user.getEmployeeId());
								item.setLastUpdatedOn(date);
								item.setLastUpdatedName(user.getName());
								item.setSubmissionDate(date);
								item.setAction(ActionEnum.PROGRAM_RFQ_SUBMISSION_SUBMIT.name()); 
								
								
								//LOG.warning("wad" + String.valueOf(item.getId()));
								if (!hasAq && QuoteSBConstant.QUOTE_STAGE_FINISH.equals(item.getStage())
										&& QuoteSBConstant.RFQ_STATUS_AQ.equals(item.getStatus())) {
									hasAq = true;
								}
							}
							quoteSB.submitProgramRfq(needSubmitQuote);
							if (hasAq) {
								aqQuoteList.add(needSubmitQuote);
							}
						}
						ut.commit();
						//Create SAP QUOTE FOR every has AQ'S quote.
						for (Quote aqQuote : aqQuoteList) {
	    					try{
	    						LOG.info("createQuoteToSoForAQ(): Create SAP QUOTE for AQ");
	    						createQuoteToSoForAQ(aqQuote.getQuoteItems(), bizUnit);
	    						LOG.info("createQuoteToSoForAQ(): End Create SAP QUOTE for AQ");
	    					}
	 	    				catch(Exception e){
	 	    					e.printStackTrace();
	 	    				}
						}
						// requestContext.execute("PF('submitRfqDialog').hide()");
						// TODO
						submitResultInfo = new SubmitResultInfo();
						for (QuoteItem item : submitedQuote.getQuoteItems()) {
							if (QuoteSBConstant.QUOTE_STAGE_FINISH.equals(item.getStage())
									&& QuoteSBConstant.RFQ_STATUS_AQ.equals(item.getStatus())) {
								submitResultInfo.setAsAq(submitResultInfo.getAsAq() + 1);
							} else if (QuoteSBConstant.QUOTE_STAGE_DRAFT.equals(item.getStage())) {
								submitResultInfo.setAsDraft(submitResultInfo.getAsDraft() + 1);
							} else if (QuoteSBConstant.QUOTE_STAGE_PENDING.equals(item.getStage())) {
								submitResultInfo.setAsPending(submitResultInfo.getAsPending() + 1);
							}
						}
						submitResultInfo.setTotal(submitedQuote.getQuoteItems().size());
						this.resultQuote = submitedQuote;
						this.updateSystemInfoForBizUnitChange();
						
						//Many place need to call updateSystemInfoForBizUnitChange,so refresh rfq curr again when submition is done
						if (null != bizUnit.getDefaultCurrency()){
							this.setRfqCurr(bizUnit.getDefaultCurrency().name());
						}
						
						FacesUtil.redirect("WebPromo/ShoppingCartSubmitResult.jsf");
						try {
							
							for (Quote submitQuote : aqQuoteList) {
								LOG.info("submitAction(): Begin sending email for AQ");
								QuotationEmailVO vo = new QuotationEmailVO();
								vo.setFormNumber(submitQuote.getFormNumber());
								vo.setQuoteId(submitQuote.getId());
								/*vo.setHssfWorkbook(
										getQuoteTemplateBySoldTo(submitQuote.getSoldToCustomer(), submitQuote));*/
								vo.setQuote(submitQuote);
								vo.setSubmissionDateFromQuote(true);
								vo.setSoldToCustomer(submitQuote.getSoldToCustomer());
								vo.setRecipient(submitQuote.getSales().getName());
								String regionCodeName = bizUnit.getName();
								vo.setSender(sysMaintSB.getEmailSignName(regionCodeName) + "<br/>"
										+ sysMaintSB.getEmailHotLine(regionCodeName) + "<br/>Email Box: "
										+ sysMaintSB.getEmailSignContent(regionCodeName) + "<br/>");
								vo.setFromEmail(sysMaintSB.getEmailAddress(regionCodeName));
								String fullCustomerName = submitQuote.getSoldToCustomer().getCustomerFullName();
								vo = ejbCommonSB.commonLogicForDpRFQandRFQ(submitQuote, vo,
										submitQuote.getSoldToCustomer(), user, fullCustomerName);
								///ejbCommonSB.sendQuotationEmail(vo);
								/*SendQuotationEmailStrategy strategy = (SendQuotationEmailStrategy) cacheUtil.getStrategy(SendQuotationEmailStrategy.class,
										bizUnit.getName());*/
								
								SendQuotationEmailStrategy strategy =(SendQuotationEmailStrategy) StrategyFactory.getSingletonFactory()
										.getStrategy(SendQuotationEmailStrategy.class,bizUnit.getName(),
												this.getClass().getClassLoader());
								
								strategy.sendQuotationEmail(vo);
								LOG.info("submitAction(): end sending email for AQ");
							}
							
						} catch (Exception ex) {
							 LOG.log(Level.SEVERE, "Exception in send email for submitting ShoppingCart \n "
							 		+ "exception message : " + ex.getMessage(), ex);
							 throw ex;
						}
						

					} catch (Exception ex) {
						if(ut.getStatus() != Status.STATUS_NO_TRANSACTION) {
							LOG.log(Level.SEVERE,
									"Exception in save submit ShoppingCart and begin rollback , exception message : "
											+ ex.getMessage(),
									ex);
							ut.rollback();
						}
						throw ex;
					}

				} catch (WebQuoteException we) {
					this.showMessage(FacesMessage.SEVERITY_ERROR, we.getMessage());
				} catch (Exception ex) {
					LOG.log(Level.SEVERE, "Exception in submit ShoppingCart , exception message : " + ex.getMessage(), ex);
					this.showMessage(FacesMessage.SEVERITY_ERROR, ResourceMB.getText("wq.message.errorMsgSubmit"));
				}
			}
		}
	}

	/*
	 * Result page. download the quotation.
	 */
	/*public StreamedContent getFile() throws WebQuoteException {
		LOG.fine("call | DownloadAction");

		StreamedContent file = null;
		File xlsFile = null;
		FileOutputStream fileOut = null;
		FileInputStream fileIn = null;
		try {
			HSSFWorkbook hw = getQuoteTemplateBySoldTo(resultQuote, tempSTList);
			String tempPath = sysConfigSB.getProperyValue(QuoteConstant.TEMP_DIR);
			String fileName = tempPath + "Quotation.xls";
			xlsFile = new File(fileName);
			fileOut = new FileOutputStream(xlsFile);
			hw.write(fileOut);
			fileIn = new FileInputStream(xlsFile);
			file = new DefaultStreamedContent(fileIn, "application/vnd.ms-excel", fileName);

		} catch (IOException ex) {
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
					ResourceMB.getText("wq.message.downloadfail") + ".", ""));
			throw new WebQuoteException(ex);

		}

		finally {

			if (fileOut != null)
				fileOut = null;
			if (fileIn != null)
				fileIn = null;
			if (xlsFile != null) {
				if (xlsFile.exists())
					xlsFile.delete();
			}
		}

		return file;
	}
*/
	/*
	 * For Cusotmer part
	 */

	public void createProspectiveCustomerWithForcing() {
		try {
			createProspectiveCustomer("Y");
		} catch (CheckedException e) {
			LOG.log(Level.WARNING, MessageFormatorUtil.getParameterizedStringFromException(e));
			handleErrorMsg(e.getMessage());
		}
	}

	public void createProspectiveCustomerWithoutForcing() {
		try {

			boolean isAllowInput = true;
			String allowFieldName = "";

			if (!QuoteUtil.isEmpty(this.newProspectiveCustomerAddress3)) {
				char[] dataArray = this.newProspectiveCustomerAddress3.toCharArray();
				for (char ss : dataArray) {
					if (!QuoteConstant.ALLOW_CHARACTER.contains(String.valueOf(ss))) {
						isAllowInput = false;
						allowFieldName = ResourceMB.getText("wq.label.address");
						break;
					}
				}
			}

			if (!QuoteUtil.isEmpty(this.newProspectiveCustomerCity)) {
				char[] dataArray = this.newProspectiveCustomerCity.toCharArray();
				for (char ss : dataArray) {
					if (!QuoteConstant.ALLOW_CHARACTER.contains(String.valueOf(ss))) {
						isAllowInput = false;
						allowFieldName = ResourceMB.getText("wq.label.city");
						break;
					}
				}
			}

			if (!QuoteUtil.isEmpty(this.newProspectiveCustomerName1)) {
				char[] dataArray = this.newProspectiveCustomerName1.toCharArray();
				for (char ss : dataArray) {
					if (!QuoteConstant.ALLOW_CHARACTER.contains(String.valueOf(ss))) {
						isAllowInput = false;
						break;
					}
				}
			}
			String message = null;
			if (isAllowInput) {

				String errorMsg = "";

				errorMsg = ejbCommonSB.validationMessage(message, errorMsg, newProspectiveCustomerName1,
						newProspectiveCustomerCountry, newProspectiveCustomerCity, newProspectiveCustomerSalesOrg);

				LOG.log(Level.INFO, "this.newProspectiveCustomerName1 = " + this.newProspectiveCustomerName1);
				LOG.log(Level.INFO, "this.newProspectiveCustomerCountry = " + this.newProspectiveCustomerCountry);
				LOG.log(Level.INFO, "this.newProspectiveCustomerCity = " + this.newProspectiveCustomerCity);
				LOG.log(Level.INFO, "this.newProspectiveCustomerSalesOrg = " + this.newProspectiveCustomerSalesOrg);
				LOG.log(Level.INFO, "errorMsg = " + errorMsg);

				if (this.newProspectiveCustomerAddress3 != null && this.newProspectiveCustomerAddress3.length() > 35) {
					message = ResourceMB.getText("wq.message.addrLenError");
					FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "", message);
					FacesContext.getCurrentInstance().addMessage("newProspectiveCustomerGrowl", msg);
				} else {
					if (errorMsg.length() > 0) {
						message = ResourceMB.getText("wq.message.mandFieldError");
						FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, message, errorMsg);
						FacesContext.getCurrentInstance().addMessage("newProspectiveCustomerGrowl", msg);
					} else {
						newProspectiveCustomerDuplicated = false;
						createProspectiveCustomer(null);
						RequestContext requestContext = RequestContext.getCurrentInstance();
						requestContext.execute("PF('customer_confirmation_dialog').show()");
					}

				}
			} else {
				message = ResourceMB.getParameterizedString("wq.message.fieldValidtnError", allowFieldName);
				FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "", message);
				FacesContext.getCurrentInstance().addMessage("newProspectiveCustomerGrowl", msg);
			}
		} catch (CheckedException e) {
			LOG.log(Level.SEVERE,
					"Error occured while creating new customer with name: " + this.newProspectiveCustomerName1 + ", "
							+ "city: " + this.newProspectiveCustomerCity + ", country: "
							+ this.newProspectiveCustomerCountry + ", " + "Sales org: "
							+ this.newProspectiveCustomerSalesOrg + ", Reason for failure: "
							+ MessageFormatorUtil.getParameterizedStringFromException(e),
					e);
			handleErrorMsg(e.getMessage());
		}
	}

	public void changeCityByCountry() {
		//Bryan Start
		//Country country = CountryCacheManager.getCountry(newProspectiveCustomerCountry);
		Country country = cacheUtil.getCountry(newProspectiveCustomerCountry);
		//Bryan End
		List<String> cityCodes = new ArrayList<String>();
		List<String> cityNames = new ArrayList<String>();
		if (country != null) {
			List<City> cities = citySB.findByCountry(country); // CityCacheManager.getCitiesByCountry(newProspectiveCustomerCountry);
			if (cities != null) {
				for (City city : cities) {
					cityCodes.add(city.getName());
					cityNames.add(city.getId());
				}
			}
		}
		citySelectList = QuoteUtil.createFilterOptions(cityCodes.toArray(new String[cityCodes.size()]),
				cityNames.toArray(new String[cityNames.size()]), false, false, true);
	}

	public void updateSoldToCustomerNumber() {
		quote.getSoldToCustomer().setCustomerNumber(
				extractCustomerNumberFromCustomerNameLabel(quote.getSoldToCustomer().getCustomerName1()));
		quote.getSoldToCustomer().setCustomerName1(
				extractCustomerNameFromCustomerNameLabel(quote.getSoldToCustomer().getCustomerName1()));
		Customer customerT = customerSB.findByPK(quote.getSoldToCustomer().getCustomerNumber());
		quote.setSoldToCustomer(customerT);

		if (customerT.getDeleteFlag().booleanValue())
			deleteCustomer = true;
		else
			deleteCustomer = false;

	}

	public String extractCustomerNameFromCustomerNumberLabel(String label) {
		String[] labels = label.split(QuoteConstant.AUTOCOMPLETE_SEPARATOR);
		if (labels.length == 1)
			return null;
		return labels[1].trim();
	}

	public String extractCustomerNumberFromCustomerNumberLabel(String label) {
		if(label==null) {
			return "";
		}
		String[] labels = label.split(QuoteConstant.AUTOCOMPLETE_SEPARATOR);
		return labels[0].trim();
	}

	public String extractCustomerNumberFromCustomerNameLabel(String label) {
		String[] labels = label.split(QuoteConstant.AUTOCOMPLETE_SEPARATOR);
		if (labels.length == 1)
			return null;
		return labels[1].trim();
	}

	public String extractCustomerNameFromCustomerNameLabel(String label) {
		String[] labels = label.split(QuoteConstant.AUTOCOMPLETE_SEPARATOR);
		return labels[0].trim();
	}

	public void updateSoldToCustomerName_old() {
		// quote.getSoldToCustomer().setCustomerName1(extractCustomerNameFromCustomerNumberLabel(quote.getSoldToCustomer().getCustomerNumber()));
		if (!StringUtils.isEmpty(quote.getSoldToCustomer().getCustomerNumber())) {
			quote.getSoldToCustomer().setCustomerNumber(
					extractCustomerNumberFromCustomerNumberLabel(quote.getSoldToCustomer().getCustomerNumber()));
			Customer customerT = customerSB.findByPK(quote.getSoldToCustomer().getCustomerNumber());
			if (customerT != null) {
				quote.setSoldToCustomer(customerT);
				if (customerT.getDeleteFlag() != null && customerT.getDeleteFlag().booleanValue())
					deleteCustomer = true;
				else
					deleteCustomer = false;
			} else {
				Customer newCu = new Customer();
				newCu.setCustomerNumber(quote.getSoldToCustomer().getCustomerNumber());
				newCu.setCustomerName1("");
				quote.setSoldToCustomer(newCu);
			}
		} else {
			Customer newCu = new Customer();
			newCu.setCustomerNumber(quote.getSoldToCustomer().getCustomerNumber());
			newCu.setCustomerName1("");
			quote.setSoldToCustomer(newCu);
		}

	}

	public void updateSoldToCustomerName() {
		
		this.setNeedSave(true);
		Customer newCu = new Customer();
		if (this.quote.getSoldToCustomer() != null) {
			newCu.setCustomerNumber(quote.getSoldToCustomer().getCustomerNumber());
		} else {
			newCu.setCustomerNumber(EMPTY_STR);
		}
		newCu.setCustomerName1(EMPTY_STR);
		if (this.salesBizUnit == null) {
			this.quote.setSoldToCustomer(newCu);
			this.showMessage(FacesMessage.SEVERITY_WARN, ResourceMB.getText("wq.message.salesmanEmpty"));
			return;
		}
		if (this.quote.getSoldToCustomer() != null && this.quote.getSoldToCustomer().getCustomerNumber() != null) {
			String custNumber = this.quote.getSoldToCustomer().getCustomerNumber();
			//Customer cust = this.quote.getSoldToCustomer();
			List<String> accountGroups = new ArrayList<String>();
			accountGroups.add(QuoteSBConstant.ACCOUNT_GROUP_SOLDTO);
			accountGroups.add(QuoteSBConstant.ACCOUNT_GROUP_PROSPECTIVE_CUSTOMER);
			accountGroups.add(QuoteSBConstant.ACCOUNT_GROUP_CPD);
			List<Customer> customers = customerSB.findCustomerByCustomerNumber(custNumber, null, accountGroups,
					this.salesBizUnit, 0, 100);
			boolean pickTheCustomer = false;
			Customer customer = null;
			if (customers != null && customers.size() > 0) {
				pickTheCustomer = true;
				customer = customers.get(0);
			}

			if (pickTheCustomer) {
				boolean isInvalidCustomer = customerSB.isInvalidCustomer(custNumber);
				if (isInvalidCustomer) {
					LOG.info("Invalid Special Customer : " + custNumber);
					FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_WARN,
							ResourceMB.getText("wq.label.invalidCust") + " :",
							ResourceMB.getText("wq.label.soldToCodeCustomer") + " : " + custNumber);
					FacesContext.getCurrentInstance().addMessage("messages_submit", msg);

				} else {

					if (customer.getDeleteFlag() != null && customer.getDeleteFlag().booleanValue()) {
						deleteCustomer = true;
						FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_WARN,
								ResourceMB.getText("wq.message.deltdCust") + " :",
								ResourceMB.getText("wq.label.soldToCodeCustomer") + " : "
										+ getCustomerFullName(customer) + "(" + custNumber + ")");
						FacesContext.getCurrentInstance().addMessage("messages_submit", msg);
						// RequestContext requestContext =
						// RequestContext.getCurrentInstance();
						// requestContext.update("form_rfqSubmission:RfqSubmissionGrowl");
						return;
					} else {
						deleteCustomer = false;
						this.quote.setSoldToCustomer(customer);
					}
				}
			} else {
				this.quote.setSoldToCustomer(newCu);
				LOG.info("Invalid Customer : " + custNumber);
				FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_WARN,
						ResourceMB.getText("wq.label.invalidCust") + " :",
						ResourceMB.getText("wq.label.soldToCodeCustomer") + " : " + custNumber);
				FacesContext.getCurrentInstance().addMessage("messages_submit", msg);
			}

		}
	}

	public void updateEndCustomerName() {
		//LOG.info("updateEndCustomerName() before " + this.quote.getEndCustomer().getCustomerNumber());
		this.quote.getEndCustomer().setCustomerNumber(
				extractCustomerNumberFromCustomerNumberLabel(this.quote.getEndCustomer().getCustomerNumber()));
		//LOG.info("updateEndCustomerName()  " + this.quote.getEndCustomer().getCustomerNumber());
		this.setNeedSave(true);
		Customer newCu = this.getEmptyCustomer();
		if(quote.getEndCustomer() !=null) {
			newCu.setCustomerNumber(quote.getEndCustomer().getCustomerNumber());
		}
		//replace the old endcust
		if (this.salesBizUnit == null) {
			this.quote.setEndCustomer(newCu);
			FacesUtil.showWarnMessage(ResourceMB.getText("wq.message.salesmanEmpty"), "growl_submit");
			return;
		}
		if (this.quote.getEndCustomer() != null && this.quote.getEndCustomer().getCustomerNumber() != null) {
			String custNumber = this.quote.getEndCustomer().getCustomerNumber().trim();
			//Customer cust = this.quote.getEndCustomer();
			String soldToNumber = "";
			if (this.quote.getSoldToCustomer() != null) {
				soldToNumber = this.quote.getSoldToCustomer().getCustomerNumber();
				if(soldToNumber !=null) {
					soldToNumber = soldToNumber.trim();
				}
			}
			Customer endCust = getEndCustomer(custNumber, soldToNumber);
			if (endCust == null) {
				quote.setEndCustomer(newCu);
			} else {
				quote.setEndCustomer(endCust);
			}
			if (custNumber.length() > 0 && endCust == null) {
				FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_WARN,
						ResourceMB.getText("wq.label.invalidCust") + " :",
						ResourceMB.getText("wq.label.endCust") + " : " + custNumber);
				FacesContext.getCurrentInstance().addMessage("messages_submit", msg);
			}

		}

	}

	private Customer getEndCustomer(String endCustNumber, String soldToNumber) {
		List<String> accountGroup = new ArrayList<String>();
		accountGroup.add(QuoteSBConstant.ACCOUNT_GROUP_ENDCUSTOMER);
		List<Customer> customers = customerSB.findEndCustomerExact(endCustNumber, soldToNumber, accountGroup,
				this.salesBizUnit, QuoteConstant.DEFAULT_AUTOCOMPLETE_FIRST_RESULT,
				QuoteConstant.DEFAULT_AUTOCOMPLETE_MAX_RESULTS);

		removeSpecialCustomer(customers);
		if (customers.isEmpty()) {
			return null;
		} else {
			return customers.get(0);
		}
	}
	
	//no exact search with endnUMBER
	private List<Customer> getEndCustomers(String endCustNumber, String soldToNumber) {
		List<String> accountGroup = new ArrayList<String>();
		accountGroup.add(QuoteSBConstant.ACCOUNT_GROUP_ENDCUSTOMER);
		List<Customer> customers = customerSB.findEndCustomer(endCustNumber, soldToNumber, accountGroup,
				this.salesBizUnit, QuoteConstant.DEFAULT_AUTOCOMPLETE_FIRST_RESULT,
				QuoteConstant.DEFAULT_AUTOCOMPLETE_MAX_RESULTS);

		removeSpecialCustomer(customers);
		return customers;
	}

	public void updateSoldToCustomerNameInProgRfqItemList(int rowIndex) {

		// String errMsg = "";
		QuoteItem item = null;
		try {
			item = this.quote.getQuoteItems().get(rowIndex);
		} catch (Exception ex) {
			this.showMessage(FacesMessage.SEVERITY_ERROR,
					ResourceMB.getParameterizedText("wq.message.noFoundQuoteItems", String.valueOf(rowIndex + 1)));
			return;
		}
		if (item != null) {
			String custNum = item.getSoldToCustomer().getCustomerNumber().trim();
			if (!QuoteUtil.isEmpty(custNum)) {
				Customer cust = this.getCustomerInfomationByCode(custNum);
				if (cust == null) {
					item.setSoldToCustomer(this.getEmptyCustomer());
					item.getSoldToCustomer().setCustomerNumber(custNum);
					this.handleErrorMsg(ResourceMB.getParameterizedText("wq.error.soldToCodeNofound",
							String.valueOf(rowIndex + 1)));
				} else {
					item.setSoldToCustomer(cust);
				}
			} else {
				item.setSoldToCustomer(this.getEmptyCustomer());
				this.handleErrorMsg(
						ResourceMB.getParameterizedText("wq.error.soldToCodeNofound", String.valueOf(rowIndex + 1)));

			}
			this.setNeedSave(true);
			quoteSB.applyDefaultCostIndicatorLogic(item, true);
		}
	}

	public void updateEndCustomerNameInProgRfqItemList(int rowIndex) {

		// String errMsg = "";
		QuoteItem item = null;
		try {
			item = this.quote.getQuoteItems().get(rowIndex);
		} catch (Exception ex) {
			this.showMessage(FacesMessage.SEVERITY_ERROR,
					ResourceMB.getParameterizedText("wq.message.noFoundQuoteItems", String.valueOf(rowIndex + 1)));
			return;
		}
		if (item != null) {
			item.getEndCustomer().setCustomerNumber(
					extractCustomerNumberFromCustomerNumberLabel(item.getEndCustomer().getCustomerNumber()));
			String custNum = item.getEndCustomer().getCustomerNumber();
			if (!QuoteUtil.isEmpty(custNum)) {
				Customer cust = this.getEndCustomer(custNum.trim(), item.getSoldToCustomer().getCustomerNumber().trim());
				if (cust == null) {
					item.setEndCustomer(this.getEmptyCustomer());
					item.getEndCustomer().setCustomerNumber(custNum);
					this.handleErrorMsg(
							ResourceMB.getParameterizedText("wq.error.endToCodeNofound", String.valueOf(rowIndex + 1)));
				} else {
					item.setEndCustomer(cust);
				}
			} else {
				item.setEndCustomer(this.getEmptyCustomer());
			}
			this.setNeedSave(true);
			quoteSB.applyDefaultCostIndicatorLogic(item, true);
			//FacesUtil.updateUI(":growl_submit",":form_submit:submissionTable");
		}
	}

	public Customer getCustomerInfomationByCode(String custNumber) {
		// Customer customer = customerSB.findByPK(rfqItem
		// .getSoldToCustomerNumber());
		List<String> accountGroups = new ArrayList<String>();
		accountGroups.add(QuoteSBConstant.ACCOUNT_GROUP_SOLDTO);
		accountGroups.add(QuoteSBConstant.ACCOUNT_GROUP_PROSPECTIVE_CUSTOMER);
		accountGroups.add(QuoteSBConstant.ACCOUNT_GROUP_CPD);
		List<Customer> customers = customerSB.findCustomerByCustomerNumber(custNumber, null, accountGroups,
				this.getSalesBizUnit(), 0, 100);
		Customer customer = null;
		if (customers != null && customers.size() > 0) {
			customer = customers.get(0);
		}
		return customer;

	}

	private void removeSpecialCustomer(List<Customer> customers) {
		if (customers != null && customers.size() > 0) {
			for (Iterator<Customer> itr = customers.iterator(); itr.hasNext();) {
				Customer customer = itr.next();
				if (customerSB.isInvalidCustomer(customer.getCustomerNumber())) {
					itr.remove();
				}
			}
		}
	}

	public void validateSoldToCustomerName() {
		List<String> accountGroup = new ArrayList<String>();
		accountGroup.add(QuoteSBConstant.ACCOUNT_GROUP_SOLDTO);
		List<Customer> customers = customerSB.findCustomerByCustomerName(quote.getSoldToCustomer().getCustomerName1(),
				null, accountGroup, salesBizUnit);
		if (QuoteUtil.isEmptyCustomerList(customers)) {
			quote.getSoldToCustomer().setCustomerName1(null);
		}
	}

	private void clearCustomerSearchResult() {
		searchCustomers = new ArrayList<Customer>();
		customerNameSearch = null;
	}
	public void searchCustomersAction() {

		// Handle search key word < 3
		if (customerNameSearch != null && customerNameSearch.length() < 3) {
			this.showMessage(FacesMessage.SEVERITY_WARN, ResourceMB.getText("wq.message.cutmrLenError"));
			return;
		}
		searchCustomers = findCustomers(customerTypeSearch, customerNameSearch);
		if (searchCustomers != null) {
			List<String> customerNumbers = new ArrayList<String>();
			for (Iterator<Customer> itr = searchCustomers.iterator(); itr.hasNext();) {
				Customer customer = itr.next();
				if (customerNumbers.contains(customer.getCustomerNumber())) {
					itr.remove();
				} else {
					customerNumbers.add(customer.getCustomerNumber());
				}
			}
			searchCustomersCount = searchCustomers.size();
		}
	}

	
	/*
	 * CUSTOMER SOURCE CODE
	 */
	// =================================================================================================
	public void createProspectiveCustomer(String duplicate) throws CheckedException {
		javax.xml.ws.Holder<ZwqCustomer> eCustdtl = new javax.xml.ws.Holder<ZwqCustomer>();
		javax.xml.ws.Holder<java.lang.String> eKunnr = new javax.xml.ws.Holder<java.lang.String>();
		try {

			String address = "";
			if (newProspectiveCustomerAddress3 != null)
				address += newProspectiveCustomerAddress3;

			newProspectiveCustomerName2 = "";
			if (newProspectiveCustomerName1 != null
					&& newProspectiveCustomerName1.length() > QuoteConstant.CUSTOMER_NAME_LENGTH) {
				newProspectiveCustomerName2 = newProspectiveCustomerName1.substring(QuoteConstant.CUSTOMER_NAME_LENGTH);
				newProspectiveCustomerName1 = newProspectiveCustomerName1.substring(0,
						QuoteConstant.CUSTOMER_NAME_LENGTH);
			}

			sapWebServiceSB.createProspectiveCustomer(QuoteSBConstant.CREATE_CUSTOMER_TYPE_CUSTOMER,
					newProspectiveCustomerCity, duplicate, newProspectiveCustomerCountry, newProspectiveCustomerName1,
					newProspectiveCustomerName2, // name2
					"", // Postal code
					"", // Region
					"", // Search term
					"", // Division
					address, // Street
					"", // Sales office
					newProspectiveCustomerSalesOrg, // Sales Org
					"", "USD", // currency
					eCustdtl, eKunnr);
			if (eKunnr.value.equals("")) {
				newProspectiveCustomerDuplicated = true;
				ZwqCustomer existingCustomer = eCustdtl.value;

				duplicatedCustomerCode = existingCustomer.getKunnr();
				duplicatedCustomerName = existingCustomer.getName();
				duplicatedCustomerType = ejbCommonSB.existingCustomer(duplicatedCustomerCode, duplicatedCustomerName,
						duplicatedCustomerType, existingCustomer);

				duplicatedSalesOrg = existingCustomer.getVkorg();
				duplicatedCountry = existingCustomer.getLand();
				if (duplicatedCountry != null && !"".equalsIgnoreCase(duplicatedCountry)) {
					//Bryan Start
					//Country tempC = CountryCacheManager.getCountry(duplicatedCountry);
					Country tempC = cacheUtil.getCountry(duplicatedCountry);
					//Bryan End
					if (tempC != null)
						duplicatedCountry = tempC.getName();
				}
				duplicatedCity = existingCustomer.getCity();
				if (!QuoteUtil.isEmpty(duplicatedCity)) {
					String[] cities = duplicatedCity.split(",");
					duplicatedCity = "";
					for (String city : cities) {
						City cityObj = citySB.findByPK(city.trim());
						if (cityObj != null) {
							duplicatedCity += cityObj.getName() + ",";
						} else {
							duplicatedCity += city.trim() + ",";
						}
					}
					if (duplicatedCity.length() > 0)
						duplicatedCity = duplicatedCity.substring(0, duplicatedCity.length() - 1);
				}
			} else {
				newProspectiveCustomerDuplicated = false;
				duplicatedCustomerCode = eKunnr.value.replaceFirst("^0+(?!$)", "");
				Customer customerTemp = new Customer();
				customerTemp.setCustomerNumber(duplicatedCustomerCode);
				customerTemp.setNewCustomerFlag(true);
				customerTemp.setCreatedOn(QuoteUtil.getCurrentTime());
				if (newProspectiveCustomerType.equalsIgnoreCase(QuoteSBConstant.CREATE_CUSTOMER_TYPE_CUSTOMER)) {
					customerTemp.setAccountGroup(QuoteSBConstant.ACCOUNT_GROUP_PROSPECTIVE_CUSTOMER);
				} else {
					customerTemp.setAccountGroup(QuoteSBConstant.ACCOUNT_GROUP_ENDCUSTOMER);
				}
				customerTemp.setDeleteFlag(false);
				String customerName = "";
				if (newProspectiveCustomerName1 != null) {
					customerName += newProspectiveCustomerName1;
					customerTemp.setCustomerName1(newProspectiveCustomerName1);
				}
				if (newProspectiveCustomerName2 != null) {
					customerName += newProspectiveCustomerName2;
					customerTemp.setCustomerName2(newProspectiveCustomerName2);
				}

				CustomerSale customerSale = new CustomerSale();
				//Bryan Start
				//List<SalesOrg> salesOrgs = SalesOrgCacheManager.getSalesOrgByBizUnit(salesBizUnit.getName());
				List<SalesOrg> salesOrgs = cacheUtil.getSalesOrgByBizUnit(salesBizUnit.getName());
				//Bryan End
				for (SalesOrg salesOrg : salesOrgs) {
					if (salesOrg.getName().equals(newProspectiveCustomerSalesOrg))
						customerSale.setSalesOrgBean(salesOrg);
				}
				customerSale.setCreatedOn(QuoteUtil.getCurrentTime());
				customerSale.setLastUpdatedOn(QuoteUtil.getCurrentTime());

				CustomerSalePK customerSalePK = new CustomerSalePK();
				customerSalePK.setCustomerNumber(duplicatedCustomerCode);
				customerSalePK.setDistributionChannel(QuoteSBConstant.DEFAULT_DISTRIBUTION_CHANNEL);
				customerSalePK.setDivision(QuoteSBConstant.DEFAULT_DIVISION);
				customerSalePK.setSalesOrg(newProspectiveCustomerSalesOrg);
				customerSale.setId(customerSalePK);

				List<CustomerSale> customerSales = new ArrayList<CustomerSale>();
				customerSales.add(customerSale);

				customerTemp.setCustomerSales(customerSales);
				for (int m = 0; m < customerSales.size(); m++) {
					customerSales.get(m).setCustomer(customerTemp);
				}

				customerSB.createCustomer(customerTemp);
			}
		} catch (AppException e) {
			throw new CheckedException(ResourceMB.getText("wq.exception.createNewCutmrFail"));

		}
	}

	public void updateCustomer() {
		{
			LOG.fine("call | updateCustomer");
			if (this.selectedSearchCustomer != null) {
				if (SOLD_TO_CUST.equals(this.selectForWhichCustomer)) {
					quote.setSoldToCustomer(selectedSearchCustomer);
				} else if (END_CUST.equals(this.selectForWhichCustomer)) {
					quote.setEndCustomer(selectedSearchCustomer);
				}
				this.setNeedSave(true);
				// not sure this, so note by damon@20160607
				/*
				 * String customerName =
				 * this.selectedSearchCustomer.getCustomerName1();
				 * if(this.selectedSearchCustomer.getCustomerName2()!=null) {
				 * customerName =
				 * customerName+this.selectedSearchCustomer.getCustomerName2();
				 * }
				 */
				// String customerName =
				// this.selectedSearchCustomer.getCustomerFullName();
				// quote.getSoldToCustomer().setCustomerName1(customerName);
			}
			resetCustomerSearch();
		}
	}

	public void resetCustomerSearch() {
		searchCustomersCount = 0;
		searchCustomers = null;
		customerTypeSearch = QuoteSBConstant.ALL;
		customerNameSearch = null;
	}

	public String convertCustomerSalesToSalesOrg(List<CustomerSale> customerSales) {
		String salesOrg = "";
		if (customerSales != null) {
			for (CustomerSale customerSale : customerSales) {
				if (salesOrg.length() > 0)
					salesOrg += QuoteConstant.SALES_ORG_SEPARATER;
				salesOrg += customerSale.getSalesOrgBean().getName();
			}
		}
		return salesOrg;
	}

	public String convertAccountGroupToName(String accountGroup) {
		//Bryan Start
		//return AccountGroupCacheManager.getAccountGroup(accountGroup);
		return cacheUtil.getAccountGroup(accountGroup);
		//Bryan End
	}

	public List<String> autoCompleteCustomerNameLabelList(List<Customer> customers) {
		List<String> list = null;
		if (customers != null) {
			list = new ArrayList<String>();
			List<String> dupList = new ArrayList<String>();
			for (Customer customer : customers) {
				if (customer != null) {
					if (!QuoteUtil.isEmpty(customer.getCustomerName1())
							&& !QuoteUtil.isEmpty(customer.getCustomerNumber())) {
						String customerName = getCustomerFullName(customer);
						String customerNumber = customer.getCustomerNumber();
						String salesOrg = convertCustomerSalesToSalesOrg(customer.getCustomerSales());
						String label = customerName + QuoteConstant.AUTOCOMPLETE_SEPARATOR + customerNumber
								+ QuoteConstant.AUTOCOMPLETE_SEPARATOR + salesOrg;

						if (!dupList.contains(label)) {
							list.add(label);
							dupList.add(label);
						}
					}
				}
			}
		}
		return list;
	}

	public List<String> autoCompleteCustomerNumberLabelList(String key) {
		List<String> accountGroup = new ArrayList<String>();
		accountGroup.add(QuoteSBConstant.ACCOUNT_GROUP_SOLDTO);
		List<Customer> customers = customerSB.findCustomerByCustomerNumberContain(key, null, accountGroup, salesBizUnit,
				QuoteConstant.DEFAULT_AUTOCOMPLETE_FIRST_RESULT, QuoteConstant.DEFAULT_AUTOCOMPLETE_MAX_RESULTS);
		List<String> list = new ArrayList<String>();
		List<String> dupList = new ArrayList<String>();
		for (Customer customer : customers) {
			String label = "";
			label += customer.getCustomerNumber();
			label += QuoteConstant.AUTOCOMPLETE_SEPARATOR;
			label += getCustomerFullName(customer);
			label += QuoteConstant.AUTOCOMPLETE_SEPARATOR;
			label += convertCustomerSalesToSalesOrg(customer.getCustomerSales());
			;
			if (!dupList.contains(label)) {
				list.add(label);
				dupList.add(label);
			}
		}
		return list;
	}

	public List<String> autoCompleteSoldToCustomerNameLabelList(String key) {
		List<String> accountGroup = new ArrayList<String>();
		accountGroup.add(QuoteSBConstant.ACCOUNT_GROUP_SOLDTO);
		List<Customer> customers = customerSB.findCustomerByCustomerNameContain(key, null, accountGroup, salesBizUnit,
				QuoteConstant.DEFAULT_AUTOCOMPLETE_FIRST_RESULT, QuoteConstant.DEFAULT_AUTOCOMPLETE_MAX_RESULTS);
		return autoCompleteCustomerNameLabelList(customers);
	}

	public List<Customer> findCustomers(String customerType, String customerName) {
		String message = null;

		if (customerName != null && !customerName.isEmpty() && customerName.length() < 3) {
			message = ResourceMB.getText("wq.message.cutmrLenError");
			FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, message, "");
			FacesContext.getCurrentInstance().addMessage(null, msg);
			return null;
		}

		if (QuoteUtil.isEmpty(customerName)) {
			String errorMessage = "";
			if (QuoteUtil.isEmpty(customerName))
				message = ResourceMB.getText("wq.message.cutmrLenError");
			errorMessage += message + ", ";
			message = ResourceMB.getText("wq.message.mandFieldError");
			errorMessage = errorMessage.substring(0, errorMessage.length() - 2);
			FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, message, errorMessage);
			FacesContext.getCurrentInstance().addMessage("message_customer_search_mandatory_is_empty", msg);
			return null;
		}

		List<String> accountGroup = new ArrayList<String>();
		if(SOLD_TO_CUST.equals(this.selectForWhichCustomer)) {
			accountGroup.add(QuoteSBConstant.ACCOUNT_GROUP_SOLDTO);
			accountGroup.add(QuoteSBConstant.ACCOUNT_GROUP_PROSPECTIVE_CUSTOMER);
		} else if(END_CUST.equals(this.selectForWhichCustomer)) {
			accountGroup.add(QuoteSBConstant.ACCOUNT_GROUP_ENDCUSTOMER);
		}
		

		if (customerName.startsWith("*"))
			return customerSB.findCustomerByCustomerNameEndWith(customerName.replaceAll("\\*", ""), QuoteSBConstant.ALL,
					accountGroup, salesBizUnit);
		else if (customerName.endsWith("*"))
			return customerSB.findCustomerByCustomerNameStartWith(customerName.replaceAll("\\*", ""),
					QuoteSBConstant.ALL, accountGroup, salesBizUnit);
		else
			return customerSB.findCustomerByCustomerNameContain(customerName.replaceAll("\\*", ""), QuoteSBConstant.ALL,
					accountGroup, salesBizUnit);
	}

	public Quote joinTargetListAndDraft(ProgramPricer[] selectedProgramMaterials,
			HashMap<Long, ProgramPricer> targetList, Quote draftQuote) {

		LOG.fine("call joinTargetListAndDraft");
		Quote returnQuote = initialQuote();
		Set<Long> tempSet = new HashSet<Long>();

		if (draftQuote != null) {
			returnQuote = getProgramDraftQuoteCopy(draftQuote);

			List<QuoteItem> tempQuoteItemList = returnQuote.getQuoteItems();
			if (null != tempQuoteItemList && tempQuoteItemList.size() > 0) {
				for (QuoteItem item : tempQuoteItemList) {
					// Delinkage change on Oct 29 , 2013 by Tonmy
					// tempSet.add(new
					// Long(item.getRequestedMaterial().getProgramMaterial().getId()));
					// tempSet.add(new
					// Long(item.getRequestedMaterialForProgram().getProgramMaterial().getId()));
					// PROGRM PRICER ENHANCEMENT
					tempSet.add(new Long(item.getRequestedProgramMaterialForProgram().getId()));
				}
			}
		}

		if (selectedProgramMaterials.length > 0) {

			if (targetList == null) {
				targetList = new HashMap<Long, ProgramPricer>();
			}
			for (int i = 0; i < selectedProgramMaterials.length; i++) {
				ProgramPricer item = selectedProgramMaterials[i];
				ProgramPricer obj = (ProgramPricer) targetList.get(new Long(item.getId()));
				if (obj == null) {
					targetList.put(new Long(item.getId()), item);
				}

			}

		}

		if (targetList != null && targetList.size() > 0) {
			for (Long key : targetList.keySet()) {
				if (tempSet.size() > 0) {
					if (!tempSet.contains(new Long(targetList.get(key).getId()))) {
						returnQuote = commonJoinDraftList(returnQuote, targetList, key);
					}
				} else {
					returnQuote = commonJoinDraftList(returnQuote, targetList, key);
				}
			}
		}

		return returnQuote;
	}

	/*
	 * private void deepCopydRaftQuoteInDB(Quote returnQuote, Quote quoteInDB) {
	 * if(quoteInDB }
	 */
	private Quote commonJoinDraftList(Quote returnQuote, HashMap<Long, ProgramPricer> targetList, Long key) {
		ProgramPricer tempPm = targetList.get(key);
		QuoteItem newQ = new QuoteItem();
		// Delinkage change on Oct 29 , 2013 by Tonmy
		// newQ.setRequestedMaterial(tempPm.getMaterial());
		newQ.setRequestedMfr(tempPm.getMaterial().getManufacturer());
		newQ.setRequestedPartNumber(tempPm.getMaterial().getFullMfrPartNumber());
		newQ.setRequestedMaterialForProgram(getMaterial(newQ));
		// PROGRM PRICER ENHANCEMENT
		newQ.setRequestedProgramMaterialForProgram(tempPm);
		if (tempPm.getCostIndicator() != null) {
			newQ.setCostIndicator(tempPm.getCostIndicator().getName());
		}

		newQ.setQuotedMfr(tempPm.getMaterial().getManufacturer());
		newQ.setQuotedPartNumber(tempPm.getMaterial().getFullMfrPartNumber());
		newQ.setQuotedMaterial(getMaterial(newQ));

		newQ.setMoq(tempPm.getMoq());
		newQ.setMpq(tempPm.getMpq());
		newQ.setQuote(returnQuote);
		returnQuote.getQuoteItems().add(newQ);
		return returnQuote;
	}

	public void updateSalesman() {
		LOG.info("call updateSalesman");
		User user1 = null;
		String salesNumber = quote.getSales().getEmployeeId();
		if (!EMPTY_STR.equalsIgnoreCase(salesNumber)) {
			user1 = userSB.findByEmployeeIdWithAllRelation(salesNumber);
		}

		if (user1 != null) {
			quote.setSales(user1);
			// WebQuote 2.2 enhancement. 6.1.1 part 2.
			quote.setTeam(user1.getTeam());
			// if(this.salesBizUnit !=null )
			this.salesBizUnit = user1.getDefaultBizUnit();
			
		} else {
			User u = new User();
			u.setEmployeeId(EMPTY_STR);
			u.setName(EMPTY_STR);
			quote.setSales(u);
			// WebQuote 2.2 enhancement. 6.1.1 part 2.
			Team tempTeam = new Team();
			tempTeam.setName(EMPTY_STR);
			quote.setTeam(tempTeam);
		}
		updateSystemInfoForSalesChange();
	}

	public void resultCloseAction() {
		LOG.fine("call resultCancelAction");
		RequestContext requestContext = RequestContext.getCurrentInstance();
		requestContext.execute("PF('submitRfqResultDialog').hide()");
		FacesContext ctx = FacesContext.getCurrentInstance();
		/*
		 * Object value =
		 * ctx.getApplication().getELResolver().getValue(ctx.getELContext(),
		 * null, "commodityMB"); CommodityMB tempObj = ((CommodityMB) value);
		 */
		this.updateSystemInfoForBizUnitChange();
	}

	/**** upload excel for step1 *****/
	public void handleFileUpload() throws IOException {

		ShoppingCartUploadStrategy uploadStrategy = new ShoppingCartUploadStrategy();
		uploadStrategy.setUser(user);
		boolean isUploadFile = uploadStrategy.isValidateUpload(uploadFile);
		if (!isUploadFile) {
			this.showMessage(FacesMessage.SEVERITY_ERROR, ResourceMB.getText("wq.message.pleaseSelectFile"));
			return;
		}

		this.setUploadFileName(uploadFile.getFileName());

		long start = System.currentTimeMillis();

		boolean isExcelFile = uploadStrategy.isExcelFile(uploadFile);
		if (!isExcelFile) {
			this.showMessage(FacesMessage.SEVERITY_ERROR, ResourceMB.getText("wq.message.selExcelFile"));
			return;
		}
		// uploadStrategy.isValidateFileColumn(currentUploadSheet, index,
		// templateName, columnOfLength)
		boolean isRequiredFile = uploadStrategy.isValidateFileColumn(uploadFile);
		if (!isRequiredFile) {
			this.showMessage(FacesMessage.SEVERITY_ERROR, ResourceMB.getText("wq.message.excelFileFormat"));
			return;
		}
		Excel20007Reader reader = new Excel20007Reader(uploadFile, 0, new ShoppingCartProcessSheet());
		int countRows = reader.getCountrows();
		LOG.info("catalog search upload user : " + user.getName() + " upload size: " + countRows + " upload file :"
				+ uploadFile.getFileName());
		boolean isExceedAllowMaxNum = uploadStrategy.isExceedAllowMaxNum(countRows,
				ShoppingCartConstant.NUMBER_OF_ALLOW_MAX_UPLOAD + ShoppingCartConstant.TEMPLATE_HEADER_ROW_NUMBER);
		if (isExceedAllowMaxNum) {
			String errMsg = ResourceMB.getText("wq.message.maximum") + " "
					+ ShoppingCartConstant.NUMBER_OF_ALLOW_MAX_UPLOAD + ResourceMB.getText("wq.message.recordsAllowed")
					+ countRows;
			this.showMessage(FacesMessage.SEVERITY_ERROR, errMsg);
			return;
		} else {
			if (countRows == ShoppingCartConstant.TEMPLATE_HEADER_ROW_NUMBER) {
				this.showMessage(FacesMessage.SEVERITY_ERROR, ResourceMB.getText("wq.message.noDataUploadFile"));
				return;
			}
		}

		@SuppressWarnings("unchecked")
		List<ShoppingCartLoadItemBean> beans = reader.excel2Beans(null);

		if (beans == null || beans.size() == 0) {
			this.showMessage(FacesMessage.SEVERITY_ERROR, ResourceMB.getText("wq.message.noDataUploadFile"));
			return;
		}
		String errMsg = uploadStrategy.verifyFields(beans);
		if (PricerUploadHelper.isHaveErrorMsg(errMsg)) {
			this.showMessage(FacesMessage.SEVERITY_ERROR, errMsg);
			return;
		}
		// and also set the entity to bean when exist
		errMsg = this.verifyDataInDB(beans);
		if (PricerUploadHelper.isHaveErrorMsg(errMsg)) {
			this.showMessage(FacesMessage.SEVERITY_ERROR, errMsg);
			return;
		}
		long end = System.currentTimeMillis();
		LOG.info("upload end,takes " + (end - start) + " ms proceed record size " + beans.size());
		// this.setCatalogSearchItems(beans);
		// TODO
		if (this.quote == null) {
			this.quote = this.initialQuote();
		}
		List<QuoteItem> qItemInExcels = new ArrayList<QuoteItem>();
		for (ShoppingCartLoadItemBean bean : beans) {
			try {
				qItemInExcels.add(ConvertShopCartItemToQuoteItem(bean, quote, user));
			} catch (Exception ex) {
				LOG.info("ConvertShopCartItemToQuoteItem " + ex.getMessage() + ex.getStackTrace());
				this.showMessage(FacesMessage.SEVERITY_ERROR, ex.getMessage());
			}
		}
		this.quote.getQuoteItems().clear();
		this.quote.getQuoteItems().addAll(qItemInExcels);
		this.setNeedSave(true);
		reset();
	}

	// step 1
	// TODO
	public ShoppingCartLoadItemBean ConvertQuoteItemToShopCartItem(QuoteItem quoteItem) {
		ShoppingCartLoadItemBean shopCartItem = new ShoppingCartLoadItemBean();
		if (quoteItem.getQuotedMfr() != null) {
			shopCartItem.setMfr(quoteItem.getQuotedMfr().getName());
		}

		shopCartItem.setFullMfrPart(quoteItem.getRequestedPartNumber());
		if (quoteItem.getSoldToCustomer() != null) {
			shopCartItem.setSoldToCode(quoteItem.getSoldToCustomer().getCustomerNumber());
			shopCartItem.setSoldToParty(quoteItem.getSoldToCustomer().getCustomerFullName());
		}
		if (quoteItem.getEndCustomer() != null) {
			shopCartItem.setEndCustomerCode(quoteItem.getEndCustomer().getCustomerNumber());
			shopCartItem.setEndCustomerName(quoteItem.getEndCustomer().getCustomerFullName());
		}
		if (quoteItem.getMpq() != null) {
			shopCartItem.setMpq(String.valueOf(quoteItem.getMpq()));
		}
		if (quoteItem.getMoq() != null) {
			shopCartItem.setMoq(String.valueOf(quoteItem.getMoq()));
		}
		if (!QuoteUtil.isEmpty(quoteItem.getRequestedQty()) && quoteItem.getRequestedQty() > 0) {
			shopCartItem.setRequestQtyStr(String.valueOf(quoteItem.getRequestedQty()));
		}
		if (quoteItem.getTargetResale() != null) {
			shopCartItem.setTargetResale(String.valueOf(quoteItem.getTargetResale()));
		}
		if (quoteItem.getFinalQuotationPrice() != null) {
			shopCartItem.setFinalPrice(String.valueOf(quoteItem.getFinalQuotationPrice()));
		}
		// TODO
		// get quote
		//replaced by DamonChen@20181108
		//List<QuantityBreakPrice> qbkList = programMaterialSB.getOrderQties(quoteItem);
		List<QuantityBreakPrice> qbkList =getOrderQties(quoteItem);
		if (!qbkList.isEmpty()) {
			shopCartItem.setOrderQty(TransferHelper.convertQrderQtyForExcel(qbkList, true));
			shopCartItem.setNormalSellPrice(TransferHelper.convertNormalSellPriceForExcel(qbkList, true));
			shopCartItem.setSalesCost(TransferHelper.convertSalesCostForExcel(qbkList, true));
			shopCartItem.setSuggestedResale(TransferHelper.convertSuggestedResaleForExcel(qbkList, true));

		} else if (quoteItem.getMinSellPrice() != null && quoteItem.getMinSellPrice() > 0) {
			if(quoteItem.convertToRfqValueWithDouble(quoteItem.getMinSellPrice() )!=null){
			shopCartItem.setNormalSellPrice(quoteItem.convertToRfqValueWithDouble(quoteItem.getMinSellPrice()).toString());
			}
		} else {
			//TODO
			//shopCartItem.setNormalSellPrice("get quote");
		}
		shopCartItem.setItemRemarks(quoteItem.getRemarks());
		return shopCartItem;
	}

	/* public void onCellEdit(CellEditEvent event) {
	        Object oldValue = event.getOldValue();
	        Object newValue = event.getNewValue();
	         
	        if(newValue != null && !newValue.equals(oldValue)) {
	            FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "Cell Changed", "Old: " + oldValue + ", New:" + newValue);
	            FacesContext.getCurrentInstance().addMessage(null, msg);
	        }
	    }
*/
	/******
	 * this is used to convert
	 * @param shopCartItem
	 * @param qt
	 * @param user
	 * @return
	 */
	public QuoteItem ConvertShopCartItemToQuoteItem(final ShoppingCartLoadItemBean shopCartItem, final Quote qt,
			User user) {

		QuoteItem quoteItem = new QuoteItem();
		quoteItem.setQuote(qt);
		quoteItem.setRequestedMfr(shopCartItem.getRequestedMfr());
		quoteItem.setRequestedPartNumber(shopCartItem.getFullMfrPart());
		quoteItem.setQuotedMfr(shopCartItem.getRequestedMfr());
		quoteItem.setQuotedPartNumber(shopCartItem.getFullMfrPart());
		quoteItem.setRequestedMaterialForProgram(shopCartItem.getMaterial());
		quoteItem.setQuotedMaterial(shopCartItem.getMaterial());

		quoteItem.setSoldToCustomer(shopCartItem.getSoldToCustomer());
		if (quoteItem.getSoldToCustomer() == null) {
			quoteItem.setSoldToCustomer(this.getEmptyCustomer());
		}
		quoteItem.setEndCustomer(shopCartItem.getEndCustomer());
		if (quoteItem.getEndCustomer() == null) {
			quoteItem.setEndCustomer(this.getEmptyCustomer());
		}
		if (!QuoteUtil.isEmpty(shopCartItem.getMoq())) {
			quoteItem.setMoq(Integer.valueOf(shopCartItem.getMoq()));
		}
		if (!QuoteUtil.isEmpty(shopCartItem.getMpq())) {
			quoteItem.setMpq(Integer.valueOf(shopCartItem.getMpq()));
		}
		if (!QuoteUtil.isEmpty(shopCartItem.getRequestQtyStr())) {
			quoteItem.setRequestedQty(Integer.valueOf(shopCartItem.getRequestQtyStr()));
		}
		if (!QuoteUtil.isEmpty(shopCartItem.getTargetResale())) {
			quoteItem.setTargetResale(Double.valueOf(shopCartItem.getTargetResale()));
		}
		if (!QuoteUtil.isEmpty(shopCartItem.getFinalPrice())) {
			quoteItem.setFinalQuotationPrice(Double.valueOf(shopCartItem.getFinalPrice()));
		}
		quoteItem.setRfqCurr(Currency.getByCurrencyName(rfqCurr));
		// user
		quoteItem.setRemarks(shopCartItem.getItemRemarks());
		quoteSB.applyDefaultCostIndicatorLogic(quoteItem, true);
		//quoteItem.setAction(ActionEnum.SHOPPINGCART_EXCEL_UPLOAD_ADD_ITEMS.toString());
		return quoteItem;
	}

	/*****
	 * 
	 * @return
	 */
	public StreamedContent getDownloadAll() {
		if (this.quote == null || this.quote.getQuoteItems().isEmpty()) {
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
					ResourceMB.getText("wq.message.selectOneRecord"), ""));
			return null;
		}
		List<ShoppingCartLoadItemBean> items = new ArrayList<ShoppingCartLoadItemBean>();
		for (QuoteItem quoteItem : this.quote.getQuoteItems()) {
			items.add(this.ConvertQuoteItemToShopCartItem(quoteItem));
		}
		downloadAll = downloadSrategy.getDownloadFile(this.getRegion(), items, ShoppingCartConstant.TEMPLATE_NAME);

		if (downloadAll == null) {
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
					ResourceMB.getText("wq.message.downloadError") + "!", ""));
			return null;
		}
		return downloadAll;
	}

	/****
	 * 
	 * @return
	 */
	public StreamedContent getDownloadAllSubmitResultFile() {
		LOG.info("begin downloadAllSubmitResultFile ");
		ShopCartSubmitResultDownLoadStrategy resultDownloadSrategy = new ShopCartSubmitResultDownLoadStrategy(this.isQco());
		StreamedContent file = null;
		if (this.resultQuote==null || this.resultQuote.getQuoteItems() == null || this.resultQuote.getQuoteItems().isEmpty()) {
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
					ResourceMB.getText("wq.message.selectOneRecord"), ""));
			LOG.info(" no data ");
			return null;
		}
		convertToRfqCurrValue(resultQuote);
		LOG.info("begin downloadAllSubmitResultFile ");
		file = resultDownloadSrategy.getDownloadFile(this.getRegion(), this.resultQuote.getQuoteItems(),
				ShoppingCartConstant.SUBMIT_RESULT_TEMPLATE_NAME);

		if (file == null) {
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
					ResourceMB.getText("wq.message.downloadError") + "!", ""));
			return null;
		}
		//this.submittedQuote||
		/*if ( this.getSelectedSubmitResultItems() == null || this.getSelectedSubmitResultItems().isEmpty()) {
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
					ResourceMB.getText("wq.message.selectOneRecord"), ""));
			return null;
		}
		file = resultDownloadSrategy.getDownloadFile(this.getRegion(), this.getSelectedSubmitResultItems(),
				ShoppingCartConstant.SUBMIT_RESULT_TEMPLATE_NAME);

		if (file == null) {
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
					ResourceMB.getText("wq.message.downloadError") + "!", ""));
			return null;
		}*/
		return file;
	}

	/**   
	* @Description:  from buy curr value to rfq curr value
	* @author 042659
	* @param resultQuote2       
	* @return void    
	* @throws  
	*/  
	private void convertToRfqCurrValue(Quote resultQuote) {
		// TODO Auto-generated method stub
		if(resultQuote ==null){
			return;
		}
		if(resultQuote.getQuoteItems() ==null){
			return;
		}
		
		for(QuoteItem item: resultQuote.getQuoteItems()){
			
			item.setQuotedPrice(item.convertToRfqValueWithDouble(item.getQuotedPrice())==null?null:item.convertToRfqValueWithDouble(item.getQuotedPrice()).doubleValue());
		    item.setSalesCost(item.convertToRfqValue(item.getSalesCost()));
		    item.setSuggestedResale(item.convertToRfqValue(item.getSuggestedResale()));
		}

		
		
	}

	/******
	 * this will be call by CatalogSearchMB
	 * @param pricerInfos
	 * @param rfqCurr2 
	 */
	public void addToDB(List<PricerInfo> pricerInfos, String rfqCurr2) {
		if (pricerInfos.isEmpty()) {
			return;
		}
		Log.info("call addToDB . user:" + user.getEmployeeId());
		Quote draftQuote = quoteSB.getProgDraftRfq(user, bizUnit);
		boolean noDraftQuoteInDB = false;
		if(draftQuote ==null) {
			draftQuote = this.getDrafQuoteForIntoDB(null);
			noDraftQuoteInDB = true;
		}
		Date date = new Date();
		draftQuote.setLastUpdatedBy(user.getEmployeeId());
		draftQuote.setLastUpdatedOn(date);
		for (PricerInfo pricerInfo : pricerInfos) {
			QuoteItem quoteItem = new QuoteItem();
			if (!noDraftQuoteInDB && draftQuote != null && draftQuote.getQuoteItems() != null && !draftQuote.getQuoteItems().isEmpty()
					&& draftQuote.getQuoteItems().get(0).getRfqCurr() != null) {

				pricerInfo.setRfqCurr(draftQuote.getQuoteItems().get(0).getRfqCurr().name());
				//added for defect# 16 by DamonChen@20181114
				quoteSB.applyDefaultCostIndicatorLogic(pricerInfo, true);
			} else {
				if(!QuoteUtil.isEmpty(rfqCurr2)){
					pricerInfo.setRfqCurr(rfqCurr2);
				}else{
					pricerInfo.setRfqCurr(bizUnit.getDefaultCurrency().name());
				}
			}
			quoteItem.setLastUpdatedBy(user.getEmployeeId());
			quoteItem.setLastUpdatedOn(date);
			quoteItem.setLastUpdatedName(user.getName());
			pricerInfo.fillPriceInfoToQuoteItem(quoteItem);
			quoteItem.setRequestedMfr(pricerInfo.getQuotedMfr());
			quoteItem.setRequestedMaterialForProgram(pricerInfo.getQuotedMaterial());
			quoteItem.setRequestedPartNumber(pricerInfo.getFullMfrPartNumber());
			quoteItem.setStage(QuoteSBConstant.QUOTE_STATE_PROG_DRAFT);
			quoteItem.setAction(ActionEnum.SHOPPINGCART_CATALOG_SEARCH_ADD_ITEMS.toString());
			quoteItem.setQuote(draftQuote);
			draftQuote.addItem(quoteItem);
			//reset the value  in page
			pricerInfo.setRfqCurr(rfqCurr2);
		}
		try {
			if(noDraftQuoteInDB) {
				quoteSB.saveProgDraftQuote(draftQuote, user, bizUnit);
			}else {
				quoteSB.saveQuote(draftQuote);
			}
		}catch (Exception e) {
			Log.info("call addToDB Failed. user:" + user.getEmployeeId() +";\n" + e.getMessage() + e.getStackTrace());
		}
		
		this.refreshShoppingCartInfo();
	}

	private void updateExchangeCurrencyInfoByBu(String regionName) {
		// added by June for project RMB cur to initilise exchange currency pull
		///List<String> currencyLst = exChangeRateSB.findAllExCurrencyByBu(regionName);
/*		exCurrencyList = QuoteUtil.createFilterOptions(currencyLst.toArray(new String[currencyLst.size()]),
				currencyLst.toArray(new String[currencyLst.size()]), false, false);

		ExcCurrencyDefault defaultCurrency = exChangeRateSB.findDefaultCurrencyByBu(regionName);*/
		
		
		BizUnit bu=bizUnitSB.findByPk(regionName);
		List<String> currencyLst = new ArrayList<String>(bu.getAllowCurrencies());
		exCurrencyList = QuoteUtil.createFilterOptions(currencyLst.toArray(new String[currencyLst.size()]),
				currencyLst.toArray(new String[currencyLst.size()]), false, false,true);
		
		
		if (null != bu.getDefaultCurrency()){
			this.setRfqCurr(bu.getDefaultCurrency().name());
		}
		
		initRfqcurr(this.quote);
		
	}

	/******
	 * this method will be call by CommodityMB
	 * @param mainVOs
	 */
	public void addCatalogMainVOToDB(CatalogMainVO[] mainVOs) {
		if (mainVOs == null || mainVOs.length < 1) {
			return;
		}
		Log.info("call addCatalogMainVOToDB . user:" + user.getEmployeeId() +" ::mainVOs length::" +mainVOs.length);
		Quote draftQuote = quoteSB.getProgDraftRfq(user, bizUnit);
		boolean noDraftQuoteInDB = false;
		if(draftQuote ==null) {
			draftQuote = this.getDrafQuoteForIntoDB(null);
			noDraftQuoteInDB = true;
		}
		Date date = new Date();
		draftQuote.setLastUpdatedBy(user.getEmployeeId());
		draftQuote.setLastUpdatedOn(date);
		for (CatalogMainVO mainVO : mainVOs) {
			QuoteItem quoteItem = ConvertToQuoteItemFromCatalogMainVO(mainVO, date,draftQuote,noDraftQuoteInDB);
			quoteItem.setQuote(draftQuote);
			draftQuote.addItem(quoteItem);
			//draftQuote.getQuoteItems().add(quoteItem);
			//Log.info("no get" + + draftQuote.quoteItems.size());
			//Log.info("no get" + + draftQuote.quoteItems.hashCode());
			//Log.info("get" + + draftQuote.getQuoteItems().hashCode());
			//Log.info(" for mainVOs, draftQuote.getQuoteItems before save size:::::" +draftQuote.getQuoteItems().size());
		}
		try {
			if(noDraftQuoteInDB) {
				quoteSB.saveProgDraftQuote(draftQuote, user, bizUnit);
				Log.info("end call addCatalogMainVOToDB noDraftQuoteInDB ." );
			}else {
				Log.info("end call addCatalogMainVOToDB has Quote Data. draftQuote.getQuoteItems before save size:::::" +draftQuote.getQuoteItems().size());
				quoteSB.saveQuote(draftQuote);
				Log.info("end call addCatalogMainVOToDB has Quote Data. draftQuote.getQuoteItems size:::::" +draftQuote.getQuoteItems().size());
			}
		}catch (Exception e) {
			Log.info("call addCatalogMainVOToDB Failed. user:" + user.getEmployeeId() +";\n" + e.getMessage() + e.getStackTrace());
		}
		Log.info("end call addCatalogMainVOToDB . user:" + user.getEmployeeId());
		this.refreshShoppingCartInfo();
		 
	}

	private QuoteItem ConvertToQuoteItemFromCatalogMainVO(CatalogMainVO mainVO, Date date, Quote draftQuote, boolean isNoDraftQuoteInDB) {
		QuoteItem quoteItem = new QuoteItem();
		quoteItem.setLastUpdatedBy(user.getEmployeeId());
		quoteItem.setLastUpdatedOn(date);
		quoteItem.setLastUpdatedName(user.getName());
		try {
			Pricer pricer = programMaterialSB.getQBreakPriceByPricerId(Long.valueOf(mainVO.getId()));
/*			quoteItem.setRequestedMaterialForProgram(pricer.getMaterial());
			quoteItem.setRequestedPartNumber(pricer.getMaterial().getFullMfrPartNumber());
			quoteItem.setRequestedMfr(pricer.getMaterial().getManufacturer());
			quoteItem.setQuotedMaterial(pricer.getMaterial());
			quoteItem.setQuotedMfr(pricer.getMaterial().getManufacturer());
			quoteItem.setQuotedPartNumber(pricer.getMaterial().getFullMfrPartNumber());
			quoteItem.setPricerId(pricer.getId());
			// add by Vincent 20181022
			quoteItem.setBuyCurr(Currency.valueOf(mainVO.getBuyCurrency()));*/
			Currency defaultCurrency = bizUnit.getDefaultCurrency();
			//Added by DamonChen@20181113
			if ( !isNoDraftQuoteInDB && draftQuote != null && draftQuote.getQuoteItems() != null && !draftQuote.getQuoteItems().isEmpty()) {
				if (draftQuote.getQuoteItems().get(0).getRfqCurr() != null) {
					defaultCurrency = draftQuote.getQuoteItems().get(0).getRfqCurr();
				}
			}
			quoteItem.setRfqCurr(defaultCurrency);
			//quoteItem.setFinalCurr(defaultCurrency);
			//PricerInfo pricerInfo = quoteItem.createPricerInfo();
			PricerInfo pricerInfo = new PricerInfo();
			pricerInfo.setRfqCurr(defaultCurrency.name());
			pricerInfo.setPricerId(pricer.getId());
			pricerInfo.setFullMfrPartNumber(pricer.getMaterial().getFullMfrPartNumber());
			pricerInfo.setMfr(pricer.getMaterial().getManufacturer().getName());
		  	pricerInfo.setQuotedMfr(pricer.getMaterial().getManufacturer());
	    	pricerInfo.setQuotedMaterial(pricer.getMaterial());
	    	pricerInfo.setBizUnit(bizUnit.getName());
	    	pricerInfo.setAllowedCustomers(user.getCustomers());
			pricerInfo.setExRateDate(new Date());
			quoteSB.applyDefaultCostIndicatorLogic(pricerInfo, true);
			pricerInfo.fillPriceInfoToQuoteItem(quoteItem);
			quoteItem.setRequestedMfr(pricerInfo.getQuotedMfr());
			quoteItem.setRequestedMaterialForProgram(pricerInfo.getQuotedMaterial());
			quoteItem.setRequestedPartNumber(pricerInfo.getFullMfrPartNumber());
			quoteItem.setStage(QuoteSBConstant.QUOTE_STATE_PROG_DRAFT);
			quoteItem.setAction(ActionEnum.SHOPPINGCART_CATALOG_MAIN_ADD_ITEMS.toString());
		} catch (Exception ex) {
			LOG.severe("Get Pricer from CatalogMainVO error :" + ex.getMessage());
			throw ex;
		}
		return quoteItem;
	}

	private void updateSystemInfoForBizUnitChange() {
		LOG.fine("call |  updateSystemInfo");
		this.updateSystemInfoForBizUnitChange(user, bizUnit);
	}

	public void updateSystemInfoForBizUnitChange(User user, BizUnit bizUnit) {
		LOG.fine("call |  updateSystemInfo(User user, BizUnit bizUnit)");
		// change the region
		this.bizUnit = bizUnit;
		this.setRegion(bizUnit.getName());
		this.updateExchangeCurrencyInfoByBu(bizUnit.getName());
		// &nbsp &nbsp &nbsp &nbsp &nbsp
		this.refreshShoppingCartInfo();
	}
	
	public void refreshShoppingCartInfo(){
		String shoppingLinkInfo = ResourceMB.getText("wq.link.shoppingCart") + " ({0}:{1})";
		shoppingLinkInfo = MessageFormat.format(shoppingLinkInfo, this.getRegion(),
				quoteSB.getRfqDraftCount(user, bizUnit));
		this.setShoppingCartInfo(shoppingLinkInfo);
		this.quote = this.initialQuote();
	}
	
	public void shoppingCartRedirect(){
		String url = "WebPromo/ShoppingCart.jsf";
		FacesUtil.redirect(url);
		resetAction();
	}
 
	public void remarksChange(){
		this.setNeedSave(true);
	}
	/******
	 * set soldTo and endCust to quoteItems
	 */
	public void updateSoldToAndEndCustomerToItem()  {
		this.setNeedSave(true);
		if (this.quote != null) {
			for (QuoteItem quoteItem : this.quote.getQuoteItems()) {
				quoteItem.setRfqCurr(Currency.getByCurrencyName(this.rfqCurr));
				try {
					if (this.quote.getSoldToCustomer() == null) {
						quoteItem.setSoldToCustomer(this.getEmptyCustomer());
					}else {
						quoteItem.setSoldToCustomer((Customer)this.quote.getSoldToCustomer().deepClone());
					}
					
					if (this.quote.getEndCustomer() == null) {
						quoteItem.setEndCustomer(this.getEmptyCustomer());
					}else {
						quoteItem.setEndCustomer((Customer)this.quote.getEndCustomer().deepClone());
					}
				}catch(Exception e)
				{			
					LOG.severe("Customer deepClone error in updateSoldToOrEndCustomerToItem ::" + e.getMessage() + e.getStackTrace());
				}
				String salesName = quote.getSales().getName();
				if(!QuoteUtil.isEmpty(salesName)) {
					try {
						quoteSB.applyDefaultCostIndicatorLogic(quoteItem, true);
					}catch (Exception e) {
						
					}
					// refresh
					
					
				}
			}
		}

	}

	// public Quote createNewQuote() {
	// Quote returnQ = new Quote();
	// if(quote != null) {
	// if (quote.getSales() != null) {
	// returnQ.setSales(quote.getSales());
	// }
	// if (quote.getSoldToCustomer() != null) {
	// returnQ.setSoldToCustomer(quote.getSoldToCustomer());
	// }
	// if (quote.getRemarksToCustomer() != null) {
	// returnQ.setRemarksToCustomer(quote.getRemarksToCustomer());
	// }
	// }else {
	//
	// }
	// }
	//

	public String verifyDataInDB(List<ShoppingCartLoadItemBean> beans) {
		StringBuffer sb = new StringBuffer("");
		for (ShoppingCartLoadItemBean bean : beans) {
			if (PricerUtils.isAllowAppend(sb.length())) {
				// 1 get material
				if (!QuoteUtil.isEmpty(bean.getMfr())) {
					bean.setMaterial(materialSB.find(bean.getMfr(), bean.getFullMfrPart()));
					if (bean.getMaterial() == null) {
						sb.append(ResourceMB.getParameterizedText("wq.error.materialNoFound",
								String.valueOf(bean.getLineSeq())) + "<br/>");
					}
				}
				// wq.error.soldToCodeNofound
				if (!QuoteUtil.isEmpty(bean.getSoldToCode())) {
					bean.setSoldToCustomer(this.getCustomerInfomationByCode(bean.getSoldToCode().trim()));
					if (bean.getSoldToCustomer() == null) {
						sb.append(ResourceMB.getParameterizedText("wq.error.soldToCodeNofound",
								String.valueOf(bean.getLineSeq())) + "<br/>");
					}
				}
				
				// wq.error.endToCodeNofound
				if (!QuoteUtil.isEmpty(bean.getEndCustomerCode())) {
					//fixed for defect#102 by DamonChen@20181210
					bean.setEndCustomer(this.getEndCustomer(bean.getEndCustomerCode().trim(), bean.getSoldToCode()));
					if (bean.getEndCustomer() == null) {
						sb.append(ResourceMB.getParameterizedText("wq.error.endToCodeNofound",
								String.valueOf(bean.getLineSeq())) + "<br/>");
					}
				}
				// get mfr
				if (!QuoteUtil.isEmpty(bean.getMfr())) {
					bean.setRequestedMfr(manufacturerSB.findManufacturerByName(bean.getMfr(), bizUnit));
					if (bean.getRequestedMfr() == null) {
						sb.append(ResourceMB.getParameterizedText("wq.error.mfrNoFound",
								String.valueOf(bean.getLineSeq())) + "<br/>");
					}
				}
			}
		}
		return sb.toString();
	}

	// public
	// public
	/***
	 * @Transient private String drmsUpdateFailedDesc;
	 * 
	 * @Transient private String drmsUpdateFailedDesc;
	 * 
	 * @Transient private String drmsUpdateFailedDesc;
	 * 
	 * @Transient private String drmsUpdateFailedDesc;
	 * 
	 * @Transient private String drmsUpdateFailedDesc;
	 * 
	 * @Transient private String drmsUpdateFailedDesc;
	 */

	// public
	public UploadedFile getUploadFile() {
		return uploadFile;
	}

	public void setUploadFile(UploadedFile uploadFile) {
		this.uploadFile = uploadFile;
	}

	public String getUploadFileName() {
		return uploadFileName;
	}

	public void setUploadFileName(String uploadFileName) {
		this.uploadFileName = uploadFileName;
	}

	public void showMessage(Severity severityError, String errMsg) {
		FacesContext.getCurrentInstance().addMessage("messages_submit", new FacesMessage(severityError, errMsg, ""));
		reset();
	}
	/*
	 * public void showGrowl(Severity severityError, String errMsg) {
	 * FacesContext.getCurrentInstance().addMessage("growl_submit_target", new
	 * FacesMessage(severityError,EMPTY_STR, errMsg)); reset(); }
	 */

	private void reset() {
		uploadFile = null;
		// uploadFileName = null;
		// action = null;
	}

	/*public HSSFWorkbook getQuoteTemplateBySoldTo(Quote quote, List<String> specialList) throws WebQuoteException {

		if (quote == null)
			throw new WebQuoteException(ResourceMB.getText("wq.exception.nullQuote"));
		Customer customerTemp = quote.getSoldToCustomer();
		if (customerTemp == null)
			throw new WebQuoteException(ResourceMB.getText("wq.exception.nullCustomer"));
		Customer customer = customerSB.findByPK(customerTemp.getCustomerNumber());
		if (customer == null)
			throw new WebQuoteException(ResourceMB.getText("wq.exception.customerNotFound"));
		quote.setSoldToCustomer(customer);
		HSSFWorkbook wb = null;
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		if (quote != null) {
			wb = getHSSFWorkbook(quote.getBizUnit().getName());
			if (null == wb) {
				throw new WebQuoteException(ResourceMB.getText("wq.message.downloadfail"));
			}
			HSSFSheet sheet = wb.getSheet("Quotation");
			HSSFFont normalStyle = wb.createFont();
			normalStyle.setFontHeightInPoints((short) 8);
			HSSFCellStyle style1 = wb.createCellStyle();
			style1.setWrapText(true);
			style1.setFillForegroundColor(HSSFColor.WHITE.index);
			style1.setFillPattern(CellStyle.SOLID_FOREGROUND);
			style1.setBorderLeft(HSSFCellStyle.BORDER_THIN);
			style1.setBorderRight(HSSFCellStyle.BORDER_THIN);
			style1.setBorderBottom(HSSFCellStyle.BORDER_THIN);
			style1.setBorderTop(HSSFCellStyle.BORDER_THIN);
			style1.setFont(normalStyle);
			style1.setAlignment(HSSFCellStyle.ALIGN_CENTER);

			// set latest exchange rate to excel ,
			// HSSFSheet sheet = wb.getSheet("Quotation");
			fillRateToXLS(quote, sheet); // added by June to get latest exchange
											// rate at

			for (int i = 0; i < 10; i++) {
				for (int j = 0; j <= QuoteConstant.QUOTE_TEMPLATE_HEADER_END; j++) {
					String position = QuoteUtil.convertExcelPositionToString(i, j);
					HSSFRow row = sheet.getRow(j);
					HSSFCell cell = row.getCell(i);
					switch (position) {
					case "E3":
						// Fixed the issue 1543 and 1548 on Feb 24, 2014
						if (quote.getSubmissionDate() != null)
							cell.setCellValue(sdf.format(quote.getSubmissionDate()));
						break;
					case "E5":
						cell.setCellValue(sdf.format(QuoteUtil.getCurrentTime()));
						break;
					case "F7":
						cell.setCellValue(quote.getFormNumber());
						break;
					case "H7":
						cell.setCellValue(quote.getYourReference());
						break;
					case "F8":
						cell.setCellValue(quote.getSales() == null ? null : quote.getSales().getName());
						break;
					case "H8":
						if (quote.getSales() != null && quote.getSales().getTeam() != null) {
							cell.setCellValue(quote.getSales().getTeam().getName());
						}
						break;
					case "J8":
						if (quote.getSales() != null) {
							cell.setCellValue(quote.getSales().getEmployeeId());
						}
						break;
					case "F9":
						if (quote.getSoldToCustomer() != null) {
							String customerName = quote.getSoldToCustomer().getCustomerFullName();
							
							 * if(quote.getSoldToCustomer().getCustomerName2()
							 * != null) customerName +=
							 * quote.getSoldToCustomer().getCustomerName2();
							 
							cell.setCellValue(
									customerName + " (" + quote.getSoldToCustomer().getCustomerNumber() + ")");
						}
						break;
					case "F10":
						// cell.setCellValue();
						break;
					case "F11":
						// cell.setCellValue();
						break;
					case "F12":
						cell.setCellValue(quote.getSoldToCustomer().getCustomerType());
						break;
					case "H12":
						// cell.setCellValue();
						break;
					case "F13":
						// cell.setCellValue();
						break;
					case "H13":
						// cell.setCellValue();
						break;
					case "F14":
						// cell.setCellValue();
						break;
					case "F15":
						// cell.setCellValue();
						break;
					case "H15":
						// cell.setCellValue();
						break;
					case "F16":
						// cell.setCellValue();
						break;
					case "H16":
						// cell.setCellValue();
						break;
					case "F17":
						// cell.setCellValue();
						break;
					case "H17":
						// cell.setCellValue();
						break;
					case "F18":
						// cell.setCellValue();
						break;
					case "H18":
						// cell.setCellValue();
						break;
					case "F19":
						// cell.setCellValue();
						break;
					case "H19":
						// cell.setCellValue();
						break;
					case "I19":
						// cell.setCellValue();
						break;
					case "J19":
						// cell.setCellValue();
						break;
					case "F20":
						cell.setCellValue(quote.getRemarks());
						break;
					case "G20":
						// cell.setCellValue();
						break;
					case "F21":
						cell.setCellValue(quote.getRemarks());
						break;
					case "G21":
						// cell.setCellValue();
						break;
					}
				}
			}
			List<QuoteItem> quoteItems = quote.getQuoteItems();
			if (quoteItems.size() > 1) {
				for (int i = 1; i < quoteItems.size(); i++) {
					QuoteUtil.copyRow(sheet, QuoteConstant.QUOTE_ITEM_ROW_START,
							QuoteConstant.QUOTE_ITEM_ROW_START + i);
				}
			}
			for (int i = 0; i < quoteItems.size(); i++) {
				QuoteItem quoteItem = quoteItems.get(i);
				HSSFRow row = sheet.getRow(QuoteConstant.QUOTE_ITEM_ROW_START + i);
				HSSFCell cellSequence = row.getCell(2);
				cellSequence.setCellValue("No." + (i + 1));

				HSSFCell cellQuoteNumber = row.getCell(3);
				HSSFCell cellFirstRfqCode = row.getCell(4);
				HSSFCell cellQuotedPartNumber = row.getCell(8);

				if (QuoteSBConstant.QUOTE_STAGE_PENDING.equalsIgnoreCase(quoteItem.getStage())) {
					cellQuoteNumber.setCellValue(EMPTY_STR);
					cellFirstRfqCode.setCellValue(EMPTY_STR);
					cellQuotedPartNumber.setCellValue(EMPTY_STR);
				} else {
					cellQuoteNumber.setCellValue(quoteItem.getQuoteNumber());
					cellFirstRfqCode.setCellValue(quoteItem.getFirstRfqCode());
					if (quoteItem.getQuotedMaterial() != null)
						cellQuotedPartNumber.setCellValue(quoteItem.getQuotedMaterial().getFullMfrPartNumber());
				}

				HSSFCell cellMfr = row.getCell(5);
				HSSFCell cellMfrName = row.getCell(6);
				// Delinkage change on Oct 29 , 2013 by Tonmy
				// if(quoteItem.getRequestedMaterial() != null){
				// cell_mfr.setCellValue(quoteItem.getRequestedMaterial().getManufacturer().getName());
				// }
				// HSSFCell cell_requestedPartNumber = row.getCell(6);
				// if(quoteItem.getRequestedMaterial() != null){
				// cell_requestedPartNumber.setCellValue(quoteItem.getRequestedMaterial().getFullMfrPartNumber());
				// }

				if (quoteItem.getRequestedMfr() != null) {
					cellMfr.setCellValue(quoteItem.getRequestedMfr().getName());
					String mfrDescription = quoteItem.getRequestedMfr().getDescription();
					if (mfrDescription != null) {
						String sub = quoteItem.getRequestedMfr().getName() + " ";
						if (mfrDescription.startsWith(sub)) {
							cellMfrName
									.setCellValue(quoteItem.getRequestedMfr().getDescription().substring(sub.length()));
						}
					}
				}

				// if(quoteItem.getRequestedMfr() != null){
				// cell_mfr.setCellValue(quoteItem.getRequestedMfr().getName());
				// }
				HSSFCell cellRequestedPartNumber = row.getCell(7);
				if (quoteItem.getRequestedPartNumber() != null) {
					cellRequestedPartNumber.setCellValue(quoteItem.getRequestedPartNumber());
				}

				HSSFCell cellAqcc = row.getCell(9);
				cellAqcc.setCellStyle(style1);
				if (quoteItem.getQcComment() != null && quoteItem.getQcComment().contains("<br/>")) {
					quoteItem.setQcComment(quoteItem.getQcComment().replaceAll("<br/>", "\r\n"));
				}
				cellAqcc.setCellValue(quoteItem.getQcComment());

				HSSFCell cellRequiredQty = row.getCell(10);
				cellRequiredQty.setCellValue(quoteItem.getRequestedQty());

				HSSFCell cellEau = row.getCell(11);
				String eau = "";
				if (quoteItem.getEau() != null)
					eau = quoteItem.getEau().toString();
				else
					eau = "";
				cellEau.setCellValue(eau);

				HSSFCell cellTargetPrice = row.getCell(12);
				if (quoteItem.getTargetResale() != null)
					cellTargetPrice.setCellValue(quoteItem.getTargetResale());

				HSSFCell cellQuotedPrice = row.getCell(13);
				if (QuoteSBConstant.RFQ_STATUS_AQ.equalsIgnoreCase(quoteItem.getStatus())) {
					if (quoteItem.getQuotedPrice() != null)
						cellQuotedPrice.setCellValue(quoteItem.getQuotedPrice());
				} else {
					cellQuotedPrice.setCellValue("In Progress");
				}

				HSSFCell cellPmoq = row.getCell(14);
				if (quoteItem.getPmoq() != null)
					cellPmoq.setCellValue(quoteItem.getPmoq());
				HSSFCell cellMpq = row.getCell(15);
				if (quoteItem.getMpq() != null)
					cellMpq.setCellValue(quoteItem.getMpq());
				HSSFCell cellMoq = row.getCell(16);
				if (quoteItem.getMoq() != null)
					cellMoq.setCellValue(quoteItem.getMoq());
				HSSFCell cellLeadTime = row.getCell(17);
				if (QuoteSBConstant.RFQ_STATUS_AQ.equalsIgnoreCase(quoteItem.getStatus())) {
					if (quoteItem.getLeadTime() != null)
						cellLeadTime.setCellValue(quoteItem.getLeadTime());
				} else {
					cellLeadTime.setCellValue(EMPTY_STR);
				}
				// New field "Quotation effective date"
				HSSFCell cellQuotationEffective = row.getCell(18);
				if (quoteItem.getQuotationEffectiveDate() != null) {
					cellQuotationEffective.setCellValue(DateUtils.formatDate(quoteItem.getQuotationEffectiveDate()));
				}

				HSSFCell cellPriceValidity = row.getCell(19);
				if (QuoteSBConstant.RFQ_STATUS_AQ.equalsIgnoreCase(quoteItem.getStatus())) {
					if (quoteItem.getPriceValidity() != null)
						cellPriceValidity.setCellValue(quoteItem.getPriceValidity());
				} else {
					cellPriceValidity.setCellValue(EMPTY_STR);
				}

				HSSFCell cellShipmentValidity = row.getCell(20);
				if (QuoteSBConstant.RFQ_STATUS_AQ.equalsIgnoreCase(quoteItem.getStatus())) {
					if (quoteItem.getShipmentValidity() != null) {
						cellShipmentValidity.setCellValue(DateUtils.formatDate(quoteItem.getShipmentValidity()));
					}

				} else {
					cellShipmentValidity.setCellValue(EMPTY_STR);
				}

				HSSFCell cellTermsAndConditions = row.getCell(21);
				cellTermsAndConditions.setCellStyle(style1);
				if (quoteItem.getTermsAndConditions() != null)
					cellTermsAndConditions.setCellValue(quoteItem.getTermsAndConditions());
				HSSFCell cellAllocationFlag = row.getCell(22);
				if (quoteItem.getAllocationFlag() != null)
					cellAllocationFlag.setCellValue(quoteItem.getAllocationFlag().booleanValue()
							? QuoteConstant.OPTION_YES : QuoteConstant.OPTION_NO);
				HSSFCell cellRescheduleWindow = row.getCell(23);
				if (quoteItem.getRescheduleWindow() != null)
					cellRescheduleWindow.setCellValue(quoteItem.getRescheduleWindow());
				HSSFCell cellCancellationWindow = row.getCell(24);
				if (quoteItem.getCancellationWindow() != null)
					cellCancellationWindow.setCellValue(quoteItem.getCancellationWindow());
				HSSFCell cellSoldTo = row.getCell(25);
				if (quoteItem.getSoldToCustomer() != null) {
					String customerName = quoteItem.getSoldToCustomer().getCustomerFullName();
					
					 * if(customerName != null &&
					 * quoteItem.getSoldToCustomer().getCustomerName2() != null)
					 * customerName +=
					 * quoteItem.getSoldToCustomer().getCustomerName2();
					 
					if (customerName != null)
						cellSoldTo.setCellValue(
								customerName + " (" + quote.getSoldToCustomer().getCustomerNumber() + ")");
				}
				HSSFCell cellShipTo = row.getCell(26);
				if (quoteItem.getShipToCustomer() != null) {
					String customerName = quoteItem.getShipToCustomer().getCustomerFullName();
					
					 * if(customerName != null &&
					 * quoteItem.getShipToCustomer().getCustomerName2() != null)
					 * customerName +=
					 * quoteItem.getShipToCustomer().getCustomerName2();
					 
					if (customerName != null)
						cellShipTo.setCellValue(customerName);
				}
				HSSFCell cellEndCustomer = row.getCell(27);
				if (quoteItem.getEndCustomer() != null) {
					String customerName = quoteItem.getEndCustomer().getCustomerFullName();
					
					 * if(customerName != null &&
					 * quoteItem.getEndCustomer().getCustomerName2() != null)
					 * customerName +=
					 * quoteItem.getEndCustomer().getCustomerName2();
					 
					if (customerName != null)
						cellEndCustomer.setCellValue(customerName);
				}
				HSSFCell cellItemRemarks = row.getCell(28);
				if (quoteItem.getRemarks() != null)
					cellItemRemarks.setCellValue(quoteItem.getRemarks());

			}
			if (specialList != null && specialList.size() > 0) {
				StringBuffer speStrSb = new StringBuffer();
				for (int i = 0; i < specialList.size(); i++) {
					if (i != 0) {
						speStrSb.append(specialList.get(i)).append("\r\n");
					}
				}

				if (speStrSb != null && speStrSb.toString().length() > 0) {
					HSSFRow row = sheet.getRow(QuoteConstant.QUOTE_ITEM_ROW_START + quoteItems.size() + 2);
					HSSFCell cellSpeic = row.getCell(8);
					cellSpeic.setCellValue(speStrSb.toString());
				}

			}
		}
		return wb;
	}*/

	public HSSFWorkbook getHSSFWorkbook(String bizUnitName) {
		FileInputStream fileIn;
		HSSFWorkbook wb = null;
		String templateName = null;
		String filePath = null;
		try {
			templateName = sysMaintSB.getQuotationTemplate(bizUnitName);
			filePath = DeploymentConfiguration.configPath + File.separator + templateName;
			fileIn = new FileInputStream(filePath);
			wb = new HSSFWorkbook(fileIn);
		} catch (IOException e) {
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
					ResourceMB.getText("wq.message.downloadfail") + ".", ""));
			LOG.log(Level.SEVERE, "Exception in getting file : " + templateName + ", Biz Unit name: " + bizUnitName
					+ " , Exception message : " + e.getMessage(), e);
			return null;
		}
		return wb;
	}

/*	public void processAutoQuote(Quote submitedQuote) throws FileNotFoundException, IOException, Exception {
		QuotationEmailVO vo = new QuotationEmailVO();
		vo.setFormNumber(submitedQuote.getFormNumber());
		vo.setLink(getUrl() + "/RFQ/MyQuoteListForSales.jsf?quoteid=" + submitedQuote.getId());
		vo.setRecipient(quote.getSales().getName());
		vo.setSender(getSender(submitedQuote));
		vo.setSubject(getQuotationSubject(submitedQuote));
		vo.setFromEmail(getFromEmail(submitedQuote.getBizUnit().getName()));
		vo.setToEmails(generateEmailToList(submitedQuote));

		File xlsFile = null;

		FileOutputStream fileOut = null;
		try {
			MailInfoBean mailInfoBean = new MailInfoBean();
			mailInfoBean.setMailSubject(vo.getSubject());
			mailInfoBean.setMailFrom(vo.getFromEmail());
			mailInfoBean.setMailTo(vo.getToEmails());
			String content = "Dear " + vo.getRecipient() + ",<br/><br/>";
			content += "Quotation is attached. Good Selling!<br/><br/>";
			content += "RFQ Form: " + vo.getFormNumber() + "<br/>";
			content += "<a href=\"" + vo.getLink() + "\">" + vo.getLink() + "</a><br/>";
			content += "Best Regards," + "<br/>";
			content += vo.getSender() + "<br/>";
			mailInfoBean.setMailContent(content);

			HSSFWorkbook hw = getQuoteTemplateBySoldTo(submitedQuote, tempSTList);
			String tempPath = sysConfigSB.getProperyValue(QuoteConstant.TEMP_DIR);
			String fileName = tempPath + ProgRfqSubmitHelper.getQuotationFileName(submitedQuote);
			xlsFile = new File(fileName + ".xls");
			fileOut = new FileOutputStream(xlsFile);
			hw.write(fileOut);
			mailInfoBean.setZipFile(xlsFile);
			mailUtilsSB.sendAttachedMail(mailInfoBean);
		} catch (FileNotFoundException ex) {
			throw ex;
		} catch (IOException ex1) {
			throw ex1;
		} catch (Exception ex2) {
			throw ex2;
		} finally {

			if (fileOut != null)
				fileOut = null;
			if (xlsFile != null) {
				if (xlsFile.exists())
					xlsFile.delete();
			}
		}
	}*/

	public void handleErrorMsg(String msg) {
		FacesMessage msgTemp = new FacesMessage(FacesMessage.SEVERITY_ERROR, EMPTY_STR, msg);
		FacesContext.getCurrentInstance().addMessage("growl_submit_target", msgTemp);
	}

	public void removeAttachment() {
		attachments.remove(seletedDeletedAttachementRowId);
		List<QuoteItem> quoteItems = quote.getQuoteItems();
		quoteItems.get(this.itemNumberForAttachment).setAttachments(attachments);
		this.setNeedSave(true);
	/*	for (QuoteItem item : quoteItems) {
			// Delinkage change on Oct 29 , 2013 by Tonmy
			// if(item.getRequestedMaterial().getProgramMaterial().getId()==itemNumberForAttachment)
			// PROGRM PRICER ENHANCEMENT
			// if(item.getRequestedMaterialForProgram().getProgramMaterial().getId()==itemNumberForAttachment)
			if (item.getRequestedProgramMaterialForProgram().getId() == itemNumberForAttachment) {
				item.setAttachments(attachments);
			}
		}*/
	}

	public void updateNewProspectiveCustomer() {
		newCustomerTitle = ResourceMB.getText("wq.label.prospectiveCust");
		clearNewCustProspective();
		newProspectiveCustomerType = QuoteSBConstant.CREATE_CUSTOMER_TYPE_CUSTOMER;
		newProspectiveCustomerName1 = quote.getSoldToCustomer().getCustomerName1();
		
		/*clearNewCustProspective();
		newProspectiveCustomerName1 = quote.getSoldToCustomer().getCustomerName1();
		newProspectiveCustomerCountry = " ";
		newProspectiveCustomerCity = "";
		newProspectiveCustomerSalesOrg = " ";
		newCustomerTitle = ResourceMB.getText("wq.label.endCust");
		RequestContext requestContext = RequestContext.getCurrentInstance();
		requestContext.update("form_rfqSubmission:newProspectiveCustomer_dialog");
		clearNewCustProspective();
		newProspectiveCustomerType = QuoteSBConstant.CREATE_CUSTOMER_TYPE_END_CUSTOMER;
		newProspectiveCustomerName1 = rfqHeader.getEndCustomerName();*/
	}
	public void updateNewProspectiveEndCustomer() {
		newCustomerTitle = ResourceMB.getText("wq.label.endCust");
		clearNewCustProspective();
		newProspectiveCustomerType = QuoteSBConstant.CREATE_CUSTOMER_TYPE_END_CUSTOMER;
		newProspectiveCustomerName1 = quote.getEndCustomer().getCustomerName1();
	}

	
	private void clearNewCustProspective() {
		newProspectiveCustomerName1 = "";
		newProspectiveCustomerName2 = "";
		newProspectiveCustomerCountry = "";
		newProspectiveCustomerCity = "";
		newProspectiveCustomerAddress3 = "";
		newProspectiveCustomerAddress4 = "";
		newProspectiveCustomerSalesOrg = "";
	}

	public void updateNewSoldToCode() {

		this.setNeedSave(true);
		// logger.log(Level.INFO, "PERFORMANCE START - updateNewSoldToCode()");
		Customer customertt = customerSB.findByPK(duplicatedCustomerCode);
		if(QuoteSBConstant.CREATE_CUSTOMER_TYPE_CUSTOMER.equalsIgnoreCase(newProspectiveCustomerType)) {
			quote.setSoldToCustomer(customertt.deepClone());
		}else if (QuoteSBConstant.CREATE_CUSTOMER_TYPE_END_CUSTOMER.equalsIgnoreCase(newProspectiveCustomerType)) {
			quote.setEndCustomer(customertt.deepClone());
		}
		this.clearNewCustProspective();
		
		// logger.log(Level.INFO, "PERFORMANCE END - updateNewSoldToCode()");
	}

	public BizUnit getBizUnit() {
		return bizUnit;
	}

	public void setBizUnit(BizUnit bizUnit) {
		this.bizUnit = bizUnit;
	}

	public void switchBizUnit(BizUnit bizUnit) {
		this.bizUnit = bizUnit;
	}

	public void deleteAllAction() {
		LOG.fine("call | deleteAllAction");
		clearPageQuote();
		updateSystemInfoForSalesChange();
		this.setNeedSave(true);
		/*
		 * List<QuoteItem> quoteItems = quote.getQuoteItems();
		 * quoteItems.clear(); quote.setQuoteItems(quoteItems);
		 */
	}

	public BizUnit getSalesBizUnit() {
		return salesBizUnit;
	}

	public void setSalesBizUnit(BizUnit salesBizUnit) {
		this.salesBizUnit = salesBizUnit;
	}

	// Delinkage change on Oct 29 , 2013 by Tonmy
	public Material getMaterial(QuoteItem item) {
		Material tempM = (Material) programMaterialSB.findExactMaterialByQuoteItem(item, bizUnit);
		if (tempM == null)
			return null;
		return tempM;
	}

	public ProgramPricer getProgramMaterialByCostIndicatorAndBizUnit(Material tempM, QuoteItem item) {
		// PROGRM PRICER ENHANCEMENT
		ProgramPricer tempPM = materialSB.getSpecifiedValidProgMatByBizUintAndCostIndicator(tempM, bizUnit.getName(),
				item.getCostIndicator());
		// ProgramMaterial tempPM =
		// tempM.getSpecifiedValidProgMatByBizUintAndCostIndicator(bizUnit.getName(),
		// item.getCostIndicator());
		if (tempPM == null) {
			return null;
		}
		return tempPM;
	}

	// Delinkage change on Oct 29 , 2013 by Tonmy
	// public ProgramMaterial getProgramMaterial(QuoteItem item)
	// {
	// Material tempM =
	// (Material)programMaterialSB.findExactMaterialByQuoteItem(item, bizUnit);
	// if(tempM== null)
	// return null;
	// return tempM.getProgramMaterial();
	// }

	// Delinkage change on Oct 29 , 2013 by Tonmy
	// public long getProgramMaterialId(QuoteItem item)
	// {
	// Material tempM =
	// (Material)programMaterialSB.findExactMaterialByQuoteItem(item, bizUnit);
	// if(tempM== null)
	// return 0;
	// return tempM.getProgramMaterial().getId();
	// }

	// Delinkage change on Oct 29 , 2013 by Tonmy
	public Quote getProgramDraftQuoteCopy(Quote quote) {
		Quote returnQ = new Quote();
		if (quote.getSales() != null) {
			returnQ.setSales(quote.getSales());
		}
		if (quote.getSoldToCustomer() != null) {
			returnQ.setSoldToCustomer(quote.getSoldToCustomer());
		}
		if (quote.getRemarks() != null) {
			returnQ.setRemarks(quote.getRemarks());
		}

		List<QuoteItem> newQuoteItemList = new ArrayList<QuoteItem>();
		List<QuoteItem> quoteItemList = quote.getQuoteItems();
		if (quoteItemList != null && quoteItemList.size() > 0) {
			for (QuoteItem item : quoteItemList) {
				QuoteItem newQ = new QuoteItem();
				// Delinkage change on Oct 29 , 2013 by Tonmy
				// PROGRM PRICER ENHANCEMENT
				// ProgramMaterial tempPm =
				// item.getRequestedMaterialForProgram().getProgramMaterial();
				ProgramPricer tempPm = item.getRequestedProgramMaterialForProgram();
				// PROGRM PRICER ENHANCEMENT
				newQ.setRequestedProgramMaterialForProgram(tempPm);
				newQ.setRequestedMfr(item.getRequestedMfr());
				newQ.setRequestedPartNumber(item.getRequestedPartNumber());
				newQ.setRequestedMaterialForProgram(item.getRequestedMaterialForProgram());
				newQ.setQuotedMfr(item.getRequestedMfr());
				newQ.setQuotedPartNumber(item.getRequestedPartNumber());
				newQ.setQuotedMaterial(item.getRequestedMaterialForProgram());
				newQ.setMoq(tempPm.getMoq());
				newQ.setMpq(tempPm.getMpq());
				if (item.getRequestedQty() != null) {
					newQ.setRequestedQty(item.getRequestedQty());
				}
				if (item.getTargetResale() != null) {
					newQ.setTargetResale(item.getTargetResale());
				}
				newQ.setRemarks(item.getRemarks());
				newQ.setQuote(returnQ);
				// if(item.getAttachments()!=null &
				// item.getAttachments().size()>0)
				// {
				// for(Attachment att: item.getAttachments())
				// {
				// Attachment newAtt = new Attachment();
				// newAtt.setFileImage(att.getFileImage());
				// newAtt.getFileName(att.getFileName());
				// newAtt.getQuote();
				// }
				// }
				newQuoteItemList.add(newQ);
			}
		}
		returnQ.setQuoteItems(newQuoteItemList);
		return returnQ;

	}

	public void fillDispalyQuoteWithDraftQuoteInDB(Quote disPlayQuote, Quote quote) {
		if (quote.getSales() != null) {
			disPlayQuote.setSales(quote.getSales());
			//Fixed defect#103 by DamonChen@20181211
			if (!this.isSalesRole()) {
				disPlayQuote.setTeam(quote.getSales().getTeam());
			}
		}
		try {
			if (quote.getSoldToCustomer() != null) {
				disPlayQuote.setSoldToCustomer((Customer)quote.getSoldToCustomer().deepClone());
			}
			if (quote.getEndCustomer() != null) {
				disPlayQuote.setEndCustomer((Customer)quote.getEndCustomer().deepClone());
			}
		} catch (Exception e) {
			 LOG.severe("Customer.deepClone() ERROR.\n" + e.getStackTrace());
		}
		if (quote.getRemarks() != null) {
			disPlayQuote.setRemarks(quote.getRemarks());
		}
		List<QuoteItem> newQuoteItemList = new ArrayList<QuoteItem>();
		List<QuoteItem> quoteItemList = quote.getQuoteItems();
		if (quoteItemList != null && quoteItemList.size() > 0) {
			for (QuoteItem item : quoteItemList) {
				QuoteItem newQ = QuoteItem.newInstance(item);
				
				
				newQ.setId(item.getId());
				newQ.setPricerId(item.getPricerId());
				newQ.setMinSellPrice(item.getMinSellPrice());
				newQ.setFinalQuotationPrice(item.getFinalQuotationPrice());
				newQ.setEndCustomer(item.getEndCustomer());
				newQ.setSoldToCustomer(item.getSoldToCustomer());
				newQ.setEndCustomer(item.getEndCustomer());
				
				newQ.setRequestedMfr(item.getRequestedMfr());
				newQ.setRequestedPartNumber(item.getRequestedPartNumber());
				newQ.setRequestedMaterialForProgram(item.getQuotedMaterial());
				newQ.setQuotedMfr(item.getRequestedMfr());
				newQ.setQuotedPartNumber(item.getRequestedPartNumber());
				newQ.setQuotedMaterial(item.getQuotedMaterial());
				newQ.setMoq(item.getMoq());
				newQ.setMpq(item.getMpq());
				if (item.getRequestedQty() != null) {
					newQ.setRequestedQty(item.getRequestedQty());
				}
				if (item.getTargetResale() != null) {
					newQ.setTargetResale(item.getTargetResale());
				}
				newQ.setRemarks(item.getRemarks());
				this.handleQuoItemForDisplay(Arrays.asList(newQ));
				newQ.setQuote(disPlayQuote);
				newQuoteItemList.add(newQ);
			}
		}
		disPlayQuote.setQuoteItems(newQuoteItemList);
	}

	private Customer getEmptyCustomer() {
		Customer cust = new Customer();
		cust.setCustomerNumber(EMPTY_STR);
		cust.setCustomerName1(EMPTY_STR);
		return cust;
	}

	// Delinkage change on Oct 29 , 2013 by Tonmy
	public boolean quoteEquals(Object obj1, Object obj2) {
		if (obj1 == obj2)
			return true;
		if (obj2 == null)
			return false;
		if (obj1.getClass() != obj2.getClass())
			return false;

		Quote origial = (Quote) obj1;
		Quote other = (Quote) obj2;
		if (origial.getBizUnit() == null) {
			if (other.getBizUnit() != null) {
				return false;
			}

		} else if (!origial.getBizUnit().equals(other.getBizUnit())) {
			return false;
		}

		if (origial.getQuoteItems() == null) {
			if (other.getQuoteItems() != null && other.getQuoteItems().size() != 0) {
				return false;
			}
		} else {
			if (other.getQuoteItems() == null && origial.getQuoteItems().size() != 0)
				return false;

			if (origial.getQuoteItems().size() != other.getQuoteItems().size())
				return false;

			for (int i = 0; i < origial.getQuoteItems().size(); i++) {

				origial.setQuoteItems(sortedQuoteItem(origial.getQuoteItems()));
				other.setQuoteItems(sortedQuoteItem(other.getQuoteItems()));
				if (!quoteItemEquals(origial.getQuoteItems().get(i), other.getQuoteItems().get(i))) {
					return false;
				}
			}
		}

		if (StringUtils.isEmpty(origial.getRemarks())) {
			if (!StringUtils.isEmpty(other.getRemarks()))
				return false;
		} else if (!origial.getRemarks().equals(other.getRemarks()))
			return false;

		if (origial.getSoldToCustomer() == null) {
			if (other.getSoldToCustomer() != null)
				return false;
		} else if (!customerEquals(origial.getSoldToCustomer(), other.getSoldToCustomer())) {
			return false;
		}

		if (origial.getSales() == null) {
			if (other.getSales() != null)
				return false;
		} else if (!origial.getSales().equals(other.getSales())) {
			return false;
		}

		return true;
	}

	// Delinkage change on Oct 29 , 2013 by Tonmy
	public List<QuoteItem> sortedQuoteItem(List<QuoteItem> qiList) {
		List<QuoteItem> sorted = new ArrayList<QuoteItem>(qiList);
		Collections.sort(sorted, new Comparator() {
			@Override
			public int compare(Object o1, Object o2) {
				// long fisrtOne =
				// ((QuoteItem)o1).getRequestedMaterial().getProgramMaterial().getId();
				// long secondOne =
				// ((QuoteItem)o2).getRequestedMaterial().getProgramMaterial().getId();
				// PROGRM PRICER ENHANCEMENT
				// long fisrtOne =
				// ((QuoteItem)o1).getRequestedMaterialForProgram().getProgramMaterial().getId();
				// long secondOne =
				// ((QuoteItem)o2).getRequestedMaterialForProgram().getProgramMaterial().getId();
				long fisrtOne = ((QuoteItem) o1).getRequestedProgramMaterialForProgram().getId();
				long secondOne = ((QuoteItem) o2).getRequestedProgramMaterialForProgram().getId();
				int sortByAgeFlag = Long.valueOf(fisrtOne).compareTo(Long.valueOf(secondOne));
				return sortByAgeFlag;
			}
		});
		return sorted;
	}

	// Delinkage change on Oct 29 , 2013 by Tonmy
	public boolean quoteItemEquals(QuoteItem q1, QuoteItem q2) {
		if (q1 == q2)
			return true;
		if (q1.getClass() != q2.getClass())
			return false;

		// if (attachments == null || other.attachments.size()==0) {
		// if (other.attachments != null && other.attachments.size()>0)
		// return false;
		// }
		// else
		// {
		// if (attachments.size()!=other.attachments.size())
		// return false;
		// }

		if (q1.getMoq() == null) {
			if (q2.getMoq() != null)
				return false;
		} else if (Integer.compare(q1.getMoq(), q2.getMoq()) != 0) {
			return false;
		}

		if (q1.getMpq() == null) {
			if (q2.getMpq() != null)
				return false;
		} else if (Integer.compare(q1.getMpq(), q2.getMpq()) != 0) {
			return false;
		}

		// Delinkage change on Oct 29 , 2013 by Tonmy
		if (q1.getRequestedMfr() == null) {
			if (q2.getRequestedMfr() != null)
				return false;
		} else if (q1.getRequestedMfr().getId() != q2.getRequestedMfr().getId()) {
			return false;
		}
		if (q1.getRequestedPartNumber() == null) {
			if (q2.getRequestedPartNumber() != null)
				return false;
		} else if (!q1.getRequestedPartNumber().equalsIgnoreCase(q2.getRequestedPartNumber())) {
			return false;
		}

		if (q1.getRequestedQty() == null) {
			if (q2.getRequestedQty() != null)
				return false;
		} else if (Integer.compare(q1.getRequestedQty(), q2.getRequestedQty()) != 0) {
			return false;
		}

		if (q1.getTargetResale() == null) {
			if (q2.getTargetResale() != null)
				return false;
		} else if (Double.compare(q1.getTargetResale(), q2.getTargetResale()) != 0) {
			return false;
		}

		if (StringUtils.isEmpty(q1.getRemarks())) {
			if (!StringUtils.isEmpty(q2.getRemarks()))
				return false;
		} else if (!q1.getRemarks().equals(q2.getRemarks())) {
			return false;
		}

		return true;
	}

	public boolean customerEquals(Customer c1, Customer c2) {

		if (c1.getClass() != c2.getClass())
			return false;

		if (StringUtils.isEmpty(c1.getCustomerName1())) {
			if (!StringUtils.isEmpty(c2.getCustomerName1()))
				return false;
		} else if (!c1.getCustomerName1().equalsIgnoreCase(c2.getCustomerName1())) {
			return false;
		}

		if (StringUtils.isEmpty(c1.getCustomerNumber())) {
			if (!StringUtils.isEmpty(c2.getCustomerNumber()))
				return false;
		} else if (!c1.getCustomerNumber().equalsIgnoreCase(c2.getCustomerNumber())) {
			return false;
		}

		return true;
	}

	
	public List<String> autoCompleteSalesNumber(String key) {

		if (key != null && !key.isEmpty())
			key = key.trim();
		return  ejbCommonSB.autoCompleteSalesNumber(key, bizUnit);
		/*List<User> users = null;
		if (user != null && user.getRoles() != null) {
			for (Role r : user.getRoles()) {
				if (r.getName().equals(QuoteSBConstant.ROLE_CS)
						|| QuoteSBConstant.ROLE_CS_MANAGER.equals(r.getName())) {
					users = userSB.wFindAllSalesForCs(null, key, user, false);
					if (user != null && users.size() > 0) {
						return formatAutoCompleteUserCode(users);
					}
				} else if (r.getName().equals(QuoteSBConstant.ROLE_INSIDE_SALES)) {
					// issue 1192
					users = userSB.wFindAllSalesForCsForInsideSales(null, key, user);
					if (user != null && users.size() > 0) {
						return formatAutoCompleteUserCode(users);
					}
				} else if (r.getName().equals(QuoteSBConstant.ROLE_QC_OPERATION)) {
					users = userSB.wFindAllSalesByBizUnit(null, key,
							new String[] { QuoteSBConstant.ROLE_SALES, QuoteSBConstant.ROLE_INSIDE_SALES,
									QuoteSBConstant.ROLE_SALES_MANAGER, QuoteSBConstant.ROLE_SALES_DIRECTOR,
									QuoteSBConstant.ROLE_SALES_GM },
							bizUnit);
					if (user != null && users.size() > 0) {
						return formatAutoCompleteUserCode(users);
					}
				}
			}
		}

		return Collections.emptyList();*/
	}

	public List<String> autoCompleteSalesName(String key) {

		// logger.log(Level.INFO,
		// "PERFORMANCE START - autoCompleteSalesName()");
		if (key != null && !key.isEmpty())
			key = key.trim();

		return ejbCommonSB.updateFormatAutoCompleteUserName(key, this.bizUnit, this.userSB);
	}

	public List<String> autoCompleteEndCustomerNumberLabelList(String key) {

		if (this.salesBizUnit == null) {
			this.quote.setEndCustomer(this.getEmptyCustomer());
			FacesUtil.showWarnMessage(ResourceMB.getText("wq.message.salesmanEmpty"), "growl_submit");
			return  Collections.emptyList();
		}
		if (key != null && !key.isEmpty())
			key = key.trim();
		String soldToNumber = "";
		if (this.quote.getSoldToCustomer() != null) {
			soldToNumber = this.quote.getSoldToCustomer().getCustomerNumber();
		}
		List<Customer> customers =  getEndCustomers(key,soldToNumber);
		List<String> outcome = autoCompleteCustomerNumberLabelList(customers, key);
		
		return outcome;
	}
	public void setCurFocusIndex(String key) {
		LOG.info("setCurFocusIndex ::" + key);
	}
	public List<String> autoEndCustomerNumberLabelListForItem(String key) {

		//
		FacesContext fc = FacesContext.getCurrentInstance();
	    Map<String,String> params = 
	         fc.getExternalContext().getRequestParameterMap();

		String cruIndex = params.get("rowIndex");
		final DataTable d = (DataTable) FacesContext.getCurrentInstance().getViewRoot()
				.findComponent(":form_submit:submissionTable");
		int rowIndex =d.getRowIndex();
		//int rowIndex = Integer.valueOf(cruIndex);
		LOG.info(key);
		//LOG.info(key);
		if (this.salesBizUnit == null) {
			this.quote.setEndCustomer(this.getEmptyCustomer());
			FacesUtil.showWarnMessage(ResourceMB.getText("wq.message.salesmanEmpty"), "growl_submit");
			return  Collections.emptyList();
		}
		if (key != null && !key.isEmpty())
			key = key.trim();
		String soldToNumber = "";
		QuoteItem item = null;
		try {
			item = this.quote.getQuoteItems().get(rowIndex);
		} catch (Exception ex) {
			this.showMessage(FacesMessage.SEVERITY_ERROR,
					ResourceMB.getParameterizedText("wq.message.noFoundQuoteItems", String.valueOf(rowIndex + 1)));
			return  Collections.emptyList();
		}
		if (item != null) {
			soldToNumber = item.getSoldToCustomer().getCustomerNumber();
		}
		List<Customer> customers =  getEndCustomers(key,soldToNumber);
		List<String> outcome = autoCompleteCustomerNumberLabelList(customers, key);
		
		return outcome;
	}
	 
	private List<String> autoCompleteCustomerNumberLabelList(List<Customer> customers, String key) {
		LinkedList<String> list = new LinkedList<String>();
		List<String> dupList = new ArrayList<String>();
		
		for (Customer customer : customers) {
			String label = "";
			label += customer.getCustomerNumber();
			label += QuoteConstant.AUTOCOMPLETE_SEPARATOR;
			label += getCustomerFullName(customer);
			label += QuoteConstant.AUTOCOMPLETE_SEPARATOR;
			label += convertCustomerSalesToSalesOrg(customer.getCustomerSales(), getSalesBizUnit().getName());
			if (!dupList.contains(label)) {
				if(key.equals(customer.getCustomerNumber())){
					list.addFirst(label);
				}else {
					list.add(label);
				}
				dupList.add(label);
			}
		}
		return list;
	}
	
	public void updateSalesName() {

		quote.getSales().setName(extractSalesNameFromSalesNumberLabel(quote.getSales().getEmployeeId()));
		quote.getSales().setEmployeeId(extractSalesNumberFromSalesNumberLabel(quote.getSales().getEmployeeId()));
		// User user =
		// userSB.findByEmployeeIdWithAllRelation(quote.getSales().getEmployeeId());
		// if (user != null && user.getTeam() != null)
		// {
		// rfqHeader.setTeam(user.getTeam().getName());
		// }
		updateSalesman();
		this.setNeedSave(true);

	}
	
	
	public Double getDisplayCurrMinSell(QuoteItem item){
		
		return Money.convertAsToCurrAmtWithExrates(item.getMinSellPrice(), item.getExRateBuy(), item.getExRateRfq(), item.getHandling(), true);
/*		return	Money.convertAsToCurrAmt(item.getMinSellPrice(), item.getBuyCurr(), item.getRfqCurr(),
				item.getQuote().getBizUnit(), item.getSoldToCustomer(), new Date(), item.getRfqCurr());*/
		
	}

	public void updateSalesNumber() {
		// logger.log(Level.INFO, "PERFORMANCE START - updateSalesNumber()");
		quote.getSales().setEmployeeId(extractSalesNumberFromSalesNameLabel(quote.getSales().getName()));
		quote.getSales().setName(extractSalesNameFromSalesNameLabel(quote.getSales().getName()));
		// User user =
		// userSB.findByEmployeeIdWithAllRelation(quote.getSales().getName());
		// if (user != null && user.getTeam() != null)
		// {
		// rfqHeader.setTeam(user.getTeam().getName());
		// }
		// logger.log(Level.INFO, "PERFORMANCE END - updateSalesNumber()");
		updateSalesman();
	}

	public List<String> formatAutoCompleteUserCode(List<User> users) {
		List<String> list = new ArrayList<String>();
		for (User user : users) {
			String label = "";
			label += user.getEmployeeId();
			label += QuoteConstant.AUTOCOMPLETE_SEPARATOR;
			label += user.getName();
			list.add(label);
		}
		return list;
	}

	public List<String> formatAutoCompleteUserName(List<User> users) {
		List<String> list = new ArrayList<String>();
		for (User user : users) {
			String label = "";
			label += user.getName();
			label += QuoteConstant.AUTOCOMPLETE_SEPARATOR;
			label += user.getEmployeeId();
			list.add(label);
		}
		return list;
	}

	public String extractSalesNumberFromSalesNumberLabel(String label) {
		String[] labels = label.split(QuoteConstant.AUTOCOMPLETE_SEPARATOR);
		return labels[0].trim();
	}

	public String extractSalesNameFromSalesNumberLabel(String label) {
		String[] labels = label.split(QuoteConstant.AUTOCOMPLETE_SEPARATOR);
		if (labels.length == 3)
			return labels[1].trim() + ", " + labels[2].trim();
		return labels[1].trim();
	}

	public String extractSalesNumberFromSalesNameLabel(String label) {
		String[] labels = label.split(QuoteConstant.AUTOCOMPLETE_SEPARATOR);
		if (labels.length == 3)
			return labels[2].trim();
		return labels[1].trim();
	}

	public String extractSalesNameFromSalesNameLabel(String label) {
		String[] labels = label.split(QuoteConstant.AUTOCOMPLETE_SEPARATOR);
		if (labels.length == 3)
			return labels[0].trim() + ", " + labels[1].trim();
		return labels[0].trim();
	}

	public boolean isSalesmanListForInsideSalesDisplay() {
		return salesmanListForInsideSalesDisplay;
	}

	public void setSalesmanListForInsideSalesDisplay(boolean salesmanListForInsideSalesDisplay) {
		this.salesmanListForInsideSalesDisplay = salesmanListForInsideSalesDisplay;
	}

	public String retrieveCustomerChineseName(Customer customer) {

		List<CustomerAddress> customerAddresses = customer.getCustomerAddresss();
		for (CustomerAddress customerAddress : customerAddresses) {
			if (customerAddress.getCountry() != null && customerAddress.getId().getLanguageCode() != null
					&& (customerAddress.getId().getLanguageCode().equals(QuoteSBConstant.LANGUAGE_CODE_CHINESE_C)
							|| customerAddress.getId().getLanguageCode().equals(QuoteSBConstant.LANGUAGE_CODE_CHINESE_M)
							|| customerAddress.getId().getLanguageCode()
									.equals(QuoteSBConstant.LANGUAGE_CODE_CHINESE_1))) {
				return getCustomerFullName(customerAddress);
			}
		}
		return null;
	}

	public SelectItem[] getExCurrencyList() {
		return exCurrencyList;
	}

	public void setExCurrencyList(SelectItem[] exCurrencyList) {
		this.exCurrencyList = exCurrencyList;
	}

	public String getRfqCurr() {
		return rfqCurr;
	}

	public void setRfqCurr(String exCurrency) {
		this.rfqCurr = exCurrency;
	}

	public boolean isCreateProspectiveCustomerButtonDisplay() {
		return createProspectiveCustomerButtonDisplay;
	}

	public void setCreateProspectiveCustomerButtonDisplay(boolean createProspectiveCustomerButtonDisplay) {
		this.createProspectiveCustomerButtonDisplay = createProspectiveCustomerButtonDisplay;
	}

	/**
	 * @param newQ
	 * @param tempPm
	 */
	private void progRFQQuote(QuoteItem newQ, ProgramPricer tempPm) {
		// Delinkage change on Oct 29 , 2013 by Tonmy
		// newQ.setRequestedMaterial(tempPm.getMaterial());
		newQ.setRequestedMfr(tempPm.getMaterial().getManufacturer());
		newQ.setRequestedPartNumber(tempPm.getMaterial().getFullMfrPartNumber());
		newQ.setRequestedMaterialForProgram(getMaterial(newQ));
		// PROGRM PRICER ENHANCEMENT
		newQ.setRequestedProgramMaterialForProgram(tempPm);
		if (tempPm.getCostIndicator() != null) {
			newQ.setCostIndicator(tempPm.getCostIndicator().getName());
		}

		newQ.setQuotedMfr(tempPm.getMaterial().getManufacturer());
		newQ.setQuotedPartNumber(tempPm.getMaterial().getFullMfrPartNumber());
		newQ.setQuotedMaterial(getMaterial(newQ));

		newQ.setMoq(tempPm.getMoq());
		newQ.setMpq(tempPm.getMpq());
	}

	public SelectItem[] getRegionList() {
		return regionList;
	}

	public void setRegionList(SelectItem[] regionList) {
		this.regionList = regionList;
	}

	public String getRegion() {
		return region;
	}

	public void setRegion(String region) {
		this.region = region;
	}

	public String getShoppingCartInfo() {
		return shoppingCartInfo;
	}

	public void setShoppingCartInfo(String shoppingCartInfo) {
		this.shoppingCartInfo = shoppingCartInfo;
	}

	public List<QuoteItem> getSelectedSubmitResultItems() {
		return selectedSubmitResultItems;
	}

	public void setSelectedSubmitResultItems(List<QuoteItem> selectedSubmitResultItems) {
		this.selectedSubmitResultItems = selectedSubmitResultItems;
	}

	// progRfqSubmitMB
	// public

	public class SubmitResultInfo {
		private int asAq;
		private int asPending;
		private int asDraft;
		private int total;

		public int getAsAq() {
			return asAq;
		}

		public void setAsAq(int asAq) {
			this.asAq = asAq;
		}

		public int getAsPending() {
			return asPending;
		}

		public void setAsPending(int asPending) {
			this.asPending = asPending;
		}

		public int getAsDraft() {
			return asDraft;
		}

		public void setAsDraft(int asDraft) {
			this.asDraft = asDraft;
		}

		public int getTotal() {
			return total;
		}

		public void setTotal(int total) {
			this.total = total;
		}

	}

	public SubmitResultInfo getSubmitResultInfo() {
		return submitResultInfo;
	}

	public void setSubmitResultInfo(SubmitResultInfo submitResultInfo) {
		this.submitResultInfo = submitResultInfo;
	}

	public boolean isSalesRole() {
		return salesRole;
	}

	public void setSalesRole(boolean salesRole) {
		this.salesRole = salesRole;
	}

	public Integer getSelectedRowIndex() {
		return selectedRowIndex;
	}

	public void setSelectedRowIndex(Integer selectedRowIndex) {
		this.selectedRowIndex = selectedRowIndex;
	}

	public boolean isNeedSave() {
		return needSave;
	}

	public void setNeedSave(boolean needSave) {
		if(needSave!=this.needSave){
			FacesUtil.updateUI("form_submit:saveCart");
		}
		this.needSave = needSave;
	}
	public String getSystemInfo() {
		return systemInfo;
	}

	public void setSystemInfo(String systemInfo) {
		this.systemInfo = systemInfo;
	}

	public String getNewCustomerTitle() {
		return newCustomerTitle;
	}

	public void setNewCustomerTitle(String newCustomerTitle) {
		this.newCustomerTitle = newCustomerTitle;
	}

	public String getNewProspectiveCustomerType() {
		return newProspectiveCustomerType;
	}

	public void setNewProspectiveCustomerType(String newProspectiveCustomerType) {
		this.newProspectiveCustomerType = newProspectiveCustomerType;
	}

	public boolean isQco() {
		return qco;
	}

	public void setQco(boolean qco) {
		this.qco = qco;
	}
	
	
	

	public List<QuantityBreakPrice> getOrderQties(QuoteItem quoteItem) {
		// TODO Auto-generated method stub
		List<QuantityBreakPrice> QuantityBreakPriceList =super.getOrderQties(quoteItem);
		if(QuantityBreakPriceList == null ||QuantityBreakPriceList.size()==0){
			return new ArrayList<QuantityBreakPrice>(); 
		}
		
		Pricer pricer=materialSB.findPricerById(quoteItem.getPricerId());
		if(pricer ==null ||pricer.getCurrency()==null){
			return new ArrayList<QuantityBreakPrice>();
		}
		//for(QuantityBreakPrice p:QuantityBreakPriceList){
			Money.convertAsToCurrAmtWithExrates(QuantityBreakPriceList, pricer.getCurrency(), Currency.getByCurrencyName(rfqCurr), bizUnit, quoteItem.getSoldToCustomer(), new Date()
					, Currency.getByCurrencyName(rfqCurr));
		//}
		return QuantityBreakPriceList;
	/*	QtyBreakPricer QtyBreakPricer = getQBreakPriceByPricerId(quoteItem.getPricerId());
		if (QtyBreakPricer != null) {
			return QtyBreakPricer.calQtyBreakRange();
		}else {
			return new ArrayList<QuantityBreakPrice>();
		}*/
	}
}
