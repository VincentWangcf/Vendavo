package com.avnet.emasia.webquote.commodity.bean;

import java.io.Serializable;
import java.util.Date;
import java.util.logging.Logger;

import com.avnet.emasia.webquote.commodity.constant.PRICER_TYPE;
import com.avnet.emasia.webquote.commodity.constant.PricerConstant;
import com.avnet.emasia.webquote.commodity.util.PricerUtils;
import com.avnet.emasia.webquote.commodity.util.StringUtils;
import com.avnet.emasia.webquote.entity.BizUnit;
import com.avnet.emasia.webquote.entity.CostIndicator;
import com.avnet.emasia.webquote.entity.Customer;
import com.avnet.emasia.webquote.entity.Material;
import com.avnet.emasia.webquote.entity.MaterialRegional;
import com.avnet.emasia.webquote.entity.Pricer;
import com.avnet.emasia.webquote.entity.SalesCostType;
import com.avnet.emasia.webquote.util.QuoteUtil;
import com.avnet.emasia.webquote.utilities.bean.PricerExcelAlias;

public class PricerUploadTemplateBean implements Serializable, Cloneable {
	static final Logger LOGGER=Logger.getLogger(PricerUploadTemplateBean.class.getSimpleName());
	/**
	 * 
	 */
	private static final long serialVersionUID = 2906742934766897739L;
	private long metarialId;
	private long metarialDetailId; // update metarialDetail
	private int lineSeq;
	private String version;
	private boolean isErr;
	private boolean isDuplicate;
	private boolean mfrInDb;
	private BizUnit bizUnit;
	private String qedCheckState;
	private CostIndicator ocostIndicator;
	private Material material;
	private Customer soldToCustomer;
	private Customer endCustomer;
	private Date quotationEffectiveDateFrom;
	private Date quotationEffectiveDateTo;
	private Date shipmentValidityFrom;
	private Date shipmentValidityTo;

	private Pricer pricer;
	private MaterialRegional materialRegional;
	private String lastUpdatedBy;
	private String lastUpdatedOn;

	private long contractPriceId;

	private boolean canUpdatePG2;
	private Date disQuotationEffectiveDate;

	public String action;
	private String mfrName;

	private SalesCostType salesCostTypeEnum;

	public SalesCostType getSalesCostTypeEnum() {
		return salesCostTypeEnum;
	}

	public void setSalesCostTypeEnum(SalesCostType salesCostTypeEnum) {
		this.salesCostTypeEnum = salesCostTypeEnum;
	}

	public MaterialRegional getMaterialRegional() {
		return materialRegional;
	}

	public void setMaterialRegional(MaterialRegional materialRegional) {
		this.materialRegional = materialRegional;
	}

	public String getMfrName() {
		return mfrName;
	}

	public void setMfrName(String mfrName) {
		this.mfrName = mfrName;
	}

	public Pricer getPricer() {
		return pricer;
	}

	public void setPricer(Pricer pricer) {
		this.pricer = pricer;
	}

	@PricerExcelAlias(mandatory = "true,true,true,true,true,true", fieldLength = "-1", name = "wq.label.pricerType", cellNum = "1")
	private String pricerType;

	@PricerExcelAlias(mandatory = "false,false,false,true,true,true", fieldLength = "-1", name = "wq.label.region", cellNum = "2")
	private String region;

	@PricerExcelAlias(mandatory = "false,false,false,false,false,true", fieldLength = "-1", name = "wq.label.salesCostType", cellNum = "3")
	private String salesCostType;

	@PricerExcelAlias(mandatory = "true,true,true,true,true,true", fieldLength = "20", name = "wq.label.mfr", cellNum = "4")
	private String mfr;

	@PricerExcelAlias(mandatory = "true,true,true,true,true,true", fieldLength = "40", name = "wq.label.mfrPN", cellNum = "5")
	private String fullMfrPart;

	@PricerExcelAlias(mandatory = "true,true,true,false,true,true", fieldLength = "5", name = "wq.label.buyCurInExcel", cellNum = "6")
	private String currency;

	@PricerExcelAlias(mandatory = "true,true,true,false,false,true", fieldLength = "-1", name = "wq.label.cost", cellNum = "7")
	private String cost;

	@PricerExcelAlias(mandatory = "true,true,true,false,false,true", fieldLength = "-1", name = "wq.label.costIndicator", cellNum = "8")
	private String costIndicator;

	@PricerExcelAlias(mandatory = "true,true,true,false,false,true", fieldLength = "13", name = "wq.label.validity", cellNum = "9")
	private String validity;

	@PricerExcelAlias(mandatory = "false,false,false,false,false,false", fieldLength = "-1", name = "wq.label.shipmentVal", cellNum = "10")
	private String shipmentValidity;

	@PricerExcelAlias(mandatory = "false,true,false,false,false,true", fieldLength = "14", name = "wq.label.MPQ", cellNum = "11")
	private String mPQ;

	@PricerExcelAlias(mandatory = "false,false,false,false,false,false", fieldLength = "14", name = "wq.label.moq", cellNum = "12")
	private String mOQ;

