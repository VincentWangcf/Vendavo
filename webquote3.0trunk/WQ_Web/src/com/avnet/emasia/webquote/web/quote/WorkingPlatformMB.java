package com.avnet.emasia.webquote.web.quote;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.Serializable;
import java.math.BigDecimal;
import java.net.InetAddress;
import java.text.*;
import java.util.*;
import java.util.concurrent.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import static java.util.stream.Collectors.*;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.*;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.faces.event.AjaxBehaviorEvent;
import javax.faces.model.SelectItem;
import javax.json.Json;
import javax.json.JsonObjectBuilder;
import javax.persistence.OptimisticLockException;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.cxf.common.util.StringUtils;
import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddressList;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.eclipse.jetty.util.log.Log;
import org.primefaces.component.datatable.DataTable;
import org.primefaces.context.RequestContext;
import org.primefaces.event.*;
import org.primefaces.event.data.PageEvent;
import org.primefaces.model.*;
import org.springframework.security.core.context.SecurityContextHolder;
import com.avnet.emasia.webquote.commodity.helper.ProgRfqSubmitHelper;
import com.avnet.emasia.webquote.constants.*;
import com.avnet.emasia.webquote.dp.EJBCommonSB;
import com.avnet.emasia.webquote.dp.xml.requestquote.DropDownFilters;
import com.avnet.emasia.webquote.entity.*;
import com.avnet.emasia.webquote.entity.Currency;
import com.avnet.emasia.webquote.exception.AppException;
import com.avnet.emasia.webquote.exception.WebQuoteException;
import com.avnet.emasia.webquote.quote.ejb.*;
import com.avnet.emasia.webquote.quote.ejb.constant.QuoteSBConstant;
import static com.avnet.emasia.webquote.quote.ejb.constant.QuoteSBConstant.*;
import com.avnet.emasia.webquote.quote.strategy.interfaces.DrmsValidationUpdateStrategy;
import com.avnet.emasia.webquote.quote.vo.*;
import com.avnet.emasia.webquote.reports.constant.ReportConstant;
import com.avnet.emasia.webquote.reports.ejb.*;
import com.avnet.emasia.webquote.reports.util.ProgOqmspUtil;
import com.avnet.emasia.webquote.reports.util.ReportConvertor;
import com.avnet.emasia.webquote.strategy.StrategyFactory;
import com.avnet.emasia.webquote.user.ejb.*;
import com.avnet.emasia.webquote.utilites.resources.ResourceMB;
import com.avnet.emasia.webquote.utilites.web.common.CacheUtil;
import com.avnet.emasia.webquote.utilites.web.util.*;
import com.avnet.emasia.webquote.utilites.web.util.QuoteUtil.MapKeyComparator;
import com.avnet.emasia.webquote.utilities.MessageFormatorUtil;
import com.avnet.emasia.webquote.utilities.bean.MailInfoBean;
import com.avnet.emasia.webquote.utilities.common.OSTimeZone;
import com.avnet.emasia.webquote.utilities.common.SysConfigSB;
import com.avnet.emasia.webquote.utilities.ejb.MailUtilsSB;
import com.avnet.emasia.webquote.vo.*;
import com.avnet.emasia.webquote.web.datatable.SelectableLazyDataModel;
import com.avnet.emasia.webquote.web.quote.constant.QuoteConstant;
import com.avnet.emasia.webquote.web.quote.strategy.SendQuotationEmailStrategy;
import com.avnet.emasia.webquote.web.quote.vo.*;
import com.avnet.emasia.webquote.web.security.WQUserDetails;
import com.avnet.emasia.webquote.web.user.UserInfo;

import ch.qos.logback.core.joran.conditional.ElseAction;

@ManagedBean
@SessionScoped
// NEC Pagination Changes : Restructured class to add lazy loading and
// pagination
public class WorkingPlatformMB extends CommonBean implements Serializable {

	private static final long serialVersionUID = -1363888879417136529L;
	/*@EJB
	TaskScheduleManager tmr;*/
	@EJB
	QuoteSB quoteSB;

	@EJB
	BizUnitSB bizUnitSB;
	
	@EJB
	RestrictedCustomerMappingSB restrictedCustomerSB;

	@EJB
	QuoteTransactionSB quoteTransactionSB;

	@EJB
	private ApplicationSB applicationSB;

	@EJB
	PosSB posSB;

	@EJB
	private CacheUtil cacheUtil;

	@EJB
	ManufacturerSB manufacturerSB;

	@EJB
	CustomerSB customerSB;

	@EJB
	VendorReportSB vendorReportSB;

	@EJB
	FreeStockSB freeStockSB;

	@EJB
	MailUtilsSB mailUtilsSB;

	@EJB
	AttachmentSB attachmentSB;

	@EJB
	CostIndicatorSB costIndicatorSB;

	@EJB
	SysConfigSB sysConfigSB;

	@EJB
	SAPWebServiceSB sapWebServiceSB;

	// INC0023876
	@EJB
	SystemCodeMaintenanceSB sysMaintSB;

	@EJB
	AsyncPostQuotationSB asyncPostQuotation;

	@EJB
	PricerTypeMappingSB pricerTypeMappingSB;

	@EJB
	private BmtQuoteSB bmtQuoteSB;
	@EJB
	private MaterialTypeSB materialTypeSB;
	@EJB
	private ProgramTypeSB programTypeSB;

	@EJB
	private EJBCommonSB ejbCommonSB;

	@EJB
	MaterialSB materialSB;

	@EJB
	UserSB userSB;

	@EJB
	ExcelReportSB excelReportSB;

	@EJB
	OfflineReportSB offlineReprtSB;

	@EJB
	SapDpaCustSB sapDpaCustSB;

	// private boolean checkedInOnRowSelect = false;
	private String materialType;

	private transient UploadedFile workingPlatformExcel;

	private List<String> mfrCodes;
	private List<String> costIndicatorCodes;
	private List<String> materialTypes;

	private SelectItem[] filterMfrSelectList;
	private SelectItem[] filterTeamSelectList;
	private SelectItem[] filterCustomerSelectList;
	private List<SelectItem> filterBmtFlagSelectList;

	private volatile SelectItem[] mfrSelectList;
	private SelectItem[] costIndicatorSelectList;
	private SelectItem[] materialTypeSelectList;

	// WebQuote 2.2 enhancement : fields changes.
	private List<SelectItem> designInCatList;
	private List<SelectItem> designRegionList;
	private List<SelectItem> filterDesignRegionList;
	private List<AvnetQuoteCentreCommentVO> avnetQuoteCentreCommentList;
	private List<TermsAndConditionsVO> termsAndConditionsList;

	// override the property in super class
	private SelectItem[] qtyIndicatorOptions;

	private AvnetQuoteCentreCommentVO selectedQcExternalComment;
	private AvnetQuoteCentreCommentVO selectedQcInternalComment;
	private TermsAndConditionsVO selectedTermsAndConditions;
	private AvnetQuoteCentreCommentVO selectedDetailQcExternalComment;
	private AvnetQuoteCentreCommentVO selectedDetailQcInternalComment;
	private TermsAndConditionsVO selectedDetailTermsAndConditions;

	// private List<WorkingPlatformItemVO> workingPlatformItems;
	private List<WorkingPlatformItemVO> searchedWorkingPlatformItems;
	private List<WorkingPlatformItemVO> selectedWorkingPlatformItems;
	private transient List<WorkingPlatformItemVO> selectedItToBmtItems;
	private WorkingPlatformItemVO selectedWorkingPlatformItem;

	private List<String> filterMfrOptionList = new ArrayList<String>();
	private List<String> filterTeamOptionList = new ArrayList<String>();
	private List<String> filterCustomerOptionList = new ArrayList<String>();
	private Map<String, String> filterBmtFlagOptionList = new TreeMap<String, String>(new MapKeyComparator());
	private List<String> filterDRegionOptionList = new ArrayList<String>();

	// private List<String> filterMfrOptionListTotal = new ArrayList<String>();

	// private Set<String> searchMfrCodeSet=new HashSet<String>();

	private String internalTransferComment;
	private String internalComment;

	private List<FreeStock> freeStockList = new ArrayList<FreeStock>();
	private List<FreeStock> filteredFreeStockList = new ArrayList<FreeStock>();

	private List<ProceedQuotationVO> proceedQuotations;
	private int proceedQuotationCount;

	private int qcCounter;
	private int itCounter;
	private int sqCounter;
	private int ritCounter;
	private int rbitCounter;
	private int bitCounter;
	private int bqCounter;
	private int rbqCounter;

	private int quoteItemCount;
	private int searchMaterialDetailsCount;

	private int updateRfqCount;
	private String selectedUpdateRfqStatus;

	@SuppressWarnings("unused")
	private boolean anyOneProceed = false;
	private boolean refreshAll = false;
	private boolean editMode = false;
	private int rowIndex;

	private WorkingPlatformItemVO selectedQuoteItem;
	/*
	 * display pos history of the user
	 */
	// private List<PosStatisticVO> posStatistics;
	private List<PosSummary> posSummary;

	/*
	 * display pos history of the user
	 */
	private List<WorkingPlatformItemVO> quotationHistorys;
	private List<WorkingPlatformItemVO> filteredQuotationHistorys;

	/*
	 * display pos history of the user
	 */
	private List<VendorReport> vendorQuotationHistorys;
	private List<VendorReport> filteredVendorQuotationHistorys;

	private String currentQuarter;
	private String last1Quarter;
	private String last2Quarter;
	private String last3Quarter;

	private String selectedQuoteNumber = "";
	private String updateId;

	private User user;
	private List<String> emailContents;
	private String emailContent;
	private String emailTo;
	private String emailFrom;
	private String emailCc;
	private List<User> emailAddressList; // CS Sales PM CM BMT

	private List<QuotationWarningVO> quotationWarnings;
	private List<QuoteItem> selectedQuotationWarnings;
	private List<WorkingPlatformItemVO> preQuoteItems;

	private int internalTransferEmailCount;
	private int invalidateRfqCount;

	private String freeStockPartNumberSearch;
	private String freeStockMfrSearch;

	private String posHistorySoldToCustomerNameSearch;
	private String posHistoryPartNumberSearch;
	private String posHistoryMfrSearch;

	private String quoteHistorySoldToCustomerNameSearch;
	private String quoteHistoryPartNumberSearch;
	private String quoteHistoryMfrSearch;

	private String vendorQuoteHistorySoldToCustomerNameSearch;
	private String vendorQuoteHistoryPartNumberSearch;
	private String vendorQuoteHistoryMfrSearch;

	private List<Attachment> referenceAttachments;
	private List<Attachment> displayAttachmentList;
	private transient StreamedContent rfqAttachment;

	private WorkingPlatformItemVO seletedItem;
	private String displayExternalComment;
	private String displayInternalComment;
	private String displayTermsAndConditions;
	private String displayPmComment;

	private int selectedRowIndex;

	// used for popup dialog of internal transfer
	private List<WorkingPlatformEmailVO> updateStatusItems;
	// used for popup dialog of invalid quote
	private List<WorkingPlatformEmailVO> invalidQuoteEmailVOs;

	private UpdateSprVO updateSprVO;

	private boolean showAllCustomerPos = false;

	private static final Logger LOGGER = Logger.getLogger(WorkingPlatformMB.class.getName());

	private DateFormat dateFormatOfDMY = new SimpleDateFormat("dd/MM/yyyy");

	private List<String> exceedRecipientList;

	private String failMessage;

	private Map<String, String> filterMap;

	// When user click "proceed quotation" and "invalid quote", he need stay on
	// same page after datatable refreshed
	private boolean keepFirstRow = false;

	private int firstRow = 0;

	private int firstRowPosition;

	private int searchCustomersCount = 0;
	private String customerNameSearch;
	private List<Customer> searchCustomers;
	private List<Customer> selectedSearchCustomers;
	private Customer selectedSearchCustomer;
	private String endCustomerTypeSearch;
	private SelectItem[] customerTypeSearchSelectList;
	private SelectItem[] endCustomerSearchRegionList;
	private String endCustomerSearchRegion;
	private String endCustomerSearchType;

	private String restrictedCustomerMsg;
	private List<RestrictedCustomerMapping> restrictedCustList;

	MyQuoteSearchCriteria criteria = new MyQuoteSearchCriteria();

	private String[] headerArray = { "Avnet Quote#", "Revert Version", "First RFQ Code", "Pending Day", "RFQ Status",
			"Quote Type", "$Link", "Sales Cost Part", "Sales Cost Type", "Valid RFQ", "Resale Indicator", "BUY CUR",
			"Avnet Quoted Price", "Sales Cost", "Suggested Resale", "Avnet Quoted P/N", "RFQ CUR",
			"Target Resale at RFQ CUR", "Target Resale at BUY CUR", "Exchange Rate", "Required Qty", "EAU",
			"Multi-Usage", "QC External Comment", "QC Internal Comment", "PM Comment", "Price Validity",
			"Shipment Validity", "MFR Quote #", "MFR Debit #", "MFR Quote Qty", "Avnet Quoted Qty", "Allocation Part",
			"Qty Indicator", "Cost Indicator", "Cost", "Bottom Price", "Min Sell Price", "Lead Time", "MPQ", "MOQ",
			"MOV", "MFR", "Sold-To-Code", "Sold-To-Party", "Customer Group", "Requested P/N", "Pricer List Remarks 1",
			"Pricer List Remarks 2", "Pricer List Remarks 3", "Pricer List Remarks 4", "Product Group 2", "Description",
			"Biz Program Type", "Order Qty", "Min Sell Price(Pricer)", "Sales Cost (Pricer)",
			"Suggested Resale (Pricer)", "Quotation Effective Date", "Terms and Conditions", "Rescheduling Window",
			"Cancellation Window", "RFQ Submission Date", "Requester Reference", "Salesman Employee Code", "Salesman",
			"Team", "Customer Type", "Project Name", "Application", "Segment", "Design Location", "Design In Cat",
			"DRMS Project ID", "End Customer", "Ship-To-Code", "Ship-To-Party", "End Customer Code", "LOA", "Remarks",
			"Competitor Information", "MFR DR #", "PP Schedule", "MP Schedule", "Item Remarks", "SPR",
			"Reason For Refresh", "Form No", "Copy to CS", "Sold To Party (Chinese)", "System ID", "QC Region",
			"Design Region", "Sap Material Number", "BMT Flag", "BMT MFR DR#", "BMT Suggest Cost", "BMT Suggest Resale",
			"BMT MFR Quote#", "BMT SQ Effective Date", "BMT SQ Expiry Date", "BMT Comments", "BMT Quoted Qty",
			"BMT Suggest Cost (Non-USD)", "BMT Suggest Resale (Non-USD)", "BMT Currency", "BMT Exchange Rate (Non-USD)",
			"Last Update BMT" };

	// Added By Lenon.Yang(043044) 2016-11-07
	private WorkingPlatFormSearchCriteria workingPlatFormSearchCriteria;
	// discard
	// private SelectItem[] mfrSearchSelectList;

	private List<PricerInfo> pricerInfosInMaterialPopup = new ArrayList<>();

	private List<PricerInfo> selectedPricerInfosInMaterialPopup;

	private List<PricerInfo> pricerInfosInPricerPopup = new ArrayList<>();

	private PricerInfo selectedPricerInfoInPricerPopup;

	private final static String[] StatisTargets = { QuoteSBConstant.RFQ_STATUS_QC, QuoteSBConstant.RFQ_STATUS_IT,
			QuoteSBConstant.RFQ_STATUS_SQ, QuoteSBConstant.RFQ_STATUS_RIT, QuoteSBConstant.RFQ_STATUS_RBIT,
			QuoteSBConstant.RFQ_STATUS_BIT, QuoteSBConstant.RFQ_STATUS_BQ, QuoteSBConstant.RFQ_STATUS_RBQ };

	@ManagedProperty(value = "#{resourceMB}")
	/** To Inject ResourceMB instance */
	private ResourceMB resourceMB;

	// NEC Pagination Changes : To hold values for pagination for working
	// platform Items
	private LazyDataModelForWorkingPlateform workingPlatformItems = new LazyDataModelForWorkingPlateform();
	// mutil thread
	private static final ExecutorService exc = Executors.newCachedThreadPool();

	public WorkingPlatformMB() {
		// Bryan Start

		List<String> pastFourQuarter = QuoteUtil.getPastFourQuarter();
		currentQuarter = pastFourQuarter.get(0);
		last1Quarter = pastFourQuarter.get(1);
		last2Quarter = pastFourQuarter.get(2);
		last3Quarter = pastFourQuarter.get(3);

		materialTypes = new ArrayList<String>();
		materialTypes.add(QuoteSBConstant.MATERIAL_TYPE_NORMAL);
		materialTypes.add(QuoteSBConstant.MATERIAL_TYPE_PROGRAM);

		List<String> materialTypeLists = new ArrayList<String>();
		materialTypeLists.add(QuoteSBConstant.MATERIAL_TYPE_NORMAL);
		materialTypeLists.add(QuoteSBConstant.MATERIAL_TYPE_PROGRAM);
		materialTypeSelectList = QuoteUtil.createFilterOptions(
				materialTypeLists.toArray(new String[materialTypeLists.size()]),
				materialTypeLists.toArray(new String[materialTypeLists.size()]), false, false, true);
	}

	public void setDefaultSearchCriteria(User user) {
		workingPlatFormSearchCriteria = new WorkingPlatFormSearchCriteria();
		List<StatusEnum> statuss = Arrays.asList(StatusEnum.values());
		List<String> strStatuss = new ArrayList<String>();
		for (StatusEnum stt : statuss) {
			if (!org.apache.commons.lang.StringUtils.equals(StatusEnum.AQ.toString(), stt.toString())) {
				strStatuss.add(stt.toString());
			}

		}
		List<String> defaultSelectedStatuss = new ArrayList<String>();
		defaultSelectedStatuss.add(StatusEnum.SQ.toString());
		defaultSelectedStatuss.add(StatusEnum.QC.toString());
		defaultSelectedStatuss.add(StatusEnum.IT.toString());
		defaultSelectedStatuss.add(StatusEnum.RIT.toString());
		defaultSelectedStatuss.add(StatusEnum.RBQ.toString());
		defaultSelectedStatuss.add(StatusEnum.RBIT.toString());
		workingPlatFormSearchCriteria.setStatus(defaultSelectedStatuss);
		workingPlatFormSearchCriteria.setPageStatus(strStatuss);
		workingPlatFormSearchCriteria.setRecordNumber("500");
		workingPlatFormSearchCriteria.setSelectedMfr("*ALL");
		workingPlatFormSearchCriteria.setSelectedSalesCostPart(QuoteConstant.OPTION_ALL);

	}

	@PostConstruct
	private void postContruct() {
		// Bryan Start
		List<String> qtyIndicatorList = cacheUtil.getQtyIndicatorList();
		qtyIndicatorOptions = QuoteUtil.createFilterOptions(
				qtyIndicatorList.toArray(new String[qtyIndicatorList.size()]),
				qtyIndicatorList.toArray(new String[qtyIndicatorList.size()]), false, false, false);

		avnetQuoteCentreCommentList = cacheUtil.getAvnetQuoteCentreCommentList();
		termsAndConditionsList = cacheUtil.getTermsAndConditionsList();

		List<String> designRegionLists = cacheUtil.getDesignRegionList();
		designRegionList = FacesUtil.selectItemsProvider(designRegionLists, true);
		filterDesignRegionList = FacesUtil.selectItemsProvider(designRegionLists, true);
		List<String> designInCats = cacheUtil.getDesignInCatList();
		designInCatList = FacesUtil.selectItemsProvider(designInCats, true);
		// Bryan End

		user = ((WQUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUser();

		bizUnit = user.getDefaultBizUnit();
		setDefaultSearchCriteria(user);
		mfrCodes = new ArrayList<String>();
		List<String> fullMfrCodes = new ArrayList<String>();
		searchedWorkingPlatformItems = new ArrayList<WorkingPlatformItemVO>();
		if (user.getDataAccesses() != null && user.getDataAccesses().size() > 0) {
			for (DataAccess dataAccess : user.getDataAccesses()) {
				if (dataAccess.getManufacturer() == null) {
					mfrCodes = new ArrayList<String>();
					// Bryan Start
					// List<Manufacturer> manufacturers =
					// MfrCacheManager.getMfrListByBizUnit(bizUnit.getName());
					List<Manufacturer> manufacturers = cacheUtil.getMfrListByBizUnit(bizUnit.getName());
					// Bryan End
					if (manufacturers != null) {
						for (Manufacturer manufacturer : manufacturers)
							mfrCodes.add(manufacturer.getName());
					}
					break;

				} else {
					Manufacturer manufacturer = dataAccess.getManufacturer();
					if (manufacturer != null && !mfrCodes.contains(manufacturer.getName()))
						mfrCodes.add(manufacturer.getName());
				}
			}
		}

		// Bryan Start
		// List<Manufacturer> manufacturers =
		// MfrCacheManager.getMfrListByBizUnit(bizUnit.getName());
		List<Manufacturer> manufacturers = cacheUtil.getMfrListByBizUnit(bizUnit.getName());
		// Bryan End
		if (manufacturers != null) {
			for (Manufacturer manufacturer : manufacturers)
				fullMfrCodes.add(manufacturer.getName());
		}

		customerTypeSearchSelectList = QuoteUtil.createFilterOptions(QuoteConstant.CUSTOMER_TYPE, false, true);
		// Bryan Start
		// List<String> bizUnitCodes = BizUnitCacheManager.getBizUnitList();
		List<String> bizUnitCodes = cacheUtil.getBizUnitList();
		// Bryan End
		endCustomerSearchRegionList = QuoteUtil
				.createFilterOptions(bizUnitCodes.toArray(new String[bizUnitCodes.size()]), false, false);

		List<String> roleNames = ejbCommonSB.populateRoleNameList();

		emailAddressList = userSB.findWorkingPlatformEmailListByBizUnit(roleNames, bizUnit);
		// UserCacheManager.getEmailAddress(bizUnit);
		refreshQuoteTable();

	}

	/****** allow currencies **/
	public SelectItem[] convertCurrencyToSelectItem() {
		Set<String> currs = user.getDefaultBizUnit().getAllowCurrencies();
		return QuoteUtil.createFilterOptions(currs.toArray(new String[currs.size()]),
				currs.toArray(new String[currs.size()]), false, false, true);
	}

	private void handleFitersDropDown(List<DropDownFilters> list, boolean forRefresh) {
		List<String> mfrList = list.parallelStream().map(d -> d.getMfr()).filter(d -> d != null).distinct().sorted()
				.collect(toList());
		List<String> soldToList = list.parallelStream().filter(d -> d != null).map(d -> d.getSoldTo().getName())
				.distinct().sorted().collect(toList());
		List<String> bmtCodeList = list.parallelStream().map(d -> d.getBmtFlagCode()).filter(d -> d != null).distinct()
				.collect(toList());
		List<String> dRegionList = list.parallelStream().map(d -> d.getDesignRegion()).filter(d -> d != null).distinct()
				.sorted().collect(toList());
		List<String> teamList = list.parallelStream().map(d -> d.getTeam()).filter(d -> d != null).distinct().sorted()
				.collect(toList());

		this.filterMfrSelectList = QuoteUtil.createFilterOptionsForManufecturer(false, true, false,
				mfrList.toArray(new String[mfrList.size()]));
		this.filterCustomerSelectList = QuoteUtil.createFilterOptionsForWP(
				soldToList.toArray(new String[soldToList.size()]), soldToList.toArray(new String[soldToList.size()]),
				false, true, false);
		this.filterDesignRegionList = FacesUtil.selectItemsProvider(dRegionList, true);
		this.filterTeamSelectList = QuoteUtil.createFilterOptionsForWP(teamList.toArray(new String[teamList.size()]),
				teamList.toArray(new String[teamList.size()]), false, true, false);

		Map<String, String> bmtFlagOptionList = new TreeMap<String, String>(new MapKeyComparator());
		for (String code : bmtCodeList) {
			BmtFlag bmt = new BmtFlag(code);
			bmtFlagOptionList.put(bmt.getDescription(), bmt.getBmtFlagCode());
		}
		this.filterBmtFlagSelectList = QuoteUtil.createBmtFilterOptionsForWP(bmtFlagOptionList);
		// FacesUtil.updateUI("form_pendinglist:datatable_pendinglist:p_mfr:filter",
		// "form_pendinglist:datatable_pendinglist:p_soldTo:filter");
		if (!forRefresh) {
			filterMfrOptionList = mfrList;
			filterTeamOptionList = teamList;
			filterBmtFlagOptionList = bmtFlagOptionList;
			filterCustomerOptionList = soldToList;
			filterDRegionOptionList = dRegionList;
			dynamicCustomerFilterByJS();
		}
	}

	// CREATE DYNATIC FILTER WHEN FROM OTHERE FILTER CHANGED.
	public void dynamicCustomerFilterByJS() {
		JsonObjectBuilder jsonBuider = Json.createObjectBuilder();
		jsonBuider.add("mfrCode_size", filterMfrOptionList.size());
		jsonBuider.add("team_size", filterTeamOptionList.size());
		jsonBuider.add("soldTo_size", filterCustomerOptionList.size());
		jsonBuider.add("bmt_size", filterBmtFlagOptionList.size());
		jsonBuider.add("dRegion_size", this.filterDRegionOptionList.size());

		for (int i = 0; i < filterMfrOptionList.size(); i++)
			jsonBuider.add("mfrCode" + i, StringEscapeUtils.escapeXml(filterMfrOptionList.get(i)));
		for (int i = 0; i < filterTeamOptionList.size(); i++)
			jsonBuider.add("team" + i, StringEscapeUtils.escapeXml(filterTeamOptionList.get(i)));
		for (int i = 0; i < filterCustomerOptionList.size(); i++)
			jsonBuider.add("soldTo" + i, StringEscapeUtils.escapeXml(filterCustomerOptionList.get(i)));
		List<String> bmtKeys = new ArrayList<>(filterBmtFlagOptionList.keySet());
		for (int i = 0; i < bmtKeys.size(); i++) {
			jsonBuider.add("bmtValue" + i, StringEscapeUtils.escapeXml(bmtKeys.get(i)));
			jsonBuider.add("bmtKey" + i, StringEscapeUtils.escapeXml(filterBmtFlagOptionList.get(bmtKeys.get(i))));
		}
		for (int i = 0; i < filterDRegionOptionList.size(); i++)
			jsonBuider.add("dRegion" + i, StringEscapeUtils.escapeXml(filterDRegionOptionList.get(i)));
		// only can call one time for builder
		RequestContext.getCurrentInstance().execute("handleFilteredLazy(" + jsonBuider.build().toString() + ")");

	}

	private void initMfrSearchSelectItem(MyQuoteSearchCriteria criteria) {
		Long start1 = System.currentTimeMillis();
		List<String> searchMfrCodes = quoteSB.searchMfrNameList(criteria);
		LOGGER.info("searchMfrNameList::: " + (System.currentTimeMillis() - start1) + " ms.");
		if (searchMfrCodes.size() > 0) {
			mfrSelectList = QuoteUtil.createFilterOptions(searchMfrCodes.toArray(new String[searchMfrCodes.size()]),
					searchMfrCodes.toArray(new String[searchMfrCodes.size()]), false, false);
		}
	}

	public void clearFilter() {
		DataTable dataTable = (DataTable) FacesContext.getCurrentInstance().getViewRoot()
				.findComponent("form_pendinglist:datatable_pendinglist");
		if (dataTable != null) {
			dataTable.setFilters(null);
			dataTable.setFilteredValue(null);
			dataTable.reset();
		}
	}

	private void testFactory() {
		//tmr.callPersist();
		//tmr.callByCreate();
		/*Money moneyeee = Money.of(new BigDecimal("2.6001"), Currency.valueOf("RMB"))
				.convertToBuy(Currency.valueOf("USD"), bizUnit, null, new Date(), 10);
		System.out.println(moneyeee.getAmount().toString());

		Log.info("ROLES :::::::::::::::::::::::::::" + user.getRoles());
		Log.info("REGIONS :::::::::::::::::::::::::::" + user.getAllBizUnits());
		List<String> bs = user.getAllBizUnits().stream().collect(Collectors.toList());
		List<String> rs = user.getRoles().stream().map(p -> p.getName()).collect(Collectors.toList());
		boolean result = ComponetConfigService.show("P1", "C1", bs, rs);
		Log.info("result :::::::::::::::::::::::::::" + result);
		Log.info("result old :::::::::::::::::::::::::::" + ComponetConfigService.show("P1", "C1", user));
		boolean result2 = ComponetConfigService.show("P1", "C2", user);

		Log.info("result2 old:::::::::::::::::::::::::::" + result2);
		boolean result2new = ComponetConfigService.show("P1", "C2", bs, rs);
		Log.info("result2 :::::::::::::::::::::::::::" + result2new);

		Object d2 = StrategyFactory.getSingletonFactory().getStrategy(SendQuotationEmailStrategy.class, "CHINA",
				this.getClass().getClassLoader());
		Log.info("SendQuotationEmailStrategy D2 :::::::::::::::::::::::::::" + d2);
		Object d3 = StrategyFactory.getSingletonFactory().getStrategy(SendQuotationEmailStrategy.class, "JAPAN",
				this.getClass().getClassLoader());
		Log.info("SendQuotationEmailStrategy D3 :::::::::::::::::::::::::::" + d3);*/

	}

	// NEC Pagination Changes: for refresh new pricing button
	public void refreshNewPricing() {

		// NEC Pagination changes: get the selected data from cache
		// CacheSynCommandClusterService.doClearExRatesCache();
		//testFactory();
		if (workingPlatformItems.getCacheSelectedItems() != null) {
			// NEC Pagination changes: get the selected data from cache
			Set<WorkingPlatformItemVO> selectedWPLst = workingPlatformItems.getCacheSelectedItems();
			Set<String> ids = new HashSet<String>();
			for (WorkingPlatformItemVO item : selectedWPLst) {
				if (item.getQuoteItem().getStatus() != null
						&& !item.getQuoteItem().getStatus().toUpperCase().equals(QuoteSBConstant.RFQ_STATUS_RIT)) {
					item.getQuoteItem().setQuotedPartNumber(item.getQuotedPartNumber());
					item.getQuoteItem().setAqcc(item.getAqccStr());
					quoteSB.refreshingNewPricing(item.getQuoteItem());
					item.setQuoteItem(item.getQuoteItem(), bizUnit);
					item.setChanged(true);
					ids.add(String.valueOf(item.getQuoteItem().getId()));
				}
			}
			for (String id : ids) {
				workingPlatformItems.addChangeItem(id);
			}
		}
	}

	// NEC Pagination Changes: for refresh button
	public void refreshQuoteTable() {
		long start = System.currentTimeMillis();
		// logger.log(Level.INFO, "PERFORMANCE START - refreshQuoteTable()");
		if (filterMfrSelectList == null)
			filterMfrSelectList = new SelectItem[0];
		if (filterTeamSelectList == null)
			filterTeamSelectList = new SelectItem[0];
		if (filterCustomerSelectList == null)
			filterCustomerSelectList = new SelectItem[0];
		if (filterBmtFlagSelectList == null)
			filterBmtFlagSelectList = new ArrayList<>();
		if (filterMfrOptionList == null)
			filterMfrOptionList = new ArrayList<String>();
		if (filterTeamOptionList == null)
			filterTeamOptionList = new ArrayList<String>();
		if (filterCustomerOptionList == null)
			filterCustomerOptionList = new ArrayList<String>();
		if (filterBmtFlagOptionList == null)
			filterBmtFlagOptionList = new TreeMap<String, String>(new MapKeyComparator());

		if (mfrCodes != null && mfrCodes.size() > 0) {
			// criteria.setQuoteNumber(Arrays.asList("CQ0072940", "CQ0146914"));
			List<String> stage = new ArrayList<String>();
			stage.add(QuoteSBConstant.QUOTE_STAGE_PENDING);
			criteria.setStage(stage);
			List<BizUnit> bizUnits = new ArrayList<BizUnit>();
			bizUnits.add(user.getCurrenBizUnit());
			criteria.setBizUnits(bizUnits);
			criteria.setDataAccesses(user.getDataAccesses());
			criteria.setSalsCostAccessFlag(user.isSalsCostAccessFlag());
			Long start1 = System.currentTimeMillis();
			// need to use current thread copy. may
			MyQuoteSearchCriteria criteriaCopy = criteria.deepClone();
			// a dialog need mfrList. so it is done at that time.
			exc.execute(() -> initMfrSearchSelectItem(criteriaCopy));

			LOGGER.info("create filter finished, took " + (System.currentTimeMillis() - start1) + " ms.");
			criteria.setWorkingPlatFormSearchCriteria(workingPlatFormSearchCriteria);
			workingPlatformItems.startPagination();
			RequestContext.getCurrentInstance().execute("rfqCount();");

		} else {
			searchedWorkingPlatformItems = new ArrayList<WorkingPlatformItemVO>();
			searchedWorkingPlatformItems = null;
		}
		editMode = false;
		// clear the cache
		this.workingPlatformItems.getCacheModifiedItems().clear();
		// this.workingPlatformItems.cellChangeListener(String.valueOf(item.getQuoteItem().getId()));
		// logger.log(Level.INFO, "PERFORMANCE END - refreshQuoteTable()");

		LOGGER.info("refresh quote table finished, takes " + (System.currentTimeMillis() - start) + " ms, user id:"
				+ user.getEmployeeId() + ", region:" + user.getDefaultBizUnit().getName());
	}

	private void logWarning(String param, String msg, Throwable e) {
		LOGGER.log(Level.WARNING,
				"Exception in adding in " + param + "'s value " + msg + " , exception message : " + e.getMessage());
	}

	private WorkingPlatformItemVO rfqStatusCount(WorkingPlatformItemVO item) {
		if (!filterMfrOptionList.contains(item.getQuoteItem().getRequestedMfr().getName())) {
			try {
				if (!QuoteUtil.isEmpty(item.getQuoteItem().getRequestedMfr().getName()))
					filterMfrOptionList.add(item.getQuoteItem().getRequestedMfr().getName());
			} catch (Exception ex) {
				logWarning("filterMfrOptionList", item.getQuoteItem().getRequestedMfr().getName(), ex);
			}
		}
		if (item.getQuoteItem().getQuote().getSales().getTeam() != null
				&& !filterTeamOptionList.contains(item.getQuoteItem().getQuote().getSales().getTeam().getName())) {
			try {
				if (!QuoteUtil.isEmpty(item.getQuoteItem().getQuote().getSales().getTeam().getName()))
					filterTeamOptionList.add(item.getQuoteItem().getQuote().getSales().getTeam().getName());
			} catch (Exception ex) {
				logWarning("filterTeamOptionList", item.getQuoteItem().getQuote().getSales().getTeam().getName(), ex);
			}
		}
		if (!filterCustomerOptionList.contains(item.getQuoteItem().getQuote().getSoldToCustomerName())) {
			try {
				if (!QuoteUtil.isEmpty(item.getQuoteItem().getQuote().getSoldToCustomerName()))
					filterCustomerOptionList.add(item.getQuoteItem().getQuote().getSoldToCustomerName());
			} catch (Exception ex) {
				logWarning("filterCustomerOptionList", item.getQuoteItem().getQuote().getSoldToCustomerName(), ex);
			}
		}
		try {
			if (item.getQuoteItem().getQuoteItemDesign() != null) {
				filterBmtFlagOptionList.put(item.getQuoteItem().getQuoteItemDesign().getBmtFlag().getDescription(),
						item.getQuoteItem().getQuoteItemDesign().getBmtFlag().getBmtFlagCode());
			}
		} catch (Exception ex) {
			logWarning("filterBmtFlagOptionList",
					item.getQuoteItem().getQuoteItemDesign().getBmtFlag().getBmtFlagCode(), ex);
		}
		return item;
	}

	public void rfqStatusCount() {

		filterMfrOptionList = new ArrayList<String>();
		filterTeamOptionList = new ArrayList<String>();
		filterCustomerOptionList = new ArrayList<String>();
		filterBmtFlagOptionList = new TreeMap<String, String>(new MapKeyComparator());

		if (this.searchedWorkingPlatformItems != null) {
			for (WorkingPlatformItemVO item : searchedWorkingPlatformItems) {
				if (item.getQuoteItem() != null && item.getQuoteItem().getStatus() != null) {
					item = rfqStatusCount(item);
					// item = getRFQQuoteItem(item);
				}
			}
		} else {
			for (WorkingPlatformItemVO item : searchedWorkingPlatformItems) {
				if (item.getQuoteItem() != null && item.getQuoteItem().getStatus() != null) {
					item = rfqStatusCount(item);
					// item = getRFQQuoteItem(item);
				}
			}

		}
		this.qcCounter = getQcCounter();
		this.itCounter = getItCounter();
		this.sqCounter = getSqCounter();
		this.ritCounter = getRitCounter();
		this.rbitCounter = getRbitCounter();
		this.bitCounter = getBitCounter();
		this.bqCounter = getBqCounter();
		this.rbqCounter = getRbqCounter();
		FacesUtil.updateUI("form_pendinglist:counter");

	}

	public void updateVendorRfqStatus() {

		// logger.log(Level.INFO, "PERFORMANCE START -
		// updateVendorRfqStatus()");
		try {
			doKeepFirstRow();

			Date lastSqUpdateTime = QuoteUtil.getCurrentTime();

			if (getCacheSelectionDataList() != null) {
				List<QuoteItem> quoteItems = new ArrayList<QuoteItem>();
				for (WorkingPlatformItemVO item : getCacheSelectionDataList()) {
					LOGGER.log(Level.INFO, UserInfo.getUser().getEmployeeId() + " Vendor System Quote#: "
							+ item.getQuoteItem().getQuoteNumber());
					item.getQuoteItem().setStatus(QuoteSBConstant.RFQ_STATUS_SQ);
					item.setStatusStr(QuoteSBConstant.RFQ_STATUS_SQ);
					item.getQuoteItem().setLastSqUpdateTime(lastSqUpdateTime);
					quoteItems.add(item.getQuoteItem());
				}
				if (selectedQuoteItem != null) {
					selectedQuoteItem.setStatusStr(QuoteSBConstant.RFQ_STATUS_SQ);
				}
				setLastUpdateFields(quoteItems);
				// quoteTransactionSB.updateQuoteItem(quoteItems);
				this.updateQuoteItem(quoteItems, ActionEnum.WP_VENDOR_SYSTEM);
				// changed By Lenon 2017/03/07 Fix Ticket ICN0671813
				for (int i = 0; i < getCacheSelectionDataList().size(); i++) {
					getCacheSelectionDataList().get(i).setQuoteItem(quoteItems.get(i));
				}
				// rfqStatusCount();
				selectedWorkingPlatformItems = new ArrayList<WorkingPlatformItemVO>();
				workingPlatformItems.clearCacheSelectionItems();

				// updated by DamonChen@20180408
				// refreshQuoteTable();
				// Fixed by DamonChen@20180427
				workingPlatformItems.setRequery(true);
				FacesUtil.updateUI("form_pendinglist:datatable_pendinglist");
			}
		} catch (Exception ex) {
			LOGGER.log(Level.WARNING, "updateVendorRfqStatus encounter error:", ex);

		}
		// logger.log(Level.INFO, "PERFORMANCE END - updateVendorRfqStatus()");
	}

	public void updateItToQcRfqStatus() {

		// logger.log(Level.INFO, "PERFORMANCE START -
		// updateVendorRfqStatus()");
		try {
			doKeepFirstRow();

			Date lastSqUpdateTime = QuoteUtil.getCurrentTime();

			if (getCacheSelectionDataList() != null) {
				List<QuoteItem> quoteItems = new ArrayList<QuoteItem>();
				for (WorkingPlatformItemVO item : getCacheSelectionDataList()) {
					LOGGER.log(Level.INFO, UserInfo.getUser().getEmployeeId() + "IT TO QC: Quote# "
							+ item.getQuoteItem().getQuoteNumber());
					item.getQuoteItem().setStatus(QuoteSBConstant.RFQ_STATUS_QC);
					item.setStatusStr(QuoteSBConstant.RFQ_STATUS_QC);
					item.getQuoteItem().setLastSqUpdateTime(lastSqUpdateTime);
					item.getQuoteItem().setLastRitUpdateTime(lastSqUpdateTime);// bmt:
																				// 5.4.1
					quoteItems.add(item.getQuoteItem());
				}
				setLastUpdateFields(quoteItems);
				// quoteTransactionSB.updateQuoteItem(quoteItems);
				this.updateQuoteItem(quoteItems, ActionEnum.WP_IT_TO_QC);
				// rfqStatusCount();
				FacesUtil.updateUI("form_pendinglist:counter");
				selectedWorkingPlatformItems = new ArrayList<WorkingPlatformItemVO>();
				workingPlatformItems.clearCacheSelectionItems();
			}
		} catch (Exception ex) {
			LOGGER.log(Level.WARNING, "updateItToQcRfqStatus encounter error:", ex);
		}

		// logger.log(Level.INFO, "PERFORMANCE END - updateVendorRfqStatus()");
	}

	public void updateItToBmtRfqStatus() {
		try {
			doKeepFirstRow();
			// saveQuotation();

			Date lastSqUpdateTime = QuoteUtil.getCurrentTime();

			if (selectedItToBmtItems != null) {
				List<QuoteItem> quoteItems = new ArrayList<QuoteItem>();
				for (WorkingPlatformItemVO item : selectedItToBmtItems) {
					LOGGER.log(Level.INFO, UserInfo.getUser().getEmployeeId() + " Internal Transfer to BMT Quote#: "
							+ item.getQuoteItem().getQuoteNumber());

					item.getQuoteItem().setStatus(QuoteSBConstant.RFQ_STATUS_BIT);
					item.setStatusStr(QuoteSBConstant.RFQ_STATUS_BIT);
					item.getQuoteItem().setLastSqUpdateTime(lastSqUpdateTime);
					if (item.getQuoteItem().getQuoteItemDesign() == null) {
						QuoteItemDesign qid = new QuoteItemDesign();
						qid.setQuoteItem(item.getQuoteItem());
						qid.setLastUpdatedBy(UserInfo.getUser().getEmployeeId());
						qid.setLastUpdatedTime(new Date());
						qid.setCreatedBy(UserInfo.getUser().getEmployeeId());
						qid.setCreatedTime(new Date());
						BmtFlag b = new BmtFlag();
						b.setBmtFlagCode(BmtFlagEnum.EMPTY.code());
						qid.setBmtFlag(b);
						item.getQuoteItem().setQuoteItemDesign(qid);
					}
					quoteItems.add(item.getQuoteItem());
				}
				setLastUpdateFields(quoteItems);
				try {
					this.sendEmailAfterItToBmt(selectedItToBmtItems, UserInfo.getUser());
				} catch (Exception e) {
					FacesUtil.showWarnMessage(ResourceMB.getText("wq.button.ITbmt"),
							ResourceMB.getText("wq.label.sendEmailError") + ".");
					LOGGER.log(Level.SEVERE, UserInfo.getUser().getEmployeeId()
							+ " updateItToQcRfqStatus send email error: " + e.getMessage(), e);
					return;
				}
				//Use exception to throw message.
				this.updateQuoteItem(quoteItems, ActionEnum.WP_SEND_TO_BMT);
				selectedWorkingPlatformItems = new ArrayList<WorkingPlatformItemVO>();
				workingPlatformItems.clearCacheSelectionItems();
				// rfqStatusCount();
				FacesUtil.executeJS("PF('itToBmt_RFQ_dialog').hide()");
				// FacesUtil.showInfoMessage(MSG_MESSAGE_TITLE_IT_TO_BMT,
				// "Successful.");;//defect 65
				FacesUtil.updateUI("form_pendinglist_dialog:itToBmt_email_panel",
						"form_pendinglist_dialog:itToBmt_RFQ_dialog", "form_pendinglist:datatable_pendinglist",
						"form_pendinglist:counter");
			}
		} catch (Exception ex) {
			FacesUtil.showWarnMessage(ResourceMB.getText("wq.button.ITbmt"),
					ResourceMB.getText("wq.label.errorFound") + ".");
			LOGGER.log(Level.SEVERE, "updateItToQcRfqStatus encounter error: "
					+ MessageFormatorUtil.getParameterizedStringFromException(ex), ex);

		}
	}

	private List<String> findReceivers(QuoteItem item) {

		List<Object[]> dataAccessAndBizUnits = new ArrayList<Object[]>();

		Object[] dataAccessAndBizUnit = new Object[2];

		DataAccess dataAccess = new DataAccess();

		dataAccess.setManufacturer(item.getRequestedMfr());
		dataAccess.setProductGroup(item.getProductGroup2());
		if (item.getMaterialTypeId() != null) {
			dataAccess.setMaterialType(materialTypeSB.findByPK(item.getMaterialTypeId()));
		}
		if (item.getProgramType() != null) {
			dataAccess.setProgramType(programTypeSB.findByName(item.getProgramType()));
		}
		dataAccess.setTeam(item.getQuote().getTeam());

		dataAccessAndBizUnit[0] = dataAccess;
		dataAccessAndBizUnit[1] = item.getQuote().getBizUnit();

		dataAccessAndBizUnits.add(dataAccessAndBizUnit);

		List<Role> roles = new ArrayList<>();
		Role role = bmtQuoteSB.findRoleByName(QuoteSBConstant.ROLE_BMT);
		roles.add(role);
		List<User> receiverUsers = userSB.findByDataAccess(dataAccessAndBizUnits, roles);
		List<String> receivers = new ArrayList<String>();
		for (User u : receiverUsers) {
			receivers.add(u.getEmailAddress());
		}
		return receivers;
	}

	private void sendEmailAfterItToBmt(List<WorkingPlatformItemVO> workingPlatformItemVOs, User user)
			throws WebQuoteException {
		for (WorkingPlatformItemVO vo : workingPlatformItemVOs) {
			StringBuilder mailContent = new StringBuilder();

			mailContent.append("Dear BMT,<br/><br/>");
			mailContent.append("Remarks from QC: ").append(vo.getEmailMessage()).append("<br/><br/>");
			mailContent.append("This is an Internal Transfer RFQs.<br/><br/>");
			mailContent.append("Form Number: ").append(vo.getQuoteItem().getQuote().getFormNumber()).append("<br/>");
			mailContent.append("Quote Number: ").append(vo.getQuoteItem().getQuoteNumber()).append("<br/>");
			mailContent.append("End Customer: ").append(vo.getQuoteItem().getEndCustomerFullName()).append("<br/>");
			mailContent.append("Quoted Part Number: ").append(vo.getQuoteItem().getRequestedPartNumber())
					.append("<br/>");
			mailContent.append("BMT Flag: ")
					.append(vo.getQuoteItem().getQuoteItemDesign() != null
							? vo.getQuoteItem().getQuoteItemDesign().getBmtFlag().getDescription() : "")
					.append("<br/><br/><br/>");
			mailContent.append("Best Regards,<br/>");
			mailContent.append(sysMaintSB.getEmailSignName(bizUnit.getName()));
			mailContent.append("<br/>").append(sysMaintSB.getEmailHotLine(bizUnit.getName()));
			mailContent.append("<br/> " + "Email Box: ").append(sysMaintSB.getEmailSignContent(bizUnit.getName()));

			MailInfoBean mailInfoBean = new MailInfoBean();
			mailInfoBean.setMailContent(mailContent.toString());
			mailInfoBean.setMailSubject(vo.getRequestedMfr() + " - " + vo.getQuoteItem().getQuote().getFormNumber()
					+ " - " + ResourceMB.getText("wq.label.needYrAction"));

			mailInfoBean.setMailFrom(user.getEmailAddress());
			mailInfoBean.setMailFromInName(user.getName());
			// TODO: verify
			List<String> toAddress = findReceivers(vo.getQuoteItem());
			mailInfoBean.setMailTo(toAddress);
			try {
				mailUtilsSB.sendHtmlMail(mailInfoBean);
			} catch (Exception e) {
				LOGGER.log(Level.SEVERE,
						UserInfo.getUser().getEmployeeId() + " sendEmailAfterItToBmt error with address : "
								+ org.apache.commons.lang.StringUtils.join(toAddress, ','));
				try {
					mailUtilsSB.sendErrorEmail(
							"Wrong email address: " + org.apache.commons.lang.StringUtils.join(toAddress, ','),
							"Transfer to BMT error on QC working platform page");
				} catch (Exception e2) {
					LOGGER.log(Level.SEVERE,
							UserInfo.getUser().getEmployeeId() + "  send  error email error "
									+ org.apache.commons.lang.StringUtils.join(toAddress, ',') + ", Exception message: "
									+ MessageFormatorUtil.getParameterizedStringFromException(e2),
							e2);

				}
				throw new WebQuoteException(e);
			}
		}
	}

	public void updateInternalTransferWithRecipientChecking() {
		if (exceedRecipientList == null) {
			exceedRecipientList = new ArrayList<>();
		}
		exceedRecipientList.clear();
		for (WorkingPlatformEmailVO pvo : updateStatusItems) {
			if (pvo.getEmailsCc() != null && pvo.getEmailsCc().size() > QuoteConstant.ALERT_NUMBER_OF_RECIPIENT) {
				if (exceedRecipientList == null)
					exceedRecipientList = new ArrayList<String>();
				exceedRecipientList.add(ResourceMB.getParameterizedText("wq.label.recipientsTo",
						"" + QuoteConstant.ALERT_NUMBER_OF_RECIPIENT) + " " + pvo.getMfr());
			}
			if (pvo.getEmailsTo() != null && pvo.getEmailsTo().size() > QuoteConstant.ALERT_NUMBER_OF_RECIPIENT) {
				if (exceedRecipientList == null)
					exceedRecipientList = new ArrayList<String>();
				exceedRecipientList.add(ResourceMB.getParameterizedText("wq.label.recipientsCc",
						"" + QuoteConstant.ALERT_NUMBER_OF_RECIPIENT) + " " + pvo.getMfr());
			}
		}
		if (exceedRecipientList != null && exceedRecipientList.size() > 0) {
			RequestContext context = RequestContext.getCurrentInstance();
			context.addCallbackParam("exceedRecipient", "1");
			return;
		}
		updateInternalTransfer();
		if (exceedRecipientList.size() <= 0) {
			exceedRecipientList = null;
		}
	}

	public void updateInternalTransfer() {

		// logger.log(Level.INFO, "PERFORMANCE START -
		// updateInternalTransfer()");

		if (updateStatusItems != null) {
			for (int i = 0; i < updateStatusItems.size(); i++) {
				WorkingPlatformEmailVO workingPlatformEmailVO = updateStatusItems.get(i);
				String productLine = workingPlatformEmailVO.getMfr();
				String content = "Dear PM,<br/><br/>";

				if (!QuoteUtil.isEmpty(workingPlatformEmailVO.getMessage())) {
					content += "Remarks from Quote Centre : ";
					content += workingPlatformEmailVO.getMessage();
					content += "<br/><br/>";
				}

				content += "This is an Internal Transfer RFQs. Please provide cost, suggested Resale and related quoting information for this RFQs.<br/>";
				content += "Please click into the link(s) for your response<br/><br/>";
				content += "Customer: ";
				for (String customer : workingPlatformEmailVO.getCustomers()) {
					content += customer;
				}
				content += "<br/>";
				content += "Project: ";
				for (String project : workingPlatformEmailVO.getProjects()) {
					content += project;
				}
				content += "<br/><br/>";
				content += "This RFQs:<br/>";

				if (workingPlatformEmailVO.getQuoteItems() != null) {
					for (QuoteItem item : workingPlatformEmailVO.getQuoteItems()) {
						content += item.getRequestedPartNumber();
						content += ", ";
						content = content + super.getUrl() + "/RFQ/ResponseInternalTransfer.jsf?quoteNumber="
								+ item.getQuoteNumber();
						content += "</br>";
					}
				}
				content += "<br/>";
				content += "Your RFQs Pending List" + ":</br>";
				content = content + "Your RFQ" + ": " + super.getUrl() + "/RFQ/ResponseInternalTransfer.jsf";
				content += "<br/><br/>";

				List<String> emailTos = workingPlatformEmailVO.getEmailsTo();
				List<String> emailCcs = workingPlatformEmailVO.getEmailsCc();
				// String[] emailArr = emails.split(",");
				List<String> emailToList = new ArrayList<String>();
				if (emailTos != null && emailTos.size() > 0) {
					List<User> users = userSB.findByEmployeeIds(emailTos);
					for (User user : users) {
						emailToList.add(user.getEmailAddress());
					}
				}
				List<String> emailCcList = new ArrayList<String>();
				if (emailCcs != null && emailCcs.size() > 0) {
					List<User> users = userSB.findByEmployeeIds(emailCcs);
					for (User user : users) {
						emailCcList.add(user.getEmailAddress());
					}
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

				// get bcc information
				List<String> bccEmails = new ArrayList<String>();
				bccEmails.add(sysMaintSB.getEmailAddress(bizUnit.getName()));
				bccEmails.add(user.getEmailAddress());
				mailInfoBean.setMailBcc(bccEmails);

				mailInfoBean.setMailTo(emailToList);
				mailInfoBean.setMailCc(emailCcList);

				content += "Best Regards" + "<br/>";
				content += sysMaintSB.getEmailSignName(bizUnit.getName()) + "<br/>"
						+ sysMaintSB.getEmailHotLine(bizUnit.getName()) + "<br/>" + "Email Box : "
						+ sysMaintSB.getEmailSignContent(bizUnit.getName());

				mailInfoBean.setMailContent(content);

				try {
					mailUtilsSB.sendHtmlMail(mailInfoBean);
				} catch (Exception e) {
					LOGGER.log(Level.SEVERE, "Exception while sending mail from:" + mailInfoBean.getMailFrom()
							+ ", Subject: " + mailInfoBean.getMailSubject() + "Email Sending Error : " + e.getMessage(),
							e);
				}

			}

			Date lastItUpdateTime = QuoteUtil.getCurrentTime();
			List<QuoteItem> quoteItems = new ArrayList<QuoteItem>();
			for (WorkingPlatformItemVO item : getCacheSelectionDataList()) {
				LOGGER.log(Level.INFO, UserInfo.getUser().getEmployeeId() + " Internal Transfer to PM Quote#:"
						+ item.getQuoteItem().getQuoteNumber());
				item.getQuoteItem().setStatus(QuoteSBConstant.RFQ_STATUS_IT);
				item.setStatusStr(QuoteSBConstant.RFQ_STATUS_IT);
				item.getQuoteItem().setLastItUpdateTime(lastItUpdateTime);

				// item.getQuoteItem().setCost(null);
				for (int m = 0; m < updateStatusItems.size(); m++) {
					WorkingPlatformEmailVO workingPlatformEmailVO = updateStatusItems.get(m);
					if (workingPlatformEmailVO != null) {
						for (QuoteItem quoteItem : workingPlatformEmailVO.getQuoteItems()) {
							if (quoteItem.getQuoteNumber().equals(item.getQuoteItem().getQuoteNumber())) {
								List<Attachment> attaches = workingPlatformEmailVO.getAttachments();
								if (attaches != null) {
									List<Attachment> as = new ArrayList<Attachment>();
									for (Attachment attach : attaches) {
										Attachment a = new Attachment();
										a.setFileImage(attach.getFileImage());
										a.setFileName(attach.getFileName());
										a.setType(attach.getType());
										a.setQuoteItem(quoteItem);
										as.add(a);
									}
									item.getQuoteItem().setAttachments(as);
								}
							}
						}
					}
				}
				quoteItems.add(item.getQuoteItem());
			}
			setLastUpdateFields(quoteItems);
			// quoteTransactionSB.updateQuoteItem(quoteItems);
			// List<String> result =
			this.updateQuoteItem(quoteItems, ActionEnum.WP_SEND_TO_PM);
			// rfqStatusCount();
			// defect 65
			// if(result != null && quoteItems != null && quoteItems.size() ==
			// result.size()){
			// FacesUtil.showInfoMessage("Internal Transfer to PM",
			// "successfully");
			// }
			selectedWorkingPlatformItems = new ArrayList<WorkingPlatformItemVO>();
			workingPlatformItems.clearCacheSelectionItems();
		}

		// logger.log(Level.INFO, "PERFORMANCE END - updateInternalTransfer()");
	}

	public void searchForPricerPopup(ActionEvent event) {
		WorkingPlatformItemVO item = (WorkingPlatformItemVO) event.getComponent().getAttributes()
				.get("targetQuoteItem");
		// item.getQuoteItem().setAqcc(item.getAqccStr());
		item.fillBackQuoteItem();
		seletedItem = item;
		selectedPricerInfoInPricerPopup = null;
		searchMaterialDetailsCount = 0;
		if (item != null) {
			PricerInfo pricerInfo = item.createPricerInfo();
			pricerInfosInPricerPopup = materialSB.searchForPricerPopup(pricerInfo);
		}
	}

	public void checkRfqStatus(AjaxBehaviorEvent event) {
		WorkingPlatformItemVO item = (WorkingPlatformItemVO) event.getComponent().getAttributes()
				.get("targetQuoteItem");
		if (!item.getQuoteItem().getStatus().equals(item.getStatusStr())) {
			item.changed = true;
			if (item.getStatusStr().equals(QuoteSBConstant.RFQ_STATUS_IT)) {
				FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, QuoteConstant.MESSAGE_NOT_SAVED,
						ResourceMB.getText("wq.label.clickButtonInternalTransfer") + ".");
				FacesContext.getCurrentInstance().addMessage("workingPlatformGrowl", msg);
				item.setStatusStr(item.getQuoteItem().getStatus());
			} else if (item.getStatusStr().equals(QuoteSBConstant.RFQ_STATUS_RIT)) {
				FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, QuoteConstant.MESSAGE_NOT_SAVED,
						ResourceMB.getText("wq.label.pmChangeStatusRIT") + ".");
				FacesContext.getCurrentInstance().addMessage("workingPlatformGrowl", msg);
				item.setStatusStr(item.getQuoteItem().getStatus());
			} else if (item.getQuoteItem().getStatus().equals(QuoteSBConstant.RFQ_STATUS_QC)) {
				if (item.getStatusStr().equals(QuoteSBConstant.RFQ_STATUS_RIT)) {
					FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, QuoteConstant.MESSAGE_NOT_SAVED,
							ResourceMB.getText("wq.label.qcNotChangeTORIT") + ".");
					FacesContext.getCurrentInstance().addMessage("workingPlatformGrowl", msg);
					item.setStatusStr(item.getQuoteItem().getStatus());
				}
			} else if (item.getQuoteItem().getStatus().equals(QuoteSBConstant.RFQ_STATUS_RIT)) {
				if (item.getStatusStr().equals(QuoteSBConstant.RFQ_STATUS_QC)) {
					FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, QuoteConstant.MESSAGE_NOT_SAVED,
							ResourceMB.getText("wq.label.ritNotChangeTOQC") + ".");
					FacesContext.getCurrentInstance().addMessage("workingPlatformGrowl", msg);
					item.setStatusStr(item.getQuoteItem().getStatus());
				}
			} else if (item.getQuoteItem().getStatus().equals(QuoteSBConstant.RFQ_STATUS_SQ)) {
				if (item.getStatusStr().equals(QuoteSBConstant.RFQ_STATUS_QC)) {
					FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, QuoteConstant.MESSAGE_NOT_SAVED,
							ResourceMB.getText("wq.label.sqNotChangeQC") + ".");
					FacesContext.getCurrentInstance().addMessage("workingPlatformGrowl", msg);
					item.setStatusStr(item.getQuoteItem().getStatus());
				} else if (item.getStatusStr().equals(QuoteSBConstant.RFQ_STATUS_RIT)) {
					FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, QuoteConstant.MESSAGE_NOT_SAVED,
							ResourceMB.getText("wq.label.sqtChangeRIT") + ".");
					FacesContext.getCurrentInstance().addMessage("workingPlatformGrowl", msg);
					item.setStatusStr(item.getQuoteItem().getStatus());
				}
			}
		}
	}

	public void updateSelectedQuoteItem(ActionEvent event) {
		WorkingPlatformItemVO item = (WorkingPlatformItemVO) event.getComponent().getAttributes()
				.get("targetQuoteItem");
		rowIndex = (Integer) event.getComponent().getAttributes().get("targetRowIndex");

		if (editMode && this.selectedQuoteItem.changed) {
			FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, QuoteConstant.MESSAGE_NOT_SAVED,
					this.selectedQuoteItem.getQuoteItem().getQuoteNumber()
							+ ResourceMB.getText("wq.label.notSavedInRFQDetailPanel"));
			FacesContext.getCurrentInstance().addMessage("workingPlatformGrowl", msg);
		} else {

			this.displayExternalComment = item.getAqccStr();
			this.displayInternalComment = item.getInternalCommentStr();
			this.displayTermsAndConditions = item.getTermsAndConditionsStr();
			this.displayPmComment = item.getQuoteItem().getInternalTransferComment();

			if (selectedQuoteItem == null || (selectedQuoteItem != null && item != null
					&& selectedQuoteItem.getQuoteItem().getId() != item.getQuoteItem().getId())) {
				this.selectedQuoteItem = (WorkingPlatformItemVO) event.getComponent().getAttributes()
						.get("targetQuoteItem");
				;
				// updateFreeStock(this.selectedQuoteItem.getQuoteItem().getQuotedMaterial());

				this.editMode = false;
				this.showAllCustomerPos = false;

				// posStatisticsByPartAndCustomer(item);

				freeStockMfrSearch = this.selectedQuoteItem.getQuotedMfr();
				freeStockPartNumberSearch = this.selectedQuoteItem.getQuotedPartNumber();
				posHistoryMfrSearch = this.selectedQuoteItem.getQuotedMfr();
				posHistoryPartNumberSearch = this.selectedQuoteItem.getQuotedPartNumber();
				posHistorySoldToCustomerNameSearch = getCustomerFullName(
						this.selectedQuoteItem.getQuoteItem().getSoldToCustomer());
				quoteHistoryMfrSearch = this.selectedQuoteItem.getQuotedMfr();
				quoteHistoryPartNumberSearch = this.selectedQuoteItem.getQuotedPartNumber();
				quoteHistorySoldToCustomerNameSearch = getCustomerFullName(
						this.selectedQuoteItem.getQuoteItem().getSoldToCustomer());
				vendorQuoteHistoryMfrSearch = this.selectedQuoteItem.getQuotedMfr();
				vendorQuoteHistoryPartNumberSearch = this.selectedQuoteItem.getQuotedPartNumber();
				vendorQuoteHistorySoldToCustomerNameSearch = getCustomerFullName(
						this.selectedQuoteItem.getQuoteItem().getSoldToCustomer());

				vendorQuotationHistorys = null;
				if (quotationHistorys != null) {
					quotationHistorys.clear();
					;
				}

				RequestContext context = RequestContext.getCurrentInstance();

				context.execute("PF('datatable_pendinglist_var').unselectAllRows();");
				context.execute("PF('datatable_pendinglist_var').selectRow(" + rowIndex + ");");

				context.update("ap_rfqdetailreference:form_rfqdetailreference:panelgrid_rfqdetailreference");
				// context.update("form_pos:datatable_posHistoryList");
				context.update("form_quotationHistory:quoteHistorySearch");
				context.update("form_systemQuotation:vendorQuoteHistorySearch");
				context.update("form_freeStock:freeStockSearch");
				context.update("form_pos:posHistorySearch");

			}
		}
	}

	public void onRowUnselect(UnselectEvent event) {
		workingPlatformItems.onRowUnselect(event);
	}

	/**
	 * BMT SDD: 5.4.2.1
	 * 
	 * @param event
	 */
	public void onRowSelectForBmtItems(SelectEvent event) {
		WorkingPlatformItemVO vo = (WorkingPlatformItemVO) event.getObject();
		if (vo != null) {
			if (vo.disabledSelectionFromItToBmt()) {
				selectedItToBmtItems.remove(vo);
			}
		}
	}

	public void rowSelectupdate(SelectEvent event) {
		if (this.editMode && selectedQuoteItem.isChanged()) {
			FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, QuoteConstant.MESSAGE_NOT_SAVED,
					this.selectedQuoteItem.getQuoteItem().getQuoteNumber() + " "
							+ ResourceMB.getText("wq.label.notSavedInRFQDetailPanel"));
			FacesContext.getCurrentInstance().addMessage("workingPlatformGrowl", msg);
		} else {

			if (getCacheSelectionDataList() != null && getCacheSelectionDataList().size() > 0) {
				RequestContext context = RequestContext.getCurrentInstance();
				WorkingPlatformItemVO item = null;
				// Pagination: Changes done for WQ 982
				if (getCacheSelectionDataList().size() == 1) {
					item = getCacheSelectionDataList().get(0);
				} else if (getCacheSelectionDataList().size() > 1) {

					// added by damonChen for fixing that can't show the correct
					// quote in RFQ Details Panel on WP page
					WorkingPlatformItemVO vo = (WorkingPlatformItemVO) event.getObject();
					if (vo != null) {
						item = vo;
					}
					// end by damon
				}
				// Pagination: Changes ends
				if (selectedQuoteItem == null || (selectedQuoteItem != null && item != null
						&& selectedQuoteItem.getQuoteItem().getId() != item.getQuoteItem().getId())) {
					this.selectedQuoteItem = item;

					List<WorkingPlatformItemVO> list = searchedWorkingPlatformItems != null ? 
							searchedWorkingPlatformItems : getCacheSelectionDataList();
					rowIndex = list.indexOf(selectedQuoteItem);

					// update now to speed up process, update again at the end
					// of the code
					// context.update("ap_rfqdetailreference:form_rfqdetailreference:panelgrid_rfqdetailreference");

					// updateFreeStock(this.selectedQuoteItem.getQuoteItem().getQuotedMaterial());
					this.editMode = false;
					this.showAllCustomerPos = false;

					// posStatisticsByPartAndCustomer(this.selectedQuoteItem);

					freeStockMfrSearch = this.selectedQuoteItem.getQuotedMfr();
					freeStockPartNumberSearch = this.selectedQuoteItem.getQuotedPartNumber();
					posHistoryMfrSearch = this.selectedQuoteItem.getQuotedMfr();
					posHistoryPartNumberSearch = this.selectedQuoteItem.getQuotedPartNumber();
					posHistorySoldToCustomerNameSearch = this.selectedQuoteItem.getQuoteItem().getSoldToCustomer()
							.getCustomerName1();
					quoteHistoryMfrSearch = this.selectedQuoteItem.getQuotedMfr();
					quoteHistoryPartNumberSearch = this.selectedQuoteItem.getQuotedPartNumber();
					quoteHistorySoldToCustomerNameSearch = this.selectedQuoteItem.getQuoteItem().getQuote()
							.getSoldToCustomerName();
					vendorQuoteHistoryMfrSearch = this.selectedQuoteItem.getQuotedMfr();
					vendorQuoteHistoryPartNumberSearch = this.selectedQuoteItem.getQuotedPartNumber();
					vendorQuoteHistorySoldToCustomerNameSearch = getCustomerFullName(
							this.selectedQuoteItem.getQuoteItem().getSoldToCustomer());

					this.displayExternalComment = this.selectedQuoteItem.getAqccStr();
					this.displayInternalComment = this.selectedQuoteItem.getInternalCommentStr();
					this.displayTermsAndConditions = this.selectedQuoteItem.getTermsAndConditionsStr();
					this.displayPmComment = this.selectedQuoteItem.getQuoteItem().getInternalTransferComment();

					vendorQuotationHistorys = null;
					if (quotationHistorys != null) {
						quotationHistorys.clear();
					}

					// context.execute("datatable_pendinglist_var.unselectAllRows();");
					// context.execute("datatable_pendinglist_var.selectRow("+rowIndex+");");

					context.update("ap_rfqdetailreference:form_rfqdetailreference:panelgrid_rfqdetailreference");
					// context.update("form_pos:datatable_posHistoryList");
					context.update("form_quotationHistory:quoteHistorySearch");
					context.update("form_systemQuotation:vendorQuoteHistorySearch");
					context.update("form_freeStock:freeStockSearch");
					context.update("form_pos:posHistorySearch");
				}
			}
		}
		// BMT: 5.4.1 > 2.1
		if (event.getObject() != null && ((WorkingPlatformItemVO) event.getObject()).isDisableEditable()) {
			getCacheSelectionDataList().remove((WorkingPlatformItemVO) event.getObject());
			// FacesUtil.executeJS("datatable_pendinglist_var.unselectRow("+rowIndex+");");
			FacesUtil.updateUI("form_pendinglist:datatable_pendinglist");
		}
	}

	/*public void submitQcExternalComment() {
		WorkingPlatformItemVO item = null;
		if (this.searchedWorkingPlatformItems != null) {
			item = this.searchedWorkingPlatformItems.get(rowIndex);
		} else {
			item = this.searchedWorkingPlatformItems.get(rowIndex);
		}
		item.setAqccStr(displayExternalComment);
	}

	public void submitQcInternalComment() {
		WorkingPlatformItemVO item = null;
		if (this.searchedWorkingPlatformItems != null) {
			item = this.searchedWorkingPlatformItems.get(rowIndex);
		} else {
			item = this.searchedWorkingPlatformItems.get(rowIndex);
		}
		item.setInternalCommentStr(displayInternalComment);
	}

	public void submitTermsAndConditions() {
		WorkingPlatformItemVO item = null;
		if (this.searchedWorkingPlatformItems != null) {
			item = this.searchedWorkingPlatformItems.get(rowIndex);
		} else {
			item = this.searchedWorkingPlatformItems.get(rowIndex);
		}
		item.setTermsAndConditionsStr(displayTermsAndConditions);
	}*/

	public void updateSelectedQcExternalComment() {
		displayExternalComment = this.selectedQcExternalComment.getName();
	}

	public void updateSelectedQcInternalComment() {
		displayInternalComment = this.selectedQcInternalComment.getName();
	}

	public void updateSelectedTermsAndConditions() {
		displayTermsAndConditions = this.selectedTermsAndConditions.getName();
	}

	public void updateSelectedDetailQcExternalComment() {
		this.selectedQuoteItem.setAqccStr(this.selectedDetailQcExternalComment.getName());
		editMode = true;
	}

	public void updateSelectedDetailQcInternalComment() {
		this.selectedQuoteItem.setInternalCommentStr(this.selectedDetailQcInternalComment.getName());
		editMode = true;
	}

	public void updateSelectedDetailTermsAndConditions() {
		this.selectedQuoteItem.setTermsAndConditionsStr(this.selectedDetailTermsAndConditions.getName());
		editMode = true;
	}

	public void updateCost(AjaxBehaviorEvent event) {

		// logger.log(Level.INFO, "PERFORMANCE START - updateCost(int
		// rowIndex)");
		WorkingPlatformItemVO item = (WorkingPlatformItemVO) event.getComponent().getAttributes()
				.get("targetQuoteItem");
		this.workingPlatformItems.addChangeItem(String.valueOf(item.getQuoteItem().getId()));
		if (!QuoteUtil.isEmpty(item.getCostStr()) && !QuoteUtil.isEmpty(item.getTargetResaleStr_buy())) {
			try {
				item.setTargetMarginStr(null);
				Double targetResale = Double.parseDouble(item.getTargetResaleStr_buy());
				Double cost = Double.parseDouble(item.getCostStr());
				if(targetResale !=null && targetResale != 0d) {
					Double targetMargin = 100 * (targetResale - cost) / targetResale;
					item.setTargetMarginStr(df2.format(targetMargin) + "%");
				}
			} catch (Exception ex) {
				LOGGER.log(Level.WARNING,
						"Error occured while updating cost for Cost Indicators: " + item.getCostIndicatorStr()
								+ ", MPQ: " + item.getMpqStr() + ", " + "Quotation Effective Date: "
								+ item.getQuotationEffectiveDateStr() + ", Reason for failure: "
								+ MessageFormatorUtil.getParameterizedStringFromException(ex),
						ex);
			}
		}

		if (!QuoteUtil.isEmpty(item.getCostStr()) && !QuoteUtil.isEmpty(item.getQuotedPriceStr())) {
			try {
				item.setResaleMarginStr(null);
				Double quotedPrice = Double.parseDouble(item.getQuotedPriceStr());
				Double cost = Double.parseDouble(item.getCostStr());
				if(quotedPrice !=null && quotedPrice != 0d) {
					Double resaleMargin = 100 * (quotedPrice - cost) / quotedPrice;
					item.setResaleMarginStr(df2.format(resaleMargin) + "%");
				}
				
			} catch (Exception ex) {
				LOGGER.log(Level.WARNING,
						"Error occured while updating cost for Cost Indicators: " + item.getCostIndicatorStr()
								+ ", MPQ: " + item.getMpqStr() + ", " + "Quotation Effective Date: "
								+ item.getQuotationEffectiveDateStr() + ", Reason for failure: "
								+ MessageFormatorUtil.getParameterizedStringFromException(ex),
						ex);
			}
		} else if (!QuoteUtil.isEmpty(item.getCostStr()) && !QuoteUtil.isEmpty(item.getResaleMarginStr())) {
			try {
				String tmp = item.getResaleMarginStr();
				if (item.getResaleMarginStr().endsWith("%")) {
					tmp = item.getResaleMarginStr().substring(0, item.getResaleMarginStr().length() - 1);
				}
				Double resaleMargin = Double.parseDouble(tmp);
				Double cost = Double.parseDouble(item.getCostStr());

				Double quotedPrice = cost / (1 - (resaleMargin / 100));
				item.setResaleMarginStr(df5.format(quotedPrice));
			} catch (Exception ex) {
				LOGGER.log(Level.WARNING,
						"Error occured while updating cost for Cost Indicators: " + item.getCostIndicatorStr()
								+ ", MPQ: " + item.getMpqStr() + ", " + "Quotation Effective Date: "
								+ item.getQuotationEffectiveDateStr() + ", Reason for failure: "
								+ MessageFormatorUtil.getParameterizedStringFromException(ex),
						ex);
			}
		}
		// logger.log(Level.INFO, "PERFORMANCE END - updateCost(int rowIndex)");

	}

	public void triggerChangeFromTab(AjaxBehaviorEvent event) {
		WorkingPlatformItemVO item = (WorkingPlatformItemVO) event.getComponent().getAttributes()
				.get("targetQuoteItem");
		this.workingPlatformItems.addChangeItem(String.valueOf(item.getQuoteItem().getId()));
	}

	public void updateVoChange(AjaxBehaviorEvent event) {
		WorkingPlatformItemVO item = (WorkingPlatformItemVO) event.getComponent().getAttributes()
				.get("targetQuoteItem");
		// this.workingPlatformItems.cellChangeListener(String.valueOf(item.getQuoteItem().getId()));
		// added for Defect 128, users complaint that in QC Working Platform, it
		// always clear the input value,by DamonChen@20180308
		this.workingPlatformItems.addChangeItem(String.valueOf(item.getQuoteItem().getId()));
		item.setChanged(true);
	}

	public void updateQcExternalComment(AjaxBehaviorEvent event) {
		WorkingPlatformItemVO item = (WorkingPlatformItemVO) event.getComponent().getAttributes()
				.get("targetQuoteItem");
		item.getQuoteItem().setAqcc(item.getAqccStr());
		this.workingPlatformItems.cellChangeListener(String.valueOf(item.getQuoteItem().getId()));
	}

	public void updateQuotedPrice(AjaxBehaviorEvent event) {
		WorkingPlatformItemVO item = (WorkingPlatformItemVO) event.getComponent().getAttributes()
				.get("targetQuoteItem");
		this.workingPlatformItems.addChangeItem(String.valueOf(item.getQuoteItem().getId()));
		if (item != null) {
			try {
				// added control logic by DamonChen@20180416, will throw out
				// NumberFormatException if QuotedPrice is empty.
				if (!QuoteUtil.isEmpty(item.getQuotedPriceStr())) {
					if (!QuoteUtil.isEmpty(item.getCostStr())) {
						item.setResaleMarginStr(null);
						Double cost = Double.parseDouble(item.getCostStr());
						Double quotedPrice = Double.parseDouble(item.getQuotedPriceStr());

						Double resaleMargin = 100 * (quotedPrice - cost) / quotedPrice;
						item.setResaleMarginStr(df2.format(resaleMargin) + "%");

					} else if (!QuoteUtil.isEmpty(item.getResaleMarginStr())) {

						Double resaleMargin = null;
						item.setCostStr(null);
						String tmpMargin = item.getResaleMarginStr();
						if (item.getResaleMarginStr().endsWith("%")) {
							tmpMargin = item.getResaleMarginStr().substring(0, item.getResaleMarginStr().length() - 1);
						}
						resaleMargin = Double.parseDouble(tmpMargin);
						Double quotedPrice = Double.parseDouble(item.getQuotedPriceStr());

						Double cost = quotedPrice * (1 - (resaleMargin / 100));
						item.setCostStr(df5.format(cost));
					}
					item.setSalesCostTypeName(SalesCostType.NonSC.name());
					item.setSalesCostStr(null);
					item.setSuggestedResaleStr(null);
				}
			} catch (Exception ex) {
				LOGGER.log(Level.WARNING,
						"Error occured in updating quoted price for Quoted MFR: " + item.getQuotedMfr() + ", "
								+ "Reason for failure: " + MessageFormatorUtil.getParameterizedStringFromException(ex),
						ex);
			}
		}
		// logger.log(Level.INFO, "PERFORMANCE END - updateQuotedPrice(int
		// rowIndex)");
	}

	public void updateSalesCosteType(AjaxBehaviorEvent event) {
		WorkingPlatformItemVO item = (WorkingPlatformItemVO) event.getComponent().getAttributes()
				.get("targetQuoteItem");
		LOGGER.log(Level.INFO, "PERFORMANCE START - updateSalesCosteType()" + item.getQuoteItem().getQuoteNumber());

		if (item != null) {
			this.workingPlatformItems.cellChangeListener(String.valueOf(item.getQuoteItem().getId()));
			if (!QuoteUtil.isEmpty(item.getSalesCostTypeName())) {
				// changed for defect#271 by DamonChen@20180417
				item.setQuotedPriceStr(null);
				item.setResaleMarginStr(null);
				if (item.getSalesCostTypeName() == null
						|| SalesCostType.NonSC.name().equals(item.getSalesCostTypeName())) {
					item.setSalesCostStr(null);
					item.setSuggestedResaleStr(null);
					item.setDisableEditableForSalesCost(false);
				} else {
					item.setDisableEditableForSalesCost(true);
				}
			}
		}

	}

	public void updateQuotedMargin(AjaxBehaviorEvent event) {

		// logger.log(Level.INFO, "PERFORMANCE START - updateQuotedMargin()");
		WorkingPlatformItemVO item = (WorkingPlatformItemVO) event.getComponent().getAttributes()
				.get("targetQuoteItem");

		if (item != null) {
			try {
				this.workingPlatformItems.addChangeItem(String.valueOf(item.getQuoteItem().getId()));
				// added control logic by DamonChen@20180416, will throw out
				// NumberFormatException if resaleMargin is empty.
				if (!QuoteUtil.isEmpty(item.getResaleMarginStr())) {
					if (!QuoteUtil.isEmpty(item.getCostStr())) {
						item.setQuotedPriceStr(null);
						Double cost = Double.parseDouble(item.getCostStr());
						Double resaleMargin = null;

						String tmpMargin = item.getResaleMarginStr();
						if (item.getResaleMarginStr().endsWith("%")) {
							tmpMargin = item.getResaleMarginStr().substring(0, item.getResaleMarginStr().length() - 1);
						}
						resaleMargin = Double.parseDouble(tmpMargin);

						Double quotedPrice = cost / (1 - (resaleMargin / 100));
						item.setQuotedPriceStr(df5.format(quotedPrice));
					} else if (!QuoteUtil.isEmpty(item.getQuotedPriceStr())) {
						item.setCostStr(null);
						Double quotedPrice = Double.parseDouble(item.getQuotedPriceStr());
						Double resaleMargin = null;

						String tmpMargin = item.getResaleMarginStr();
						if (item.getResaleMarginStr().endsWith("%")) {
							tmpMargin = item.getResaleMarginStr().substring(0, item.getResaleMarginStr().length() - 1);
						}
						resaleMargin = Double.parseDouble(tmpMargin);

						// logger.log(Level.INFO, "PERFORMANCE START - resale
						// Margin = " + resaleMargin);

						Double cost = quotedPrice * (1 - (resaleMargin / 100));
						item.setCostStr(df5.format(cost));
					}
				}

			} catch (Exception ex) {
				LOGGER.log(Level.WARNING, "EXception in parsing the double value for item cost: " + item.getCostStr()
						+ ", exception message : " + ex.getMessage(), ex);
			}
		} else {
			LOGGER.log(Level.INFO, "item is null");
		}
		// logger.log(Level.INFO, "PERFORMANCE END - updateQuotedMargin(int
		// rowIndex)");
	}

	public void uploadAttachment(FileUploadEvent event) {

		// logger.log(Level.INFO, "PERFORMANCE START -
		// uploadAttachment(FileUploadEvent event)");

		Attachment attachment = new Attachment();
		String fileName = event.getFile().getFileName().substring(event.getFile().getFileName().lastIndexOf("\\") + 1,
				event.getFile().getFileName().length());
		attachment.setFileName(fileName);
		attachment.setType(QuoteSBConstant.ATTACHMENT_TYPE_QUOTE_ITEM);
		attachment.setFileImage(event.getFile().getContents());
		WorkingPlatformEmailVO workingPlatformEmailVO = this.updateStatusItems.get(this.rowIndex);
		List<Attachment> attachments = workingPlatformEmailVO.getAttachments();
		if (attachments == null)
			attachments = new ArrayList<Attachment>();
		attachments.add(attachment);
		workingPlatformEmailVO.setAttachments(attachments);
		// this.updateStatusItems.set(this.rowIndex, workingPlatformEmailVO);

		// logger.log(Level.INFO, "PERFORMANCE END -
		// uploadAttachment(FileUploadEvent event)");
	}

	public void uploadQCAttachment(FileUploadEvent event) {
		Attachment attachment = new Attachment();
		String fileName = event.getFile().getFileName().substring(event.getFile().getFileName().lastIndexOf("\\") + 1,
				event.getFile().getFileName().length());
		attachment.setFileName(fileName);
		attachment.setType(QuoteSBConstant.ATTACHMENT_TYPE_QC);
		attachment.setFileImage(event.getFile().getContents());

		// String rowIndex =
		// String.valueOf(event.getComponent().getAttributes().get("rowIndex"));
		WorkingPlatformItemVO itemVO = (WorkingPlatformItemVO) event.getComponent().getAttributes().get("forObject");
		attachment.setQuoteItem(itemVO.getQuoteItem());
		itemVO.getQcAttachments().add(attachment);
		this.workingPlatformItems.cellChangeListener(String.valueOf(itemVO.getQuoteItem().getId()));
		FacesUtil.updateUI("form_pendinglist:datatable_pendinglist:" + rowIndex + ":qcAttachments");
	}

	public void updateTableFormAttachments(QuoteItem item) {
		attachments = new ArrayList<Attachment>();
		attachments = attachmentSB.findFormAttachment(item.getQuote().getId());
		if (attachments != null) {
			attachmentCount = attachments.size();
		}
	}

	public void updateTableFormAttachments(WorkingPlatformItemVO item) {

		// logger.log(Level.INFO, "PERFORMANCE START -
		// updateTableFormAttachments(int rowIndex)");

		// updateRowIndex(rowIndex);
		attachments = new ArrayList<Attachment>();
		attachments = attachmentSB.findFormAttachment(item.getQuoteItem().getQuote().getId());
		if (attachments != null) {
			attachmentCount = attachments.size();
		}

		// logger.log(Level.INFO, "PERFORMANCE END -
		// updateTableFormAttachments(int rowIndex)");

	}

	public void updateTableInternalTransferAttachments(QuoteItem item) {

		// logger.log(Level.INFO, "PERFORMANCE START -
		// updateTableInternalTransferAttachments(int rowIndex)");

		attachments = new ArrayList<Attachment>();
		attachments = attachmentSB.findInternalTransferAttachment(item.getQuoteNumber());
		if (attachments != null) {
			attachmentCount = attachments.size();
		}

		// logger.log(Level.INFO, "PERFORMANCE END -
		// updateTableInternalTransferAttachments(int rowIndex)");
	}

	public void updateQCAttachments(String quoteNumber) {

		// logger.log(Level.INFO, "PERFORMANCE START -
		// updateTableInternalTransferAttachments(int rowIndex)");

		attachments = new ArrayList<Attachment>();
		attachments = attachmentSB.findQCAttachment(quoteNumber);
		if (attachments != null) {
			attachmentCount = attachments.size();
		}

		// logger.log(Level.INFO, "PERFORMANCE END -
		// updateTableInternalTransferAttachments(int rowIndex)");
	}

	public void updateTableInternalTransferAttachments(WorkingPlatformItemVO item) {

		// logger.log(Level.INFO, "PERFORMANCE START -
		// updateTableInternalTransferAttachments(int rowIndex)");

		// updateRowIndex(rowIndex);

		attachments = new ArrayList<Attachment>();
		attachments = attachmentSB.findInternalTransferAttachment(item.getQuoteItem().getQuoteNumber());
		if (attachments != null) {
			attachmentCount = attachments.size();
		}

		// logger.log(Level.INFO, "PERFORMANCE END -
		// updateTableInternalTransferAttachments(int rowIndex)");
	}

	public void updateTableRefreshAttachments(QuoteItem item) {

		// logger.log(Level.INFO, "PERFORMANCE START -
		// updateTableRefreshAttachments(int rowIndex)");

		attachments = new ArrayList<Attachment>();
		attachments = attachmentSB.findRefreshQuoteAttachment(item.getQuoteNumber());
		if (attachments != null) {
			attachmentCount = attachments.size();
		}

		// logger.log(Level.INFO, "PERFORMANCE END -
		// updateTableRefreshAttachments(int rowIndex)");

	}

	public void updateTableRefreshAttachments(WorkingPlatformItemVO item) {

		// logger.log(Level.INFO, "PERFORMANCE START -
		// updateTableRefreshAttachments(int rowIndex)");

		attachments = new ArrayList<Attachment>();
		attachments = attachmentSB.findRefreshQuoteAttachment(item.getQuoteItem().getQuoteNumber());
		if (attachments != null) {
			attachmentCount = attachments.size();
		}

		// logger.log(Level.INFO, "PERFORMANCE END -
		// updateTableRefreshAttachments(int rowIndex)");

	}

	public void updateTableAttachments(QuoteItem item) {

		// logger.log(Level.INFO, "PERFORMANCE START -
		// updateTableAttachments(String quoteNumber)");

		attachments = new ArrayList<Attachment>();
		attachments = attachmentSB.findAttachment(item.getQuoteNumber());
		if (attachments != null) {
			attachmentCount = attachments.size();
		}

		// logger.log(Level.INFO, "PERFORMANCE END -
		// updateTableAttachments(String quoteNumber)");

	}

	public void updateTableAttachments(WorkingPlatformItemVO item) {

		// logger.log(Level.INFO, "PERFORMANCE START -
		// updateTableAttachments(String quoteNumber)");
		attachments = new ArrayList<Attachment>();
		attachments = attachmentSB.findAttachment(item.getQuoteItem().getQuoteNumber());
		if (attachments != null) {
			attachmentCount = attachments.size();
		}

		// logger.log(Level.INFO, "PERFORMANCE END -
		// updateTableAttachments(String quoteNumber)");

	}

	public void updateAttachments(int rowIndex) {

		// logger.log(Level.INFO, "PERFORMANCE START - updateAttachments(int
		// rowIndex)");

		updateRowIndex(rowIndex);
		WorkingPlatformEmailVO workingPlatformEmailVO = this.updateStatusItems.get(rowIndex);
		attachments = workingPlatformEmailVO.getAttachments();
		if (attachments != null) {
			attachmentCount = attachments.size();
		}

		// logger.log(Level.INFO, "PERFORMANCE END - updateAttachments(int
		// rowIndex)");

	}

	public void removeAttachment(int attachmentRowIndex) {

		// logger.log(Level.INFO, "PERFORMANCE START - removeAttachment(int
		// attachmentRowIndex)");
		//rowIndex->attachmentRowIndex TODO
		attachments.remove(attachmentRowIndex);
		WorkingPlatformEmailVO workingPlatformEmailVO = this.updateStatusItems.get(rowIndex);
		workingPlatformEmailVO.setAttachments(attachments);
		// this.updateStatusItems.set(rowIndex, workingPlatformEmailVO);

		// logger.log(Level.INFO, "PERFORMANCE END - removeAttachment(int
		// attachmentRowIndex)");

	}

	public void downloadAttachment(long id) {
		Attachment attachment = attachmentSB.findById(id);
		byte[] bytes = attachment.getFileImage();
		String mimeType = DownloadUtil.getMimeType(attachment.getFileName());
		downloadAttachment = new DefaultStreamedContent(new ByteArrayInputStream(bytes), mimeType,
				attachment.getFileName());
	}

	public void updateRowIndex(int rowIndex) {
		this.rowIndex = rowIndex;
	}

	public void searchForMaterialPopup(ActionEvent event) {

		// logger.log(Level.INFO, "PERFORMANCE START -
		// updateSearchMfrPartNumber(int rowIndex)");

		selectedPricerInfosInMaterialPopup = new ArrayList<PricerInfo>();
		selectedWorkingPlatformItem = (WorkingPlatformItemVO) event.getComponent().getAttributes()
				.get("targetQuoteItem");
		WorkingPlatformItemVO item = (WorkingPlatformItemVO) event.getComponent().getAttributes()
				.get("targetQuoteItem");
		item.getQuoteItem().setAqcc(item.getAqccStr());
		rowIndex = (Integer) event.getComponent().getAttributes().get("targetRowIndex");

		if (item != null) {
			LOGGER.log(Level.INFO,
					"Quote Item " + item.getQuoteItem().getQuoteNumber() + " is selected for the part search");
			LOGGER.log(Level.INFO, "Quote Item rowIndex = " + rowIndex);

			this.validationSearchMfr = item.getRequestedMfr();
			this.validationSearchFullMfrPartNumber = item.getQuotedPartNumber();
			PricerInfo pricerInfo = item.createPricerInfo();
			pricerInfosInMaterialPopup = materialSB.searchForMaterialPopup(pricerInfo);

			searchPartsCount = pricerInfosInMaterialPopup.size();
			this.workingPlatformItems.cellChangeListener(String.valueOf(item.getQuoteItem().getId()));
			item.setChanged(true);
		}
	}

	public void addPartController() {

		WorkingPlatformItemVO item = this.selectedWorkingPlatformItem;

		if (!isSameSalesCostFlag(item, this.selectedPricerInfosInMaterialPopup)) {
			RequestContext requestContext = RequestContext.getCurrentInstance();
			requestContext.execute("PF('partchg_add_validation_dialog').show()");
			return;
		}
		addPart();
	}

	public void addPart() {
		List<QuoteItem> items = new ArrayList<QuoteItem>();

		LOGGER.log(Level.INFO,
				"Selected Part Number Count for Alternative Quote: " + this.selectedPricerInfosInMaterialPopup.size());
		String revertVersion = "";

		for (PricerInfo pricerInfo : this.selectedPricerInfosInMaterialPopup) {
			LOGGER.log(Level.INFO, "Selected Part Number for Alternative Quote: " + pricerInfo.getMfr() + "/"
					+ pricerInfo.getFullMfrPartNumber());
			QuoteItem quoteItem = QuoteItem.newInstance(selectedWorkingPlatformItem.getQuoteItem());
			quoteItem.setAction(ActionEnum.WP_ALTERNATIVE_QUOTE.name());

			revertVersion = quoteSB.findMaxReverVersion(selectedWorkingPlatformItem.getQuoteItem().getFirstRfqCode(),
					selectedWorkingPlatformItem.getQuoteItem().getRevertVersion());
			revertVersion = QuoteUtil.getNewRevertVersionAfterAlternativePart(revertVersion);
			quoteItem.setRevertVersion(revertVersion);

			quoteItem.setSentOutTime(QuoteUtil.getCurrentTime());

			// quoteItem.setMoq(QuoteUtil.findMoq(quoteItem));

			quoteItem.setQuotedMaterial(null);

			quoteItem.setQuoteNumber(quoteSB.populateQuoteNumberOnly(bizUnit));

			items.add(quoteItem);

			pricerInfo.fillPriceInfoToQuoteItem(quoteItem);

			if (mfrCodes.contains(quoteItem.getRequestedMfr().getName())) {

				WorkingPlatformItemVO alternativeItem = new WorkingPlatformItemVO();

				// added by damonChen
				alternativeItem.setQuoteItem(quoteItem, bizUnit);
				alternativeItem.setInitialized(true);
				updateFutureFlagOfUI(alternativeItem);

				if (this.searchedWorkingPlatformItems != null) {
					for (int i = 0; i < searchedWorkingPlatformItems.size(); i++) {
						WorkingPlatformItemVO vo = searchedWorkingPlatformItems.get(i);
						if (vo.getQuoteItem().getId() == selectedWorkingPlatformItem.getQuoteItem().getId()) {
							searchedWorkingPlatformItems.add(i + 1, alternativeItem);
							break;
						}
					}
				}

				if (!filterMfrOptionList.contains(quoteItem.getQuotedMfr().getName())) {
					filterMfrOptionList.add(quoteItem.getQuotedMfr().getName());
					Collections.sort(filterMfrOptionList);
					filterMfrSelectList = QuoteUtil.createFilterOptions(
							filterMfrOptionList.toArray(new String[filterMfrOptionList.size()]),
							filterMfrOptionList.toArray(new String[filterMfrOptionList.size()]), false, false);
				}
			}

		}
		setLastUpdateFields(items);
		for (int i = 0; i < items.size(); i++) {
			quoteSB.createAlternativeQuoteItem(items.get(i));
		}
		// call setRequery is the second way to fix that users can't see the
		// quote they add
		// workingPlatformItems.setRequery(true);
		refreshQuoteTable();
	}

	private boolean isSameSalesCostFlag(WorkingPlatformItemVO item,
			List<PricerInfo> selectedPricerInfosInMaterialPopup2) {
		boolean b = item.getQuoteItem().isSalesCostFlag();
		for (PricerInfo pricerInfo : selectedPricerInfosInMaterialPopup2) {
			if (pricerInfo.isSalesCostFlag() != b) {
				return false;
			}
		}
		return true;
	}

	public void replacePartController() {

		WorkingPlatformItemVO item = this.selectedWorkingPlatformItem;

		if (!isSameSalesCostFlag(item, this.selectedPricerInfosInMaterialPopup)) {
			RequestContext requestContext = RequestContext.getCurrentInstance();
			requestContext.execute("PF('partchg_replace_validation_dialog').show()");
			return;

		}

		replacePart();
	}

	public void replacePart() {

		if (selectedPricerInfosInMaterialPopup.size() != 1) {
			// to do, prompt user
			return;
		}

		LOGGER.log(Level.INFO, "replacePart begin ");
		PricerInfo pricerInfo = selectedPricerInfosInMaterialPopup.get(0);
		LOGGER.log(Level.INFO, "Selected Part Number for Alternative Quote: " + pricerInfo.getMfr() + "/"
				+ pricerInfo.getFullMfrPartNumber());

		pricerInfo.fillPriceInfoToQuoteItem(selectedWorkingPlatformItem.getQuoteItem());
		// Defect 380, updated the future icon status
		updateFutureFlagOfUI(selectedWorkingPlatformItem);

		selectedWorkingPlatformItem.setQuoteItem(selectedWorkingPlatformItem.getQuoteItem(), bizUnit);
		selectedWorkingPlatformItem.setChanged(true);
		clearSalesCostFields(selectedWorkingPlatformItem);
		this.workingPlatformItems
				.cellChangeListener(String.valueOf(selectedWorkingPlatformItem.getQuoteItem().getId()));
		// addedd by DamonChen
		RequestContext.getCurrentInstance().execute("updateSaveRfqDetail();");
		LOGGER.log(Level.INFO, "replacePart end ");
	}

	/**
	 * @Description: TODO @author 042659 @param
	 *               selectedWorkingPlatformItem2 @return void @throws
	 */
	private void clearSalesCostFields(WorkingPlatformItemVO item) {
		item.setSalesCostStr(null);
		item.setSuggestedResaleStr(null);
		item.setQuotedPriceStr(null);
		item.setResaleMarginStr(null);
		if (QuoteUtil.isEmpty(item.getSalesCostTypeName())
				|| SalesCostType.NonSC.name().equals(item.getSalesCostTypeName())) {
			item.setSalesCostTypeName(SalesCostType.NonSC.name());
			item.setDisableEditableForSalesCost(false);
		} else {
			item.setDisableEditableForSalesCost(true);
		}

	}

	public void updateRfqAttachment(int rowIndex) {

		// logger.log(Level.INFO, "PERFORMANCE START - updateRfqAttachment(int
		// rowIndex)");

		rfqAttachment = null;
		if (displayAttachmentList != null) {
			Attachment attachment = displayAttachmentList.get(rowIndex);
			InputStream byteIn = new ByteArrayInputStream(attachment.getFileImage());
			rfqAttachment = new DefaultStreamedContent(byteIn, DownloadUtil.getMimeType(attachment.getFileName()),
					attachment.getFileName());
		}

		// logger.log(Level.INFO, "PERFORMANCE END - updateRfqAttachment(int
		// rowIndex)");

	}

	public void updateFormLevelAttachment(int rowIndex) {

		// logger.log(Level.INFO, "PERFORMANCE START -
		// updateFormLevelAttachment(int rowIndex)");

		WorkingPlatformItemVO item = null;
		if (this.searchedWorkingPlatformItems != null)
			item = this.searchedWorkingPlatformItems.get(rowIndex);
		else
			item = this.searchedWorkingPlatformItems.get(rowIndex);
		this.displayAttachmentList = item.getQuoteItem().getQuote().getAttachments();

		// logger.log(Level.INFO, "PERFORMANCE END -
		// updateFormLevelAttachment(int rowIndex)");

	}

	public void updateAttachment(int rowIndex) {

		// logger.log(Level.INFO, "PERFORMANCE START - updateAttachment(int
		// rowIndex)");

		WorkingPlatformItemVO item = null;
		if (this.searchedWorkingPlatformItems != null)
			item = this.searchedWorkingPlatformItems.get(rowIndex);
		else
			item = this.searchedWorkingPlatformItems.get(rowIndex);
		this.displayAttachmentList = item.getQuoteItem().getAttachments();

		// logger.log(Level.INFO, "PERFORMANCE END - updateAttachment(int
		// rowIndex)");

	}

	public void updateInternalTransferComment(int rowIndex) {

		// logger.log(Level.INFO, "PERFORMANCE START -
		// updateInternalTransferComment(int rowIndex)");

		WorkingPlatformItemVO item = null;
		if (this.searchedWorkingPlatformItems != null)
			item = this.searchedWorkingPlatformItems.get(rowIndex);
		else
			item = this.searchedWorkingPlatformItems.get(rowIndex);
		this.internalTransferComment = item.getQuoteItem().getInternalTransferComment();

		// logger.log(Level.INFO, "PERFORMANCE END -
		// updateInternalTransferComment(int rowIndex)");

	}

	public void updateInternalComment(int rowIndex) {

		// logger.log(Level.INFO, "PERFORMANCE START - updateInternalComment(int
		// rowIndex)");

		WorkingPlatformItemVO item = null;
		if (this.searchedWorkingPlatformItems != null)
			item = this.searchedWorkingPlatformItems.get(rowIndex);
		else
			item = this.searchedWorkingPlatformItems.get(rowIndex);
		this.internalComment = item.getQuoteItem().getInternalComment();

		// logger.log(Level.INFO, "PERFORMANCE END - updateInternalComment(int
		// rowIndex)");

	}

	public void updateFreeStock(Material material) {

		LOGGER.log(Level.INFO, "PERFORMANCE START - updateFreeStock(Material material)");

		freeStockList = null;
		if (material != null && material.getId() > 0) {
			// Bryan Start
			freeStockList = freeStockSB.findFreeStock(material, cacheUtil.getPlantCodesByBizUnit(bizUnit.getName()));
			// Bryan End
		}
		LOGGER.log(Level.INFO, "PERFORMANCE END - updateFreeStock(Material material)");

	}

	public void searchFreeStock() {
		// Bryan Start
		freeStockList = freeStockSB.searchFreeStock(this.freeStockMfrSearch, this.freeStockPartNumberSearch,
				cacheUtil.getPlantCodesByBizUnit(bizUnit.getName()));
		// End
	}

	public void changeQuotedPartNumber(AjaxBehaviorEvent event) {

		// logger.log(Level.INFO, "PERFORMANCE START - updateQuotedMaterial(int
		// rowIndex)");
		WorkingPlatformItemVO item = (WorkingPlatformItemVO) event.getComponent().getAttributes()
				.get("targetQuoteItem");
		item.setSapPartNumberStr("");
		item.getQuoteItem().setSapPartNumber("");
		String partNumber = item.getQuotedPartNumber();
		String mfrName = item.getQuoteItem().getRequestedMfr().getName();
		item.getQuoteItem().setQuotedPartNumber(partNumber);
		quoteSB.changeQuotedPartNumber(item.getQuoteItem(), mfrName, partNumber);
		// item = updateQuotedMaterial(item);
		updateFutureFlagOfUI(item);
		// PROGRM PRICER ENHANCEMENT
		// item.setQuoteItem(item.getQuoteItem(), true);
		item.setQuoteItem(item.getQuoteItem(), true, bizUnit);
		clearSalesCostFields(item);
		this.workingPlatformItems.cellChangeListener(String.valueOf(item.getQuoteItem().getId()));

	}

	/**
	 * public void updateQtyIndicator(AjaxBehaviorEvent event){
	 * 
	 * //logger.log(Level.INFO, "PERFORMANCE START - updateQtyIndicator(int
	 * rowIndex)"); WorkingPlatformItemVO item =
	 * (WorkingPlatformItemVO)event.getComponent().getAttributes().get("targetQuoteItem");
	 * //logger.log(Level.INFO, "PERFORMANCE END - updateQtyIndicator(int
	 * rowIndex)");
	 * 
	 * }
	 * 
	 * public void updateQuotedQty(AjaxBehaviorEvent event){
	 * //logger.log(Level.INFO, "PERFORMANCE START - updateQuotedQty(int
	 * rowIndex)"); WorkingPlatformItemVO item =
	 * (WorkingPlatformItemVO)event.getComponent().getAttributes().get("targetQuoteItem");
	 * //logger.log(Level.INFO, "PERFORMANCE END - updateQuotedQty(int
	 * rowIndex)");
	 * 
	 * }
	 **/

	public void switchCostIndicator(AjaxBehaviorEvent event) {
		WorkingPlatformItemVO item = (WorkingPlatformItemVO) event.getComponent().getAttributes()
				.get("targetQuoteItem");
		// updateCostIndicator(item,true,0);
		// item.getQuoteItem().setAqcc(item.getAqccStr());
		item.fillBackQuoteItem();
		quoteSB.switchCostIndicator(item.getQuoteItem(), costIndicatorSB.findCostIndicator(item.getCostIndicatorStr()),
				Currency.valueOf(item.getBuyCurrStr()));

		item.setQuoteItem(item.getQuoteItem(), bizUnit);
		clearSalesCostFields(item);
		this.workingPlatformItems.cellChangeListenerWithoutUpdateUI(String.valueOf(item.getQuoteItem().getId()));
		// to do to remove
		//
		// item.setQuotedPriceStr(null);
		// item.setResaleMarginStr(null);
		// item.setQuotedQtyStr(null);
	}

	public void switchBuyCurrency(AjaxBehaviorEvent event) {
		switchCostIndicator(event);
	}

	public void selectStatusForVendorQuote() {

		// logger.log(Level.INFO, "PERFORMANCE START -
		// selectQcOnlyFromSelectedQuoteItems()");
		updateRfqCount = 0;
		for (Iterator<WorkingPlatformItemVO> itr = getCacheSelectionDataList().iterator(); itr.hasNext();) {
			WorkingPlatformItemVO item = itr.next();
			if (!item.getQuoteItem().getStatus().equals(QuoteSBConstant.RFQ_STATUS_QC)
					&& !item.getQuoteItem().getStatus().equals(QuoteSBConstant.RFQ_STATUS_IT)
					&& !item.getQuoteItem().getStatus().equals(QuoteSBConstant.RFQ_STATUS_RBIT)
					&& !item.getQuoteItem().getStatus().equals(QuoteSBConstant.RFQ_STATUS_RBQ)
					&& !item.getQuoteItem().getStatus().equals(QuoteSBConstant.RFQ_STATUS_RIT)) {
				// itr.remove();
				// NEC Pagination changes: get the selected data from cache
				workingPlatformItems.getCacheSelectedItems().remove(item);
			} else {
				updateRfqCount++;
			}
		}

		// logger.log(Level.INFO, "PERFORMANCE END -
		// selectQcOnlyFromSelectedQuoteItems()");

	}

	public void selectStatusForItToQcQuote() {
		updateRfqCount = 0;
		if (getCacheSelectionDataList() != null) {
			for (Iterator<WorkingPlatformItemVO> itr = getCacheSelectionDataList().iterator(); itr.hasNext();) {
				WorkingPlatformItemVO item = itr.next();
				if (!item.getQuoteItem().getStatus().equals(QuoteSBConstant.RFQ_STATUS_IT)) {
					// itr.remove();
					// NEC Pagination changes: get the selected data from cache
					this.workingPlatformItems.getCacheSelectedItems().remove(item);
				} else {
					updateRfqCount++;
				}
			}
		}
	}

	public void selectStatusForInternalTransfer() {

		// logger.log(Level.INFO, "PERFORMANCE START -
		// selectQcAndSqOnlyFromSelectedQuoteItems()");
		updateRfqCount = 0;
		for (Iterator<WorkingPlatformItemVO> itr = getCacheSelectionDataList().iterator(); itr.hasNext();) {
			WorkingPlatformItemVO item = itr.next();
			if (!item.getQuoteItem().getStatus().equals(QuoteSBConstant.RFQ_STATUS_QC)
					&& !item.getQuoteItem().getStatus().equals(QuoteSBConstant.RFQ_STATUS_IT)
					&& !item.getQuoteItem().getStatus().equals(QuoteSBConstant.RFQ_STATUS_RIT)
					&& !item.getQuoteItem().getStatus().equals(QuoteSBConstant.RFQ_STATUS_RBQ)
					&& !item.getQuoteItem().getStatus().equals(QuoteSBConstant.RFQ_STATUS_RBIT)
					&& !item.getQuoteItem().getStatus().equals(QuoteSBConstant.RFQ_STATUS_SQ)) {
				itr.remove();
			} else {
				updateRfqCount++;
			}
		}

		// logger.log(Level.INFO, "PERFORMANCE END -
		// selectQcAndSqOnlyFromSelectedQuoteItems()");
	}

	public void groupVendorRfqForStatusChange() {
		RequestContext context = RequestContext.getCurrentInstance();

		if (commonSaveQuotation(false) == false) {
			context.addCallbackParam("saveValidationSucceed", "0");
			return;
		} else {
			context.addCallbackParam("saveValidationSucceed", "1");
		}
		selectStatusForVendorQuote();
	}

	public void openItToBmtDialog() {
		if (getCacheSelectionDataList() == null || getCacheSelectionDataList().isEmpty()) {
			FacesUtil.showWarnMessage(ResourceMB.getText("wq.button.ITbmt"),
					ResourceMB.getText("wq.label.selectQuote") + ".");
		} else {
			if (!commonSaveQuotation(false))
				return;
			getSelectedItToBmtItems().clear();
			for (WorkingPlatformItemVO vo : getCacheSelectionDataList()) {
				if (!vo.disabledSelectionFromItToBmt()) {
					getSelectedItToBmtItems().add(vo);
				}
			}
			FacesUtil.updateUI("form_pendinglist_dialog:itToBmt_RFQ_dialog");
		}
	}

	public void openItToBmtEmailDialog() {
		if (selectedItToBmtItems == null || selectedItToBmtItems.isEmpty()) {
			FacesUtil.showWarnMessage(ResourceMB.getText("wq.button.ITbmt"),
					ResourceMB.getText("wq.label.selectQuote") + ".");
		} else {
			for (WorkingPlatformItemVO vo : selectedItToBmtItems) {
				vo.setEmailMessage("");
			}
			FacesUtil.updateUI("form_pendinglist_dialog:itToBmt_email_panel");
		}
	}

	public void groupItToQcRfqForStatusChange() {
		RequestContext context = RequestContext.getCurrentInstance();

		if (commonSaveQuotation(false) == false) {
			context.addCallbackParam("saveValidationSucceed", "0");
			return;
		} else {
			context.addCallbackParam("saveValidationSucceed", "1");
		}
		selectStatusForItToQcQuote();
	}

	public void proceedInternalTransferEmail() {
		RequestContext context = RequestContext.getCurrentInstance();
		NumberFormat df = NumberFormat.getNumberInstance();
		df.setMaximumFractionDigits(2);
		// NEC Pagination changes: get the selected data from cache
		for (WorkingPlatformItemVO vo : workingPlatformItems.getCacheSelectedItems()) {
			QuoteItem item = vo.getQuoteItem();

			if (com.avnet.emasia.webquote.utilities.util.QuoteUtil.getDrmsKey(item) == null) {
				continue;
			}

			String resaleMargin = vo.getResaleMarginStr();

			if (resaleMargin == null || resaleMargin.equals("")) {
				continue;
			}
			if (resaleMargin.endsWith("%")) {
				resaleMargin = resaleMargin.substring(0, resaleMargin.length() - 1);
			}
			double dResaleMargin = Double.parseDouble(resaleMargin);
			String internalComment = vo.getInternalCommentStr();
			if (dResaleMargin < 0) {
				if (item.getAuthGp() != null) {
					String s = ResourceMB.getText("wq.label.quoteGP") + "% " + df.format(dResaleMargin) + " % < "
							+ ResourceMB.getText("wq.label.a") + ". " + ResourceMB.getText("wq.label.gp") + " "
							+ df.format(item.getAuthGp()) + "% ";
					if (org.apache.commons.lang.StringUtils.isNotBlank(internalComment)) {
						internalComment = internalComment + ";" + s;
					}
					vo.setInternalCommentStr(internalComment);
				}
				continue;
			}
			if (com.avnet.emasia.webquote.utilities.util.QuoteUtil.compareQGPAndAGP(dResaleMargin,
					item.getAuthGp()) < 0) {
				item.setAuthGpReasonCode(null);
				item.setAuthGpReasonDesc(null);

				String s = ResourceMB.getText("wq.label.quoteGP") + " " + df.format(dResaleMargin) + "% <  "
						+ ResourceMB.getText("wq.label.a") + ". " + ResourceMB.getText("wq.label.gp") + " "
						+ df.format(item.getAuthGp()) + "% ";
				if (org.apache.commons.lang.StringUtils.isNotBlank(internalComment)) {
					internalComment = internalComment + ";" + s;
				}
				vo.setInternalCommentStr(internalComment);
				break;
			}

		}

		if (!commonSaveQuotation(false)) {
			context.addCallbackParam("saveValidationSucceed", "0");
			return;
		} else {
			context.addCallbackParam("saveValidationSucceed", "1");
		}

		selectStatusForInternalTransfer();
		updateStatusItems = new ArrayList<WorkingPlatformEmailVO>();
		List<List<WorkingPlatformItemVO>> itemLists = new ArrayList<List<WorkingPlatformItemVO>>();

		// Group For PM Internal Transfer
		// NEC Pagination changes: get the selected data from cache
		if (workingPlatformItems.getCacheSelectedItems() != null) {
			List<String> dupQuoteNumbers = new ArrayList<String>();
			// NEC Pagination changes: get the selected data from cache
			for (WorkingPlatformItemVO item : workingPlatformItems.getCacheSelectedItems()) {
				if (!dupQuoteNumbers.contains(item.getQuoteItem().getQuoteNumber())) {
					List<WorkingPlatformItemVO> itemList = new ArrayList<WorkingPlatformItemVO>();
					itemList.add(item);
					dupQuoteNumbers.add(item.getQuoteItem().getQuoteNumber());
					// NEC Pagination changes: get the selected data from cache
					for (WorkingPlatformItemVO subItem : workingPlatformItems.getCacheSelectedItems()) {
						if (!dupQuoteNumbers.contains(subItem.getQuoteItem().getQuoteNumber())) {
							if (item.getQuoteItem().getRequestedMfr().getName()
									.equals(subItem.getQuoteItem().getRequestedMfr().getName())) {
								if ((item.getQuoteItem().getProductGroup2() != null
										&& subItem.getQuoteItem().getProductGroup2() != null
										&& item.getQuoteItem().getProductGroup2().getName()
												.equals(subItem.getQuoteItem().getProductGroup2().getName()))
										|| (item.getQuoteItem().getProductGroup2() == null
												&& subItem.getQuoteItem().getProductGroup2() == null)) {
									if ((item.getQuoteItem().getQuote().getSales().getTeam() != null
											&& subItem.getQuoteItem().getQuote().getSales().getTeam() != null
											&& item.getQuoteItem().getQuote().getSales().getTeam().getName().equals(
													subItem.getQuoteItem().getQuote().getSales().getTeam().getName()))
											|| (item.getQuoteItem().getQuote().getSales().getTeam() == null
													&& item.getQuoteItem().getQuote().getSales().getTeam() == null)) {
										// andy modify 2.2
										if ((item.getQuoteItem().getMaterialTypeId() != null
												&& subItem.getQuoteItem().getMaterialTypeId() != null
												&& item.getQuoteItem().getMaterialTypeId()
														.equals(subItem.getQuoteItem().getMaterialTypeId()))
												|| (subItem.getQuoteItem().getMaterialTypeId() == null
														&& subItem.getQuoteItem().getMaterialTypeId() == null)) {

											itemList.add(subItem);
											dupQuoteNumbers.add(subItem.getQuoteItem().getQuoteNumber());
										}
									}
								}
							}
						}
					}
					itemLists.add(itemList);
				}
			}
		}

		for (List<WorkingPlatformItemVO> itemList : itemLists) {
			WorkingPlatformItemVO item = itemList.get(0);

			List<QuoteItem> quoteItemList = new ArrayList<QuoteItem>();
			List<String> quoteItems = new ArrayList<String>();
			List<String> formNumbers = new ArrayList<String>();
			List<String> partNumbers = new ArrayList<String>();
			List<String> customers = new ArrayList<String>();
			List<String> projects = new ArrayList<String>();
			List<String> emailTos = findPmForInternalTransfer(item);
			List<String> emailCcs = new ArrayList<String>();
			Manufacturer mfr = item.getQuoteItem().getRequestedMfr();
			for (WorkingPlatformItemVO subItem : itemList) {
				quoteItemList.add(subItem.getQuoteItem());
				quoteItems.add(subItem.getQuoteItem().getQuoteNumber());

				if (!formNumbers.contains(subItem.getQuoteItem().getQuote().getFormNumber()))
					formNumbers.add(subItem.getQuoteItem().getQuote().getFormNumber());

				partNumbers.add(subItem.getQuoteItem().getRequestedPartNumber());

				if (subItem.getQuoteItem().getSoldToCustomer() != null
						&& getCustomerFullName(subItem.getQuoteItem().getSoldToCustomer()) != null
						&& !customers.contains(getCustomerFullName(subItem.getQuoteItem().getSoldToCustomer()))) {
					customers.add(getCustomerFullName(subItem.getQuoteItem().getSoldToCustomer()));
				}
				if (subItem.getQuoteItem().getProjectName() != null
						&& !projects.contains(subItem.getQuoteItem().getProjectName())) {
					projects.add(subItem.getQuoteItem().getProjectName());
				}
			}

			WorkingPlatformEmailVO workingPlatformEmailVO = new WorkingPlatformEmailVO();
			workingPlatformEmailVO.setEmailsTo(emailTos);
			workingPlatformEmailVO.setEmailsToSelectList(emailTos, this.emailAddressList);
			workingPlatformEmailVO.setEmailsCc(emailCcs);
			workingPlatformEmailVO.setEmailsCcSelectList(emailCcs, this.emailAddressList);
			// workingPlatformEmailVO.setEmailToAddresses(emails);
			workingPlatformEmailVO.setMfr(mfr.getName());
			workingPlatformEmailVO.setQuoteItems(quoteItemList);
			// workingPlatformEmailVO.setFormNumbers(formNumbers);
			workingPlatformEmailVO.setPartNumbers(partNumbers);
			workingPlatformEmailVO.setCustomers(customers);
			workingPlatformEmailVO.setProjects(projects);
			updateStatusItems.add(workingPlatformEmailVO);
		}

		doKeepFirstRow();
		// logger.log(Level.INFO, "PERFORMANCE END -
		// groupInternalTransferRfqForStatusChange()");

	}

	public void proceedInvalidationEmail() {

		RequestContext context = RequestContext.getCurrentInstance();

		if (commonSaveQuotation(false) == false) {
			context.addCallbackParam("saveValidationSucceed", "0");
			return;
		} else {
			context.addCallbackParam("saveValidationSucceed", "1");
		}

		// logger.log(Level.INFO, "PERFORMANCE START -
		// groupInternalTransferRfqForStatusChange()");
		// NEC Pagination changes: get the selected data from cache
		invalidateRfqCount = this.workingPlatformItems.getCacheSelectedItems().size();
		try {
			if (getCacheSelectionDataList() != null) {
				LOGGER.log(Level.INFO, getCacheSelectionDataList().size() + " selected Quote Item to invalidate");
				for (WorkingPlatformItemVO item : getCacheSelectionDataList()) {
					if (QuoteUtil.isEmpty(item.getQuoteItem().getAqcc())) {
						RequestContext.getCurrentInstance().addCallbackParam("emptyQcExternalCommentIsFound", true);
						FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR,
								QuoteConstant.MESSAGE_MISSING_MANDATORY_FIELD,
								ResourceMB.getText("wq.label.avnetQuoteCenterComment"));
						FacesContext.getCurrentInstance().addMessage("workingPlatformGrowl", msg);
						LOGGER.log(Level.SEVERE, msg.getSummary() + " - " + msg.getDetail());
						throw (new Exception());
					}
				}
				RequestContext.getCurrentInstance().addCallbackParam("emptyQcExternalCommentIsFound", false);

				invalidQuoteEmailVOs = new ArrayList<WorkingPlatformEmailVO>();
				HashMap<String, List<WorkingPlatformItemVO>> itemLists = new HashMap<String, List<WorkingPlatformItemVO>>();
				if (getCacheSelectionDataList() != null) {
					for (WorkingPlatformItemVO item : getCacheSelectionDataList()) {
						List<WorkingPlatformItemVO> itemList = null;
						if (itemLists.get(item.getQuoteItem().getQuote().getFormNumber()) == null) {
							itemList = new ArrayList<WorkingPlatformItemVO>();
						} else {
							itemList = itemLists.get(item.getQuoteItem().getQuote().getFormNumber());
						}
						itemList.add(item);
						itemLists.put(item.getQuoteItem().getQuote().getFormNumber(), itemList);

					}
				}

				for (String formNumber : itemLists.keySet()) {

					LOGGER.log(Level.INFO, "group Invalidate Quote by Form Number : " + formNumber);
					List<WorkingPlatformItemVO> itemList = itemLists.get(formNumber);
					LOGGER.log(Level.INFO, "group Invalidate Quote with Quote Item size : " + itemList.size());

					List<QuoteItem> quoteItemList = new ArrayList<QuoteItem>();
					List<String> partNumbers = new ArrayList<String>();
					List<String> mfrs = new ArrayList<String>();
					List<String> emailTos = new ArrayList<String>();
					List<String> emailCcs = new ArrayList<String>();
					String salesman = "";
					String team = "";
					for (WorkingPlatformItemVO item : itemList) {

						quoteItemList.add(item.getQuoteItem());

						salesman = item.getQuoteItem().getQuote().getSales().getName();
						if (item.getQuoteItem().getQuote().getSales().getTeam() != null)
							team = item.getQuoteItem().getQuote().getSales().getTeam().getName();
						mfrs.add(item.getQuoteItem().getRequestedMfr().getName());
						partNumbers.add(item.getQuoteItem().getRequestedPartNumber());
						emailTos.add(item.getQuoteItem().getQuote().getSales().getEmployeeId());

						String copyToCs = item.getQuoteItem().getQuote().getCopyToCS();
						if (!QuoteUtil.isEmpty(copyToCs)) {
							String[] css = item.getQuoteItem().getQuote().getCopyToCS().split(";");
							if (css.length > 0) {
								for (int m = 0; m < css.length; m++) {
									emailCcs.add(css[m]);
								}
							}
						}
						// add cc pms
						emailCcs.addAll(findPmForInternalTransfer(item));
					}
					// emailCcs =
					// emailCcs.stream().distinct().collect(Collectors.toList());

					WorkingPlatformEmailVO workingPlatformEmailVO = new WorkingPlatformEmailVO();
					workingPlatformEmailVO.setEmailsTo(emailTos);
					workingPlatformEmailVO.setEmailsToSelectList(emailTos, this.emailAddressList);
					workingPlatformEmailVO.setEmailsCc(emailCcs);
					workingPlatformEmailVO.setEmailsCcSelectList(emailCcs, this.emailAddressList);
					workingPlatformEmailVO.setQuoteItems(quoteItemList);
					workingPlatformEmailVO.setFormNumber(formNumber);

					workingPlatformEmailVO.setSalesman(salesman);
					workingPlatformEmailVO.setTeam(team);
					workingPlatformEmailVO.setMfrs(mfrs);
					workingPlatformEmailVO.setPartNumbers(partNumbers);
					invalidQuoteEmailVOs.add(workingPlatformEmailVO);
				}

				// logger.log(Level.INFO, "PERFORMANCE END -
				// groupInternalTransferRfqForStatusChange()");

			} else {
				LOGGER.log(Level.INFO, "No Selected Quote Item to invalidate");
			}
		} catch (Exception ex) {
			LOGGER.log(Level.WARNING, "Unable to invalidate mail" + ex.getMessage());
		}

	}

	private List<String> findPmForInternalTransfer(final WorkingPlatformItemVO item) {
		boolean isPgType = QuoteSBConstant.MATERIAL_TYPE_PROGRAM.equals(item.getQuoteItem().getMaterialTypeId());
		String mType = isPgType ? QuoteSBConstant.MATERIAL_TYPE_PROGRAM : QuoteSBConstant.MATERIAL_TYPE_NORMAL;

		List<User> users = userSB.findPmForInternalTransfer(item.getQuoteItem().getRequestedMfr(),
				item.getQuoteItem().getProductGroup2(), item.getQuoteItem().getQuote().getTeam(), mType, bizUnit);

		List<String> pmEmployeeIds = users.stream()
				.filter(u -> u.getRoles() != null
						&& u.getRoles().stream().anyMatch(r -> r.getName().equals(QuoteSBConstant.ROLE_PM)))
				.map(u -> u.getEmployeeId()).distinct().collect(Collectors.toList());
		return pmEmployeeIds;

	}

	public void refreshNextEmailForInternalTransfer() {

		// logger.log(Level.INFO, "PERFORMANCE START -
		// refreshNextEmailForInternalTransfer()");

		this.internalTransferEmailCount--;
		emailContent = emailContents.get(emailContents.size() - internalTransferEmailCount);

		// logger.log(Level.INFO, "PERFORMANCE END -
		// refreshNextEmailForInternalTransfer()");

	}

	private List<String> validateAndFillQuoteItem(WorkingPlatformItemVO item) {
		List<String> messages = new ArrayList<String>();
		dateFormatOfDMY.setLenient(false);
		if (QuoteUtil.isEmpty(item.getQuotedPartNumber()))
			messages.add(ResourceMB.getParameterizedText("wq.label.quotedMaterialAt", "" + item.getQuotedPartNumber())
					+ " " + item.getQuoteItem().getAlternativeQuoteNumber());

		try {
			if (!QuoteUtil.isEmpty(item.getCostStr()))
				Double.parseDouble(item.getCostStr());
		} catch (Exception ex) {
			LOGGER.log(Level.WARNING, "Error occured for item cost: " + item.getCostStr() + ", " + "Reason for failure: "
					+ MessageFormatorUtil.getParameterizedStringFromException(ex), ex);
			messages.add(ResourceMB.getParameterizedText("wq.label.costAt", "" + item.getCostStr()) + " "
					+ item.getQuoteItem().getAlternativeQuoteNumber());
		}

		try {
			if (!QuoteUtil.isEmpty(item.getMpqStr()))
				Integer.parseInt(item.getMpqStr());
		} catch (Exception ex) {
			LOGGER.log(Level.WARNING, "Error occured for item MPQ: " + item.getMpqStr() + ", " + "Reason for failure: "
					+ MessageFormatorUtil.getParameterizedStringFromException(ex), ex);
			messages.add(ResourceMB.getParameterizedText("wq.label.mpqAt", "" + item.getMpqStr()) + " "
					+ item.getQuoteItem().getAlternativeQuoteNumber());
		}

		try {
			if (!QuoteUtil.isEmpty(item.getMoqStr()))
				Integer.parseInt(item.getMoqStr());
		} catch (Exception ex) {
			LOGGER.log(Level.WARNING, "Error occured for item MOQ: " + item.getMoqStr() + ", " + "Reason for failure: "
					+ MessageFormatorUtil.getParameterizedStringFromException(ex), ex);
			messages.add(ResourceMB.getParameterizedText("wq.label.moqAt", "" + item.getMoqStr()) + " "
					+ item.getQuoteItem().getAlternativeQuoteNumber());
		}

		try {
			if (!QuoteUtil.isEmpty(item.getMovStr()))
				Integer.parseInt(item.getMovStr());
		} catch (Exception ex) {
			LOGGER.log(Level.WARNING, "Error occured for item MOV: " + item.getMovStr() + ", " + "Reason for failure: "
					+ MessageFormatorUtil.getParameterizedStringFromException(ex), ex);
			messages.add(ResourceMB.getParameterizedText("wq.label.movAt", "" + item.getMovStr()) + " "
					+ item.getQuoteItem().getAlternativeQuoteNumber());
		}

		try {
			if (!QuoteUtil.isEmpty(item.getResaleMarginStr())) {
				if (item.getResaleMarginStr().endsWith("%")) {
					String tmpMargin = item.getResaleMarginStr().substring(0, item.getResaleMarginStr().length() - 1);
					Double.parseDouble(tmpMargin);
				} else {
					Double.parseDouble(item.getResaleMarginStr());
				}
			}
		} catch (Exception ex) {
			LOGGER.log(Level.WARNING, "Error occured for item resale margin: " + item.getResaleMarginStr() + ", "
					+ "Reason for failure: " + MessageFormatorUtil.getParameterizedStringFromException(ex), ex);
			messages.add(ResourceMB.getParameterizedText("wq.label.quotedMarginAt", "" + item.getResaleMarginStr())
					+ " " + item.getQuoteItem().getAlternativeQuoteNumber());
		}

		try {
			if (!QuoteUtil.isEmpty(item.getSalesCostStr()))
				Double.parseDouble(item.getSalesCostStr().trim());
		} catch (Exception ex) {
			LOGGER.log(Level.WARNING, "Error occured for item sales cost: " + item.getSalesCostStr() + ", "
					+ "Reason for failure: " + MessageFormatorUtil.getParameterizedStringFromException(ex), ex);
			messages.add(ResourceMB.getParameterizedText("wq.message.salesCostNotAllowedAtRow",
					item.getQuoteItem().getAlternativeQuoteNumber()));
		}

		try {
			if (!QuoteUtil.isEmpty(item.getSuggestedResaleStr()))
				Double.parseDouble(item.getSuggestedResaleStr().trim());
		} catch (Exception ex) {
			LOGGER.log(Level.WARNING, "Error occured for item sales cost: " + item.getSuggestedResaleStr() + ", "
					+ "Reason for failure: " + MessageFormatorUtil.getParameterizedStringFromException(ex), ex);
			messages.add(ResourceMB.getParameterizedText("wq.message.suggestedResaleNotAllowedAtRow",
					item.getQuoteItem().getAlternativeQuoteNumber()));
		}

		try {
			if (!QuoteUtil.isEmpty(item.getQuotedQtyStr()))
				Integer.parseInt(item.getQuotedQtyStr());
		} catch (Exception ex) {
			LOGGER.log(Level.WARNING, "Error occured for item quoted quantity: " + item.getQuotedQtyStr() + ", "
					+ "Reason for failure: " + MessageFormatorUtil.getParameterizedStringFromException(ex), ex);
			messages.add(ResourceMB.getParameterizedText("wq.label.quotedQtyAt", "" + item.getQuotedQtyStr()) + " "
					+ item.getQuoteItem().getAlternativeQuoteNumber());
		}

		try {
			if (!QuoteUtil.isEmpty(item.getPriceValidityStr())) {

				int priceValidityNumber = Integer.parseInt(item.getPriceValidityStr());
				LOGGER.log(Level.INFO, "" + priceValidityNumber);
				int priceValidityMax = Integer
						.parseInt(sysConfigSB.getProperyValue(QuoteConstant.PRICE_VALIDITY_NUM_MAX));
				LOGGER.log(Level.INFO, "" + priceValidityMax);
				if (priceValidityNumber > priceValidityMax) {

					LOGGER.log(Level.WARNING, "item price validity: " + item.getPriceValidityStr() + ", "
							+ ">  priceValidityMax:" + priceValidityMax);
					messages.add(ResourceMB.getParameterizedText("wq.message.PriceValidityOutLimitAt",
							"" + item.getPriceValidityStr()) + " " + item.getQuoteItem().getAlternativeQuoteNumber()
							+ "<br/>");

				}

			}
		} catch (Exception ex) {
			if (!QuoteUtil.isValidDate(item.getPriceValidityStr())) {
				LOGGER.log(Level.WARNING,
						"Error occured for item price validity: " + item.getPriceValidityStr() + ", "
								+ "Reason for failure: " + MessageFormatorUtil.getParameterizedStringFromException(ex),
						ex);
				messages.add(
						ResourceMB.getParameterizedText("wq.label.priceValidityAt", "" + item.getPriceValidityStr())
								+ " " + item.getQuoteItem().getAlternativeQuoteNumber());
			}
		}

		if (!QuoteUtil.isEmpty(item.getShipmentValidityStr())) {
			if (!QuoteUtil.isValidDate(item.getShipmentValidityStr())) {
				messages.add(ResourceMB.getParameterizedText("wq.label.shipmentValidityAt",
						"" + item.getShipmentValidityStr()) + " " + item.getQuoteItem().getAlternativeQuoteNumber());
			}
		}

		try {
			if (!QuoteUtil.isEmpty(item.getQuotationEffectiveDateStr())) {
				dateFormatOfDMY.parse(item.getQuotationEffectiveDateStr());
			}
		} catch (Exception e) {
			LOGGER.log(Level.WARNING,
					"Error occured for item Quotation effective date: " + item.getQuotationEffectiveDateStr() + ", "
							+ "Reason for failure: " + MessageFormatorUtil.getParameterizedStringFromException(e),
					e);
			messages.add(ResourceMB.getParameterizedText("wq.label.quotedEffectiveDateAt",
					"" + item.getQuotationEffectiveDateStr()) + " " + item.getQuoteItem().getAlternativeQuoteNumber());
		}

		try {
			if (!QuoteUtil.isEmpty(item.getCancellationWindowStr()))
				Integer.parseInt(item.getCancellationWindowStr());
		} catch (Exception ex) {
			LOGGER.log(Level.WARNING, "Error occured for item Cancellation Window: " + item.getCancellationWindowStr()
					+ ", " + "Reason for failure: " + MessageFormatorUtil.getParameterizedStringFromException(ex), ex);
			messages.add(ResourceMB.getParameterizedText("wq.label.cancellationWindowAt",
					"" + item.getCancellationWindowStr()) + " " + item.getQuoteItem().getAlternativeQuoteNumber());
		}

		try {
			if (!QuoteUtil.isEmpty(item.getRescheduleWindowStr()))
				Integer.parseInt(item.getRescheduleWindowStr());
		} catch (Exception ex) {
			LOGGER.log(Level.WARNING, "Error occured for item Reshedule Window: " + item.getRescheduleWindowStr() + ", "
					+ "Reason for failure: " + MessageFormatorUtil.getParameterizedStringFromException(ex), ex);
			messages.add(
					ResourceMB.getParameterizedText("wq.label.rescheduleWindowAt", "" + item.getRescheduleWindowStr())
							+ " " + item.getQuoteItem().getAlternativeQuoteNumber());
		}

		try {
			if (!QuoteUtil.isEmpty(item.getVendorQuoteQtyStr()))
				Integer.parseInt(item.getVendorQuoteQtyStr());
		} catch (Exception ex) {
			LOGGER.log(Level.WARNING, "Error occured for item Vendor Quote Quantity: " + item.getVendorQuoteQtyStr()
					+ ", " + "Reason for failure: " + MessageFormatorUtil.getParameterizedStringFromException(ex), ex);
			messages.add(ResourceMB.getParameterizedText("wq.label.mfrQuoteQtyAt", "" + item.getVendorQuoteQtyStr())
					+ " " + item.getQuoteItem().getAlternativeQuoteNumber());
		}

		try {
			if (!QuoteUtil.isEmpty(item.getQuotedPriceStr()))
				Double.parseDouble(item.getQuotedPriceStr());
		} catch (Exception ex) {
			LOGGER.log(Level.WARNING, "Error occured for item quoted price: " + item.getQuotedPriceStr() + ", "
					+ "Reason for failure: " + MessageFormatorUtil.getParameterizedStringFromException(ex), ex);
			messages.add(ResourceMB.getParameterizedText("wq.label.quotedPriceAt", "" + item.getQuotedPriceStr()) + " "
					+ item.getQuoteItem().getAlternativeQuoteNumber());
		}

		if (!QuoteUtil.isEmpty(item.getResaleIndicatorStr()) && !item.getResaleIndicatorStr().matches("[0-9]{4}")
				&& !item.getResaleIndicatorStr().matches("[0-9]{2}[A]{2}")) {
			messages.add(
					ResourceMB.getParameterizedText("wq.label.resaleIndicatorAt", "" + item.getResaleIndicatorStr())
							+ " " + item.getQuoteItem().getAlternativeQuoteNumber());
		}

		if (!QuoteUtil.isEmpty(item.getEndCustomerCodeStr())) {
			Customer endCust = customerSB.findByPK(item.getEndCustomerCodeStr());
			if (endCust == null) {
				messages.add(
						ResourceMB.getParameterizedText("wq.label.endCustomerCodeAt", "" + item.getEndCustomerCodeStr())
								+ " " + item.getQuoteItem().getAlternativeQuoteNumber());
			}
		}

		if (!QuoteUtil.isEmpty(item.getCustomerGroupId())) {
			if (!sapDpaCustSB.isValidCustomerGroup(item.getCustomerGroupId(),
					item.getQuoteItem().getQuote().getBizUnit().getName(),
					item.getQuoteItem().getSoldToCustomer().getCustomerNumber(), item.getEndCustomerCodeStr())) {
				// messages.add(ResourceMB.getText("wq.message.customerGroupNotMatched")+"
				// " + item.getCustomerGroupId());
				messages.add(ResourceMB.getParameterizedText("wq.message.customerGroupNotMatchedAtRow",
						item.getQuoteItem().getAlternativeQuoteNumber()));

			}
		}

		if (messages.size() == 0) {
			messages = null;
			QuoteItem quoteItem = item.getQuoteItem();// quoteSB.findQuoteItemByQuoteNumber(item.getQuoteItem().getQuoteNumber());
			// andy modify 2.2
			quoteItem.setLastUpdatedBy(user.getEmployeeId());
			quoteItem.setLastUpdatedName(user.getName());
			quoteItem.setProgramType(item.getProgramType());

			quoteItem.setLastUpdatedOn(QuoteUtil.getCurrentTime());
			quoteItem.setStatus(item.getStatusStr());
			if (quoteItem.getQuotedMfr() == null) {
				quoteItem.setQuotedMfr(quoteItem.getRequestedMfr());
			}
			quoteItem.setQuotedPartNumber(item.getQuotedPartNumber());

			if (!QuoteUtil.isEmpty(item.getCostStr())) {
				quoteItem.setCost(Double.parseDouble(item.getCostStr()));
			} else {
				quoteItem.setCost(null);
			}

			if (!QuoteUtil.isEmpty(item.getBottomPriceStr())) {
				quoteItem.setBottomPrice(Double.parseDouble(item.getBottomPriceStr()));
			} else {
				quoteItem.setBottomPrice(null);
			}

			if (!QuoteUtil.isEmpty(item.getMinSellPriceStr())) {
				quoteItem.setMinSellPrice(Double.parseDouble(item.getMinSellPriceStr()));
			} else {
				quoteItem.setMinSellPrice(null);
			}

			if (!QuoteUtil.isEmpty(item.getMpqStr())) {
				quoteItem.setMpq(Integer.parseInt(item.getMpqStr()));
			} else {
				quoteItem.setMpq(null);
			}

			if (!QuoteUtil.isEmpty(item.getMoqStr())) {
				quoteItem.setMoq(Integer.parseInt(item.getMoqStr()));
			} else {
				quoteItem.setMoq(null);
			}

			if (!QuoteUtil.isEmpty(item.getMovStr())) {
				quoteItem.setMov(Integer.parseInt(item.getMovStr()));
			} else {
				quoteItem.setMov(null);
			}

			quoteItem.setLeadTime(item.getLeadTimeStr());

			quoteItem.setPriceListRemarks1(item.getPriceListRemarks1Str());
			quoteItem.setPriceListRemarks2(item.getPriceListRemarks2Str());
			quoteItem.setPriceListRemarks3(item.getPriceListRemarks3Str());
			quoteItem.setPriceListRemarks4(item.getPriceListRemarks4Str());

			quoteItem.setPriceValidity(item.getPriceValidityStr());

			if (!QuoteUtil.isEmpty(item.getShipmentValidityStr())) {
				try {
					quoteItem.setShipmentValidity(dateFormatOfDMY.parse(item.getShipmentValidityStr()));
				} catch (Exception ex) {
					LOGGER.log(Level.WARNING,
							"Error occured for item Shipment Validity: " + item.getShipmentValidityStr() + ", "
									+ "Reason for failure: "
									+ MessageFormatorUtil.getParameterizedStringFromException(ex),
							ex);
				}
			} else {
				quoteItem.setShipmentValidity(null);
			}

			if (!QuoteUtil.isEmpty(item.getQuotationEffectiveDateStr())) {
				try {
					quoteItem.setQuotationEffectiveDate(dateFormatOfDMY.parse(item.getQuotationEffectiveDateStr()));
				} catch (Exception ex) {
					LOGGER.log(Level.WARNING,
							"Error occured for item Quotation Effective Date: " + item.getQuotationEffectiveDateStr()
									+ ", " + "Reason for failure: "
									+ MessageFormatorUtil.getParameterizedStringFromException(ex),
							ex);
				}
			} else {
				quoteItem.setQuotationEffectiveDate(null);
			}

			quoteItem.setTermsAndConditions(item.getTermsAndConditionsStr());
			quoteItem.setDescription(item.getDescriptionStr());

			if (!QuoteUtil.isEmpty(item.getResaleMarginStr())) {
				String tmpMargin = item.getResaleMarginStr();
				if (item.getResaleMarginStr().endsWith("%")) {
					tmpMargin = item.getResaleMarginStr().substring(0, item.getResaleMarginStr().length() - 1);
				}
				quoteItem.setResaleMargin(Double.parseDouble(tmpMargin));
			} else {
				quoteItem.setResaleMargin(null);
			}

			if (!QuoteUtil.isEmpty(item.getQuotedPriceStr())) {
				quoteItem.setQuotedPrice(Double.parseDouble(item.getQuotedPriceStr()));
			} else {
				quoteItem.setQuotedPrice(null);
			}

			if (!QuoteUtil.isEmpty(item.getQuotedQtyStr())) {
				quoteItem.setQuotedQty(Integer.parseInt(item.getQuotedQtyStr()));
			} else {
				quoteItem.setQuotedQty(null);
			}

			if (!QuoteUtil.isEmpty(item.getCancellationWindowStr())) {
				quoteItem.setCancellationWindow(Integer.parseInt(item.getCancellationWindowStr()));
			} else {
				quoteItem.setCancellationWindow(null);
			}

			if (!QuoteUtil.isEmpty(item.getRescheduleWindowStr())) {
				quoteItem.setRescheduleWindow(Integer.parseInt(item.getRescheduleWindowStr()));
			} else {
				quoteItem.setRescheduleWindow(null);
			}

			quoteItem.setCostIndicator(item.getCostIndicatorStr());
			quoteItem.setQtyIndicator(item.getQtyIndicatorStr());
			quoteItem.setDesignInCat(item.getDesignInCatStr());
			quoteItem.setAllocationFlag(item.getAllocationFlagStr().equals("Yes") ? true : false);
			quoteItem.setMultiUsageFlag(item.getMultiUsageFlagStr().equals("Yes") ? true : false);
			quoteItem.setValidFlag(item.getValidFlagStr().equals("Yes") ? true : false);
			quoteItem.setAqcc(item.getAqccStr());
			quoteItem.setInternalComment(item.getInternalCommentStr());

			if (QuoteUtil.isEmpty(item.getResaleIndicatorStr())) {
				quoteItem.setResaleIndicator(QuoteSBConstant.DEFAULT_RESALE_INDICATOR);
				quoteItem.setResaleMin(QuoteSBConstant.DEFAULT_RESALE_INDICATOR_MIN);
				quoteItem.setResaleMax(QuoteSBConstant.DEFAULT_RESALE_INDICATOR_MAX);
			} else {
				quoteItem.setResaleIndicator(item.getResaleIndicatorStr());
				String minResale = quoteItem.getResaleIndicator().substring(0, 2);
				String maxResale = quoteItem.getResaleIndicator().substring(2, 4);
				// fix INC0276031
				// if(quoteItem.getQuotedPrice() != null){
				quoteItem.setResaleMin(Double.parseDouble(minResale));
				if (maxResale.matches("[0-9]{2}"))
					quoteItem.setResaleMax(Double.parseDouble(maxResale));
				else
					quoteItem.setResaleMax(QuoteSBConstant.DEFAULT_RESALE_INDICATOR_MAX);
				// }
			}

			quoteItem.setVendorQuoteNumber(item.getVendorQuoteNumberStr());
			quoteItem.setVendorDebitNumber(item.getVendorDebitNumberStr());
			if (!QuoteUtil.isEmpty(item.getVendorQuoteQtyStr())) {
				quoteItem.setVendorQuoteQty(Integer.parseInt(item.getVendorQuoteQtyStr()));
			} else {
				quoteItem.setVendorQuoteQty(null);
			}

			// logger.log(Level.INFO, "PERFORMANCE END - save
			// quoteItem.setEndCustomer:"+item.getEndCustomerCodeStr());
			// logger.log(Level.INFO, "PERFORMANCE END - save
			// quoteItem.setEndCustomerName:"+item.getEndCustomerNameStr());
			if (!StringUtils.isEmpty(item.getEndCustomerCodeStr())) {
				Customer tempCust = customerSB.findByPK(item.getEndCustomerCodeStr());
				quoteItem.setEndCustomer(tempCust);
				quoteItem.setEndCustomerName(getCustomerFullName(tempCust));
			} else {

				if (!StringUtils.isEmpty(item.getEndCustomerNameStr())) {
					quoteItem.setEndCustomer(null);
					quoteItem.setEndCustomerName(item.getEndCustomerNameStr());
				} else {
					quoteItem.setEndCustomer(null);
					quoteItem.setEndCustomerName(null);
				}

			}

			if (!QuoteUtil.isEmpty(item.getCustomerGroupId())) {
				quoteItem.setCustomerGroupId(item.getCustomerGroupId());
			} else {
				quoteItem.setCustomerGroupId(null);
			}

			if (!QuoteUtil.isEmpty(item.getSalesCostTypeName())) {
				if (item.getSalesCostTypeName().equals(SalesCostType.ZINC.name())) {
					quoteItem.setSalesCostType(SalesCostType.ZINC);
				} else if (item.getSalesCostTypeName().equals(SalesCostType.ZIND.name())) {
					quoteItem.setSalesCostType(SalesCostType.ZIND);
				} else if (item.getSalesCostTypeName().equals(SalesCostType.NonSC.name())) {
					quoteItem.setSalesCostType(SalesCostType.NonSC);

				}
			} else {
				quoteItem.setSalesCostType(null);
			}

			if (!QuoteUtil.isEmpty(item.getSalesCostStr())) {
				quoteItem.setSalesCost(new BigDecimal(item.getSalesCostStr().trim()));
			} else {
				quoteItem.setSalesCost(null);
			}

			if (!QuoteUtil.isEmpty(item.getSuggestedResaleStr())) {
				quoteItem.setSuggestedResale(new BigDecimal(item.getSuggestedResaleStr().trim()));
			} else {
				quoteItem.setSuggestedResale(null);
			}

			if (!QuoteUtil.isEmpty(item.getCancellationWindowStr())) {
				quoteItem.setCancellationWindow(Integer.parseInt(item.getCancellationWindowStr()));
			} else {
				quoteItem.setCancellationWindow(null);
			}

			if (!QuoteUtil.isEmpty(item.getRescheduleWindowStr())) {
				quoteItem.setRescheduleWindow((Integer.parseInt(item.getRescheduleWindowStr())));
			} else {
				quoteItem.setRescheduleWindow(null);
			}

			quoteSB.updateReferenceMarginForSalesAndCs(quoteItem);// set
																	// reference
																	// Margin

			// PROGRM PRICER ENHANCEMENT
			// item.setQuoteItem(quoteItem);
			item.setQuoteItem(quoteItem, bizUnit);

		}

		return messages;
	}

	public void saveRfqDetailQuotation() {
		String errorMsg = "";

		List<String> messages = validateAndFillQuoteItem(this.selectedQuoteItem);
		List<String> successUpdateItems = null;
		if (messages != null) {
			LOGGER.log(Level.INFO,
					this.selectedQuoteItem.getQuoteItem().getQuoteNumber() + " validation for save is failed");
			for (String message : messages) {
				errorMsg += message + "\n";
			}
		} else {
			List<QuoteItem> draftItems = new ArrayList<QuoteItem>();
			QuoteItem item = this.selectedQuoteItem.getQuoteItem();
			item.getAttachments().addAll(selectedQuoteItem.getQcAttachments());
			draftItems.add(item);
			// add by Lenon.Yang(043044) 2016.05.10
			setLastUpdateFields(draftItems);
			// quoteTransactionSB.updateQuoteItem(draftItems);
			getUpdatedVersionQuoteItem(draftItems);
			successUpdateItems = this.updateQuoteItem(draftItems, ActionEnum.WP_SAVE);
			// QuoteItem quoteItem =
			// quoteSB.updateQuoteItem(this.selectedQuoteItem.getQuoteItem());
			setUpdatedVersionQuoteItem(draftItems);
			QuoteItem quoteItem = this.selectedQuoteItem.getQuoteItem();
			// PROGRM PRICER ENHANCEMENT
			// this.selectedQuoteItem.setQuoteItem(quoteItem);
			this.selectedQuoteItem.setQuoteItem(quoteItem, bizUnit);
			this.selectedQuoteItem.setChanged(false);
			// this.workingPlatformItems.cellChangeListener(String.valueOf(selectedQuoteItem.getQuoteItem().getId()));

			if (this.searchedWorkingPlatformItems != null) {
				for (int i = 0; i < searchedWorkingPlatformItems.size(); i++) {
					if (searchedWorkingPlatformItems.get(i).getQuoteItem().getId() == this.selectedQuoteItem
							.getQuoteItem().getId()) {
						this.searchedWorkingPlatformItems.set(i, this.selectedQuoteItem);
						break;
					}
				}
			} else {
				for (int i = 0; i < searchedWorkingPlatformItems.size(); i++) {
					if (searchedWorkingPlatformItems.get(i).getQuoteItem().getId() == this.selectedQuoteItem
							.getQuoteItem().getId()) {
						this.searchedWorkingPlatformItems.set(i, this.selectedQuoteItem);
						break;
					}
				}
			}
		}
		// this.setworkingPlatformItems(this.workingPlatformItems);
		this.workingPlatformItems.getCacheModifiedItems().remove(this.selectedQuoteItem);
		if (errorMsg.length() > 0) {
			FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR,
					ResourceMB.getText("wq.msg.invalidFormat") + " :", errorMsg);
			FacesContext.getCurrentInstance().addMessage("form_pendinglist:workingPlatformRfqGrowl", msg);
		} else {
			if (null != successUpdateItems && successUpdateItems.size() > 0) {
				String quoteNumbers = "";
				for (String quoteNumber : successUpdateItems) {
					quoteNumbers = quoteNumber + ",";
				}
				quoteNumbers = quoteNumbers.substring(0, quoteNumbers.length() - 1);
				FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_INFO,
						quoteNumbers + QuoteConstant.MESSAGE_SAVED, errorMsg);
				FacesContext.getCurrentInstance().addMessage("form_pendinglist:workingPlatformRfqGrowl", msg);
				RequestContext.getCurrentInstance().execute("updateSaveRfqDetail();");
			}
			selectedQuoteItem.getQcAttachments().clear();
		}
		setEditMode(true);
	}

	/**
	 * this method is used to set updated version for quoteItem in selectedDatas
	 * 
	 * @param draftItems
	 */
	private void setUpdatedVersionQuoteItem(List<QuoteItem> draftItems) {
		for (WorkingPlatformItemVO selectedRecord : workingPlatformItems.getCacheSelectedItems()) {
			if (draftItems.get(0).getId() == selectedRecord.getQuoteItem().getId()) {
				if (selectedRecord.getQuoteItem().getVersion() != draftItems.get(0).getVersion()) {
					selectedRecord.getQuoteItem().setVersion(draftItems.get(0).getVersion());
				}
				break;
			}
		}
	}

	/**
	 * this method is used to get updated version for quoteItem from
	 * selectedDatas
	 * 
	 * @param draftItems
	 */
	private void getUpdatedVersionQuoteItem(List<QuoteItem> draftItems) {
		for (WorkingPlatformItemVO selectedRecord : workingPlatformItems.getCacheSelectedItems()) {
			if (draftItems.get(0).getId() == selectedRecord.getQuoteItem().getId()) {
				if (selectedRecord.getQuoteItem().getVersion() != draftItems.get(0).getVersion()) {
					draftItems.get(0).setVersion(selectedRecord.getQuoteItem().getVersion());
				}
				break;
			}
		}
	}

	public void saveQuotation() {
		boolean isNoError = commonSaveQuotation();
		/* IF validate error ,don't update QuoteItem */
		if (!isNoError) {
			return;
		}
		List<QuoteItem> quoteItems = new ArrayList<QuoteItem>();
		List<Long> quoteItemIds = new ArrayList<Long>();
		if (this.searchedWorkingPlatformItems != null) {
			List<WorkingPlatformItemVO> modifiedDataList = getCacheModifyDataList();
			for (WorkingPlatformItemVO item : searchedWorkingPlatformItems) {
				int index = modifiedDataList.indexOf(item);
				if (index != -1) {
					quoteItems.add(item.getQuoteItem());
					quoteItemIds.add(item.getQuoteItem().getId());
					item.setChanged(false);
					item.getQcAttachments().clear();
				}
			}
		}
		setLastUpdateFields(quoteItems);
		if (quoteItems != null && quoteItems.size() > 0) {
			this.updateQuoteItem(quoteItems, ActionEnum.WP_SAVE);
			refreshQuoteTable();
		}

	}

	public boolean commonSaveQuotation() {
		return commonSaveQuotation(true);
	}

	public boolean commonSaveQuotation(boolean saveAll) {

		boolean succeed = true;

		LOGGER.log(Level.INFO, "PERFORMANCE START - saveQuotation()");
		List<WorkingPlatformItemVO> modifiedDataList = getCacheModifyDataList();
		LOGGER.log(Level.INFO, "modifiedDataList is :" + modifiedDataList.size());
		String errorMsg = "";
		if (this.getCacheSelectionDataList() != null && !saveAll) {
			for (int i = 0; i < getCacheSelectionDataList().size(); i++) {
				WorkingPlatformItemVO item = getCacheSelectionDataList().get(i);
				int index = modifiedDataList.indexOf(item);
				if (index != -1) {
					List<String> messages = validateAndFillQuoteItem(item);
					if (messages != null) {
						LOGGER.log(Level.INFO, item.getQuoteItem().getQuoteNumber() + " validation for save is failed");
						for (String message : messages) {
							errorMsg += message + "\n";
						}
					}
				}
			}
		} else if (this.searchedWorkingPlatformItems != null && saveAll) {
			for (int i = 0; i < searchedWorkingPlatformItems.size(); i++) {
				WorkingPlatformItemVO item = searchedWorkingPlatformItems.get(i);

				int index = modifiedDataList.indexOf(item);
				if (index != -1) {

					List<String> messages = validateAndFillQuoteItem(item);
					if (messages != null) {
						LOGGER.log(Level.INFO, item.getQuoteItem().getQuoteNumber() + " validation for save is failed");
						for (String message : messages) {
							errorMsg += message + "\n";
						}
					}
				}
			}
		}
		if (errorMsg.length() > 0) {
			succeed = false;
			FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR,
					ResourceMB.getText("wq.msg.invalidFormat") + " :", errorMsg);
			FacesContext.getCurrentInstance().addMessage("workingPlatformGrowl", msg);
		}

		return succeed;
	}

	/**
	 * 2)add private function handleNameForQuotationHistory and use it in the
	 * function of searchQuoteHistory
	 ****/
	public void searchQuoteHistory() {
		/*
		 * <<<<<<< .working quotationHistorys =
		 * ejbCommonSB.searchQuoteHistory(quoteHistoryMfrSearch,
		 * quoteHistoryPartNumberSearch, quoteHistorySoldToCustomerNameSearch,
		 * quotationHistorys, quoteSB, bizUnit); ||||||| .merge-left.r21525
		 * =======
		 */

		// logger.log(Level.INFO, "PERFORMANCE START - searchQuoteHistory()");
		if (QuoteUtil.isEmpty(this.quoteHistoryMfrSearch))
			this.quoteHistoryMfrSearch = null;
		if (QuoteUtil.isEmpty(this.quoteHistoryPartNumberSearch))
			this.quoteHistoryPartNumberSearch = null;
		if (QuoteUtil.isEmpty(this.quoteHistorySoldToCustomerNameSearch))
			this.quoteHistorySoldToCustomerNameSearch = null;

		if (quoteHistoryMfrSearch == null && quoteHistoryPartNumberSearch == null
				&& quoteHistorySoldToCustomerNameSearch == null) {
			FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, QuoteConstant.MESSAGE_SEARCH_CRITERIA,
					"One input criteria is needed at least");
			FacesContext.getCurrentInstance().addMessage("form_pendinglist:workingPlatformGrowl", msg);
		} else if (quoteHistoryPartNumberSearch != null && quoteHistoryPartNumberSearch.length() < 3) {
			FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, QuoteConstant.MESSAGE_INVALID_FORMAT,
					"Part Number is Minimum 3 Chars");
			FacesContext.getCurrentInstance().addMessage("form_pendinglist:workingPlatformGrowl", msg);

		} else {
			quotationHistorys = new ArrayList<WorkingPlatformItemVO>();

			List<String> stage = new ArrayList<String>();
			stage.add(QuoteSBConstant.QUOTE_STAGE_FINISH);
			stage.add(QuoteSBConstant.QUOTE_STAGE_PENDING);
			stage.add(QuoteSBConstant.QUOTE_STAGE_INVALID);

			List<QuoteItem> quotationItemHistorys = quoteSB.findQuotesByMaterialsAndBizUnitAndStageAndStatusAndCustomer(
					this.quoteHistoryPartNumberSearch, this.quoteHistoryMfrSearch, bizUnit, stage, null,
					this.quoteHistorySoldToCustomerNameSearch);
			if (quotationItemHistorys != null) {
				for (QuoteItem quoteItem : quotationItemHistorys) {
					WorkingPlatformItemVO item = new WorkingPlatformItemVO();
					// PROGRM PRICER ENHANCEMENT
					// item.setQuoteItem(quoteItem);
					item.setQuoteItem(quoteItem, bizUnit);

					// Only change in QuotationHistoryList page: set Customer
					// from Quote table when Quote stage is INVALID or FINISH.
					handleNameForQuotationHistory(item);

					quotationHistorys.add(item);
				}
			}
		}
		// logger.log(Level.INFO, "PERFORMANCE END - searchQuoteHistory()");
	}

	private void handleNameForQuotationHistory(WorkingPlatformItemVO item) {
		// Only change in QuotationHistoryList page: set Customer
		// from Quote table when Quote stage is INVALID or FINISH.
		if (item == null) {
			return;
		}
		QuoteItem quoteItem = item.getQuoteItem();
		if (quoteItem == null) {
			return;
		}
		String endCustomerName = "";
		if (QuoteSBConstant.QUOTE_STAGE_FINISH.equals(quoteItem.getStage())
				|| QuoteSBConstant.QUOTE_STAGE_INVALID.equals(quoteItem.getStage())) {
			// set CustomerName
			item.setSoldToCustomerNameStr(quoteItem.getQuote().getSoldToCustomerName());
			// for endCustomerName
			if (quoteItem.getId() == 0) {
				if (null == quoteItem.getQuote().getEndCustomer()) {
					endCustomerName = quoteItem.getQuote().getEndCustomerName();
				} else {
					endCustomerName = quoteItem.getQuote().getEndCustomer().getCustomerFullName();
				}
			} else {
				// when the customer name of quote item is not empty, then show
				// it, otherwise we need to extract the customer name from its
				// master table then show master table��s name
				if (!QuoteUtil.isEmpty(quoteItem.getEndCustomerName())) {
					endCustomerName = quoteItem.getEndCustomerName();
				} else {
					if (null != quoteItem.getEndCustomer()) {
						endCustomerName = quoteItem.getEndCustomer().getCustomerFullName();
					}
				}
			}
		} else {
			endCustomerName = quoteItem.getEndCustomer() != null ? quoteItem.getEndCustomer().getCustomerFullName()
					: quoteItem.getEndCustomerName();
		}
		// set EndCustomerName
		item.setEndCustomerName(endCustomerName);
	}

	public void searchVendorQuoteHistory() {

		// logger.log(Level.INFO, "PERFORMANCE START -
		// searchVendorQuoteHistory()");

		if (QuoteUtil.isEmpty(this.vendorQuoteHistoryMfrSearch))
			this.vendorQuoteHistoryMfrSearch = null;
		if (QuoteUtil.isEmpty(this.vendorQuoteHistoryPartNumberSearch))
			this.vendorQuoteHistoryPartNumberSearch = null;
		if (QuoteUtil.isEmpty(this.vendorQuoteHistorySoldToCustomerNameSearch))
			this.vendorQuoteHistorySoldToCustomerNameSearch = null;

		if (vendorQuoteHistoryMfrSearch == null && vendorQuoteHistoryPartNumberSearch == null
				&& vendorQuoteHistorySoldToCustomerNameSearch == null) {
			FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, QuoteConstant.MESSAGE_SEARCH_CRITERIA,
					ResourceMB.getText("wq.message.inputCriteria"));
			FacesContext.getCurrentInstance().addMessage("form_pendinglist:workingPlatformGrowl", msg);
		} else if (vendorQuoteHistoryPartNumberSearch != null && vendorQuoteHistoryPartNumberSearch.length() < 3) {
			FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR,
					ResourceMB.getText("wq.msg.invalidFormat") + " :", ResourceMB.getText("wq.message.minPN"));
			FacesContext.getCurrentInstance().addMessage("form_pendinglist:workingPlatformGrowl", msg);

		} else {
			List<VendorReport> vendorQuotationHistorysTemp = vendorReportSB.findVendorReportByPartNumberAndCustomer(
					this.vendorQuoteHistoryMfrSearch, this.vendorQuoteHistoryPartNumberSearch,
					this.vendorQuoteHistorySoldToCustomerNameSearch, this.bizUnit);
			if (vendorQuotationHistorysTemp != null && vendorQuotationHistorysTemp.size() > 0) {
				for (VendorReport vr : vendorQuotationHistorysTemp) {
					vr.setAuthQty(formatNumber(vr.getAuthQty()));

					vr.setConsumedQty(formatNumber(vr.getConsumedQty()));

					vr.setOpenQty(formatNumber(vr.getOpenQty()));
				}
			}
			vendorQuotationHistorys = vendorQuotationHistorysTemp;
		}

		// logger.log(Level.INFO, "PERFORMANCE END -
		// searchVendorQuoteHistory()");

	}

	public void searchPosHistory() {

		if (QuoteUtil.isEmpty(this.posHistoryMfrSearch))
			this.posHistoryMfrSearch = null;
		if (QuoteUtil.isEmpty(this.posHistoryPartNumberSearch))
			this.posHistoryPartNumberSearch = null;
		if (QuoteUtil.isEmpty(this.posHistorySoldToCustomerNameSearch))
			this.posHistorySoldToCustomerNameSearch = null;

		if (posHistoryMfrSearch == null && posHistoryPartNumberSearch == null
				&& posHistorySoldToCustomerNameSearch == null) {
			FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, QuoteConstant.MESSAGE_SEARCH_CRITERIA,
					ResourceMB.getText("wq.message.inputCriteria"));
			FacesContext.getCurrentInstance().addMessage("form_pendinglist:workingPlatformGrowl", msg);
		} else if (posHistoryPartNumberSearch != null && posHistoryPartNumberSearch.length() < 3) {
			FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR,
					ResourceMB.getText("wq.msg.invalidFormat") + " :", ResourceMB.getText("wq.message.minPN"));
			FacesContext.getCurrentInstance().addMessage("form_pendinglist:workingPlatformGrowl", msg);

		} else {
			posSummary = null;
			LOGGER.log(Level.INFO, "Get POS by Material = " + this.posHistoryMfrSearch + " / "
					+ this.posHistoryPartNumberSearch + " and customer = " + this.posHistorySoldToCustomerNameSearch);
			posSummary = posSB.findPosSummary(posHistoryMfrSearch, posHistoryPartNumberSearch,
					posHistorySoldToCustomerNameSearch);
			if (posSummary == null) {
				LOGGER.log(Level.INFO, " 0 record(s) is/are found for POS.");
			} else {
				LOGGER.log(Level.INFO, posSummary.size() + " record(s) is/are found for POS.");
			}
		}
	}

	public List<String> autoCompletePartList(String key) {

		// logger.log(Level.INFO, "PERFORMANCE START -
		// autoCompletePartList(String key)");

		FacesContext context = FacesContext.getCurrentInstance();
		WorkingPlatformItemVO item = (WorkingPlatformItemVO) UIComponent.getCurrentComponent(context).getAttributes()
				.get("targetQuoteItem");

		List<BizUnit> bizUnits = new ArrayList<BizUnit>();
		bizUnits.add(this.bizUnit);

		if (item != null) {
			List<Material> materials = materialSB.wFindMaterialByMfrPartNumberWithPage(key, bizUnits,
					QuoteConstant.DEFAULT_AUTOCOMPLETE_FIRST_RESULT, QuoteConstant.DEFAULT_AUTOCOMPLETE_MAX_RESULTS);
			if (materials != null) {
				List<String> materialList = new ArrayList<String>();
				for (Material material : materials) {
					if (material.getManufacturer().getName().equals(item.getQuoteItem().getRequestedMfr().getName())
							&& (material.getValid() == null || material.getValid() == true)) {
						materialList.add(material.getFullMfrPartNumber());
					}
				}
				// logger.log(Level.INFO, "PERFORMANCE END -
				// autoCompletePartList(String key)");
				return materialList;
			}
		}
		LOGGER.log(Level.INFO, "PERFORMANCE END - autoCompletePartList(String key)");
		return null;

	}

	public void validateInvalidQuotation() {

		RequestContext context = RequestContext.getCurrentInstance();

		if (!commonSaveQuotation(false))
			return;
		context.addCallbackParam("saveValidationSucceed", "1");

		quotationWarnings = new ArrayList<QuotationWarningVO>();
		// NEC Pagination changes: get the selected data from cache
		if (this.workingPlatformItems.getCacheSelectedItems() != null) {
			// NEC Pagination changes: get the selected data from cache
			for (WorkingPlatformItemVO item : workingPlatformItems.getCacheSelectedItems()) {
				if (item != null) {
					QuotationWarningVO quotationWarningVO = new QuotationWarningVO();

					String quoteNumber = item.getQuoteItem().getQuoteNumber();
					String qcExternalComment = item.getQuoteItem().getAqcc();

					boolean mpqError = false;
					boolean moqError = false;
					boolean movError = false;
					boolean quotedQtyError = false;
					boolean quotedPriceError = false;
					boolean leadTimeError = false;
					boolean costIndicatorError = false;
					boolean qtyIndicatorError = false;
					boolean priceValidityError = false;
					boolean costError = false;
					boolean shipmentValidityError = false;
					boolean mfrQuoteQtyError = false;
					boolean aqQtyError = false;
					boolean pmoqError = false;

					boolean quotedPriceWarning = false;
					boolean marginWarning = false;
					boolean quotedQtyWarning = false;
					boolean costWarning = false;
					boolean shipmentValidityWarning = false;

					boolean qcExternalCommentError = false;

					List<String> messages = new ArrayList<String>();

					if (QuoteUtil.isEmpty(qcExternalComment)) {
						qcExternalCommentError = true;
						messages.add(ResourceMB.getText("wq.label.qcCommentInvalidate") + ".");
					}

					if (!(qcExternalCommentError)) {
						quotationWarningVO.setProceed(true);
					} else {
						quotationWarningVO.setProceed(false);
					}
					quotationWarningVO.setInvalidWorkingPlatformItemVO(item);
					quotationWarningVO = validateInvalidQuotation(quotationWarningVO, mpqError, moqError, movError,
							quotedQtyError, quotedPriceError, leadTimeError, costIndicatorError, qtyIndicatorError,
							priceValidityError, costError, shipmentValidityError, mfrQuoteQtyError, aqQtyError,
							pmoqError, quotedPriceWarning, marginWarning, quotedQtyWarning, costWarning,
							shipmentValidityWarning, qcExternalCommentError, false, null, false);
					quotationWarningVO.setMessages(messages);
					quotationWarnings.add(quotationWarningVO);

				}
			}
		}
		this.quoteItemCount = quotationWarnings.size();
		if (quotationWarnings.size() == 0)
			quotationWarnings = null;
	}

	public void validateQuotation() {

		// logger.log(Level.INFO, "PERFORMANCE START - validateQuotation()");
		RequestContext context = RequestContext.getCurrentInstance();
		getRestrictedCustomerList();
		if (!commonSaveQuotation(false))
			return;
		context.addCallbackParam("saveValidationSucceed", "1");

		quotationWarnings = new ArrayList<QuotationWarningVO>();
		// NEC Pagination changes: get the selected data from cache
		if (workingPlatformItems.getCacheSelectedItems() != null) {

			List<QuoteItem> quoteItems = new ArrayList<QuoteItem>();
			// NEC Pagination changes: get the selected data from cache
			for (WorkingPlatformItemVO item : workingPlatformItems.getCacheSelectedItems()) {
				quoteItems.add(item.getQuoteItem());
			}

			boolean enquiryDRMSSucceed = true;
			try {
				sapWebServiceSB.enquirySAPAGP(quoteItems);
			} catch (AppException e) {
				LOGGER.log(Level.SEVERE, "Error occured in validating Quoatation, Reason for failure is "
						+ MessageFormatorUtil.getParameterizedStringFromException(e), e);
				enquiryDRMSSucceed = false;
			}

			// NEC Pagination changes: get the selected data from cache
			for (WorkingPlatformItemVO item : workingPlatformItems.getCacheSelectedItems()) {
				if (item != null) {
					QuotationWarningVO quotationWarningVO = new QuotationWarningVO();

					String quoteNumber = item.getQuoteItem().getQuoteNumber();
					String quotedPartnumber = null;
					if (item.getQuoteItem().getQuotedMaterial() != null)
						quotedPartnumber = item.getQuoteItem().getQuotedMaterial().getFullMfrPartNumber();
					String costIndicator = item.getQuoteItem().getCostIndicator();
					String qtyIndicator = item.getQuoteItem().getQtyIndicator();
					String priceValidity = item.getQuoteItem().getPriceValidity();
					Double cost = item.getQuoteItem().getCost();
					Double quotedPrice = item.getQuoteItem().getQuotedPrice();
					Integer quotedQty = item.getQuoteItem().getQuotedQty();
					// Double targetPrice =
					// item.getQuoteItem().getTargetResale();
					Double margin = null;
					if (cost != null && quotedPrice != null) {
						margin = 100 * (quotedPrice - cost) / quotedPrice;
					}
					Date shipmentValidity = item.getQuoteItem().getShipmentValidity();
					Integer vendorQuoteQty = item.getQuoteItem().getVendorQuoteQty();
					String vendorDebitNumber = item.getQuoteItem().getVendorDebitNumber();

					Integer mpq = item.getQuoteItem().getMpq();
					Integer moq = item.getQuoteItem().getMoq();
					Integer mov = item.getQuoteItem().getMov();

					String qcExternalComment = item.getQuoteItem().getAqcc();
					String endCustomerName = item.getQuoteItem().getEndCustomerName();
					String endCustomerCode = null;
					if (item.getQuoteItem().getEndCustomer() != null) {
						endCustomerCode = item.getQuoteItem().getEndCustomer().getCustomerNumber();
					}
					Date quoteEffectiveDate = item.getQuoteItem().getQuotationEffectiveDate();
					boolean mpqError = false;
					boolean moqError = false;
					boolean movError = false;
					boolean quotedQtyError = false;
					boolean quotedPriceError = false;
					boolean leadTimeError = false;
					boolean costIndicatorError = false;
					boolean qtyIndicatorError = false;
					boolean priceValidityError = false;
					boolean costError = false;
					boolean shipmentValidityError = false;
					boolean mfrQuoteQtyError = false;
					boolean aqQtyError = false;
					boolean pmoqError = false;
					boolean quotedPriceWarning = false;
					boolean marginWarning = false;
					boolean quotedQtyWarning = false;
					boolean costWarning = false;
					boolean shipmentValidityWarning = false;
					boolean restrictCustomerWarning = false;
					boolean qcExternalCommentError = false;

					boolean drmsError = false;

					boolean endCustomerMatchError = false;
					boolean quoteEffectiveDateError = false;

					boolean isProgramItem = false;
					if (item.getQuoteItem().getMaterialTypeId() != null && item.getQuoteItem().getMaterialTypeId()
							.equalsIgnoreCase(QuoteSBConstant.MATERIAL_TYPE_PROGRAM)) {
						isProgramItem = true;
					}

					List<String> messages = new ArrayList<String>();

					if (item.getValidFlagStr().equals(QuoteConstant.OPTION_YES)) {
						if (item.getQuoteItem().getMpq() == null || item.getQuoteItem().getMpq() == 0) {
							mpqError = true;
							messages.add(ResourceMB.getText("wq.label.mpqIsBlankNotAllowed") + ".");
						}
						if (item.getQuoteItem().getMoq() == null || item.getQuoteItem().getMoq() == 0) {
							moqError = true;
						}
						if (item.getQuoteItem().getMov() == null || item.getQuoteItem().getMov() == 0) {
							movError = true;
						}
						if (!(moqError && movError)) {
							moqError = false;
							movError = false;
						} else {
							messages.add(ResourceMB.getText("wq.label.mpqAndMovBlankNotAllowed") + ".");
						}

						if (item.getQuoteItem().getMpq() != null && item.getQuoteItem().getMoq() != null
								&& item.getQuoteItem().getMpq() > item.getQuoteItem().getMoq()) {
							mpqError = true;
							messages.add(ResourceMB.getText("wq.label.mpqBlankNotAllowed") + ".");
						}

						if ((item.getQuoteItem().getQuotedQty() == null || item.getQuoteItem().getQuotedQty() == 0)
								&& item.getQuoteItem().getQtyIndicator() != null
								&& item.getQuoteItem().getQtyIndicator().equals(QuoteConstant.QTY_INDICATOR_EXACT)) {
							quotedQtyError = true;
							messages.add(ResourceMB.getText("wq.message.quoteQtyBlank") + ".");
						}

						if ((item.getQuoteItem().isSalesCostFlag() != true)
								|| (item.getQuoteItem().isSalesCostFlag() == true
										&& SalesCostType.NonSC.equals(item.getQuoteItem().getSalesCostType()))) {
							if (item.getQuoteItem().getQuotedPrice() == null
									|| (item.getQuoteItem().getQuotedPrice() != null
											&& item.getQuoteItem().getQuotedPrice() == 0.0)) {
								quotedPriceError = true;
								messages.add(ResourceMB.getText("wq.label.quotedPriceBlankNotAllowed") + ".");
							}
						}

						if (QuoteUtil.isEmpty(item.getQuoteItem().getLeadTime())) {
							leadTimeError = true;
							messages.add(ResourceMB.getText("wq.label.leadTimeBlankNotAllowed") + ".");
						}
						if (QuoteUtil.isEmpty(costIndicator)) {
							costIndicatorError = true;
							messages.add(ResourceMB.getText("wq.label.costIndicatorBlankNotAllowed") + ".");
						}
						if (QuoteUtil.isEmpty(qtyIndicator)) {
							qtyIndicatorError = true;
							messages.add(ResourceMB.getText("wq.label.qtyIndicatorBlankNotAllowed") + ".");
						} else if (qtyIndicator.toUpperCase().trim().equals("LIMIT")) {
							if (item.getQuoteItem().getPricerId() == 0L) {
								qtyIndicatorError = true;
							} else {
								Pricer pricer = materialSB.findPricerById(item.getQuoteItem().getPricerId());
								if (pricer == null || !(pricer instanceof QtyBreakPricer)) {
									qtyIndicatorError = true;
								}
							}
							if (qtyIndicatorError) {
								messages.add(ResourceMB.getText("wq.label.qtyIndicatorNormalQuoteItem") + ".");
							}
						}
						if (QuoteUtil.isEmpty(priceValidity)) {
							priceValidityError = true;
							messages.add(ResourceMB.getText("wq.label.priceValidityBlank") + ".");
						} else {
							Date validity = null;
							Date quoteExpiryDate = null;

							if (item.getQuoteItem().getPriceValidity().matches("[0-9]{1,}")) {
								int shift = Integer.parseInt(item.getQuoteItem().getPriceValidity());
								// added by DamonChen@20180627
								int priceValidityMax = Integer
										.parseInt(sysConfigSB.getProperyValue(QuoteConstant.PRICE_VALIDITY_NUM_MAX));
								if (shift > priceValidityMax) {
									priceValidityError = true;
									messages.add(ResourceMB.getText("wq.message.PriceValidityOutLimit"));
								}
								validity = QuoteUtil.shiftDate(QuoteUtil.getCurrentTime(), shift);

								if (isProgramItem) {
									quoteExpiryDate = QuoteUtil.shiftDate(validity,
											QuoteSBConstant.QUOTE_EXPIRY_DATE_SHIFT_PROGRAM);
								} else {
									quoteExpiryDate = QuoteUtil.shiftDate(validity,
											QuoteSBConstant.QUOTE_EXPIRY_DATE_SHIFT_NORMAL);
								}

								if (quoteExpiryDate == null || (quoteExpiryDate != null && quoteExpiryDate
										.before(QuoteUtil.getStartOfDay(QuoteUtil.getCurrentTime())))) {
									priceValidityError = true;
									messages.add(ResourceMB.getText("wq.label.priceValidityExpired") + ".");
								}
							} else {
								dateFormatOfDMY.setLenient(false);
								try {
									validity = dateFormatOfDMY.parse(item.getQuoteItem().getPriceValidity());

									if (isProgramItem) {
										quoteExpiryDate = QuoteUtil.shiftDate(validity,
												QuoteSBConstant.QUOTE_EXPIRY_DATE_SHIFT_PROGRAM);
									} else {
										quoteExpiryDate = QuoteUtil.shiftDate(validity,
												QuoteSBConstant.QUOTE_EXPIRY_DATE_SHIFT_NORMAL);
									}

									if (quoteExpiryDate == null || (quoteExpiryDate != null && quoteExpiryDate
											.before(QuoteUtil.getStartOfDay(QuoteUtil.getCurrentTime())))) {
										priceValidityError = true;
										messages.add(ResourceMB.getText("wq.label.priceValidityExpired") + ".");
									}
								} catch (ParseException e) {
									LOGGER.log(Level.WARNING, e.getMessage());
									priceValidityError = true;
									messages.add(ResourceMB.getText("wq.label.priceValidityInvalidFormat") + ".");
								}
							}

						}

						if (quoteEffectiveDate != null && !QuoteUtil.isEmpty(priceValidity)) {
							Date validity = null;

							if (item.getQuoteItem().getPriceValidity().matches("[0-9]{1,}")) {
								int shift = Integer.parseInt(item.getQuoteItem().getPriceValidity());
								validity = QuoteUtil.shiftDate(QuoteUtil.getCurrentTime(), shift);
								if (quoteEffectiveDate.after(validity)) {
									quoteEffectiveDateError = true;
									messages.add(ResourceMB.getText("wq.label.quotationEffectiveDatePrior") + ".");
								}
							} else {
								dateFormatOfDMY.setLenient(false);
								try {
									validity = dateFormatOfDMY.parse(item.getQuoteItem().getPriceValidity());
									if (quoteEffectiveDate.after(validity)) {
										quoteEffectiveDateError = true;
										messages.add(ResourceMB.getText("wq.label.quotationEffectiveDatePrior") + ".");
									}
								} catch (ParseException e) {
									LOGGER.log(Level.WARNING,
											"Error occured while validating quotation for Quoted MFR: "
													+ item.getQuotedMfr() + ", Reason for failure: " + e.getMessage(),
											e);
									priceValidityError = true;
									messages.add(ResourceMB.getText("wq.label.priceValidityInvalidFormat") + ".");
								}
							}
						}

						if (!QuoteUtil.isEmpty(vendorDebitNumber) && QuoteUtil.isEmpty(vendorQuoteQty)) {
							mfrQuoteQtyError = true;
							messages.add(ResourceMB.getText("wq.message.mrfBlank") + ".");
						}
						if (cost == null) {
							costError = true;
							messages.add(ResourceMB.getText("wq.label.costIsBlank") + ".");
						} else if (cost == 0) {
							costWarning = true;
							messages.add(ResourceMB.getText("wq.label.costZero") + ".");
						}

						String pmoq = null;
						Integer aqQty = null;
						if (item.getQuoteItem().getQuotedQty() == null || item.getQuoteItem().getQuotedQty() == 0) {
							aqQty = QuoteUtil.findAqQty(item.getQuoteItem());
							ProgramPricer tempPm = materialSB.getProgramMat(item.getQuoteItem().getQuotedMaterial(),
									bizUnit, item.getQuoteItem().getCostIndicator());
							pmoq = QuoteUtil.findPmoqWithPM(item.getQuoteItem(), bizUnit, tempPm);
						} else {
							aqQty = item.getQuoteItem().getQuotedQty();
							ProgramPricer tempPm = materialSB.getProgramMat(item.getQuoteItem().getQuotedMaterial(),
									bizUnit, item.getQuoteItem().getCostIndicator());
							pmoq = QuoteUtil.findPmoqWithPM(item.getQuoteItem(), item.getQuoteItem().getQuotedQty(),
									bizUnit, tempPm);
						}

						if (pmoq == null) {

							// Fixed by DamonChen@20180411
							if (!QuoteUtil.isEmpty(qtyIndicator)) {
								if (qtyIndicator.endsWith("%")) {

									if (moq == null || (moq != null && moq == 0)) {
										pmoqError = true;
										if (!messages.contains(ResourceMB.getParameterizedText(
												"wq.label.moqZeroQtyIndicator", qtyIndicator) + "."))
											messages.add(ResourceMB.getParameterizedText("wq.label.moqZeroQtyIndicator",
													qtyIndicator) + ".");
									}

								} else if (qtyIndicator.toUpperCase()
										.equals(QuoteConstant.QTY_INDICATOR_MOQ.toUpperCase())) {

									pmoqError = true;
									if (!messages.contains(ResourceMB
											.getParameterizedText("wq.label.moqZeroQtyIndicator", qtyIndicator) + "."))
										messages.add(ResourceMB.getParameterizedText("wq.label.moqZeroQtyIndicator",
												qtyIndicator) + ".");

								} else if (qtyIndicator.toUpperCase()
										.equals(QuoteConstant.QTY_INDICATOR_MOV.toUpperCase())) {

									if (mov == null || (mov != null && mov == 0)) {
										pmoqError = true;
										if (!messages.contains(ResourceMB.getParameterizedText(
												"wq.label.movZeroQtyIndicator", qtyIndicator) + "."))
											messages.add(ResourceMB.getParameterizedText("wq.label.movZeroQtyIndicator",
													qtyIndicator) + ".");
									}

									if (cost == null || (cost != null && cost == 0)) {
										pmoqError = true;
										if (!messages.contains(ResourceMB.getParameterizedText(
												"wq.label.costZeroQtyIndicator", qtyIndicator) + "."))
											messages.add(ResourceMB.getParameterizedText(
													"wq.label.costZeroQtyIndicator", qtyIndicator) + ".");
									}

								} /*
									 * else
									 * if(qtyIndicator.toUpperCase().equals(
									 * QuoteConstant.QTY_INDICATOR_LIMIT.
									 * toUpperCase())){
									 * 
									 * pmoqError = true;
									 * if(!messages.contains(ResourceMB.
									 * getParameterizedText(
									 * "wq.label.qtyBreakZeroQtyIndicator",
									 * qtyIndicator)+"."))
									 * messages.add(ResourceMB.
									 * getParameterizedText(
									 * "wq.label.qtyBreakZeroQtyIndicator",
									 * qtyIndicator)+".");
									 * 
									 * }
									 */
							}
						}

						if (aqQty == null) {
							// Fixed by DamonChen@20180411
							if (!QuoteUtil.isEmpty(qtyIndicator)) {
								if (qtyIndicator.endsWith("%")) {

									if (moq == null || (moq != null && moq == 0)) {
										aqQtyError = true;
										if (!messages.contains(ResourceMB.getParameterizedText(
												"wq.label.moqZeroQtyIndicator", qtyIndicator) + "."))
											messages.add(ResourceMB.getParameterizedText("wq.label.moqZeroQtyIndicator",
													qtyIndicator) + ".");

									}

								} else if (qtyIndicator.toUpperCase()
										.equals(QuoteConstant.QTY_INDICATOR_MOQ.toUpperCase())) {

									if (moq == null || (moq != null && moq == 0)) {
										aqQtyError = true;
										if (!messages.contains(ResourceMB.getParameterizedText(
												"wq.label.moqZeroQtyIndicator", qtyIndicator) + "."))
											messages.add(ResourceMB.getParameterizedText("wq.label.moqZeroQtyIndicator",
													qtyIndicator) + ".");

									}

								} else if (qtyIndicator.toUpperCase()
										.equals(QuoteConstant.QTY_INDICATOR_MPQ.toUpperCase())) {

									if (moq == null || (moq != null && moq == 0)) {
										aqQtyError = true;
										if (!messages.contains(ResourceMB.getParameterizedText(
												"wq.label.moqZeroQtyIndicator", qtyIndicator) + "."))
											messages.add(ResourceMB.getParameterizedText("wq.label.moqZeroQtyIndicator",
													qtyIndicator) + ".");

									}

								} else if (qtyIndicator.toUpperCase()
										.equals(QuoteConstant.QTY_INDICATOR_MOV.toUpperCase())) {

									if (mov == null || (mov != null && mov == 0)) {
										aqQtyError = true;
										if (!messages.contains(ResourceMB.getParameterizedText(
												"wq.label.movZeroQtyIndicator", qtyIndicator) + "."))
											messages.add(ResourceMB.getParameterizedText("wq.label.movZeroQtyIndicator",
													qtyIndicator) + ".");

									}

									if (cost == null || (cost != null && cost == 0)) {
										aqQtyError = true;
										if (!messages.contains(ResourceMB.getParameterizedText(
												"wq.label.costZeroQtyIndicator", qtyIndicator) + "."))
											messages.add(ResourceMB.getParameterizedText(
													"wq.label.costZeroQtyIndicator", qtyIndicator) + ".");

									}

								}
							}
						}
						// WQ 2.2 enhancement , end customer checking.
						/*
						 * INC0155169 if(!StringUtils.isEmpty(endCustomerCode)
						 * && !StringUtils.isEmpty(endCustomerName)) { Customer
						 * customer = customerSB.findByPK(endCustomerCode);
						 * if(customer==null ||
						 * !endCustomerName.equalsIgnoreCase(getCustomerFullName
						 * (customer))) {
						 * 
						 * endCustomerMatchError = true; messages.
						 * add("End Customer Code and Name do not match."); } }
						 */

						/*
						 * BigDecimal tResaleBuy = item.getQuoteItem()
						 * .convertToBuyValueWithDouble(item.getQuoteItem().
						 * getTargetResale()); Double targetPriceAsBuy;
						 * 
						 * // Double targetPriceAsBuy = //
						 * item.getQuoteItem().getTargetResaleBuyCur(); //Tough
						 * // request to remove repeated function if (tResaleBuy
						 * != null) targetPriceAsBuy = tResaleBuy.doubleValue();
						 * else targetPriceAsBuy = null;
						 */
						//TODO
						BigDecimal tResaleBuy = item.getQuoteItem().convertToBuyValueWithDouble(
								item.getQuoteItem().getTargetResale()); 
						Double targetPriceAsBuy = null;
						if (tResaleBuy!= null) targetPriceAsBuy = tResaleBuy.doubleValue();
						if(quotedPrice != null && targetPriceAsBuy != null) {
							if (quotedPrice < targetPriceAsBuy) {
								quotedPriceWarning = true;
								messages.add(ResourceMB.getText("wq.label.quotedPriceLessTargetPrice") + ".");
							}else if (quotedPrice.doubleValue() == targetPriceAsBuy.doubleValue()) {
								//reset quotedPrice as 10 decaiml point to let convert to RFQ CURR like 
								//the same as target price of RFQ CURR 
								BigDecimal tResaleBuyWithMorePoint = item.getQuoteItem().convertToBuyValueWithDoubleAndScale(
										item.getQuoteItem().getTargetResale(), Constants.SCALE_FOR_QUOTEDPRICE);
								if(tResaleBuyWithMorePoint != null)
								{
									item.getQuoteItem().setQuotedPrice(tResaleBuyWithMorePoint.doubleValue());
									LOGGER.config(() -> "tResaleBuyWithMorePoint::" + tResaleBuyWithMorePoint.doubleValue());
									LOGGER.config(() -> {
										BigDecimal quotedPriceRfq = item.getQuoteItem().convertToRfqValueWithDouble(tResaleBuyWithMorePoint.doubleValue());
										return "Use more point data to set QuotedPrice. Then rfq QuotedPrice is :::" + quotedPriceRfq.doubleValue();
									});
								}
							}
						}
						
						/*BigDecimal quotedPriceRfq = item.getQuoteItem().convertToRfqValueWithDouble(quotedPrice);
						Double qbRfq = quotedPriceRfq == null ? null : quotedPriceRfq.doubleValue();
						Double targetResale = item.getQuoteItem().getTargetResale();
						//quotedPriceRfq.compareTo(BigDecimal.valueOf(targetResale))<0;
						if (qbRfq != null && targetResale != null && qbRfq < targetResale) {
							quotedPriceWarning = true;
							messages.add(ResourceMB.getText("wq.label.quotedPriceLessTargetPrice") + ".");
						}*/
						if (margin != null && (margin <= QuoteConstant.MARGIN_LOWER_LIMIT
								|| margin > QuoteConstant.MARGIN_UPPER_LIMIT)) {
							marginWarning = true;
							messages.add("MARGIN is " + df2.format(margin) + "%.");
						}
						if (quotedQty != null && quotedQty > QuoteConstant.QUOTED_QTY_UPPER_LIMIT) {
							quotedQtyWarning = true;
							messages.add(ResourceMB.getParameterizedText("wq.label.quotedQtyGreater",
									QuoteConstant.QUOTED_QTY_UPPER_LIMIT_STRING));
						}

						if (shipmentValidity != null) {

							Date shipmentValidityStart = QuoteUtil.getStartOfDay(QuoteUtil.getCurrentTime());

							if (shipmentValidity.before(shipmentValidityStart)) {
								shipmentValidityError = true;
								messages.add(ResourceMB.getText("wq.label.shipmentValidityAllowed") + ".");
							} else {
								Date shipmentValidityEnd = QuoteUtil
										.getStartOfDay(QuoteUtil.shiftDate(QuoteUtil.getCurrentTime(), 8));
								if (shipmentValidity.before(shipmentValidityEnd)) {
									shipmentValidityWarning = true;
									messages.add(ResourceMB.getText("wq.label.oneWeekShipmentValidity") + ".");
								}

							}
						}

						// WQ 2.2 enhancement , restricted customer checking.
						StringBuffer errorMessageSb = new StringBuffer();

						if (hasMatchedRetrictedCustomer(item.getQuoteItem())) {
							// logger.log(Level.INFO, "PERFORMANCE - add
							// message");
							// <MFR>,<P/N>,<Cost Indicator>,<Product Group 1-4>
							errorMessageSb.append("&lt;").append(item.getQuoteItem().getQuote().getBizUnit().getName())
							.append("&gt;,&lt;").append(item.getQuoteItem().getRequestedMfr().getName())
									.append("&gt;,&lt;").append(item.getQuoteItem().getRequestedPartNumber())
									
									.append("&gt;,&lt;")
									.append(((item.getQuoteItem().getProductGroup1() != null
											&& !StringUtils.isEmpty(item.getQuoteItem().getProductGroup1().getName()))
													? item.getQuoteItem().getProductGroup1().getName() : ""))
									.append(",")
									.append(((item.getQuoteItem().getProductGroup2() != null
											&& !StringUtils.isEmpty(item.getQuoteItem().getProductGroup2().getName()))
													? item.getQuoteItem().getProductGroup2().getName() : ""))
									.append(",")
									.append(!StringUtils.isEmpty(item.getQuoteItem().getProductGroup3())
											? item.getQuoteItem().getProductGroup3() : "")
									.append(",")
									.append(!StringUtils.isEmpty(item.getQuoteItem().getProductGroup4())
											? item.getQuoteItem().getProductGroup4() : "")
									.append(" ").append("&gt;")
									.append(" " + ResourceMB.getText("wq.label.restrictedSoldToCustomer"));
						}

						if (!StringUtils.isEmpty(errorMessageSb.toString())) {
							messages.add(errorMessageSb.toString());
							restrictCustomerWarning = true;
						}
					} else {
						if (QuoteUtil.isEmpty(qcExternalComment)) {
							qcExternalCommentError = true;
							messages.add(ResourceMB.getText("wq.label.qcCommentInvalidate") + ".");
						}
					}

					Boolean valid = item.getQuoteItem().getValidFlag();
					if (com.avnet.emasia.webquote.utilities.util.QuoteUtil.getDrmsKey(item.getQuoteItem()) != null
							&& (valid != null && valid.equals(true))) {

						if (enquiryDRMSSucceed == false) {
							messages.add(ResourceMB.getText("wq.label.drmsSAP"));
							drmsError = true;
						} else {

							if (item.getQuoteItem().getDrmsUpdated() == false) {
								drmsError = true;
								messages.add(item.getQuoteItem().getDrmsUpdateFailedDesc());
							} else {
								// Bryan Start
								// Added by Lenon(043044) 2016/11/30
								// DrmsValidationUpdateStrategy
								// drmsValidationUpdateStrategy =
								// DrmsValidationUpdateStrategyFactory.getInstance()
								// .getDrmsValidationUpdateStrategy(user.getDefaultBizUnit().getName());
								DrmsValidationUpdateStrategy drmsValidationUpdateStrategy = cacheUtil
										.getDrmsValidationUpdateStrategy(user.getDefaultBizUnit().getName());
								// Bryan End
								String msg = drmsValidationUpdateStrategy.compareQGPAndAGP(item.getQuoteItem());
								if (org.apache.commons.lang.StringUtils.isNotBlank(msg)) {
									messages.add(msg);
									drmsError = true;
								}
							}
						}
					}

					// For sales cost project by DamonChen

					boolean salesCostDataError = false;
					if (item.getValidFlagStr().equals(QuoteConstant.OPTION_YES)) {

						salesCostDataError = validateSalesCostData(item.getQuoteItem(), messages);

						if (item.getQuoteItem().getSalesCost() != null && item.getQuoteItem().getCost() != null) {
							if (item.getQuoteItem().getSalesCost()
									.compareTo(new BigDecimal(item.getQuoteItem().getCost())) == -1) {

								marginWarning = true;
								messages.add(ResourceMB.getText("wq.message.salesCostBelowCost") + ".");
							}
						}
						if (item.getQuoteItem().isSalesCostFlag() && item.getQuoteItem().getSalesCostType() != null) {
							if (!SalesCostType.ZIND.equals(item.getQuoteItem().getSalesCostType())) {
								// LOGGER.log(Level.INFO,
								// "quoteItem.getCostIndicator() : " +
								// item.getQuoteItem().getCostIndicator());
								// LOGGER.log(Level.INFO, "it is true or false :
								// " +
								// item.getQuoteItem().getCostIndicator().startsWith("B-"));
								if (item.getQuoteItem().getCostIndicator() != null
										&& (item.getQuoteItem().getCostIndicator().startsWith("B-"))) {
									marginWarning = true;
									messages.add(ResourceMB.getText("wq.message.NotMatchSelectedCostIndicator"));
								}
							}

						}
					}

					if (!salesCostDataError && !(mpqError || moqError || movError || quotedQtyError || quotedPriceError
							|| leadTimeError || costIndicatorError || qtyIndicatorError || priceValidityError
							|| costError || shipmentValidityError || mfrQuoteQtyError || aqQtyError || pmoqError
							|| quotedPriceWarning || marginWarning || quotedQtyWarning || costWarning
							|| shipmentValidityWarning || restrictCustomerWarning || qcExternalCommentError || drmsError
							|| endCustomerMatchError || quoteEffectiveDateError)) {
						quotationWarningVO.setProceed(true);
					} else {
						quotationWarningVO.setProceed(false);
					}

					if (item.getValidFlagStr().equals(QuoteConstant.OPTION_YES)) {
						quotationWarningVO.setWorkingPlatformItemVO(item);
					} else {
						quotationWarningVO.setInvalidWorkingPlatformItemVO(item);
					}
					quotationWarningVO = validateInvalidQuotation(quotationWarningVO, mpqError, moqError, movError,
							quotedQtyError, quotedPriceError, leadTimeError, costIndicatorError, qtyIndicatorError,
							priceValidityError, costError, shipmentValidityError, mfrQuoteQtyError, aqQtyError,
							pmoqError, quotedPriceWarning, marginWarning, quotedQtyWarning, costWarning,
							shipmentValidityWarning, qcExternalCommentError, quoteEffectiveDateError,
							"validateQuotation", salesCostDataError);
					quotationWarningVO.setDrmsError(drmsError);
					// WQ 2.2 enhancement , end customer checking.
					quotationWarningVO.setEndCustomerMatchError(endCustomerMatchError);
					quotationWarningVO.setRestrictCustomerWarning(restrictCustomerWarning);

					quotationWarningVO.setMessages(messages);

					quotationWarnings.add(quotationWarningVO);
				}
			}
		}
		this.quoteItemCount = quotationWarnings.size();
		if (quotationWarnings.size() == 0)
			quotationWarnings = null;

		// logger.log(Level.INFO, "PERFORMANCE END - validateQuotation()");

	}

	private boolean validateSalesCostData(QuoteItem quoteItem, List<String> messages) {

		boolean isError = false;
		if (quoteItem.getSalesCost() == null && quoteItem.isSalesCostFlag() && quoteItem.getSalesCostType() != null
				&& (!SalesCostType.NonSC.equals(quoteItem.getSalesCostType()))) {
			isError = true;
			messages.add(ResourceMB.getText("wq.message.salesCostBlank") + ".");
		}

		if (quoteItem.getSalesCostType() != null) {
			if ((!SalesCostType.ZINC.equals(quoteItem.getSalesCostType()))
					&& (!SalesCostType.ZIND.equals(quoteItem.getSalesCostType())
							&& (!SalesCostType.NonSC.equals(quoteItem.getSalesCostType())))) {
				isError = true;
				messages.add(ResourceMB.getText("wq.message.salesCostTypeInvalid") + ".");
			}

		}

		if (quoteItem.isSalesCostFlag() == true && quoteItem.getSalesCostType() != null
				&& (!SalesCostType.NonSC.equals(quoteItem.getSalesCostType()))) {
			if (quoteItem.getQuotedPrice() != null) {
				isError = true;
				messages.add(ResourceMB.getText("wq.message.quotedResaleNotAllowed") + ".");
			}
		} else {

			if (quoteItem.getSalesCost() != null) {
				isError = true;
				messages.add(ResourceMB.getText("wq.message.salesCostNotAllowed") + ".");
			}

			if (quoteItem.getSuggestedResale() != null) {
				isError = true;
				messages.add(ResourceMB.getText("wq.message.suggestedResaleNotAllowed") + ".");
			}
		}

		return isError;
	}

	private QuotationWarningVO validateInvalidQuotation(final QuotationWarningVO quotationWarningVO,
			final boolean mpqError, final boolean moqError, final boolean movError, final boolean quotedQtyError,
			final boolean quotedPriceError, final boolean leadTimeError, final boolean costIndicatorError,
			final boolean qtyIndicatorError, final boolean priceValidityError, final boolean costError,
			final boolean shipmentValidityError, final boolean mfrQuoteQtyError, final boolean aqQtyError,
			final boolean pmoqError, final boolean quotedPriceWarning, final boolean marginWarning,
			final boolean quotedQtyWarning, final boolean costWarning, final boolean shipmentValidityWarning,
			final boolean qcExternalCommentError, final boolean quoteEffectiveDateError, final String flowName,
			final boolean salesCostDataError) {
		quotationWarningVO.setMpqError(mpqError);
		quotationWarningVO.setMoqError(moqError);
		quotationWarningVO.setMovError(movError);
		quotationWarningVO.setQuotedQtyError(quotedQtyError);
		quotationWarningVO.setQuotedPriceError(quotedPriceError);
		quotationWarningVO.setLeadTimeError(leadTimeError);
		quotationWarningVO.setCostIndicatorError(costIndicatorError);
		quotationWarningVO.setQtyIndicatorError(qtyIndicatorError);
		quotationWarningVO.setPriceValidityError(priceValidityError);
		quotationWarningVO.setCostError(costError);
		quotationWarningVO.setShipmentValidityError(shipmentValidityError);
		quotationWarningVO.setMfrQuoteQtyError(mfrQuoteQtyError);
		quotationWarningVO.setAqQtyError(aqQtyError);
		quotationWarningVO.setPmoqError(pmoqError);
		quotationWarningVO.setQuotedPriceWarning(quotedPriceWarning);
		quotationWarningVO.setMarginWarning(marginWarning);
		quotationWarningVO.setQuotedQtyWarning(quotedQtyWarning);
		quotationWarningVO.setCostWarning(costWarning);
		quotationWarningVO.setShipmentValidityWarning(shipmentValidityWarning);
		quotationWarningVO.setSalesCostDataError(salesCostDataError);
		if ("validateQuotation".equals(flowName)) {
			quotationWarningVO.setQuoteEffectiveDateError(quoteEffectiveDateError);
		}
		quotationWarningVO.setQcExternalCommentError(qcExternalCommentError);
		return quotationWarningVO;
	}

	public void proceedQuotationEmail() {
		preQuoteItems = new ArrayList<WorkingPlatformItemVO>();
		proceedQuotationEmailSetting();
	}

	public void proceedQuotationEmailSetting() {
		// logger.log(Level.INFO, "PERFORMANCE START -
		// proceedQuotationEmail()");

		if (quotationWarnings != null) {
			for (QuotationWarningVO item : quotationWarnings) {
				if (item.isProceed()) {
					if (item.getWorkingPlatformItemVO() != null) {
						preQuoteItems.add(item.getWorkingPlatformItemVO());
					} else if (item.getInvalidWorkingPlatformItemVO() != null) {
						item.getInvalidWorkingPlatformItemVO().getQuoteItem().setValidFlag(false);
						item.getInvalidWorkingPlatformItemVO().setValidFlagStr(QuoteConstant.OPTION_NO);
						preQuoteItems.add(item.getInvalidWorkingPlatformItemVO());
					}
				}
			}
		}

		List<String> formIds = new ArrayList<String>();
		List<String> formNumbers = new ArrayList<String>();
		proceedQuotations = new ArrayList<ProceedQuotationVO>();
		for (WorkingPlatformItemVO item : preQuoteItems) {
			if (!formNumbers.contains(item.getQuoteItem().getQuote().getFormNumber())) {
				formIds.add(String.valueOf(item.getQuoteItem().getQuote().getId()));
				formNumbers.add(item.getQuoteItem().getQuote().getFormNumber());
				ProceedQuotationVO proceedQuotationVO = new ProceedQuotationVO();
				proceedQuotationVO.setEmployeeId(item.getQuoteItem().getQuote().getSales().getEmployeeId());
				proceedQuotationVO.setEmployeeName(item.getQuoteItem().getQuote().getSales().getName());
				List<String> emailAddressTo = new ArrayList<String>();
				emailAddressTo.add(item.getQuoteItem().getQuote().getSales().getEmployeeId());
				proceedQuotationVO.setEmailsTo(emailAddressTo);
				proceedQuotationVO.setEmailsToSelectList(emailAddressTo, this.emailAddressList);
				List<String> emailAddressCc = new ArrayList<String>();
				if (item.getQuoteItem().getQuote().getCopyToCS() != null) {
					String[] css = item.getQuoteItem().getQuote().getCopyToCS().split(";");
					if (css.length > 0) {
						for (int m = 0; m < css.length; m++) {
							emailAddressCc.add(css[m]);
						}
					}
				}

				proceedQuotationVO.setEmailsCc(emailAddressCc);
				proceedQuotationVO.setEmailsCcSelectList(emailAddressCc, this.emailAddressList);

				List<QuoteItem> quoteItems = new ArrayList<QuoteItem>();
				quoteItems.add(item.getQuoteItem());
				proceedQuotationVO.setQuoteItems(quoteItems);
				proceedQuotationVO.setFormId(String.valueOf(item.getQuoteItem().getQuote().getId()));
				proceedQuotationVO.setFormNumber(item.getQuoteItem().getQuote().getFormNumber());
				proceedQuotationVO.setSoldToCustomer(item.getQuoteItem().getSoldToCustomer());

				List<Long> highLightedRecordList = new ArrayList<Long>();

				highLightedRecordList.add(item.getQuoteItem().getId());

				proceedQuotationVO.setHighlightedRecords(highLightedRecordList);
				proceedQuotations.add(proceedQuotationVO);
			} else {
				for (ProceedQuotationVO pqVO : proceedQuotations) {
					if (pqVO.getFormNumber().equals(item.getQuoteItem().getQuote().getFormNumber())) {
						List<Long> highLightedRecordList = pqVO.getHighlightedRecords();
						if (highLightedRecordList == null)
							highLightedRecordList = new ArrayList<Long>();

						highLightedRecordList.add(item.getQuoteItem().getId());

						pqVO.setHighlightedRecords(highLightedRecordList);

						List<QuoteItem> quoteItems = pqVO.getQuoteItems();
						quoteItems.add(item.getQuoteItem());
						pqVO.setQuoteItems(quoteItems);
						break;
					}
				}
			}
		}

		proceedQuotationCount = proceedQuotations.size();

		String procRequest = "";
		for (ProceedQuotationVO pqVO : proceedQuotations) {
			List<QuoteItem> quoteItems = pqVO.getQuoteItems();
			String quoteItemIds = "";
			String quoteId = null;
			String team = null;
			String bizUnit = null;
			for (QuoteItem quoteItem : quoteItems) {
				if (quoteId == null)
					quoteId = String.valueOf(quoteItem.getQuote().getId());
				if (team == null && quoteItem.getQuote().getTeam() != null) {
					team = quoteItem.getQuote().getTeam().getName();
				}
				if (bizUnit == null && quoteItem.getQuote().getBizUnit() != null) {
					bizUnit = quoteItem.getQuote().getBizUnit().getName();
				}
				quoteItemIds += "," + quoteItem.getId();
			}
			if (procRequest.length() > 0)
				procRequest += ";";
			procRequest += quoteId + "," + team + "," + bizUnit + quoteItemIds;
		}

		String forms = "";
		for (String form : formIds) {
			forms += form + ";";
		}

		LOGGER.log(Level.INFO, "pm_grouping request : " + procRequest);
		String procResponse = userSB.findPmForInternalTransfer(forms, procRequest);
		LOGGER.log(Level.INFO, "pm_grouping result : " + procResponse);

		LOGGER.log(Level.INFO, "pm_grouping forms : " + forms);
		List<String> responses = userSB.getPmForInternalTransfer(forms, bizUnit.getName());
		LOGGER.log(Level.INFO, "pm_grouping response : " + responses);

		for (ProceedQuotationVO pqVO : proceedQuotations) {
			for (String user : responses) {
				if (user.startsWith(pqVO.getFormId())) {
					String[] subUsers = user.split(",");
					List<String> emailAddressCc = pqVO.getEmailsCc();
					for (int i = 0; i < subUsers.length; i++) {
						if (i > 0 && subUsers[i].length() > 0)
							emailAddressCc.add(subUsers[i]);
					}
					pqVO.setEmailsCc(emailAddressCc);
					pqVO.setEmailsCcSelectList(emailAddressCc, this.emailAddressList);
					break;
				}
			}
		}

	}

	public void proceedQuotationWithRecipientChecking() {

		if (exceedRecipientList == null) {
			exceedRecipientList = new ArrayList<>();
		}
		exceedRecipientList.clear();
		for (ProceedQuotationVO pvo : proceedQuotations) {
			proceedInvalidQuotationWithRecipientChecking(exceedRecipientList, pvo.getEmailsCc(), pvo.getEmailsTo(),
					pvo.getFormNumber());
		}
		if (exceedRecipientList.size() <= 0) {
			exceedRecipientList = null;
		}
		if (exceedRecipientList != null && exceedRecipientList.size() > 0) {
			RequestContext context = RequestContext.getCurrentInstance();
			context.addCallbackParam("exceedRecipient", "1");
			return;
		}
		proceedQuotationWithRecipientChecking(exceedRecipientList);
	}

	private void proceedQuotationWithRecipientChecking(List<String> exceedRecipientList) {

		proceedQuotation();
	}

	public void proceedQuotation() {

		List<QuoteItem> quoteItems = new ArrayList<QuoteItem>();
		List<QuoteItem> invalidQuoteItems = new ArrayList<QuoteItem>();
		List<String> successUpdQuoteNumbers = new ArrayList<String>();
		try {
			String quoteItemIds = "";
			// fix incident INC0018368 June Guo 20140527 to add sdf2
			DateFormat sdf2 = new SimpleDateFormat("dd/MM/yyyy");
			sdf2.setTimeZone(OSTimeZone.getOsTimeZone());

			for (WorkingPlatformItemVO item : preQuoteItems) {
				if (item != null && item.getValidFlagStr().equals(QuoteConstant.OPTION_NO)) {
					Date current = QuoteUtil.getCurrentTime();
					item.getQuoteItem().setStage(QuoteSBConstant.QUOTE_STAGE_INVALID);
					item.getQuoteItem().setLastQcUpdatedOn(current);
					// andy modify 2.2
					item.getQuoteItem().setLastUpdatedQc(user.getEmployeeId());
					item.getQuoteItem().setLastUpdatedQcName(user.getName());
					item.getQuoteItem().setLastUpdatedBy(user.getEmployeeId());
					item.getQuoteItem().setLastUpdatedName(user.getName());
					item.getQuoteItem().setSentOutTime(current);
					item.getQuoteItem().setLastUpdatedOn(current);
					item.getQuoteItem().setQuoteExpiryDate(current);// set quote
																	// Expiry
																	// Date
																	// (moved
																	// from
																	// QuoteItemListener)
					// List<QuoteItem> qItems = new ArrayList<QuoteItem>();
					// qItems.add(item.getQuoteItem());
					//
					// quoteTransactionSB.updateQuoteItem(qItems);
					updateQuoteSoldToCustomerName(item.getQuoteItem());
					invalidQuoteItems.add(item.getQuoteItem());
				} else {
					Date current = QuoteUtil.getCurrentTime();
					item.getQuoteItem().setStage(QuoteSBConstant.QUOTE_STAGE_FINISH);
					// PROGRM PRICER ENHANCEMENT
					// item.setQuoteItem(ncnrFilter(item.getQuoteItem()));
					item.getQuoteItem().applyNcnrFilter();
					item.setQuoteItem(item.getQuoteItem(), bizUnit);
					item.getQuoteItem().setLastQcUpdatedOn(current);
					// andy modify 2.2
					item.getQuoteItem().setLastUpdatedQc(user.getEmployeeId());
					item.getQuoteItem().setLastUpdatedQcName(user.getName());
					item.getQuoteItem().setLastUpdatedBy(user.getEmployeeId());
					item.getQuoteItem().setLastUpdatedName(user.getName());
					item.getQuoteItem().setSentOutTime(current);
					item.getQuoteItem().setLastUpdatedOn(current);
					// WQ 2.2 enhancement.
					// When all checking is passed in Quotation Process, the
					// system checks whether the Quotation Effective Date is
					// Blank or not.
					// If Quotation Effective Date is Blank, the Quotation
					// Effective Date is assigned as Quote Release Date.
					if (item.getQuoteItem().getQuotationEffectiveDate() == null) {
						item.getQuoteItem().setQuotationEffectiveDate(current);
					}

					// quoteSB.updateReferenceMarginForSalesAndCs(item.getQuoteItem());
					if (QuoteUtil.isEmpty(item.getQuoteItem().getSapPartNumber())
							&& item.getQuoteItem().getQuotedMfr() != null) {
						String sapPartNumber = materialSB.findDefaultSapPartNumber(
								item.getQuoteItem().getQuotedPartNumber(), item.getQuoteItem().getQuotedMfr().getName(),
								bizUnit);
						item.getQuoteItem().setSapPartNumber(sapPartNumber);
					}

					/*
					 * if(!QuoteUtil.isEmpty(item.getQuoteItem().
					 * getResaleIndicator())){ String minResale =
					 * item.getQuoteItem().getResaleIndicator().substring(0,2);
					 * String maxResale =
					 * item.getQuoteItem().getResaleIndicator().substring(2,4);
					 * if(item.getQuoteItem().getQuotedPrice() != null){
					 * item.getQuoteItem().setResaleMin(Double.parseDouble(
					 * minResale)); if(maxResale.matches("[0-9]{2}"))
					 * item.getQuoteItem().setResaleMax(Double.parseDouble(
					 * maxResale)); else
					 * item.getQuoteItem().setResaleMax(QuoteSBConstant.
					 * DEFAULT_RESALE_INDICATOR_MAX); } }
					 */

					Date validity = null;
					if (item.getQuoteItem().getPriceValidity().matches("^(0|[1-9][0-9]*)$")) {
						int shift = Integer.parseInt(item.getQuoteItem().getPriceValidity());
						validity = QuoteUtil.shiftDate(item.getQuoteItem().getSentOutTime(), shift);
					} else {
						try {
							validity = sdf2.parse(item.getQuoteItem().getPriceValidity());
						} catch (ParseException e) {
							LOGGER.log(Level.WARNING, "Error occured while validating quotation for Quoted MFR: "
									+ item.getQuotedMfr() + ", Reason for failure: " + e.getMessage(), e);
						}
					}

					item.getQuoteItem().setPoExpiryDate(new Date(validity.getTime()));

					if (validity != null) {
						// andy modify 2.2
						String materialTypeName = item.getQuoteItem().getMaterialTypeId();
						if (QuoteSBConstant.MATERIAL_TYPE_NORMAL.equals(materialTypeName)) {
							Date quoteExpiryDate = QuoteUtil.calculateQuoteExpiryDate(validity,
									QuoteSBConstant.MATERIAL_TYPE_NORMAL);
							item.getQuoteItem().setQuoteExpiryDate(quoteExpiryDate);
						} else if (QuoteSBConstant.MATERIAL_TYPE_PROGRAM.equals(materialTypeName)) {
							Date quoteExpiryDate = QuoteUtil.calculateQuoteExpiryDate(validity,
									QuoteSBConstant.MATERIAL_TYPE_PROGRAM);
							item.getQuoteItem().setQuoteExpiryDate(quoteExpiryDate);
						} else {
							Date quoteExpiryDate = QuoteUtil.calculateQuoteExpiryDate(validity,
									QuoteSBConstant.MATERIAL_TYPE_NORMAL);
							item.getQuoteItem().setQuoteExpiryDate(quoteExpiryDate);
						}
					}
					/*
					 * if(item.getQuoteItem().getQuotedQty() == null ||
					 * (item.getQuoteItem().getQuotedQty() != null &&
					 * item.getQuoteItem().getQuotedQty() == 0)){
					 * item.getQuoteItem().setMoq(QuoteUtil.findMoq(item.
					 * getQuoteItem()));
					 * item.getQuoteItem().setQuotedQty(QuoteUtil.findAqQty(item
					 * .getQuoteItem())); //PROGRM PRICER ENHANCEMENT
					 * //item.getQuoteItem().setPmoq(QuoteUtil.findPmoq(item.
					 * getQuoteItem()));
					 * //item.getQuoteItem().setPmoq(QuoteUtil.findPmoq(item.
					 * getQuoteItem(), bizUnit)); ProgramPricer tempPm =
					 * materialSB.getProgramMat(item.getQuoteItem().
					 * getQuotedMaterial(), bizUnit,
					 * item.getQuoteItem().getCostIndicator());
					 * item.getQuoteItem().setPmoq(QuoteUtil.findPmoqWithPM(item
					 * .getQuoteItem(), bizUnit,tempPm)); } else {
					 * item.getQuoteItem().setMoq(QuoteUtil.findMoq(item.
					 * getQuoteItem()));
					 * item.getQuoteItem().setQuotedQty(item.getQuoteItem().
					 * getQuotedQty()); //PROGRM PRICER ENHANCEMENT
					 * //item.getQuoteItem().setPmoq(QuoteUtil.findPmoq(item.
					 * getQuoteItem(), item.getQuoteItem().getQuotedQty()));
					 * //item.getQuoteItem().setPmoq(QuoteUtil.findPmoq(item.
					 * getQuoteItem(),
					 * item.getQuoteItem().getQuotedQty(),bizUnit));
					 * ProgramPricer tempPm =
					 * materialSB.getProgramMat(item.getQuoteItem().
					 * getQuotedMaterial(), bizUnit,
					 * item.getQuoteItem().getCostIndicator());
					 * item.getQuoteItem().setPmoq(QuoteUtil.findPmoqWithPM(item
					 * .getQuoteItem(),
					 * item.getQuoteItem().getQuotedQty(),bizUnit,tempPm)); }
					 */

					updateQuoteSoldToCustomerName(item.getQuoteItem());
					quoteItems.add(item.getQuoteItem());
				}
			}

			// Fix INC0272129
			// update the invalid quote item. get the success quote number.
			successUpdQuoteNumbers = this.updateQuoteItem(invalidQuoteItems, ActionEnum.WP_RELEASE_QUOTE);

			// for the valid quote item ---------update quote rate and send to
			// SAP
			// quoteSB.updateRateForQuoteItems(quoteItems); //added by June for
			// RMB cur project 20140704

			quoteSB.saveQuoteItemAndUpdateDRMS(quoteItems, user.getEmployeeId(), ActionEnum.WP_RELEASE_QUOTE, user);
		} catch (Exception ex) {
			// added By Lenon(043044) 2016/12/12 when catch exception set the
			// stage to PENDING
			LOGGER.log(Level.WARNING, "Error occured for employee id: " + user.getEmployeeId() + ", Reason for failure: "
					+ MessageFormatorUtil.getParameterizedStringFromException(ex), ex);
			for (QuoteItem temItem : quoteItems) {
				temItem.setStage(QuoteSBConstant.QUOTE_STAGE_PENDING);
			}
			String msg = ex.getMessage();
			int position = msg.indexOf(":");
			if (position != -1) {
				msg = msg.substring(position + 1);
			}
			failMessage = msg;
			RequestContext context = RequestContext.getCurrentInstance();
			context.addCallbackParam("failMessage", 1);
			return;

		}

		doKeepFirstRow();

		/*************************************************************************/
		this.selectedWorkingPlatformItem = null;
		// refreshQuoteTableWithoutQuery(quoteItems);
		// refreshQuoteTable();
		// Fixed by DamonChen@20180427
		workingPlatformItems.clearCacheSelectionItems();
		workingPlatformItems.setRequery(true);
		FacesUtil.updateUI("form_pendinglist:datatable_pendinglist");
		// logger.log(Level.INFO, "PERFORMANCE END - proceedQuotation()");
		String quoteItemIdArray = "";
		if (null == successUpdQuoteNumbers) {
			successUpdQuoteNumbers = new ArrayList<String>();
		}
		for (QuoteItem quoteItem : quoteItems) {
			if (quoteItemIdArray.length() > 0)
				quoteItemIdArray += ",";
			quoteItemIdArray += quoteItem.getId();
			successUpdQuoteNumbers.add(quoteItem.getQuoteNumber());
		}
		for (QuoteItem invalidQuoteItem : invalidQuoteItems) {
			if (quoteItemIdArray.length() > 0)
				quoteItemIdArray += ",";
			if (successUpdQuoteNumbers.contains(invalidQuoteItem.getQuoteNumber())) { // Fix
																						// INC0272129
				quoteItemIdArray += invalidQuoteItem.getId();
			}
		}
		List<QuotationEmail> emails = getQuotationEmails(proceedQuotations, user, successUpdQuoteNumbers);
		asyncPostQuotation.proceedQuotation(emails, user, quoteItemIdArray);
	}

	public void proceedInvalidQuotationWithRecipientChecking() {
		if (exceedRecipientList == null) {
			exceedRecipientList = new ArrayList<>();
		}
		exceedRecipientList.clear();
		for (WorkingPlatformEmailVO pvo : invalidQuoteEmailVOs) {
			proceedInvalidQuotationWithRecipientChecking(exceedRecipientList, pvo.getEmailsCc(), pvo.getEmailsTo(),
					pvo.getFormNumber());
		}
		if (exceedRecipientList.size() <= 0) {
			exceedRecipientList = null;
		}
		if (exceedRecipientList != null && exceedRecipientList.size() > 0) {
			RequestContext context = RequestContext.getCurrentInstance();
			context.addCallbackParam("exceedRecipient", "1");
			return;
		}
		proceedQuotationWithRecipientChecking(exceedRecipientList);
	}

	private void proceedInvalidQuotationWithRecipientChecking(List<String> exceedRecipientList,
			final List<String> emailsCC, final List<String> emailsTo, final String formNumber) {
		if (emailsCC != null && emailsCC.size() > QuoteConstant.ALERT_NUMBER_OF_RECIPIENT) {
			if (exceedRecipientList == null)
				exceedRecipientList = new ArrayList<String>();
			exceedRecipientList.add(ResourceMB.getParameterizedText("wq.label.recipientsTo",
					"" + QuoteConstant.ALERT_NUMBER_OF_RECIPIENT) + " " + formNumber);
		}
		if (emailsTo != null && emailsTo.size() > QuoteConstant.ALERT_NUMBER_OF_RECIPIENT) {
			if (exceedRecipientList == null)
				exceedRecipientList = new ArrayList<String>();
			exceedRecipientList.add(ResourceMB.getParameterizedText("wq.label.recipientsCc",
					"" + QuoteConstant.ALERT_NUMBER_OF_RECIPIENT) + " " + formNumber);
		}

	}

	public void proceedInvalidQuotation() {

		// logger.log(Level.INFO, "PERFORMANCE START - proceedQuotation()");
		List<String> successUpdQuoteNumbers = null;
		try {

			quotationWarnings = null;
			preQuoteItems = new ArrayList<WorkingPlatformItemVO>();

			List<QuoteItem> quoteItems = new ArrayList<QuoteItem>();

			// NEC Pagination changes: get the selected data from cache
			for (WorkingPlatformItemVO item : this.workingPlatformItems.getCacheSelectedItems()) {
				if (item != null) {
					Date current = QuoteUtil.getCurrentTime();
					item.getQuoteItem().setStage(QuoteSBConstant.QUOTE_STAGE_INVALID);
					item.getQuoteItem().setLastQcUpdatedOn(current);
					// andy modify 2.2
					item.getQuoteItem().setLastUpdatedQc(user.getEmployeeId());
					item.getQuoteItem().setLastUpdatedQcName(user.getName());
					item.getQuoteItem().setLastUpdatedBy(user.getEmployeeId());
					item.getQuoteItem().setLastUpdatedName(user.getName());
					item.getQuoteItem().setSentOutTime(current);

					// List<QuoteItem> qItems = new ArrayList<QuoteItem>();
					// qItems.add(item.getQuoteItem());
					// quoteTransactionSB.updateQuoteItem(qItems);
					quoteItems.add(item.getQuoteItem());
					preQuoteItems.add(item);
					// createQuoteToSo(quoteItem);
				}
			}
			successUpdQuoteNumbers = this.updateQuoteItem(quoteItems, ActionEnum.WP_INVALIDATE_QUOTE);
		} catch (Exception ex) {
			LOGGER.log(Level.WARNING, "Error occured in proceedInvalidQuotation, Reason for failure: "
					+ MessageFormatorUtil.getParameterizedStringFromException(ex), ex);
		}

		try {
			/*
			 * for fix INC0272129, maybe there are some quote will not update
			 * successfully. need to check this quote update successfully or not
			 * before sent email.
			 * 
			 */
			if (null != successUpdQuoteNumbers) {

				proceedQuotationEmailSetting();

				for (ProceedQuotationVO pqVO : proceedQuotations) {
					QuotationEmailVO vo = new QuotationEmailVO();
					vo.setFormNumber(pqVO.getFormNumber());
					List<QuoteItem> sortQuoteItems = pqVO.getQuote().getQuoteItems();
					List<QuoteItem> successSrtQuoteItems = pqVO.getQuote().getQuoteItems();
					Collections.sort(sortQuoteItems, QuoteItem.alternativeQuoteNumberComparator);

					List<Long> highLightedRecordList = (List<Long>) pqVO.getHighlightedRecords();
					for (QuoteItem qi : sortQuoteItems) {
						for (Long id : highLightedRecordList) {
							if (id.longValue() == qi.getId()) {
								qi.setHightlighted(true);
							}
						}
						if (successUpdQuoteNumbers.contains(qi.getQuoteNumber())) { // only
						 
							successSrtQuoteItems.add(qi);
						}
					}
					if (null != successSrtQuoteItems && successSrtQuoteItems.size() > 0) { // if
							 
						pqVO.getQuote().setQuoteItems(successSrtQuoteItems); // to
																 
						vo.setQuoteId(pqVO.getQuote().getId());
						// vo.setHssfWorkbook(getQuoteTemplateBySoldTo(pqVO.getSoldToCustomer(),
						// pqVO.getQuote()));
						vo.setQuote(pqVO.getQuote());
						vo.setSoldToCustomer(pqVO.getSoldToCustomer());

						vo.setLink(super.getUrl() + "/RFQ/MyQuoteListForSales.jsf");
						vo.setRecipient(pqVO.getEmployeeName());
						String sender = sysMaintSB.getEmailSignName(bizUnit.getName()) + "<br/>"
								+ sysMaintSB.getEmailHotLine(bizUnit.getName()) + "<br/>" + "Email Box: "
								+ sysMaintSB.getEmailSignContent(bizUnit.getName()) + "<br/>";
						vo.setSender(sender);

						vo.setFromEmail(UserInfo.getUser().getEmailAddress());
						String subject = "Quotation - " + pqVO.getFormNumber() + " - ";
						String fileName = "Quotation - " + pqVO.getFormNumber() + " - ";
						String fullCustomerName = getCustomerFullName(pqVO.getSoldToCustomer());
						if (fullCustomerName != null) {
							subject += " customer " + fullCustomerName;

							if (fullCustomerName.length() > 40) {
								fileName += " - " + fullCustomerName.substring(0, 40);
							} else {
								fileName += " - " + fullCustomerName;
							}
						}
						vo.setSubject(subject);
						vo.setFileName(fileName);

						for (WorkingPlatformEmailVO workingPlatformEmailVO : invalidQuoteEmailVOs) {
							if (workingPlatformEmailVO.getFormNumber().equals(pqVO.getFormNumber())) {
								pqVO.setEmailsTo(workingPlatformEmailVO.getEmailsTo());
								pqVO.setEmailsCc(workingPlatformEmailVO.getEmailsCc());
								pqVO.setMessage(workingPlatformEmailVO.getMessage());
							}
						}
						LOGGER.log(Level.INFO, "Send " + pqVO.getFormNumber() + " To : " + pqVO.getEmailsTo());
						List<String> toEmails = new ArrayList<String>();
						if (pqVO.getEmailsTo() != null && pqVO.getEmailsTo().size() > 0) {
							List<User> users = userSB.findByEmployeeIds(pqVO.getEmailsTo());
							for (User user : users) {
								toEmails.add(user.getEmployeeId());
							}
						}
						vo.setToEmails(toEmails);

						LOGGER.log(Level.INFO, "Send " + pqVO.getFormNumber() + " Cc : " + pqVO.getEmailsCc());
						List<String> ccEmails = new ArrayList<String>();
						if (pqVO.getEmailsCc() != null && pqVO.getEmailsCc().size() > 0) {
							List<User> users = userSB.findByEmployeeIds(pqVO.getEmailsCc());
							for (User user : users) {
								ccEmails.add(user.getEmployeeId());
							}
						}
						vo.setCcEmails(ccEmails);
						List<String> bcc = new ArrayList<String>();
						bcc.add(user.getEmailAddress());
						vo.setBccEmails(bcc);

						vo.setRemark(pqVO.getMessage());
						// sendQuotationEmail(vo);

						// SendQuotationEmailStrategy strategy =
						// (SendQuotationEmailStrategy)
						// cacheUtil.getStrategy(SendQuotationEmailStrategy.class,
						// UserInfo.getUser().getDefaultBizUnit().getName());

						SendQuotationEmailStrategy strategy = (SendQuotationEmailStrategy) StrategyFactory
								.getSingletonFactory().getStrategy(SendQuotationEmailStrategy.class,
										UserInfo.getUser().getDefaultBizUnit().getName(),
										this.getClass().getClassLoader());

						strategy.sendQuotationEmail(vo);

					} // successSrtQuoteItems
				} // for
			} // successUpdateItem

		} catch (Exception ex) {
			LOGGER.log(Level.WARNING, "Error occured in proceedInvalidQuotation, Reason for failure: "
					+ MessageFormatorUtil.getParameterizedStringFromException(ex), ex);
		}
		this.selectedWorkingPlatformItem = null;
		doKeepFirstRow();

		// refreshQuoteTable();
		// Fixed by DamonChen@20180427
		workingPlatformItems.clearCacheSelectionItems();
		workingPlatformItems.setRequery(true);
		FacesUtil.updateUI("form_pendinglist:datatable_pendinglist");
		// logger.log(Level.INFO, "PERFORMANCE END - proceedQuotation()");

	}

	public void uploadWorkingPlatform() {

		// logger.log(Level.INFO, "PERFORMANCE START -
		// uploadWorkingPlatform()");

		try {
			List<QuoteItem> noPendingQuoteItems = new ArrayList<QuoteItem>();
			if (workingPlatformExcel != null) {

				long uploadfileSizeLimit = sysMaintSB.getUploadFileSizeLimit();

				if (workingPlatformExcel.getSize() > uploadfileSizeLimit) {
					FacesContext.getCurrentInstance().addMessage("workingPlatformKey", new FacesMessage(
							FacesMessage.SEVERITY_WARN,
							ResourceMB.getParameterizedText("wq.error.fileSizeError", "" + uploadfileSizeLimit) + ".",
							""));
					return;
				}

				List<QuoteItem> quoteItems = new ArrayList<QuoteItem>();

				InputStream file = new ByteArrayInputStream(workingPlatformExcel.getContents());
				String fileName = workingPlatformExcel.getFileName().toString();
				Workbook workbook = null;
				if (fileName.endsWith("xls") || fileName.endsWith("XLS")) {
					workbook = new HSSFWorkbook(file);
				} else {
					workbook = new XSSFWorkbook(file);
				}
				Sheet sheet = workbook.getSheetAt(0);
				try {
					for (Row row : sheet) {
						//reg
						/** validateHeaderFromat */
						if (row.getRowNum() == 0) {
							// validation for template header added by
							// Lenon.Yang(043044) 2016/07/19
							String errorMsg = "";
							int errIdx = 1;
							for (Iterator<Cell> cellIt = row.cellIterator(); cellIt.hasNext();) {
								Cell tempCell = cellIt.next();
								int cellColIndex = tempCell.getColumnIndex();
								if (cellColIndex <= headerArray.length) {
									String headerName = headerArray[cellColIndex];
									String cellVal = tempCell.getStringCellValue();
									if (!(org.apache.commons.lang.StringUtils.isNotBlank(cellVal)
											&& org.apache.commons.lang.StringUtils.equals(headerName,
													cellVal.trim()))) {
										errorMsg += errIdx + "."
												+ ResourceMB.getParameterizedText("wq.label.headerName", "" + cellVal)
												+ (cellColIndex + 1) + ResourceMB.getParameterizedText(
														"wq.label.incorrectHeaderName", "" + headerName)
												+ " .<br/>";
										if (errIdx == 5) {
											break;
										}

										errIdx++;
									}
								} else {
									errorMsg = ResourceMB.getText("wq.message.qcPrice") + ".";
									break;
								}
							}
							if (org.apache.commons.lang.StringUtils.isNotBlank(errorMsg)) {
								FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_WARN,
										ResourceMB.getText("wq.msg.invalidFormat") + " :", errorMsg);
								FacesContext.getCurrentInstance().addMessage("workingPlatformGrowl", msg);

								return;
							}
						}
						//endreg
						
						if (row.getRowNum() > 0) {
							QuoteItem quoteItem = null;
							// TODO
							Cell cell = row.getCell(85 + 5, Row.CREATE_NULL_AS_BLANK);
							String cellValue = cell.getStringCellValue().trim();
							cellValue = cellValue.replaceFirst("A", "");
							LOGGER.log(Level.INFO, "Find QuoteItem : " + cellValue);
							long id = 0;
							try {
								id = Long.parseLong(cellValue);
								quoteItem = quoteSB.findByPK(id);
								if (quoteItem != null && !QuoteSBConstant.QUOTE_STAGE_PENDING
										.equalsIgnoreCase(quoteItem.getStage())) {
									noPendingQuoteItems.add(quoteItem);
									LOGGER.log(Level.WARNING,
											"Finish Quote Item will be ignored in Pending List Upload : " + cellValue);
									continue;
								}
								if (quoteItem != null && (QuoteSBConstant.RFQ_STATUS_BQ.equals(quoteItem.getStatus())
										|| QuoteSBConstant.RFQ_STATUS_BIT.equals(quoteItem.getStatus()))) {
									LOGGER.log(Level.WARNING,
											"BQ or BIT Quote Item will be ignored in Pending List Upload : "
													+ cellValue);
									continue;
								}
							} catch (Exception ex) {
								LOGGER.log(Level.WARNING, "Invalid Quote Item id in Pending List Upload : " + cellValue,
										ex);
							}
							
							uploadWorkingPlatform(cellValue, cell, row, quoteItem);
							
							if (quoteItem != null) {
								quoteItems.add(quoteItem);
							}
						}
					}
					setLastUpdateFields(quoteItems);
					StringBuilder successMsg = new StringBuilder(ResourceMB.getText("wq.msg.uploadSuccess") + "!");
					if (noPendingQuoteItems.size() > 0) {
						int idx = 1;
						successMsg.append("<br/>");
						successMsg.append(ResourceMB.getText("wq.msg.quotesNotUploaded") + ":").append("<br/>");
						for (QuoteItem qItem : noPendingQuoteItems) {
							successMsg.append(idx + "." + qItem.getQuoteNumber()).append("<br/>");
						}
					}
					this.updateQuoteItem(quoteItems, ActionEnum.WP_EXCEL_UPLOAD);
					refreshQuoteTable();
					FacesUtil.showInfoMessage("", successMsg.toString());
				} catch (Exception ex)// TODO PMD Exception
				{
					LOGGER.log(Level.WARNING, "Error occured while uploading file: " + workingPlatformExcel.getFileName()
							+ ", Reason for failure: " + MessageFormatorUtil.getParameterizedStringFromException(ex),
							ex);
				}
				file.close();
			}
		} catch (Exception e) {
			// FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR,
			// ResourceMB.getText("wq.msg.invalidFormat")+" :",
			// ResourceMB.getText("wq.label.invalidFileFormat"));
			FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR,
					ResourceMB.getText("wq.label.invalidFileFormat"), null);
			FacesContext.getCurrentInstance().addMessage("workingPlatformGrowl", msg);
			LOGGER.log(Level.WARNING, "Error occured while uploading file: " + workingPlatformExcel.getFileName()
					+ ", Reason for failure: " + MessageFormatorUtil.getParameterizedStringFromException(e), e);
		}

		// logger.log(Level.INFO, "PERFORMANCE END - uploadWorkingPlatform()");

	}

	/**
	 * @throws Exception
	 * 
	 */
	private void uploadWorkingPlatform(String cellValue, Cell cell, Row row, QuoteItem quoteItem)
			throws Exception {
		boolean buyCurrOrCostIndicatorChanged = false;
		int offset = 5;
		for (int colIndex = 0; colIndex < QuoteConstant.MAX_PENDING_COL_NUMBER - 1; colIndex++) {
			cellValue = null;
			cell = row.getCell(colIndex, Row.CREATE_NULL_AS_BLANK);
			
			if (cell == null)
				continue;
			switch (cell.getCellType()) {
			case Cell.CELL_TYPE_BOOLEAN:
				cellValue = cell.getBooleanCellValue() ? QuoteConstant.OPTION_YES : QuoteConstant.OPTION_NO;
				break;
			case Cell.CELL_TYPE_NUMERIC:
				if (HSSFDateUtil.isCellDateFormatted(cell)) {
					Date date = HSSFDateUtil.getJavaDate(cell.getNumericCellValue());
					cellValue = dateFormatOfDMY.format(date);
				} else {
					cellValue = String.valueOf(cell.getNumericCellValue());
					cellValue = new BigDecimal(cellValue).stripTrailingZeros().toPlainString();
				}
				break;
			case Cell.CELL_TYPE_STRING:
				cellValue = cell.getStringCellValue().trim();
				break;
			}
			//reg
			if (colIndex == 0) {
				if (quoteItem == null) {
					break;
				}
			} else if (colIndex == 8) {// sales cost type
				if (!QuoteUtil.isEmpty(cellValue)) {
					if (cellValue.trim().equals(SalesCostType.ZINC.name())) {
						quoteItem.setSalesCostType(SalesCostType.ZINC);
					} else if (cellValue.trim().equals(SalesCostType.ZIND.name())) {
						quoteItem.setSalesCostType(SalesCostType.ZIND);
					} else if (cellValue.trim().equals(SalesCostType.NonSC.name())) {
						quoteItem.setSalesCostType(SalesCostType.NonSC);
					} else {
						 
						throwUploadErrorMesg(cellValue, row.getRowNum(),
								ResourceMB.getText("wq.label.salesCostTypeInvalid") + " :",
								ResourceMB.getParameterizedText("wq.message.salesCostTypeInvalidAtRow",
										"" + (row.getRowNum() + 1)),
								null);
					}
				} else {
					quoteItem.setSalesCostType(SalesCostType.NonSC);
				}
			}

			else if (colIndex == 9) {// Valid RFQ
				try {
					if (QuoteUtil.isEmpty(cellValue)) {
						// throw(new Exception());
					} else {
						if (!cellValue.equals(QuoteConstant.OPTION_YES) && !cellValue.equals(QuoteConstant.OPTION_NO)) {
							throw (new Exception());
						} else {
							quoteItem.setValidFlag(cellValue.equals(QuoteConstant.OPTION_YES) ? true : false);
						}
					}
				} catch (Exception ex) {
					 
					throwUploadErrorMesg(cellValue, row.getRowNum(), ResourceMB.getText("wq.msg.invalidFormat") + " :",
							ResourceMB.getParameterizedText("wq.label.validRFQAtRow", "" + (row.getRowNum() + 1)), ex);
				}
			} else if (colIndex == 10) {// Resale Indicator
				if (!QuoteUtil.isEmpty(cellValue) && !cellValue.matches("[0-9]{4}")
						&& !cellValue.matches("[0-9]{2}[A]{2}")) {
					 
					throwUploadErrorMesg(cellValue,
							row.getRowNum(), ResourceMB.getText("wq.msg.invalidFormat") + " :", ResourceMB
									.getParameterizedText("wq.label.resaleIndicatorAtRow", "" + (row.getRowNum() + 1)),
							null);
				} else {
					quoteItem.setResaleIndicator(cellValue);
					// Fixed by Damonchen@20180514
					String minResale = cellValue.substring(0, 2);
					String maxResale = cellValue.substring(2, 4);
					// fix INC0276031
					// if(quoteItem.getQuotedPrice() != null){
					quoteItem.setResaleMin(Double.parseDouble(minResale));
					if (maxResale.matches("[0-9]{2}"))
						quoteItem.setResaleMax(Double.parseDouble(maxResale));
					else
						quoteItem.setResaleMax(QuoteSBConstant.DEFAULT_RESALE_INDICATOR_MAX);
					// }
				}
			} else if (colIndex == 11) {// buy curr
				buyCurrOrCostIndicatorChanged = false;
				if (!QuoteUtil.isEmpty(cellValue)) {
					if (Currency.hasValue(cellValue.trim())) {
						// noaccesscurrency
						boolean access = user.getDefaultBizUnit().getAllowCurrencies().contains(cellValue.trim());
						if (!access) {
							String errInfo = ResourceMB.getText("wq.error.noaccesscurrency");
							 
							throwUploadErrorMesg(cellValue, row.getRowNum(), ResourceMB.getText("wq.message.row") + " ["
									+ (row.getRowNum() + 1) + "] :" + errInfo, errInfo, null);
						} else {
							Currency buyCurr = Currency.valueOf(cellValue.trim());
							buyCurrOrCostIndicatorChanged = buyCurr != quoteItem.getBuyCurr();
							quoteItem.setBuyCurr(buyCurr);
						}

						// wq.error.noaccesscurrency
					} else {

						String errInfo = ResourceMB.getText("wq.error.notCurrencyType");
						 
						throwUploadErrorMesg(cellValue, row.getRowNum(),
								ResourceMB.getText("wq.message.row") + " [" + (row.getRowNum() + 1) + "] :" + errInfo,
								errInfo, null);
					}
				} else {
					String errInfo = ResourceMB.getText("wq.error.mustinputbuycurr");
					throwUploadErrorMesg(cellValue, row.getRowNum(),
							ResourceMB.getText("wq.message.row") + " [" + (row.getRowNum() + 1) + "] :" + errInfo,
							errInfo, null);
				}
			} else if (colIndex == 12) {
				if (!QuoteUtil.isEmpty(cellValue)) {// Quoted Resale

					if (quoteItem.isSalesCostFlag() && null != quoteItem.getSalesCostType()
							&& !SalesCostType.NonSC.equals(quoteItem.getSalesCostType())) {
						
						throwUploadErrorMesg(cellValue, row.getRowNum(),
								ResourceMB.getText("wq.label.quotedResaleNotAllowed") + " :",
								ResourceMB.getParameterizedText("wq.message.quotedResaleNotAllowedAtRow",
										"" + (row.getRowNum() + 1)),
								null);
					} else {
						try {
							quoteItem.setQuotedPrice(Double.parseDouble(cellValue));
						} catch (Exception ex) {
							throwUploadErrorMesg(cellValue, row.getRowNum(),
									ResourceMB.getText("wq.msg.invalidFormat") + " :", ResourceMB.getParameterizedText(
											"wq.label.quotedResaleAtRow", "" + (row.getRowNum() + 1)),
									null);
						}
					}

				} else {
					quoteItem.setQuotedPrice(null);
				}
			} else if (colIndex == 13) {

				if (!QuoteUtil.isEmpty(cellValue)) {// sales cost
					// try{

					if (quoteItem.isSalesCostFlag() && null != quoteItem.getSalesCostType()
							&& !SalesCostType.NonSC.equals(quoteItem.getSalesCostType())) {
						quoteItem.setSalesCost(new BigDecimal(cellValue));
					} else {
						throwUploadErrorMesg(cellValue, row.getRowNum(),
								ResourceMB.getText("wq.label.salesCostNotAllowed") + " :",
								ResourceMB.getParameterizedText("wq.message.salesCostNotAllowedAtRow",
										"" + (row.getRowNum() + 1)),
								null);
					}
				} else {
					quoteItem.setSalesCost(null);
				}
			} else if (colIndex == 14) {

				if (!QuoteUtil.isEmpty(cellValue)) {// Suggested Resale
					// try{
					if (quoteItem.isSalesCostFlag() && null != quoteItem.getSalesCostType()
							&& !SalesCostType.NonSC.equals(quoteItem.getSalesCostType())) {
						quoteItem.setSuggestedResale(new BigDecimal(cellValue));
					} else {
						 
						throwUploadErrorMesg(cellValue, row.getRowNum(),
								ResourceMB.getText("wq.label.suggestedResaleNotAllowed") + " :",
								ResourceMB.getParameterizedText("wq.message.suggestedResaleNotAllowedAtRow",
										"" + (row.getRowNum() + 1)),
								null);
					}

				} else {
					quoteItem.setSuggestedResale(null);
				}
			} else if (colIndex == 15) { // Quoted P/N check at MFR
				String strCellVlue = org.apache.commons.lang.StringUtils
						.stripEnd(org.apache.commons.lang.StringUtils.stripStart(cellValue, null), null);
				 
				quoteItem.setQuotedPartNumber(strCellVlue);
			}

			else if (colIndex == 17 + offset) {// Multi-Usage
				try {
					if (QuoteUtil.isEmpty(cellValue)) {
						// throw(new Exception());
					} else {
						if (!cellValue.equals(QuoteConstant.OPTION_YES) && !cellValue.equals(QuoteConstant.OPTION_NO)) {
							throw (new Exception());
						} else {
							quoteItem.setMultiUsageFlag(cellValue.equals(QuoteConstant.OPTION_YES) ? true : false);
						}
					}
				} catch (Exception ex) {
					 
					throwUploadErrorMesg(cellValue, row.getRowNum(), ResourceMB.getText("wq.msg.invalidFormat") + " :",
							ResourceMB.getParameterizedText("wq.label.multiUsageAtRow", "" + (row.getRowNum() + 1)),
							ex);
				}
			} else if (colIndex == 18 + offset) {// QC External Comment
				// TODO: length checking
				quoteItem.setAqcc(cellValue);
			}

			else if (colIndex == 19 + offset) {// Internal Comment
				// TODO:validation
				quoteItem.setInternalComment(cellValue);
			} else if (colIndex == 21 + offset) {// Price Validity
				if (!QuoteUtil.isEmpty(cellValue)) {
					boolean throwExc = false;
					try {
						// added by DamonChen@20180627
						int cellValueShift = Integer.parseInt(cellValue);
						int priceValidityMax = Integer
								.parseInt(sysConfigSB.getProperyValue(QuoteConstant.PRICE_VALIDITY_NUM_MAX));
						if (cellValueShift > priceValidityMax) {
							// messages.add(ResourceMB.getText("wq.message.PriceValidityOutLimit"));
							// throwExc = true;
							Object[] arr = { String.valueOf(cellValueShift), String.valueOf(row.getRowNum() + 1) };
							 
							throwUploadErrorMesg(cellValue, row.getRowNum(),
									ResourceMB.getText("wq.msg.invalidFormat") + " :",
									ResourceMB.getParameterizedString("wq.message.PriceValidityOutLimitAtRow", arr),
									null);
						} else {
							quoteItem.setPriceValidity(cellValue);
						}
					} catch (Exception e) {
						try {
							// Integer value = convertToInteger(cellValue);

							if (!QuoteUtil.isValidDate(cellValue)) {
								 
								throwUploadErrorMesg(cellValue, row.getRowNum(),
										ResourceMB.getText("wq.msg.invalidFormat") + " :",
										ResourceMB.getParameterizedText("wq.label.priceValidityAtRow",
												"" + (row.getRowNum() + 1)),
										e);
							} else {
								quoteItem.setPriceValidity(cellValue);
							}

						} catch (Exception ex) {
							 
							throwUploadErrorMesg(cellValue, row.getRowNum(),
									ResourceMB.getText("wq.msg.invalidFormat") + " :", ResourceMB.getParameterizedText(
											"wq.label.priceValidityAtRow", "" + (row.getRowNum() + 1)),
									ex);
						}
					}
					/*
					 * if (throwExc) { throw (new Exception()); }
					 */
				} else {
					quoteItem.setPriceValidity(null);
				}
			} else if (colIndex == 22 + offset) {// Shipment Validity
				if (!QuoteUtil.isEmpty(cellValue)) {
					try {
						Date date = dateFormatOfDMY.parse(cellValue);
						quoteItem.setShipmentValidity(date);
					} catch (Exception ex) {
						 
						throwUploadErrorMesg(cellValue, row.getRowNum(),
								ResourceMB.getText("wq.msg.invalidFormat") + " :", ResourceMB.getParameterizedText(
										"wq.label.shipmentValidityAtRow", "" + (row.getRowNum() + 1)),
								ex);
						// throw (ex);
					}
				} else {
					quoteItem.setShipmentValidity(null);
				}
			} else if (colIndex == 23 + offset) {// MFR Quote#
				// TODO:validation
				quoteItem.setVendorQuoteNumber(cellValue);
			} else if (colIndex == 24 + offset) {// MFR Debit#
				// TODO:validation
				quoteItem.setVendorDebitNumber(cellValue);
			} else if (colIndex == 25 + offset) {// MFR Quote Qty
				if (!QuoteUtil.isEmpty(cellValue)) {
					try {
						Integer value = convertToInteger(cellValue);
						if (value == null)
							throw (new Exception());
						quoteItem.setVendorQuoteQty(value);
					} catch (Exception ex) {
						 
						throwUploadErrorMesg(cellValue,
								row.getRowNum(), ResourceMB.getText("wq.msg.invalidFormat") + " :", ResourceMB
										.getParameterizedText("wq.label.mfrQuoteQtyAtRow", "" + (row.getRowNum() + 1)),
								ex);
					}
				} else {
					quoteItem.setVendorQuoteQty(null);
				}
			} else if (colIndex == 26 + offset) {// Quoted Qty
				if (!QuoteUtil.isEmpty(cellValue)) {
					try {
						Integer value = convertToInteger(cellValue);
						if (value == null)
							throw (new Exception());
						quoteItem.setQuotedQty(value);
					} catch (Exception ex) {
						 
						throwUploadErrorMesg(cellValue, row.getRowNum(),
								ResourceMB.getText("wq.msg.invalidFormat") + " :",
								ResourceMB.getParameterizedText("wq.label.quotedQtyAtRow", "" + (row.getRowNum() + 1)),
								null);
					}
				} else {
					quoteItem.setQuotedQty(null);
				}
			} else if (colIndex == 27 + offset) {// Allocatiion Part
				try {
					if (QuoteUtil.isEmpty(cellValue)) {
						// throw(new Exception());
					} else {
						if (!cellValue.equals(QuoteConstant.OPTION_YES) && !cellValue.equals(QuoteConstant.OPTION_NO)) {
							throw (new Exception());
						} else {
							quoteItem.setAllocationFlag(cellValue.equals(QuoteConstant.OPTION_YES) ? true : false);
						}
					}
				} catch (Exception ex) {
					 
					throwUploadErrorMesg(cellValue, row.getRowNum(), ResourceMB.getText("wq.msg.invalidFormat") + " :",
							ResourceMB.getParameterizedText("wq.label.allocationPartAtRow", "" + (row.getRowNum() + 1)),
							ex);
				}
			} else if (colIndex == 28 + offset) {// Qty Indicator
				if (!QuoteUtil.isEmpty(cellValue)) {
					// Bryan Start
					// List<String> qtyIndicators =
					// QtyIndicatorCacheManager.getQtyIndicatorList();
					List<String> qtyIndicators = cacheUtil.getQtyIndicatorList();
					// Bryan End
					String qtyIndicator = null;
					for (String ind : qtyIndicators) {
						if (ind.toUpperCase().equals(cellValue.toUpperCase()))
							qtyIndicator = ind;
					}
					if (qtyIndicator != null) {
						quoteItem.setQtyIndicator(qtyIndicator);
					} else {
						 
						throwUploadErrorMesg(cellValue, row.getRowNum(),
								ResourceMB.getText("wq.msg.invalidFormat") + " :",
								ResourceMB.getParameterizedText("wq.label.qtyIndicatorAtRow",
										"" + (row.getRowNum() + 1)) + ".",
								null);
					}
				}
			}

			else if (colIndex == 29 + offset) {// Cost Indicator
				String costIndicatorInDB = quoteItem.getCostIndicator();
				if (!QuoteUtil.isEmpty(cellValue)) {
					// Bryan Start
					// List<String> costIndicators =
					// CostIndicatorCacheManager.getCostIndicator();
					List<String> costIndicators = cacheUtil.getCostIndicator();
					// Bryan End
					if (costIndicators.contains(cellValue)) {
						quoteItem.setCostIndicator(cellValue);
					} else {
						 
						throwUploadErrorMesg(cellValue, row.getRowNum(),
								ResourceMB.getText("wq.msg.invalidFormat") + " :",
								ResourceMB.getParameterizedText("wq.label.costIndicatorAtRow",
										"" + (row.getRowNum() + 1)) + ".",
								null);
					}
				} else {
					quoteItem.setCostIndicator(null);
				}
				if(!buyCurrOrCostIndicatorChanged) {
					buyCurrOrCostIndicatorChanged = QuoteUtil.isEqualsWithTrimAndIgnoreCase(cellValue, costIndicatorInDB);
				}
				
			} else if (colIndex == 30 + offset) {// Cost
				if (!QuoteUtil.isEmpty(cellValue)) {
					try {
						quoteItem.setCost(Double.parseDouble(cellValue));
					} catch (Exception ex) {
						 
						throwUploadErrorMesg(cellValue, row.getRowNum(),
								ResourceMB.getText("wq.msg.invalidFormat") + " :",
								ResourceMB.getParameterizedText("wq.label.costAtRow", "" + (row.getRowNum() + 1)), ex);
					}
				} else {
					quoteItem.setCost(null);
				}
			}
			// else if(colIndex == 29) {} //Min Sell
			// else if(colIndex == 30) {} //Norm Sell
			else if (colIndex == 33 + offset) {// Lead Time
				// TODO: validation
				quoteItem.setLeadTime(cellValue);
			} else if (colIndex == 34 + offset) {// MPQ
				if (!QuoteUtil.isEmpty(cellValue)) {
					try {
						Integer value = convertToInteger(cellValue);
						if (value == null)
							throw (new Exception());
						quoteItem.setMpq(value);
					} catch (Exception ex) {
						 
						throwUploadErrorMesg(cellValue, row.getRowNum(),
								ResourceMB.getText("wq.msg.invalidFormat") + " :",
								ResourceMB.getParameterizedText("wq.label.mpqAtRow", "" + (row.getRowNum() + 1)), ex);
					}
				} else {
					quoteItem.setMpq(null);
				}
			} else if (colIndex == 35 + offset) {// MOQ
				if (!QuoteUtil.isEmpty(cellValue)) {
					try {
						Integer value = convertToInteger(cellValue);
						if (value == null)
							throw (new Exception());
						quoteItem.setMoq(value);
					} catch (Exception ex) {
						 
						throwUploadErrorMesg(cellValue, row.getRowNum(),
								ResourceMB.getText("wq.msg.invalidFormat") + " :",
								ResourceMB.getParameterizedText("wq.label.moqAtRow", "" + (row.getRowNum() + 1)), ex);
					}
				} else {
					quoteItem.setMoq(null);
				}
			} else if (colIndex == 36 + offset) {// MOV
				if (!QuoteUtil.isEmpty(cellValue)) {
					try {
						Integer value = convertToInteger(cellValue);
						if (value == null)
							throw (new Exception());
						quoteItem.setMov(value);
					} catch (Exception ex) {
						 
						throwUploadErrorMesg(cellValue, row.getRowNum(),
								ResourceMB.getText("wq.msg.invalidFormat") + " :",
								ResourceMB.getParameterizedText("wq.label.movAtRow", "" + (row.getRowNum() + 1)), null);
					}
				} else {
					quoteItem.setMov(null);
				}
			}

			else if (colIndex == 40 + offset) {

				if (!QuoteUtil.isEmpty(cellValue)) {
					if (!sapDpaCustSB.isValidCustomerGroup(cellValue, quoteItem.getQuote().getBizUnit().getName(),
							quoteItem.getSoldToCustomer().getCustomerNumber(), quoteItem.getEndCustomer() == null ? null
									: quoteItem.getEndCustomer().getCustomerNumber())) {
						 
						throwUploadErrorMesg(cellValue, row.getRowNum(),
								ResourceMB.getText("wq.message.customerGroupNotMatched") + " :",
								ResourceMB.getParameterizedText("wq.message.customerGroupNotMatchedAtRow",
										"" + (row.getRowNum() + 1)),
								null);
					} else {
						quoteItem.setCustomerGroupId(cellValue);
					}
				} else {
					quoteItem.setCustomerGroupId(null);
				}
			} else if (colIndex == 53 + offset) {// Quotation effective date
				if (!QuoteUtil.isEmpty(cellValue)) {
					try {
						Date date = dateFormatOfDMY.parse(cellValue);
						quoteItem.setQuotationEffectiveDate(date);
					} catch (Exception e) {
 
						throwUploadErrorMesg(cellValue, row.getRowNum(),
								ResourceMB.getText("wq.msg.invalidFormat") + " :", ResourceMB.getParameterizedText(
										"wq.label.quotationEffectiveDateAtRow", "" + (row.getRowNum() + 1)),
								e);

					}
				} else {
					quoteItem.setQuotationEffectiveDate(null);
				}
			} else if (colIndex == 54 + offset) {// Quotation T&C
				// todo: validation
				quoteItem.setTermsAndConditions(cellValue);
			} else if (colIndex == 55 + offset) {// Rescheduling Window
				if (!QuoteUtil.isEmpty(cellValue)) {
					try {
						Integer value = convertToInteger(cellValue);
						if (value == null)
							throw (new Exception());
						quoteItem.setRescheduleWindow(value);
					} catch (Exception ex) {
						 
						throwUploadErrorMesg(cellValue, row.getRowNum(),
								ResourceMB.getText("wq.msg.invalidFormat") + " :", ResourceMB.getParameterizedText(
										"wq.label.rescheduleWindowAtRow", "" + (row.getRowNum() + 1)),
								ex);
					}
				} else {
					quoteItem.setRescheduleWindow(null);
				}
			} else if (colIndex == 56 + offset) {// Cancellation Window
				if (!QuoteUtil.isEmpty(cellValue)) {
					try {
						Integer value = convertToInteger(cellValue);
						if (value == null)
							throw (new Exception());
						quoteItem.setCancellationWindow(value);
					} catch (Exception ex) {
						 
						throwUploadErrorMesg(cellValue,
								row.getRowNum(), ResourceMB.getText("wq.msg.invalidFormat") + " :", ResourceMB
										.getParameterizedText("wq.label.cancelWindowAtRow", "" + (row.getRowNum() + 1)),
								ex);
					}
				} else {
					quoteItem.setCancellationWindow(null);
				}
			} else if (colIndex == 66 + offset) {// Design Location update
													// by defect 237
				if (!QuoteUtil.isEmpty(cellValue)) {
					quoteItem.setDesignLocation(cellValue);
				} else {
					quoteItem.setDesignLocation(null);
				}
			} else if (colIndex == 67 + offset) {// Design In Cat
				if (!QuoteUtil.isEmpty(cellValue)) {
					List<String> designInCats = cacheUtil.getDesignInCatList();
					// Bryan End
					if (designInCats.contains(cellValue)) {
						quoteItem.setDesignInCat(cellValue);
					} else {

						throwUploadErrorMesg(cellValue, row.getRowNum(),
								ResourceMB.getText("wq.msg.invalidFormat") + " :",
								ResourceMB.getText("wq.label.designInCatAtRow") + " " + (row.getRowNum() + 1) + ".",
								null);
					}
				} else {
					quoteItem.setDesignInCat(null);
				}
			} else if (colIndex == 87 + offset) {// Design Region :
				if (!QuoteUtil.isEmpty(cellValue)
						&& !org.apache.commons.lang.StringUtils.equals(cellValue, "-select-")) {
					boolean isAllowRegion = false;
					if (designRegionList != null || !designRegionList.isEmpty()) {
						for (SelectItem si : designRegionList) {
							if (si.getLabel().equals(cellValue)) {
								isAllowRegion = true;
								break;
							}
						}
					} else {
						 
						throwUploadErrorMesg(cellValue, row.getRowNum(),
								ResourceMB.getText("wq.label.designRegionEmpty"),
								ResourceMB.getText("wq.label.designRegionRow") + " " + (row.getRowNum() + 1) + ".",
								null);
					}
					if (isAllowRegion) {
						quoteItem.setDesignRegion(cellValue);
					} else {
						 
						throwUploadErrorMesg(cellValue, row.getRowNum(),
								ResourceMB.getText("wq.msg.invalidFormat") + " :",
								ResourceMB.getText("wq.label.designRegionRow") + " " + (row.getRowNum() + 1) + ".",
								null);
					}
				} else {
					quoteItem.setDesignRegion(null);
				}
			}
			//endreg
		}
		adjustAfterUpload(quoteItem, buyCurrOrCostIndicatorChanged);
	}
	

	private void throwUploadErrorMesg(String cellValue, int rowNum, String summary, String detail, Exception e)
			throws Exception {
		FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, summary, detail);
		FacesContext.getCurrentInstance().addMessage("workingPlatformGrowl", msg);
		LOGGER.log(Level.WARNING, "cellValue=[" + cellValue + "]");
		LOGGER.log(Level.WARNING, msg.getSummary() + " - " + msg.getDetail());
		throw (e == null ? new Exception() : e);
	}

	private void adjustAfterUpload(QuoteItem quoteItem, boolean buyCurrOrCostIndicatorChanged) {
		if (buyCurrOrCostIndicatorChanged) {
			quoteSB.updateByBuyCurrChanged(quoteItem);
		} else {
			quoteItem.reCalMargin();
		}
	}

	public void preExportOrder(Object order) {
		for (int i = 0; i < headerArray.length; i++) {
			((StringBuilder) order).append(headerArray[i]);
			if (i != headerArray.length) {
				((StringBuilder) order).append(",");
			}
		}
	}

	public void postProcessQCXLS(Object document) {
		LOGGER.info("User: " + UserInfo.getUser().getEmployeeId() + " Export QC Working platform table");
		HSSFWorkbook wb = (HSSFWorkbook) document;
		HSSFSheet sheet = wb.getSheetAt(0);

		CellStyle stylePink = ExcelUtil.createStyle(wb, POICellColor.OPTIONAL, CellStyle.ALIGN_LEFT);
		CellStyle styleYellow = ExcelUtil.createStyle(wb, POICellColor.MANDATORY, CellStyle.ALIGN_LEFT);
		CellStyle sheetStyle = ExcelUtil.createStyle(wb, POICellColor.NORMAL, CellStyle.ALIGN_LEFT);

		sheet = wb.getSheetAt(0);
		for (int i = 0; i < sheet.getPhysicalNumberOfRows(); i++) {
			HSSFRow row = sheet.getRow(i);
			for (int c = 0; c < row.getPhysicalNumberOfCells(); c++) {
				try {// defect 243
					if (c == 5 || c == 6 || c == 12 || c == 34 || c == 7 || c == 28 || c == 27 || c == 9 || c == 35
							|| c == 36 || c == 19 || c == 33) {
						row.getCell(c).setCellStyle(styleYellow);
					} else if (c == 8 || c == 8 || c == 58 || c == 59 || c == 79 || c == 13 || c == 11 || c == 15
							|| c == 37 || c == 20 || c == 14 || c == 17 || c == 18 || c == 39 || c == 40 || c == 38
							|| c == 10) {
						row.getCell(c).setCellStyle(stylePink);
					} else {
						row.getCell(c).setCellStyle(sheetStyle);
					}
					if (c == 32 && i != 0) {// defect 268
						String quantityBreak = row.getCell(c).getStringCellValue();
						if (quantityBreak != null && quantityBreak.length() > 0) {
							quantityBreak = quantityBreak.replace("<div style='width:100%'>", "")
									.replaceAll("<div style='float:left;width:50%'>", "")
									.replaceAll("</div><div style='float:right;width:50%' >", "   ")
									.replaceAll("</div>", "\r\n").replaceAll("\r\n\r\n", "");
							row.getCell(c).setCellValue(quantityBreak);
						}
					}
				} catch (Exception e) {
					LOGGER.log(Level.WARNING,
							"Error occured for User Id: " + UserInfo.getUser().getEmployeeId() + ", Sheet name"
									+ sheet.getSheetName() + "Reason for failure: "
									+ MessageFormatorUtil.getParameterizedStringFromException(e),
							e);
				}
			}
		}

		// TODO: 5 Valid RFQ
		// TODO: 27 Cost Indicator
		// TODO: 9 Qty Indicator
		// TODO: 13 Multi Usage
		// TODO: 10 Allocation Part
		// TODO: 79 Sap Part Number

		// TODO: 58 Design Location
		// TODO: 78 design region
		// TODO: 59 Design-In Cat

		String[] booleanItems = { QuoteConstant.OPTION_YES, QuoteConstant.OPTION_NO };
		DVConstraint booleanConstraint = DVConstraint.createExplicitListConstraint(booleanItems);

		CellRangeAddressList validRFQRegions = new CellRangeAddressList(1, -1, 5, 5);
		CellRangeAddressList multiUsageRegions = new CellRangeAddressList(1, -1, 13, 13);
		CellRangeAddressList allocationPartRegions = new CellRangeAddressList(1, -1, 10, 10);
		CellRangeAddressList qtyIndicatorRegions = new CellRangeAddressList(1, -1, 9, 9);
		CellRangeAddressList qCExternalCommentRegions = new CellRangeAddressList(1, -1, 11, 11);
		CellRangeAddressList qCInternalCommentRegions = new CellRangeAddressList(1, -1, 15, 15);
		HSSFDataValidation multiUsageDataValidation = new HSSFDataValidation(multiUsageRegions, booleanConstraint);
		HSSFDataValidation validRFQDataValidation = new HSSFDataValidation(validRFQRegions, booleanConstraint);
		HSSFDataValidation allocationPartDataValidation = new HSSFDataValidation(allocationPartRegions,
				booleanConstraint);
		sheet.addValidationData(validRFQDataValidation);
		sheet.addValidationData(multiUsageDataValidation);
		sheet.addValidationData(allocationPartDataValidation);

		CellRangeAddressList costIndicatorRegions = new CellRangeAddressList(1, -1, 27, 27);
		CellRangeAddressList designLocaationRegions = new CellRangeAddressList(1, -1, 58, 58);
		CellRangeAddressList designRegionRegions = new CellRangeAddressList(1, -1, 79, 79);

		CellRangeAddressList designInCatRegions = new CellRangeAddressList(1, -1, 59, 59);
		// Bryan Start
		// String[] designLocaationItems = (String[])
		// DesignLocationCacheManager.getDesignLocationMap().keySet().toArray(new
		// String[0]);
		String[] designLocaationItems = (String[]) cacheUtil.getDesignLocationMap().keySet().toArray(new String[0]);

		// String[] designInCatItems = (String[])
		// DesignInCatCacheManager.getDesignInCatList().toArray(new String[0]);
		String[] designInCatItems = (String[]) cacheUtil.getDesignInCatList().toArray(new String[0]);

		// String[] designRegionItems = (String[])
		// DesignLocationCacheManager.getDesignRegionList().toArray(new
		// String[0]);
		String[] designRegionItems = (String[]) cacheUtil.getDesignRegionList().toArray(new String[0]);

		// String[] costIndicatorItems = (String[])
		// CostIndicatorCacheManager.getCostIndicator().toArray(new String[0]);
		String[] costIndicatorItems = (String[]) cacheUtil.getCostIndicator().toArray(new String[0]);

		// String[] qtyIndicatorItems = (String[])
		// QtyIndicatorCacheManager.getQtyIndicatorList().toArray(new
		// String[0]);
		String[] qtyIndicatorItems = (String[]) cacheUtil.getQtyIndicatorList().toArray(new String[0]);
		// Bryan End
		List<String> avnetQuoteCentreComment = new ArrayList<>();
		for (AvnetQuoteCentreCommentVO vo : avnetQuoteCentreCommentList) {
			avnetQuoteCentreComment.add(vo.getName());
		}
		String[] qCExternalCommentItems = (String[]) avnetQuoteCentreComment.toArray(new String[0]);
		String[] qCInternalCommentItems = (String[]) avnetQuoteCentreComment.toArray(new String[0]);
		HSSFSheet hidden = wb.createSheet("hidden");
		wb.setSheetHidden(1, true);

		for (int i = 0, length = designLocaationItems.length; i < length; i++) {
			HSSFRow row = hidden.createRow(i);
			HSSFCell cell = row.createCell(0);
			cell.setCellValue(designLocaationItems[i]);
		}
		for (int i = 0, length = designInCatItems.length; i < length; i++) {
			HSSFRow row = hidden.getRow(i);
			if (row == null) {
				row = hidden.createRow(i);
			}
			HSSFCell cell = row.createCell(2);
			cell.setCellValue(designInCatItems[i]);
		}
		for (int i = 0, length = designRegionItems.length; i < length; i++) {
			HSSFRow row = hidden.getRow(i);
			if (row == null) {
				row = hidden.createRow(i);
			}
			HSSFCell cell = row.createCell(1);
			cell.setCellValue(designRegionItems[i]);
		}
		for (int i = 0, length = costIndicatorItems.length; i < length; i++) {
			HSSFRow row = hidden.getRow(i);
			if (row == null) {
				row = hidden.createRow(i);
			}
			HSSFCell cell = row.createCell(3);
			cell.setCellValue(costIndicatorItems[i]);
		}
		for (int i = 0, length = qtyIndicatorItems.length; i < length; i++) {
			HSSFRow row = hidden.getRow(i);
			if (row == null) {
				row = hidden.createRow(i);
			}
			HSSFCell cell = row.createCell(4);
			cell.setCellValue(qtyIndicatorItems[i]);
		}
		for (int i = 0, length = qCExternalCommentItems.length; i < length; i++) {
			HSSFRow row = hidden.getRow(i);
			if (row == null) {
				row = hidden.createRow(i);
			}
			HSSFCell cell = row.createCell(5);
			cell.setCellValue(qCExternalCommentItems[i]);
		}
		for (int i = 0, length = qCInternalCommentItems.length; i < length; i++) {
			HSSFRow row = hidden.getRow(i);
			if (row == null) {
				row = hidden.createRow(i);
			}
			HSSFCell cell = row.createCell(6);
			cell.setCellValue(qCInternalCommentItems[i]);
		}
		try {
			Name namedCelldesignLocation = wb.createName();
			namedCelldesignLocation.setNameName("hidden");
			namedCelldesignLocation.setRefersToFormula("hidden!$A$1:$A$" + designLocaationItems.length);
			DVConstraint constraintdesignLocation = DVConstraint.createFormulaListConstraint("hidden");

			HSSFDataValidation designLocationDataValidation = new HSSFDataValidation(designLocaationRegions,
					constraintdesignLocation);
			designLocationDataValidation.setShowErrorBox(false);
			sheet.addValidationData(designLocationDataValidation);

			Name namedCellDesignInCat = wb.createName();
			namedCellDesignInCat.setNameName("hidden2");
			namedCellDesignInCat.setRefersToFormula("hidden!$C$1:$C$" + designInCatItems.length);
			DVConstraint constraintDesignInCat = DVConstraint.createFormulaListConstraint("hidden2");

			HSSFDataValidation designInCatDataValidation = new HSSFDataValidation(designInCatRegions,
					constraintDesignInCat);
			designInCatDataValidation.setShowErrorBox(false);
			sheet.addValidationData(designInCatDataValidation);

			Name namedCellDesignRegion = wb.createName();
			namedCellDesignRegion.setNameName("hidden1");
			namedCellDesignRegion.setRefersToFormula("hidden!$B$1:$B$" + designRegionItems.length);
			DVConstraint constraintDesignRegion = DVConstraint.createFormulaListConstraint("hidden1");
			HSSFDataValidation designRegionDataValidation = new HSSFDataValidation(designRegionRegions,
					constraintDesignRegion);
			designRegionDataValidation.setShowErrorBox(false);
			sheet.addValidationData(designRegionDataValidation);

			Name namedCellcostIndicator = wb.createName();
			namedCellcostIndicator.setNameName("hidden3");
			namedCellcostIndicator.setRefersToFormula("hidden!$D$1:$D$" + costIndicatorItems.length);
			DVConstraint constraintcostIndicator = DVConstraint.createFormulaListConstraint("hidden3");

			HSSFDataValidation costIndicatorDataValidation = new HSSFDataValidation(costIndicatorRegions,
					constraintcostIndicator);
			costIndicatorDataValidation.setShowErrorBox(false);
			sheet.addValidationData(costIndicatorDataValidation);

			Name namedCellqtyIndicator = wb.createName();
			namedCellqtyIndicator.setNameName("hidden4");
			namedCellqtyIndicator.setRefersToFormula("hidden!$E$1:$E$" + qtyIndicatorItems.length);
			DVConstraint constraintqtyIndicator = DVConstraint.createFormulaListConstraint("hidden4");

			HSSFDataValidation qtyIndicatorDataValidation = new HSSFDataValidation(qtyIndicatorRegions,
					constraintqtyIndicator);
			qtyIndicatorDataValidation.setShowErrorBox(false);
			sheet.addValidationData(qtyIndicatorDataValidation);

			Name namedCellQCExternalComment = wb.createName();
			namedCellQCExternalComment.setNameName("hidden5");
			namedCellQCExternalComment.setRefersToFormula("hidden!$F$1:$F$" + qCExternalCommentItems.length);
			DVConstraint constraintQCExternalComment = DVConstraint.createFormulaListConstraint("hidden5");

			HSSFDataValidation qCExternalCommentDataValidation = new HSSFDataValidation(qCExternalCommentRegions,
					constraintQCExternalComment);
			qCExternalCommentDataValidation.setShowErrorBox(false);
			sheet.addValidationData(qCExternalCommentDataValidation);

			Name namedCellQCInternalComment = wb.createName();
			namedCellQCInternalComment.setNameName("hidden6");
			namedCellQCInternalComment.setRefersToFormula("hidden!$G$1:$G$" + qCInternalCommentItems.length);
			DVConstraint constraintQCInternalComment = DVConstraint.createFormulaListConstraint("hidden6");

			HSSFDataValidation qCInternalCommentDataValidation = new HSSFDataValidation(qCInternalCommentRegions,
					constraintQCInternalComment);
			qCInternalCommentDataValidation.setShowErrorBox(false);
			sheet.addValidationData(qCInternalCommentDataValidation);
		} catch (Exception e) {
			LOGGER.log(Level.WARNING, "Error occured for User Id: " + UserInfo.getUser().getEmployeeId() + ", "
					+ "Reason for failure: " + MessageFormatorUtil.getParameterizedStringFromException(e), e);
		}
	}

	public void postProcessXLSForQuoteHistory(Object document) {
		String[] columns = { "Quote Stage", "RFQ Status", "Form#", "Avnet Quote#", "Sales Cost Part", "Sales Cost Type",
				"MFR", "MFR P/N", "Avnet Quoted Part", "Customer", "End Customer", "Customer Group ID", "Required Qty",
				"EAU", "Quoted Margin %", "BUY CUR", "Cost", "Sales Cost", "Suggested Resale", "Quoted Price (BUY CUR)",
				"Target Resale (BUY CUR)", "Cost Indicator", "RFQ CUR", "Quoted Price (RFQ CUR)",
				"Target Resale (RFQ CUR)", "FNL CUR", "Final Price", "Salesman", "Team", "Price Validity",
				"Shipment Validity", "MFR Quote #", "MFR Debit #", "MFR Quote Qty", "QC Internal Comment",
				"QC External Comment", "PMOQ", "MPQ", "MOQ", "MOV", "Lead Time", "Multi Usage", "Qty Indicator",
				"RFQ Submission Date", "Quote Release Date", "Target Price Margin %", "Quote Reference Margin %",
				"Customer Type", "Application", "Project Name", "Design Location", "Rescheduling Window",
				"Cancellation Window", "Quotation T&C", "Quote Type", "RFQ Form Attachment", "RFQ Item Attachment",
				"Refresh Attachment", "PM Attachment", "QC Attachment", "PM Comment", "Remarks", "Item Remarks",
				"Competitor Information", "Design-in Cat", "DRMS Project ID", "MFR DR#", "DR Expiry Date",
				"Authorized GP %", "Reasons for Authorized GP% Change", "Remarks of Reason", "PP Schedule",
				"MP Schedule", "PO Expiry Date", "Quote Expiry Date", "Avnet Quoted Qty", "Requester Reference",
				"Salesman Code", "LOA", "Resale Indicator", "Alloca- tion Part", "Revert Version", "First RFQ Code",
				"Segment", "BMT Biz", "Reason For Refresh", "Copy to CS", "Product Group 2", "Business Program Type",
				"Sold To Code", "Ship To Code", "Ship To Party", "End Customer Code", "SAP Material Number", "SPR",
				"Quoted By", "Sold To Party (Chinese)", "BMT Flag", "Design Region", "BMT MFR DR#", "BMT Suggest Cost",
				"BMT Suggest Resale", "BMT Suggest Margin", "BMT MFR Quoted#", "BMT SQ Effective Date",
				"BMT SQExpiry Date", "BMT Comments", "BMT Quoted Qty", "BMT Suggest Cost(Non-USD)",
				"BMT Suggest Resale(Non-USD)", "BMT Currency", "BMT Exchange Rate(Non-USD)" };
		postProcessXLS(document, columns);
	}

	public void postProcessXLS(Object document, String[] columns) {
		ejbCommonSB.postProcessXLSForWorkingPlantPage(document, columns, quotationHistorys);
	}

	private void doKeepFirstRow() {
		final DataTable d = (DataTable) FacesContext.getCurrentInstance().getViewRoot()
				.findComponent(":form_pendinglist:datatable_pendinglist");
		if (d != null) {
			firstRow = d.getFirst();
		} else {
			firstRow = 0;
		}
		keepFirstRow = true;
	}

	public void restoreFirstRow() {

		if (keepFirstRow == true) {
			final DataTable d = (DataTable) FacesContext.getCurrentInstance().getViewRoot()
					.findComponent(":form_pendinglist:datatable_pendinglist");
			if (d != null && this.searchedWorkingPlatformItems != null) {
				if (firstRow >= this.searchedWorkingPlatformItems.size()) {
					d.setFirst(0);
				} else {
					d.setFirst(firstRow);
				}
			}
		}

		keepFirstRow = false;
	}

	public void onPageChange(PageEvent event) {
		this.setFirstRowPosition(((DataTable) event.getSource()).getFirst());
		LOGGER.info(String.valueOf(firstRowPosition));
	}

	public SelectItem[] getMfrSelectList() {
		return mfrSelectList;
	}

	public void setMfrSelectList(SelectItem[] mfrSelectList) {
		this.mfrSelectList = mfrSelectList;
	}

	public List<WorkingPlatformItemVO> getSelectedWorkingPlatformItems() {
		return selectedWorkingPlatformItems;
	}

	public void setSelectedWorkingPlatformItems(List<WorkingPlatformItemVO> selectedWorkingPlatformItems) {
		this.selectedWorkingPlatformItems = selectedWorkingPlatformItems;
	}

	public void setSelectedWorkingPlatformItem(WorkingPlatformItemVO selectedWorkingPlatformItem) {
		this.selectedWorkingPlatformItem = selectedWorkingPlatformItem;
	}

	public int getRbitCounter() {
		return rbitCounter;
	}

	public void setBitCounter(int bitCounter) {
		this.bitCounter = bitCounter;
	}

	public void setBqCounter(int bqCounter) {
		this.bqCounter = bqCounter;
	}

	public int getBitCounter() {
		return bitCounter;
	}

	public int getBqCounter() {
		return bqCounter;
	}

	public int getRbqCounter() {
		return rbqCounter;
	}

	public void setRbitCounter(int rbitCounter) {
		this.rbitCounter = rbitCounter;
	}

	public void setRbqCounter(int rbqCounter) {
		this.rbqCounter = rbqCounter;
	}

	public int getQcCounter() {
		return qcCounter;
	}

	public void setQcCounter(int qcCounter) {
		this.qcCounter = qcCounter;
	}

	public int getItCounter() {
		return itCounter;
	}

	public void setItCounter(int itCounter) {
		this.itCounter = itCounter;
	}

	public int getSqCounter() {
		return sqCounter;
	}

	public void setSqCounter(int sqCounter) {
		this.sqCounter = sqCounter;
	}

	public List<WorkingPlatformItemVO> getQuotationHistorys() {
		return quotationHistorys;
	}

	public void setQuotationHistorys(List<WorkingPlatformItemVO> quotationHistorys) {
		this.quotationHistorys = quotationHistorys;
	}

	public void setQuoteSB(QuoteSB quoteSB) {
		this.quoteSB = quoteSB;
	}

	public BizUnit getBizUnit() {
		return bizUnit;
	}

	public void setBizUnit(BizUnit bizUnit) {
		this.bizUnit = bizUnit;
	}

	/**
	 * @return the searchedWorkingPlatformItems
	 */
	public List<WorkingPlatformItemVO> getSearchedWorkingPlatformItems() {
		return searchedWorkingPlatformItems;
	}

	/**
	 * @param searchedWorkingPlatformItems
	 *            the searchedWorkingPlatformItems to set
	 */
	public void setSearchedWorkingPlatformItems(List<WorkingPlatformItemVO> searchedWorkingPlatformItems) {
		if (workingPlatformItems != null) {

			this.searchedWorkingPlatformItems = searchedWorkingPlatformItems;
			// rfqStatusCount();
			RequestContext context = RequestContext.getCurrentInstance();

			Collections.sort(filterMfrOptionList);
			Collections.sort(filterTeamOptionList);
			Collections.sort(filterCustomerOptionList);

			context.addCallbackParam("mfrCode_size", filterMfrOptionList.size());
			context.addCallbackParam("team_size", filterTeamOptionList.size());
			context.addCallbackParam("soldTo_size", filterCustomerOptionList.size());
			context.addCallbackParam("bmt_size", filterBmtFlagOptionList.size());

			for (int i = 0; i < filterMfrOptionList.size(); i++)
				context.addCallbackParam("mfrCode" + i, StringEscapeUtils.escapeXml(filterMfrOptionList.get(i)));
			for (int i = 0; i < filterTeamOptionList.size(); i++)
				context.addCallbackParam("team" + i, StringEscapeUtils.escapeXml(filterTeamOptionList.get(i)));
			for (int i = 0; i < filterCustomerOptionList.size(); i++)
				context.addCallbackParam("soldTo" + i, StringEscapeUtils.escapeXml(filterCustomerOptionList.get(i)));
			List<String> bmtKeys = new ArrayList<>(filterBmtFlagOptionList.keySet());
			for (int i = 0; i < bmtKeys.size(); i++) {
				context.addCallbackParam("bmtValue" + i, StringEscapeUtils.escapeXml(bmtKeys.get(i)));
				context.addCallbackParam("bmtKey" + i,
						StringEscapeUtils.escapeXml(filterBmtFlagOptionList.get(bmtKeys.get(i))));
			}

			FacesUtil.updateUI("form_pendinglist:counter");
		} else {
			this.searchedWorkingPlatformItems = searchedWorkingPlatformItems;
		}
	}

	public WorkingPlatformItemVO getSelectedWorkingPlatformItem() {
		return selectedWorkingPlatformItem;
	}

	public List<WorkingPlatformItemVO> getFilteredQuotationHistorys() {
		return filteredQuotationHistorys;
	}

	public void setFilteredQuotationHistorys(List<WorkingPlatformItemVO> filteredQuotationHistorys) {
		this.filteredQuotationHistorys = filteredQuotationHistorys;
	}

	public List<String> getMfrCodes() {
		return mfrCodes;
	}

	public void setMfrCodes(List<String> mfrCodes) {
		this.mfrCodes = mfrCodes;
	}

	public List<String> getMaterialTypes() {
		return materialTypes;
	}

	public void setMaterialTypes(List<String> materialTypes) {
		this.materialTypes = materialTypes;
	}

	public int getUpdateRfqCount() {
		return updateRfqCount;
	}

	public void setUpdateRfqCount(int updateRfqCount) {
		this.updateRfqCount = updateRfqCount;
	}

	public String getSelectedUpdateRfqStatus() {
		return selectedUpdateRfqStatus;
	}

	public void setSelectedUpdateRfqStatus(String selectedUpdateRfqStatus) {
		this.selectedUpdateRfqStatus = selectedUpdateRfqStatus;
	}

	public SelectItem[] getCostIndicatorSelectList() {
		return costIndicatorSelectList;
	}

	public void setCostIndicatorSelectList(SelectItem[] costIndicatorSelectList) {
		this.costIndicatorSelectList = costIndicatorSelectList;
	}

	public List<String> getCostIndicatorCodes() {
		return costIndicatorCodes;
	}

	public void setCostIndicatorCodes(List<String> costIndicatorCodes) {
		this.costIndicatorCodes = costIndicatorCodes;
	}

	/*
	 * public StreamedContent getWorkingPlatformTable() {
	 * 
	 * // logger.log(Level.INFO, "PERFORMANCE START - //
	 * getWorkingPlatformTable()"); downloadWorkingPlatform(); //
	 * logger.log(Level.INFO, "PERFORMANCE END - // getWorkingPlatformTable()");
	 * return workingPlatformTable; }
	 */

	/*
	 * public void setWorkingPlatformTable(StreamedContent workingPlatformTable)
	 * { this.workingPlatformTable = workingPlatformTable; }
	 */

	public void setPosSB(PosSB posSB) {
		this.posSB = posSB;
	}

	public String getCurrentQuarter() {
		return currentQuarter;
	}

	public void setCurrentQuarter(String currentQuarter) {
		this.currentQuarter = currentQuarter;
	}

	public String getLast1Quarter() {
		return last1Quarter;
	}

	public void setLast1Quarter(String last1Quarter) {
		this.last1Quarter = last1Quarter;
	}

	public String getLast2Quarter() {
		return last2Quarter;
	}

	public void setLast2Quarter(String last2Quarter) {
		this.last2Quarter = last2Quarter;
	}

	public String getLast3Quarter() {
		return last3Quarter;
	}

	public void setLast3Quarter(String last3Quarter) {
		this.last3Quarter = last3Quarter;
	}

	public String getSelectedQuoteNumber() {
		return selectedQuoteNumber;
	}

	public void setSelectedQuoteNumber(String selectedQuoteNumber) {
		this.selectedQuoteNumber = selectedQuoteNumber;
	}

	public String getUpdateId() {
		return updateId;
	}

	public void setUpdateId(String updateId) {
		this.updateId = updateId;
	}

	public void setVendorReportSB(VendorReportSB vendorReportSB) {
		this.vendorReportSB = vendorReportSB;
	}

	public List<VendorReport> getVendorQuotationHistorys() {
		return vendorQuotationHistorys;
	}

	public void setVendorQuotationHistorys(List<VendorReport> vendorQuotationHistorys) {
		this.vendorQuotationHistorys = vendorQuotationHistorys;
	}

	public List<VendorReport> getFilteredVendorQuotationHistorys() {
		return filteredVendorQuotationHistorys;
	}

	public void setFilteredVendorQuotationHistorys(List<VendorReport> filteredVendorQuotationHistorys) {
		this.filteredVendorQuotationHistorys = filteredVendorQuotationHistorys;
	}

	public String getEmailContent() {
		return emailContent;
	}

	public void setEmailContent(String emailContent) {
		this.emailContent = emailContent;
	}

	public String getEmailTo() {
		return emailTo;
	}

	public void setEmailTo(String emailTo) {
		this.emailTo = emailTo;
	}

	public String getEmailFrom() {
		return emailFrom;
	}

	public void setEmailFrom(String emailFrom) {
		this.emailFrom = emailFrom;
	}

	public String getEmailCc() {
		return emailCc;
	}

	public void setEmailCc(String emailCc) {
		this.emailCc = emailCc;
	}

	public void setCustomerSB(CustomerSB customerSB) {
		this.customerSB = customerSB;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public List<QuotationWarningVO> getQuotationWarnings() {
		return quotationWarnings;
	}

	public void setQuotationWarnings(List<QuotationWarningVO> quotationWarnings) {
		this.quotationWarnings = quotationWarnings;
	}

	public List<QuoteItem> getSelectedQuotationWarnings() {
		return selectedQuotationWarnings;
	}

	public void setSelectedQuotationWarnings(List<QuoteItem> selectedQuotationWarnings) {
		this.selectedQuotationWarnings = selectedQuotationWarnings;
	}

	public List<WorkingPlatformItemVO> getPreQuoteItems() {
		return preQuoteItems;
	}

	public void setPreQuoteItems(List<WorkingPlatformItemVO> preQuoteItems) {
		this.preQuoteItems = preQuoteItems;
	}

	public UploadedFile getWorkingPlatformExcel() {
		return workingPlatformExcel;
	}

	public void setWorkingPlatformExcel(UploadedFile workingPlatformExcel) {
		this.workingPlatformExcel = workingPlatformExcel;
	}

	public List<String> getEmailContents() {
		return emailContents;
	}

	public void setEmailContents(List<String> emailContents) {
		this.emailContents = emailContents;
	}

	public int getInternalTransferEmailCount() {
		return internalTransferEmailCount;
	}

	public void setInternalTransferEmailCount(int internalTransferEmailCount) {
		this.internalTransferEmailCount = internalTransferEmailCount;
	}

	public List<Attachment> getReferenceAttachments() {
		return referenceAttachments;
	}

	public void setReferenceAttachments(List<Attachment> referenceAttachments) {
		this.referenceAttachments = referenceAttachments;
	}

	public int getInvalidateRfqCount() {
		return invalidateRfqCount;
	}

	public void setInvalidateRfqCount(int invalidateRfqCount) {
		this.invalidateRfqCount = invalidateRfqCount;
	}

	public boolean isRefreshAll() {
		return refreshAll;
	}

	public void setRefreshAll(boolean refreshAll) {
		this.refreshAll = refreshAll;
	}

	public String getQuoteHistorySoldToCustomerNameSearch() {
		return quoteHistorySoldToCustomerNameSearch;
	}

	public void setQuoteHistorySoldToCustomerNameSearch(String quoteHistorySoldToCustomerNameSearch) {
		this.quoteHistorySoldToCustomerNameSearch = quoteHistorySoldToCustomerNameSearch;
	}

	public String getQuoteHistoryPartNumberSearch() {
		return quoteHistoryPartNumberSearch;
	}

	public void setQuoteHistoryPartNumberSearch(String quoteHistoryPartNumberSearch) {
		this.quoteHistoryPartNumberSearch = quoteHistoryPartNumberSearch;
	}

	public String getQuoteHistoryMfrSearch() {
		return quoteHistoryMfrSearch;
	}

	public void setQuoteHistoryMfrSearch(String quoteHistoryMfrSearch) {
		this.quoteHistoryMfrSearch = quoteHistoryMfrSearch;
	}

	public String getVendorQuoteHistorySoldToCustomerNameSearch() {
		return vendorQuoteHistorySoldToCustomerNameSearch;
	}

	public void setVendorQuoteHistorySoldToCustomerNameSearch(String vendorQuoteHistorySoldToCustomerNameSearch) {
		this.vendorQuoteHistorySoldToCustomerNameSearch = vendorQuoteHistorySoldToCustomerNameSearch;
	}

	public String getVendorQuoteHistoryPartNumberSearch() {
		return vendorQuoteHistoryPartNumberSearch;
	}

	public void setVendorQuoteHistoryPartNumberSearch(String vendorQuoteHistoryPartNumberSearch) {
		this.vendorQuoteHistoryPartNumberSearch = vendorQuoteHistoryPartNumberSearch;
	}

	public String getVendorQuoteHistoryMfrSearch() {
		return vendorQuoteHistoryMfrSearch;
	}

	public void setVendorQuoteHistoryMfrSearch(String vendorQuoteHistoryMfrSearch) {
		this.vendorQuoteHistoryMfrSearch = vendorQuoteHistoryMfrSearch;
	}

	public String getInternalTransferComment() {
		return internalTransferComment;
	}

	public void setInternalTransferComment(String internalTransferComment) {
		this.internalTransferComment = internalTransferComment;
	}

	public String getInternalComment() {
		return internalComment;
	}

	public void setInternalComment(String internalComment) {
		this.internalComment = internalComment;
	}

	public List<Attachment> getDisplayAttachmentList() {
		return displayAttachmentList;
	}

	public void setDisplayAttachmentList(List<Attachment> displayAttachmentList) {
		this.displayAttachmentList = displayAttachmentList;
	}

	public StreamedContent getRfqAttachment() {
		return rfqAttachment;
	}

	public void setRfqAttachment(StreamedContent rfqAttachment) {
		this.rfqAttachment = rfqAttachment;
	}

	public List<WorkingPlatformEmailVO> getUpdateStatusItems() {
		return updateStatusItems;
	}

	public void setUpdateStatusItems(List<WorkingPlatformEmailVO> updateStatusItems) {
		this.updateStatusItems = updateStatusItems;
	}

	public int getRowIndex() {
		return rowIndex;
	}

	public void setRowIndex(int rowIndex) {
		this.rowIndex = rowIndex;
	}

	public List<Attachment> getAttachments() {
		if (attachments != null)
			attachmentCount = attachments.size();
		else
			attachmentCount = 0;
		return attachments;
	}

	public void setAttachments(List<Attachment> attachments) {
		this.attachments = attachments;
	}

	public String getMaterialType() {
		return materialType;
	}

	public void setMaterialType(String materialType) {
		this.materialType = materialType;
	}

	public SelectItem[] getMaterialTypeSelectList() {
		return materialTypeSelectList;
	}

	public void setMaterialTypeSelectList(SelectItem[] materialTypeSelectList) {
		this.materialTypeSelectList = materialTypeSelectList;
	}

	public int getQuoteItemCount() {
		return quoteItemCount;
	}

	public void setQuoteItemCount(int quoteItemCount) {
		this.quoteItemCount = quoteItemCount;
	}

	public int getSelectedRowIndex() {
		return selectedRowIndex;
	}

	public void setSelectedRowIndex(int selectedRowIndex) {
		this.selectedRowIndex = selectedRowIndex;
	}

	public boolean isHasRefreshAttachment(QuoteItem item) {

		if (item != null && item.isAttachmentAvailable()) {
			item = quoteSB.findByPK(item.getId());
			List<Attachment> attaches = item.getAttachments();
			if (attaches != null && attaches.size() > 0) {
				for (Attachment attach : attaches) {
					if (attach.getType() != null && attach.getType().equals(QuoteSBConstant.ATTACHMENT_TYPE_REFRESH))
						return true;
				}
			}
		}
		return false;
	}

	public boolean isHasInternalTransferAttachment(QuoteItem item) {

		if (item != null && item.isAttachmentAvailable()) {
			item = quoteSB.findByPK(item.getId());
			List<Attachment> attaches = item.getAttachments();
			if (attaches != null && attaches.size() > 0) {
				for (Attachment attach : attaches) {
					if (attach.getType() != null
							&& attach.getType().equals(QuoteSBConstant.ATTACHMENT_TYPE_INTERNAL_TRANSFER))
						return true;
				}
			}
		}

		return false;
	}

	// QuoteItem quoteItem = quoteSB.findByPK(item.getId());
	public boolean isHasFormAttachment(QuoteItem item) {

		if (item != null) {
			Quote quote = item.getQuote();
			if (quote != null && quote.isAttachmentAvailable()) {
				quote = quoteSB.findQuoteByPK(item.getQuote().getId());
				if (quote != null) {
					List<Attachment> attaches = quote.getAttachments();
					if (attaches != null && attaches.size() > 0) {
						return true;
					}
				}
			}
		}

		return false;
	}

	public boolean isHasAttachment(QuoteItem item) {
		if (item != null && item.isAttachmentAvailable()) {
			item = quoteSB.findByPK(item.getId());
			List<Attachment> attaches = item.getAttachments();
			if (attaches != null && attaches.size() > 0) {
				for (Attachment attach : attaches) {
					if (attach.getType() != null && attach.getType().equals(QuoteSBConstant.ATTACHMENT_TYPE_QUOTE_ITEM))
						return true;
				}
			}
		}

		return false;
	}

	public List<ProceedQuotationVO> getProceedQuotations() {
		return proceedQuotations;
	}

	public void setProceedQuotations(List<ProceedQuotationVO> proceedQuotations) {
		this.proceedQuotations = proceedQuotations;
	}

	public int getProceedQuotationCount() {
		return proceedQuotationCount;
	}

	public void setProceedQuotationCount(int proceedQuotationCount) {
		this.proceedQuotationCount = proceedQuotationCount;
	}

	public int getRitCounter() {
		return ritCounter;
	}

	public void setRitCounter(int ritCounter) {
		this.ritCounter = ritCounter;
	}

	public boolean isShowAllCustomerPos() {
		return showAllCustomerPos;
	}

	public void setShowAllCustomerPos(boolean showAllCustomerPos) {
		this.showAllCustomerPos = showAllCustomerPos;
	}

	public int getSearchMaterialDetailsCount() {
		return searchMaterialDetailsCount;
	}

	public void setSearchMaterialDetailsCount(int searchMaterialDetailsCount) {
		this.searchMaterialDetailsCount = searchMaterialDetailsCount;
	}

	public List<AvnetQuoteCentreCommentVO> getAvnetQuoteCentreCommentList() {
		return avnetQuoteCentreCommentList;
	}

	public void setAvnetQuoteCentreCommentList(List<AvnetQuoteCentreCommentVO> avnetQuoteCentreCommentList) {
		this.avnetQuoteCentreCommentList = avnetQuoteCentreCommentList;
	}

	public List<TermsAndConditionsVO> getTermsAndConditionsList() {
		return termsAndConditionsList;
	}

	public void setTermsAndConditionsList(List<TermsAndConditionsVO> termsAndConditionsList) {
		this.termsAndConditionsList = termsAndConditionsList;
	}

	public AvnetQuoteCentreCommentVO getSelectedQcExternalComment() {
		return selectedQcExternalComment;
	}

	public void setSelectedQcExternalComment(AvnetQuoteCentreCommentVO selectedQcExternalComment) {
		this.selectedQcExternalComment = selectedQcExternalComment;
	}

	public TermsAndConditionsVO getSelectedTermsAndConditions() {
		return selectedTermsAndConditions;
	}

	public void setSelectedTermsAndConditions(TermsAndConditionsVO selectedTermsAndConditions) {
		this.selectedTermsAndConditions = selectedTermsAndConditions;
	}

	public WorkingPlatformItemVO getSelectedQuoteItem() {
		return selectedQuoteItem;
	}

	public void setSelectedQuoteItem(WorkingPlatformItemVO selectedQuoteItem) {
		this.selectedQuoteItem = selectedQuoteItem;
	}

	public AvnetQuoteCentreCommentVO getSelectedQcInternalComment() {
		return selectedQcInternalComment;
	}

	public void setSelectedQcInternalComment(AvnetQuoteCentreCommentVO selectedQcInternalComment) {
		this.selectedQcInternalComment = selectedQcInternalComment;
	}

	public AvnetQuoteCentreCommentVO getSelectedDetailQcExternalComment() {
		return selectedDetailQcExternalComment;
	}

	public void setSelectedDetailQcExternalComment(AvnetQuoteCentreCommentVO selectedDetailQcExternalComment) {
		this.selectedDetailQcExternalComment = selectedDetailQcExternalComment;
	}

	public AvnetQuoteCentreCommentVO getSelectedDetailQcInternalComment() {
		return selectedDetailQcInternalComment;
	}

	public void setSelectedDetailQcInternalComment(AvnetQuoteCentreCommentVO selectedDetailQcInternalComment) {
		this.selectedDetailQcInternalComment = selectedDetailQcInternalComment;
	}

	public TermsAndConditionsVO getSelectedDetailTermsAndConditions() {
		return selectedDetailTermsAndConditions;
	}

	public void setSelectedDetailTermsAndConditions(TermsAndConditionsVO selectedDetailTermsAndConditions) {
		this.selectedDetailTermsAndConditions = selectedDetailTermsAndConditions;
	}

	public List<PosSummary> getPosSummary() {
		return posSummary;
	}

	public void setPosSummary(List<PosSummary> posSummary) {
		this.posSummary = posSummary;
	}

	public boolean isEditMode() {
		return editMode;
	}

	public void setEditMode(boolean editMode) {
		this.editMode = editMode;
	}

	public String getDisplayExternalComment() {
		return displayExternalComment;
	}

	public void setDisplayExternalComment(String displayExternalComment) {
		this.displayExternalComment = displayExternalComment;
	}

	public String getDisplayInternalComment() {
		return displayInternalComment;
	}

	public void setDisplayInternalComment(String displayInternalComment) {
		this.displayInternalComment = displayInternalComment;
	}

	public String getDisplayTermsAndConditions() {
		return displayTermsAndConditions;
	}

	public void setDisplayTermsAndConditions(String displayTermsAndConditions) {
		this.displayTermsAndConditions = displayTermsAndConditions;
	}

	public String getDisplayPmComment() {
		return displayPmComment;
	}

	public void setDisplayPmComment(String displayPmComment) {
		this.displayPmComment = displayPmComment;
	}

	public SelectItem[] getFilterMfrSelectList() {
		return filterMfrSelectList;
	}

	public void setFilterMfrSelectList(SelectItem[] filterMfrSelectList) {
		this.filterMfrSelectList = filterMfrSelectList;
	}

	public SelectItem[] getFilterTeamSelectList() {
		return filterTeamSelectList;
	}

	public void setFilterTeamSelectList(SelectItem[] filterTeamSelectList) {
		this.filterTeamSelectList = filterTeamSelectList;
	}

	public SelectItem[] getFilterCustomerSelectList() {
		return filterCustomerSelectList;
	}

	public List<SelectItem> getFilterBmtFlagSelectList() {
		return filterBmtFlagSelectList;
	}

	public void setFilterCustomerSelectList(SelectItem[] filterCustomerSelectList) {
		this.filterCustomerSelectList = filterCustomerSelectList;
	}

	public SelectItem[] getQtyIndicatorOptions() {
		return this.qtyIndicatorOptions;
	}

	public void setQtyIndicatorOptions(SelectItem[] qtyIndicatorOptions) {
		this.qtyIndicatorOptions = qtyIndicatorOptions;
	}

	public List<WorkingPlatformEmailVO> getInvalidQuoteEmailVOs() {
		return invalidQuoteEmailVOs;
	}

	public void setInvalidQuoteEmailVOs(List<WorkingPlatformEmailVO> invalidQuoteEmailVOs) {
		this.invalidQuoteEmailVOs = invalidQuoteEmailVOs;
	}

	public String getPosHistorySoldToCustomerNameSearch() {
		return posHistorySoldToCustomerNameSearch;
	}

	public void setPosHistorySoldToCustomerNameSearch(String posHistorySoldToCustomerNameSearch) {
		this.posHistorySoldToCustomerNameSearch = posHistorySoldToCustomerNameSearch;
	}

	public String getPosHistoryPartNumberSearch() {
		return posHistoryPartNumberSearch;
	}

	public void setPosHistoryPartNumberSearch(String posHistoryPartNumberSearch) {
		this.posHistoryPartNumberSearch = posHistoryPartNumberSearch;
	}

	public String getPosHistoryMfrSearch() {
		return posHistoryMfrSearch;
	}

	public void setPosHistoryMfrSearch(String posHistoryMfrSearch) {
		this.posHistoryMfrSearch = posHistoryMfrSearch;
	}

	public String getFreeStockPartNumberSearch() {
		return freeStockPartNumberSearch;
	}

	public void setFreeStockPartNumberSearch(String freeStockPartNumberSearch) {
		this.freeStockPartNumberSearch = freeStockPartNumberSearch;
	}

	public String getFreeStockMfrSearch() {
		return freeStockMfrSearch;
	}

	public void setFreeStockMfrSearch(String freeStockMfrSearch) {
		this.freeStockMfrSearch = freeStockMfrSearch;
	}

	public List<FreeStock> getFreeStockList() {
		return freeStockList;
	}

	public void setFreeStockList(List<FreeStock> freeStockList) {
		this.freeStockList = freeStockList;
	}

	public List<FreeStock> getFilteredFreeStockList() {
		return filteredFreeStockList;
	}

	public void setFilteredFreeStockList(List<FreeStock> filteredFreeStockList) {
		this.filteredFreeStockList = filteredFreeStockList;
	}

	public List<User> getEmailAddressList() {
		return emailAddressList;
	}

	public void setEmailAddressList(List<User> emailAddressList) {
		this.emailAddressList = emailAddressList;
	}

	public boolean isAnyOneProceed() {
		if (quotationWarnings != null && quotationWarnings.size() > 0) {
			return quotationWarnings.stream().anyMatch(p -> p != null && p.isProceed());
		}
		return false;
	}

	public void setAnyOneProceed(boolean anyOneProceed) {
		this.anyOneProceed = anyOneProceed;
	}

	public List<String> getExceedRecipientList() {
		return exceedRecipientList;
	}

	public void setExceedRecipientList(List<String> exceedRecipientList) {
		this.exceedRecipientList = exceedRecipientList;
	}

	public String getFailMessage() {
		return failMessage;
	}

	public void setFailMessage(String failMessage) {
		this.failMessage = failMessage;
	}

	public Map<String, String> getFilterMap() {
		return filterMap;
	}

	public void setFilterMap(Map<String, String> filterMap) {
		this.filterMap = filterMap;
	}

	public int getFirstRowPosition() {
		return firstRowPosition;
	}

	public void setFirstRowPosition(int firstRowPosition) {
		this.firstRowPosition = firstRowPosition;
	}

	public String formatNumber(String origiStr) {
		String returnStr = null;
		if (origiStr != null) {
			// DecimalFormat
			// formatter=(DecimalFormat)DecimalFormat.getNumberInstance(resourceMB.getResourceLocale());
			DecimalFormat formatter = new DecimalFormat("###,###,###,###");
			if (QuoteUtil.isNumber(origiStr)) {
				returnStr = formatter.format(Integer.valueOf(origiStr));
			}
		}
		return returnStr;
	}

	public List<SelectItem> getDesignInCatList() {
		return designInCatList;
	}

	public void setDesignInCatList(List<SelectItem> designInCatList) {
		this.designInCatList = designInCatList;
	}

	public List<PricerInfo> getPricerInfosInMaterialPopup() {
		return pricerInfosInMaterialPopup;
	}

	public void setPricerInfosInMaterialPopup(List<PricerInfo> pricerInfosInMaterialPopup) {
		this.pricerInfosInMaterialPopup = pricerInfosInMaterialPopup;
	}

	public List<PricerInfo> getSelectedPricerInfosInMaterialPopup() {
		return selectedPricerInfosInMaterialPopup;
	}

	public void setSelectedPricerInfosInMaterialPopup(List<PricerInfo> selectedPricerInfosInMaterialPopup) {
		this.selectedPricerInfosInMaterialPopup = selectedPricerInfosInMaterialPopup;
	}

	public List<PricerInfo> getPricerInfosInPricerPopup() {
		return pricerInfosInPricerPopup;
	}

	public void setPricerInfosInPricerPopup(List<PricerInfo> pricerInfosInPricerPopup) {
		this.pricerInfosInPricerPopup = pricerInfosInPricerPopup;
	}

	public PricerInfo getSelectedPricerInfoInPricerPopup() {
		return selectedPricerInfoInPricerPopup;
	}

	public void setSelectedPricerInfoInPricerPopup(PricerInfo selectedPricerInfoInPricerPopup) {
		this.selectedPricerInfoInPricerPopup = selectedPricerInfoInPricerPopup;
	}

	public void selectPricer() {
		if (selectedPricerInfoInPricerPopup != null) {
			WorkingPlatformItemVO itemVo = seletedItem;
			QuoteItem item = seletedItem.getQuoteItem();
			quoteSB.selectPricer(item, selectedPricerInfoInPricerPopup.getPricerId());
			seletedItem.setQuoteItem(item, bizUnit);
			clearSalesCostFields(itemVo);
			this.workingPlatformItems.cellChangeListenerWithoutUpdateUI(String.valueOf(item.getId()));
		}
	}

	public WorkingPlatformItemVO getSeletedItem() {
		return seletedItem;
	}

	public void setSeletedItem(WorkingPlatformItemVO seletedItem) {
		this.seletedItem = seletedItem;
	}

	public void updateEndCustomerCode() {
		selectedQuoteItem.setEndCustomerCodeStr(null);
	}

	public void updateEndCustomerName() {
		String endCustomerCodeStr = selectedQuoteItem.getEndCustomerCodeStr();
		if (endCustomerCodeStr != null && endCustomerCodeStr.length() > 0) {
			Customer customer = customerSB.findByPK(endCustomerCodeStr);

			if (customer != null) {
				boolean deleteCustomer = false;
				if (customer.getDeleteFlag() != null && customer.getDeleteFlag().booleanValue()) {
					deleteCustomer = true;
					FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR,
							ResourceMB.getText("wq.message.deltdCust") + " :",
							ResourceMB.getText("wq.label.endCust") + " : " + selectedQuoteItem.getEndCustomerNameStr()
									+ "(" + selectedQuoteItem.getEndCustomerCodeStr() + ")");
					FacesContext.getCurrentInstance().addMessage("rfq_detail_workingPlatformGrowl", msg);
				} else {
					deleteCustomer = false;
				}
				selectedQuoteItem.setEndCustomerNameStr(null);
				selectedQuoteItem.setEndCustomerCodeStr(null);

				selectedQuoteItem.setEndCustomerCodeStr(customer.getCustomerNumber());
				selectedQuoteItem.setEndCustomerNameStr(getCustomerFullName(customer));

			} else {
				LOGGER.log(Level.INFO, "updateEndCustomerName invalid customer:");
				FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR,
						ResourceMB.getText("wq.label.invalidCust") + " :",
						ResourceMB.getText("wq.label.endCust") + " : " + selectedQuoteItem.getEndCustomerCodeStr());
				FacesContext.getCurrentInstance().addMessage("workingPlatformRfqGrowl", msg);
			}

		}
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

	public int getSearchCustomersCount() {
		return searchCustomersCount;
	}

	public void setSearchCustomersCount(int searchCustomersCount) {
		this.searchCustomersCount = searchCustomersCount;
	}

	public String getEndCustomerTypeSearch() {
		return endCustomerTypeSearch;
	}

	public void setEndCustomerTypeSearch(String endCustomerTypeSearch) {
		this.endCustomerTypeSearch = endCustomerTypeSearch;
	}

	public SelectItem[] getCustomerTypeSearchSelectList() {
		return customerTypeSearchSelectList;
	}

	public void setCustomerTypeSearchSelectList(SelectItem[] customerTypeSearchSelectList) {
		this.customerTypeSearchSelectList = customerTypeSearchSelectList;
	}

	public SelectItem[] getEndCustomerSearchRegionList() {
		return endCustomerSearchRegionList;
	}

	public void setEndCustomerSearchRegionList(SelectItem[] endCustomerSearchRegionList) {
		this.endCustomerSearchRegionList = endCustomerSearchRegionList;
	}

	public String getCustomerNameSearch() {
		return customerNameSearch;
	}

	public void setCustomerNameSearch(String customerNameSearch) {
		this.customerNameSearch = customerNameSearch;
	}

	public String getEndCustomerSearchRegion() {
		return endCustomerSearchRegion;
	}

	public void setEndCustomerSearchRegion(String endCustomerSearchRegion) {
		this.endCustomerSearchRegion = endCustomerSearchRegion;
	}

	public String getEndCustomerSearchType() {
		return endCustomerSearchType;
	}

	public void setEndCustomerSearchType(String endCustomerSearchType) {
		this.endCustomerSearchType = endCustomerSearchType;
	}

	public String getRestrictedCustomerMsg() {
		return restrictedCustomerMsg;
	}

	public void setRestrictedCustomerMsg(String restrictedCustomerMsg) {
		this.restrictedCustomerMsg = restrictedCustomerMsg;
	}

	public List<RestrictedCustomerMapping> getRestrictedCustList() {
		return restrictedCustList;
	}

	public void setRestrictedCustList(List<RestrictedCustomerMapping> restrictedCustList) {
		this.restrictedCustList = restrictedCustList;
	}

	public void updateCustomer() {
		// logger.log(Level.INFO, "PERFORMANCE START - updateCustomer()");
		// RequestContext requestContext = RequestContext.getCurrentInstance();
		if (this.selectedSearchCustomer != null) {
			String selectedCustomerNumber = this.selectedSearchCustomer.getCustomerNumber();
			String selectedCustomerFullName = getCustomerFullName(this.selectedSearchCustomer);
			this.selectedQuoteItem.setEndCustomerCodeStr(selectedCustomerNumber);
			this.selectedQuoteItem.setEndCustomerNameStr(selectedCustomerFullName);
			this.selectedQuoteItem.getQuoteItem().setEndCustomer(selectedSearchCustomer);
			this.selectedQuoteItem.getQuoteItem().setEndCustomerName(selectedSearchCustomer.getCustomerFullName());
			this.workingPlatformItems.cellChangeListener(String.valueOf(this.selectedQuoteItem.getQuoteItem().getId()));
			RequestContext context = RequestContext.getCurrentInstance();
			context.addCallbackParam("selected", "1");
		} else {

			RequestContext context = RequestContext.getCurrentInstance();
			context.addCallbackParam("selected", "0");

			FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "",
					ResourceMB.getText("wq.label.selectOneCustmer") + ".");
			FacesContext.getCurrentInstance().addMessage("CustomerDialogGrowl", msg);

		}
		// logger.log(Level.INFO, "PERFORMANCE End - updateCustomer()");
	}

	public void endCustomerSearchTypeChange() {
		if ("1".equalsIgnoreCase(endCustomerSearchType)) {
			changeToEndCustomerCodeSearch(null);
		} else if ("2".equalsIgnoreCase(endCustomerSearchType)) {
			searchCustomers = new ArrayList<Customer>();
			selectedSearchCustomers = new ArrayList<Customer>();
			searchCustomersCount = 0;
			setEndCustomerSearchRegion("");
		}

	}

	// public void endCustomerSearchRegionChange()
	// {
	// //logger.log(Level.INFO, "PERFORMANCE START -
	// endCustomerSearchRegionChange()");
	// searchCustomers = new ArrayList<Customer>();
	// selectedSearchCustomers = new ArrayList<Customer>();
	// searchCustomersCount=0;
	// setEndCustomerSearchRegion("");
	// setCustomerNameSearch("");
	//
	// if(!StringUtils.isEmpty(endCustomerSearchRegion))
	// {
	// //logger.log(Level.INFO, "PERFORMANCE START -
	// endCustomerSearchRegionChange() endCustomerSearchRegion is not empty");
	// List<String> accountGroup = new ArrayList<String>();
	// accountGroup.add(QuoteSBConstant.ACCOUNT_GROUP_ENDCUSTOMER);
	//
	// BizUnit searchBizUnit = new BizUnit();
	// searchBizUnit.setName(endCustomerSearchRegion);
	// searchCustomers = customerSB.findCustomerByRegion(
	// accountGroup, searchBizUnit);
	// if (searchCustomers != null)
	// {
	// //logger.log(Level.INFO, "PERFORMANCE START -
	// endCustomerSearchRegionChange() searchCustomers is not empty");
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
	// else
	// {
	// searchCustomers = new ArrayList<Customer>();
	// searchCustomersCount=0;
	// }
	// //logger.log(Level.INFO, "PERFORMANCE end -
	// endCustomerSearchRegionChange()");
	// }

	public void resetCustomerSearch() {
		searchCustomersCount = 0;
		searchCustomers = new ArrayList<Customer>();
		customerNameSearch = null;
		setEndCustomerSearchType("1");
		setEndCustomerSearchRegion("");

		// logger.log(Level.INFO, "PERFORMANCE END - resetCustomerSearch()");
	}

	public void clearCustomerChangeFlag() {
		resetCustomerSearch();
	}

	public void changeToEndCustomerCodeSearch(ActionEvent event) {
		clearCustomerChangeFlag();
		if (event != null) {
			WorkingPlatformItemVO item = (WorkingPlatformItemVO) event.getComponent().getAttributes()
					.get("targetQuoteItem");
			Object targetRowIndex = event.getComponent().getAttributes().get("targetRowIndex");
			if (targetRowIndex != null) {
				rowIndex = (Integer) targetRowIndex;
			}
			this.selectedQuoteItem = item;

			if (item.getQuoteItem().getSoldToCustomer() != null
					&& item.getQuoteItem().getSoldToCustomer().getCustomerNumber() != null
					&& !QuoteUtil.isEmpty(item.getQuoteItem().getSoldToCustomer().getCustomerNumber())
					&& item.getQuoteItem().getQuote() != null) {
				try {
					// Updated by tonmy on 16 Oct 2013 , for cross regions .
					// salesBizUnit instead of bizUnit
					List<CustomerPartner> customerPartners = customerSB.findEndCustomerBySoldToCode(
							item.getQuoteItem().getSoldToCustomer().getCustomerNumber(),
							item.getQuoteItem().getQuote().getBizUnit());
					searchCustomers = new ArrayList<Customer>();
					for (CustomerPartner cp : customerPartners) {
						Customer endCustomer = customerSB.findByPK(cp.getPartnerCustomerCode());
						if (endCustomer != null)
							searchCustomers.add(endCustomer);
					}
				} catch (Exception ex) {
					LOGGER.log(Level.SEVERE, "Error occured while End Customer search for Customer no.:"
							+ item.getQuoteItem().getSoldToCustomer().getCustomerNumber() + ", " + "Customer name: "
							+ item.getQuoteItem().getSoldToCustomer().getCustomerFullName() + ", Reason for failure: "
							+ MessageFormatorUtil.getParameterizedStringFromException(ex), ex);
				}

				if (searchCustomers != null) {
					List<String> customerNumbers = new ArrayList<String>();
					for (Iterator<Customer> itr = searchCustomers.iterator(); itr.hasNext();) {
						Customer customer = itr.next();
						if (customer == null || customer.getCustomerNumber() == null)
							continue;
						if (customerNumbers.contains(customer.getCustomerNumber())) {
							itr.remove();
						} else {
							customerNumbers.add(customer.getCustomerNumber());
						}
					}

					if (searchCustomers != null) {
						searchCustomersCount = searchCustomers.size();
					} else {
						searchCustomersCount = 0;
					}
				}
			}

			setEndCustomerSearchType("1");
		}

	}

	public void searchCustomers() {

		// logger.log(Level.INFO, "PERFORMANCE START - searchCustomers()");
		setEndCustomerSearchType("1");

		// Handle search key word < 3
		if (customerNameSearch != null && customerNameSearch.length() < 3) {
			FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "",
					ResourceMB.getText("wq.message.cutmrLenError"));
			FacesContext.getCurrentInstance().addMessage("CustomerDialogGrowl", msg);
			return;
		}

		searchCustomers = findCustomers(customerNameSearch);
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

		// logger.log(Level.INFO, "PERFORMANCE END - searchCustomers()");
	}

	public List<Customer> findCustomers(String customerName) {

		LOGGER.log(Level.INFO, "PERFORMANCE START - findCustomers()");

		if (QuoteUtil.isEmpty(customerName)) {
			String errorMessage = ResourceMB.getText("wq.label.custmrName");
			FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR,
					QuoteConstant.MESSAGE_MISSING_MANDATORY_FIELD, errorMessage);
			FacesContext.getCurrentInstance().addMessage("endCustomerDialogGrowl", msg);
			return null;
		}

		List<String> accountGroup = new ArrayList<String>();
		accountGroup.add(QuoteSBConstant.ACCOUNT_GROUP_ENDCUSTOMER);
		List<Customer> customers = null;

		// Updated by tonmy on 16 Oct 2013 , for cross regions . salesBizUnit
		// instead of bizUnit
		if (customerName.startsWith("*"))
			customers = customerSB.findCustomerByCustomerNameEndWith(customerName.replaceAll("\\*", ""),
					QuoteSBConstant.ALL, accountGroup, bizUnit);
		else if (customerName.endsWith("*"))
			customers = customerSB.findCustomerByCustomerNameStartWith(customerName.replaceAll("\\*", ""),
					QuoteSBConstant.ALL, accountGroup, bizUnit);
		else
			customers = customerSB.findCustomerByCustomerNameContain(customerName.replaceAll("\\*", ""),
					QuoteSBConstant.ALL, accountGroup, bizUnit);

		// logger.log(Level.INFO, "PERFORMANCE END - findCustomers()");
		return customers;
	}

	public boolean hasMatchedRetrictedCustomer(QuoteItem quoteItem) {
		

		RestrictedCustomerMappingParameter rcp = new RestrictedCustomerMappingParameter();
		rcp.setMfr(quoteItem.getRequestedMfr() == null ? null : quoteItem.getRequestedMfr().getName());
		rcp.setSoldToCustomerNumber(quoteItem.getSoldToCustomer() == null ? null : quoteItem.getSoldToCustomer().getCustomerNumber());
		if (quoteItem.getQuote() != null && quoteItem.getQuote().getBizUnit() != null) {
			rcp.setBizUnit(quoteItem.getQuote().getBizUnit().getName());
		}
		rcp.setEndCustomerCode(quoteItem.getEndCustomer() == null ? null : quoteItem.getEndCustomer().getCustomerNumber());
		rcp.setRequiredPartNumber(quoteItem.getRequestedPartNumber());
		rcp.setProductGroup1Name(quoteItem.getProductGroup1() == null ? null : quoteItem.getProductGroup1().getName());
		rcp.setProductGroup2Name(quoteItem.getProductGroup2() == null ? null : quoteItem.getProductGroup2().getName());
		rcp.setProductGroup3Name(quoteItem.getProductGroup3());
		rcp.setProductGroup4Name(quoteItem.getProductGroup4());
		
		
		return restrictedCustomerSB.hasMatchedRetrictedCustomer(rcp, restrictedCustList);
		/*// logger.info("hasMatchedRetrictedCustomer cost
		// indicator:"+quoteItem.getCostIndicator());
		boolean returnB = false;
		for (RestrictedCustomerMapping rcm : restrictedCustList) {
			// logger.info("getMandatoryKey(rfqItem):"+getMandatoryKey(quoteItem));
			// logger.info("rcm.getMandatoryKey():"+rcm.getMandatoryKey());
			if (getMandatoryKey(quoteItem).equalsIgnoreCase(rcm.getMandatoryKey())) {

				String prodGroup1 = null, prodGroup2 = null, prodGroup3 = null, prodGroup4 = null;
				if (quoteItem.getProductGroup1() != null)
					prodGroup1 = quoteItem.getProductGroup1().getName();
				if (quoteItem.getProductGroup2() != null)
					prodGroup2 = quoteItem.getProductGroup2().getName();
				prodGroup3 = quoteItem.getProductGroup3();
				prodGroup4 = quoteItem.getProductGroup4();

				// logger.info("hasMatchedRetrictedCustomer
				// group1:"+prodGroup1);
				// logger.info("hasMatchedRetrictedCustomer
				// group2:"+prodGroup2);
				// logger.info("hasMatchedRetrictedCustomer
				// group3:"+prodGroup3);
				// logger.info("hasMatchedRetrictedCustomer
				// group4:"+prodGroup4);
				if (!StringUtils.isEmpty(rcm.getPartNumber())
						&& !rcm.getPartNumber().equalsIgnoreCase(quoteItem.getRequestedPartNumber())) {
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
				// logger.info("hasMatchedRetrictedCustomer return true");
				return true;
			}
		}
		return returnB;*/
	}

	public void getRestrictedCustomerList() {
		List<RestrictedCustomerMappingSearchCriteria> restrictedCustomerCriteriaList = new ArrayList<RestrictedCustomerMappingSearchCriteria>();
		if (getCacheSelectionDataList() != null && getCacheSelectionDataList().size() > 0) {
			for (WorkingPlatformItemVO rfqItemVo : getCacheSelectionDataList()) {
				RestrictedCustomerMappingSearchCriteria rcc = new RestrictedCustomerMappingSearchCriteria();
				rcc.setBizUnit(rfqItemVo.getQuoteItem().getQuote().getBizUnit().getName());
				rcc.setMfrKeyword(rfqItemVo.getQuoteItem().getRequestedMfr().getName());
				//rcc.setSoldToCodeKeyword(rfqItemVo.getQuoteItem().getSoldToCustomer().getCustomerNumber());
				restrictedCustomerCriteriaList.add(rcc);
			}
			restrictedCustList = restrictedCustomerSB.findRestrictedCust(restrictedCustomerCriteriaList);
		} else {
			restrictedCustList = new ArrayList<RestrictedCustomerMapping>();
		}
	}

	public String getMandatoryKey(QuoteItem quoteItem) {
		StringBuffer sb = new StringBuffer();
		sb.append(quoteItem.getRequestedMfr().getName()).append("|").append(quoteItem.getCostIndicator()).append("|")
				.append(quoteItem.getSoldToCustomer().getCustomerNumber());
		return sb.toString();
	}

	private List<QuotationEmail> getQuotationEmails(List<ProceedQuotationVO> proceedQuotationVOS, User currentUser,
			List<String> successUpdQuoteNumbers) {

		List<QuotationEmail> emails = new ArrayList<QuotationEmail>();
		if (null != successUpdQuoteNumbers) {
			for (ProceedQuotationVO pqVO : proceedQuotationVOS) {
				String quoteItemIds = "";
				String toEmails = "";
				String ccEmails = "";
				String remark = "";
				QuotationEmail qEmail = new QuotationEmail();
				if (pqVO.getEmailsCc() != null && pqVO.getEmailsCc().size() > 0) {
					for (String email : pqVO.getEmailsCc()) {
						ccEmails += email + ",";
					}
				}
				qEmail.setCcEmployeeId(ccEmails);
				QuotationEmailPK pk = new QuotationEmailPK();
				pk.setFormId(pqVO.getFormId());
				pk.setFromEmployeeId(currentUser.getEmployeeId());
				pk.setUpdateDate(new Date());
				qEmail.setId(pk);

				List<Long> highLightedRecordList = (List<Long>) pqVO.getHighlightedRecords();
				// for fix ticket INC0272129 only send email to that success
				// ----------start
				List<QuoteItem> quoteItems = pqVO.getQuoteItems();
				List<Long> successItemIds = new ArrayList<Long>();
				for (QuoteItem item : quoteItems) {
					if (successUpdQuoteNumbers.contains(item.getQuoteNumber())) { // only
																					// the
																					// success
																					// update
																					// quote
																					// item
																					// generate
																					// email.
						successItemIds.add(item.getId());
					}
				}
				// -------end-------
				if (null != successItemIds && successItemIds.size() > 0) {
					if (highLightedRecordList != null) {
						for (Long id : highLightedRecordList) {
							if (successItemIds.contains(id)) { // only the
																// success
																// update record
																// send email to
																// user.
								quoteItemIds += id + ",";
							}
						}
					}
					qEmail.setQuoteItemId(quoteItemIds);
					if (pqVO.getMessage() != null) {
						remark += pqVO.getMessage().replaceAll("'", " ");
					} else {
						remark += pqVO.getMessage();
					}
					qEmail.setRemark(remark);
					qEmail.setStatus(true);
					if (pqVO.getEmailsTo() != null && pqVO.getEmailsTo().size() > 0) {
						for (String email : pqVO.getEmailsTo()) {
							toEmails += email + ",";
						}
					}
					String currentEmployeeId = user.getEmployeeId();
					if (currentEmployeeId != null && toEmails.indexOf(currentEmployeeId) == -1) {
						toEmails += currentEmployeeId + ",";
					}
					qEmail.setToEmployeeId(toEmails);
					qEmail.setToEmployeeName(pqVO.getEmployeeName());

					emails.add(qEmail);
				} // null!=successItemIds && successItemIds.size()>0
			} // for
		} // null!=successUpdQuoteNumbers
		return emails;
	}

	public void updateSapPartNumber(AjaxBehaviorEvent event) {
		WorkingPlatformItemVO item = (WorkingPlatformItemVO) event.getComponent().getAttributes()
				.get("targetQuoteItem");
		// item.getQuoteItem().setSapPartNumber(item.getSapPartNumberStr());
		// fixed by DamonChen@20180409
		this.workingPlatformItems.cellChangeListenerWithoutUpdateUI(String.valueOf(item.getQuoteItem().getId()));
	}

	/*
	 * public Set<String> getFuturePricerMap(List<QuoteItem> quoteItems, BizUnit
	 * bizUnit) { Set<String> returnSet = new HashSet<String>();
	 * List<FuturePriceCriteria> futurePriceCriterias = new
	 * ArrayList<FuturePriceCriteria>(); if(quoteItems!=null &&
	 * quoteItems.size()>0) { for(QuoteItem qi : quoteItems) {
	 * if(qi.getRequestedMfr()!=null && qi.getQuotedPartNumber()!=null) {
	 * FuturePriceCriteria fpc = new FuturePriceCriteria();
	 * fpc.setBizUnit(bizUnit.getName());
	 * fpc.setMfr(qi.getRequestedMfr().getName());
	 * fpc.setPartNumber(qi.getQuotedPartNumber());
	 * futurePriceCriterias.add(fpc); } } }
	 * 
	 * if(futurePriceCriterias!=null && futurePriceCriterias.size()>0) {
	 * returnSet = pricerTypeMappingSB.getFuturePriceMap(futurePriceCriterias);
	 * } return returnSet; }
	 */

	public void searchInMaterialPopup(ActionEvent event) {
		if (validationSearchFullMfrPartNumber == null
				|| (validationSearchFullMfrPartNumber != null && validationSearchFullMfrPartNumber.length() < 3)) {
			FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR,
					ResourceMB.getText("wq.msg.invalidFormat") + " :", ResourceMB.getText("wq.message.minPN"));
			FacesContext.getCurrentInstance().addMessage("workingPlatformRfqGrowl", msg);
		} else {
			if (validationSearchFullMfrPartNumber != null)
				validationSearchFullMfrPartNumber = validationSearchFullMfrPartNumber.trim();
			if (validationSearchMfr != null)
				validationSearchMfr = validationSearchMfr.trim();

			WorkingPlatformItemVO item = this.selectedWorkingPlatformItem;
			PricerInfo pricerInfo = item.createPricerInfo();
			pricerInfo.setMfr(validationSearchMfr);
			pricerInfo.setFullMfrPartNumber(validationSearchFullMfrPartNumber);

			pricerInfosInMaterialPopup = materialSB.searchForMaterialPopup(pricerInfo);

			searchPartsCount = pricerInfosInMaterialPopup.size();
		}
	}

	public void fillInQBPStr(List<WorkingPlatformItemVO> workingPlatformItems, BizUnit bizUnit) {
		if (workingPlatformItems != null && workingPlatformItems.size() > 0) {
			for (WorkingPlatformItemVO vo : workingPlatformItems) {
				vo.setQbpStr(getQBPStr(vo, bizUnit));
			}
		}
	}

	public String getQBPStr(WorkingPlatformItemVO vo, BizUnit bizUnit) {
		if (vo.getQuoteItem().getMaterialTypeId() == null
				|| vo.getQuoteItem().getMaterialTypeId().equalsIgnoreCase(QuoteSBConstant.MATERIAL_TYPE_NORMAL)) {
			return "";
		}

		StringBuffer sb = new StringBuffer();
		if (vo.getQuoteItem().getQuotedMaterial() != null) {
			// PROGRM PRICER ENHANCEMENT
			// ProgramMaterial pm =
			// vo.getQuoteItem().getQuotedMaterial().getProgramMaterial();
			// ProgramMaterial pm =
			// vo.getQuoteItem().getQuotedMaterial().getOneValidProgMatByBizUint(bizUnit.getName());
			String costIndicator = vo.getCostIndicatorStr();

			Material material = vo.getQuoteItem().getQuotedMaterial();
			material = materialSB.findMaterialByPK(material.getId());
			ProgramPricer pm = materialSB.getOneValidProgMatByBizUint(material, bizUnit.getName());
			if (pm != null) {
				List<com.avnet.emasia.webquote.vo.Oqmsp> opmspList = ProgRfqSubmitHelper.getOpmspList(pm);
				if (opmspList != null && opmspList.size() > 0) {
					for (com.avnet.emasia.webquote.vo.Oqmsp o : opmspList) {
						sb.append(o.getOq()).append("   ").append(o.getMsp()).append("\r\n");
					}
				}
			}
		}
		return sb.toString();
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

	public void updateFutureFlagOfUI(WorkingPlatformItemVO item) {
		// boolean hasFP = pricerTypeMappingSB.isHasFuturePrice(mfr, partNumber,
		// bizUnit.getName());
		item.setHasFuturePricer(quoteSB.calPricerNumber(item.getQuoteItem())[2] > 0 ? true : false);
	}

	private void setLastUpdateFields(List<QuoteItem> quoteItems) {
		if (quoteItems == null || quoteItems.isEmpty()) {
			return;
		}
		Date current = QuoteUtil.getCurrentTime();
		for (QuoteItem quoteItem : quoteItems) {
			quoteItem.setLastUpdatedOn(current);
			quoteItem.setLastQcUpdatedOn(current);
			quoteItem.setLastUpdatedQc(user.getEmployeeId());
			quoteItem.setLastUpdatedQcName(user.getName());
			quoteItem.setLastUpdatedBy(user.getEmployeeId());
			quoteItem.setLastUpdatedName(user.getName());
		}
		return;
	}

	/**
	 * fix ticket INC0272129 Remove current code on re-set version number in
	 * QuoteTransactionSB. Commit the quote_item one by one instead of in a
	 * batch. If there is concurrency exception detected, proceed on the next
	 * quote_item until all of the quote_item in list is processed. Record which
	 * quote_item is updated successfully, which is failed. Notify user on
	 * quote_item failed to update (using growl). For the quote_item updated
	 * successfully, proceed on next action like create sap quote, send email
	 * etc. If some quote_item are updated successfully, some are failed in one
	 * batch. Notify user on the failed one and proceed to on successful one.
	 * When user click the Save icon, refresh the working platform after saving
	 * (no matter the saving is successful or not)
	 * 
	 * @param quoteItems
	 * @return successful quote
	 */
	public List<String> updateQuoteItem(List<QuoteItem> quoteItems, ActionEnum action) {
		Map<String, String> updateInfo = quoteTransactionSB.updateQuoteItem(quoteItems, action);
		String lockError = updateInfo.get("lockEx");
		String commonError = updateInfo.get("commonEx");
		String successQuoteNumber = updateInfo.get("success");
		if (null != lockError && !lockError.isEmpty()) {
			String lockInfo = "<" + lockError.substring(0, lockError.length() - 1) + "> "
					+ ResourceMB.getText("wq.label.updatedByAnotherUser") + ".";
			FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR,
					ResourceMB.getText("wq.label.updateQuoteInfo"), lockInfo);
			FacesContext.getCurrentInstance().addMessage("wpInfo", msg);
		}
		if (null != commonError && !commonError.isEmpty()) {
			String msgInfo = "<" + commonError.substring(0, commonError.length() - 1) + "> "
					+ ResourceMB.getText("wq.label.notSavedSuccessfullyDBError") + ".";

			FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR,
					ResourceMB.getText("wq.label.updateQuoteInfo"), msgInfo);
			FacesContext.getCurrentInstance().addMessage("wpInfo", msg);
		}
		if (null != successQuoteNumber && !successQuoteNumber.isEmpty() && successQuoteNumber.length() > 0) {

			List<String> successUpdateItems = new ArrayList<String>();
			String[] updatedItems = successQuoteNumber.split(",");
			for (int i = 0; i < updatedItems.length; i++) {
				successUpdateItems.add(updatedItems[i]);
			}
			return successUpdateItems;
		} else {
			return null;
		}
	}

	public List<SelectItem> getDesignRegionList() {
		return designRegionList;
	}

	public List<WorkingPlatformItemVO> getSelectedItToBmtItems() {
		if (selectedItToBmtItems == null) {
			selectedItToBmtItems = new ArrayList<>();
		}
		return selectedItToBmtItems;
	}

	public void setSelectedItToBmtItems(List<WorkingPlatformItemVO> selectedItToBmtItems) {
		this.selectedItToBmtItems = selectedItToBmtItems;
	}

	public UpdateSprVO getUpdateSprVO() {
		return updateSprVO;
	}

	private transient String MSG_TITLE_UPDATE_SPR_FIELDS = ResourceMB.getText("wq.label.updateSPRFields");

	public void sendOfflineReport() {
		Log.info("call Workingplatform download Offline Report");
		// int pageSize = 10000;
		User tempUser = UserInfo.getUser();
		criteria.setAction(QuoteSBConstant.WORKINGPLATFORM_ACTION_OFFLINE);
		OfflineReportParam param = new OfflineReportParam();
		param.setCriteriaBeanValue(criteria);
		param.setReportName(QuoteConstant.QC_WORKINGPLATFORM);
		param.setEmployeeId(String.valueOf(tempUser.getEmployeeId()));
		param.setRemoteEjbClass(RemoteEjbClassEnum.WORKINGPLATFORM_REMOTE_EJB.classSimpleName());
		// added for download offline function need the records on the WP page
		// only, by Damonchen@20180312
		DataTable dataTable = (DataTable) FacesContext.getCurrentInstance().getViewRoot()
				.findComponent("form_pendinglist:datatable_pendinglist");
		Map<String, Object> filtersMap = dataTable.getFilters();
		param.setFiltersMap(filtersMap);
		// end
		offlineReprtSB.sendOffLineReportRemote(param);
		FacesContext.getCurrentInstance().addMessage("wpInfo",
				new FacesMessage(FacesMessage.SEVERITY_INFO, "",
						ResourceMB.getText("wq.message.requestSubmitted") + ". <br />"
								+ ResourceMB.getText("wq.message.reportSentYourEmail") + ". <br />"
								+ ResourceMB.getText("wq.message.withInOneH") + "."));
		return;
	}

	public void exportSelected() {
		LOGGER.log(Level.INFO, "Run function exportSelected begin....");
		try {
			List<WorkingPlatformItemVO> selectionList = new ArrayList<WorkingPlatformItemVO>();
			// NEC Pagination changes: get the selected data from cache
			selectionList.addAll(workingPlatformItems.getCacheSelectedItems());
			if (selectionList.size() == 0) {
				FacesContext.getCurrentInstance().addMessage("wpInfo", new FacesMessage(FacesMessage.SEVERITY_WARN,
						ResourceMB.getText("wq.message.noRecordSelected") + " !", ""));
				return;
			}
			if (selectionList.size() > 0) {
				for (WorkingPlatformItemVO vo : selectionList) {
					// String quantityBreak =
					// getQuantityBreakExcelCellText(vo.getQuoteItem());
					// vo.setQuantityBreakStr(quantityBreak);
					vo.setOrderQties(programMaterialSB.getOrderQties(vo.getQuoteItem()));
				}
			}

			String templatePath = sysConfigSB.getProperyValue(ReportConstant.TEMPLATE_PATH);
			// templatePath = "C:/temp";
			// TODO
			try {
				String address = InetAddress.getLocalHost().getHostName().toString();
				if ("cis2115vmts".equalsIgnoreCase(address)) {
					templatePath = "C:\\david\\sharefolder\\tempd";
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			LOGGER.log(Level.INFO, "templatePath is:" + templatePath);
			ExcelReport excelReport = excelReportSB.getExcelReportByReportName(QuoteConstant.QC_WORKINGPLATFORM);
			LOGGER.log(Level.INFO, "excelReport is:" + excelReport.toString());
			List newList = ReportConvertor.convertToWorkingPlatFormBean(selectionList);
			DownloadUtil.outputExcelFile(excelReport, templatePath, newList, FacesContext.getCurrentInstance());
			LOGGER.log(Level.INFO, "Run function exportSelected end....");
		} catch (Exception e) {
			LOGGER.log(Level.SEVERE, "Download Selected Quote Error,Report Name:" + QuoteConstant.QC_WORKINGPLATFORM,
					e);
			// Log.info("Download Selected Quote Error,Report Name:" +
			// QuoteConstant.QC_WORKINGPLATFORM);
		}

	}

	private String getQuantityBreakExcelCellText(QuoteItem item) {
		StringBuffer sb = new StringBuffer();
		if (item != null) {
			Material material = item.getQuotedMaterial();
			if (material != null) {
				material = materialSB.findMaterialByPK(material.getId());
				String costIndicator = item.getCostIndicator();
				ProgramPricer pm = (ProgramPricer) material.getValidProgPricerByBizUintAndCostIndicator(
						item.getQuote().getBizUnit().getName(), costIndicator);
				if (pm != null) {
					List<Oqmsp> opmspList = ProgOqmspUtil.getOpmspList(pm);
					if (opmspList != null && opmspList.size() > 0) {
						for (Oqmsp o : opmspList) {
							sb.append(o.getOq()).append("   ").append(o.getMsp()).append("\r\n");
						}
					}
				}
			}
		}
		return sb.toString();
	}

	@EJB
	private DesignLocationSB designLocationSB;

	public void initSprItems() {
		// Pagination : changes done for WQ-1014
		String MSG_TITLE_UPDATE_SPR_FIELDS = ResourceMB.getText("wq.label.updateSPRFields");
		// Pagination : changes ends
		saveQuotation();
		if (getCacheSelectionDataList() != null && !getCacheSelectionDataList().isEmpty()) {
			if (getCacheSelectionDataList().size() != 1) {// defect 230, new
															// request for only
															// allow sleect one
															// record
				FacesUtil.showWarnMessage(MSG_TITLE_UPDATE_SPR_FIELDS,
						ResourceMB.getText("wq.label.selectUpdateSPRFields") + ".");
				return;
			}

			long tempId = getCacheSelectionDataList().get(0).getQuoteItem().getQuote().getId();
			String designRegion = getCacheSelectionDataList().get(0).getQuoteItem().getDesignRegion();

			for (WorkingPlatformItemVO wp : getCacheSelectionDataList()) {
				if (tempId != wp.getQuoteItem().getQuote().getId()) {
					FacesUtil.showWarnMessage(MSG_TITLE_UPDATE_SPR_FIELDS,
							ResourceMB.getText("wq.label.selectQuoteWithSameFormNumber") + ".");
					updateSprVO = null;
					return;
				}
				if (!org.apache.commons.lang.StringUtils.equals(designRegion, wp.getQuoteItem().getDesignRegion())) {
					FacesUtil.showWarnMessage(MSG_TITLE_UPDATE_SPR_FIELDS,
							ResourceMB.getText("wq.label.selectSameDesignRegion") + ".");
					updateSprVO = null;
					return;
				}
				// bmt: 5.4.2.3
				if (!QuoteSBConstant.QUOTE_STAGE_PENDING.equals(wp.getQuoteItem().getStage())) {
					FacesUtil.showWarnMessage(MSG_TITLE_UPDATE_SPR_FIELDS,
							ResourceMB.getText("wq.label.selectPendingQuote") + ".");
					updateSprVO = null;
					return;
				}
			}
			updateSprVO = new UpdateSprVO();
			updateSprVO.setApplyType("selected");
			if (getCacheSelectionDataList().size() == 1) {
				QuoteItem qi = getCacheSelectionDataList().get(0).getQuoteItem();
				updateSprVO.setApplication(qi.getApplication());
				updateSprVO.setCompetitorInformation(qi.getCompetitorInformation());
				updateSprVO.setDesignLocation(qi.getDesignLocation());
				if (qi.getShipToCustomer() != null) {
					updateSprVO.setShipToCode(qi.getShipToCustomer().getCustomerNumber());
					updateSprVO.setShipToParty(qi.getShipToCustomer().getCustomerFullName());
				}
				updateSprVO.setProjectName(qi.getProjectName());
				updateSprVO.setEau(qi.getEau());
				updateSprVO.setMfrDr(qi.getSupplierDrNumber());
				updateSprVO.setPpSchedule(qi.getPpSchedule());
				updateSprVO.setMpSchedule(qi.getMpSchedule());
				updateSprVO.setDesignRegion(designRegion);
				updateSprVO.setFormNumber(qi.getQuote().getFormNumber());

			}

			FacesUtil.updateUI("form_pendinglist_dialog");
		} else {
			FacesUtil.showWarnMessage(MSG_TITLE_UPDATE_SPR_FIELDS, ResourceMB.getText("wq.label.selectQuote") + ".");
		}
	}

	private List<QuoteItem> getUpdateSprItems() {
		if ("selected".equals(updateSprVO.getApplyType())) {
			List<QuoteItem> qis = new ArrayList<>(getCacheSelectionDataList().size());
			if (getCacheSelectionDataList() != null) {
				for (WorkingPlatformItemVO wqi : getCacheSelectionDataList()) {
					qis.add(wqi.getQuoteItem());
				}
			}
			return setSprFields(qis, updateSprVO);
		} else {
			// TODO: cache null result
			String designRegion = getCacheSelectionDataList().get(0).getQuoteItem().getDesignRegion();
			if (org.apache.commons.lang.StringUtils.isEmpty(designRegion)) {
				designRegion = null;
			}
			List<QuoteItem> items = quoteSB.findQuoteItemByFormNumberAndStage(
					getCacheSelectionDataList().get(0).getQuoteItem().getQuote().getId(),
					QuoteSBConstant.QUOTE_STAGE_PENDING, designRegion);// TODO:
																		// other
																		// condition
			return setSprFields(items, updateSprVO);
		}
	}

	private List<QuoteItem> setSprFields(List<QuoteItem> qis, UpdateSprVO updateSprVO) {
		Customer shipTo = customerSB.findByPK(updateSprVO.getShipToCode());
		for (QuoteItem qi : qis) {
			LOGGER.log(Level.INFO, UserInfo.getUser().getEmployeeId() + " update spr on QC working platform. quote#: "
					+ qi.getQuoteNumber());
			qi.setDesignLocation(updateSprVO.getDesignLocation());
			qi.setShipToCustomer(shipTo);
			qi.setShipToCustomerName(updateSprVO.getShipToParty());// TODO:
																	// verify
			qi.setProjectName(updateSprVO.getProjectName());
			qi.setApplication(updateSprVO.getApplication());
			qi.setCompetitorInformation(updateSprVO.getCompetitorInformation());
			qi.setEau(updateSprVO.getEau());
			qi.setSupplierDrNumber(updateSprVO.getMfrDr());
			qi.setPpSchedule(updateSprVO.getPpSchedule());
			qi.setMpSchedule(updateSprVO.getMpSchedule());

			// add by Lennon.Yang(043044) 2016.05.09
			Date current = QuoteUtil.getCurrentTime();
			qi.setLastQcUpdatedOn(current);
			qi.setLastUpdatedQc(user.getEmployeeId());
			qi.setLastUpdatedQcName(user.getName());
			qi.setLastUpdatedBy(user.getEmployeeId());
			qi.setLastUpdatedName(user.getName());
			qi.setLastUpdatedOn(current);
		}
		return qis;
	}

	public void saveUpdateSprItems() {
		// Pagination : changes done for WQ-1014
		String MSG_TITLE_UPDATE_SPR_FIELDS = ResourceMB.getText("wq.label.updateSPRFields");
		// Pagination : changes ends
		try {
			if (sprChecking()) {
				List<QuoteItem> updateItems = quoteSB.updateQuoteItems(getUpdateSprItems(), ActionEnum.WP_UPDATE_SPR);
				// update datatable on page
				for (QuoteItem qi : updateItems) {
					for (WorkingPlatformItemVO vo : searchedWorkingPlatformItems) {
						if (qi.getId() == vo.getQuoteItem().getId()) {
							vo.setQuoteItem(qi, bizUnit);
							break;
						}
					}
					if (searchedWorkingPlatformItems != null) {
						for (WorkingPlatformItemVO vo : searchedWorkingPlatformItems) {
							if (qi.getId() == vo.getQuoteItem().getId()) {
								vo.setQuoteItem(qi, bizUnit);
								break;
							}
						}
					}

				}
				// FacesUtil.showInfoMessage(MSG_TITLE_UPDATE_SPR_FIELDS,
				// "Successful.");
				FacesUtil.updateUI("form_pendinglist:datatable_pendinglist", "form_pendinglist_dialog",
						"ap_rfqdetailreference");
			}
		} catch (Exception e) {
			if (e.getCause() instanceof OptimisticLockException) {
				FacesUtil.showWarnMessage(MSG_TITLE_UPDATE_SPR_FIELDS,
						ResourceMB.getText("wq.label.saveFailedClickRefreshButton") + ".");
				FacesUtil.updateUI("wpInfo");
				LOGGER.log(Level.SEVERE, "Exception in save Update Spr Items, exception message : " + e.getMessage());
			} else {
				FacesUtil.showWarnMessage(MSG_TITLE_UPDATE_SPR_FIELDS, ResourceMB.getText("wq.label.saveFailed") + ".");
				FacesUtil.updateUI("wpInfo");
				LOGGER.log(Level.SEVERE, "Error occured, Save failed for Form no.:" + updateSprVO.getFormNumber()
						+ ", Reason for failure is: " + MessageFormatorUtil.getParameterizedStringFromException(e), e);
			}
		}

	}

	private boolean sprChecking() {
		// todo: validation field data
		StringBuilder msg = new StringBuilder();
		if (!QuoteUtil.isEmpty(updateSprVO.getShipToCode())) {
			Customer shipTo = customerSB.findByPK(updateSprVO.getShipToCode());
			if (shipTo == null) {
				msg.append(ResourceMB.getText("wq.label.shipToCodeDoesntExist") + ",");
			}
		}
		if (msg.length() > 0) {
			// Pagination : changes done for WQ-1014
			FacesUtil.showWarnMessage(ResourceMB.getText("wq.label.updateSPRFields"),
					msg.append(ResourceMB.getText("wq.message.error") + " .").toString());
			// Pagination : changes done for WQ-1014
			FacesUtil.updateUI("wpInfo");
			return false;
		}
		return true;
	}

	public void setUpdateSprVO(UpdateSprVO updateSprVO) {
		this.updateSprVO = updateSprVO;
	}

	public List<String> autoCompleteProjectNameList(String key) {
		List<String> projectNames = null;
		if (QuoteUtil.isEmpty(key)) {
			projectNames = quoteSB.findProjectNameBySoldToInQuoteHistory(
					getCacheSelectionDataList().get(0).getQuoteItem().getSoldToCustomer().getCustomerNumber());
		} else {
			projectNames = quoteSB.findProjectNameInQuoteHistory(key);
		}

		// logger.log(Level.INFO,
		// "PERFORMANCE END - autoCompleteProjectNameList()");
		return projectNames;
	}

	public List<String> autoCompleteApplicationList(String key) {
		List<String> applicationNames = null;
		if (QuoteUtil.isEmpty(key)) {
			applicationNames = quoteSB.findApplicationNameBySoldToInQuoteHistory(
					getCacheSelectionDataList().get(0).getQuoteItem().getSoldToCustomer().getCustomerNumber());
		} else {
			// Bryan Start
			// List<String> applicationList =
			// ApplicationCacheManager.getApplicationList();
			List<String> applicationList = cacheUtil.getApplicationList();
			// Bryan End
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

	// ship to search start
	public void searchShipTo() {
		if (QuoteUtil.isEmpty(searchShipToName)) {
			searchShipTos = new ArrayList<Customer>();
			return;
		}
		List<String> accountGroup = new ArrayList<String>();
		accountGroup.add(QuoteSBConstant.ACCOUNT_GROUP_SOLDTO);
		accountGroup.add(QuoteSBConstant.ACCOUNT_GROUP_SHIPTO);
		accountGroup.add(QuoteSBConstant.ACCOUNT_GROUP_PROSPECTIVE_CUSTOMER);
		// TODO: try & msg
		searchShipTos = customerSB.findCustomerByCustomerNameContain(searchShipToName, null, accountGroup, bizUnit);
	}

	private String searchShipToName;
	private Customer selectedShipTo;
	private List<Customer> searchShipTos;

	public String getSearchShipToName() {
		return searchShipToName;
	}

	public void setSearchShipToName(String searchShipToName) {
		this.searchShipToName = searchShipToName;
	}

	public Customer getSelectedShipTo() {
		if (selectedShipTo == null) {
			selectedShipTo = new Customer();
		}

		return selectedShipTo;
	}

	public void setSelectedShipTo(Customer selectedShipTo) {
		this.selectedShipTo = selectedShipTo;
	}

	public List<Customer> getSearchShipTos() {
		return searchShipTos;
	}

	public void setSearchShipTos(List<Customer> searchShipTos) {
		this.searchShipTos = searchShipTos;
	}
	// ship to search end

	public WorkingPlatFormSearchCriteria getWorkingPlatFormSearchCriteria() {
		return workingPlatFormSearchCriteria;
	}

	public void setWorkingPlatFormSearchCriteria(WorkingPlatFormSearchCriteria workingPlatFormSearchCriteria) {
		this.workingPlatFormSearchCriteria = workingPlatFormSearchCriteria;
	}

	/*
	 * public SelectItem[] getMfrSearchSelectList() { return
	 * mfrSearchSelectList; }
	 * 
	 * 
	 * public void setMfrSearchSelectList(SelectItem[] mfrSearchSelectList) {
	 * this.mfrSearchSelectList = mfrSearchSelectList; }
	 */
	/**
	 * Setter for resourceMB
	 * 
	 * @param resourceMB
	 *            ResourceMB
	 */
	public void setResourceMB(final ResourceMB resourceMB) {
		this.resourceMB = resourceMB;
	}

	// updated for keep quote.soldtoCustomerName same to customer's customer
	// full name by Damon@20170317
	public QuoteItem updateQuoteSoldToCustomerName(QuoteItem quoteItem) {
		LOGGER.log(Level.INFO, "begin if need to update quote.soldtocustomername logic");
		Quote quoteForSoldCust = quoteItem.getQuote();
		if (quoteForSoldCust != null && quoteForSoldCust.getSoldToCustomer() != null
				&& (quoteForSoldCust.getSoldToCustomerName() == null || (!quoteForSoldCust.getSoldToCustomerName()
						.equals(quoteForSoldCust.getSoldToCustomer().getCustomerFullName())))) {

			quoteForSoldCust.setSoldToCustomerName(quoteForSoldCust.getSoldToCustomer().getCustomerFullName());
			String iRt = quoteTransactionSB.updateQuoteSoldToCustomerName(quoteForSoldCust);
			LOGGER.log(Level.INFO, "need update quote.soldtocustomername to :"
					+ quoteForSoldCust.getSoldToCustomer().getCustomerFullName() + " iRt:" + iRt);
		}
		LOGGER.log(Level.INFO, "end  if need to update quote.soldtocustomername logic");

		return quoteItem;
	}

	// NEC Pagination Changes: Getter method for fetching Selected Data from
	// Cache
	public List<WorkingPlatformItemVO> getCacheSelectionDataList() {
		List<WorkingPlatformItemVO> selectionList = new ArrayList<WorkingPlatformItemVO>();
		// NEC Pagination changes: get the selected data from cache
		selectionList.addAll(workingPlatformItems.getCacheSelectedItems());
		return selectionList;
	}

	// Added by DamonChen
	public List<WorkingPlatformItemVO> getCacheModifyDataList() {
		List<WorkingPlatformItemVO> selectionList = new ArrayList<WorkingPlatformItemVO>();
		// NEC Pagination changes: get the selected data from cache
		selectionList.addAll(workingPlatformItems.getCacheModifiedItems());
		return selectionList;
	}

	/**
	 * @param workingPlatformItems
	 *            the workingPlatformItems to set
	 */
	public void setWorkingPlatformItems(LazyDataModelForWorkingPlateform workingPlatformItems) {
		this.workingPlatformItems = workingPlatformItems;
	}

	/**
	 * @return the workingPlatformItems
	 */
	public LazyDataModelForWorkingPlateform getWorkingPlatformItems() {
		return workingPlatformItems;
	}

	// NEC Pagination Changes: Inner Class created for implementing Pagination
	// with Lazy Loading
	class LazyDataModelForWorkingPlateform extends SelectableLazyDataModel<WorkingPlatformItemVO> {
		/**
		 * 
		 */
		private static final long serialVersionUID = 8841907086704370497L;

		// NEC Pagination Changes: sets maximum no. of pages that can be stored
		// in cache at a time
		@Override
		public void startPagination() {
			super.startPagination();
			String cachePageSizeVal = sysConfigSB.getProperyValue(SelectableLazyDataModel.CACHE_PAGE_SIZE);
			try {
				super.setCachePageSize(Integer.parseInt(cachePageSizeVal));
			} catch (Exception e) {
				LOGGER.log(Level.WARNING, "Unable to set cacahe page size", e);
			}

		}

		/**
		 * @Description: TODO @author 042659 @param valueOf @return void @throws
		 */
		public void addChangeItem(String id) {
			// LOGGER.info(" call addChangeItem, the quote id is:" + id);
			workingPlatformItems.setCacheModifyData(id);

		}

		// NEC Pagination Changes: Method override of Abstract Class
		// BaseLazyDataMB
		@Override
		// NEC Pagination changes: it is callback method
		public void cellChangeListener(String id) {
			// LOGGER.info(" call cellChangeListener ::" + id);
			workingPlatformItems.setCacheModifyData(id);
			FacesUtil.updateUI("form_pendinglist:datatable_pendinglist");
		}

		// Created by DamonChen@20180309
		public void cellChangeListenerWithoutUpdateUI(String id) {
			// LOGGER.info(" call cellChangeListenerWithoutUpdateUI ::" + id);
			workingPlatformItems.setCacheModifyData(id);
		}

		// NEC Pagination Changes: Getter for lazy loading
		@Override
		// NEC Pagination changes: return data for pagination
		protected SelectableLazyDataModel<WorkingPlatformItemVO> getLazyData() {
			return workingPlatformItems;
		}

		@Override
		// NEC Pagination changes: it is called on row select
		public void onRowSelect(SelectEvent event) {
			super.onRowSelect(event);
			rowSelectupdate(event);
			removeItemBasedOnStaus(event);
			/*RequestContext  requestContext = RequestContext.getCurrentInstance();
			//requestContext.getApplicationContext()
			LOGGER.info("size:::"+requestContext.getAttributes());
			requestContext.getAttributes().keySet()
			.stream().forEach(key ->
			LOGGER.info(key + ":::" +
			RequestContext.getCurrentInstance().getAttributes().get(key)));*/
			
		}

		// NEC Pagination changes: it is called on row select check box
		public void onRowSelectCheckbox(SelectEvent event) {
			super.onRowSelectCheckbox(event);
			rowSelectupdate(event);
			removeItemBasedOnStaus(event);
		}

		// added by DamonChen@20180724 for fixing that User can select BIT and
		// BQ quote and proceed quote,begin
		private void removeItemBasedOnStaus(SelectEvent event) {
			WorkingPlatformItemVO vo = (WorkingPlatformItemVO) event.getObject();
			if (vo != null && vo.isDisableEditable()) {
				super.getCacheSelectedItems().remove(vo);
			}
		}

		public void onToggleSelect(ToggleSelectEvent event) {
			super.onToggleSelect(event);
			Iterator<WorkingPlatformItemVO> iterator = super.getCacheSelectedItems().iterator();
			while (iterator.hasNext()) {
				WorkingPlatformItemVO vo = iterator.next();
				if (vo != null && vo.isDisableEditable()) {
					iterator.remove();
				}
			}
		}
		// added by DamonChen@20180724 for fixing that User can select BIT and
		// BQ quote and proceed quote,end

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * com.avnet.emasia.webquote.web.datatable.LazyDataModelForPagination#
		 * findLazyData(int, int, java.lang.String, java.lang.String,
		 * java.util.Map)
		 */
		// NEC Pagination Changes: finds data for current page
		@Override
		public List<WorkingPlatformItemVO> findLazyData(int first, int pageSize, String sortField, String sortOrder,
				Map<String, Object> filters) {
			return quoteSB.findLazyData(criteria, first, pageSize, sortField, sortOrder, filters);
		}

		// NEC Pagination Changes: for calculation/logic after fetching records
		// from EJB layer
		@Override
		public List populateData(List searchResult) {
			List<QuoteItem> quoteSearchResult = (List<QuoteItem>) searchResult;
			// LOGGER.info("search finished, took " +
			// (System.currentTimeMillis() - start1) + " ms.");
			searchedWorkingPlatformItems = new ArrayList<WorkingPlatformItemVO>();

			for (QuoteItem quoteItem : quoteSearchResult) {
				WorkingPlatformItemVO item = new WorkingPlatformItemVO();
				// PROGRM PRICER ENHANCEMENT
				// item.setQuoteItem(item.getQuoteItem());
				item.setQuoteItem(quoteItem, bizUnit);

				int[] icons = quoteSB.calPricerNumber(quoteItem);
				item.setHasFuturePricer(icons[2] > 0 ? true : false);
				item.setChanged(false);
				item.setInitialized(true);
				item = rfqStatusCount(item);
				// added for show cost indicator icons
				item.setIconsArray(icons);
				searchedWorkingPlatformItems.add(item);

				if (selectedQuoteItem != null && selectedQuoteItem.getQuoteItem().getId() == quoteItem.getId()) {
					// changed to below for the bug that unable to key in the
					// cost in the rfq detail (working platform page),caused by
					// NEC
					if (!workingPlatformItems.getCacheModifiedItems().contains(selectedQuoteItem)) {
						selectedQuoteItem = item;
					}

				}
			}

			if (searchedWorkingPlatformItems != null && searchedWorkingPlatformItems.size() > 0) {
				LOGGER.info("workingPlatformItems size: " + searchedWorkingPlatformItems.size());

				for (Iterator<WorkingPlatformItemVO> itr = searchedWorkingPlatformItems.iterator(); itr.hasNext();) {
					WorkingPlatformItemVO filteredItem = itr.next();
					boolean found = false;
					for (QuoteItem quoteItem : quoteSearchResult) {
						if (filteredItem.getQuoteItem().getId() == quoteItem.getId()) {
							found = true;
							// PROGRM PRICER ENHANCEMENT
							filteredItem.setQuoteItem(quoteItem, bizUnit);
							filteredItem.setChanged(false);
							filteredItem.setInitialized(true);
							// changed to below for the bug that unable to key
							// in the cost in the rfq detail (working platform
							// page),caused by NEC
							if (selectedQuoteItem != null && selectedQuoteItem.getQuoteItem().getId() == filteredItem
									.getQuoteItem().getId()) {
								if (!workingPlatformItems.getCacheModifiedItems().contains(selectedQuoteItem)) {
									selectedQuoteItem = filteredItem;
								}

							}
						}
					}
					if (found == false) {
						itr.remove();
					}
				}
			}

			RequestContext.getCurrentInstance().execute("rfqCount();");
			// RequestContext.getCurrentInstance().update("datatable_pendinglist:p_mfr");
			return searchedWorkingPlatformItems;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * com.avnet.emasia.webquote.web.datatable.LazyDataModelForPagination#
		 * findLazyDataCount(java.lang.String, java.lang.String, java.util.Map)
		 */
		// NEC Pagination Changes: finds count of records in database
		@Override
		public int findLazyDataCount(String sortField, String sortOrder, Map<String, Object> filters) {
			long start = System.currentTimeMillis();
			int cnt = 0;
			try {
				Future<List<DropDownFilters>> futFilters = exc
						.submit(() -> quoteSB.searchFilterDropDownList(criteria, null, null, filters, null));

				Future<Map<String, Integer>> futMap = exc
						.submit(() -> quoteSB.searchStaticsCount(criteria, filters, StatisTargets));

				cnt = quoteSB.findLazyDataCount(criteria, null, sortField, sortOrder, filters);

				qcWorkingPlateFormStatusCounter(futMap.get());
				// if get more than 0 records, will handle filterList.
				if (cnt > 0) {
					handleFitersDropDown(futFilters.get(), false);
					LOGGER.info("cnt > 0 : then handleFitersDropDown.");
				}
				LOGGER.info("findLazyDataCount timespan:::::" + (System.currentTimeMillis() - start));

			} catch (Exception e) {
				LOGGER.log(Level.SEVERE, "findLazyDataCount failed", e);
			}
			return cnt;
		}

		private void qcWorkingPlateFormStatusCounter(Map<String, Integer> map) {
			setQcCounter(map.getOrDefault(RFQ_STATUS_QC, 0));
			setItCounter(map.getOrDefault(RFQ_STATUS_IT, 0));
			setSqCounter(map.getOrDefault(RFQ_STATUS_SQ, 0));
			setRitCounter(map.getOrDefault(RFQ_STATUS_RIT, 0));
			setRbitCounter(map.getOrDefault(RFQ_STATUS_RBIT, 0));
			setBitCounter(map.getOrDefault(RFQ_STATUS_BIT, 0));
			setBqCounter(map.getOrDefault(RFQ_STATUS_BQ, 0));
			setRbqCounter(map.getOrDefault(RFQ_STATUS_RBQ, 0));

			WorkingPlatformMB workingPlatformMB = (WorkingPlatformMB) FacesContext.getCurrentInstance()
					.getExternalContext().getSessionMap().get("workingPlatformMB");
			workingPlatformMB.rfqStatusCount();

			RequestContext.getCurrentInstance().execute("rfqCount();");
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see com.avnet.emasia.webquote.web.datatable.SelectableLazyDataModel#
		 * findLazyPreviousPageData(int, int, int, java.lang.String,
		 * java.lang.String, java.util.Map,
		 * java.util.concurrent.ConcurrentHashMap, java.util.Queue, int)
		 */
		// NEC Pagination Changes: finds data for previous page through
		// asynchronous call to database
		@Override
		public void findLazyPreviousPageData(int first, int pageSize, int pageNumber, String sortField, String sort,
				Map<String, Object> filters, ConcurrentHashMap<Integer, List<WorkingPlatformItemVO>> map,
				Queue<Integer> queue, int cachePageSize) {
			quoteSB.findLazyNextPageDataAsynchronously(criteria, first, pageSize, pageNumber, sortField, sort, filters,
					map, queue, cachePageSize);
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see com.avnet.emasia.webquote.web.datatable.SelectableLazyDataModel#
		 * findLazyNextPageData(int, int, int, java.lang.String,
		 * java.lang.String, java.util.Map,
		 * java.util.concurrent.ConcurrentHashMap, java.util.Queue, int)
		 */
		// NEC Pagination Changes: finds data for next page through asynchronous
		// call to database
		@Override
		public void findLazyNextPageData(int first, int pageSize, int pageNumber, String sortField, String sort,
				Map<String, Object> filters, ConcurrentHashMap<Integer, List<WorkingPlatformItemVO>> map,
				Queue<Integer> queue, int cachePageSize) {
			quoteSB.findLazyNextPageDataAsynchronously(criteria, first, pageSize, pageNumber, sortField, sort, filters,
					map, queue, cachePageSize);
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * com.avnet.emasia.webquote.web.datatable.LazyDataModelForPagination#
		 * getRowKey(java.lang.Object)
		 */
		@Override
		public Object getRowKey(WorkingPlatformItemVO object) {
			return object.getQuoteItem().getId();
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * com.avnet.emasia.webquote.web.datatable.LazyDataModelForPagination#
		 * getRowData(java.lang.String)
		 */
		@Override
		public WorkingPlatformItemVO getRowData(String rowKey) {
			List<WorkingPlatformItemVO> list = (List<WorkingPlatformItemVO>) getWrappedData();
			if (!list.isEmpty()) {
				for (WorkingPlatformItemVO workingPlatformItemVO : list) {
					String key = (String.valueOf(workingPlatformItemVO.getQuoteItem().getId()));
					if (rowKey.equals(key))
						return workingPlatformItemVO;
				}
			}
			return null;
		}

	}

	public String getUpdateIds() {
		return "future_pricer_icon," + "future_pricer_colum," + "futurePriceIcon," + "p_mpq," + "p_moq," + "p_mov,"
				+ "p_leadTime," + "p_cost," + "p_quotedMargin," + "p_quotedPrice," + "p_validity,"
				+ "p_shipmentValidity," + "p_targetMargin," + "p_costIndicator," +

				"p_buycurr," + "p_exRateStr," + "p_buyTargetResale," +

				"p_minSell," + "p_normSell," + "p_priceRemark1," + "p_priceRemark2," + "p_priceRemark3,"
				+ "p_priceRemark4," + "p_productGroup1," + "p_productGroup2," + "p_productGroup3," + "p_productGroup4,"
				+ "p_orderQtys_id,	" + "p_description," + "p_minSellPrice_id," + "p_salesCost_id,"
				+ "p_suggestedResale_id," + "p_termsAndConditions," + "p_cancelWindow," + "p_reschedulingWindow,"
				+ "p_quotationEffectiveDate," + "p_sapPartNumber," + "p_bizProgramType," + "p_qcExternalComment,"
				+ "p_qtyIndicator," + "p_quotedQty," + "p_salesCostType," + "p_salesCostPart," + "p_allocation_part,"
				+ ":ap_rfqdetailreference:form_rfqdetailreference";
	}

	public String getDetailTableUpdateIds() {

		return "d_sapPartNumber," + "d_mpq," + "d_moq," + "d_mov," + "d_leadTime," + "d_cost," + "d_quotedMargin,"
				+ "d_quotedPrice," + "d_quotedQty," + "d_validity," + "d_shipmentValidity," + "d_targetMargin,"
				+ "d_costIndicator," + "d_minSell," + "d_normSell," + "d_priceListRemark1," + "d_priceListRemark2,"
				+ "d_priceListRemark3," + "d_priceListRemark4," +

				"p_ref_orderQtys_id," + "p_ref_minSellPrice_id," + "p_ref_salesCost_id," + "p_ref_suggestedResale_id,"
				+ "d_salesCostPart," + "d_salescosttype," + "d_salescost," + "d_suggestedResale," +

				"d_buycurr," + "d_buyTargetResale," + "d_exRateStr," +
				// "d_suggestedResale," +

				"d_authGp," + "d_authGpReasonDesc," + "d_authGpRemark," + "d_drExpiryDate," + "d_cancelWindow,"
				+ "d_reschedulingWindow," + "d_quotationEffective," + "detail_quotationTc"
				+ ",:ap_rfqdetailreference:form_rfqdetailreference";
	}

	public List<SelectItem> getFilterDesignRegionList() {
		return filterDesignRegionList;
	}

	public void setFilterDesignRegionList(List<SelectItem> filterDesignRegionList) {
		this.filterDesignRegionList = filterDesignRegionList;
	}

}