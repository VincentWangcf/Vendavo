package com.avnet.emasia.webquote.vo;

import java.io.Serializable;
import java.math.BigDecimal;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang.StringUtils;

import com.avnet.emasia.webquote.entity.BizUnit;
import com.avnet.emasia.webquote.entity.Currency;
import com.avnet.emasia.webquote.entity.Customer;
import com.avnet.emasia.webquote.entity.ExchangeRate;
import com.avnet.emasia.webquote.entity.Manufacturer;
import com.avnet.emasia.webquote.entity.Material;
import com.avnet.emasia.webquote.entity.Money;
import com.avnet.emasia.webquote.entity.NormalPricer;
import com.avnet.emasia.webquote.entity.ProductGroup;
import com.avnet.emasia.webquote.entity.QtyCal;
import com.avnet.emasia.webquote.entity.QuantityBreakPrice;
import com.avnet.emasia.webquote.entity.QuoteItem;
import com.avnet.emasia.webquote.entity.SalesCostType;
import com.avnet.emasia.webquote.helper.TransferHelper;
import com.avnet.emasia.webquote.quote.ejb.constant.QuoteSBConstant;

public class PricerInfo implements Serializable {

	private static final Logger LOGGER = Logger.getLogger(PricerInfo.class.getName());
	private static final long serialVersionUID = 7697011599760935818L;
	// Out parameters

	private long pricerId;

	private Double cost;
	private Integer moq;
	private Integer mov;
	private Integer mpq;
	private String leadTime;

	private String priceValidity;
	private Date shipmentValidity;

	private Double minSell;
	private Double bottomPrice;

	private String termsAndConditions;
	private String priceListRemarks1;
	private String priceListRemarks2;
	private String priceListRemarks3;
	private String priceListRemarks4;
	private String description;
	private Date quotationEffectiveDate;

	private String materialTypeId;
	private String programType;
	private String costIndicator;

	private Date displayQuoteEffDate;

	// In parameters
	private String bizUnit;
	private String mfr;
	private String fullMfrPartNumber;
	private String soldToCustomerNumber;
	private String soldToCustomerName;
	private String endToCustomerNumber;
	private String endToCustomerName;
	private String salesEmployeeId;
	private long salesId;

	private String qcComment;

	private List<Customer> allowedCustomers;
	private Customer soldToCustomer;
	private Customer endCustomer;

	private String sapPartNumber;

	private int itemNumber;

	private Integer requestedQty;

	private Boolean allocationFlag;

	private Boolean multiUsageFlag;

	// new fields for sales cost project
	private boolean salesCostFlag;
	private SalesCostType salesCostType;
	private BigDecimal salesCost;
	private BigDecimal suggestedResale;
	private String custoemrGroupId;

	private Integer reschedulingWindow;
	private Integer cancellationWindow;
	private String qtyIndicator;

	private ProductGroup productGroup2;
	private ProductGroup productGroup1;
	private String productGroup3;
	private String productGroup4;

	// public String salesCostPart;
	private String resaleIndicator;

	private String pricerType;

	private Date quoteExpiryDate;

	private boolean hasRestrictCustomer;

	private int seq;
	private String bottomPriceStr;

	private Manufacturer quotedMfr;

	private Material quotedMaterial;

	private String pmoq;
	private Integer quotedQty;
	private Date poExpiryDate;
	// to do, to remove
	private String orderQty;
	private List<QuantityBreakPrice> orderQties = new ArrayList<QuantityBreakPrice>();
	private Double quotedPrice;
	private String vendorQuoteNumber;
	private Integer vendorQuoteQty;
	private String rfqCurr;
	private String buyCurr;
	private String finalCurr;
	private Date submitDate;

	private BigDecimal exRateRfq;

	private BigDecimal exRateBuy;

	private Double disRfqCurrMinSell;

	private BigDecimal handling;

	private BigDecimal vat;
	
	private Date exRateDate;
	
	public PricerInfo() {
	}

	public Double getDisRfqCurrMinSell() {
		return disRfqCurrMinSell;
	}