	@PricerExcelAlias(mandatory = "false,false,false,false,false,true", fieldLength = "13", name = "wq.label.mov", cellNum = "13")
	private String mOV;

	@PricerExcelAlias(mandatory = "false,true,false,false,false,true", fieldLength = "255", name = "wq.label.leadTime", cellNum = "14")
	private String leadTime;

	@PricerExcelAlias(mandatory = "false,false,false,false,false,false", fieldLength = "-1", name = "wq.label.QuotationEffectiveDate", cellNum = "15")
	private String quotationEffectiveDate;

	@PricerExcelAlias(mandatory = "false,false,false,false,false,false", fieldLength = "255", name = "wq.label.TermsAndConditions", cellNum = "16")
	private String termsAndConditions;

	@PricerExcelAlias(mandatory = "false,false,true,false,false,false", fieldLength = "16", name = "wq.label.minSellPrice", cellNum = "17")
	private String minSell;

	@PricerExcelAlias(mandatory = "false,false,false,false,false,false", fieldLength = "255", name = "wq.label.QCExternalComment", cellNum = "18")
	private String avnetQuoteCentreComments;

	@PricerExcelAlias(mandatory = "false,false,false,false,false,false", fieldLength = "40", name = "wq.label.proGroup1", cellNum = "19")
	private String productGroup1;
	// Mike said,remove the mandatory for Normal
	@PricerExcelAlias(mandatory = "false,false,false,true,false,false", fieldLength = "40", name = "wq.label.proGroup2", cellNum = "20")
	private String productGroup2;

	@PricerExcelAlias(mandatory = "false,false,false,false,false,false", fieldLength = "250", name = "wq.label.proGroup3", cellNum = "21")
	private String productGroup3;

	@PricerExcelAlias(mandatory = "false,false,false,false,false,false", fieldLength = "250", name = "wq.label.proGroup4", cellNum = "22")
	private String productGroup4;

	@PricerExcelAlias(mandatory = "false,false,false,false,false,false", fieldLength = "255", name = "wq.label.pnDescripton", cellNum = "23")
	private String description;

	@PricerExcelAlias(mandatory = "false,false,false,false,false,false", fieldLength = "255", name = "wq.label.priceListRemarks", cellNum = "24")
	private String priceListRemarks;

	@PricerExcelAlias(mandatory = "false,false,false,false,false,false", fieldLength = "255", name = "wq.label.priceListRemarks2", cellNum = "25")
	private String priceListRemarks2;

	@PricerExcelAlias(mandatory = "false,false,false,false,false,false", fieldLength = "255", name = "wq.label.priceListRemarks3", cellNum = "26")
	private String priceListRemarks3;

	@PricerExcelAlias(mandatory = "false,false,false,false,false,false", fieldLength = "255", name = "wq.label.priceListRemarks4", cellNum = "27")
	private String priceListRemarks4;

	@PricerExcelAlias(mandatory = "false,false,false,false,false,false", fieldLength = "-1", name = "wq.label.bottomPrice", cellNum = "28")
	private String bottomPrice;
	// Mike said,remove the mandatory for Normal
	@PricerExcelAlias(mandatory = "false,false,false,true,false,false", fieldLength = "-1", name = "wq.label.productCat", cellNum = "29")
	private String productCat;

	@PricerExcelAlias(mandatory = "false,true,false,false,false,true", fieldLength = "-1", name = "wq.label.prog", cellNum = "30")
	private String program;

	@PricerExcelAlias(mandatory = "false,false,false,true,false,false", fieldLength = "-1", name = "wq.label.salesCostFlag", cellNum = "31")
	private String salesCostFlag;

	@PricerExcelAlias(mandatory = "false,false,false,false,false,false", fieldLength = "-1", name = "wq.label.displayOnTop", cellNum = "32")
	private String displayOnTop;

	@PricerExcelAlias(mandatory = "false,false,false,false,false,false", fieldLength = "-1", name = "wq.label.specialItemIndicator", cellNum = "33")
	private String specialItemIndicator;

	@PricerExcelAlias(mandatory = "false,false,false,false,false,false", fieldLength = "-1", name = "wq.label.AvailableToSellQuantity", cellNum = "34")
	private String availabletoSellQuantity;

	@PricerExcelAlias(mandatory = "false,false,false,false,false,false", fieldLength = "-1", name = "wq.label.zINMControlQty", cellNum = "35")
	private String qtyControl;

	@PricerExcelAlias(mandatory = "false,true,false,false,false,true", fieldLength = "-1", name = "wq.label.quantityBreak1", cellNum = "36")
	private String quantityBreak1;

	@PricerExcelAlias(mandatory = "false,true,false,false,false,false", fieldLength = "-1", name = "wq.label.quantityBreakPrice1", cellNum = "37")
	private String quantityBreakPrice1;

	@PricerExcelAlias(mandatory = "false,false,false,false,false,true", fieldLength = "-1", name = "wq.label.quantityBreakSalesCost1", cellNum = "38")
	private String quantityBreakSalesCost1;

	@PricerExcelAlias(mandatory = "false,false,false,false,false,false", fieldLength = "-1", name = "wq.label.quantityBreakSuggestedResale1", cellNum = "39")
	private String quantityBreakSuggestedResale1;

