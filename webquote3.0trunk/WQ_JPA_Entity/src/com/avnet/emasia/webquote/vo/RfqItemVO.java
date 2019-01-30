package com.avnet.emasia.webquote.vo;


import java.io.Serializable;
import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.MessageFormat;
import java.text.ParseException;
import com.avnet.emasia.webquote.utilities.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.Transient;
import javax.persistence.TypedQuery;

import org.apache.commons.lang.StringUtils;

import com.avnet.emasia.webquote.constants.Constants;
import com.avnet.emasia.webquote.ejb.interceptor.ThreadLocalEntityManager;
import com.avnet.emasia.webquote.entity.Attachment;
import com.avnet.emasia.webquote.entity.BizUnit;
import com.avnet.emasia.webquote.entity.Currency;
import com.avnet.emasia.webquote.entity.Customer;
import com.avnet.emasia.webquote.entity.ExchangeRate;
import com.avnet.emasia.webquote.entity.Manufacturer;
import com.avnet.emasia.webquote.entity.ManufacturerMapping;
import com.avnet.emasia.webquote.entity.Material;
import com.avnet.emasia.webquote.entity.Money;
import com.avnet.emasia.webquote.entity.Pricer;
import com.avnet.emasia.webquote.entity.ProductGroup;
import com.avnet.emasia.webquote.entity.ProgramPricer;
import com.avnet.emasia.webquote.entity.QuantityBreakPrice;
import com.avnet.emasia.webquote.entity.QuoteItem;
import com.avnet.emasia.webquote.entity.SalesCostType;
import com.avnet.emasia.webquote.entity.SapMaterial;
import com.avnet.emasia.webquote.entity.util.AppDateUtil;
import com.avnet.emasia.webquote.quote.ejb.constant.QuoteSBConstant;

public class RfqItemVO implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Logger logger = Logger.getLogger(RfqItemVO.class.getName());;

	private long id;
	
	private int itemNumber;
	private boolean pricer;
	private Double cost;
	private String costIndicator;
	private boolean drmsLinkage;
	private boolean newPartNumber;
	private String quoteNumber;
	private String quotedPartNumber;
	private String partMask;
	private Long projectId;
	private List<DrProjectVO> drProjects;
	private String status;
	private String stage;
	private String quotedPrice;
	private long quoteId;
	private Integer mov;
	private Material material;
	private Boolean multiUsage;
	private Double minSellPrice;
	private Double bottomPrice;
	private String priceListRemarks1;
	private String priceListRemarks2;
	private String priceListRemarks3;
	private String priceListRemarks4;
	private String description;
	private String qcComment;
	
	//mfrdetail
	private Integer reschedulingWindow;
	private Integer cancellationWindow;
	private String resaleIndicator;
	private String qtyIndicator;
	private String productCategory;
	private String productProduct;
	
	//in the rfq item table
	private boolean specialPriceIndicator = false;
	private String mfr;
	private String requiredPartNumber;
	private String sapPartNumber;
	private boolean validPartNumber;
	private Integer mpq;	
	private Integer moq;	
	private String leadTime;
	private String requiredQty;
	private String eau;	
	private String targetResale;
	private String soldToCustomerNumber; //header duplication
	private String soldToCustomerName; //header duplication
	private String customerType; //header duplication
	private String shipToCustomerNumber; //header duplication
	private String shipToCustomerName; //header duplication
	private String endCustomerName; //header duplication
	private String endCustomerNumber; //header duplication
	private String enquirySegment; //header duplication
	private String projectName; //header duplication
	private String application; //header duplication
	private String designLocation; //header duplication
	private String designRegion; //header duplication,note added by Damon
	//WebQuote 2.2 enhancement :  fields changes.
//	private boolean avnetRegionalDemandCreation = false;
	private Long drmsNumber; // = projectId
	private String competitorInformation;
	private String supplierDrNumber;
	private String mpSchedule; //header duplication
	private String ppSchedule; //header duplication
	//WebQuote 2.2 enhancement :  fields changes. 
//	private boolean orderOnHand; //header duplication
	private boolean loa; //header duplication
	private String itemRemarks;
	private List<Attachment> attachments;
	private String priceValidity;
	private Date shipmentValidity;
	private String key4SapPartNumberSelectedMap;
	
	private boolean salesCostFlag;
	private SalesCostType salesCostType;
	private BigDecimal salesCost;
	private BigDecimal suggestedResale;
	
	
	private ProductGroup productGroup2;
	private ProductGroup productGroup1;	
	private String productGroup3;
	private String productGroup4;
	private String pmoq;
	private Date poExpiryDate;
	private Date quoteExpiryDate;
	private String programType;
	private Integer quotedQty;
	private boolean aqFlag;
	private long pricerId;
	private Material quotedMaterial;
	
	private List<QuantityBreakPrice> orderQties;
	
	private List<QuantityBreakPrice> DisplayOrderQties;
	
	
	private String dupQuoteItemPoExpiryDateStr;
	
	private String vendorQuoteNumber;
	private Integer vendorQuoteQty;
	
	//For Normal Pricer and Contract Pricer, qtyIndicator is from mfrDetail;
	//For Program Pricer and Sales Cost Pricer, qtyIndicator is from pricer;
	private boolean qtyIndicatorFromMfrDetail = true;
	
	
	
	//END OF in the rfq item table
	
	//Added By Tonmy on 5 Aug 2013:
	//For issue 749
	private String firstPageKey;
	/*
	 * P: program
	 * N: normal
	 * C: contract
	 */
	private String costIndicatorType;
	/*
	 * 1:green 
	 * 2:yellow
	 * 3:red
	 * 0:nothing
	 */
	private int statusColor = 0 ; 

	
	private boolean validated = false;
	//Added by Tonmy on 20 Aug 2013. for issue 796
	private int version;
	
	private String termsAndCondition;
	@Transient
	private boolean hasAttach;
	
	
	//The chagne for DRMS margin retention project
	private Integer drNedaLineNumber;	
	
	private Integer drNedaId;		
	
	
	
	//WebQuote 2.2 enhancement :  fields changes. 
	private String quoteType;
	private String designInCat;
	//WebQuote 2.2 enhancement : new field : quotation effective date
	private Date quotaionEffectiveDate;
	//BMT require
	private boolean bmtCheckedFlag =false;
	
    //for Resubmit Invalid RFQ 
	private String reSubmitType;
	
	private RfqHeaderVO rfqHeaderVO;
	
	//this field is from program pricer ,added by Damon
	private Boolean allocationFlag;
	
	
	//need this field , should be QuoteSBConstant.MATERIAL_TYPE_PROGRAM or  MATERIAL_TYPE_NORMAL,added by Damon
	private String materialTypeId;
	
	private ProgramPricer requestedProgramMaterialForProgram;
	
	private Customer soldToCustomer;
	
	private Customer endCustomer;
	
