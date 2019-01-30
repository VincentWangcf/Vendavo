package com.avnet.emasia.webquote.web.quote;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.math.BigDecimal;
import java.net.InetAddress;
import java.text.DecimalFormat;
import java.text.ParseException;
import com.avnet.emasia.webquote.utilities.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
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
import javax.faces.event.AjaxBehaviorEvent;
import javax.faces.event.PhaseEvent;
import javax.faces.event.PhaseId;
import javax.faces.model.SelectItem;
import javax.persistence.OptimisticLockException;
import javax.persistence.Tuple;

import org.apache.commons.lang.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFDateUtil;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.DataValidation;
import org.apache.poi.ss.usermodel.DataValidationConstraint;
import org.apache.poi.ss.usermodel.DataValidationHelper;
import org.apache.poi.ss.usermodel.Name;
import org.apache.poi.ss.util.CellRangeAddressList;
import org.apache.poi.util.IOUtils;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFColor;
import org.apache.poi.xssf.usermodel.XSSFDataValidationHelper;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.primefaces.component.datatable.DataTable;
import org.primefaces.context.RequestContext;
import org.primefaces.event.CellEditEvent;
import org.primefaces.event.SelectEvent;
import org.primefaces.event.UnselectEvent;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;
import org.primefaces.model.UploadedFile;
import org.springframework.security.core.context.SecurityContextHolder;

import com.avnet.emasia.webquote.cache.MfrDetailCacheManager;
import com.avnet.emasia.webquote.commodity.constant.PricerConstant;
import com.avnet.emasia.webquote.entity.Attachment;
import com.avnet.emasia.webquote.entity.BizUnit;
import com.avnet.emasia.webquote.entity.CostIndicator;
import com.avnet.emasia.webquote.entity.Currency;
import com.avnet.emasia.webquote.entity.DrmsAgpRea;
import com.avnet.emasia.webquote.entity.Manufacturer;
import com.avnet.emasia.webquote.entity.ManufacturerDetail;
import com.avnet.emasia.webquote.entity.Material;
import com.avnet.emasia.webquote.entity.NormalPricer;
import com.avnet.emasia.webquote.entity.QuoteItem;
import com.avnet.emasia.webquote.entity.SalesCostType;
import com.avnet.emasia.webquote.entity.User;
import com.avnet.emasia.webquote.exception.AppException;
import com.avnet.emasia.webquote.quote.ejb.CostIndicatorSB;
import com.avnet.emasia.webquote.quote.ejb.ManufacturerSB;
import com.avnet.emasia.webquote.quote.ejb.MaterialSB;
import com.avnet.emasia.webquote.quote.ejb.MyQuoteSearchSB;
import com.avnet.emasia.webquote.quote.ejb.PricerTypeMappingSB;
import com.avnet.emasia.webquote.quote.ejb.QuoteSB;
import com.avnet.emasia.webquote.quote.ejb.ResponseInternalTransferSB;
import com.avnet.emasia.webquote.quote.ejb.SAPWebServiceSB;
import com.avnet.emasia.webquote.quote.ejb.SapDpaCustSB;
import com.avnet.emasia.webquote.quote.ejb.SystemCodeMaintenanceSB;
import com.avnet.emasia.webquote.quote.ejb.constant.QuoteSBConstant;
import com.avnet.emasia.webquote.quote.strategy.factory.DrmsValidationUpdateStrategyFactory;
import com.avnet.emasia.webquote.quote.strategy.interfaces.DrmsValidationUpdateStrategy;
import com.avnet.emasia.webquote.quote.vo.FuturePriceCriteria;
import com.avnet.emasia.webquote.quote.vo.MyQuoteSearchCriteria;
import com.avnet.emasia.webquote.quote.vo.QuoteItemVo;
import com.avnet.emasia.webquote.quote.vo.WorkingPlatformItemVO;
import com.avnet.emasia.webquote.user.ejb.UserSB;
import com.avnet.emasia.webquote.utilites.resources.ResourceMB;
import com.avnet.emasia.webquote.utilites.web.common.CacheUtil;
import com.avnet.emasia.webquote.utilites.web.util.DeploymentConfiguration;
import com.avnet.emasia.webquote.utilites.web.util.QuoteUtil;
import com.avnet.emasia.webquote.utilities.MessageFormatorUtil;
import com.avnet.emasia.webquote.utilities.common.SysConfigSB;
import com.avnet.emasia.webquote.utilities.util.DateUtil;
import com.avnet.emasia.webquote.vo.PricerInfo;
import com.avnet.emasia.webquote.web.datatable.SelectableLazyDataModel;
import com.avnet.emasia.webquote.web.quote.cache.MfrCacheManager;
import com.avnet.emasia.webquote.web.quote.cache.QtyIndicatorCacheManager;
import com.avnet.emasia.webquote.web.quote.constant.QuoteConstant;
import com.avnet.emasia.webquote.web.quote.vo.PartModel;
import com.avnet.emasia.webquote.web.security.WQUserDetails;
import com.avnet.emasia.webquote.web.user.UserInfo;

/*
 * PASSWORD TO OPEN RITData_Template.xlsx sheet protection is avnet2013/webquote2013
 */
@ManagedBean
@SessionScoped
// NEC Pagination Changes: Class modified for implementing lazy loading in
// pagination
public class ResponseInternalTransferMB extends AttachmentEditMB implements Serializable {

	// Bryan Start
	@EJB
	CacheUtil cacheUtil;
	// Bryan End

	User user = null;

	private static final long serialVersionUID = -3246375851392860760L;

	private static final Logger LOG = Logger.getLogger(ResponseInternalTransferMB.class.getName());

	private String updateIds = ":form:growl, quotedPartNumber_in, mpq_in,moq_in,mov_in,"
			+ "leadTime_in,cost_in,quotedMargin_in,quotedPrice_in,quoteMarginPercentage,"
			+ "quotedQty_in,quotationEffectiveDate_in,validity_in,shipmentValidity_in,targetMargin,targetMarginPercentage,"
			+ "costIndicator_in, :form:panelGrid_responseITDetails, cancellationWindow_in, termsAndConditions_in,  "
			+ " rescheduleWindow_in, qtyIndicator_in, multiUsageFlag_in, " +

			"p_buycurr," + "p_exRateStr," + "p_buyTargetResale," + "p_priceRemark1," + "p_priceRemark2,"
			+ "p_priceRemark3," + "p_priceRemark4," +

			"resaleIndicator_in, authGpReasonDesc,sapPartNumber_in, allocationFlag_in,p_salesCost,p_suggestedResale,salesCostType_select,p_salesCostPart";

	private final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");

	private transient UploadedFile ritDataExcel;

	private transient StreamedContent downloadRitDataExcel;

	@EJB
	MyQuoteSearchSB myQuoteSearchSB;

	@EJB
	SystemCodeMaintenanceSB systemCodeMaintenanceSB;

	@EJB
	ResponseInternalTransferSB responseInternalTransferSB;

	@EJB
	MaterialSB materialSB;

	@EJB
	UserSB userSB;

	@EJB
	CostIndicatorSB costIndicatorSB;

	@EJB
	ManufacturerSB manufacturerSB;

	@EJB
	SysConfigSB sysConfigSB;

	@EJB
	QuoteSB quoteSB;

	@EJB
	SAPWebServiceSB sapWebServiceSB;

	// WebQuote 2.2 enhancement :
	@EJB
	PricerTypeMappingSB pricerTypeMappingSB;

	@EJB
	SapDpaCustSB sapDpaCustSB;

	// NEC Pagination changes: create for supporting the pagination with lazy
	// loading
	private LazyDataModelForResponseInternalTransfer quoteItemVo = new LazyDataModelForResponseInternalTransfer();

	private MyQuoteSearchCriteria criteria;

	private List<String> costIndicators = new ArrayList<String>();

	private List<String> qtyIndicators = new ArrayList<String>();

	private List<PricerInfo> pricerInfosInMaterialPopup = new ArrayList<>();

	private PricerInfo selectedPricerInfoInMaterialPopup;

	private List<PricerInfo> pricerInfosInPricerPopup = new ArrayList<>();

	private PricerInfo selectedPricerInfoInPricerPopup;

	private SelectItem[] mfrSelectList;

	private QuoteItemVo selectedQuoteItemVo;

	private String quoteNumber;

	private boolean allowAutoSearch = true;

	private List<String> avnetQuoteCentreComments;

	private List<String> termsAndConditions;

	private List<DrmsAgpRea> drmsAgpReasons;

	// used in Part Number search dialog box
	private String mfr;
	private String requestedPartNumber;

	private PartModel partModel;
	private int searchPartsCount = 0;
	private DecimalFormat df2 = new DecimalFormat("#");

	private QuoteItemVo seletedItem;

	// Used in restrict to update list from cache
	boolean cacheModificationFlag;

	// added by damonchen
	private SelectItem[] qtyIndicatorOptions;

	@ManagedProperty(value = "#{resourceMB}")
	/** To Inject ResourceMB instance */
	private ResourceMB resourceMB;

	public void setQuoteNumber(String quoteNumber) {
		allowAutoSearch = false;

		this.quoteNumber = quoteNumber;
		List<String> quoteNumbers = new ArrayList<String>();
		quoteNumbers.add(quoteNumber);
		criteria.setQuoteNumber(quoteNumbers);

		List<String> attachmentTypes = new ArrayList<String>();
		attachmentTypes.add(QuoteSBConstant.ATTACHMENT_TYPE_QUOTE);
		attachmentTypes.add(QuoteSBConstant.ATTACHMENT_TYPE_QUOTE_ITEM);
		attachmentTypes.add(QuoteSBConstant.ATTACHMENT_TYPE_REFRESH);
		criteria.setAttachmentType(attachmentTypes);
		setupUserInfoToCriteria();
		setupBizUnit();

		// LOG.info(criteria.toString());

		quoteItemVos = myQuoteSearchSB.search(criteria, resourceMB.getResourceLocale());
		criteria.setQuoteNumber(null);

		for (QuoteItemVo vo : quoteItemVos) {
			vo.setQuotedPartNumber(vo.getQuoteItem().getRequestedPartNumber());

			// Fix INC0027001 added By June missed term and condition and
			// shipment validity ----start
			// vo.setShipmentValidity(vo.getQuoteItem().getShipmentValidityStr());
			// vo.setQuotationEffectiveDate(vo.getQuoteItem().getQuotationEffectiveDateStr());
			// vo.setTermsAndConditionsStr(vo.getQuoteItem().getTermsAndConditions());
			// vo.setAqccStr(vo.getQuoteItem().getInternalTransferComment());
			// //added this clause just to keep same logic for pmSearch() method
			// Fix INC0027001 added By June missed term and condition and
			// shipment validity ----End

		}

		// NEC Pagination changes: get the selected data from cache
		selectedQuoteItemVos = new ArrayList<>(getCacheSelectionDatas());

		removedAttachments = new HashMap<Long, List<Attachment>>();

		selectedQuoteItemVo = null;
	}

	public ResponseInternalTransferMB() {

		super.attachmentType = QuoteSBConstant.ATTACHMENT_TYPE_INTERNAL_TRANSFER;
		super.showExistingAttachment = true;

	}

	/*
	 * public void cellChangeListener(String id) { // LOG.log(Level.INFO,
	 * "call updateFieldListener: {0}", id);
	 * this.quoteItemVo.setCacheModifyData(id);
	 * FacesUtil.updateUI("topForm:save_all_Button"); }
	 */

	// add cache for changed
	/*
	 * public void onCellEdit() { LOG.info("Call onCellEdit"); CellEditEvent
	 * event =new CellEditEvent(); Object newValue = event.getNewValue(); Object
	 * oldValue = event.getOldValue(); if(newValue!=null &&
	 * !newValue.equals(oldValue)) {
	 * this.quoteItemVo.setCacheModifyData(event.getRowKey());
	 * FacesUtil.updateUI("topForm:save_all_Button"); } }
	 */

	private Date getDate(int field, int amount) {
		Date date = new Date();
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		cal.add(field, amount);

		return cal.getTime();
	}

	@PostConstruct
	public void postContruct() {

		criteria = new MyQuoteSearchCriteria();
		user = ((WQUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUser();
		BizUnit bizUnit = user.getDefaultBizUnit();
		// Bryan Start
		// List<Manufacturer> manufacturers =
		// MfrCacheManager.getMfrListByBizUnit(bizUnit.getName());
		List<Manufacturer> manufacturers = cacheUtil.getMfrListByBizUnit(bizUnit.getName());
		// Bryan End
		List<String> fullMfrCodes = new ArrayList<String>();
		Date date = new Date();

		criteria.setPageSentOutDateFrom(getDate(Calendar.MONTH, -6));
		criteria.setPageSentOutDateTo(date);
		// added by DamonChen
		criteria.setSearchPage(QuoteSBConstant.RESPONSEINTERNALTRANSFER_PAGE);
		criteria.setSalsCostAccessFlag(user.isSalsCostAccessFlag());

		selectedQuoteItemVo = new QuoteItemVo();
		selectedQuoteItemVo.setQuoteItem(new QuoteItem());

		costIndicators.add("-SELECT-");

		for (CostIndicator costIndicator : costIndicatorSB.findAllActive()) {
			costIndicators.add(costIndicator.getName());
		}

		Collections.sort(costIndicators);

		// qtyIndicators.add("-SELECT-");

		// Bryan Start
		// qtyIndicators.addAll(1,
		// QtyIndicatorCacheManager.getQtyIndicatorList());
		// qtyIndicators.addAll(1, cacheUtil.getQtyIndicatorList());

		qtyIndicators = cacheUtil.getQtyIndicatorList();
		// List<String> qtyIndicatorList = cacheUtil.getQtyIndicatorList();
		qtyIndicatorOptions = QuoteUtil.createFilterOptions(qtyIndicators.toArray(new String[qtyIndicators.size()]),
				qtyIndicators.toArray(new String[qtyIndicators.size()]), false, false, false);

		// Bryan End

		// for(String qtyIndicator :
		// systemCodeMaintenanceSB.findQuantityIndicator()){
		// qtyIndicators.add(qtyIndicator);
		// }

		if (manufacturers != null) {
			for (Manufacturer manufacturer : manufacturers)
				fullMfrCodes.add(manufacturer.getName());
		}

		avnetQuoteCentreComments = systemCodeMaintenanceSB.findAvnetQuoteCentreComment();
		Collections.sort(avnetQuoteCentreComments);

		termsAndConditions = systemCodeMaintenanceSB.findTermsAndConditions(bizUnit.getName());
		Collections.sort(termsAndConditions);

		drmsAgpReasons = quoteSB.findAllDrmsAgpReasons();

		// NEC Pagination changes: called to fetch data for data table
		final DataTable d = (DataTable) FacesContext.getCurrentInstance().getViewRoot()
				.findComponent(":form:datatable_response_internal_transfer");
		if (d != null) {
			int first = 0;
			d.reset();
			d.setFirst(first);
		}

		quoteItemVo = new LazyDataModelForResponseInternalTransfer();

	}

	private void setupBizUnit() {

		User user = UserInfo.getUser();

		criteria.setBizUnits(user.getBizUnits());

	}

	private void setupUserInfoToCriteria() {

		User user = UserInfo.getUser();

		criteria.setDataAccesses(userSB.findAllDataAccesses(user));

		List<String> stage = new ArrayList<String>();
		stage.add(QuoteSBConstant.QUOTE_STAGE_PENDING);
		criteria.setStage(stage);

		List<String> status = new ArrayList<String>();
		status.add(QuoteSBConstant.RFQ_STATUS_IT);
		criteria.setStatus(status);

	}

	public void downloadXLS() {
		List<QuoteItemVo> quoteItemVosForDownload = new ArrayList<QuoteItemVo>();
		allowAutoSearch = false;
		criteria.setupUIInCriteria();
		setupUserInfoToCriteria();
		setupBizUnit();
		List<String> attachmentTypes = new ArrayList<String>();
		attachmentTypes.add(QuoteSBConstant.ATTACHMENT_TYPE_QUOTE);
		attachmentTypes.add(QuoteSBConstant.ATTACHMENT_TYPE_QUOTE_ITEM);
		attachmentTypes.add(QuoteSBConstant.ATTACHMENT_TYPE_REFRESH);
		criteria.setAttachmentType(attachmentTypes);
		// added for download offline function need the records on the WP page
		// only, by Damonchen@20180312
		DataTable dataTable = (DataTable) FacesContext.getCurrentInstance().getViewRoot()
				.findComponent("form:datatable_response_internal_transfer");
		Map<String, Object> filtersMap = dataTable.getFilters();
		criteria.setFiltersMap(filtersMap);
		// end
		criteria.setAction(QuoteSBConstant.RESPONSEIT_DOWNLOAD_SEARCH_ACTION);
		// LOG.info(criteria.toString());

		if (criteria.getsQuotedPartNumber() != null && criteria.getsQuotedPartNumber().length() > 0
				&& criteria.getsQuotedPartNumber().length() < 3) {
			FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR,
					ResourceMB.getText("wq.message.minLenError"), "");
			FacesContext.getCurrentInstance().addMessage("growl", msg);
			return;
		}

		if (criteria.getsCustomerName() != null && criteria.getsCustomerName().length() > 0
				&& criteria.getsCustomerName().length() < 3) {
			FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR,
					ResourceMB.getText("wq.message.cutmrLenError"), "");
			FacesContext.getCurrentInstance().addMessage("growl", msg);
			return;
		}
		if (criteria.getPageSentOutDateFrom() != null && criteria.getPageSentOutDateFrom() != null
				&& criteria.getPageSentOutDateFrom().after(criteria.getPageSentOutDateTo())) {
			FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR,
					ResourceMB.getText("wq.message.quoteRelDateMsg"), "");
			FacesContext.getCurrentInstance().addMessage("growl", msg);
			return;
		}
		quoteItemVosForDownload = myQuoteSearchSB.search(criteria, resourceMB.getResourceLocale());
		this.quoteItemVo.populateData(quoteItemVosForDownload);

		// please note that need to set filtersMap as null , as so many method
		// call myQuoteSearchSB.search(criteria,resourceMB.getResourceLocale()
		// );,
		criteria.setFiltersMap(null);
		if (quoteItemVosForDownload.size() == 0 && (filteredQuoteItemVos == null || filteredQuoteItemVos.isEmpty())) {
			String errorMessage = ResourceMB.getText("wq.message.dataExportReq");
			FacesContext.getCurrentInstance().addMessage("growl",
					new FacesMessage(FacesMessage.SEVERITY_ERROR, errorMessage, ""));
			return;
		}