	@PricerExcelAlias(mandatory = "false,false,false,false,false,false", fieldLength = "-1", name = "wq.label.quantityBreak2", cellNum = "40")
	private String quantityBreak2;

	@PricerExcelAlias(mandatory = "false,false,false,false,false,false", fieldLength = "-1", name = "wq.label.quantityBreakPrice2", cellNum = "41")
	private String quantityBreakPrice2;

	@PricerExcelAlias(mandatory = "false,false,false,false,false,false", fieldLength = "-1", name = "wq.label.quantityBreakSalesCost2", cellNum = "42")
	private String quantityBreakSalesCost2;

	@PricerExcelAlias(mandatory = "false,false,false,false,false,false", fieldLength = "-1", name = "wq.label.quantityBreakSuggestedResale2", cellNum = "43")
	private String quantityBreakSuggestedResale2;

	@PricerExcelAlias(mandatory = "false,false,false,false,false,false", fieldLength = "-1", name = "wq.label.quantityBreak3", cellNum = "44")
	private String quantityBreak3;

	@PricerExcelAlias(mandatory = "false,false,false,false,false,false", fieldLength = "-1", name = "wq.label.quantityBreakPrice3", cellNum = "45")
	private String quantityBreakPrice3;

	@PricerExcelAlias(mandatory = "false,false,false,false,false,,false", fieldLength = "-1", name = "wq.label.quantityBreakSalesCost3", cellNum = "46")
	private String quantityBreakSalesCost3;

	@PricerExcelAlias(mandatory = "false,false,false,false,false,false", fieldLength = "-1", name = "wq.label.quantityBreakSuggestedResale3", cellNum = "47")
	private String quantityBreakSuggestedResale3;

	@PricerExcelAlias(mandatory = "false,false,false,false,false,false", fieldLength = "-1", name = "wq.label.quantityBreak4", cellNum = "48")
	private String quantityBreak4;

	@PricerExcelAlias(mandatory = "false,false,false,false,false,false", fieldLength = "-1", name = "wq.label.quantityBreakPrice4", cellNum = "49")
	private String quantityBreakPrice4;

	@PricerExcelAlias(mandatory = "false,false,false,false,false,false", fieldLength = "-1", name = "wq.label.quantityBreakSalesCost4", cellNum = "50")
	private String quantityBreakSalesCost4;

	@PricerExcelAlias(mandatory = "false,false,false,false,false,false", fieldLength = "-1", name = "wq.label.quantityBreakSuggestedResale4", cellNum = "51")
	private String quantityBreakSuggestedResale4;

	@PricerExcelAlias(mandatory = "false,false,false,false,false,false", fieldLength = "-1", name = "wq.label.quantityBreak5", cellNum = "52")
	private String quantityBreak5;

	@PricerExcelAlias(mandatory = "false,false,false,false,false,false", fieldLength = "-1", name = "wq.label.quantityBreakPrice5", cellNum = "53")
	private String quantityBreakPrice5;

	@PricerExcelAlias(mandatory = "false,false,false,false,false,false", fieldLength = "-1", name = "wq.label.quantityBreakSalesCost5", cellNum = "54")
	private String quantityBreakSalesCost5;

	@PricerExcelAlias(mandatory = "false,false,false,false,false,false", fieldLength = "-1", name = "wq.label.quantityBreakSuggestedResale5", cellNum = "55")
	private String quantityBreakSuggestedResale5;

	@PricerExcelAlias(mandatory = "false,true,false,false,false,false", fieldLength = "-1", name = "wq.label.programEffectiveDate", cellNum = "56")
	private String programEffectiveDate;

	@PricerExcelAlias(mandatory = "false,true,false,false,false,false", fieldLength = "-1", name = "wq.label.programclosingDate", cellNum = "57")
	private String programClosingDate;

	@PricerExcelAlias(mandatory = "false,true,false,false,false,true", fieldLength = "-1", name = "wq.label.qtyIndicator", cellNum = "58")
	private String qtyIndicator;

	@PricerExcelAlias(mandatory = "false,true,false,false,false,false", fieldLength = "-1", name = "wq.label.newItemIndicator", cellNum = "59")
	private String newItemIndicator;

	@PricerExcelAlias(mandatory = "false,true,false,false,false,false", fieldLength = "-1", name = "wq.label.allocFlag", cellNum = "60")
	private String allocationFlag;

	@PricerExcelAlias(mandatory = "false,true,false,false,false,false", fieldLength = "-1", name = "wq.label.resaleIndicator", cellNum = "61")
	private String resaleIndicator;

	@PricerExcelAlias(mandatory = "false,false,false,false,false,true", fieldLength = "-1", name = "wq.label.AQFlag", cellNum = "62")
	private String aQFlag;

	@PricerExcelAlias(mandatory = "false,false,false,false,false,false", fieldLength = "-1", name = "wq.label.availableSellMore", cellNum = "63")
	private String availableToSellMoreFlag;

	@PricerExcelAlias(mandatory = "false,false,false,false,false,false", fieldLength = "-1", name = "wq.label.customerNameSoldToParty", cellNum = "64")
	private String customerName;

