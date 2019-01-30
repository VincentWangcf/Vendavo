package com.avnet.emasia.webquote.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import com.avnet.emasia.webquote.utilities.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.EntityManager;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import javax.persistence.TypedQuery;
import javax.persistence.Version;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang.StringUtils;
import org.eclipse.persistence.annotations.Customizer;

import com.avnet.emasia.webquote.constants.Constants;
import com.avnet.emasia.webquote.ejb.interceptor.ThreadLocalEntityManager;
import com.avnet.emasia.webquote.entity.util.QuoteItemListener;
import com.avnet.emasia.webquote.quote.ejb.constant.QuoteSBConstant;
import com.avnet.emasia.webquote.strategy.MoneyConvertStrategy;
import com.avnet.emasia.webquote.strategy.StrategyFactory;
import com.avnet.emasia.webquote.vo.PricerInfo;
import com.avnet.emasia.webquote.vo.RfqHeaderVO;
import com.avnet.emasia.webquote.vo.RfqItemVO;

@Entity
@Table(name="QUOTE_ITEM")
@Customizer(com.avnet.emasia.webquote.entity.QuoteItemCustomizer.class)
@EntityListeners(QuoteItemListener.class)
public class QuoteItem implements Serializable, Cloneable {
	private static final long serialVersionUID = 1L;
	
	private static final Logger LOGGER = Logger.getLogger(QuoteItem.class.getName());
	 
	
	@Transient
	public String quotedQtyStr;
	@Transient
	public String shipmentValidityStr;
	@Transient
	public DecimalFormat df2 = new DecimalFormat("#");
	@Transient
	public DecimalFormat df5 = new DecimalFormat("#");
	@Transient
	public SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");		
	@Transient
	public String quotationEffectiveDateStr;
	
	@Id
	@SequenceGenerator(name="QUOTE_ITEM_ID_GENERATOR", sequenceName="WQ_SEQ",allocationSize=1)
	@GeneratedValue(strategy=GenerationType.SEQUENCE, generator="QUOTE_ITEM_ID_GENERATOR")
	@Column(name="ID", unique=true, nullable=false)
	private long id;

	@Column(name="QUOTE_NUMBER")
	private String quoteNumber;

	@Column(name="ALTERNATIVE_QUOTE_NUMBER")
	private String alternativeQuoteNumber;

	//uni-directional many-to-one association to Customer
	@ManyToOne
	@JoinColumn(name="END_CUSTOMER_NUMBER")
	private Customer endCustomer;

	//uni-directional many-to-one association to Customer
	@ManyToOne
	@JoinColumn(name="SHIP_TO_CUSTOMER_NUMBER")
	private Customer shipToCustomer;

	//uni-directional many-to-one association to Customer
	@ManyToOne
	@JoinColumn(name="SOLD_TO_CUSTOMER_NUMBER")
	private Customer soldToCustomer;

	// Delinkage change on Oct 29 , 2013 by Tonmy
//	@ManyToOne(fetch=FetchType.EAGER)
//	@JoinColumn(name="REQUESTED_MATERIAL_ID")
//	private Material requestedMaterial;
	

	@ManyToOne
	@JoinColumn(name="QUOTED_MATERIAL_ID")
	private Material quotedMaterial;

	@Column(name="APPLICATION", length=255)
	private String application;

	@Column(name="AQCC", length=1024)
	private String aqcc;

	@Column(name="DESCRIPTION", length=1024)
	private String description;

	@Column(name="PROJECT_NAME", length=255)
	private String projectName;

	@Column(name="INTERNAL_TRANSFER_COMMENT", length=1024)
	private String internalTransferComment;

	@Column(name="BOTTOM_PRICE")
	private Double bottomPrice;

	@Column(name="MIN_SELL_PRICE")
	private Double minSellPrice;

	@Column(name="PRICE_LIST_REMARKS_1", length=255)
	private String priceListRemarks1;

	@Column(name="PRICE_LIST_REMARKS_2", length=255)
	private String priceListRemarks2;

	@Column(name="PRICE_LIST_REMARKS_3", length=255)
	private String priceListRemarks3;

	@Column(name="PRICE_LIST_REMARKS_4", length=255)
	private String priceListRemarks4;

	@Column(name="PRICE_VALIDITY", length=20)	
	private String priceValidity;

	@Column(name="RESALE_MARGIN", length=1024)
	private Double resaleMargin;

	@Column(name="TARGET_MARGIN", length=1024)
	private Double targetMargin;

	@Column(name="REFERENCE_MARGIN", length=1024)
	private Double referenceMargin;	

	@Column(name="ALLOCATION_FLAG", length=1)
	private Boolean allocationFlag;

	@Column(name="BMT_CHECKED_FLAG", length=1)
	private Boolean bmtCheckedFlag;

	@Column(name="VALID_FLAG", length=1)
	private Boolean validFlag;

	@Column(name="BOM_NUMBER", length=20)
	private String bomNumber;

	@Column(name="BOM_REMARKS", length=255)
	private String bomRemarks;

	@Column(name="CANCELLATION_WINDOW")
	private Integer cancellationWindow;

	@Column(name="COMPETITOR_INFORMATION", length=255)
	private String competitorInformation;

	/**Corresponding buyCurrency**/
	@Column(precision=10, scale=5)
	private Double cost;

	@Column(name="COST_INDICATOR", length=20)
	private String costIndicator;

	@Column(name="DRMS_NUMBER", precision=7)
	private Long drmsNumber;

	@Column(name="EAU")
	private Integer eau;

	@Column(name="ENQUIRY_SEGMENT", length=50)
	private String enquirySegment;

	@Column(name="FIRST_RFQ_CODE", length=20)
	private String firstRfqCode;

	@Column(name="INTERNAL_COMMENT", length=255)
	private String internalComment;

	@Column(name="ITEM_SEQ")
	private Integer itemSeq;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="LAST_PM_UPDATED_ON")
	private Date lastPmUpdatedOn;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="LAST_QC_UPDATED_ON")
	private Date lastQcUpdatedOn;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="LAST_UPDATED_ON")
	private Date lastUpdatedOn;

	@Column(name = "LAST_UPDATED_PM", length = 10)
	private String lastUpdatedPm;

	@Column(name = "LAST_UPDATED_QC", length = 10)
	private String lastUpdatedQc;

	@Column(name="LEAD_TIME", length=30)
	private String leadTime;

	@Column(name="DESIGN_LOCATION", length=255)
	private String designLocation;

	@Column(name="LOA_FLAG", length=1)
	private Boolean loaFlag;


	private Integer moq;

	private Integer mov;


	@Column(name="MP_SCHEDULE")
	private String mpSchedule;

	private Integer mpq;

	@Column(name="MULTI_USAGE_FLAG", length=1)
	private Boolean multiUsageFlag;

	private String pmoq;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="PO_EXPIRY_DATE")
	private Date poExpiryDate;

//	private String warning;

	@Column(name="PP_SCHEDULE")
	private String ppSchedule;

	@Column(name="QC_COMMENT", length=255)
	private String qcComment;

	@Column(name="QTY_INDICATOR", length=20)
	private String qtyIndicator;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="QUOTE_EXPIRY_DATE")
	private Date quoteExpiryDate;

	@Column(name="QUOTED_QTY")
	private Integer quotedQty;

