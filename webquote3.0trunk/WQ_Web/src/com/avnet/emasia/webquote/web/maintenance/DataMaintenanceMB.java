package com.avnet.emasia.webquote.web.maintenance;

import java.io.File;
import java.io.Serializable;
import java.math.BigDecimal;
import java.text.DateFormat;
import com.avnet.emasia.webquote.utilities.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;

import org.apache.commons.collections.CollectionUtils;
import org.primefaces.component.datatable.DataTable;
import org.primefaces.context.RequestContext;
import org.primefaces.event.SelectEvent;
import org.primefaces.event.ToggleSelectEvent;
import org.primefaces.event.UnselectEvent;

import com.avnet.emasia.webquote.constants.ActionEnum;
import com.avnet.emasia.webquote.constants.QuoteSourceEnum;
import com.avnet.emasia.webquote.constants.StatusEnum;
import com.avnet.emasia.webquote.entity.Attachment;
import com.avnet.emasia.webquote.entity.BizUnit;
import com.avnet.emasia.webquote.entity.CostIndicator;
import com.avnet.emasia.webquote.entity.Customer;
import com.avnet.emasia.webquote.entity.Manufacturer;
import com.avnet.emasia.webquote.entity.Material;
import com.avnet.emasia.webquote.entity.Quote;
import com.avnet.emasia.webquote.entity.QuoteItem;
import com.avnet.emasia.webquote.entity.Team;
import com.avnet.emasia.webquote.entity.User;
import com.avnet.emasia.webquote.exception.AppException;
import com.avnet.emasia.webquote.exception.WebQuoteException;
import com.avnet.emasia.webquote.quote.ejb.CostIndicatorSB;
import com.avnet.emasia.webquote.quote.ejb.ManufacturerSB;
import com.avnet.emasia.webquote.quote.ejb.MaterialSB;
import com.avnet.emasia.webquote.quote.ejb.MyQuoteSearchSB;
import com.avnet.emasia.webquote.quote.ejb.QuoteSB;
import com.avnet.emasia.webquote.quote.ejb.QuoteTransactionSB;
import com.avnet.emasia.webquote.quote.ejb.SAPWebServiceSB;
import com.avnet.emasia.webquote.quote.ejb.SystemCodeMaintenanceSB;
import com.avnet.emasia.webquote.quote.ejb.constant.QuoteSBConstant;
import com.avnet.emasia.webquote.quote.vo.MyQuoteSearchCriteria;
import com.avnet.emasia.webquote.quote.vo.QuoteItemVo;
import com.avnet.emasia.webquote.user.ejb.UserSB;
import com.avnet.emasia.webquote.utilites.resources.ResourceMB;
import com.avnet.emasia.webquote.utilites.web.util.QuoteUtil;
import com.avnet.emasia.webquote.utilities.MessageFormatorUtil;
import com.avnet.emasia.webquote.utilities.bean.MailInfoBean;
import com.avnet.emasia.webquote.utilities.common.SysConfigSB;
import com.avnet.emasia.webquote.utilities.ejb.MailUtilsSB;
import com.avnet.emasia.webquote.web.datatable.SelectableLazyDataModel;
import com.avnet.emasia.webquote.web.quote.FacesUtil;
import com.avnet.emasia.webquote.web.quote.job.FileUtil;
import com.avnet.emasia.webquote.web.user.UserInfo;
import com.sap.document.sap.soap.functions.mc_style.TableOfZquoteMsg;
import com.sap.document.sap.soap.functions.mc_style.ZquoteMsg;

/**
 * The Class DataMaintenanceMB.
 */
@ManagedBean
@SessionScoped
//NEC Pagination Changes: Class modified for implementing lazy loading in pagination
public class DataMaintenanceMB  implements Serializable {

	/** The quote item vo. */
	//NEC Pagination changes :  It is used for lazy data
	private LazyDataModelForDataMaintenance quoteItemVos = new LazyDataModelForDataMaintenance();

	/** The sys config SB. */
	@EJB
	SysConfigSB sysConfigSB;

	/** The Constant SDF. */
	private static final SimpleDateFormat SDF = new SimpleDateFormat("dd/MM/yyyy");
	
	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = -3705122712201855188L;
	
	/** The Constant LOG. */
	private static final Logger LOG = Logger.getLogger(DataMaintenanceMB.class
			.getName());

	/** The criteria. */
	private MyQuoteSearchCriteria criteria;
	
	/** The update criteria. */
	private QuoteItem updateCriteria;

	
	/** The my quote search SB. */
	@EJB
	private MyQuoteSearchSB myQuoteSearchSB;

	/** The cost indicator SB. */
	@EJB
	private CostIndicatorSB costIndicatorSB;

	/** The manufacturer SB. */
	@EJB
	private ManufacturerSB manufacturerSB;

	/** The material SB. */
	@EJB
	private MaterialSB materialSB;

	/** The user SB. */
	@EJB
	private UserSB userSB;

	/** The quote SB. */
	@EJB
	private QuoteSB quoteSB;

	/** The quote transaction SB. */
	@EJB
	private QuoteTransactionSB quoteTransactionSB;

	/** The mail utils SB. */
	@EJB
	private MailUtilsSB mailUtilsSB;

	/** The sys maint SB. */
	@EJB
	SystemCodeMaintenanceSB sysMaintSB;

	/** The web service SB. */
	@EJB
	private SAPWebServiceSB webServiceSB;

	/** The user. */
	private User user = UserInfo.getUser();

	/** The filtered quote item vos. */
	private List<QuoteItemVo> filteredQuoteItemVos;



	/** The records exceed max allowed. */
	private boolean recordsExceedMaxAllowed = false;
	
	/** The generate new. */
	private boolean generateNew = false;
	
	/** The selected quote item vos. */
	private List<QuoteItemVo> selectedQuoteItemVos = new ArrayList<QuoteItemVo>();
	
	/** The cost indicators. */
	private List<String> costIndicators = new ArrayList<String>();
	
	/** The invalidated quote numbers. */
	private List<String> invalidatedQuoteNumbers = new ArrayList<String>();
	
	/** The selected quote item vo. */
	private QuoteItemVo selectedQuoteItemVo = new QuoteItemVo();

	/** The quote num str. */
	private String quoteNumStr = "";
	
	/** The new quote no str. */
	private String newQuoteNoStr;
	
	/** The comfirm result. */
	private String comfirmResult;
	
	/** The email user str. */
	private String emailUserStr = "";
	
	/** The email info. */
	private String emailInfo;
	
	/** The invalidate. */
	private String invalidate;
	
	/** The invalidates. */
	private SelectItem[] invalidates;
	
	/** The price validity. */
	private String priceValidity;
	
	/** The shipment validity. */
	private String shipmentValidity;
	
	/** The internal comment. */
	private String internalComment;
	
	/** The sales. */
	private String sales;
	
	/** The cs. */
	private String cs;
	
	/** The creator. */
	private String creator;
	
	/** The quote part no. */
	private String quotePartNo;
	
	/** The cost. */
	private String cost;
	
	/** The quote margin. */
	private String quoteMargin;
	
	/** The vendor quote no. */
	private String vendorQuoteNo;
	
	/** The update success. */
	private boolean updateSuccess;
	
	/** The remove shipment validity. */
	private boolean removeShipmentValidity;
	
	/** The error message. */
	private String errorMessage;
	
	/** The sales men. */
	private String salesMen;
	
	/** The sales man. */
	private String salesMan;
	
	/** The reassign sales selection. */
	private String reassignSalesSelection;
	
	/** The s po expiry date. */
	private String sPoExpiryDate;
	
	/** The s quote expiry date. */
	private String sQuoteExpiryDate;
	
	/** The batch quote num str. */
	private String batchQuoteNumStr;
	
	/** The batch sales. */
	private String batchSales;
	
	/** The batch cs. */
	private String batchCs;
	
	/** The batch creator. */
	private String batchCreator;
	
	/** The batch stages. */
	private String batchStages;
	
	/** The original cost. */
	private Double originalCost;
	
	

	/*invalidateAndGenerate enable */
	private boolean enable;
	
	/** The resource MB. */
	@ManagedProperty(value="#{resourceMB}")
	/** To Inject ResourceMB instance  */
	private ResourceMB resourceMB;
	// mutil thread
	private static final ExecutorService EXCSINGLE = Executors.newSingleThreadExecutor();
	
	public boolean isEnable() {
		return enable;
	}

	public void setEnable(boolean enable) {
		this.enable = enable;
	}

	/**
	 * Checks if is removes the shipment validity.
	 *
	 * @return true, if is removes the shipment validity
	 */
	public boolean isRemoveShipmentValidity() {
		return removeShipmentValidity;
	}

	/**
	 * Sets the removes the shipment validity.
	 *
	 * @param removeShipmentValidity the new removes the shipment validity
	 */
	public void setRemoveShipmentValidity(boolean removeShipmentValidity) {
		this.removeShipmentValidity = removeShipmentValidity;
	}

	/**
	 * Gets the original cost.
	 *
	 * @return the original cost
	 */
	public Double getOriginalCost() {
		return originalCost;
	}

	/**
	 * Sets the original cost.
	 *
	 * @param originalCost the new original cost
	 */
	public void setOriginalCost(Double originalCost) {
		this.originalCost = originalCost;
	}

	/**
	 * Gets the cost indicators.
	 *
	 * @return the cost indicators
	 */
	public List<String> getCostIndicators() {
		return costIndicators;
	}

	/**
	 * Sets the cost indicators.
	 *
	 * @param costIndicators the new cost indicators
	 */
	public void setCostIndicators(List<String> costIndicators) {
		this.costIndicators = costIndicators;
	}

	/**
	 * Gets the reassign sales selection.
	 *
	 * @return the reassign sales selection
	 */
	public String getReassignSalesSelection() {
		return reassignSalesSelection;
	}

	/**
	 * Sets the reassign sales selection.
	 *
	 * @param reassignSalesSelection the new reassign sales selection
	 */
	public void setReassignSalesSelection(String reassignSalesSelection) {
		this.reassignSalesSelection = reassignSalesSelection;
	}

	/**
	 * Gets the sales man.
	 *
	 * @return the sales man
	 */
	public String getSalesMan() {
		return salesMan;
	}

	/**
	 * Sets the sales man.
	 *
	 * @param salesMan the new sales man
	 */
	public void setSalesMan(String salesMan) {
		this.salesMan = salesMan;
	}

	/**
	 * Gets the sales men.
	 *
	 * @return the sales men
	 */
	public String getSalesMen() {
		return salesMen;
	}

	/**
	 * Sets the sales men.
	 *
	 * @param salesMen the new sales men
	 */
	public void setSalesMen(String salesMen) {
		this.salesMen = salesMen;
	}

	/**
	 * Gets the error message.
	 *
	 * @return the error message
	 */
	public String getErrorMessage() {
		return errorMessage;
	}