	@PricerExcelAlias(mandatory = "false,false,true,false,false,false", fieldLength = "-1", name = "wq.label.SoldToCode", cellNum = "65")
	private String soldToCode;

	@PricerExcelAlias(mandatory = "false,false,false,false,false,false", fieldLength = "-1", name = "wq.label.endCust", cellNum = "66")
	private String endCustomerName;

	@PricerExcelAlias(mandatory = "false,false,false,false,false,false", fieldLength = "-1", name = "wq.label.EndCustomerCode", cellNum = "67")
	private String endCustomerCode;

	@PricerExcelAlias(mandatory = "false,false,true,false,false,false", fieldLength = "-1", name = "wq.label.lstartDate", cellNum = "68")
	private String startDate;
	@PricerExcelAlias(mandatory = "false,false,false,false,false,false", fieldLength = "-1", name = "wq.label.vendorQuoteNum", cellNum = "69")
	private String vendorQuoteNo;
	@PricerExcelAlias(mandatory = "false,false,false,false,false,false", fieldLength = "-1", name = "wq.label.vendorQuoteQty", cellNum = "70")
	private String vendorQuoteQty;
	@PricerExcelAlias(mandatory = "false,false,true,false,false,false", fieldLength = "-1", name = "wq.label.overridden", cellNum = "71")
	private String overriddenStr;

	public String getCost() {
		return cost;
	}

	public void setCost(String cost) {
		this.cost = PricerUtils.roundedFor6(StringUtils.trim(cost));
	}

	public String getCostIndicator() {
		return costIndicator;
	}

	public void setCostIndicator(String costIndicator) {
		this.costIndicator = StringUtils.trim(costIndicator);
	}

	public String getValidity() {
		return validity;
	}

	public void setValidity(String validity) {
		this.validity = StringUtils.trim(validity);
	}

	public String getShipmentValidity() {
		return shipmentValidity;
	}

	public void setShipmentValidity(String shipmentValidity) {
		this.shipmentValidity = StringUtils.trim(shipmentValidity);
	}

	public String getMPQ() {
		return mPQ;
	}

	public void setMPQ(String mPQ) {
		this.mPQ = StringUtils.trim(mPQ);
	}

	public String getMOQ() {
		return mOQ;
	}

	public void setMOQ(String mOQ) {
		this.mOQ = StringUtils.trim(mOQ);
	}

	public String getMOV() {
		return mOV;
	}

	public void setMOV(String mOV) {
		this.mOV = StringUtils.trim(mOV);
	}

	public String getLeadTime() {
		return leadTime;
	}

	public void setLeadTime(String leadTime) {
		this.leadTime = StringUtils.trim(leadTime);
	}

	public String getQuotationEffectiveDate() {
		return quotationEffectiveDate;
	}

	public void setQuotationEffectiveDate(String quotationEffectiveDate) {
		this.quotationEffectiveDate = StringUtils.trim(quotationEffectiveDate);
	}

	public String getTermsAndConditions() {
		return termsAndConditions;
	}

	public void setTermsAndConditions(String termsAndConditions) {
		this.termsAndConditions = StringUtils.trim(termsAndConditions);
	}

	public String getMinSell() {
		return minSell;
	}

	public void setMinSell(String minSell) {
		this.minSell = PricerUtils.roundedFor6(StringUtils.trim(minSell));
	}

	public String getAvnetQuoteCentreComments() {
		return avnetQuoteCentreComments;
	}

	public void setAvnetQuoteCentreComments(String avnetQuoteCentreComments) {
		this.avnetQuoteCentreComments = StringUtils.trim(avnetQuoteCentreComments);
	}

	public String getProductGroup1() {
		return productGroup1;
	}

	public void setProductGroup1(String productGroup1) {
		this.productGroup1 = StringUtils.trim(productGroup1);
	}

	public String getProductGroup2() {
		return productGroup2;
	}

	public void setProductGroup2(String productGroup2) {
		this.productGroup2 = StringUtils.trim(productGroup2);
	}

	public String getProductGroup3() {
		return productGroup3;
	}

	public void setProductGroup3(String productGroup3) {
		this.productGroup3 = StringUtils.trim(productGroup3);
	}

	public String getProductGroup4() {
		return productGroup4;
	}

	public void setProductGroup4(String productGroup4) {
		this.productGroup4 = StringUtils.trim(productGroup4);
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = StringUtils.trim(description);
	}

	public String getPriceListRemarks() {
		return priceListRemarks;
	}

	public void setPriceListRemarks(String priceListRemarks) {
		this.priceListRemarks = StringUtils.trim(priceListRemarks);
	}

	public String getPriceListRemarks2() {
		return priceListRemarks2;
	}

	public void setPriceListRemarks2(String priceListRemarks2) {
		this.priceListRemarks2 = StringUtils.trim(priceListRemarks2);
	}

	public String getPriceListRemarks3() {
		return priceListRemarks3;
	}

	public void setPriceListRemarks3(String priceListRemarks3) {
		this.priceListRemarks3 = StringUtils.trim(priceListRemarks3);
	}