//	@Column(name="QUOTED_FLAG", length=1)
//	private Boolean quotedFlag;
	@Column(name="QUOTED_PRICE", precision=15, scale=10)
	private Double quotedPrice;

	@Column(name="REQUESTED_QTY")
	private Integer requestedQty;

	@Column(name="RESALE_INDICATOR", length=4)
	private String resaleIndicator;

	@Column(name="RESALE_MAX", precision=10, scale=5)
	private Double resaleMax;

	@Column(name="RESALE_MIN", precision=10, scale=5)
	private Double resaleMin;

	@Column(name="RESCHEDULE_WINDOW")
	private Integer rescheduleWindow;

	@Column(name="REVERT_VERSION", length=5)
	private String revertVersion;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="SHIPMENT_VALIDITY")
	private Date shipmentValidity;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="LAST_IT_UPDATE_TIME")
	private Date lastItUpdateTime;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="LAST_RIT_UPDATE_TIME")
	private Date lastRitUpdateTime;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="LAST_SQ_UPDATE_TIME")
	private Date lastSqUpdateTime;

	@Column(name="SPR_FLAG", length=1)
	private Boolean sprFlag;

	@Column(length=10)
	private String status;

	@Column(length=10)
	private String stage;	

	@Column(name="SUPPLIER_DR_NUMBER", length=50)
	private String supplierDrNumber;

	@Column(name="TARGET_RESALE", precision=9, scale=5)
	private Double targetResale;

	@Column(name="TERMS_AND_CONDITIONS", length=255)
	private String termsAndConditions;

	@Column(name="VENDOR_DEBIT_NUMBER", length=50)
	private String vendorDebitNumber;

	@Column(name="VENDOR_QUOTE_NUMBER", length=50)
	private String vendorQuoteNumber;

	@Version
	@Column(name="\"VERSION\"")
	private Integer version;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="SENT_OUT_TIME")
	private Date sentOutTime;

	@Column(name="REASON_FOR_REFRESH")
	private String reasonForRefresh;

	@Column(name="REMARKS")
	private String remarks;

	@Column(name="END_CUSTOMER_NAME")
	private String endCustomerName;

	@Column(name="SHIP_TO_CUSTOMER_NAME")
	private String shipToCustomerName;

	@Column(name="CUSTOMER_TYPE")
	private String customerType;

	@Column(name = "MATERIAL_TYPE_ID", length = 20)
	private String materialTypeId;
	
	@Column(name = "PROGRAM_TYPE", length = 20)
	private String programType;

	@ManyToOne
	@JoinColumn(name="PRODUCT_GROUP2_ID")
	private ProductGroup productGroup2;

	@ManyToOne
	@JoinColumn(name="PRODUCT_GROUP1_ID")
	private ProductGroup productGroup1;	

	@Column(name="PRODUCT_GROUP3")
	private String productGroup3;

	@Column(name="PRODUCT_GROUP4")
	private String productGroup4;

	//bi-directional many-to-one association to Attachment
	@OneToMany(mappedBy="quoteItem", cascade=CascadeType.ALL)
	private List<Attachment> attachments;

	@Column(name = "LAST_UPDATED_BY", length = 10)
	private String lastUpdatedBy;


	@Column(name="VENDOR_QUOTE_QTY")
	private Integer vendorQuoteQty;

	@Column(name="FINAL_QUOTATION_PRICE")
	private Double finalQuotationPrice;
	
	@Transient
	private String finalQuotationPriceToString;
	@Column(name="SALES_COST_FLAG")
	private boolean salesCostFlag;
	
	@Column(name="SALES_COST_TYPE")
	@Enumerated(EnumType.STRING)
	private SalesCostType salesCostType;
	
	@Column(name="SALES_COST")
	private BigDecimal salesCost;

	@Column(name="SUGGESTED_RESALE")
	private BigDecimal suggestedResale;

	@Column(name="CUSTOMER_GROUP_ID")
	private String customerGroupId;
	
	@Column(name="PRICER_ID")
	private long pricerId;

	//bi-directional many-to-one association to Quote
	@ManyToOne
	@JoinColumn(name="QUOTE_ID")
	private Quote quote;
	
	@Column(name="MIGRATION_BATCH_NUMBER")
	private String migrationBatchNumber;	

	@Transient
	private boolean error;

	@Transient
	private boolean hiddenWR;

	@Transient
	private boolean hightlighted;

	// Delinkage change on Oct 29 , 2013 by Tonmy
	@ManyToOne(fetch=FetchType.EAGER)
	@JoinColumn(name="REQUESTED_MFR_ID")
	private Manufacturer requestedMfr;

	@Column(name="REQUESTED_PART_NUMBER")
	private String requestedPartNumber;
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="SUBMISSION_DATE")
	private Date submissionDate;
	
	@Column(name="BUY_CURR", unique=true, nullable=false, length=20)
	@Enumerated(EnumType.STRING)
	private Currency buyCurr;

	@Column(name="FINAL_CURR", unique=true, nullable=false, length=20)
	@Enumerated(EnumType.STRING)
	private Currency finalCurr;
	
	@Column(name="RFQ_CURR", unique=true, nullable=false, length=20)
	@Enumerated(EnumType.STRING)
	private Currency rfqCurr;

	@Column(name="EX_RATE_BUY")
	private BigDecimal exRateBuy;

	@Column(name="EX_RATE_RFQ")
	private BigDecimal exRateRfq;
	
	@Column(name="VAT")
	private BigDecimal vat;

	@Column(name="HANDLING")
	private BigDecimal handling;
	
	
	// Delinkage change on Oct 29 , 2013 by Tonmy
	@ManyToOne(fetch=FetchType.EAGER)
	@JoinColumn(name="QUOTED_MFR_ID")
	private Manufacturer quotedMfr;

	@Column(name="QUOTED_PART_NUMBER")
	private String quotedPartNumber;
	
	@Transient
	private Material requestedMaterialForProgram;
	
	@Transient
	private ProgramPricer requestedProgramMaterialForProgram;
	
	@Transient
	private List<String> pmEmployeeList;

	
	@Transient
	private boolean drmsUpdated;;
	
	@Transient
	private String drmsUpdateFailedDesc;
	
	@Column(name = "LAST_UPDATED_PM_NAME", length = 40)
	private String lastUpdatedPmName;
	
	@Column(name = "LAST_UPDATED_QC_NAME", length = 40)
	private String lastUpdatedQcName;
	
	@Column(name = "LAST_UPDATED_NAME", length = 40)
	private String lastUpdatedName;
	
	@Column(name = "USED_FLAG", precision = 22, scale = 0)
	private Boolean usedFlag;
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "QUOTATION_EFFECTIVE_DATE")
	private Date quotationEffectiveDate;
	
	@Column(name = "DESIGN_IN_CAT", length = 20)
	private String designInCat;
	
	@Column(name = "QUOTE_TYPE", length = 20)
	private String quoteType;
	
	@Column(name="SAP_PART_NUMBER", length=36)
	private String sapPartNumber;
	
	@OneToOne(mappedBy="quoteItem", cascade=CascadeType.ALL)
	private QuoteItemDesign quoteItemDesign;

	@OneToMany(mappedBy="quoteItem", cascade=CascadeType.ALL)
	private List<QuoteResponseTimeHistory> quoteResponseTimeHistorys;
	
	@Column(name = "DESIGN_REGION")
	private String designRegion;
	
	
	@Column(name = "DP_REFERENCE_LINE_ID")
	private String dpReferenceLineId;
	
	@Column(name = "ACTION")
	private String action;
	
	@Transient
	private String bmtDescCode;
	
	@Transient
	private String dupMatchCode;
	
	@Transient
	private String reSubmitType;
	
	public static final int ATTACHMENT_TYPE_LENGTH = 5;
	
	/**
	 * add by Lenon,Yang(2016.03.22)
	 * 
	 * the value for the column attachmentFlag like '00010'(1 means has attachment,0 means no)
	 * and from left to right represents the value for all type of attachments below:
	 * 
	 * 1.BMT_ATTACHMENT
	 * 2.QC_ATTACHMENT
	 * 3.PM_ATTACHMENT
	 * 4.REFRESH_ATTACHMENT
	 * 5.ITEM_ATTACHMENT
	 * 
	 */
	@Column(name="ATTACHMENT_FLAG")
	private String attachmentFlag = "00000";	
	
	public List<String> getPmEmployeeList()
	{
		return pmEmployeeList;
	}
	@Transient
	public void setPmEmployeeList(List<String> pmEmployeeList)
	{
		this.pmEmployeeList = pmEmployeeList;
	}
	
	public boolean isHightlighted() {
		return hightlighted;
	}
	@Transient
	public void setHightlighted(boolean hightlighted) {
		this.hightlighted = hightlighted;
	}
	public boolean isHiddenWR() {
		return hiddenWR;
	}
	@Transient
	public void setHiddenWR(boolean hiddenWR) {
		this.hiddenWR = hiddenWR;
	}
	public boolean isError() {
		return error;
	}
	@Transient
	public void setError(boolean error) {
		this.error = error;
	}
	
	@Column(name="AUTH_GP")
	private Double authGp;		

	@Column(name="AUTH_GP_REASON_CODE")
	private String authGpReasonCode;		

	@Column(name="AUTH_GP_REASON_DESC")
	private String authGpReasonDesc;		

	@Column(name="AUTH_GP_REMARK")
	private String authGpRemark;		

	@Temporal(TemporalType.DATE)
	@Column(name="DR_EXPIRY_DATE")
	private Date drExpiryDate;		

	@Column(name="DR_NEDA_LINE_NUMBER")
	private Integer drNedaLineNumber;		

	@Column(name="DR_NEDA_ID")
	private Integer drNedaId;		

	@Column(name="ORIGINAL_AUTH_GP")
	private Double orginalAuthGp;
	
	@Column(name = "FIS_YEAR", length = 4)
	private String fisYear;
	@Column(name = "FIS_QTR", length = 6)
	private String fisQtr;
	@Column(name = "FIS_MTH", length = 6)
	private String fisMth;
	
	@Column(name="LAST_UPDATED_BMT")
	private String lastUpdatedBmt;
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "LAST_QC_IN_DATE")
	private Date lastQcInDate;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "LAST_QC_OUT_DATE")
	private Date lastQcOutDate;

	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "LAST_SQ_IN_DATE")
	private Date lastSqInDate;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "LAST_SQ_OUT_DATE")
	private Date lastSqOutDate;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "LAST_BMT_IN_DATE")
	private Date lastBmtInDate;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "LAST_BMT_OUT_DATE")
	private Date lastBmtOutDate;
	
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "LAST_BMT_UPDATE_TIME")
	private Date lastBmtUpdateTime;	
	
	@Column(name = "RATE_REMARKS")
	private String rateRemarks;
	
	/*@Column(name="EXCHANGE_RATE_TYPE")
	@Enumerated(EnumType.STRING)
	private ExchangeRateType exchangeRateType;*/
	
	@Column(name="EX_RATE_FNL")
	private BigDecimal exRateFnl;
	
	@Transient
	public boolean isITStatus;
	
	public boolean isITStatus()
	{
		return isITStatus;
	}
	@Transient
	public void setITStatus(boolean isITStatus)
	{
		this.isITStatus = isITStatus;
	}
	
	public QuoteItem() {
		df2.setMaximumFractionDigits(2);
		df2.setMinimumFractionDigits(2);
		df2.setMinimumIntegerDigits(1);
		df5.setMaximumFractionDigits(5);
		df5.setMinimumFractionDigits(5);
		df5.setMinimumIntegerDigits(1);
		this.setAttachments(new ArrayList<Attachment>()); 
		this.setQuoteResponseTimeHistorys(new ArrayList<QuoteResponseTimeHistory>());
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getQuoteNumber() {
		return quoteNumber;
	}

	public void setQuoteNumber(String quoteNumber) {
		this.quoteNumber = quoteNumber;
	}

	public Customer getEndCustomer() {
		return endCustomer;
	}

	public void setEndCustomer(Customer endCustomer) {
		this.endCustomer = endCustomer;
	}

	public Customer getShipToCustomer() {
		return shipToCustomer;
	}

	public void setShipToCustomer(Customer shipToCustomer) {
		this.shipToCustomer = shipToCustomer;
	}

	public Customer getSoldToCustomer() {
		return soldToCustomer;
	}

	public void setSoldToCustomer(Customer soldToCustomer) {
		this.soldToCustomer = soldToCustomer;
	}

	// Delinkage change on Oct 29 , 2013 by Tonmy
//	public Material getRequestedMaterial() {
//		return requestedMaterial;
//	}
//
//	public void setRequestedMaterial(Material requestedMaterial) {
//		this.requestedMaterial = requestedMaterial;
//	}

	public Material getQuotedMaterial() {
		return quotedMaterial;
	}

	public void setQuotedMaterial(Material quotedMaterial) {
		this.quotedMaterial = quotedMaterial;
	}

	public String getApplication() {
		return application;
	}

	public void setApplication(String application) {
		this.application = application;
	}

	public String getAqcc() {
		return aqcc;
	}

	public void setAqcc(String aqcc) {
		this.aqcc = aqcc;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getProjectName() {
		return projectName;
	}

	public void setProjectName(String projectName) {
		this.projectName = projectName;
	}

	public String getInternalTransferComment() {
		return internalTransferComment;
	}

	public void setInternalTransferComment(String internalTransferComment) {
		this.internalTransferComment = internalTransferComment;
	}

	public Double getBottomPrice() {
		return bottomPrice;
	}

	public void setBottomPrice(Double minSellPrice) {
		this.bottomPrice = minSellPrice;
	}

	public Double getMinSellPrice() {
		return minSellPrice;
	}

	public void setMinSellPrice(Double normSellPrice) {
		this.minSellPrice = normSellPrice;
	}

	public String getPriceListRemarks1() {
		return priceListRemarks1;
	}

	public void setPriceListRemarks1(String priceListRemarks1) {
		this.priceListRemarks1 = priceListRemarks1;
	}

	public String getPriceListRemarks2() {
		return priceListRemarks2;
	}

	public void setPriceListRemarks2(String priceListRemarks2) {
		this.priceListRemarks2 = priceListRemarks2;
	}

	public String getPriceListRemarks3() {
		return priceListRemarks3;
	}

	public void setPriceListRemarks3(String priceListRemarks3) {
		this.priceListRemarks3 = priceListRemarks3;
	}

	public String getPriceListRemarks4() {
		return priceListRemarks4;
	}

	public void setPriceListRemarks4(String priceListRemarks4) {
		this.priceListRemarks4 = priceListRemarks4;
	}

	
	public String getPriceValidity() {
		return priceValidity;
	}

	public void setPriceValidity(String priceValidity) {
		this.priceValidity = priceValidity;
	}

	public Double getResaleMargin() {
		return this.resaleMargin;
	}

	public Double getTargetMargin() {
		return this.targetMargin;
	}

	public Boolean getAllocationFlag() {
		return allocationFlag;
	}

	public void setAllocationFlag(Boolean allocationFlag) {
		this.allocationFlag = allocationFlag;
	}

	public Boolean getBmtCheckedFlag() {
		return bmtCheckedFlag;
	}

	public void setBmtCheckedFlag(Boolean bmtCheckedFlag) {
		this.bmtCheckedFlag = bmtCheckedFlag;
	}

	public String getBomNumber() {
		return bomNumber;
	}

	public void setBomNumber(String bomNumber) {
		this.bomNumber = bomNumber;
	}

	public String getBomRemarks() {
		return bomRemarks;
	}

	public void setBomRemarks(String bomRemarks) {
		this.bomRemarks = bomRemarks;
	}

	public String getCompetitorInformation() {
		return competitorInformation;
	}

	public void setCompetitorInformation(String competitorInformation) {
		this.competitorInformation = competitorInformation;
	}

	public Double getCost() {
		/*
		if(this.cost != null && this.cost == 0.0)
			return null;
		 */
		return cost;
	}

	public void setCost(Double cost) {
		this.cost = cost;
	}

	public String getCostIndicator() {
		return costIndicator;
	}

	public void setCostIndicator(String costIndicator) {
		this.costIndicator = costIndicator;
	}

	public Integer getEau() {
		return eau;
	}

	public void setEau(Integer eau) {
		this.eau = eau;
	}

	public String getEnquirySegment() {
		return enquirySegment;
	}

	public void setEnquirySegment(String enquirySegment) {
		this.enquirySegment = enquirySegment;
	}

	public String getFirstRfqCode() {
		return firstRfqCode;
	}

	public void setFirstRfqCode(String firstRfqCode) {
		this.firstRfqCode = firstRfqCode;
	}

	public String getInternalComment() {
		return internalComment;
	}

	public void setInternalComment(String internalComment) {
		this.internalComment = internalComment;
	}

	public Integer getItemSeq() {
		return itemSeq;
	}

	public void setItemSeq(Integer itemSeq) {
		this.itemSeq = itemSeq;
	}

	public Date getLastPmUpdatedOn() {
		return lastPmUpdatedOn;
	}

	public void setLastPmUpdatedOn(Date lastPmUpdatedOn) {
		this.lastPmUpdatedOn = lastPmUpdatedOn;
	}

	public Date getLastQcUpdatedOn() {
		return lastQcUpdatedOn;
	}

	public void setLastQcUpdatedOn(Date lastQcUpdatedOn) {
		this.lastQcUpdatedOn = lastQcUpdatedOn;
	}

	public Date getLastUpdatedOn() {
		return lastUpdatedOn;
	}

	public void setLastUpdatedOn(Date lastUpdatedOn) {
		this.lastUpdatedOn = lastUpdatedOn;
	}



	public String getLeadTime() {
		return leadTime;
	}

	public void setLeadTime(String leadTime) {
		this.leadTime = leadTime;
	}

	public String getDesignLocation() {
		return designLocation;
	}

	public void setDesignLocation(String designLocation) {
		this.designLocation = designLocation;
	}

	public Boolean getLoaFlag() {
		return loaFlag;
	}

	public void setLoaFlag(Boolean loaFlag) {
		this.loaFlag = loaFlag;
	}

	public Integer getMpq() {
		return mpq;
	}

	public void setMpq(Integer mpq) {
		this.mpq = mpq;
	}
 
	public Integer getMoq() {
		return moq;
	}

	public void setMoq(Integer moq) {	
		this.moq = moq;
	}

	public Integer getMov() {
		return mov;
	}

	public void setMov(Integer mov) {	
		this.mov = mov;
	}

	public String getMpSchedule() {
		return mpSchedule;
	}

	public void setMpSchedule(String mpSchedule) {
		this.mpSchedule = mpSchedule;
	}

	public Boolean getMultiUsageFlag() {
		return multiUsageFlag;
	} 

	public void setMultiUsageFlag(Boolean multiUsageFlag) {
		this.multiUsageFlag = multiUsageFlag;
	}

	public String getPmoq() {
		return pmoq;
	}  

	public void setPmoq(String pmoq) {
		this.pmoq = pmoq;
	}

	public Date getPoExpiryDate() {
		return poExpiryDate;
	}

	public void setPoExpiryDate(Date poExpiryDate) {
		this.poExpiryDate = poExpiryDate;
	}

	public String getPpSchedule() {
		return ppSchedule;
	}

	public void setPpSchedule(String ppSchedule) {
		this.ppSchedule = ppSchedule;
	}

	public String getQcComment() {
		return qcComment;
	}

	public void setQcComment(String qcComment) {
		this.qcComment = qcComment;
	}

	public String getQtyIndicator() {
		return qtyIndicator;
	}

	public void setQtyIndicator(String qtyIndicator) {
		this.qtyIndicator = qtyIndicator;
	}

	public Date getQuoteExpiryDate() {
		return quoteExpiryDate;
	}

	public void setQuoteExpiryDate(Date quoteExpiryDate) {
		this.quoteExpiryDate = quoteExpiryDate;
	}

	public Integer getQuotedQty() {
		if(this.quotedQty != null && this.quotedQty == 0)
			return null;
		return quotedQty;
	}

	public void setQuotedQty(Integer quotedQty) {
		this.quotedQty = quotedQty;
	}

//	public Boolean getQuotedFlag() {
//		return quotedFlag;
//	}
//
//	public void setQuotedFlag(Boolean quotedFlag) {
//		this.quotedFlag = quotedFlag;
//	}

	public Double getQuotedPrice() {
		return quotedPrice;
	}

	public void setQuotedPrice(Double quotedPrice) {
		this.quotedPrice = quotedPrice;
	}

	public Integer getRequestedQty() {
		return requestedQty;
	}

	public void setRequestedQty(Integer requestedQty) {
		this.requestedQty = requestedQty;
	}

	public String getResaleIndicator() {
		return resaleIndicator;
	}

	public void setResaleIndicator(String resaleIndicator) {
		this.resaleIndicator = resaleIndicator;
	}

	public Double getResaleMax() {
		return resaleMax;
	}

	public void setResaleMax(Double resaleMax) {
		this.resaleMax = resaleMax;
	}

	public Double getResaleMin() {
		return resaleMin;
	}

	public void setResaleMin(Double resaleMin) {
		this.resaleMin = resaleMin;
	}

	public Integer getCancellationWindow() {
		return cancellationWindow;
	}

	public void setCancellationWindow(Integer cancellationWindow) {
		this.cancellationWindow = cancellationWindow;
	}

	public Integer getRescheduleWindow() {
		return rescheduleWindow;
	}

	public void setRescheduleWindow(Integer rescheduleWindow) {
		this.rescheduleWindow = rescheduleWindow;
	}

	public String getRevertVersion() {
		return revertVersion;
	}

	public void setRevertVersion(String revertVersion) {
		this.revertVersion = revertVersion;
	}

	public Date getShipmentValidity() {
		return shipmentValidity;
	}

	public void setShipmentValidity(Date shipmentValidity) {
		this.shipmentValidity = shipmentValidity;
	}

	public Boolean getSprFlag() {
		return sprFlag;
	}

	public void setSprFlag(Boolean sprFlag) {
		this.sprFlag = sprFlag;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getSupplierDrNumber() {
		return supplierDrNumber;
	}

	public void setSupplierDrNumber(String supplierDrNumber) {
		this.supplierDrNumber = supplierDrNumber;
	}

	public Double getTargetResale() {
		return targetResale;
	}

	public void setTargetResale(Double targetResale) {
		this.targetResale = targetResale;
	}

	public String getTermsAndConditions() {
		return termsAndConditions;
	}

	public void setTermsAndConditions(String termsAndConditions) {
		this.termsAndConditions = termsAndConditions;
	}

	/**
	 * MFR Debit#
	 * @return
	 */
	public String getVendorDebitNumber() {
		return vendorDebitNumber;
	}

	public void setVendorDebitNumber(String vendorDebitNumber) {
		this.vendorDebitNumber = vendorDebitNumber;
	}

        /**
         * MFR Quote#
         * @return 
         */
	public String getVendorQuoteNumber() {
		return vendorQuoteNumber;
	}

	public void setVendorQuoteNumber(String vendorQuoteNumber) {
		this.vendorQuoteNumber = vendorQuoteNumber;
	}

	public Integer getVersion() {
		return version;
	}

	public void setVersion(Integer version) {
		this.version = version;
	}

	public Date getSentOutTime() {
		return sentOutTime;
	}

	public void setSentOutTime(Date sentOutTime) {
		this.sentOutTime = sentOutTime;
	}

	public String getReasonForRefresh() {
		return reasonForRefresh;
	}

	public void setReasonForRefresh(String reasonForRefresh) {
		this.reasonForRefresh = reasonForRefresh;
	}

	public List<Attachment> getAttachments() {
		return attachments;
	}

	public void setAttachments(List<Attachment> attachments) {
		this.attachments = attachments;
	}

	public Quote getQuote() {
		return quote;
	}

	public void setQuote(Quote quote) {
		this.quote = quote;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	public String getRemarks() {
		return remarks;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}

	public String getCustomerType() {
		return customerType;
	}

	public void setCustomerType(String customerType) {
		this.customerType = customerType;
	}

	public String getMaterialTypeId()
	{
		return materialTypeId;
	}
	public void setMaterialTypeId(String materialTypeId)
	{
		this.materialTypeId = materialTypeId;
	}

	
	public String getProgramType()
	{
		return programType;
	}
	public void setProgramType(String programType)
	{
		this.programType = programType;
	}
	public ProductGroup getProductGroup2() {
		return productGroup2;
	}

	public void setProductGroup2(ProductGroup productGroup2) {
		this.productGroup2 = productGroup2;
	}

	public Long getDrmsNumber() {
		return drmsNumber;
	}

	public void setDrmsNumber(Long drmsNumber) {
		this.drmsNumber = drmsNumber;
	}

	public String getStage() {
		return stage;
	}

	public void setStage(String stage) {
		this.stage = stage;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((getAttachments() == null) ? 0 : getAttachments().hashCode());
		result = prime * result + ((moq == null) ? 0 : moq.hashCode());
		result = prime * result + ((mpq == null) ? 0 : mpq.hashCode());
		result = prime * result
				+ ((requestedMfr == null) ? 0 : requestedMfr.hashCode());
		result = prime * result
				+ ((requestedPartNumber == null) ? 0 : requestedPartNumber.hashCode());
		
		
		result = prime * result
				+ ((requestedQty == null) ? 0 : requestedQty.hashCode());
		result = prime * result
				+ ((targetResale == null) ? 0 : targetResale.hashCode());
		return result;
	}



	public Integer getVendorQuoteQty() {
		return vendorQuoteQty;
	}

	public void setVendorQuoteQty(Integer vendorQuoteQty) {
		this.vendorQuoteQty = vendorQuoteQty;
	}

	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("QuoteItem [")
		.append(" quoteNumber=").append(quoteNumber)
		.append(" version=").append(version)
		.append(" id=").append(id)
		.append(" mpq=").append(mpq)
		.append(" moq=").append(moq);

		sb.append(" requestedMfr=").append(requestedMfr);
		sb.append(" requestedPartNumber=").append(requestedPartNumber);
		sb.append(" requestedQty=").append(requestedQty)
		.append(" targetResale=").append(targetResale)
		.append(" remarks=").append(remarks);
		sb.append(" ]");
		return sb.toString();
	}
	
	public Double getFinalQuotationPrice() {
		return finalQuotationPrice;
	}
//	public static String copy(String str, int n) {
//	    String result = str;
//	    for(int i = 0; i< n; i++) {
//	       result = result.concat(str);
//	    }
//	    return result;
//	}
	public String getFinalQuotationPriceToString() {
		if(this.finalQuotationPrice!=null){
			String str1=(finalQuotationPrice+"");
			String substring2 = str1.substring(0,3).replace(".", "");
			//System.out.println(substring2);
			if(str1.contains("E")){
				String substring = str1.substring(str1.length()-1, str1.length());
				int parseInt = Integer.parseInt(substring);
				 String result = "";
				 for(int i = 0; i< parseInt; i++) {
					
				       result = result.concat("0");
				     //Removed by DamonChen@20181226
				     //  System.out.println(result);
				    }
				 if(result.length()>1){
					return result= result.substring(0,1)+"."+result.substring(1,result.length())+substring2;
				 }
				//Removed by DamonChen@20181226
				 //System.out.println(result);
			}
			
		}
		return null;
	}
	public void setFinalQuotationPrice(Double finalQuotationPrice) {
		this.finalQuotationPrice = finalQuotationPrice;
	}
	public Boolean getValidFlag() {
		return validFlag;
	}
	public void setValidFlag(Boolean validFlag) {
		this.validFlag = validFlag;
	}
	public String getEndCustomerName() {
		return endCustomerName;
	}
	public void setEndCustomerName(String endCustomerName) {
		this.endCustomerName = endCustomerName;
	}
	public String getShipToCustomerName() {
		return shipToCustomerName;
	}
	public void setShipToCustomerName(String shipToCustomerName) {
		this.shipToCustomerName = shipToCustomerName;
	}
	public ProductGroup getProductGroup1() {
		return productGroup1;
	}
	public void setProductGroup1(ProductGroup productGroup1) {
		this.productGroup1 = productGroup1;
	}
	public String getProductGroup3() {
		return productGroup3;
	}
	public void setProductGroup3(String productGroup3) {
		this.productGroup3 = productGroup3;
	}
	public String getProductGroup4() {
		return productGroup4;
	}
	public void setProductGroup4(String productGroup4) {
		this.productGroup4 = productGroup4;
	}
	public Double getReferenceMargin() {
		return referenceMargin;
	}
	public void setReferenceMargin(Double referenceMargin) {
		this.referenceMargin = referenceMargin;
	}

	public List<Attachment> getAttachments(String type){
		List<Attachment> outcome = new ArrayList<>();				
		if(this.getAttachments() != null && this.getAttachments().size() > 0){
			for(Attachment attach : getAttachments()){
				if(attach != null && attach.getType().equals(type)){
					outcome.add(attach);
				}
			}
		}
		return outcome;
	}

	public String getAlternativeQuoteNumber() {
		return alternativeQuoteNumber;
	}
	public void setAlternativeQuoteNumber(String alternativeQuoteNumber) {
		this.alternativeQuoteNumber = alternativeQuoteNumber;
	}

	public String getMultiUsageFlagStr() {
		if(this.multiUsageFlag != null && this.multiUsageFlag)
			return "Yes";
		else
			return "No";
	}

	public String getAllocationFlagStr() {
		if(this.allocationFlag != null && this.allocationFlag)
			return "Yes";
		else
			return "No";
	}

	public String getSprFlagStr(){
		if(this.sprFlag != null && this.sprFlag)
			return "Yes";
		else
			return "No";		
	}

	public String getBmtCheckedFlagStr(){
		if(this.bmtCheckedFlag != null && this.bmtCheckedFlag)
			return "Yes";
		else
			return "No";		
	}


	public String getLoaFlagStr() {
		if(this.loaFlag != null && this.loaFlag)
			return "Yes";
		else
			return "No";
	}	


	public String getSoldToCustomerFullName(){
		if(this.soldToCustomer != null){
			String customerName = soldToCustomer.getCustomerFullName();
			return customerName;
		}
		return null;	
	}

	public String getEndCustomerFullName(){
		if(this.endCustomer != null){
			String customerName = endCustomer.getCustomerFullName();
			return customerName;
		}
		return this.endCustomerName;
	}	

	public String getShipToCustomerFullName(){
		if(this.shipToCustomer != null){
			String customerName = shipToCustomer.getCustomerFullName();
			return customerName;
		}
		return this.shipToCustomerName;
	}	


	public String getTargetMarginStr() {
		Double tempTargetMargin = this.getTargetMargin();
		if(tempTargetMargin != null){
			return df2.format(tempTargetMargin)+"%";			
		}
		return null;
	}

	public void setMultiUsageFlagStr(String value) {
		if(value != null && value.equals("Yes")){
			setMultiUsageFlag(true);
		} else {
			setMultiUsageFlag(false);
		}
	}

	public void setAllocationFlagStr(String value) {	
		if(value != null && value.equals("Yes")){
			setAllocationFlag(true);
		} else {
			setAllocationFlag(false);
		}
	}

	public void setSprFlagStr(String value){
		if(value != null && value.equals("Yes")){
			setSprFlag(true);
		} else {
			setSprFlag(false);
		}	
	}

	public void setBmtCheckedFlagStr(String value){
		if(value != null && value.equals("Yes")){
			setBmtCheckedFlag(true);
		} else {
			setBmtCheckedFlag(false);
		}				
	}


	public void setLoaFlagStr(String value) {
		if(value != null && value.equals("Yes")){
			setLoaFlag(true);
		} else {
			setLoaFlag(false);
		}	
	}	

	public void setResaleMargin(Double value) {
		this.resaleMargin = value;
	}

	public String getPoExpiryDateStr(){
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		if(this.poExpiryDate != null){
			return sdf.format(this.poExpiryDate);
		} else {
			return null;
		}
	}

	public String getQuoteExpiryDateStr(){
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		if(this.quoteExpiryDate != null){
			return sdf.format(this.quoteExpiryDate);
		} else {
			return null;
		}
	}

	public int getPendingDay(){
		Date date = submissionDate;
		return getPendingDay(date);
	}
	private int getPendingDay(Date date) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);
		Calendar today = Calendar.getInstance();
		today.set(Calendar.HOUR_OF_DAY, 0);
		today.set(Calendar.MINUTE, 0);
		today.set(Calendar.SECOND, 0);
		today.set(Calendar.MILLISECOND, 0);
		if(cal.after(today) || cal.equals(today)){
			return 0;
		}
		int diff = 0;
		if(cal.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY || cal.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY){
			diff--;
		}
		do{
			cal.add(Calendar.DAY_OF_MONTH, 1);
			if(cal.get(Calendar.DAY_OF_WEEK) != Calendar.SATURDAY && cal.get(Calendar.DAY_OF_WEEK) != Calendar.SUNDAY){
				diff++;
			}
		}while(! cal.equals(today));
		return diff < 0 ? 0 : diff;
	}

	public int getPendingDayForIT(){
		Date date = getLastItUpdateTime();
		if(date!=null)
		{
			return getPendingDay(date);
		}
		else
		{
			return -1; //represents getLastItUpdateTime in db table is null
		}
	}


	public static Comparator<QuoteItem> alternativeQuoteNumberComparator = new Comparator<QuoteItem>() {

		public int compare(QuoteItem quoteItem1, QuoteItem quoteItem2) {

			if(quoteItem1.getAlternativeQuoteNumber() == null)
				return 1;
			if(quoteItem2.getAlternativeQuoteNumber() == null)
				return -1;

			//ascending order
			return quoteItem1.getAlternativeQuoteNumber().compareTo(quoteItem2.getAlternativeQuoteNumber());

			//descending order
			//return quoteItem2.getAlternativeQuoteNumber().compareTo(quoteItem1.getAlternativeQuoteNumber());
		}

	};

	public boolean hasDifferentValue(String val, String oldValue, String newValid){
		if(oldValue == null && newValid == null){
			return false;
		} else if((oldValue == null && newValid != null) || (oldValue != null && newValid == null)){
			//logger.log(Level.INFO, val + " : " + oldValue + " => " + newValid);
			return true;
		} else {
			if(oldValue.equals(newValid)){
				return false;
			} else {
				//logger.log(Level.INFO, val + " : " + oldValue + " => " + newValid);
				return true;
			}
		}
	}

	public boolean hasDifferentValue(String val, Integer oldValue, Integer newValid){
		if(oldValue == null && newValid == null){
			return false;
		} else if((oldValue == null && newValid != null) || (oldValue != null && newValid == null)){
			//logger.log(Level.INFO, val + " : " + oldValue + " => " + newValid);
			return true;
		} else {
			if(oldValue == newValid){
				return false;
			} else {
				//logger.log(Level.INFO, val + " : " + oldValue + " => " + newValid);
				return true;
			}
		}
	}

	public boolean hasDifferentValue(String val, Double oldValue, Double newValid){
		if(oldValue == null && newValid == null){
			return false;
		} else if((oldValue == null && newValid != null) || (oldValue != null && newValid == null)){
			//logger.log(Level.INFO, val + " : " + oldValue + " => " + newValid);
			return true;
		} else {
			if(oldValue == newValid){
				return false;
			} else {
				//logger.log(Level.INFO, val + " : " + oldValue + " => " + newValid);
				return true;
			}
		}
	}	

	public boolean hasDifferentValue(String val, Boolean oldValue, Boolean newValid){
		if(oldValue == null && newValid == null){
			return false;
		} else if((oldValue == null && newValid != null) || (oldValue != null && newValid == null)){
			//logger.log(Level.INFO, val + " : " + oldValue + " => " + newValid);
			return true;
		} else {
			if((oldValue && newValid) || (!oldValue && !newValid)){
				return false;
			} else {
				//logger.log(Level.INFO, val + " : " + oldValue + " => " + newValid);
				return true;
			}
		}
	}

	public boolean hasDifferentValue(String val, Date oldValue, Date newValid){
		if(oldValue == null && newValid == null){
			return false;
		} else if(((oldValue == null && newValid != null)) || (oldValue != null && newValid == null)){
			//logger.log(Level.INFO, val + " : " + oldValue + " => " + newValid);
			return true;
		} else {
			if(oldValue.getTime() == newValid.getTime()){
				return false;
			} else {
				//logger.log(Level.INFO, val + " : " + oldValue + " => " + newValid);
				return true;
			}
		}
	}

	public boolean hasDifferentValue(String val, Material oldValue, Material newValid){
		if(oldValue == null && newValid == null){
			return false;
		}else if(((oldValue == null && newValid != null)) || (oldValue != null && newValid == null)){
			//logger.log(Level.INFO, val + " : " + oldValue + " => " + newValid);
			return true;
		} else {
			if(oldValue.getId() == newValid.getId()){
				return false;
			} else {
				//logger.log(Level.INFO, val + " : " + oldValue + " => " + newValid);
				return true;
			}
		}
	}


	public static boolean isEmpty(String value) {
		return (value == null || value != null && value.trim().length() == 0);
	}
	public String getQuotedQtyStr() {
		if(quotedQtyStr == null){
			this.quotedQtyStr = (quotedQty==null?null:String.valueOf(quotedQty));
		}
		return quotedQtyStr;
	}
	public void setQuotedQtyStr(String quotedQtyStr) {
//		if(hasDifferentValue("quotedQtyStr", this.quotedQtyStr, quotedQtyStr)) setWarning(NOT_NULL);
		if(!isEmpty(quotedQtyStr)){
		try {
			this.quotedQty = Integer.parseInt(quotedQtyStr);
		} catch (Exception ex){				
			LOGGER.log(Level.WARNING, "Exception in parsing to integer"+quotedQtyStr+" , Exception Message : "+ex.getMessage(), ex);
		}		
		}
		this.quotedQtyStr = quotedQtyStr;
	}
	/*
	public String getShipmentValidityStr() {
		if(shipmentValidityStr == null){
			this.shipmentValidityStr = (shipmentValidity==null?null:sdf.format(shipmentValidity));
		}
		return shipmentValidityStr;
	}
	public void setShipmentValidityStr(String shipmentValidityStr) {
//		if(hasDifferentValue("shipmentValidityStr", this.shipmentValidityStr, shipmentValidityStr)) setWarning(NOT_NULL);	
		this.shipmentValidityStr = shipmentValidityStr;
	}
	*/
	/*
	public String getQuotationEffectiveDateStr() {
		if(quotationEffectiveDateStr == null){
			this.quotationEffectiveDateStr = (quotationEffectiveDate==null?null:sdf.format(quotationEffectiveDate));
		}
		return quotationEffectiveDateStr;
	}
	public void setQuotationEffectiveDateStr(String quotationEffectiveDateStr) {
		this.quotationEffectiveDateStr = quotationEffectiveDateStr;
	}
	*/
	public void setTargetMargin(Double targetMargin) {
		this.targetMargin = targetMargin;
	}	

	public boolean isAttachmentAvailable()
	{
		if(attachments!=null && attachments.size()>0)
		{
			return true;
		}
		else
		{
			return false;
		}
	}
	 
	public Boolean getItemAttachmentFlag() {
		if(StringUtils.isBlank(this.attachmentFlag) || attachmentFlag.length() != ATTACHMENT_TYPE_LENGTH){
            return false;
        }
		return StringUtils.equals(String.valueOf(attachmentFlag.charAt(4)), "1");
	}
	
	
	public Boolean getRefreshAttachmentFlag() {
		if(StringUtils.isBlank(this.attachmentFlag) || attachmentFlag.length() != ATTACHMENT_TYPE_LENGTH){
            return false;
        }
		return StringUtils.equals(String.valueOf(attachmentFlag.charAt(3)), "1");
	}
	
	
	public Boolean getPmAttachmentFlag() {
		if(StringUtils.isBlank(this.attachmentFlag) || attachmentFlag.length() != ATTACHMENT_TYPE_LENGTH){
            return false;
        }
		return StringUtils.equals(String.valueOf(attachmentFlag.charAt(2)), "1");
	}
	
	public Date getLastItUpdateTime() {
		return lastItUpdateTime;
	}
	public void setLastItUpdateTime(Date lastItUpdateTime) {
		this.lastItUpdateTime = lastItUpdateTime;
	}
	public Date getLastRitUpdateTime() {
		return lastRitUpdateTime;
	}
	public void setLastRitUpdateTime(Date lastRitUpdateTime) {
		this.lastRitUpdateTime = lastRitUpdateTime;
	}
	public Date getLastSqUpdateTime() {
		return lastSqUpdateTime;
	}
	public void setLastSqUpdateTime(Date lastSqUpdateTime) {
		this.lastSqUpdateTime = lastSqUpdateTime;
	}
	public Manufacturer getRequestedMfr()
	{
		return requestedMfr;
	}
	public void setRequestedMfr(Manufacturer requestedMfr)
	{
		this.requestedMfr = requestedMfr;
	}
	public String getRequestedPartNumber()
	{
		return requestedPartNumber;
	}
	public void setRequestedPartNumber(String requestedPartNumber)
	{
		this.requestedPartNumber = requestedPartNumber;
	}
	public Material getRequestedMaterialForProgram()
	{
		return requestedMaterialForProgram;
	}
	@Transient
	public void setRequestedMaterialForProgram(Material requestedMaterialForProgram)
	{
		this.requestedMaterialForProgram = requestedMaterialForProgram;
	}
	
	
	public ProgramPricer getRequestedProgramMaterialForProgram() {
		return requestedProgramMaterialForProgram;
	}
	@Transient
	public void setRequestedProgramMaterialForProgram(
			ProgramPricer requestedProgramMaterialForProgram) {
		this.requestedProgramMaterialForProgram = requestedProgramMaterialForProgram;
	}
	
	public String getMigrationBatchNumber() {
		return migrationBatchNumber;
	}
	public void setMigrationBatchNumber(String migrationBatchNumber) {
		this.migrationBatchNumber = migrationBatchNumber;
	}
	public Manufacturer getQuotedMfr() {
		return quotedMfr;
	}
	public void setQuotedMfr(Manufacturer quotedMfr) {
		this.quotedMfr = quotedMfr;
	}
	public String getQuotedPartNumber()
	{
		return quotedPartNumber;
	}
	public void setQuotedPartNumber(String quotedPartNumber)
	{
		this.quotedPartNumber = quotedPartNumber;
	}
	public Double getAuthGp() {
		return authGp;
	}
	public void setAuthGp(Double authGp) {
		this.authGp = authGp;
	}
	public String getAuthGpReasonCode() {
		return authGpReasonCode;
	}
	public void setAuthGpReasonCode(String authGpReasonCode) {
		this.authGpReasonCode = authGpReasonCode;
	}
	public String getAuthGpReasonDesc() {
		return authGpReasonDesc;
	}
	public void setAuthGpReasonDesc(String authGpReasonDesc) {
		this.authGpReasonDesc = authGpReasonDesc;
	}
	public String getAuthGpRemark() {
		return authGpRemark;
	}
	public void setAuthGpRemark(String authGpRemark) {
		this.authGpRemark = authGpRemark;
	}
	public Date getDrExpiryDate() {
		return drExpiryDate;
	}
	public void setDrExpiryDate(Date drExpiryDate) {
		this.drExpiryDate = drExpiryDate;
	}
	public Integer getDrNedaLineNumber() {
		return drNedaLineNumber;
	}
	public void setDrNedaLineNumber(Integer drNedaLineNumber) {
		this.drNedaLineNumber = drNedaLineNumber;
	}
	public Integer getDrNedaId() {
		return drNedaId;
	}
	public void setDrNedaId(Integer drNedaId) {
		this.drNedaId = drNedaId;
	}
	public boolean getDrmsUpdated() {
		return drmsUpdated;
	}
	public void setDrmsUpdated(boolean drmsUpdated) {
		this.drmsUpdated = drmsUpdated;
	}	


	public String getSmpq() {
        if(mpq == null){
               return null;
        }
        return String.valueOf(mpq);
	}

	public void setSmpq(String smpq) {
        if(isEmpty(smpq)){
               mpq = null;
        }
        else
        {
        	try{
        		mpq = Integer.parseInt(smpq);
        	}catch(Exception e){
        		LOGGER.log(Level.WARNING, "Exception in parsing to integer"+smpq+" , Exception Message : "+e.getMessage(), e);
        		mpq = null;
        	}
        }
	}
	
	public String getSmoq() {
        if(moq == null){
               return null;
        }
        return String.valueOf(moq);
	}

	public void setSmoq(String smoq) {
        if(isEmpty(smoq)){
               moq = null;
        }
        else
        {
        	try{
        		moq = Integer.parseInt(smoq);
        	}catch(Exception e){
        		LOGGER.log(Level.WARNING, "Exception in parsing to integer"+smoq+" , Exception Message : "+e.getMessage(), e);
        		moq = null;
        	}
        }
	}
	
	public String getSmov() {
        if(mov == null){
               return null;
        }
        return String.valueOf(mov);
	}

	public void setSmov(String smov) {
        if(isEmpty(smov)){
               mov = null;
        }
        else
        {
        	try{
        		mov = Integer.parseInt(smov);
        	}catch(Exception e){
        		LOGGER.log(Level.WARNING, "Exception in parsing to integer"+smov+" , Exception Message : "+e.getMessage(), e);
        		mov = null;
        	}
        }
	}
	
	public String getSvendorQuoteQty() {
        if(vendorQuoteQty == null){
               return null;
        }
        return String.valueOf(vendorQuoteQty);
	}

	public void setSvendorQuoteQty(String svendorQuoteQty) {
        if(isEmpty(svendorQuoteQty)){
        	vendorQuoteQty = null;
        }
        else
        {
        	try{
        		vendorQuoteQty = Integer.parseInt(svendorQuoteQty);
        	}catch(Exception e){
        		LOGGER.log(Level.WARNING, "Exception in parsing to integer"+svendorQuoteQty+" , Exception Message : "+e.getMessage(), e);
        		vendorQuoteQty = null;
        	}
        }
	}
	
	public String getScancellationWindow() {
        if(cancellationWindow == null){
               return null;
        }
        return String.valueOf(cancellationWindow);
	}

	public void setScancellationWindow(String scancellationWindow) {
        if(isEmpty(scancellationWindow)){
        	cancellationWindow = null;
        }
        else
        {
        	try{
        		cancellationWindow = Integer.parseInt(scancellationWindow);
        	}catch(Exception e){
        		LOGGER.log(Level.WARNING, "Exception in parsing to integer"+scancellationWindow+" , Exception Message : "+e.getMessage(), e);
        		cancellationWindow = null;
        	}
        }
	}
	
	public String getSrescheduleWindow() {
        if(rescheduleWindow == null){
               return null;
        }
        return String.valueOf(rescheduleWindow);
	}

	public void setSrescheduleWindow(String srescheduleWindow) {
        if(isEmpty(srescheduleWindow)){
        	rescheduleWindow = null;
        }
        else
        {
        	try{
        		rescheduleWindow = Integer.parseInt(srescheduleWindow);
        	}catch(Exception e){
        		LOGGER.log(Level.WARNING, "Exception in parsing to integer"+srescheduleWindow+" , Exception Message : "+e.getMessage(), e);
        		rescheduleWindow = null;
        	}
        }
	}
	public String getDrmsUpdateFailedDesc() {
		return drmsUpdateFailedDesc;
	}
	public void setDrmsUpdateFailedDesc(String drmsUpdateFailedDesc) {
		this.drmsUpdateFailedDesc = drmsUpdateFailedDesc;
	}
	public Date getSubmissionDate() {
		return submissionDate;
	}
	public void setSubmissionDate(Date submissionDate) {
		this.submissionDate = submissionDate;
	}

	public Currency getBuyCurr() {
		return buyCurr;
	}
	public void setBuyCurr(Currency buyCurr) {
		this.buyCurr = buyCurr;
	}
	
	public Currency getRfqCurr() {
		return rfqCurr;
	}
	public void setRfqCurr(Currency rfqCurr) {
		this.rfqCurr = rfqCurr;
	}
	
	
	public Currency getFinalCurr() {
		return finalCurr;
	}
	public void setFinalCurr(Currency finalCurr) {
		this.finalCurr = finalCurr;
	}
	public BigDecimal getExRateBuy() {
		return exRateBuy;
	}
	public void setExRateBuy(BigDecimal exRateFrom) {
		this.exRateBuy = exRateFrom;
	}
	public BigDecimal getExRateRfq() {
		return exRateRfq;
	}
	public void setExRateRfq(BigDecimal exRateTo) {
		this.exRateRfq = exRateTo;
	}
	public BigDecimal getVat() {
		return vat;
	}
	public void setVat(BigDecimal vat) {
		this.vat = vat;
	}
	public BigDecimal getHandling() {
		return handling;
	}
	public void setHandling(BigDecimal handling) {
		this.handling = handling;
	}
	public Double getOrginalAuthGp() {
		return orginalAuthGp;
	}
	public void setOrginalAuthGp(Double orginalAuthGp) {
		this.orginalAuthGp = orginalAuthGp;
	}
	public String getLastUpdatedPm()
	{
		return lastUpdatedPm;
	}
	public void setLastUpdatedPm(String lastUpdatedPm)
	{
		this.lastUpdatedPm = lastUpdatedPm;
	}
	public String getLastUpdatedQc()
	{
		return lastUpdatedQc;
	}
	public void setLastUpdatedQc(String lastUpdatedQc)
	{
		this.lastUpdatedQc = lastUpdatedQc;
	}
	public String getLastUpdatedBy()
	{
		return lastUpdatedBy;
	}
	public void setLastUpdatedBy(String lastUpdatedBy)
	{
		this.lastUpdatedBy = lastUpdatedBy;
	}
	public String getLastUpdatedPmName()
	{
		return lastUpdatedPmName;
	}
	public void setLastUpdatedPmName(String lastUpdatedPmName)
	{
		this.lastUpdatedPmName = lastUpdatedPmName;
	}
	public String getLastUpdatedQcName()
	{
		return lastUpdatedQcName;
	}
	public void setLastUpdatedQcName(String lastUpdatedQcName)
	{
		this.lastUpdatedQcName = lastUpdatedQcName;
	}
	public String getLastUpdatedName()
	{
		return lastUpdatedName;
	}
	public void setLastUpdatedName(String lastUpdatedName)
	{
		this.lastUpdatedName = lastUpdatedName;
	}
	
	
	
	
	public Boolean getUsedFlag()
	{
		return this.usedFlag;
	}

	public void setUsedFlag(Boolean usedFlag)
	{
		this.usedFlag = usedFlag;
	}
	
	

	public Date getQuotationEffectiveDate()
	{
		return this.quotationEffectiveDate;
	}

	public void setQuotationEffectiveDate(Date quotationEffectiveDate)
	{
		this.quotationEffectiveDate = quotationEffectiveDate;
	}
	
	
	
	public String getQuoteType()
	{
		return this.quoteType;
	}

	public void setQuoteType(String quoteType)
	{
		this.quoteType = quoteType;
	}
	public String getDesignInCat()
	{
		return designInCat;
	}
	public void setDesignInCat(String designInCat)
	{
		this.designInCat = designInCat;
	}
	
	

	public String getFisYear()
	{
		return this.fisYear;
	}

	public void setFisYear(String fisYear)
	{
		this.fisYear = fisYear;
	}

	public String getFisQtr()
	{
		return this.fisQtr;
	}

	public void setFisQtr(String fisQtr)
	{
		this.fisQtr = fisQtr;
	}


	public String getFisMth()
	{
		return this.fisMth;
	}

	public void setFisMth(String fisMth)
	{
		this.fisMth = fisMth;
	}	
	
	public String getSapPartNumber() {
		return this.sapPartNumber;
	}

	public void setSapPartNumber(String sapPartNumber) {
		this.sapPartNumber = sapPartNumber;
	}

	public QuoteItemDesign getQuoteItemDesign() {
		return quoteItemDesign;
	}
	
	public void setQuoteItemDesign(QuoteItemDesign quoteItemDesign) {
		this.quoteItemDesign = quoteItemDesign;
	}
	
	public List<QuoteResponseTimeHistory> getQuoteResponseTimeHistorys() {
		return quoteResponseTimeHistorys;
	}
	
	public void setQuoteResponseTimeHistorys(List<QuoteResponseTimeHistory> quoteResponseTimeHistorys) {
		this.quoteResponseTimeHistorys = quoteResponseTimeHistorys;
	}


	public String getDesignRegion() {
		return designRegion;
	}
	
	public void setDesignRegion(String designRegion) {
		this.designRegion = designRegion;
	}
	public String getDpReferenceLineId() {
		return dpReferenceLineId;
	}
	public void setDpReferenceLineId(String dpReferenceLineId) {
		this.dpReferenceLineId = dpReferenceLineId;
	}
	public String getBmtDescCode() {
		return bmtDescCode;
	}
	public void setBmtDescCode(String bmtDescCode) {
		this.bmtDescCode = bmtDescCode;
	}
	public String getDupMatchCode() {
		return dupMatchCode;
	}
	public void setDupMatchCode(String dupMatchCode) {
		this.dupMatchCode = dupMatchCode;
	}	

	public String getLastUpdatedBmt() {
		return lastUpdatedBmt;
	}
	public void setLastUpdatedBmt(String lastUpdatedBmt) {
		this.lastUpdatedBmt = lastUpdatedBmt;
	}
	
	public Date getLastQcInDate() {
		return lastQcInDate;
	}
	public void setLastQcInDate(Date lastQcInDate) {
		this.lastQcInDate = lastQcInDate;
	}
	public Date getLastQcOutDate() {
		return lastQcOutDate;
	}
	public void setLastQcOutDate(Date lastQcOutDate) {
		this.lastQcOutDate = lastQcOutDate;
	}
	public Date getLastSqInDate() {
		return lastSqInDate;
	}
	public void setLastSqInDate(Date lastSqInDate) {
		this.lastSqInDate = lastSqInDate;
	}
	public Date getLastSqOutDate() {
		return lastSqOutDate;
	}
	public void setLastSqOutDate(Date lastSqOutDate) {
		this.lastSqOutDate = lastSqOutDate;
	}
	public Date getLastBmtInDate() {
		return lastBmtInDate;
	}
	public void setLastBmtInDate(Date lastBmtInDate) {
		this.lastBmtInDate = lastBmtInDate;
	}
	public Date getLastBmtOutDate() {
		return lastBmtOutDate;
	}
	public void setLastBmtOutDate(Date lastBmtOutDate) {
		this.lastBmtOutDate = lastBmtOutDate;
	}
	public Date getLastBmtUpdateTime() {
		return lastBmtUpdateTime;
	}
	public void setLastBmtUpdateTime(Date lastBmtUpdateTime) {
		this.lastBmtUpdateTime = lastBmtUpdateTime;
	}
	
	public String getRateRemarks() {
		return rateRemarks;
	}
	public void setRateRemarks(String rateRemarks) {
		this.rateRemarks = rateRemarks;
	}

	/*public ExchangeRateType getExchangeRateType() {
		return exchangeRateType;
	}
	public void setExchangeRateType(ExchangeRateType exchangeRateType) {
		this.exchangeRateType = exchangeRateType;
	}*/
	public BigDecimal getExRateFnl() {
		return exRateFnl;
	}
	public void setExRateFnl(BigDecimal exRateFnl) {
		this.exRateFnl = exRateFnl;
	}

	
	public String getAttachmentFlag() {
		return attachmentFlag;
	}
	
	public void setAttachmentFlag(String attachmentFlag) {
		this.attachmentFlag = attachmentFlag;
	}

	public Boolean getQcAttachmentFlag() {
		if(StringUtils.isBlank(this.attachmentFlag) || attachmentFlag.length() != ATTACHMENT_TYPE_LENGTH){
            return false;
        }
		return StringUtils.equals(String.valueOf(attachmentFlag.charAt(1)), "1");
	}
	
	public Boolean getBmtAttachmentFlag() {
		if(StringUtils.isBlank(this.attachmentFlag) || attachmentFlag.length() != ATTACHMENT_TYPE_LENGTH){
            return false;
        }
		return StringUtils.equals(String.valueOf(attachmentFlag.charAt(0)), "1");
	}
	
	public Boolean getHasAnyAttachment() {
		if(StringUtils.isBlank(this.attachmentFlag) || attachmentFlag.length() != ATTACHMENT_TYPE_LENGTH){
            return false;
        }
		return attachmentFlag.contains("1");
	}
	
	
	public boolean isSalesCostFlag() {
		return salesCostFlag;
	}
	public void setSalesCostFlag(boolean salesCostFlag) {
		this.salesCostFlag = salesCostFlag;
	}
	public SalesCostType getSalesCostType() {
		return salesCostType;
	}
	public void setSalesCostType(SalesCostType salesCostType) {
		this.salesCostType = salesCostType;
	}
	public BigDecimal getSalesCost() {
		return salesCost;
	}
	public void setSalesCost(BigDecimal salesCost) {
		this.salesCost = salesCost;
	}
	public BigDecimal getSuggestedResale() {
		return suggestedResale;
	}
	public void setSuggestedResale(BigDecimal suggestedResale) {
		this.suggestedResale = suggestedResale;
	}
	public String getCustomerGroupId() {
		return customerGroupId;
	}
	public void setCustomerGroupId(String custoemrGroupId) {
		this.customerGroupId = custoemrGroupId;
	}
	
	public long getPricerId() {
		return pricerId;
	}
	public void setPricerId(long pricerId) {
		this.pricerId = pricerId;
	}
	
	public Pricer getPricer() {
		EntityManager em = ThreadLocalEntityManager.em();
		if (em == null || em.isOpen() == false) {
			return null;
		}
		return em.find(Pricer.class, pricerId);
	}

	public String getAction() {
		return action;
	}
	
	public void setAction(String action) {
		this.action = action;
	}
	public String getReSubmitType() {
		return reSubmitType;
	}
	public void setReSubmitType(String reSubmitType) {
		this.reSubmitType = reSubmitType;
	}
	
	@Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if(obj == null){
            return false;
        }
        if(obj instanceof QuoteItem){
           final QuoteItem o = (QuoteItem)obj;
           return o.getId()==(getId());
        }
        return false;
    }
	
	
	/*
	 * to do to remove
	public void clearPricingInfo() {
		
		setQuotedPrice(null);
		setResaleMargin(null);
		setTargetMargin(null);
		setReferenceMargin(null);
		setQuotedQty(null);
		
		setCost(null);
		setCostIndicator(null);
		setMinSellPrice(null);
		setBottomPrice(null);
		setMpq(null);
		setMoq(null);
		setMov(null);
		setLeadTime(null);
		setPriceValidity(null);
		setShipmentValidity(null);
		setQuotationEffectiveDate(null);
		
		setPriceListRemarks1(null);
		setPriceListRemarks2(null);
		setPriceListRemarks3(null);
		setPriceListRemarks4(null);
		setDescription(null);
		setTermsAndConditions(null);
		
	}
	*/
	
	
	public PricerInfo createPricerInfo() {
		PricerInfo pricerInfo = new PricerInfo();
		pricerInfo.setPricerId(pricerId);
    	pricerInfo.setMfr(quotedMfr.getName());
    	pricerInfo.setQuotedMfr(quotedMfr);
    	pricerInfo.setFullMfrPartNumber(quotedPartNumber);
    	pricerInfo.setQuotedMaterial(quotedMaterial);
    	pricerInfo.setBizUnit(getQuote().getBizUnit().getName());
    	pricerInfo.setSoldToCustomer(getSoldToCustomer());
    	pricerInfo.setEndCustomer(getEndCustomer());
    	pricerInfo.setSalesId(getQuote().getSales().getId());
    	pricerInfo.setRequestedQty(requestedQty);
    	
    	 
    	pricerInfo.setAllocationFlag(allocationFlag);
    	pricerInfo.setQcComment(aqcc);
    	pricerInfo.setBottomPrice(bottomPrice);
    	pricerInfo.setCancellationWindow(cancellationWindow);
    	pricerInfo.setCost(cost);
    	pricerInfo.setCostIndicator(costIndicator);
    	pricerInfo.setDescription(description);
    	pricerInfo.setLeadTime(leadTime);
    	pricerInfo.setMaterialTypeId(materialTypeId);
    	pricerInfo.setMinSell(minSellPrice);
    	pricerInfo.setMoq(moq);
    	pricerInfo.setMov(mov);
    	pricerInfo.setMpq(mpq);
    	pricerInfo.setMultiUsageFlag(multiUsageFlag);
    	pricerInfo.setPmoq(pmoq);
    	pricerInfo.setPoExpiryDate(poExpiryDate);
    	pricerInfo.setPriceListRemarks1(priceListRemarks1);
    	pricerInfo.setPriceListRemarks2(priceListRemarks2);
    	pricerInfo.setPriceListRemarks3(priceListRemarks3);
    	pricerInfo.setPriceListRemarks4(priceListRemarks4);
    	pricerInfo.setPriceValidity(priceValidity);
    	pricerInfo.setProductGroup1(productGroup1);
    	pricerInfo.setProductGroup2(productGroup2);
    	pricerInfo.setProductGroup3(productGroup3);
    	pricerInfo.setProductGroup4(productGroup4);
    	pricerInfo.setProgramType(programType);
    	pricerInfo.setQtyIndicator(qtyIndicator);
    	pricerInfo.setQuoteExpiryDate(quoteExpiryDate);
    	pricerInfo.setQuotedPrice(quotedPrice);
    	pricerInfo.setQuotedQty(quotedQty);
    	pricerInfo.setResaleIndicator(resaleIndicator);
    	pricerInfo.setReschedulingWindow(rescheduleWindow);
    	pricerInfo.setSalesCost(salesCost);
    	pricerInfo.setSalesCostFlag(salesCostFlag);
    	pricerInfo.setSalesCostType(salesCostType);
    	pricerInfo.setShipmentValidity(shipmentValidity);
    	pricerInfo.setSuggestedResale(suggestedResale);
    	pricerInfo.setTermsAndConditions(termsAndConditions);
    	pricerInfo.setVendorQuoteNumber(vendorQuoteNumber);
    	pricerInfo.setVendorQuoteQty(vendorQuoteQty);
    	
    	pricerInfo.setRfqCurr(rfqCurr.toString());
    	if(buyCurr !=null){
    	pricerInfo.setBuyCurr(buyCurr.toString());
    	}
    	pricerInfo.setSubmitDate(submissionDate);
    	pricerInfo.setExRateDate(this.getExRateDate());
		return pricerInfo;
	}

	public RfqItemVO createRfqItemVO() {
		RfqItemVO rfqItem = new RfqItemVO();
    	rfqItem.setMfr(quotedMfr.getName());
    	rfqItem.setRequiredPartNumber(requestedPartNumber);
    	
    	RfqHeaderVO rfqHeader =  new RfqHeaderVO();
    	rfqItem.setRfqHeaderVO(rfqHeader);
    	rfqHeader.setBizUnit(getQuote().getBizUnit().getName());
    	rfqHeader.setSalesEmployeeNumber(getQuote().getSales().getEmployeeId());
    	rfqItem.setSoldToCustomer(getSoldToCustomer());
    	rfqItem.setEndCustomer(getEndCustomer());
    	rfqItem.setRequiredQty(String.valueOf(requestedQty));
    	
    	
    	rfqItem.setAllocationFlag(allocationFlag);
    	rfqItem.setQcComment(aqcc);
    	rfqItem.setBottomPrice(bottomPrice);
    	rfqItem.setCancellationWindow(cancellationWindow);
    	rfqItem.setCost(cost);
    	rfqItem.setCostIndicator(costIndicator);
    	rfqItem.setDescription(description);
    	rfqItem.setLeadTime(leadTime);
    	rfqItem.setMaterialTypeId(materialTypeId);
    	rfqItem.setMinSellPrice(minSellPrice);
    	rfqItem.setTargetResale(targetResale ==null ? null :targetResale.toString());
    	rfqItem.setMoq(moq);
    	rfqItem.setMov(mov);
    	rfqItem.setMpq(mpq);
    	rfqItem.setMultiUsage(multiUsageFlag);
    	rfqItem.setPmoq(pmoq);
    	rfqItem.setPoExpiryDate(poExpiryDate);
    	rfqItem.setPriceListRemarks1(priceListRemarks1);
    	rfqItem.setPriceListRemarks2(priceListRemarks2);
    	rfqItem.setPriceListRemarks3(priceListRemarks3);
    	rfqItem.setPriceListRemarks4(priceListRemarks4);
    	rfqItem.setPriceValidity(priceValidity);
    	rfqItem.setProductGroup1(productGroup1);
    	rfqItem.setProductGroup2(productGroup2);
    	rfqItem.setProductGroup3(productGroup3);
    	rfqItem.setProductGroup4(productGroup4);
    	rfqItem.setProgramType(programType);
    	rfqItem.setQtyIndicator(qtyIndicator);
    	rfqItem.setQuotaionEffectiveDate(quotationEffectiveDate);
    	rfqItem.setQuoteExpiryDate(quoteExpiryDate);
    	rfqItem.setQuotedQty(quotedQty);
    	rfqItem.setResaleIndicator(resaleIndicator);
    	rfqItem.setReschedulingWindow(rescheduleWindow);
    	rfqItem.setItemRemarks(remarks);
    	rfqItem.setSalesCost(salesCost);
    	rfqItem.setSalesCostFlag(salesCostFlag);
    	rfqItem.setSalesCostType(salesCostType);
    	rfqItem.setShipmentValidity(shipmentValidity);
    	rfqItem.setSuggestedResale(suggestedResale);
    	rfqItem.setTermsAndCondition(termsAndConditions);
    	rfqItem.setVendorQuoteNumber(vendorQuoteNumber);
    	rfqItem.setVendorQuoteQty(vendorQuoteQty);
    	rfqItem.setAttachments(attachments);
    	
    	
    	rfqItem.setRfqCurr(rfqCurr.name());
    	
		return rfqItem;
	}

	
	public static QuoteItem newInstance(QuoteItem quoteItem) {
		QuoteItem newQuoteItem = new QuoteItem();
		try {
			BeanUtils.copyProperties(newQuoteItem, quoteItem);
		} catch (Exception e) {
			LOGGER.log(Level.SEVERE,"Exception occured when copying quoteItem " + quoteItem.getId() + e.getMessage(),e);
		}
		newQuoteItem.setId(0);
		newQuoteItem.setQuoteNumber(null);
		
		
		QuoteItemDesign quoteItemDesign = quoteItem.getQuoteItemDesign();
		if(quoteItemDesign != null && quoteItemDesign.getId() != null){

			QuoteItemDesign newQuoteItemDesign = new QuoteItemDesign();
			try {
				BeanUtils.copyProperties(newQuoteItemDesign, quoteItemDesign);
			} catch (Exception e) {
				LOGGER.log(Level.SEVERE,"add part setup bmt data error: " + e.getMessage(),e);
			}
			newQuoteItemDesign.setId(0L);
			newQuoteItemDesign.setQuoteItem(newQuoteItem);
			newQuoteItemDesign.setVersion(0);
			newQuoteItem.setQuoteItemDesign(newQuoteItemDesign);
		}
		
		if(quoteItem.getAttachments()!=null && quoteItem.getAttachments().size()>0){
		
		List<Attachment> newAttachments = new ArrayList<>();
		for (Attachment attachment : quoteItem.getAttachments()) {
			try {
				Attachment newAttachment = new Attachment();
				BeanUtils.copyProperties(newAttachment, attachment);
				newAttachment.setId(0L);
				newAttachment.setQuoteItem(newQuoteItem);
				newAttachments.add(newAttachment);
			} catch (Exception e) {
				LOGGER.log(Level.SEVERE,"Exception occured for attachment: "+attachment.getFileName()+"copying field data error: " + e.getMessage(),e);
			}
		}
		newQuoteItem.setAttachments(newAttachments);
		
		}
		
		List<QuoteResponseTimeHistory> newHistories = new ArrayList<>();
		for (QuoteResponseTimeHistory history : quoteItem.getQuoteResponseTimeHistorys()) {
			try {
				QuoteResponseTimeHistory newHistory = new QuoteResponseTimeHistory();
				BeanUtils.copyProperties(newHistory, history);
				newHistory.setId(0L);
				newHistory.setVersion(0);
				newHistory.setQuoteItem(newQuoteItem);
				newHistories.add(newHistory);
			} catch (Exception e) {
				LOGGER.log(Level.SEVERE,"Exception occured for Quote Response time history ID: "+history.getId().toString()+", copying field data error: " + e.getMessage(),e);
			}
		}
		newQuoteItem.setQuoteResponseTimeHistorys(newHistories);
		
		return newQuoteItem;
	}
	
	public void applyNcnrFilter() {
		if (termsAndConditions != null && termsAndConditions.toUpperCase().contains("NCNR")) {
			cancellationWindow = null;
			rescheduleWindow = null;
		}
	}
	
	void calResaleMaxMin() {
		if(! StringUtils.isEmpty(resaleIndicator)){
			String minResale = resaleIndicator.substring(0,2);
			String maxResale = resaleIndicator.substring(2,4);
			if(quotedPrice != null){
				resaleMin = Double.parseDouble(minResale);
				if(maxResale.matches("[0-9]{2}"))
					resaleMax = Double.parseDouble(maxResale);
				else
					resaleMax = QuoteSBConstant.DEFAULT_RESALE_INDICATOR_MAX;
			}
		}
	}
	
	public void postProcessAfterFinish() {
		calResaleMargin();
		calTargetMargin();
		quotedMfr.applyReferenceMarginLogic(this);
		//reCalExchRate();
		//calExchRate();
		calQty();
		applyNCNRLogic();
	}
	
	public void reCalMargin() {
		//r
		this.resaleMargin = null;
		this.calResaleMargin();
		//t
		this.reCalTargetMargin();
	}
	
	public void reCalTargetMargin() {
		this.targetMargin = null;
		this.calTargetMargin();
	}
	
    private void calResaleMargin() {
    	if (resaleMargin != null) {
    		return;
    	}
    	if (cost == null || quotedPrice == null || quotedPrice == 0d) {
    		return;
    	}
    	
		if(cost == 0d) {
			this.resaleMargin = 100d;
		} else {
			this.resaleMargin= 100*(quotedPrice-cost)/quotedPrice;
		}
    }
    
    private void calTargetMargin() {
    	if (targetMargin != null) {
    		return;
    	}
    	if (cost == null || targetResale == null || targetResale == 0d) {
    		return;
    	}
    	
		if(cost == 0d) {
			this.targetMargin = 100d;
		} else {
			BigDecimal targetResaleAsBuyCurr = this.convertToBuyValueWithDouble(targetResale);
			
			if(targetResaleAsBuyCurr != null && targetResaleAsBuyCurr.doubleValue()>0d) {
				this.targetMargin = 100*(targetResaleAsBuyCurr.doubleValue()-cost)/targetResaleAsBuyCurr.doubleValue();
			}
		}
    }
    
   /*
    * use 
    * when buy curr changed by uploading
    *  quoteItem in workplant form page.
    *  This is used by quoteItem has been submitted.
    * */
    public void reCalExchRateForSubmitted() {
    	
    	ExchangeRate[] rates = Money.getExchangeRate(this.buyCurr, 
    			this.rfqCurr, getQuote().getBizUnit(), soldToCustomer, getExRateDate());
    	if (rates == null || rates[0] == null || rates[1] == null) {
    		return ;
    	}
    	exRateBuy = rates[0].getExRateTo();
		exRateRfq = rates[1].getExRateTo();
		vat = rates[1].getVat();
		handling = rates[0].getHandling().multiply(rates[1].getHandling());
    }
    
    //Japan need to use submitdate 
    //Asia need to use current date to re get exRate.
    public Date getExRateDate() {
    	MoneyConvertStrategy strategy = (MoneyConvertStrategy)StrategyFactory.getSingletonFactory().getStrategy(MoneyConvertStrategy.class,
    			this.getQuote().getBizUnit().getName());
    	LOGGER.config(() -> strategy.getClass().getName() + " used.");
    	return  strategy.getExRateDateTime(this);
    }
    
    /*private void calExchRate() {
    	ExchangeRate rate = findExchangeRate();
    	if (rate == null) {
    		return ;
    	}
    	exRateBuy = rate.getExRateFrom();
		exRateRfq = rate.getExRateTo();
		vat = rate.getVat();
		handling = rate.getHandling();
		exRateBuy = rate.getExRateFrom();
    }*/
    
	
	/*private ExchangeRate findExchangeRate() {
		if(StringUtils.isBlank(buyCurr.name())) {
			buyCurr = Currency.USD;
		}
		
		CriteriaBuilder cb = ThreadLocalEntityManager.em().getCriteriaBuilder();
		CriteriaQuery<ExchangeRate> c = cb.createQuery(ExchangeRate.class);
		Root<ExchangeRate> rate = c.from(ExchangeRate.class);
		List<Predicate> criterias = new ArrayList<Predicate>();
		criterias.add(cb.equal(rate.get("currFrom"), buyCurr));
		criterias.add(cb.equal(rate.get("currTo"), rfqCurr));
		criterias.add(cb.equal(rate.get("bizUnit"), getQuote().getBizUnit().getName()));
		criterias.add(cb.equal(rate.get("soldToCode"), getQuote().getSoldToCustomer().getCustomerNumber()));
		//criterias.add(cb.equal(rate.get("deleteFlag"), false ));
		c.where(criterias.toArray(new Predicate[]{}));
		
		TypedQuery<ExchangeRate> q = ThreadLocalEntityManager.em().createQuery(c);
		List<ExchangeRate> rates = q.getResultList();
		if(! rates.isEmpty()) {
			return rates.get(0);
		}
		
		criterias.set(3, cb.isNull(rate.get("soldToCode")));
		c.where(criterias.toArray(new Predicate[]{}));
		
		q = ThreadLocalEntityManager.em().createQuery(c);
		rates = q.getResultList();
		if(! rates.isEmpty()) {
			return rates.get(0);
		}else {
			return null;
		}
	}*/    
	
	private void calQty() {
		QtyCal qtyCal =  new QtyCal(this);
//		qtyIndicator =  qtyCal.calQtyIndicator();
//		moq = qtyCal.calMoq();
//		quotedQty = qtyCal.calQuotedQty();
		qtyCal.cal();
		qtyCal.fill(this);
		if (StringUtils.equalsIgnoreCase(qtyIndicator, "LIMIT")) {
			if (getPricer() instanceof QtyBreakPricer) {
				QtyBreakPricer qtyBreakPricer = (QtyBreakPricer) getPricer();
				qtyBreakPricer.calQtyBreakRange();
				pmoq = qtyBreakPricer.getQtyBreakPrice(quotedQty) == null ? null : qtyBreakPricer.getQtyBreakPrice(quotedQty).getQtyRange();
			}
		}
	}
	
	private void applyNCNRLogic() {
		if(termsAndConditions !=null && termsAndConditions.toUpperCase().contains("NCNR")) {
			cancellationWindow = null;
			rescheduleWindow = null;
		}
	}
	/**   
	* @Description: convertToRfqValue
	* @author 042659
	* @param salesCost2
	* @return       
	* @return Object    
	* @throws  
	*/  
	public BigDecimal convertToRfqValue(BigDecimal bigValue) {
		// TODO Auto-generated method stub
		
		
		if(bigValue ==null){
			return null;
		}
		
		if(this.exRateRfq ==null){
			return null;
		}
		
		if(this.exRateBuy ==null){
			return null;
		}
		

		return Money.convertAsToCurrAmtWithExrates(bigValue, exRateBuy, exRateRfq, handling, true);
	}
	
	public BigDecimal convertToRfqValueWithDouble(Double doubleValue) {
		
		if(doubleValue ==null){
			return null;
		}
		return convertToRfqValue(BigDecimal.valueOf(doubleValue));
	}
	
	
	
	public BigDecimal convertToBuyValue(BigDecimal bigValue, int... scale) {
		// TODO Auto-generated method stub
		if(bigValue ==null){
			return null;
		}
		
		if(this.exRateRfq ==null){
			return null;
		}
		
		if(this.exRateBuy ==null){
			return null;
		}
		return Money.convertAsToCurrAmtWithExrates(bigValue, exRateRfq, exRateBuy, handling, false, scale);
	}
	
	public BigDecimal convertToBuyValueWithDoubleAndScale(Double doubleValue, int... scale) {
		
		if(doubleValue ==null){
			return null;
		}
		return convertToBuyValue(BigDecimal.valueOf(doubleValue), scale);
	}

	public BigDecimal convertToBuyValueWithDouble(Double doubleValue) {
		
		if(doubleValue ==null){
			return null;
		}
		return convertToBuyValue(BigDecimal.valueOf(doubleValue));
	}
	
	public BigDecimal getExRate() {
		/*BigDecimal buy = getExRateBuy();
		BigDecimal rfq = getExRateRfq();
		BigDecimal handling = getHandling();*/
		if(exRateBuy != null && exRateRfq != null && handling != null ) {
			return  exRateRfq.multiply(handling).divide(exRateBuy, Constants.SCALE, Constants.ROUNDING_MODE);
		}
		return null;
	}
	
	public BigDecimal getExRateFnlCalc() {
		/*BigDecimal buy = getExRateBuy();
		BigDecimal rfq = getExRateRfq();
		BigDecimal handling = getHandling();*/
		if(exRateBuy != null && exRateFnl != null && handling != null && exRateBuy.compareTo(BigDecimal.ZERO)!=0 ) {
			return  exRateFnl.multiply(handling).divide(exRateBuy, Constants.SCALE, Constants.ROUNDING_MODE);
		}
		return null;
	}
	
	public String getResaleRange() {
		String sResaleRange = "";
		String downIndicator = "";
		String upIndicator = "";
		int downVal=0, upVal=0;
		double quotedPrice = 0;
		//logger.info("QuoteItemVo.getResaleRange: Begin");
		
		if (getResaleIndicator()!=null && !getResaleIndicator().isEmpty() && getResaleIndicator().length()>=2) {
			//LOGGER.info("QuoteItem.getResaleRange: Indicator = " + getResaleIndicator());
			downIndicator = getResaleIndicator().substring(0, 2);
			if (getResaleIndicator().length()>=4) upIndicator = getResaleIndicator().substring(2, 4);
		}

		//quotedPrice = getQuotedPriceRFQCur();
		BigDecimal qPriceRFQ = this.convertToRfqValueWithDouble(this.quotedPrice);
		
		if (qPriceRFQ!=null) {
			quotedPrice = qPriceRFQ.doubleValue();
			//quotedPrice = (double) Math.round(quotedPrice * 100) / 100; //round to 2 decimal
			
			DecimalFormat df = new DecimalFormat("#0.00000");
			if (!downIndicator.isEmpty()) {
				df.setRoundingMode(RoundingMode.UP);
				if (StringUtils.isNumeric(downIndicator)) {
					downVal = Integer.parseInt(downIndicator);
					
					if (downVal>0) sResaleRange = df.format(quotedPrice * (100-downVal)/100);
					else sResaleRange = df.format(quotedPrice);
					//if (downVal>0) sResaleRange = String.format("%.2f", quotedPrice * (100-downVal)/100);
					//else sResaleRange = String.format("%.2f", quotedPrice);
				}
				else sResaleRange = "-" + df.format(quotedPrice); 
				//sResaleRange = "-" + String.format("%.2f", quotedPrice);
			}
			
			if (!upIndicator.isEmpty()){
				df.setRoundingMode(RoundingMode.DOWN);
				if (StringUtils.isNumeric(upIndicator)) {
					upVal = Integer.parseInt(upIndicator);
					if (upVal>0) sResaleRange = sResaleRange + " - " + df.format(quotedPrice * (100+upVal)/100);
					//if (upVal>0) sResaleRange = sResaleRange + "-" + String.format("%.2f", quotedPrice * (100+upVal)/100);
					//else sResaleRange = sResaleRange;
				}
				else sResaleRange =  sResaleRange + "+"; 
			}
		}
/*		
		LOGGER.info("QuoteItem.getResaleRange: dVal=" + downVal 
				+ "; uVal=" + upVal
				+ "; dInd=" + downIndicator
				+ "; uInd=" + upIndicator
				+ "; qPrice=" + quotedPrice);
*/		
		return sResaleRange;
	}