	public void setDisRfqCurrMinSell(Double disRfqCurrMinSell) {
		this.disRfqCurrMinSell = disRfqCurrMinSell;
	}

	public String getFinalCurr() {
		return finalCurr;
	}

	public void setFinalCurr(String finalCurr) {
		this.finalCurr = finalCurr;
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

	public long getPricerId() {
		return pricerId;
	}

	public void setPricerId(long pricerId) {
		this.pricerId = pricerId;
	}

	public Double getCost() {
		return cost;
	}

	public void setCost(Double cost) {
		this.cost = cost;
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

	public Integer getMpq() {
		return mpq;
	}

	public void setMpq(Integer mpq) {
		this.mpq = mpq;
	}

	public String getLeadTime() {
		return leadTime;
	}

	public void setLeadTime(String leadTime) {
		this.leadTime = leadTime;
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

	public Double getMinSell() {
		return minSell;
	}

	public void setMinSell(Double minSell) {
		this.minSell = minSell;
	}

	public Double getBottomPrice() {
		return bottomPrice;
	}

	public void setBottomPrice(Double bottomPrice) {
		this.bottomPrice = bottomPrice;
	}

	public String getTermsAndConditions() {
		return termsAndConditions;
	}

	public void setTermsAndConditions(String termsAndConditions) {
		this.termsAndConditions = termsAndConditions;
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

	public String getResaleIndicator() {
		return resaleIndicator;
	}

	public void setResaleIndicator(String resaleIndicator) {
		this.resaleIndicator = resaleIndicator;
	}

	public Date getQuotationEffectiveDate() {
		return quotationEffectiveDate;
	}

	public void setQuotationEffectiveDate(Date quotationEffectiveDate) {
		this.quotationEffectiveDate = quotationEffectiveDate;
	}

	public String getBizUnit() {
		return bizUnit;
	}

	public void setBizUnit(String bizUnit) {
		this.bizUnit = bizUnit;
	}

	public String getFullMfrPartNumber() {
		return fullMfrPartNumber;
	}

	public void setFullMfrPartNumber(String fullMfrPartNumber) {
		this.fullMfrPartNumber = fullMfrPartNumber;
	}

	public String getMfr() {
		return mfr;
	}

	public void setMfr(String mfr) {
		this.mfr = mfr;
	}

	public String getSoldToCustomerNumber() {
		return soldToCustomerNumber;
	}

	public void setSoldToCustomerNumber(String soldToCustomerNumber) {
		this.soldToCustomerNumber = soldToCustomerNumber;
	}

	public String getSoldToCustomerName() {
		return soldToCustomerName;
	}

	public void setSoldToCustomerName(String soldToCustomerName) {
		this.soldToCustomerName = soldToCustomerName;
	}

	public String getEndToCustomerNumber() {
		return endToCustomerNumber;
	}

	public void setEndToCustomerNumber(String endToCustomerNumber) {
		this.endToCustomerNumber = endToCustomerNumber;
	}

	public String getEndToCustomerName() {
		return endToCustomerName;
	}

	public void setEndToCustomerName(String endToCustomerName) {
		this.endToCustomerName = endToCustomerName;
	}

	public String getSalesEmployeeId() {
		return salesEmployeeId;
	}

	public void setSalesEmployeeId(String salesEmployeeId) {
		this.salesEmployeeId = salesEmployeeId;
	}

	public long getSalesId() {
		return salesId;
	}

	public void setSalesId(long salesId) {
		this.salesId = salesId;
	}

	public String getMaterialTypeId() {
		return materialTypeId;
	}

	public void setMaterialTypeId(String materialTypeId) {
		this.materialTypeId = materialTypeId;
	}

	public String getProgramType() {
		return programType;
	}

	public void setProgramType(String programType) {
		this.programType = programType;
	}

	public String getCostIndicator() {
		return costIndicator;
	}

	public void setCostIndicator(String costIndicator) {
		this.costIndicator = costIndicator;
	}

	public Date getDisplayQuoteEffDate() {
		return displayQuoteEffDate;
	}

	public void setDisplayQuoteEffDate(Date displayQuoteEffDate) {
		this.displayQuoteEffDate = displayQuoteEffDate;
	}

	public String getSapPartNumber() {
		return sapPartNumber;
	}

	public void setSapPartNumber(String sapPartNumber) {
		this.sapPartNumber = sapPartNumber;
	}

	public String getPmoq() {
		return pmoq;
	}

	public void setPmoq(String pmoq) {
		this.pmoq = pmoq;
	}

	public Integer getQuotedQty() {
		return quotedQty;
	}

	public void setQuotedQty(Integer quotedQty) {
		this.quotedQty = quotedQty;
	}

	public Date getPoExpiryDate() {
		return poExpiryDate;
	}

	public void setPoExpiryDate(Date poExpiryDate) {
		this.poExpiryDate = poExpiryDate;
	}

	public String getOrderQty() {
		return orderQty;
	}

	public void setOrderQty(String orderQty) {
		this.orderQty = orderQty;
	}

	public List<QuantityBreakPrice> getOrderQties() {
		return orderQties;
	}

	public void setOrderQties(List<QuantityBreakPrice> orderQties) {
		this.orderQties = orderQties;
	}

	public Double getQuotedPrice() {
		return quotedPrice;
	}

	public void setQuotedPrice(Double quotedPrice) {
		this.quotedPrice = quotedPrice;
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

	@Override
	public String toString() {
		return "PricerInfo [pricerId=" + pricerId + ", mfr=" + mfr + ", fullMfrPartNumber=" + fullMfrPartNumber
				+ ", bizUnit=" + bizUnit + ", soldToCustomerNumber=" + soldToCustomerNumber + ", endToCustomerNumber="
				+ endToCustomerNumber + ", cost=" + cost + ", minSell=" + minSell + ", mpq=" + mpq + ", priceValidity="
				+ priceValidity + ", materialTypeId=" + materialTypeId + ", programType=" + programType
				+ ", costIndicator=" + costIndicator + ", requestedQty=" + requestedQty + ", salesCostFlag="
				+ salesCostFlag + ", salesCostType=" + salesCostType + ", salesCost=" + salesCost + ", suggestedResale="
				+ suggestedResale + ", qtyIndicator=" + qtyIndicator + ", pricerType=" + pricerType + "]";
	}

	public String getQcComment() {
		return qcComment;
	}

	public void setQcComment(String qcComment) {
		this.qcComment = qcComment;
	}

	public List<Customer> getAllowedCustomers() {
		return allowedCustomers;
	}

	public void setAllowedCustomers(List<Customer> allowedCustomers) {
		this.allowedCustomers = allowedCustomers;
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

	public int getItemNumber() {
		return itemNumber;
	}

	public void setItemNumber(int itemNumber) {
		this.itemNumber = itemNumber;
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

	public String getQtyIndicator() {
		return qtyIndicator;
	}

	public void setQtyIndicator(String qtyIndicator) {
		this.qtyIndicator = qtyIndicator;
	}

	public Manufacturer getQuotedMfr() {
		return quotedMfr;
	}

	public void setQuotedMfr(Manufacturer quotedMfr) {
		this.quotedMfr = quotedMfr;
	}

	public Material getQuotedMaterial() {
		return quotedMaterial;
	}

	public void setQuotedMaterial(Material quotedMaterial) {
		this.quotedMaterial = quotedMaterial;
	}

	public void fillPriceInfoToQuoteItem(QuoteItem quoteItem) {

		quoteItem.setQuotedMfr(quotedMfr);
		quoteItem.setQuotedMaterial(quotedMaterial);
		// quoteItem.setRequestedMfr(quotedMfr);
		// quoteItem.setRequestedMaterialForProgram(quotedMaterial);
		// quoteItem.setRequestedPartNumber(fullMfrPartNumber);
		quoteItem.setRequestedQty(requestedQty);
		quoteItem.setPricerId(pricerId);
		if (soldToCustomer != null) {
			quoteItem.setSoldToCustomer(soldToCustomer);
		}
		if (endCustomer != null) {
			quoteItem.setEndCustomer(endCustomer);
		}
		quoteItem.setQuotedPartNumber(fullMfrPartNumber);

		quoteItem.setAllocationFlag(allocationFlag);
		if (quoteItem.getAqcc() != null) {
			if (qcComment != null) {
				if (!quoteItem.getAqcc().toUpperCase().contains(qcComment.toUpperCase())) {
					quoteItem.setAqcc(quoteItem.getAqcc() + ";" + qcComment);
				}
			}
		} else {
			quoteItem.setAqcc(qcComment);
		}
		quoteItem.setBottomPrice(bottomPrice);
		quoteItem.setCancellationWindow(cancellationWindow);
		quoteItem.setCost(cost);
		quoteItem.setCostIndicator(costIndicator);
		quoteItem.setDescription(description);
		quoteItem.setLeadTime(leadTime);
		quoteItem.setMaterialTypeId(materialTypeId);
		quoteItem.setMinSellPrice(minSell);

		quoteItem.setMoq(moq);
		quoteItem.setMov(mov);
		quoteItem.setMpq(mpq);

		quoteItem.setMultiUsageFlag(multiUsageFlag);

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
		quoteItem.setQuotedPrice(quotedPrice);
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

		quoteItem.setTermsAndConditions(termsAndConditions);
		// fixed by Damonchen@20180419,For defect#259,284
		quoteItem.setQuotationEffectiveDate(displayQuoteEffDate);

		// quoteItem.setBuyCurr(buyCurr==null ? null :
		// Currency.valueOf(buyCurr));
		quoteItem.setBuyCurr(Currency.valueOf(buyCurr));
		quoteItem.setRfqCurr(Currency.getByCurrencyName(rfqCurr));
		quoteItem.setFinalCurr(Currency.getByCurrencyName(rfqCurr));
		quoteItem.setExRateRfq(exRateRfq);
		quoteItem.setExRateBuy(exRateBuy);
		quoteItem.setHandling(handling);
		quoteItem.setVat(vat);
		quoteItem.reCalMargin();
		// quoteItem.setTargetMargin(calTargetMargin());

	}

	/*
	 * public Double calTargetMargin() { Double result = null; if(cost!=null) {
	 * BigDecimal costAsRfq = null; if(cost == 0d) {
	 * if(!StringUtils.isEmpty(targetResale) && getAsDouble(targetResale) > 0d)
	 * { result= 100d; } } else if(cost > 0d && (costAsRfq=toRfqCurr(cost)) !=
	 * null) {
	 * 
	 * if(!StringUtils.isEmpty(targetResale) && getAsDouble(targetResale) > 0d)
	 * { result=
	 * 100*(getAsDouble(targetResale)-costAsRfq.doubleValue())/getAsDouble(
	 * targetResale); } } } return result; }
	 * 
	 * public Double calResalesMargin() { Double result = null; if(cost!=null) {
	 * //BigDecimal costAsRfq = null; if(cost == 0d) {
	 * if(!StringUtils.isBlank(quotedPrice) && getAsDouble(quotedPrice) > 0d) {
	 * result= 100d; } //&& (costAsRfq=toRfqCurr(cost)) != null } else if(cost >
	 * 0d ) { if(!StringUtils.isBlank(quotedPrice) && getAsDouble(quotedPrice) >
	 * 0d) { result=
	 * 100*(getAsDouble(quotedPrice)-cost)/getAsDouble(quotedPrice); } } }
	 * return result;
	 * 
	 * }
	 */

	public Integer getRequestedQty() {
		return requestedQty;
	}

	public void setRequestedQty(Integer requestedQty) {
		this.requestedQty = requestedQty;
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

	public String getCustoemrGroupId() {
		return custoemrGroupId;
	}

	public void setCustoemrGroupId(String custoemrGroupId) {
		this.custoemrGroupId = custoemrGroupId;
	}

	public Boolean getAllocationFlag() {
		return allocationFlag;
	}

	public void setAllocationFlag(Boolean allocationFlag) {
		this.allocationFlag = allocationFlag;
	}

	public Boolean getMultiUsageFlag() {
		return multiUsageFlag;
	}

	public void setMultiUsageFlag(Boolean multiUsageFlag) {
		this.multiUsageFlag = multiUsageFlag;
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

	public static PricerInfo newInstance(PricerInfo pricerInfo) {
		PricerInfo newPricerInfo = new PricerInfo();
		try {
			BeanUtils.copyProperties(newPricerInfo, pricerInfo);
		} catch (Exception e) {
			LOGGER.log(Level.SEVERE, "Exception occured when copying PricerInfo " + e.getMessage(), e);
		}

		return newPricerInfo;
	}

	public String getPricerType() {
		return pricerType;
	}

	public void setPricerType(String pricerType) {
		this.pricerType = pricerType;
	}

	public Date getQuoteExpiryDate() {
		return quoteExpiryDate;
	}

	public void setQuoteExpiryDate(Date quoteExpiryDate) {
		this.quoteExpiryDate = quoteExpiryDate;
	}

	public boolean isHasRestrictCustomer() {
		return hasRestrictCustomer;
	}

	public void setHasRestrictCustomer(boolean hasRestrictCustomer) {
		this.hasRestrictCustomer = hasRestrictCustomer;
	}

	/*** for export excel , pls do not remove **/
	public String getSalesCostStr() {
		return TransferHelper.ConvertBigDecimalToStr(this.getSalesCost());
	}

	/*** for export excel , pls do not remove **/
	public String getSuggestedResaleStr() {
		return TransferHelper.ConvertBigDecimalToStr(this.getSuggestedResale());
	}

	public String getBottomPriceStr() {
		return bottomPriceStr;
	}

	public void setBottomPriceStr(String bottomPriceStr) {
		this.bottomPriceStr = bottomPriceStr;
	}

	public int getSeq() {
		return seq;
	}

	public void setSeq(int seq) {
		this.seq = seq;
	}

	/*
	 * this method has strict business definition. Be careful to change.
	 */
	// 2018-04-09 comment on fields - cancellationWindow , multiUsageFlag,
	// qtyIndicator, reschedulingWindow, resaleIndicator
	public void clearPricingInfo() {
		quotedMaterial = null;
		pricerId = 0L;

		allocationFlag = null;
		// qcComment = null;
		bottomPrice = null;
		// cancellationWindow = null;
		cost = null;
		costIndicator = null;
		description = null;
		leadTime = null;
		// materialTypeId = null; ?
		minSell = null;
		moq = null;
		mov = null;
		mpq = null;
		// multiUsageFlag = null;
		pmoq = null;
		poExpiryDate = null;
		priceListRemarks1 = null;
		priceListRemarks2 = null;
		priceListRemarks3 = null;
		priceListRemarks4 = null;
		priceValidity = null;
		// productGroup1 = null; from MaterialRegional, not clear
		// productGroup2 = null;
		// productGroup3 = null;
		// productGroup4 = null;
		// programType = null; not clear, used for data access
		// qtyIndicator = null;
		quotationEffectiveDate = null;
		quoteExpiryDate = null;

		// quotedQty = null;
		quotedPrice = null;
		// resaleIndicator = null;
		// reschedulingWindow = null;
		salesCost = null;
		// salesCostFlag = false; from MaterialRegional, not clear
		// salesCostType = null;
		shipmentValidity = null;
		suggestedResale = null;
		termsAndConditions = null;

		buyCurr = null;
		exRateBuy = null;
		exRateRfq = null;
		handling = null;
		vat = null;
		// vendorQuoteNumber = null;
		// vendorQuoteQty = null;

	}

	public void fillWithDefaultValue() {
		if (StringUtils.isEmpty(getResaleIndicator())) {
			setResaleIndicator("00AA");
		}

		if (StringUtils.isEmpty(getQtyIndicator())) {
			setQtyIndicator("MOQ");
		}

		if (StringUtils.isEmpty(this.getBuyCurr())) {
			this.setBuyCurr(this.getRfqCurr());
			this.calExRate();
		}
	}

	public void adjustAfterNoFoundBuyCurr() {
		if (StringUtils.isEmpty(this.getBuyCurr())) {
			this.setBuyCurr(this.getRfqCurr());
			this.calExRate();
		}
	}

	public void calAfterFillin() {
		if (salesCostFlag) {
			// move the salesCostType to the back, such as
			// SalesCostType.ZBMP.equals(salesCostType),by DamonChen@20180222
			if (SalesCostType.ZBMP.equals(salesCostType)) {
				salesCostType = SalesCostType.ZINC;
			} else if (SalesCostType.ZBMD.equals(salesCostType)) {
				salesCostType = SalesCostType.ZIND;
			}
		}
		// calExRate();
	}

	public static PricerInfo createInstance(PricerInfo pricerInfo) {

		PricerInfo newPricerInfo = new PricerInfo();
		try {
			BeanUtils.copyProperties(newPricerInfo, pricerInfo);
		} catch (Exception e) {
			LOGGER.log(Level.SEVERE, "Exception occured when copying PricerInfo " + e.getMessage(), e);
		}
		return newPricerInfo;

	}

	public String getRfqCurr() {
		return rfqCurr;
	}

	public void setRfqCurr(String rfqCurr) {
		this.rfqCurr = rfqCurr;
	}

	public String getBuyCurr() {
		return buyCurr;
	}

	public void setBuyCurr(String buyCurr) {
		this.buyCurr = buyCurr;
	}

	public Date getSubmitDate() {
		return submitDate;
	}

	public void setSubmitDate(Date submitDate) {
		this.submitDate = submitDate;
	}

	/*public Double getCostOfRfq() {
		return toRfqCurr(this.cost);
	}*/

	public BigDecimal getHandling() {
		return handling;
	}

	public void setHandling(BigDecimal handling) {
		this.handling = handling;
	}

	public BigDecimal getVat() {
		return vat;
	}

	public void setVat(BigDecimal vat) {
		this.vat = vat;
	}

	public Date getExRateDate() {
		return exRateDate;
	}

	public void setExRateDate(Date exRateDate) {
		this.exRateDate = exRateDate;
	}

	private Double toRfqCurr(Double value) {
		if (null == value)
			return null;
		return toRfqCurr(BigDecimal.valueOf(value));
	}

	private Double toRfqCurr(BigDecimal value) {
		/********/
		try {
			if (value == null || buyCurr == null || rfqCurr == null || this.getBizUnit() == null)
				return null;
			Money money = Money.of(value, Currency.valueOf(buyCurr)).convertToRfq(Currency.valueOf(rfqCurr),
					new BizUnit(this.getBizUnit()), this.getSoldToCustomer(), new Date());
			if (money != null) {
				return money.getAmount().doubleValue();
				// logger.info("for Pricer get converted minsell Money::" +
				// money.toString());
			}
		} catch (Exception e) {
			LOGGER.warning(MessageFormat.format(" id:[{0}] can not convert to RfqCurr:[{1}] Money ." + e.getMessage(),
					this.getPricerId(), this.getRfqCurr()));
		}
		return null;
	}

	// static boolean hasDone = false;
	public void calExRate() {
		try {
			ExchangeRate[] exRates = Money.getExchangeRate(buyCurr, rfqCurr, this.getBizUnit(),
					this.getSoldToCustomer(), this.getExRateDate());
			if (exRates != null && exRates.length == 2) {
				if (exRates[0] != null)
					this.exRateBuy = exRates[0].getExRateTo();
				if (exRates[1] != null) {
					this.exRateRfq = exRates[1].getExRateTo();
					this.vat = exRates[1].getVat();
				}
				if (exRates[0] != null && exRates[1] != null) {
					this.handling = exRates[0].getHandling().multiply(exRates[1].getHandling());
				}
				LOGGER.config(() -> "Found exRateBuy and exRateRfq :::" + Arrays.asList(exRates).toString());
			}
			/*
			 * if(!hasDone) { Logger rootLogger =
			 * LogManager.getLogManager().getLogger("");
			 * rootLogger.setLevel(Level.FINE); for (Handler h :
			 * rootLogger.getHandlers()) { h.setLevel(Level.FINE); } hasDone =
			 * true; }
			 */

		} catch (Exception e) {
			LOGGER.log(Level.SEVERE, "", e);
		}
	}
}
