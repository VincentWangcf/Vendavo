package com.avnet.emasia.webquote.web.quote;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.math.BigDecimal;
import java.text.ParseException;
import com.avnet.emasia.webquote.utilities.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.SessionScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.event.AjaxBehaviorEvent;
import javax.faces.model.SelectItem;

import org.apache.cxf.common.util.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.jboss.ejb3.annotation.TransactionTimeout;
import org.primefaces.component.commandbutton.CommandButton;
import org.primefaces.context.RequestContext;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.event.FlowEvent;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;
import org.primefaces.model.UploadedFile;

import com.avnet.emasia.webquote.cache.DesignLocationCacheManager;
import com.avnet.emasia.webquote.cache.MfrDetailCacheManager;
import com.avnet.emasia.webquote.commodity.helper.ProgRfqSubmitHelper;
import com.avnet.emasia.webquote.component.show.config.ComponetConfigService;
import com.avnet.emasia.webquote.constants.ActionEnum;
import com.avnet.emasia.webquote.dp.EJBCommonSB;
import com.avnet.emasia.webquote.entity.Attachment;
import com.avnet.emasia.webquote.entity.AutoTransferPm;
import com.avnet.emasia.webquote.entity.BizUnit;
import com.avnet.emasia.webquote.entity.City;
import com.avnet.emasia.webquote.entity.ContractPricer;
import com.avnet.emasia.webquote.entity.Country;
import com.avnet.emasia.webquote.entity.Currency;
import com.avnet.emasia.webquote.entity.Customer;
import com.avnet.emasia.webquote.entity.CustomerAddress;
import com.avnet.emasia.webquote.entity.CustomerPartner;
import com.avnet.emasia.webquote.entity.CustomerSale;
import com.avnet.emasia.webquote.entity.CustomerSalePK;
import com.avnet.emasia.webquote.entity.DesignLocation;
import com.avnet.emasia.webquote.entity.DrNedaItem;
import com.avnet.emasia.webquote.entity.ExcCurrencyDefault;
import com.avnet.emasia.webquote.entity.Manufacturer;
import com.avnet.emasia.webquote.entity.ManufacturerDetail;
import com.avnet.emasia.webquote.entity.Material;
import com.avnet.emasia.webquote.entity.Money;
import com.avnet.emasia.webquote.entity.NormalPricer;
import com.avnet.emasia.webquote.entity.ProgramPricer;
import com.avnet.emasia.webquote.entity.QuantityBreakPrice;
import com.avnet.emasia.webquote.entity.Quote;
import com.avnet.emasia.webquote.entity.QuoteItem;
import com.avnet.emasia.webquote.entity.RFQSubmissionTypeEnum;
import com.avnet.emasia.webquote.entity.RestrictedCustomerMapping;
import com.avnet.emasia.webquote.entity.Role;
import com.avnet.emasia.webquote.entity.SalesOrg;
import com.avnet.emasia.webquote.entity.User;
import com.avnet.emasia.webquote.exception.AppException;
import com.avnet.emasia.webquote.exception.WebQuoteException;
import com.avnet.emasia.webquote.quote.ejb.AutoTransferPmSB;
import com.avnet.emasia.webquote.quote.ejb.CitySB;
import com.avnet.emasia.webquote.quote.ejb.CountrySB;
import com.avnet.emasia.webquote.quote.ejb.CustomerSB;
import com.avnet.emasia.webquote.quote.ejb.DesignLocationSB;
import com.avnet.emasia.webquote.quote.ejb.DrmsSB;
import com.avnet.emasia.webquote.quote.ejb.ExchangeRateSB;
import com.avnet.emasia.webquote.quote.ejb.ManufacturerSB;
import com.avnet.emasia.webquote.quote.ejb.MyQuoteSearchSB;
import com.avnet.emasia.webquote.quote.ejb.QuoteSB;
import com.avnet.emasia.webquote.quote.ejb.RestrictedCustomerMappingSB;
import com.avnet.emasia.webquote.quote.ejb.RfqItemValidatorSB;
import com.avnet.emasia.webquote.quote.ejb.SAPWebServiceSB;
import com.avnet.emasia.webquote.quote.ejb.SystemCodeMaintenanceSB;
import com.avnet.emasia.webquote.quote.ejb.constant.QuoteSBConstant;
import com.avnet.emasia.webquote.quote.vo.CatalogSearchResult;
import com.avnet.emasia.webquote.quote.vo.MyQuoteSearchCriteria;
import com.avnet.emasia.webquote.quote.vo.RestrictedCustomerMappingSearchCriteria;
import com.avnet.emasia.webquote.strategy.StrategyFactory;
import com.avnet.emasia.webquote.user.ejb.ApplicationSB;
import com.avnet.emasia.webquote.user.ejb.BizUnitSB;
import com.avnet.emasia.webquote.user.ejb.RoleSB;
import com.avnet.emasia.webquote.user.ejb.TeamSB;
import com.avnet.emasia.webquote.user.ejb.UserSB;
import com.avnet.emasia.webquote.utilites.resources.ResourceMB;
import com.avnet.emasia.webquote.utilites.web.common.CacheUtil;
import com.avnet.emasia.webquote.utilites.web.common.Constants;
import com.avnet.emasia.webquote.utilites.web.util.DeploymentConfiguration;
import com.avnet.emasia.webquote.utilites.web.util.DownloadUtil;
import com.avnet.emasia.webquote.utilites.web.util.QuoteUtil;
import com.avnet.emasia.webquote.utilities.DateUtils;
import com.avnet.emasia.webquote.utilities.MessageFormatorUtil;
import com.avnet.emasia.webquote.utilities.bean.MailInfoBean;
import com.avnet.emasia.webquote.utilities.common.OSTimeZone;
import com.avnet.emasia.webquote.utilities.util.BeanUtilsExtends;
import com.avnet.emasia.webquote.vo.DrProjectVO;
import com.avnet.emasia.webquote.vo.PricerInfo;
import com.avnet.emasia.webquote.vo.RfqHeaderVO;
import com.avnet.emasia.webquote.vo.RfqItemVO;
import com.avnet.emasia.webquote.web.quote.cache.ApplicationCacheManager;
import com.avnet.emasia.webquote.web.quote.cache.BizUnitCacheManager;
import com.avnet.emasia.webquote.web.quote.cache.CountryCacheManager;
import com.avnet.emasia.webquote.web.quote.cache.CurrencyCacheManager;
import com.avnet.emasia.webquote.web.quote.cache.CustomerCacheManager;
import com.avnet.emasia.webquote.web.quote.cache.DesignInCatCacheManager;
//import com.avnet.emasia.webquote.web.quote.cache.DesignLocationCacheManager;
import com.avnet.emasia.webquote.web.quote.cache.EnquirySegmentCacheManager;
import com.avnet.emasia.webquote.web.quote.cache.MfrCacheManager;
import com.avnet.emasia.webquote.web.quote.cache.ProjectNameCacheManager;
import com.avnet.emasia.webquote.web.quote.cache.QuoteTypeCacheManager;
import com.avnet.emasia.webquote.web.quote.cache.RoleCacheManager;
import com.avnet.emasia.webquote.web.quote.cache.SalesOrgCacheManager;
import com.avnet.emasia.webquote.web.quote.cache.ValidationRuleCacheManager;
import com.avnet.emasia.webquote.web.quote.constant.QuoteConstant;
import com.avnet.emasia.webquote.web.quote.strategy.SendQuotationEmailStrategy;
import com.avnet.emasia.webquote.web.quote.vo.PartModel;
import com.avnet.emasia.webquote.web.quote.vo.QuotationEmailVO;
import com.avnet.emasia.webquote.web.quote.vo.ValidationRuleVO;
import com.avnet.emasia.webquote.web.quote.vo.WorkingPlatformEmailVO;
import com.avnet.emasia.webquote.web.user.UserInfo;
//import com.sap.document.sap.soap.functions.mc_style.ZwqCustomer;
import com.avnet.emasia.webquote.webservice.customer.ZwqCustomer;

@ManagedBean
@SessionScoped
public class RfqSubmissionMB extends CommonBean implements Serializable {

	private static final long serialVersionUID = -7191069325520480655L;

	static final Logger LOG = Logger.getLogger(RfqSubmissionMB.class.getSimpleName());

	//Bryan Start
	@EJB
	private CacheUtil cacheUtil;
	//Bryan End
	
	@EJB
	private UserSB userSB;

	@EJB
	private QuoteSB quoteSB;

	@EJB
	private CustomerSB customerSB;

	@EJB
	private ExchangeRateSB exChangeRateSB;

	@EJB
	private BizUnitSB bizUnitSB;

	@EJB
	private AutoTransferPmSB autoTransferPmSB;

	@EJB
	private DrmsSB drmsSB;

	@EJB
	private TeamSB teamSB;

	@EJB
	private RoleSB roleSB;

	@EJB
	private SAPWebServiceSB sapWebServiceSB;

	@EJB
	private ManufacturerSB manufacturerSB;

	@EJB
	private CitySB citySB;

	@EJB
	private CountrySB countrySB;

	@EJB
	private ApplicationSB applicationSB;

	@EJB
	private MyQuoteSearchSB myQuoteSearchSB;

	@EJB
	private RestrictedCustomerMappingSB restrictedCustomerSB;

	// INC0023876
	@EJB
	private SystemCodeMaintenanceSB sysMaintSB;

	@EJB
	protected EJBCommonSB ejbCommonSB;

	@EJB
	private RfqItemValidatorSB rfqItemValidatorSB;
	// added by damon
	@EJB
	private DesignLocationSB designLocationSB;
	@ManagedProperty("#{catalogSearchMB}")
	CatalogSearchMB catalogSearchMB;

/*	@ManagedProperty("#{partSearchLogicMB}")
	PartSearchLogicMB partSearchLogicMB;*/
	private boolean updateDataTable = false;

	private transient StreamedContent rfqTemplate;

	private boolean itemSelect = false;
	private boolean deleteCustomer = false;
	private boolean isInsideSales = false;
	private boolean isSales = false;
	private boolean isQco = false;
	private boolean isQcp = false;
	private boolean isShowDLink=false;
//	public  String showDuplicatedItemFlag;

	private RfqHeaderVO rfqHeader;
	private String requestFormNumber;
	private String requestQuoteId;
	private String requestQuoteItemId;
	private String requestAction;

	private Quote quote;
	private Quote resultQuote;
	private List<QuoteItem> resultQuoteItems;

	private List<RfqItemVO> filteredRfqItems;
	private List<RfqItemVO> rfqItems;

	private List<RfqItemVO> duplicatedrfqItems = new ArrayList<RfqItemVO>();
	private RfqItemVO selectedRfqItem;

	private boolean newPart = false;

	private List<PricerInfo> requiredPartNumberList;
	private List<PricerInfo> selectedRequiredPartNumberList;

	private List<Customer> searchCustomers;
	private List<Customer> selectedSearchCustomers;
	private Customer selectedSearchCustomer;

	private List<DrProjectVO> drProjects;
	private DrProjectVO selectedDrProject;
	private List<DrProjectVO> filteredDrProjects;
	private List<DrProjectVO> restOfDrProjects;

	private List<User> csList;
	private List<User> selectedCsList;

	private SelectItem[] bizUnitSelectList;
	private SelectItem[] csSelectList;
	private SelectItem[] customerTypeSearchSelectList;
	private SelectItem[] customerTypeSelectList;
	private SelectItem[] projectNameSelectList;
	private SelectItem[] designLocationSelectList;
	private SelectItem[] designRegionSelectList;
	private SelectItem[] applicationSelectList;
	private SelectItem[] enquirySegmentSelectList;
	private SelectItem[] teamSelectList;
	private SelectItem[] endCustomerSelectList;
	private SelectItem[] soldToCustomerNumberSelectList;
	private SelectItem[] shipToCustomerNumberSelectList;
	private SelectItem[] endCustomerNumberSelectList;
	private SelectItem[] endCustomerNameSelectList;
	private SelectItem[] currencySelectList;
	private SelectItem[] countrySelectList;
	private SelectItem[] citySelectList;
	private SelectItem[] salesOrgSelectList;
	private SelectItem[] ppScheduleSelectList;
	private SelectItem[] mpScheduleSelectList;
	private SelectItem[] mfrSelectList;
	private SelectItem[] mfrSearchSelectList;
	private Map<String, SelectItem[]> sapPartNumberSelectedMap;

	// added by June for Project RMB cur 20140704
	private SelectItem[] exCurrencyList;
	private SelectItem[] endCustomerSearchRegionList;
	private SelectItem[] quoteTypeList;
	private SelectItem[] designInCatList;

	private String customerTypeSearch;
	private String customerNameSearch;

	private int itemNumberCount = 0;
	private String currentToggleMfr;
	private int itemNumberForPartSearch = 0;
	private int itemNumberForAttachment = 0;
	private int itemNumberForPopup = 0;
	private boolean drmsFound = false;

	private int searchCustomersCount = 0;

	private boolean soldToCodeSearch = true;
	private boolean shipToCodeSearch = false;
	private boolean endCustomerCodeSearch = false;

	private int soldToCodeItemSearch;
	private int shipToCodeItemSearch;
	private int endCustomerCodeItemSearch;

	// "E": end customer creation , "C" : customer creation
	private String newProspectiveCustomerType;
	private String newProspectiveCustomerName1;
	private String newProspectiveCustomerName2;
	private String newProspectiveCustomerCountry;
	private String newProspectiveCustomerCity;

	private String newProspectiveCustomerAddress3;
	private String newProspectiveCustomerAddress4;
	private String newProspectiveCustomerSalesOrg;
	private boolean newProspectiveCustomerDuplicated;

	private String newProspectiveCustomerError;

	private String duplicatedCustomerCode;
	private String duplicatedCustomerName;
	private String duplicatedCustomerType;
	private String duplicatedCountry;
	private String duplicatedCity;
	private String duplicatedSalesOrg;

	private String requestFailSoldToCode;
	private String requestcontainsDraft;

	private int toBeNewPartItemNumber;
	private String toBeNewPartNumber;
	private String toBeNewMfr;

	private User user;

	private transient UploadedFile formAttachment;
	private String formAttachmentName;
	private transient UploadedFile rfqExcel;
	private transient StreamedContent rfqSubmissionResultReport;
	private transient StreamedContent drmsResultReport;

	private int aqQuoteCount;
	private int qcQuoteCount;
	private int itQuoteCount;
	private int draftQuoteCount;

	private boolean clearMB = false;

	// Added by tonmy li.
	private boolean isSalesRole = false;
	private boolean isInsideSalesRole = false;
	private boolean isCSRole = false;
	private boolean isQCOperation = false;
	private boolean isQCPricing = false;

	// Added by Tonmy Li on 6 Sep 2013. allowed access customer of the user.
	private List<Customer> allowCustomers;

	private Set<Long> continueQuoteItems;

	// Added by Tonmy on 14 Oct 2013. fix 747.
	private int pendingDRMSCount = 0;
	private List<RfqItemVO> pendingDRMSRfqs = new ArrayList<RfqItemVO>();
	private final static double ZERO_DOUBLE = 0d;
	private final static int FOURTY = 40;

	private int removeAttIndex = 0;

	private static final Logger LOGGER = Logger.getLogger(RfqSubmissionMB.class.getName());

	private static final int ZERO = 0;

	private String endCustomerSearchType;
	private String endCustomerSearchRegion;
	//private List<RestrictedCustomerMapping> restrictedCustList;
	private String newCustomerTitle;

	private boolean foundErrorMsg = false;

	private transient XSSFWorkbook workbook;

	private String uploadFileName = null;

	private List<RfqItemVO> pendingBmtRfqs;

	private boolean selectedAllSprIt;
	private boolean selectedAllSprFlag;

	private boolean isValidateDrms = true;

	private boolean createProspectiveCustomerButtonDisplay = true;
	
	private boolean confirmGoNextFlag=false;

	@ManagedProperty(value = "#{resourceMB}")
	/** To Inject ResourceMB instance */
	private ResourceMB resourceMB;

	HashMap<String, RfqItemVO> tmpMap= new HashMap<String,RfqItemVO>();
	
	private String salemanFillMessage;
	public RfqSubmissionMB() {
		// logger.log(Level.INFO, "PERFORMANCE START - RfqSubmissionMB()");

		//initialMB();

		// logger.log(Level.INFO, "PERFORMANCE END - RfqSubmissionMB()");
	}

	public void initialMB() {

		// logger.log(Level.INFO, "PERFORMANCE START - initialMB()");
		continueQuoteItems = null;
		rfqHeader = new RfqHeaderVO();
		resetRfqSubmission();

		customerTypeSelectList = QuoteUtil.createFilterOptions(QuoteConstant.CUSTOMER_TYPE, QuoteConstant.CUSTOMER_TYPE,
				false, false);
		customerTypeSearchSelectList = QuoteUtil.createFilterOptions(QuoteConstant.CUSTOMER_TYPE, false, true);
		teamSelectList = QuoteUtil.createFilterOptions(QuoteConstant.TEAM);
		soldToCustomerNumberSelectList = QuoteUtil.createFilterOptions(new String[0], true, false);
		shipToCustomerNumberSelectList = QuoteUtil.createFilterOptions(new String[0], true, false);
		ppScheduleSelectList = QuoteUtil
				.createFilterOptions(QuoteUtil.generateLatest24Month(QuoteConstant.DATE_FORMAT_MP_PP_SCHEUDLE));
		mpScheduleSelectList = QuoteUtil
				.createFilterOptions(QuoteUtil.generateLatest24Month(QuoteConstant.DATE_FORMAT_MP_PP_SCHEUDLE));
		List<String> currencyCodes =null;
        if(user.getDefaultBizUnit() !=null){
        	currencyCodes= new ArrayList<String>(user.getDefaultBizUnit().getAllowCurrencies());
        }
        rfqHeader.setRfqCurr(user.getDefaultBizUnit().getDefaultCurrency().name());
		currencySelectList = QuoteUtil.createFilterOptions(currencyCodes.toArray(new String[currencyCodes.size()]));

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

		//Bryan Start
		//List<String> applicationCodes = ApplicationCacheManager.getApplicationList();
		List<String> applicationCodes = cacheUtil.getApplicationList();
		//Bryan End
		applicationSelectList = QuoteUtil
				.createFilterOptions(applicationCodes.toArray(new String[applicationCodes.size()]), true, false);

		//Bryan Start
		//List<String> enquirySegmentCodes = EnquirySegmentCacheManager.getEnquirySegmentList();
		List<String> enquirySegmentCodes = cacheUtil.getEnquirySegmentList();
		//Bryan End
		enquirySegmentSelectList = QuoteUtil
				.createFilterOptions(enquirySegmentCodes.toArray(new String[enquirySegmentCodes.size()]), false, false);

		List<String> projectNameCodes = ProjectNameCacheManager.getAllProjectNames();
		projectNameSelectList = QuoteUtil
				.createFilterOptions(projectNameCodes.toArray(new String[projectNameCodes.size()]), true, false);

		// added by Damon@20151224
		//Bryan Start
		//List<DesignLocation> designLocations = DesignLocationCacheManager.getDesignLocationList();
		List<DesignLocation> designLocations = cacheUtil.getDesignLocationList();
		//Bryan End
		List<String> designLocationCodes = new ArrayList();
		if (designLocations != null && designLocations.size() > 0) {
			for (DesignLocation d : designLocations) {
				if (!designLocationCodes.contains(d.getCode())) {
					designLocationCodes.add(d.getCode());
				}
			}
		} else {
			logInfo("the DesignLocationList get from DesignLocationCacheManager is null");
		}
		designLocationSelectList = QuoteUtil
				.createFilterOptions(designLocationCodes.toArray(new String[designLocationCodes.size()]), true, false);

		//Bryan Start
		//List<String> designRegions = DesignLocationCacheManager.getDesignRegionList();
		List<String> designRegions = cacheUtil.getDesignRegionList();
		//Bryan End
		designRegionSelectList = QuoteUtil.createFilterOptions(designRegions.toArray(new String[designRegions.size()]),
				false, false);

		//Bryan Start
		//List<String> bizUnitCodes = BizUnitCacheManager.getBizUnitList();
		List<String> bizUnitCodes = cacheUtil.getBizUnitList();
		//Bryan End
		endCustomerSearchRegionList = QuoteUtil
				.createFilterOptions(bizUnitCodes.toArray(new String[bizUnitCodes.size()]), false, false);
		// WebQuote 2.2 enhancement : fields changes.
		//Bryan Start
		//List<String> quoteTypes = QuoteTypeCacheManager.getQuoteTypeList();
		List<String> quoteTypes = cacheUtil.getQuoteTypeList();
		//Bryan End
		quoteTypeList = QuoteUtil.createFilterOptions(quoteTypes.toArray(new String[quoteTypes.size()]), false, false);
		//Bryan Start
		//List<String> designInCats = DesignInCatCacheManager.getDesignInCatList();
		List<String> designInCats = cacheUtil.getDesignInCatList();
		//Bryan End
		designInCatList = QuoteUtil.createFilterOptions(designInCats.toArray(new String[designInCats.size()]), false,
				false);

		actionForMyQuote();
		selectedSearchParts = new ArrayList<PricerInfo>();
		sapPartNumberSelectedMap = new HashMap<String, SelectItem[]>();
		// logger.log(Level.INFO, "PERFORMANCE END - initialMB()");
		
	}

	public void actionForMyQuote() {

		// logger.log(Level.INFO, "PERFORMANCE START - actionForMyQuote()");
		Map<String, String> parameterMap = (Map<String, String>) FacesContext.getCurrentInstance().getExternalContext()
				.getRequestParameterMap();
		String param = parameterMap.get("clear");
		if (param != null && "1".equals(param)) {
			clearMB = true;
		}
		if (clearMB) {
			resetMB();
		}
		if (requestQuoteId != null) {
			if (requestAction != null) {
				resetMB();
				resetRfqSubmission();
				if (requestAction.equals(QuoteConstant.ACTION_COPY_QUOTE_HEADER)) {
					copyQuoteHeader();
				} else if (requestAction.equals(QuoteConstant.ACTION_CONTINUE_RFQ)) {
					continueRfqSubmission(false);
				} else if (requestAction.equals(QuoteConstant.ACTION_RESUBMIT_INVALID_RFQ)) {
					resubmitInvalidRfq();
				} else if (requestAction.equals(QuoteConstant.ACTION_INSTANT_PRICE_CHECK)) {
					instantPriceChecking();
				}
				requestQuoteId = null;
				requestQuoteItemId = null;
				requestAction = null;
			}
		}

		// logger.log(Level.INFO, "PERFORMANCE END - actionForMyQuote()");
	}

	public void resetMBFlag() {
		clearMB = true;
	}

	public void resetMB() {

		logInfo( "PERFORMANCE START - resetMB()");
		continueQuoteItems = null;
		RfqSubmissionMB rfqMB = (RfqSubmissionMB) FacesContext.getCurrentInstance().getExternalContext().getSessionMap()
				.get("rfqSubmissionMB");
		if (rfqMB != null) {
			rfqMB.setRfqItems(new ArrayList<RfqItemVO>());
			rfqHeader = new RfqHeaderVO();

			resetAndPostConstructCommonLogic();
			// fix : 1238
			bizUnit = user.getDefaultBizUnit();

			setMfrSelectItems();
			updateSalesOrg();

			rfqHeader.setBizUnit(bizUnit.getName());
			rfqMB.setRfqHeader(rfqHeader);
			rfqMB.setQuote(null);

			updateExchangeCurrencyInfoByBu(bizUnit);

			FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("rfqSubmissionMB", rfqMB);
			resetRfqSubmission();
			resetTmpVar();
		}
		clearMB = false;
		// Fix issue 1125 by Tonmy Li on 11,Nov 2013
		resetPartSearchPanel();
		logInfo("PERFORMANCE END - resetMB()");
	}

	/**   
	* @Description: TODO
	* @author 042659      
	* @return void    
	* @throws  
	*/  
	private void resetTmpVar() {
		if(tmpMap == null){
			tmpMap=new HashMap<String,RfqItemVO>();
		}else{
			tmpMap.clear();
		}
		if(duplicatedrfqItems == null){
			duplicatedrfqItems=new ArrayList<RfqItemVO>();
		}else{
			duplicatedrfqItems.clear();
		}
		confirmGoNextFlag=false;
		
	}

	private void setMfrSelectItems() {
		//Bryan Start
		//List<Manufacturer> manufacturers = MfrCacheManager.getMfrListByBizUnit(bizUnit.getName());
		List<Manufacturer> manufacturers = cacheUtil.getMfrListByBizUnit(bizUnit.getName());
		//Bryan End
		List<String> mfrCodes = new ArrayList<String>();
		if (manufacturers != null) {
			for (Manufacturer manufacturer : manufacturers)
				mfrCodes.add(manufacturer.getName());
		}
		mfrSelectList = QuoteUtil.createFilterOptions(mfrCodes.toArray(new String[mfrCodes.size()]),
				mfrCodes.toArray(new String[mfrCodes.size()]), false, false);
		mfrSearchSelectList = QuoteUtil.createFilterOptions(mfrCodes.toArray(new String[mfrCodes.size()]),
				mfrCodes.toArray(new String[mfrCodes.size()]), false, true);
	}

	@PostConstruct
	private void postContruct() {

		user = UserInfo.getUser();
		//logInfo("PERFORMANCE begin - postContruct()");
		initialMB();
		// logger.log(Level.INFO, "PERFORMANCE START - postContruct()");

		bizUnit = user.getDefaultBizUnit();
		// Added by Tonmy Li on 6 Sep 2013.
		allowCustomers = userSB.findAllCustomers(user);

		isInsideSalesRole = applicationSB.isActionAccessibleByUser(user, "RfqSubmission.roleProspective.InsideSales");
		isSalesRole = applicationSB.isActionAccessibleByUser(user, "RfqSubmission.roleProspective.Sales");
		isCSRole = applicationSB.isActionAccessibleByUser(user, "RfqSubmission.roleProspective.CS");
		isQCOperation = applicationSB.isActionAccessibleByUser(user, "RfqSubmission.roleProspective.QCOperation");
		isQCPricing = applicationSB.isActionAccessibleByUser(user, "RfqSubmission.roleProspective.QCPricing");

		setMfrSelectItems();

		rfqHeader.setBizUnit(bizUnit.getName());
		logInfo("cacheUtil.getRole(QuoteSBConstant.ROLE_SALES) Begin");
		//Bryan Start
		//Role role = RoleCacheManager.getRole(QuoteSBConstant.ROLE_SALES);
		Role role = cacheUtil.getRole(QuoteSBConstant.ROLE_SALES);
		//Bryan End
		logInfo("cacheUtil.getRole(QuoteSBConstant.ROLE_SALES) END");
		resetAndPostConstructCommonLogic();

		updateCsForSales();

		updateExchangeCurrencyInfoByBu(bizUnit);

		// webquote japan not need to show create prospective customer button.
		// added by Lenon.Yang(043044) 2016/07/16
		this.createProspectiveCustomerButtonDisplay = applicationSB.isActionAccessibleByUser(user,
				QuoteConstant.RFQ_SUBMITTION_ALLOW_CREATE_PROSPECTIVE_CUSTOMER);
		String tmpStr=ResourceMB.getText("wq.message.salemanFill");
		salemanFillMessage=tmpStr==null?"":tmpStr;
		//logInfo("PERFORMANCE END - postContruct()");
		
		

		this.isShowDLink=ComponetConfigService.showByRegion("rfqSubmission", "dLink", Arrays.asList(bizUnit.getName()));

	}

	private void resetAndPostConstructCommonLogic() {
		// Added by Tonmy at 3 Sep . Enhanced the role checking .
		if (isSalesRole) {
			// logInfo("is Sales Role");
			isSales = true;
			rfqHeader.setSalesEmployeeNumber(user.getEmployeeId());
			rfqHeader.setSalesEmployeeName(user.getName());

			rfqHeader.setSalesBizUnit(user.getDefaultBizUnit());
			if (user.getTeam() != null)
				rfqHeader.setTeam(user.getTeam().getName());
		} else if (isInsideSalesRole) {
			logInfo("is Inside Sales Role");
			isInsideSales = true;
			
		} else if (isCSRole) {
			// logInfo("is CS Role");
			this.selectedCsList = new ArrayList<User>();
			this.selectedCsList.add(user);
			rfqHeader.setCopyToCs(user.getEmployeeId() + ";");
			rfqHeader.setCopyToCsName(user.getName() + ";");
		} else if (isQCOperation) {
			logInfo("is QC Operation");
			this.isQco = true;
		} else if (isQCPricing) {
			logInfo("is QC Pricing");
			this.isQcp = true;
		}
	}

	public void updateMfr(int rowIndex) {

		// logger.log(Level.INFO, "PERFORMANCE START - updateMfr()");

		this.rfqItems.get(rowIndex).setRequiredPartNumber(null);
		this.rfqItems.get(rowIndex).setMpq(null);
		this.rfqItems.get(rowIndex).setMoq(null);
		this.rfqItems.get(rowIndex).setLeadTime(null);

		// logger.log(Level.INFO, "PERFORMANCE END - updateMfr()");
	}

	public void resetRfqSubmission() {

		// logger.log(Level.INFO, "PERFORMANCE START - resetRfqSubmission()");

		itemNumberCount = 0;
		selectedAllSprIt=false;
		selectedAllSprFlag=false;
		rfqItems = new ArrayList<RfqItemVO>();
		createTenRfqDetail();
		this.formAttachmentName = null;

		// logger.log(Level.INFO, "PERFORMANCE END - resetRfqSubmission()");
	}

	public void resetRfqSubmissionForSwitch() {

		// logger.log(Level.INFO, "PERFORMANCE START - resetRfqSubmission()");

		itemNumberCount = 0;
		rfqItems = new ArrayList<RfqItemVO>();
		createTenRfqDetailWithoutFillHeader();
		this.formAttachmentName = null;

		// logger.log(Level.INFO, "PERFORMANCE END - resetRfqSubmission()");
	}

	public void instantPriceChecking() {

		logInfo("PERFORMANCE START - instantPriceChecking()");
		String seletedRegionFromInstantPriceCheck = catalogSearchMB.getRegion();
		if (seletedRegionFromInstantPriceCheck != null) {
			logInfo("Region selection from InstantPriceChecking: " + seletedRegionFromInstantPriceCheck);
			rfqHeader.setBizUnit(seletedRegionFromInstantPriceCheck);
			updateBizUnit();
		}

		rfqItems = new ArrayList<RfqItemVO>();
		itemNumberCount = 0;
		List<PricerInfo> results = catalogSearchMB.getSelectedResults();

		if (results != null) {
			rfqItems = new ArrayList<RfqItemVO>();
			for (PricerInfo catalogSearchResult : results) {
				logInfo("Data from InstantPriceChecking: " + catalogSearchResult.getMfr() + "/"
						+ catalogSearchResult.getFullMfrPartNumber() + "/" + itemNumberCount);
				RfqItemVO rfqItem = new RfqItemVO();
				rfqItem.setMfr(catalogSearchResult.getMfr());
				rfqItem.setRequiredPartNumber(catalogSearchResult.getFullMfrPartNumber());
				rfqItem.setMpq(catalogSearchResult.getMpq());
				rfqItem.setMoq(catalogSearchResult.getMoq());
				rfqItem.setLeadTime(catalogSearchResult.getLeadTime());
				rfqItem.setValidPartNumber(true);
				rfqItem.setStatusColor(1);
				if (catalogSearchResult.getRequestedQty() != null
						&& catalogSearchResult.getRequestedQty() != ZERO)
					rfqItem.setRequiredQty(String.valueOf(catalogSearchResult.getRequestedQty()));
				if (catalogSearchResult.getBottomPrice() != null
						&& catalogSearchResult.getBottomPrice() > ZERO_DOUBLE)
					rfqItem.setTargetResale(String.valueOf(catalogSearchResult.getBottomPrice()));
				rfqItem.setItemNumber(++itemNumberCount);

				// the sold to code in instant price checking does not bring to
				// RFQ Submission.
				rfqItem.setSoldToCustomerNumber(catalogSearchResult.getSoldToCustomerNumber());
				// rfqItem.setSoldToCustomerName(instantPriceCheckResult.getSoldToCustomerName());
				setCustomerInfomationByCode(rfqItem);

				rfqItems.add(rfqItem);
			}
		}
		logInfo("PERFORMANCE END - instantPriceChecking()");
	}

	public void copyQuoteHeader() {

		// logger.log(Level.INFO, "PERFORMANCE START - copyQuoteHeader()");

		resetRfqSubmission();
		if (requestQuoteId != null) {
			long id = Long.parseLong(requestQuoteId);
			Quote requestQuote = quoteSB.findQuoteByPK(id);
			if (requestQuote.getSales() != null) {
				rfqHeader.setSalesEmployeeNumber(requestQuote.getSales().getEmployeeId());
				rfqHeader.setSalesEmployeeName(requestQuote.getSales().getName());
				if (requestQuote.getSales().getTeam() != null)
					rfqHeader.setTeam(requestQuote.getSales().getTeam().getName());
			}
			// WebQuote 2.2 enhancement : fields changes.
			// rfqHeader.setBmtChecked(requestQuote.getBmtCheckedFlag());
			rfqHeader.setDesignInCat(requestQuote.getDesignInCat());
			rfqHeader.setEnquirySegment(requestQuote.getEnquirySegment());

			if (requestQuote.getSoldToCustomer() != null) {
				rfqHeader.setSoldToCustomerNumber(requestQuote.getSoldToCustomer().getCustomerNumber());
				rfqHeader.setSoldToCustomerName(getCustomerFullName(requestQuote.getSoldToCustomer()));
			}
			rfqHeader.setCustomerType(requestQuote.getCustomerType());

			if (requestQuote.getSoldToCustomerNameChinese() != null
					&& !StringUtils.isEmpty(requestQuote.getSoldToCustomerNameChinese())) {
				rfqHeader.setChineseSoldToCustomerName(requestQuote.getSoldToCustomerNameChinese());
			}

			if (requestQuote.getEndCustomer() != null) {
				rfqHeader.setEndCustomerNumber(requestQuote.getEndCustomer().getCustomerNumber());
				rfqHeader.setEndCustomerName(getCustomerFullName(requestQuote.getEndCustomer()));
			} else if (requestQuote.getEndCustomerName() != null) {
				rfqHeader.setEndCustomerName(requestQuote.getEndCustomerName());
			}
			if (requestQuote.getShipToCustomer() != null) {
				rfqHeader.setShipToCustomerNumber(requestQuote.getShipToCustomer().getCustomerNumber());
				rfqHeader.setShipToCustomerName(getCustomerFullName(requestQuote.getShipToCustomer()));
			} else if (requestQuote.getShipToCustomerName() != null) {
				rfqHeader.setShipToCustomerName(requestQuote.getShipToCustomerName());
			}

			rfqHeader.setBizUnit(requestQuote.getBizUnit().getName()); // fix
																		// defect
																		// 69
																		// June
																		// 20140904
																		// in
																		// project
																		// quote_matching_logic_enhance

			rfqHeader.setProjectName(requestQuote.getProjectName());
			rfqHeader.setApplication(requestQuote.getApplication());

			rfqHeader.setDesignLocation(requestQuote.getDesignLocation());
			rfqHeader.setDesignRegion(requestQuote.getDesignRegion());
			rfqHeader.setMpSchedule(requestQuote.getMpSchedule());
			rfqHeader.setPpSchedule(requestQuote.getPpSchedule());
			rfqHeader.setYourReference(requestQuote.getYourReference());
			rfqHeader.setCopyToCs(requestQuote.getCopyToCS());
			// Material restructure and quote_item delinkage. changed on 10 Apr
			// 2014.
			rfqHeader.setCopyToCsName(requestQuote.getCopyToCsName());

			rfqHeader.setRemarks(requestQuote.getRemarks());
            //fixed by DamonChen@20180102,as will throw a null poiter exception if the requestQuote.getLoaFlag() is null
			rfqHeader.setLoa(requestQuote.getLoaFlag()== null?false:requestQuote.getLoaFlag());

			// WebQuote 2.2 enhancement : fields changes.
			// rfqHeader.setOrderOnHand(requestQuote.getOrderOnHandFlag());
			// rfqHeader.setBom(requestQuote.getBomFlag());
			rfqHeader.setDesignInCat(requestQuote.getDesignInCat());
			rfqHeader.setQuoteType(requestQuote.getQuoteType());
			// added by damon@20160719
			if (rfqHeader.getSalesBizUnit() == null) {
				User saleUser = userSB.findByEmployeeIdWithAllRelation(rfqHeader.getSalesEmployeeNumber());
				if (saleUser != null) {
					rfqHeader.setSalesBizUnit(saleUser.getDefaultBizUnit());
				}
			}
			// INC0155169
			if (requestQuote.getEndCustomer() != null) {
				rfqHeader.setEndCustomerNumber(requestQuote.getEndCustomer().getCustomerNumber());
				rfqHeader.setEndCustomerName(requestQuote.getEndCustomer().getCustomerFullName());
			}
			
			//Multi-currency 201810 (whwong)
			if (requestQuote.getQuoteItems().size()>0) rfqHeader.setRfqCurr(requestQuote.getQuoteItems().get(0).getRfqCurr().name());
			rfqHeader.setdLinkFlag(requestQuote.isdLinkFlag());
			
			if (isInsideSalesRole || isCSRole || isQCOperation) {
				updateCsForSales();
			}
		}

		// logger.log(Level.INFO, "PERFORMANCE END - copyQuoteHeader()");
	}

	public void resubmitInvalidRfq() {
		fillInRequestRfqSubmission(QuoteSBConstant.QUOTE_STAGE_INVALID, false, 0l);
	}

	public void continueRfqSubmission(boolean draft) {
		continueRfqSubmission(draft, 0l);
	}

	// @Params draft: if it is true means it call after user save drfat.
	public void continueRfqSubmission(boolean draft, long keyId) {

		fillInRequestRfqSubmission(QuoteSBConstant.QUOTE_STAGE_DRAFT, draft, keyId);
	}

	public void fillInRequestRfqSubmission(String stage, boolean draft, long keyId) {

		// logger.log(Level.INFO,
		// "PERFORMANCE START - continueRfqSubmission()");
		LOGGER.info("[the function fillInRequestRfqSubmission]  requestQuoteId is: " + requestQuoteId);
		itemNumberCount = 0;
		rfqItems = new ArrayList<RfqItemVO>();
		long id = 0l;
		if (draft) {
			id = keyId;
		} else {
			if (requestQuoteId != null) {
				id = Long.parseLong(requestQuoteId);
			}
		}

		if (id != 0l) {
			quote = quoteSB.findQuoteByPK(id);
			rfqHeader.setFormNumber(quote.getFormNumber());
			rfqHeader.setId(id);
			rfqHeader.setVersion(quote.getVersion());
			rfqHeader.setContinueSubmit(true);
			if (!draft) {
				rfqHeader.setDisableRfqCurr(true);
			}
			// for Resubmit Invalid RFQ damon@20160801
			rfqHeader.setReSubmitType(stage);
			// INC0019856
			bizUnit = quote.getBizUnit();
			if (quote.getBizUnit() != null) {
				rfqHeader.setBizUnit(quote.getBizUnit().getName());
			}
			// Fixed the issue 1543 and 1548 on Feb 24, 2014
			// rfqHeader.setUploadTime(quote.getUploadTime());

			if (quote.getSales() != null) {
				rfqHeader.setSalesEmployeeNumber(quote.getSales().getEmployeeId());
				rfqHeader.setSalesEmployeeName(quote.getSales().getName());
				if (quote.getSales().getTeam() != null)
					rfqHeader.setTeam(quote.getSales().getTeam().getName());

				// modified by lenon 2016-07-06
				if (rfqHeader.getSalesBizUnit() == null) {
					User saleUser = userSB.findByEmployeeIdWithAllRelation(rfqHeader.getSalesEmployeeNumber());
					if (saleUser != null) {
						rfqHeader.setSalesBizUnit(saleUser.getDefaultBizUnit());
					}
				}

			}

			// WebQuote 2.2 enhancement : fields changes.
			// if(quote.getBmtCheckedFlag()!=null)
			// {
			// rfqHeader.setBmtChecked(quote.getBmtCheckedFlag().booleanValue());
			// }
			rfqHeader.setDesignInCat(quote.getDesignInCat());

			rfqHeader.setEnquirySegment(quote.getEnquirySegment());

			if (quote.getSoldToCustomer() != null) {
				rfqHeader.setSoldToCustomerNumber(quote.getSoldToCustomer().getCustomerNumber());
				rfqHeader.setSoldToCustomerName(getCustomerFullName(quote.getSoldToCustomer()));
				// Fixed the issue 1548, 1543
				retrieveEndCustomerChineseName();
			}
			rfqHeader.setCustomerType(quote.getCustomerType());

			if (quote.getEndCustomer() != null) {
				rfqHeader.setEndCustomerNumber(quote.getEndCustomer().getCustomerNumber());
				rfqHeader.setEndCustomerName(getCustomerFullName(quote.getEndCustomer()));
			} else if (quote.getEndCustomerName() != null) {
				rfqHeader.setEndCustomerName(quote.getEndCustomerName());
			}
			if (quote.getShipToCustomer() != null) {
				rfqHeader.setShipToCustomerNumber(quote.getShipToCustomer().getCustomerNumber());
				rfqHeader.setShipToCustomerName(getCustomerFullName(quote.getShipToCustomer()));
			} else if (quote.getShipToCustomerName() != null) {
				rfqHeader.setShipToCustomerName(quote.getShipToCustomerName());
			}

			rfqHeader.setProjectName(quote.getProjectName());
			rfqHeader.setApplication(quote.getApplication());

			rfqHeader.setDesignLocation(quote.getDesignLocation());
			// modified by Lenon.Yang(043044) 2016/05/30
			rfqHeader.setDesignRegion(quote.getDesignRegion());
			rfqHeader.setMpSchedule(quote.getMpSchedule());
			rfqHeader.setPpSchedule(quote.getPpSchedule());
			rfqHeader.setYourReference(quote.getYourReference());
			rfqHeader.setCopyToCs(quote.getCopyToCS());
			// Material restructure and quote_item delinkage. changed on 10 Apr
			// 2014.
			rfqHeader.setCopyToCsName(quote.getCopyToCsName());

			rfqHeader.setRemarks(quote.getRemarks());

			if (quote.getLoaFlag() != null) {
				rfqHeader.setLoa(quote.getLoaFlag().booleanValue());
			}

			// WebQuote 2.2 enhancement : fields changes.
			// if(quote.getOrderOnHandFlag()!=null)
			// {
			// rfqHeader.setOrderOnHand(quote.getOrderOnHandFlag().booleanValue());
			// }
			// if(quote.getBomFlag()!=null)
			// {
			// rfqHeader.setBom(quote.getBomFlag().booleanValue());
			// }
			rfqHeader.setQuoteType(quote.getQuoteType());

			if (quote.getAttachments() != null && quote.getAttachments().size() > 0) {
				rfqHeader.setFormAttachments(quote.getAttachments());
			} else {
				List<Attachment> atList = new ArrayList<Attachment>();
				rfqHeader.setFormAttachments(atList);
			}

			// fixed issue 1548 . need to update the timestamp at quote object .
			if (QuoteSBConstant.QUOTE_STAGE_INVALID.equalsIgnoreCase(stage)) {
				rfqHeader.setUploadTime(null);
			}

			//Multi-currency 201810 (whwong)
			if (quote.getQuoteItems().size()>0) rfqHeader.setRfqCurr(quote.getQuoteItems().get(0).getRfqCurr().name());
			rfqHeader.setdLinkFlag(quote.isdLinkFlag());
			
			List<QuoteItem> requestQuoteItems = quote.getQuoteItems();
			rfqItems = new ArrayList<RfqItemVO>();
			if (requestQuoteItems != null) {
				for (QuoteItem item : requestQuoteItems) {

					if ((item.getStage() != null && item.getStage().equals(stage))
							|| (item.getStage() != null && item.getStage().equals(stage) && draft)) {
						RfqItemVO rfqItem = new RfqItemVO();

						rfqItem.setQuoteId(item.getQuote().getId());
						// Added by Tonmy on 20 Aug 2013. for issue 796
						rfqItem.setQuoteNumber(item.getQuoteNumber());
						rfqItem.setId(item.getId());
						rfqItem.setVersion(item.getVersion());
						rfqItem.setReSubmitType(stage);

						if (item.getSoldToCustomer() != null) {
							rfqItem.setSoldToCustomerName(getCustomerFullName(item.getSoldToCustomer()));
							rfqItem.setSoldToCustomerNumber(item.getSoldToCustomer().getCustomerNumber());
							rfqItem.setCustomerType(item.getSoldToCustomer().getCustomerType());
						}
						if (item.getEndCustomer() != null) {
							rfqItem.setEndCustomerName(getCustomerFullName(item.getEndCustomer()));
							rfqItem.setEndCustomerNumber(item.getEndCustomer().getCustomerNumber());
						} else {
							if (item.getEndCustomerName() != null) {
								rfqItem.setEndCustomerName(item.getEndCustomerName());
							}
						}

						if (item.getShipToCustomer() != null) {
							rfqItem.setShipToCustomerName(getCustomerFullName(item.getShipToCustomer()));
							rfqItem.setShipToCustomerNumber(item.getShipToCustomer().getCustomerNumber());
						}
						rfqItem.setMpSchedule(item.getMpSchedule());
						rfqItem.setPpSchedule(item.getPpSchedule());
						rfqItem.setApplication(item.getApplication());
						rfqItem.setProjectName(item.getProjectName());
						rfqItem.setDesignLocation(item.getDesignLocation());
						rfqItem.setDesignRegion(item.getDesignRegion());

						// modified by Lenon.Yang(043044) 2016/05/30
						if (org.apache.commons.lang.StringUtils.isBlank(rfqHeader.getDesignRegion())) {
							rfqHeader.setDesignRegion(item.getDesignRegion());
						}
						if (item.getBmtCheckedFlag() != null) {
							rfqItem.setBmtCheckedFlag(item.getBmtCheckedFlag());
						}
						if (item.getLoaFlag() != null) {
							rfqItem.setLoa(item.getLoaFlag().booleanValue());
						}

						// WebQuote 2.2 enhancement : fields changes.
						// if (item.getOrderOnHandFlag() != null)
						// {
						// rfqItem.setOrderOnHand(item.getOrderOnHandFlag()
						// .booleanValue());
						// }
						rfqItem.setQuoteType(item.getQuoteType());

						// WebQuote 2.2 enhancement : fields changes.
						// if (item.getArdc() != null)
						// {
						// rfqItem.setAvnetRegionalDemandCreation(item
						// .getArdc().booleanValue());
						// }
						rfqItem.setDesignInCat(item.getDesignInCat());

						// }
						if (item.getRequestedMfr() != null) {
							rfqItem.setMfr(item.getRequestedMfr().getName());

						}
						if (item.getRequestedPartNumber() != null) {
							rfqItem.setRequiredPartNumber(item.getRequestedPartNumber());
						}

						if (item.getEau() != null)
							rfqItem.setEau(String.valueOf(item.getEau()));
						if (item.getTargetResale() != null) {
							// rfqItem.setTargetResale(NumberFormat.getNumberInstance(resourceMB.getResourceLocale()).format(item.getTargetResale()));
							rfqItem.setTargetResale(QuoteUtil.formatDouble(item.getTargetResale()));
						}

						if (item.getRequestedQty() != null)
							rfqItem.setRequiredQty(String.valueOf(item.getRequestedQty()));
						rfqItem.setCost(item.getCost());
						rfqItem.setCostIndicator(item.getCostIndicator());
						rfqItem.setCompetitorInformation(item.getCompetitorInformation());

						rfqItem.setEnquirySegment(item.getEnquirySegment());

						rfqItem.setProjectName(item.getProjectName());

						rfqItem.setApplication(item.getApplication());

						rfqItem.setDrmsNumber(item.getDrmsNumber());
						rfqItem.setSupplierDrNumber(item.getSupplierDrNumber());
						rfqItem.setCancellationWindow(item.getCancellationWindow());
						rfqItem.setReschedulingWindow(item.getRescheduleWindow());
						if (item.getAttachments() != null && item.getAttachments().size() > 0) {
							List<Attachment> atList = new ArrayList<Attachment>();
							for (Attachment at : item.getAttachments()) {
								Attachment att = new Attachment();
								att.setFileImage(at.getFileImage());
								att.setFileName(at.getFileName());
								att.setType(at.getType());
								atList.add(att);
							}
							rfqItem.setAttachments(atList);
						} else {
							List<Attachment> atList = new ArrayList<Attachment>();
							rfqItem.setAttachments(atList);
						}

						if (item.getMpq() != null)
							rfqItem.setMpq(item.getMpq());
						if (item.getMoq() != null)
							rfqItem.setMoq(item.getMoq());
						rfqItem.setLeadTime(item.getLeadTime());

						// Fixed Production issue INC0045925
						if (item.getRemarks() != null)
							rfqItem.setItemRemarks(item.getRemarks());
						if (item.getSprFlag() != null && item.getSprFlag() ) {
							rfqItem.setSpecialPriceIndicator(item.getSprFlag());
						}
						rfqItem.setItemNumber(++itemNumberCount);
						
					    rfqItem.setRfqCurr(item.getRfqCurr()==null?null:item.getRfqCurr().name());
						rfqItem.setBuyCurr(item.getBuyCurr()==null?null:item.getBuyCurr().name());

						rfqItems.add(rfqItem);
					}
				}
				getRestrictedCustomerList();
				validateRfqs();
			}

		}
		setMfrSelectItems();
		// logger.log(Level.INFO, "PERFORMANCE END - continueRfqSubmission()");
	}

	public void updateCustomerType() {

		// logger.log(Level.INFO, "PERFORMANCE START - updateCustomerType()");

		for (int i = 0; i < rfqItems.size(); i++) {
			if (!QuoteUtil.isEmpty(rfqItems.get(i).getSoldToCustomerNumber())
					&& rfqItems.get(i).getSoldToCustomerNumber().equals(rfqHeader.getSoldToCustomerNumber()))
				rfqItems.get(i).setCustomerType(rfqHeader.getCustomerType());
		}

		// logger.log(Level.INFO, "PERFORMANCE END - updateCustomerType()");
	}

	public void updateBizUnit() {

		// logger.log(Level.INFO, "PERFORMANCE START - updateBizUnit()");

		bizUnit = bizUnitSB.findByPk(rfqHeader.getBizUnit());
		// WebQuote 2.2 enhancement : fields changes.
		// rfqHeader.setBmtChecked(false);
		rfqHeader.setDesignInCat(null);
		rfqHeader.setEnquirySegment(null);

		// for issue 709 . Season confirm with user . if the region switch,
		// don't clear the customer field of header.
		// rfqHeader.setSoldToCustomerNumber(null);
		// rfqHeader.setSoldToCustomerName(null);
		// rfqHeader.setChineseSoldToCustomerName(null);
		// rfqHeader.setCustomerType(null);
		// rfqHeader.setShipToCustomerNumber(null);
		// rfqHeader.setShipToCustomerName(null);
		// rfqHeader.setEndCustomerNumber(null);
		// rfqHeader.setEndCustomerName(null);
		rfqHeader.setProjectName(null);
		rfqHeader.setApplication(null);
		rfqHeader.setDesignLocation(null);
		rfqHeader.setMpSchedule(null);
		rfqHeader.setPpSchedule(null);
		rfqHeader.setYourReference(null);
		rfqHeader.setCopyToCs(null);
		rfqHeader.setCopyToCsName(null);
		rfqHeader.setRemarks(null);
		rfqHeader.setLoa(false);
		// WebQuote 2.2 enhancement : fields changes.
		// rfqHeader.setOrderOnHand(false);
		// rfqHeader.setBom(false);

		if (isSalesRole == true) {

		} else {
			rfqHeader.setSalesEmployeeNumber(null);
			rfqHeader.setSalesEmployeeName(null);
		}

		rfqHeader.setQuoteType(null);
		rfqHeader.setFormAttachments(null);

		//Bryan Start
		//List<Manufacturer> manufacturers = MfrCacheManager.getMfrListByBizUnit(bizUnit.getName());
		List<Manufacturer> manufacturers = cacheUtil.getMfrListByBizUnit(bizUnit.getName());
		//Bryan End
		List<String> mfrCodes = new ArrayList<String>();
		if (manufacturers != null) {
			for (Manufacturer manufacturer : manufacturers)
				mfrCodes.add(manufacturer.getName());
		}
		mfrSelectList = QuoteUtil.createFilterOptions(mfrCodes.toArray(new String[mfrCodes.size()]),
				mfrCodes.toArray(new String[mfrCodes.size()]), false, false);

		mfrSearchSelectList = QuoteUtil.createFilterOptions(mfrCodes.toArray(new String[mfrCodes.size()]),
				mfrCodes.toArray(new String[mfrCodes.size()]), false, true);

		updateExchangeCurrencyInfoByBu(bizUnit);

		resetRfqSubmissionForSwitch();
		updateSalesOrg();

		this.isShowDLink=ComponetConfigService.showByRegion("rfqSubmission", "dLinkCheckBox", Arrays.asList(bizUnit.getName()));


		
		
		// logger.log(Level.INFO, "PERFORMANCE END - updateBizUnit()");
	}

	/**
	 * update exchange currency information according to bizunit
	 * 
	 * @param buName
	 */
	private void updateExchangeCurrencyInfoByBu(BizUnit bu) {
		// added by June for project RMB cur to initilise exchange currency pull
		// downmenu 20140812
		
		List<String> currencyLst = new ArrayList<String>(bu.getAllowCurrencies());
		exCurrencyList = QuoteUtil.createFilterOptions(currencyLst.toArray(new String[currencyLst.size()]),
				currencyLst.toArray(new String[currencyLst.size()]), false, false,true);
			rfqHeader.setRfqCurr(bu.getDefaultCurrency().name());
	}

	public void updateSalesOrg() {

		// logger.log(Level.INFO, "PERFORMANCE START - updateSalesOrg()");

		// List<String> salesOrgCodes =
		// SalesOrgCacheManager.getSalesOrgCodesByBizUnit(rfqHeader.getBizUnit());
		// Changed by tonmy on 12 Oct 2013 . for the cross region function.
		//Bryan Start
		//List<String> salesOrgCodes = SalesOrgCacheManager.getSalesOrgCodesByBizUnit(user.getDefaultBizUnit().getName());
		List<String> salesOrgCodes = cacheUtil.getSalesOrgCodesByBizUnit(user.getDefaultBizUnit().getName());
		//Bryan End
		salesOrgSelectList = QuoteUtil.createFilterOptions(salesOrgCodes.toArray(new String[salesOrgCodes.size()]),
				salesOrgCodes.toArray(new String[salesOrgCodes.size()]), false, false);

		// logger.log(Level.INFO, "PERFORMANCE END - updateSalesOrg()");
	}

	public void updateCsForSales() {

		// logger.log(Level.INFO, "PERFORMANCE START - updateCsForSales()");

		List<String> bizUnitCodes = new ArrayList<String>();
		if (user.getBizUnits() != null) {
			for (BizUnit bizUnit : user.getBizUnits())
				bizUnitCodes.add(bizUnit.getName());
		}
		bizUnitSelectList = QuoteUtil.createFilterOptions(bizUnitCodes.toArray(new String[bizUnitCodes.size()]),
				bizUnitCodes.toArray(new String[bizUnitCodes.size()]), false, false, true);
		updateSalesOrg();

		String sales = rfqHeader.getSalesEmployeeNumber();
		csList = new ArrayList<User>();

		if (sales != null) {
			if (isSales) {
				// inactive CS issue
				csList = userSB.findAllActiveCsForSales(user);
			} else {
				User user = userSB.findByEmployeeIdLazily(sales);
				// inactive CS issue
				csList = userSB.findAllActiveCsForSales(user);
			}
		}
		// change the sorting of cc list of copy to CS, by name in alphabetical
		// order.
		Comparator<User> comparator = new Comparator<User>() {
			public int compare(User s1, User s2) {
				String value1 = "" + s1.getName();
				String value2 = "" + s2.getName();
				return value1.compareTo(value2);
			}
		};
		Collections.sort(csList, comparator);
		// logger.log(Level.INFO, "PERFORMANCE END - updateCsForSales()");
	}

	public void changeCityByCountry() {

		// logger.log(Level.INFO, "PERFORMANCE START - changeCityByCountry()");

		//Bryan Start
		//Country country = CountryCacheManager.getCountry(newProspectiveCustomerCountry);
		Country country = cacheUtil.getCountry(newProspectiveCustomerCountry);
		// End
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
				cityNames.toArray(new String[cityNames.size()]), false, false);

		// logger.log(Level.INFO, "PERFORMANCE END - changeCityByCountry()");
	}

	public void uploadFormAttachment() {

		// logger.log(Level.INFO, "PERFORMANCE START - uploadFormAttachment()");
		String fileName = "";
		try {
			//fixed attachment issue byDamonchen@20180601
			if(formAttachment !=null && formAttachment.getContents().length>0 
					&&(!QuoteUtil.isEmpty(formAttachment.getFileName()))){
			Attachment attachment = new Attachment();
			fileName = formAttachment.getFileName().substring(formAttachment.getFileName().lastIndexOf("\\") + 1,
					formAttachment.getFileName().length());
			attachment.setFileName(fileName);
			attachment.setFileImage(QuoteUtil.getUploadFileContent(formAttachment));
			attachment.setType(QuoteSBConstant.ATTACHMENT_TYPE_QUOTE);

			List<Attachment> attachments = null;
			if (rfqHeader.getFormAttachments() != null) {
				attachments = rfqHeader.getFormAttachments();
			} else {
				attachments = new ArrayList<Attachment>();
			}
			attachments.add(attachment);
			rfqHeader.setFormAttachments(attachments);
			RequestContext requestContext = RequestContext.getCurrentInstance();
			requestContext.update("form_rfqSubmission:headerAttIcon");
			FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "",
					ResourceMB.getText("wq.error.attchUploadSucess") + ".");
			FacesContext.getCurrentInstance().addMessage("RfqSubmissionGrowl_1", msg);
			}
		} catch (Exception e) {
			LOGGER.log(Level.SEVERE, "Error occured in uploading file: " + fileName + ", Reason for failure: "
					+ MessageFormatorUtil.getParameterizedStringFromException(e), e);
			FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "",
					ResourceMB.getText("wq.message.attchmntUploadFail"));
			FacesContext.getCurrentInstance().addMessage("RfqSubmissionGrowl_1", msg);
		}
		// logger.log(Level.INFO, "PERFORMANCE END - uploadFormAttachment()");
	}

	public void readBatchRfqExcelForm() {

		// logger.log(Level.INFO,
		// "PERFORMANCE START - readBatchRfqExcelForm()");
		itemNumberCount = 0;
		rfqItems = new ArrayList<RfqItemVO>();
		try {
			if (rfqExcel != null) {
				uploadFileName = rfqExcel.getFileName();
				InputStream file = rfqExcel.getInputstream();
				// XSSFWorkbook workbook = new XSSFWorkbook(file);
				workbook = new XSSFWorkbook(file); // found this bug and fixed
													// it when merged the add
													// sap partnumber in
													// WebQutoe from 2.2.13 to
													// Truck by Alex on 14 Oct
													// 2014
				XSSFSheet sheet = workbook.getSheetAt(0);

				XSSFFont normalStyle = workbook.createFont();
				normalStyle.setFontHeightInPoints((short) 8);

				short pinkColor = HSSFColor.PINK.index;
				XSSFCellStyle pinkFontPinkBackStyle = workbook.createCellStyle();
				pinkFontPinkBackStyle.setFillForegroundColor(pinkColor);
				pinkFontPinkBackStyle.setFillPattern(CellStyle.SOLID_FOREGROUND);
				pinkFontPinkBackStyle.setBorderLeft(HSSFCellStyle.BORDER_THIN);
				pinkFontPinkBackStyle.setBorderRight(HSSFCellStyle.BORDER_THIN);
				pinkFontPinkBackStyle.setBorderBottom(HSSFCellStyle.BORDER_THIN);
				pinkFontPinkBackStyle.setBorderTop(HSSFCellStyle.BORDER_THIN);
				pinkFontPinkBackStyle.setFont(normalStyle);
				pinkFontPinkBackStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER);

				short whiteColor = HSSFColor.WHITE.index;
				XSSFCellStyle normalFontPinkBackStyle = workbook.createCellStyle();
				normalFontPinkBackStyle.setFillForegroundColor(whiteColor);
				normalFontPinkBackStyle.setFillPattern(CellStyle.SOLID_FOREGROUND);
				normalFontPinkBackStyle.setBorderLeft(HSSFCellStyle.BORDER_THIN);
				normalFontPinkBackStyle.setBorderRight(HSSFCellStyle.BORDER_THIN);
				normalFontPinkBackStyle.setBorderBottom(HSSFCellStyle.BORDER_THIN);
				normalFontPinkBackStyle.setBorderTop(HSSFCellStyle.BORDER_THIN);
				normalFontPinkBackStyle.setFont(normalStyle);
				normalFontPinkBackStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER);
				StringBuilder errorSb = new StringBuilder();

				Iterator<Row> rowIterator = sheet.iterator();

				SimpleDateFormat sdf = new SimpleDateFormat("MMM-yy");
				while (rowIterator.hasNext()) {
					Row row = rowIterator.next();
					int colIndex = 0;
					String errorMsg = null;
					if (row.getRowNum() + 1 >= QuoteConstant.RFQ_ITEM_START_ROW) {
						Iterator<Cell> cellIterator = row.cellIterator();

						/***
						 * New template from RFQ submission form
						 */

						RfqItemVO rfqItem = new RfqItemVO();
						String strSpr = null;
						String strMfr = null;
						String strRequestedPartNumber = null;
						String strSapPartNumber = null;
						String strRequiredQty = null;
						String strEau = null;
						String strTargetResale = null;

						// WebQuote 2.2 enhancement : fields changes.
						// String str_ardc = null;
						String strDesignInCat = null;
						String strDrms = null;
						String strSupplierDrNumber = null;

						String strCompetitorInformation = null;
						String strRemarks = null;

						String strSoldToCode = null;
						String strSoldToParty = null;
						String strShipToCode = null;
						String strShipToParty = null;
						String strEndCustomerCode = null;
						String strEndCustomerName = null;
						String strEnquirySegment = null;
						String strProjectName = null;
						String strApplication = null;
						String strDesignLocation = null;
						String strMpSchedule = null;
						String strPpSchedule = null;
						// WebQuote 2.2 enhancement : fields changes.
						// String str_orderOnHand = null;
						String strQuoteType = null;
						String strLoa = null;
						String strDesignRegion = null;
						String strBmtCheckFlag = null;
						errorSb.setLength(0);

						while (cellIterator.hasNext()) {
							Cell cell = cellIterator.next();

							// String[] cellPosition =
							// QuoteUtil.convertExcelPosition(cell.getColumnIndex(),
							// cell.getRowIndex());
							String cellValue = null;
							switch (cell.getCellType()) {
							case Cell.CELL_TYPE_BOOLEAN:
								cellValue = cell.getBooleanCellValue() ? QuoteConstant.OPTION_YES
										: QuoteConstant.OPTION_NO;
								break;
							case Cell.CELL_TYPE_NUMERIC:
								cellValue = QuoteUtil.numberFormatter(cell.getNumericCellValue());
								if (cellValue != null)
									cellValue = cellValue.trim();
								break;
							case Cell.CELL_TYPE_STRING:
								cellValue = cell.getStringCellValue();
								if (cellValue != null)
									cellValue = cellValue.trim();
								break;

							}

							/*
							 * Bid-To-Buy=24 LOA=25 Local DR=7, SPR =1 to fix
							 * Defect#36: convert YES AND NO or yes and no to
							 * Yes or No
							 */
							int i = 0;

							// if(colIndex ==26|| colIndex ==25 || colIndex ==8
							// ||colIndex ==1) {

							// logger.info("========TTTTT1"+colIndex+"====:"+cellValue);
							if (colIndex == 2 || colIndex == 1 || colIndex == 28) {
								cellValue = org.apache.commons.lang.StringUtils
										.capitalize(org.apache.commons.lang.StringUtils.lowerCase(cellValue));
							}
							// logger.info("========TTTTT2"+colIndex+"====:"+cellValue);
							if (colIndex == ++i)
								strBmtCheckFlag = cellValue;
							if (colIndex == ++i)
								strSpr = cellValue;
							if (colIndex == ++i)
								strMfr = cellValue;
							if (colIndex == ++i)
								strRequestedPartNumber = cellValue;
							if (colIndex == ++i)
								strSapPartNumber = cellValue;
							if (colIndex == ++i)
								strRequiredQty = cellValue;
							if (colIndex == ++i)
								strEau = cellValue;
							if (colIndex == ++i)
								strTargetResale = cellValue;
							// WebQuote 2.2 enhancement : fields changes.
							// if (colIndex == 7)
							// str_ardc = cellValue;
							if (colIndex == ++i)
								strDesignInCat = cellValue;
							if (colIndex == ++i)
								strDrms = cellValue;
							if (colIndex == ++i)
								strSupplierDrNumber = cellValue;

							if (colIndex == ++i)
								strCompetitorInformation = cellValue;
							if (colIndex == ++i)
								strRemarks = cellValue;

							if (colIndex == ++i)
								strSoldToCode = cellValue;
							if (colIndex == ++i)
								strSoldToParty = cellValue;
							if (colIndex == ++i)
								strShipToCode = cellValue;
							if (colIndex == ++i)
								strShipToParty = cellValue;
							if (colIndex == ++i)
								strEndCustomerCode = cellValue;
							if (colIndex == ++i)
								strEndCustomerName = cellValue;
							if (colIndex == ++i)
								strEnquirySegment = cellValue;
							if (colIndex == ++i)
								strProjectName = cellValue;
							if (colIndex == ++i)
								strApplication = cellValue;
							if (colIndex == ++i)
								strDesignRegion = cellValue;
							if (colIndex == ++i)
								strDesignLocation = cellValue;
							if (colIndex == ++i)
								strMpSchedule = cellValue;
							if (colIndex == ++i)
								strPpSchedule = cellValue;
							if (colIndex == ++i)
								strQuoteType = cellValue;
							if (colIndex == ++i)
								strLoa = cellValue;

							colIndex++;

						}

						if (strSpr == null && strMfr == null && strBmtCheckFlag == null
								&& strRequestedPartNumber == null && strRequiredQty == null && strEau == null
								&& strTargetResale == null && strSoldToCode == null && strSoldToParty == null
								&& strShipToCode == null && strShipToParty == null && strEndCustomerCode == null
								&& strEndCustomerName == null && strEnquirySegment == null && strProjectName == null
								&& strApplication == null && strDesignLocation == null && strDesignRegion == null
								&& strDesignInCat == null && strDrms == null && strCompetitorInformation == null
								&& strSupplierDrNumber == null && strMpSchedule == null && strPpSchedule == null
								&& strQuoteType == null && strLoa == null && strRemarks == null)
							break;

						/* str_bmtCheckFlag */
						try {
							if (!QuoteUtil.isEmpty(strBmtCheckFlag)) {
								if (!strBmtCheckFlag.equals(QuoteConstant.OPTION_YES)
										&& !strBmtCheckFlag.equals(QuoteConstant.OPTION_NO)) {
									throw (new Exception());
								} else {
									rfqItem.setBmtCheckedFlag(
											strBmtCheckFlag.equals(QuoteConstant.OPTION_YES) ? true : false);
								}
							}

						} catch (Exception ex) {
							LOGGER.log(Level.SEVERE,
									"Error occured in uploading file: " + uploadFileName + ", BMT AQ: "
											+ strBmtCheckFlag + ", " + "Reason for failure: "
											+ MessageFormatorUtil.getParameterizedStringFromException(ex),
									ex);
							final String message = ResourceMB.getText("wq.message.bmtFlag");
							errorSb.append(message).append(",");
						}

						/* SPR */
						try {
							if (QuoteUtil.isEmpty(strSpr)) {
								// throw(new Exception());
							} else {
								if (!strSpr.equals(QuoteConstant.OPTION_YES)
										&& !strSpr.equals(QuoteConstant.OPTION_NO)) {
									throw (new Exception());
								} else {
									rfqItem.setSpecialPriceIndicator(
											strSpr.equals(QuoteConstant.OPTION_YES) ? true : false);
								}
							}

						} catch (Exception ex) {
							LOGGER.log(Level.SEVERE,
									"Error occured in uploading file: " + uploadFileName + ", SPR: " + strSpr + ", "
											+ "Reason for failure: "
											+ MessageFormatorUtil.getParameterizedStringFromException(ex),
									ex);
							final String message = ResourceMB.getText("wq.message.invalidSPIndicator");
							errorSb.append(message).append(",");
						}

						/* MFR */
						try {
							if (QuoteUtil.isEmpty(strMfr)) {
								throw (new Exception());
							}
						} catch (Exception ex) {
							LOGGER.log(Level.SEVERE,
									"Error occured in uploading file: " + uploadFileName + ", MFR: " + strMfr + ", "
											+ "Reason for failure: "
											+ MessageFormatorUtil.getParameterizedStringFromException(ex),
									ex);
							final String message = ResourceMB.getText("wq.message.mfrCode");
							errorSb.append(message).append(",");
						}

						// Added by Tonmy on 20 Aug 2013
						try {
							if (!QuoteUtil.isEmpty(strMfr)) {
								List<Manufacturer> mfrList = manufacturerSB.findManufacturerLIstByName(strMfr, bizUnit);
								if (mfrList != null && mfrList.size() > 0) {
									rfqItem.setMfr(strMfr.toUpperCase());
								} else {
									throw (new Exception());
								}

							}
						} catch (Exception ex) {
							LOGGER.log(Level.SEVERE,
									"Error occured in uploading file: " + uploadFileName + ", MFR: " + strMfr + ", "
											+ "Reason for failure: "
											+ MessageFormatorUtil.getParameterizedStringFromException(ex),
									ex);
							final String message = ResourceMB.getText("wq.message.invalidMFR");
							errorSb.append(message).append(",");
						}

						/* Requested P/N */
						try {
							if (!QuoteUtil.isEmpty(strRequestedPartNumber)) {
								// List<Material> materials =
								// materialSB.findMaterialByMfrAndFullMfrPart(rfqItem.getMfr(),
								// cellValue, bizUnit);
								rfqItem.setRequiredPartNumber(strRequestedPartNumber);
								logInfo("Reading " + (itemNumberCount + 1) + ":" + rfqItem.getMfr() + "/"
										+ rfqItem.getRequiredPartNumber());
								/*
								 * if(materials == null || materials.size() ==
								 * 0){ throw(new Exception()); }
								 */
							} else {
								throw (new Exception());
							}
						} catch (Exception ex) {
							LOGGER.log(Level.SEVERE,
									"Error occured in uploading file: " + uploadFileName + ", Requested P/N: "
											+ strRequestedPartNumber + ", " + "Reason for failure: "
											+ MessageFormatorUtil.getParameterizedStringFromException(ex),
									ex);
							final String message = ResourceMB.getText("wq.message.requetsedPNNotExit");
							errorSb.append(message).append(",");

						}

						/* sapPartNumber */
						String keyStr = bizUnit.getName() + "==" + strMfr + "==" + strRequestedPartNumber;
						SelectItem[] sapPartNumberList = convertSapPartNumberToSelectItem(strMfr,
								strRequestedPartNumber);
						if (!QuoteUtil.isEmpty(strSapPartNumber)) {
							if (sapPartNumberList != null && sapPartNumberList.length > 1) { // the
																								// first
																								// element
																								// is
																								// the
																								// --select--
								boolean inputSapPartNumberExisted = false;
								for (SelectItem si : sapPartNumberList) {
									if (strSapPartNumber.trim().equals((String) si.getValue())) {
										inputSapPartNumberExisted = true;
										break;
									}
								}
								if (!inputSapPartNumberExisted) {
									final String message = ResourceMB.getText("wq.message.sapPNNotCorrect");
									errorSb.append(message).append(",");

								}
							}
						}

						sapPartNumberSelectedMap.put(keyStr, sapPartNumberList);
						rfqItem.setSapPartNumber(strSapPartNumber);
						rfqItem.setKey4SapPartNumberSelectedMap(keyStr);

						/* Required Qty */
						try {
							if (!QuoteUtil.isEmpty(strRequiredQty)) {
								if (convertToInteger(strRequiredQty) != null) {
									rfqItem.setRequiredQty(convertToStringAsInteger(strRequiredQty));
								} else {
									final String message = ResourceMB.getText("wq.message.invalideReqQty");
									errorSb.append(message).append(",");

								}
							} else {
								final String message = ResourceMB.getText("wq.message.reqQtyNotExit");
								errorSb.append(message).append(",");
							}
						} catch (Exception ex) {
							throw (ex);
						}

						/* EAU */
						try {
							if (!QuoteUtil.isEmpty(strEau)) {
								if (convertToInteger(strEau) != null) {
									rfqItem.setEau(convertToStringAsInteger(strEau));
								} else {
									throw (new Exception());
								}
							}
						} catch (Exception ex) {
							LOGGER.log(Level.SEVERE,
									"Error occured in uploading file: " + uploadFileName + ", EAU: " + strEau + ", "
											+ "Reason for failure: "
											+ MessageFormatorUtil.getParameterizedStringFromException(ex),
									ex);
							final String message = ResourceMB.getText("wq.message.invalidFormatEU");
							errorSb.append(message).append(",");
						}

						/* Target Resale */
						try {
							if (strTargetResale !=null && !QuoteUtil.isDecimal(strTargetResale)) {
								throw (new Exception());
							} else {
								rfqItem.setTargetResale(strTargetResale);
							}
						} catch (Exception ex) {
							LOGGER.log(Level.SEVERE,
									"Error occured in uploading file: " + uploadFileName + ", Target Resale"
											+ strTargetResale + ", " + "Reason for failure: "
											+ MessageFormatorUtil.getParameterizedStringFromException(ex),
									ex);
							final String message = ResourceMB.getText("wq.message.invalidTargetResale");
							errorSb.append(message).append(",");
						}

						/* Sold To Code */
						if (!QuoteUtil.isEmpty(strSoldToCode)) {
							Customer customer = null;
							try {
								List<String> accountGroups = new ArrayList<String>();
								accountGroups.add(QuoteSBConstant.ACCOUNT_GROUP_SOLDTO);
								accountGroups.add(QuoteSBConstant.ACCOUNT_GROUP_PROSPECTIVE_CUSTOMER);
								accountGroups.add(QuoteSBConstant.ACCOUNT_GROUP_CPD);
								List<Customer> customers = customerSB.findCustomerByCustomerNumber(strSoldToCode, null,
										accountGroups, rfqHeader.getSalesBizUnit(), 0, 100);

								if (customers != null && customers.size() > 0) {
									customer = customers.get(0);
								}

								if (customer == null) {
									throw (new Exception());
								}
							} catch (Exception ex) {
								LOGGER.log(Level.SEVERE,
										"Error occured in uploading file: " + uploadFileName + ", Sold To Code: "
												+ strSoldToCode + ", " + "Reason for failure: "
												+ MessageFormatorUtil.getParameterizedStringFromException(ex),
										ex);
								final String message = ResourceMB.getText("wq.message.invalidSoldCustNumber");
								errorSb.append(message + " :").append(strSoldToCode).append(",");

							}

							try {

								if (customer != null && customer.getDeleteFlag() != null
										&& customer.getDeleteFlag().booleanValue()) {
									throw (new Exception());
								} else {
									rfqItem.setSoldToCustomerNumber(customer.getCustomerNumber());
									rfqItem.setSoldToCustomerName(getCustomerFullName(customer));
									rfqItem.setCustomerType(customer.getCustomerType());
								}
							} catch (Exception ex) {
								LOGGER.log(Level.SEVERE,
										"Error occured in uploading file: " + uploadFileName + ", Reason for failure: "
												+ MessageFormatorUtil.getParameterizedStringFromException(ex),
										ex);
								final String message = ResourceMB.getText("wq.message.foundDelCust");
								errorSb.append(message + " : ").append(strSoldToCode).append(",");
							}
						}

						/* Sold To Party */
						if (!QuoteUtil.isEmpty(strSoldToParty)) {
							/*
							 * try{ Customer customer =
							 * customerSB.findByPK(str_soldToParty); if(customer
							 * != null){
							 * rfqItem.setSoldToCustomerNumber(customer
							 * .getCustomerNumber());
							 * rfqItem.setSoldToCustomerName
							 * (getCustomerFullName(customer));
							 * rfqItem.setCustomerType
							 * (customer.getCustomerType()); } else { throw(new
							 * Exception()); } } catch (Exception ex){
							 * FacesMessage msg = new
							 * FacesMessage(FacesMessage.SEVERITY_WARN,
							 * "Invalid Customer :", "Item "+
							 * (itemNumberCount+1)
							 * +" : Invalid Sold To Customer Number " +
							 * str_soldToParty);
							 * FacesContext.getCurrentInstance(
							 * ).addMessage("RfqSubmissionGrowl", msg);
							 * throw(ex); }
							 */
						}

						/* Ship To Code */
						if (!QuoteUtil.isEmpty(strShipToCode)) {
							try {
								Customer customer = customerSB.findByPK(strShipToCode);
								if (customer != null) {
									rfqItem.setShipToCustomerNumber(customer.getCustomerNumber());
									rfqItem.setShipToCustomerName(getCustomerFullName(customer));
								} else {
									throw (new Exception());
								}
							} catch (Exception ex) {
								LOGGER.log(Level.SEVERE,
										"Error occured in uploading file: " + uploadFileName + ", Ship to code: "
												+ strShipToCode + ", " + "Reason for failure: "
												+ MessageFormatorUtil.getParameterizedStringFromException(ex),
										ex);
								final String message = ResourceMB.getText("wq.message.invalidShipCustNumber");
								errorSb.append(message + " : ").append(strShipToCode).append(",");
							}
						}

						/* Ship To Party */
						if (!QuoteUtil.isEmpty(strShipToParty)
								&& QuoteUtil.isEmpty(rfqItem.getShipToCustomerNumber())) {
							rfqItem.setShipToCustomerName(strShipToParty);
						}

						/* End Customer Code */
						if (!QuoteUtil.isEmpty(strEndCustomerCode)) {
							try {
								Customer customer = customerSB.findByPK(strEndCustomerCode);
								if (customer != null) {
									rfqItem.setEndCustomerNumber(customer.getCustomerNumber());
									rfqItem.setEndCustomerName(getCustomerFullName(customer));
								} else {
									errorSb.append(ResourceMB.getText("wq.message.invalidEndCustomer") + " : ")
											.append(strEndCustomerCode).append(",");
								}
							} catch (Exception ex) {
								LOGGER.log(Level.SEVERE,
										"Error occured in uploading file: " + uploadFileName + ", End Customer Code: "
												+ strEndCustomerCode + ", " + "Reason for failure: "
												+ MessageFormatorUtil.getParameterizedStringFromException(ex),
										ex);
								final String message = ResourceMB.getText("wq.message.invalidEndCust");
								errorSb.append(message).append(",");
							}
						}

						/* End Customer Name */
						if (!QuoteUtil.isEmpty(strEndCustomerName)
								&& QuoteUtil.isEmpty(rfqItem.getEndCustomerNumber())) {
							// rfqItem.setEndCustomerName(str_endCustomerName);
							//// Prevent user to enter the End Customer freely
							// by damon@20161108
							final String message = ResourceMB.getText("wq.message.invalidEndCust");
							errorSb.append(message).append(",");

						}

						rfqItem.setEnquirySegment(strEnquirySegment);
						rfqItem.setProjectName(strProjectName);
						rfqItem.setApplication(strApplication);
						rfqItem.setDesignLocation(strDesignLocation);

						// WebQuote 2.2 enhancement : fields changes.
						/* Avnet Regional Demand Creation */
						// try
						// {
						// if (QuoteUtil.isEmpty(str_ardc))
						// {
						// // throw(new Exception());
						// }
						// else
						// {
						// if (!str_ardc.equals(QuoteConstant.OPTION_YES)
						// && !str_ardc
						// .equals(QuoteConstant.OPTION_NO))
						// {
						// throw (new Exception());
						// }
						// else
						// {
						// rfqItem.setAvnetRegionalDemandCreation(str_ardc
						// .equals(QuoteConstant.OPTION_YES) ? true
						// : false);
						// }
						// }
						// }
						// catch (Exception ex)
						// {
						// FacesMessage msg = new FacesMessage(
						// FacesMessage.SEVERITY_WARN,
						// QuoteConstant.MESSAGE_INVALID_FORMAT,
						// "Avnet Regional Demand Creation at item "
						// + (itemNumberCount + 1)
						// + QuoteConstant.MESSAGE_FOLLOWING_RFQ_NOT_LOAD);
						// FacesContext.getCurrentInstance().addMessage(
						// "RfqSubmissionGrowl", msg);
						// throw (ex);
						// }
						/* Design in cat */
						// Fixed for issue 431
						String message = null;
						try {
							if (!QuoteUtil.isEmpty(strDesignInCat)) {
								//Bryan Start
								//String value = DesignInCatCacheManager.getDesignInCat(strDesignInCat);
								String value = cacheUtil.getDesignInCat(strDesignInCat);
								//Bryan End

								if (!QuoteUtil.isEmpty(value)) {
									rfqItem.setDesignInCat(strDesignInCat);
								} else {
									message = ResourceMB.getText("wq.message.invalidDesignInCat");
									errorSb.append(message).append(",");

								}

							}
						} catch (Exception ex) {
							LOGGER.log(Level.SEVERE,
									"Error occured in uploading file: " + uploadFileName + ", Design in Cat: "
											+ strDesignInCat + ", " + "Reason for failure: "
											+ MessageFormatorUtil.getParameterizedStringFromException(ex),
									ex);
							message = ResourceMB.getText("wq.message.invalidDesignInCat");
							errorSb.append(message).append(",");

						}

						/* drms */
						try {
							if (!QuoteUtil.isEmpty(strDrms))
								rfqItem.setDrmsNumber(Long.parseLong(convertToStringAsInteger(strDrms)));
						} catch (Exception ex) {
							LOGGER.log(Level.SEVERE,
									"Error occured in uploading file: " + uploadFileName + ", DRMS Project ID: "
											+ strDrms + ", " + "Reason for failure: "
											+ MessageFormatorUtil.getParameterizedStringFromException(ex),
									ex);
							message = ResourceMB.getText("wq.message.invalidDRMSId");
							errorSb.append(message).append(",");

						}

						rfqItem.setCompetitorInformation(strCompetitorInformation);

						/* MFR DR */
						/*
						 * fix INC0160300 try { if(!QuoteUtil.isEmpty(str_drms)
						 * && QuoteUtil.isEmpty(str_supplierDrNumber)) { throw
						 * (new Exception()); } else {
						 * rfqItem.setSupplierDrNumber(str_supplierDrNumber); }
						 * 
						 * } catch (Exception ex) { errorMsg =
						 * "MFR DR# does not exist"; }
						 */
						if (!QuoteUtil.isEmpty(strSupplierDrNumber)) {
							rfqItem.setSupplierDrNumber(strSupplierDrNumber);
						}

						/* design Region */
						try {
							if (!QuoteUtil.isEmpty(strDesignRegion)) {
								//Bryan Start
								//List<String> designList = DesignLocationCacheManager.getDesignRegionList();
								List<String> designList = cacheUtil.getDesignRegionList();
								//Bryan End
								if (!designList.contains(strDesignRegion)) {
									throw (new Exception());
								} else {
									rfqItem.setDesignRegion(strDesignRegion);
								}
								;
							}
						} catch (Exception ex) {
							LOGGER.log(Level.SEVERE,
									"Error occured in uploading file: " + uploadFileName + ", Design Region: "
											+ strDesignRegion + ", " + "Reason for failure: "
											+ MessageFormatorUtil.getParameterizedStringFromException(ex),
									ex);
							message = ResourceMB.getText("wq.message.invalidDesignRegion");
							errorSb.append(message).append(",");
						}

						/* validate DesignLocation for BMT project */
						try {
							if (!QuoteUtil.isEmpty(strDesignLocation)) {
								/*
								 * List<String>
								 * designList=DesignLocationCacheManager.
								 * getDesignRegionList();
								 * if(!designList.contains(str_designRegion)){
								 * throw (new Exception()); }else{
								 * rfqItem.setDesignRegion(str_designRegion); };
								 */
								//Bryan Start
//								DesignLocation dLocation = DesignLocationCacheManager
//										.getDesignLocation(strDesignLocation);
								DesignLocation dLocation = cacheUtil
										.getDesignLocation(strDesignLocation);
								//Bryan End
								if (dLocation != null) {

									if (dLocation.getDesignRegion() != null
											&& (!dLocation.getDesignRegion().equals(strDesignRegion))) {
										message = ResourceMB.getText("wq.message.designLocUnmatchDesignRegion");
										errorSb.append(message).append(",");
									}

								}

							}
						} catch (Exception ex) {
							LOGGER.log(Level.SEVERE,
									"Error occured in uploading file: " + uploadFileName + ", Design Location: "
											+ strDesignLocation + "Reason for failure: "
											+ MessageFormatorUtil.getParameterizedStringFromException(ex),
									ex);
							message = ResourceMB.getText("wq.message.designLocUnmatchDesignRegion");
							errorSb.append(message).append(",");
						}

						// validate DesignRgion is not null,but the DesignLation
						// is null.Can't pass
						try {
							if (!QuoteUtil.isEmpty(strDesignRegion)) {
								if (QuoteUtil.isEmpty(strDesignLocation)) {
									throw (new Exception());
								}
							}
						} catch (Exception ex) {
							LOGGER.log(Level.SEVERE,
									"Error occured in uploading file: " + uploadFileName + ", Design Region: "
											+ strDesignRegion + ", Design Location: " + strDesignLocation + ","
											+ " Reason for failure: "
											+ MessageFormatorUtil.getParameterizedStringFromException(ex),
									ex);
							message = ResourceMB.getText("wq.message.designLocDesignRegionError");
							errorSb.append(message).append(",");
						}

						/* MP Schedule */
						try {
							if (!QuoteUtil.isEmpty(strMpSchedule)) {
								sdf.parse(strMpSchedule);
								rfqItem.setMpSchedule(strMpSchedule);
							}
						} catch (Exception ex) {
							LOGGER.log(Level.SEVERE,
									"Error occured in uploading file: " + uploadFileName + ", MP Schedule: "
											+ strMpSchedule + ", " + "Reason for failure: "
											+ MessageFormatorUtil.getParameterizedStringFromException(ex),
									ex);
							message = ResourceMB.getText("wq.message.invalidMPScehd");
							errorSb.append(message).append(",");
						}

						/* PP Schedule */
						try {
							if (!QuoteUtil.isEmpty(strPpSchedule)) {
								sdf.parse(strPpSchedule);
								rfqItem.setPpSchedule(strPpSchedule);
							}
						} catch (Exception ex) {
							LOGGER.log(Level.SEVERE,
									"Error occured in uploading file: " + uploadFileName + ", PP Schedule: "
											+ strPpSchedule + ", " + "Reason for failure: "
											+ MessageFormatorUtil.getParameterizedStringFromException(ex),
									ex);
							message = ResourceMB.getText("wq.message.invalidPPScehd");
							errorSb.append(message).append(",");
						}

						/* Quote Type */
						// fix bug for 431
						try {
							if (!QuoteUtil.isEmpty(strQuoteType)) {
								// WebQuote 2.2 enhancement : fields changes.
								//Bryan Start
								//String type = QuoteTypeCacheManager.getQuoteType(strQuoteType);
								String type = cacheUtil.getQuoteType(strQuoteType);
								//Bryan End
								if (QuoteUtil.isEmpty(type)) {
									LOGGER.info("this add from coding");

								} else {
									rfqItem.setQuoteType(strQuoteType);
								}
							}
						} catch (Exception ex) {
							LOGGER.log(Level.SEVERE,
									"Error occured in uploading file: " + uploadFileName + ", Quote Type: "
											+ strQuoteType + ", " + "Reason for failure: "
											+ MessageFormatorUtil.getParameterizedStringFromException(ex),
									ex);
							message = ResourceMB.getText("wq.message.invalidQuoteType");
							errorSb.append(message).append(",");
						}

						/* LOA */
						try {
							if (QuoteUtil.isEmpty(strLoa)) {
								// throw(new Exception());
							} else {
								if (!strLoa.equals(QuoteConstant.OPTION_YES)
										&& !strLoa.equals(QuoteConstant.OPTION_NO)) {
									throw (new Exception());
								} else {
									rfqItem.setLoa(strLoa.equals(QuoteConstant.OPTION_YES) ? true : false);
								}
							}
						} catch (Exception ex) {
							LOGGER.log(Level.SEVERE,
									"Error occured in uploading file: " + uploadFileName + ", LOA: " + strLoa + ", "
											+ "Reason for failure: "
											+ MessageFormatorUtil.getParameterizedStringFromException(ex),
									ex);
							errorMsg = ResourceMB.getText("wq.message.invalidLOA");
							errorSb.append(errorMsg).append(",");

						}

						rfqItem.setItemRemarks(strRemarks);

						if (errorSb.length() > 0) {
							// added to template
							// need modify this by Damon
							foundErrorMsg = true;
							Cell cell = row.createCell(29);
							for (int i = 1; i <= 28; i++) {
								row.getCell(i).setCellStyle(pinkFontPinkBackStyle);
							}
							cell.setCellValue(errorSb.toString().substring(0, errorSb.length() - 1));

						} else {
							rfqItem.setItemNumber(++itemNumberCount);
							rfqItems.add(rfqItem);

							Cell cell = row.createCell(29);
							cell.setCellValue("");
							for (int i = 1; i <= 28; i++) {
								row.getCell(i).setCellStyle(normalFontPinkBackStyle);
							}

						}
					}
				}
				file.close();
			}
		} catch (Exception e) {
			LOGGER.log(Level.SEVERE, "Error occured in uploading file: " + uploadFileName + ", Reason for failure: "
					+ MessageFormatorUtil.getParameterizedStringFromException(e), e);
		}

		// logger.log(Level.INFO, "PERFORMANCE END - readBatchRfqExcelForm()");

	}

	public void clearSearchPart() {
		// clear the memory
		requiredPartNumberList = null;
		selectedRequiredPartNumberList = null;
		searchParts = null;
		selectedSearchParts = null;
		selectedSearchPart = null;
	}

	/*
	 * Wizard to control the work flow
	 */
	@TransactionTimeout(value = 36000, unit = TimeUnit.SECONDS)
	public String onFlowProcess(FlowEvent event) {

		String validateConfig = sysConfigSB.getProperyValue(QuoteConstant.RFQ_SUBMISSION_DRMS_VALIDATION);
		final Locale locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
		if (!org.apache.commons.lang.StringUtils.equalsIgnoreCase("true", validateConfig)) {
			isValidateDrms = false;
		}
		
		// logger.log(Level.INFO, "PERFORMANCE START - onFlowProcess()");
		if (event.getOldStep() != null && event.getNewStep() != null) {
			// step 1
			if (event.getOldStep().equals("basicDetails") && event.getNewStep().equals("drms_match")) {
				logInfo("FROM FIRST PAGE TO DRMS MATCH PAGE");
				logInfo("the total size of rfqItems on step one is:"+rfqItems.size());
				workbook = null;
				for (RfqItemVO rfqItem : rfqItems) {
					logInfo("FirstPage-CheckPoint one : ["+rfqItem.getItemNumber()+"]"  + rfqItem.getMfr() + "/" + rfqItem.getRequiredPartNumber()
							+ " - " + rfqItem.getSoldToCustomerNumber());
					rfqItem.setRfqHeaderVO(rfqHeader);
				}
				removeEmptyRfq();
				clearSearchPart();

				boolean errorFound = false;

				try {
					//quoteHeaderVOChecking();
					
				
					
					rfqItemValidatorSB.validateNewCustomerCreation(rfqHeader, newProspectiveCustomerName1,newProspectiveCustomerCountry,newProspectiveCustomerCity,newProspectiveCustomerSalesOrg,newProspectiveCustomerAddress3,locale);
					// *********************validate the properties of  rfqHeader, begin******************//

					rfqItemValidatorSB.validateQuoteHeader(rfqHeader, locale);

					// *********************validate the properties of rfqHeader, end******************//
					// ********************************************************************************//
					// *********************validate the properties of rfqItems, begin******************//


					rfqItemValidatorSB.validateRfqItemSize(rfqItems,locale);
				
					fillInEmptyFieldFromHeader();
					
					rfqItemValidatorSB.validateRfqItem(rfqItems, locale,bizUnit,isValidateDrms);


					//quoteItemVoChecking();
					//getRestrictedCustomerList(); find it is unused , by DamonChen
					fillInBatchRfqs();
					

					if (validationRule("RfqSubmissionGrowl_1"))
						return event.getOldStep();
					     errorFound = true;
					     
					 rfqItemValidatorSB.validateRestrictedCustomer(rfqItems, locale,bizUnit);
					     
				}catch(WebQuoteException e){
					FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_WARN,
							e.getMainMessage(), e.getMessage());
					FacesContext.getCurrentInstance().addMessage("RfqSubmissionGrowl_1", msg);
					return event.getOldStep();
				} catch (Exception ex) {
					// String s=ex.getMessage();
					LOGGER.log(Level.SEVERE, "Error occured in flow, Reason for failure: " + ex.toString(), ex);
					FacesUtil.showWarnMessage(QuoteSBConstant.SYSTEM_ERROR, ex.getMessage(),
							"RfqSubmissionGrowl_1");
					return event.getOldStep();
				}
				
				
				if (isValidateDrms) {// add by lenon 2016/07/14 webquote japan
										// not need to validate drms
					int tempPendingDrmsCount = checkDrmsLinkage();
					/*
					 * for fixed enhancment 220 and 217 on quote match logic
					 * project June guo 20141202 to get DR expiry from SAP by
					 * Project ID,Neda ID,Neda Line Number
					 */
					List<DrProjectVO> projects = new ArrayList<DrProjectVO>();
					for (RfqItemVO rfqItem : rfqItems) {
						if (null != rfqItem.getDrProjects() && rfqItem.isDrmsLinkage()) {
							for (DrProjectVO vo : rfqItem.getDrProjects()) {
								vo.setSalesEmployeeId(rfqHeader.getSalesEmployeeNumber());
								projects.add(vo);
							}
						}
					}
					try {
						sapWebServiceSB.enquirySAPAGPToGetDrExpiryDate(projects);
						// SR for removing the validity check during DRMS
						// Matching at RFQ Submission (modified by Lenon,Yang
						// 2016/3/31)
						// rollback the SR for removing the validity check
						// during DRMS Matching (2016/4/12)

						// for defect 220 further enhancement which filter out
						// the expired DR project in the DRMS dialog.
						List<DrProjectVO> noExpiryProjectsPerRFQ = null;
						for (RfqItemVO rfqItem : rfqItems) {
							noExpiryProjectsPerRFQ = new ArrayList<DrProjectVO>();
							if (null != rfqItem.getDrProjects() && rfqItem.isDrmsLinkage()) {
								List<DrProjectVO> projectsInRfq = rfqItem.getDrProjects();
								for (DrProjectVO vo : projectsInRfq) {
									if (null != vo.getDisplayDrExpiryDate()) {
										noExpiryProjectsPerRFQ.add(vo);
									}
									//noExpiryProjectsPerRFQ.add(vo);For test
								}
							}
							rfqItem.setDrProjects(noExpiryProjectsPerRFQ); // set
																			// back
																			// all
																			// not
																			// expiry
																			// project
							if (null == noExpiryProjectsPerRFQ || 0 == noExpiryProjectsPerRFQ.size()) {
								rfqItem.setDrmsLinkage(false); // if there are
																// no matched
																// projects,
																// don't need to
																// display icon
																// of link
								if (pendingDRMSRfqs != null) {
									pendingDRMSRfqs.remove(rfqItem);
								}
							}
						}

						if (pendingDRMSRfqs != null) {
							pendingDRMSCount = pendingDRMSRfqs.size();
						} else {
							pendingDRMSCount = 0;
						}
						tempPendingDrmsCount = pendingDRMSCount;
					} catch (Exception ex) {
						LOGGER.log(Level.SEVERE, "Error occured in flow, Reason for failure: "
								+ MessageFormatorUtil.getParameterizedStringFromException(ex), ex);
						FacesMessage msg = new FacesMessage(
								ResourceMB.getText("wq.message.sapError") + ": " + ex.getMessage());
						FacesContext.getCurrentInstance().addMessage("RfqSubmissionGrowl_1", msg);
						errorFound = true;
						return event.getOldStep();
					}

					for (RfqItemVO rfqItem : rfqItems) {
						logInfo("FirstPage-CheckPoint two : " + rfqItem.getMfr() + "/" + rfqItem.getRequiredPartNumber()
								+ " - " + rfqItem.getSoldToCustomerNumber());
					}
                   //below are invalid code, don't know who added it,  commented by damonChen@2012
/*					if (!drmsFound) {
						FacesMessage msg = new FacesMessage(ResourceMB.getText("wq.message.note") + " : ",
								ResourceMB.getText("wq.message.noDRProjFound") + ".");
						FacesContext.getCurrentInstance().addMessage("RfqSubmissionGrowl_1", msg);
					}*/

					if (tempPendingDrmsCount > 0) {
						FacesMessage msg = new FacesMessage(ResourceMB.getText("wq.message.note") + " : ", ResourceMB
								.getParameterizedString("wq.message.pendingEnteringDRMS", tempPendingDrmsCount));
						FacesContext.getCurrentInstance().addMessage("RfqSubmissionGrowl_1", msg);
					}
				}

				// For issue 749
				// Set first page key.
				for (RfqItemVO rfqItem : rfqItems) {
					if (rfqItem.getStage() == null) {
						rfqItem.setFirstPageKey(QuoteUtil.getFristPageKey(rfqItem));
					}
				}
				

				
				extractDuplicatedRfqItem(duplicatedrfqItems,tmpMap,rfqItems);
				
			
			//	RequestContext requestContext = RequestContext.getCurrentInstance();
				//requestContext.update("form_rfqSubmission:duplicated_rfqItem_panel");
			
				 logInfo("FROM DRMS MATCH TO BASICDETAILS PAGE  :"+confirmGoNextFlag);
				 if(!confirmGoNextFlag){
					 
					   if(duplicatedrfqItems.size()>0 ){
		                	RequestContext requestContext = RequestContext.getCurrentInstance();
		    				requestContext.execute("PF('confirm_duplicatedrfq_submit_dialog').show()");
		    				confirmGoNextFlag=true;
		    				return event.getOldStep();
		                }	
				 }
             			
				
				
			}
			// added by damon@20170220
			else if (event.getOldStep().equals("drms_match") && event.getNewStep().equals("basicDetails")) {
				logInfo("FROM DRMS MATCH TO BASICDETAILS PAGE");
				confirmGoNextFlag=false;
				for (RfqItemVO rfqItem : rfqItems) {
					ClearDataAutoFillToRfqItem(rfqItem);
				}
				
				//showDuplicatedItemFlag="1";
				
			
			}
			// step 2
			else if (event.getOldStep().equals("drms_match") && event.getNewStep().equals("confirmation")) {
				logInfo("FROM DRMS MATCH TO CONFORMATION PAGE");
				logInfo("the total size of rfqItems on step two is:"+rfqItems.size());
				for (RfqItemVO rfqItem : rfqItems) {
					logInfo("DRMSPage-CheckPoint one : ["+rfqItem.getItemNumber()+"]" + rfqItem.getMfr() + "/" + rfqItem.getRequiredPartNumber()
							+ " - " + rfqItem.getSoldToCustomerNumber());
				}
				clearSearchPart();
				boolean errorFound = false;

				String rfqMandatoryMessage = "";
				for (RfqItemVO rfqItem : rfqItems) {
					if (rfqItem.getStage() == null) {
						if (QuoteUtil.isEmpty(rfqItem.getMfr()))
							rfqMandatoryMessage += "MFR, ";
						if (QuoteUtil.isEmpty(rfqItem.getRequiredPartNumber()))
							rfqMandatoryMessage += ResourceMB.getText("wq.label.requested") + " P/N, ";
						if (QuoteUtil.isEmpty(rfqItem.getRequiredQty()))
							rfqMandatoryMessage += ResourceMB.getText("wq.label.reqQty") + ", ";
					}
				}
				if (!QuoteUtil.isEmpty(rfqMandatoryMessage)) {
					FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_WARN,
							ResourceMB.getText("wq.message.mandFieldError") + " :",
							rfqMandatoryMessage.substring(0, rfqMandatoryMessage.length() - 2));
					FacesContext.getCurrentInstance().addMessage("RfqSubmissionGrowl2_1", msg);
					errorFound = true;
				}

				// For issue 1041. added the validation for SPR on 17 Oct 2013.
				//Fixed the auto delete SPR tick logic at the second step by DamonChen@20190128
				String sprValidationMessage = "";
				for (RfqItemVO rfqItem : rfqItems) {
					if (rfqItem.getTargetResale() != null && !QuoteUtil.isEmpty(rfqItem.getTargetResale())) {
						Double tr = Double.valueOf(rfqItem.getTargetResale());
						if (rfqItem.getRfqCurrMinSellPrice() != null && rfqItem.getRfqCurrMinSellPrice() > ZERO) {
							if (tr >= rfqItem.getRfqCurrMinSellPrice()) {
								if (rfqItem.isSpecialPriceIndicator()) {
									rfqItem.setSpecialPriceIndicator(false);
									sprValidationMessage = "sprValidationMessage";
								}
							}
						}

						if (rfqItem.getRfqCurrSalesCost() != null && rfqItem.getRfqCurrSalesCost() > ZERO) {
							if (tr >= rfqItem.getRfqCurrSalesCost()) {
								if (rfqItem.isSpecialPriceIndicator()) {
									rfqItem.setSpecialPriceIndicator(false);
									sprValidationMessage = "sprValidationMessage";
								}
							}

						}
					}

				}
				if (!QuoteUtil.isEmpty(sprValidationMessage)) {
					FacesMessage msg = new FacesMessage(ResourceMB.getText("wq.message.note") + " : ",
							ResourceMB.getText("wq.message.sprRemoved"));
					FacesContext.getCurrentInstance().addMessage("RfqSubmissionGrowl_confirm_1", msg);
				}

				// WebQuote 2.2 enhancement : fields changes.
				String designInCatValidationMessage = "";
				for (RfqItemVO rfqItem : rfqItems) {
					if (rfqItem.getDrmsNumber() == null || rfqItem.getDrmsNumber() == 0l) {
						if (rfqItem.getDesignInCat() != null
								&& QuoteSBConstant.DESIGN_IN_CAT_LOCAL_DR.equalsIgnoreCase(rfqItem.getDesignInCat())) {

							designInCatValidationMessage = ResourceMB.getText("wq.message.row") + "#<"
									+ rfqItem.getItemNumber() + ">: "
									+ ResourceMB.getText("wq.error.drmsProjectSubmission");
						}

					}

					if (QuoteUtil.isEmpty(rfqItem.getSupplierDrNumber())) {
						if (rfqItem.getDesignInCat() != null
								&& QuoteSBConstant.DESIGN_IN_CAT_BMT_DR.equalsIgnoreCase(rfqItem.getDesignInCat())) {
							designInCatValidationMessage = ResourceMB.getText("wq.message.row") + "#<"
									+ rfqItem.getItemNumber() + ">: " + ResourceMB.getText("wq.error.supplierDRNull");
						}
					}

					// added by Damon
					if (rfqItem.isBmtCheckedFlag()) {

						if (QuoteUtil.isEmpty(rfqItem.getDesignRegion()) || (rfqItem.getDesignRegion().equals("ASIA"))
								|| (rfqItem.getDesignRegion().equals(QuoteConstant.DEFAULT_SELECT))) {
							designInCatValidationMessage = designInCatValidationMessage + "  "
									+ ResourceMB.getText("wq.message.row") + "#<" + rfqItem.getItemNumber() + ">:"
									+ ResourceMB.getText("wq.message.designRegionCheckError");

						}

					}
					// validate DesignRgion is not null,but the DesignLation is
					// null.Can't pass
					if (QuoteUtil.isEmpty(rfqItem.getDesignLocation())) {
						if ((!QuoteUtil.isEmpty(rfqItem.getDesignRegion()))
								&& (!rfqItem.getDesignRegion().equals(QuoteConstant.DEFAULT_SELECT))) {
							designInCatValidationMessage = ResourceMB.getText("wq.message.row") + "#<"
									+ rfqItem.getItemNumber() + ">: "
									+ ResourceMB.getText("wq.message.designLocDesignRegionError");
						}
					}


				}
				if (!QuoteUtil.isEmpty(designInCatValidationMessage)) {

					FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_WARN,
							ResourceMB.getText("wq.message.note") + " : ", designInCatValidationMessage);
					FacesContext.getCurrentInstance().addMessage("RfqSubmissionGrowl2_1", msg);
					errorFound = true;
				}

/*				try {
					quoteItemVoChecking();
				} catch (Exception e) {
					LOGGER.log(Level.SEVERE, "Error occured in flow, Reason for failure: "
							+ MessageFormatorUtil.getParameterizedStringFromException(e), e);
					errorFound = true;
				}*/

				if (errorFound) {
					FacesContext context = FacesContext.getCurrentInstance();
					RequestContext requestContext = RequestContext.getCurrentInstance();
					CommandButton nextBtn = (CommandButton) context.getViewRoot()
							.findComponent(":form_rfqSubmission:nextButton");
					nextBtn.setStyleClass("nextButton");
					requestContext.update("form_rfqSubmission:nextButton");

					return event.getOldStep();
				}

				for (RfqItemVO rfqItem : rfqItems) {
				/*	logInfo("DRMSPage-CheckPoint two : " + rfqItem.getMfr() + "/" + rfqItem.getRequiredPartNumber()
							+ " - " + rfqItem.getSoldToCustomerNumber());*/
					
                       handleDisplayOrderQties(rfqItem);
				}
				extractDuplicatedRfqItem(duplicatedrfqItems,tmpMap,rfqItems);

			}
			// step 3
			else if (event.getOldStep().equals("confirmation") && event.getNewStep().equals("drProjectReport")) {
			}
		}

		// logger.log(Level.INFO, "PERFORMANCE END - onFlowProcess()");

		return event.getNewStep();
	}


	/**   
	* @Description: TODO
	* @author 042659
	* @param rfqItem       
	* @return void    
	* @throws  
	*/  
	private void handleDisplayOrderQties(RfqItemVO rfqItem) {
		// TODO Auto-generated method stub
		if (rfqItem.getOrderQties() == null || rfqItem.getOrderQties().isEmpty()) {
			return;

		}

		List<QuantityBreakPrice> tmpOrderQties = new ArrayList<QuantityBreakPrice>();
		// tmpOrderQties.addAll(rfqItem.getOrderQties());
		for (QuantityBreakPrice q : rfqItem.getOrderQties()) {

			QuantityBreakPrice qbp = new QuantityBreakPrice();
			qbp.setId(q.getId());
			qbp.setQtyRange(q.getQtyRange());
			qbp.setQuantityBreak(q.getQuantityBreak());
			qbp.setSalesCost(q.getSalesCost());
			qbp.setVersion(q.getVersion());
			qbp.setQuantityBreakPrice(q.getQuantityBreakPrice());
			qbp.setSuggestedResale(q.getSuggestedResale());
			tmpOrderQties.add(qbp);
		}

		rfqItem.setDisplayOrderQties(tmpOrderQties);
		Money.convertAsToCurrAmtWithExrates(rfqItem.getDisplayOrderQties(), Currency.getByCurrencyName(rfqItem.getBuyCurr()),
				Currency.getByCurrencyName(rfqItem.getRfqCurr()), new BizUnit(rfqItem.getRfqHeaderVO().getBizUnit()), rfqItem.getSoldToCustomer(),
				new Date(), Currency.getByCurrencyName(rfqItem.getRfqCurr()));

	}

	/**   
	* @Description: TODO
	* @author 042659
	* @param duplicatedrfqItems2
	* @param tmpMap2
	* @param rfqItems2      
	* @return void    
	* @throws  
	*/  
	private void extractDuplicatedRfqItem(List<RfqItemVO> duplicatedrfqItems, HashMap<String, RfqItemVO> tmpMap, List<RfqItemVO> rfqItems) {
		// TODO Auto-generated method stub

		if(tmpMap == null){
			tmpMap=new HashMap<String,RfqItemVO>();
		}else{
			tmpMap.clear();
		}
		if(duplicatedrfqItems == null){
			duplicatedrfqItems=new ArrayList<RfqItemVO>();
		}else{
			duplicatedrfqItems.clear();
		}
		
		rfqItemValidatorSB.extractDuplicatedRfqItems(duplicatedrfqItems,tmpMap,rfqItems);
	}

	/*
	 * Util Function
	 */
	// =============================================================================================================
	public void changeMfr(int itemNumber) {

		// logger.log(Level.INFO, "PERFORMANCE START - changeMfr()");

		RfqItemVO rfqItem = getRfqItemByItemNumber(itemNumber);
		rfqItem.setRequiredPartNumber(null);

		// logger.log(Level.INFO, "PERFORMANCE END - changeMfr()");
	}

	public void updateCopyToCs() {

		// logger.log(Level.INFO, "PERFORMANCE START - updateCopyToCs()");
		// Material restructure and quote_item delinkage. changed on 10 Apr
		// 2014.
		String copyToCs = "";
		String copyToCsName = "";
		if (this.selectedCsList != null) {
			for (User user : selectedCsList) {
				copyToCs += user.getEmployeeId() + ";";
				copyToCsName += user.getName() + ";";
			}
		}
		rfqHeader.setCopyToCs(copyToCs);
		rfqHeader.setCopyToCsName(copyToCsName);

		// logger.log(Level.INFO, "PERFORMANCE END - updateCopyToCs()");
	}

	public void removeEmptyRfq() {

		List<RfqItemVO> returnList = new ArrayList<RfqItemVO>();
		for (Iterator<RfqItemVO> itr = rfqItems.iterator(); itr.hasNext();) {
			RfqItemVO rfqItem = itr.next();
			if (!QuoteUtil.isEmpty(rfqItem.getRequiredPartNumber()))
				returnList.add(rfqItem);
		}
		this.itemNumberCount = 0;
		for (RfqItemVO rfqItem : returnList) {
			rfqItem.setItemNumber(++itemNumberCount);
			returnList.set(rfqItem.getItemNumber() - 1, rfqItem);
		}
		rfqItems = returnList;

		// logger.log(Level.INFO, "PERFORMANCE END - removeEmptyRfq()");
	}

	private RfqItemVO getRfqItemByItemNumber(int itemNumber) {
		return rfqItems.get(itemNumber - 1);
	}

	public void updateItemNumberForSearchPart(int itemNumber) {
		// logger.log(Level.INFO,
		// "PERFORMANCE START - updateItemNumberForSearchPart()");
		// logger.info("updateItemNumberForSearchPart start");
		logInfo("updateItemNumberForSearchPart before this.itemNumberForPartSearch :" + this.itemNumberForPartSearch);
		this.itemNumberForPartSearch = itemNumber;
		resetPartSearchPanel();
		RfqItemVO tempVo = (RfqItemVO) rfqItems.get(itemNumber - 1);
		String tempPart = tempVo.getRequiredPartNumber();
		if (tempPart != null && !tempPart.isEmpty()) {
			setValidationSearchFullMfrPartNumber(tempPart);
		}

		logInfo("updateItemNumberForSearchPart after this.itemNumberForPartSearch :" + this.itemNumberForPartSearch);
		// logger.info("updateItemNumberForSearchPart end");
		// logger.log(Level.INFO,
		// "PERFORMANCE END - updateItemNumberForSearchPart()");
	}

	public void resetPartSearchPanel() {
		logInfo("resetPartSearchPanel start");
		setValidationSearchMfr(QuoteSBConstant.ALL);
		setValidationSearchFullMfrPartNumber("");
		if (selectedSearchParts != null && selectedSearchParts.size() > 0) {
			selectedSearchParts.clear();
			selectedSearchParts = new ArrayList<PricerInfo>();
		}
		if (searchParts != null && searchParts.size() > 0) {
			searchParts.clear();
		}
		partModel = new PartModel(searchParts);
		logInfo("resetPartSearchPanel end");
	}

	public void uploadAttachment(FileUploadEvent event) {
		// logger.log(Level.INFO, "PERFORMANCE START - uploadAttachment()");
		long fileSize = event.getFile().getSize();
		if (fileSize > 5 * 1024 * 1024) {
			FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "",
					ResourceMB.getText("wq.message.uplodAttchmntError") + ".");
			FacesContext.getCurrentInstance().addMessage("messages_1", msg);
		} else {
			Attachment attachment = new Attachment();
			String fileName = event.getFile().getFileName().substring(
					event.getFile().getFileName().lastIndexOf("\\") + 1, event.getFile().getFileName().length());
			attachment.setFileName(fileName);
			attachment.setType(QuoteSBConstant.ATTACHMENT_TYPE_QUOTE_ITEM);
			attachment.setFileImage(QuoteUtil.getUploadFileContent(event.getFile()));
			RfqItemVO rfqItem = getRfqItemByItemNumber(this.itemNumberForAttachment);
			List<Attachment> attachments = rfqItem.getAttachments();
			if (attachments == null)
				attachments = new ArrayList<Attachment>();
			attachments.add(attachment);
			rfqItem.setAttachments(attachments);
			rfqItems.set(rfqItem.getItemNumber() - 1, rfqItem);

			RequestContext requestContext = RequestContext.getCurrentInstance();
			requestContext.update("form_rfqSubmission:datatable_basicDetails_rfqSubmission");

			FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "",
					ResourceMB.getText("wq.error.attchUploadSucess") + ".");
			FacesContext.getCurrentInstance().addMessage("messages_1", msg);
		}
		// logger.log(Level.INFO, "PERFORMANCE END - uploadAttachment()");
	}

	public void uploadRfqBatchFile() {

		// logger.log(Level.INFO, "PERFORMANCE START - uploadRfqBatchFile()");

		if (rfqExcel != null) {

			long uploadfileSizeLimit = sysMaintSB.getUploadFileSizeLimit();

			if (rfqExcel.getSize() > uploadfileSizeLimit) {
				Object[] objArr = { uploadfileSizeLimit };
				String message = ResourceMB.getParameterizedString("wq.error.fileSizeError", objArr);
				FacesContext.getCurrentInstance().addMessage("RfqSubmissionGrowl_1",
						new FacesMessage(FacesMessage.SEVERITY_WARN, message, ""));
				return;
			}

			readBatchRfqExcelForm();
			// validateRfqs();
		}

		// logger.log(Level.INFO, "PERFORMANCE END - uploadRfqBatchFile()");
	}

	public void fillInEmptyFieldFromHeader() {

		// logger.log(Level.INFO,
		// "PERFORMANCE START - fillInEmptyFieldFromHeader()");
		for (RfqItemVO rfqItem : rfqItems) {
			if (QuoteUtil.isEmpty(rfqItem.getSoldToCustomerNumber()))
				rfqItem.setSoldToCustomerNumber(rfqHeader.getSoldToCustomerNumber());
			if (QuoteUtil.isEmpty(rfqItem.getSoldToCustomerName()))
				rfqItem.setSoldToCustomerName(rfqHeader.getSoldToCustomerName());
			if (QuoteUtil.isEmpty(rfqItem.getCustomerType()))
				rfqItem.setCustomerType(rfqHeader.getCustomerType());
			if (QuoteUtil.isEmpty(rfqItem.getEndCustomerNumber())) {
				rfqItem.setEndCustomerNumber(rfqHeader.getEndCustomerNumber());
			}
			if (QuoteUtil.isEmpty(rfqHeader.getEndCustomerName())) {
				rfqItem.setEndCustomerName(rfqHeader.getEndCustomerName());
			}
			if (QuoteUtil.isEmpty(rfqItem.getEndCustomerName()))
				rfqItem.setEndCustomerName(rfqHeader.getEndCustomerName());
			if (QuoteUtil.isEmpty(rfqItem.getShipToCustomerNumber()))
				rfqItem.setShipToCustomerNumber(rfqHeader.getShipToCustomerNumber());
			if (QuoteUtil.isEmpty(rfqItem.getShipToCustomerName()))
				rfqItem.setShipToCustomerName(rfqHeader.getShipToCustomerName());
			if (QuoteUtil.isEmpty(rfqItem.getApplication()))
				rfqItem.setApplication(rfqHeader.getApplication());
			if (QuoteUtil.isEmpty(rfqItem.getEnquirySegment()))
				rfqItem.setEnquirySegment(rfqHeader.getEnquirySegment());
			if (QuoteUtil.isEmpty(rfqItem.getProjectName()))
				rfqItem.setProjectName(rfqHeader.getProjectName());
			if (QuoteUtil.isEmpty(rfqItem.getDesignLocation()))
				rfqItem.setDesignLocation(rfqHeader.getDesignLocation());
			if (QuoteUtil.isEmpty(rfqItem.getDesignRegion()))
				rfqItem.setDesignRegion(rfqHeader.getDesignRegion());
			if (QuoteUtil.isEmpty(rfqItem.getMpSchedule()))
				rfqItem.setMpSchedule(rfqHeader.getMpSchedule());
			if (QuoteUtil.isEmpty(rfqItem.getPpSchedule()))
				rfqItem.setPpSchedule(rfqHeader.getPpSchedule());

			// WebQuote 2.2 enhancement : fields changes.
			// if (rfqHeader.isOrderOnHand())
			// rfqItem.setOrderOnHand(true);
			if (QuoteUtil.isEmpty(rfqItem.getQuoteType()))
				rfqItem.setQuoteType(rfqHeader.getQuoteType());
			if (QuoteUtil.isEmpty(rfqItem.getDesignInCat()))
				rfqItem.setDesignInCat(rfqHeader.getDesignInCat());
			if (rfqHeader.isLoa())
				rfqItem.setLoa(true);
			
			/*if (QuoteUtil.isEmpty(rfqItem.getRfqCurr()))*/
				rfqItem.setRfqCurr(rfqHeader.getRfqCurr());
		}

		// logger.log(Level.INFO,
		// "PERFORMANCE END - fillInEmptyFieldFromHeader()");
	}

	public void createTenRfqDetail() {

		// logger.log(Level.INFO, "PERFORMANCE START - createTenRfqDetail()");

		for (int i = 0; i < 10; i++) {
			RfqItemVO rfqItem = new RfqItemVO();
			rfqItem.setItemNumber(++itemNumberCount);
			rfqItem.setValidPartNumber(true);
			rfqItems.add(rfqItem);
		}
		fillInEmptyFieldFromHeader();
		
		

		// logger.log(Level.INFO, "PERFORMANCE END - createTenRfqDetail()");
	}

	public void createTenRfqDetailWithoutFillHeader() {

		// logger.log(Level.INFO, "PERFORMANCE START - createTenRfqDetail()");

		for (int i = 0; i < 10; i++) {
			RfqItemVO rfqItem = new RfqItemVO();
			rfqItem.setItemNumber(++itemNumberCount);
			rfqItem.setValidPartNumber(true);
			rfqItems.add(rfqItem);
		}

		// logger.log(Level.INFO, "PERFORMANCE END - createTenRfqDetail()");
	}

	public void deleteAllRfqDetail() {

		rfqItems.clear();
		rfqItems = new ArrayList<RfqItemVO>();
		itemNumberCount = 0;

		for (int i = 0; i < 10; i++) {
			RfqItemVO rfqItem = new RfqItemVO();
			rfqItem.setItemNumber(++itemNumberCount);
			rfqItem.setValidPartNumber(true);
			rfqItems.add(rfqItem);
		}

	}

	public void saveDraft() {

		// logger.log(Level.INFO, "PERFORMANCE START - saveDraft()");
		String message = null;
		if (QuoteUtil.isEmpty(rfqHeader.getSalesEmployeeNumber())
				|| QuoteUtil.isEmpty(rfqHeader.getSalesEmployeeName())) {
			String fields = "";
			if (QuoteUtil.isEmpty(rfqHeader.getSalesEmployeeNumber())) {
				message = ResourceMB.getText("wq.label.salesmanEmpCode");
				fields += " " + message;
			}
			if (QuoteUtil.isEmpty(rfqHeader.getSalesEmployeeName())) {
				if (fields.length() > 0)
					fields += ", ";
				message = ResourceMB.getText("wq.label.empName");
				fields += message;
			}
			FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_WARN,
					ResourceMB.getText("wq.message.mandFieldError") + " :", fields);
			FacesContext.getCurrentInstance().addMessage("RfqSubmissionGrowl_1", msg);
		} else {

			fillInEmptyFieldFromHeader();
			for (RfqItemVO rfqItem : rfqItems) {
				rfqItem.setStage(QuoteSBConstant.QUOTE_STAGE_DRAFT);
				rfqItem.setStatus(QuoteSBConstant.RFQ_STATUS_DQ);
				rfqItems.set(rfqItem.getItemNumber() - 1, rfqItem);
			}
			try {
				quote = submitDraftRfqs();
			} catch (Exception e) {

				LOGGER.log(Level.SEVERE, "Exception in submitting the RFQ Draft, exception message : " + e.getMessage(),
						e);
			}
			continueRfqSubmission(true, quote.getId());
		}

		// logger.log(Level.INFO, "PERFORMANCE END - saveDraft()");
	}

	public void saveToBeNewPartItemNumber(int itemNumber) {

		// logger.log(Level.INFO,
		// "PERFORMANCE START - saveToBeNewPartItemNumber()");

		toBeNewPartItemNumber = itemNumber;
		RfqItemVO rfqItem = getRfqItemByItemNumber(itemNumber);
		toBeNewPartNumber = rfqItem.getRequiredPartNumber();
		toBeNewMfr = rfqItem.getMfr();

		// logger.log(Level.INFO,
		// "PERFORMANCE END - saveToBeNewPartItemNumber()");
	}

	public void downloadRfqSubmissionResultReport() {

		// logger.log(Level.INFO,
		// "PERFORMANCE START - downloadRfqSubmissionResultReport()");

		List<List<String>> data = DownloadUtil.convertQuoteItemToReportVO(resultQuoteItems,this.isQco);
		List<String> header = new ArrayList<String>();
		header.add("Form #");
		header.add("Avnet Quote #");
		header.add("MFR");
		header.add("Requested P/N");
		header.add("Avnet Quoted P/N");
		header.add("Required Qty");
		header.add("EAU");
		header.add("Sales Cost Part");	
		header.add("RFQ Curr");	
		header.add("Target Resale");
		header.add("sales Cost");
		header.add("Suggested Resale");
		header.add("Avnet Quoted Price");
		header.add("Status");
		header.add("SPR");
		header.add("Quote Stage");
		rfqSubmissionResultReport = DownloadUtil.generateExcel(header, data, "Download.xlsx");

		// logger.log(Level.INFO,
		// "PERFORMANCE END - downloadRfqSubmissionResultReport()");
	}

	public void downloadDrmsResultReport() {

		// logger.log(Level.INFO,
		// "PERFORMANCE START - downloadDrmsResultReport()");

		List<List<String>> data = DownloadUtil.convertDrProjectToReportVO(restOfDrProjects);
		List<String> header = new ArrayList<String>();
		header.add("Sales Org");
		header.add("Supplier Dr#");
		header.add("Project Name");
		header.add("Description");
		header.add("Part Mask");
		header.add("Project ID");
		header.add("Neda ID");
		header.add("Creation Date");
		header.add("FAE");
		header.add("Group Project ID");
		header.add("Supplier Name");
		header.add("Core ID");
		header.add("Dr Price");
		header.add("Qty Per Board");
		header.add("Production Date");
		header.add("Assembly Qty");
		header.add("Din$");
		// header.add("Additional Info 3"); UPDATED BY Damon
		drmsResultReport = DownloadUtil.generateExcel(header, data, "Download.xls");

		// logger.log(Level.INFO,
		// "PERFORMANCE END - downloadDrmsResultReport()");
	}

	public boolean validationRule(String gowlId) {
		// "RfqSubmissionGrowl"
		// logger.log(Level.INFO, "PERFORMANCE START - validationRule()");

		boolean hasError = false;
		try {
			for (RfqItemVO rfqItem : rfqItems) {
				if (rfqItem.isSpecialPriceIndicator()) {
					//Bryan Start
//					ValidationRuleVO validationRuleVO = ValidationRuleCacheManager
//							.getValidationRule(rfqHeader.getBizUnit(), rfqItem.getMfr());
					ValidationRuleVO validationRuleVO = cacheUtil
							.getValidationRule(rfqHeader.getBizUnit(), rfqItem.getMfr());
					//Bryan End
					if (validationRuleVO != null) {
						if (validationRuleVO.isApplication()) {
							if (QuoteUtil.isEmpty(rfqItem.getApplication())) {
								FacesUtil.showWarnMessage(ResourceMB.getText("wq.error.MandateSPR") + ":",
										ResourceMB.getText("wq.message.appRow") + " " + rfqItem.getItemNumber() + ".",
										gowlId);
								hasError = true;
								throw (new Exception(ResourceMB.getText("wq.error.MandateSPR")));
							}
						}
						if (validationRuleVO.isCompetitorInformation()) {
							if (QuoteUtil.isEmpty(rfqItem.getCompetitorInformation())) {
								FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_WARN,
										ResourceMB.getText("wq.error.MandateSPR") + ":",
										ResourceMB.getText("wq.message.comptInfo") + " "
												+ ResourceMB.getText("wq.message.atRow") + " " + rfqItem.getItemNumber()
												+ ".");
								FacesContext.getCurrentInstance().addMessage(gowlId, msg);
								hasError = true;
								throw (new Exception());
							}
						}
						if (validationRuleVO.isDesignLocation()) {
							if (QuoteUtil.isEmpty(rfqItem.getDesignLocation())) {
								FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_WARN,
										ResourceMB.getText("wq.error.MandateSPR") + ":",
										ResourceMB.getText("wq.label.designLoc") + " "
												+ ResourceMB.getText("wq.message.atRow") + " " + rfqItem.getItemNumber()
												+ ".");
								FacesContext.getCurrentInstance().addMessage(gowlId, msg);
								hasError = true;
								throw (new Exception());
							}
						}
						if (validationRuleVO.isEau()) {
							if (QuoteUtil.isEmpty(rfqItem.getEau())) {
								FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_WARN,
										ResourceMB.getText("wq.error.MandateSPR") + ":",
										"EAU" + " " + ResourceMB.getText("wq.message.atRow") + " "
												+ rfqItem.getItemNumber() + ".");
								FacesContext.getCurrentInstance().addMessage(gowlId, msg);
								hasError = true;
								throw (new Exception());
							}
						}
						if (validationRuleVO.isEndCustomerCode()) {

							if (QuoteUtil.isEmpty(rfqItem.getEndCustomerNumber())) {
								FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_WARN,
										ResourceMB.getText("wq.error.MandateSPR") + ":",
										ResourceMB.getText("wq.label.EndCustomerCode") + " "
												+ ResourceMB.getText("wq.message.atRow") + " " + rfqItem.getItemNumber()
												+ ".");
								FacesContext.getCurrentInstance().addMessage(gowlId, msg);
								hasError = true;
								throw (new Exception());
							} else {
								// 2.2 enhancment
								boolean isValidEC = customerSB.isValidEndCustomer(rfqItem.getEndCustomerNumber());
								if (!isValidEC) {
									FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_WARN,
											ResourceMB.getText("wq.error.MandateSPR") + ":",
											ResourceMB.getText("wq.message.row") + " <#" + rfqItem.getItemNumber()
													+ "> - " + ResourceMB.getText("wq.message.invalidEndCust") + " : <"
													+ rfqItem.getEndCustomerNumber() + ">");
									FacesContext.getCurrentInstance().addMessage(gowlId, msg);
									rfqItem.setEndCustomerNumber("");
									rfqItem.setEndCustomerName("");
									hasError = true;
									throw (new Exception());
								}
							}
						}
						if (validationRuleVO.isMpSchedule()) {
							if (QuoteUtil.isEmpty(rfqItem.getMpSchedule())) {
								FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_WARN,
										ResourceMB.getText("wq.error.MandateSPR") + ":",
										ResourceMB.getText("wq.label.MpSchedule") + " "
												+ ResourceMB.getText("wq.message.atRow") + " " + rfqItem.getItemNumber()
												+ ".");
								FacesContext.getCurrentInstance().addMessage(gowlId, msg);
								hasError = true;
								throw (new Exception());
							}
						}
						if (validationRuleVO.isPpSchedule()) {
							if (QuoteUtil.isEmpty(rfqItem.getPpSchedule())) {
								FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_WARN,
										ResourceMB.getText("wq.error.MandateSPR") + ":",
										ResourceMB.getText("wq.label.ppSchedule") + " "
												+ ResourceMB.getText("wq.message.atRow") + " " + rfqItem.getItemNumber()
												+ ".");
								FacesContext.getCurrentInstance().addMessage(gowlId, msg);
								hasError = true;
								throw (new Exception());
							}
						}
						if (validationRuleVO.isProjectName()) {
							if (QuoteUtil.isEmpty(rfqItem.getProjectName())) {
								FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_WARN,
										ResourceMB.getText("wq.error.MandateSPR") + ":",
										ResourceMB.getText("wq.label.projectName") + " "
												+ ResourceMB.getText("wq.message.atRow") + " " + rfqItem.getItemNumber()
												+ ".");
								FacesContext.getCurrentInstance().addMessage(gowlId, msg);
								hasError = true;
								throw (new Exception());
							}
						}
						if (validationRuleVO.isShipToCode()) {
							if (QuoteUtil.isEmpty(rfqItem.getShipToCustomerNumber())
									&& QuoteUtil.isEmpty(rfqItem.getShipToCustomerName())) {
								FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_WARN,
										ResourceMB.getText("wq.error.MandateSPR") + ":",
										ResourceMB.getText("wq.label.shipCustNameNum") + " "
												+ ResourceMB.getText("wq.message.atRow") + " " + rfqItem.getItemNumber()
												+ ".");
								FacesContext.getCurrentInstance().addMessage(gowlId, msg);
								hasError = true;
								throw (new Exception());
							}
						}
						if (validationRuleVO.isSupplierDrNumber()) {
							String designInCat = rfqItem.getDesignInCat();

							/**
							 * if SPR rule indicate that the Supplier DR# is
							 * mandatory, add extra process to check the Design
							 * In Cat if Design In Cat is not BMT DR, skip the
							 * SPR Rule for the Supplier DR# at the RFQ
							 */
							if (!QuoteUtil.isEmpty(designInCat)
									&& !designInCat.equalsIgnoreCase(QuoteSBConstant.DESIGN_IN_CAT_NON_BMT_DR)
									&& QuoteUtil.isEmpty(rfqItem.getSupplierDrNumber())) {
								FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_WARN,
										ResourceMB.getText("wq.error.MandateSPR") + ":",
										ResourceMB.getText("wq.message.supplierDR") + " "
												+ ResourceMB.getText("wq.message.atRow") + " " + rfqItem.getItemNumber()
												+ ".");
								FacesContext.getCurrentInstance().addMessage(gowlId, msg);
								hasError = true;
								throw (new Exception());
							}
						}
						if (validationRuleVO.isTargetResale()) {
							if (rfqItem.getTargetResale() == null) {
								FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_WARN,
										ResourceMB.getText("wq.error.MandateSPR") + ":",
										ResourceMB.getText("wq.label.trgResale") + " "
												+ ResourceMB.getText("wq.message.atRow") + " " + rfqItem.getItemNumber()
												+ ".");
								FacesContext.getCurrentInstance().addMessage(gowlId, msg);
								hasError = true;
								throw (new Exception());
							}
						}
					}

					// It's rollback, by damon@20160401
					/*
					 * if(rfqItem.isSpecialPriceIndicator()){
					 * if(!QuoteUtil.isEmpty(rfqItem.getDesignRegion()) &&
					 * (!rfqItem.getDesignRegion().equals(QuoteConstant.
					 * DEFAULT_SELECT))
					 * &&(!rfqItem.getDesignRegion().equals("ASIA"))){
					 * FacesMessage msg = new
					 * FacesMessage(FacesMessage.SEVERITY_WARN,"Note : ",
					 * "Row#<"+rfqItem.getItemNumber()
					 * +">:SPR validation is NOT apply to BMT case. Please uncheck SPR checkbox before proceed."
					 * ); FacesContext.getCurrentInstance().addMessage(gowlId,
					 * msg); hasError = true; throw (new Exception()); }
					 * 
					 * }
					 */

				}
			}
		} catch (Exception ex) {
			LOGGER.log(Level.SEVERE,
					"Exception in invalidating the rule for id: " + gowlId + ", exception message : " + ex.getMessage());
		}

		// logger.log(Level.INFO, "PERFORMANCE END - validationRule()");
		return hasError;
	}

	// =============================================================================================================
	/*
	 * END OF Util Function
	 */

	/*
	 * Call From xhtml
	 */
	// ================================================================================================================
	public void updateApplication() {

		// logger.log(Level.INFO, "PERFORMANCE START - updateApplication()");

		if (updateDataTable) {
			for (RfqItemVO rfqItem : rfqItems) {
				if (rfqItem != null) {
					rfqItem.setApplication(rfqHeader.getApplication());
					rfqItems.set(rfqItem.getItemNumber() - 1, rfqItem);
				}
			}
		}
		// logger.log(Level.INFO, "PERFORMANCE END - updateApplication()");
	}

	public void updateProjectName() {

		// logger.log(Level.INFO, "PERFORMANCE START - updateProjectName()");

		if (updateDataTable) {
			for (RfqItemVO rfqItem : rfqItems) {
				if (rfqItem != null) {
					rfqItem.setProjectName(rfqHeader.getProjectName());
					rfqItems.set(rfqItem.getItemNumber() - 1, rfqItem);
				}
			}
		}

		// logger.log(Level.INFO, "PERFORMANCE END - updateProjectName()");
	}

	public void updateEnquirySegment() {

		// logger.log(Level.INFO, "PERFORMANCE START - updateEnquirySegment()");

		if (updateDataTable) {
			for (RfqItemVO rfqItem : rfqItems) {
				if (rfqItem != null) {
					rfqItem.setEnquirySegment(rfqHeader.getEnquirySegment());
					rfqItems.set(rfqItem.getItemNumber() - 1, rfqItem);
				}
			}
		}

		// logger.log(Level.INFO, "PERFORMANCE END - updateEnquirySegment()");
	}

	public void updateMpSchedule() {

		// logger.log(Level.INFO, "PERFORMANCE START - updateMpSchedule()");

		if (updateDataTable) {
			for (RfqItemVO rfqItem : rfqItems) {
				if (rfqItem != null) {
					rfqItem.setMpSchedule(rfqHeader.getMpSchedule());
					rfqItems.set(rfqItem.getItemNumber() - 1, rfqItem);
				}
			}
		}

		// logger.log(Level.INFO, "PERFORMANCE END - updateMpSchedule()");
	}

	public void updatePpSchedule() {

		// logger.log(Level.INFO, "PERFORMANCE START - updatePpSchedule()");

		if (updateDataTable) {
			for (RfqItemVO rfqItem : rfqItems) {
				if (rfqItem != null) {
					rfqItem.setPpSchedule(rfqHeader.getPpSchedule());
					rfqItems.set(rfqItem.getItemNumber() - 1, rfqItem);
				}
			}
		}

		// logger.log(Level.INFO, "PERFORMANCE END - updatePpSchedule()");
	}

	public void updateLoa() {

		// logger.log(Level.INFO, "PERFORMANCE START - updateLoa()");

		if (updateDataTable) {
			for (RfqItemVO rfqItem : rfqItems) {
				if (rfqItem != null) {
					rfqItem.setLoa(rfqHeader.isLoa());
					rfqItems.set(rfqItem.getItemNumber() - 1, rfqItem);
				}
			}
		}

		// logger.log(Level.INFO, "PERFORMANCE END - updateLoa()");
	}

	public void removeRfqItem(int itemNumber) {

		// logger.log(Level.INFO, "PERFORMANCE START - removeRfqItem()");

		for (Iterator<RfqItemVO> itr = rfqItems.iterator(); itr.hasNext();) {
			RfqItemVO rfqItem = itr.next();
			if (rfqItem.getItemNumber() == itemNumber)
				itr.remove();
		}
		this.itemNumberCount = 0;
		for (int i = 0; i < rfqItems.size(); i++) {
			rfqItems.get(i).setItemNumber(++itemNumberCount);
		}

		// logger.log(Level.INFO, "PERFORMANCE END - removeRfqItem()");
	}

	public void updateEau(int itemNumber) {

		// logger.log(Level.INFO, "PERFORMANCE START - updateEau()");

		RfqItemVO rfqItem = getRfqItemByItemNumber(itemNumber);
		if (!QuoteUtil.isInteger(rfqItem.getEau())) {
			rfqItem.setEau(null);
			rfqItems.set(rfqItem.getItemNumber() - 1, rfqItem);
		}

		// logger.log(Level.INFO, "PERFORMANCE END - updateEau()");
	}

	public void updateRequiredQty(int itemNumber) {

		// logger.log(Level.INFO, "PERFORMANCE START - updateRequiredQty()");

		RfqItemVO rfqItem = getRfqItemByItemNumber(itemNumber);
		if (!QuoteUtil.isInteger(rfqItem.getRequiredQty())) {
			rfqItem.setRequiredQty(null);
			rfqItems.set(rfqItem.getItemNumber() - 1, rfqItem);
		}

		// logger.log(Level.INFO, "PERFORMANCE END - updateRequiredQty()");
	}

	public void updateTargetResale(int itemNumber) {

		// logger.log(Level.INFO, "PERFORMANCE START - updateTargetResale()");

		RfqItemVO rfqItem = getRfqItemByItemNumber(itemNumber);
		if (!QuoteUtil.isDecimal(rfqItem.getTargetResale())) {
			rfqItem.setTargetResale(null);
			rfqItems.set(rfqItem.getItemNumber() - 1, rfqItem);
		}

		// logger.log(Level.INFO, "PERFORMANCE END - updateTargetResale()");
	}

	public void checkSpecialPriceIndicator(int itemNumber) {

		// logger.log(Level.INFO,
		// "PERFORMANCE START - checkSpecialPriceIndicator()");

		updateTargetResale(itemNumber);
		RfqItemVO rfqItem = getRfqItemByItemNumber(itemNumber);
		if (rfqItem.getTargetResale() != null && rfqItem.getCost() != null) {
			Double tr = Double.valueOf(rfqItem.getTargetResale());
			if (rfqItem.getCost().compareTo(tr) > 0) {
				rfqItem.setSpecialPriceIndicator(true);
			} else {
				rfqItem.setSpecialPriceIndicator(false);
			}
			rfqItems.set(rfqItem.getItemNumber() - 1, rfqItem);
		}
		// logger.log(Level.INFO,
		// "PERFORMANCE END - checkSpecialPriceIndicator()");
	}

	public void updateNewProspectiveCustomer() {

		// logger.log(Level.INFO,
		// "PERFORMANCE START - updateNewProspectiveCustomer()");
		newCustomerTitle = ResourceMB.getText("wq.label.prospectiveCust");
		RequestContext requestContext = RequestContext.getCurrentInstance();
		requestContext.update("form_rfqSubmission:newProspectiveCustomer_dialog");
		clearNewCustProspective();
		newProspectiveCustomerType = QuoteSBConstant.CREATE_CUSTOMER_TYPE_CUSTOMER;
		newProspectiveCustomerName1 = rfqHeader.getSoldToCustomerName();

		// logger.log(Level.INFO,
		// "PERFORMANCE END - updateNewProspectiveCustomer()");
	}

	public void updateNewProspectiveEndCustomer() {

		// logger.log(Level.INFO,
		// "PERFORMANCE START - updateNewProspectiveCustomer()");
		newCustomerTitle = ResourceMB.getText("wq.label.endCust");
		RequestContext requestContext = RequestContext.getCurrentInstance();
		requestContext.update("form_rfqSubmission:newProspectiveCustomer_dialog");
		clearNewCustProspective();
		newProspectiveCustomerType = QuoteSBConstant.CREATE_CUSTOMER_TYPE_END_CUSTOMER;
		newProspectiveCustomerName1 = rfqHeader.getEndCustomerName();

		// logger.log(Level.INFO,
		// "PERFORMANCE END - updateNewProspectiveCustomer()");
	}

	public void clearNewCustProspective() {
		newProspectiveCustomerName1 = "";
		newProspectiveCustomerName2 = "";
		newProspectiveCustomerCountry = "";
		newProspectiveCustomerCity = "";
		newProspectiveCustomerAddress3 = "";
		newProspectiveCustomerAddress4 = "";
		newProspectiveCustomerSalesOrg = "";
	}

	/*
	 * find the Project By EndCustomer
	 */
	public List<String> autoCompleteProjectNameList(String key) {

		// logger.log(Level.INFO,
		// "PERFORMANCE START - autoCompleteProjectNameList()");

		itemSelect = false;
		List<String> projectNames = null;
		if (QuoteUtil.isEmpty(key)) {
			if (rfqHeader.getSoldToCustomerNumber() != null) {
				projectNames = quoteSB.findProjectNameBySoldToInQuoteHistory(rfqHeader.getSoldToCustomerNumber());
			} else {
				projectNames = new ArrayList<String>();
			}
		} else {
			projectNames = quoteSB.findProjectNameInQuoteHistory(key);
		}

		// logger.log(Level.INFO,
		// "PERFORMANCE END - autoCompleteProjectNameList()");
		return projectNames;
	}

	/*
	 * Added by Tonmy Li on 29 Jul 2013 Change the application selection box to
	 * auto complete componenent .
	 */
	public List<String> autoCompleteApplicationList(String key) {

		// logger.log(Level.INFO,
		// "PERFORMANCE START - autoCompleteApplicationList()");

		itemSelect = false;
		List<String> applicationNames = null;
		if (QuoteUtil.isEmpty(key)) {
			if (rfqHeader.getSoldToCustomerNumber() != null) {
				applicationNames = quoteSB
						.findApplicationNameBySoldToInQuoteHistory(rfqHeader.getSoldToCustomerNumber());
			} else {
				//Bryan Start
				//applicationNames = ApplicationCacheManager.getApplicationList();
				applicationNames = cacheUtil.getApplicationList();
				//Bryan ENd
			}
		} else {
			//Bryan Start
			//List<String> applicationList = ApplicationCacheManager.getApplicationList();
			List<String> applicationList = cacheUtil.getApplicationList();
			//Bryan End
			List<String> returnStrList = new ArrayList<String>();
			for (String str : applicationList) {
				if (str != null && str.toUpperCase().contains(key.toUpperCase())) {
					returnStrList.add(str);
				}
			}
			applicationNames = returnStrList;
		}

		// logger.log(Level.INFO,
		// "PERFORMANCE END - autoCompleteApplicationList()");
		return applicationNames;
	}

	public void updateFormAttachment() {
		attachments = rfqHeader.getFormAttachments();
	}

	public void updateItemNumberForAttachment(int itemNumber) {

		// logger.log(Level.INFO,
		// "PERFORMANCE START - updateItemNumberForAttachment()");

		itemNumberForAttachment = itemNumber;
		RfqItemVO rfqItem = getRfqItemByItemNumber(itemNumber);
		attachments = rfqItem.getAttachments();

		// logger.log(Level.INFO,
		// "PERFORMANCE END - updateItemNumberForAttachment()");
	}

	public void updateSelectedRfqItem(int itemNumber) {
		this.selectedRfqItem = getRfqItemByItemNumber(itemNumber);
		refreshSapPartNumber(itemNumber);
	}

	public void updateSelectedCustomerTypeInRfqItem(int itemNumber) {
		this.selectedRfqItem = getRfqItemByItemNumber(itemNumber);
		for (RfqItemVO vo : rfqItems) {
			if (vo.getSoldToCustomerNumber() != null
					&& vo.getSoldToCustomerNumber().equals(selectedRfqItem.getSoldToCustomerNumber())) {
				vo.setCustomerType(selectedRfqItem.getCustomerType());
			}
		}
	}

	public void removeAttachment() {

		// logger.log(Level.INFO, "PERFORMANCE START - removeAttachment()");

		attachments.remove(this.removeAttIndex);
		RfqItemVO rfqItem = getRfqItemByItemNumber(itemNumberForAttachment);
		rfqItem.setAttachments(attachments);
		rfqItems.set(rfqItem.getItemNumber() - 1, rfqItem);
		this.removeAttIndex = 0;
		// logger.log(Level.INFO, "PERFORMANCE END - removeAttachment()");
	}

	public void removeAttachmentForm() {

		// logger.log(Level.INFO, "PERFORMANCE START - removeAttachment()");
		List<Attachment> attachments = rfqHeader.getFormAttachments();
		attachments.remove(this.removeAttIndex);
		rfqHeader.setFormAttachments(attachments);
		this.removeAttIndex = 0;
		// logger.log(Level.INFO, "PERFORMANCE END - removeAttachment()");
	}

	public void saveInvalidRfqAsDraft() {

		// logger.log(Level.INFO,
		// "PERFORMANCE START - saveInvalidRfqAsDraft()");

		for (RfqItemVO rfqItem : rfqItems) {
			// Changed by tonmy on 23 Jul 2013
			// if(!rfqItem.isValidPartNumber()){
			if (rfqItem.getStatusColor() == 3) {
				rfqItem.setStage(QuoteSBConstant.QUOTE_STAGE_DRAFT);
				rfqItem.setStatus(QuoteSBConstant.RFQ_STATUS_DQ);
				rfqItems.set(rfqItem.getItemNumber() - 1, rfqItem);
			}
		}

		// logger.log(Level.INFO, "PERFORMANCE END - saveInvalidRfqAsDraft()");
	}

	private void fillInBatchRfqs() throws Exception {

		logInfo("PERFORMANCE START - fillInBatchRfqs()");

		logInfo("PERFORMANCE START - start to find batch Materials with " + rfqItems.size() + " rfqItems");
		
		try {
			this.applyDefaultCostIndictorOrAQLogic(rfqHeader, rfqItems,allowCustomers,"D");
		} catch (Exception e) {
			LOGGER.log(Level.SEVERE,"[" + user.getEmployeeId() + " ]"+"Get error when validateBatchRfqs," + e.getMessage(),e);
			throw e;
		}
		
		
		copyCustomerFromHeaderToALLItem(rfqItems);

		logInfo("PERFORMANCE END - fillInBatchRfqs()");
	}

	private void copyCustomerFromHeaderToALLItem(List<RfqItemVO> rfqItems2) {

		for (RfqItemVO riv : rfqItems2) {
			this.copyCustomerFromHeaderToItem(riv);
		}

	}

	public void validateRfqs() {

		// logger.log(Level.INFO, "PERFORMANCE START - validateRfqs()");

/*		for (RfqItemVO rfqItem : rfqItems) {
			validateRfq(rfqItem.getItemNumber());
		}*/
	    
		try {
			applyDefaultCostIndictorOrAQLogic(rfqHeader, rfqItems,allowCustomers,"D");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			LOGGER.log(Level.SEVERE,"[" + user.getEmployeeId() + " ]"+"Get error when validate rfqs," + e.getMessage(),e);
			FacesUtil.showWarnMessage(QuoteSBConstant.SYSTEM_ERROR, "",
					"RfqSubmissionGrowl_1");
			return;
		}
		
		copyCustomerFromHeaderToALLItem(rfqItems);

		// logger.log(Level.INFO, "PERFORMANCE END - validateRfqs()");
	}

	public void validateMfr(int itemNumber) {

		// logger.log(Level.INFO, "PERFORMANCE START - validateMfr()");

		RfqItemVO rfqItem = getRfqItemByItemNumber(itemNumber);
		if (QuoteUtil.isEmpty(rfqItem.getMfr())) {
			FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_WARN,
					ResourceMB.getText("wq.message.mfrCodeNeeded") + " :",
					ResourceMB.getText("wq.message.inputMFRCode") + ".");
			FacesContext.getCurrentInstance().addMessage("RfqSubmissionGrowl", msg);
			rfqItem.setRequiredPartNumber(null);
			rfqItems.set(itemNumber - 1, rfqItem);
		}
		if (rfqItem.getMfr() != null) {
			currentToggleMfr = rfqItem.getMfr();
		}

		// logger.log(Level.INFO, "PERFORMANCE END - validateMfr()");
	}

	public void validateSelectedRfq(int itemNumber) {
		RfqItemVO rfqItem = getRfqItemByItemNumber(itemNumber);
		if (rfqItem != null && rfqItem.getRequiredPartNumber() != null
				&& rfqItem.getRequiredPartNumber().indexOf(QuoteConstant.PART_SEPARATOR) > -1) {
			String[] requestMaterial = rfqItem.getRequiredPartNumber().split(QuoteConstant.PART_SPLIT_SEPARATOR);
			rfqItem.setMfr(requestMaterial[0].trim());
			rfqItem.setRequiredPartNumber(requestMaterial[1].trim());
			// validateRfq(rfqItem.getItemNumber());
			refreshSapPartNumber(itemNumber);
		}
		// added by damon@20160829
		fillInEmptyFieldFromHeader();
	}

	/*public void validateRfq(int itemNumber) {

		// logger.log(Level.INFO, "PERFORMANCE START - validateRfq()");

		RfqItemVO rfqItem = getRfqItemByItemNumber(itemNumber);
		if (rfqItem != null) {
			String partNumber = rfqItem.getRequiredPartNumber();
			if (null != partNumber && !QuoteUtil.isEmpty(partNumber)) {
				String mfr = null;
				if (rfqItem != null) {
					mfr = rfqItem.getMfr();
				}
				logInfo("validate " + mfr + "/" + partNumber + "/" + bizUnit.getName());

				// Material lowestCostMaterial = materialSB.findLowestCost(mfr,
				// partNumber, bizUnit);
				// Updated by Tonmy on 2013 9 Sep 2013. change the retrieving
				// cost
				// indicator logic.
				List<CostIndicatorSearchCriteriaItem> ciscItemList = new ArrayList<CostIndicatorSearchCriteriaItem>();
				CostIndicatorSearchCriteriaItem ciscItem = new CostIndicatorSearchCriteriaItem();
				ciscItem.setEndCustomerName(rfqItem.getEndCustomerName());
				ciscItem.setEndCustomerNumber(rfqItem.getEndCustomerNumber());
				ciscItem.setFullMfrPart(rfqItem.getRequiredPartNumber());
				ciscItem.setMfr(rfqItem.getMfr());
				ciscItem.setSoldToCustomerName(rfqItem.getSoldToCustomerName());
				ciscItem.setSoldToCustomerNumber(rfqItem.getSoldToCustomerNumber());
				ciscItem.setExcatPart(true);
				ciscItemList.add(ciscItem);
				CostIndicatorSearchCriteria cisc = new CostIndicatorSearchCriteria();
				cisc.setAllowCustomers(allowCustomers);
				cisc.setBizUnit(bizUnit);
				cisc.setItems(ciscItemList);
				cisc.setUser(user);
				cisc.setRequiredEmptyDetailMaterial(true);
				cisc.setRequiredContractPrice(true);
				Material lowestCostMaterial = materialSB.getProperCostIndicatorMaterial(cisc);

				if (mfr != null) {// materialSB.getStatusColor mfr must be not
									// null
					rfqItem.setStatusColor(materialSB.getStatusColor(mfr, lowestCostMaterial, bizUnit,
							manufacturerSB.isPricerByBizUnitAndMfr(bizUnit, rfqItem.getMfr())));
				}
				// boolean isFound = (lowestCostMaterial != null ? true :
				// false);
				// rfqItem.setValidPartNumber(isFound);
				if (rfqItem.getStatusColor() == 1) {
					autoFillDataToRfqItem(itemNumber, lowestCostMaterial);
				} else {
					rfqItem.setMpq(null);
					rfqItem.setMoq(null);
					rfqItem.setMov(null);
					rfqItem.setLeadTime(null);

				}

				rfqItems.set(itemNumber - 1, rfqItem);
			} else if (partNumber != null
					&& (partNumber.length() == 0 || partNumber.length() > 0 && partNumber.trim().length() == 0)) {
				rfqItem.setRequiredPartNumber(null);
				rfqItem.setMpq(null);
				rfqItem.setMoq(null);
				rfqItem.setMov(null);
				rfqItem.setLeadTime(null);
				rfqItems.set(itemNumber - 1, rfqItem);
			}
		}
		// logger.log(Level.INFO, "PERFORMANCE END - validateRfq()");
	}*/

	// ==================================================================================================================================
	/*
	 * END OF Validation
	 */

	/*
	 * Auto Fill
	 */
	// ==========================================================================================================
/*	public void autoFillDataToRfqItem(int itemNumber, Material material) {

		// logger.log(Level.INFO,
		// "PERFORMANCE START - autoFillDataToRfqItem()");

		RfqItemVO rfqItem = getRfqItemByItemNumber(itemNumber);
		rfqItem.setMaterial(material);
		autoFillDataToRfqItem(rfqItem);

		// logger.log(Level.INFO, "PERFORMANCE END - autoFillDataToRfqItem()");

	}*/

/*	private void autoFillDataToRfqItem(RfqItemVO rfqItem) {/*

		if (!QuoteUtil.isEmpty(rfqItem.getRequiredPartNumber())) {
			Material material = rfqItem.getMaterial();
			NormalPricer abookCostMD = null;
			if (material != null) {
				abookCostMD = material.getAbookMaterialDetail();
			}
			List<String> failedCostIndicator = new ArrayList<String>();
			Object costIndicator = nextCostIndicator(rfqItem, failedCostIndicator);
			for (; costIndicator != null; costIndicator = nextCostIndicator(rfqItem, failedCostIndicator)) {
				// logger.info(" found another avaialble cost indicator.");
				if (costIndicator instanceof ContractPricer) {
					ContractPricer newProperMD = (ContractPricer) costIndicator;
					// logger.info(" new avaialble cost indicator is
					// ContractPrice price
					// "+newProperMD.getCostIndicator().getName()+"||");

					// tenatively keep this part of code, can remove after
					// verifying it is not used, like in 2.3.8
					
					 * if(hasMatchedRetrictedCustomer(newProperMD.
					 * getCostIndicator().getName(),rfqItem)) {
					 * //logger.info(" restricted customer checking failed ");
					 * failedCostIndicator.add(newProperMD.getCostIndicator().
					 * getName()); continue; } else {
					 
					if (abookCostMD != null) {
						rfqItem = loadCostIndicatorInfo(rfqItem, abookCostMD, material);
					}
					rfqItem = loadContractPriceInfo(rfqItem, newProperMD);
					rfqItem.setCostIndicatorType(QuoteSBConstant.RFQ_COST_INDICATOR_TYPE_C);
					break;
					// }

				} else if (costIndicator instanceof ProgramPricer) {
					ProgramPricer newProperMD = (ProgramPricer) costIndicator;
					// logger.info(" new avaialble cost indicator is
					// ProgramMaterial price
					// "+newProperMD.getCostIndicator().getName()+"||"+newProperMD.getMinSell());
					// tenatively keep this part of code, can remove after
					// verifying it is not used, like in 2.3.8
					
					 * if(hasMatchedRetrictedCustomer(newProperMD.
					 * getCostIndicator().getName(),rfqItem)) {
					 * //logger.info(" restricted customer checking failed ");
					 * failedCostIndicator.add(newProperMD.getCostIndicator().
					 * getName()); continue; } else {
					 
					rfqItem = loadProgramCostIndicatorInfo(rfqItem, (ProgramPricer) newProperMD, material);
					rfqItem.setCostIndicatorType(QuoteSBConstant.RFQ_COST_INDICATOR_TYPE_P);
					break;
					// }

				} else if (costIndicator instanceof NormalPricer) {
					NormalPricer newProperMD = (NormalPricer) costIndicator;
					// logger.info(" new avaialble cost indicator is
					// MaterialDetail
					// price"+newProperMD.getCostIndicator().getName()+"||"+newProperMD.getNormalSell());
					if (newProperMD.getCostIndicator() != null
							&& "A-Book Cost".equalsIgnoreCase(newProperMD.getCostIndicator().getName())) {

						// logger.info(" new avaialble cost indicator is
						// MaterialDetail price is abook price , not need to
						// check restricted customer");
						rfqItem = loadCostIndicatorInfo(rfqItem, newProperMD, material);
						rfqItem.setCostIndicatorType(QuoteSBConstant.RFQ_COST_INDICATOR_TYPE_N);
						break;
					}
					// tenatively keep this part of code, can remove after
					// verifying it is not used, like in 2.3.8
					
					 * if(hasMatchedRetrictedCustomer(newProperMD.
					 * getCostIndicator().getName(),rfqItem)) {
					 * //logger.info(" restricted customer checking failed ");
					 * failedCostIndicator.add(newProperMD.getCostIndicator().
					 * getName()); continue; } else {
					 
					rfqItem = loadCostIndicatorInfo(rfqItem, newProperMD, material);
					rfqItem.setCostIndicatorType(QuoteSBConstant.RFQ_COST_INDICATOR_TYPE_N);
					break;
					// }

				}

			}
			copyCustomerFromHeaderToItem(rfqItem);
		}

	*/
		//}

	/*
	 * CUSTOMER SOURCE CODE
	 */
	// =================================================================================================
	public void createProspectiveCustomerWithForcing() {

		// logger.log(Level.INFO,
		// "PERFORMANCE START - createProspectiveCustomerWithForcing()");

		createProspectiveCustomer("Y");

		// logger.log(Level.INFO,
		// "PERFORMANCE END - createProspectiveCustomerWithForcing()");
	}

	public void createProspectiveCustomerWithoutForcing() {

		// logger.log(Level.INFO,
		// "PERFORMANCE START - createProspectiveCustomerWithoutForcing()");
		boolean isAllowInput = true;
		String allowFieldName = "";
		// Fixed issue PM1388626 on Mar 21 2014
		if (null != this.newProspectiveCustomerName1) {
			this.newProspectiveCustomerName1 = this.newProspectiveCustomerName1.trim();
		}

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
					allowFieldName = ResourceMB.getText("wq.label.custmrName");
					break;
				}
			}
		}

		String message = null;

		if (isAllowInput) {
			String errorMsg = "";
			errorMsg = ejbCommonSB.validationMessage(message, errorMsg, newProspectiveCustomerName1,
					newProspectiveCustomerCountry, newProspectiveCustomerCity, newProspectiveCustomerSalesOrg);

			logInfo("this.newProspectiveCustomerName1 = " + this.newProspectiveCustomerName1);
			logInfo("this.newProspectiveCustomerCountry = " + this.newProspectiveCustomerCountry);
			logInfo("this.newProspectiveCustomerCity = " + this.newProspectiveCustomerCity);
			logInfo("this.newProspectiveCustomerSalesOrg = " + this.newProspectiveCustomerSalesOrg);
			logInfo("errorMsg = " + errorMsg);
			logInfo("this.newProspectiveCustomerAddress3 = " + this.newProspectiveCustomerAddress3);

			if (this.newProspectiveCustomerAddress3 != null && this.newProspectiveCustomerAddress3.length() > 35) {
				message = ResourceMB.getText("wq.message.addrLenError");
				FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "", message);
				FacesContext.getCurrentInstance().addMessage("newProspectiveCustomerGrowl_1", msg);
			} else {
				if (errorMsg.length() > 0) {
					message = ResourceMB.getText("wq.message.mandFieldError");
					FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_WARN, message + " : ", errorMsg);
					FacesContext.getCurrentInstance().addMessage("newProspectiveCustomerGrowl_1", msg);
				} else {
					newProspectiveCustomerDuplicated = false;
					createProspectiveCustomer(null);
					RequestContext requestContext = RequestContext.getCurrentInstance();
					requestContext.execute("PF('confirmation_dialog').show()");
					requestContext.update("form_rfqSubmission:confirmation_dialog");
				}
			}
		} else {
			Object[] objArr = { allowFieldName };
			message = ResourceMB.getParameterizedString("wq.message.fieldValidtnError", objArr);
			FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "", message);
			FacesContext.getCurrentInstance().addMessage("newProspectiveCustomerGrowl_1", msg);
		}

		// logger.log(Level.INFO,
		// "PERFORMANCE END - createProspectiveCustomerWithoutForcing()");
	}

	public void createProspectiveCustomer(String duplicate) {

		// logger.log(Level.INFO,
		// "PERFORMANCE START - createProspectiveCustomer()");

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

			sapWebServiceSB.createProspectiveCustomer(newProspectiveCustomerType, newProspectiveCustomerCity, duplicate,
					newProspectiveCustomerCountry, newProspectiveCustomerName1, newProspectiveCustomerName2, // name2
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
				duplicatedCity = existingCustomer.getCity();
				duplicatedCountry = existingCustomer.getLand();
				if (duplicatedCountry != null && !"".equalsIgnoreCase(duplicatedCountry)) {
					//Bryan Start
					//Country tempC = CountryCacheManager.getCountry(duplicatedCountry);
					Country tempC = cacheUtil.getCountry(duplicatedCountry);
					//Bryan End
					if (tempC != null)
						duplicatedCountry = tempC.getName();
				}

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
				Customer customer = new Customer();
				customer.setDeleteFlag(false);
				customer.setCustomerNumber(duplicatedCustomerCode);
				customer.setNewCustomerFlag(true);
				customer.setCreatedOn(QuoteUtil.getCurrentTime());
				if (newProspectiveCustomerType.equalsIgnoreCase(QuoteSBConstant.CREATE_CUSTOMER_TYPE_CUSTOMER)) {
					customer.setAccountGroup(QuoteSBConstant.ACCOUNT_GROUP_PROSPECTIVE_CUSTOMER);
				} else {
					customer.setAccountGroup(QuoteSBConstant.ACCOUNT_GROUP_ENDCUSTOMER);
				}

				String customerName = "";
				if (newProspectiveCustomerName1 != null) {
					customerName += newProspectiveCustomerName1;
					customer.setCustomerName1(newProspectiveCustomerName1);
				}
				if (newProspectiveCustomerName2 != null) {
					customerName += newProspectiveCustomerName2;
					customer.setCustomerName2(newProspectiveCustomerName2);
				}

				CustomerSale customerSale = new CustomerSale();
				//Bryan Staart
//				List<SalesOrg> salesOrgs = SalesOrgCacheManager
//						.getSalesOrgByBizUnit(rfqHeader.getSalesBizUnit().getName());
				List<SalesOrg> salesOrgs = cacheUtil
						.getSalesOrgByBizUnit(rfqHeader.getSalesBizUnit().getName());
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

				customer.setCustomerSales(customerSales);
				for (int m = 0; m < customerSales.size(); m++) {
					customerSales.get(m).setCustomer(customer);
				}

				customer = customerSB.createCustomer(customer);
			}
		} catch (AppException e) {
			LOGGER.log(
					Level.SEVERE, "Exception in creating perspective customer for the value: " + duplicate
							+ " , Exception message : " + MessageFormatorUtil.getParameterizedStringFromException(e),
					e);
		}

		// logger.log(Level.INFO,
		// "PERFORMANCE END - createProspectiveCustomer()");
	}

	public void updateNewSoldToCode() {

		// logger.log(Level.INFO, "PERFORMANCE START - updateNewSoldToCode()");

		Customer customer = customerSB.findByPK(duplicatedCustomerCode);
		if (QuoteSBConstant.CREATE_CUSTOMER_TYPE_CUSTOMER.equalsIgnoreCase(newProspectiveCustomerType)) {
			rfqHeader.setSoldToCustomerNumber(customer.getCustomerNumber());
			rfqHeader.setSoldToCustomerName(getCustomerFullName(customer));
			rfqHeader.setCustomerType(null);
			if (updateDataTable) {
				for (RfqItemVO rfqItem : rfqItems) {
					if (rfqItem != null) {
						// if(QuoteUtil.isEmpty(rfqItem.getSoldToCustomerName())
						// &&
						// QuoteUtil.isEmpty(rfqItem.getSoldToCustomerNumber())){
						if (QuoteUtil.isEmpty(rfqItem.getRequiredPartNumber())) {
							rfqItem.setSoldToCustomerName(rfqHeader.getSoldToCustomerName());
							rfqItem.setSoldToCustomerNumber(rfqHeader.getSoldToCustomerNumber());
							rfqItem.setCustomerType(rfqHeader.getCustomerType());
							rfqItems.set(rfqItem.getItemNumber() - 1, rfqItem);
						}
					}
				}
			}
		} else {
			rfqHeader.setEndCustomerNumber(customer.getCustomerNumber());
			rfqHeader.setEndCustomerName(getCustomerFullName(customer));
			if (updateDataTable) {
				for (RfqItemVO rfqItem : rfqItems) {
					if (rfqItem != null) {
						// if(QuoteUtil.isEmpty(rfqItem.getSoldToCustomerName())
						// &&
						// QuoteUtil.isEmpty(rfqItem.getSoldToCustomerNumber())){
						if (QuoteUtil.isEmpty(rfqItem.getRequiredPartNumber())) {
							rfqItem.setEndCustomerName(rfqHeader.getEndCustomerName());
							rfqItem.setEndCustomerNumber(rfqHeader.getEndCustomerNumber());
							rfqItems.set(rfqItem.getItemNumber() - 1, rfqItem);
						}
					}
				}
			}
		}
		clearNewCustProspective();
		// logger.log(Level.INFO, "PERFORMANCE END - updateNewSoldToCode()");
	}

	public void copyCustomerFromHeaderToItem(RfqItemVO rfqItem) {

		// logger.log(Level.INFO,
		// "PERFORMANCE START - copyCustomerFromHeaderToItem()");

		if (QuoteUtil.isEmpty(rfqItem.getSoldToCustomerName())
				&& QuoteUtil.isEmpty(rfqItem.getSoldToCustomerNumber())) {
			rfqItem.setSoldToCustomerName(rfqHeader.getSoldToCustomerName());
			rfqItem.setSoldToCustomerNumber(rfqHeader.getSoldToCustomerNumber());
			rfqItem.setCustomerType(rfqHeader.getCustomerType());
		}
		if (QuoteUtil.isEmpty(rfqItem.getShipToCustomerName())
				&& QuoteUtil.isEmpty(rfqItem.getShipToCustomerNumber())) {
			rfqItem.setShipToCustomerName(rfqHeader.getShipToCustomerName());
			rfqItem.setShipToCustomerNumber(rfqHeader.getShipToCustomerNumber());
		}
		if (QuoteUtil.isEmpty(rfqItem.getEndCustomerName()) && QuoteUtil.isEmpty(rfqItem.getEndCustomerNumber())) {
			rfqItem.setEndCustomerName(rfqHeader.getEndCustomerName());
			rfqItem.setEndCustomerNumber(rfqHeader.getEndCustomerNumber());
		}
		rfqItems.set(rfqItem.getItemNumber() - 1, rfqItem);

		// logger.log(Level.INFO,
		// "PERFORMANCE END - copyCustomerFromHeaderToItem()");
	}

	public String extractCustomerNameFromCustomerNumberLabel(String label) {

		// logger.log(Level.INFO,
		// "PERFORMANCE START - extractCustomerNameFromCustomerNumberLabel()");

		String[] labels = label.split(QuoteConstant.AUTOCOMPLETE_SEPARATOR);
		if (labels.length == 1)
			return null;

		// logger.log(Level.INFO,
		// "PERFORMANCE END - extractCustomerNameFromCustomerNumberLabel()");

		return labels[1].trim();
	}

	public String extractCustomerNumberFromCustomerNumberLabel(String label) {

		// logger.log(Level.INFO,
		// "PERFORMANCE START -
		// extractCustomerNumberFromCustomerNumberLabel()");

		String[] labels = label.split(QuoteConstant.AUTOCOMPLETE_SEPARATOR);

		// logger.log(Level.INFO,
		// "PERFORMANCE END - extractCustomerNumberFromCustomerNumberLabel()");
		return labels[0].trim();
	}

	public String extractCustomerNumberFromCustomerNameLabel(String label) {

		// logger.log(Level.INFO,
		// "PERFORMANCE START - extractCustomerNumberFromCustomerNameLabel()");
		if (label == null) {
			return null;
		}
		String[] labels = label.split(QuoteConstant.AUTOCOMPLETE_SEPARATOR);
		if (labels.length == 1)
			return null;

		// logger.log(Level.INFO,
		// "PERFORMANCE END - extractCustomerNumberFromCustomerNameLabel()");

		return labels[1].trim();
	}

	public String extractCustomerNameFromCustomerNameLabel(String label) {

		// logger.log(Level.INFO,
		// "PERFORMANCE START - extractCustomerNameFromCustomerNameLabel()");
		if (label == null) {
			return null;
		}
		String[] labels = label.split(QuoteConstant.AUTOCOMPLETE_SEPARATOR);

		// logger.log(Level.INFO,
		// "PERFORMANCE END - extractCustomerNameFromCustomerNameLabel()");
		return labels[0].trim();
	}

	public String extractSalesNumberFromSalesNameLabel(String label) {

		// logger.log(Level.INFO,
		// "PERFORMANCE START - extractSalesNumberFromSalesNameLabel()");

		String[] labels = label.split(QuoteConstant.AUTOCOMPLETE_SEPARATOR);
		if (labels.length == 3)
			return labels[2].trim();

		// logger.log(Level.INFO,
		// "PERFORMANCE END - extractSalesNumberFromSalesNameLabel()");
		return labels[1].trim();
	}

	public String extractSalesNameFromSalesNameLabel(String label) {

		// logger.log(Level.INFO,
		// "PERFORMANCE START - extractSalesNameFromSalesNameLabel()");

		String[] labels = label.split(QuoteConstant.AUTOCOMPLETE_SEPARATOR);
		if (labels.length == 3)
			return labels[0].trim() + ", " + labels[1].trim();

		// logger.log(Level.INFO,
		// "PERFORMANCE END - extractSalesNameFromSalesNameLabel()");
		return labels[0].trim();
	}

	public String extractSalesNumberFromSalesNumberLabel(String label) {

		// logger.log(Level.INFO,
		// "PERFORMANCE START - extractSalesNumberFromSalesNumberLabel()");

		String[] labels = label.split(QuoteConstant.AUTOCOMPLETE_SEPARATOR);

		// logger.log(Level.INFO,
		// "PERFORMANCE END - extractSalesNumberFromSalesNumberLabel()");
		return labels[0].trim();
	}

	public String extractSalesNameFromSalesNumberLabel(String label) {

		// logger.log(Level.INFO,
		// "PERFORMANCE START - extractSalesNameFromSalesNumberLabel()");
		LOGGER.info("call extractSalesNameFromSalesNumberLabel | label : " + label);

		String[] labels = label.split(QuoteConstant.AUTOCOMPLETE_SEPARATOR);
		if (labels.length == 3)
			return labels[1].trim() + ", " + labels[2].trim();

		// logger.log(Level.INFO,
		// "PERFORMANCE END - extractSalesNameFromSalesNumberLabel()");
		// TODO:ArrayIndexOutOfBoundsException
		if (labels.length == 2) {
			return labels[1].trim();
		}
		return labels[0].trim();
	}

	public void updateSoldToCustomerNumber() {

		// logger.log(Level.INFO,
		// "PERFORMANCE START - updateSoldToCustomerNumber()");

		rfqHeader
				.setSoldToCustomerNumber(extractCustomerNumberFromCustomerNameLabel(rfqHeader.getSoldToCustomerName()));
		rfqHeader.setSoldToCustomerName(extractCustomerNameFromCustomerNameLabel(rfqHeader.getSoldToCustomerName()));
		Customer customer = customerSB.findByPK(rfqHeader.getSoldToCustomerNumber());

		if (customer.getDeleteFlag() != null && customer.getDeleteFlag().booleanValue()) {
			deleteCustomer = true;
			FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_WARN,
					ResourceMB.getText("wq.message.deltdCust") + " :", ResourceMB.getText("wq.label.soldToCodeCustomer")
							+ " : " + getCustomerFullName(customer) + "(" + rfqHeader.getSoldToCustomerNumber() + ")");
			FacesContext.getCurrentInstance().addMessage("RfqSubmissionGrowl", msg);
		} else {
			boolean isInvalidCustomer = customerSB.isInvalidCustomer(rfqHeader.getSoldToCustomerNumber());
			if (isInvalidCustomer) {
				logInfo("Invalid Special Customer : " + rfqHeader.getSoldToCustomerNumber());
				FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_WARN,
						ResourceMB.getText("wq.label.invalidCust") + " :",
						ResourceMB.getText("wq.label.soldToCodeCustomer") + " : "
								+ rfqHeader.getSoldToCustomerNumber());
				FacesContext.getCurrentInstance().addMessage("RfqSubmissionGrowl_1", msg);
				RequestContext requestContext = RequestContext.getCurrentInstance();
				requestContext.update("form_rfqSubmission:RfqSubmissionGrowl");
			}
			deleteCustomer = false;
		}

		rfqHeader.setCustomerType(customer.getCustomerType());

		rfqHeader.setChineseSoldToCustomerName(null);
		List<CustomerAddress> customerAddresses = customer.getCustomerAddresss();

		updateSoldToCode(customerAddresses);

		if (updateDataTable) {
			for (RfqItemVO rfqItem : rfqItems) {
				if (rfqItem != null) {
					// if(QuoteUtil.isEmpty(rfqItem.getSoldToCustomerName()) &&
					// QuoteUtil.isEmpty(rfqItem.getSoldToCustomerNumber())){
					if (QuoteUtil.isEmpty(rfqItem.getRequiredPartNumber())) {
						rfqItem.setSoldToCustomerName(rfqHeader.getSoldToCustomerName());
						rfqItem.setSoldToCustomerNumber(rfqHeader.getSoldToCustomerNumber());
						rfqItem.setCustomerType(rfqHeader.getCustomerType());
						rfqItems.set(rfqItem.getItemNumber() - 1, rfqItem);
					}
				}
			}
		}
		updateEndCustomerSelectListBySoldToCustomer();

		// logger.log(Level.INFO,
		// "PERFORMANCE END - updateSoldToCustomerNumber()");
	}

	public void retrieveEndCustomerChineseName() {
		// logger.log(Level.INFO,
		// "PERFORMANCE START - updateSoldToCustomerNumber()");
		Customer customer = customerSB.findByPK(rfqHeader.getSoldToCustomerNumber());
		rfqHeader.setChineseSoldToCustomerName(null);
		List<CustomerAddress> customerAddresses = customer.getCustomerAddresss();

		updateSoldToCode(customerAddresses);
	}

	public void updateSoldToCustomerName() {
		if (rfqHeader.getSalesBizUnit() == null) {
			rfqHeader.setSoldToCustomerNumber(null);
			rfqHeader.setSoldToCustomerName(null);
			FacesUtil.showWarnMessage(ResourceMB.getText("wq.message.salesmanEmpty"), "RfqSubmissionGrowl_1");
			return;
		}

		// logger.log(Level.INFO,
		// "PERFORMANCE START - updateSoldToCustomerName()");

		if (rfqHeader.getSoldToCustomerNumber() != null && rfqHeader.getSoldToCustomerNumber().length() > 0) {
			List<String> accountGroups = new ArrayList<String>();
			accountGroups.add(QuoteSBConstant.ACCOUNT_GROUP_SOLDTO);
			accountGroups.add(QuoteSBConstant.ACCOUNT_GROUP_PROSPECTIVE_CUSTOMER);
			accountGroups.add(QuoteSBConstant.ACCOUNT_GROUP_CPD);
			List<Customer> customers = customerSB.findCustomerByCustomerNumber(rfqHeader.getSoldToCustomerNumber(),
					null, accountGroups, rfqHeader.getSalesBizUnit(), 0, 100);

			rfqHeader.setSoldToCustomerName(null);
			rfqHeader.setChineseSoldToCustomerName(null);
			rfqHeader.setCustomerType(null);
			boolean pickTheCustomer = false;
			Customer customer = null;
			if (customers != null && customers.size() > 0) {
				pickTheCustomer = true;
				customer = customers.get(0);
			}

			if (pickTheCustomer) {
				boolean isInvalidCustomer = customerSB.isInvalidCustomer(rfqHeader.getSoldToCustomerNumber());
				if (isInvalidCustomer) {
					logInfo("Invalid Special Customer : " + rfqHeader.getSoldToCustomerNumber());
					FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_WARN,
							ResourceMB.getText("wq.label.invalidCust") + " :",
							ResourceMB.getText("wq.label.soldToCodeCustomer") + " : "
									+ rfqHeader.getSoldToCustomerNumber());
					FacesContext.getCurrentInstance().addMessage("RfqSubmissionGrowl_1", msg);
					RequestContext requestContext = RequestContext.getCurrentInstance();
					requestContext.update("form_rfqSubmission:RfqSubmissionGrowl");
				} else {

					if (customer.getDeleteFlag() != null && customer.getDeleteFlag().booleanValue()) {
						deleteCustomer = true;
						FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_WARN,
								ResourceMB.getText("wq.message.deltdCust") + " :",
								ResourceMB.getText("wq.label.soldToCodeCustomer") + " : "
										+ getCustomerFullName(customer) + "(" + rfqHeader.getSoldToCustomerNumber()
										+ ")");
						FacesContext.getCurrentInstance().addMessage("RfqSubmissionGrowl_1", msg);
						RequestContext requestContext = RequestContext.getCurrentInstance();
						requestContext.update("form_rfqSubmission:RfqSubmissionGrowl");
						return;
					} else {
						deleteCustomer = false;

						rfqHeader.setCustomerType(customer.getCustomerType());
						rfqHeader.setSoldToCustomerName(this.getCustomerFullName(customer));
						// rfqHeader.setChineseSoldToCustomerName(null);
						List<CustomerAddress> customerAddresses = customer.getCustomerAddresss();
						for (CustomerAddress customerAddress : customerAddresses) {
							if (customerAddress.getCountry() != null
									&& customerAddress.getId().getLanguageCode() != null
									&& (customerAddress.getId().getLanguageCode()
											.equals(QuoteSBConstant.LANGUAGE_CODE_CHINESE_C)
											|| customerAddress.getId().getLanguageCode()
													.equals(QuoteSBConstant.LANGUAGE_CODE_CHINESE_M)
											|| customerAddress.getId().getLanguageCode()
													.equals(QuoteSBConstant.LANGUAGE_CODE_CHINESE_1))) {
								rfqHeader.setChineseSoldToCustomerName(
										QuoteUtil.unicodeToChinese(getCustomerFullName(customerAddress)));
								break;
							}
						}

						if (updateDataTable) {
							for (RfqItemVO rfqItem : rfqItems) {
								if (rfqItem != null) {
									// if(QuoteUtil.isEmpty(rfqItem.getSoldToCustomerName())
									// &&
									// QuoteUtil.isEmpty(rfqItem.getSoldToCustomerNumber())){
									if (QuoteUtil.isEmpty(rfqItem.getRequiredPartNumber())) {
										rfqItem.setSoldToCustomerName(rfqHeader.getSoldToCustomerName());
										rfqItem.setSoldToCustomerNumber(rfqHeader.getSoldToCustomerNumber());
										rfqItem.setCustomerType(rfqHeader.getCustomerType());
										rfqItems.set(rfqItem.getItemNumber() - 1, rfqItem);
									}
								}
							}
						}
					}
				}
				// for performance improvement.
				// updateEndCustomerSelectListBySoldToCustomer();
			} else {
				logInfo("Invalid Customer : " + rfqHeader.getSoldToCustomerNumber());
				FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_WARN,
						ResourceMB.getText("wq.label.invalidCust") + " :",
						ResourceMB.getText("wq.label.soldToCodeCustomer") + " : "
								+ rfqHeader.getSoldToCustomerNumber());
				FacesContext.getCurrentInstance().addMessage("RfqSubmissionGrowl_1", msg);
				RequestContext requestContext = RequestContext.getCurrentInstance();
				requestContext.update("form_rfqSubmission:RfqSubmissionGrowl");
			}
			// logger.log(Level.INFO,
			// "PERFORMANCE END - updateSoldToCustomerName()");

		}

	}

	public void updateSalesNumber() {

		// logger.log(Level.INFO, "PERFORMANCE START - updateSalesNumber()");

		rfqHeader.setSalesEmployeeNumber(extractSalesNumberFromSalesNameLabel(rfqHeader.getSalesEmployeeName()));
		rfqHeader.setSalesEmployeeName(extractSalesNameFromSalesNameLabel(rfqHeader.getSalesEmployeeName()));
		User user = userSB.findByEmployeeIdWithAllRelation(rfqHeader.getSalesEmployeeNumber());
		if (user != null && user.getTeam() != null) {
			rfqHeader.setTeam(user.getTeam().getName());
		}
		updateCsForSales();

		if (user != null) {
			allowCustomers = userSB.findAllCustomers(user);
			rfqHeader.setSalesBizUnit(user.getDefaultBizUnit());
		} else {
			rfqHeader.setSalesBizUnit(null);
		}

		// logger.log(Level.INFO, "PERFORMANCE END - updateSalesNumber()");
	}

	public void updateSalesName() {
		// logger.log(Level.INFO, "PERFORMANCE START - updateSalesName()");
		rfqHeader.setSalesEmployeeName(extractSalesNameFromSalesNumberLabel(rfqHeader.getSalesEmployeeNumber()));
		rfqHeader.setSalesEmployeeNumber(extractSalesNumberFromSalesNumberLabel(rfqHeader.getSalesEmployeeNumber()));
		User user = userSB.findByEmployeeIdWithAllRelation(rfqHeader.getSalesEmployeeNumber());
		if (user != null && user.getTeam() != null) {
			rfqHeader.setTeam(user.getTeam().getName());
		}
		updateCsForSales();

		if (user != null) {
			allowCustomers = userSB.findAllCustomers(user);
			rfqHeader.setSalesBizUnit(user.getDefaultBizUnit());
		} else {
			rfqHeader.setSalesBizUnit(null);
		}
		// logger.log(Level.INFO, "PERFORMANCE END - updateSalesName()");
	}

	/**
	 * @deprecated
	 */
	public void validateSoldToCustomerName() {

		// logger.log(Level.INFO,
		// "PERFORMANCE START - validateSoldToCustomerName()");

		List<String> accountGroup = new ArrayList<String>();
		accountGroup.add(QuoteSBConstant.ACCOUNT_GROUP_SOLDTO);
		// Updated by tonmy on 16 Oct 2013 , for cross regions . salesBizUnit
		// instead of bizUnit
		List<Customer> customers = customerSB.findCustomerByCustomerName(rfqHeader.getSoldToCustomerName(), null,
				accountGroup, rfqHeader.getSalesBizUnit());
		if (QuoteUtil.isEmptyCustomerList(customers)) {
			rfqHeader.setSoldToCustomerNumber(null);
		}

		// logger.log(Level.INFO,
		// "PERFORMANCE END - validateSoldToCustomerName()");
	}

	public void updateShipToCustomerNumber() {

		// logger.log(Level.INFO,
		// "PERFORMANCE START - updateShipToCustomerNumber()");

		rfqHeader
				.setShipToCustomerNumber(extractCustomerNumberFromCustomerNameLabel(rfqHeader.getShipToCustomerName()));
		rfqHeader.setShipToCustomerName(extractCustomerNameFromCustomerNameLabel(rfqHeader.getShipToCustomerName()));
		if (updateDataTable) {
			for (RfqItemVO rfqItem : rfqItems) {
				if (rfqItem != null) {
					// if(QuoteUtil.isEmpty(rfqItem.getShipToCustomerName()) &&
					// QuoteUtil.isEmpty(rfqItem.getShipToCustomerNumber())){
					if (QuoteUtil.isEmpty(rfqItem.getRequiredPartNumber())) {
						rfqItem.setShipToCustomerName(rfqHeader.getShipToCustomerName());
						rfqItem.setShipToCustomerNumber(rfqHeader.getShipToCustomerNumber());
						rfqItems.set(rfqItem.getItemNumber() - 1, rfqItem);
					}
				}
			}
		}

		// logger.log(Level.INFO,
		// "PERFORMANCE END - updateShipToCustomerNumber()");

	}

	public void updateShipToCustomerName() {

		// logger.log(Level.INFO,
		// "PERFORMANCE START - updateShipToCustomerName()");

		rfqHeader
				.setShipToCustomerName(extractCustomerNameFromCustomerNumberLabel(rfqHeader.getShipToCustomerNumber()));
		rfqHeader.setShipToCustomerNumber(
				extractCustomerNumberFromCustomerNumberLabel(rfqHeader.getShipToCustomerNumber()));
		if (updateDataTable) {
			for (RfqItemVO rfqItem : rfqItems) {
				if (rfqItem != null) {
					// if(QuoteUtil.isEmpty(rfqItem.getShipToCustomerName()) &&
					// QuoteUtil.isEmpty(rfqItem.getShipToCustomerNumber())){
					if (QuoteUtil.isEmpty(rfqItem.getRequiredPartNumber())) {
						rfqItem.setShipToCustomerName(rfqHeader.getShipToCustomerName());
						rfqItem.setShipToCustomerNumber(rfqHeader.getShipToCustomerNumber());
						rfqItems.set(rfqItem.getItemNumber() - 1, rfqItem);
					}
				}
			}
		}

		// logger.log(Level.INFO,
		// "PERFORMANCE END - updateShipToCustomerName()");

	}

	public void validateShipToCustomerName() {

		// logger.log(Level.INFO,
		// "PERFORMANCE START - validateShipToCustomerName()");

		List<String> accountGroup = new ArrayList<String>();
		accountGroup.add(QuoteSBConstant.ACCOUNT_GROUP_SHIPTO);
		// Updated by tonmy on 16 Oct 2013 , for cross regions . salesBizUnit
		// instead of bizUnit
		List<Customer> customers = customerSB.findCustomerByCustomerName(rfqHeader.getShipToCustomerName(), null,
				accountGroup, rfqHeader.getSalesBizUnit());
		if (QuoteUtil.isEmptyCustomerList(customers)) {
			rfqHeader.setShipToCustomerNumber(null);
		}

		// logger.log(Level.INFO,
		// "PERFORMANCE END - validateShipToCustomerName()");
	}

	public void updateEndCustomerNumber() {

		// logger.log(Level.INFO,
		// "PERFORMANCE START - updateEndCustomerNumber()");

		rfqHeader.setEndCustomerNumber(extractCustomerNumberFromCustomerNameLabel(rfqHeader.getEndCustomerName()));
		rfqHeader.setEndCustomerName(extractCustomerNameFromCustomerNameLabel(rfqHeader.getEndCustomerName()));
		if (updateDataTable) {
			for (RfqItemVO rfqItem : rfqItems) {
				if (rfqItem != null) {
					// if(QuoteUtil.isEmpty(rfqItem.getEndCustomerName()) &&
					// QuoteUtil.isEmpty(rfqItem.getEndCustomerNumber())){
					if (QuoteUtil.isEmpty(rfqItem.getRequiredPartNumber())) {
						rfqItem.setEndCustomerName(rfqHeader.getEndCustomerName());
						rfqItem.setEndCustomerNumber(rfqHeader.getEndCustomerNumber());
						rfqItems.set(rfqItem.getItemNumber() - 1, rfqItem);
					}
				}
			}
		}

		// logger.log(Level.INFO,
		// "PERFORMANCE END - updateEndCustomerNumber()");
	}

	public void updateEndCustomerName() {
		// logger.log(Level.INFO,
		// "PERFORMANCE START - updateEndCustomerName()");
		rfqHeader.setEndCustomerName(extractCustomerNameFromCustomerNumberLabel(rfqHeader.getEndCustomerNumber()));
		rfqHeader.setEndCustomerNumber(extractCustomerNumberFromCustomerNumberLabel(rfqHeader.getEndCustomerNumber()));
		if (updateDataTable) {
			for (RfqItemVO rfqItem : rfqItems) {
				if (rfqItem != null) {
					// if(QuoteUtil.isEmpty(rfqItem.getEndCustomerName()) &&
					// QuoteUtil.isEmpty(rfqItem.getEndCustomerNumber())){
					if (QuoteUtil.isEmpty(rfqItem.getRequiredPartNumber())) {
						rfqItem.setEndCustomerName(rfqHeader.getEndCustomerName());
						rfqItem.setEndCustomerNumber(rfqHeader.getEndCustomerNumber());
						rfqItems.set(rfqItem.getItemNumber() - 1, rfqItem);
					}
				}
			}
		}

		// logger.log(Level.INFO, "PERFORMANCE END - updateEndCustomerName()");
	}

	/**
	 * @deprecated
	 */
	public void updateEndCustomerNameOnBlur() {
		boolean isInvalidCustomer = customerSB.isInvalidCustomer(rfqHeader.getEndCustomerNumber());
		if (isInvalidCustomer) {
			rfqHeader.setEndCustomerName(null);
			logInfo("Invalid Special Customer : " + rfqHeader.getEndCustomerNumber());
			FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_WARN,
					ResourceMB.getText("wq.label.invalidCust") + " :",
					ResourceMB.getText("wq.label.endCust") + " : " + rfqHeader.getEndCustomerNumber());
			FacesContext.getCurrentInstance().addMessage("RfqSubmissionGrowl_1", msg);
			RequestContext requestContext = RequestContext.getCurrentInstance();
			requestContext.update("form_rfqSubmission:RfqSubmissionGrowl");
		}
	}

	public void validateEndCustomerName() {

		// logger.log(Level.INFO,
		// "PERFORMANCE START - validateEndCustomerName()");

		List<String> accountGroup = new ArrayList<String>();
		accountGroup.add(QuoteSBConstant.ACCOUNT_GROUP_ENDCUSTOMER);
		// Updated by tonmy on 16 Oct 2013 , for cross regions . salesBizUnit
		// instead of bizUnit
		List<Customer> customers = customerSB.findCustomerByCustomerName(rfqHeader.getEndCustomerName(), null,
				accountGroup, rfqHeader.getSalesBizUnit());
		if (QuoteUtil.isEmptyCustomerList(customers)) {
			rfqHeader.setEndCustomerNumber(null);
		}

		// logger.log(Level.INFO,
		// "PERFORMANCE END - validateEndCustomerName()");
	}

	public void updateSoldToCustomerNumberInRfqItemList(int itemNumber) {

		// logger.log(Level.INFO,
		// "PERFORMANCE START - updateSoldToCustomerNumberInRfqItemList()");

		RfqItemVO rfqItem = getRfqItemByItemNumber(itemNumber);
		rfqItem.setSoldToCustomerNumber(extractCustomerNumberFromCustomerNameLabel(rfqItem.getSoldToCustomerName()));
		rfqItem.setSoldToCustomerName(extractCustomerNameFromCustomerNameLabel(rfqItem.getSoldToCustomerName()));
		Customer customer = customerSB.findByPK(rfqItem.getSoldToCustomerNumber());
		rfqItem.setCustomerType(customer.getCustomerType());
		rfqItems.set(itemNumber - 1, rfqItem);

		// logger.log(Level.INFO,
		// "PERFORMANCE END - updateSoldToCustomerNumberInRfqItemList()");
	}

	public void updateSoldToCustomerNameInRfqItemList(int itemNumber) {

		// logger.log(Level.INFO,
		// "PERFORMANCE START - updateSoldToCustomerNameInRfqItemList()");

		RfqItemVO rfqItem = getRfqItemByItemNumber(itemNumber);
		Boolean haveSameSoldToCustomer = false;
		for (RfqItemVO vo : rfqItems) {
			if (vo.getItemNumber() != rfqItem.getItemNumber()) {
				if (!QuoteUtil.isEmpty(vo.getSoldToCustomerNumber())
						&& !QuoteUtil.isEmpty(rfqItem.getSoldToCustomerNumber())
						&& vo.getSoldToCustomerNumber().equals(rfqItem.getSoldToCustomerNumber())) {
					haveSameSoldToCustomer = true;
					rfqItem.setSoldToCustomerName(vo.getSoldToCustomerName());
					rfqItem.setCustomerType(vo.getCustomerType());
					break;
				}
			}
		}
		if (!haveSameSoldToCustomer && !QuoteUtil.isEmpty(rfqItem.getSoldToCustomerNumber())) {
			Customer customer = setCustomerInfomationByCode(rfqItem);
			if (customer == null) {
				FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_WARN,
						ResourceMB.getText("wq.label.invalidCust") + " :",
						ResourceMB.getText("wq.label.soldToCodeCustomer") + " : " + rfqItem.getSoldToCustomerNumber());
				FacesContext.getCurrentInstance().addMessage("RfqSubmissionGrowl_1", msg);
			} else {
				if (customer.getDeleteFlag() != null && customer.getDeleteFlag().booleanValue()) {
					rfqItem.setSoldToCustomerName(null);
					rfqItem.setCustomerType(null);

					FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_WARN,
							ResourceMB.getText("wq.message.deltdCust") + " :",
							ResourceMB.getText("wq.label.soldToCodeCustomer") + " : " + getCustomerFullName(customer)
									+ "(" + customer.getCustomerNumber() + ")");
					FacesContext.getCurrentInstance().addMessage("RfqSubmissionGrowl_1", msg);
					RequestContext requestContext = RequestContext.getCurrentInstance();
					requestContext.update("form_rfqSubmission:RfqSubmissionGrowl");
					return;
				}

				boolean isInvalidCustomer = customerSB.isInvalidCustomer(rfqItem.getSoldToCustomerNumber());
				if (isInvalidCustomer) {
					logInfo("Invalid Special Customer : " + rfqHeader.getSoldToCustomerNumber());
					FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_WARN,
							ResourceMB.getText("wq.label.invalidCust") + " :",
							ResourceMB.getText("wq.label.soldToCodeCustomer") + " : "
									+ rfqItem.getSoldToCustomerNumber());
					FacesContext.getCurrentInstance().addMessage("RfqSubmissionGrowl_1", msg);
					RequestContext requestContext = RequestContext.getCurrentInstance();
					requestContext.update("form_rfqSubmission:RfqSubmissionGrowl");
				}
			}
		}
		rfqItems.set(itemNumber - 1, rfqItem);

		// logger.log(Level.INFO,
		// "PERFORMANCE END - updateSoldToCustomerNameInRfqItemList()");
	}

	public Customer setCustomerInfomationByCode(RfqItemVO rfqItem) {
		// Customer customer = customerSB.findByPK(rfqItem
		// .getSoldToCustomerNumber());
		List<String> accountGroups = new ArrayList<String>();
		accountGroups.add(QuoteSBConstant.ACCOUNT_GROUP_SOLDTO);
		accountGroups.add(QuoteSBConstant.ACCOUNT_GROUP_PROSPECTIVE_CUSTOMER);
		accountGroups.add(QuoteSBConstant.ACCOUNT_GROUP_CPD);
		List<Customer> customers = customerSB.findCustomerByCustomerNumber(rfqItem.getSoldToCustomerNumber(), null,
				accountGroups, rfqHeader.getSalesBizUnit(), 0, 100);
		Customer customer = null;
		if (customers != null && customers.size() > 0) {
			customer = customers.get(0);
		}

		rfqItem.setSoldToCustomerName(null);
		rfqItem.setCustomerType(null);

		if (customer != null) {
			rfqItem.setSoldToCustomerName(this.getCustomerFullName(customer));
			rfqItem.setCustomerType(customer.getCustomerType());
		}
		return customer;

	}

	public void updateShipToCustomerNumberInRfqItemList(int itemNumber) {

		// logger.log(Level.INFO,
		// "PERFORMANCE START - updateShipToCustomerNumberInRfqItemList()");

		RfqItemVO rfqItem = getRfqItemByItemNumber(itemNumber);
		rfqItem.setShipToCustomerNumber(extractCustomerNumberFromCustomerNameLabel(rfqItem.getShipToCustomerName()));
		rfqItem.setShipToCustomerName(extractCustomerNameFromCustomerNameLabel(rfqItem.getShipToCustomerName()));
		rfqItems.set(itemNumber - 1, rfqItem);

		// logger.log(Level.INFO,
		// "PERFORMANCE END - updateShipToCustomerNumberInRfqItemList()");
	}

	public void updateShipToCustomerNameInRfqItemList(int itemNumber) {

		// logger.log(Level.INFO,
		// "PERFORMANCE START - updateShipToCustomerNameInRfqItemList()");

		RfqItemVO rfqItem = getRfqItemByItemNumber(itemNumber);
		rfqItem.setShipToCustomerName(extractCustomerNameFromCustomerNumberLabel(rfqItem.getShipToCustomerNumber()));
		rfqItem.setShipToCustomerNumber(
				extractCustomerNumberFromCustomerNumberLabel(rfqItem.getShipToCustomerNumber()));
		rfqItems.set(itemNumber - 1, rfqItem);

		// logger.log(Level.INFO,
		// "PERFORMANCE END - updateShipToCustomerNameInRfqItemList()");
	}

	public void updateEndCustomerNumberInRfqItemList(int itemNumber) {

		// logger.log(Level.INFO,
		// "PERFORMANCE START - updateEndCustomerNumberInRfqItemList()");

		RfqItemVO rfqItem = getRfqItemByItemNumber(itemNumber);
		rfqItem.setEndCustomerNumber(extractCustomerNumberFromCustomerNameLabel(rfqItem.getEndCustomerName()));
		rfqItem.setEndCustomerName(extractCustomerNameFromCustomerNameLabel(rfqItem.getEndCustomerName()));
		rfqItems.set(itemNumber - 1, rfqItem);

		// logger.log(Level.INFO,
		// "PERFORMANCE END - updateEndCustomerNumberInRfqItemList()");
	}

	/**
	 * @deprecated
	 * @param itemNumber
	 */
	public void updateEndCustomerNumberInRfqItemListOnBlur(int itemNumber) {

		// logger.log(Level.INFO,
		// "PERFORMANCE START - updateEndCustomerNumberInRfqItemList()");

		RfqItemVO rfqItem = getRfqItemByItemNumber(itemNumber);

		boolean isInvalidCustomer = customerSB.isInvalidCustomer(rfqItem.getEndCustomerNumber());
		if (isInvalidCustomer) {
			logInfo("Invalid Special Customer : " + rfqHeader.getEndCustomerNumber());
			rfqItem.setEndCustomerName(null);
			rfqItems.set(itemNumber - 1, rfqItem);
			FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_WARN,
					ResourceMB.getText("wq.label.invalidCust") + " :",
					ResourceMB.getText("wq.label.endCust") + " : " + rfqItem.getEndCustomerNumber());
			FacesContext.getCurrentInstance().addMessage("RfqSubmissionGrowl_1", msg);
			RequestContext requestContext = RequestContext.getCurrentInstance();
			requestContext.update("form_rfqSubmission:RfqSubmissionGrowl");
		}

		// logger.log(Level.INFO,
		// "PERFORMANCE END - updateEndCustomerNumberInRfqItemList()");
	}

	public void updateEndCustomerNameInRfqItemList(int itemNumber) {
		// logger.log(Level.INFO,
		// "PERFORMANCE START - updateEndCustomerNameInRfqItemList()");

		RfqItemVO rfqItem = getRfqItemByItemNumber(itemNumber);

		rfqItem.setEndCustomerName(extractCustomerNameFromCustomerNumberLabel(rfqItem.getEndCustomerNumber()));
		rfqItem.setEndCustomerNumber(extractCustomerNumberFromCustomerNumberLabel(rfqItem.getEndCustomerNumber()));
		rfqItems.set(itemNumber - 1, rfqItem);
		// logger.log(Level.INFO,
		// "PERFORMANCE END - updateEndCustomerNameInRfqItemList()");
	}

	/**
	 * @deprecated
	 * @param itemNumber
	 */
	public void updateEndCustomerNameInRfqItemListOnBlur(int itemNumber) {

		// logger.log(Level.INFO,
		// "PERFORMANCE START - updateEndCustomerNameInRfqItemList()");

		RfqItemVO rfqItem = getRfqItemByItemNumber(itemNumber);
		boolean isInvalidCustomer = customerSB.isInvalidCustomer(rfqItem.getEndCustomerNumber());
		if (isInvalidCustomer) {
			logInfo("Invalid Special Customer : " + rfqItem.getEndCustomerNumber());
			rfqItem.setEndCustomerName(null);
			rfqItems.set(itemNumber - 1, rfqItem);
			FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_WARN,
					ResourceMB.getText("wq.label.invalidCust") + " :",
					ResourceMB.getText("wq.label.endCust") + " : " + rfqItem.getEndCustomerNumber());
			FacesContext.getCurrentInstance().addMessage("RfqSubmissionGrowl_1", msg);
			RequestContext requestContext = RequestContext.getCurrentInstance();
			requestContext.update("form_rfqSubmission:RfqSubmissionGrowl");
		}
	}

	/**
	 * @deprecated
	 * @param itemNumber
	 */
	public void updateEndCustomerNameInRfqItemListOnBlurConfirm(int itemNumber) {

		// logger.log(Level.INFO,
		// "PERFORMANCE START - updateEndCustomerNameInRfqItemList()");
		RfqItemVO rfqItem = getRfqItemByItemNumber(itemNumber);
		boolean isInvalidCustomer = customerSB.isInvalidCustomer(rfqItem.getEndCustomerNumber());
		if (isInvalidCustomer) {
			logInfo("Invalid Special Customer : " + rfqItem.getEndCustomerNumber());
			FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_WARN,
					ResourceMB.getText("wq.label.invalidCust") + " :",
					ResourceMB.getText("wq.label.endCust") + " : " + rfqItem.getEndCustomerNumber());
			FacesContext.getCurrentInstance().addMessage("RfqSubmissionGrowl_confirm_1", msg);
			RequestContext requestContext = RequestContext.getCurrentInstance();
			requestContext.update("form_rfqSubmission:RfqSubmissionGrowl_confirm");
		}
	}

	public List<String> autoCompleteSoldToCustomerNameLabelList(String key) {

		// logger.log(Level.INFO,
		// "PERFORMANCE START - autoCompleteSoldToCustomerNameLabelList()");
		if (key != null && !key.isEmpty())
			key = key.trim();

		List<String> accountGroup = new ArrayList<String>();
		accountGroup.add(QuoteSBConstant.ACCOUNT_GROUP_SOLDTO);
		accountGroup.add(QuoteSBConstant.ACCOUNT_GROUP_PROSPECTIVE_CUSTOMER);
		itemSelect = false;
		// Updated by tonmy on 16 Oct 2013 , for cross regions . salesBizUnit
		// instead of bizUnit
		List<Customer> customers = customerSB.findCustomerByCustomerNameContain(key, null, accountGroup,
				rfqHeader.getSalesBizUnit(), QuoteConstant.DEFAULT_AUTOCOMPLETE_FIRST_RESULT,
				QuoteConstant.DEFAULT_AUTOCOMPLETE_MAX_RESULTS);
		removeSpecialCustomer(customers);
		// logger.log(Level.INFO,
		// "PERFORMANCE END - autoCompleteSoldToCustomerNameLabelList()");
		return autoCompleteCustomerNameLabelList(customers);
	}

	public List<String> autoCompleteShipToCustomerNameLabelList(String key) {

		// logger.log(Level.INFO,
		// "PERFORMANCE START - autoCompleteShipToCustomerNameLabelList()");
		if (key != null && !key.isEmpty())
			key = key.trim();

		List<String> accountGroup = new ArrayList<String>();
		accountGroup.add(QuoteSBConstant.ACCOUNT_GROUP_SOLDTO);
		accountGroup.add(QuoteSBConstant.ACCOUNT_GROUP_SHIPTO);
		accountGroup.add(QuoteSBConstant.ACCOUNT_GROUP_PROSPECTIVE_CUSTOMER);
		itemSelect = false;
		// Updated by tonmy on 16 Oct 2013 , for cross regions . salesBizUnit
		// instead of bizUnit
		List<Customer> customers = customerSB.findCustomerByCustomerNameContain(key, null, accountGroup,
				rfqHeader.getSalesBizUnit(), QuoteConstant.DEFAULT_AUTOCOMPLETE_FIRST_RESULT,
				QuoteConstant.DEFAULT_AUTOCOMPLETE_MAX_RESULTS);

		// logger.log(Level.INFO,
		// "PERFORMANCE END - autoCompleteShipToCustomerNameLabelList()");
		return autoCompleteCustomerNameLabelList(customers);
	}

	public List<String> autoCompleteEndCustomerNameLabelList(String key) {

		// logger.log(Level.INFO,
		// "PERFORMANCE START - autoCompleteEndCustomerNameLabelList()");
		if (key != null && !key.isEmpty())
			key = key.trim();

		itemSelect = false;
		List<Customer> customers = null;
		if (QuoteUtil.isEmpty(key)) {
			if (rfqHeader.getSoldToCustomerNumber() != null) {
				customers = CustomerCacheManager.getEndCustomerBySoldToCustomer(rfqHeader.getSoldToCustomerNumber());
			}
		} else {
			List<String> accountGroup = new ArrayList<String>();
			accountGroup.add(QuoteSBConstant.ACCOUNT_GROUP_ENDCUSTOMER);
			customers = customerSB.findCustomerByCustomerNameContain(key, null, accountGroup, bizUnit,
					QuoteConstant.DEFAULT_AUTOCOMPLETE_FIRST_RESULT, QuoteConstant.DEFAULT_AUTOCOMPLETE_MAX_RESULTS);
			removeSpecialCustomer(customers);
		}
		// logger.log(Level.INFO,
		// "PERFORMANCE END - autoCompleteEndCustomerNameLabelList()");
		return autoCompleteCustomerNameLabelList(customers);
	}

	public List<String> autoCompleteCustomerNameLabelList(List<Customer> customers) {

		// logger.log(Level.INFO,
		// "PERFORMANCE START - autoCompleteCustomerNameLabelList()");

		itemSelect = false;
		List<String> list = null;
		if (customers != null) {
			list = new ArrayList<String>();
			List<String> dupList = new ArrayList<String>();
			for (Customer customer : customers) {
				if (customer != null) {
					if (!QuoteUtil.isEmpty(customer.getCustomerFullName())
							&& !QuoteUtil.isEmpty(customer.getCustomerNumber())) {
						String customerName = getCustomerFullName(customer);
						String customerNumber = customer.getCustomerNumber();
						String salesOrg = convertCustomerSalesToSalesOrg(customer.getCustomerSales(),
								rfqHeader.getBizUnit());

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

		// logger.log(Level.INFO,
		// "PERFORMANCE END - autoCompleteCustomerNameLabelList()");

		return list;
	}

	/**
	 * @deprecated
	 * @param key
	 * @return
	 */
	public List<String> autoCompleteSoldToCustomerNumberLabelList(String key) {

		// logger.log(Level.INFO,
		// "PERFORMANCE START - autoCompleteSoldToCustomerNumberLabelList()");
		if (key != null && !key.isEmpty())
			key = key.trim();

		List<String> accountGroup = new ArrayList<String>();
		accountGroup.add(QuoteSBConstant.ACCOUNT_GROUP_SOLDTO);
		itemSelect = false;
		// Updated by tonmy on 16 Oct 2013 , for cross regions . salesBizUnit
		// instead of bizUnit
		List<Customer> customers = customerSB.findCustomerByCustomerNumberContain(key, null, accountGroup,
				rfqHeader.getSalesBizUnit(), QuoteConstant.DEFAULT_AUTOCOMPLETE_FIRST_RESULT,
				QuoteConstant.DEFAULT_AUTOCOMPLETE_MAX_RESULTS);

		// logger.log(Level.INFO,
		// "PERFORMANCE END - autoCompleteSoldToCustomerNumberLabelList()");

		return autoCompleteCustomerNumberLabelList(customers);
	}

	public List<String> autoCompleteShipToCustomerNumberLabelList(String key) {

		// logger.log(Level.INFO,
		// "PERFORMANCE START - autoCompleteShipToCustomerNumberLabelList()");
		if (key != null && !key.isEmpty())
			key = key.trim();

		List<String> accountGroup = new ArrayList<String>();
		accountGroup.add(QuoteSBConstant.ACCOUNT_GROUP_SOLDTO);
		accountGroup.add(QuoteSBConstant.ACCOUNT_GROUP_SHIPTO);
		accountGroup.add(QuoteSBConstant.ACCOUNT_GROUP_PROSPECTIVE_CUSTOMER);
		itemSelect = false;
		// Updated by tonmy on 16 Oct 2013 , for cross regions . salesBizUnit
		// instead of bizUnit
		List<Customer> customers = customerSB.findCustomerByCustomerNumberContain(key, null, accountGroup,
				rfqHeader.getSalesBizUnit(), QuoteConstant.DEFAULT_AUTOCOMPLETE_FIRST_RESULT,
				QuoteConstant.DEFAULT_AUTOCOMPLETE_MAX_RESULTS);

		// logger.log(Level.INFO,
		// "PERFORMANCE END - autoCompleteShipToCustomerNumberLabelList()");

		return autoCompleteCustomerNumberLabelList(customers);
	}

	private Customer findEndCustomerBySoldToAndEnd(String endNumber, String soldToNumber) {
		if (QuoteUtil.isEmpty(endNumber) || QuoteUtil.isEmpty(soldToNumber)) {
			return null;
		}

		List<String> accountGroup = new ArrayList<String>();
		accountGroup.add(QuoteSBConstant.ACCOUNT_GROUP_ENDCUSTOMER);
		// Allow to input account group as 0001 (sold to party) and 0005
		// (prospective customer),added by Damon for BMT requests
		accountGroup.add(QuoteSBConstant.ACCOUNT_GROUP_SOLDTO);
		accountGroup.add(QuoteSBConstant.ACCOUNT_GROUP_PROSPECTIVE_CUSTOMER);
		Customer customer = customerSB.findEndCustomerForRFQ(endNumber, soldToNumber, accountGroup,
				rfqHeader.getSalesBizUnit());
		return customer;
	}

	public List<String> autoCompleteEndCustomerNumberLabelList(String key) {

		if (rfqHeader.getSalesBizUnit() == null) {
			rfqHeader.setEndCustomerNumber(null);
			rfqHeader.setEndCustomerName(null);

			FacesUtil.showWarnMessage(ResourceMB.getText("wq.message.salesmanEmpty"), "RfqSubmissionGrowl_1");
			return Collections.emptyList();
		}
		// logger.log(Level.INFO,
		// "PERFORMANCE START - autoCompleteEndCustomerNumberLabelList()");
		if (key != null && !key.isEmpty())
			key = key.trim();

		List<String> accountGroup = new ArrayList<String>();
		accountGroup.add(QuoteSBConstant.ACCOUNT_GROUP_ENDCUSTOMER);
		itemSelect = false;
		// Updated by tonmy on 16 Oct 2013 , for cross regions . salesBizUnit
		// instead of bizUnit
		// Fix ticket INC0170065 June Guo 20150805
		List<Customer> customers = customerSB.findEndCustomer(key, this.rfqHeader.getSoldToCustomerNumber(),
				accountGroup, rfqHeader.getSalesBizUnit(), QuoteConstant.DEFAULT_AUTOCOMPLETE_FIRST_RESULT,
				QuoteConstant.DEFAULT_AUTOCOMPLETE_MAX_RESULTS);

		removeSpecialCustomer(customers);
		// logger.log(Level.INFO,
		// "PERFORMANCE END - autoCompleteEndCustomerNumberLabelList()");
		List<String> outcome = autoCompleteCustomerNumberLabelList(customers);
		if (outcome == null || outcome.isEmpty()) {
			rfqHeader.setEndCustomerName(null);
			RequestContext.getCurrentInstance().update("form_rfqSubmission:basicDetails_endCustomerName");
		}
		return outcome;
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

	public List<String> autoCompleteCustomerNumberLabelList(List<Customer> customers) {

		// logger.log(Level.INFO,
		// "PERFORMANCE START - autoCompleteCustomerNumberLabelList()");

		itemSelect = false;
		List<String> list = new ArrayList<String>();
		List<String> dupList = new ArrayList<String>();
		for (Customer customer : customers) {
			String label = "";
			label += customer.getCustomerNumber();
			label += QuoteConstant.AUTOCOMPLETE_SEPARATOR;
			label += getCustomerFullName(customer);
			label += QuoteConstant.AUTOCOMPLETE_SEPARATOR;
			label += convertCustomerSalesToSalesOrg(customer.getCustomerSales(), rfqHeader.getSalesBizUnit().getName());
			if (!dupList.contains(label)) {
				list.add(label);
				dupList.add(label);
			}
		}

		// logger.log(Level.INFO,
		// "PERFORMANCE END - autoCompleteCustomerNumberLabelList()");
		return list;
	}

	public List<String> autoCompleteSalesNumber(String key) {

		// logger.log(Level.INFO,
		// "PERFORMANCE START - autoCompleteSalesName()");
		if (key != null && !key.isEmpty())
			key = key.trim();

		itemSelect = false;

		//return ejbCommonSB.autoCompleteSalesNumber(key, rfqHeader, this.bizUnit, this.userSB);
		return ejbCommonSB.autoCompleteSalesNumber(key, this.bizUnit);
	}

	public List<String> autoCompleteSalesName(String key) {

		// logger.log(Level.INFO,
		// "PERFORMANCE START - autoCompleteSalesName()");
		if (key != null && !key.isEmpty())
			key = key.trim();

		itemSelect = false;

		return ejbCommonSB.updateFormatAutoCompleteUserName(key, this.bizUnit, this.userSB);
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

	public void autoFillSoldToCustomerInformation() {

		// logger.log(Level.INFO,
		// "PERFORMANCE START - autoFillSoldToCustomerInformation()");

		String soldToCustomerNumber = rfqHeader.getSoldToCustomerNumber();
		Customer customer = customerSB.findByPK(soldToCustomerNumber);
		if (customer != null) {
			rfqHeader.setCustomerType(customer.getCustomerType());
			rfqHeader.setSoldToCustomerName(getCustomerFullName(customer));
		} else {
			rfqHeader.setCustomerType(null);
			rfqHeader.setSoldToCustomerName(null);
		}

		// logger.log(Level.INFO,
		// "PERFORMANCE END - autoFillSoldToCustomerInformation()");
	}

	public void autoFillShipToCustomerInformation() {

		// logger.log(Level.INFO,
		// "PERFORMANCE START - autoFillShipToCustomerInformation()");

		String shipToCustomerNumber = rfqHeader.getShipToCustomerNumber();

		// logger.log(Level.INFO,
		// "PERFORMANCE END - autoFillShipToCustomerInformation()");
	}

	public void autoFillEndCustomerInformation() {

		// logger.log(Level.INFO,
		// "PERFORMANCE START - autoFillEndCustomerInformation()");

		// String endCustomerNumber = rfqHeader.getEndCustomerNumber();

		// logger.log(Level.INFO,
		// "PERFORMANCE END - autoFillEndCustomerInformation()");
	}

	public void updateEndCustomerSelectListBySoldToCustomer() {

		// logger.log(Level.INFO,
		// "PERFORMANCE START - updateEndCustomerSelectListBySoldToCustomer()");

		List<Customer> customers = null;
		if (CustomerCacheManager.getEndCustomerBySoldToCustomer(rfqHeader.getSoldToCustomerNumber()) != null) {
			customers = CustomerCacheManager.getEndCustomerBySoldToCustomer(rfqHeader.getSoldToCustomerNumber());
		} else {
			try {
				// Updated by tonmy on 16 Oct 2013 , for cross regions .
				// salesBizUnit instead of bizUnit
				List<CustomerPartner> customerPartners = customerSB
						.findEndCustomerBySoldToCode(rfqHeader.getSoldToCustomerNumber(), rfqHeader.getSalesBizUnit());
				customers = new ArrayList<Customer>();
				for (CustomerPartner cp : customerPartners) {
					Customer endCustomer = customerSB.findByPK(cp.getPartnerCustomerCode());
					customers.add(endCustomer);
				}
				CustomerCacheManager.putSoldToVsEndCustomers(rfqHeader.getSoldToCustomerNumber(), customers);
			} catch (Exception ex) {
				LOGGER.log(Level.SEVERE,
						"Exception in update customer : " + rfqHeader.getEndCustomerNumber()
								+ " , Sales Business unit : " + rfqHeader.getSalesBizUnit() + " , EXception message : "
								+ ex.getMessage(),
						ex);
			}
		}
		List<String> list = new ArrayList<String>();
		for (Customer customer : customers) {
			list.add(getCustomerFullName(customer));
		}
		endCustomerNameSelectList = QuoteUtil.createFilterOptions(list.toArray(new String[list.size()]));
		if (endCustomerNameSelectList != null && endCustomerNameSelectList.length == 2) {
			this.rfqHeader.setEndCustomerNumber((String) endCustomerNameSelectList[1].getValue());
		}

		// logger.log(Level.INFO,
		// "PERFORMANCE END - updateEndCustomerSelectListBySoldToCustomer()");
	}

	public void searchCustomers() {
		// logger.log(Level.INFO, "PERFORMANCE START - searchCustomers()");
		setEndCustomerSearchType("1");
		logInfo("Search Customer Type = " + customerTypeSearch + " AND Customer Name = " + customerNameSearch);

		// Handle search key word < 3
		if (customerNameSearch != null && customerNameSearch.length() < 3) {

			FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "",
					ResourceMB.getText("wq.message.cutmrLenError"));
			FacesContext.getCurrentInstance().addMessage("CustomerDialogGrowl", msg);
			return;
		} else {
			searchCustomers = findCustomers(customerTypeSearch, customerNameSearch);
			if (searchCustomers != null) {
				List<String> customerNumbers = new ArrayList<String>();
				for (Iterator<Customer> itr = searchCustomers.iterator(); itr.hasNext();) {
					Customer customer = itr.next();
					boolean isInvalidCustomer = customerSB.isInvalidCustomer(customer.getCustomerNumber());
					if (isInvalidCustomer) {// isInvalidCustomer ,remove it
						itr.remove();
					} else {
						if (customerNumbers.contains(customer.getCustomerNumber())) {
							itr.remove();
						} else {
							customerNumbers.add(customer.getCustomerNumber());
						}
					}
				}
				searchCustomersCount = searchCustomers.size();
			}
		}
		// logger.log(Level.INFO, "PERFORMANCE END - searchCustomers()");
	}

	public List<Customer> findCustomers(String customerType, String customerName) {

		// logger.log(Level.INFO, "PERFORMANCE START - findCustomers()");

		if (QuoteUtil.isEmpty(customerName)) {
			String errorMessage = ResourceMB.getText("wq.label.custmrName") + " ";
			FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_WARN,
					ResourceMB.getText("wq.message.mandFieldError") + " : ", errorMessage);
			FacesContext.getCurrentInstance().addMessage("RfqSubmissionGrowl", msg);
			return null;
		}

		List<String> accountGroup = new ArrayList<String>();
		if (soldToCodeSearch || soldToCodeItemSearch > 0) {
			accountGroup.add(QuoteSBConstant.ACCOUNT_GROUP_SOLDTO);
			accountGroup.add(QuoteSBConstant.ACCOUNT_GROUP_PROSPECTIVE_CUSTOMER);
		} else if (shipToCodeSearch || shipToCodeItemSearch > 0) {
			accountGroup.add(QuoteSBConstant.ACCOUNT_GROUP_SOLDTO);
			accountGroup.add(QuoteSBConstant.ACCOUNT_GROUP_SHIPTO);
			accountGroup.add(QuoteSBConstant.ACCOUNT_GROUP_PROSPECTIVE_CUSTOMER);
		} else if (endCustomerCodeSearch || endCustomerCodeItemSearch > 0)
			accountGroup.add(QuoteSBConstant.ACCOUNT_GROUP_ENDCUSTOMER);

		List<Customer> customers = null;

		// Updated by tonmy on 16 Oct 2013 , for cross regions . salesBizUnit
		// instead of bizUnit
		if (customerName.startsWith("*"))
			customers = customerSB.findCustomerByCustomerNameEndWith(customerName.replaceAll("\\*", ""),
					QuoteSBConstant.ALL, accountGroup, rfqHeader.getSalesBizUnit());
		else if (customerName.endsWith("*"))
			customers = customerSB.findCustomerByCustomerNameStartWith(customerName.replaceAll("\\*", ""),
					QuoteSBConstant.ALL, accountGroup, rfqHeader.getSalesBizUnit());
		else
			customers = customerSB.findCustomerByCustomerNameContain(customerName.replaceAll("\\*", ""),
					QuoteSBConstant.ALL, accountGroup, rfqHeader.getSalesBizUnit());

		// logger.log(Level.INFO, "PERFORMANCE END - findCustomers()");
		return customers;
	}

	public void updateCustomer() {

		// logger.log(Level.INFO, "PERFORMANCE START - updateCustomer()");
		RequestContext requestContext = RequestContext.getCurrentInstance();
		boolean updateTableLevel = false;
		if (this.selectedSearchCustomer != null) {
			if (soldToCodeSearch) {
				rfqHeader.setSoldToCustomerNumber(this.selectedSearchCustomer.getCustomerNumber());
				rfqHeader.setSoldToCustomerName(getCustomerFullName(this.selectedSearchCustomer));
				rfqHeader.setCustomerType(this.selectedSearchCustomer.getCustomerType());
				// fix ticket INC0045089 June 20141113 -----start
				rfqHeader.setChineseSoldToCustomerName(null); // to set the
																// chinese name
																// as null for
																// the null
																// chinese
																// customer
				List<CustomerAddress> customerAddresses = this.selectedSearchCustomer.getCustomerAddresss();
				for (CustomerAddress customerAddress : customerAddresses) {
					if (customerAddress.getCountry() != null && customerAddress.getId().getLanguageCode() != null
							&& (customerAddress.getId().getLanguageCode()
									.equals(QuoteSBConstant.LANGUAGE_CODE_CHINESE_C)
									|| customerAddress.getId().getLanguageCode()
											.equals(QuoteSBConstant.LANGUAGE_CODE_CHINESE_M)
									|| customerAddress.getId().getLanguageCode()
											.equals(QuoteSBConstant.LANGUAGE_CODE_CHINESE_1))) {
						rfqHeader.setChineseSoldToCustomerName(
								QuoteUtil.unicodeToChinese(getCustomerFullName(customerAddress)));
						break;
					}
				}
				// fix ticket INC0045089 June 20141113 -----End

				if (this.selectedSearchCustomer.getDeleteFlag() != null
						&& this.selectedSearchCustomer.getDeleteFlag().booleanValue()) {
					deleteCustomer = true;
					FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_WARN,
							ResourceMB.getText("wq.message.deltdCust") + " :",
							ResourceMB.getText("wq.label.soldToCodeCustomer") + " : "
									+ getCustomerFullName(this.selectedSearchCustomer) + "("
									+ rfqHeader.getSoldToCustomerNumber() + ")");
					FacesContext.getCurrentInstance().addMessage("RfqSubmissionGrowl", msg);
				} else
					deleteCustomer = false;

				requestContext.update("form_rfqSubmission:basicDetails_soldToCustomerNumber");
				requestContext.update("form_rfqSubmission:basicDetails_soldToCustomerName");
				requestContext.update("form_rfqSubmission:basicDetails_chinese_soldToCustomerName");
				requestContext.update("form_rfqSubmission:basicDetails_customerType");

			} else if (shipToCodeSearch) {
				rfqHeader.setShipToCustomerNumber(this.selectedSearchCustomer.getCustomerNumber());
				rfqHeader.setShipToCustomerName(getCustomerFullName(this.selectedSearchCustomer));

				requestContext.update("form_rfqSubmission:basicDetails_shipToCustomerNumber");
				requestContext.update("form_rfqSubmission:basicDetails_shipToCustomerName");
				// update for shipToParty tooltip by damon@20160812
				requestContext.update("form_rfqSubmission:customerPanelGrid");

			} else if (endCustomerCodeSearch) {
				rfqHeader.setEndCustomerNumber(this.selectedSearchCustomer.getCustomerNumber());
				rfqHeader.setEndCustomerName(getCustomerFullName(this.selectedSearchCustomer));

				requestContext.update("form_rfqSubmission:basicDetails_endCustomerNumber");
				requestContext.update("form_rfqSubmission:basicDetails_endCustomerName");
				// update for endCustomerName tooltip by damon@20160812
				requestContext.update("form_rfqSubmission:customerPanelGrid");

			} else if (soldToCodeItemSearch > 0) {
				RfqItemVO rfqItem = getRfqItemByItemNumber(soldToCodeItemSearch);
				rfqItem.setSoldToCustomerNumber(this.selectedSearchCustomer.getCustomerNumber());
				rfqItem.setSoldToCustomerName(getCustomerFullName(this.selectedSearchCustomer));
				rfqItem.setCustomerType(this.selectedSearchCustomer.getCustomerType());
				rfqItems.set(soldToCodeItemSearch - 1, rfqItem);

				requestContext.update("form_rfqSubmission:datatable_basicDetails_rfqSubmission:"
						+ (soldToCodeItemSearch - 1) + ":soldToCodeItemOut");
				requestContext.update("form_rfqSubmission:datatable_basicDetails_rfqSubmission:"
						+ (soldToCodeItemSearch - 1) + ":soldToPartyItemOut");
				requestContext.update("form_rfqSubmission:datatable_basicDetails_rfqSubmission:"
						+ (soldToCodeItemSearch - 1) + ":basicDetails_cutomerTypeItemOut");

			} else if (shipToCodeItemSearch > 0) {
				RfqItemVO rfqItem = getRfqItemByItemNumber(shipToCodeItemSearch);
				rfqItem.setShipToCustomerNumber(this.selectedSearchCustomer.getCustomerNumber());
				rfqItem.setShipToCustomerName(getCustomerFullName(this.selectedSearchCustomer));
				rfqItems.set(shipToCodeItemSearch - 1, rfqItem);

				requestContext.update("form_rfqSubmission:datatable_basicDetails_rfqSubmission:"
						+ (shipToCodeItemSearch - 1) + ":shipToCodeItemOut");
				requestContext.update("form_rfqSubmission:datatable_basicDetails_rfqSubmission:"
						+ (shipToCodeItemSearch - 1) + ":shipToPartyItemOut");

			} else if (endCustomerCodeItemSearch > 0) {
				RfqItemVO rfqItem = getRfqItemByItemNumber(endCustomerCodeItemSearch);
				rfqItem.setEndCustomerNumber(this.selectedSearchCustomer.getCustomerNumber());
				rfqItem.setEndCustomerName(getCustomerFullName(this.selectedSearchCustomer));

				requestContext.update("form_rfqSubmission:datatable_basicDetails_rfqSubmission:"
						+ (endCustomerCodeItemSearch - 1) + ":endCustomerItemOut");
				requestContext.update("form_rfqSubmission:datatable_basicDetails_rfqSubmission:"
						+ (endCustomerCodeItemSearch - 1) + ":endCustomerCodeItemOut");

			}

			updateCustomerAtItem();
		}

		// logger.log(Level.INFO, "PERFORMANCE END - updateCustomer()");
	}

	public void updateCustomerAtItem() {

		// logger.log(Level.INFO, "PERFORMANCE START - updateCustomerAtItem()");
		RequestContext requestContext = RequestContext.getCurrentInstance();
		if (this.updateDataTable) {
			if (soldToCodeSearch) {
				for (RfqItemVO rfqItem : rfqItems) {
					if (rfqItem != null) {
						if (QuoteUtil.isEmpty(rfqItem.getRequiredPartNumber())) {
							rfqItem.setSoldToCustomerName(rfqHeader.getSoldToCustomerName());
							rfqItem.setSoldToCustomerNumber(rfqHeader.getSoldToCustomerNumber());
							rfqItems.set(rfqItem.getItemNumber() - 1, rfqItem);
							requestContext.update("form_rfqSubmission:datatable_basicDetails_rfqSubmission:"
									+ (rfqItem.getItemNumber() - 1) + ":soldToCodeItemOut");
							requestContext.update("form_rfqSubmission:datatable_basicDetails_rfqSubmission:"
									+ (rfqItem.getItemNumber() - 1) + ":soldToPartyItemOut");
						}
					}
				}

			} else if (shipToCodeSearch) {
				for (RfqItemVO rfqItem : rfqItems) {
					if (rfqItem != null) {
						if (QuoteUtil.isEmpty(rfqItem.getRequiredPartNumber())) {
							rfqItem.setShipToCustomerName(rfqHeader.getShipToCustomerName());
							rfqItem.setShipToCustomerNumber(rfqHeader.getShipToCustomerNumber());
							rfqItems.set(rfqItem.getItemNumber() - 1, rfqItem);
							requestContext.update("form_rfqSubmission:datatable_basicDetails_rfqSubmission:"
									+ (rfqItem.getItemNumber() - 1) + ":shipToCodeItemOut");
							requestContext.update("form_rfqSubmission:datatable_basicDetails_rfqSubmission:"
									+ (rfqItem.getItemNumber() - 1) + ":shipToPartyItemOut");
						}
					}
				}

			} else if (endCustomerCodeSearch) {
				for (RfqItemVO rfqItem : rfqItems) {
					if (rfqItem != null) {
						if (QuoteUtil.isEmpty(rfqItem.getRequiredPartNumber())) {
							rfqItem.setEndCustomerName(rfqHeader.getEndCustomerName());
							rfqItem.setEndCustomerNumber(rfqHeader.getEndCustomerNumber());
							rfqItems.set(rfqItem.getItemNumber() - 1, rfqItem);
							requestContext.update("form_rfqSubmission:datatable_basicDetails_rfqSubmission:"
									+ (rfqItem.getItemNumber() - 1) + ":endCustomerItemOut");
							requestContext.update("form_rfqSubmission:datatable_basicDetails_rfqSubmission:"
									+ (rfqItem.getItemNumber() - 1) + ":endCustomerCodeItemOut");
						}
					}
				}

			}
		}

		// logger.log(Level.INFO, "PERFORMANCE END - updateCustomerAtItem()");
	}

	public void resetCustomerSearch() {

		// logger.log(Level.INFO, "PERFORMANCE START - resetCustomerSearch()");

		searchCustomersCount = 0;
		searchCustomers = null;
		customerTypeSearch = QuoteSBConstant.ALL;
		customerNameSearch = null;
		setEndCustomerSearchType("1");
		setEndCustomerSearchRegion("");

		// logger.log(Level.INFO, "PERFORMANCE END - resetCustomerSearch()");
	}

	public void clearCustomerChangeFlag() {

		// logger.log(Level.INFO,
		// "PERFORMANCE START - clearCustomerChangeFlag()");

		soldToCodeSearch = false;
		shipToCodeSearch = false;
		endCustomerCodeSearch = false;
		soldToCodeItemSearch = 0;
		shipToCodeItemSearch = 0;
		endCustomerCodeItemSearch = 0;
		resetCustomerSearch();

		// logger.log(Level.INFO,
		// "PERFORMANCE END - clearCustomerChangeFlag()");
	}

	public void changeToSoldToCodeSearch() {

		// logger.log(Level.INFO,
		// "PERFORMANCE START - changeToSoldToCodeSearch()");

		clearCustomerChangeFlag();
		soldToCodeSearch = true;

		// logger.log(Level.INFO,
		// "PERFORMANCE END - changeToSoldToCodeSearch()");
	}

	public void changeToShipToCodeSearch() {

		// logger.log(Level.INFO,
		// "PERFORMANCE START - changeToShipToCodeSearch()");

		clearCustomerChangeFlag();
		shipToCodeSearch = true;

		// logger.log(Level.INFO,
		// "PERFORMANCE END - changeToShipToCodeSearch()");
	}

	public void changeToEndCustomerCodeSearch() {

		// logger.log(Level.INFO,
		// "PERFORMANCE START - changeToEndCustomerCodeSearch()");

		clearCustomerChangeFlag();
		if (rfqHeader.getSoldToCustomerNumber() != null && !QuoteUtil.isEmpty(rfqHeader.getSoldToCustomerNumber())) {
			try {
				// Updated by tonmy on 16 Oct 2013 , for cross regions .
				// salesBizUnit instead of bizUnit
				List<CustomerPartner> customerPartners = customerSB
						.findEndCustomerBySoldToCode(rfqHeader.getSoldToCustomerNumber(), rfqHeader.getSalesBizUnit());
				searchCustomers = new ArrayList<Customer>();
				for (CustomerPartner cp : customerPartners) {
					Customer endCustomer = customerSB.findByPK(cp.getPartnerCustomerCode());
					// added by damon 'if'
					if (endCustomer != null) {
						searchCustomers.add(endCustomer);
					}
				}
			} catch (Exception ex) {
				LOGGER.log(Level.SEVERE,
						"Exception in change  customer search , customer number :  " + rfqHeader.getEndCustomerNumber()
								+ " , Sales Business unit : " + rfqHeader.getSalesBizUnit() + " , EXception message : "
								+ ex.getMessage(),
						ex);
			}

			if (searchCustomers != null) {
				List<String> customerNumbers = new ArrayList<String>();
				for (Iterator<Customer> itr = searchCustomers.iterator(); itr.hasNext();) {
					Customer customer = itr.next();
					if (customer == null || customerNumbers == null)
						continue;
					if (customerNumbers.contains(customer.getCustomerNumber())) {
						itr.remove();
					} else {
						customerNumbers.add(customer.getCustomerNumber());
					}
				}
				searchCustomersCount = searchCustomers.size();
			}
		}

		endCustomerCodeSearch = true;
		setEndCustomerSearchType("1");
		// logger.log(Level.INFO,
		// "PERFORMANCE END - changeToEndCustomerCodeSearch()");
	}

	public void endCustomerSearchTypeChange() {
		if ("1".equalsIgnoreCase(endCustomerSearchType)) {
			changeToEndCustomerCodeSearch();
		} else if ("2".equalsIgnoreCase(endCustomerSearchType)) {
			searchCustomers = new ArrayList<Customer>();
			searchCustomersCount = 0;
			setEndCustomerSearchRegion("");
		}

	}

	public void endCustomerSearchRegionChange(AjaxBehaviorEvent event) {
		String selectedRegion = (String) event.getComponent().getAttributes().get("value");
		// setCustomerNameSearch("");
		if (!StringUtils.isEmpty(endCustomerSearchRegion)) {
			List<String> accountGroup = new ArrayList<String>();
			accountGroup.add(QuoteSBConstant.ACCOUNT_GROUP_ENDCUSTOMER);
			BizUnit tempBizUnit = new BizUnit();
			tempBizUnit.setName(selectedRegion);
			searchCustomers = customerSB.findCustomerByRegionAndName(accountGroup, tempBizUnit, customerNameSearch);
			if (searchCustomers != null) {
				List<String> customerNumbers = new ArrayList<String>();
				for (Iterator<Customer> itr = searchCustomers.iterator(); itr.hasNext();) {
					Customer customer = itr.next();
					if (customer == null || customerNumbers == null)
						continue;
					if (customerNumbers.contains(customer.getCustomerNumber())) {
						itr.remove();
					} else {
						customerNumbers.add(customer.getCustomerNumber());
					}
				}
				searchCustomersCount = searchCustomers.size();
			}
		} else {
			searchCustomers = new ArrayList<Customer>();
			searchCustomersCount = 0;
		}
	}

	/*
	 * search end customer by region
	 */
	public void searchEndCustomerByRegion() {

		// logger.log(Level.INFO,
		// "PERFORMANCE START - changeToEndCustomerCodeSearch()");

		// clearCustomerChangeFlag();
		// if (rfqHeader.getSoldToCustomerNumber() != null
		// && !QuoteUtil.isEmpty(rfqHeader.getSoldToCustomerNumber()))
		// {
		// try
		// {
		// //Updated by tonmy on 16 Oct 2013 , for cross regions . salesBizUnit
		// instead of bizUnit
		// List<CustomerPartner> customerPartners = customerSB
		// .findEndCustomerBySoldToCode(
		// rfqHeader.getSoldToCustomerNumber(), salesBizUnit);
		// searchCustomers = new ArrayList<Customer>();
		// for (CustomerPartner cp : customerPartners)
		// {
		// Customer endCustomer = customerSB.findByPK(cp
		// .getPartnerCustomerCode());
		// searchCustomers.add(endCustomer);
		// }
		// }
		// catch (Exception ex)
		// {
		// logger.log(Level.SEVERE, ex.getMessage());
		// }
		//
		// if (searchCustomers != null)
		// {
		// List<String> customerNumbers = new ArrayList<String>();
		// for (Iterator<Customer> itr = searchCustomers.iterator(); itr
		// .hasNext();)
		// {
		// Customer customer = itr.next();
		// if (customerNumbers.contains(customer.getCustomerNumber()))
		// {
		// itr.remove();
		// }
		// else
		// {
		// customerNumbers.add(customer.getCustomerNumber());
		// }
		// }
		// searchCustomersCount = searchCustomers.size();
		// }
		// }
		//
		// endCustomerCodeSearch = true;

		// logger.log(Level.INFO,
		// "PERFORMANCE END - changeToEndCustomerCodeSearch()");
	}

	public void changeToSoldToCodeItemSearch(int itemNumber) {

		// logger.log(Level.INFO,
		// "PERFORMANCE START - changeToSoldToCodeItemSearch()");

		clearCustomerChangeFlag();
		soldToCodeItemSearch = itemNumber;

		// logger.log(Level.INFO,
		// "PERFORMANCE END - changeToSoldToCodeItemSearch()");
	}

	public void changeToShipToCodeItemSearch(int itemNumber) {

		// logger.log(Level.INFO,
		// "PERFORMANCE START - changeToShipToCodeItemSearch()");

		clearCustomerChangeFlag();
		shipToCodeItemSearch = itemNumber;

		// logger.log(Level.INFO,
		// "PERFORMANCE END - changeToShipToCodeItemSearch()");
	}

	public void changeToEndCustomerCodeItemSearch(int itemNumber) {

		// logger.log(Level.INFO,
		// "PERFORMANCE START - changeToEndCustomerCodeItemSearch()");

		clearCustomerChangeFlag();
		if (rfqHeader.getSoldToCustomerNumber() != null && !QuoteUtil.isEmpty(rfqHeader.getSoldToCustomerNumber())) {
			searchCustomers = CustomerCacheManager.getEndCustomerBySoldToCustomer(rfqHeader.getSoldToCustomerNumber());
			if (searchCustomers != null) {
				List<String> customerNumbers = new ArrayList<String>();
				for (Iterator<Customer> itr = searchCustomers.iterator(); itr.hasNext();) {
					Customer customer = itr.next();
					if (customer == null || customerNumbers == null)
						continue;
					if (customerNumbers.contains(customer.getCustomerNumber())) {
						itr.remove();
					} else {
						customerNumbers.add(customer.getCustomerNumber());
					}
				}
				searchCustomersCount = searchCustomers.size();
			}
		}
		endCustomerCodeItemSearch = itemNumber;
		// logger.log(Level.INFO,
		// "PERFORMANCE END - changeToEndCustomerCodeItemSearch()");
	}

	// =================================================================================================
	/*
	 * END OF CUSTOMER SOURCE CODE
	 */

	/*
	 * autocomplete function
	 */
	// ========================================================================================================================
	// public List<String> autoCompletePartList(String key)
	// {
	//
	// // logger.log(Level.INFO, "PERFORMANCE START - autoCompletePartList()");
	//
	// itemSelect = false;
	// String mfr = null;
	// if (selectedRfqItem != null)
	// mfr = this.selectedRfqItem.getMfr();
	// logInfo("mfr=" + mfr + "/key=" + key);
	//
	// // Added by Tonmy at 16 Sep 2013 . for issue 991
	// if (key != null)
	// key = key.trim();
	//
	// List<BizUnit> bizUnits = new ArrayList<BizUnit>();
	// bizUnits.add(this.bizUnit);
	//
	//// List<Tuple> tuple = materialSB.wFindPartNumberAndMfr(key, bizUnits,
	//// QuoteConstant.DEFAULT_AUTOCOMPLETE_FIRST_RESULT,
	//// QuoteConstant.DEFAULT_AUTOCOMPLETE_MAX_RESULTS);
	// List<Tuple> tuple2 = materialSB.wFindPartNumberAndMfr2(key, bizUnits,
	// QuoteConstant.DEFAULT_AUTOCOMPLETE_FIRST_RESULT,
	// QuoteConstant.DEFAULT_AUTOCOMPLETE_MAX_RESULTS);
	// if (tuple2 != null)
	// {
	// List<String> materialList = new ArrayList<String>();
	//// for (Tuple material : tuple)
	//// {
	//// //0 is fullMfrPartNumber and 1 is mfr
	//// if (!materialList.contains(material.get(0) +
	// QuoteConstant.PART_SEPARATOR + material.get(1)))
	//// {
	//// materialList.add(material.get(0) + QuoteConstant.PART_SEPARATOR +
	// material.get(1));
	//// }
	//// }
	// for (Tuple material : tuple2)
	// {
	// //0 is fullMfrPartNumber and 1 is mfr
	// if (!materialList.contains(material.get(0) + QuoteConstant.PART_SEPARATOR
	// + material.get(1)))
	// {
	// materialList.add(material.get(0) + QuoteConstant.PART_SEPARATOR +
	// material.get(1));
	// }
	// }
	// // logger.log(Level.INFO,
	// // "PERFORMANCE END - autoCompletePartList()");
	// return materialList;
	// }
	// // }
	//
	// // logger.log(Level.INFO, "PERFORMANCE END - autoCompletePartList()");
	// return null;
	// }

	public List<String> autoCompletePartList(String key) {

		// logger.log(Level.INFO, "PERFORMANCE START - autoCompletePartList()");

		itemSelect = false;
		String mfr = null;
		if (selectedRfqItem != null)
			mfr = this.selectedRfqItem.getMfr();
		logInfo("mfr=" + mfr + "/key=" + key);

		// Added by Tonmy at 16 Sep 2013 . for issue 991
		if (key != null)
			key = key.trim();
		//TODO david
		PricerInfo pricerInfo = new PricerInfo();
		pricerInfo.setExRateDate(new Date());
		pricerInfo.setMfr(mfr);
		pricerInfo.setFullMfrPartNumber(key);
		pricerInfo.setBizUnit(bizUnit.getName());
		pricerInfo.setSalesId(userSB.findByEmployeeIdLazily(rfqHeader.getSalesEmployeeNumber()).getId());
		pricerInfo.setRfqCurr(rfqHeader.getRfqCurr());

		List<PricerInfo> returnList = materialSB.searchForMaterialPopup(pricerInfo);
		
//		List<PricerInfo> requiredPartNumberList =partSearchLogicMB.newSearchParts(null, key, bizUnit.getName(), false, allowCustomers, 0, QuoteConstant.DEFAULT_AUTOCOMPLETE_FIRST_RESULT,
//				Integer.parseInt(sysConfigSB
//						.getProperyValue("MAX_SEARCH_RESULT")));


			List<String> materialList = new ArrayList<String>();
			for (PricerInfo pricerInfo1 : returnList) {
				if (!materialList
						.contains(pricerInfo1.getMfr() + QuoteConstant.PART_SEPARATOR + pricerInfo1.getFullMfrPartNumber())) {
					materialList.add(pricerInfo1.getMfr() + QuoteConstant.PART_SEPARATOR + pricerInfo1.getFullMfrPartNumber());
				}
			}
			return materialList;

	}

	// ========================================================================================================================
	/*
	 * END OF autocomplete function
	 */

	/*
	 * Drms Checking
	 */
	// =======================================================================================================================
	public void downloadDrProjectReport() {

	}

	public int checkDrmsLinkage() {
		// logInfo("checkDrmsLinkage");
		int pendingDRMS = 0;
		// logger.log(Level.INFO, "PERFORMANCE START - checkDrmsLinkage()");
		// Map<String,DrProjectVO> tempMap = new HashMap<String,DrProjectVO>();
		if (rfqItems != null) {
			for (RfqItemVO rfqItem : rfqItems) {
				List<DrProjectVO> drProjects = new ArrayList<DrProjectVO>();
				// Added by Tonmy on 1 Aug 2013 . if this part 's drms linkage
				// is
				// true .will not update it again.
				// Changed by Tonmy Li on 5 Aug . if the part is n't the same as
				// the
				// first key in . need to re-generated the Drms
				if (!QuoteUtil.getFristPageKey(rfqItem).equalsIgnoreCase(rfqItem.getFirstPageKey())) {
					if (!QuoteUtil.isEmpty(rfqItem.getFirstPageKey())) {
						rfqItem.setDrmsLinkage(false);
						rfqItem.setDrProjects(new ArrayList<DrProjectVO>());
						rfqItem.setDrmsNumber(null);
					}
					List<DrNedaItem> drNedaItems = drmsSB.findMatchedDrProject(rfqItem.getSoldToCustomerNumber(),
							rfqItem.getEndCustomerNumber(), rfqItem.getMfr(), rfqItem.getRequiredPartNumber());

					boolean isDrmsLinkage = false;
					if (!QuoteUtil.isEmptyNedaItemList(drNedaItems)) {
						isDrmsLinkage = true;
						drmsFound = true;
						List<String> dupDrProjects = new ArrayList<String>();
						for (DrNedaItem drNedaItem : drNedaItems) {
							String dupCheck = drNedaItem.getId().getNedaLineNumber() + "_"
									+ drNedaItem.getId().getProjectId() + "_" + drNedaItem.getId().getNedaId();
							if (!dupDrProjects.contains(dupCheck)) {
								dupDrProjects.add(dupCheck);
								DrProjectVO drProjectVO = new DrProjectVO();
								drProjectVO.setDrNedaItem(drNedaItem);
								drProjects.add(drProjectVO);
								// if(!tempMap.containsKey(dupCheck))
								// tempMap.put(dupCheck, drProjectVO);
							}
						}
						rfqItem.setDrProjects(drProjects);

					}
					rfqItem.setDrmsLinkage(isDrmsLinkage);
					rfqItems.set(rfqItem.getItemNumber() - 1, rfqItem);
				}

			}
		}

		if (rfqItems != null && rfqItems.size() > 0) {
			// Issue 1124 . Fixed by Tonmy on 13, Nov
			pendingDRMSRfqs = new ArrayList<RfqItemVO>();
			for (RfqItemVO rfqItem : rfqItems) {
				if (rfqItem.isDrmsLinkage() && (rfqItem.getDrmsNumber() == null || rfqItem.getDrmsNumber() == 0l)) {
					pendingDRMSRfqs.add(rfqItem);
				}
			}
			if (pendingDRMSRfqs != null) {
				pendingDRMSCount = pendingDRMSRfqs.size();
				pendingDRMS = pendingDRMSCount;
			}
		}

		/*
		 * if(tempMap!=null && tempMap.size()>0) { Iterator<Entry<String,
		 * DrProjectVO>> entryKeyIterator = tempMap.entrySet().iterator(); while
		 * (entryKeyIterator.hasNext()) { Entry<String, DrProjectVO> e =
		 * entryKeyIterator.next();
		 * restOfDrProjects.add((DrProjectVO)e.getValue()); }
		 * 
		 * 
		 * }
		 */
		// logger.log(Level.INFO, "PERFORMANCE END - checkDrmsLinkage()");
		return pendingDRMS;
	}

	public void popupDrms(int itemNumber) {

		// logger.log(Level.INFO, "PERFORMANCE START - popupDrms()");

		int drmsItemNumber = 0;
		RfqItemVO rfqItem = getRfqItemByItemNumber(itemNumber);
		drProjects = rfqItem.getDrProjects();
		for (DrProjectVO drProject : drProjects) {
			drProject.setItemNumber(++drmsItemNumber);
			drProject.setRefItemNumber(itemNumber);
			drProjects.set(drProject.getItemNumber() - 1, drProject);
		}

		// logger.log(Level.INFO, "PERFORMANCE END - popupDrms()");
	}

	public void addDrmsToRfqs() {

		// logger.log(Level.INFO, "PERFORMANCE START - addDrmsToRfqs()");

		if (selectedDrProject != null) {
			int refItemNumber = selectedDrProject.getRefItemNumber();
			RfqItemVO rfqItem = getRfqItemByItemNumber(refItemNumber);
			rfqItem.setProjectId(
					selectedDrProject.getDrNedaItem().getDrNedaHeader().getDrProjectHeader().getProjectId());
			rfqItem.setPartMask(selectedDrProject.getDrNedaItem().getPartMask());
			rfqItem.setDrmsNumber(
					selectedDrProject.getDrNedaItem().getDrNedaHeader().getDrProjectHeader().getProjectId());
			rfqItem.setSupplierDrNumber(selectedDrProject.getDrNedaItem().getDrNumber());
			rfqItem.setSpecialPriceIndicator(true);
			rfqItem.setProjectName(
					selectedDrProject.getDrNedaItem().getDrNedaHeader().getDrProjectHeader().getProjectName());

			// the change for DRMS margin retention project
			rfqItem.setDrNedaId(Long.valueOf(selectedDrProject.getDrNedaItem().getId().getNedaId()).intValue());
			rfqItem.setDrNedaLineNumber(
					Long.valueOf(selectedDrProject.getDrNedaItem().getId().getNedaLineNumber()).intValue());

			rfqItems.set(rfqItem.getItemNumber() - 1, rfqItem);

			// Removed the assigned DRMS Rfq
			if (pendingDRMSRfqs != null && pendingDRMSRfqs.size() > 0) {
				List<RfqItemVO> removedRfqList = new ArrayList<RfqItemVO>();
				for (RfqItemVO tempRfq : pendingDRMSRfqs) {
					if (tempRfq.getMfr().equalsIgnoreCase(rfqItem.getMfr())
							&& tempRfq.getRequiredPartNumber().equalsIgnoreCase(rfqItem.getRequiredPartNumber())) {
						removedRfqList.add(tempRfq);
						break;
					}
				}
				if (removedRfqList != null && removedRfqList.size() > 0) {
					pendingDRMSRfqs.removeAll(removedRfqList);
					pendingDRMSCount = pendingDRMSRfqs.size();
				}

			}

			// updated by damon@20160613

			FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_INFO, ResourceMB.getText("wq.message.note") + ":",
					ResourceMB.getText("wq.error.manadateSPRFields"));
			FacesContext.getCurrentInstance().addMessage("RfqSubmissionGrowl2_1", msg);
			RequestContext requestContext = RequestContext.getCurrentInstance();
			requestContext.update("form_rfqSubmission:RfqSubmissionGrowl2");
		}

		// logger.log(Level.INFO, "PERFORMANCE END - addDrmsToRfqs()");
	}

	public void popupRequiredPartNumberList(int itemNumber) {

		// logger.log(Level.INFO,
		// "PERFORMANCE START - popupRequiredPartNumberList()");
		this.itemNumberForPopup = itemNumber;
		RfqItemVO rfqItem = getRfqItemByItemNumber(itemNumber);
		String searchPartMask = rfqItem.getPartMask();
		String searchMfr = null;
		searchMfr = rfqItem.getMfr();
		if(!QuoteUtil.isEmpty(searchPartMask)){	
			searchPartMask=searchPartMask.trim();
			if (searchPartMask.startsWith("*")
				|| searchPartMask.endsWith("*")) {
				searchPartMask=searchPartMask.replaceAll("\\*", "");
		}
		}
		
		List<PricerInfo> returnList = new ArrayList<PricerInfo>();

		//String maxResultStr = sysConfigSB.getProperyValue("MAX_SEARCH_RESULT");
		//int maxResult = Integer.valueOf(maxResultStr);
		//TODO david
		PricerInfo pricerInfo = new PricerInfo();
		pricerInfo.setExRateDate(new Date());
		pricerInfo.setMfr(searchMfr);
		pricerInfo.setFullMfrPartNumber(searchPartMask);
		pricerInfo.setBizUnit(bizUnit.getName());
		pricerInfo.setSalesId(userSB.findByEmployeeIdLazily(rfqHeader.getSalesEmployeeNumber()).getId());
		pricerInfo.setRfqCurr(rfqHeader.getRfqCurr());
		returnList = materialSB.searchForMaterialPopup(pricerInfo);
		if (returnList != null && returnList.size() > 0) {
			for (int i = 0; i < returnList.size(); i++) {
				PricerInfo sv = returnList.get(i);
				sv.setItemNumber(i + 1);
				returnList.set(i, sv);
			}
		}
		searchParts = returnList;

		//partModel = new PartModel(searchParts);
/*		if (searchParts != null) {
			// logger.info("searchParts result is not null , and size
			// ="+searchParts.size());
			searchPartsCount = searchParts.size();
		}*/
		
		partPopupModel = new PartModel(searchParts);
		// requiredPartNumberList = searchParts(searchMfr, searchPartMask,
		// itemNumber);

		// logger.log(Level.INFO,
		// "PERFORMANCE END - popupRequiredPartNumberList()");
	}

	public void addRequiredPartNumberToRfqs() {

		LOGGER.log(Level.INFO, "PERFORMANCE START - addRequiredPartNumberToRfqs()");

		if (selectedRequiredPartNumberList != null && selectedRequiredPartNumberList.size() > 0) {
			List<RfqItemVO> newRfqItems = new ArrayList<RfqItemVO>();
			// SearchPartVO firstVo = selectedRequiredPartNumberList.get(0);
			int itemNumber = this.itemNumberForPopup;
			RfqItemVO rfqItem = getRfqItemByItemNumber(itemNumber);
			int itemNumberCount = 0;
			for (int j = 0; j < rfqItems.size(); j++) {
				if (itemNumber == j + 1) {
					for (int i = 0; i < selectedRequiredPartNumberList.size(); i++) {
						PricerInfo tempVo = selectedRequiredPartNumberList.get(i);
						if (rfqItem != null) {
							RfqItemVO newRfqItem = rfqItem.copy();
							newRfqItem.setRequiredPartNumber(tempVo.getFullMfrPartNumber());
							newRfqItem.setMpq(tempVo.getMpq());
							newRfqItem.setMoq(tempVo.getMoq());
							newRfqItem.setLeadTime(tempVo.getLeadTime());
							newRfqItem.setItemNumber(++itemNumberCount);
							newRfqItems.add(newRfqItem);
						}
					}
				} else {
					RfqItemVO newRfqItem = rfqItems.get(j).copy();
					newRfqItem.setItemNumber(++itemNumberCount);
					newRfqItems.add(newRfqItem);
				}
			}

			rfqItems = newRfqItems;
/*			for (RfqItemVO rfqVo : rfqItems) {
				validateRfq(rfqVo.getItemNumber());
			}*/
			   
			  try {
				this.applyDefaultCostIndictorOrAQLogic(rfqHeader, rfqItems,allowCustomers,"D");
			} catch (Exception e) {
				// TODO Auto-generated catch block
				LOGGER.log(Level.SEVERE, "[" + user.getEmployeeId() + " ]"+"Get error when addRequiredPartNumberToRfqs ," + e.getMessage(),e);
				FacesUtil.showWarnMessage(QuoteSBConstant.SYSTEM_ERROR, "",
						"RfqSubmissionGrowl_1");
				return;
			}
			
            copyCustomerFromHeaderToALLItem(rfqItems);

		}

		// logger.log(Level.INFO,
		// "PERFORMANCE END - addRequiredPartNumberToRfqs()");
	}



	// =======================================================================================================================
	/*
	 * END OF Drms Checking
	 */

	/*
	 * Alternate Part
	 */
	// =======================================================================================================================

	public void submitAsNewPart() {

		// logger.log(Level.INFO, "PERFORMANCE START - submitAsNewPart()");

		RfqItemVO rfqItem = getRfqItemByItemNumber(toBeNewPartItemNumber);
		rfqItem.setNewPartNumber(true);
		rfqItems.set(toBeNewPartItemNumber - 1, rfqItem);

		// logger.log(Level.INFO, "PERFORMANCE END - submitAsNewPart()");
	}

	public void addParts() {

		// logger.log(Level.INFO, "PERFORMANCE START - addParts()");
		// logger.info("addParts start");
		// logger.info("addParts start
		// this.itemNumberForPartSearch:"+this.itemNumberForPartSearch);
		// logger.info("addParts selectedSearchParts size: "+
		// selectedSearchParts.size());
		if (selectedSearchParts != null && selectedSearchParts.size() > 0) {
			List<RfqItemVO> newRfqItems = new ArrayList<RfqItemVO>();
			int ind = this.itemNumberForPartSearch;
			RfqItemVO selectedVo = (RfqItemVO) rfqItems.get(ind - 1);
			for (int i = 0; i < selectedSearchParts.size(); i++) {
				PricerInfo searchPartVO = selectedSearchParts.get(i);
				RfqItemVO rfqItem = new RfqItemVO();
				if (i == 0) {
					if (selectedVo.isSpecialPriceIndicator()) {
						rfqItem.setSpecialPriceIndicator(true);
					}
					if (selectedVo.getId() != 0d) {
						rfqItem.setId(selectedVo.getId());
						rfqItem.setVersion(selectedVo.getVersion());
						rfqItem.setQuoteNumber(selectedVo.getQuoteNumber());
					}
					if (selectedVo.getRequiredQty() != null) {
						rfqItem.setRequiredQty(selectedVo.getRequiredQty());
					}
				}
				SelectItem[] sapPartNumberList = convertSapPartNumberToSelectItem(searchPartVO.getMfr(),
						searchPartVO.getFullMfrPartNumber());
				String keyStr = bizUnit.getName() + "==" + searchPartVO.getMfr() + "==" + searchPartVO.getFullMfrPartNumber();
				if (sapPartNumberList != null && sapPartNumberList.length > 0) {
					sapPartNumberSelectedMap.put(keyStr, sapPartNumberList);
				}
				rfqItem.setKey4SapPartNumberSelectedMap(keyStr);
				rfqItem.setRequiredPartNumber(searchPartVO.getFullMfrPartNumber());
				rfqItem.setMpq(searchPartVO.getMpq());
				rfqItem.setMoq(searchPartVO.getMoq());
				rfqItem.setLeadTime(searchPartVO.getLeadTime());
				rfqItem.setMfr(searchPartVO.getMfr());
				rfqItem.setBuyCurr(searchPartVO.getBuyCurr());
				rfqItem.setExRateBuy(searchPartVO.getExRateBuy());
				rfqItem.setExRateRfq(searchPartVO.getExRateRfq());
				rfqItem.setVat(searchPartVO.getVat());
				rfqItem.setHandling(searchPartVO.getHandling());
				newRfqItems.add(rfqItem);
			}

			// logger.info("addParts newRfqItems size: "+ newRfqItems.size());
			// logger.info("rfqItems size:"+rfqItems.size());
			// logger.info("ind:"+ind);
			for (int i = 0; i < newRfqItems.size(); i++) {
				if (i == 0) {
					// logger.info("addParts start i=0");
					rfqItems.set(ind - 1, newRfqItems.get(i));
				} else {
					// logger.info("addParts start i:"+i);
					rfqItems.add(ind + i - 1, newRfqItems.get(i));
				}

			}
			this.itemNumberCount = 0;
			for (int i = 0; i < rfqItems.size(); i++) {
				rfqItems.get(i).setItemNumber(++itemNumberCount);
				// validateRfq(rfqItems.get(i).getItemNumber());
			}
			fillInEmptyFieldFromHeader();
		}
		// logger.info("addParts end");
		// logger.log(Level.INFO, "PERFORMANCE END - addParts()");
	}

	// =======================================================================================================================
	/*
	 * Alternate Part
	 */

	public void checkNewProspectiveCustomer() {
	}

	public void submitRfqs() {

		logInfo("PERFORMANCE START - submitRfqs()");
		if (null == rfqItems || 0 == rfqItems.size()) { // fix defect 73 June
														// Guo 20170924 at quote
														// match logic project
			/*
			 * FacesMessage msg = new
			 * FacesMessage(FacesMessage.SEVERITY_WARN,QuoteConstant.
			 * MESSAGE_MISSING_MANDATORY_FIELD,"No RFQ is found for submission."
			 * ); FacesContext.getCurrentInstance().addMessage(
			 * "RfqSubmissionGrowl_confirm_1", msg);
			 */

			FacesMessage msg = new FacesMessage(ResourceMB.getText("wq.message.note") + ":",
					ResourceMB.getText("wq.error.noRFQFound") + ".");
			FacesContext.getCurrentInstance().addMessage("RfqSubmissionGrowl_confirm_1", msg);

			continueQuoteItems = null;

			return;
		}

		for (RfqItemVO rfqItem : rfqItems) {
			logInfo("ConfirmationPage-CheckPoint one : " + rfqItem.getMfr() + "/" + rfqItem.getRequiredPartNumber()
					+ " - " + rfqItem.getSoldToCustomerNumber());
			
			trimSpace(rfqItem);
		}
		resetQuoteCount();

		boolean endCustomerChecking = true;
		for (RfqItemVO rfqItem : rfqItems) {

			if (!QuoteUtil.isEmpty(rfqItem.getEndCustomerNumber())) {
				Customer itemEndCutomer = findEndCustomerBySoldToAndEnd(rfqItem.getEndCustomerNumber(),
						rfqItem.getSoldToCustomerNumber());
				if (itemEndCutomer == null) {
					FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_WARN,
							ResourceMB.getText("wq.label.invalidCust") + " :",
							ResourceMB.getText("wq.message.invalidCust") + "  " + rfqItem.getEndCustomerNumber() + " "
									+ ResourceMB.getText("wq.message.atRow") + " " + rfqItem.getItemNumber());
					FacesContext.getCurrentInstance().addMessage("RfqSubmissionGrowl_confirm_1", msg);
					RequestContext requestContext = RequestContext.getCurrentInstance();
					requestContext.update("form_rfqSubmission:RfqSubmissionGrowl_confirm");
					endCustomerChecking = false;
				} else if (!QuoteUtil.isEmpty(rfqItem.getEndCustomerName())
						&& !rfqItem.getEndCustomerName().equals(getCustomerFullName(itemEndCutomer))) {
					FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_WARN,
							ResourceMB.getText("wq.label.invalidCust") + " :",
							ResourceMB.getText("wq.message.invalidCust") + "  " + rfqItem.getEndCustomerNumber() + " "
									+ ResourceMB.getText("wq.label.notMatchCustName"));
					FacesContext.getCurrentInstance().addMessage("RfqSubmissionGrowl_confirm_1", msg);
					RequestContext requestContext = RequestContext.getCurrentInstance();
					requestContext.update("form_rfqSubmission:RfqSubmissionGrowl_confirm");
					endCustomerChecking = false;
				}

				// add validation ,blocked when it is special customer by
				// damon@20161110
				boolean isInvalidCustomer = customerSB.isInvalidCustomer(rfqItem.getEndCustomerNumber());
				if (isInvalidCustomer) {

					FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_WARN,
							ResourceMB.getText("wq.label.invalidCust") + " :",
							ResourceMB.getText("wq.label.endCustNum") + ":  " + rfqItem.getEndCustomerNumber());
					FacesContext.getCurrentInstance().addMessage("RfqSubmissionGrowl_confirm_1", msg);
					RequestContext requestContext = RequestContext.getCurrentInstance();
					requestContext.update("form_rfqSubmission:RfqSubmissionGrowl_confirm");
					endCustomerChecking = false;
				}

			}

			if (QuoteUtil.isEmpty(rfqItem.getEndCustomerNumber())
					&& (!QuoteUtil.isEmpty(rfqItem.getEndCustomerName()))) {
				FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_WARN,
						ResourceMB.getText("wq.label.invalidCust") + " :",
						ResourceMB.getText("wq.message.endCustCode") + ":  "

								+ rfqItem.getEndCustomerName());
				FacesContext.getCurrentInstance().addMessage("RfqSubmissionGrowl_confirm_1", msg);
				RequestContext requestContext = RequestContext.getCurrentInstance();
				requestContext.update("form_rfqSubmission:RfqSubmissionGrowl_confirm");
				endCustomerChecking = false;
			}

		}

		if (endCustomerChecking)
			if (!validationRule("RfqSubmissionGrowl_confirm_1")) {

				if (rfqSubmitChecking()) {
					return;
				}
				HashMap<String, String> soldToMap = new HashMap<String, String>();
				for (RfqItemVO rfqItem : rfqItems) {
					logInfo("Pre-submit Summary : " + rfqItem.getMfr() + "/" + rfqItem.getRequiredPartNumber() + " - "
							+ rfqItem.getSoldToCustomerNumber());
					soldToMap.put(rfqItem.getSoldToCustomerNumber(), rfqItem.getSoldToCustomerNumber());
				}

				Set<String> soldToCodes = soldToMap.keySet();
				try {
					applyDefaultCostIndictorOrAQLogic(rfqHeader, rfqItems,allowCustomers,"A");
					
				} catch (Exception we) {
					LOGGER.log(Level.SEVERE,we.getMessage(),we);
					FacesUtil.showWarnMessage(QuoteSBConstant.SYSTEM_ERROR, "",
							"RfqSubmissionGrowl_confirm_1");
					return;

				}
				
				
				try {
					rfqItemValidatorSB.validateRestrictedCustomer(rfqItems, FacesContext.getCurrentInstance().getViewRoot().getLocale(), bizUnit);

				} catch (WebQuoteException e) {
					FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_WARN, e.getMainMessage(), e.getMessage());
					FacesContext.getCurrentInstance().addMessage("RfqSubmissionGrowl_confirm_1", msg);
					return;
				}
				
				String formNumbers = "";
				String failSoldToCode = "";
				resultQuoteItems = new ArrayList<QuoteItem>();
				List<Quote> submitsQuotes = new ArrayList<>();
				for (String soldToCode : soldToCodes) {
					logInfo("Start To Submit for Sold To Code : " + soldToCode);

					try {
						quote = null;
						Quote submitQuote = submitRfqs(soldToCode);
						// for defect 406 , if the spr is checked. it will
						// return null result.
						if (submitQuote == null) {
							logInfo("SPR checked, and the process will stop here");
							return;
						} else {
							submitsQuotes.add(submitQuote);
						}

					} catch (Exception ex) {

						failSoldToCode += soldToCode + ";";
						LOGGER.log(Level.SEVERE, "Failed to submit RFQ, failed to sold code : " + failSoldToCode
								+ " , exception message : " + ex.getMessage(), ex);
					}
				}

				try {
					saveRFQs(submitsQuotes);
				} catch (Exception e) {
					LOGGER.log(Level.SEVERE, "Exception in saving RFQs , exception message : " + e.getMessage(), e);
					FacesUtil.showWarnMessage(QuoteSBConstant.SYSTEM_ERROR, e.getMessage(),
							"RfqSubmissionGrowl_confirm_1");

					return;
				}

				for (Quote submitQuote : submitsQuotes) {
					String soldToCode = "";
					try {
						soldToCode = submitQuote.getQuoteItems().get(0).getSoldToCustomer().getCustomerNumber();
						List<QuoteItem> quoteItems = submitQuote.getQuoteItems();
						
					

						boolean hasAutoQuote = false;
						for (QuoteItem quoteItem : quoteItems) {
							// Added by Tonmy li on 13 Sep 2013 . filtered out
							// the
							// old quote items.
							if (continueQuoteItems != null && continueQuoteItems.size() > 0) {
								if (continueQuoteItems.contains(new Long(quoteItem.getId())))
									continue;
							}
							quoteItem.setQuote(submitQuote);
							resultQuoteItems.add(quoteItem);
							if (quoteItem.getStatus() != null
									&& quoteItem.getStatus().equals(QuoteSBConstant.RFQ_STATUS_AQ))
								hasAutoQuote = true;
						}
						if (hasAutoQuote) {
							QuotationEmailVO vo = new QuotationEmailVO();
							vo.setFormNumber(submitQuote.getFormNumber());
							vo.setQuoteId(submitQuote.getId());
							Customer customer = customerSB.findByPK(soldToCode);
							//vo.setHssfWorkbook(getQuoteTemplateBySoldTo(customer, submitQuote));
							
							/*Collections.sort(submitQuote.getQuoteItems(),  new Comparator<QuoteItem>() {
					            public int compare(QuoteItem e1, QuoteItem e2) {
					                return e1.getQuoteNumber().compareTo(e2.getQuoteNumber());
					            }});*/
							vo.setQuote(submitQuote);
							vo.setSoldToCustomer(customer);
							vo.setSubmissionDateFromQuote(true);
							vo.setRecipient(rfqHeader.getSalesEmployeeName());
							// modified By Lenon.Yang(043044) 2016-04-28
							String regionCodeName = bizUnit.getName();
							vo.setSender(sysMaintSB.getEmailSignName(regionCodeName) + "<br/>"
									+ sysMaintSB.getEmailHotLine(regionCodeName) + "<br/>Email Box: "
									+ sysMaintSB.getEmailSignContent(regionCodeName) + "<br/>");
							vo.setFromEmail(sysMaintSB.getEmailAddress(regionCodeName));

							String fullCustomerName = getCustomerFullName(customer);

							vo = ejbCommonSB.commonLogicForDpRFQandRFQ(submitQuote, vo, customer, user,
									fullCustomerName);

							formNumbers += submitQuote.getFormNumber() + ";";
							//sendQuotationEmail(vo);
							String strategyKey=UserInfo.getUser().getDefaultBizUnit().getName();
							/*if(submitQuote.isdLinkFlag()){
								strategyKey=Constants.DEFAULT;
							}*/
						/*	SendQuotationEmailStrategy strategy = (SendQuotationEmailStrategy) cacheUtil.getStrategy(SendQuotationEmailStrategy.class,
									strategyKey);*/
							
							SendQuotationEmailStrategy strategy =(SendQuotationEmailStrategy) StrategyFactory.getSingletonFactory()
									.getStrategy(SendQuotationEmailStrategy.class,strategyKey,
											this.getClass().getClassLoader());
							strategy.sendQuotationEmail(vo);
						}
					} catch (Exception ex) {

						LOGGER.log(Level.SEVERE, "Exception in saving RFQs , exception message : " + ex.getMessage(),
								ex);
						failSoldToCode += soldToCode + ";";
					}
				}

				try {
					String domain = getDomain();
					String link = "/" + domain + "/RFQ/RFQSubmissionResultLayout.jsf?formNumber=" + formNumbers;
					if (failSoldToCode != null && failSoldToCode.length() > 0) {
						link += "&failSoldToCode=" + failSoldToCode;
					}
					if (draftQuoteCount > 0) {
						link += "&containsDraft=Y";
					}
					FacesContext.getCurrentInstance().getExternalContext().redirect(link);
				} catch (IOException e) {

					LOGGER.log(Level.SEVERE, "EXception in getting domain name, exception message : " + e.getMessage(),
							e);
				}

			}
		continueQuoteItems = null;
		// logger.log(Level.INFO, "PERFORMANCE END - submitRfqs()");
	}

	private void trimSpace(RfqItemVO rfqItem) {
		// TODO Auto-generated method stub
		if(org.apache.commons.lang.StringUtils.isBlank(rfqItem.getTargetResale())){
			rfqItem.setTargetResale(null);
		}
				
		
	}

	/**
	 * get customer chinese name.
	 * 
	 * @param customer
	 * @return
	 */
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

	public void resetQuoteCount() {

		// logger.log(Level.INFO, "PERFORMANCE START - resetQuoteCount()");

		aqQuoteCount = 0;
		qcQuoteCount = 0;
		itQuoteCount = 0;
		draftQuoteCount = 0;

		// logger.log(Level.INFO, "PERFORMANCE END - resetQuoteCount()");
	}

	public Quote submitDraftRfqs() {

		// logger.log(Level.INFO, "PERFORMANCE START - submitRfqs()");
		int tmpDraftQuoteCount = 0;
		Quote originalQuote = null;
		List<Long> projectIds = new ArrayList<Long>();

		removeEmptyRfq();

		if (quote == null) {
			quote = new Quote();
		}

		// Added by tonmy, for issue .
		quote.setStage(QuoteSBConstant.QUOTE_STAGE_DRAFT);
		// Added by Tonmy on 12 Sep 2013 . for issue 978
		if (rfqHeader.isContinueSubmit()) {
			quote.setContinueSubmit(true);
			originalQuote = quoteSB.findQuoteByPK(quote.getId());
		}
		List<QuoteItem> quoteItems = commonQuoteListDraftAndSubmit(originalQuote);

		int count = 0;
		for (RfqItemVO rfqItem : rfqItems) {
			logInfo("setting quoteItem from rfqItem (" + (++count) + ") = " + rfqItem.getMfr() + "/"
					+ rfqItem.getRequiredPartNumber());

			if (true) {
				QuoteItem quoteItem = new QuoteItem();

				// Fixed the issue 1543 and 1548 on Feb 24, 2014
				quoteItem.setSubmissionDate(quote.getSubmissionDate());

				if (rfqItem.getId() > 0) {
					// Added by Tonmy on 20 Aug 2013. for issue 796
					quoteItem.setId(rfqItem.getId());
					quoteItem.setQuoteNumber(rfqItem.getQuoteNumber());
					quoteItem.setVersion(rfqItem.getVersion());
					logInfo("Saved item with id " + rfqItem.getId());
					if (quote != null && quote.getQuoteItems() != null) {
						for (QuoteItem item : quote.getQuoteItems()) {
							if (item.getId() == rfqItem.getId())
								quoteItem = item;
						}
					}
				}

				/*
				 * copy data
				 */
				// if(quoteItem.getVersion() == null)
				// quoteItem.setVersion(0);
				// quoteItem.setVersion(quoteItem.getVersion()+1);

				// Restructured coding by tonmy ;
				// move below set method into another method
/*				quoteItem = copyBasicDataToQuoteItem(quoteItem, rfqItem);
				quoteItem = copyCostIndicatorRelatedData1ToQuoteItem(quoteItem, rfqItem);
				quoteItem = copyCustomerDataToQuoteItem(quoteItem, rfqItem);

				quoteItem = commonRFQDraftAndSubmit(rfqItem, quoteItem);
*/              

				quoteItem = copyCustomerDataToQuoteItem(quoteItem, rfqItem);
				quoteSB.fillPriceInfoToQuoteItem(rfqItem,quoteItem);
				//rfqItem.fillPriceInfoToQuoteItem(quoteItem);
				extractProjectIds(rfqItem,projectIds);
				/*
				 * end of Copy Data
				 */
				quoteItem.setStage(QuoteSBConstant.QUOTE_STAGE_DRAFT);
				quoteItem.setStatus(QuoteSBConstant.RFQ_STATUS_DQ);
				quoteItem.setLastUpdatedBy(user.getEmployeeId());
				quoteItem.setLastUpdatedName(user.getName());
				quoteItem.setLastUpdatedOn(new Date());
				tmpDraftQuoteCount++;

				if (QuoteSBConstant.QUOTE_STAGE_DRAFT.equalsIgnoreCase(quoteItem.getStage())) {
					quoteItem.setAqcc(QuoteSBConstant.DEFAULT_QC_EXTERNAL_COMMENT);
					quoteItem.setInternalComment(QuoteSBConstant.DEFAULT_QC_INTERNAL_COMMENT);
					tmpDraftQuoteCount++;
					// Requested by Timothy to comment this function . set the
					// submission date to the draft quote item .
					// Fixed the issue 1543 and 1548 on Feb 24, 2014
					// if those is draft RFQ . the submission date will be
					// cleared.
					// quoteItem.setSubmissionDate(null);
				}

				// fixed issue 1508 . if it is AQ and moq is null . set the moq.
				if (QuoteSBConstant.RFQ_STATUS_AQ.equalsIgnoreCase(quoteItem.getStatus())
						&& (quoteItem.getMoq() == null || quoteItem.getMoq().intValue() == ZERO)) {
					quoteItem.setMoq(QuoteUtil.findMoq(quoteItem));
				}

				if (rfqItem.getAttachments() != null) {
					for (int i = 0; i < rfqItem.getAttachments().size(); i++) {
						rfqItem.getAttachments().get(i).setQuoteItem(quoteItem);
					}
				}

				quoteItem.setAttachments(rfqItem.getAttachments());
				quoteItems.add(quoteItem);
			}
		}

		restOfDrProjects = new ArrayList<DrProjectVO>();

		List<Attachment> attachments = rfqHeader.getFormAttachments();
		quote.setAttachments(attachments);
		if (attachments != null) {
			for (int k = 0; k < attachments.size(); k++) {
				attachments.get(k).setQuote(quote);
			}
		}

	
		quote.setQuoteItems(quoteItems);
		for (int i = 0; i < quoteItems.size(); i++) {
			// add action to quoteItem by damon@20160805
			quoteItems.get(i).setAction(ActionEnum.RFQ_SUBMISSION_SAVE.name());
			quoteItems.get(i).setQuote(quote);
		}
		quoteSB.postProcessAfterRfqSubmitFinish(quoteItems);
		// Fixed issue 1570. if it is AQ update reference margin
		//quoteItems = myQuoteSearchSB.updateReferenceMarginForSubmission(quoteItems);
		
		

		// logInfo("start the save the submitting RFQs");
		quote = quoteSB.saveQuote(quote);
		// logInfo("finish the save the submitting RFQs");
		if (quote != null) {
			logInfo("quote.getId()=" + quote.getId());
			rfqHeader.setId(quote.getId());
			List<QuoteItem> savedQuoteItems = quote.getQuoteItems();
			if (savedQuoteItems != null) {
				for (int i = 0; i < savedQuoteItems.size(); i++) {
					logInfo("savedQuoteItems.get(i).getId()=" + savedQuoteItems.get(i).getId());
					rfqItems.get(i).setId(savedQuoteItems.get(i).getId());
				}
			}
		}
		logInfo("End To Save Quote");

		draftQuoteCount += tmpDraftQuoteCount;

		// logger.log(Level.INFO, "PERFORMANCE END - submitRfqs()");
		return quote;
	}

	private Quote submitRfqs(String soldToCode) throws WebQuoteException

	{
		// logger.log(Level.INFO, "PERFORMANCE START - submitRfqs()");

		int tmpAqQuoteCount = 0;
		int tmpQcQuoteCount = 0;
		int tmpItQuoteCount = 0;
		int tmpDraftQuoteCount = 0;
		Quote originalQuote = null;
	List<Long> projectIds = new ArrayList<Long>();

		removeEmptyRfq();

		if (quote == null) {
			quote = new Quote();
		}

		// Added by Tonmy on 12 Sep 2013 . for issue 978
		if (rfqHeader.isContinueSubmit()) {
			quote.setContinueSubmit(true);
			originalQuote = quoteSB.findQuoteByPK(quote.getId());
		}
		// for Resubmit Invalid RFQ damon@20160801
		if (!QuoteUtil.isEmpty(rfqHeader.getReSubmitType())) {
			quote.setReSubmitType(rfqHeader.getReSubmitType());
		}

		List<QuoteItem> quoteItems = commonQuoteListDraftAndSubmit(originalQuote);

		int count = 0;
		for (RfqItemVO rfqItem : rfqItems) {
			logInfo("setting quoteItem from rfqItem (" + (++count) + ") = " + rfqItem.getMfr() + "/"
					+ rfqItem.getRequiredPartNumber());
			String iResult = null;

			if ((soldToCode == null)
					|| (true && soldToCode != null && rfqItem.getSoldToCustomerNumber().equals(soldToCode))) {
				QuoteItem quoteItem = new QuoteItem();

				QuoteItem dupQuoteItem = new QuoteItem();

				// Fixed the issue 1543 and 1548 on Feb 24, 2014
				quoteItem.setSubmissionDate(quote.getSubmissionDate());
				//quoteItem.setReSubmitType(quote.getReSubmitType());

				if (rfqItem.getId() > 0) {
					// Added by Tonmy on 20 Aug 2013. for issue 796
					quoteItem.setId(rfqItem.getId());
					quoteItem.setQuoteNumber(rfqItem.getQuoteNumber());
					quoteItem.setVersion(rfqItem.getVersion());
					quoteItem.setReSubmitType(quote.getReSubmitType());
					logInfo("Saved item with id " + rfqItem.getId());
					if (quote != null && quote.getQuoteItems() != null) {
						for (QuoteItem item : quote.getQuoteItems()) {
							if (item.getId() == rfqItem.getId())
								quoteItem = item;
						}
					}
				}

				/*
				 * copy data
				 */
				// if(quoteItem.getVersion() == null)
				// quoteItem.setVersion(0);
				// quoteItem.setVersion(quoteItem.getVersion()+1);

				// Restructured coding by tonmy ;
				// move below set method into another method
			/*	if (!QuoteUtil.isEmpty(rfqItem.getReSubmitType())) {
					quoteItem.setReSubmitType(rfqItem.getReSubmitType());
				}*/
				
				//copyRfqItemDataToQuoteItem(quoteItem,rfqItem);
				quoteItem = copyCustomerDataToQuoteItem(quoteItem, rfqItem);
				quoteSB.fillPriceInfoToQuoteItem(rfqItem,quoteItem);
				//rfqItem.fillPriceInfoToQuoteItem(quoteItem);
				//quoteItem = copyBasicDataToQuoteItem(quoteItem, rfqItem);
				//quoteItem = copyCostIndicatorRelatedData1ToQuoteItem(quoteItem, rfqItem);
				//quoteItem = copyCustomerDataToQuoteItem(quoteItem, rfqItem);
//				quoteItem.setBmtCheckedFlag(rfqItem.isBmtCheckedFlag());
				
				//need add below :
				quoteItem.setLastUpdatedBy(user.getEmployeeId());
				quoteItem.setLastUpdatedName(user.getName());
				quoteItem.setLastUpdatedOn(QuoteUtil.getCurrentTime());
				
				quoteItem.setQuote(quote);
				//quoteItem = commonRFQDraftAndSubmit(rfqItem, quoteItem);
				
				
				
				extractProjectIds(rfqItem,projectIds);
				
		
				/*
				 * end of Copy Data
				 */
				if (QuoteSBConstant.QUOTE_STAGE_DRAFT.equals(rfqItem.getStage())&&QuoteSBConstant.RFQ_STATUS_DQ.equals(rfqItem.getStatus())) {
					//quoteItem.setStage(QuoteSBConstant.QUOTE_STAGE_DRAFT);
					//quoteItem.setStatus(QuoteSBConstant.RFQ_STATUS_DQ);
					tmpDraftQuoteCount++;
				}
				//added by DamonChen@20180502, go to DRAFT is higher priority even if user clicks SPR box 
				else{

			//	if (quoteItem.getStage() == null) {

			/*		// Delinkage change on Oct 29 , 2013 by Tonmy--annotated by damon@20171108
					if (quoteItem.getRequestedMaterialForSubmit() != null) {
						quoteItem.setQuotedMaterial(quoteItem.getRequestedMaterialForSubmit());
					}*/
					// fixed the issue 1508
					// quoteItem.setMoq(QuoteUtil.findMoq(quoteItem));
					//quoteItem.setMoq(rfqItem.getMoq());
					// quoteItem.setPmoq(QuoteUtil.findPmoq(quoteItem));
					// quoteItem.setQuotedQty(QuoteUtil.findAqQty(quoteItem));
					///quoteItem = copyCostIndicatorRelatedData2ToQuoteItem(quoteItem, rfqItem);
					///quoteItem = copyCostIndicatorRelatedData3ToQuoteItem(quoteItem, rfqItem);

					// Webquote 2.2 enhancement :
					// Retricted customer checking.
					// if the costIndicator is blocked will get another proper
					// cost indicator for AQ checking.
					// logger.info("the restricted customer checking | start ");
					// boolean retrictedCustomerChecking = true;
					// List<String> failedCostIndicator = new
					// ArrayList<String>();
					// logger.info("the restricted customer checking | end ");
					// Fixed 1041 by TonmyLi, 18 Oct ,2013
					boolean allRequiredFieldFilled = !fieldValidityChecked(quoteItem);
					// tenatively keep this part of code, can remove after
					// verifying it is not used, like in 2.3.8
					/*
					 * if(!retrictedCustomerChecking) {
					 * //logger.info("retrictedCustomerChecking is false");
					 * allRequiredFieldFilled = false; }
					 */
					// if(allRequiredFieldFilled)
					// {
					// // logger.info("allRequiredFieldFilled is true");
					// }
					// else
					// {
					// //logger.info("allRequiredFieldFilled is false");
					// }
					if (quoteItem.getSprFlag()) {
						if (quoteItem.getDesignRegion() == null || quoteItem.getDesignRegion().trim().equals("")
								|| quoteItem.getDesignRegion().trim().equals("ASIA")) {
							quoteItem.setStatus(QuoteSBConstant.RFQ_STATUS_QC);
							quoteItem.setStage(QuoteSBConstant.QUOTE_STAGE_PENDING);
						} else {
							quoteItem.setStatus(QuoteSBConstant.RFQ_STATUS_BQ);
							quoteItem.setStage(QuoteSBConstant.QUOTE_STAGE_PENDING);
							quoteItem.setBmtDescCode("99");
							quoteSB.setQuoteItemDesignMapping(quoteItem, user);
						}

					}
					else {
						

						//extract the logic that auto to Spr by DammonChen
						// change quoteItem.getTargetResale() to rfqItem.getMinSellPriceAsMoney(

						 if(quoteItem.getTargetResale()!=null && quoteItem.getTargetResale()!=ZERO_DOUBLE && allRequiredFieldFilled)
						{
							if ((rfqItem.getRfqCurrMinSellPrice() != null && rfqItem.getRfqCurrMinSellPrice() > ZERO_DOUBLE
									&& quoteItem.getTargetResale() < rfqItem.getRfqCurrMinSellPrice())
									|| (rfqItem.getRfqCurrSalesCost() != null && rfqItem.getRfqCurrSalesCost() > ZERO_DOUBLE
											&& quoteItem.getTargetResale() < rfqItem.getRfqCurrSalesCost())) {
								// Fixed defect 406
								logInfo("auto set spr true");
								quoteItem.setSprFlag(true);
								rfqItem.setSpecialPriceIndicator(true);
								if (validationRule("RfqSubmissionGrowl_confirm_1")) {
									return null;
								}
								// fixe by DamonChen@20180912,
								// Need change stage and status if auto Spr
								if (quoteItem.getDesignRegion() == null || quoteItem.getDesignRegion().trim().equals("")
										|| quoteItem.getDesignRegion().trim().equals("ASIA")) {
									quoteItem.setStatus(QuoteSBConstant.RFQ_STATUS_QC);
									quoteItem.setStage(QuoteSBConstant.QUOTE_STAGE_PENDING);
								} else {
									quoteSB.applyDefaultCostIndicatorLogic(quoteItem, false);
									quoteItem.setStatus(QuoteSBConstant.RFQ_STATUS_BQ);
									quoteItem.setStage(QuoteSBConstant.QUOTE_STAGE_PENDING);
									quoteItem.setBmtDescCode("99");
									quoteSB.setQuoteItemDesignMapping(quoteItem, user);
								}

							}
						}
					}
				}
						/*
						// Fixed ticket INC0164138
						quoteItem = ncnrFilter(quoteItem);
						if (quoteItem.getTargetResale() != null && quoteItem.getTargetResale() != ZERO_DOUBLE) {
							if (quoteItem.getMinSellPrice() != null && quoteItem.getMinSellPrice() > ZERO_DOUBLE) {
								if (quoteItem.getTargetResale() >= quoteItem.getMinSellPrice()) {
									if (allRequiredFieldFilled) {
										// It is auto quote.
										quoteItem.setQuotedPrice(quoteItem.getTargetResale());

										commonLogicForQuotedPrice(quoteItem);

										if (quoteItem.getQuotationEffectiveDate() == null) {
											quoteItem.setQuotationEffectiveDate(quote.getSubmissionDate());
										}
										// PROGRM PRICER ENHANCEMENT
										// quoteItem.setPmoq(QuoteUtil.findPmoq(quoteItem));
										quoteItem.setPmoq(QuoteUtil.findPmoq(quoteItem));
										quoteItem.setQuotedQty(QuoteUtil.findAqQty(quoteItem));

										quoteItem = aqLogic(quoteItem, rfqItem);
										// mofifyed by damon
										iResult = quoteSB.copyAndStageQuoteItem(quoteItem, dupQuoteItem, "1", user);
										if (iResult == null) {
											LOGGER.log(Level.INFO, "copy and stage dupicate quoteItem  failed !");
										} else {
											if (!iResult.equals("BMTFLAGCHECK-NO-DUP")) {
												tmpAqQuoteCount++;
											}

										}

										// fix defect 266 by damon@20160720
										if (quoteItem.getStatus() != null
												&& quoteItem.getStatus().equals(QuoteSBConstant.RFQ_STATUS_BQ)) {
											quoteItem.setQuotedQty(null);
										}

									} else {
										// mofifyed by damon
										// 0-not pass AQ logic,1 pass AQ logic
										iResult = quoteSB.copyAndStageQuoteItem(quoteItem, dupQuoteItem, "0", user);
										if (iResult == null)
											LOGGER.log(Level.INFO, "copy and stage dupicate quoteItem  failed !");
									}
								} else {// be careful to this damon
									if (allRequiredFieldFilled) {
										// Fixed defect 406
										logInfo("set spr true");
										quoteItem.setSprFlag(true);
										rfqItem.setSpecialPriceIndicator(true);
										if (validationRule("RfqSubmissionGrowl_confirm_1")) {
											return null;
										}

										
										 * quoteItem.setStatus(QuoteSBConstant.
										 * RFQ_STATUS_QC);
										 * quoteItem.setStage(QuoteSBConstant.
										 * QUOTE_STAGE_PENDING);
										 
										// updated by damon@20160711
										if (quoteItem.getDesignRegion() == null
												|| quoteItem.getDesignRegion().trim().equals("")
												|| quoteItem.getDesignRegion().trim().equals("ASIA")) {
											quoteItem.setStatus(QuoteSBConstant.RFQ_STATUS_QC);
											quoteItem.setStage(QuoteSBConstant.QUOTE_STAGE_PENDING);
										} else {
											quoteItem.setStatus(QuoteSBConstant.RFQ_STATUS_BQ);
											quoteItem.setStage(QuoteSBConstant.QUOTE_STAGE_PENDING);
											quoteItem.setBmtDescCode("99");
											quoteSB.setQuoteItemDesignMapping(quoteItem, user);
										}
									} else {
										
										 * quoteItem.setStatus(QuoteSBConstant.
										 * RFQ_STATUS_QC);
										 * quoteItem.setStage(QuoteSBConstant.
										 * QUOTE_STAGE_PENDING);
										 
										// mofifyed by damon
										iResult = quoteSB.copyAndStageQuoteItem(quoteItem, dupQuoteItem, "0", user);
										if (iResult == null)
											LOGGER.log(Level.INFO, "copy and stage dupicate quoteItem  failed !");
									}
								}
							} else {
								
								 * quoteItem.setStatus(QuoteSBConstant.
								 * RFQ_STATUS_QC);
								 * quoteItem.setStage(QuoteSBConstant.
								 * QUOTE_STAGE_PENDING);
								 

								// mofifyed by damon
								iResult = quoteSB.copyAndStageQuoteItem(quoteItem, dupQuoteItem, "0", user);
								if (iResult == null)
									LOGGER.log(Level.INFO, "copy and stage dupicate quoteItem  failed !");

							}
						} else {

							if (allRequiredFieldFilled && quoteItem.getMinSellPrice() != null
									&& quoteItem.getMinSellPrice() > ZERO_DOUBLE) {
								quoteItem.setQuotedPrice(quoteItem.getMinSellPrice());

								commonLogicForQuotedPrice(quoteItem);

								
								 * quoteItem.setStatus(QuoteSBConstant.
								 * RFQ_STATUS_AQ);
								 * quoteItem.setStage(QuoteSBConstant.
								 * QUOTE_STAGE_FINISH); noted by damon
								 
								if (quoteItem.getQuotationEffectiveDate() == null) {
									quoteItem.setQuotationEffectiveDate(quote.getSubmissionDate());
								}
								// quoteItem = ncnrFilter(quoteItem);
								// PROGRM PRICER ENHANCEMENT
								// quoteItem.setPmoq(QuoteUtil.findPmoq(quoteItem));
								quoteItem.setPmoq(QuoteUtil.findPmoq(quoteItem));
								quoteItem.setQuotedQty(QuoteUtil.findAqQty(quoteItem));
								quoteItem = aqLogic(quoteItem, rfqItem);
								// mofifyed by damon
								iResult = quoteSB.copyAndStageQuoteItem(quoteItem, dupQuoteItem, "1", user);
								if (iResult == null) {
									LOGGER.log(Level.INFO, "copy and stage dupicate quoteItem  failed !");
								} else {
									if (!iResult.equals("BMTFLAGCHECK-NO-DUP")) {
										tmpAqQuoteCount++;
									}

								}

								// fix defect 266 by damon@20160720
								if (quoteItem.getStatus() != null
										&& quoteItem.getStatus().equals(QuoteSBConstant.RFQ_STATUS_BQ)) {
									quoteItem.setQuotedQty(null);
								}
							} else {
								
								 * quoteItem.setStatus(QuoteSBConstant.
								 * RFQ_STATUS_QC);
								 * quoteItem.setStage(QuoteSBConstant.
								 * QUOTE_STAGE_PENDING); noted by damon
								 *
								// mofifyed by damon
								iResult = quoteSB.copyAndStageQuoteItem(quoteItem, dupQuoteItem, "0", user);
								if (iResult == null)
									LOGGER.log(Level.INFO, "copy and stage dupicate quoteItem  failed !");
							}
						}
						if (quoteItem.getStatus() != null
								&& quoteItem.getStatus().equals(QuoteSBConstant.RFQ_STATUS_AQ)) {
							// TW Enhancement - add SAP part number to Quote
							// Item
							if (QuoteUtil.isEmpty(rfqItem.getSapPartNumber())) {
								String sapPartNumber = materialSB.findDefaultSapPartNumber(
										rfqItem.getRequiredPartNumber(), rfqItem.getMfr(), bizUnit);
								quoteItem.setSapPartNumber(sapPartNumber);

								// added by damon ,if duplicate quoteItem is not
								// null and status is aq
								if (iResult != null && iResult.equals("DUP")) {
									if (dupQuoteItem != null && dupQuoteItem.getStatus() != null
											&& dupQuoteItem.getStatus().equals(QuoteSBConstant.RFQ_STATUS_AQ)) {
										dupQuoteItem.setSapPartNumber(sapPartNumber);
									}

								}
							}
						}
					*/
					//}

					// if status is BQ, what status transfer to ? by damon
					if (QuoteSBConstant.QUOTE_STAGE_PENDING.equalsIgnoreCase(quoteItem.getStage())) {
						List<AutoTransferPm> autoTransferPms = autoTransferPmSB.findVendorReportByPartNumber(
								rfqItem.getMfr(), rfqItem.getSoldToCustomerNumber(), rfqItem.getRequiredPartNumber(),
								bizUnit.getName());
						if (autoTransferPms != null) {
							List<String> pmEmployeeList = new ArrayList<String>();
							for (AutoTransferPm autoTransferPm : autoTransferPms) {
								// logInfo("Mfr = "
								// + autoTransferPm.getManufacturer());
								// logInfo("PartNumber = "
								// + autoTransferPm
								// .getFullMfrPartNumber());
								// logInfo("Sold To Code = "
								// + autoTransferPm
								// .getSoldToCustomerNumber());
								// logInfo("PM Employee Number = "
								// + autoTransferPm
								// .getSoldToCustomerNumber());
								pmEmployeeList.add(autoTransferPm.getPmEmployeeNumber());
							}
							quoteItem.setStatus(QuoteSBConstant.RFQ_STATUS_IT);
							quoteItem.setITStatus(true);
							quoteItem.setLastItUpdateTime(QuoteUtil.getCurrentTime());
							quoteItem.setStage(QuoteSBConstant.QUOTE_STAGE_PENDING);
							quoteItem.setPmEmployeeList(pmEmployeeList);
							tmpItQuoteCount++;
						} else {
							//calcResaleMargin(quoteItem);
							/*
							 * quoteItem.setStatus(QuoteSBConstant.RFQ_STATUS_QC
							 * ); quoteItem.setStage(QuoteSBConstant.
							 * QUOTE_STAGE_PENDING); noted by damon
							 */
							// quoteItem.setQuotedMaterial(null);
							quoteItem.setQuotedPrice(null);
							//tmpQcQuoteCount++;
						}

					}

			//	}

				if (QuoteSBConstant.QUOTE_STAGE_DRAFT.equalsIgnoreCase(quoteItem.getStage())) {
					quoteItem.setAqcc(QuoteSBConstant.DEFAULT_QC_EXTERNAL_COMMENT);
					quoteItem.setInternalComment(QuoteSBConstant.DEFAULT_QC_INTERNAL_COMMENT);

					// Requested by Timothy to comment this function . set the
					// submission date to the draft quote item .
					// Fixed the issue 1543 and 1548 on Feb 24, 2014
					// if those is draft RFQ . the submission date will be
					// cleared.
					// quoteItem.setSubmissionDate(null);
				}

				// fixed issue 1508 . if it is AQ and moq is null . set the moq.
				//if (QuoteSBConstant.RFQ_STATUS_AQ.equalsIgnoreCase(quoteItem.getStatus())
					///	&& (quoteItem.getMoq() == null || quoteItem.getMoq().intValue() == ZERO)) {
				///	quoteItem.setMoq(QuoteUtil.findMoq(quoteItem));
				///}
				// added by damon for above issue
			/*	if (iResult != null && iResult.equals("DUP")) {
					if (dupQuoteItem != null && dupQuoteItem.getStatus() != null) {
						if (QuoteSBConstant.RFQ_STATUS_AQ.equalsIgnoreCase(dupQuoteItem.getStatus())
								&& (dupQuoteItem.getMoq() == null || dupQuoteItem.getMoq().intValue() == ZERO)) {
							dupQuoteItem.setMoq(QuoteUtil.findMoq(dupQuoteItem));
						}
					}
				}*/
/*
				if (rfqItem.getAttachments() != null) {
					for (int i = 0; i < rfqItem.getAttachments().size(); i++) {
						rfqItem.getAttachments().get(i).setQuoteItem(quoteItem);
					}
				}
				quoteItem.setAttachments(rfqItem.getAttachments());*/
			/*	if (iResult != null && iResult.equals("DUP")) {
					dupQuoteItem.setAttachments(rfqItem.getAttachments());
					// quoteItems.add(dupQuoteItem);

					// fix defect#264 merge to quote item when continue submit
					// DRAFT quote imtem for BMT case by damon@20160725
					if (quote.isContinueSubmit()) {
						if (quoteItem.getId() != 0L && dupQuoteItem.getId() != 0L
								&& quoteItem.getId() == dupQuoteItem.getId()) {
							dupQuoteItem.setId(0L);
							dupQuoteItem.setVersion(0);
							// avoid an accident to QUOTE_ITEM_UK1,quote_number
							// is unique. by damon@20160725
							dupQuoteItem.setQuoteNumber(null);
						}
					}
				}*/
				
				LOGGER.info("ready:"+quote.getReSubmitType()+"==="+quoteItem.getReSubmitType());
				// generate a new quoteitem,for Resubmit Invalid RFQ
				if ((!QuoteUtil.isEmpty(quote.getReSubmitType())) && (!QuoteUtil.isEmpty(quoteItem.getReSubmitType()))
						&& QuoteSBConstant.QUOTE_STAGE_INVALID.equals(quote.getReSubmitType())
						&& QuoteSBConstant.QUOTE_STAGE_INVALID.equals(quoteItem.getReSubmitType())) {
					LOGGER.info("generate a new quoteitem,for Resubmit Invalid RFQ"+quoteItem.getId()+"==="+quoteItem.getQuoteNumber());
					if (continueQuoteItems != null && continueQuoteItems.size() > 0) {
						if (!continueQuoteItems.contains(new Long(quoteItem.getId()))) {
							continueQuoteItems.add(new Long(quoteItem.getId()));
						}
					} else {
						continueQuoteItems.add(new Long(quoteItem.getId()));
					}
					QuoteItem cQuoteItem = new QuoteItem();
					BeanUtilsExtends.copyProperties(cQuoteItem, quoteItem);
					cQuoteItem.setQuoteNumber(null);
					cQuoteItem.setId(0L);
					cQuoteItem.setVersion(0);
					cQuoteItem.setFirstRfqCode(quoteItem.getQuoteNumber());
			      /*	
			       * need to set QuoteItemDesignMapping if resubmit invalidate quote SPR  is true ,by DamonChen@20180731
					}*/
					
					if (cQuoteItem.getSprFlag()) {
						if (cQuoteItem.getDesignRegion() != null && ("EMEA".equals(cQuoteItem.getDesignRegion().trim())
								|| "EMA".equals(cQuoteItem.getDesignRegion().trim()))) {
							cQuoteItem.setStatus(QuoteSBConstant.RFQ_STATUS_BQ);
							cQuoteItem.setStage(QuoteSBConstant.QUOTE_STAGE_PENDING);
							cQuoteItem.setBmtDescCode("99");
							quoteSB.setQuoteItemDesignMapping(cQuoteItem, user);
						}

					}
					if (cQuoteItem.getAttachments() != null && cQuoteItem.getAttachments().size() > 0) {
						List<Attachment> atList = new ArrayList<Attachment>();
						for (Attachment at : cQuoteItem.getAttachments()) {
							Attachment att = new Attachment();
							att.setFileImage(at.getFileImage());
							att.setFileName(at.getFileName());
							att.setType(at.getType());
							att.setQuoteItem(cQuoteItem);
							atList.add(att);
						}
						cQuoteItem.setAttachments(atList);
					}
					quoteItems.add(cQuoteItem);
					if (quoteItem.getId() > 0L) {
						LOGGER.info("the Quote id is: " + quoteItem.getId());
						quoteItem = quoteSB.findByPK(quoteItem.getId());
					}
					quoteItems.add(quoteItem);
					
					//Fixed by Damon@20180627
					if (!cQuoteItem.getSprFlag() && (!QuoteSBConstant.QUOTE_STAGE_DRAFT.equals(rfqItem.getStage()))
							&& (!QuoteSBConstant.QUOTE_STAGE_INVALID.equals(cQuoteItem.getStage()))) {
						iResult = quoteSB.generateDuplicatedQuoteItem(cQuoteItem, dupQuoteItem, user);
					}
				} else {
					quoteItems.add(quoteItem);
					if (!quoteItem.getSprFlag() && (!QuoteSBConstant.QUOTE_STAGE_DRAFT.equals(rfqItem.getStage()))
							&& (!QuoteSBConstant.QUOTE_STAGE_INVALID.equals(quoteItem.getStage()))) {
						iResult = quoteSB.generateDuplicatedQuoteItem(quoteItem, dupQuoteItem, user);
					}
				}
				
				//keep the logic same to the previous logic that get submiissionDate for quotation effective date
				if (QuoteSBConstant.QUOTE_STAGE_FINISH.equalsIgnoreCase(quoteItem.getStage())
						&& QuoteSBConstant.RFQ_STATUS_AQ.equalsIgnoreCase(quoteItem.getStatus())) {
					if (quoteItem.getQuotationEffectiveDate() == null) {
						quoteItem.setQuotationEffectiveDate(quote.getSubmissionDate());
					}
				}
				 
				  
				// for the ori quote number gt dup quote numberdamon@20160804
				//don't need to generate duplication quote if spr flag is true or DQ quoteitem

				if (iResult != null && iResult.equals("DUP")) {
					dupQuoteItem.setQuote(quote);
					quoteItems.add(dupQuoteItem);
				}

			}
		}

		restOfDrProjects = new ArrayList<DrProjectVO>();
		if (projectIds.size() > 0) {
			List<DrNedaItem> drNedaItems = drmsSB.findDrNedaItemsByProjectIds(projectIds);
			for (DrNedaItem drNedaItem : drNedaItems) {
				DrProjectVO drProjectVO = new DrProjectVO();
				drProjectVO.setDrNedaItem(drNedaItem);
				this.restOfDrProjects.add(drProjectVO);
			}
		}

		List<Attachment> attachments = rfqHeader.getFormAttachments();
		if (attachments != null && attachments.size() > 0) {
			quote.setFormAttachmentFlag(true);
		}
		quote.setAttachments(attachments);
		if (attachments != null) {
			for (int k = 0; k < attachments.size(); k++) {
				attachments.get(k).setQuote(quote);
			}
		}
        
		
		quote.setQuoteItems(quoteItems);
		for (int i = 0; i < quoteItems.size(); i++) {
			// add action to quoteItem by damon@20160805
			quoteItems.get(i).setAction(ActionEnum.RFQ_SUBMISSION_SBUMIT.name());
			//quoteItems.get(i).setQuote(quote);
		}
		quoteSB.postProcessAfterRfqSubmitFinish(quoteItems);
		// Fixed issue 1570. if it is AQ update reference margin
		//quoteItems = myQuoteSearchSB.updateReferenceMarginForSubmission(quoteItems);

		// below for-loop is used for re-assign default_cost_indicator

		for (QuoteItem quoteItem : quote.getQuoteItems()) {
			// exclude other quote item when request from resubmit invlid
			// button.
			if (quote.getSubmissionDate() != quoteItem.getSubmissionDate()) {
				continue;
			}

			if (quoteItem.getStage() != null && !quoteItem.getStage().equals(QuoteSBConstant.QUOTE_STAGE_PENDING)) {
				continue;
			}

		//	List<String> failedCostIndicator = new ArrayList<String>();
			RfqItemVO rfqItem = getRfqItemVO(quoteItem);
			if (rfqItem == null) {
				continue;
			}
			
			
	/*		Material lowestCostMaterial = rfqItem.getMaterial();
			if (lowestCostMaterial == null) {
				continue;
			}

			//NormalPricer abookCostMD = lowestCostMaterial.getAbookMaterialDetail();
			Object costIndicator = nextCostIndicator(rfqItem, failedCostIndicator);
			if (costIndicator == null) {
				continue;
			}*/
			
			//quoteItem = copyCostIndicatorRelatedData1ToQuoteItem(quoteItem, rfqItem);
			//quoteItem = copyCostIndicatorRelatedData2ToQuoteItem(quoteItem, rfqItem);
			///quoteItem = copyCostIndicatorRelatedData3ToQuoteItem(quoteItem, rfqItem);
/*
			if (costIndicator instanceof ContractPricer) {
				ContractPricer newProperMD = (ContractPricer) costIndicator;
				if (abookCostMD != null) {
					rfqItem = loadCostIndicatorInfo(rfqItem, abookCostMD, lowestCostMaterial);
				}
				rfqItem = loadContractPriceInfo(rfqItem, newProperMD);
				rfqItem.setCostIndicatorType(QuoteSBConstant.RFQ_COST_INDICATOR_TYPE_C);
				copyCostIndicatorRelatedData1ToQuoteItem(quoteItem, rfqItem);
				copyCostIndicatorRelatedData2ToQuoteItem(quoteItem, rfqItem);
				copyCostIndicatorRelatedData3ToQuoteItem(quoteItem, rfqItem);
			} else if (costIndicator instanceof ProgramPricer) {
				ProgramPricer newProperMD = (ProgramPricer) costIndicator;
				rfqItem = loadProgramCostIndicatorInfo(rfqItem, newProperMD, lowestCostMaterial);
				rfqItem.setCostIndicatorType(QuoteSBConstant.RFQ_COST_INDICATOR_TYPE_P);
				copyCostIndicatorRelatedData1ToQuoteItem(quoteItem, rfqItem);
				copyCostIndicatorRelatedData2ToQuoteItem(quoteItem, rfqItem);
				copyCostIndicatorRelatedData3ToQuoteItem(quoteItem, rfqItem);
			} else if (costIndicator instanceof NormalPricer) {
				NormalPricer newProperMD = (NormalPricer) costIndicator;
				rfqItem = loadCostIndicatorInfo(rfqItem, newProperMD, lowestCostMaterial);
				rfqItem.setCostIndicatorType(QuoteSBConstant.RFQ_COST_INDICATOR_TYPE_N);
				quoteItem = copyCostIndicatorRelatedData1ToQuoteItem(quoteItem, rfqItem);
				quoteItem = copyCostIndicatorRelatedData2ToQuoteItem(quoteItem, rfqItem);
				quoteItem = copyCostIndicatorRelatedData3ToQuoteItem(quoteItem, rfqItem);
			}
*/
		}

		// below for-loop is used for applying cost_indicator_rule
		/*for (QuoteItem quoteItem : quote.getQuoteItems()) {
			if (quoteItem.getStage() != null && !quoteItem.getStage().equals(QuoteSBConstant.QUOTE_STAGE_PENDING)) {
				continue;
			}

			String costIndicator = quoteItem.getCostIndicator();
			Material material = quoteItem.getQuotedMaterial();
			if (costIndicator == null || material == null) { // quoteItem not
																// assigned a
																// default cost
																// indicator
				continue;
			}
			PricerInfo pricerInfo = new PricerInfo();
			Object minCostMaterialDetail = null;
			NormalPricer aBookCostMatrialDetail = material.getAbookMaterialDetail();
			if (aBookCostMatrialDetail == null) {
				continue;
			}

			ContractPricer cp = material.getContractPrice();
			if (cp != null && cp.getCostIndicator() != null
					&& cp.getCostIndicator().getName().equalsIgnoreCase(costIndicator)) {
				minCostMaterialDetail = cp;
			} else {
				List<NormalPricer> materialDetails = quoteItem.getQuotedMaterial().getProperMaterialDetailList();
				if (materialDetails != null && !materialDetails.isEmpty()) {
					for (NormalPricer d : materialDetails) {
						if (d.getCostIndicator().getName().equals(costIndicator)) {
							minCostMaterialDetail = d;
							break;
						}
					}
				}
			}
			if (minCostMaterialDetail != null) {

				pricerInfo.setCostIndicator(costIndicator);
				quoteSB.fillPricingInfo(pricerInfo, minCostMaterialDetail, aBookCostMatrialDetail);
				pricerInfo.fillPriceInfoToQuoteItem(quoteItem, false);
				quoteItem.setCostIndicator(costIndicator);
			}

		}*/

		// added by June for RMB cur project 20140704
		//quoteSB.updateRateForQuoteItems(quote.getQuoteItems());
        
		calculateQuoteItemByStatus(quote);
		itQuoteCount += tmpItQuoteCount;

		// logger.log(Level.INFO, "PERFORMANCE END - submitRfqs()");
		// added by damon
		// setQuoteItemDesign(quote);
		return quote;
	}

	/**   
	* @Description: TODO
	* @author 042659
	* @param tmpAqQuoteCount
	* @param tmpQcQuoteCount      
	* @return void    
	* @throws  
	*/  
	private void calculateQuoteItemByStatus(Quote q) {
		for (QuoteItem item : q.getQuoteItems()) {
			if (continueQuoteItems != null && continueQuoteItems.size() > 0) {
			if (continueQuoteItems.contains(new Long(item.getId())))
				continue;
			}
			if (QuoteSBConstant.RFQ_STATUS_AQ.equals(item.getStatus())) {
				aqQuoteCount++;
			} else if (QuoteSBConstant.RFQ_STATUS_QC.equals(item.getStatus())||QuoteSBConstant.RFQ_STATUS_BQ.equals(item.getStatus())) {
				qcQuoteCount++;
			} else if (QuoteSBConstant.RFQ_STATUS_DQ.equals(item.getStatus())) {
				draftQuoteCount++;
			}

		}
		
	}

	/**   
	* @Description: TODO
	* @author 042659
	* @param rfqItem
	* @param projectIds      
	* @return void    
	* @throws  
	*/  
	private void extractProjectIds(RfqItemVO rfqItem, List<Long> projectIds) {
		//this part for drmsd by DamonChen@20171108 begin
		if (rfqItem.getDrmsNumber() != null && rfqItem.getDrmsNumber() > 0
				&& !projectIds.contains(rfqItem.getDrmsNumber())) {
			projectIds.add(rfqItem.getDrmsNumber());
		}
		List<String> projectNameList = ProjectNameCacheManager.getProjectNamesBySoldToAndEndCustomer(
				rfqHeader.getSoldToCustomerNumber(), rfqHeader.getEndCustomerNumber());
		if (projectNameList == null)
			projectNameList = new ArrayList<String>();
		if (!QuoteUtil.isEmpty(rfqItem.getProjectName()) && !projectNameList.contains(rfqItem.getProjectName())) {
			projectNameList.add(rfqItem.getProjectName());
			ProjectNameCacheManager.putProjectNamesBySoldToAndEndCustomer(rfqHeader.getSoldToCustomerNumber(),
					rfqHeader.getEndCustomerNumber(), projectNameList);
			projectNameList = null;
		}
		//this part for drmsd by DamonChen@20171108 end
		
	}

	/**   
	* @Description: TODO
	* @author 042659
	* @param quoteItem
	* @param rfqItem      
	* @return void    
	* @throws  
	*/  
	private void copyRfqItemDataToQuoteItem(QuoteItem quoteItem, RfqItemVO rfqItem) {

		
		//from copyBasicDataToQuoteItem
		quoteItem.setValidFlag(true);
		quoteItem.setAllocationFlag(QuoteSBConstant.DEFAULT_ALLOCATION_FLAG);
		quoteItem.setRevertVersion(QuoteSBConstant.DEFAULT_REVERT_VERISON);
		quoteItem.setDesignInCat(rfqItem.getDesignInCat());
		quoteItem.setSentOutTime(QuoteUtil.getCurrentTime());
		quoteItem.setApplication(rfqItem.getApplication());
		quoteItem.setCompetitorInformation(rfqItem.getCompetitorInformation());
		quoteItem.setQuoteType(rfqItem.getQuoteType());
		quoteItem.setLoaFlag(rfqItem.isLoa());
		if (rfqItem.getEau() != null) {
			quoteItem.setEau(Integer.valueOf(rfqItem.getEau()));
		}
		quoteItem.setEnquirySegment(rfqItem.getEnquirySegment());
		quoteItem.setRemarks(rfqItem.getItemRemarks());
		quoteItem.setLastUpdatedBy(user.getEmployeeId());
		quoteItem.setLastUpdatedName(user.getName());
		quoteItem.setLastUpdatedOn(QuoteUtil.getCurrentTime());
		quoteItem.setMpSchedule(rfqItem.getMpSchedule());
		quoteItem.setPpSchedule(rfqItem.getPpSchedule());
		// the change for DRMS margin retention project
		quoteItem.setDrNedaId(rfqItem.getDrNedaId());
		quoteItem.setDrNedaLineNumber(rfqItem.getDrNedaLineNumber());
		quoteItem.setDesignLocation(rfqItem.getDesignLocation());
		quoteItem.setDesignRegion(rfqItem.getDesignRegion());
		quoteItem.setDrmsNumber(rfqItem.getDrmsNumber());
		
		quoteItem.setSalesCost(rfqItem.getSalesCost());
		quoteItem.setSalesCostFlag(rfqItem.isSalesCostFlag());
		quoteItem.setSuggestedResale(rfqItem.getSuggestedResale());
		quoteItem.setSalesCostType(rfqItem.getSalesCostType());
		quoteItem.setPmoq(rfqItem.getPmoq());
		
		quoteItem.setStatus(rfqItem.getStatus());
		quoteItem.setStage(rfqItem.getStage());
		if(!QuoteUtil.isEmpty(rfqItem.getQuotedPrice())){
			quoteItem.setQuotedPrice(Double.parseDouble(rfqItem.getQuotedPrice()));
		}
	
		
		quoteItem.setBmtCheckedFlag(rfqItem.isBmtCheckedFlag());
		copyCustomerDataToQuoteItem(quoteItem, rfqItem);
		copyCostIndicatorRelatedData1ToQuoteItem(quoteItem, rfqItem);
		copyCostIndicatorRelatedData2ToQuoteItem(quoteItem, rfqItem);
	    copyCostIndicatorRelatedData3ToQuoteItem(quoteItem, rfqItem);
	  
	    
		if (rfqItem.getStatus() != null
				&& rfqItem.getStatus().equals(QuoteSBConstant.RFQ_STATUS_AQ)) {
			// TW Enhancement - add SAP part number to Quote
			// Item
			if (QuoteUtil.isEmpty(rfqItem.getSapPartNumber())) {
				String sapPartNumber = materialSB.findDefaultSapPartNumber(
						rfqItem.getRequiredPartNumber(), rfqItem.getMfr(), bizUnit);
				quoteItem.setSapPartNumber(sapPartNumber);
			}
		}
		
		quoteItem.applyNcnrFilter();


	}

	private void calcResaleMargin(QuoteItem quoteItem) {
		if (quoteItem.getQuotedPrice() != null && quoteItem.getCost() != null) {
			try {
				// Updated by Tonmy Li on 19 Aug . the
				// resales margin is equals to Quote
				// margin
				quoteItem.setResaleMargin(
						QuoteUtil.calculateResalesMargin(quoteItem.getCost(), quoteItem.getQuotedPrice()));

			} catch (Exception ex) {
				LOGGER.log(Level.SEVERE,
						"Error occured in setting Resale margin for Quote Item quoted price: "
								+ quoteItem.getQuotedPrice() + ", Quoted Item Cost: " + quoteItem.getCost() + ", "
								+ "Reason for failure: " + MessageFormatorUtil.getParameterizedStringFromException(ex),
						ex);
			}
		}
		if (quoteItem.getTargetResale() != null && quoteItem.getCost() != null) {
			try {
				// Updated by Tonmy Li on 19 Aug . the
				// value should be set to target margin
				quoteItem.setTargetMargin(
						QuoteUtil.calculateTargetMargin(quoteItem.getCost(), quoteItem.getTargetResale()));
			} catch (Exception ex) {
				LOGGER.log(Level.SEVERE,
						"Error occured in setting Target Margin for Quote Item Target Resale: "
								+ quoteItem.getTargetResale() + ", Quoted Item Cost: " + quoteItem.getCost() + ", "
								+ "Reason for failure: " + MessageFormatorUtil.getParameterizedStringFromException(ex),
						ex);
			}
		}
	}

	private void saveRFQs(List<Quote> quotes) {
		quoteSB.submitRFQs(quotes);
		// logInfo("finish the save the submitting RFQs");
		for (Quote quote : quotes) {
			logInfo("Submitted Quote : Form#" + quote.getFormNumber());
			groupInternalTransferRfq(quote.getQuoteItems(), rfqHeader.isContinueSubmit());
			List<QuoteItem> aqQuoteItems = new ArrayList<QuoteItem>();
			if (quote.getQuoteItems() != null) {
				for (QuoteItem aqQuoteItem : quote.getQuoteItems()) {
					// Added by tonmy on 13 Sep 2013 . filtred out the old quote
					// item.
					if (continueQuoteItems != null && continueQuoteItems.size() > 0) {
						if (continueQuoteItems.contains(new Long(aqQuoteItem.getId())))
							continue;
					}
					if (aqQuoteItem.getStage() != null
							&& aqQuoteItem.getStage().equals(QuoteSBConstant.QUOTE_STAGE_FINISH))
						aqQuoteItems.add(aqQuoteItem);
				}
			}
			if (aqQuoteItems.size() > 0) {
				logInfo("start to send to SAP");
				ejbCommonSB.createQuoteToSo(aqQuoteItems);
				logInfo("finish to send to SAP");
			}
		}
	}

	public void groupInternalTransferRfq(List<QuoteItem> quotedItems, boolean isContinue) {

		// logger.log(Level.INFO,
		// "PERFORMANCE START - groupInternalTransferRfq()");

		List<QuoteItem> itQuoteItems = new ArrayList<QuoteItem>();
		for (QuoteItem quoteItem : quotedItems) {
			// if (quoteItem.getStatus() != null
			// && quoteItem.getStatus().equals(
			// QuoteSBConstant.RFQ_STATUS_IT))
			// {
			// itQuoteItems.add(quoteItem);
			// }
			if (quoteItem.getStatus() != null && quoteItem.getStatus().equals(QuoteSBConstant.RFQ_STATUS_IT)) {
				if (isContinue) {
					if (quoteItem.isITStatus()) {
						itQuoteItems.add(quoteItem);
					}
				} else {
					itQuoteItems.add(quoteItem);
				}
			}
		}
		List<WorkingPlatformEmailVO> updateStatusItems = new ArrayList<WorkingPlatformEmailVO>();
		List<List<QuoteItem>> quoteItemLists = new ArrayList<List<QuoteItem>>();
		if (itQuoteItems != null && itQuoteItems.size() > 0) {
			List<String> dupQuoteNumbers = new ArrayList<String>();
			for (QuoteItem quoteItem : itQuoteItems) {
				if (!dupQuoteNumbers.contains(quoteItem.getQuoteNumber())) {
					List<QuoteItem> quoteItemList = new ArrayList<QuoteItem>();
					quoteItemList.add(quoteItem);
					dupQuoteNumbers.add(quoteItem.getQuoteNumber());
					for (QuoteItem subQuoteItem : itQuoteItems) {
						if (!dupQuoteNumbers.contains(subQuoteItem.getQuoteNumber())) {
							// Delinkage change on Oct 29 , 2013 by Tonmy
							// if (quoteItem
							// .getRequestedMaterial()
							// .getManufacturer()
							// .getName()
							// .equals(subQuoteItem.getRequestedMaterial()
							// .getManufacturer().getName()))
							if (quoteItem.getRequestedMfr().getName()
									.equals(subQuoteItem.getRequestedMfr().getName())) {
								if ((quoteItem.getProductGroup2() != null && subQuoteItem.getProductGroup2() != null
										&& quoteItem.getProductGroup2().getName()
												.equals(subQuoteItem.getProductGroup2().getName()))
										|| (quoteItem.getProductGroup2() == null
												&& subQuoteItem.getProductGroup2() == null)) {
									if ((quoteItem.getQuote().getSales().getTeam() != null
											&& subQuoteItem.getQuote().getSales().getTeam() != null
											&& quoteItem.getQuote().getSales().getTeam().getName()
													.equals(subQuoteItem.getQuote().getSales().getTeam().getName()))
											|| (quoteItem.getQuote().getSales().getTeam() == null
													&& quoteItem.getQuote().getSales().getTeam() == null)) {
										// if ((quoteItem.getMaterialType() !=
										// null
										// && subQuoteItem
										// .getMaterialType() != null &&
										// quoteItem
										// .getMaterialType()
										// .getName()
										// .equals(subQuoteItem
										// .getMaterialType()
										// .getName()))
										// || (quoteItem.getMaterialType() ==
										// null && subQuoteItem
										// .getMaterialType() == null))
										// Material restructure and quote_item
										// delinkage. changed on 10 Apr 2014.
										if ((quoteItem.getMaterialTypeId() != null
												&& subQuoteItem.getMaterialTypeId() != null
												&& quoteItem.getMaterialTypeId()
														.equalsIgnoreCase(subQuoteItem.getMaterialTypeId()))
												|| (quoteItem.getMaterialTypeId() == null
														&& subQuoteItem.getMaterialTypeId() == null)) {
											quoteItemList.add(subQuoteItem);
											dupQuoteNumbers.add(subQuoteItem.getQuoteNumber());
										}
									}
								}
							}
						}
					}
					quoteItemLists.add(quoteItemList);
				}
			}
		}
		for (List<QuoteItem> quoteItemList : quoteItemLists) {
			QuoteItem quoteItem = quoteItemList.get(0);
			// Delinkage change on Oct 29 , 2013 by Tonmy
			// List<User> users = userSB.findPmForInternalTransfer(quoteItem
			// .getRequestedMaterial().getManufacturer(), quoteItem
			// .getProductGroup2(), quoteItem.getQuote().getTeam(),
			// quoteItem.getMaterialType());
			List<String> users = quoteItem.getPmEmployeeList();

			List<String> quoteItems = new ArrayList<String>();
			List<String> formNumbers = new ArrayList<String>();
			List<String> partNumbers = new ArrayList<String>();
			List<String> customers = new ArrayList<String>();
			List<String> projects = new ArrayList<String>();
			List<String> emails = new ArrayList<String>();
			// Delinkage change on Oct 29 , 2013 by Tonmy
			// Manufacturer mfr = quoteItem.getRequestedMaterial()
			// .getManufacturer();
			Manufacturer mfr = quoteItem.getRequestedMfr();
			for (QuoteItem item : quoteItemList) {
				quoteItems.add(item.getQuoteNumber());
				// Delinkage change on Oct 29 , 2013 by Tonmy
				// partNumbers.add(item.getRequestedMaterial()
				// .getFullMfrPartNumber());
				partNumbers.add(item.getRequestedPartNumber());
				if (item.getSoldToCustomer() != null && getCustomerFullName(item.getSoldToCustomer()) != null
						&& !customers.contains(getCustomerFullName(item.getSoldToCustomer()))) {
					customers.add(getCustomerFullName(item.getSoldToCustomer()));
				}
				if (item.getProjectName() != null && !projects.contains(item.getProjectName())) {
					projects.add(item.getProjectName());
				}
			}
			if (users != null) {
				for (String pmEmployeeId : users) {
					emails.add(pmEmployeeId);
				}
			}
			WorkingPlatformEmailVO workingPlatformEmailVO = new WorkingPlatformEmailVO();
			workingPlatformEmailVO.setEmailsTo(emails);
			workingPlatformEmailVO.setMfr(mfr.getName());
			workingPlatformEmailVO.setQuoteItems(quoteItemList);
			workingPlatformEmailVO.setPartNumbers(partNumbers);
			workingPlatformEmailVO.setCustomers(customers);
			workingPlatformEmailVO.setProjects(projects);
			updateStatusItems.add(workingPlatformEmailVO);
		}
		updateInternalTransfer(updateStatusItems);

		// logger.log(Level.INFO,
		// "PERFORMANCE END - groupInternalTransferRfq()");
	}

	public void updateInternalTransfer(List<WorkingPlatformEmailVO> updateStatusItems) {

		// logger.log(Level.INFO,
		// "PERFORMANCE START - updateInternalTransfer()");

		if (updateStatusItems != null) {
			for (int i = 0; i < updateStatusItems.size(); i++) {
				WorkingPlatformEmailVO workingPlatformEmailVO = updateStatusItems.get(i);
				String productLine = workingPlatformEmailVO.getMfr();
				String content = "Dear PM,<br/><br/>";
				content += "This is an Internal Transfer RFQs. Please provide cost, suggested Resale and related quoting information for this RFQs.<br/>";
				content += "Please click into the link(s) for your response<br/><br/>";
				content += "Customer: " + workingPlatformEmailVO.getCustomers();
				content += "<br/>";
				content += "Project: " + workingPlatformEmailVO.getProjects();
				content += "<br/><br/>";
				content += "This RFQs:<br/>";

				if (workingPlatformEmailVO.getQuoteItems() != null) {
					for (QuoteItem item : workingPlatformEmailVO.getQuoteItems()) {
						// Delinkage change on Oct 29 , 2013 by Tonmy
						// content += item.getRequestedMaterial()
						// .getFullMfrPartNumber();
						content += item.getRequestedPartNumber();
						content += ", ";
						content += getUrl() + "/RFQ/ResponseInternalTransfer.jsf?quoteNumber=" + item.getQuoteNumber();
						content += "</br>";
					}
				}

				content += "<br/>";
				content += "Your RFQs Pending List:</br>";
				content += "Your RFQ: " + getUrl() + "/RFQ/ResponseInternalTransfer.jsf";
				content += "<br/><br/>";

				if (!QuoteUtil.isEmpty(workingPlatformEmailVO.getMessage())) {
					content += "Remarks from Quote Centre : ";
					content += workingPlatformEmailVO.getMessage();
					content += "<br/><br/>";
				}

				List<String> emails = workingPlatformEmailVO.getEmailsTo();
				List<String> emailList = new ArrayList<String>();
				List<User> users = getUserSB().findByEmployeeIds(emails);
				for (User m : users) {
					emailList.add(m.getEmailAddress());
				}

				MailInfoBean mailInfoBean = new MailInfoBean();
				String formNumbers = "";
				if (workingPlatformEmailVO.getQuoteItems() != null) {
					for (QuoteItem item : workingPlatformEmailVO.getQuoteItems()) {
						if (formNumbers.indexOf(item.getQuote().getFormNumber()) == -1) {
							if (formNumbers.length() > 0)
								formNumbers += ",";
							formNumbers += item.getQuote().getFormNumber();
						}
					}
				}
				mailInfoBean.setMailSubject(productLine + " - " + formNumbers + " - Need Yr Action: Transferring RFQ");
				mailInfoBean.setMailFrom(user.getEmailAddress());
				mailInfoBean.setMailFromInName(user.getName());
				mailInfoBean.setMailTo(emailList);
				mailInfoBean.setMailCc(null);

				// remove the hardcode email content information
				String bu = bizUnit.getName();
				content += sysMaintSB.getEmailSignName(bu) + "<br/>" + sysMaintSB.getEmailHotLine(bu)
						+ "<br/>Email Box: " + sysMaintSB.getEmailSignContent(bu) + "<br/>";

				mailInfoBean.setMailContent(content);
				try {
					mailUtilsSB.sendHtmlMail(mailInfoBean);
				} catch (Exception e) {
					LOGGER.log(Level.SEVERE, "Send email failed, email sender: " + mailInfoBean.getMailFrom()
							+ ", Subject: " + mailInfoBean.getMailSubject() + ", Reason for failure: " + e.getMessage(),
							e);
				}

			}
		}

		// logger.log(Level.INFO, "PERFORMANCE END - updateInternalTransfer()");
	}

	public String convertCustomerSalesToSalesOrg(List<CustomerSale> customerSales) {
		return convertCustomerSalesToSalesOrg(customerSales, bizUnit.getName());
	}

	public QuoteSB getQuoteSB() {
		return quoteSB;
	}

	public void setQuoteSB(QuoteSB quoteSB) {
		this.quoteSB = quoteSB;
	}

	public RfqHeaderVO getRfqHeader() {
		return rfqHeader;
	}

	public void setRfqHeader(RfqHeaderVO rfqHeader) {
		this.rfqHeader = rfqHeader;
	}

	public String getRequestFormNumber() {
		return requestFormNumber;
	}

	public void setRequestFormNumber(String requestFormNumber) {
		this.requestFormNumber = requestFormNumber;
	}

	public List<RfqItemVO> getFilteredRfqItems() {
		return filteredRfqItems;
	}

	public void setFilteredRfqItems(List<RfqItemVO> filteredRfqItems) {
		this.filteredRfqItems = filteredRfqItems;
	}

	public List<RfqItemVO> getRfqItems() {
		return rfqItems;
	}

	public void setRfqItems(List<RfqItemVO> rfqItems) {
		this.rfqItems = rfqItems;
	}
	
	


	public RfqItemVO getSelectedRfqItem() {
		return selectedRfqItem;
	}

	public List<RfqItemVO> getDuplicatedrfqItems() {
		return duplicatedrfqItems;
	}

	public void setDuplicatedrfqItems(List<RfqItemVO> dupplicatedrfqItems) {
		this.duplicatedrfqItems = dupplicatedrfqItems;
	}

	public void setSelectedRfqItem(RfqItemVO selectedRfqItem) {
		this.selectedRfqItem = selectedRfqItem;
	}

	public boolean isNewPart() {
		return newPart;
	}

	public void setNewPart(boolean newPart) {
		this.newPart = newPart;
	}

	public List<PricerInfo> getRequiredPartNumberList() {
		return requiredPartNumberList;
	}

	public void setRequiredPartNumberList(List<PricerInfo> requiredPartNumberList) {
		this.requiredPartNumberList = requiredPartNumberList;
	}

	public List<PricerInfo> getSelectedRequiredPartNumberList() {
		return selectedRequiredPartNumberList;
	}

	public void setSelectedRequiredPartNumberList(List<PricerInfo> selectedRequiredPartNumberList) {
		this.selectedRequiredPartNumberList = selectedRequiredPartNumberList;
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

	public List<DrProjectVO> getDrProjects() {
		return drProjects;
	}

	public void setDrProjects(List<DrProjectVO> drProjects) {
		this.drProjects = drProjects;
	}

	public DrProjectVO getSelectedDrProject() {
		return selectedDrProject;
	}

	public void setSelectedDrProject(DrProjectVO selectedDrProject) {
		this.selectedDrProject = selectedDrProject;
	}

	public List<DrProjectVO> getFilteredDrProjects() {
		return filteredDrProjects;
	}

	public void setFilteredDrProjects(List<DrProjectVO> filteredDrProjects) {
		this.filteredDrProjects = filteredDrProjects;
	}

	public List<DrProjectVO> getRestOfDrProjects() {
		return restOfDrProjects;
	}

	public void setRestOfDrProjects(List<DrProjectVO> restOfDrProjects) {
		this.restOfDrProjects = restOfDrProjects;
	}

	public SelectItem[] getCustomerTypeSelectList() {
		return customerTypeSelectList;
	}

	public void setCustomerTypeSelectList(SelectItem[] customerTypeSelectList) {
		this.customerTypeSelectList = customerTypeSelectList;
	}

	public SelectItem[] getProjectNameSelectList() {
		return projectNameSelectList;
	}

	public void setProjectNameSelectList(SelectItem[] projectNameSelectList) {
		this.projectNameSelectList = projectNameSelectList;
	}

	public SelectItem[] getDesignLocationSelectList() {
		return designLocationSelectList;
	}

	public void setDesignLocationSelectList(SelectItem[] designLocationSelectList) {
		this.designLocationSelectList = designLocationSelectList;
	}

	public SelectItem[] getDesignRegionSelectList() {
		return designRegionSelectList;
	}

	public void setDesignRegionSelectList(SelectItem[] designRegionSelectList) {
		this.designRegionSelectList = designRegionSelectList;
	}

	public SelectItem[] getApplicationSelectList() {
		return applicationSelectList;
	}

	public void setApplicationSelectList(SelectItem[] applicationSelectList) {
		this.applicationSelectList = applicationSelectList;
	}

	public SelectItem[] getTeamSelectList() {
		return teamSelectList;
	}

	public void setTeamSelectList(SelectItem[] teamSelectList) {
		this.teamSelectList = teamSelectList;
	}

	public SelectItem[] getEndCustomerSelectList() {
		return endCustomerSelectList;
	}

	public void setEndCustomerSelectList(SelectItem[] endCustomerSelectList) {
		this.endCustomerSelectList = endCustomerSelectList;
	}

	public SelectItem[] getSoldToCustomerNumberSelectList() {
		return soldToCustomerNumberSelectList;
	}

	public void setSoldToCustomerNumberSelectList(SelectItem[] soldToCustomerNumberSelectList) {
		this.soldToCustomerNumberSelectList = soldToCustomerNumberSelectList;
	}

	public SelectItem[] getShipToCustomerNumberSelectList() {
		return shipToCustomerNumberSelectList;
	}

	public void setShipToCustomerNumberSelectList(SelectItem[] shipToCustomerNumberSelectList) {
		this.shipToCustomerNumberSelectList = shipToCustomerNumberSelectList;
	}

	public SelectItem[] getEndCustomerNumberSelectList() {
		return endCustomerNumberSelectList;
	}

	public void setEndCustomerNumberSelectList(SelectItem[] endCustomerNumberSelectList) {
		this.endCustomerNumberSelectList = endCustomerNumberSelectList;
	}

	public SelectItem[] getEndCustomerNameSelectList() {
		return endCustomerNameSelectList;
	}

	public void setEndCustomerNameSelectList(SelectItem[] endCustomerNameSelectList) {
		this.endCustomerNameSelectList = endCustomerNameSelectList;
	}

	public SelectItem[] getCurrencySelectList() {
		return currencySelectList;
	}

	public void setCurrencySelectList(SelectItem[] currencySelectList) {
		this.currencySelectList = currencySelectList;
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

	public SelectItem[] getPpScheduleSelectList() {
		return ppScheduleSelectList;
	}

	public void setPpScheduleSelectList(SelectItem[] ppScheduleSelectList) {
		this.ppScheduleSelectList = ppScheduleSelectList;
	}

	public SelectItem[] getMpScheduleSelectList() {
		return mpScheduleSelectList;
	}

	public void setMpScheduleSelectList(SelectItem[] mpScheduleSelectList) {
		this.mpScheduleSelectList = mpScheduleSelectList;
	}

	public String getCustomerTypeSearch() {
		return customerTypeSearch;
	}

	public void setCustomerTypeSearch(String customerTypeSearch) {
		this.customerTypeSearch = customerTypeSearch;
	}

	public String getCustomerNameSearch() {
		return customerNameSearch;
	}

	public void setCustomerNameSearch(String customerNameSearch) {
		this.customerNameSearch = customerNameSearch;
	}

	public int getItemNumberCount() {
		return itemNumberCount;
	}

	public void setItemNumberCount(int itemNumberCount) {
		this.itemNumberCount = itemNumberCount;
	}

	public String getCurrentToggleMfr() {
		return currentToggleMfr;
	}

	public void setCurrentToggleMfr(String currentToggleMfr) {
		this.currentToggleMfr = currentToggleMfr;
	}

	public boolean isDrmsFound() {
		return drmsFound;
	}

	public void setDrmsFound(boolean drmsFound) {
		this.drmsFound = drmsFound;
	}

	public int getSearchCustomersCount() {
		return searchCustomersCount;
	}

	public void setSearchCustomersCount(int searchCustomersCount) {
		this.searchCustomersCount = searchCustomersCount;
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

	public int getToBeNewPartItemNumber() {
		return toBeNewPartItemNumber;
	}

	public void setToBeNewPartItemNumber(int toBeNewPartItemNumber) {
		this.toBeNewPartItemNumber = toBeNewPartItemNumber;
	}

	public String getToBeNewPartNumber() {
		return toBeNewPartNumber;
	}

	public void setToBeNewPartNumber(String toBeNewPartNumber) {
		this.toBeNewPartNumber = toBeNewPartNumber;
	}

	public void setSoldToCodeSearch(boolean soldToCodeSearch) {
		this.soldToCodeSearch = soldToCodeSearch;
	}

	public void setShipToCodeSearch(boolean shipToCodeSearch) {
		this.shipToCodeSearch = shipToCodeSearch;
	}

	public void setEndCustomerCodeSearch(boolean endCustomerCodeSearch) {
		this.endCustomerCodeSearch = endCustomerCodeSearch;
	}

	public SelectItem[] getMfrSelectList() {
		return mfrSelectList;
	}

	public void setMfrSelectList(SelectItem[] mfrSelectList) {
		this.mfrSelectList = mfrSelectList;
	}

	public boolean soldToCodeSearch() {
		return soldToCodeSearch;
	}

	public boolean shipToCodeSearch() {
		return shipToCodeSearch;
	}

	public boolean endCustomerCodeSearch() {
		return endCustomerCodeSearch;
	}

	public SelectItem[] getEnquirySegmentSelectList() {
		return enquirySegmentSelectList;
	}

	public void setEnquirySegmentSelectList(SelectItem[] enquirySegmentSelectList) {
		this.enquirySegmentSelectList = enquirySegmentSelectList;
	}

	public SelectItem[] getBizUnitSelectList() {
		return bizUnitSelectList;
	}

	public void setBizUnitSelectList(SelectItem[] bizUnitSelectList) {
		this.bizUnitSelectList = bizUnitSelectList;
	}

	public BizUnit getBizUnit() {
		return bizUnit;
	}

	public void setBizUnit(BizUnit bizUnit) {
		this.bizUnit = bizUnit;
	}

	public UploadedFile getRfqExcel() {
		return rfqExcel;
	}

	public void setRfqExcel(UploadedFile rfqExcel) {
		this.rfqExcel = rfqExcel;
	}

	public CustomerSB getCustomerSB() {
		return customerSB;
	}

	public void setCustomerSB(CustomerSB customerSB) {
		this.customerSB = customerSB;
	}

	public int getSoldToCodeItemSearch() {
		return soldToCodeItemSearch;
	}

	public void setSoldToCodeItemSearch(int soldToCodeItemSearch) {
		this.soldToCodeItemSearch = soldToCodeItemSearch;
	}

	public int getShipToCodeItemSearch() {
		return shipToCodeItemSearch;
	}

	public void setShipToCodeItemSearch(int shipToCodeItemSearch) {
		this.shipToCodeItemSearch = shipToCodeItemSearch;
	}

	public int getEndCustomerCodeItemSearch() {
		return endCustomerCodeItemSearch;
	}

	public void setEndCustomerCodeItemSearch(int endCustomerCodeItemSearch) {
		this.endCustomerCodeItemSearch = endCustomerCodeItemSearch;
	}

	public boolean isSoldToCodeSearch() {
		return soldToCodeSearch;
	}

	public boolean isShipToCodeSearch() {
		return shipToCodeSearch;
	}

	public boolean isEndCustomerCodeSearch() {
		return endCustomerCodeSearch;
	}

	public int getItemNumberForAttachment() {
		return itemNumberForAttachment;
	}

	public void setItemNumberForAttachment(int itemNumberForAttachment) {
		this.itemNumberForAttachment = itemNumberForAttachment;
	}

	public SelectItem[] getCustomerTypeSearchSelectList() {
		return customerTypeSearchSelectList;
	}

	public void setCustomerTypeSearchSelectList(SelectItem[] customerTypeSearchSelectList) {
		this.customerTypeSearchSelectList = customerTypeSearchSelectList;
	}

	public BizUnitSB getBizUnitSB() {
		return bizUnitSB;
	}

	public void setBizUnitSB(BizUnitSB bizUnitSB) {
		this.bizUnitSB = bizUnitSB;
	}

	public SelectItem[] getCsSelectList() {
		return csSelectList;
	}

	public void setCsSelectList(SelectItem[] csSelectList) {
		this.csSelectList = csSelectList;
	}

	public DrmsSB getDrmsSB() {
		return drmsSB;
	}

	public void setDrmsSB(DrmsSB drmsSB) {
		this.drmsSB = drmsSB;
	}

	public UserSB getUserSB() {
		return userSB;
	}

	public void setUserSB(UserSB userSB) {
		this.userSB = userSB;
	}

	public TeamSB getTeamSB() {
		return teamSB;
	}

	public void setTeamSB(TeamSB teamSB) {
		this.teamSB = teamSB;
	}

	public Quote getResultQuote() {
		return resultQuote;
	}

	public void setResultQuote(Quote resultQuote) {
		this.resultQuote = resultQuote;
	}

	public List<QuoteItem> getResultQuoteItems() {
		return resultQuoteItems;
	}

	public void setResultQuoteItems(List<QuoteItem> resultQuoteItems) {
		this.resultQuoteItems = resultQuoteItems;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public StreamedContent getRfqSubmissionResultReport() {
		downloadRfqSubmissionResultReport();
		return rfqSubmissionResultReport;
	}

	public void setRfqSubmissionResultReport(StreamedContent rfqSubmissionResultReport) {
		this.rfqSubmissionResultReport = rfqSubmissionResultReport;
	}

	public StreamedContent getDrmsResultReport() {
		downloadDrmsResultReport();
		return drmsResultReport;
	}

	public void setDrmsResultReport(StreamedContent drmsResultReport) {
		this.drmsResultReport = drmsResultReport;
	}

	public String getToBeNewMfr() {
		return toBeNewMfr;
	}

	public void setToBeNewMfr(String toBeNewMfr) {
		this.toBeNewMfr = toBeNewMfr;
	}

	public int getAqQuoteCount() {
		return aqQuoteCount;
	}

	public void setAqQuoteCount(int aqQuoteCount) {
		this.aqQuoteCount = aqQuoteCount;
	}

	public int getQcQuoteCount() {
		return qcQuoteCount;
	}

	public void setQcQuoteCount(int qcQuoteCount) {
		this.qcQuoteCount = qcQuoteCount;
	}

	public int getItQuoteCount() {
		return itQuoteCount;
	}

	public void setItQuoteCount(int itQuoteCount) {
		this.itQuoteCount = itQuoteCount;
	}

	public int getDraftQuoteCount() {
		return draftQuoteCount;
	}

	public void setDraftQuoteCount(int draftQuoteCount) {
		this.draftQuoteCount = draftQuoteCount;
	}

	public boolean isDeleteCustomer() {
		return deleteCustomer;
	}

	public void setDeleteCustomer(boolean deleteCustomer) {
		this.deleteCustomer = deleteCustomer;
	}

	public RoleSB getRoleSB() {
		return roleSB;
	}

	public void setRoleSB(RoleSB roleSB) {
		this.roleSB = roleSB;
	}

	public SAPWebServiceSB getSapWebServiceSB() {
		return sapWebServiceSB;
	}

	public void setSapWebServiceSB(SAPWebServiceSB sapWebServiceSB) {
		this.sapWebServiceSB = sapWebServiceSB;
	}

	public ManufacturerSB getManufacturerSB() {
		return manufacturerSB;
	}

	public void setManufacturerSB(ManufacturerSB manufacturerSB) {
		this.manufacturerSB = manufacturerSB;
	}

	public boolean isSales() {
		return isSales;
	}

	public void setSales(boolean isSales) {
		this.isSales = isSales;
	}


	public StreamedContent getRfqTemplate() {
		String bizUnit = rfqHeader.getBizUnit();
		String template = null;
		if (org.apache.commons.lang.StringUtils.isNotBlank(bizUnit)) {
			template = sysMaintSB.getRFQSubmissionTemplate(bizUnit);
			if (org.apache.commons.lang.StringUtils.isNotBlank(template)) {
				template = template.trim();
			}
		}

		if (template == null) {
			return null;
		} else {
			String filePath = DeploymentConfiguration.configPath + File.separator + template;
			FileInputStream fileIn = null;
			try {
				fileIn = new FileInputStream(filePath);
				if (fileIn != null)
					rfqTemplate = new DefaultStreamedContent(fileIn, DownloadUtil.getMimeType(template), template);
			} catch (FileNotFoundException e) {
				FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_WARN,
						ResourceMB.getText("wq.message.downloadfail") + ".", "");
				FacesContext.getCurrentInstance().addMessage("RfqSubmissionGrowl_1", msg);
				LOG.log(Level.SEVERE, "File on this path " + filePath + " not found " + e.getMessage(), e);
			}

			return rfqTemplate;
		}
	}

	public void setRfqTemplate(StreamedContent rfqTemplate) {
		this.rfqTemplate = rfqTemplate;
	}

	public void setRequestQuoteId(String requestQuoteId) {
		this.requestQuoteId = requestQuoteId;
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

	public UploadedFile getFormAttachment() {
		return formAttachment;
	}

	public void setFormAttachment(UploadedFile formAttachment) {
		this.formAttachment = formAttachment;
		if (this.formAttachment != null) {
			formAttachmentName = this.formAttachment.getFileName();
			/*
			 * String[] splitName = formAttachmentName.split("\\");
			 * if(splitName.length > 1) formAttachmentName =
			 * splitName[splitName.length-1];
			 */
		}
	}

	public List<User> getCsList() {
		return csList;
	}

	public void setCsList(List<User> csList) {
		this.csList = csList;
	}

	public List<User> getSelectedCsList() {
		return selectedCsList;
	}

	public void setSelectedCsList(List<User> selectedCsList) {
		this.selectedCsList = selectedCsList;
	}

	public String getRequestAction() {
		return requestAction;
	}

	public void setRequestAction(String requestAction) {
		this.requestAction = requestAction;
	}

	public String getRequestQuoteId() {
		return requestQuoteId;
	}

	public String getRequestQuoteItemId() {
		return requestQuoteItemId;
	}

	public void setRequestQuoteItemId(String requestQuoteItemId) {
		this.requestQuoteItemId = requestQuoteItemId;
	}

	public int getItemNumberForPartSearch() {
		return itemNumberForPartSearch;
	}

	public void setItemNumberForPartSearch(int itemNumberForPartSearch) {
		this.itemNumberForPartSearch = itemNumberForPartSearch;
	}

	public int getItemNumberForPopup() {
		return itemNumberForPopup;
	}

	public void setItemNumberForPopup(int itemNumberForPopup) {
		this.itemNumberForPopup = itemNumberForPopup;
	}

	public String getFormAttachmentName() {
		return formAttachmentName;
	}

	public void setFormAttachmentName(String formAttachmentName) {
		this.formAttachmentName = formAttachmentName;
	}

	public String getRequestFailSoldToCode() {
		return requestFailSoldToCode;
	}

	public void setRequestFailSoldToCode(String requestFailSoldToCode) {
		this.requestFailSoldToCode = requestFailSoldToCode;
		if (this.requestFailSoldToCode != null) {
			// According Season 's request. change the error message.
			String message = ResourceMB.getText("wq.label.rfqSubmit");
			// String[] soldTos = this.requestFailSoldToCode.split(";");
			// for (String soldTo : soldTos)
			// {
			// message += soldTo + ",";
			// }
			// message += ". Please contact Help Desk.";
			FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_WARN,
					ResourceMB.getText("wq.message.intErr") + " :", message);
			FacesContext.getCurrentInstance().addMessage("RfqSubmissionGrowl_1", msg);
		}
	}

	public Quote getQuote() {
		return quote;
	}

	public void setQuote(Quote quote) {
		this.quote = quote;
	}

	public boolean isQco() {
		return isQco;
	}

	public void setQco(boolean isQco) {
		this.isQco = isQco;
	}

	public CatalogSearchMB getCatalogSearchMB() {
		return catalogSearchMB;
	}

	public void setCatalogSearchMB(CatalogSearchMB catalogSearchMB) {
		this.catalogSearchMB = catalogSearchMB;
	}

	public boolean isQcp() {
		return isQcp;
	}

	public void setQcp(boolean isQcp) {
		this.isQcp = isQcp;
	}

	

	public boolean isShowDLink() {
		return isShowDLink;
	}

	public void setShowDLink(boolean isShowDLink) {
		this.isShowDLink = isShowDLink;
	}

	public SelectItem[] getMfrSearchSelectList() {
		return mfrSearchSelectList;
	}

	public void setMfrSearchSelectList(SelectItem[] mfrSearchSelectList) {
		this.mfrSearchSelectList = mfrSearchSelectList;
	}

	public String getNewProspectiveCustomerError() {
		return newProspectiveCustomerError;
	}

	public void setNewProspectiveCustomerError(String newProspectiveCustomerError) {
		this.newProspectiveCustomerError = newProspectiveCustomerError;
	}

	public String getRequestcontainsDraft() {
		return requestcontainsDraft;
	}

	public void setRequestcontainsDraft(String requestcontainsDraft) {
		this.requestcontainsDraft = requestcontainsDraft;
		if (this.requestcontainsDraft.equalsIgnoreCase("Y")) {
			FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "",
					ResourceMB.getText("wq.message.rfqStageDraft") + ".");
			FacesContext.getCurrentInstance().addMessage("RfqSubmissionGrowlResult_1", msg);
		}
	}

	public List<RfqItemVO> getPendingBmtRfqs() {
		return pendingBmtRfqs;
	}

	public void setPendingBmtRfqs(List<RfqItemVO> pendingBmtRfqs) {
		this.pendingBmtRfqs = pendingBmtRfqs;
	}

	// Created By Tonmy 27 Aug 2013
	public void searchInMaterialPopup() {

		List<PricerInfo> returnList = new ArrayList<PricerInfo>();

		String maxResultStr = sysConfigSB.getProperyValue("MAX_SEARCH_RESULT");
		int maxResult = Integer.valueOf(maxResultStr);

		// Added by Tonmy at 16 Sep 2013 . for issue 991
		if (validationSearchFullMfrPartNumber != null)
			validationSearchFullMfrPartNumber = validationSearchFullMfrPartNumber.trim();

		if (validationSearchMfr != null)
			validationSearchMfr = validationSearchMfr.trim();
		//TODO DAVID this is right.
		PricerInfo pricerInfo = new PricerInfo();
		pricerInfo.setExRateDate(new Date());
		pricerInfo.setMfr(validationSearchMfr);
		pricerInfo.setFullMfrPartNumber(validationSearchFullMfrPartNumber);
		pricerInfo.setBizUnit(bizUnit.getName());
		pricerInfo.setSalesId(userSB.findByEmployeeIdLazily(rfqHeader.getSalesEmployeeNumber()).getId());
		pricerInfo.setRfqCurr(rfqHeader.getRfqCurr());
		pricerInfo.setExRateDate(new Date());
		returnList = materialSB.searchForMaterialPopup(pricerInfo);
		
/*		List<CostIndicatorSearchCriteriaItem> ciscItemList = new ArrayList<CostIndicatorSearchCriteriaItem>();
		CostIndicatorSearchCriteriaItem ciscItem = new CostIndicatorSearchCriteriaItem();
		ciscItem.setFullMfrPart(validationSearchFullMfrPartNumber);
		ciscItem.setMfr(validationSearchMfr);
		ciscItem.setExcatPart(false);
		ciscItemList.add(ciscItem);
		CostIndicatorSearchCriteria cisc = new CostIndicatorSearchCriteria();
		cisc.setAllowCustomers(allowCustomers);
		cisc.setBizUnit(bizUnit);
		cisc.setItems(ciscItemList);
		cisc.setUser(user);
		cisc.setMaxResult(maxResult);
		cisc.setRequiredEmptyDetailMaterial(true);
		cisc.setRequiredContractPrice(true);
		returnList = partSearchLogicMB.newSearchParts(cisc);*/
		
		
/*		returnList =partSearchLogicMB.newSearchParts(validationSearchMfr, validationSearchFullMfrPartNumber, bizUnit.getName(), false, allowCustomers, 0, QuoteConstant.DEFAULT_AUTOCOMPLETE_FIRST_RESULT,
				Integer.parseInt(sysConfigSB
						.getProperyValue("MAX_SEARCH_RESULT")));
*/
		if (returnList != null && returnList.size() > 0) {
			for (int i = 0; i < returnList.size(); i++) {
				PricerInfo sv = returnList.get(i);
				sv.setItemNumber(i + 1);
				returnList.set(i, sv);
			}
		}
		searchParts = returnList;

		partModel = new PartModel(searchParts);
		if (searchParts != null) {
			// logger.info("searchParts result is not null , and size
			// ="+searchParts.size());
			searchPartsCount = searchParts.size();
		}
		// logger.info("searchParts end");
	}
/*
	public PartSearchLogicMB getPartSearchLogicMB() {
		return partSearchLogicMB;
	}

	public void setPartSearchLogicMB(PartSearchLogicMB partSearchLogicMB) {
		this.partSearchLogicMB = partSearchLogicMB;
	}*/

	public int getPendingDRMSCount() {
		return pendingDRMSCount;
	}

	public void setPendingDRMSCount(int pendingDRMSCount) {
		this.pendingDRMSCount = pendingDRMSCount;
	}

	public List<RfqItemVO> getPendingDRMSRfqs() {
		return pendingDRMSRfqs;
	}

	public void setPendingDRMSRfqs(List<RfqItemVO> pendingDRMSRfqs) {
		this.pendingDRMSRfqs = pendingDRMSRfqs;
	}

	public void resetForm() {
		resetMB();
		resetRfqSubmission();
		requestQuoteId = null;
		requestQuoteItemId = null;
		requestAction = null;

		// FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "RFQ
		// submission form reset <br />successfully.","");
		// FacesContext.getCurrentInstance().addMessage(
		// "RfqSubmissionGrowl", msg);

	}

	public int getRemoveAttIndex() {
		return removeAttIndex;
	}

	public void setRemoveAttIndex(int removeAttIndex) {
		this.removeAttIndex = removeAttIndex;
	}

	public void updateRemoveAttachmentIndex(int index) {
		this.removeAttIndex = index;
	}

	public void logInfo(String str) {
		if (user != null) {
			LOGGER.info("[" + user.getEmployeeId() + "] " + str);
		} else {
			LOGGER.info(str);
		}
	}

	private RfqItemVO loadContractPriceInfo(RfqItemVO rfqItem, ContractPricer cp) {

		if (cp != null) {
			rfqItem.setCost(cp.getCost());
			rfqItem.setCostIndicator(cp.getCostIndicator().getName());
			rfqItem.setMinSellPrice(cp.getMinSellPrice());
			rfqItem.setShipmentValidity(cp.getShipmentValidity());
			rfqItem.setPriceValidity(cp.getPriceValidity());
			// For defect 118
			if (cp.getMpq() != null)
				rfqItem.setMpq(cp.getMpq());
			if (cp.getMoq() != null)
				rfqItem.setMoq(cp.getMoq());
			if (cp.getTermsAndConditions() != null && !cp.getTermsAndConditions().isEmpty()) {
				rfqItem.setTermsAndCondition(cp.getTermsAndConditions());
			}
			// For defect 384
			if (cp.getAvnetQcComments() != null && !cp.getAvnetQcComments().isEmpty()) {
				rfqItem.setQcComment(cp.getAvnetQcComments());
			}
		}

		return rfqItem;

	}

	/*private RfqItemVO loadCostIndicatorInfo(RfqItemVO rfqItem, NormalPricer materialDetail,
			Material lowestCostMaterial) {

		if (materialDetail != null) {
			Manufacturer mfr = null;

			rfqItem.setMpq(materialDetail.getMpq());
			rfqItem.setMoq(materialDetail.getMoq());
			rfqItem.setMov(materialDetail.getMov());
			rfqItem.setLeadTime(materialDetail.getLeadTime());
			rfqItem.setCost(materialDetail.getCost());
			rfqItem.setNormSell(materialDetail.getNormalSell());

			// contract price does not need normal pricer's minSell
			// if(lowestCostMaterial.getContractPrice() == null){
			// rfqItem.setMinSell(materialDetail.getMinSell());
			// }
			// Contract Pricer mapping enhancement
			if (!QuoteUtil.hasContractPrice(rfqItem, lowestCostMaterial)) {
				rfqItem.setMinSell(materialDetail.getMinSell());
			}

			rfqItem.setShipmentValidity(materialDetail.getShipmentValidity());
			rfqItem.setPriceValidity(materialDetail.getPriceValidity());

			rfqItem.setPriceListRemarks1(materialDetail.getPriceListRemarks1());
			rfqItem.setPriceListRemarks2(materialDetail.getPriceListRemarks2());
			rfqItem.setPriceListRemarks3(materialDetail.getPriceListRemarks3());
			rfqItem.setPriceListRemarks4(materialDetail.getPriceListRemarks4());

			rfqItem.setDescription(materialDetail.getDescription());

			if (materialDetail.getCostIndicator() != null)
				rfqItem.setCostIndicator(materialDetail.getCostIndicator().getName());
			rfqItem.setQtyIndicator(materialDetail.getQtyIndicator());

			// below code are unused any more .confirmed with Timothy on 16 Oct
			// 2013
			if (rfqItem.getMfr() != null) {
				mfr = manufacturerSB.findManufacturerByName(rfqItem.getMfr(), bizUnit);
			}

			rfqItem.setMultiUsage(true);
			rfqItem.setQtyIndicator(materialDetail.getQtyIndicator());
			rfqItem.setCancellationWindow(materialDetail.getCancellationWindow());
			rfqItem.setReschedulingWindow(materialDetail.getRescheduledWindow());
			rfqItem.setTermsAndCondition(materialDetail.getTermsAndConditions());
			// WebQuote 2.2 enhancement : new field : quotation effective date
			// Change to use quotation display date.
			rfqItem.setQuotaionEffectiveDate(materialDetail.getDisplayQuoteEffDate());
			ManufacturerDetail md = null;
			if (mfr != null && lowestCostMaterial.getProductGroup2() != null
					&& lowestCostMaterial.getProductCategory() != null && bizUnit != null) {
				md = MfrDetailCacheManager.getMfrDetail(mfr.getId(), lowestCostMaterial.getProductGroup2().getId(),
						lowestCostMaterial.getProductCategory().getId(), bizUnit.getName());
				if (md != null) {
					String log = "MFR Detail is found and inserted with ";
					rfqItem.setMultiUsage(md.getMultiUsage());
					log += ", " + md.getResaleIndicator();
					rfqItem.setResaleIndicator(md.getResaleIndicator());

					if (materialDetail.getQtyIndicator() == null) {
						log += ", " + md.getQuantityIndicator();
						rfqItem.setQtyIndicator(md.getQuantityIndicator());
					}
					if (materialDetail.getCancellationWindow() == null)
						rfqItem.setCancellationWindow(md.getCancellationWindow());
					if (materialDetail.getRescheduledWindow() == null)
						rfqItem.setReschedulingWindow(md.getReschedulingWindow());

					logInfo(log);
				}
			}

		}

		return rfqItem;

	}*/

	/*private RfqItemVO loadProgramCostIndicatorInfo(RfqItemVO rfqItem, ProgramPricer programMD,
			Material lowestCostMaterial) {

		if (programMD != null) {
			Manufacturer mfr = null;

			rfqItem.setMpq(programMD.getMpq());
			rfqItem.setMoq(programMD.getMoq());
			rfqItem.setMov(programMD.getMov());
			rfqItem.setLeadTime(programMD.getLeadTime());
			// rfqItem.setCost(programMD.getProgramMinimumCost());
			// for defect 378
			rfqItem.setCost(programMD.getCost());

			// Fixed issue 1404
			if (programMD.getQuantityBreakPrices() != null && rfqItem.getRequiredQty() != null
					&& !rfqItem.getRequiredQty().isEmpty()) {
				try {
					QuoteItem tempQi = new QuoteItem();
					tempQi.setRequestedMaterialForProgram(lowestCostMaterial);
					tempQi.setRequestedQty(Integer.valueOf(rfqItem.getRequiredQty()));
					tempQi.setMpq(programMD.getMpq());
					// PROGRM PRICER ENHANCEMENT
					tempQi.setRequestedProgramMaterialForProgram(programMD);
					Double tempPrice = ProgRfqSubmitHelper.getPrice(tempQi);
					if (tempPrice != null && tempPrice != 0d && tempPrice != -999999d) {
						rfqItem.setMinSellPrice(tempPrice);
					}
				} catch (Exception e) {
					LOGGER.log(Level.SEVERE, "Error occured for MFR: " + rfqItem.getMfr() + ", Reason for failure: "
							+ MessageFormatorUtil.getParameterizedStringFromException(e), e);
					rfqItem.setMinSellPrice(null);
				}
			} else {
				rfqItem.setMinSellPrice(programMD.getNormalSell());
			}

			//rfqItem.setMinSell(programMD.getMinSell());
			rfqItem.setShipmentValidity(programMD.getShipmentValidity());
			rfqItem.setPriceValidity(programMD.getPriceValidity());

			rfqItem.setPriceListRemarks1(programMD.getPriceListRemarks1());
			rfqItem.setPriceListRemarks2(programMD.getPriceListRemarks2());
			rfqItem.setPriceListRemarks3(programMD.getPriceListRemarks3());
			rfqItem.setPriceListRemarks4(programMD.getPriceListRemarks4());

			rfqItem.setDescription(programMD.getDescription());

			if (programMD.getCostIndicator() != null)
				rfqItem.setCostIndicator(programMD.getCostIndicator().getName());
			rfqItem.setQtyIndicator(programMD.getQtyIndicator());

			// below code are unused any more .confirmed with Timothy on 16 Oct
			// 2013
			if (rfqItem.getMfr() != null) {
				mfr = manufacturerSB.findManufacturerByName(rfqItem.getMfr(), bizUnit);
			}

			rfqItem.setMultiUsage(true);
			rfqItem.setQtyIndicator(programMD.getQtyIndicator());
			rfqItem.setCancellationWindow(programMD.getCancellationWindow());
			rfqItem.setReschedulingWindow(programMD.getRescheduledWindow());
			rfqItem.setTermsAndCondition(programMD.getTermsAndConditions());

			// fixed issue 378
			if (programMD.getAvnetQcComments() != null && !programMD.getAvnetQcComments().isEmpty()) {
				rfqItem.setQcComment(programMD.getAvnetQcComments());
			}

			ManufacturerDetail md = null;
			if (mfr != null && lowestCostMaterial.getProductGroup2() != null
					&& lowestCostMaterial.getProductCategory() != null && bizUnit != null) {
				md = MfrDetailCacheManager.getMfrDetail(mfr.getId(), lowestCostMaterial.getProductGroup2().getId(),
						lowestCostMaterial.getProductCategory().getId(), bizUnit.getName());
				if (md != null) {
					String log = "MFR Detail is found and inserted with ";
					rfqItem.setMultiUsage(md.getMultiUsage());
					log += ", " + md.getResaleIndicator();
					rfqItem.setResaleIndicator(md.getResaleIndicator());

					if (programMD.getQtyIndicator() == null) {
						log += ", " + md.getQuantityIndicator();
						rfqItem.setQtyIndicator(md.getQuantityIndicator());
					}
					if (programMD.getCancellationWindow() == null)
						rfqItem.setCancellationWindow(md.getCancellationWindow());
					if (programMD.getRescheduledWindow() == null)
						rfqItem.setReschedulingWindow(md.getReschedulingWindow());

					// logInfo(log);
				}
			}
		}

		return rfqItem;

	}*/

	//private QuoteItem aqLogic(QuoteItem item, RfqItemVO rfqItem) {
	/*
		// logger.info("==aqLogic");
		// NormalPricer abookCostMD =
		// rfqItem.getMaterial().getAbookMaterialDetail();

		// MaterialDetail properMD =
		// rfqItem.getMaterial().getProperMaterialDetail();
		
		 * NormalPricer properMD = null;
		 * if(rfqItem.getMaterial().getProperMaterialDetailList() != null &&
		 * !rfqItem.getMaterial().getProperMaterialDetailList().isEmpty()){
		 * properMD =
		 * rfqItem.getMaterial().getProperMaterialDetailList().get(0); }
		 * 
		 * NormalPricer programMB = rfqItem.getMaterial().getPmaterialDetail();
		 * //ContractPrice cp = rfqItem.getMaterial().getContractPrice();
		 * //Contract Pricer mapping enhancement ContractPricer cp =
		 * QuoteUtil.getContractPrice(rfqItem, rfqItem.getMaterial());
		 * 
		 * if(QuoteSBConstant.RFQ_COST_INDICATOR_TYPE_C.equalsIgnoreCase(rfqItem
		 * .getCostIndicatorType())) {
		 * 
		 * //logger.info("==aqLogic is contract price"); //i. LT, MPQ, T&C
		 * should follow A-Book Cost if(cp.getTermsAndConditions()!=null &&
		 * !cp.getTermsAndConditions().isEmpty()) {
		 * item.setTermsAndConditions(cp.getTermsAndConditions()); }
		 * 
		 * if(abookCostMD!=null) { if(item.getTermsAndConditions()==null ||
		 * item.getTermsAndConditions().isEmpty()) {
		 * item.setTermsAndConditions(abookCostMD.getTermsAndConditions()); }
		 * item.setLeadTime(abookCostMD.getLeadTime());
		 * item.setMpq(abookCostMD.getMpq()); }
		 * 
		 * // common code moved to utitlity class
		 * 
		 * item.setTermsAndConditions(cp.getTermsAndConditions());
		 * item.setLeadTime(rfqItem.getLeadTime());
		 * item.setMpq(rfqItem.getMpq());
		 * 
		 * item = commonAcLogic(item, rfqItem);
		 * 
		 * } else
		 * if(QuoteSBConstant.RFQ_COST_INDICATOR_TYPE_N.equalsIgnoreCase(rfqItem
		 * .getCostIndicatorType())) {
		 * //logger.info("==aqLogic is normal cost");
		 * 
		 * // common code moved to utitlity class item = commonAcLogic(item,
		 * rfqItem);
		 * 
		 * 
		 * }
		 
		if (QuoteSBConstant.RFQ_COST_INDICATOR_TYPE_P.equalsIgnoreCase(rfqItem.getCostIndicatorType())) {
			// logger.info("==aqLogic is program cost");
			// logger.info("==aqLogic
			// properMD.getQtyIndicator():"+properMD.getQtyIndicator());
			// it is program

			try {
				item = ProgRfqSubmitHelper.processAutoQuoteItemForNormalSubmissionForRfqPage(item, rfqItem);
			} catch (ParseException e) {
				LOGGER.log(Level.SEVERE, "Error occured for Item MPQ:" + item.getMpq() + ", Reason for failure: "
						+ MessageFormatorUtil.getParameterizedStringFromException(e), e);
			}
			if (rfqItem.getCostIndicator() != null) {
				item.setQtyIndicator(rfqItem.getQtyIndicator());
			}
		} else {

			item.setTermsAndConditions(rfqItem.getTermsAndCondition());
			item.setLeadTime(rfqItem.getLeadTime());
			item.setMpq(rfqItem.getMpq());

			item = commonAcLogic(item, rfqItem);
		}

		return item;
	*/
		//}

	public String getEndCustomerSearchType() {
		return endCustomerSearchType;
	}

	public void setEndCustomerSearchType(String endCustomerSearchType) {
		this.endCustomerSearchType = endCustomerSearchType;
	}

	public String getEndCustomerSearchRegion() {
		return endCustomerSearchRegion;
	}

	public void setEndCustomerSearchRegion(String endCustomerSearchRegion) {
		this.endCustomerSearchRegion = endCustomerSearchRegion;
	}

	public SelectItem[] getEndCustomerSearchRegionList() {
		return endCustomerSearchRegionList;
	}

	public void setEndCustomerSearchRegionList(SelectItem[] endCustomerSearchRegionList) {
		this.endCustomerSearchRegionList = endCustomerSearchRegionList;
	}

	public SelectItem[] getQuoteTypeList() {
		return quoteTypeList;
	}

	public void setQuoteTypeList(SelectItem[] quoteTypeList) {
		this.quoteTypeList = quoteTypeList;
	}

	public SelectItem[] getDesignInCatList() {
		return designInCatList;
	}

	public void setDesignInCatList(SelectItem[] designInCatList) {
		this.designInCatList = designInCatList;
	}

	private void getRestrictedCustomerList() {
		List<RestrictedCustomerMappingSearchCriteria> restrictedCustomerCriteriaList = new ArrayList<RestrictedCustomerMappingSearchCriteria>();
		if (rfqItems != null && rfqItems.size() > 0) {
			for (RfqItemVO rfqItem : rfqItems) {
				RestrictedCustomerMappingSearchCriteria rcc = new RestrictedCustomerMappingSearchCriteria();
				rcc.setBizUnit(rfqHeader.getBizUnit());
				rcc.setMfrKeyword(rfqItem.getMfr());
				rcc.setSoldToCodeKeyword(rfqItem.getSoldToCustomerNumber());
				restrictedCustomerCriteriaList.add(rcc);
			}
			//restrictedCustList = restrictedCustomerSB.findRestrictedCust(restrictedCustomerCriteriaList);
		} else {
			//restrictedCustList = new ArrayList<RestrictedCustomerMapping>();
		}
	}

/*	private boolean hasMatchedRetrictedCustomer(RfqItemVO rfqItem) {
		// logger.info("hasMatchedRetrictedCustomer cost
		// indicator:"+rfqItem.getCostIndicator());
		boolean returnB = false;
		if (restrictedCustList != null) {
			String val = getMandatoryKey(rfqItem);
			returnB = checkRetrictedCustomer(val, rfqItem);
		}
		return returnB;
	}
*/
/*	private boolean hasMatchedRetrictedCustomer(String costIndicator, RfqItemVO rfqItem) {

		String val = getMandatoryKey(rfqItem.getMfr(), costIndicator, rfqItem.getSoldToCustomerNumber());

		return checkRetrictedCustomer(val, rfqItem);
	}*/

	public String getMandatoryKey(RfqItemVO rfqItem) {
		StringBuffer sb = new StringBuffer();
		sb.append(rfqItem.getMfr()).append("|").append(rfqItem.getCostIndicator()).append("|")
				.append(rfqItem.getSoldToCustomerNumber());
		return sb.toString();
	}

	private RfqItemVO getRfqItemVO(QuoteItem quoteItem) {
		String key = "";
		if (quoteItem.getSoldToCustomer() != null) {
			key += quoteItem.getSoldToCustomer().getCustomerNumber();
		}
		key += "|" + quoteItem.getRequestedMfr().getName() + "|" + quoteItem.getRequestedPartNumber();
		if (rfqItems != null && !rfqItems.isEmpty()) {
			for (RfqItemVO rfqItem : rfqItems) {
				if (rfqItem.getSoldToCustomerNumber() != null) {
					if (key.equalsIgnoreCase(rfqItem.getSoldToCustomerNumber() + "|" + rfqItem.getMfr() + "|"
							+ rfqItem.getRequiredPartNumber())) {
						return rfqItem;
					}
				}
			}
		}

		return null;
	}

	/*private Object nextCostIndicator(RfqItemVO rfqItem, List<String> failedCostIndicator) {
		// logger.info("nextCostIndicator ");
		Object returnMD = null;
		Material lowestCostMaterial = rfqItem.getMaterial();

		if (lowestCostMaterial == null) {
			return null;
		}

		ContractPricer cp = QuoteUtil.getContractPrice(rfqItem, rfqItem.getMaterial());
		if (cp == null || failedCostIndicator.contains(cp.getCostIndicator().getName()))
			cp = null;

		return ejbCommonSB.commonNextCostIndicator(cp, lowestCostMaterial, failedCostIndicator);

		
		 * 
		 * 
		 * // MaterialDetail abookCostMD =
		 * lowestCostMaterial.getAbookMaterialDetail(); // if(abookCostMD==null
		 * ||
		 * failedCostIndicator.contains(abookCostMD.getCostIndicator().getName()
		 * )) // abookCostMD = null; MaterialDetail programMD =
		 * lowestCostMaterial.getPmaterialDetail(); if(programMD==null ||
		 * failedCostIndicator.contains(programMD.getCostIndicator().getName()))
		 * programMD = null; MaterialDetail expiredABookMD =
		 * lowestCostMaterial.getExpiredAbookMeterialDetail();
		 * if(expiredABookMD==null ||
		 * failedCostIndicator.contains(expiredABookMD.getCostIndicator().
		 * getName())) expiredABookMD = null;
		 * 
		 * 
		 * MaterialDetail properMD =
		 * lowestCostMaterial.getProperMaterialDetail(); if(properMD!=null &&
		 * failedCostIndicator.contains(properMD.getCostIndicator().getName()))
		 * properMD = null;
		 * 
		 * if(properMD==null) { List<MaterialDetail> properMDList =
		 * lowestCostMaterial.getProperMaterialDetailList();
		 * if(properMDList!=null && properMDList.size()>0) for(MaterialDetail md
		 * : properMDList) {
		 * if(!failedCostIndicator.contains(md.getCostIndicator().getName())) {
		 * properMD = md; break; } }
		 * 
		 * //If all the proper material detail is not found and the expired a
		 * book exist , set the expired a book as default if(properMD==null &&
		 * expiredABookMD!=null) properMD= expiredABookMD; }
		 * 
		 * if(cp!=null) { //logger.info("==cp is not null"); //Contract price
		 * overridden flag is No if(!cp.getOverrideFlag()) { returnMD = cp; }
		 * else { //logger.info("==cp override flag is YES"); //Contract price
		 * overridden flag is Yes // if the contract price is lower than the
		 * properMD . pick the contract price. //When Same Cost from different
		 * types, the choosing sequence should be Contract first, then Program
		 * last Normal. if(properMD!=null) {
		 * if(cp.getCost().doubleValue()<=properMD.getCost().doubleValue()) {
		 * returnMD = cp; } else { //logger.info("==cp cost is not lowest");
		 * 
		 * if(programMD!=null &&
		 * programMD.getCost()<=properMD.getCost().doubleValue()) { returnMD =
		 * programMD; } else { returnMD = properMD; } } } else { returnMD = cp;
		 * }
		 * 
		 * 
		 * } } else { //logger.info("==cp is null"); if(properMD!=null) {
		 * //logger.info("==find the lowest cost");
		 * 
		 * if(programMD!=null &&
		 * programMD.getCost()<=properMD.getCost().doubleValue()) { returnMD =
		 * programMD; } else { returnMD = properMD; } } else { returnMD =
		 * properMD; } }
		 * 
		 * } // if(returnMD==null) //
		 * logger.info("nextCostIndicator return null "); // else //
		 * logger.info("nextCostIndicator have next cost indicator: "); return
		 * returnMD;
		 
	}
*/
	protected QuoteItem copyBasicDataToQuoteItem(QuoteItem quoteItem, RfqItemVO rfqItem) {
		quoteItem.setValidFlag(true);
		quoteItem.setAllocationFlag(QuoteSBConstant.DEFAULT_ALLOCATION_FLAG);
		quoteItem.setRevertVersion(QuoteSBConstant.DEFAULT_REVERT_VERISON);
		quoteItem.setDesignInCat(rfqItem.getDesignInCat());
		quoteItem.setSentOutTime(QuoteUtil.getCurrentTime());
		quoteItem.setApplication(rfqItem.getApplication());
		quoteItem.setCompetitorInformation(rfqItem.getCompetitorInformation());
		quoteItem.setQuoteType(rfqItem.getQuoteType());
		quoteItem.setLoaFlag(rfqItem.isLoa());
		if (rfqItem.getEau() != null) {
			quoteItem.setEau(Integer.valueOf(rfqItem.getEau()));
		}
		quoteItem.setEnquirySegment(rfqItem.getEnquirySegment());
		quoteItem.setRemarks(rfqItem.getItemRemarks());
		quoteItem.setLastUpdatedBy(user.getEmployeeId());
		quoteItem.setLastUpdatedName(user.getName());
		quoteItem.setLastUpdatedOn(QuoteUtil.getCurrentTime());
		quoteItem.setMpSchedule(rfqItem.getMpSchedule());
		quoteItem.setPpSchedule(rfqItem.getPpSchedule());
		// the change for DRMS margin retention project
		quoteItem.setDrNedaId(rfqItem.getDrNedaId());
		quoteItem.setDrNedaLineNumber(rfqItem.getDrNedaLineNumber());
		quoteItem.setDesignLocation(rfqItem.getDesignLocation());
		quoteItem.setDesignRegion(rfqItem.getDesignRegion());
		quoteItem.setDrmsNumber(rfqItem.getDrmsNumber());
		return quoteItem;
	}

	protected QuoteItem copyCostIndicatorRelatedData1ToQuoteItem(QuoteItem quoteItem, RfqItemVO rfqItem) {
		quoteItem.setCostIndicator(rfqItem.getCostIndicator());
		quoteItem.setPriceListRemarks1(rfqItem.getPriceListRemarks1());
		quoteItem.setPriceListRemarks2(rfqItem.getPriceListRemarks2());
		quoteItem.setPriceListRemarks3(rfqItem.getPriceListRemarks3());
		quoteItem.setPriceListRemarks4(rfqItem.getPriceListRemarks4());
		quoteItem.setCancellationWindow(rfqItem.getCancellationWindow());
		quoteItem.setRescheduleWindow(rfqItem.getReschedulingWindow());
		quoteItem.setMultiUsageFlag(rfqItem.getMultiUsage());
		quoteItem.setLeadTime(rfqItem.getLeadTime());
		quoteItem.setTermsAndConditions(rfqItem.getTermsAndCondition());
		quoteItem.setAqcc(rfqItem.getQcComment());
		quoteItem.setDescription(rfqItem.getDescription());
		quoteItem.setMoq(rfqItem.getMoq());
		quoteItem.setMov(rfqItem.getMov());
		quoteItem.setMpq(rfqItem.getMpq());
		if (rfqItem.getCost() != null) {
			quoteItem.setCost(rfqItem.getCost());
		}
		if (rfqItem.getMinSellPrice() != null) {
			quoteItem.setMinSellPrice(rfqItem.getMinSellPrice());
		}
		quoteItem.setBottomPrice(rfqItem.getBottomPrice());
		if (rfqItem.getQtyIndicator() != null) {
			quoteItem.setQtyIndicator(rfqItem.getQtyIndicator());
		} else {
			quoteItem.setQtyIndicator(QuoteConstant.QTY_INDICATOR_MOQ);
		}
		// WebQuote 2.2 enhancement : new field : quotation effective date
		quoteItem.setQuotationEffectiveDate(rfqItem.getQuotaionEffectiveDate());
		if (rfqItem.getResaleIndicator() == null) {
			quoteItem.setResaleIndicator(QuoteSBConstant.DEFAULT_RESALE_INDICATOR);
			quoteItem.setResaleMin(QuoteSBConstant.DEFAULT_RESALE_INDICATOR_MIN);
			quoteItem.setResaleMax(QuoteSBConstant.DEFAULT_RESALE_INDICATOR_MAX);
		} else {
			quoteItem.setResaleIndicator(rfqItem.getResaleIndicator());
			String minResale = rfqItem.getResaleIndicator().substring(0, 2);
			String maxResale = rfqItem.getResaleIndicator().substring(2, 4);
			// fix INC0276031,
			// if (quoteItem.getQuotedPrice() != null)
			// {
			quoteItem.setResaleMin(Double.parseDouble(minResale));
			if (maxResale.matches("[0-9]{2}"))
				quoteItem.setResaleMax(Double.parseDouble(maxResale));
			else
				quoteItem.setResaleMax(QuoteSBConstant.DEFAULT_RESALE_INDICATOR_MAX);
			// }
		}
		// Material restructure and quote_item delinkage. changed on 10 Apr
		// 2014.
		// if(rfqItem.getCostIndicatorType() != null &&
		// rfqItem.getCostIndicatorType().equals(QuoteSBConstant.RFQ_COST_INDICATOR_TYPE_P))
		if (rfqItem.getCostIndicatorType() != null && rfqItem.getCostIndicatorType().equals(QuoteSBConstant.MATERIAL_TYPE_PROGRAM)) {
			quoteItem.setMaterialTypeId(QuoteSBConstant.MATERIAL_TYPE_PROGRAM);
		} else if (rfqItem.getCostIndicatorType() != null && rfqItem.getCostIndicatorType().equals(QuoteSBConstant.MATERIAL_TYPE_NORMAL)){
			quoteItem.setMaterialTypeId(QuoteSBConstant.MATERIAL_TYPE_NORMAL);
		}
		quoteItem.setRequestedProgramMaterialForProgram(rfqItem.getRequestedProgramMaterialForProgram());
		
		quoteItem.setTargetMargin(rfqItem.calTargetMargin());
		quoteItem.setResaleMargin(rfqItem.calResalesMargin());
		return quoteItem;
	}

	protected QuoteItem copyCostIndicatorRelatedData2ToQuoteItem(QuoteItem quoteItem, RfqItemVO rfqItem) {
		quoteItem.setShipmentValidity(rfqItem.getShipmentValidity());
		quoteItem.setPoExpiryDate(rfqItem.getShipmentValidity());
		quoteItem.setPriceValidity(rfqItem.getPriceValidity());
		return quoteItem;
	}

	protected QuoteItem copyCostIndicatorRelatedData3ToQuoteItem(QuoteItem quoteItem, RfqItemVO rfqItem) {
		if (rfqItem.getPriceValidity() != null) {
			Date validity = null;
			if (quoteItem.getPriceValidity().matches("[0-9]{1,}")) {
				int shift = Integer.parseInt(quoteItem.getPriceValidity());
				validity = QuoteUtil.shiftDate(quoteItem.getSentOutTime(), shift);
			} else {
				SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
				// fix incident INC0018368 june 20140529
				sdf.setTimeZone(OSTimeZone.getOsTimeZone());
				try {
					validity = sdf.parse(quoteItem.getPriceValidity());
				} catch (ParseException e) {
					LOGGER.log(Level.SEVERE, "Error occured for Quote item id: " + quoteItem.getQuote().getId()
							+ ", Reason for failure: " + e.getMessage(), e);
				}
			}
			if (validity != null) {
				quoteItem.setPoExpiryDate(validity);
				if (rfqItem.getCostIndicatorType() != null
						&& rfqItem.getCostIndicatorType().equalsIgnoreCase(QuoteSBConstant.MATERIAL_TYPE_NORMAL)) {
					Date quoteExpiryDate = QuoteUtil.calculateQuoteExpiryDate(validity,
							QuoteSBConstant.MATERIAL_TYPE_NORMAL);
					quoteItem.setQuoteExpiryDate(quoteExpiryDate);
				} else if (rfqItem.getCostIndicatorType() != null
						&& rfqItem.getCostIndicatorType().equalsIgnoreCase(QuoteSBConstant.MATERIAL_TYPE_PROGRAM)) {
					Date quoteExpiryDate = QuoteUtil.calculateQuoteExpiryDate(validity,
							QuoteSBConstant.MATERIAL_TYPE_PROGRAM);
					quoteItem.setQuoteExpiryDate(quoteExpiryDate);
				} else {
					Date quoteExpiryDate = QuoteUtil.calculateQuoteExpiryDate(validity,
							QuoteSBConstant.MATERIAL_TYPE_NORMAL);
					quoteItem.setQuoteExpiryDate(quoteExpiryDate);
				}
			}
		}
		return quoteItem;
	}

	protected QuoteItem copyCustomerDataToQuoteItem(QuoteItem quoteItem, RfqItemVO rfqItem) {
		if (rfqItem.getEndCustomerNumber() != null && !rfqItem.getEndCustomerNumber().isEmpty()) {
			// logger.info("@@rfqItem.getEndCustomerNumber() "+
			// rfqItem.getEndCustomerNumber() );
			Customer customer = customerSB.findByPK(rfqItem.getEndCustomerNumber());
			if (customer != null) {
				quoteItem.setEndCustomer(customer);
				if (customer.getCustomerFullName() != null && !customer.getCustomerFullName().isEmpty()) {
					quoteItem.setEndCustomerName(customer.getCustomerFullName());
				}
			}
		} else if (rfqItem.getEndCustomerName() != null) {
			// logger.info("@@rfqItem.getEndCustomerName()
			// "+rfqItem.getEndCustomerName() );
			quoteItem.setEndCustomerName(rfqItem.getEndCustomerName());
		}
		if (rfqItem.getShipToCustomerNumber() != null && !rfqItem.getShipToCustomerNumber().isEmpty()) {
			Customer customer = customerSB.findByPK(rfqItem.getShipToCustomerNumber());
			// add name to ShipToCustomerName when shipToCustomerNumber is not
			// null. by damon@20160830
			if (customer != null) {
				quoteItem.setShipToCustomer(customer);
				if (customer.getCustomerFullName() != null && !customer.getCustomerFullName().isEmpty()) {
					quoteItem.setShipToCustomerName(customer.getCustomerFullName());
				}

			}
		} else if (rfqItem.getShipToCustomerName() != null) {
			quoteItem.setShipToCustomerName(rfqItem.getShipToCustomerName());
		}
		if (rfqItem.getSoldToCustomerNumber() != null && !rfqItem.getSoldToCustomerNumber().isEmpty()) {
			Customer customer = customerSB.findByPK(rfqItem.getSoldToCustomerNumber());
			if (customer != null && rfqItem.getCustomerType() != null) {
				customer.setCustomerType(rfqItem.getCustomerType());
				customerSB.updateCustomer(customer);
			}
			if (customer != null) {
				quote.setSoldToCustomer(customer);
				// Defect #49
				quote.setSoldToCustomerName(getCustomerFullName(customer));
				quote.setSoldToCustomerNameChinese(retrieveCustomerChineseName(customer)); // fix
																							// defect
																							// 67
																							// June
																							// 20140904
																							// set
																							// customer
																							// chinese
																							// name
																							// according
																							// to
																							// item's
																							// sold
																							// to
																							// code
																							// number
				quote.setCustomerType(rfqItem.getCustomerType());

				quoteItem.setSoldToCustomer(customer);
				quoteItem.setCustomerType(rfqItem.getCustomerType());
			}
		}
		return quoteItem;
	}

/*	public void fillDataToRfqItem(RfqItemVO rfqItem) {

		// logger.log(Level.INFO,
		// "PERFORMANCE START - autoFillDataToRfqItem()");
		// logger.info("==autoFillDataToRfqItem start");
		if (!QuoteUtil.isEmpty(rfqItem.getRequiredPartNumber())) {
			Material lowestCostMaterial = rfqItem.getMaterial();

			if (lowestCostMaterial != null) {
				// logger.info("==autoFillDataToRfqItem have
				// lowestCostMaterial");
				// ContractPrice cp = lowestCostMaterial.getContractPrice();
				// Contract Pricer mapping enhancement
				ContractPricer cp = QuoteUtil.getContractPrice(rfqItem, rfqItem.getMaterial());
				NormalPricer abookCostMD = lowestCostMaterial.getAbookMaterialDetail();
				// MaterialDetail properMD =
				// lowestCostMaterial.getProperMaterialDetail();
				NormalPricer properMD = null;
				if (lowestCostMaterial.getProperMaterialDetailList() != null
						&& !lowestCostMaterial.getProperMaterialDetailList().isEmpty()) {
					properMD = lowestCostMaterial.getProperMaterialDetailList().get(0);
				}
				NormalPricer programMD = lowestCostMaterial.getPmaterialDetail();
				NormalPricer expiredABookMD = lowestCostMaterial.getExpiredAbookMeterialDetail();
				List<NormalPricer> properMDList = lowestCostMaterial.getProperMaterialDetailList();
				// if(cp!=null)
				// {
				// logger.info("==autoFillDataToRfqItem cp is "+cp.getCost());
				// }
				// if(abookCostMD!=null)
				// {
				// logger.info("==autoFillDataToRfqItem abookCostMD is
				// "+abookCostMD.getCost());
				// }
				// if(properMD!=null)
				// {
				// logger.info("==autoFillDataToRfqItem properMD is
				// "+properMD.getCost());
				// }
				// if(programMD!=null)
				// {
				// logger.info("==autoFillDataToRfqItem programMD is
				// "+programMD.getCost());
				// }
				// Contract price is Non-expired
				if (cp != null) {
					// logger.info("==cp is not null");
					// Contract price overridden flag is No
					if (!cp.getOverrideFlag()) {
						// logger.info("==cp override flag is NO");
						// 1) Find Non-expired Contract pricing according to
						// salesman & sold to & EC when the ��Can be
						// overridden�� Flag is ��No��.
						if (abookCostMD != null) {
							rfqItem = loadCostIndicatorInfo(rfqItem, abookCostMD, lowestCostMaterial);
						}
						rfqItem = loadContractPriceInfo(rfqItem, cp);
						rfqItem.setCostIndicatorType(QuoteSBConstant.RFQ_COST_INDICATOR_TYPE_C);
					} else {
						// logger.info("==cp override flag is YES");
						// Contract price overridden flag is Yes
						// if the contract price is lower than the properMD .
						// pick the contract price.
						// When Same Cost from different types, the choosing
						// sequence should be Contract first, then Program last
						// Normal.
						if (properMD != null) {
							if (cp.getCost().doubleValue() <= properMD.getCost().doubleValue()) {
								// logger.info("==cp cost is lowest");
								if (abookCostMD != null) {
									rfqItem = loadCostIndicatorInfo(rfqItem, abookCostMD, lowestCostMaterial);
								}
								rfqItem = loadContractPriceInfo(rfqItem, cp);
								rfqItem.setCostIndicatorType(QuoteSBConstant.RFQ_COST_INDICATOR_TYPE_C);
							} else {
								// logger.info("==cp cost is not lowest");

								if (programMD != null && programMD.getCost() <= properMD.getCost().doubleValue()) {
									rfqItem = loadProgramCostIndicatorInfo(rfqItem, (ProgramPricer) programMD,
											lowestCostMaterial);
									rfqItem.setCostIndicatorType(QuoteSBConstant.RFQ_COST_INDICATOR_TYPE_P);
								} else {
									rfqItem = loadCostIndicatorInfo(rfqItem, properMD, lowestCostMaterial);
									rfqItem.setCostIndicatorType(QuoteSBConstant.RFQ_COST_INDICATOR_TYPE_N);
								}
							}
						} else {
							if (abookCostMD != null) {
								rfqItem = loadCostIndicatorInfo(rfqItem, abookCostMD, lowestCostMaterial);
							}
							rfqItem = loadContractPriceInfo(rfqItem, cp);
							rfqItem.setCostIndicatorType(QuoteSBConstant.RFQ_COST_INDICATOR_TYPE_C);
						}

					}
				} else {
					// logger.info("==cp is null");
					if (properMD != null) {
						// logger.info("==find the lowest cost");

						if (programMD != null && programMD.getCost() <= properMD.getCost().doubleValue()) {
							rfqItem = loadProgramCostIndicatorInfo(rfqItem, (ProgramPricer) programMD,
									lowestCostMaterial);
							rfqItem.setCostIndicatorType(QuoteSBConstant.RFQ_COST_INDICATOR_TYPE_P);
						} else {
							rfqItem = loadCostIndicatorInfo(rfqItem, properMD, lowestCostMaterial);
							rfqItem.setCostIndicatorType(QuoteSBConstant.RFQ_COST_INDICATOR_TYPE_N);
						}
					} else {
						rfqItem = loadCostIndicatorInfo(rfqItem, expiredABookMD, lowestCostMaterial);
						rfqItem.setCostIndicatorType(QuoteSBConstant.RFQ_COST_INDICATOR_TYPE_N);
					}
				}

			}
			copyCustomerFromHeaderToItem(rfqItem);

			// logger.log(Level.INFO,
			// "PERFORMANCE END - autoFillDataToRfqItem()");
		}
	}*/

	public SelectItem[] getExCurrencyList() {
		return exCurrencyList;
	}

	public void setExCurrencyList(SelectItem[] exCurrencyList) {
		this.exCurrencyList = exCurrencyList;
	}

	public String getNewCustomerTitle() {
		return newCustomerTitle;
	}

	public void setNewCustomerTitle(String newCustomerTitle) {
		this.newCustomerTitle = newCustomerTitle;
	}

	public StreamedContent getVerifiedTemplate() {
		if (workbook != null) {
			StreamedContent tempStreamedContent = null;
			java.io.InputStream inStream = null;

			try {
				String tempPath = sysConfigSB.getProperyValue(QuoteConstant.TEMP_DIR);
				String tempFileName = tempPath + DateUtils.getDefaultDateStrEmailTimeStamp() + "_temp.xls";
				LOGGER.info("The file get from:[" + tempFileName + "]");
				FileOutputStream fileOut = new FileOutputStream(tempFileName);
				workbook.write(fileOut);
				fileOut.close();
				inStream = new java.io.FileInputStream(tempFileName);
				tempStreamedContent = new DefaultStreamedContent(inStream, "application/vnd.ms-excel", uploadFileName);
				LOGGER.info("the file is wiritten successfully");
			} catch (Exception e) {
				LOGGER.log(Level.SEVERE, "Error occured for file: " + uploadFileName + ", Reason for failure: "
						+ MessageFormatorUtil.getParameterizedStringFromException(e), e);
			}
			return tempStreamedContent;
		} else {
			LOGGER.info("The file(workbook) is null");
			return null;
		}

	}

	public boolean isFoundErrorMsg() {
		return foundErrorMsg;
	}

	public void setFoundErrorMsg(boolean foundErrorMsg) {
		this.foundErrorMsg = foundErrorMsg;
	}

	public void checkVerifiedPopupWindow() {
		if (foundErrorMsg) {
			foundErrorMsg = false;
			RequestContext requestContext = RequestContext.getCurrentInstance();
			requestContext.execute("PF('uploadBatchRfqsValidation_dialog').show()");
		}
	}

	public String getUploadFileName() {
		return uploadFileName;
	}

	public void setUploadFileName(String uploadFileName) {
		this.uploadFileName = uploadFileName;
	}

	public Map<String, SelectItem[]> getSapPartNumberSelectedMap() {
		return sapPartNumberSelectedMap;
	}

	public void setSapPartNumberSelectedMap(Map<String, SelectItem[]> sapPartNumberSelectedMap) {
		this.sapPartNumberSelectedMap = sapPartNumberSelectedMap;
	}

	public SelectItem[] getSapPartNumberSelectList(String key) {

		return sapPartNumberSelectedMap.get(key) == null ? QuoteUtil.createFilterOptions(new String[] {})
				: sapPartNumberSelectedMap.get(key);

	}

	public void refreshSapPartNumber(int itemNumber) {
		this.selectedRfqItem = getRfqItemByItemNumber(itemNumber);
		this.selectedRfqItem.setRfqCurr(rfqHeader.getRfqCurr());
		logInfo("the select rfqItem info is:"+this.selectedRfqItem.toString());
/*		String mfrName = this.selectedRfqItem.getMfr();
		String requestedPartNumber = this.selectedRfqItem.getRequiredPartNumber();
		String keyStr = bizUnit.getName() + "==" + mfrName + "==" + requestedPartNumber;
		SelectItem[] sapPartNumberList = convertSapPartNumberToSelectItem(mfrName, requestedPartNumber);
		if (sapPartNumberList != null && sapPartNumberList.length > 0) {
			sapPartNumberSelectedMap.put(keyStr, sapPartNumberList);
		}
		this.selectedRfqItem.setKey4SapPartNumberSelectedMap(keyStr);*/
		List<RfqItemVO> rfqItemList =new ArrayList<RfqItemVO>();
		rfqItemList.add(this.selectedRfqItem);
		try {
			this.applyDefaultCostIndictorOrAQLogic(rfqHeader, rfqItemList,allowCustomers,"D");
		} catch (Exception e) {
			// TODO Auto-generated catch block

			LOGGER.log(Level.SEVERE,"[" + user.getEmployeeId() + " ]"+"Get error when refreshSapPartNumber," + e.getMessage(),e);
			FacesUtil.showWarnMessage(QuoteSBConstant.SYSTEM_ERROR, e.getMessage(),
					"RfqSubmissionGrowl_1");
			return;
		}
		rfqItems.set(itemNumber-1, this.selectedRfqItem);
	}

	private void quoteHeaderVOChecking() throws WebQuoteException {
		StringBuffer message = new StringBuffer();
		String resourceMessage = null;
		if (QuoteUtil.isEmpty(rfqHeader.getSalesEmployeeNumber())) {
			resourceMessage = ResourceMB.getText("wq.label.salesmanEmpCode");
			message.append(resourceMessage + ", ");
		}
		if (QuoteUtil.isEmpty(rfqHeader.getSalesEmployeeName())) {
			resourceMessage = ResourceMB.getText("wq.label.empName");
			message.append(resourceMessage + ", ");
		}

		if (QuoteUtil.isEmpty(rfqHeader.getSoldToCustomerNumber())) {
			resourceMessage = ResourceMB.getText("wq.label.SoldToCode");
			message.append(resourceMessage + ", ");
		}

		if (QuoteUtil.isEmpty(rfqHeader.getCustomerType())) {
			resourceMessage = ResourceMB.getText("wq.label.custmrType");
			message.append(resourceMessage + ", ");
		}
		if (QuoteUtil.isEmpty(rfqHeader.getEnquirySegment())) {
			resourceMessage = ResourceMB.getText("wq.label.enqrySegment");
			message.append(resourceMessage + ", ");
		}
		if (QuoteUtil.isEmpty(rfqHeader.getQuoteType())) {
			resourceMessage = ResourceMB.getText("wq.label.QuoteType");
			message.append(resourceMessage + ", ");
		}
		if (QuoteUtil.isEmpty(rfqHeader.getDesignInCat())) {
			resourceMessage = ResourceMB.getText("wq.error.designInCat");
			message.append(resourceMessage + ", ");
		}

		if (message.length() != 0) {
			FacesUtil.showWarnMessage(ResourceMB.getText("wq.message.mandFieldError") + " : ",
					message.append(ResourceMB.getText("wq.message.atRFQHeader") + ".").toString(),
					"RfqSubmissionGrowl_1");
			throw new WebQuoteException("", new Exception(message.toString()));
		} else {
			// check sold to code,end customer,ship to code match with sales man
			// default bizunit
			List<String> soldToAccountGroup = new ArrayList<String>();
			soldToAccountGroup.add(QuoteSBConstant.ACCOUNT_GROUP_SOLDTO);
			soldToAccountGroup.add(QuoteSBConstant.ACCOUNT_GROUP_PROSPECTIVE_CUSTOMER);
			boolean soldToChecking = customerSB.verifyCustomerNumberWithSalesDefaultBizUnit(
					rfqHeader.getSoldToCustomerNumber(), soldToAccountGroup, rfqHeader.getSalesBizUnit());
			if (!soldToChecking) {
				message.append(ResourceMB.getText("wq.label.SoldToCode") + ", ");
			}

			if (!QuoteUtil.isEmpty(rfqHeader.getShipToCustomerNumber())) {
				List<String> shipToAccountGroup = new ArrayList<String>();
				shipToAccountGroup.add(QuoteSBConstant.ACCOUNT_GROUP_SOLDTO);
				shipToAccountGroup.add(QuoteSBConstant.ACCOUNT_GROUP_SHIPTO);
				shipToAccountGroup.add(QuoteSBConstant.ACCOUNT_GROUP_PROSPECTIVE_CUSTOMER);
				boolean shipToChecking = customerSB.verifyCustomerNumberWithSalesDefaultBizUnit(
						rfqHeader.getShipToCustomerNumber(), shipToAccountGroup, rfqHeader.getSalesBizUnit());
				if (!shipToChecking) {
					message.append(ResourceMB.getText("wq.label.ShipToCode") + ", ");
				}
			}

			if (!QuoteUtil.isEmpty(rfqHeader.getEndCustomerNumber())) {
				Customer endChecking = findEndCustomerBySoldToAndEnd(rfqHeader.getEndCustomerNumber(),
						rfqHeader.getSoldToCustomerNumber());
				if (endChecking == null) {
					message.append(ResourceMB.getText("wq.label.endCust") + ", ");
				}
			}

			if (message.length() != 0) {
				FacesUtil.showWarnMessage(ResourceMB.getText("wq.message.custValidatnFails") + ":",
						message.append(ResourceMB.getText("wq.message.atRFQHeader") + ".").toString(),
						"RfqSubmissionGrowl_1");
				throw new WebQuoteException("", new Exception());
			}
		}
	}

/*	private void quoteItemVoChecking() throws WebQuoteException {
		StringBuffer message = new StringBuffer();

		for (RfqItemVO rfqItem : rfqItems) {

			if (QuoteUtil.isEmpty(rfqItem.getSoldToCustomerNumber())) {
				message.append(ResourceMB.getText("wq.label.SoldToCode") + ", ");
			}

			if (QuoteUtil.isEmpty(rfqItem.getCustomerType())) {
				message.append(ResourceMB.getText("wq.label.custmrType") + ", ");
			}
			if (QuoteUtil.isEmpty(rfqItem.getEnquirySegment())) {
				message.append(ResourceMB.getText("wq.label.enqrySegment") + ", ");
			}
			if (QuoteUtil.isEmpty(rfqItem.getQuoteType())) {
				message.append(ResourceMB.getText("wq.label.QuoteType") + ", ");
			}
			if (QuoteUtil.isEmpty(rfqItem.getDesignInCat())) {
				message.append(ResourceMB.getText("wq.error.designInCat") + ", ");
			}
			if (message.length() != 0) {
				FacesUtil.showWarnMessage(ResourceMB.getText("wq.message.mandFieldError") + " : ",
						message.append(ResourceMB.getText("wq.message.atRow") + ": ").append(rfqItem.getItemNumber())
								.toString(),
						"RfqSubmissionGrowl_1");
				throw new WebQuoteException("", new Exception());
			}
			// check sold to code,end customer,ship to code match with sales man
			// default bizunit
			List<String> soldToAccountGroup = new ArrayList<String>();
			soldToAccountGroup.add(QuoteSBConstant.ACCOUNT_GROUP_SOLDTO);
			soldToAccountGroup.add(QuoteSBConstant.ACCOUNT_GROUP_PROSPECTIVE_CUSTOMER);
			boolean soldToChecking = customerSB.verifyCustomerNumberWithSalesDefaultBizUnit(
					rfqItem.getSoldToCustomerNumber(), soldToAccountGroup, rfqHeader.getSalesBizUnit());
			if (!soldToChecking) {
				message.append(ResourceMB.getText("wq.label.SoldToCode") + ", ");
			}

			if (!QuoteUtil.isEmpty(rfqItem.getShipToCustomerNumber())) {
				List<String> shipToAccountGroup = new ArrayList<String>();
				shipToAccountGroup.add(QuoteSBConstant.ACCOUNT_GROUP_SOLDTO);
				shipToAccountGroup.add(QuoteSBConstant.ACCOUNT_GROUP_SHIPTO);
				shipToAccountGroup.add(QuoteSBConstant.ACCOUNT_GROUP_PROSPECTIVE_CUSTOMER);
				boolean shipToChecking = customerSB.verifyCustomerNumberWithSalesDefaultBizUnit(
						rfqItem.getShipToCustomerNumber(), shipToAccountGroup, rfqHeader.getSalesBizUnit());
				if (!shipToChecking) {
					message.append(ResourceMB.getText("wq.label.ShipToCode") + ", ");
				}
			}

			if (!QuoteUtil.isEmpty(rfqItem.getEndCustomerNumber())) {
				Customer endChecking = findEndCustomerBySoldToAndEnd(rfqItem.getEndCustomerNumber(),
						rfqItem.getSoldToCustomerNumber());
				if (endChecking == null) {

					message.append(ResourceMB.getText("wq.label.endCust") + ", ");
				}
			}
			if (message.length() != 0) {
				FacesUtil.showWarnMessage(ResourceMB.getText("wq.message.custValidatnFails") + ":",
						message.append(ResourceMB.getText("wq.message.atRow") + ": ").append(rfqItem.getItemNumber())
								.toString(),
						"RfqSubmissionGrowl_1");
				throw new WebQuoteException("", new Exception());
			}
		}

	}*/

	/**
	 * check mandatory field before submitation
	 * 
	 * @return if not pass checking return true, else return false
	 */
	private boolean rfqSubmitChecking() {
		try {
			quoteHeaderVOChecking();
			//quoteItemVoChecking();
		} catch (Exception e) {
			LOGGER.log(Level.SEVERE, "Error occured while checking before submitting, Reason for failure: "
					+ MessageFormatorUtil.getParameterizedStringFromException(e), e);
			return true;
		}
		return false;
	}

	public boolean emptySalesMan() {
		return rfqHeader.getSalesBizUnit() == null;
	}

	public String emptySalesManMessage() {
		return emptySalesMan() ? salemanFillMessage : "";
	}

	public List<String> designRegionOnchange(String inputRegion) {
		FacesContext context = FacesContext.getCurrentInstance();
		String paRegion = (String) UIComponent.getCurrentComponent(context).getAttributes().get("region");
		//logInfo("DDD"+"getParam:" + paRegion);
		// logInfo("inpParam:"+Region.toString());
		List<String> designLocationCodes = new ArrayList<String>();
		//added test code by DamonChen
		//Bryan Start
		//List<DesignLocation> designLocations = DesignLocationCacheManager.getDesignLocationList();
		List<DesignLocation> designLocations = cacheUtil.getDesignLocationList();
		//Bryan End
		if (designLocations != null && designLocations.size() > 0) {
			for (DesignLocation d : designLocations) {
				if (!designLocationCodes.contains(d.getCode())) {
					if (paRegion == null || paRegion.equals(QuoteConstant.DEFAULT_SELECT) || paRegion.equals("")
							|| paRegion.trim().equals("")) {
						designLocationCodes.add(d.getCode());
					} else {
						if (paRegion.equals(d.getDesignRegion())) {
							designLocationCodes.add(d.getCode());
						}
					}

				}
			}
			/*
			 * designLocationSelectList = QuoteUtil .createFilterOptions(
			 * designLocationCodes .toArray(new String[designLocationCodes
			 * .size()]), true, false);
			 */

		} else {
			logInfo("the DesignLocationList get from DesignLocationCacheManager is null");
		}

		if (!org.apache.commons.lang.StringUtils.isEmpty(inputRegion)) {
			List<String> machResult = new ArrayList<String>();
			for (String str : designLocationCodes) {
				if (str.toUpperCase().contains(inputRegion.toUpperCase())) {
					machResult.add(str);
				}
			}
			return machResult;
		}

		//logInfo("designRegionOnchange method end");
		return designLocationCodes;

	}

	public void getRfqs4SelectBmt() {
		logInfo("begin to call function getRfqs4SelectBmt!");
		// init pendingBmtRfqs
		if (pendingBmtRfqs != null && pendingBmtRfqs.size() > 0) {
			pendingBmtRfqs.clear();
			pendingBmtRfqs = new ArrayList<RfqItemVO>();

		} else {
			pendingBmtRfqs = new ArrayList<RfqItemVO>();
		}
		// pendingBmtRfqs.clear();
		RequestContext context = RequestContext.getCurrentInstance();
		for (Iterator<RfqItemVO> itr = rfqItems.iterator(); itr.hasNext();) {
			RfqItemVO rfqItem = itr.next();
			// logInfo(rfqItem.getEndCustomerNumber() + "***************"+
			// rfqItem.getDesignRegion());
			if (!QuoteUtil.isEmpty(rfqItem.getDesignRegion()) && !rfqItem.getDesignRegion().equals("ASIA")) {
				if (!QuoteUtil.isEmpty(rfqItem.getEndCustomerNumber())) {
					if (!pendingBmtRfqs.contains(rfqItem)) {
						pendingBmtRfqs.add(rfqItem);
					}
				}
			}

		}

		if (pendingBmtRfqs != null) {
			logInfo("the length of pendingBmtRfqs is: " + pendingBmtRfqs.size());
		} else {
			logInfo("the length of pendingBmtRfqs is null");
		}

		if (pendingBmtRfqs != null && pendingBmtRfqs.size() > 0) {
			context.addCallbackParam("comfirmFlag", "1");
		} else {
			context.addCallbackParam("comfirmFlag", "0");
			// this.submitRfqs();
		}

	}

	public void updateRfqItemBmtCheckedFlag() {

		for (RfqItemVO rVO : pendingBmtRfqs) {

			RfqItemVO rfqItem = getRfqItemByItemNumber(rVO.getItemNumber());

			rfqItem.setBmtCheckedFlag(rVO.isBmtCheckedFlag());
			rfqItems.set(rfqItem.getItemNumber() - 1, rfqItem);

		}

		this.submitRfqs();
	}

	// for the SPRvalidation, now the validation is rollback, by damon@20160401
	/*
	 * public void sprValidation(int itemNumber,String growlId,String growlFor){
	 * RfqItemVO itemv =rfqItems.get(itemNumber-1); if(itemv !=null){
	 * 
	 * if(itemv.isSpecialPriceIndicator()){
	 * if(!QuoteUtil.isEmpty(itemv.getDesignRegion()) &&
	 * (!itemv.getDesignRegion().equals(QuoteConstant.DEFAULT_SELECT))
	 * &&(!itemv.getDesignRegion().equals("ASIA"))){
	 * rfqItems.get(itemNumber-1).setSpecialPriceIndicator(false); FacesMessage
	 * msg = new FacesMessage(FacesMessage.SEVERITY_WARN, " ",
	 * "At row#<"+itemNumber+">:SPR is not applied during BMT Verification");
	 * FacesContext.getCurrentInstance().addMessage(growlFor, msg);
	 * RequestContext requestContext = RequestContext.getCurrentInstance();
	 * requestContext.update("form_rfqSubmission:"+growlId); return; } }
	 * 
	 * }
	 * 
	 * }
	 */

	public void bmtadFlagValidation(int itemNumber, String growlId, String growlFor) {
		RfqItemVO itemv = rfqItems.get(itemNumber - 1);
		if (itemv != null) {
			if (itemv.isBmtCheckedFlag()) {
				if (QuoteUtil.isEmpty(itemv.getDesignRegion()) || (itemv.getDesignRegion().equals("ASIA"))
						|| (itemv.getDesignRegion().equals(QuoteConstant.DEFAULT_SELECT))) {
					rfqItems.get(itemNumber - 1).setBmtCheckedFlag(false);

					FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_WARN, " ",
							ResourceMB.getText("wq.message.atRow") + "#<" + itemNumber + ">:"
									+ ResourceMB.getText("wq.message.designRegionCheckError"));
					FacesContext.getCurrentInstance().addMessage(growlFor, msg);
					RequestContext requestContext = RequestContext.getCurrentInstance();
					requestContext.update("form_rfqSubmission:" + growlId);
					return;
				}
			}

		}

	}

	public void selectOrDeslectAllSpr() {
		LOGGER.info("selectedAllSprFlag:" + selectedAllSprFlag);
		if(selectedAllSprFlag){
			selectedAllSprFlag=false;
		}else{
			selectedAllSprFlag=true;
		}

		if (selectedAllSprFlag) {
			for (RfqItemVO item : rfqItems) {
				item.setSpecialPriceIndicator(true);
			}
			this.selectedAllSprIt=true;
		} else {
			for (RfqItemVO item : rfqItems) {
				item.setSpecialPriceIndicator(false);
			}
			this.selectedAllSprIt=false;
		}
		
		LOGGER.info("selectedAllSprIt end:" + selectedAllSprIt);

	}

	public boolean isSelectedAllSprIt() {
		return selectedAllSprIt;
	}

	public void setSelectedAllSprIt(boolean selectedAllSprItTTT) {

		this.selectedAllSprIt = selectedAllSprItTTT;
	}

	public boolean isCreateProspectiveCustomerButtonDisplay() {
		return createProspectiveCustomerButtonDisplay;
	}

	public void setCreateProspectiveCustomerButtonDisplay(boolean createProspectiveCustomerButtonDisplay) {
		this.createProspectiveCustomerButtonDisplay = createProspectiveCustomerButtonDisplay;
	}

	/**
	 * Setter for resourceMB
	 * 
	 * @param resourceMB
	 *            ResourceMB
	 */
	public void setResourceMB(final ResourceMB resourceMB) {
		this.resourceMB = resourceMB;
	}

	/**
	 * @param restrictedCustList
	 * @param val
	 * @param rfqItem
	 * @return
	 */
	//private boolean checkRetrictedCustomer(String val, RfqItemVO rfqItem) {
	/*

		boolean returnB = false;

		for (RestrictedCustomerMapping rcm : restrictedCustList) {

			if (val.equalsIgnoreCase(rcm.getMandatoryKey())) {
				Material material = rfqItem.getMaterial();
				String prodGroup1 = null, prodGroup2 = null, prodGroup3 = null, prodGroup4 = null;
				if (material.getProductGroup1() != null)
					prodGroup1 = material.getProductGroup1().getName();
				if (material.getProductGroup2() != null)
					prodGroup2 = material.getProductGroup2().getName();
				prodGroup3 = material.getProductGroup3();
				prodGroup4 = material.getProductGroup4();

				if (!StringUtils.isEmpty(rcm.getPartNumber())
						&& !rcm.getPartNumber().equalsIgnoreCase(rfqItem.getRequiredPartNumber())) {
					continue;
				}
				if (!StringUtils.isEmpty(rcm.getProductGroup1())
						&& !rcm.getProductGroup1().equalsIgnoreCase(prodGroup1)) {
					continue;
				}
				if (!StringUtils.isEmpty(rcm.getProductGroup2())
						&& !rcm.getProductGroup2().equalsIgnoreCase(prodGroup2)) {
					continue;
				}
				if (!StringUtils.isEmpty(rcm.getProductGroup3())
						&& !rcm.getProductGroup3().equalsIgnoreCase(prodGroup3)) {
					continue;
				}
				if (!StringUtils.isEmpty(rcm.getProductGroup4())
						&& !rcm.getProductGroup4().equalsIgnoreCase(prodGroup4)) {
					continue;
				}
				return true;
			}
		}
		return returnB;
	*/
		//}

	/**
	 * @param customerAddresses
	 */
	private void updateSoldToCode(List<CustomerAddress> customerAddresses) {
		for (CustomerAddress customerAddress : customerAddresses) {
			if (customerAddress.getCountry() != null && customerAddress.getId().getLanguageCode() != null
					&& (customerAddress.getId().getLanguageCode().equals(QuoteSBConstant.LANGUAGE_CODE_CHINESE_C)
							|| customerAddress.getId().getLanguageCode().equals(QuoteSBConstant.LANGUAGE_CODE_CHINESE_M)
							|| customerAddress.getId().getLanguageCode()
									.equals(QuoteSBConstant.LANGUAGE_CODE_CHINESE_1))) {
				rfqHeader.setChineseSoldToCustomerName(getCustomerFullName(customerAddress));
				break;
			}
		}
	}

	/**
	 * @param item
	 * @param rfqItem
	 * @param programMB
	 *            kicked off by Damon Chen
	 */
	private QuoteItem commonAcLogic(QuoteItem item, RfqItemVO rfqItem) {
		// it is normal
		if (item.getMov() == null || item.getMov().doubleValue() == ZERO_DOUBLE) {
			item.setQtyIndicator("MOQ");
			// PROGRM PRICER ENHANCEMENT
			// item.setPmoq(QuoteUtil.findPmoq(item));
			item.setPmoq(QuoteUtil.findPmoq(item));
		} else if (item.getMoq() == null || item.getMoq().doubleValue() == ZERO_DOUBLE) {
			item.setQtyIndicator("MOV");
			item.setMoq(QuoteUtil.mpqRoundUp(item.getMov() / item.getCost(), item.getMpq().intValue()));
			// PROGRM PRICER ENHANCEMENT
			// item.setPmoq(QuoteUtil.findPmoq(item));
			item.setPmoq(QuoteUtil.findPmoq(item));
		} else {
			double firstP = item.getMov() / item.getCost();
			double secondP = item.getMoq();
			if (firstP > secondP) {
				item.setQtyIndicator("MOV");
				item.setMoq(QuoteUtil.mpqRoundUp(firstP, item.getMpq().intValue()));
				// PROGRM PRICER ENHANCEMENT
				// item.setPmoq(QuoteUtil.findPmoq(item));
				item.setPmoq(QuoteUtil.findPmoq(item));
			} else {
				item.setQtyIndicator("MOQ");
				// PROGRM PRICER ENHANCEMENT
				// item.setPmoq(QuoteUtil.findPmoq(item));
				item.setPmoq(QuoteUtil.findPmoq(item));
			}
		}
		return item;
	}

	/**
	 * @param quoteItem
	 * @param rfqHeader
	 * @param rfqItem
	 * @param projectIds
	 * @param bizUnit
	 */
/*	private QuoteItem commonRFQDraftAndSubmit(RfqItemVO rfqItem, QuoteItem quoteItem) {// added
																												// by
																												// June
																												// 20140707
																												// for
																												// RMB
																												// cur
																												// Project
		quoteItem.setCurrFrom("USD");
		quoteItem.setCurrTo(rfqHeader.getExCurrency());

		// Fixed the issue 712
		if (quoteItem.getDrmsNumber() != null && quoteItem.getDrmsNumber().longValue() == 0l) {
			quoteItem.setDrmsNumber(null);
		}


		if (!QuoteUtil.isEmpty(rfqItem.getProjectName()))
			quoteItem.setProjectName(rfqItem.getProjectName());


		quoteItem.setQuoteNumber(rfqItem.getQuoteNumber());
		quoteItem.setStage(rfqItem.getStage());
		if (rfqItem.getRequiredQty() != null) {
			quoteItem.setRequestedQty(Integer.valueOf(rfqItem.getRequiredQty()));
		}

		quoteItem.setSprFlag(rfqItem.isSpecialPriceIndicator());
		quoteItem.setSupplierDrNumber(rfqItem.getSupplierDrNumber());
		if (rfqItem.getTargetResale() != null && !StringUtils.isEmpty(rfqItem.getTargetResale())) {
			quoteItem.setTargetResale(Double.valueOf(rfqItem.getTargetResale()));
		}

		quoteItem.setRequestedPartNumber(rfqItem.getRequiredPartNumber());
		quoteItem.setQuotedPartNumber(rfqItem.getRequiredPartNumber());
		if (rfqItem.getSapPartNumber() != null) {
			quoteItem.setSapPartNumber(rfqItem.getSapPartNumber());
		}
		if (rfqItem.getMfr() != null) {
			Manufacturer mfr = manufacturerSB.findManufacturerByName(rfqItem.getMfr(), bizUnit);
			quoteItem.setRequestedMfr(mfr);
			quoteItem.setQuotedMfr(mfr);
		}

		if (rfqItem.getMaterial() != null) {
			//quoteItem.setreq(rfqItem.getMaterial());
			quoteItem.setQuotedMaterial(rfqItem.getMaterial());
		}

		if (rfqItem.getMaterial() != null) {
			Material tempMateril = rfqItem.getMaterial();
			quoteItem.setProductGroup2(tempMateril.getProductGroup2());
			quoteItem.setProductGroup1(tempMateril.getProductGroup1());
			quoteItem.setProductGroup3(tempMateril.getProductGroup3());
			quoteItem.setProductGroup4(tempMateril.getProductGroup4());
		}
		return quoteItem;
	}*/

	/**
	 * @param quote
	 * @param rfqHeader
	 * @param bizUnit
	 * @param user
	 * @param isSales
	 */
	private void commonQuoteDraftAndSubmit() {
		if (rfqHeader.getId() != 0l) {
			quote.setId(rfqHeader.getId());
			quote.setFormNumber(rfqHeader.getFormNumber());
			// Added by Tonmy on 20 Aug 2013. for issue 796
			quote.setVersion(rfqHeader.getVersion());
		}

		// Material restructure and quote_item delinkage. changed on 10 Apr
		// 2014.
		quote.setBizUnit(bizUnit);
		quote.setCreatedBy(user.getEmployeeId());
		quote.setCreatedName(user.getName());
		quote.setCreatedOn(QuoteUtil.getCurrentTime());
		quote.setLastUpdatedBy(user.getEmployeeId());
		quote.setLastUpdatedName(user.getName());
		quote.setLastUpdatedOn(QuoteUtil.getCurrentTime());
		quote.setSubmissionDate(QuoteUtil.getCurrentTime());

		if (this.isSales) {
			quote.setSales(user);
			quote.setTeam(user.getTeam());
		} else {
			User saleUser = userSB.findByEmployeeIdLazily(rfqHeader.getSalesEmployeeNumber());
			quote.setSales(saleUser);
			if (saleUser != null)
				quote.setTeam(saleUser.getTeam());
		}

		// WebQuote 2.2 enhancement : fields changes.
		// quote.setBmtCheckedFlag(rfqHeader.isBmtChecked());
		quote.setDesignInCat(rfqHeader.getDesignInCat());
		quote.setEnquirySegment(rfqHeader.getEnquirySegment());
		quote.setdLinkFlag(rfqHeader.isdLinkFlag());

		if (rfqHeader.getSoldToCustomerNumber() != null && !rfqHeader.getSoldToCustomerNumber().isEmpty()) {
			Customer soldTo = customerSB.findByPK(rfqHeader.getSoldToCustomerNumber());
			quote.setSoldToCustomer(soldTo);
		}

		quote.setCustomerType(rfqHeader.getCustomerType());
		// INC0018065, INC0018819
		quote.setSoldToCustomerName(rfqHeader.getSoldToCustomerName());
		quote.setSoldToCustomerNameChinese(rfqHeader.getChineseSoldToCustomerName());

		// fix defect 59 by June 20140911
		if (rfqHeader.getShipToCustomerName() != null) {
			quote.setShipToCustomerName(rfqHeader.getShipToCustomerName());
		}
		if (rfqHeader.getShipToCustomerNumber() != null && !rfqHeader.getShipToCustomerNumber().isEmpty()) {
			Customer shipTo = customerSB.findByPK(rfqHeader.getShipToCustomerNumber());
			quote.setShipToCustomer(shipTo);
			
			//added by DamonChen@20180402
			if (rfqHeader.getShipToCustomerName() == null && shipTo!=null) {
				// logger.info("@@rfqHeader.getEndCustomerName() :
				// "+rfqHeader.getEndCustomerName());
				quote.setShipToCustomerName(shipTo.getCustomerFullName());
			}
		}


		
		// fix defect 59 by June 20140911
		if (rfqHeader.getEndCustomerName() != null) {
			// logger.info("@@rfqHeader.getEndCustomerName() :
			// "+rfqHeader.getEndCustomerName());
			quote.setEndCustomerName(rfqHeader.getEndCustomerName());
		}

		if (rfqHeader.getEndCustomerNumber() != null && !rfqHeader.getEndCustomerNumber().isEmpty()) {
			// logger.info("@@rfqHeader.getEndCustomerNumber() :
			// "+rfqHeader.getEndCustomerNumber());
			Customer endCust = customerSB.findByPK(rfqHeader.getEndCustomerNumber());
			quote.setEndCustomer(endCust);
			//added by DamonChen@20180402
			if (rfqHeader.getEndCustomerName() == null && endCust!=null) {
				// logger.info("@@rfqHeader.getEndCustomerName() :
				// "+rfqHeader.getEndCustomerName());
				quote.setEndCustomerName(endCust.getCustomerFullName());
			}
		}
	
		quote.setProjectName(rfqHeader.getProjectName());
		quote.setApplication(rfqHeader.getApplication());
	}

	/**
	 * @param quoteItem
	 */
	private void commonLogicForQuotedPrice(QuoteItem quoteItem) {
		calcResaleMargin(quoteItem);
	}

	private List<QuoteItem> commonQuoteListDraftAndSubmit(Quote originalQuote) {

		commonQuoteDraftAndSubmit();

		// need to add designRegion Damon
		quote.setDesignRegion(rfqHeader.getDesignRegion());
		quote.setDesignLocation(rfqHeader.getDesignLocation());
		quote.setMpSchedule(rfqHeader.getMpSchedule());
		quote.setPpSchedule(rfqHeader.getPpSchedule());
		quote.setYourReference(rfqHeader.getYourReference());
		quote.setCopyToCS(rfqHeader.getCopyToCs());
		// Material restructure and quote_item delinkage. changed on 10 Apr
		// 2014.
		quote.setCopyToCsName(rfqHeader.getCopyToCsName());
		// quote.setVersion(quote.getVersion()+1);

		quote.setRemarks(rfqHeader.getRemarks());

		quote.setLoaFlag(rfqHeader.isLoa());
		// WebQuote 2.2 enhancement : fields changes.
		// quote.setBomFlag(rfqHeader.isBom());
		// quote.setOrderOnHandFlag(rfqHeader.isOrderOnHand());
		quote.setQuoteType(rfqHeader.getQuoteType());

		List<QuoteItem> quoteItems = new ArrayList<QuoteItem>();

		// Added by Tonmy on 12 Sep 2013 . for issue 978
		if (rfqHeader.isContinueSubmit()) {
			Set<Long> idLong = new HashSet<Long>();
			continueQuoteItems = new HashSet<Long>();
			for (RfqItemVO rfqItem : rfqItems) {
				idLong.add(new Long(rfqItem.getId()));
			}
			originalQuote = quoteSB.findQuoteByPK(quote.getId());
			if (originalQuote != null && originalQuote.getQuoteItems() != null
					&& originalQuote.getQuoteItems().size() > 0)
				for (QuoteItem item : originalQuote.getQuoteItems()) {
					if (!idLong.contains(new Long(item.getId()))) {
						quoteItems.add(item);
						continueQuoteItems.add(new Long(item.getId()));
					}

				}
		}
		return quoteItems;
	}
	/*
	 * During RFQ Submission, if users stop at step 3 BMT AQ suggestion window
	 * and go back to step 1 to change the part number , if meeting one of the
	 * following followingconditions , system is not able to capture the new
	 * part number for RFQ Submission. 1. The new part number has no price 2.
	 * Can¡¯t get price using the part number and MFR on the RFQ Submission
	 * Page.
	 */

	private void ClearDataAutoFillToRfqItem(RfqItemVO rfqItem) {
		LOGGER.info("[" + user.getEmployeeId() + " ]" + "Begining  Clear Data that AutoFillToRfqItem:"
				+ rfqItem.getMfr() + "|" + rfqItem.getRequiredPartNumber());
		rfqItem.setMpq(null);
		rfqItem.setMoq(null);
		rfqItem.setMov(null);
		rfqItem.setLeadTime(null);
		rfqItem.setCostIndicatorType(null);
		rfqItem.setMinSellPrice(null);
		rfqItem.setCost(null);
		rfqItem.setBottomPrice(null);
		rfqItem.setShipmentValidity(null);
		rfqItem.setPriceValidity(null);
		rfqItem.setQuotaionEffectiveDate(null);
		rfqItem.setPriceListRemarks1(null);
		rfqItem.setPriceListRemarks2(null);
		rfqItem.setPriceListRemarks3(null);
		rfqItem.setPriceListRemarks4(null);
		rfqItem.setDescription(null);
		rfqItem.setQtyIndicator(null);
		rfqItem.setCost(null);
		rfqItem.setResaleIndicator(null);
		rfqItem.setMultiUsage(null);
		rfqItem.setCostIndicator(null);
		rfqItem.setCancellationWindow(null);
		rfqItem.setReschedulingWindow(null);
		rfqItem.setTermsAndCondition(null);
		rfqItem.setQcComment(null);
		LOGGER.info("[" + user.getEmployeeId() + " ]" + "Ending  Clear Data that AutoFillToRfqItem:" + rfqItem.getMfr()
				+ "|" + rfqItem.getRequiredPartNumber());

	}

	private void applyDefaultCostIndictorOrAQLogic(RfqHeaderVO pRfqHeader, List<RfqItemVO> pRfqItems, List<Customer> allowCustomers2,String applyFlag) throws Exception {
		try {
			pRfqHeader.setAllowCustomers(allowCustomers2);
			for (RfqItemVO riv : pRfqItems) {
				riv.setRfqHeaderVO(pRfqHeader);
				if (!StringUtils.isEmpty(riv.getSoldToCustomerNumber())) {

					Customer customer = customerSB.findByPK(riv.getSoldToCustomerNumber());
					riv.setSoldToCustomer(customer);
				}
				if (!StringUtils.isEmpty(riv.getEndCustomerNumber())) {

					Customer endCustomer = customerSB.findByPK(riv.getEndCustomerNumber());
					riv.setEndCustomer(endCustomer);

				}
				LOGGER.info("begin call fillInpricerInfo:"+riv.toString());;
			}
			
			if(!StringUtils.isEmpty(applyFlag) && applyFlag.equalsIgnoreCase("A")){
				materialSB.applyAQLogic(pRfqItems, RFQSubmissionTypeEnum.NormalRFQSubmission);
			}else{
				materialSB.applyDefaultCostIndictorLogic(pRfqItems);
			}
			
			
			for (RfqItemVO riv : pRfqItems) {
	
				LOGGER.info("end call fillInpricerInfo:"+riv.toString());;
			}
		} catch (Exception e) {
			LOGGER.severe("[" + user.getEmployeeId() + " ]" + "Get error when fill in ifqItems," + e.getMessage());
			throw e;
		}

	}
	
	
	public void deleteDuplicatedItems(){
		List<RfqItemVO> tmpList= new ArrayList<RfqItemVO>();
		for(RfqItemVO item: tmpMap.values()){
			tmpList.add(item);
		}
		rfqItems=tmpList;
		
		duplicatedrfqItems.clear();
	}
	
	public void updateConfirmGoNextFlag()
	{
		confirmGoNextFlag=false;
	}
	
	
	public void updateDollarLinkFlag(){
		if(rfqHeader.isdLinkFlag()){
			rfqHeader.setRfqCurr(Currency.USD.name());
		}

	}
	
	public void updateRfqCurr(){
		if(!Currency.USD.name().equals(rfqHeader.getRfqCurr())){
			rfqHeader.setdLinkFlag(false);
			
		}
	}

}