/*
	public BigDecimal getExRateNewRFQCur() {
		if(getBuyCurr() != null && getRfqCurr() != null) {
			BigDecimal retValue = null;
			//logger.info("QuoteItem.getResaleRange: Begin");
			ExchangeRate exRates[];
			//List<ExchangeRate> exchangeRates = Money.getExchangeRate(this.getBuyCurr(), this.getRfqCurr(), this.getQuote().getBizUnit(), this.getSoldToCustomer(), new Date());
			
			//exRate = findExchangeRate();		//get new rate
			//exRate = Money.findExchangeRate(this.getBuyCurr(), this.getRfqCurr(), this.getQuote().getBizUnit(), this.getSoldToCustomer(), new Date(), exchangeRates, false);
			exRates = Money.getExchangeRate(this.getBuyCurr(), this.getRfqCurr(), this.getQuote().getBizUnit(), this.getSoldToCustomer(), new Date());
			if (exRates.length>1 && exRates[1].getHandling() !=null) {
				retValue = exRates[1].getExRateTo();
				//retValue = Money.getExchangeRate(this.getBuyCurr(), this.getRfqCurr(), this.getQuote().getBizUnit(), this.getSoldToCustomer(), new Date())[0].getExRateTo();
				retValue = retValue.multiply(exRates[1].getHandling()).divide(exRates[0].getExRateTo(), Constants.SCALE, Constants.ROUNDING_MODE);
		        LOGGER.info("QuoteItem.getExRateNewRFQCur: rate=" + retValue);
			}
			
			return retValue;
		}
		else return null;
	}

	public double getQuotedPriceRFQCurNewExRate() {
		double quotedPrice = 0;

		if (getExRateNewRFQCur()!=null){
	        quotedPrice = getQuotedPrice() * this.getExRateNewRFQCur().doubleValue();
	        LOGGER.info("QuoteItem.getQuotedPriceRFQCurNewExRate: qPrice=" + quotedPrice);
		}

		return quotedPrice;
	}
*/
	