	/**
	 * Sets the error message.
	 *
	 * @param errorMessage the new error message
	 */
	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}

	/**
	 * Checks if is update success.
	 *
	 * @return true, if is update success
	 */
	public boolean isUpdateSuccess() {
		return updateSuccess;
	}

	/**
	 * Sets the update success.
	 *
	 * @param updateSuccess the new update success
	 */
	public void setUpdateSuccess(boolean updateSuccess) {
		this.updateSuccess = updateSuccess;
	}

	/**
	 * Gets the quote part no.
	 *
	 * @return the quote part no
	 */
	public String getQuotePartNo() {
		return quotePartNo;
	}

	/**
	 * Sets the quote part no.
	 *
	 * @param quotePartNo the new quote part no
	 */
	public void setQuotePartNo(String quotePartNo) {
		this.quotePartNo = quotePartNo;
	}

	/**
	 * Gets the cost.
	 *
	 * @return the cost
	 */
	public String getCost() {
		return cost;
	}

	/**
	 * Sets the cost.
	 *
	 * @param cost the new cost
	 */
	public void setCost(String cost) {
		this.cost = cost;
	}

	/**
	 * Gets the quote margin.
	 *
	 * @return the quote margin
	 */
	public String getQuoteMargin() {
		return quoteMargin;
	}

	/**
	 * Sets the quote margin.
	 *
	 * @param quoteMargin the new quote margin
	 */
	public void setQuoteMargin(String quoteMargin) {
		this.quoteMargin = quoteMargin;
	}

	/**
	 * Gets the vendor quote no.
	 *
	 * @return the vendor quote no
	 */
	public String getVendorQuoteNo() {
		return vendorQuoteNo;
	}

	/**
	 * Sets the vendor quote no.
	 *
	 * @param vendorQuoteNo the new vendor quote no
	 */
	public void setVendorQuoteNo(String vendorQuoteNo) {
		this.vendorQuoteNo = vendorQuoteNo;
	}

	/**
	 * Gets the sales.
	 *
	 * @return the sales
	 */
	public String getSales() {
		return sales;
	}

	/**
	 * Sets the sales.
	 *
	 * @param sales the new sales
	 */
	public void setSales(String sales) {
		this.sales = sales;
	}

	/**
	 * Gets the cs.
	 *
	 * @return the cs
	 */
	public String getCs() {
		return cs;
	}

	/**
	 * Sets the cs.
	 *
	 * @param cs the new cs
	 */
	public void setCs(String cs) {
		this.cs = cs;
	}

	/**
	 * Gets the creator.
	 *
	 * @return the creator
	 */
	public String getCreator() {
		return creator;
	}

	/**
	 * Sets the creator.
	 *
	 * @param creator the new creator
	 */
	public void setCreator(String creator) {
		this.creator = creator;
	}

	/**
	 * Gets the internal comment.
	 *
	 * @return the internal comment
	 */
	public String getInternalComment() {
		return internalComment;
	}

	/**
	 * Sets the internal comment.
	 *
	 * @param internalComment the new internal comment
	 */
	public void setInternalComment(String internalComment) {
		this.internalComment = internalComment;
	}

	/**
	 * Gets the price validity.
	 *
	 * @return the price validity
	 */
	public String getPriceValidity() {
		return priceValidity;
	}

	/**
	 * Sets the price validity.
	 *
	 * @param priceValidity the new price validity
	 */
	public void setPriceValidity(String priceValidity) {
		this.priceValidity = priceValidity;
	}

	/**
	 * Gets the shipment validity.
	 *
	 * @return the shipment validity
	 */
	public String getShipmentValidity() {
		return shipmentValidity;
	}

	/**
	 * Sets the shipment validity.
	 *
	 * @param shipmentValidity the new shipment validity
	 */
	public void setShipmentValidity(String shipmentValidity) {
		this.shipmentValidity = shipmentValidity;
	}

	/**
	 * Gets the comfirm result.
	 *
	 * @return the comfirm result
	 */
	public String getComfirmResult() {
		return comfirmResult;
	}

	/**
	 * Sets the comfirm result.
	 *
	 * @param comfirmResult the new comfirm result
	 */
	public void setComfirmResult(String comfirmResult) {
		this.comfirmResult = comfirmResult;
	}

	/**
	 * Gets the update criteria.
	 *
	 * @return the update criteria
	 */
	public QuoteItem getUpdateCriteria() {
		return updateCriteria;
	}

	/**
	 * Sets the update criteria.
	 *
	 * @param updateCriteria the new update criteria
	 */
	public void setUpdateCriteria(QuoteItem updateCriteria) {
		this.updateCriteria = updateCriteria;
	}

	/**
	 * Checks if is generate new.
	 *
	 * @return true, if is generate new
	 */
	public boolean isGenerateNew() {
		return generateNew;
	}

	/**
	 * Sets the generate new.
	 *
	 * @param generateNew the new generate new
	 */
	public void setGenerateNew(boolean generateNew) {
		this.generateNew = generateNew;
	}

	/**
	 * Gets the new quote no str.
	 *
	 * @return the new quote no str
	 */
	public String getNewQuoteNoStr() {
		return newQuoteNoStr;
	}

	/**
	 * Sets the new quote no str.
	 *
	 * @param newQuoteNoStr the new new quote no str
	 */
	public void setNewQuoteNoStr(String newQuoteNoStr) {
		this.newQuoteNoStr = newQuoteNoStr;
	}

	/**
	 * Gets the email info.
	 *
	 * @return the email info
	 */
	public String getEmailInfo() {
		return emailInfo;
	}

	/**
	 * Sets the email info.
	 *
	 * @param emailInfo the new email info
	 */
	public void setEmailInfo(String emailInfo) {
		this.emailInfo = emailInfo;
	}

	/**
	 * Gets the invalidate.
	 *
	 * @return the invalidate
	 */
	public String getInvalidate() {
		return invalidate;
	}

	/**
	 * Sets the invalidate.
	 *
	 * @param invalidate the new invalidate
	 */
	public void setInvalidate(String invalidate) {
		this.invalidate = invalidate;
	}

	/**
	 * Gets the invalidates.
	 *
	 * @return the invalidates
	 */
	public SelectItem[] getInvalidates() {
		return invalidates;
	}

	/**
	 * Sets the invalidates.
	 *
	 * @param invalidates the new invalidates
	 */
	public void setInvalidates(SelectItem[] invalidates) {
		this.invalidates = invalidates;
	}

	/**
	 * Gets the selected quote item vo.
	 *
	 * @return the selected quote item vo
	 */
	public QuoteItemVo getSelectedQuoteItemVo() {
		return selectedQuoteItemVo;
	}

	/**
	 * Sets the selected quote item vo.
	 *
	 * @param selectedQuoteItemVo the new selected quote item vo
	 */
	public void setSelectedQuoteItemVo(QuoteItemVo selectedQuoteItemVo) {
		this.selectedQuoteItemVo = selectedQuoteItemVo;
	}

	/**
	 * Gets the quote num str.
	 *
	 * @return the quote num str
	 */
	public String getQuoteNumStr() {
		return quoteNumStr;
	}

	/**
	 * Sets the quote num str.
	 *
	 * @param quoteNumStr the new quote num str
	 */
	public void setQuoteNumStr(String quoteNumStr) {
		this.quoteNumStr = quoteNumStr;
	}

	/**
	 * Gets the email user str.
	 *
	 * @return the email user str
	 */
	public String getEmailUserStr() {
		return emailUserStr;
	}

	/**
	 * Sets the email user str.
	 *
	 * @param emailUserStr the new email user str
	 */
	public void setEmailUserStr(String emailUserStr) {
		this.emailUserStr = emailUserStr;
	}

	

	

	/**
	 * Gets the selected quote item vos.
	 *
	 * @return the selected quote item vos
	 */
	public List<QuoteItemVo> getSelectedQuoteItemVos() {
		return selectedQuoteItemVos;
	}

	/**
	 * Sets the selected quote item vos.
	 *
	 * @param selectedQuoteItemVos the new selected quote item vos
	 */
	public void setSelectedQuoteItemVos(List<QuoteItemVo> selectedQuoteItemVos) {
		this.selectedQuoteItemVos = selectedQuoteItemVos;
	}

	/**
	 * Gets the filtered quote item vos.
	 *
	 * @return the filtered quote item vos
	 */
	public List<QuoteItemVo> getFilteredQuoteItemVos() {
		return filteredQuoteItemVos;
	}

	/**
	 * Sets the filtered quote item vos.
	 *
	 * @param filteredQuoteItemVos the new filtered quote item vos
	 */
	public void setFilteredQuoteItemVos(List<QuoteItemVo> filteredQuoteItemVos) {
		this.filteredQuoteItemVos = filteredQuoteItemVos;
	}

	/**
	 * Checks if is records exceed max allowed.
	 *
	 * @return true, if is records exceed max allowed
	 */
	public boolean isRecordsExceedMaxAllowed() {
		return recordsExceedMaxAllowed;
	}

	/**
	 * Sets the records exceed max allowed.
	 *
	 * @param recordsExceedMaxAllowed the new records exceed max allowed
	 */
	public void setRecordsExceedMaxAllowed(boolean recordsExceedMaxAllowed) {
		this.recordsExceedMaxAllowed = recordsExceedMaxAllowed;
	}

	/**
	 * Gets the criteria.
	 *
	 * @return the criteria
	 */
	public MyQuoteSearchCriteria getCriteria() {
		return criteria;
	}

	/**
	 * Sets the criteria.
	 *
	 * @param criteria the new criteria
	 */
	public void setCriteria(MyQuoteSearchCriteria criteria) {
		this.criteria = criteria;
	}

	/**
	 * Gets the s po expiry date.
	 *
	 * @return the s po expiry date
	 */
	public String getsPoExpiryDate() {
		return sPoExpiryDate;
	}

	/**
	 * Sets the s po expiry date.
	 *
	 * @param sPoExpiryDate the new s po expiry date
	 */
	public void setsPoExpiryDate(String sPoExpiryDate) {
		this.sPoExpiryDate = sPoExpiryDate;
	}

	/**
	 * Gets the s quote expiry date.
	 *
	 * @return the s quote expiry date
	 */
	public String getsQuoteExpiryDate() {
		return sQuoteExpiryDate;
	}

	/**
	 * Sets the s quote expiry date.
	 *
	 * @param sQuoteExpiryDate the new s quote expiry date
	 */
	public void setsQuoteExpiryDate(String sQuoteExpiryDate) {
		this.sQuoteExpiryDate = sQuoteExpiryDate;
	}

	/**
	 * Gets the invalidated quote numbers.
	 *
	 * @return the invalidated quote numbers
	 */
	public List<String> getInvalidatedQuoteNumbers() {
		return invalidatedQuoteNumbers;
	}

	/**
	 * Sets the invalidated quote numbers.
	 *
	 * @param invalidatedQuoteNumbers the new invalidated quote numbers
	 */
	public void setInvalidatedQuoteNumbers(List<String> invalidatedQuoteNumbers) {
		this.invalidatedQuoteNumbers = invalidatedQuoteNumbers;
	}

	/**
	 * Gets the invalidated quote numbers string.
	 *
	 * @return the invalidated quote numbers string
	 */
	public String getInvalidatedQuoteNumbersString()
	{
		String quoteNumbers = "";

		for(String q : invalidatedQuoteNumbers)
		{
			quoteNumbers += "[" + q + "]";
		}

		return quoteNumbers;
	}

	/**
	 * Gets the batch quote num str.
	 *
	 * @return the batch quote num str
	 */
	public String getBatchQuoteNumStr() {
		return batchQuoteNumStr;
	}

	/**
	 * Sets the batch quote num str.
	 *
	 * @param batchQuoteNumStr the new batch quote num str
	 */
	public void setBatchQuoteNumStr(String batchQuoteNumStr) {
		this.batchQuoteNumStr = batchQuoteNumStr;
	}

	/**
	 * Gets the batch sales.
	 *
	 * @return the batch sales
	 */
	public String getBatchSales() {
		return batchSales;
	}

	/**
	 * Sets the batch sales.
	 *
	 * @param batchSales the new batch sales
	 */
	public void setBatchSales(String batchSales) {
		this.batchSales = batchSales;
	}

	/**
	 * Gets the batch cs.
	 *
	 * @return the batch cs
	 */
	public String getBatchCs() {
		return batchCs;
	}

	/**
	 * Sets the batch cs.
	 *
	 * @param batchCs the new batch cs
	 */
	public void setBatchCs(String batchCs) {
		this.batchCs = batchCs;
	}

	/**
	 * Gets the batch creator.
	 *
	 * @return the batch creator
	 */
	public String getBatchCreator() {
		return batchCreator;
	}

	/**
	 * Sets the batch creator.
	 *
	 * @param batchCreator the new batch creator
	 */
	public void setBatchCreator(String batchCreator) {
		this.batchCreator = batchCreator;
	}

	/**
	 * Gets the batch stages.
	 *
	 * @return the batch stages
	 */
	public String getBatchStages() {
		return batchStages;
	}

	/**
	 * Sets the batch stages.
	 *
	 * @param batchStages the new batch stages
	 */
	public void setBatchStages(String batchStages) {
		this.batchStages = batchStages;
	}

	/**
	 * Inits the.
	 */
	@PostConstruct
	public void init() {

		//updateCriteria = new QuoteItem();
		criteria = new MyQuoteSearchCriteria();
		Date date = new Date();
		criteria.setPageSentOutDateFrom((getDate(Calendar.MONTH, -6)));
		criteria.setPageSentOutDateTo(date);


		//fix defect 95
		List<StatusEnum> statuss = Arrays.asList(StatusEnum.values());
		List<String> strStatuss = new ArrayList<String> ();
		for(StatusEnum stt:statuss) {
			strStatuss.add(stt.toString());
		}
		criteria.setStatus(strStatuss);
		criteria.setPageStatus(strStatuss);


		List<String> stage = new ArrayList<String>();
		stage = new ArrayList<String>();
		stage.add(QuoteSBConstant.QUOTE_STAGE_DRAFT);
		stage.add(QuoteSBConstant.QUOTE_STAGE_FINISH);
		stage.add(QuoteSBConstant.QUOTE_STAGE_INVALID);
		stage.add(QuoteSBConstant.QUOTE_STAGE_PENDING);

		criteria.setPageStage(stage);
		criteria.setStage(stage);
		criteria.setPageSalesCSStage(stage);

		invalidate = "invalidateAndGenerate";
		reassignSalesSelection = "selectedRecordsOnly";
		updateSuccess = false;
		errorMessage = "";
		selectedQuoteItemVos = null;

		//costIndicators.add(ResourceMB.getText("wq.label.sel"));
		for(CostIndicator costIndicator : costIndicatorSB.findAllActive()){
			costIndicators.add(costIndicator.getName());
		}
		java.util.Collections.sort(costIndicators);
		criteria.setSalsCostAccessFlag(null);
		
	}

	/**
	 * Gets the date.
	 *
	 * @param field the field
	 * @param amount the amount
	 * @return the date
	 */
	private Date getDate(int field, int amount) {
		Date date = new Date();
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		cal.add(field, amount);

		return cal.getTime();
	}

	/**
	 * Auto complete for manufacturer.
	 *
	 * @param key the key
	 * @return the list
	 */
	public List<String> autoCompleteForManufacturer(String key) {
		List<Manufacturer> mfrs = manufacturerSB.mFindManufacturerByName(key,
				user.getBizUnits());

		List<String> result = new ArrayList<String>();

		for (Manufacturer mfr : mfrs) {
			result.add(mfr.getName());
		}

		return result;

	}

	/**
	 * Auto complete for part number.
	 *
	 * @param key the key
	 * @return the list
	 */
	public List<String> autoCompleteForPartNumber(String key) {
		List<Material> materials = materialSB
				.wFindMaterialByMfrPartNumberWithPage(key, user.getBizUnits(),
						0, 10);

		List<String> result = new ArrayList<String>();

		for (Material material : materials) {
			result.add(material.getFullMfrPartNumber());
		}

		return result;

	}

	/**
	 * Auto complete for employee name.
	 *
	 * @param key the key
	 * @return the list
	 */
	public List<String> autoCompleteForEmployeeName(String key) {
		List<BizUnit> bizUnits = user.getBizUnits();

		List<User> users = userSB.wFindUserByNameAndRoleWithPage(key, null,
				bizUnits, 0, 10);

		List<String> result = new ArrayList<String>();

		for (User user1 : users) {
			result.add(user1.getName());
		}

		return result;

	}

	/**
	 * Auto complete for customer.
	 *
	 * @param key the key
	 * @return the list
	 */
	public List<String> autoCompleteForCustomer(String key) {
		List<String> rltLst = new ArrayList<String>();
		List<Customer> custrLst = userSB.findAutoCompleteCustomers(key);

		for (Customer ct : custrLst) {
			rltLst.add(ct.getCustomerFullName());
		}

		return rltLst;
	}

	/**
	 * Check date.
	 *
	 * @return the string
	 */
	private String checkDate() {

		String errMsg = ResourceMB.getText("wq.message.date")+".";
		if (criteria.getPageUploadDateFrom() != null
				&& criteria.getPageUploadDateTo() != null) {
			if (criteria.getPageUploadDateFrom().after(
					criteria.getPageUploadDateTo())) {
				return  errMsg;
			}
		}

		if (criteria.getPageSentOutDateFrom() != null
				&& criteria.getPageSentOutDateTo() != null) {
			if (criteria.getPageSentOutDateFrom().after(
					criteria.getPageSentOutDateTo())) {
				return  errMsg;
			}
		}

		if (criteria.getPagePoExpiryDateFrom() != null
				&& criteria.getPagePoExpiryDateTo() != null) {
			if (criteria.getPagePoExpiryDateFrom().after(
					criteria.getPagePoExpiryDateTo())) {
				return  errMsg;
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

	/**
	 * Setup biz unit.
	 */
	private void setupBizUnit() {
		criteria.setBizUnits(user.getBizUnits());
	}


	/**
	 * Search.
	 */
	public void search() {
		//LOG.info(criteria.toString());

		/*#### Fix for quote data maintenance search does not show results####*/
		User user = UserInfo.getUser();
		criteria.setDataAccesses(userSB.findAllDataAccesses(user));

		List<BizUnit> singleBizUnit = new ArrayList<BizUnit>();
		singleBizUnit.add(user.getDefaultBizUnit());
		for(BizUnit b:user.getBizUnits()){
			singleBizUnit.add(b);
		}
		
		criteria.setBizUnits(singleBizUnit);

		

		//NEC Pagination changes:It is used to initialize the Lazy data table. And it just returns the Lazy data list
		final DataTable d = (DataTable) FacesContext.getCurrentInstance().getViewRoot()
		         .findComponent("f:quote_datatable");
		int first = 0;
		if(null!=d){
			d.setFirst(first);
		}
		

		quoteItemVos.startPagination();
		
		filteredQuoteItemVos = null;

		selectedQuoteItemVos = null;
	}

	/**
	 * Qc operation search.
	 */
	public void qcOperationSearch() {
		String errMsg = checkDate();
		if (errMsg != null) {
			FacesContext.getCurrentInstance().addMessage("messages",
					new FacesMessage(FacesMessage.SEVERITY_ERROR, errMsg, ""));
			return;
		}

		criteria.setupUIInCriteria();
		setupBizUnit();
		search();
	}

	/**
	 * Generate new pending qno.
	 *
	 * @return the string
	 */
	public String generateNewPendingQno() {
		return quoteSB.populateQuoteNumberOnly(user.getDefaultBizUnit());
	}

	

	/**
	 * Sets the value for shipment validity.
	 */
	private void setValueForShipmentValidity(){
		//invalidate finished quote related info
		//NEC Pagination changes: get the selected data from cache 
		Set<QuoteItemVo> datas = getCacheSelectionDatas();
		if(datas.isEmpty()) return ;  
		QuoteItemVo quoteItem = datas.iterator().next();
		quoteNumStr = quoteItem.getQuoteItem().getQuoteNumber();

		sales = quoteItem.getQuoteItem().getQuote()
				.getSales().getName();

		//Processing copyToCS;
		cs = quoteItem.getQuoteItem().getQuote().getCopyToCsName();	

		creator = quoteItem.getQuoteItem().getQuote().getCreatedName();

		//updateInformation related info
		if(datas.size()>0)
		{
			selectedQuoteItemVo.setQuoteItem(quoteItem.getQuoteItem());
			this.setOriginalCost(quoteItem.getQuoteItem().getCost());
		}
		else
		{
			selectedQuoteItemVo = null;
		}

		//reassignSales related info
		salesMen = "";
		Iterator<QuoteItemVo> iterator = datas.iterator();
		for(int i = 0 ; i < datas.size() ; i++)
		{
			QuoteItemVo item = iterator.next();
			if(i == datas.size()-1)
			{
				salesMen += item.getQuoteItem().getQuote().getSales().getName() + ".";
			}
			else if(i == 0)
			{
				salesMen = item.getQuoteItem().getQuote().getSales().getName() + ", ";
			}
			else
			{
				salesMen += item.getQuoteItem().getQuote().getSales().getName() + ", ";
			}
		}

		//updateValidity related info
		removeShipmentValidity = false;

	}


	/**
	 * Update information.
	 */
	public void updateInformation()
	{

		//NEC Pagination changes: get the selected data from cache 
		Set<QuoteItemVo> cacheData = getCacheSelectionDatas();
		//validate no of quote item selected
		if(cacheData == null || cacheData.size()==0)
		{
			updateSuccess = false;

			errorMessage = ResourceMB.getText("wq.message.selQuoteItem");
			FacesContext.getCurrentInstance().addMessage("growl", new FacesMessage(FacesMessage.SEVERITY_ERROR,errorMessage, ""));
			return;
		}

		//Check if more than quote item is selected
		if(cacheData.size()>1)
		{
			updateSuccess = false;

			errorMessage = ResourceMB.getText("wq.message.updatedQuote");
			FacesContext.getCurrentInstance().addMessage("growl", new FacesMessage(FacesMessage.SEVERITY_ERROR, errorMessage, ""));
			return;
		}

		QuoteItem quoteItem = cacheData.iterator().next().getQuoteItem();

		//Set cost = 0 if it is null
		if(quoteItem.getCost() == null)
		{
			quoteItem.setCost(0.00);
		}
		/*fixed that When user uses the Update Information function in Quote Data Maintain page, it does not have the checking on Cost Indicator. 
		If the quote is capturing some old cost indicator, it will save as '-select-'. by Damonchen220180820  begin ========*/
		if(quoteItem.getCostIndicator()==null ||(quoteItem.getCostIndicator()!=null && quoteItem.getCostIndicator().equals(ResourceMB.getText("wq.label.sel"))))
		{
			updateSuccess = false;

			errorMessage = ResourceMB.getText("wq.message.invalidCostIndicator");
			FacesContext.getCurrentInstance().addMessage("growl", new FacesMessage(FacesMessage.SEVERITY_ERROR, errorMessage, ""));
			return;
		}
		
		/* by Damonchen220180820  end ========*/
		
		//fixed by DamonChen@20180320
        quoteItem.setResaleIndicator(quoteItem.getResaleIndicator()==null?null:quoteItem.getResaleIndicator().toUpperCase());
		LOG.info("Updating quote info " + quoteItem.getQuoteNumber());
		quoteItem.setResaleMargin(QuoteUtil.calculateResalesMargin(quoteItem.getCost(),quoteItem.getQuotedPrice()));// fix INC0064370 

		quoteItem.setReferenceMargin(null);
		quoteItem.setAction(ActionEnum.DM_UPDATE.name()); // add audit action 
		quoteSB.updateReferenceMarginForSalesAndCs(quoteItem);
		setLastUpdateInfo(quoteItem); // June 2014/06/20 fix setTheLstUpdateInfo
		quoteItem = quoteSB.updateQuoteItem(quoteItem);
		LOG.info(quoteItem.getQuoteNumber() + " updated");
		updateSuccess = true;

		List<QuoteItem> items = new ArrayList<QuoteItem>();
		items.add(quoteItem);
		createSAPQuote(items);

		search();		 
		errorMessage=ResourceMB.getParameterizedString("wq.message.addedSuccessfully", String.valueOf(quoteItem.getQuoteNumber()));
		FacesContext.getCurrentInstance().addMessage("growl", new FacesMessage(FacesMessage.SEVERITY_INFO,errorMessage , ""));
		RequestContext.getCurrentInstance().execute("PF('dialogUpdateInformationWidget').hide()");
	}

	/**
	 * Update validity.
	 */
	public void updateValidity()
	{
		//NEC Pagination changes: get the selected data from cache 
		Set<QuoteItemVo> cacheData = getCacheSelectionDatas();
		//validate no of quote item selected
		if(cacheData == null || cacheData.size()==0)
		{
			updateSuccess = false;

			errorMessage= ResourceMB.getText("wq.message.selQuoteItem");
			FacesContext.getCurrentInstance().addMessage("growl", new FacesMessage(FacesMessage.SEVERITY_ERROR,errorMessage, ""));
			return;
		}

		if(priceValidity != null && !priceValidity.equals(""))
		{
			try
			{
				Integer.parseInt(priceValidity);
			}
			catch(Exception e)
			{
				LOG.log(Level.SEVERE, "Error in price format, date is : "+priceValidity+", Reason for failure: "+e.getMessage());
				try
				{
					SDF.setLenient(false);
					SDF.parse(priceValidity);
				}
				catch(Exception ioe)
				{
					updateSuccess = false;

					errorMessage = ResourceMB.getText("wq.message.priceValidityFormat");
					FacesContext.getCurrentInstance().addMessage("growl", new FacesMessage(FacesMessage.SEVERITY_ERROR,errorMessage, ""));
					LOG.log(Level.SEVERE, "Error in price format, date is : "+priceValidity+", Reason for failure: "+e.getMessage(),e);
					return ;
				}
			}
		}
		Date poExpiryDate = null;
		if(sPoExpiryDate != null && !sPoExpiryDate.equals(""))
		{
			try 
			{
				SDF.setLenient(false);
				poExpiryDate = SDF.parse(sPoExpiryDate);
				poExpiryDate = new Date(poExpiryDate.getTime() + 1000 * 60 * 60 * 15);
			}
			catch(Exception ioe)
			{
				updateSuccess = false;
				LOG.log(Level.SEVERE, "Error in PO expiry date, date is : "+sPoExpiryDate+", Reason for failure: "+ioe.getMessage(),ioe);
				errorMessage= ResourceMB.getText("wq.message.poExpiryDate");
				FacesContext.getCurrentInstance().addMessage("growl", new FacesMessage(FacesMessage.SEVERITY_ERROR,errorMessage, ""));
				return ;
			}
		}

		Date quoteExpiryDate = null;
		if(sQuoteExpiryDate != null && !sQuoteExpiryDate.equals(""))
		{
			try 
			{
				SDF.setLenient(false);
				quoteExpiryDate = SDF.parse(sQuoteExpiryDate);
				quoteExpiryDate = new Date(quoteExpiryDate.getTime() + 1000 * 60 * 60 * 15);
			}
			catch(Exception ioe)
			{
				updateSuccess = false;
				LOG.log(Level.SEVERE, "Error in Quoete expiry date, date is : "+sQuoteExpiryDate+", Reason for failure: "+ioe.getMessage(),ioe);
				errorMessage= ResourceMB.getText("wq.message.quoteExpiryDate");
				FacesContext.getCurrentInstance().addMessage("growl", new FacesMessage(FacesMessage.SEVERITY_ERROR,errorMessage, ""));
				return ;
			}

			/*for(int i = 0 ; i < cacheData.size() ; i++)
			{
				QuoteItem quoteItem = selectedQuoteItemVos.get(i).getQuoteItem();
				if(quoteItem.getQuotationEffectiveDate() != null && quoteItem.getQuotationEffectiveDate().after(new Date(quoteExpiryDate.getTime() + 1000*60*60*9)))
				{

					DateFormat df =  new SimpleDateFormat("dd/MM/yyyy");


					Object[] dynamicParameters= {String.valueOf(df.format(quoteExpiryDate)), String.valueOf(df.format(quoteItem.getQuotationEffectiveDate())),String.valueOf(df.format(quoteItem.getQuoteNumber()))};
					errorMessage=ResourceMB.getParameterizedString("wq.expiryDateQuoteEffectiveDate", dynamicParameters);
					FacesContext.getCurrentInstance().addMessage("growl", new FacesMessage(FacesMessage.SEVERITY_ERROR,errorMessage, ""));
					return;
				}

			}*/

			for (Iterator<QuoteItemVo> iterator = cacheData.iterator(); iterator.hasNext();) {
				QuoteItemVo quoteItemVo = iterator.next();
				QuoteItem quoteItem = quoteItemVo.getQuoteItem();
				if(quoteItem.getQuotationEffectiveDate() != null && quoteItem.getQuotationEffectiveDate().after(new Date(quoteExpiryDate.getTime() + 1000*60*60*9)))
				{

					DateFormat df =  new SimpleDateFormat("dd/MM/yyyy");

                    //a bug caused by NEC,  so need to change , by Damonchen
					Object[] dynamicParameters= {String.valueOf(df.format(quoteExpiryDate)), String.valueOf(df.format(quoteItem.getQuotationEffectiveDate())),""+quoteItem.getQuoteNumber()};
					errorMessage=ResourceMB.getParameterizedString("wq.expiryDateQuoteEffectiveDate", dynamicParameters);
					FacesContext.getCurrentInstance().addMessage("growl", new FacesMessage(FacesMessage.SEVERITY_ERROR,errorMessage, ""));
					return;
				}
			}

		}

		Date shipmentValidityDate = null;
		if(shipmentValidity != null && !shipmentValidity.equals(""))
		{
			try 
			{
				SDF.setLenient(false);
				shipmentValidityDate = SDF.parse(shipmentValidity);
			}
			catch(Exception ioe)
			{
				updateSuccess = false;
				LOG.log(Level.SEVERE, "Error in Quoete expiry date, date is : "+shipmentValidity+", Reason for failure: "+ioe.getMessage(),ioe);
				errorMessage= ResourceMB.getText("wq.message.shipmentValidity");
				FacesContext.getCurrentInstance().addMessage("growl", new FacesMessage(FacesMessage.SEVERITY_ERROR,errorMessage, ""));
				return ;
			}
		}

		List<QuoteItem> items = new ArrayList<QuoteItem>();
		for (Iterator<QuoteItemVo> iterator = cacheData.iterator(); iterator.hasNext();) {
			QuoteItemVo quoteItemVo = iterator.next();
			QuoteItem quoteItem = quoteItemVo.getQuoteItem();
			if(priceValidity != null && !priceValidity.equals(""))
			{
				quoteItem.setPriceValidity(priceValidity);
			}

			if(poExpiryDate != null)
			{
				quoteItem.setPoExpiryDate(poExpiryDate);
			}

			if(quoteExpiryDate != null)
			{
				quoteItem.setQuoteExpiryDate(quoteExpiryDate);
			}

			if(removeShipmentValidity == true)
			{
				quoteItem.setShipmentValidity(null);
			}
			else if(shipmentValidity != null && !shipmentValidity.equals(""))
			{
				quoteItem.setShipmentValidity(shipmentValidityDate);
			}

			String currentInternalComment = quoteItem.getInternalComment();
			if(currentInternalComment == null || currentInternalComment.equals(""))
			{
				if(internalComment != null && !internalComment.equals(""))
					currentInternalComment = internalComment;
			}
			else
			{
				if(internalComment != null && !internalComment.equals(""))
					currentInternalComment += " | " + internalComment;
			}
			quoteItem.setAction(ActionEnum.DM_UPDATE_VALIDITY.name()); // add audit action 
			quoteItem.setInternalComment(currentInternalComment);
			setLastUpdateInfo(quoteItem); // June 2014/06/20 fix setTheLstUpdateInfo
			items.add(quoteItem);

		}

		if(cacheData.size()>0)
		{
			ArrayList<QuoteItemVo> selectionData = new ArrayList<>();
			selectionData.addAll(cacheData);
			quoteSB.saveQuoteItems(selectionData);
			updateSuccess = true;

			if(items.size()>0){
				createSAPQuote(items);
			}

			search();

			errorMessage= ResourceMB.getText("wq.message.quoteValidityUpdated");
			FacesContext.getCurrentInstance().addMessage("growl", new FacesMessage(FacesMessage.SEVERITY_INFO,errorMessage, ""));
		}
	} 

	/**
	 * Batch invalidate quote.
	 */
	public void batchInvalidateQuote()
	{
		long batStart = System.currentTimeMillis();
		errorMessage = "";
		//NEC Pagination changes: get the selected data from cache 
		Set<QuoteItemVo> cacheDatas = getCacheSelectionDatas();

		//validate no of quote item selected
		if(cacheDatas!=null&&cacheDatas.isEmpty())
		{

			errorMessage = ResourceMB.getText("wq.message.selQuoteItem");
			updateSuccess = false;
			FacesContext.getCurrentInstance().addMessage("growl", new FacesMessage(FacesMessage.SEVERITY_ERROR,errorMessage, ""));
			return;
		}

		invalidatedQuoteNumbers = new ArrayList<String>();

		List<QuoteItem> items = new ArrayList<QuoteItem>();
		Iterator<QuoteItemVo> iterator = cacheDatas.iterator();
		while (iterator.hasNext()) {
			QuoteItemVo quoteItemVo = iterator.next();
			try
			{
				quoteItemVo.getQuoteItem().setStage(QuoteSBConstant.QUOTE_STAGE_INVALID);
				LOG.info("Invalidating " + quoteItemVo.getQuoteItem().getQuoteNumber());
				quoteItemVo.getQuoteItem().setAction(ActionEnum.DM_BATCH_INVALIDATE.name()); // add audit action 
				setLastUpdateInfo(quoteItemVo.getQuoteItem()); // June 2014/06/20 fix setTheLstUpdateInfo
				QuoteItem quoteItem = quoteSB.updateQuoteItem(quoteItemVo.getQuoteItem());
				LOG.info(quoteItem.getQuoteNumber() + " is now invalid");
				invalidatedQuoteNumbers.add(quoteItem.getQuoteNumber());
				updateSuccess = true;
				errorMessage += "[" + quoteItem.getQuoteNumber() + "] ";
			}
			catch(Exception e)
			{
				//Optimistic Lock Exception issue, we refresh the data and retry invalidate sequence
				/*
				LOG.severe("Saving invalid status for " + quoteItem.getQuoteNumber()  + " to database failed, refreshing data and retrying invalidate sequence");
				quoteItem = quoteSB.findQuoteItemByQuoteNumber(quoteItem.getQuoteNumber());
				quoteItem.setStage(QuoteSBConstant.QUOTE_STAGE_INVALID);
				LOG.info("Invalidating " + quoteItem.getQuoteNumber());
				setLastUpdateInfo(quoteItem); // June 2014/06/20 fix setTheLstUpdateInfo
				quoteItem = quoteSB.updateQuoteItem(quoteItem);
				LOG.info(quoteItem.getQuoteNumber() + " is now invalid");
				updateSuccess = true;
				errorMessage += "[" + quoteItem.getQuoteNumber() + "] ";
				 */
				LOG.log(Level.SEVERE, "Saving invalid status for " + quoteItemVo.getQuoteItem().getQuoteNumber()  + " to database failed. Reason for failure: "+MessageFormatorUtil.getParameterizedStringFromException(e), e);
				updateSuccess = false;

				errorMessage = ResourceMB.getParameterizedString("wq.message.invalidStatusDatabaseFailed",String.valueOf((quoteItemVo.getQuoteItem().getQuoteNumber())));
				search();
				FacesContext.getCurrentInstance().addMessage("growl", new FacesMessage(FacesMessage.SEVERITY_INFO,errorMessage, ""));
				return;
			}
			
			invalidate = "invalidateOnly";
			EXCSINGLE.execute(()-> {
				try {
					String mailFrom = "Asia-AEMC.QuoteCentre@Avnet.com";
					String signContent = "";
					String bizUnitName = user.getDefaultBizUnit().getName();

					//Email content
					String emailContent = "Dear recipient(s):";
					/*emailContent += "\n\n" + ResourceMB.getParameterizedString("wq.message.emailValidated", String.valueOf(quoteItem.getQuoteNumber()));*/
					emailContent += "\n\n" + quoteItemVo.getQuoteItem().getQuoteNumber() + " has been invalidated.";
					emailContent += "\n\nReason: " + emailInfo;
					emailContent += "\n\nBest Regards,";
					if (!QuoteUtil.isEmpty(bizUnitName)) {
						mailFrom = sysMaintSB.getEmailAddress(bizUnitName);
						signContent = "\n" + sysMaintSB.getEmailSignName(bizUnitName)
						+ "\n" + sysMaintSB.getEmailHotLine(bizUnitName)
						+ "\nEmail Box: "
						+ sysMaintSB.getEmailSignContent(bizUnitName);
					}
					createMailwithValues(signContent,emailContent,quoteItemVo.getQuoteItem(),mailFrom);
				} catch (Exception e) {
					LOG.log(Level.SEVERE, "Send email fial for " + quoteItemVo.getQuoteItem().getQuoteNumber() + ".", e);
				}
			});
			
			items.add(quoteItemVo.getQuoteItem());

		}
		long start = System.currentTimeMillis();
		createSAPQuote(items);
		LOG.info("createSAPQuote(items) take time " +(System.currentTimeMillis()-start));
		errorMessage+= ResourceMB.getText("wq.message.isInvalid");
		search();
		FacesContext.getCurrentInstance().addMessage("growl", new FacesMessage(FacesMessage.SEVERITY_INFO,errorMessage, ""));
		LOG.info("batchInvalidateQuote() take time " +(System.currentTimeMillis()-batStart));
	}
	
	/**
	 * update the last update information for fix setTheLstUpdateInfo.
	 *
	 * @param qi the new last update info
	 */
	private void setLastUpdateInfo(QuoteItem qi) {
		qi.setLastUpdatedBy(user.getEmployeeId());
		qi.setLastUpdatedName(user.getName());
		Date date = new Date();
		qi.setLastUpdatedOn(date);
		//		qi.setLastUpdatedQc(user.getEmployeeId());
		//		qi.setLastUpdatedQcName(user.getName());
		//		qi.setLastQcUpdatedOn(date);
	}


	/**
	 * Invalidate finished quote.
	 *
	 * @throws WebQuoteException the web quote exception
	 */
	public void invalidateFinishedQuote() throws WebQuoteException
	{
		//NEC Pagination changes: get the selected data from cache 
		Set<QuoteItemVo> cacheData = getCacheSelectionDatas();
		//validate no of quote item selected
		if(cacheData == null || cacheData.size()==0)
		{

			errorMessage= ResourceMB.getText("wq.message.selQuoteItem");
			updateSuccess = false;
			FacesContext.getCurrentInstance().addMessage("growl", new FacesMessage(FacesMessage.SEVERITY_ERROR,errorMessage, ""));
			return;
		}

		//Check if more than 1 quote item is selected
		if(cacheData.size()>1)
		{

			errorMessage= ResourceMB.getText("wq.message.finishedQuote");
			updateSuccess = false;
			FacesContext.getCurrentInstance().addMessage("growl", new FacesMessage(FacesMessage.SEVERITY_ERROR,errorMessage, ""));
			return ;
		}

		QuoteItemVo quoteItemVo = cacheData.iterator().next();

		//Check quote item status
		if(!quoteItemVo.getQuoteItem().getStage().equals(QuoteSBConstant.QUOTE_STAGE_FINISH) && !quoteItemVo.getQuoteItem().getStage().equals(QuoteSBConstant.QUOTE_STAGE_PENDING))
		{
			errorMessage=ResourceMB.getParameterizedString("wq.message.statusFinishPending", String.valueOf((quoteItemVo.getQuoteItem().getQuoteNumber())));
			updateSuccess = false;
			FacesContext.getCurrentInstance().addMessage("growl", new FacesMessage(FacesMessage.SEVERITY_ERROR,errorMessage, ""));
			return ;
		}
		if(invalidate.equals("invalidateAndGenerate"))
		{
			
			// can't generate a new one if the quote is from Qbuilder,added  by DamonChen@20180820
			if(quoteItemVo.getQuoteItem().getRequestedQty()==null)
			{

				errorMessage= ResourceMB.getText("wq.message.CanNotCreateQBQuote");
				updateSuccess = false;
				FacesContext.getCurrentInstance().addMessage("growl", new FacesMessage(FacesMessage.SEVERITY_ERROR,errorMessage, ""));
				return ;
			}
		}

		//Invalid Quote Item
		try
		{
			quoteItemVo.getQuoteItem().setStage(QuoteSBConstant.QUOTE_STAGE_INVALID);
			quoteItemVo.getQuoteItem().setAction(ActionEnum.DM_INVALIDATE.name()); // add audit action 
			quoteItemVo.getQuoteItem().setQuoteExpiryDate(new Date()); //set quote Expiry Date (moved from QuoteItemListener)
			LOG.info("Invalidating " + quoteItemVo.getQuoteItem().getQuoteNumber());
			setLastUpdateInfo(quoteItemVo.getQuoteItem()); // June 2014/06/20 fix setTheLstUpdateInfo
			QuoteItem quoteItem = quoteSB.updateQuoteItem(quoteItemVo.getQuoteItem());
			LOG.info(quoteItemVo.getQuoteItem().getQuoteNumber() + " is now invalid");
			updateSuccess = true;

			errorMessage =quoteItemVo.getQuoteItem().getQuoteNumber() +" "+ ResourceMB.getText("wq.message.isNowInvalid");

		}
		catch(Exception e)
		{
			//Optimistic Lock Exception issue, we refresh the data and retry invalidate sequence

			/*
			LOG.severe("Saving invalid status for " + quoteItem.getQuoteNumber()  + " to database failed, refreshing data and retrying invalidate sequence");
			quoteItem = quoteSB.findQuoteItemByQuoteNumber(quoteItem.getQuoteNumber());
			quoteItem.setStage(QuoteSBConstant.QUOTE_STAGE_INVALID);
			LOG.info("Invalidating " + quoteItem.getQuoteNumber());
			setLastUpdateInfo(quoteItem); // June 2014/06/20 fix setTheLstUpdateInfo
			quoteItem = quoteSB.updateQuoteItem(quoteItem);
			LOG.info(quoteItem.getQuoteNumber() + " is now invalid");
			updateSuccess = true;
			errorMessage = quoteItem.getQuoteNumber() + " is now invalid";
			 */
			LOG.log(Level.SEVERE, "Saving invalid status for " + quoteItemVo.getQuoteItem().getQuoteNumber()+ ", Reason for failure: "+MessageFormatorUtil.getParameterizedStringFromException(e), e);
			updateSuccess = false;
			errorMessage=ResourceMB.getParameterizedString("wq.message.invalidStatusDatabaseFailed", String.valueOf((quoteItemVo.getQuoteItem().getQuoteNumber())));
			search();
			FacesContext.getCurrentInstance().addMessage("growl", new FacesMessage(FacesMessage.SEVERITY_INFO,errorMessage, ""));
			return;
		}

		//Email content
		String emailContent = "Dear recipient(s):";
		emailContent += "\n\n" + quoteItemVo.getQuoteItem().getQuoteNumber() + " has been invalidated.";
		emailContent += "\n\nReason: " + emailInfo;

		//Generate a new Quote Item
		QuoteItem newQuoteItem = null;
		if(invalidate.equals("invalidateAndGenerate"))
		{
			newQuoteItem = new QuoteItem();
			String newQuoteNumber = quoteSB.populateQuoteNumberOnly(quoteItemVo.getQuoteItem().getQuote()
					.getBizUnit());
			Date date = new Date();
			newQuoteItem = setQuoteItemValue(newQuoteItem,quoteItemVo.getQuoteItem(),newQuoteNumber,date);
			List<Attachment> oldAttachments = quoteItemVo.getQuoteItem().getAttachments();
			newQuoteItem = setValueForOldnNewAttachmentQuoteItem(oldAttachments,newQuoteItem);

			quoteSB.applyDefaultCostIndicatorLogic(newQuoteItem, true);

			//Saving new quote item into the database
			try
			{	
				LOG.info("Generating New Quote Item " + newQuoteItem.getQuoteNumber());
				newQuoteItem.setAction(ActionEnum.DM_INVALIDATE_CREATE.name()); // add audit action 
				quoteSB.saveQuoteItem(newQuoteItem);
				LOG.info(newQuoteItem.getQuoteNumber() + " generated");

				errorMessage=errorMessage +" "+ ResourceMB.getParameterizedString("wq.message.generated", String.valueOf(newQuoteItem.getQuoteNumber()))+"\n";
				newQuoteNoStr = newQuoteItem.getQuoteNumber();

				//Appending Email
				emailContent += "\n\nA new quote has been generated in the same form and QC will release it to you soon.\nThank You."; 
			}
			catch(Exception e)
			{
				//Typically eclipselink-cannot be updated because it has changed or been deleted since it was last read error.
				//We now grab the data from the database, and update it with the values that user sees then regenerate the RFQ.
				LOG.log(Level.SEVERE, "Error generating new quote number : "+quoteItemVo.getQuoteItem().getQuoteNumber()+", Exception message: "+MessageFormatorUtil.getParameterizedStringFromException(e),e);
				newQuoteItem = quoteSB.findQuoteItemByQuoteNumber(quoteItemVo.getQuoteItem().getQuoteNumber());
				newQuoteItem = setQuoteItemValue(newQuoteItem,quoteItemVo.getQuoteItem(),newQuoteNumber,date);
				oldAttachments = quoteItemVo.getQuoteItem().getAttachments();
				newQuoteItem = setValueForOldnNewAttachmentQuoteItem(oldAttachments,newQuoteItem);
				LOG.info("Generating New Quote Item " + newQuoteItem.getQuoteNumber());
				newQuoteItem.setAction(ActionEnum.DM_INVALIDATE_CREATE.name()); // add audit action 
				quoteSB.saveQuoteItem(newQuoteItem);
				LOG.info(newQuoteItem.getQuoteNumber() + " generated");
				errorMessage+=ResourceMB.getParameterizedString("wq.message.generated", String.valueOf(newQuoteItem.getQuoteNumber()))+"\n";
				newQuoteNoStr = newQuoteItem.getQuoteNumber();

				//Appending Email)
				emailContent += "\n\nA new quote has been generated in the same form and QC will release it to you soon.\nThank You."; 
			}
		}
		else
		{
			invalidate = "invalidateOnly";
		}

		String mailFrom = "Asia-AEMC.QuoteCentre@Avnet.com";
		String signContent = "";
		String bizUnitName = user.getDefaultBizUnit().getName();

		emailContent += "\n\nBest Regards,";
		if (!QuoteUtil.isEmpty(bizUnitName)) {
			mailFrom = sysMaintSB.getEmailAddress(bizUnitName);
			signContent = "\n" + sysMaintSB.getEmailSignName(bizUnitName)
			+ "\n" + sysMaintSB.getEmailHotLine(bizUnitName)
			+ "\nEmail Box: "
			+ sysMaintSB.getEmailSignContent(bizUnitName);
		}

		QuoteItem quoteItem = createMailwithValues(signContent,emailContent,quoteItemVo.getQuoteItem(),mailFrom);

		List<QuoteItem> items = new ArrayList<QuoteItem>();
		items.add(quoteItemVo.getQuoteItem());
		long start = System.currentTimeMillis();
		createSAPQuote(items);
		LOG.info("createSAPQuote(items) take time " +(System.currentTimeMillis()-start));
		start = System.currentTimeMillis();
		search();
		LOG.info("search() take time " +(System.currentTimeMillis()-start));
		FacesContext.getCurrentInstance().addMessage("growl", new FacesMessage(FacesMessage.SEVERITY_INFO,errorMessage, ""));
	}


	/**
	 * Creates the mailwith values.
	 *
	 * @param signContent the sign content
	 * @param emailContent the email content
	 * @param quoteItem the quote item
	 * @param mailFrom the mail from
	 * @return the quote item
	 */
	private QuoteItem createMailwithValues(String signContent, String emailContent, QuoteItem quoteItem, String mailFrom){
		if(signContent!= null &&  signContent.contains("<br/>")){
			signContent=signContent.replace("<br/>", "\n");

		}
		emailContent += signContent;

		MailInfoBean mailBean = new MailInfoBean();
		List<String> mailToList = new ArrayList<String>();
		//Processing email recipient;
		String cs = quoteItem.getQuote().getCopyToCS();

		if(cs!=null && cs.length()!=0)
		{
			String [] csArray =  cs.split(";");
			for(int i = 0 ; i < csArray.length ; i++)
			{
				String csEmail = userSB.findByEmployeeIdLazily(csArray[i]).getEmailAddress();
				mailToList.add(csEmail);
			}
		}
		mailToList.add(quoteItem.getQuote().getSales().getEmailAddress());
		User createdBy = userSB.findByEmployeeIdLazily(quoteItem.getQuote().getCreatedBy());
		if(createdBy!=null){
			mailToList.add(createdBy.getEmailAddress());
		}
		//Email recipient
		if(mailToList!=null && mailToList.size()!=0)
		{
			try
			{
				//Emailing relevant parties
				LOG.info("Emailing relevant parties");
				mailBean.setMailTo(mailToList);
				mailBean.setMailContent(emailContent);
				mailBean.setMailFrom(mailFrom);
				mailBean.setMailSubject("Quote " + quoteItem.getQuoteNumber() + " invalidated.");
				mailUtilsSB.sendTextMail(mailBean);
			} 
			catch(Exception e)
			{
				LOG.log(Level.SEVERE, "Error occured when sending email for quote number : "+quoteItem.getQuoteNumber() +", exception message: "+ e.getMessage(), e);
			}
		}
		return quoteItem;
	}

	/**
	 * Sets the value for oldn new attachment quote item.
	 *
	 * @param oldAttachments the old attachments
	 * @param newQuoteItem the new quote item
	 * @return the quote item
	 * @throws WebQuoteException the web quote exception
	 */
	private QuoteItem setValueForOldnNewAttachmentQuoteItem(List<Attachment> oldAttachments, QuoteItem newQuoteItem) throws WebQuoteException {
		if(oldAttachments != null && oldAttachments.size() != 0){
			List<Attachment> newAttachments = new ArrayList<Attachment>();
			for(Attachment oldAttachment : oldAttachments){
				Attachment  newattachment = new Attachment();
				byte[] fileByteArray = oldAttachment.getFileImage();
				if(fileByteArray == null || !(fileByteArray.length > 0)) {
					// modified by Yang,Lenon for read attachments from host server(2015.12.31)
					String fileRootPath = sysConfigSB.getProperyValue(QuoteSBConstant.ATTACHMENT_ROOT_PATH);
					String realFilePath = fileRootPath + File.separator + oldAttachment.getFilePath() + File.separator + oldAttachment.getFileNameActual();
					fileByteArray = FileUtil.file2Byte(realFilePath);
				} 
				newattachment.setFileImage(fileByteArray);
				//newattachment.setFileImage(oldAttachment.getFileImage());
				newattachment.setFileName(oldAttachment.getFileName());
				newattachment.setNewAttachment(false);
				newattachment.setQuote(oldAttachment.getQuote());
				newattachment.setQuoteItem(newQuoteItem);
				newattachment.setType(oldAttachment.getType());
				newAttachments.add(newattachment);
			}
			newQuoteItem.setAttachments(newAttachments);
		}
		return newQuoteItem;
	}

	/**
	 * Sets the quote item value.
	 *
	 * @param newQuoteItem the new quote item
	 * @param quoteItem the quote item
	 * @param newQuoteNumber the new quote number
	 * @param date the date
	 * @return the quote item
	 */
	private QuoteItem setQuoteItemValue(QuoteItem newQuoteItem, QuoteItem quoteItem, String newQuoteNumber, Date date) {
		//Fix multiCurr 20181113 
		
		newQuoteItem.setBuyCurr(quoteItem.getBuyCurr());
		newQuoteItem.setRfqCurr(quoteItem.getRfqCurr());
		newQuoteItem.setExRateFnl(quoteItem.getExRateFnl());;
		newQuoteItem.setExRateRfq(quoteItem.getExRateRfq());
		//newQuoteItem.set;
		newQuoteItem.setQuote(quoteItem.getQuote());
		newQuoteItem.setSubmissionDate(date);
		newQuoteItem.setQuoteNumber(newQuoteNumber);
		newQuoteItem.setAlternativeQuoteNumber(newQuoteNumber);
		newQuoteItem.setEndCustomer(quoteItem.getEndCustomer());
		newQuoteItem.setShipToCustomer(quoteItem.getShipToCustomer());
		newQuoteItem.setSoldToCustomer(quoteItem.getSoldToCustomer());
		newQuoteItem.setRequestedMfr(quoteItem.getRequestedMfr());
		newQuoteItem.setRequestedPartNumber(quoteItem.getRequestedPartNumber());
		newQuoteItem.setQuotedMaterial(quoteItem.getQuotedMaterial());
		newQuoteItem.setApplication(quoteItem.getApplication());
		newQuoteItem.setAqcc(quoteItem.getAqcc());
		newQuoteItem.setDescription(quoteItem.getDescription());

		newQuoteItem.setProjectName(quoteItem.getProjectName());
		newQuoteItem.setInternalTransferComment(quoteItem
				.getInternalTransferComment());
		newQuoteItem.setBottomPrice(quoteItem.getBottomPrice());
		newQuoteItem.setMinSellPrice(quoteItem.getMinSellPrice());
		newQuoteItem.setPriceListRemarks1(quoteItem.getPriceListRemarks1());
		newQuoteItem.setPriceListRemarks2(quoteItem.getPriceListRemarks2());
		newQuoteItem.setPriceListRemarks3(quoteItem.getPriceListRemarks3());
		newQuoteItem.setPriceListRemarks4(quoteItem.getPriceListRemarks4());
		newQuoteItem.setPriceValidity(quoteItem.getPriceValidity());
		newQuoteItem.setResaleMargin(quoteItem.getResaleMargin());
		newQuoteItem.setTargetResale(quoteItem.getTargetResale());
		newQuoteItem.setReferenceMargin(quoteItem.getReferenceMargin());
		newQuoteItem.setAllocationFlag(quoteItem.getAllocationFlag());

		newQuoteItem.setBmtCheckedFlag(quoteItem.getBmtCheckedFlag());
		newQuoteItem.setValidFlag(quoteItem.getValidFlag());
		newQuoteItem.setBomNumber(quoteItem.getBomNumber());
		newQuoteItem.setBomRemarks(quoteItem.getBomRemarks());
		newQuoteItem.setCancellationWindow(quoteItem.getCancellationWindow());
		newQuoteItem.setCompetitorInformation(quoteItem.getCompetitorInformation());
		newQuoteItem.setCost(quoteItem.getCost());
		newQuoteItem.setCostIndicator(quoteItem.getCostIndicator());
		newQuoteItem.setDrmsNumber(quoteItem.getDrmsNumber());
		newQuoteItem.setEau(quoteItem.getEau());
		newQuoteItem.setEnquirySegment(quoteItem.getEnquirySegment());
		newQuoteItem.setFirstRfqCode(quoteItem.getFirstRfqCode());
		newQuoteItem.setInternalComment(quoteItem.getInternalComment());
		newQuoteItem.setItemSeq(quoteItem.getItemSeq());
		newQuoteItem.setLastPmUpdatedOn(quoteItem.getLastPmUpdatedOn());
		newQuoteItem.setLastQcUpdatedOn(quoteItem.getLastQcUpdatedOn());
		newQuoteItem.setLastUpdatedOn(date);
		newQuoteItem.setLastUpdatedPm(quoteItem.getLastUpdatedPm());
		newQuoteItem.setLastUpdatedPmName(quoteItem.getLastUpdatedPmName());
		newQuoteItem.setLastUpdatedQc(quoteItem.getLastUpdatedQc());
		newQuoteItem.setLastUpdatedQcName(quoteItem.getLastUpdatedQcName());
		newQuoteItem.setLeadTime(quoteItem.getLeadTime());
		newQuoteItem.setDesignLocation(quoteItem.getDesignLocation());
		newQuoteItem.setLoaFlag(quoteItem.getLoaFlag());
		newQuoteItem.setQuoteType(quoteItem.getQuoteType());

		newQuoteItem.setMoq(quoteItem.getMoq());
		newQuoteItem.setMov(quoteItem.getMov());
		newQuoteItem.setMpSchedule(quoteItem.getMpSchedule());
		newQuoteItem.setMpq(quoteItem.getMpq());
		newQuoteItem.setMultiUsageFlag(quoteItem.getMultiUsageFlag());
		newQuoteItem.setPmoq(quoteItem.getPmoq());
		newQuoteItem.setPoExpiryDate(quoteItem.getPoExpiryDate());

		newQuoteItem.setPpSchedule(quoteItem.getPpSchedule());
		newQuoteItem.setQcComment(quoteItem.getQcComment());
		newQuoteItem.setQtyIndicator(quoteItem.getQtyIndicator());
		newQuoteItem.setQuoteExpiryDate(quoteItem.getQuoteExpiryDate());
		newQuoteItem.setQuotedQty(quoteItem.getQuotedQty());

		newQuoteItem.setQuotedPrice(quoteItem.getQuotedPrice());
		newQuoteItem.setRequestedQty(quoteItem.getRequestedQty());
		newQuoteItem.setResaleIndicator(quoteItem.getResaleIndicator());
		newQuoteItem.setResaleMax(quoteItem.getResaleMax());
		newQuoteItem.setResaleMin(quoteItem.getResaleMin());
		newQuoteItem.setRescheduleWindow(quoteItem.getRescheduleWindow());
		newQuoteItem.setRevertVersion(quoteItem.getRevertVersion());
		newQuoteItem.setShipmentValidity(quoteItem.getShipmentValidity());
		newQuoteItem.setSprFlag(quoteItem.getSprFlag());
		newQuoteItem.setStatus(QuoteSBConstant.RFQ_STATUS_QC);
		newQuoteItem.setStage(QuoteSBConstant.QUOTE_STAGE_PENDING);
		newQuoteItem.setSupplierDrNumber(quoteItem.getSupplierDrNumber());
		newQuoteItem.setTargetResale(quoteItem.getTargetResale());
		newQuoteItem.setTermsAndConditions(quoteItem.getTermsAndConditions());
		newQuoteItem.setVendorDebitNumber(quoteItem.getVendorDebitNumber());
		newQuoteItem.setVendorQuoteNumber(quoteItem.getVendorQuoteNumber());
		newQuoteItem.setSentOutTime(date);
		newQuoteItem.setReasonForRefresh(quoteItem.getReasonForRefresh());
		newQuoteItem.setRemarks(quoteItem.getRemarks());
		//fix ticket INC0378628 
		if(null!=quoteItem.getEndCustomer()) {
			newQuoteItem.setEndCustomerName(quoteItem.getEndCustomer().getCustomerFullName());
		}else {
			newQuoteItem.setEndCustomerName(quoteItem.getEndCustomerName());
		}


		newQuoteItem.setShipToCustomerName(quoteItem.getShipToCustomerName());
		newQuoteItem.setCustomerType(quoteItem.getCustomerType());

		//andy modify 2.2
		newQuoteItem.setMaterialTypeId(quoteItem.getMaterialTypeId());
		//newQuoteItem.setMaterialType(quoteItem.getMaterialType());

		newQuoteItem.setProgramType(quoteItem.getProgramType());
		newQuoteItem.setProductGroup1(quoteItem.getProductGroup1());
		newQuoteItem.setProductGroup2(quoteItem.getProductGroup2());
		newQuoteItem.setProductGroup3(quoteItem.getProductGroup3());
		newQuoteItem.setProductGroup4(quoteItem.getProductGroup4());
		//newQuoteItem.setAttachments(quoteItem.getAttachments());
		newQuoteItem.setLastUpdatedBy(quoteItem.getLastUpdatedBy());
		newQuoteItem.setLastUpdatedName(quoteItem.getLastUpdatedName());
		newQuoteItem.setVendorQuoteQty(quoteItem.getVendorQuoteQty());
		newQuoteItem.setFinalQuotationPrice(quoteItem.getFinalQuotationPrice());
		newQuoteItem.setQuotedMfr(quoteItem.getQuotedMfr());
		newQuoteItem.setQuotedPartNumber(quoteItem.getQuotedPartNumber());
		newQuoteItem.setCustomerGroupId(quoteItem.getCustomerGroupId());
		return newQuoteItem;
	}

	/**
	 * Reassign sales.
	 */
	public void reassignSales()
	{
		User user = null;
		//NEC Pagination changes: get the selected data from cache 
		Set<QuoteItemVo> cacheDatas = getCacheSelectionDatas();
		//validate no of quote item selected
		if(cacheDatas == null || cacheDatas.size()==0)
		{
			updateSuccess = false;

			errorMessage= ResourceMB.getText("wq.message.selQuoteItem");
			FacesContext.getCurrentInstance().addMessage("growl", new FacesMessage(FacesMessage.SEVERITY_ERROR,errorMessage, ""));
			return;
		}

		//validate internalComment field
		if(internalComment==null || internalComment.equals(""))
		{
			updateSuccess = false;

			errorMessage = ResourceMB.getText("wq.message.internalCommentEmpty");
			FacesContext.getCurrentInstance().addMessage("growl", new FacesMessage(FacesMessage.SEVERITY_ERROR,errorMessage, ""));
			return;
		}

		//validate sales field
		if(salesMan==null || salesMan.equals(""))
		{
			updateSuccess = false;

			errorMessage= ResourceMB.getText("wq.message.salemanempID");
			FacesContext.getCurrentInstance().addMessage("growl", new FacesMessage(FacesMessage.SEVERITY_ERROR,errorMessage, ""));
			return;
		}
		else
		{
			try
			{
				String [] salesRoleNames = {"ROLE_SALES","ROLE_INSIDE_SALES","ROLE_SALES_MANAGER","ROLE_SALES_DIRECTOR","ROLE_SALES_GM"};
				user = userSB.findSalesByEmployeeId(salesMan.trim(), salesRoleNames);

				if(user == null)
				{
					updateSuccess = false;

					errorMessage = ResourceMB.getText("wq.message.empIDNotfound");
					FacesContext.getCurrentInstance().addMessage("growl", new FacesMessage(FacesMessage.SEVERITY_ERROR,errorMessage, ""));
					return;
				}



				for (Iterator<QuoteItemVo> iterator = cacheDatas.iterator(); iterator.hasNext();) {
					QuoteItemVo quoteItemVo =  iterator.next();
					List<BizUnit> oldSalesRegions = quoteItemVo.getQuoteItem().getQuote().getSales().getBizUnits();
					List<BizUnit> newSalesRegions = user.getBizUnits();

					if(CollectionUtils.intersection(oldSalesRegions, newSalesRegions).size()==0)
					{
						updateSuccess = false;

						errorMessage= ResourceMB.getText("wq.message.quoteAss");
						FacesContext.getCurrentInstance().addMessage("growl", new FacesMessage(FacesMessage.SEVERITY_ERROR,errorMessage, ""));
						return;
					}
				}
			}
			catch(Exception e)
			{
				updateSuccess = false;
				LOG.log(Level.SEVERE, "Error in reasigning, Exception message: "+MessageFormatorUtil.getParameterizedStringFromException(e), e);
				errorMessage = ResourceMB.getText("wq.message.empID");
				FacesContext.getCurrentInstance().addMessage("growl", new FacesMessage(FacesMessage.SEVERITY_ERROR,errorMessage, ""));
				return;
			}
		}

		try{
			if(reassignSalesSelection.equals("selectedRecordsOnly"))
			{
				for (Iterator<QuoteItemVo> iterator = cacheDatas.iterator(); iterator.hasNext();) {
					QuoteItemVo quoteItemVo =  iterator.next();
					//Setting sales to quote
					Quote quote = quoteItemVo.getQuoteItem().getQuote();
					quote.setSales(user);

					//after assign to sale man the team of quote is the same as quote.
					Team team = user.getTeam();
					quote.setTeam(team);

					//Saving quote
					LOG.info("Assigning " + quote.getFormNumber() + " to " + user.getName());
					quoteSB.saveQuote(quote);
					LOG.info("Assigned");

					//Append Internal Comment
					QuoteItem quoteItem = quoteItemVo.getQuoteItem();
					String currentInternalComment = quoteItem.getInternalComment();
					if(currentInternalComment == null || currentInternalComment.equals(""))
					{
						if(internalComment != null && !internalComment.equals(""))
							currentInternalComment = internalComment;
					}
					else
					{
						if(internalComment != null && !internalComment.equals(""))
							currentInternalComment += " | " + internalComment;
					}
					quoteItem.setInternalComment(currentInternalComment);
					quoteItem.setAction(ActionEnum.DM_REASSIGN_SALE.name()); // add audit action 

					//Saving quoteItem
					LOG.info("Saving InternalComment " + quoteItem.getQuoteNumber());
					setLastUpdateInfo(quoteItem); // June 2014/06/20 fix setTheLstUpdateInfo
					quoteSB.updateQuoteItem(quoteItem);
					LOG.info("Saved");
				}
			}
			else if(reassignSalesSelection.equals("allRecords"))
			{
				//Get all Sales;
				for (Iterator<QuoteItemVo> iterator = cacheDatas.iterator(); iterator.hasNext();) {
					QuoteItemVo quoteItemVo =  iterator.next();

					Quote quote = quoteItemVo.getQuoteItem().getQuote();
					List<Quote> quoteLists = quoteSB.findQuoteByEmployeeID(quote.getSales().getEmployeeId());
					for(int j = 0 ; j < quoteLists.size() ; j++)
					{
						//setting sales
						Quote quoteUnderSales = quoteLists.get(j);
						quoteUnderSales.setSales(user);

						//after assign to sale man the team of quote is the same as quote.
						Team team = user.getTeam();
						quoteUnderSales.setTeam(team);

						//Saving sales
						LOG.info("Assigning " + quoteUnderSales.getFormNumber() + " to " + user.getName());
						quoteSB.saveQuote(quoteUnderSales);
						LOG.info("Updated");

						List<QuoteItem> quoteItemList = quoteUnderSales.getQuoteItems();

						for(int k = 0 ; k < quoteItemList.size() ; k++)
						{
							//Append Internal Comment
							QuoteItem quoteItem = quoteItemList.get(k);
							String currentInternalComment = quoteItem.getInternalComment();
							if(currentInternalComment == null || currentInternalComment.equals(""))
							{
								if(internalComment != null && !internalComment.equals(""))
									currentInternalComment = internalComment;
							}
							else
							{
								if(internalComment != null && !internalComment.equals(""))
									currentInternalComment += " | " + internalComment;
							}
							quoteItem.setInternalComment(currentInternalComment);
							setLastUpdateInfo(quoteItem); // June 2014/06/20 fix setTheLstUpdateInfo
							/*
							//Saving quoteItem
							LOG.info("Saving InternalComment " + quoteItem.getQuoteNumber());
							quoteSB.updateQuoteItem(quoteItem);
							LOG.info("Saved");*/

						}
						LOG.info("Saving InternalComments of all quote items under quote " + quoteUnderSales.getFormNumber());

						quoteTransactionSB.updateQuoteItem(quoteItemList, ActionEnum.DM_REASSIGN_SALE);
						LOG.info("Saved");
					}

				}
			}
		}
		catch(Exception e)
		{
			LOG.log(Level.SEVERE, "Exception in Reassigning the sales Sales Selection : "+reassignSalesSelection+" Exception message: "+ MessageFormatorUtil.getParameterizedStringFromException(e),e);
			updateSuccess = false;

			errorMessage = ResourceMB.getText("wq.message.errReassign");

			FacesContext.getCurrentInstance().addMessage("growl", new FacesMessage(FacesMessage.SEVERITY_ERROR,errorMessage, ""));
			return;
		}

		updateSuccess = true;
		search();

		errorMessage = ResourceMB.getText("wq.message.quoteReass");
		FacesContext.getCurrentInstance().addMessage("growl", new FacesMessage(FacesMessage.SEVERITY_INFO,errorMessage, ""));

	}

	/**
	 * Check selected quote item vos batch invalidate quote.
	 */
	public void checkSelectedQuoteItemVosBatchInvalidateQuote()
	{

		//NEC Pagination changes: get the selected data from cache 
		Set<QuoteItemVo> cacheData = getCacheSelectionDatas();
		if(cacheData == null || cacheData.size()==0)
		{

			errorMessage= ResourceMB.getText("wq.message.selQuoteItemProcess");
			FacesContext.getCurrentInstance().addMessage("growl", new FacesMessage(FacesMessage.SEVERITY_ERROR,errorMessage, ""));
		}
		else if(batchStages.contains("INVALID") || batchStages.contains("DRAFT"))
		{

			errorMessage = ResourceMB.getText("wq.message.stage");
			FacesContext.getCurrentInstance().addMessage("growl", new FacesMessage(FacesMessage.SEVERITY_ERROR,errorMessage, ""));
		}
		else
		{	
			boolean canGo = true;
			Iterator<QuoteItemVo> iterator = cacheData.iterator();
			while (iterator.hasNext()) {
				QuoteItemVo quoteItemVo =  iterator.next();
				if(QuoteSourceEnum.DP.toString().equals(quoteItemVo.getQuoteItem().getQuote().getQuoteSource())) {
					canGo = false;
					break;
				}
			}

			if(!canGo) {

				errorMessage = ResourceMB.getText("wq.message.dpQuote");
				FacesContext.getCurrentInstance().addMessage("growl", new FacesMessage(FacesMessage.SEVERITY_ERROR,errorMessage, ""));
			} else {
				emailInfo = "";
				RequestContext.getCurrentInstance().execute("PF('dialogBatchInvalidateQuoteWidget').show()");
			}

		}
	}

	/**
	 * Check selected quote item vos invalidate finished quote.
	 */
	public void checkSelectedQuoteItemVosInvalidateFinishedQuote()
	{
		this.enable=false;
		invalidate="invalidateAndGenerate";
		if(getCacheSelectionDatas().size()==0){
			errorMessage = ResourceMB.getText("wq.message.selQuoteItemProcess");
			FacesContext.getCurrentInstance().addMessage("growl", new FacesMessage(FacesMessage.SEVERITY_ERROR,errorMessage, ""));
			return ;
		}
		QuoteItemVo itemVo = getCacheSelectionDatas().iterator().next();
		//NEC Pagination changes: get the selected data from cache 
		if(getCacheSelectionDatas() == null || getCacheSelectionDatas().size()==0)
		{

			errorMessage = ResourceMB.getText("wq.message.selQuoteItemProcess");
			FacesContext.getCurrentInstance().addMessage("growl", new FacesMessage(FacesMessage.SEVERITY_ERROR,errorMessage, ""));
		}
		else if(batchStages.contains("INVALID") || batchStages.contains("DRAFT"))
		{

			errorMessage= ResourceMB.getText("wq.message.stage");
			FacesContext.getCurrentInstance().addMessage("growl", new FacesMessage(FacesMessage.SEVERITY_ERROR,errorMessage, ""));
		}
		
		 //boolean isDLink = false;
		 //vo.getQuoteItem().getQuote().isdLinkFlag() && vo.getQuoteItem().getRfqCurr().name()!=vo.getQuoteItem().getFinalCurr().name()
		/*else if(itemVo.getQuoteItem().getQuote().isdLinkFlag() && itemVo.getQuoteItem().getRfqCurr().name()!=itemVo.getQuoteItem().getFinalCurr().name()){
			errorMessage= ResourceMB.getText("wq.message.childDLinkquotes");
			FacesContext.getCurrentInstance().addMessage("growl", new FacesMessage(FacesMessage.SEVERITY_ERROR,errorMessage, ""));
		}*/
		//NEC Pagination changes: get the selected data from cache 
		else if(getCacheSelectionDatas().size()>1)
		{

			errorMessage = ResourceMB.getText("wq.message.finishedQuote");
			FacesContext.getCurrentInstance().addMessage("growl", new FacesMessage(FacesMessage.SEVERITY_ERROR,errorMessage, ""));
		}
		else
		{
			boolean canGo = true;
			//NEC Pagination changes: get the selected data from cache 
			QuoteItemVo item = getCacheSelectionDatas().iterator().next();
			if(QuoteSourceEnum.DP.toString().equals(item.getQuoteItem().getQuote().getQuoteSource())) {
				canGo = false;
			}
			if(!canGo) {

				errorMessage = ResourceMB.getText("wq.message.dpQuote");
				FacesContext.getCurrentInstance().addMessage("growl", new FacesMessage(FacesMessage.SEVERITY_ERROR,errorMessage, ""));
			} else {
				emailInfo = "";
				
				if(itemVo.getQuoteItem().getQuote().isdLinkFlag()&& itemVo.getQuoteItem().getRfqCurr().name()!=itemVo.getQuoteItem().getFinalCurr().name()){
					invalidate = "invalidateOnly";
					this.enable=true;
				}
				
				RequestContext.getCurrentInstance().execute("PF('dialogInvalidateFinishedQuoteWidget').show()");
			}

		}
	}

	/**
	 * Check selected quote item vos update information.
	 */
	public void checkSelectedQuoteItemVosUpdateInformation()
	{
		//NEC Pagination changes: get the selected data from cache 
		Set<QuoteItemVo> cacheData = getCacheSelectionDatas();
		QuoteItemVo quoteItem = cacheData.iterator().next();
		if(cacheData == null || cacheData.size()==0)
		{

			errorMessage= ResourceMB.getText("wq.message.selQuoteItemProcess");
			FacesContext.getCurrentInstance().addMessage("growl", new FacesMessage(FacesMessage.SEVERITY_ERROR,errorMessage, ""));
		}
		else if(cacheData.size()>1)
		{

			errorMessage = ResourceMB.getText("wq.message.onlyOneQuoteUpdated");
			FacesContext.getCurrentInstance().addMessage("growl", new FacesMessage(FacesMessage.SEVERITY_ERROR,errorMessage, ""));
		}
		else if(!quoteItem.getQuoteItem().getStage().equals("FINISH"))
		{

			errorMessage = ResourceMB.getText("wq.message.quoteItemStageFinish");
			FacesContext.getCurrentInstance().addMessage("growl", new FacesMessage(FacesMessage.SEVERITY_ERROR,errorMessage, ""));
		}
		else {
			boolean canGo = true;
			if (QuoteSourceEnum.DP.toString().equals(quoteItem.getQuoteItem().getQuote().getQuoteSource())) {
				canGo = false;
			}
			if (!canGo) {
				errorMessage = ResourceMB.getText("wq.message.dpQuote");
				FacesContext.getCurrentInstance().addMessage(
						"growl",new FacesMessage(FacesMessage.SEVERITY_ERROR,errorMessage, ""));
			} else {
				RequestContext.getCurrentInstance().execute("PF('dialogUpdateInformationWidget').show()");
			}
		}
	}

	/**
	 * Check selected quote item vos update validity.
	 */
	public void checkSelectedQuoteItemVosUpdateValidity()
	{
		//NEC Pagination changes: get the selected data from cache 
		Set<QuoteItemVo> cacheData = getCacheSelectionDatas();
		if(cacheData == null || cacheData.size()==0)
		{

			errorMessage= ResourceMB.getText("wq.message.selQuoteItemProcess");
			FacesContext.getCurrentInstance().addMessage("growl", new FacesMessage(FacesMessage.SEVERITY_ERROR,errorMessage, ""));
		}
		else
		{
			Iterator<QuoteItemVo> iterator = cacheData.iterator();
			while (iterator.hasNext()) {
				QuoteItemVo quoteItem =  iterator.next();
				if(!"FINISH".equals(quoteItem.getQuoteItem().getStage())) {
					errorMessage = ResourceMB.getText("wq.message.quoteItemStageFinish");
					FacesContext.getCurrentInstance().addMessage("growl", new FacesMessage(FacesMessage.SEVERITY_ERROR,errorMessage, ""));
					return;
				}

				if(QuoteSourceEnum.DP.toString().equals(quoteItem.getQuoteItem().getQuote().getQuoteSource())) {
					errorMessage = ResourceMB.getText("wq.message.dpQuote");
					FacesContext.getCurrentInstance().addMessage("growl", new FacesMessage(FacesMessage.SEVERITY_ERROR,errorMessage, ""));
					return;
				}				
			}

			RequestContext.getCurrentInstance().execute("PF('dialogUpdateValidityWidget').show()");
		}
	}

	/**
	 * Check selected quote item vos reassign sales.
	 */
	public void checkSelectedQuoteItemVosReassignSales() {
		//NEC Pagination changes: get the selected data from cache 
		Set<QuoteItemVo> cacheData = getCacheSelectionDatas();
		if (cacheData == null || cacheData.size() == 0) {

			errorMessage = ResourceMB.getText("wq.message.selQuoteItemProcess");
			FacesContext.getCurrentInstance().addMessage(
					"growl",new FacesMessage(FacesMessage.SEVERITY_ERROR, errorMessage,""));
		} else {

			boolean canGo = true;
			for (QuoteItemVo quoteItem : cacheData) {
				if(QuoteSourceEnum.DP.toString().equals(quoteItem.getQuoteItem().getQuote().getQuoteSource())) {
					canGo = false;
					break;
				}
			}

			if(!canGo) {
				errorMessage = ResourceMB.getText("wq.message.dpQuote");
				FacesContext.getCurrentInstance().addMessage("growl", new FacesMessage(FacesMessage.SEVERITY_ERROR,errorMessage, ""));
			} else {
				RequestContext.getCurrentInstance().execute("PF('dialogReassignSalesWidget').show()");
			}

		}
	}

	/**
	 * Cancel save.
	 */
	public void cancelSave() {
		//commented by Damonchen@20180328
	/*	selectedQuoteItemVos = null;
		quoteItemVos.stopPagination();
		filteredQuoteItemVos = null;
		selectedQuoteItemVo = null;
		qcOperationSearch();*/
	}
	//add by Vincent 20181126
	public String cancelSaveUpdateInformation() {
		
		return "/RFQ/DataMaintenance.xhtml?faces-redirect=true";
	}
	/**
	 * Creates the SAP quote.
	 *
	 * @param items the items
	 */
	private void createSAPQuote(List<QuoteItem> items) {
		try {
			TableOfZquoteMsg tableMsg = webServiceSB.createSAPQuote(items);
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

		} catch (AppException e) {
			LOG.log(Level.WARNING, "Error occured when creating sap quote");
		}
	}
	
	/**
	 * Setter for resourceMB.
	 *
	 * @param resourceMB ResourceMB
	 */
	public void setResourceMB(final ResourceMB resourceMB) {
		this.resourceMB = resourceMB;
	}

	

	/**
	 * NEC Pagination changes:
	 * Gets the quote item vo.
	 *
	 * @return the quote item vo
	 */
	public LazyDataModelForDataMaintenance getQuoteItemVos() {
		return quoteItemVos;
	}

	/**
	 * NEC Pagination changes:
	 * Sets the quote item vo. This is setter
	 *
	 * @param quoteItemVo the new quote item vo
	 */
	public void setQuoteItemVos(LazyDataModelForDataMaintenance quoteItemVos) {
		this.quoteItemVos = quoteItemVos;
	}

	
	public Set<QuoteItemVo> getCacheSelectionDatas() {
		return quoteItemVos.getCacheSelectedItems();
	}
	
	//NEC Pagination Changes: Inner Class created for implementing Pagination with Lazy Loading
	public class LazyDataModelForDataMaintenance extends SelectableLazyDataModel<QuoteItemVo>{
		
		
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
		
		//NEC Pagination Changes: finds data for previous page through asynchronous call to database
		@Override
		public void findLazyPreviousPageData(int first, int pageSize, int pageNumber, String sortField, String sort, Map<String, Object> filters,
				ConcurrentHashMap<Integer, List<QuoteItemVo>> map, Queue<Integer> queue,int cachePageSize) {
			
			myQuoteSearchSB.dataMaintenanceAsyncSearch(criteria, first, pageSize, pageNumber, sortField, sort, filters, map, queue,cachePageSize);
		}
		
		//NEC Pagination Changes: finds data for next page through asynchronous call to database
		@Override
		public void findLazyNextPageData(int first, int pageSize, int pageNumber, String sortField, String sort, Map<String, Object> filters,
				ConcurrentHashMap<Integer, List<QuoteItemVo>> map, Queue<Integer> queue,int cachePageSize) {
			myQuoteSearchSB.dataMaintenanceAsyncSearch(criteria, first, pageSize, pageNumber, sortField, sort, filters, map, queue,cachePageSize);
		}
		
		//NEC Pagination Changes: finds data for previous page through asynchronous call to database
		@Override
	     public List<QuoteItemVo> findLazyData(int first, int pageSize, String sortField, String sortOrder,
	                                     Map<String, Object> filters) {
			
			List<QuoteItemVo> outCome = myQuoteSearchSB.dataMaintenanceSearch(criteria, first, pageSize, sortField, sortOrder, filters);
			return outCome;

	     }
		
		//To find out no of record present in DB
		@Override
	     public int findLazyDataCount(String sortField, String sortOrder, Map<String, Object> filters) {
			return myQuoteSearchSB.dataMaintenanceCount(criteria,sortField, sortOrder, filters);
	     }
		
		
		/* (non-Javadoc)
		 * @see com.avnet.emasia.webquote.web.datatable.BaseLazyDataMB#onRowSelectCheckbox(org.primefaces.event.SelectEvent)
		 */
		@Override
		//NEC Pagination changes:  it is called on on row select check box
		public void onRowSelectCheckbox(SelectEvent event) {
			super.onRowSelectCheckbox(event);
			onRowSelect(event);
		}
		
		/* (non-Javadoc)
		 * @see com.avnet.emasia.webquote.web.datatable.BaseLazyDataMB#onRowUnselectCheckbox(org.primefaces.event.UnselectEvent)
		 */
		@Override
		//NEC Pagination changes:  it is called on on row unselect check box
		public void onRowUnselectCheckbox(UnselectEvent event) {
			super.onRowUnselectCheckbox(event);
			onRowUnselect(event);
		}
		
		//override by damonchen@20180316
		public void onToggleSelect(ToggleSelectEvent event) {
			super.onToggleSelect(event);
			// batch invalidate quote related info
			batchQuoteNumStr = "";
			batchSales = "";
			batchCs = "";
			batchCreator = "";
			batchStages = "";
			// NEC Pagination changes: get the selected data from cache
			Set<QuoteItemVo> datas = getCacheSelectionDatas();
			Iterator<QuoteItemVo> iterator = datas.iterator();
			while (iterator.hasNext()) {
				QuoteItemVo quoteItem = (QuoteItemVo) iterator.next();
				batchStages += "[" + quoteItem.getQuoteItem().getStage() + "]";
				batchQuoteNumStr += "[" + quoteItem.getQuoteItem().getQuoteNumber() + "]";

				if (!batchSales.contains(quoteItem.getQuoteItem().getQuote().getSales().getName())) {
					batchSales += "[" + quoteItem.getQuoteItem().getQuote().getSales().getName() + "(Sales)]";
				}

				// Processing copyToCS;
				String batchCsString = quoteItem.getQuoteItem().getQuote().getCopyToCsName();

				if (batchCsString != null && batchCsString.length() != 0) {
					String[] csArray = batchCsString.split(";");

					for (int k = 0; k < csArray.length; k++) {
						String csName = csArray[k];
						if (!batchCs.contains(csName)) {
							batchCs += "[" + csName + "(CS)]";
						}
					}
				}
				String createdName = quoteItem.getQuoteItem().getQuote().getCreatedName();
				if (createdName != null && !batchCreator.contains(createdName)) {
					batchCreator += "[" + createdName + "]";
				}

			}

			setValueForShipmentValidity();
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
		protected SelectableLazyDataModel<QuoteItemVo> getLazyData() {
			return quoteItemVos;
		}
		/* (non-Javadoc)
		 * @see com.avnet.emasia.webquote.web.datatable.BaseLazyDataMB#cellChangeListener(java.lang.String)
		 */
		@Override
		//NEC Pagination changes:  it is callback method
		public void cellChangeListener(String id) {
			quoteItemVos.setCacheModifyData(id);
			FacesUtil.updateUI("f:quote_datatable");
		}
		
		public List<QuoteItemVo> getSelectionItems() {
			Set<QuoteItemVo> cacheDatas = getCacheSelectionDatas();
			selectionItems =  new ArrayList<>();
			selectionItems.addAll(cacheDatas);
			return selectionItems;
		}
		
		
		/* (non-Javadoc)
		 * @see com.avnet.emasia.webquote.web.datatable.BaseLazyDataMB#onRowSelect(org.primefaces.event.SelectEvent)
		 */
		public void onRowSelect(SelectEvent event) {
			super.onRowSelect(event);
			//batch invalidate quote related info
			batchQuoteNumStr = "";
			batchSales = "";
			batchCs = "";
			batchCreator = "";
			batchStages = "";
			//NEC Pagination changes: get the selected data from cache 
			Set<QuoteItemVo> datas = getCacheSelectionDatas();
			Iterator<QuoteItemVo> iterator = datas.iterator();
			while (iterator.hasNext()) {
				QuoteItemVo quoteItem = (QuoteItemVo) iterator.next();
				batchStages += "[" + quoteItem.getQuoteItem().getStage()+ "]";
				batchQuoteNumStr += "[" + quoteItem.getQuoteItem().getQuoteNumber() + "]";

				if(!batchSales.contains(quoteItem.getQuoteItem().getQuote()
						.getSales().getName()))
				{
					batchSales += "[" + quoteItem.getQuoteItem().getQuote()
							.getSales().getName() + "(Sales)]";
				}

				//Processing copyToCS;
				String batchCsString = quoteItem.getQuoteItem().getQuote().getCopyToCsName();

				if (batchCsString != null && batchCsString.length() != 0) {
					String[] csArray = batchCsString.split(";");

					for (int k = 0; k < csArray.length; k++) {
						String csName = csArray[k];
						if (!batchCs.contains(csName)) {
							batchCs += "[" + csName + "(CS)]";
						}
					}
				}
				String createdName = quoteItem.getQuoteItem().getQuote().getCreatedName();
				if(createdName!=null&&!batchCreator.contains(createdName)){
					batchCreator += "[" + createdName+ "]";
				}


			}
			/*for(int i = 0 ; i < selectedQuoteItemVos.size() ; i++)
			{
				batchStages += "[" + selectedQuoteItemVos.get(i).getQuoteItem().getStage()+ "]";
				batchQuoteNumStr += "[" + selectedQuoteItemVos.get(i).getQuoteItem().getQuoteNumber() + "]";

				if(!batchSales.contains(selectedQuoteItemVos.get(i).getQuoteItem().getQuote()
						.getSales().getName()))
				{
				batchSales += "[" + selectedQuoteItemVos.get(i).getQuoteItem().getQuote()
						.getSales().getName() + "(Sales)]";
				}

				//Processing copyToCS;
				String batchCsString = selectedQuoteItemVos.get(i).getQuoteItem().getQuote().getCopyToCsName();

				if (batchCsString != null && batchCsString.length() != 0) {
					String[] csArray = batchCsString.split(";");

					for (int k = 0; k < csArray.length; k++) {
						String csName = csArray[k];
						if (!batchCs.contains(csName)) {
							batchCs += "[" + csName + "(CS)]";
						}
					}
				}
				String createdName = selectedQuoteItemVos.get(i).getQuoteItem().getQuote().getCreatedName();
				if(createdName!=null&&!batchCreator.contains(createdName)){
					batchCreator += "[" + createdName+ "]";
				}
			}*/

			setValueForShipmentValidity();
		}


		/* (non-Javadoc)
		 * @see com.avnet.emasia.webquote.web.datatable.BaseLazyDataMB#onRowUnselect(org.primefaces.event.UnselectEvent)
		 */
		public void onRowUnselect(UnselectEvent event) {
			super.onRowUnselect(event);
			//batch invalidate quote related info
			batchQuoteNumStr = "";
			batchSales = "";
			batchCs = "";
			batchCreator = "";
			batchStages = "";
			//NEC Pagination changes: get the selected data from cache 
			Set<QuoteItemVo> cacheData = getCacheSelectionDatas(); 
			if(cacheData.size()>0)
			{
				Iterator<QuoteItemVo> iterator = cacheData.iterator();
				while (iterator.hasNext()) {
					QuoteItemVo quoteItem = iterator.next();

					batchStages += "[" + quoteItem.getQuoteItem().getStage()+ "]";
					batchQuoteNumStr += "[" + quoteItem.getQuoteItem().getQuoteNumber() + "]";

					if(!batchSales.contains(quoteItem.getQuoteItem().getQuote()
							.getSales().getName()))
					{
						batchSales += "[" + quoteItem.getQuoteItem().getQuote()
								.getSales().getName() + "(Sales)]";
					}

					//Processing copyToCS;
					String batchCsString = quoteItem.getQuoteItem().getQuote().getCopyToCsName();
					if (batchCsString != null && batchCsString.length() != 0) {
						String[] csArray = batchCsString.split(";");

						for (int k = 0; k < csArray.length; k++) {
							String csName = csArray[k];
							if (!batchCs.contains(csArray[k])) {
								batchCs += "[" + csName + "(CS)]";
							}
						}
					}

					if(!batchCreator.contains(quoteItem.getQuoteItem().getQuote().getCreatedName()))
					{
						batchCreator += "[" + quoteItem.getQuoteItem().getQuote().getCreatedName()+ "]";
					}

				}
			}
			else
			{
				selectedQuoteItemVo = new QuoteItemVo();
				creator = null;
				cs = null;
				quoteNumStr = null;
				sales = null;
				salesMen = null;
			}

		}
	}


}