	public String getPriceListRemarks4() {
		return priceListRemarks4;
	}

	public void setPriceListRemarks4(String priceListRemarks4) {
		this.priceListRemarks4 = StringUtils.trim(priceListRemarks4);
	}

	public String getBottomPrice() {
		return bottomPrice;
	}

	public void setBottomPrice(String bottomPrice) {
		this.bottomPrice = PricerUtils.roundedFor6(StringUtils.trim(bottomPrice));
	}

	public String getProductCat() {
		return productCat;
	}

	public void setProductCat(String productCat) {
		this.productCat = StringUtils.trim(productCat);
	}

	public String getProgram() {
		return program;
	}

	public void setProgram(String program) {
		this.program = StringUtils.trim(program);
	}

	public String getDisplayOnTop() {
		return displayOnTop;
	}

	public void setDisplayOnTop(String displayOnTop) {
		this.displayOnTop = StringUtils.trim(displayOnTop);
	}

	public String getSpecialItemIndicator() {
		return specialItemIndicator;
	}

	public void setSpecialItemIndicator(String specialItemIndicator) {
		this.specialItemIndicator = StringUtils.trim(specialItemIndicator);
	}

	public String getAvailabletoSellQuantity() {
		return availabletoSellQuantity;
	}

	public void setAvailabletoSellQuantity(String availabletoSellQuantity) {
		this.availabletoSellQuantity = StringUtils.trim(availabletoSellQuantity);
	}

	public String getQuantityBreak1() {
		return quantityBreak1;
	}

	public void setQuantityBreak1(String quantityBreak1) {
		this.quantityBreak1 = StringUtils.trim(quantityBreak1);
	}

	public String getQuantityBreakPrice1() {
		return quantityBreakPrice1;
	}

	public void setQuantityBreakPrice1(String quantityBreakPrice1) {
		this.quantityBreakPrice1 = PricerUtils.roundedFor6(StringUtils.trim(quantityBreakPrice1));
	}

	public String getQuantityBreak2() {
		return quantityBreak2;
	}

	public void setQuantityBreak2(String quantityBreak2) {
		this.quantityBreak2 = StringUtils.trim(quantityBreak2);
	}

	public String getQuantityBreakPrice2() {
		return quantityBreakPrice2;
	}

	public void setQuantityBreakPrice2(String quantityBreakPrice2) {
		this.quantityBreakPrice2 = PricerUtils.roundedFor6(StringUtils.trim(quantityBreakPrice2));
	}

	public String getQuantityBreak3() {
		return quantityBreak3;
	}

	public void setQuantityBreak3(String quantityBreak3) {
		this.quantityBreak3 = StringUtils.trim(quantityBreak3);
	}

	public String getQuantityBreakPrice3() {
		return quantityBreakPrice3;
	}

	public void setQuantityBreakPrice3(String quantityBreakPrice3) {
		this.quantityBreakPrice3 = PricerUtils.roundedFor6(StringUtils.trim(quantityBreakPrice3));
	}

	public String getQuantityBreak4() {
		return quantityBreak4;
	}

	public void setQuantityBreak4(String quantityBreak4) {
		this.quantityBreak4 = StringUtils.trim(quantityBreak4);
	}

	public String getQuantityBreakPrice4() {
		return quantityBreakPrice4;
	}

	public void setQuantityBreakPrice4(String quantityBreakPrice4) {
		this.quantityBreakPrice4 = PricerUtils.roundedFor6(StringUtils.trim(quantityBreakPrice4));
	}

	public String getQuantityBreak5() {
		return quantityBreak5;
	}

	public void setQuantityBreak5(String quantityBreak5) {
		this.quantityBreak5 = StringUtils.trim(quantityBreak5);
	}

	public String getQuantityBreakPrice5() {
		return quantityBreakPrice5;
	}

	public void setQuantityBreakPrice5(String quantityBreakPrice5) {
		this.quantityBreakPrice5 = PricerUtils.roundedFor6(StringUtils.trim(quantityBreakPrice5));
	}