/*	
	public BigDecimal getExRateOldRFQCur() {
		if(suggestedResale != null && exRateBuy != null && exRateRfq != null && handling != null ) {
			BigDecimal retValue;
			//logger.info("QuoteItem.getResaleRange: Begin");
	
			retValue = this.getExRateRfq();
			retValue = retValue.multiply(this.getHandling());
	        LOGGER.info("QuoteItem.getExRateOldRFQCur: rate=" + retValue);
			
			return retValue;
		}
		else return null;
	}
*/
/*	
	//Multi Currency project 201810 (whwong): added new rules to return FinalQuotationPrice for ReviseFinalQuote
	public Double getFinalQuotationPriceRevise () {
		if (this.finalQuotationPrice==null) {
			return this.getQuotedPriceRFQCur();
		}
		else return this.finalQuotationPrice;
	}

	public void setFinalQuotationPriceRevise(Double finalQuotationPriceRevise) {
		this.finalQuotationPrice = finalQuotationPriceRevise;
	}
*/	
	public BigDecimal getCalculatedExchRate (){
		if (getExRateRfq()!=null && getVat()!=null && this.getHandling()!=null){
			return getExRateRfq().multiply(this.getVat()).multiply(this.getHandling());
		}
		else return null;
	}
	
	public QuoteItem clone() {
		try {
			return (QuoteItem) super.clone();
		} catch(Exception e){
			LOGGER.log(Level.SEVERE, "QuoteItem clone() failed.", e);
		}
		return null;
	}
	

}