//	private Date poExpiryDate;
	
	private String messages;
	
	private String  matchedQuoteNumber;
	
	//add  buyCurr , rfqCurr for MultiCurrency Project by Damonchen@20180814
	private String buyCurr;
	
	private String rfqCurr;
	
	private boolean dLinkFlag;
	
	private BigDecimal exRateRfq;
	
	private BigDecimal exRateBuy;
	
	private BigDecimal handling;
	
	private BigDecimal vat;
	
	
	public boolean isdLinkFlag() {
		return dLinkFlag;
	}
	public void setdLinkFlag(boolean dLinkFlag) {
		this.dLinkFlag = dLinkFlag;
	}
	public String getBuyCurr() {
		return buyCurr;
	}
	public void setBuyCurr(String buyCurr) {
		this.buyCurr = buyCurr;
	}
	public String getRfqCurr() {
		return rfqCurr;
	}
	public void setRfqCurr(String rfqCurr) {
		this.rfqCurr = rfqCurr;
	}
	
	
	public BigDecimal getExRateRfq() {
		return exRateRfq;
	}
	public void setExRateRfq(BigDecimal exRateRfq) {
		this.exRateRfq = exRateRfq;
	}
	public BigDecimal getExRateBuy() {
		return exRateBuy;
	}
	public void setExRateBuy(BigDecimal exRateBuy) {
		this.exRateBuy = exRateBuy;
	}
	public String getQuoteType()
	{
		return quoteType;
	}
	public void setQuoteType(String quoteType)
	{
		//Fixed defect 420
		if(!StringUtils.isEmpty(quoteType))
    	{
			quoteType=quoteType.trim();
    		this.quoteType = quoteType;
    	}
		
		//this.quoteType = quoteType;
	}
	public String getDesignInCat()
	{
		return designInCat;
	}
	public void setDesignInCat(String designInCat)
	{
		//Fixed defect 420
		if(!StringUtils.isEmpty(designInCat))
    	{
			designInCat=designInCat.trim();
    		this.designInCat = designInCat;
    	}
		//this.designInCat = designInCat;
	}
	public String getTermsAndCondition() {
		return termsAndCondition;
	}
	public void setTermsAndCondition(String termsAndCondition) {
		this.termsAndCondition = termsAndCondition;
	}
	public boolean isSpecialPriceIndicator() {
		return specialPriceIndicator;
	}
	public void setSpecialPriceIndicator(boolean specialPriceIndicator) {
		this.specialPriceIndicator = specialPriceIndicator;
	}
	public String getMfr() {
		//Added by Tonmy at 16 Sep 2013 . for issue 991
    	if(mfr!=null)
    		mfr=mfr.trim();
		return mfr;
	}
	public void setMfr(String mfr) {
		//Added by Tonmy at 16 Sep 2013 . for issue 991
    	if(!StringUtils.isEmpty(mfr))
    	{
    		mfr=mfr.trim();
    		this.mfr = mfr;
    	}
//    	else
//    	{
//    		logger.info("set MFR as empty | invoked by "+QuoteUtil.getCallingMethodName());
//    	}
	}
	public String getRequiredPartNumber() {
		//Added by Tonmy at 16 Sep 2013 . for issue 991
    	if(requiredPartNumber!=null)
    	{
    		requiredPartNumber=requiredPartNumber.trim();
    		requiredPartNumber=requiredPartNumber.toUpperCase();
    	}
    		
		return requiredPartNumber;
	}
	public void setRequiredPartNumber(String requiredPartNumber) {
		//Added by Tonmy at 16 Sep 2013 . for issue 991
    	if(requiredPartNumber!=null)
    	{
    		requiredPartNumber=requiredPartNumber.trim();
    		requiredPartNumber=requiredPartNumber.toUpperCase();
    	}
		this.requiredPartNumber = requiredPartNumber;
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
	public String getLeadTime() {
		
		//Added by Tonmy at 16 Sep 2013 . for issue 991
    	if(leadTime!=null)
    		leadTime=leadTime.trim();
    	
		return leadTime;
	}
	public void setLeadTime(String leadTime) {
		
		//Added by Tonmy at 16 Sep 2013 . for issue 991
    	if(leadTime!=null)
    		leadTime=leadTime.trim();
    	
		this.leadTime = leadTime;
	}
	public String getRequiredQty() {
		return requiredQty;
	}
	public void setRequiredQty(String requiredQty) {
		this.requiredQty = requiredQty;
	}
	public String getEau() {
		return eau;
	}
	public void setEau(String eau) {
		if(isInteger(eau))
			this.eau = eau;
		else
			this.eau = null;
	}
	public String getTargetResale() {
		return targetResale;
	}
	public void setTargetResale(String targetResale) {
		this.targetResale = targetResale;
	}
	//WebQuote 2.2 enhancement :  fields changes.
//	public boolean isAvnetRegionalDemandCreation() {
//		return avnetRegionalDemandCreation;
//	}
//	public void setAvnetRegionalDemandCreation(boolean avnetRegionalDemandCreation) {
//		this.avnetRegionalDemandCreation = avnetRegionalDemandCreation;
//	}
	public Long getDrmsNumber() {
		return drmsNumber;
	}
	public void setDrmsNumber(Long drmsNumber) {
		this.drmsNumber = drmsNumber;
	}
	public String getCompetitorInformation() {
		return competitorInformation;
	}
	public void setCompetitorInformation(String competitorInformation) {
		this.competitorInformation = competitorInformation;
	}
	public String getSupplierDrNumber() {
		return supplierDrNumber;
	}
	public void setSupplierDrNumber(String supplierDrNumber) {
		this.supplierDrNumber = supplierDrNumber;
	}
	public String getItemRemarks() {
		return itemRemarks;
	}
	public void setItemRemarks(String itemRemarks) {
		this.itemRemarks = itemRemarks;
	}
	public int getItemNumber() {
		return itemNumber;
	}
	public void setItemNumber(int itemNumber) {
		this.itemNumber = itemNumber;
	}
	public boolean isValidPartNumber() {
		return validPartNumber;
	}
	public void setValidPartNumber(boolean validPartNumber) {
		this.validPartNumber = validPartNumber;
	}
	public Double getCost() {
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
	public boolean isDrmsLinkage() {
		return drmsLinkage;
	}
	public void setDrmsLinkage(boolean drmsLinkage) {
		this.drmsLinkage = drmsLinkage;
	}
	public boolean isNewPartNumber() {
		return newPartNumber;
	}
	public void setNewPartNumber(boolean newPartNumber) {
		this.newPartNumber = newPartNumber;
	}
	public String getQuoteNumber() {
		return quoteNumber;
	}
	public void setQuoteNumber(String quoteNumber) {
		this.quoteNumber = quoteNumber;
	}
	public String getQuotedPartNumber() {
		return quotedPartNumber;
	}
	public void setQuotedPartNumber(String quotedPartNumber) {
		this.quotedPartNumber = quotedPartNumber;
	}
	public String getPartMask() {
		return partMask;
	}
	public void setPartMask(String partMask) {
		this.partMask = partMask;
	}
	public Long getProjectId() {
		return projectId;
	}
	public void setProjectId(Long projectId) {
		this.projectId = projectId;
	}
	public List<DrProjectVO> getDrProjects() {
		return drProjects;
	}
	public void setDrProjects(List<DrProjectVO> drProjects) {
		this.drProjects = drProjects;
	}
	public Integer getReschedulingWindow() {
		return reschedulingWindow;
	}
	public void setReschedulingWindow(Integer reschedulingWindow) {
		this.reschedulingWindow = reschedulingWindow;
	}
	public Integer getCancellationWindow() {
		return cancellationWindow;
	}
	public void setCancellationWindow(Integer cancellationWindow) {
		this.cancellationWindow = cancellationWindow;
	}
	public String getResaleIndicator() {
		return resaleIndicator;
	}
	public void setResaleIndicator(String resaleIndicator) {
		this.resaleIndicator = resaleIndicator;
	}
	public String getQtyIndicator() {
		return qtyIndicator;
	}
	public void setQtyIndicator(String qtyIndicator) {
		this.qtyIndicator = qtyIndicator;
	}
	public String getProductCategory() {
		return productCategory;
	}
	public void setProductCategory(String productCategory) {
		this.productCategory = productCategory;
	}
	public String getProductProduct() {
		return productProduct;
	}
	public void setProductProduct(String productProduct) {
		this.productProduct = productProduct;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getQuotedPrice() {
		return quotedPrice;
	}
	public void setQuotedPrice(String quotedPrice) {
		this.quotedPrice = quotedPrice;
	}
	public List<Attachment> getAttachments() {
		return attachments;
	}
	public void setAttachments(List<Attachment> attachments) {
		this.attachments = attachments;
	}
	public String getStage() {
		return stage;
	}
	public void setStage(String stage) {
		this.stage = stage;
	}
	public Integer getMov() {
		return mov;
	}
	public void setMov(Integer mov) {
		this.mov = mov;
	}
	public Material getMaterial() {
		return material;
	}
	public void setMaterial(Material material) {
		this.material = material;
	}
	public Boolean getMultiUsage() {
		return multiUsage;
	}
	public void setMultiUsage(Boolean multiUsage) {
		this.multiUsage = multiUsage;
	}
	public boolean isPricer() {
		return pricer;
	}
	public void setPricer(boolean pricer) {
		this.pricer = pricer;
	}
	public long getQuoteId() {
		return quoteId;
	}
	public void setQuoteId(long quoteId) {
		this.quoteId = quoteId;
	}
	public Double getMinSellPrice() {
		return minSellPrice;
	}
	public void setMinSellPrice(Double minSellPrice) {
		this.minSellPrice = minSellPrice;
	}
	public Double getBottomPrice() {
		return bottomPrice;
	}
	public void setBottomPrice(Double minSell) {
		this.bottomPrice = minSell;
	}
	public String getSoldToCustomerNumber() {
		return soldToCustomerNumber;
	}
	public void setSoldToCustomerNumber(String soldToCustomerNumber) {
		//Added by Tonmy at 16 Sep 2013 . for issue 991
    	if(soldToCustomerNumber!=null)
    		soldToCustomerNumber=soldToCustomerNumber.trim();
    	
		this.soldToCustomerNumber = soldToCustomerNumber;
	}
	public String getSoldToCustomerName() {
		//Added by Tonmy at 16 Sep 2013 . for issue 991
    	if(soldToCustomerNumber!=null)
    		soldToCustomerNumber=soldToCustomerNumber.trim();
    	
		return soldToCustomerName;
	}
	public void setSoldToCustomerName(String soldToCustomerName) {
		//Added by Tonmy at 16 Sep 2013 . for issue 991
    	if(shipToCustomerNumber!=null)
    		shipToCustomerNumber=shipToCustomerNumber.trim();
    	
		this.soldToCustomerName = soldToCustomerName;
	}
	public String getShipToCustomerNumber() {
		//Added by Tonmy at 16 Sep 2013 . for issue 991
    	if(shipToCustomerNumber!=null)
    		shipToCustomerNumber=shipToCustomerNumber.trim();
    	
		return shipToCustomerNumber;
	}
	public void setShipToCustomerNumber(String shipToCustomerNumber) {
		//Added by Tonmy at 16 Sep 2013 . for issue 991
    	if(shipToCustomerNumber!=null)
    		shipToCustomerNumber=shipToCustomerNumber.trim();
    	
		this.shipToCustomerNumber = shipToCustomerNumber;
	}
	public String getShipToCustomerName() {
		//Added by Tonmy at 16 Sep 2013 . for issue 991
    	if(shipToCustomerName!=null)
    		shipToCustomerName=shipToCustomerName.trim();
    	
		return shipToCustomerName;
	}
	public void setShipToCustomerName(String shipToCustomerName) {
		//Added by Tonmy at 16 Sep 2013 . for issue 991
    	if(shipToCustomerName!=null)
    		shipToCustomerName=shipToCustomerName.trim();
    	
		this.shipToCustomerName = shipToCustomerName;
	}
	public String getEndCustomerNumber() {
		//Added by Tonmy at 16 Sep 2013 . for issue 991
    	if(endCustomerNumber!=null)
    		endCustomerNumber=endCustomerNumber.trim();
    	
		return endCustomerNumber;
	}
	public void setEndCustomerNumber(String endCustomerNumber) {
		//Added by Tonmy at 16 Sep 2013 . for issue 991
    	if(endCustomerNumber!=null)
    		endCustomerNumber=endCustomerNumber.trim();
    	
		this.endCustomerNumber = endCustomerNumber;
	}
	public String getEndCustomerName() {
		//Added by Tonmy at 16 Sep 2013 . for issue 991
    	if(endCustomerName!=null)
    		endCustomerName=endCustomerName.trim();
    	
		return endCustomerName;
	}
	public void setEndCustomerName(String endCustomerName) {
		//Added by Tonmy at 16 Sep 2013 . for issue 991
    	if(endCustomerName!=null)
    		endCustomerName=endCustomerName.trim();
		
		this.endCustomerName = endCustomerName;
	}
	public String getCustomerType() {
		return customerType;
	}
	public void setCustomerType(String customerType) {
		//this.customerType = customerType;
		//fixed the customer type become empty issue .
    	if(StringUtils.isNotBlank(customerType))
    	{
    		customerType=customerType.trim();
    		this.customerType = customerType;
    	}
	}
	public String getEnquirySegment() {
		return enquirySegment;
	}
	public void setEnquirySegment(String enquirySegment) {
		this.enquirySegment = enquirySegment;
	}
	public String getProjectName() {
		//Added by Tonmy at 16 Sep 2013 . for issue 991
    	if(projectName!=null)
    		projectName=projectName.trim();
    	
		return projectName;
	}
	public void setProjectName(String projectName) {
		//Added by Tonmy at 16 Sep 2013 . for issue 991
    	if(projectName!=null)
    		projectName=projectName.trim();
		
		this.projectName = projectName;
	}
	public String getApplication() {
		//Added by Tonmy at 16 Sep 2013 . for issue 991
    	if(application!=null)
    		application=application.trim();
    	
		return application;
	}
	public void setApplication(String application) {
		//Added by Tonmy at 16 Sep 2013 . for issue 991
    	if(application!=null)
    		application=application.trim();
    	
		this.application = application;
	}
	public String getDesignLocation() {
		//Added by Tonmy at 16 Sep 2013 . for issue 991
    	if(designLocation!=null)
    		designLocation=designLocation.trim();
    	
		return designLocation;
	}
	public void setDesignLocation(String designLocation) {
		//Added by Tonmy at 16 Sep 2013 . for issue 991
    	if(designLocation!=null)
    		designLocation=designLocation.trim();
    	
		this.designLocation = designLocation;
	}
	public void setDesignRegion(String designRegion) {
    	if(designRegion!=null)
    		designRegion=designRegion.trim();
    	
		this.designRegion = designRegion;
	}
	public String getDesignRegion() {
    	if(designRegion!=null)
    		designRegion=designRegion.trim();
    	
		return designRegion;
	}
	public String getMpSchedule() {
		return mpSchedule;
	}
	public void setMpSchedule(String mpSchedule) {
		this.mpSchedule = mpSchedule;
	}
	public String getPpSchedule() {
		return ppSchedule;
	}
	public void setPpSchedule(String ppSchedule) {
		this.ppSchedule = ppSchedule;
	}
//	public boolean isOrderOnHand() {
//		return orderOnHand;
//	}
//	public void setOrderOnHand(boolean orderOnHand) {
//		this.orderOnHand = orderOnHand;
//	}
	public boolean isLoa() {
		return loa;
	}
	public void setLoa(boolean loa) {
		this.loa = loa;
	}
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public String getPriceValidity() {
		return priceValidity;
	}
	public void setPriceValidity(String priceValidity) {
		this.priceValidity = priceValidity;
	}
	public Date getShipmentValidity() {
		return shipmentValidity;
	}
	public void setShipmentValidity(Date shipmentValidity) {
		this.shipmentValidity = shipmentValidity;
	}
	public boolean isValidated() {
		return validated;
	}
	public void setValidated(boolean validated) {
		this.validated = validated;
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
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public int getStatusColor() {
		return statusColor;
	}
	public void setStatusColor(int statusColor) {
		this.statusColor = statusColor;
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
	
	public ProductGroup getProductGroup2() {
		return productGroup2;
	}
	public void setProductGroup2(ProductGroup productGroup2) {
		this.productGroup2 = productGroup2;
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
	
	
	//Added by Tonmy on 20 Aug 2013. for issue 796
	public int getVersion() {
		return version;
	}

	public void setVersion(int version) {
		this.version = version;
	}
	
	public String getFirstPageKey() {
		return firstPageKey;
	}
	public void setFirstPageKey(String firstPageKey) {
		this.firstPageKey = firstPageKey;
	}
	
	
	public boolean isBmtCheckedFlag() {
		return bmtCheckedFlag;
	}
	public void setBmtCheckedFlag(boolean bmtCheckedFlag) {
		this.bmtCheckedFlag = bmtCheckedFlag;
	}
	
	
	public String getMaterialTypeId() {
		return materialTypeId;
	}
	public void setMaterialTypeId(String materialTypeId) {
		this.materialTypeId = materialTypeId;
	}

	public String getPmoq() {
		return pmoq;
	}
	public void setPmoq(String pmoq) {
		this.pmoq = pmoq;
	}
	
	public Date getQuoteExpiryDate() {
		return quoteExpiryDate;
	}
	public void setQuoteExpiryDate(Date quoteExpiryDate) {
		this.quoteExpiryDate = quoteExpiryDate;
	}
	public String getProgramType() {
		return programType;
	}
	public void setProgramType(String programType) {
		this.programType = programType;
	}
	
	public Integer getQuotedQty() {
		return quotedQty;
	}
	public void setQuotedQty(Integer quotedQty) {
		this.quotedQty = quotedQty;
	}
	public Boolean getAllocationFlag() {
		return this.allocationFlag;
	}
	public void setAllocationFlag(Boolean allocationFlag) {
		this.allocationFlag = allocationFlag;
	}
	

	public boolean isAqFlag() {
		return aqFlag;
	}
	public void setAqFlag(boolean aqFlag) {
		this.aqFlag = aqFlag;
	}
	public long getPricerId() {
		return pricerId;
	}
	public void setPricerId(long pricerId) {
		this.pricerId = pricerId;
	}
	
	public Material getQuotedMaterial() {
		return quotedMaterial;
	}
	public void setQuotedMaterial(Material quotedMaterial) {
		this.quotedMaterial = quotedMaterial;
	}

	public List<QuantityBreakPrice> getOrderQties() {
		return orderQties;
	}
	public void setOrderQties(List<QuantityBreakPrice> orderQties) {
		this.orderQties = orderQties;
	}
	
	public List<QuantityBreakPrice> getDisplayOrderQties() {
		return DisplayOrderQties;
	}
	public void setDisplayOrderQties(List<QuantityBreakPrice> displayOrderQties) {
		DisplayOrderQties = displayOrderQties;
	}
	public String getVendorQuoteNumber() {
		return vendorQuoteNumber;
	}
	public void setVendorQuoteNumber(String vendorQuoteNumber) {
		this.vendorQuoteNumber = vendorQuoteNumber;
	}
	public Integer getVendorQuoteQty() {
		return vendorQuoteQty;
	}
	public void setVendorQuoteQty(Integer vendorQuoteQty) {
		this.vendorQuoteQty = vendorQuoteQty;
	}
	public RfqItemVO copy()	{
		RfqItemVO newVo = new RfqItemVO(); 
        newVo.setId(this.id);
		newVo.setSpecialPriceIndicator(this.specialPriceIndicator);
		newVo.setMfr(this.mfr);
		newVo.setRequiredPartNumber(this.requiredPartNumber);
		newVo.setMpq(this.mpq);
		newVo.setMoq(this.moq);
		newVo.setLeadTime(this.leadTime);
		newVo.setRequiredQty(this.requiredQty);
		newVo.setEau(this.eau);
		newVo.getTargetResale();
		newVo.setTargetResale(this.targetResale);
		//WebQuote 2.2 enhancement :  fields changes.
//		newVo.setAvnetRegionalDemandCreation(this.avnetRegionalDemandCreation);
		newVo.setDrmsNumber(this.drmsNumber);
		newVo.setCompetitorInformation(this.competitorInformation);
		newVo.setSupplierDrNumber(this.supplierDrNumber);
		newVo.setItemRemarks(this.itemRemarks);
		newVo.setItemNumber(this.itemNumber);
		newVo.setValidPartNumber(this.validPartNumber);
		newVo.setCost(this.cost);
		newVo.setCostIndicator(this.costIndicator);
		newVo.setDrmsLinkage(this.drmsLinkage);
		newVo.setNewPartNumber(this.newPartNumber);
		newVo.setQuoteNumber(this.quoteNumber);
		newVo.setQuotedPartNumber(this.quotedPartNumber);
		newVo.setPartMask(this.partMask);
		newVo.setProjectId(this.projectId);
		newVo.setDrProjects(this.drProjects);
		newVo.setReschedulingWindow(this.reschedulingWindow);
		newVo.setCancellationWindow(this.cancellationWindow);
		newVo.setResaleIndicator(this.resaleIndicator);
		newVo.setQtyIndicator(this.qtyIndicator);
		newVo.setProductCategory(this.productCategory);
		newVo.setProductProduct(this.productProduct);
		newVo.setStatus(this.status);
		newVo.setQuotedPrice(this.quotedPrice);
		newVo.setAttachments(this.attachments);
		newVo.setStage(this.stage);
		newVo.setMov(this.mov);
		newVo.setMaterial(this.material);
		newVo.setMultiUsage(this.multiUsage);
		newVo.setPricer(this.pricer);
		newVo.setQuoteId(this.quoteId);
		newVo.setMinSellPrice(this.minSellPrice);
		newVo.setBottomPrice(this.bottomPrice);
		newVo.setSoldToCustomerNumber(this.soldToCustomerNumber);
		newVo.setSoldToCustomerName(this.soldToCustomerName);
		newVo.setShipToCustomerNumber(this.shipToCustomerNumber);
		newVo.setShipToCustomerName(this.shipToCustomerName);
		newVo.setEndCustomerNumber(this.endCustomerNumber);
		newVo.setEndCustomerName(this.endCustomerName);
		newVo.setCustomerType(this.customerType);
		newVo.setEnquirySegment(this.enquirySegment);
		newVo.setProjectName(this.projectName);
		newVo.setApplication(this.application);
		newVo.setDesignLocation(this.designLocation);
		newVo.setDesignRegion(designRegion);
		newVo.setMpSchedule(this.mpSchedule);
		newVo.setPpSchedule(this.ppSchedule);
//		newVo.setOrderOnHand(this.orderOnHand);
		newVo.setLoa(this.loa);
		newVo.setId(this.id);
		newVo.setPriceValidity(this.priceValidity);
		newVo.setShipmentValidity(this.shipmentValidity);
		newVo.setValidated(this.validated);
		newVo.setPriceListRemarks1(this.priceListRemarks1);
		newVo.setPriceListRemarks2(this.priceListRemarks2);
		newVo.setPriceListRemarks3(this.priceListRemarks3);
		newVo.setPriceListRemarks4(this.priceListRemarks4);
		newVo.setDescription(this.description);
		newVo.setStatusColor(this.statusColor);
	    newVo.setFirstPageKey(this.firstPageKey);
	    newVo.setCostIndicatorType(this.costIndicatorType);
	    newVo.setDrNedaLineNumber(this.drNedaLineNumber);
	    newVo.setDrNedaId(this.drNedaId);
	    newVo.setQcComment(this.qcComment);
	    newVo.setBmtCheckedFlag(this.bmtCheckedFlag);
	    newVo.setAqFlag(this.aqFlag);
	    newVo.setdLinkFlag(this.dLinkFlag);
		return newVo;
	}
	
	
	public boolean isHasAttach()
	{
		if(this.getAttachments()!=null && this.getAttachments().size()>0)
		{
			return true;
		}
		return false;
	}
	public void setHasAttach(boolean hasAttach)
	{
		this.hasAttach = hasAttach;
	}
	
	public String getCostIndicatorType() {
		return costIndicatorType;
	}
	public void setCostIndicatorType(String costIndicatorType) {
		this.costIndicatorType = costIndicatorType;
	}

	public Integer getDrNedaLineNumber()
	{
		return drNedaLineNumber;
	}
	public void setDrNedaLineNumber(Integer drNedaLineNumber)
	{
		this.drNedaLineNumber = drNedaLineNumber;
	}
	public Integer getDrNedaId()
	{
		return drNedaId;
	}
	public void setDrNedaId(Integer drNedaId)
	{
		this.drNedaId = drNedaId;
	}
	public Date getQuotaionEffectiveDate()
	{
		return quotaionEffectiveDate;
	}
	public void setQuotaionEffectiveDate(Date quotaionEffectiveDate)
	{
		this.quotaionEffectiveDate = quotaionEffectiveDate;
	}
	
	public String getSapPartNumber() {
		return sapPartNumber;
	}
	public void setSapPartNumber(String sapPartNumber) {
		this.sapPartNumber = sapPartNumber;
	}
	public String getKey4SapPartNumberSelectedMap() {
		return key4SapPartNumberSelectedMap;
	}
	public void setKey4SapPartNumberSelectedMap(String key4SapPartNumberSelectedMap) {
		this.key4SapPartNumberSelectedMap = key4SapPartNumberSelectedMap;
	}
	public String getQcComment() {
		return qcComment;
	}
	public void setQcComment(String qcComment) {
		this.qcComment = qcComment;
	}
	public String getReSubmitType() {
		return reSubmitType;
	}
	public void setReSubmitType(String reSubmitType) {
		this.reSubmitType = reSubmitType;
	}
	
	public RfqHeaderVO getRfqHeaderVO() {
		return rfqHeaderVO;
	}
	public void setRfqHeaderVO(RfqHeaderVO rfqHeaderVO) {
		this.rfqHeaderVO = rfqHeaderVO;
	}
	private  boolean isInteger(String value) {
		return StringUtils.isNotBlank(value) && value.matches("[1-9]{1}[0-9]{0,9}");
	}
	
	public Customer getSoldToCustomer() {
		return soldToCustomer;
	}
	public void setSoldToCustomer(Customer soldToCustomer) {
		this.soldToCustomer = soldToCustomer;
	}
	public Customer getEndCustomer() {
		return endCustomer;
	}
	public void setEndCustomer(Customer endCustomer) {
		this.endCustomer = endCustomer;
	}
	
	
	public ProgramPricer getRequestedProgramMaterialForProgram() {
		return requestedProgramMaterialForProgram;
	}
	public void setRequestedProgramMaterialForProgram(ProgramPricer requestedProgramMaterialForProgram) {
		this.requestedProgramMaterialForProgram = requestedProgramMaterialForProgram;
	}
	
	public boolean isQtyIndicatorFromMfrDetail() {
		return qtyIndicatorFromMfrDetail;
	}
	public void setQtyIndicatorFromMfrDetail(boolean qtyIndicatorFromMfrDetail) {
		this.qtyIndicatorFromMfrDetail = qtyIndicatorFromMfrDetail;
	}

	public Integer getRequiredQtyAsInt() {
		if (requiredQty == null) {
			return null;
		} else {
			try {
				return Integer.parseInt(requiredQty);
			} catch (Exception e) {
				return null;
			}
		}
		
	}
	
	
	
	public String getDupQuoteItemPoExpiryDateStr() {
		return dupQuoteItemPoExpiryDateStr;
	}
	public void setDupQuoteItemPoExpiryDateStr(String dupQuoteItemPoExpiryDateStr) {
		this.dupQuoteItemPoExpiryDateStr = dupQuoteItemPoExpiryDateStr;
	}
	public Date getPoExpiryDate() {
		return poExpiryDate;
	}
	public void setPoExpiryDate(Date poExpiryDate) {
		this.poExpiryDate = poExpiryDate;
	}
	public String getMessages() {
		return messages;
	}
	public void setMessages(String messages) {
		this.messages = messages;
	}
	
	
	
	
	public String getMatchedQuoteNumber() {
		return matchedQuoteNumber;
	}
	public void setMatchedQuoteNumber(String matchedQuoteNumber) {
		this.matchedQuoteNumber = matchedQuoteNumber;
	}

	public boolean matchAQConditions() {
		
		if(mpq == null || mpq <= 0) {
			return false;
		}
			
		if((moq == null || moq <= 0) && (mov == null || mov <= 0)) {
			return false;
		}
		
		//only need check qtyIndicator = MOV, no need to check qtyIndicatgor = MOQ.
		if(qtyIndicator != null && qtyIndicator.equalsIgnoreCase("MOV") && (mov == null || mov <= 0)) {
			return false;
		}
			
		if(StringUtils.isEmpty(leadTime)) {
			return false;
		}
		
		if(StringUtils.isEmpty(costIndicator)) {
			return false;
		}
			
		if(this.getPriceValidityAsDate() == null || this.getPriceValidityAsDate().before(getCurrentTime())) {
			return false;
		}
			
		if(getShipmentValidity() != null && this.getShipmentValidity().before(getCurrentTime())) {
			return false;
		}
		
		if (this.aqFlag ==  false) {
			return false;
		}
		
		if (salesCostType ==  null) {
			return false;
		}
		
		if (cost == null || cost <= 0d ) {
			return false;
		}
		
		if ((salesCost == null && minSellPrice == null) || (salesCost != null && minSellPrice != null)) {
			return false;
		} else if (salesCost != null) {
			BigDecimal salesCostAsRfq = this.toRfqCurr(salesCost);
			if(salesCostAsRfq == null || salesCostAsRfq.compareTo(BigDecimal.ZERO) <= 0) {
				return false;
			}
			if ( ! StringUtils.isEmpty(targetResale) && Double.parseDouble(targetResale) < salesCostAsRfq.doubleValue() ) {
				return false;
			}
		} else if (minSellPrice != null) {
			if (minSellPrice.doubleValue() <= 0d) {
				return false;
			}
			BigDecimal minSellPriceAsRfq = this.toRfqCurr(minSellPrice);
			if (! StringUtils.isEmpty(targetResale) && minSellPriceAsRfq != null && Double.parseDouble(targetResale) < minSellPriceAsRfq.doubleValue() ) {
				return false;
			}
		} 
		
//		if (salesCostFlag && ! salesCostType.equals(SalesCostType.NonSC)) {
//			
//			/* this checking is no longer needed
//			if (suggestedResale == null || suggestedResale.doubleValue() <= 0 ) {
//				return false;
//			}
//			*/
//			
//			
//			/*if (salesCost == null || salesCost.compareTo(new BigDecimal(0)) <= 0) {
//				return false;
//			}*/
//			
//			BigDecimal salesCostAsRfq = this.toRfqCurr(salesCost);
//			if(salesCostAsRfq == null || salesCostAsRfq.compareTo(BigDecimal.ZERO) <= 0) {
//				return false;
//			}
//			if ( ! StringUtils.isEmpty(targetResale) && Double.parseDouble(targetResale) < salesCostAsRfq.doubleValue() ) {
//				return false;
//			}
//			
//		} else {
//			
//			if (cost == null || cost <= 0d || minSellPrice == null || minSellPrice.doubleValue() <= 0d) {
//				return false;
//			}
//			BigDecimal minSellAsRfq = this.toRfqCurr(minSellPrice);
//			if (! StringUtils.isEmpty(targetResale) && minSellAsRfq != null &&Double.parseDouble(targetResale) < minSellAsRfq.doubleValue() ) {
//				return false;
//			}
//
//		}
		
		return true;
	}
	
	public void calAfterFillin() {

		boolean isShiftFlag = false;
		if (specialPriceIndicator == false && matchAQConditions()) {
			//minSellPrice != null means the RfqItemVO is not filled by SalesCostPricer, 
			//and thus we need fill in the quotedPrice
			if (minSellPrice != null) {
				if (!StringUtils.isBlank(targetResale)) { 
					//TODO
					// this should be saved bigger scale for converting to RfqCurr in future.
					BigDecimal asBuy = this.toBuyCurr(new BigDecimal(targetResale),	Constants.SCALE_FOR_QUOTEDPRICE);
					if(asBuy != null) {
						quotedPrice = String.valueOf(asBuy);
					}
				} else {
					quotedPrice = String.valueOf(minSellPrice);
				}
			}
			
			if (quotaionEffectiveDate == null) {
				quotaionEffectiveDate = new Date();
			}
			description = null;
			priceListRemarks1 = null;
			priceListRemarks2 = null;
			priceListRemarks3 = null;
			priceListRemarks4 = null;
			if (isQtyIndicatorFromMfrDetail()) {
				qtyIndicator = null;
			}
			status = QuoteSBConstant.RFQ_STATUS_AQ;
			stage = QuoteSBConstant.QUOTE_STAGE_FINISH;
			isShiftFlag = isBmtQuote();
			logger.log(Level.INFO, "match AQ conditions.");
		} else {
			status = QuoteSBConstant.RFQ_STATUS_QC;
			stage = QuoteSBConstant.QUOTE_STAGE_PENDING;
			salesCost = null;
			suggestedResale = null;
			isShiftFlag = true;
			

		}
		if (isShiftFlag) {
			if (salesCostFlag) {
				//move the salesCostType to the back, such as SalesCostType.ZBMP.equals(salesCostType),by DamonChen@20180222
				if (SalesCostType.ZBMP.equals(salesCostType)) {
					salesCostType = SalesCostType.ZINC;
				} else if (SalesCostType.ZBMD.equals(salesCostType)) {
					salesCostType = SalesCostType.ZIND;
				}
			}
		}
		
		//calExRate();
		//calBuyCurrToRfqCurr();
	}
	
	public void calExRate() {
		try{
			ExchangeRate[] exRates = Money.getExchangeRate(buyCurr, rfqCurr, this.getRfqHeaderVO().getBizUnit(), this.getSoldToCustomer(), this.getExRateDate());
			 if(exRates !=null && exRates.length == 2) {
				 if(exRates[0] != null) this.exRateBuy = exRates[0].getExRateTo();
				 if(exRates[1] != null) {
					 this.exRateRfq = exRates[1].getExRateTo();
					 this.vat = exRates[1].getVat();
				 }
				 if(exRates[0] != null && exRates[1] != null)
				 {
					 this.handling =  exRates[0].getHandling().multiply(exRates[1].getHandling());
				 }
				 
			 }
		} catch(Exception e) {
			logger.severe(e.getMessage());
		}
	}
	
	public Date getExRateDate() {
		return new Date();
	}
	
	
	private BigDecimal toRfqCurr(Double value) {
		if(value == null) return null;
		return toRfqCurr(BigDecimal.valueOf(value));
	}
	private BigDecimal toBuyCurr(Double value, int... scale) {
		if(value == null) return null;
		return toBuyCurr(BigDecimal.valueOf(value), scale);
	}
	
	
	
	private BigDecimal toRfqCurr(BigDecimal value) {
		/********/
		try {
			if(value == null || buyCurr==null ||rfqCurr==null) return null;
			Money money = Money.of(value, Currency.valueOf(buyCurr)).convertToRfq(Currency.valueOf(rfqCurr),
					new BizUnit(this.getRfqHeaderVO().getBizUnit()), this.getSoldToCustomer(), this.getExRateDate());
			if(money != null) {
				 return money.getAmount();
				//logger.info("for Pricer get converted minsell Money::" + money.toString());
			}
		} catch(Exception e) {
			logger.warning(MessageFormat.format("RfqItemVO ,"
					+ " id:[{0}] can not convert to RfqCurr:[{1}] Money ." + e.getMessage(), this.getId(), this.getRfqCurr()));
		}
		return null;
	}
	
	private BigDecimal toBuyCurr(BigDecimal value, int... scale) {
		/********/
		try {
			if(value == null || buyCurr==null ||rfqCurr==null) return null;
			Money money = Money.of(value, Currency.valueOf(rfqCurr)).convertToBuy(Currency.valueOf(buyCurr),
					new BizUnit(this.getRfqHeaderVO().getBizUnit()), this.getSoldToCustomer(), this.getExRateDate(), scale);
			if(money != null) {
				 return money.getAmount();
				//logger.info("for Pricer get converted minsell Money::" + money.toString());
			}
		} catch(Exception e) {
			logger.warning(MessageFormat.format("RfqItemVO ,"
					+ " id:[{0}] can not convert to toBuyCurr:[{1}] Money ." + e.getMessage(), this.getId(), this.getRfqCurr()));
		}
		return null;
	}
	
	
	/*private void calBuyCurrToRfqCurr() {
		this.salesCost = toRfqCurr(salesCost);
		this.suggestedResale = toRfqCurr(suggestedResale);
	}
	*/
	//*======some static methods will been moved to a util class by damon,begin======*//
	public static Date getCurrentDateZeroHour(){
		Calendar cal = Calendar.getInstance();
		cal.setTimeZone(TimeZone.getTimeZone("GMT+8"));
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);


		return cal.getTime();
	}
	
	public static Date getCurrentTime() {
		return new Date();
	}	
	

	public BigDecimal getVat() {
		return vat;
	}
	public void setVat(BigDecimal vat) {
		this.vat = vat;
	}
	public static Date shitDateByProgramType(Date date, String materialType){
		Calendar cal = Calendar.getInstance();
		cal.setTimeZone(TimeZone.getTimeZone("GMT+8"));
		cal.setTime(date);
		if (materialType.equals(QuoteSBConstant.MATERIAL_TYPE_NORMAL))
			cal.add(Calendar.DATE, QuoteSBConstant.QUOTE_EXPIRY_DATE_SHIFT_NORMAL);
		else if (materialType.equals(QuoteSBConstant.MATERIAL_TYPE_PROGRAM))
			cal.add(Calendar.DATE, QuoteSBConstant.QUOTE_EXPIRY_DATE_SHIFT_PROGRAM);
		// cal.set(Calendar.HOUR_OF_DAY, 15);
		return cal.getTime();
	}

	public static Date shiftDate(Date date, int shift){
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		cal.add(Calendar.DATE, shift);
		return cal.getTime();
	}

	
	//*======some static methods will been moved to a util class by damon,end======*//
	
	@Override
	public String toString() {
		return "RfqItemVO [cost=" + cost
				+ ", quotedPrice=" + quotedPrice  
				+ ", mfr=" + mfr + ", requiredPartNumber=" + requiredPartNumber 
				+ ", soldToCustomerNumber=" + soldToCustomerNumber  
				+  ", salesCostFlag=" + salesCostFlag
				+ ", salesCost=" + salesCost  
				+ ", pricerId=" + pricerId 
				+ ", buyCurr=" + buyCurr
				+ ", rfqCurr=" + rfqCurr + ", dLinkFlag=" + dLinkFlag + ", exRateRfq=" + exRateRfq + ", exRateBuy=" + exRateBuy + "]";
	}
	
	
	public void clearPricingInfo() {
		pricerId = 0L;
		
		allocationFlag = null;
		aqFlag = false;
		qcComment  = null;
		bottomPrice = null;
		cancellationWindow = null;
		cost = null;
		costIndicator = null;
		description = null;
		leadTime = null;
//		materialTypeId = null; ?
		minSellPrice = null;
		moq = null;
		mov = null;
		mpq = null;
		multiUsage = null;
		orderQties = null;
		pmoq = null;
		poExpiryDate = null;
		priceListRemarks1 = null;
		priceListRemarks2 = null;
		priceListRemarks3 = null;
		priceListRemarks4 = null;
		priceValidity = null;
		productGroup1 = null; 
		productGroup2 = null;
		productGroup3 = null;
		productGroup4 = null;
		programType = null;
		qtyIndicator = null;
		quoteExpiryDate = null;
		quotedQty = null;
		resaleIndicator = null;
		reschedulingWindow = null;
		salesCost = null;
		salesCostFlag = false; 
		salesCostType = null; 
		shipmentValidity = null;
		suggestedResale = null;
		termsAndCondition = null;

		vendorQuoteNumber = null;
		vendorQuoteQty = null;
		
		qtyIndicatorFromMfrDetail = true;
		
		buyCurr = null;
		exRateBuy = null;
		exRateRfq = null;
		handling = null;
		vat = null;
	}
	
	private Date getPriceValidityAsDate() {
		if (priceValidity == null) {
			return null;
		}
		
		if (StringUtils.isNumeric(priceValidity)) {
			Calendar calendar = Calendar.getInstance();
			calendar.add(Calendar.DATE, Integer.parseInt(priceValidity));
			return calendar.getTime();
		} 
		DateFormat format = new SimpleDateFormat("dd/MM/yyyy"); 
		try {
			Date date =  format.parse(priceValidity);
			return date;
		} catch (ParseException e) {
			//to do
			e.printStackTrace();
		}
		
		return null;
	}
	
	public Double calResalesMargin() {
    	Double result = null;
    	if(cost!=null) {
    		//BigDecimal costAsRfq = null;
    		if(cost == 0d) {
    			if(!StringUtils.isBlank(quotedPrice) && getAsDouble(quotedPrice) > 0d) {
    				result= 100d;
    			}
    			//&& (costAsRfq=toRfqCurr(cost)) != null
    		} else if(cost > 0d ) {
    			if(!StringUtils.isBlank(quotedPrice) && getAsDouble(quotedPrice) > 0d) {
    				result= 100*(getAsDouble(quotedPrice)-cost)/getAsDouble(quotedPrice);
    			}
    		}
    	}
    	return result;

	}
	
	public Double calTargetMargin() {
    	Double result = null;
    	if(cost!=null) {
    		BigDecimal targetAsBuy = null;
    		Double target = getAsDouble(targetResale);
    		if(cost == 0d) {
    			if(!StringUtils.isEmpty(targetResale) && target > 0d) {
    				result= 100d;
    			}
    		} else if(cost > 0d && (targetAsBuy= this.toBuyCurr(target)) != null) {
    			
    			if(!StringUtils.isEmpty(targetResale) && targetAsBuy.doubleValue() > 0d) {
    				result= 100*(targetAsBuy.doubleValue()-cost)/targetAsBuy.doubleValue();
    			}
    		}
    	}
    	return result;
    }
	
	private Double getAsDouble(String value) {
		if (value == null) {
			return null;
		}
		try {
			return Double.parseDouble(value.trim());
		} catch(Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	/*
	public void calQty() {
		QtyCal qtyCal = new QtyCal(this);
		qtyIndicator = qtyCal.calQtyIndicator();
		moq = qtyCal.calMoq();
		quotedQty =  qtyCal.calQuotedQty();
	}
	*/
	
	public void fillPriceInfoToQuoteItem(QuoteItem quoteItem) {

		quoteItem.setQuotedMfr(findMfr(mfr));
		quoteItem.setQuotedMaterial(quotedMaterial);
		quoteItem.setRequestedMfr(findMfr(mfr));
		quoteItem.setRequestedMaterialForProgram(quotedMaterial);
		quoteItem.setRequestedPartNumber(requiredPartNumber);
		if (StringUtils.isNotEmpty(requiredQty)) {
			quoteItem.setRequestedQty(Integer.parseInt(requiredQty));
		}
		quoteItem.setPricerId(pricerId);
		if(soldToCustomer!=null) {
			quoteItem.setSoldToCustomer(soldToCustomer);
		}
		quoteItem.setQuotedPartNumber(quotedPartNumber);
		//fixed By DamonChen@20180412
		if(StringUtils.isEmpty(quoteItem.getQuotedPartNumber())){
			quoteItem.setQuotedPartNumber(quoteItem.getRequestedPartNumber());
		}
		
		quoteItem.setAllocationFlag(allocationFlag);
    	if(quoteItem.getAqcc() != null){
    		if(qcComment != null){
    			if(! quoteItem.getAqcc().toUpperCase().contains(qcComment.toUpperCase())){
    				quoteItem.setAqcc(quoteItem.getAqcc() + ";" + qcComment);
    			}
    		}
    	}else{
    		quoteItem.setAqcc(qcComment);
    	}
		quoteItem.setBottomPrice(bottomPrice);
		quoteItem.setCancellationWindow(cancellationWindow);
		quoteItem.setCost(cost);
		quoteItem.setCostIndicator(costIndicator);
		quoteItem.setDescription(description);
		quoteItem.setLeadTime(leadTime);
		quoteItem.setMaterialTypeId(materialTypeId);
		quoteItem.setMinSellPrice(minSellPrice);
		
		quoteItem.setMoq(moq);
		quoteItem.setMov(mov);
		quoteItem.setMpq(mpq);
		
		quoteItem.setMultiUsageFlag(multiUsage);
		
		quoteItem.setPmoq(pmoq);
		quoteItem.setPoExpiryDate(poExpiryDate);
		
		quoteItem.setPriceListRemarks1(priceListRemarks1);
		quoteItem.setPriceListRemarks2(priceListRemarks2);
		quoteItem.setPriceListRemarks3(priceListRemarks3);
		quoteItem.setPriceListRemarks4(priceListRemarks4);
    	
		quoteItem.setPriceValidity(priceValidity);
		
		quoteItem.setProductGroup1(productGroup1);
		quoteItem.setProductGroup2(productGroup2);
		quoteItem.setProductGroup3(productGroup3);
		quoteItem.setProductGroup4(productGroup4);
		
		quoteItem.setProgramType(programType);
		quoteItem.setQtyIndicator(qtyIndicator);
		quoteItem.setQuoteExpiryDate(quoteExpiryDate);
		quoteItem.setQuotedMaterial(quotedMaterial);
		quoteItem.setQuotedQty(quotedQty);
		quoteItem.setResaleIndicator(resaleIndicator);
		quoteItem.setRescheduleWindow(reschedulingWindow);
		quoteItem.setSalesCost(salesCost);
		quoteItem.setSalesCostFlag(salesCostFlag);
		quoteItem.setSalesCostType(salesCostType);
		quoteItem.setShipmentValidity(shipmentValidity);
		quoteItem.setSuggestedResale(suggestedResale);
		quoteItem.setVendorQuoteNumber(vendorQuoteNumber);
		quoteItem.setVendorQuoteQty(vendorQuoteQty);
		
		quoteItem.setTermsAndConditions(termsAndCondition);
		quoteItem.setQuotationEffectiveDate(quotaionEffectiveDate); 
		
		quoteItem.setStage(stage);
		quoteItem.setStatus(status);
		
		quoteItem.setValidFlag(true);
		quoteItem.setRevertVersion(QuoteSBConstant.DEFAULT_REVERT_VERISON);
		quoteItem.setDesignInCat(designInCat);
		quoteItem.setSentOutTime(AppDateUtil.getCurrentTime());
		quoteItem.setApplication(this.getApplication());
		quoteItem.setCompetitorInformation(this.getCompetitorInformation());
		quoteItem.setQuoteType(this.getQuoteType());
		quoteItem.setLoaFlag(this.isLoa());
		if (this.getEau() != null) {
			quoteItem.setEau(Integer.valueOf(this.getEau()));
		}
		quoteItem.setEnquirySegment(this.getEnquirySegment());
		quoteItem.setRemarks(itemRemarks);
		quoteItem.setMpSchedule(this.getMpSchedule());
		quoteItem.setPpSchedule(this.getPpSchedule());
		quoteItem.setDrNedaId(this.getDrNedaId());
		quoteItem.setDrNedaLineNumber(this.getDrNedaLineNumber());
		quoteItem.setDesignLocation(this.getDesignLocation());
		quoteItem.setDesignRegion(this.getDesignRegion());
		quoteItem.setDrmsNumber(this.getDrmsNumber());
		//Fixed by DamonChen@201804024
		if (StringUtils.isNotBlank(this.getQuotedPrice())) {
			quoteItem.setQuotedPrice(Double.parseDouble(this.getQuotedPrice().trim()));
		}
		quoteItem.setBmtCheckedFlag(this.isBmtCheckedFlag());
		if (this.getResaleIndicator() == null) {
			quoteItem.setResaleIndicator(QuoteSBConstant.DEFAULT_RESALE_INDICATOR);
			quoteItem.setResaleMin(QuoteSBConstant.DEFAULT_RESALE_INDICATOR_MIN);
			quoteItem.setResaleMax(QuoteSBConstant.DEFAULT_RESALE_INDICATOR_MAX);
		} else {
			quoteItem.setResaleIndicator(this.getResaleIndicator());
			String minResale = this.getResaleIndicator().substring(0, 2);
			String maxResale = this.getResaleIndicator().substring(2, 4);
			quoteItem.setResaleMin(Double.parseDouble(minResale));
			if (maxResale.matches("[0-9]{2}"))
				quoteItem.setResaleMax(Double.parseDouble(maxResale));
			else
				quoteItem.setResaleMax(QuoteSBConstant.DEFAULT_RESALE_INDICATOR_MAX);
		}
		quoteItem.setRequestedProgramMaterialForProgram(this.getRequestedProgramMaterialForProgram());
		quoteItem.setTargetMargin(this.calTargetMargin());
		quoteItem.setResaleMargin(this.calResalesMargin());

/*		quoteItem.setBuyCurr("USD");
		if (this.getRfqHeaderVO() != null) {
			quoteItem.setRfqCurr(this.getRfqHeaderVO().getRfqCurr());
		}*/
		
		quoteItem.setBuyCurr(Currency.getByCurrencyName(this.buyCurr));
		quoteItem.setRfqCurr(Currency.getByCurrencyName(this.rfqCurr));
		//if(quoteItem.getFinalCurr()==null){
			//quoteItem.setFinalCurr(Currency.USD);
		//Fixed for defect#30,the field"FNL CUR" will not updated in my quote search  when  change  the RFQ currency in   draft   RFQ submisson
			quoteItem.setFinalCurr(Currency.getByCurrencyName(this.rfqCurr));
		//}
		//handling is not null mean that this is called by aq logic... 
		if(this.exRateBuy != null || this.exRateRfq != null) {
			quoteItem.setExRateBuy(this.exRateBuy);
			quoteItem.setExRateRfq(this.exRateRfq);
			quoteItem.setHandling(this.handling);
			quoteItem.setVat(this.vat);	
		}
		
		if (quoteItem.getDrmsNumber() != null && quoteItem.getDrmsNumber().longValue() == 0l) {
			quoteItem.setDrmsNumber(null);
		}

		if (this.getProjectName() != null)
			quoteItem.setProjectName(this.getProjectName());

		quoteItem.setQuoteNumber(this.getQuoteNumber());

		quoteItem.setSprFlag(this.isSpecialPriceIndicator());
		quoteItem.setSupplierDrNumber(this.getSupplierDrNumber());
		if (this.getTargetResale() != null && !StringUtils.isEmpty(this.getTargetResale())) {
			quoteItem.setTargetResale(Double.valueOf(this.getTargetResale()));
		}

		if (this.getSapPartNumber() != null) {
			quoteItem.setSapPartNumber(this.getSapPartNumber());
		}
    	
		if (this.getAttachments() != null) {
			for (int i = 0; i < this.getAttachments().size(); i++) {
				this.getAttachments().get(i).setQuoteItem(quoteItem);
			}
		}
		quoteItem.setAttachments(this.getAttachments());
	}
	
	private Manufacturer findMfr(String name){
		TypedQuery<Manufacturer> query = ThreadLocalEntityManager.em().createQuery("select m from Manufacturer m where m.name = :mfr ", Manufacturer.class);
		query.setParameter("mfr", name);
		try {
			return query.getSingleResult();
		} catch (NoResultException e) {
			logger.log(Level.SEVERE, "Exception in finding the manufacturer by name : " + name + " , Exception message : "+e.getMessage(),e);
			return null;
		}
		
	}
	
	private boolean isBmtQuote() {
		if (StringUtils.isEmpty(designRegion)) {
			return false;
		}
		if (designRegion.equalsIgnoreCase("ASIA")) {
			return false;
		}
		return true;
	}
	
	public BigDecimal getHandling() {
		return handling;
	}
	public void setHandling(BigDecimal handling) {
		this.handling = handling;
	}
	public void calStageStatusWithStatusColor() {
		calStatusColor();
		
		if (StringUtils.isNotEmpty(stage) || StringUtils.isNotEmpty(status)) {
			return;
		}
		
		switch (statusColor) {
			case 3: 
				stage = QuoteSBConstant.QUOTE_STAGE_DRAFT;
				status = QuoteSBConstant.RFQ_STATUS_DQ;
				break;
			case 2:
				stage = QuoteSBConstant.QUOTE_STAGE_PENDING;
				status = QuoteSBConstant.RFQ_STATUS_QC;
				break;
			case 1:
				stage = QuoteSBConstant.QUOTE_STAGE_PENDING;
				status = QuoteSBConstant.RFQ_STATUS_QC;
				break;
			}
	}
	
	public void calStatusColor() {

		Manufacturer mfr = findMfr();
		if (mfr == null) {
			statusColor = 3;
			return;
		}
		boolean needPricer = true;
		ManufacturerMapping manufacturerMapping = mfr.getManufacturerMapping(this.getRfqHeaderVO().getBizUnit());
		
		if( manufacturerMapping == null ) {
			statusColor = 3;
			return;
		}
		
		needPricer = manufacturerMapping.getPricer(); 
		
		if (material != null) {
			
			if(material.getValid()==null || material.getValid()) {
				if (material.hasPricersForBizUnit(this.getRfqHeaderVO().getBizUnit())) {
					statusColor = 1;
				} else {
					if (needPricer) {
						List<SapMaterial> spl=material.getSapMaterials();
						logger.info("SapMaterial is:"+spl.size());
						if(spl.size()>0){
							for(SapMaterial sp:spl){
								logger.info("SapMaterial is:"+sp.toString());
							}
						}else{
							logger.info("SapMaterial is: 00000000000000");	
						}
						if (material.getSapMaterials() == null ||material.getSapMaterials().size()==0 ) {
							statusColor = 3;
							logger.info("Jump into material.getSapMaterials() == null ||material.getSapMaterials().size()==0 ");	
						} else {
							statusColor = 2;
						}
					} else {
						statusColor = 2;
					}
				}
			} else {
				if (needPricer) {
					statusColor = 3;
				} else {
					statusColor = 2;
				}
			}
			
		} else { // material == null
			if (needPricer) {
				statusColor = 3;
			} else {
				statusColor = 2;
			}
		}
		return;
	}
	
	private Manufacturer findMfr() {
			EntityManager em = ThreadLocalEntityManager.em();
			if (em == null || em.isOpen() == false) {
				return null;
			}
			TypedQuery<Manufacturer> query = em.createQuery("select m from Manufacturer m where m.name like :name", Manufacturer.class);
			query.setParameter("name", mfr);
			List<Manufacturer> mfrs = query.getResultList();
			if (mfrs.isEmpty()) {
				return null;
			} else {
				return mfrs.get(0);
			}


		
	}
	
	public void fillWithDefaultValue() {
		if (StringUtils.isEmpty(getResaleIndicator())) {
			setResaleIndicator("00AA");
		}
		
		if (StringUtils.isEmpty(getQtyIndicator())) {
			setQtyIndicator("MOQ");
		}
		
		/*if(StringUtils.isEmpty(this.getBuyCurr())) {
			this.setBuyCurr(this.getRfqCurr());
			this.calExRate();
		}*/
	}
	
	public void adjustAfterNoFoundBuyCurr() {
		if(StringUtils.isEmpty(this.getBuyCurr())) {
			this.setBuyCurr(this.getRfqCurr());
			this.calExRate();
		}
	}
	
/*	public Money getMinSellPriceAsMoney() {
		

		Money money = Money.of(this.minSellPrice, Currency.USD);
		if(money ==null){
			return null;
		}
		money.convertTo(Currency.getByCurrencyName(this.rfqCurr), new BizUnit(this.getRfqHeaderVO().getBizUnit()), this.soldToCustomer, new Date());

		return money;
	}*/
	
	
	public Double getRfqCurrMinSellPrice(){
		
		if(this.minSellPrice ==null){
			return null;
		}
		
		if(this.exRateBuy ==null){
			return null;
		}
		
		if(this.exRateRfq ==null){
			return null;
		}
		

		// new BigDecimal(this.minSellPrice).multiply(this.exRateRfq).divide(this.exRateBuy, Constants.SCALE, Constants.ROUNDING_MODE).doubleValue();
       return  Money.convertAsToCurrAmtWithExrates(this.minSellPrice, exRateBuy, exRateRfq, this.handling, true);
		/* return Money.convertAsToCurrAmt(this.minSellPrice,Currency.getByCurrencyName(this.getBuyCurr()), Currency.getByCurrencyName(this.getRfqCurr()),
				new BizUnit(this.getRfqHeaderVO().getBizUnit()), this.getSoldToCustomer(), this.getExRateDate(), Currency.getByCurrencyName(this.getRfqCurr()));*/
	}
	
	
	
	public Double getRfqCurrSalesCost(){
		
		
		if(this.getOrderQties() ==null || this.getOrderQties().isEmpty()){
			return null;
		}
		
		if(this.getOrderQties().get(0).getSalesCost()==null){
			return null;
		}
		
		if(this.exRateBuy ==null){
			return null;
		}
		
		if(this.exRateRfq ==null){
			return null;
		}
		

		// new BigDecimal(this.minSellPrice).multiply(this.exRateRfq).divide(this.exRateBuy, Constants.SCALE, Constants.ROUNDING_MODE).doubleValue();
       BigDecimal v= Money.convertAsToCurrAmtWithExrates(this.getOrderQties().get(0).getSalesCost(), exRateBuy, exRateRfq, this.handling, true);
       
       return v == null? null : v.doubleValue();
	}
}