	/*
	 * public String getQuantityBreak6() { return quantityBreak6; }
	 * 
	 * public void setQuantityBreak6(String quantityBreak6) {
	 * this.quantityBreak6 = StringUtils.trim(quantityBreak6); }
	 * 
	 * public String getQuantityBreakPrice6() { return quantityBreakPrice6; }
	 * 
	 * public void setQuantityBreakPrice6(String quantityBreakPrice6) {
	 * this.quantityBreakPrice6 =
	 * PricerUtils.roundedFor6(StringUtils.trim(quantityBreakPrice6)); }
	 * 
	 * public String getQuantityBreak7() { return quantityBreak7; }
	 * 
	 * public void setQuantityBreak7(String quantityBreak7) {
	 * this.quantityBreak7 = StringUtils.trim(quantityBreak7); }
	 * 
	 * public String getQuantityBreakPrice7() { return quantityBreakPrice7; }
	 * 
	 * public void setQuantityBreakPrice7(String quantityBreakPrice7) {
	 * this.quantityBreakPrice7 =
	 * PricerUtils.roundedFor6(StringUtils.trim(quantityBreakPrice7)); }
	 * 
	 * public String getQuantityBreak8() { return quantityBreak8; }
	 * 
	 * public void setQuantityBreak8(String quantityBreak8) {
	 * this.quantityBreak8 = StringUtils.trim(quantityBreak8); }
	 * 
	 * public String getQuantityBreakPrice8() { return quantityBreakPrice8; }
	 * 
	 * public void setQuantityBreakPrice8(String quantityBreakPrice8) {
	 * this.quantityBreakPrice8 =
	 * PricerUtils.roundedFor6(StringUtils.trim(quantityBreakPrice8)); }
	 * 
	 * public String getQuantityBreak9() { return quantityBreak9; }
	 * 
	 * public void setQuantityBreak9(String quantityBreak9) {
	 * this.quantityBreak9 = StringUtils.trim(quantityBreak9); }
	 * 
	 * public String getQuantityBreakPrice9() { return quantityBreakPrice9; }
	 * 
	 * public void setQuantityBreakPrice9(String quantityBreakPrice9) {
	 * this.quantityBreakPrice9 =
	 * PricerUtils.roundedFor6(StringUtils.trim(quantityBreakPrice9)); }
	 * 
	 * public String getQuantityBreak10() { return quantityBreak10; }
	 * 
	 * public void setQuantityBreak10(String quantityBreak10) {
	 * this.quantityBreak10 = StringUtils.trim(quantityBreak10); }
	 * 
	 * public String getQuantityBreakPrice10() { return quantityBreakPrice10; }
	 * 
	 * public void setQuantityBreakPrice10(String pquantityBreakPrice10) {
	 * this.quantityBreakPrice10 =
	 * PricerUtils.roundedFor6(StringUtils.trim(pquantityBreakPrice10)); }
	 */

	public String getProgramEffectiveDate() {
		return programEffectiveDate;
	}

	public void setProgramEffectiveDate(String programEffectiveDate) {
		this.programEffectiveDate = StringUtils.trim(programEffectiveDate);
	}

	public String getProgramClosingDate() {
		return programClosingDate;
	}

	public void setProgramClosingDate(String programClosingDate) {
		this.programClosingDate = StringUtils.trim(programClosingDate);
	}

	public String getQtyIndicator() {
		return qtyIndicator;
	}

	public void setQtyIndicator(String qtyIndicator) {
		this.qtyIndicator = StringUtils.trim(qtyIndicator);
	}

	public String getNewItemIndicator() {
		return newItemIndicator;
	}

	public void setNewItemIndicator(String newItemIndicator) {
		this.newItemIndicator = StringUtils.trim(newItemIndicator);
	}

	public String getAllocationFlag() {
		return allocationFlag;
	}

	public void setAllocationFlag(String allocationFlag) {
		this.allocationFlag = StringUtils.trim(allocationFlag);
	}

	public String getResaleIndicator() {
		return resaleIndicator == null ? null : resaleIndicator.toUpperCase();
	}

	public void setResaleIndicator(String resaleIndicator) {
		this.resaleIndicator = StringUtils.trim(resaleIndicator);
	}

	public String getAQFlag() {
		return aQFlag;
	}

	public void setAQFlag(String aQFlag) {
		this.aQFlag = StringUtils.trim(aQFlag);
	}

	public String getAvailableToSellMoreFlag() {
		return availableToSellMoreFlag;
	}

	public void setAvailableToSellMoreFlag(String availableToSellMoreFlag) {
		this.availableToSellMoreFlag = StringUtils.trim(availableToSellMoreFlag);
	}

	public String getCustomerName() {
		return customerName;
	}

	public void setCustomerName(String customerName) {
		this.customerName = StringUtils.trim(customerName);
	}

	public String getSoldToCode() {
		return soldToCode;
	}

	public void setSoldToCode(String soldToCode) {
		this.soldToCode = StringUtils.trim(soldToCode);
	}

	public String getEndCustomerName() {
		return endCustomerName;
	}

	public void setEndCustomerName(String endCustomerName) {
		this.endCustomerName = StringUtils.trim(endCustomerName);
	}

	public String getEndCustomerCode() {
		return endCustomerCode;
	}

	public void setEndCustomerCode(String endCustomerCode) {
		this.endCustomerCode = StringUtils.trim(endCustomerCode);
	}

	public String getCurrency() {
		return currency;
	}

	public void setCurrency(String currency) {
		this.currency = StringUtils.trim(currency);
	}

	public String getStartDate() {
		return startDate;
	}

	public void setStartDate(String startDate) {
		this.startDate = StringUtils.trim(startDate);
	}

	public String getVendorQuoteNo() {
		return vendorQuoteNo;
	}

	public void setVendorQuoteNo(String vendorQuoteNo) {
		this.vendorQuoteNo = StringUtils.trim(vendorQuoteNo);
	}

	public String getVendorQuoteQty() {
		return vendorQuoteQty;
	}

	public void setVendorQuoteQty(String vendorQuoteQty) {
		this.vendorQuoteQty = StringUtils.trim(vendorQuoteQty);
	}

	public String getOverriddenStr() {
		return overriddenStr;
	}