		FileInputStream input = null;
		ByteArrayOutputStream outStream = null;
		String filePath = DeploymentConfiguration.configPath + File.separator + QuoteConstant.RIT_DATA_TEMPLATE;
		//TODO
		try { 
           String address = InetAddress.getLocalHost().getHostName().toString();
           if("cis2115vmts".equalsIgnoreCase(address)) { 
        	   filePath = "C:\\david\\sharefolder\\tempd"+File.separator+QuoteConstant.RIT_DATA_TEMPLATE;
           }
        } catch (Exception e) { 
            e.printStackTrace(); 
        }
		DecimalFormat df5 = new DecimalFormat("#");
		df5.setMaximumFractionDigits(5);
		df5.setMinimumFractionDigits(5);
		df5.setMinimumIntegerDigits(1);
		// filePath =
		// "C:"+File.separator+"template0312"+File.separator+QuoteConstant.RIT_DATA_TEMPLATE;
		try {
			input = new FileInputStream(filePath);

			XSSFWorkbook wb = new XSSFWorkbook(input);
			XSSFSheet sheet = wb.getSheetAt(0);
			XSSFRow firstRow = sheet.getRow(0);
			XSSFRow row;
			XSSFCell cell;

			// sheet.protectSheet("avnet2013");

			XSSFCellStyle lockedCellStyle = wb.createCellStyle();
			// lockedCellStyle.setFillForegroundColor(new XSSFColor(new
			// java.awt.Color(179, 202, 226)));
			// lockedCellStyle.setFillPattern(CellStyle.SOLID_FOREGROUND);

			XSSFCellStyle mandatoryCellStyle = wb.createCellStyle();
			mandatoryCellStyle.setFillForegroundColor(new XSSFColor(new java.awt.Color(255, 255, 0)));
			mandatoryCellStyle.setFillPattern(CellStyle.SOLID_FOREGROUND);
			mandatoryCellStyle.setLocked(false);

			XSSFCellStyle optionalCellStyle = wb.createCellStyle();
			optionalCellStyle.setFillForegroundColor(new XSSFColor(new java.awt.Color(242, 220, 219)));

			optionalCellStyle.setFillPattern(CellStyle.SOLID_FOREGROUND);
			optionalCellStyle.setLocked(false);

			XSSFCellStyle unlockedCellStyle = wb.createCellStyle();
			unlockedCellStyle.setLocked(false);
			//no need.
			//setupExcelDropDownList(sheet, wb);

			outStream = new ByteArrayOutputStream();
			SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
			if (filteredQuoteItemVos != null && !filteredQuoteItemVos.isEmpty()) {
				quoteItemVosForDownload = filteredQuoteItemVos;
			} else {
				// quoteItemVos = this.quoteItemVos;
			}

			Collections.reverse(quoteItemVosForDownload);
			
			for (int rowNum = 0; rowNum <= quoteItemVosForDownload.size(); rowNum++) {

				if (rowNum == 0) {
					row = sheet.getRow(rowNum);
				} else {
					row = sheet.createRow(rowNum);
					QuoteItemVo vo = quoteItemVosForDownload.get(rowNum - 1);
					int cols = firstRow.getPhysicalNumberOfCells();
					int offset = 9;
					for (int c = 0; c < cols; c++) {
						try {
							cell = row.createCell(c);
							if (c == 0) // Form No
							{
								// cell = row.createCell(c);
								cell.setCellStyle(lockedCellStyle);
								if (vo.getQuoteItem().getQuote().getFormNumber() == null) {
								} else {
									cell.setCellValue(vo.getQuoteItem().getQuote().getFormNumber());
								}
							} else if (c == 1) { // Avnet Quote#
								cell.setCellStyle(lockedCellStyle);
								if (vo.getQuoteItem().getQuoteNumber() == null) {
								} else {
									cell.setCellValue(vo.getQuoteItem().getQuoteNumber());
								}
							} else if (c == 2) {// Pending Day
								cell.setCellStyle(lockedCellStyle);
								if (vo.getQuoteItem().getQuoteNumber() == null) {
								} else {
									cell.setCellValue(vo.getQuoteItem().getPendingDay());
								}
							} else if (c == 3) // MFR
							{
								cell.setCellStyle(lockedCellStyle);
								if (vo.getQuoteItem().getRequestedMfr().getName() == null) {
								} else {
									cell.setCellValue(vo.getQuoteItem().getRequestedMfr().getName());
								}
							}
							// WebQuote 2.2 enhancement : fields changes.
							// else if(c==4) // Bid-To-Buy
							// {
							//
							// cell.setCellStyle(lockedCellStyle);
							// if(vo.getQuoteItem().getOrderOnHandFlag()==null){}
							// else
							// {
							// if(vo.getQuoteItem().getOrderOnHandFlag()==true)
							// {
							// cell.setCellValue("Yes");
							// }
							// else
							// {
							// cell.setCellValue("No");
							// }
							// }
							// }
							else if (c == 4) // quote type
							{
								cell.setCellStyle(lockedCellStyle);
								if (vo.getQuoteItem().getQuoteType() == null) {
								} else {
									cell.setCellValue(vo.getQuoteItem().getQuoteType());
								}
							} else if (c == 5) // sales cost part
							{
								cell.setCellStyle(lockedCellStyle);
								if (vo.getQuoteItem().isSalesCostFlag() == true) {
									cell.setCellValue("Yes");
								} else {
									cell.setCellValue("No");
								}
							} else if (c == 6) // sales cost type
							{

								cell.setCellStyle(mandatoryCellStyle);
								if (vo.getQuoteItem().getSalesCostType() == null) {
									cell.setCellValue(QuoteConstant.SALES_COST_TYPE_NONSC);
								} else {
									cell.setCellValue(vo.getQuoteItem().getSalesCostType().name());
								}
							}

							else if (c == 7) // Salesman Name
							{
								cell.setCellStyle(lockedCellStyle);
								if (vo.getQuoteItem().getQuote().getSales().getName() == null) {
								} else {
									cell.setCellValue(vo.getQuoteItem().getQuote().getSales().getName());
								}
							} else if (c == 8) // Team
							{
								cell.setCellStyle(lockedCellStyle);
								if (vo.getQuoteItem().getQuote().getTeam() != null) {
									cell.setCellValue(vo.getQuoteItem().getQuote().getTeam().getName());
								}
							} else if (c == 9) // Customer Name(Sold To Party)
							{

								cell.setCellStyle(lockedCellStyle);
								if (vo.getQuoteItem().getSoldToCustomer() == null) {
								} else {
									cell.setCellValue(vo.getQuoteItem().getSoldToCustomer().getCustomerFullName());
								}

							} else if (c == 10) // Customer Type
							{

								cell.setCellStyle(lockedCellStyle);
								if (vo.getQuoteItem().getQuote().getCustomerType() == null) {
								} else {
									cell.setCellValue(vo.getQuoteItem().getQuote().getCustomerType());
								}
							} else if (c == 11) // End Customer
							{

								cell.setCellStyle(lockedCellStyle);
								if (vo.getQuoteItem().getEndCustomer() == null) {
								} else {
									cell.setCellValue(vo.getQuoteItem().getEndCustomer().getCustomerFullName());
								}
							} else if (c == 12) {

								cell.setCellStyle(optionalCellStyle);
								if (vo.getQuoteItem().getCustomerGroupId() == null) {
								} else {
									cell.setCellValue(vo.getQuoteItem().getCustomerGroupId());
								}
							} else if (c == 13) // Requested P/N
							{

								cell.setCellStyle(lockedCellStyle);
								if (vo.getQuoteItem().getRequestedPartNumber() == null) {
								} else {
									cell.setCellValue(vo.getQuoteItem().getRequestedPartNumber());
								}
							} else if (c == 14) // Required Qty
							{

								cell.setCellStyle(lockedCellStyle);
								if (vo.getQuoteItem().getRequestedQty() == null) {
								} else {
									cell.setCellValue(vo.getQuoteItem().getRequestedQty());
								}
							} else if (c == 15) // EAU
							{

								cell.setCellStyle(lockedCellStyle);
								if (vo.getQuoteItem().getEau() == null) {
								} else {
									cell.setCellValue(vo.getQuoteItem().getEau());
								}
							} else if (c == 16) // $Link
							{

								cell.setCellStyle(lockedCellStyle);
								if (vo.getQuoteItem().getQuote().isdLinkFlag()) {
									cell.setCellValue("Yes");
								} else {
									cell.setCellValue("No");
								}
							} else if (c == 17) // RFQ CUR
							{

								cell.setCellStyle(lockedCellStyle);
								if (vo.getQuoteItem().getRfqCurr() == null) {
								} else {
									cell.setCellValue(vo.getQuoteItem().getRfqCurr().toString());
								}
							} else if (c == 18) // Target Price (RFQ CUR)
							{

								cell.setCellStyle(lockedCellStyle);
								if (vo.getQuoteItem().getTargetResale() == null) {
								} else {
									cell.setCellValue(vo.getQuoteItem().getTargetResale());
								}
							} else if (c == 19) // Target Price Margin
							{

								cell.setCellStyle(lockedCellStyle);
								if (vo.getQuoteItem().getTargetMargin() == null) {
								} else {
									cell.setCellValue(vo.getQuoteItem().getTargetMargin());
								}
							} else if (c == 20) // Exchange Rate
							{

								cell.setCellStyle(lockedCellStyle);
								if (vo.getQuoteItem().getExRate() == null) {
								} else
									cell.setCellValue(vo.getQuoteItem().getExRate().toString());
							}

							else if (c == 21) // Avnet Quoted P/N
							{

								cell.setCellStyle(mandatoryCellStyle);
								if (vo.getQuotedPartNumber() == null) {
								} else
									cell.setCellValue(vo.getQuotedPartNumber());
							}
							// vo.quoteItem.convertToBuyValueWithDouble(vo.quoteItem.targetResale)
							else if (c == 22) // Target Price (BUY CUR)
							{

								cell.setCellStyle(lockedCellStyle);
								if (vo.getQuoteItem().getTargetResale() == null) {
								} else {
									BigDecimal v = vo.getQuoteItem()
											.convertToBuyValueWithDouble(vo.getQuoteItem().getTargetResale());
									if (v != null) {
										cell.setCellValue(v.toString());
									}
								}

							}

							else if (c == 23) // Quote Margin (aka Real Margin)
							{

								cell.setCellStyle(optionalCellStyle);
								if (vo.getQuoteItem().getResaleMargin() == null) {
								} else {
									cell.setCellValue(vo.getQuoteItem().getResaleMargin());
								}
							} else if (c == 24) // Avnet Quoted Price
							{

								cell.setCellStyle(mandatoryCellStyle);
								if (vo.getQuoteItem().getQuotedPrice() == null
										|| vo.getQuoteItem().getQuotedPrice() == 0.0) {
								} else {
									cell.setCellValue( df5.format(vo.getQuoteItem().getQuotedPrice()));
								}
							}

							else if (c == 25) // sales cost
							{

								cell.setCellStyle(mandatoryCellStyle);
								if (vo.getQuoteItem().getSalesCost() == null
										|| (vo.getQuoteItem().getSalesCost().compareTo(BigDecimal.ZERO) == 0)) {
								} else {
									cell.setCellValue(vo.getQuoteItem().getSalesCost().doubleValue());
								}
							}

							else if (c == 26) // Suggested Resale
							{

								// cell.setCellStyle(mandatoryCellStyle);
								cell.setCellStyle(optionalCellStyle);
								if (vo.getQuoteItem().getSuggestedResale() == null
										|| (vo.getQuoteItem().getSuggestedResale().compareTo(BigDecimal.ZERO) == 0)) {
								} else {
									cell.setCellValue(vo.getQuoteItem().getSuggestedResale().doubleValue());
								}
							}

							else if (c == 27) // Cost Indicator
							{

								cell.setCellStyle(mandatoryCellStyle);
								if (vo.getQuoteItem().getCostIndicator() == null) {
								} else
									cell.setCellValue(vo.getQuoteItem().getCostIndicator());
							} else if (c == 28) // BUY CUR
							{

								cell.setCellStyle(mandatoryCellStyle);
								if (vo.getQuoteItem().getBuyCurr() == null) {
								} else {
									cell.setCellValue(vo.getQuoteItem().getBuyCurr().toString());
								}

							} else if (c == 29) // Cost
							{

								cell.setCellStyle(mandatoryCellStyle);
								if (vo.getQuoteItem().getCost() == null) {
								} else {
									cell.setCellValue(vo.getQuoteItem().getCost());
								}
							} else if (c == 30) // Qty Indicator
							{

								cell.setCellStyle(mandatoryCellStyle);
								if (vo.getQuoteItem().getQtyIndicator() == null) {
								} else
									cell.setCellValue(vo.getQuoteItem().getQtyIndicator());
							} else if (c == 31) // Avnet Quoted Qty
							{

								cell.setCellStyle(optionalCellStyle);
								if (vo.getQuoteItem().getQuotedQty() == null) {
								} else
									cell.setCellValue(vo.getQuoteItem().getQuotedQty());
							} else if (c == 32) // Pricer List Remarks 1
							{

								cell.setCellStyle(lockedCellStyle);
								if (vo.getQuoteItem().getPriceListRemarks1() == null) {
								} else
									cell.setCellValue(vo.getQuoteItem().getPriceListRemarks1());
							} else if (c == 33) // Pricer List Remarks 2
							{

								cell.setCellStyle(lockedCellStyle);
								if (vo.getQuoteItem().getPriceListRemarks2() == null) {
								} else
									cell.setCellValue(vo.getQuoteItem().getPriceListRemarks2());
							} else if (c == 34) // Pricer List Remarks 3
							{

								cell.setCellStyle(lockedCellStyle);
								if (vo.getQuoteItem().getPriceListRemarks3() == null) {
								} else
									cell.setCellValue(vo.getQuoteItem().getPriceListRemarks3());
							} else if (c == 35) // Pricer List Remarks 4
							{

								cell.setCellStyle(lockedCellStyle);
								if (vo.getQuoteItem().getPriceListRemarks4() == null) {
								} else
									cell.setCellValue(vo.getQuoteItem().getPriceListRemarks4());
							}
							// add bmt fields
							// Design Location
							else if (c == 27 + offset) {

								cell.setCellStyle(unlockedCellStyle);
								if (vo.getQuoteItem().getDesignLocation() == null) {
								} else
									cell.setCellValue(vo.getQuoteItem().getDesignLocation());
							}
							// Design Region
							else if (c == 28 + offset) {

								cell.setCellStyle(unlockedCellStyle);
								if (vo.getQuoteItem().getDesignRegion() == null) {
								} else
									cell.setCellValue(vo.getQuoteItem().getDesignRegion());
							}
							// BMT Flag
							else if (c == 29 + offset) {

								cell.setCellStyle(unlockedCellStyle);
								if (vo.getQuoteItem().getQuoteItemDesign() == null) {
								} else
									cell.setCellValue(
											vo.getQuoteItem().getQuoteItemDesign().getBmtFlag().getDescription());
							}
							// BMT MFR DR#
							else if (c == 30 + offset) {

								cell.setCellStyle(unlockedCellStyle);
								if (vo.getQuoteItem().getQuoteItemDesign() != null
										&& vo.getQuoteItem().getQuoteItemDesign().getDrMfrNo() != null) {
									cell.setCellValue(vo.getQuoteItem().getQuoteItemDesign().getDrMfrNo());
								}
							}
							// BMT Suggest Cost
							else if (c == 31 + offset) {

								cell.setCellStyle(unlockedCellStyle);
								if (vo.getQuoteItem().getQuoteItemDesign() != null
										&& vo.getQuoteItem().getQuoteItemDesign().getDrCost() != null) {
									cell.setCellValue(
											String.valueOf(vo.getQuoteItem().getQuoteItemDesign().getDrCost()));
								}
							}
							// BMT Suggest Resale
							else if (c == 32 + offset) {

								cell.setCellStyle(unlockedCellStyle);
								if (vo.getQuoteItem().getQuoteItemDesign() != null
										&& vo.getQuoteItem().getQuoteItemDesign().getDrResale() != null) {
									cell.setCellValue(
											String.valueOf(vo.getQuoteItem().getQuoteItemDesign().getDrResale()));
								}
							}
							// BMT Sugget Margin
							else if (c == 33 + offset) {

								cell.setCellStyle(unlockedCellStyle);
								if (vo.getQuoteItem().getQuoteItemDesign() != null
										&& vo.getQuoteItem().getQuoteItemDesign().getDrMargin() != null) {
									cell.setCellValue(
											String.valueOf(vo.getQuoteItem().getQuoteItemDesign().getDrMargin()));
								}
							}
							// BMT MFR Quote#
							else if (c == 34 + offset) {

								cell.setCellStyle(unlockedCellStyle);
								if (vo.getQuoteItem().getQuoteItemDesign() != null
										&& vo.getQuoteItem().getQuoteItemDesign().getDrMfrQuoteNo() != null) {
									cell.setCellValue(vo.getQuoteItem().getQuoteItemDesign().getDrMfrQuoteNo());
								}
							}
							// BMT SQ Effective Date
							else if (c == 35 + offset) {

								cell.setCellStyle(unlockedCellStyle);
								if (vo.getQuoteItem().getQuoteItemDesign() != null
										&& vo.getQuoteItem().getQuoteItemDesign().getDrEffectiveDate() != null) {
									String date = sdf
											.format(vo.getQuoteItem().getQuoteItemDesign().getDrEffectiveDate());
									cell.setCellValue(date);
								}
							}
							// BMT SQ Expiry Date
							else if (c == 36 + offset) {

								cell.setCellStyle(unlockedCellStyle);
								if (vo.getQuoteItem().getQuoteItemDesign() != null
										&& vo.getQuoteItem().getQuoteItemDesign().getDrExpiryDate() != null) {
									String date = sdf.format(vo.getQuoteItem().getQuoteItemDesign().getDrExpiryDate());
									cell.setCellValue(date);
								}
							}
							// BMT Comments
							else if (c == 37 + offset) {

								cell.setCellStyle(unlockedCellStyle);
								if (vo.getQuoteItem().getQuoteItemDesign() != null
										&& vo.getQuoteItem().getQuoteItemDesign().getDrComments() != null) {
									cell.setCellValue(vo.getQuoteItem().getQuoteItemDesign().getDrComments());
								}
							}
							// BMT Attachment
							else if (c == 38 + offset) {

								cell.setCellStyle(unlockedCellStyle);
								cell.setCellValue(vo.getQuoteItem().getBmtAttachmentFlag() ? "Yes" : "No");
							}
							// BMT Quoted Qty
							else if (c == 39 + offset) {

								cell.setCellStyle(unlockedCellStyle);
								if (vo.getQuoteItem().getQuoteItemDesign() != null
										&& vo.getQuoteItem().getQuoteItemDesign().getDrQuoteQty() != null) {
									cell.setCellValue(vo.getQuoteItem().getQuoteItemDesign().getDrQuoteQty());
								}
							}
							// BMT Suggest Cost (Non-USD)
							else if (c == 40 + offset) {

								cell.setCellStyle(unlockedCellStyle);
								if (vo.getQuoteItem().getQuoteItemDesign() != null
										&& vo.getQuoteItem().getQuoteItemDesign().getDrCostAltCurrency() != null) {
									cell.setCellValue(String
											.valueOf(vo.getQuoteItem().getQuoteItemDesign().getDrCostAltCurrency()));
								}
							}
							// BMT Suggest Resale (Non-USD)
							else if (c == 41 + offset) {

								cell.setCellStyle(unlockedCellStyle);
								if (vo.getQuoteItem().getQuoteItemDesign() != null
										&& vo.getQuoteItem().getQuoteItemDesign().getDrResaleAltCurrency() != null) {
									cell.setCellValue(String
											.valueOf(vo.getQuoteItem().getQuoteItemDesign().getDrResaleAltCurrency()));
								}
							}
							// BMT Currency
							else if (c == 42 + offset) {

								cell.setCellStyle(unlockedCellStyle);
								if (vo.getQuoteItem().getQuoteItemDesign() != null
										&& vo.getQuoteItem().getQuoteItemDesign().getDrAltCurrency() != null) {
									cell.setCellValue(
											String.valueOf(vo.getQuoteItem().getQuoteItemDesign().getDrAltCurrency()));
								}
							}
							// BMT Exchange Rate (Non-USD)
							else if (c == 43 + offset) {

								cell.setCellStyle(unlockedCellStyle);
								if (vo.getQuoteItem().getQuoteItemDesign() != null
										&& vo.getQuoteItem().getQuoteItemDesign().getDrExchangeRate() != null) {
									cell.setCellValue(
											String.valueOf(vo.getQuoteItem().getQuoteItemDesign().getDrExchangeRate()));
								}
							}
							// bmt end
							else if (c == 44 + offset) // Multi-Usage
							{

								cell.setCellStyle(mandatoryCellStyle);
								if (vo.getQuoteItem().getMultiUsageFlag() == null) {
								} else {
									if (vo.getQuoteItem().getMultiUsageFlag() == true) {
										cell.setCellValue("Yes");
									} else {
										cell.setCellValue("No");
									}
								}
							} else if (c == 45 + offset) // QC Comment (Avnet Internal
												// Only)
							{

								cell.setCellStyle(lockedCellStyle);
								if (vo.getQuoteItem().getInternalComment() == null) {
								} else {
									cell.setCellValue(vo.getQuoteItem().getInternalComment());
								}
							} else if (c == 46 + offset) // QC Attachment
							{

								cell.setCellStyle(lockedCellStyle);
								cell.setCellValue(vo.getQuoteItem().getQcAttachmentFlag() ? "Yes" : "No");
							} else if (c == 47 + offset) // Lead Time (wks)
							{
								cell.setCellStyle(mandatoryCellStyle);
								if (vo.getQuoteItem().getLeadTime() == null) {
								} else
									cell.setCellValue(vo.getQuoteItem().getLeadTime());
							} else if (c == 48 + offset) // MPQ
							{
								cell.setCellStyle(mandatoryCellStyle);
								if (vo.getQuoteItem().getMpq() == null) {
								} else
									cell.setCellValue(vo.getQuoteItem().getMpq());
							} else if (c == 49 + offset) // MOQ
							{

								cell.setCellStyle(mandatoryCellStyle);
								if (vo.getQuoteItem().getMoq() == null) {
								} else
									cell.setCellValue(vo.getQuoteItem().getMoq());
							} else if (c == 50 + offset) // MOV
							{

								cell.setCellStyle(mandatoryCellStyle);
								if (vo.getQuoteItem().getMov() == null) {
								} else
									cell.setCellValue(vo.getQuoteItem().getMov());
							}

							else if (c == 51 + offset) // Quotation effective date
							{

								cell.setCellStyle(optionalCellStyle);
								if (vo.getQuoteItem().getQuotationEffectiveDate() == null) {
								} else
									cell.setCellValue(sdf.format(vo.getQuoteItem().getQuotationEffectiveDate()));

							}

							else if (c == 52 + offset) // Price Validity
							{

								cell.setCellStyle(mandatoryCellStyle);
								if (vo.getQuoteItem().getPriceValidity() == null) {
								} else
									cell.setCellValue(vo.getQuoteItem().getPriceValidity());
							} else if (c == 53 + offset) // Shipment Validity
							{

								cell.setCellStyle(optionalCellStyle);
								if (vo.getQuoteItem().getShipmentValidity() == null) {
								} else
									cell.setCellValue(sdf.format(vo.getQuoteItem().getShipmentValidity()));

							} else if (c == 54 + offset) // MFR Debit #
							{

								cell.setCellStyle(optionalCellStyle);
								if (vo.getQuoteItem().getVendorDebitNumber() == null) {
								} else
									cell.setCellValue(vo.getQuoteItem().getVendorDebitNumber());
							} else if (c == 55 + offset) // MFR Quote #
							{

								cell.setCellStyle(optionalCellStyle);
								if (vo.getQuoteItem().getVendorQuoteNumber() == null) {
								} else
									cell.setCellValue(vo.getQuoteItem().getVendorQuoteNumber());
							} else if (c == 56 + offset) // MFR Quote Qty
							{

								cell.setCellStyle(optionalCellStyle);
								if (vo.getQuoteItem().getVendorQuoteQty() == null) {
								} else
									cell.setCellValue(vo.getQuoteItem().getVendorQuoteQty());
							} else if (c == 57 + offset) // Rescheduling Window
							{

								cell.setCellStyle(optionalCellStyle);
								if (vo.getQuoteItem().getRescheduleWindow() == null) {
								} else
									cell.setCellValue(vo.getQuoteItem().getRescheduleWindow());
							} else if (c == 58 + offset) // Cancellation Window
							{

								cell.setCellStyle(optionalCellStyle);
								if (vo.getQuoteItem().getCancellationWindow() == null) {
								} else
									cell.setCellValue(vo.getQuoteItem().getCancellationWindow());
							} else if (c == 59 + offset) // Quotation T&C
							{

								cell.setCellStyle(optionalCellStyle);
								if (vo.getQuoteItem().getTermsAndConditions() == null) {
								} else
									cell.setCellValue(vo.getQuoteItem().getTermsAndConditions());
							} else if (c == 60 + offset) // Resale Indicator
							{

								cell.setCellStyle(optionalCellStyle);
								if (vo.getQuoteItem().getResaleIndicator() == null) {
								} else
									cell.setCellValue(vo.getQuoteItem().getResaleIndicator());
							} else if (c == 61 + offset) // RFQ Form Attachment
							{
								// updated by damon@20160810

								cell.setCellStyle(lockedCellStyle);
								if (vo.getQuoteItem().getQuote().getFormAttachmentFlag() == null) {
									cell.setCellValue("No");
								} else {
									if (vo.getQuoteItem().getQuote().getFormAttachmentFlag()) {
										cell.setCellValue("Yes");
									} else {
										cell.setCellValue("No");
									}
								}
							} else if (c == 62 + offset) // RFQ Item Attachment
							{

								cell.setCellStyle(lockedCellStyle);
								if (vo.getQuoteItem().getItemAttachmentFlag()) {
									cell.setCellValue("Yes");
								} else {
									cell.setCellValue("No");
								}
							} else if (c == 63 + offset) // Refresh Attachment
							{

								cell.setCellStyle(lockedCellStyle);
								if (vo.getQuoteItem().getRefreshAttachmentFlag()) {
									cell.setCellValue("Yes");
								} else {
									cell.setCellValue("No");
								}
							} else if (c == 64 + offset) // PM Comment
							{

								cell.setCellStyle(optionalCellStyle);
								if (vo.getQuoteItem().getInternalTransferComment() == null) {
								} else
									cell.setCellValue(vo.getQuoteItem().getInternalTransferComment());
							} else if (c == 65 + offset) // Allocation Part
							{

								cell.setCellStyle(optionalCellStyle);
								if (vo.getQuoteItem().getAllocationFlag() == null) {
								} else {
									if (vo.getQuoteItem().getAllocationFlag() == true) {
										cell.setCellValue("Yes");
									} else {
										cell.setCellValue("No");
									}
								}
							} else if (c == 66 + offset) // Authorized GP%
							{

								cell.setCellStyle(lockedCellStyle);
								if (vo.getQuoteItem().getAuthGp() != null) {
									cell.setCellValue(vo.getQuoteItem().getAuthGp());
								}
							} else if (c == 67 + offset) // Reasons for Authorized GP%
												// change
							{

								cell.setCellStyle(optionalCellStyle);
								if (vo.getQuoteItem().getAuthGpReasonDesc() != null) {
									cell.setCellValue(vo.getQuoteItem().getAuthGpReasonDesc());
								}
							} else if (c == 68 + offset) // Remarks of Reason
							{

								cell.setCellStyle(optionalCellStyle);
								if (vo.getQuoteItem().getAuthGpRemark() != null) {
									cell.setCellValue(vo.getQuoteItem().getAuthGpRemark());
								}
							} else if (c == 69 + offset) // SPR
							{

								cell.setCellStyle(lockedCellStyle);
								if (vo.getQuoteItem().getSprFlag() == null) {
								} else {
									if (vo.getQuoteItem().getSprFlag() == true) {
										cell.setCellValue("Yes");
									} else {
										cell.setCellValue("No");
									}
								}
							} else if (c == 70 + offset) // Project Name
							{

								cell.setCellStyle(lockedCellStyle);
								if (vo.getQuoteItem().getProjectName() == null) {
								} else {
									cell.setCellValue(vo.getQuoteItem().getProjectName());
								}
							} else if (c == 71 + offset) // Application
							{

								cell.setCellStyle(lockedCellStyle);
								if (vo.getQuoteItem().getApplication() == null) {
								} else {
									cell.setCellValue(vo.getQuoteItem().getApplication());
								}
							} else if (c == 72 + offset) // MP Schedule
							{

								cell.setCellStyle(lockedCellStyle);
								if (vo.getQuoteItem().getMpSchedule() == null) {
								} else {
									cell.setCellValue(vo.getQuoteItem().getMpSchedule());
								}
							} else if (c == 73 + offset) // PP Schedule
							{

								cell.setCellStyle(lockedCellStyle);
								if (vo.getQuoteItem().getPpSchedule() == null) {
								} else {
									cell.setCellValue(vo.getQuoteItem().getPpSchedule());
								}
							} else if (c == 74 + offset) // DRMS Project ID
							{

								cell.setCellStyle(lockedCellStyle);
								if (vo.getQuoteItem().getDrmsNumber() == null) {
								} else {
									cell.setCellValue(vo.getQuoteItem().getDrmsNumber());
								}
							} else if (c == 75 + offset) // MFR DR#
							{

								cell.setCellStyle(lockedCellStyle);
								if (vo.getQuoteItem().getSupplierDrNumber() == null) {
								} else {
									cell.setCellValue(vo.getQuoteItem().getSupplierDrNumber());
								}
							}
							// WebQuote 2.2 enhancement : fields changes.
							// else if(c==49) // Local DR
							// {
							//
							// cell.setCellStyle(lockedCellStyle);
							// if(vo.getQuoteItem().getArdc()==null){}
							// else
							// {
							// if(vo.getQuoteItem().getArdc()==true)
							// {
							// cell.setCellValue("Yes");
							// }
							// else
							// {
							// cell.setCellValue("No");
							// }
							// }
							// }
							else if (c == 76 + offset) // design in cat
							{

								cell.setCellStyle(lockedCellStyle);
								if (vo.getQuoteItem().getDesignInCat() == null) {
								} else {
									cell.setCellValue(vo.getQuoteItem().getDesignInCat());
								}
							} else if (c == 77 + offset) // LOA
							{

								cell.setCellStyle(lockedCellStyle);
								if (vo.getQuoteItem().getLoaFlag() == null) {
								} else {
									if (vo.getQuoteItem().getLoaFlag() == true) {
										cell.setCellValue("Yes");
									} else {
										cell.setCellValue("No");
									}
								}
							}
							// else if(c==51) // Bid-To-Bid
							// {
							//
							// cell.setCellStyle(lockedCellStyle);
							// if(vo.getQuoteItem().getQuote().getBomFlag()==null){}
							// else
							// {
							// if(vo.getQuoteItem().getQuote().getBomFlag()==true)
							// {
							// cell.setCellValue("Yes");
							// }
							// else
							// {
							// cell.setCellValue("No");
							// }
							// }
							// }
							else if (c == 78 + offset) // Product Group 2
							{

								cell.setCellStyle(lockedCellStyle);
								if (vo.getQuoteItem().getProductGroup2() == null) {
								} else {
									cell.setCellValue(vo.getQuoteItem().getProductGroup2().getName());
								}
							} else if (c == 79 + offset) // Competitor Information
							{

								cell.setCellStyle(lockedCellStyle);
								if (vo.getQuoteItem().getCompetitorInformation() == null) {
								} else {
									cell.setCellValue(vo.getQuoteItem().getCompetitorInformation());
								}
							}
							// else if(c==53) // BMT Biz
							// {
							//
							// cell.setCellStyle(lockedCellStyle);
							// if(vo.getQuoteItem().getDesignLocation()==null){}
							// else
							// {
							// cell.setCellValue(vo.getQuoteItem().getDesignLocation());
							// }
							// }
							else if (c == 80 + offset) // Business Program Type
							{

								cell.setCellStyle(lockedCellStyle);
								if (vo.getQuoteItem().getQuote().getBmtCheckedFlag() == null) {
								} else {
									if (vo.getQuoteItem().getQuote().getBmtCheckedFlag() == true) {
										cell.setCellValue("Yes");
									} else {
										cell.setCellValue("No");
									}
								}
							} else if (c == 81 + offset) // Remarks
							{

								cell.setCellStyle(lockedCellStyle);
								if (vo.getQuoteItem().getQuote().getRemarks() == null) {
								} else {
									cell.setCellValue(vo.getQuoteItem().getQuote().getRemarks());
								}
							} else if (c == 82 + offset) // Item Remarks
							{

								cell.setCellStyle(lockedCellStyle);
								if (vo.getQuoteItem().getRemarks() == null) {
								} else {
									cell.setCellValue(vo.getQuoteItem().getRemarks());
								}
							} else if (c == 83 + offset) // Reason For Refresh
							{

								cell.setCellStyle(lockedCellStyle);
								if (vo.getQuoteItem().getReasonForRefresh() == null) {
								} else {
									cell.setCellValue(vo.getQuoteItem().getReasonForRefresh());
								}
							} else if (c == 84 + offset) // Segment
							{

								cell.setCellStyle(lockedCellStyle);
								if (vo.getQuoteItem().getEnquirySegment() == null) {
								} else {
									cell.setCellValue(vo.getQuoteItem().getEnquirySegment());
								}
							} else if (c == 85 + offset) // Ship To Party
							{

								cell.setCellStyle(lockedCellStyle);
								if (vo.getQuoteItem().getShipToCustomer() == null) {
								} else {
									cell.setCellValue(vo.getQuoteItem().getShipToCustomer().getCustomerFullName());
								}
							} else if (c == 86 + offset) // Requester Reference
							{

								cell.setCellStyle(lockedCellStyle);
								if (vo.getQuoteItem().getQuote().getYourReference() == null) {
								} else {
									cell.setCellValue(vo.getQuoteItem().getQuote().getYourReference());
								}
							} else if (c == 87 + offset) // RFQ Submission Date
							{

								cell.setCellStyle(lockedCellStyle);
								if (vo.getQuoteItem().getSubmissionDate() == null) {
								} else {
									Date date = vo.getQuoteItem().getSubmissionDate();
									SimpleDateFormat df2 = new SimpleDateFormat("dd/MM/yyyy");
									String dateText = df2.format(date);
									cell.setCellValue(dateText);
								}
							} else if (c == 88 + offset) // Sold To Code
							{

								if (vo.getQuoteItem().getQuote().getSoldToCustomer() == null) {
								} else {
									cell.setCellValue(
											vo.getQuoteItem().getQuote().getSoldToCustomer().getCustomerNumber());
								}
								cell.setCellStyle(lockedCellStyle);
							} else if (c == 89 + offset) // Ship To Code
							{

								cell.setCellStyle(lockedCellStyle);
								if (vo.getQuoteItem().getQuote().getShipToCustomer() == null) {
								} else {
									cell.setCellValue(
											vo.getQuoteItem().getQuote().getShipToCustomer().getCustomerNumber());
								}
							} else if (c == 90 + offset) // End Customer Code
							{

								cell.setCellStyle(lockedCellStyle);
								if (vo.getQuoteItem().getEndCustomer() == null) {
								} else {
									cell.setCellValue(vo.getQuoteItem().getEndCustomer().getCustomerNumber());
								}
							}
						} catch (NullPointerException e) {
							LOG.info("Data retrieved for row " + rowNum + ", col " + c + " returned null");
							LOG.log(Level.SEVERE, "Exception occured in downloading file:" + filePath
									+ " , Exception message: " + e.getMessage(), e);
						}
					}
				}
			}
			wb.write(outStream);

			byte[] data = outStream.toByteArray();
			ByteArrayInputStream istream = new ByteArrayInputStream(data);
			setDownloadRitDataExcel(new DefaultStreamedContent(istream,
					"application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
					"RITData_Template_Download.xlsx"));
			input.close();
			outStream.close();
		} catch (Exception e) {
			FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR,
					ResourceMB.getText("wq.message.downloadfail") + ".", "");
			FacesContext.getCurrentInstance().addMessage("growl", msg);
			LOG.log(Level.SEVERE, "Exception occured when downloading XLSX: ", e);
		}

	}

	/**
	 * @param sheet
	 * @param validRows
	 * @param rowToQuoteItemVos
	 */
	private void pmSearchForUpload(XSSFSheet sheet, ArrayList<Integer> validRows,
			Map<Integer, Integer> rowToQuoteItemVos) {
		// public void pmSearchForUpload(XSSFSheet sheet, ArrayList<Integer>
		// validRows){
		setupUserInfoToCriteria();
		setupBizUnit();

		List<String> attachmentTypes = new ArrayList<String>();
		attachmentTypes.add(QuoteSBConstant.ATTACHMENT_TYPE_QUOTE);
		attachmentTypes.add(QuoteSBConstant.ATTACHMENT_TYPE_QUOTE_ITEM);
		attachmentTypes.add(QuoteSBConstant.ATTACHMENT_TYPE_REFRESH);
		criteria.setAttachmentType(attachmentTypes);
		criteria.setAction(QuoteSBConstant.RESPONSEIT_DOWNLOAD_SEARCH_ACTION);
		// LOG.info(criteria.toString());

		quoteItemVos = myQuoteSearchSB.search(criteria, resourceMB.getResourceLocale());
		Collections.sort(quoteItemVos, new Comparator<QuoteItemVo>() {
			public int compare(QuoteItemVo arg0, QuoteItemVo arg1) {
				return arg0.getQuoteItem().getQuoteNumber().compareTo(arg1.getQuoteItem().getQuoteNumber());
			}
		});

		LOG.info("Start applying changes from excel");
		int i = 1;
		for (QuoteItemVo vo : quoteItemVos) {
			vo.setQuotedPartNumber(vo.getQuoteItem().getQuotedPartNumber());
			// vo.setShipmentValidity(vo.getQuoteItem().getShipmentValidityStr());
			// vo.setQuotationEffectiveDate(vo.getQuoteItem().getQuotationEffectiveDateStr());
			vo.setSeq(i++);
			// vo.setTermsAndConditionsStr(vo.getQuoteItem().getTermsAndConditions());
			// vo.setAqccStr(vo.getQuoteItem().getInternalTransferComment());
		}

		int cols = 0;
		XSSFRow row = null;
		XSSFCell cell = null;

		cols = sheet.getRow(0).getPhysicalNumberOfCells();
		int offset = 9;
		boolean buyCurrOrCostIndicatorChanged = false;
		for (int r = 0; r <= validRows.size(); r++) {
			row = sheet.getRow(r);
			if (validRows.contains(r)) {
				int quoteItemVosIndex = rowToQuoteItemVos.get(r);
				QuoteItemVo vo = quoteItemVos.get(quoteItemVosIndex);
				buyCurrOrCostIndicatorChanged = false;
				for (int c = 0; c < cols; c++) {
					cell = row.getCell((int) c);
					if(cell == null) continue;
					String cellValue = null;

					switch (cell.getCellType()) {
					case Cell.CELL_TYPE_BOOLEAN:
						cellValue = cell.getBooleanCellValue() ? QuoteConstant.OPTION_YES : QuoteConstant.OPTION_NO;
						break;
					case Cell.CELL_TYPE_NUMERIC:
						if (HSSFDateUtil.isCellDateFormatted(cell)) {
							Date date = HSSFDateUtil.getJavaDate(cell.getNumericCellValue());
							cellValue = simpleDateFormat.format(date);
						} else {
							cellValue = String.valueOf(cell.getNumericCellValue());
							cellValue = new BigDecimal(cellValue).stripTrailingZeros().toPlainString();
						}
						break;
					case Cell.CELL_TYPE_STRING:
						cellValue = cell.getStringCellValue().trim();
						break;
					case Cell.CELL_TYPE_BLANK:
						cellValue = null;
						break;
					}

					// if(cell!=null && !cell.toString().trim().isEmpty())
					// {
					if (c == 6)// sales cost type
					{
						vo.getQuoteItem().setSalesCostType(SalesCostType.GetSCTypeByStr(cellValue));
					}

					else if (c == 12) {
						vo.getQuoteItem().setCustomerGroupId(cellValue);
					}
					else if (c == 18 + 3) // Avnet Quote P/N
					{
						vo.setQuotedPartNumber(cellValue);
					}
					else if (c == 19 + 4) // Quote Margin
					{
						if (cellValue != null) {
							vo.getQuoteItem().setResaleMargin((Double.parseDouble(cellValue)));
						} else {
							vo.getQuoteItem().setResaleMargin(null);
						}
					}
					else if (c == 20 + 4) // Avnet Quoted Price
					{
						if (cellValue != null) {
							vo.getQuoteItem().setQuotedPrice(Double.parseDouble(cellValue));
						} else {
							vo.getQuoteItem().setQuotedPrice(null);
						}

					}

					else if (c == 21 + 4) // sales cost
					{
						if (cellValue != null) {
							vo.getQuoteItem().setSalesCost(new BigDecimal(cellValue));
						} else {
							vo.getQuoteItem().setSalesCost(null);
						}

					}

					else if (c == 22 + 4) // Suggested Resale
					{
						if (cellValue != null) {
							vo.getQuoteItem().setSuggestedResale(new BigDecimal(cellValue));
						} else {
							vo.getQuoteItem().setSuggestedResale(null);
						}

					}
					else if (c == 23 + 4) // Cost Indicator
					{
						if(!buyCurrOrCostIndicatorChanged) {
							buyCurrOrCostIndicatorChanged = QuoteUtil.isEqualsWithTrimAndIgnoreCase(cellValue, 
									vo.getQuoteItem().getCostIndicator());
						}
						
						vo.getQuoteItem().setCostIndicator(cellValue);
					}
					else if (c == 28) // buy curr
					{
						if(!buyCurrOrCostIndicatorChanged && vo.getQuoteItem().getBuyCurr() != Currency.valueOf(cellValue)) {
							buyCurrOrCostIndicatorChanged = true;
						}
						
						vo.getQuoteItem().setBuyCurr(Currency.valueOf(cellValue));
					}
					else if (c == 24 + 5) // Cost
					{
						if (cellValue != null) {
							vo.getQuoteItem().setCost(Double.parseDouble(cellValue));
						} else {
							vo.getQuoteItem().setCost(null);
						}
					}
					else if (c == 25 + 5)// Qty Indicator
					{
						vo.getQuoteItem().setQtyIndicator(cellValue);
					}
					else if (c == 26 + 5) // Avnet Quoted Qty
					{
						if (cellValue != null) {
							vo.getQuoteItem().setQuotedQty((int) Double.parseDouble(cellValue));
						} else {
							vo.getQuoteItem().setQuotedQty(null);
						}
					}
					else if (c == 44 + offset) // Multi Usage
					{
						if ("Yes".equalsIgnoreCase(cellValue)) {
							vo.getQuoteItem().setMultiUsageFlag(true);
						} else {
							vo.getQuoteItem().setMultiUsageFlag(false);
						}
					}
					else if (c == 47 + offset) // Lead Time (wks)
					{
						vo.getQuoteItem().setLeadTime(cellValue);
					}
					else if (c == 48 + offset) // MPQ
					{
						if (cellValue != null) {
							vo.getQuoteItem().setMpq((int) Double.parseDouble(cellValue));
						} else {
							vo.getQuoteItem().setMpq(null);
						}
					}
					else if (c == 49 + offset) // MOQ
					{
						if (cellValue != null) {
							vo.getQuoteItem().setMoq((int) Double.parseDouble(cellValue));
						} else {
							vo.getQuoteItem().setMoq(null);
						}
					}
					else if (c == 50 + offset) // MOV
					{
						if (cellValue != null) {
							vo.getQuoteItem().setMov((int) Double.parseDouble(cellValue));
						} else {
							vo.getQuoteItem().setMov(null);
						}
					}

					else if (c == 51 + offset) // Quotation effective date
					{
						if (cellValue != null) {
							SimpleDateFormat sdfSource = new SimpleDateFormat("dd-MMM-yyyy");
							SimpleDateFormat sdfDestination = new SimpleDateFormat("dd/MM/yyyy");
							String quotationEffectiveDateString = cellValue;
							Date date = null;
							try {
								date = sdfSource.parse(quotationEffectiveDateString);
							} catch (Exception e) {
								LOG.log(Level.WARNING, "Exception in parsing date : " + quotationEffectiveDateString
										+ " , exception message : " + e.getMessage());
								try {
									date = sdfDestination.parse(quotationEffectiveDateString);
								} catch (ParseException e1) {
									LOG.log(Level.WARNING, "Exception in parsing date : " + quotationEffectiveDateString
											+ " , exception message : " + e.getMessage());
									String errorMessage = ResourceMB.getParameterizedText(
											"wq.message.correctQuotationEffectiveDate", String.valueOf(r + 1));
									FacesContext.getCurrentInstance().addMessage(null,
											new FacesMessage(FacesMessage.SEVERITY_ERROR, errorMessage, ""));
									return;
								}
							}

							// quotationEffectiveDateString =
							// sdfDestination.format(date);
							// vo.setQuotationEffectiveDate(quotationEffectiveDateString);
							vo.getQuoteItem().setQuotationEffectiveDate(date);

						} else {
							// vo.setQuotationEffectiveDate(null);
							vo.getQuoteItem().setQuotationEffectiveDate(null);
						}
					}

					else if (c == 52 + offset) // Price Validity
					{
						vo.getQuoteItem().setPriceValidity(cellValue);
					}
					else if (c == 53 + offset) // Shipment Validity
					{
						if (cellValue != null) {
							SimpleDateFormat sdfSource = new SimpleDateFormat("dd-MMM-yyyy");
							SimpleDateFormat sdfDestination = new SimpleDateFormat("dd/MM/yyyy");
							String shipmentValidityString = cellValue;
							Date date = null;
							try {
								date = sdfSource.parse(shipmentValidityString);
							} catch (Exception e) {
								LOG.log(Level.WARNING, "Exception in parsing date : " + shipmentValidityString
										+ " , exception message : " + e.getMessage());
								try {
									date = sdfDestination.parse(shipmentValidityString);
								} catch (ParseException e1) {
									LOG.log(Level.WARNING, "Exception in parsing date : " + shipmentValidityString
											+ " , exception message : " + e.getMessage());
									String errorMessage = ResourceMB.getParameterizedText("wq.message.correctField",
											String.valueOf(r + 1));
									FacesContext.getCurrentInstance().addMessage(null,
											new FacesMessage(FacesMessage.SEVERITY_ERROR, errorMessage, ""));
									return;
								}
							}

							// shipmentValidityString =
							// sdfDestination.format(date);
							// vo.setShipmentValidity(shipmentValidityString);
							vo.getQuoteItem().setShipmentValidity(date);
						} else {
							// vo.setShipmentValidity(null);
							vo.getQuoteItem().setShipmentValidity(null);
						}
					}
					else if (c == 54 + offset) // MFR Debit #
					{
						vo.getQuoteItem().setVendorDebitNumber(cellValue);
					}
					else if (c == 55 + offset) // MFR Quote #
					{
						vo.getQuoteItem().setVendorQuoteNumber(cellValue);
					}
					else if (c == 56 + offset) // MFR Quote Qty
					{
						if (cellValue != null) {
							vo.getQuoteItem().setVendorQuoteQty((int) Double.parseDouble(cellValue));
						} else {
							vo.getQuoteItem().setVendorQuoteQty(null);
						}
					}
					else if (c == 57 + offset) // Rescheduling Window
					{
						if (cellValue != null) {
							vo.getQuoteItem().setRescheduleWindow((int) Double.parseDouble(cellValue));
						} else {
							vo.getQuoteItem().setRescheduleWindow(null);
						}
					}
					else if (c == 58 + offset) // Cancellation Window
					{
						if (cellValue != null) {
							vo.getQuoteItem().setCancellationWindow((int) Double.parseDouble(cellValue));
						} else {
							vo.getQuoteItem().setCancellationWindow(null);
						}
					}
					else if (c == 59 + offset) // Quotation T&C
					{
						vo.getQuoteItem().setTermsAndConditions(cellValue);
						// vo.setTermsAndConditionsStr(cellValue);
					}
					else if (c == 60 + offset) // Resale Indicator
					{
						vo.getQuoteItem().setResaleIndicator(cellValue);
					}
					else if (c == 38 + offset) // RFQ Item Attachment
					{
					}
					else if (c == 39 + offset) // RFQ Form Attachment
					{
					}
					else if (c == 64 + offset) // PM Comment
					{
						vo.getQuoteItem().setInternalTransferComment(cellValue);
						// vo.setAqccStr(cellValue);
					}
					else if (c == 65 + offset) // Allocation Part
					{
						if ("Yes".equalsIgnoreCase(cellValue)) {
							vo.getQuoteItem().setAllocationFlag(true);
						} else {
							vo.getQuoteItem().setAllocationFlag(false);
						}
					}
					else if (c == 67 + offset) // Reasons for Authorized GP% change
					{
						vo.getQuoteItem().setAuthGpReasonDesc(cellValue);
						vo.setAuthGpReasonDesc(cellValue);
					}
					else if (c == 68 + offset) // Remarks of Reason
					{
						vo.getQuoteItem().setAuthGpRemark(cellValue);
					}
					// }
				}
				// Update Quote Margin
				if (vo.getQuoteItem().getCost() != null && vo.getQuoteItem().getQuotedPrice() != null) {
					if (vo.getQuoteItem().getCost() != 0 && vo.getQuoteItem().getQuotedPrice() != 0) {
						double cost = vo.getQuoteItem().getCost();
						double quotedPrice = vo.getQuoteItem().getQuotedPrice();
						vo.getQuoteItem().setResaleMargin((quotedPrice - cost) * (100 / quotedPrice));
					} else if (vo.getQuoteItem().getCost() == 0 && vo.getQuoteItem().getQuotedPrice() > 0) {
						vo.getQuoteItem().setResaleMargin(100.0);
					} else if (vo.getQuoteItem().getCost() > 0 && vo.getQuoteItem().getQuotedPrice() == 0) {
						// Edited by request 15/11/2013
						// vo.getQuoteItem().setResaleMargin(Double.NEGATIVE_INFINITY);
						vo.getQuoteItem().setResaleMargin(null);
					}
				} else {
					vo.getQuoteItem().setResaleMargin(null);
				}
				adjustAfterUpload(vo.getQuoteItem(), buyCurrOrCostIndicatorChanged);
			}
		}
		

		LOG.info("Finished applying changes from excel");

		selectedQuoteItemVos = null;

		removedAttachments = new HashMap<Long, List<Attachment>>();

		selectedQuoteItemVo = null;

		// Datatable takes data from quoteItemVo ,but in render response phase
		// quoteItemVo
		// will be updated so we have to restrict update quoteItemVo
		setCacheModificationFlag(true);

		// clear filter after upload add by Lenon(2016-09-14)
		final DataTable d = (DataTable) FacesContext.getCurrentInstance().getViewRoot()
				.findComponent(":form:datatable_response_internal_transfer");
		if (d != null && !d.getFilters().isEmpty()) {
			d.reset();
			filteredQuoteItemVos = null;
		}

	}
	
	private void adjustAfterUpload(QuoteItem quoteItem, boolean buyCurrOrCostIndicatorChanged) {
		if (buyCurrOrCostIndicatorChanged) {
			quoteSB.updateByBuyCurrChanged(quoteItem);
		} else {
			quoteItem.reCalMargin();
		}
	}

	public void setupExcelDropDownList(XSSFSheet sheet, XSSFWorkbook wb) {
		// Cost Indicator Excel dropdown list
		XSSFSheet hidden = wb.createSheet("hidden");
		for (int i = 0; i < costIndicators.size(); i++) {
			String costIndicator = costIndicators.get(i);
			XSSFRow hiddenRow = hidden.createRow(i);
			XSSFCell hiddenCell = hiddenRow.createCell(0);
			hiddenCell.setCellValue(costIndicator);
		}

		Name namedCell = wb.createName();
		namedCell.setNameName("hidden");
		namedCell.setRefersToFormula("hidden!$A$1:$A$" + costIndicators.size());
		DataValidationHelper validationHelper = new XSSFDataValidationHelper(sheet);
		DataValidationConstraint constraint = validationHelper.createFormulaListConstraint("hidden");
		// firstCol and lastCol should be be 23,updated by DamonChen
		CellRangeAddressList addressList = new CellRangeAddressList(1, 502, 23, 23);
		DataValidation validation = validationHelper.createValidation(constraint, addressList);
		wb.setSheetHidden(3, true);
		sheet.addValidationData(validation);

		// PM Comment dropdown list
		XSSFSheet hidden1 = wb.createSheet("hidden1");
		for (int i = 0; i < avnetQuoteCentreComments.size(); i++) {
			String pmComment = avnetQuoteCentreComments.get(i);
			XSSFRow hiddenRow1 = hidden1.createRow(i);
			XSSFCell hiddenCell1 = hiddenRow1.createCell(0);
			hiddenCell1.setCellValue(pmComment);
		}

		Name namedCell1 = wb.createName();
		namedCell1.setNameName("hidden1");
		namedCell1.setRefersToFormula("hidden1!$A$1:$A$" + avnetQuoteCentreComments.size());
		DataValidationHelper validationHelper1 = new XSSFDataValidationHelper(sheet);
		DataValidationConstraint constraint1 = validationHelper1.createFormulaListConstraint("hidden1");
		CellRangeAddressList addressList1 = new CellRangeAddressList(1, 502, 64, 64);
		DataValidation validation1 = validationHelper1.createValidation(constraint1, addressList1);
		validation1.setShowErrorBox(false);
		wb.setSheetHidden(4, true);
		sheet.addValidationData(validation1);
		//org.dom4j.DocumentException
		// Allocation Part Excel dropdown list
		// DataValidationHelper validationHelper2 = new
		// XSSFDataValidationHelper(
		// sheet);
		// CellRangeAddressList addressList2 = new CellRangeAddressList(1, 502,
		// 41, 41);
		// DataValidationConstraint constraint2 = validationHelper2
		// .createExplicitListConstraint(new String[] { "-SELECT-", "Yes",
		// "No" });
		// DataValidation dataValidation2 = validationHelper2.createValidation(
		// constraint2, addressList2);
		// sheet.addValidationData(dataValidation2);

		// Multi Usage dropdown list
		// DataValidationHelper validationHelper3 = new
		// XSSFDataValidationHelper(
		// sheet);
		// CellRangeAddressList addressList3 = new CellRangeAddressList(1, 502,
		// 22, 22);
		// DataValidationConstraint constraint3 = validationHelper3
		// .createExplicitListConstraint(new String[] { "-SELECT-", "Yes",
		// "No" });
		// DataValidation dataValidation3 = validationHelper3.createValidation(
		// constraint3, addressList3);
		// sheet.addValidationData(dataValidation3);

		// Qty Indicator dropdown list
		String[] qtyIndicatorArray = new String[qtyIndicators.size()];
		for (int i = 0; i < qtyIndicators.size(); i++) {
			qtyIndicatorArray[i] = qtyIndicators.get(i);
		}

		DataValidationHelper validationHelper4 = new XSSFDataValidationHelper(sheet);
		CellRangeAddressList addressList4 = new CellRangeAddressList(1, 502, 25, 25);
		DataValidationConstraint constraint4 = validationHelper4.createExplicitListConstraint(qtyIndicatorArray);
		DataValidation dataValidation4 = validationHelper4.createValidation(constraint4, addressList4);
		dataValidation4.setShowErrorBox(false);
		sheet.addValidationData(dataValidation4);

		// Terms and Conditions dropdown list
		XSSFSheet hidden2 = wb.createSheet("hidden2");
		for (int i = 0; i < termsAndConditions.size(); i++) {
			String termAndCondition = termsAndConditions.get(i);
			XSSFRow hiddenRow2 = hidden2.createRow(i);
			XSSFCell hiddenCell2 = hiddenRow2.createCell(0);
			hiddenCell2.setCellValue(termAndCondition);
		}
		if (termsAndConditions != null && termsAndConditions.size() > 0) {
			Name namedCell2 = wb.createName();
			namedCell2.setNameName("hidden2");
			namedCell2.setRefersToFormula("hidden2!$A$1:$A$" + termsAndConditions.size());
			DataValidationHelper validationHelper5 = new XSSFDataValidationHelper(sheet);
			DataValidationConstraint constraint5 = validationHelper5.createFormulaListConstraint("hidden2");
			CellRangeAddressList addressList5 = new CellRangeAddressList(1, 502, 59, 59);
			DataValidation validation5 = validationHelper5.createValidation(constraint5, addressList5);
			validation5.setShowErrorBox(false);
			wb.setSheetHidden(5, true);
			sheet.addValidationData(validation5);
		}
	}

	// NEC Pagination Changes: Modified for providing lazy loading
	public void pmSearch() {
		this.quoteItemVo.getCacheModifiedItems().clear();
		allowAutoSearch = false;
		criteria.setupUIInCriteria();
		setupUserInfoToCriteria();
		setupBizUnit();
		List<String> attachmentTypes = new ArrayList<String>();
		attachmentTypes.add(QuoteSBConstant.ATTACHMENT_TYPE_QUOTE);
		attachmentTypes.add(QuoteSBConstant.ATTACHMENT_TYPE_QUOTE_ITEM);
		attachmentTypes.add(QuoteSBConstant.ATTACHMENT_TYPE_REFRESH);
		criteria.setAttachmentType(attachmentTypes);
		// LOG.info(criteria.toString());

		if (criteria.getsQuotedPartNumber() != null && criteria.getsQuotedPartNumber().length() > 0
				&& criteria.getsQuotedPartNumber().length() < 3) {
			FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR,
					ResourceMB.getText("wq.message.minLenError"), "");
			FacesContext.getCurrentInstance().addMessage("growl", msg);
			return;
		}

		if (criteria.getsCustomerName() != null && criteria.getsCustomerName().length() > 0
				&& criteria.getsCustomerName().length() < 3) {
			FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR,
					ResourceMB.getText("wq.message.cutmrLenError"), "");
			FacesContext.getCurrentInstance().addMessage("growl", msg);
			return;
		}
		if (criteria.getPageSentOutDateFrom() != null && criteria.getPageSentOutDateFrom() != null
				&& criteria.getPageSentOutDateFrom().after(criteria.getPageSentOutDateTo())) {
			FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR,
					ResourceMB.getText("wq.message.quoteRelDateMsg"), "");
			FacesContext.getCurrentInstance().addMessage("growl", msg);
			return;
		}
		// quoteItemVos =
		// myQuoteSearchSB.search(criteria,resourceMB.getResourceLocale() );

		// NEC Pagination changes: Set datatable's first record value
		final DataTable d = (DataTable) FacesContext.getCurrentInstance().getViewRoot()
				.findComponent("form:datatable_response_internal_transfer");
		int first = 0;
		if (null != d) {
			d.setFirst(first);
		}

		// NEC Pagination changes: initialize the Lazy Criteria data model for
		// supporting the pagination lazy loading
		quoteItemVo.startPagination();

		selectedQuoteItemVos = new ArrayList<>();

		filteredQuoteItemVos = new ArrayList<>();

		removedAttachments = new HashMap<Long, List<Attachment>>();

	}

	/*
	 * public void onCellEdit(CellEditEvent event) {
	 * 
	 * if( event.getColumn().getHeaderText().equalsIgnoreCase(
	 * COLUMN_HEADER_SHIPMENT_VALIDITY)){ shipmentValidityCheck(event); return;
	 * } }
	 */

	/*
	 * 
	 * private void shipmentValidityCheck(CellEditEvent event){
	 * 
	 * int index = event.getRowIndex();
	 * 
	 * QuoteItemVo vo = quoteItemVos.get(index);
	 * 
	 * if(StringUtils.isEmpty(vo.getShipmentValidity())){
	 * vo.getQuoteItem().setShipmentValidity(null); return; }
	 * 
	 * Object newValue = event.getNewValue();
	 * 
	 * try { Date date = SDF.parse(vo.getShipmentValidity());
	 * vo.getQuoteItem().setShipmentValidity(date); } catch (ParseException e) {
	 * LOG.log(Level.WARNING,
	 * "EXception in parsing date for shipment validity : "+vo.
	 * getShipmentValidity()+" , exception message : "+e.getMessage());
	 * FacesContext.getCurrentInstance().addMessage("growl", new
	 * FacesMessage(FacesMessage.SEVERITY_WARN,
	 * ResourceMB.getText("wq.message.invalidShipmentValidity")+": " + newValue
	 * , "")); } }
	 */

	public void handleFileUpload() {
		if (ritDataExcel != null) {
			long uploadfileSizeLimit = systemCodeMaintenanceSB.getUploadFileSizeLimit();

			if (ritDataExcel.getSize() > uploadfileSizeLimit) {

				String errorMessage = ResourceMB.getParameterizedString("wq.error.fileSizeError",
						Long.toString(uploadfileSizeLimit));
				FacesContext.getCurrentInstance().addMessage("growl",
						new FacesMessage(FacesMessage.SEVERITY_WARN, errorMessage, ""));
				return;
			}

			quoteItemVos.clear();
			upload(ritDataExcel);

			// save
			if (quoteItemVos.size() > 0) {
				this.quoteItemVo.getCacheModifiedItems().clear();
				this.quoteItemVo.getCacheModifiedItems().addAll(quoteItemVos);
				save();
				this.quoteItemVo.getCacheModifiedItems().clear();
			}

		} else {
			FacesContext.getCurrentInstance().addMessage("growl",
					new FacesMessage(FacesMessage.SEVERITY_ERROR, ResourceMB.getText("wq.message.selFile"), ""));
			return;
		}
	}

	public void save() {
		// NEC Pagination changes: get the selected data from cache
		if (selectedQuoteItemVos == null) {
			selectedQuoteItemVos = new ArrayList<QuoteItemVo>();
		} else {
			selectedQuoteItemVos.clear();
		}

		// selectedQuoteItemVos.addAll(getCacheSelectionDatas());
		// save all
		selectedQuoteItemVos.addAll(this.quoteItemVo.getCacheModifiedItems());
		if (selectedQuoteItemVos.size() == 0) {
			// FacesContext.getCurrentInstance().addMessage("growl", new
			// FacesMessage(FacesMessage.SEVERITY_ERROR,ResourceMB.getText("wq.message.noRecordSelected")
			// +"!", ""));
			FacesContext.getCurrentInstance().addMessage("growl", new FacesMessage(FacesMessage.SEVERITY_ERROR,
					ResourceMB.getText("wq.message.noDataChanged") + "!", ""));
			return;
		}

		/*
		 * List<Integer> errorRows = shipmentValidityCheck();
		 * if(errorRows.size() != 0){ String errMessage =
		 * ResourceMB.getText("wq.message.shipmentValidityFormat")+".<br/>" +
		 * convertErrorRows(errorRows);
		 * FacesContext.getCurrentInstance().addMessage("growl", new
		 * FacesMessage(FacesMessage.SEVERITY_ERROR, errMessage, "")); return ;
		 * }
		 * 
		 * List<Integer> errorRow2 = quotationEffectiveDateCheck();
		 * if(errorRow2.size() != 0){ String errMessage =
		 * ResourceMB.getText("wq.message.QuotationEffectiveDateFormat")+
		 * ".<br/>" + convertErrorRows(errorRows);
		 * FacesContext.getCurrentInstance().addMessage("growl", new
		 * FacesMessage(FacesMessage.SEVERITY_ERROR, errMessage, "")); return ;
		 * }
		 */

		if (!validateSalesCostData() || !validateBeforSave(selectedQuoteItemVos)) {
			return;
		}
		;

		User user = UserInfo.getUser();

		try {
			responseInternalTransferSB.saveQuoteItemsInInternalTransfer(selectedQuoteItemVos,
					getSelectedRemovedAttachments(), user);

		} catch (OptimisticLockException oe) {
			String message = getOptimisticLockMessage(oe);
			LOG.log(Level.WARNING, "Save RFQ Failed");
			FacesContext.getCurrentInstance().addMessage("growl",
					new FacesMessage(FacesMessage.SEVERITY_ERROR, message, ""));
			return;
		} catch (Exception e) {
			LOG.log(Level.WARNING, "Save RFQ Failed");
			FacesContext.getCurrentInstance().addMessage("growl",
					new FacesMessage(FacesMessage.SEVERITY_ERROR, e.getMessage(), ""));
			return;
		}

		FacesContext.getCurrentInstance().addMessage("growl",
				new FacesMessage(FacesMessage.SEVERITY_INFO, ResourceMB.getText("wq.message.rfqSaved"), ""));

		LOG.info(user.getEmployeeId() + " saved quote item(s) " + getQuoteNumbers(this.selectedQuoteItemVos));
		pmSearch();
	}

	/**
	 * @return @Description: validate fields for sales Cost project @author
	 *         042659 @return void @throws
	 */
	private boolean validateSalesCostData() {
		// NEC Pagination changes: get the selected data from cache
		// selectedQuoteItemVos.clear();
		// selectedQuoteItemVos.addAll(getCacheSelectionDatas());
		for (QuoteItemVo vo : selectedQuoteItemVos) {
			String errMessage = null;
			if (!vo.getQuoteItem().isSalesCostFlag() == true) {
				if (vo.getQuoteItem().getSalesCost() != null
						&& vo.getQuoteItem().getSalesCost().compareTo(BigDecimal.ZERO) > 0) {

					// errMessage =
					// ResourceMB.getText("wq.message.salesCostNotAllowed") +
					// "[" + vo.getSeq() + "]";
					errMessage = ResourceMB.getParameterizedText("wq.message.salesCostNotAllowedAtRow",
							"" + vo.getSeq());
					FacesContext.getCurrentInstance().addMessage("growl",
							new FacesMessage(FacesMessage.SEVERITY_ERROR, errMessage, ""));
					return false;
				}

				if (vo.getQuoteItem().getSuggestedResale() != null
						&& vo.getQuoteItem().getSuggestedResale().compareTo(BigDecimal.ZERO) > 0) {

					// errMessage =
					// ResourceMB.getText("wq.message.suggestedResaleNotAllowed")
					// + "[" + vo.getSeq() + "]";

					errMessage = ResourceMB.getParameterizedText("wq.message.suggestedResaleNotAllowedAtRow",
							"" + vo.getSeq());
					FacesContext.getCurrentInstance().addMessage("growl",
							new FacesMessage(FacesMessage.SEVERITY_ERROR, errMessage, ""));
					return false;
				}

			}
			if (!StringUtils.isEmpty(vo.getQuoteItem().getCustomerGroupId())) {
				if (sapDpaCustSB.findSapDpaCustByCustGroupId(vo.getQuoteItem().getCustomerGroupId()) == null) {
					// errMessage =
					// ResourceMB.getText("wq.message.customerGroupInvalid") +
					// "[" + vo.getSeq() + "]";

					errMessage = ResourceMB.getParameterizedText("wq.message.customerGroupInvalidAtRow",
							"" + vo.getSeq());
					FacesContext.getCurrentInstance().addMessage("growl",
							new FacesMessage(FacesMessage.SEVERITY_ERROR, errMessage, ""));
					return false;
				}

				if (quoteItemVo != null) {
					if (!sapDpaCustSB.isValidCustomerGroup(vo.getQuoteItem().getCustomerGroupId(),
							vo.getQuoteItem().getQuote().getBizUnit().getName(),
							vo.getQuoteItem().getSoldToCustomer().getCustomerNumber(),
							vo.getQuoteItem().getEndCustomer() == null ? null
									: vo.getQuoteItem().getEndCustomer().getCustomerNumber())) {
						errMessage = ResourceMB.getParameterizedText("wq.message.customerGroupNotMatchedAtRow",
								"" + vo.getSeq());
						FacesContext.getCurrentInstance().addMessage("growl",
								new FacesMessage(FacesMessage.SEVERITY_ERROR, errMessage, ""));
						return false;
					}
				}
			}

			if (null != vo.getQuoteItem().getSalesCostType()
					&& !QuoteUtil.isEmpty(vo.getQuoteItem().getSalesCostType().name())
					&& !QuoteConstant.SALES_COST_TYPE_NONSC.equals(vo.getQuoteItem().getSalesCostType().name())) {
				if ((!QuoteConstant.SALES_COST_TYPE_ZINC.equals(vo.getQuoteItem().getSalesCostType().name()))
						&& (!QuoteConstant.SALES_COST_TYPE_ZIND.equals(vo.getQuoteItem().getSalesCostType().name()))) {
					// errMessage =
					// ResourceMB.getText("wq.message.salesCostTypeInvalidAtRow")+"["
					// + vo.getSeq() + "]";
					errMessage = ResourceMB.getParameterizedText("wq.message.salesCostTypeInvalidAtRow",
							"" + vo.getSeq());
					FacesContext.getCurrentInstance().addMessage("growl",
							new FacesMessage(FacesMessage.SEVERITY_ERROR, errMessage, ""));
					return false;
				}

			}
		}
		return true;

	}
	private boolean validateBeforSave(List<QuoteItemVo> selectedQuoteItemVos) {
		if(selectedQuoteItemVos == null) return true;
		StringBuilder errMessage = new StringBuilder();
		for(QuoteItemVo vo : selectedQuoteItemVos) {
			String priceValidity = vo.getQuoteItem().getPriceValidity();
			if (!QuoteUtil.isEmpty(priceValidity)) {
				if(priceValidity.contains("/")) {
					if (!QuoteUtil.isValidDate(priceValidity)) { 
						errMessage.append(ResourceMB.getParameterizedText("wq.label.priceValidityAt",
								priceValidity) + " " + vo.getQuoteItem().getQuoteNumber() + "<br/>");
					}
				}else {
					try {
						int priceValidityNumber = Integer.parseInt(priceValidity);
						LOG.log(Level.INFO, "" + priceValidityNumber);
						int priceValidityMax = Integer
								.parseInt(sysConfigSB.getProperyValue(QuoteConstant.PRICE_VALIDITY_NUM_MAX));
						LOG.log(Level.INFO, "" + priceValidityMax);
						if (priceValidityNumber > priceValidityMax) {

							LOG.log(Level.WARNING, "item price validity: " + priceValidity + ", "
									+ ">  priceValidityMax:" + priceValidityMax);
							
							errMessage.append(ResourceMB.getParameterizedText("wq.message.PriceValidityOutLimitAt",
									priceValidity) + " " + vo.getQuoteItem().getQuoteNumber() + "<br/>");

						}
					} catch (Exception e) {
						LOG.log(Level.WARNING, "item price validity: " + priceValidity + ", "
								+ "Can not convert to number.");
						errMessage.append(ResourceMB.getParameterizedText("wq.label.priceValidityAt",
								priceValidity) + " " + vo.getQuoteItem().getQuoteNumber() + "<br/>");
					}
				}
			}
		} 
		if(errMessage.length()>0) {
			FacesContext.getCurrentInstance().addMessage("growl",
					new FacesMessage(FacesMessage.SEVERITY_ERROR, 
							ResourceMB.getText("wq.msg.invalidFormat") + " :<br/>" + errMessage.toString(), ""));
			return false;
		}
		return true;
	}
	
	public void sendToQC(ActionEvent event) {

		RequestContext context = RequestContext.getCurrentInstance();
		User user = UserInfo.getUser();
		// Bryan Start
		// Added by Lenon(043044) 2016/11/30
		// DrmsValidationUpdateStrategy drmsValidationUpdateStrategy =
		// DrmsValidationUpdateStrategyFactory.getInstance()
		// .getDrmsValidationUpdateStrategy(user.getDefaultBizUnit().getName());
		DrmsValidationUpdateStrategy drmsValidationUpdateStrategy = cacheUtil
				.getDrmsValidationUpdateStrategy(user.getDefaultBizUnit().getName());
		// Bryan End

		// sendToQCConfirmed is true only when sending from the confirmation
		// dialog
		boolean sendToQCConfirmed = Boolean
				.parseBoolean((String) event.getComponent().getAttributes().get("sendToQCConfirmed"));
		// NEC Pagination changes: get the selected data from cache
		selectedQuoteItemVos.clear();
		selectedQuoteItemVos.addAll(getCacheSelectionDatas());
		if (sendToQCConfirmed == false) {
			if (selectedQuoteItemVos.size() == 0) {
				context.addCallbackParam("sendToQCCheckingPassed", "1");
				FacesContext.getCurrentInstance().addMessage("growl", new FacesMessage(FacesMessage.SEVERITY_ERROR,
						ResourceMB.getText("wq.message.noRecordSelected") + "!", ""));
				return;
			}

			List<Integer> errorRows = mandatoryFieldChecking();

			if (errorRows.size() != 0) {
				context.addCallbackParam("sendToQCCheckingPassed", "1");
				String errMessage = ResourceMB.getText("wq.message.itemsMandatoryRules") + ".<br/>"
						+ convertErrorRows(errorRows);
				FacesContext.getCurrentInstance().addMessage("growl",
						new FacesMessage(FacesMessage.SEVERITY_ERROR, errMessage, ""));
				return;
			}

			errorRows = vendorFieldChecking();
			if (errorRows.size() != 0) {
				context.addCallbackParam("sendToQCCheckingPassed", "1");
				String errMessage = ResourceMB.getText("wq.message.MFRCannotBeEmpty") + ".<br/>"
						+ convertErrorRows(errorRows);
				FacesContext.getCurrentInstance().addMessage("growl",
						new FacesMessage(FacesMessage.SEVERITY_ERROR, errMessage, ""));
				return;
			}

			/*
			 * errorRows = shipmentValidityCheck(); if(errorRows.size() != 0){
			 * context.addCallbackParam("sendToQCCheckingPassed", "1"); String
			 * errMessage =
			 * ResourceMB.getText("wq.message.shipmentValidityFormat")+".<br/>"
			 * + convertErrorRows(errorRows);
			 * FacesContext.getCurrentInstance().addMessage("growl", new
			 * FacesMessage(FacesMessage.SEVERITY_ERROR, errMessage, ""));
			 * return ; }
			 * 
			 * errorRows = quotationEffectiveDateCheck(); if(errorRows.size() !=
			 * 0){ context.addCallbackParam("sendToQCCheckingPassed", "1");
			 * String errMessage =
			 * ResourceMB.getText("wq.message.QuotationEffectiveDateFormat")+
			 * ".<br/>" + convertErrorRows(errorRows);
			 * FacesContext.getCurrentInstance().addMessage("growl", new
			 * FacesMessage(FacesMessage.SEVERITY_ERROR, errMessage, ""));
			 * return ; }
			 */

			errorRows = leadTimeChecking();
			if (errorRows.size() != 0) {
				context.addCallbackParam("sendToQCCheckingPassed", "1");
				String errMessage = ResourceMB.getText("wq.message.leadTimeEmpty") + ".<br/>"
						+ convertErrorRows(errorRows);
				FacesContext.getCurrentInstance().addMessage("growl",
						new FacesMessage(FacesMessage.SEVERITY_ERROR, errMessage, ""));
				return;
			}

			errorRows = quotedMaterialChecking();
			if (errorRows.size() != 0) {
				context.addCallbackParam("sendToQCCheckingPassed", "1");
				String errMessage = ResourceMB.getText("wq.message.avnetQuotedEmpty") + ".<br/>"
						+ convertErrorRows(errorRows);
				FacesContext.getCurrentInstance().addMessage("growl",
						new FacesMessage(FacesMessage.SEVERITY_ERROR, errMessage, ""));
				return;
			}

			errorRows = priceValidityChecking();
			if (errorRows.size() != 0) {
				context.addCallbackParam("sendToQCCheckingPassed", "1");
				String errMessage = ResourceMB.getText("wq.message.priceValidityFormat") + ".<br/>"
						+ convertErrorRows(errorRows);
				FacesContext.getCurrentInstance().addMessage("growl",
						new FacesMessage(FacesMessage.SEVERITY_ERROR, errMessage, ""));
				return;
			}

			errorRows = quotedPriceToTargetPriceCheck();
			if (errorRows.size() != 0) {
				context.addCallbackParam("sendToQCCheckingPassed", "1");
				String errMessage = ResourceMB.getText("wq.message.avnetQuotedTargetPrice") + ".<br/>"
						+ convertErrorRows(errorRows);
				FacesContext.getCurrentInstance().addMessage("growl",
						new FacesMessage(FacesMessage.SEVERITY_ERROR, errMessage, ""));
				return;
			}

			errorRows = priceValidtyToTodayCheck();
			if (errorRows.size() != 0) {
				context.addCallbackParam("sendToQCCheckingPassed", "1");
				String errMessage = ResourceMB.getText("wq.message.priceValidityPastDue") + ".<br/>"
						+ convertErrorRows(errorRows) + "<br/>";
				FacesContext.getCurrentInstance().addMessage("growl",
						new FacesMessage(FacesMessage.SEVERITY_ERROR, errMessage, ""));
				return;
			}

			errorRows = quotedQtyChecking();
			if (errorRows.size() != 0) {
				context.addCallbackParam("sendToQCCheckingPassed", "1");
				String errMessage = ResourceMB.getText("wq.message.qtyIndicatorEmpty") + ".<br/>"
						+ convertErrorRows(errorRows) + "<br/>";
				FacesContext.getCurrentInstance().addMessage("growl",
						new FacesMessage(FacesMessage.SEVERITY_ERROR, errMessage, ""));
				return;
			}

			for (QuoteItemVo vo : selectedQuoteItemVos) {
				if (!StringUtils.isEmpty(vo.getAuthGpReasonDesc())) {
					for (DrmsAgpRea drmsAgpReason : drmsAgpReasons) {
						if (drmsAgpReason.getAuthGpChgDesc().equals(vo.getAuthGpReasonDesc())) {
							vo.setAuthGpReasonCode(drmsAgpReason.getAuthGpChgRea());
						}
					}
				}
			}

			boolean validateAGP = drmsValidationUpdateStrategy.isValidationAGP();
			if (validateAGP) {
				List<String> errorQuoteNumbers = agpChecking();
				if (errorQuoteNumbers.size() != 0) {
					context.addCallbackParam("sendToQCCheckingPassed", "1");
					String errMessage = ResourceMB.getText("wq.message.GPQuoteMargin") + ".<br/>" + errorQuoteNumbers
							+ "<br/>";
					FacesContext.getCurrentInstance().addMessage("growl",
							new FacesMessage(FacesMessage.SEVERITY_ERROR, errMessage, ""));
					return;
				}
			}

			// show the error msg in confirmation dialog as the error is only a
			// hint, it will stop user continue.
			errorRows = quotedPriceToCostCheck();
			if (errorRows.size() != 0) {
				context.addCallbackParam("sendToQCCheckingPassed", "2");
				String errMessage = ResourceMB.getText("wq.message.avnetQuotedPriceLower") + "?<br/>"
						+ convertErrorRows(errorRows) + "<br/>";
				FacesContext.getCurrentInstance().addMessage("sendToQCCheckingMsg",
						new FacesMessage(FacesMessage.SEVERITY_ERROR, errMessage, ""));
				return;
			}

			// fix ticket : INC0018818 June 2014/05/26
			errorRows = remarksOfReasonChecking();
			if (errorRows.size() != 0) {
				context.addCallbackParam("sendToQCCheckingPassed", "1");
				String errMessage = ResourceMB.getText("wq.message.AGPremark") + "<br/>" + convertErrorRows(errorRows)
						+ "<br/>";
				FacesContext.getCurrentInstance().addMessage("growl",
						new FacesMessage(FacesMessage.SEVERITY_ERROR, errMessage, ""));
				return;
			}

			// fix ticket : INC0018818 June 2014/06/18
			errorRows = pmCommentsChecking();
			if (errorRows.size() != 0) {
				context.addCallbackParam("sendToQCCheckingPassed", "1");
				String errMessage = ResourceMB.getText("wq.message.PMCommentExceed") + "<br/>"
						+ convertErrorRows(errorRows) + "<br/>";
				FacesContext.getCurrentInstance().addMessage("growl",
						new FacesMessage(FacesMessage.SEVERITY_ERROR, errMessage, ""));
				return;
			}

			errorRows = CheckSalesCostBelowCostAndPMCommentBlank();
			if (errorRows.size() != 0) {
				context.addCallbackParam("sendToQCCheckingPassed", "1");
				String errMessage = ResourceMB.getText("wq.message.PMCommentBlank") + "<br/>"
						+ convertErrorRows(errorRows) + "<br/>";
				FacesContext.getCurrentInstance().addMessage("growl",
						new FacesMessage(FacesMessage.SEVERITY_ERROR, errMessage, ""));
				return;
			}
		}

		try {
			drmsValidationUpdateStrategy.updateAuthGpReasonInEntity(selectedQuoteItemVos);
			// quotedQtyCalculate();
			responseInternalTransferSB.sendInternalTransferToQC(selectedQuoteItemVos, getSelectedRemovedAttachments(),
					user);

		} catch (Exception e) {

			if (e.getMessage().contains("OptimisticLockException")) {
				String message = getOptimisticLockMessage(e);
				LOG.log(Level.WARNING, "Send To QC Failed", e);
				FacesContext.getCurrentInstance().addMessage("growl",
						new FacesMessage(FacesMessage.SEVERITY_ERROR, message, ""));
				return;
			}

			LOG.log(Level.WARNING, "Send To QC Failed", e);
			FacesContext.getCurrentInstance().addMessage("growl",
					new FacesMessage(FacesMessage.SEVERITY_ERROR, e.getMessage(), ""));
			return;

		}

		FacesContext.getCurrentInstance().addMessage("growl",
				new FacesMessage(FacesMessage.SEVERITY_INFO, ResourceMB.getText("wq.message.rfqSentQC"), ""));

		LOG.info(user.getEmployeeId() + " sent quote item(s) " + getSelectedQuoteNubmers() + " to QC");

		pmSearch();

	}

	/**
	 * @Description: TODO @return List<Integer> @throws
	 */
	private List<Integer> CheckSalesCostBelowCostAndPMCommentBlank() {
		List<Integer> errorRows = new ArrayList<Integer>();
		// NEC Pagination changes: get the selected data from cache
		selectedQuoteItemVos.clear();
		selectedQuoteItemVos.addAll(getCacheSelectionDatas());
		for (QuoteItemVo vo : selectedQuoteItemVos) {

			if (isMatchedSalesCostQuoteItem(vo.getQuoteItem())) {
				if (vo.getQuoteItem().getCost() != null) {
					if (vo.getQuoteItem().getSalesCost() == null || vo.getQuoteItem().getSalesCost()
							.compareTo(BigDecimal.valueOf(vo.getQuoteItem().getCost())) < 0) {

						if (StringUtils.isEmpty(vo.getQuoteItem().getInternalTransferComment())) {
							vo.setErrRow(true);
							errorRows.add(vo.getSeq());
						}
					}
				}
			}
		}
		return errorRows;
	}

	// fix ticket issue : INC0018818 June 2014/05/26
	/**
	 * to check the remark of reason exceed 60 char or not
	 * 
	 * @author 916138
	 * 
	 * @return
	 */
	private List<Integer> remarksOfReasonChecking() {

		List<Integer> errorRows = new ArrayList<Integer>();
		// NEC Pagination changes: get the selected data from cache
		selectedQuoteItemVos.clear();
		selectedQuoteItemVos.addAll(getCacheSelectionDatas());
		for (QuoteItemVo vo : selectedQuoteItemVos) {
			QuoteItem item = vo.getQuoteItem();

			if (!StringUtils.isEmpty(item.getAuthGpRemark())) {
				if (item.getAuthGpRemark().length() > 60) {
					vo.setErrRow(true);
					errorRows.add(vo.getSeq());
				}
			}
		}

		return errorRows;
	}

	// fix ticket issue : INC0018818 June 2014/06/18
	/**
	 * The error user mentioned is on "PM Comment" fields, which is 1024 byte
	 * and it is quite long. But end user still has chance to enter text
	 * exceeding it. Please add a check on this field and prompt user when it
	 * exceeds the length.
	 * 
	 * @author 916138
	 * 
	 * @return
	 */
	private List<Integer> pmCommentsChecking() {

		List<Integer> errorRows = new ArrayList<Integer>();
		// NEC Pagination changes: get the selected data from cache
		selectedQuoteItemVos.clear();
		selectedQuoteItemVos.addAll(getCacheSelectionDatas());
		for (QuoteItemVo vo : selectedQuoteItemVos) {
			if (!StringUtils.isEmpty(vo.getQuoteItem().getInternalTransferComment())) {
				if (vo.getQuoteItem().getInternalTransferComment().length() > 1024) {
					vo.setErrRow(true);
					errorRows.add(vo.getSeq());
				}
			}
		}

		return errorRows;
	}

	public void invalidInternalTransfer() {
		// NEC Pagination changes: get the selected data from cache
		selectedQuoteItemVos.clear();
		selectedQuoteItemVos.addAll(getCacheSelectionDatas());
		if (selectedQuoteItemVos.size() == 0) {
			FacesContext.getCurrentInstance().addMessage("growl", new FacesMessage(FacesMessage.SEVERITY_ERROR,
					ResourceMB.getText("wq.message.noRecordSelected") + " !", ""));
			return;
		}

		List<Integer> errorRows = pmCommentChecking();

		if (errorRows.size() != 0) {
			String errMessage = ResourceMB.getText("wq.message.EnterPMComment") + " .<br/>"
					+ convertErrorRows(errorRows);
			FacesContext.getCurrentInstance().addMessage("growl",
					new FacesMessage(FacesMessage.SEVERITY_ERROR, errMessage, ""));
			return;
		}

		User user = UserInfo.getUser();

		try {
			responseInternalTransferSB.invalidQuoteItems(selectedQuoteItemVos, getSelectedRemovedAttachments(), user);

		} catch (Exception e) {

			if (e.getMessage().contains("OptimisticLockException")) {
				String message = getOptimisticLockMessage(e);
				LOG.log(Level.WARNING, "Invalid RFQ Failed", e);
				FacesContext.getCurrentInstance().addMessage("growl",
						new FacesMessage(FacesMessage.SEVERITY_ERROR, message, ""));
				return;
			}

			LOG.log(Level.WARNING, "Invalid RFQ Failed", e);
			FacesContext.getCurrentInstance().addMessage("growl",
					new FacesMessage(FacesMessage.SEVERITY_ERROR, e.getMessage(), ""));
			return;
		}

		FacesContext.getCurrentInstance().addMessage("growl",
				new FacesMessage(FacesMessage.SEVERITY_INFO, ResourceMB.getText("wq.message.RFQInvalidated"), ""));

		LOG.info(user.getEmployeeId() + " invalid quote item" + getSelectedQuoteNubmers());
		pmSearch();

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

	private List<Integer> mandatoryFieldChecking() {

		List<Integer> errorRows = new ArrayList<Integer>();
		// NEC Pagination changes: get the selected data from cache
		selectedQuoteItemVos.clear();
		selectedQuoteItemVos.addAll(getCacheSelectionDatas());
		for (QuoteItemVo vo : selectedQuoteItemVos) {

			boolean validRow = true;

			QuoteItem item = vo.getQuoteItem();

			if (StringUtils.isEmpty(vo.getQuotedPartNumber())) {
				validRow = false;
			}

			if (item.getCost() == null || item.getCost() < 0) {
				validRow = false;
			}
			// modified by DamonChen@20180528
			if (StringUtils.isEmpty(item.getCostIndicator()) || item.getCostIndicator().equalsIgnoreCase("-SELECT-")) {
				validRow = false;
			}

			if (!isMatchedSalesCostQuoteItem(item)) {
				if (item.getQuotedPrice() == null || item.getQuotedPrice() <= 0) {
					validRow = false;
				}
			}
			if (StringUtils.isEmpty(item.getQtyIndicator()) || item.getQtyIndicator().equalsIgnoreCase("-SELECT-")) {
				validRow = false;
			}

			if (item.getMpq() == null || item.getMpq() <= 0) {
				validRow = false;
			}

			if ((item.getMoq() == null || item.getMoq() <= 0) && (item.getMov() == null || item.getMov() <= 0)) {
				validRow = false;
			}

			if (item.getLeadTime() == null) {
				validRow = false;
			}

			if (item.getPriceValidity() == null) {
				validRow = false;
			}

			if (item.isSalesCostFlag() == true) {
				if (item.getSalesCostType() == null || "".equals(item.getSalesCostType().name().trim())) {
					validRow = false;
				}

			}

			if (isMatchedSalesCostQuoteItem(item)) {
				if (item.getSalesCost() == null || (item.getSalesCost().compareTo(BigDecimal.ZERO) == 0)) {
					validRow = false;
				}
			}

			if (validRow == false) {
				vo.setErrRow(true);
				errorRows.add(vo.getSeq());
			}

		}
		return errorRows;
	}

	/**
	 * @Description: TODO @author 042659 @param item @return @return
	 *               boolean @throws
	 */
	private boolean isMatchedSalesCostQuoteItem(QuoteItem item) {
		if (item == null) {
			return false;
		}
		if (!item.isSalesCostFlag()) {
			return false;
		}
		if (item.getSalesCostType() == null
				|| (null != item.getSalesCostType() && SalesCostType.NonSC.equals(item.getSalesCostType()))) {
			return false;
		}
		return true;
	}

	private List<Integer> vendorFieldChecking() {

		List<Integer> errorRows = new ArrayList<Integer>();
		// NEC Pagination changes: get the selected data from cache
		selectedQuoteItemVos.clear();
		selectedQuoteItemVos.addAll(getCacheSelectionDatas());
		for (QuoteItemVo vo : selectedQuoteItemVos) {

			boolean validRow = true;

			QuoteItem item = vo.getQuoteItem();

			if (item.getCostIndicator().equalsIgnoreCase("C-Contract DPA Cost")
					|| item.getCostIndicator().equalsIgnoreCase("D-S&D/S&C/DPA")
					|| item.getCostIndicator().equalsIgnoreCase("F-Rebate Program")) {
				if (StringUtils.isEmpty(item.getVendorDebitNumber()) || StringUtils.isEmpty(item.getVendorQuoteNumber())
						|| item.getVendorQuoteQty() == null) {
					validRow = false;
				}
			}

			if (validRow == false) {
				vo.setErrRow(true);
				errorRows.add(vo.getSeq());
			}

		}
		return errorRows;
	}

	private List<Integer> priceValidityChecking() {

		List<Integer> errorRows = new ArrayList<Integer>();
		// NEC Pagination changes: get the selected data from cache
		selectedQuoteItemVos.clear();
		selectedQuoteItemVos.addAll(getCacheSelectionDatas());
		for (QuoteItemVo vo : selectedQuoteItemVos) {
			boolean valid = true;

			QuoteItem item = vo.getQuoteItem();

			if (item.getPriceValidity().contains("/")) {
				try {
					simpleDateFormat.parse(item.getPriceValidity());
				} catch (ParseException e) {
					LOG.log(Level.WARNING, "Exception in parsing date for price validity : " + item.getPriceValidity()
							+ " , exception message : " + e.getMessage());
					valid = false;
				}

			} else {
				try {
					Integer.parseInt(item.getPriceValidity());
				} catch (Exception e) {
					LOG.log(Level.WARNING, "Exception in parsing for price validity : " + item.getPriceValidity()
							+ " , exception message : " + e.getMessage());
					valid = false;
				}
			}

			if (valid == false) {
				vo.setErrRow(true);
				errorRows.add(vo.getSeq());
			}
		}

		return errorRows;
	}

	/*
	 * private List<Integer> shipmentValidityCheck(){
	 * 
	 * List<Integer> errorRows = new ArrayList<Integer>(); //NEC Pagination
	 * changes: get the selected data from cache selectedQuoteItemVos.clear();
	 * selectedQuoteItemVos.addAll(getCacheSelectionDatas()); for(QuoteItemVo vo
	 * : selectedQuoteItemVos){ boolean valid = true;
	 * 
	 * try { if(StringUtils.isEmpty(vo.getShipmentValidity())){
	 * vo.getQuoteItem().setShipmentValidity(null); continue; }
	 * vo.getQuoteItem().setShipmentValidity(SDF.parse(vo.getShipmentValidity())
	 * ); } catch (ParseException e) { LOG.log(Level.WARNING,
	 * "Exception in parsing date for shipment validity : "+vo.
	 * getShipmentValidity()+" , exception message : "+e.getMessage()); valid =
	 * false; }
	 * 
	 * if(valid == false ){ vo.setErrRow(true); errorRows.add(vo.getSeq()); } }
	 * 
	 * return errorRows; }
	 */

	/*
	 * private List<Integer> quotationEffectiveDateCheck(){
	 * 
	 * List<Integer> errorRows = new ArrayList<Integer>(); //NEC Pagination
	 * changes: get the selected data from cache selectedQuoteItemVos.clear();
	 * selectedQuoteItemVos.addAll(getCacheSelectionDatas()); for(QuoteItemVo vo
	 * : selectedQuoteItemVos){ boolean valid = true;
	 * 
	 * try { if(StringUtils.isEmpty(vo.getQuotationEffectiveDate())){
	 * vo.getQuoteItem().setQuotationEffectiveDate(null); continue; }
	 * vo.getQuoteItem().setQuotationEffectiveDate(SDF.parse(vo.
	 * getQuotationEffectiveDate())); } catch (ParseException e) {
	 * LOG.log(Level.WARNING,
	 * "Exception in parsing date for quaotation efective date : "+vo.
	 * getQuotationEffectiveDate()+" , exception message : "+e.getMessage());
	 * valid = false; }
	 * 
	 * if(valid == false ){ vo.setErrRow(true); errorRows.add(vo.getSeq()); } }
	 * 
	 * return errorRows; }
	 */

	private List<Integer> quotedPriceToCostCheck() {

		List<Integer> errorRows = new ArrayList<Integer>();
		// NEC Pagination changes: get the selected data from cache
		selectedQuoteItemVos.clear();
		selectedQuoteItemVos.addAll(getCacheSelectionDatas());
		for (QuoteItemVo vo : selectedQuoteItemVos) {
			boolean valid = true;
			if (!isMatchedSalesCostQuoteItem(vo.getQuoteItem())) {
				if (vo.getQuoteItem().getQuotedPrice() < vo.getQuoteItem().getCost()) {
					valid = false;
				}
			}
			if (valid == false) {
				vo.setErrRow(true);
				errorRows.add(vo.getSeq());
			}
		}

		return errorRows;
	}

	private List<Integer> quotedPriceToTargetPriceCheck() {

		List<Integer> errorRows = new ArrayList<Integer>();
		// NEC Pagination changes: get the selected data from cache
		selectedQuoteItemVos.clear();
		selectedQuoteItemVos.addAll(getCacheSelectionDatas());
		for (QuoteItemVo vo : selectedQuoteItemVos) {
			boolean valid = true;

			if (!isMatchedSalesCostQuoteItem(vo.getQuoteItem())) {
				QuoteItem item = vo.getQuoteItem();
				BigDecimal trAsBuyDeci = item.convertToBuyValueWithDouble(item.getTargetResale());
				Double trAsBuyDoub = trAsBuyDeci==null? null:trAsBuyDeci.doubleValue();
				Double quotedPrice = item.getQuotedPrice();
				if (trAsBuyDoub != null && quotedPrice !=null
						&& (trAsBuyDoub > quotedPrice && 
								StringUtils.isEmpty(vo.getQuoteItem().getInternalTransferComment()))) {
					valid = false;
				}
				/*QuoteItem item = vo.getQuoteItem();
				BigDecimal qpAsRfqDeci = item.convertToRfqValueWithDouble(vo.getQuoteItem().getQuotedPrice());
				Double qpAsRfq = qpAsRfqDeci==null? null:qpAsRfqDeci.doubleValue();
				Double targetResale = item.getTargetResale();
				if (targetResale != null && qpAsRfq !=null
						&& (targetResale > qpAsRfq
								&& StringUtils.isEmpty(vo.getQuoteItem().getInternalTransferComment()))) {
					valid = false;
				}*/
			}

			if (valid == false) {
				vo.setErrRow(true);
				errorRows.add(vo.getSeq());
			}
		}

		return errorRows;
	}

	private List<Integer> priceValidtyToTodayCheck() {

		List<Integer> errorRows = new ArrayList<Integer>();
		// NEC Pagination changes: get the selected data from cache
		selectedQuoteItemVos.clear();
		selectedQuoteItemVos.addAll(getCacheSelectionDatas());
		for (QuoteItemVo vo : selectedQuoteItemVos) {
			boolean valid = true;

			QuoteItem item = vo.getQuoteItem();

			Date priceValidity = null;

			if (item.getPriceValidity().contains("/")) {
				try {
					priceValidity = simpleDateFormat.parse(item.getPriceValidity());
					Calendar cal = Calendar.getInstance();
					cal.set(Calendar.HOUR_OF_DAY, 0);
					cal.set(Calendar.MINUTE, 0);
					cal.set(Calendar.SECOND, 0);
					cal.set(Calendar.MILLISECOND, 0);
					Date today = cal.getTime();
					long gap = priceValidity.getTime() - today.getTime();
					if (gap < 4 * 24 * 3600 * 1000) {
						valid = false;
					}
				} catch (ParseException e) {
					LOG.log(Level.WARNING, "Exception in parsing date for price validity : "
							+ vo.getQuoteItem().getPriceValidity() + " , exception message : " + e.getMessage());

				}

			} else {
				try {
					int i = Integer.parseInt(item.getPriceValidity());
					if (i < 4) {
						valid = false;
					}
				} catch (Exception e) {
					LOG.log(Level.WARNING, "Exception in parsing price validity : " + item.getPriceValidity()
							+ " , exception message : " + e.getMessage());
				}
			}

			if (valid == false) {
				vo.setErrRow(true);
				errorRows.add(vo.getSeq());
			}
		}

		return errorRows;
	}

	private List<Integer> pmCommentChecking() {

		List<Integer> errorRows = new ArrayList<Integer>();
		// NEC Pagination changes: get the selected data from cache
		selectedQuoteItemVos.clear();
		selectedQuoteItemVos.addAll(getCacheSelectionDatas());
		for (QuoteItemVo vo : selectedQuoteItemVos) {
			if (StringUtils.isEmpty(vo.getQuoteItem().getInternalTransferComment())
					|| vo.getQuoteItem().getInternalTransferComment().equalsIgnoreCase("No")) {
				vo.setErrRow(true);
				errorRows.add(vo.getSeq());
			}
		}
		return errorRows;
	}

	private List<Integer> leadTimeChecking() {

		List<Integer> errorRows = new ArrayList<Integer>();
		// NEC Pagination changes: get the selected data from cache
		selectedQuoteItemVos.clear();
		selectedQuoteItemVos.addAll(getCacheSelectionDatas());
		for (QuoteItemVo vo : selectedQuoteItemVos) {
			QuoteItem item = vo.getQuoteItem();

			String leadTime = item.getLeadTime();
			if (leadTime == null || StringUtils.isEmpty(leadTime)) {
				vo.setErrRow(true);
				errorRows.add(vo.getSeq());
			}
		}
		return errorRows;
	}

	private List<Integer> quotedQtyChecking() {

		List<Integer> errorRows = new ArrayList<Integer>();
		// NEC Pagination changes: get the selected data from cache
		selectedQuoteItemVos.clear();
		selectedQuoteItemVos.addAll(getCacheSelectionDatas());
		for (QuoteItemVo vo : selectedQuoteItemVos) {
			QuoteItem item = vo.getQuoteItem();

			Integer quotedQty = item.getQuotedQty();
			if (quotedQty == null && item.getQtyIndicator().equalsIgnoreCase(QuoteSBConstant.QTY_INDICATOR_EXACT)) {
				vo.setErrRow(true);
				errorRows.add(vo.getSeq());
			}
		}

		return errorRows;
	}

	private List<Integer> quotedMaterialChecking() {

		List<Integer> errorRows = new ArrayList<Integer>();
		// NEC Pagination changes: get the selected data from cache
		selectedQuoteItemVos.clear();
		selectedQuoteItemVos.addAll(getCacheSelectionDatas());
		for (QuoteItemVo vo : selectedQuoteItemVos) {

			if (vo.getQuotedPartNumber() == null || vo.getQuotedPartNumber().trim().equals("")) {
				vo.setErrRow(true);
				errorRows.add(vo.getSeq());
			}

		}
		return errorRows;
	}

	private List<String> agpChecking() {

		List<String> errorRows = new ArrayList<String>();
		// NEC Pagination changes: get the selected data from cache
		selectedQuoteItemVos.clear();
		selectedQuoteItemVos.addAll(getCacheSelectionDatas());
		for (QuoteItemVo vo : selectedQuoteItemVos) {

			QuoteItem item = vo.getQuoteItem();

			if (com.avnet.emasia.webquote.utilities.util.QuoteUtil.getDrmsKey(item) == null) {
				continue;
			}

			if (item.getAuthGp() == null && item.getResaleMargin() == null) {
				continue;
			} else if (item.getAuthGp() != null && item.getResaleMargin() != null) {
				if (item.getResaleMargin() < 0) {
					continue;
				}
				if (com.avnet.emasia.webquote.utilities.util.QuoteUtil.compareQGPAndAGP(item.getResaleMargin(),
						item.getAuthGp()) != 0
						&& (StringUtils.isEmpty(vo.getAuthGpReasonCode())
								|| StringUtils.isEmpty(vo.getAuthGpReasonDesc()))) {
					vo.setErrRow(true);
					errorRows.add(item.getQuoteNumber());
				}
			} else {
				vo.setErrRow(true);
				errorRows.add(item.getQuotedPartNumber());
			}
		}
		return errorRows;
	}

	private String getQuoteNumbers(List<QuoteItemVo> quoteItemVoList) {

		List<String> quoteNumbers = new ArrayList<String>();
		for (QuoteItemVo vo : quoteItemVoList) {
			quoteNumbers.add(vo.getQuoteItem().getQuoteNumber());
		}

		return quoteNumbers.toString();
	}

	private String getSelectedQuoteNubmers() {

		List<String> quoteNumbers = new ArrayList<String>();
		// NEC Pagination changes: get the selected data from cache
		selectedQuoteItemVos.clear();
		selectedQuoteItemVos.addAll(getCacheSelectionDatas());
		for (QuoteItemVo vo : selectedQuoteItemVos) {
			quoteNumbers.add(vo.getQuoteItem().getQuoteNumber());
		}

		return quoteNumbers.toString();
	}

	public List<String> autoCompletePartList(String key) {
		// NEC Pagination changes: get the selected data from cache
		selectedQuoteItemVos.clear();
		selectedQuoteItemVos.addAll(getCacheSelectionDatas());
		if (this.selectedQuoteItemVo != null) {

			List<BizUnit> bizUnits = new ArrayList<BizUnit>();
			bizUnits.add(selectedQuoteItemVo.getQuoteItem().getQuote().getBizUnit());

			List<Material> materials = materialSB.wFindMaterialByMfrPartNumberWithPage(key, bizUnits,
					QuoteConstant.DEFAULT_AUTOCOMPLETE_FIRST_RESULT, QuoteConstant.DEFAULT_AUTOCOMPLETE_MAX_RESULTS);

			if (materials != null) {

				List<String> materialList = new ArrayList<String>();
				for (Material material : materials) {
					if (material.getManufacturer().getName()
							.equals(selectedQuoteItemVo.getQuoteItem().getRequestedMfr().getName())
							&& (material.getValid() == null || material.getValid() == true)) {
						materialList.add(material.getFullMfrPartNumber());
					}
				}
				return materialList;
			}

		}
		return null;

	}

	public void changeQuotedPartNumber(AjaxBehaviorEvent event) {

		QuoteItemVo vo = (QuoteItemVo) event.getComponent().getAttributes().get("targetQuoteItemVo");

		QuoteItem item = vo.getQuoteItem();
		this.quoteItemVo.cellChangeListener(String.valueOf(item.getId()));
		String partNumber = vo.getQuotedPartNumber();
		String mfrName = item.getRequestedMfr().getName();

		quoteSB.changeQuotedPartNumber(item, mfrName, partNumber);
		item.setResaleMargin(null);
		// vo.setTermsAndConditionsStr(item.getTermsAndConditions());
		// vo.setShipmentValidity(DateUtil.dateToString(item.getShipmentValidity(),
		// "dd/MM/yyyy"));
		// vo.setQuotationEffectiveDate(DateUtil.dateToString(item.getQuotationEffectiveDate(),
		// "dd/MM/yyyy"));
	}

	public void switchCostIndicator(AjaxBehaviorEvent event) {
		QuoteItemVo vo = (QuoteItemVo) event.getComponent().getAttributes().get("targetQuoteItemVo");
		this.quoteItemVo.cellChangeListener(String.valueOf(vo.getQuoteItem().getId()));
		updateCostIndicator(vo, true, 0);
	}

	public void updateQuotedMargin(AjaxBehaviorEvent event) {
		QuoteItem item = (QuoteItem) event.getComponent().getAttributes().get("targetQuoteItem");
		this.quoteItemVo.cellChangeListener(String.valueOf(item.getId()));
		if (item != null) {
			try {
				Double quotedPrice = item.getCost() / (1 - (item.getResaleMargin() / 100));
				item.setQuotedPrice(quotedPrice);
			} catch (Exception ex) {
				LOG.log(Level.WARNING, "Exception in getting quoted price , exception message : " + ex.getMessage(), ex);
			}
		}

		updateAuthGpReason(item);

	}

	public void updateQuotedPrice(AjaxBehaviorEvent event) {

		QuoteItem item = (QuoteItem) event.getComponent().getAttributes().get("targetQuoteItem");
		this.quoteItemVo.cellChangeListener(String.valueOf(item.getId()));
		if (item != null) {
			try {
				Double resaleMargin = 100 * (item.getQuotedPrice() - item.getCost()) / item.getQuotedPrice();
				item.setResaleMargin(resaleMargin);
			} catch (Exception ex) {
				LOG.log(Level.WARNING, "EXception in getting and setting resale margin for Quoted Price: "
						+ item.getQuotedPrice() + ", " + "Quoted P/N: " + item.getQuotedPartNumber() + ex.getMessage());
				item.setResaleMargin(null);
			}
		}

		updateAuthGpReason(item);
	}

	private void updateAuthGpReason(QuoteItem item) {
		QuoteItemVo vo = null;
		for (QuoteItemVo quoteItemVo : quoteItemVos) {
			if (quoteItemVo.getQuoteItem().getId() == item.getId()) {
				vo = quoteItemVo;
				break;
			}
		}
		if (com.avnet.emasia.webquote.utilities.util.QuoteUtil.getDrmsKey(item) == null) {
			return;
		}
		if (vo != null && item.getResaleMargin() != null && item.getAuthGp() != null) {
			if (com.avnet.emasia.webquote.utilities.util.QuoteUtil.compareQGPAndAGP(item.getResaleMargin(),
					item.getAuthGp()) != 0) {
				vo.setAuthGpReasonCode(null);
				vo.setAuthGpReasonDesc(null);
			} else {
				vo.setAuthGpReasonCode(item.getAuthGpReasonCode());
				vo.setAuthGpReasonDesc(item.getAuthGpReasonDesc());
			}
		}
	}

	/**
	 * 
	 * @param item
	 * @param isDefault
	 *            true: default costIndicator pull dowm menu ,false :
	 *            costIndicator search
	 */
	public void updateCostIndicator(QuoteItemVo vo, Boolean isDefault, long pricerId) {

		QuoteItem item = vo.getQuoteItem();

		if (isDefault) {
			quoteSB.switchCostIndicator(item, costIndicatorSB.findCostIndicator(item.getCostIndicator()),
					item.getBuyCurr());
		} else {

			quoteSB.selectPricer(item, pricerId);
		}
		// vo.setTermsAndConditionsStr(item.getTermsAndConditions());
		// vo.setShipmentValidity(DateUtil.dateToString(item.getShipmentValidity(),
		// "dd/MM/yyyy"));
		// vo.setQuotationEffectiveDate(DateUtil.dateToString(item.getQuotationEffectiveDate(),
		// "dd/MM/yyyy"));
		item.setResaleMargin(null);
		updateCost(item);

	}

	public void updateCost(AjaxBehaviorEvent event) {

		QuoteItem item = (QuoteItem) event.getComponent().getAttributes().get("targetQuoteItem");
		this.quoteItemVo.cellChangeListener(String.valueOf(item.getId()));
		updateCost(item);
		updateAuthGpReason(item);
	}

	private void updateCost(QuoteItem item) {

		// this.quoteItemVo.cellChangeListener(String.valueOf(item.getId()));
		if (item.getCost() != null && item.getQuotedPrice() != null) {
			try {
				if(item.getQuotedPrice() != 0d) {
					Double resaleMargin = 100 * (item.getQuotedPrice() - item.getCost()) / item.getQuotedPrice();
					item.setResaleMargin(resaleMargin);
				}
			} catch (Exception e) {
				LOG.log(Level.WARNING, "EXception in getting and setting Quoted price: "
						+ item.getQuotedPrice().toString() + ", Exception message: " + e.getMessage());
				item.setQuotedPrice(null);
				item.setResaleMargin(null);

			}
		} else if (item.getCost() != null && item.getResaleMargin() != null) {
			try {
				double divided = 1 - (item.getResaleMargin() / 100);
				if(divided != 0d) {
					Double quotedPrice = item.getCost() / (1 - (item.getResaleMargin() / 100));
					item.setQuotedPrice(quotedPrice);
				}
				
			} catch (Exception e) {
				LOG.log(Level.WARNING, "EXception in getting and setting resale margin: "
						+ item.getResaleMargin().toString() + ", Exception message: " + e.getMessage());
				item.setQuotedPrice(null);
				item.setResaleMargin(null);
			}
		}

		if (item.getCost() != null && item.getTargetResale() != null) {
			try {
				item.reCalTargetMargin();
			} catch (Exception e) {
				//item.setTargetMargin(null);
				LOG.warning("EXception in getting and setting target margin" + e.getMessage());
			}
		}
	}

	public void searchForMaterialPopup(ActionEvent event) {

		QuoteItemVo vo = (QuoteItemVo) event.getComponent().getAttributes().get("targetQuoteItemVo");
		selectedQuoteItemVo = vo;
		QuoteItem item = vo.getQuoteItem();
		if (vo != null) {

			this.requestedPartNumber = vo.getQuoteItem().getQuotedPartNumber();
			this.quoteItemVo.cellChangeListener(String.valueOf(item.getId()));
			this.mfr = vo.getQuoteItem().getQuotedMfr().getName();
			this.mfrSelectList = new SelectItem[1];
			mfrSelectList[0] = new SelectItem();
			mfrSelectList[0].setLabel(mfr);
			mfrSelectList[0].setValue(mfr);

			pricerInfosInMaterialPopup = materialSB.searchForMaterialPopup(item.createPricerInfo());

		}

	}

	public void searchInMaterialPopup(ActionEvent event) {
		if (requestedPartNumber == null || (requestedPartNumber != null && requestedPartNumber.length() < 3)) {
			FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR,
					ResourceMB.getText("wq.msg.invalidFormat") + " :", ResourceMB.getText("wq.message.minPN"));
			FacesContext.getCurrentInstance().addMessage("workingPlatformRfqGrowl", msg);
			return;
		}
		if (mfr != null) {
			mfr = mfr.trim();
		}
		QuoteItem item = selectedQuoteItemVo.getQuoteItem();
		PricerInfo pricerInfo = item.createPricerInfo();
		pricerInfo.setMfr(mfr);
		pricerInfo.setFullMfrPartNumber(requestedPartNumber);
		pricerInfosInMaterialPopup = materialSB.searchForMaterialPopup(pricerInfo);

	}

	public void replacePart() {

		QuoteItem item = selectedQuoteItemVo.getQuoteItem();

		// item = quoteSB.changeQuotedPartNumber(item,
		// selectedPricerInfoInMaterialPopup.getMfr(),
		// selectedPricerInfoInMaterialPopup.getFullMfrPartNumber());

		if (selectedPricerInfoInMaterialPopup != null) {
			selectedPricerInfoInMaterialPopup.fillPriceInfoToQuoteItem(item);

		}
		selectedQuoteItemVo.setQuotedPartNumber(selectedPricerInfoInMaterialPopup.getFullMfrPartNumber());

		updateFuturnFlagOfUI(selectedQuoteItemVo.getQuoteItem(), selectedQuoteItemVo);
		item.setResaleMargin(null);
		updateCost(item);

	}

	public void searchForPricerPopup(ActionEvent event) {
		QuoteItemVo vo = (QuoteItemVo) event.getComponent().getAttributes().get("targetQuoteItemVo");
		if (vo == null) {
			return;
		}

		seletedItem = vo;
		QuoteItem item = vo.getQuoteItem();

		PricerInfo pricerInfo = item.createPricerInfo();
		pricerInfosInPricerPopup = materialSB.searchForPricerPopup(pricerInfo);
		this.quoteItemVo.cellChangeListener(String.valueOf(item.getId()));
	}

	public void switchBuyCurrency(AjaxBehaviorEvent event) {
		switchCostIndicator(event);
		/*
		 * QuoteItemVo vo =
		 * (QuoteItemVo)event.getComponent().getAttributes().get(
		 * "targetQuoteItemVo"); if(vo == null){ return ; }
		 * 
		 * seletedItem = vo; QuoteItem item = vo.getQuoteItem();
		 * 
		 * PricerInfo pricerInfo = item.createPricerInfo();
		 * pricerInfosInPricerPopup =
		 * materialSB.searchForPricerPopup(pricerInfo);
		 * this.quoteItemVo.cellChangeListener(String.valueOf(item.getId()));
		 */
	}

	private String getOptimisticLockMessage(Exception e) {
		String quoteNumber = QuoteUtil.extractQuoteNumberFromException(e.getMessage());

		String message = quoteNumber + ResourceMB.getText("wq.message.updateLastSearch") + " <br/> " + "1."
				+ ResourceMB.getText("wq.message.unselectRFQ") + " <br/> " + "2."
				+ ResourceMB.getText("wq.message.searchAgainLatest") + ".";

		return message;
	}

	// Upload handlers
	private void upload(UploadedFile file) {
		try {

			String fileExtn = getFileExtension(file.getFileName());

			if (!fileExtn.equalsIgnoreCase("xlsx")) {
				FacesContext.getCurrentInstance().addMessage("growl", new FacesMessage(FacesMessage.SEVERITY_ERROR,
						ResourceMB.getText("wq.message.selExcelFile") + " . ", ""));
				return;
			}

			String fileExtension = getFileExtension(file.getFileName());
			File ritDataFile = streamToFile(file.getInputstream(), fileExtension);
			boolean isvalid = validateExcelFile(ritDataFile);
			if (!isvalid) {
				this.quoteItemVos.clear();
			}
		} catch (Exception e) {
			LOG.log(Level.SEVERE, "IO error occurs during file upload, file name : " + file.getFileName(), e);
		}
	}

	private File streamToFile(InputStream in, String extension) throws IOException {
		File tempFile = null;

		tempFile = File.createTempFile("ritdata", ".xlsx");

		tempFile.deleteOnExit();
		try (FileOutputStream out = new FileOutputStream(tempFile)) {
			IOUtils.copy(in, out);
		}
		return tempFile;
	}

	private boolean validateExcelFile(File file) {
		try {

			String errorMessage = "";
			String warningMessage = "";
			String fileExtn = getFileExtension(file.getName());

			if (fileExtn.equalsIgnoreCase("xlsx")) {
				XSSFWorkbook wb = new XSSFWorkbook(OPCPackage.open(new FileInputStream(file)));
				XSSFSheet sheet = wb.getSheetAt(0);
				XSSFRow row;
				XSSFCell cell;

				int rows; // No of rows
				rows = sheet.getPhysicalNumberOfRows();

				/*
				 * remove by Damonchen@20180403,Mike need remove this 501 limit,
				 * and change to 10000
				 *
				 **/
				if (rows > 10000) {
					FacesContext.getCurrentInstance().addMessage("growl", new FacesMessage(FacesMessage.SEVERITY_ERROR,
							ResourceMB.getText("wq.message.maxRecordsAllowed") + ".", ""));
					return false;
				}

				int cols = 0; // No of columns
				int tmp = 0;

				// This trick ensures that we get the data properly even if it
				// doesn't start from first few rows
				for (int i = 0; i < 10 || i < rows; i++) {
					row = sheet.getRow(i);
					if (row != null) {
						tmp = sheet.getRow(i).getPhysicalNumberOfCells();
						if (tmp > cols)
							cols = tmp;
					}
				}

				ArrayList<Integer> validRows = new ArrayList<Integer>();
				Map<Integer, Integer> rowToQuoteItemVos = new HashMap<Integer, Integer>();
				List<String> quoteNumbers = new ArrayList<String>();

				for (int r = 0; r < rows; r++) {
					row = sheet.getRow(r);
					if (row != null && r > 0) {
						cell = row.getCell(1);
						quoteNumbers.add(cell.toString());
					}
				}

				Set<String> quoteNumberSet = new HashSet<String>(quoteNumbers);

				// Check duplicate records
				if (quoteNumberSet.size() < quoteNumbers.size()) {
					errorMessage = ResourceMB.getText("wq.message.duplicateRecordsFound") + "#";
					FacesContext.getCurrentInstance().addMessage("growl",
							new FacesMessage(FacesMessage.SEVERITY_ERROR, errorMessage, ""));
					return false;
				}

				// Perform search based on quoteNumbers found in excel file
				criteria.setQuoteNumber(quoteNumbers);
				setupUserInfoToCriteria();
				// also need set up selectedSalesCostPart, Otherwise the
				// selection on the page will been picked up. by DamonChen
				criteria.setSelectedSalesCostPart(null);
				criteria.setAction(QuoteSBConstant.RESPONSEIT_DOWNLOAD_SEARCH_ACTION);
				quoteItemVos = myQuoteSearchSB.search(criteria, resourceMB.getResourceLocale());

				Collections.sort(quoteItemVos, new Comparator<QuoteItemVo>() {
					public int compare(QuoteItemVo arg0, QuoteItemVo arg1) {
						return arg0.getQuoteItem().getQuoteNumber().compareTo(arg1.getQuoteItem().getQuoteNumber());
					}
				});

				Map<Integer, String> quoteItemVosMap = new HashMap<Integer, String>();
				for (int i = 0; i < quoteItemVos.size(); i++) {
					quoteItemVosMap.put(i, quoteItemVos.get(i).getQuoteItem().getQuoteNumber());
				}

				// get valid rows
				Map<String, QuoteItemVo> quoteMap = new HashMap<String, QuoteItemVo>();
				for (int i = 0; i < quoteItemVos.size(); i++) {
					quoteMap.put(quoteItemVos.get(i).getQuoteItem().getQuoteNumber(), quoteItemVos.get(i));
				}

				// check filtered quote items and set valid rows
				String filteredQuoteItem = "";
				for (int r = 0; r < rows; r++) {
					row = sheet.getRow(r);
					if (row != null && r > 0) {
						cell = row.getCell(1);

						// Perform a lookup
						QuoteItemVo quoteItem = quoteMap.get(cell.toString());

						if (quoteItem != null) {
							validRows.add(r);
							rowToQuoteItemVos.put(r, quoteItemVos.indexOf(quoteItem));
						}

						if (!quoteMap.keySet().contains(cell.toString())) {
							filteredQuoteItem += "[" + cell.toString() + "] ";
						}
					}
				}

				if (!filteredQuoteItem.equals("")) {
					warningMessage = ResourceMB.getText("wq.message.quoteItemsFiltered") + ": " + filteredQuoteItem;
					FacesContext.getCurrentInstance().addMessage("growl",
							new FacesMessage(FacesMessage.SEVERITY_INFO, warningMessage, ""));
				}

				LOG.info("Start fields validation");

				// field validation
				QuoteItemVo quoteItemVo = null;
				String salesCostTypeStr = null;
				int offset = 9;
				for (int r = 0; r < rows; r++) {
					row = sheet.getRow(r);

					if (validRows.contains(r)) {
						quoteItemVo = quoteMap.get(row.getCell(1).toString());
						// need a temp value for sales cost type from excel
						// file, by DamonChen@20180228
						salesCostTypeStr = null;
						for (int c = 0; c < cols; c++) {
							cell = row.getCell((int) c);
							if (cell == null)  continue;
							//&& !cell.toString().trim().isEmpty()
							//if (cell != null ) {
							
							if (c == 6)// sales cost type
							{
								if (!QuoteUtil.isEmpty(cell.toString())) {
									salesCostTypeStr = cell.toString().trim();
									if ((!QuoteConstant.SALES_COST_TYPE_NONSC.equals(cell.toString().trim()))
											&& (!QuoteConstant.SALES_COST_TYPE_ZINC.equals(cell.toString()))
											&& (!QuoteConstant.SALES_COST_TYPE_ZIND.equals(cell.toString()))) {
										// errorMessage =
										// ResourceMB.getText("wq.message.salesCostTypeInvalid")+"["+r+"]";
										errorMessage = ResourceMB.getParameterizedText(
												"wq.message.salesCostTypeInvalidAtRow",
												"" + "" + String.valueOf(r + 1));
										FacesContext.getCurrentInstance().addMessage("growl",
												new FacesMessage(FacesMessage.SEVERITY_ERROR, errorMessage, ""));
										return false;
									}

								}

							}
							if (c == 12) {
								if (!QuoteUtil.isEmpty(cell.toString())) {
									if (sapDpaCustSB.findSapDpaCustByCustGroupId(cell.toString()) == null) {
										// errorMessage =
										// ResourceMB.getText("wq.message.customerGroupInvalid")
										// + "[" + r + "]";

										errorMessage = ResourceMB.getParameterizedText(
												"wq.message.customerGroupInvalidAtRow", "" + String.valueOf(r + 1));
										FacesContext.getCurrentInstance().addMessage("growl",
												new FacesMessage(FacesMessage.SEVERITY_ERROR, errorMessage, ""));
										return false;
									}

									if (quoteItemVo != null) {
										if (!sapDpaCustSB.isValidCustomerGroup(cell.toString(),
												quoteItemVo.getQuoteItem().getQuote().getBizUnit().getName(),
												quoteItemVo.getQuoteItem().getSoldToCustomer().getCustomerNumber(),
												quoteItemVo.getQuoteItem().getEndCustomer() == null ? null
														: quoteItemVo.getQuoteItem().getEndCustomer()
																.getCustomerNumber())) {
											// errorMessage =
											// ResourceMB.getText("wq.message.customerGroupNotMatched")
											// + "[" + r + "]";
											errorMessage = ResourceMB.getParameterizedText(
													"wq.message.customerGroupNotMatchedAtRow",
													"" + String.valueOf(r + 1));

											FacesContext.getCurrentInstance().addMessage("growl", new FacesMessage(
													FacesMessage.SEVERITY_ERROR, errorMessage, ""));
											return false;
										}
									}

								}

							}
							if (c == 28) // buy curr
							{
								if(!QuoteUtil.isEmpty(cell.toString().trim()))
								{
									if(Currency.hasValue(cell.toString().trim())){
										//noaccesscurrency
										boolean access = user.getDefaultBizUnit().getAllowCurrencies().contains(cell.toString().trim());
										if(!access) {
											String errInfo = ResourceMB.getText("wq.error.noaccesscurrency");
											FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, 
													ResourceMB.getText("wq.message.row") + " [" +String.valueOf(r + 1) + "] :"
													+ errInfo, errInfo);
											FacesContext.getCurrentInstance().addMessage("growl", msg);
											return false;
										}  
										//wq.error.noaccesscurrency
									}else{
										String errInfo = ResourceMB.getText("wq.error.notCurrencyType");
										FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, 
												ResourceMB.getText("wq.message.row") + " [" +String.valueOf(r + 1) + "] :"
												+ errInfo, errInfo);
										FacesContext.getCurrentInstance().addMessage("growl", msg);
										return false;
									}
								
								}else{
									String errInfo = ResourceMB.getText("wq.error.mustinputbuycurr");
									FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, 
											ResourceMB.getText("wq.message.row") + " [" +String.valueOf(r + 1) + "] :"
											+ errInfo, errInfo);
									FacesContext.getCurrentInstance().addMessage("growl", msg);
									return false;
								}  

							}
							if (c == 15) // Avnet Quote P/N
							{

							}
							if (!cell.toString().trim().isEmpty()) {
								if (c == 19 + 4) // Quoted Margin (AKA Real Margin)
								{
									try {
										Double.parseDouble(cell.toString());
									} catch (NumberFormatException e) {
										LOG.log(Level.WARNING, "EXception in parsing double value : " + cell.toString()
												+ " , exception message : " + e.getMessage());
										errorMessage = ResourceMB.getParameterizedText("wq.message.correctQuotedMargin",
												String.valueOf(r + 1));
										FacesContext.getCurrentInstance().addMessage("growl",
												new FacesMessage(FacesMessage.SEVERITY_ERROR, errorMessage, ""));
										return false;
									}
								}
								if (c == 20 + 4) // 
								{
	
									if (!isMatchedSalesCostQuoteItemVo(quoteItemVo, salesCostTypeStr)) {
	
										// errorMessage =
										// ResourceMB.getText("wq.message.suggestedResaleNotAllowed")+"["+r+"]";
										try {
											Double.parseDouble(cell.toString());
										} catch (NumberFormatException e) {
											LOG.log(Level.WARNING, "EXception in parsing double value : "
													+ cell.toString() + " , exception message : " + e.getMessage());
											errorMessage = ResourceMB.getParameterizedText("wq.message.correctAvnetQuotedPrice",
													String.valueOf(r + 1));
											FacesContext.getCurrentInstance().addMessage(null,
													new FacesMessage(FacesMessage.SEVERITY_ERROR, errorMessage, ""));
											return false;
										}
	
									} else {
										if (Double.parseDouble(cell.toString()) > 0) {
											errorMessage = ResourceMB.getParameterizedText(
													"wq.message.quotedResaleNotAllowedAtRow", String.valueOf(r + 1));
											FacesContext.getCurrentInstance().addMessage("growl",
													new FacesMessage(FacesMessage.SEVERITY_ERROR, errorMessage, ""));
											return false;
										}
									}
	
								}
	
								if (c == 21 + 4) // Sales Cost
								{
									try {
										Double.parseDouble(cell.toString());
										if (!isMatchedSalesCostQuoteItemVo(quoteItemVo, salesCostTypeStr)
												&& Double.parseDouble(cell.toString()) > 0) {
											// errorMessage =
											// ResourceMB.getText("wq.message.salesCostNotAllowed")+"["+r+"]";
											errorMessage = ResourceMB.getParameterizedText(
													"wq.message.salesCostNotAllowedAtRow", String.valueOf(r + 1));
	
											FacesContext.getCurrentInstance().addMessage("growl",
													new FacesMessage(FacesMessage.SEVERITY_ERROR, errorMessage, ""));
											return false;
										}
									} catch (NumberFormatException e) {
										LOG.log(Level.WARNING, "EXception in parsing double value : " + cell.toString()
												+ " , exception message : " + e.getMessage());
										errorMessage = ResourceMB.getParameterizedText("wq.message.correctSalesCost",
												String.valueOf(r + 1));
	
										FacesContext.getCurrentInstance().addMessage("growl",
												new FacesMessage(FacesMessage.SEVERITY_ERROR, errorMessage, ""));
										return false;
									}
	
								}
	
								if (c == 22 + 4) // Please correct [Numeric] field:
												// Row[{0}] = [Suggested Resale]
								{
									try {
										Double.parseDouble(cell.toString());
										if (!isMatchedSalesCostQuoteItemVo(quoteItemVo, salesCostTypeStr)
												&& Double.parseDouble(cell.toString()) > 0) {
	
											// errorMessage =
											// ResourceMB.getText("wq.message.suggestedResaleNotAllowed")+"["+r+"]";
	
											errorMessage = ResourceMB.getParameterizedText(
													"wq.message.suggestedResaleNotAllowedAtRow", String.valueOf(r + 1));
											FacesContext.getCurrentInstance().addMessage("growl",
													new FacesMessage(FacesMessage.SEVERITY_ERROR, errorMessage, ""));
											return false;
										}
									} catch (NumberFormatException e) {
										LOG.log(Level.WARNING, "EXception in parsing double value : " + cell.toString()
												+ " , exception message : " + e.getMessage());
										errorMessage = ResourceMB.getParameterizedText(
												"wq.message.correctSuggestedResale", String.valueOf(r + 1));
	
										FacesContext.getCurrentInstance().addMessage("growl",
												new FacesMessage(FacesMessage.SEVERITY_ERROR, errorMessage, ""));
										return false;
									}
	
								}
	
								if (c == 23 + 4) // Cost Indicator
								{
									if (!costIndicators.contains(cell.toString())) {
										errorMessage = ResourceMB.getText("wq.message.costNotFound") + "["
												+ String.valueOf(r + 1) + "]";
										FacesContext.getCurrentInstance().addMessage(null,
												new FacesMessage(FacesMessage.SEVERITY_ERROR, errorMessage, ""));
										return false;
									}
	
								}
								
								if (c == 24 + 5) // COST
								{
									try {
										Double.parseDouble(cell.toString());
	
									} catch (NumberFormatException e) {
										LOG.log(Level.WARNING, "EXception in parsing double value : " + cell.toString()
												+ " , exception message : " + e.getMessage());
										// 
										errorMessage = ResourceMB.getParameterizedText(
												"wq.message.correctCost", String.valueOf(r + 1));
	
										FacesContext.getCurrentInstance().addMessage("growl",
												new FacesMessage(FacesMessage.SEVERITY_ERROR, errorMessage, ""));
										return false;
									}
	
								}
								
								if (c == 25 + 5)// Qty Indicator
								{
									if (!qtyIndicators.contains(cell.toString())) {
										errorMessage = ResourceMB.getText("wq.message.QINotFound") + "["
												+ String.valueOf(r + 1) + "]";
										FacesContext.getCurrentInstance().addMessage("growl",
												new FacesMessage(FacesMessage.SEVERITY_ERROR, errorMessage, ""));
										return false;
									}
	
								}
								if (c == 26 + 5) // Avnet Quoted Qty
								{
									try {
										Double.parseDouble(cell.toString());
									} catch (NumberFormatException e) {
										LOG.log(Level.WARNING, "EXception in parsing double value : " + cell.toString()
												+ " , exception message : " + e.getMessage());
										errorMessage = ResourceMB.getParameterizedText(
												"wq.message.correctAvnetQuotedQty", String.valueOf(r + 1));
										FacesContext.getCurrentInstance().addMessage("growl",
												new FacesMessage(FacesMessage.SEVERITY_ERROR, errorMessage, ""));
										return false;
									}
								}
								if (c == 44 + offset) // Multi Usage
								{
									if (!cell.toString().equalsIgnoreCase("Yes")
											&& !cell.toString().equalsIgnoreCase("No")) {
	
										errorMessage = ResourceMB.getParameterizedText("wq.message.correctMultiUsage",
												String.valueOf(r + 1));
										FacesContext.getCurrentInstance().addMessage("growl",
												new FacesMessage(FacesMessage.SEVERITY_ERROR, errorMessage, ""));
										return false;
									}
								}
								if (c == 47 + offset) // Lead Time (wks)
								{
	
								}
								if (c == 48 + offset) // MPQ
								{
									try {
										Double.parseDouble(cell.toString());
									} catch (NumberFormatException e) {
										LOG.log(Level.WARNING, "EXception in parsing double value : " + cell.toString()
												+ " , exception message : " + e.getMessage());
										errorMessage = ResourceMB.getParameterizedText("wq.message.correctMPQ",
												String.valueOf(r + 1));
										FacesContext.getCurrentInstance().addMessage("growl",
												new FacesMessage(FacesMessage.SEVERITY_ERROR, errorMessage, ""));
										return false;
									}
								}
								if (c == 49 + offset) // MOQ
								{
									try {
										Double.parseDouble(cell.toString());
									} catch (NumberFormatException e) {
										LOG.log(Level.WARNING, "EXception in parsing double value : " + cell.toString()
												+ " , exception message : " + e.getMessage());
										errorMessage = ResourceMB.getParameterizedText("wq.message.correctMOQ",
												String.valueOf(r + 1));
										FacesContext.getCurrentInstance().addMessage("growl",
												new FacesMessage(FacesMessage.SEVERITY_ERROR, errorMessage, ""));
										return false;
									}
								}
								if (c == 50 + offset) // MOV
								{
									try {
										Double.parseDouble(cell.toString());
									} catch (NumberFormatException e) {
										LOG.log(Level.WARNING, "EXception in parsing double value : " + cell.toString()
												+ " , exception message : " + e.getMessage());
										errorMessage = ResourceMB.getParameterizedText("wq.message.correctMOV",
												String.valueOf(r + 1));
										FacesContext.getCurrentInstance().addMessage("growl",
												new FacesMessage(FacesMessage.SEVERITY_ERROR, errorMessage, ""));
										return false;
									}
								}
								if (c == 56 + offset) // MFR Quote Qty
								{
									try {
										Double.parseDouble(cell.toString());
									} catch (NumberFormatException e) {
										LOG.log(Level.WARNING, "EXception in parsing double value : " + cell.toString()
												+ " , exception message : " + e.getMessage());
										errorMessage = ResourceMB.getParameterizedText("wq.message.correctMFRQuoteQTY",
												String.valueOf(r + 1));
										FacesContext.getCurrentInstance().addMessage("growl",
												new FacesMessage(FacesMessage.SEVERITY_ERROR, errorMessage, ""));
										return false;
									}
								}
								if (c == 57 + offset) // Rescheduling Window
								{
									try {
										Double.parseDouble(cell.toString());
									} catch (NumberFormatException e) {
										LOG.log(Level.WARNING, "EXception in parsing double value : " + cell.toString()
												+ " , exception message : " + e.getMessage());
										errorMessage = ResourceMB.getParameterizedText(
												"wq.message.correctReschedulingWindow", String.valueOf(r + 1));
										FacesContext.getCurrentInstance().addMessage("growl",
												new FacesMessage(FacesMessage.SEVERITY_ERROR, errorMessage, ""));
										return false;
									}
								}
								if (c == 58 + offset) // Cancellation Window
								{
									try {
										Double.parseDouble(cell.toString());
									} catch (NumberFormatException e) {
										LOG.log(Level.WARNING, "EXception in parsing double value : " + cell.toString()
												+ " , exception message : " + e.getMessage());
										errorMessage = ResourceMB.getParameterizedText(
												"wq.message.correctCancellationWindow", String.valueOf(r + 1));
										FacesContext.getCurrentInstance().addMessage("growl",
												new FacesMessage(FacesMessage.SEVERITY_ERROR, errorMessage, ""));
										return false;
									}
								}
								if (c == 65 + offset) // Allocation Part
								{
									if (!cell.toString().equalsIgnoreCase("Yes")
											&& !cell.toString().equalsIgnoreCase("No")) {
	
										errorMessage = ResourceMB.getParameterizedText(
												"wq.message.correctAllocationPart", String.valueOf(r + 1));
										FacesContext.getCurrentInstance().addMessage("growl",
												new FacesMessage(FacesMessage.SEVERITY_ERROR, errorMessage, ""));
										return false;
									}
								}
							}
							
						}
					}
				}
				LOG.info("Fields validation complete");

				criteria.setQuoteNumber(quoteNumbers);
				pmSearchForUpload(sheet, validRows, rowToQuoteItemVos);
				// criteria.setQuoteNumber(Collections.emptyList());
				return true;
				// pmSearchForUpload(sheet,validRows);
			}

		} catch (Exception ioe) {
			LOG.log(Level.SEVERE, "Exception encountered during excel validation for file: " + file.getName()
					+ ", Exception message: " + ioe.getMessage(), ioe);
			return false;
		}
		return false;
	}

	/**
	 * @Description: TODO @author 042659 @param quoteItemVo2 @param
	 *               salesCostTypeStr @return void @throws
	 */
	private boolean isMatchedSalesCostQuoteItemVo(QuoteItemVo quoteItemVo2, String str) {
		if (quoteItemVo2 == null) {
			return false;

		}
		if (quoteItemVo2.getQuoteItem() == null) {
			return false;
		}

		if (!quoteItemVo2.getQuoteItem().isSalesCostFlag()) {
			return false;
		}
		return isZINDorZINC(str);

	}

	/**
	 * @Description: TODO @author 042659 @param salesCostTypeStr @return @return
	 *               boolean @throws
	 */
	private boolean isZINDorZINC(String salesCostTypeStr) {
		// TODO Auto-generated method stub
		if (QuoteUtil.isEmpty(salesCostTypeStr)) {
			return false;
		}
		if (QuoteConstant.SALES_COST_TYPE_NONSC.equals(salesCostTypeStr)) {
			return false;
		}
		return true;
	}

	private static String getFileExtension(String fname2) {
		String fileName = fname2;
		String ext = "";
		int mid = fileName.lastIndexOf(".");
		ext = fileName.substring(mid + 1, fileName.length());
		return ext;
	}// end of upload handlers

	// Getters, Setters

	public MyQuoteSearchCriteria getCriteria() {
		return criteria;
	}

	public void setCriteria(MyQuoteSearchCriteria criteria) {
		this.criteria = criteria;
	}

	public List<String> getCostIndicators() {
		return costIndicators;
	}

	public void setCostIndicators(List<String> costIndicators) {
		this.costIndicators = costIndicators;
	}

	public List<String> getQtyIndicators() {
		return qtyIndicators;
	}

	public void setQtyIndicators(List<String> qtyIndicators) {
		this.qtyIndicators = qtyIndicators;
	}

	public long getSelectedQuoteItemId() {
		return selectedQuoteItemId;
	}

	public void setSelectedQuoteItemId(long selectedQuoteItemId) {
		this.selectedQuoteItemId = selectedQuoteItemId;
	}

	public QuoteItemVo getSelectedQuoteItemVo() {
		// allowAutoSearch = true;
		return selectedQuoteItemVo;

	}

	public void setSelectedQuoteItemVo(QuoteItemVo selectdQuoteItemVo) {
		this.selectedQuoteItemVo = selectdQuoteItemVo;
	}

	public String getQuoteNumber() {
		return quoteNumber;
	}

	public boolean isAllowAutoSearch() {
		return allowAutoSearch;
	}

	public void setAllowAutoSearch(boolean allowAutoSearch) {
		this.allowAutoSearch = allowAutoSearch;
	}

	public UploadedFile getRitDataExcel() {
		return ritDataExcel;
	}

	public void setRitDataExcel(UploadedFile ritDataExcel) {
		this.ritDataExcel = ritDataExcel;
	}

	public StreamedContent getDownloadRitDataExcel() {
		return downloadRitDataExcel;
	}

	public void setDownloadRitDataExcel(StreamedContent downloadRitDataExcel) {
		this.downloadRitDataExcel = downloadRitDataExcel;
	}

	public String getUpdateIds() {
		return updateIds;
	}

	public void setUpdateIds(String updateIds) {
		this.updateIds = updateIds;
	}

	public String getMfr() {
		return mfr;
	}

	public void setMfr(String mfr) {
		this.mfr = mfr;
	}

	public String getRequestedPartNumber() {
		return requestedPartNumber;
	}

	public void setRequestedPartNumber(String requestedPartNumber) {
		this.requestedPartNumber = requestedPartNumber;
	}

	public List<String> getTermsAndConditions() {
		return termsAndConditions;
	}

	public void setTermsAndConditions(List<String> termsAndConditions) {
		this.termsAndConditions = termsAndConditions;
	}

	public PartModel getPartModel() {
		return partModel;
	}

	public void setPartModel(PartModel partModel) {
		this.partModel = partModel;
	}

	public int getSearchPartsCount() {
		return searchPartsCount;
	}

	public void setSearchPartsCount(int searchPartsCount) {
		this.searchPartsCount = searchPartsCount;
	}

	public SelectItem[] getMfrSelectList() {
		return mfrSelectList;
	}

	public void setMfrSelectList(SelectItem[] mfrSelectList) {
		this.mfrSelectList = mfrSelectList;
	}

	public List<PricerInfo> getPricerInfosInMaterialPopup() {
		return pricerInfosInMaterialPopup;
	}

	public void setPricerInfosInMaterialPopup(List<PricerInfo> pricerInfosInMaterialPopup) {
		this.pricerInfosInMaterialPopup = pricerInfosInMaterialPopup;
	}

	public PricerInfo getSelectedPricerInfoInMaterialPopup() {
		return selectedPricerInfoInMaterialPopup;
	}

	public void setSelectedPricerInfoInMaterialPopup(PricerInfo selectedPricerInfoInMaterialPopup) {
		this.selectedPricerInfoInMaterialPopup = selectedPricerInfoInMaterialPopup;
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

	public List<String> getAvnetQuoteCentreComments() {
		return avnetQuoteCentreComments;
	}

	public void setAvnetQuoteCentreComments(List<String> avnetQuoteCentreComments) {
		this.avnetQuoteCentreComments = avnetQuoteCentreComments;
	}

	public List<DrmsAgpRea> getDrmsAgpReasons() {
		return drmsAgpReasons;
	}

	public void setDrmsAgpReasons(List<DrmsAgpRea> drmsAgpReasons) {
		this.drmsAgpReasons = drmsAgpReasons;
	}

	public boolean getCacheModificationFlag() {
		return cacheModificationFlag;
	}

	public void setCacheModificationFlag(boolean cacheModificationFlag) {
		this.cacheModificationFlag = cacheModificationFlag;
	}

	public SelectItem[] getQtyIndicatorOptions() {
		return qtyIndicatorOptions;
	}

	public void setQtyIndicatorOptions(SelectItem[] qtyIndicatorOptions) {
		this.qtyIndicatorOptions = qtyIndicatorOptions;
	}

	public QuoteItemVo getSeletedItem() {
		return seletedItem;
	}

	public void setSeletedItem(QuoteItemVo seletedItem) {
		this.seletedItem = seletedItem;
	}

	public void selectPricer() {
		if (selectedPricerInfoInPricerPopup != null) {
			LOG.info("selectedCostIndicatorAction : " + selectedPricerInfoInPricerPopup.getPricerId());
			seletedItem.getQuoteItem().setCostIndicator(selectedPricerInfoInPricerPopup.getCostIndicator());
			seletedItem.getQuoteItem().setResaleMargin(null);
			updateCostIndicator(seletedItem, false, selectedPricerInfoInPricerPopup.getPricerId());

		}
	}

	public SelectItem[] convertSapPartNumberToSelectItem(String requestedMfr, String requestPartNumber) {
		if (QuoteUtil.isEmpty(requestedMfr) || QuoteUtil.isEmpty(requestPartNumber))
			return QuoteUtil.createFilterOptions(new String[] {});
		User user = UserInfo.getUser();
		BizUnit bizUnit = user.getDefaultBizUnit();
		List<Tuple> sapPartNumberTuples = materialSB.findSapPartNumbersByPNandMfrName(requestPartNumber, requestedMfr,
				bizUnit, true);
		return QuoteUtil.convertSapPartNumberToSelectItem(requestedMfr, requestPartNumber, sapPartNumberTuples);
	}

	/*
	 * public Set<String> getFuturePricerMap(List<QuoteItemVo> quoteItemVos) {
	 * Set<String> returnSet = new HashSet<String>(); List<FuturePriceCriteria>
	 * futurePriceCriterias = new ArrayList<FuturePriceCriteria>();
	 * if(quoteItemVos!=null && quoteItemVos.size()>0) { for(QuoteItemVo qiVo :
	 * quoteItemVos) { if(qiVo.getQuoteItem()!=null &&
	 * qiVo.getQuoteItem().getQuote()!=null &&
	 * qiVo.getQuoteItem().getQuote().getBizUnit()!=null &&
	 * qiVo.getQuoteItem().getRequestedMfr()!=null &&
	 * qiVo.getQuoteItem().getQuotedPartNumber()!=null) { FuturePriceCriteria
	 * fpc = new FuturePriceCriteria();
	 * fpc.setBizUnit(qiVo.getQuoteItem().getQuote().getBizUnit().getName());
	 * fpc.setMfr(qiVo.getQuoteItem().getRequestedMfr().getName());
	 * fpc.setPartNumber(qiVo.getQuoteItem().getQuotedPartNumber());
	 * futurePriceCriterias.add(fpc); } } }
	 * 
	 * if(futurePriceCriterias!=null && futurePriceCriterias.size()>0) {
	 * returnSet = pricerTypeMappingSB.getFuturePriceMap(futurePriceCriterias);
	 * } return returnSet; }
	 */

	public void updateFuturnFlagOfUI(QuoteItem quoteItem, QuoteItemVo vo) {
		int[] iconsArray = quoteSB.calPricerNumber(vo.getQuoteItem());
		vo.setIconsArray(iconsArray);
		vo.setHasFuturePricer(iconsArray[2] > 0 ? true : false);
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

	// NEC Pagination changes: returns selected page's data
	public Set<QuoteItemVo> getCacheSelectionDatas() {
		return quoteItemVo.getCacheSelectedItems();
	}

	/**
	 * Hook used to make cacheModifyFlag false,after populate data from cache
	 * 
	 * @param event
	 */
	public void afterPhase(PhaseEvent event) {
		if (event.getPhaseId() == PhaseId.RENDER_RESPONSE && getCacheModificationFlag()) {
			setCacheModificationFlag(false);
		}
	}

	// NEC Pagination changes: it is getter for quoteItemVo
	public LazyDataModelForResponseInternalTransfer getQuoteItemVo() {
		return quoteItemVo;
	}

	// NEC Pagination changes: it is setter for quoteItemVo
	public void setQuoteItemVo(LazyDataModelForResponseInternalTransfer quoteItemVo) {
		this.quoteItemVo = quoteItemVo;
	}

	// NEC Pagination Changes: Inner Class created for implementing Pagination
	// with Lazy Loading
	public class LazyDataModelForResponseInternalTransfer extends SelectableLazyDataModel<QuoteItemVo> {

		// add cache for changed
		/*
		 * public void onCellEdit(CellEditEvent event) {
		 * LOG.info("Call onCellEdit"); Object newValue = event.getNewValue();
		 * Object oldValue = event.getOldValue(); if(newValue!=null &&
		 * !newValue.equals(oldValue)) { for(QuoteItemVo
		 * vo:this.getCurrentPageItems()) {
		 * if(String.valueOf(vo.getQuoteItem().getId()).equals(event.getRowKey()
		 * )) { this.getCacheModifiedItems().add(vo);
		 * LOG.info("Call onCellEdit get target ::id::"+event.getRowKey() +
		 * "index::"+event.getRowIndex()); return; } }
		 * 
		 * } }
		 */
		// NEC Pagination Changes: sets maximum no. of pages that can be stored
		// in cache at a time
		@Override
		public void startPagination() {
			super.startPagination();
			String cachePageSizeVal = sysConfigSB.getProperyValue(SelectableLazyDataModel.CACHE_PAGE_SIZE);
			try {
				setCachePageSize(Integer.parseInt(cachePageSizeVal));
			} catch (Exception e) {

			}

		}

		// NEC Pagination Changes: finds data for previous page through
		// asynchronous call to database
		@Override
		public void findLazyPreviousPageData(int first, int pageSize, int pageNumber, String sortField, String sort,
				Map<String, Object> filters, ConcurrentHashMap<Integer, List<QuoteItemVo>> map, Queue<Integer> queue,
				int cachePageSize) {

			myQuoteSearchSB.asyncSearch(criteria, resourceMB.getResourceLocale(), first, pageSize, pageNumber,
					sortField, sort, filters, map, queue, cachePageSize, true);
		}

		// NEC Pagination Changes: finds data for next page through asynchronous
		// call to database
		@Override
		public void findLazyNextPageData(int first, int pageSize, int pageNumber, String sortField, String sort,
				Map<String, Object> filters, ConcurrentHashMap<Integer, List<QuoteItemVo>> map, Queue<Integer> queue,
				int cachePageSize) {
			myQuoteSearchSB.asyncSearch(criteria, resourceMB.getResourceLocale(), first, pageSize, pageNumber,
					sortField, sort, filters, map, queue, cachePageSize, true);
		}

		// NEC Pagination Changes: finds data for current page
		@Override
		public List<QuoteItemVo> findLazyData(int first, int pageSize, String sortField, String sortOrder,
				Map<String, Object> filters) {

			List<QuoteItemVo> outCome = myQuoteSearchSB.search(criteria, resourceMB.getResourceLocale(), first,
					pageSize, sortField, sortOrder, filters, true);
			return outCome;

		}

		// NEC Pagination Changes: finds count of records in database
		@Override
		public int findLazyDataCount(String sortField, String sortOrder, Map<String, Object> filters) {
			return myQuoteSearchSB.count(criteria, resourceMB.getResourceLocale(), sortField, sortOrder, filters);
		}

		// NEC Pagination Changes: for calculation/logic after fetching records
		// from EJB layer
		@Override
		public List<QuoteItemVo> populateData(List<QuoteItemVo> outcome) {

			List<QuoteItem> quoteItems = new ArrayList<QuoteItem>();

			// Set<String> futureSet = getFuturePricerMap(outcome);

			for (QuoteItemVo vo : outcome) {
				quoteItems.add(vo.getQuoteItem());
				vo.setQuotedPartNumber(vo.getQuoteItem().getQuotedPartNumber());
				// vo.setShipmentValidity(vo.getQuoteItem().getShipmentValidityStr());
				// vo.setQuotationEffectiveDate(vo.getQuoteItem().getQuotationEffectiveDateStr());
				// vo.setTermsAndConditionsStr(vo.getQuoteItem().getTermsAndConditions());
				// vo.setAqccStr(vo.getQuoteItem().getInternalTransferComment());
				/*
				 * boolean hasFP = false; if(futureSet!=null &&
				 * vo.getQuoteItem().getRequestedMfr()!=null &&
				 * vo.getQuoteItem().getRequestedPartNumber()!=null) { String
				 * key = vo.getQuoteItem().getRequestedMfr().getName()+"|"+vo.
				 * getQuoteItem().getRequestedPartNumber();
				 * if(futureSet.contains(key)) { hasFP = true; } }
				 */
				int[] iconsArray = quoteSB.calPricerNumber(vo.getQuoteItem());
				vo.setIconsArray(iconsArray);
				vo.setHasFuturePricer(iconsArray[2] > 0 ? true : false);

			}

			try {
				sapWebServiceSB.enquirySAPAGP(quoteItems);
				for (QuoteItem item : quoteItems) {
					if (com.avnet.emasia.webquote.utilities.util.QuoteUtil.getDrmsKey(item) != null) {
						item.setOrginalAuthGp(item.getAuthGp());
					}
				}

			} catch (AppException e) {
				LOG.log(Level.SEVERE, "Failed to enquiry SAP to get DRMS info"
						+ MessageFormatorUtil.getParameterizedStringFromException(e), e);
			}

			for (QuoteItemVo vo : outcome) {
				vo.setAuthGpReasonCode(vo.getQuoteItem().getAuthGpReasonCode());
				vo.setAuthGpReasonDesc(vo.getQuoteItem().getAuthGpReasonDesc());
			}
			// added by damonchen
			quoteItemVos = outcome;
			return outcome;
		}

		@Override
		public Object getRowKey(QuoteItemVo object) {

			return ((QuoteItemVo) object).getQuoteItem().getId();

		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.primefaces.model.LazyDataModel#getRowData(java.lang.String)
		 */
		@Override
		public QuoteItemVo getRowData(String rowKey) {
			List<QuoteItemVo> list = (List<QuoteItemVo>) getWrappedData();
			if (!list.isEmpty()) {
				for (QuoteItemVo t : list) {

					String key = (String.valueOf(((QuoteItemVo) t).getQuoteItem().getId()));
					if (rowKey.equals(key))
						return t;

				}
			}

			return null;
		}

		// NEC Pagination Changes
		@Override
		// NEC Pagination changes: it is callback method
		public void cellChangeListener(String id) {
			// LOG.info(this.getClass() +" call cellChangeListener ::" + id);
			quoteItemVo.setCacheModifyData(id);
			FacesUtil.updateUI("form:datatable_response_internal_transfer");
		}

		// Created by DamonChen@20180309
		public void cellChangeListenerWithoutUpdateUI(String id) {
			// LOG.info(this.getClass() +" call
			// cellChangeListenerWithoutUpdateUI ::" + id);
			quoteItemVo.setCacheModifyData(id);
		}

		// NEC Pagination Changes: Getter for lazy loading
		@Override
		// NEC Pagination changes: return data for pagination
		protected SelectableLazyDataModel<QuoteItemVo> getLazyData() {
			return quoteItemVo;
		}

		@Override
		public void onRowSelectCheckbox(SelectEvent event) {
			super.onRowSelectCheckbox(event);
			QuoteItemVo vo = (QuoteItemVo) event.getObject();
			if (vo != null) {
				selectedQuoteItemVo = vo;
			}
		}

		@Override
		public void onRowUnselect(UnselectEvent event) {
			super.onRowUnselect(event);
			super.onRowUnselectCheckbox(event);
		}

		public void rowSelect(SelectEvent event) {
			super.onRowSelect(event);
			QuoteItemVo vo = (QuoteItemVo) event.getObject();
			if (vo != null) {
				selectedQuoteItemVo = vo;
			}

		}

		public List<QuoteItemVo> getSelectionItems() {
			Set<QuoteItemVo> cacheDatas = getCacheSelectionDatas();
			selectionItems = new ArrayList<>();
			selectionItems.addAll(cacheDatas);
			return selectionItems;
		}

	}

	public void updateSalesCosteType(AjaxBehaviorEvent event) {
		QuoteItemVo item = (QuoteItemVo) event.getComponent().getAttributes().get("targetQuoteItem");
		LOG.log(Level.INFO, "PERFORMANCE START - updateSalesCosteType()" + item.getQuoteItem().getQuoteNumber());

		if (item != null) {
			this.quoteItemVo.cellChangeListener(String.valueOf(item.getQuoteItem().getId()));
			if (item.getQuoteItem() != null && item.getQuoteItem().getSalesCostType() != null
					&& !QuoteUtil.isEmpty(item.getQuoteItem().getSalesCostType().name())) {
				// changed for defect#271 by DamonChen@20180417
				item.getQuoteItem().setQuotedPrice(null);
				item.getQuoteItem().setResaleMargin(null);
				if (SalesCostType.NonSC.equals(item.getQuoteItem().getSalesCostType())) {
					item.getQuoteItem().setSalesCost(null);
					item.getQuoteItem().setSuggestedResale(null);
				}
			}
		}

	}

	/****** allow currencies **/
	public SelectItem[] convertCurrencyToSelectItem() {
		Set<String> currs = user.getDefaultBizUnit().getAllowCurrencies();
		return QuoteUtil.createFilterOptions(currs.toArray(new String[currs.size()]),
				currs.toArray(new String[currs.size()]), false, false, true);
	}

}