	public void setOverriddenStr(String overriddenStr) {
		this.overriddenStr = StringUtils.trim(overriddenStr);
	}

	public int getLineSeq() {
		return lineSeq;
	}

	public void setLineSeq(int lineSeq) {
		this.lineSeq = lineSeq;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = StringUtils.trim(version);
	}

	public boolean isErr() {
		return isErr;
	}

	public void setErr(boolean isErr) {
		this.isErr = isErr;
	}

	public boolean isDuplicate() {
		return isDuplicate;
	}

	public void setDuplicate(boolean isDuplicate) {
		this.isDuplicate = isDuplicate;
	}

	public String getQedCheckState() {
		return qedCheckState;
	}

	public void setQedCheckState(String qedCheckState) {
		this.qedCheckState = StringUtils.trim(qedCheckState);
	}

	public String getAction() {
		return action;
	}

	public void setAction(String action) {
		this.action = StringUtils.trim(action);
	}

	public String getPricerType() {
		return pricerType;
	}

	public void setPricerType(String pricerType) {
		this.pricerType = StringUtils.trim(pricerType);
	}

	public String getMfr() {
		return mfr;
	}

	public void setMfr(String mfr) {
		this.mfr = StringUtils.toUpperCase(StringUtils.trim(mfr));
	}

	public String getFullMfrPart() {
		return fullMfrPart;
	}

	public void setFullMfrPart(String fullMfrPart) {
		this.fullMfrPart = StringUtils.toUpperCase(StringUtils.trim(fullMfrPart));
	}

	public BizUnit getBizUnit() {
		return bizUnit;
	}

	public void setBizUnit(BizUnit bizUnit) {
		this.bizUnit = bizUnit;
	}

	public long getMetarialId() {
		return metarialId;
	}

	public void setMetarialId(long metarialId) {
		this.metarialId = metarialId;
	}

	public long getMetarialDetailId() {
		return metarialDetailId;
	}

	public void setMetarialDetailId(long metarialDetailId) {
		this.metarialDetailId = metarialDetailId;
	}

	public CostIndicator getOcostIndicator() {
		return ocostIndicator;
	}

	public void setOcostIndicator(CostIndicator ocostIndicator) {
		this.ocostIndicator = ocostIndicator;
	}

	public Material getMaterial() {
		return material;
	}

	public void setMaterial(Material material) {
		this.material = material;
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

	public boolean isMfrInDb() {
		return mfrInDb;
	}

	public void setMfrInDb(boolean mfrInDb) {
		this.mfrInDb = mfrInDb;
	}

	public long getContractPriceId() {
		return contractPriceId;
	}

	public void setContractPriceId(long contractPriceId) {
		this.contractPriceId = contractPriceId;
	}

	public Date getQuotationEffectiveDateFrom() {
		return quotationEffectiveDateFrom;
	}

	public void setQuotationEffectiveDateFrom(Date quotationEffectiveDateFrom) {
		this.quotationEffectiveDateFrom = quotationEffectiveDateFrom;
	}

	public Date getQuotationEffectiveDateTo() {
		return quotationEffectiveDateTo;
	}

	public void setQuotationEffectiveDateTo(Date quotationEffectiveDateTo) {
		this.quotationEffectiveDateTo = quotationEffectiveDateTo;
	}

	public Date getShipmentValidityFrom() {
		return shipmentValidityFrom;
	}

	public void setShipmentValidityFrom(Date shipmentValidityFrom) {
		this.shipmentValidityFrom = shipmentValidityFrom;
	}

	public Date getShipmentValidityTo() {
		return shipmentValidityTo;
	}

	public void setShipmentValidityTo(Date shipmentValidityTo) {
		this.shipmentValidityTo = shipmentValidityTo;
	}

	public String getLastUpdatedBy() {
		return lastUpdatedBy;
	}

	public void setLastUpdatedBy(String lastUpdatedBy) {
		this.lastUpdatedBy = lastUpdatedBy;
	}

	public String getLastUpdatedOn() {
		return lastUpdatedOn;
	}

	public void setLastUpdatedOn(String lastUpdatedOn) {
		this.lastUpdatedOn = lastUpdatedOn;
	}

	public boolean isCanUpdatePG2() {
		return canUpdatePG2;
	}

	public void setCanUpdatePG2(boolean canUpdatePG2) {
		this.canUpdatePG2 = canUpdatePG2;
	}

	public Date getDisQuotationEffectiveDate() {
		return disQuotationEffectiveDate;
	}

	public void setDisQuotationEffectiveDate(Date disQuotationEffectiveDate) {
		this.disQuotationEffectiveDate = disQuotationEffectiveDate;
	}

	public String getRegion() {
		return region;
	}

	public void setRegion(String region) {
		this.region = region;
	}

	public String getSalesCostType() {
		return salesCostType;
	}

	public void setSalesCostType(String salesCostType) {
		this.salesCostType = salesCostType;
	}

	public String getSalesCostFlag() {
		return salesCostFlag;
	}

	public void setSalesCostFlag(String salesCostFlag) {
		this.salesCostFlag = salesCostFlag;
	}

	public String getQtyControl() {
		return qtyControl;
	}

	public void setQtyControl(String qtyControl) {
		this.qtyControl = qtyControl;
	}

	public String getQuantityBreakSalesCost1() {
		return quantityBreakSalesCost1;
	}

	public void setQuantityBreakSalesCost1(String quantityBreakSalesCost1) {
		this.quantityBreakSalesCost1 = quantityBreakSalesCost1;
	}

	public String getQuantityBreakSuggestedResale1() {
		return quantityBreakSuggestedResale1;
	}

	public void setQuantityBreakSuggestedResale1(String quantityBreakSuggestedResale1) {
		this.quantityBreakSuggestedResale1 = quantityBreakSuggestedResale1;
	}

	public String getQuantityBreakSalesCost2() {
		return quantityBreakSalesCost2;
	}

	public void setQuantityBreakSalesCost2(String quantityBreakSalesCost2) {
		this.quantityBreakSalesCost2 = quantityBreakSalesCost2;
	}

	public String getQuantityBreakSuggestedResale2() {
		return quantityBreakSuggestedResale2;
	}

	public void setQuantityBreakSuggestedResale2(String quantityBreakSuggestedResale2) {
		this.quantityBreakSuggestedResale2 = quantityBreakSuggestedResale2;
	}

	public String getQuantityBreakSalesCost3() {
		return quantityBreakSalesCost3;
	}

	public void setQuantityBreakSalesCost3(String quantityBreakSalesCost3) {
		this.quantityBreakSalesCost3 = quantityBreakSalesCost3;
	}

	public String getQuantityBreakSuggestedResale3() {
		return quantityBreakSuggestedResale3;
	}

	public void setQuantityBreakSuggestedResale3(String quantityBreakSuggestedResale3) {
		this.quantityBreakSuggestedResale3 = quantityBreakSuggestedResale3;
	}

	public String getQuantityBreakSalesCost4() {
		return quantityBreakSalesCost4;
	}

	public void setQuantityBreakSalesCost4(String quantityBreakSalesCost4) {
		this.quantityBreakSalesCost4 = quantityBreakSalesCost4;
	}

	public String getQuantityBreakSuggestedResale4() {
		return quantityBreakSuggestedResale4;
	}

	public void setQuantityBreakSuggestedResale4(String quantityBreakSuggestedResale4) {
		this.quantityBreakSuggestedResale4 = quantityBreakSuggestedResale4;
	}

	public String getQuantityBreakSalesCost5() {
		return quantityBreakSalesCost5;
	}

	public void setQuantityBreakSalesCost5(String quantityBreakSalesCost5) {
		this.quantityBreakSalesCost5 = quantityBreakSalesCost5;
	}

	public String getQuantityBreakSuggestedResale5() {
		return quantityBreakSuggestedResale5;
	}

	public void setQuantityBreakSuggestedResale5(String quantityBreakSuggestedResale5) {
		this.quantityBreakSuggestedResale5 = quantityBreakSuggestedResale5;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + lineSeq;
		return result;
	}

	private String keyNoQed;

	// get componnet key without QED
	public String getKeyNoQed(){
		if(this.keyNoQed != null){
			return this.keyNoQed;
		}
		
		StringBuilder key =new StringBuilder("");
		PRICER_TYPE pType =  PRICER_TYPE.getPricerTypeByName(this.getPricerType());
		switch(pType) {
			case SALESCOST :
				key.append(this.getRegion()).append(this.getPricerType())
				.append(this.getMfr()).append(this.getFullMfrPart()).append(this.getCurrency());
				if(SalesCostType.ZBMD.toString().equals(this.getSalesCostType()) 
						||SalesCostType.ZBMP.toString().equals(this.getSalesCostType())) {
					key.append(PricerConstant.COMMONPARTKEY);
				}else {
					key.append(this.getSalesCostType());
				}
				break;
			case SIMPLE :
				key.append(this.getRegion()).append(this.getPricerType())
				.append(this.getMfr()).append(this.getFullMfrPart()).append(this.getCurrency())
				.append(QuoteUtil.isEmpty(this.getCostIndicator()) ? StringUtils.EMPTY:this.getCostIndicator());
				break;
			case CONTRACT :
				key.append(this.getMfr()).append(this.getFullMfrPart())
				.append(this.getCurrency()).append(this.getCostIndicator())
				.append(this.getSoldToCode()).append(this.getEndCustomerCode());
				break;
			case NORMAL :
				key.append(this.getMfr()).append(this.getFullMfrPart()).append(this.getCurrency())
				.append(this.getCostIndicator());
				break;
			case PROGRAM :
				key.append(this.getMfr()).append(this.getFullMfrPart()).append(this.getCurrency())
				.append(this.getCostIndicator()).append(this.getProgram());
				break;
			default :
				LOGGER.info(pType + " not be supported to handle.");
				break;
		}
		return keyNoQed=key.toString();
	}


	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof PricerUploadTemplateBean))
			return false;
		PricerUploadTemplateBean other = (PricerUploadTemplateBean) obj;
		if (lineSeq != other.lineSeq)
			return false;
		return true;
	}

}
