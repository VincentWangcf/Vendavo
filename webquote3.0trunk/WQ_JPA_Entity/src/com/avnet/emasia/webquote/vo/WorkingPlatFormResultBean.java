package com.avnet.emasia.webquote.vo;

import java.math.BigDecimal;
import java.util.Date;

public class WorkingPlatFormResultBean {
	
	private String alternativeQuoteNumber;
	
	private String revertVersion;
	
	private String firstRfqCode;
	
	private String pendingDay;
	
	private String status;
	
	private String validFlagStr;
	
	private String resaleIndicatorStr;
	
	private String quotedPriceStr;
	
	private String quotedQtyStr;
	
	private String qtyIndicatorStr;
	
	private String allocationFlagStr;
	
	private String aqccStr;
	
	private String quotedPartNumber;
	
	private String multiUsageFlagStr;
	
	private String vendorQuoteNumberStr;
	
	private String internalCommentStr;
	
	private String internalTransferComment;
	
	private String vendorDebitNumberStr;
	
	private String vendorQuoteQtyStr;
	
	private String priceValidityStr;
	
	private String shipmentValidityStr;
	
	private String priceListRemarks1Str;
	
	private String priceListRemarks2Str;
	
	private String priceListRemarks3Str;
	
	private String priceListRemarks4Str;
	
	private String productGroup2Str;
	
	private String description;
	
	private String costIndicatorStr;
	
	private String costStr;
	
	private String bottomceStr;
	
	private String minSellPriceStr;
	
	private String programType;
	
	private String quantityBreak;
	
	private String leadTimeStr;
	
	private String mpqStr;
	
	private String moqStr;
	
	private String movStr;
	
	private String quotationEffectiveDateStr;
	
	private String termsAndConditionsStr;
	
	private String rescheduleWindowStr;
	
	private String cancellationWindowStr;
	
	private String mfr;
	
	private String requestedPartNumber;
	
	private String requestedQty;
	
	private String eau;
	
	private String targetResale;
	
	private String quoteType;
	
	private Date submissionDate;
	
	private String yourReference;
	
	private String employeeId;
	
	private String salesman;
	
	private String team;
	
	private String soldToCustomerNameStr;
	
	private String soldToCustomerNumber;
	
	private String customerType;
	
	private String projectName;
	
	private String application;
	
	private String enquirySegment;
	
	private String designLocation;
	
	private String designInCatStr;
	
	private Integer drmsNumber;
	
	private String endCustomerCodeStr;
	
	private String shipToCustomerNumber;
	
	private String shipToCustomerFullName;
	
	private String endCustomerCode;
	
	private String loaFlagStr;
	
	private String quoteRemarks;
	
	private String competitorInformation;
	
	private String supplierDrNumber;
	
	private String ppSchedule;
	
	private String mpSchedule;
	
	private String itemRemarks;
	
	private String sprFlagStr;
	
	private String reasonForRefresh;
	
	private String formNumber;
	
	private String copyToCS;
	
	private String soldToPartyChineseName;
	
	private String systemID;
	
	private String qcRegion;
	
	private String designRegion;
	
	private String sapPartNumberStr;
	
	private String bmtFlagCode;
	
	private String drMfrNo;
	
	private BigDecimal drCost;
	
	private BigDecimal drResale;
	
	private String drMfrQuoteNo;
	
	private Date drEffectiveDate;
	
	private Date bmtSQExpiryDate;
	
	private String drComments;
	
	private Integer drQuoteQty;
	
	private BigDecimal drCostAltCurrency;
	
	private BigDecimal drResaleAltCurrency;
	
	private String drAltCurrency;
	
	private BigDecimal drExchangeRate;
	
	private Date lastBmtUpdateTime;
    //added by DamonChen
	public String salesCostPart;
	public String salesCostTypeName;
	public String salesCostStr;
	public String suggestedResaleStr;
	public String customerGroupId;

	public String orderQty;
	public String pricerMinsell;
	public String pricerSalesCost;
	public String pricerSuggestedResale;
	
	
	private String buyCurrStr;
	    
    private String dollarLinkStr;
    
    private String rfqCurrStr;
    
    private String targetResaleStr_Buy;
    
   /* private String exRateStr_Buy;
    
    private String exRateStr_Rfq;*/
    
    private String exRateStr;

    private String quotedPriceStr_Rfq;
    
    private String costStr_Rfq;
    
	public String getAlternativeQuoteNumber() {
		return alternativeQuoteNumber;
	}

	public void setAlternativeQuoteNumber(String alternativeQuoteNumber) {
		this.alternativeQuoteNumber = alternativeQuoteNumber;
	}

	public String getRevertVersion() {
		return revertVersion;
	}

	public void setRevertVersion(String revertVersion) {
		this.revertVersion = revertVersion;
	}

	public String getFirstRfqCode() {
		return firstRfqCode;
	}

	public void setFirstRfqCode(String firstRfqCode) {
		this.firstRfqCode = firstRfqCode;
	}

	public String getPendingDay() {
		return pendingDay;
	}

	public void setPendingDay(String pendingDay) {
		this.pendingDay = pendingDay;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getValidFlagStr() {
		return validFlagStr;
	}

	public void setValidFlagStr(String validFlagStr) {
		this.validFlagStr = validFlagStr;
	}

	public String getResaleIndicatorStr() {
		return resaleIndicatorStr;
	}

	public void setResaleIndicatorStr(String resaleIndicatorStr) {
		this.resaleIndicatorStr = resaleIndicatorStr;
	}

	public String getQuotedPriceStr() {
		return quotedPriceStr;
	}

	public void setQuotedPriceStr(String quotedPriceStr) {
		this.quotedPriceStr = quotedPriceStr;
	}

	public String getQuotedQtyStr() {
		return quotedQtyStr;
	}

	public void setQuotedQtyStr(String quotedQtyStr) {
		this.quotedQtyStr = quotedQtyStr;
	}

	public String getQtyIndicatorStr() {
		return qtyIndicatorStr;
	}

	public void setQtyIndicatorStr(String qtyIndicatorStr) {
		this.qtyIndicatorStr = qtyIndicatorStr;
	}

	public String getAllocationFlagStr() {
		return allocationFlagStr;
	}

	public void setAllocationFlagStr(String allocationFlagStr) {
		this.allocationFlagStr = allocationFlagStr;
	}

	public String getAqccStr() {
		return aqccStr;
	}

	public void setAqccStr(String aqccStr) {
		this.aqccStr = aqccStr;
	}

	public String getQuotedPartNumber() {
		return quotedPartNumber;
	}

	public void setQuotedPartNumber(String quotedPartNumber) {
		this.quotedPartNumber = quotedPartNumber;
	}

	public String getMultiUsageFlagStr() {
		return multiUsageFlagStr;
	}

	public void setMultiUsageFlagStr(String multiUsageFlagStr) {
		this.multiUsageFlagStr = multiUsageFlagStr;
	}

	public String getVendorQuoteNumberStr() {
		return vendorQuoteNumberStr;
	}

	public void setVendorQuoteNumberStr(String vendorQuoteNumberStr) {
		this.vendorQuoteNumberStr = vendorQuoteNumberStr;
	}

	public String getInternalCommentStr() {
		return internalCommentStr;
	}

	public void setInternalCommentStr(String internalCommentStr) {
		this.internalCommentStr = internalCommentStr;
	}

	public String getInternalTransferComment() {
		return internalTransferComment;
	}

	public void setInternalTransferComment(String internalTransferComment) {
		this.internalTransferComment = internalTransferComment;
	}

	public String getVendorDebitNumberStr() {
		return vendorDebitNumberStr;
	}

	public void setVendorDebitNumberStr(String vendorDebitNumberStr) {
		this.vendorDebitNumberStr = vendorDebitNumberStr;
	}

	public String getVendorQuoteQtyStr() {
		return vendorQuoteQtyStr;
	}

	public void setVendorQuoteQtyStr(String vendorQuoteQtyStr) {
		this.vendorQuoteQtyStr = vendorQuoteQtyStr;
	}

	public String getPriceValidityStr() {
		return priceValidityStr;
	}

	public void setPriceValidityStr(String priceValidityStr) {
		this.priceValidityStr = priceValidityStr;
	}

	public String getShipmentValidityStr() {
		return shipmentValidityStr;
	}

	public void setShipmentValidityStr(String shipmentValidityStr) {
		this.shipmentValidityStr = shipmentValidityStr;
	}

	public String getPriceListRemarks1Str() {
		return priceListRemarks1Str;
	}

	public void setPriceListRemarks1Str(String priceListRemarks1Str) {
		this.priceListRemarks1Str = priceListRemarks1Str;
	}

	public String getPriceListRemarks2Str() {
		return priceListRemarks2Str;
	}

	public void setPriceListRemarks2Str(String priceListRemarks2Str) {
		this.priceListRemarks2Str = priceListRemarks2Str;
	}

	public String getPriceListRemarks3Str() {
		return priceListRemarks3Str;
	}

	public void setPriceListRemarks3Str(String priceListRemarks3Str) {
		this.priceListRemarks3Str = priceListRemarks3Str;
	}

	public String getPriceListRemarks4Str() {
		return priceListRemarks4Str;
	}

	public void setPriceListRemarks4Str(String priceListRemarks4Str) {
		this.priceListRemarks4Str = priceListRemarks4Str;
	}

	public String getProductGroup2Str() {
		return productGroup2Str;
	}

	public void setProductGroup2Str(String productGroup2Str) {
		this.productGroup2Str = productGroup2Str;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getCostIndicatorStr() {
		return costIndicatorStr;
	}

	public void setCostIndicatorStr(String costIndicatorStr) {
		this.costIndicatorStr = costIndicatorStr;
	}

	public String getCostStr() {
		return costStr;
	}

	public void setCostStr(String costStr) {
		this.costStr = costStr;
	}

	public String getBottomceStr() {
		return bottomceStr;
	}

	public void setBottomceStr(String bottomceStr) {
		this.bottomceStr = bottomceStr;
	}

	public String getMinSellPriceStr() {
		return minSellPriceStr;
	}

	public void setMinSellPriceStr(String minSellPriceStr) {
		this.minSellPriceStr = minSellPriceStr;
	}

	public String getProgramType() {
		return programType;
	}

	public void setProgramType(String programType) {
		this.programType = programType;
	}

	public String getQuantityBreak() {
		return quantityBreak;
	}

	public void setQuantityBreak(String quantityBreak) {
		this.quantityBreak = quantityBreak;
	}

	public String getLeadTimeStr() {
		return leadTimeStr;
	}

	public void setLeadTimeStr(String leadTimeStr) {
		this.leadTimeStr = leadTimeStr;
	}

	public String getMpqStr() {
		return mpqStr;
	}

	public void setMpqStr(String mpqStr) {
		this.mpqStr = mpqStr;
	}

	public String getMoqStr() {
		return moqStr;
	}

	public void setMoqStr(String moqStr) {
		this.moqStr = moqStr;
	}

	public String getMovStr() {
		return movStr;
	}

	public void setMovStr(String movStr) {
		this.movStr = movStr;
	}

	public String getQuotationEffectiveDateStr() {
		return quotationEffectiveDateStr;
	}

	public void setQuotationEffectiveDateStr(String quotationEffectiveDateStr) {
		this.quotationEffectiveDateStr = quotationEffectiveDateStr;
	}

	public String getTermsAndConditionsStr() {
		return termsAndConditionsStr;
	}

	public void setTermsAndConditionsStr(String termsAndConditionsStr) {
		this.termsAndConditionsStr = termsAndConditionsStr;
	}

	public String getRescheduleWindowStr() {
		return rescheduleWindowStr;
	}

	public void setRescheduleWindowStr(String rescheduleWindowStr) {
		this.rescheduleWindowStr = rescheduleWindowStr;
	}

	public String getCancellationWindowStr() {
		return cancellationWindowStr;
	}

	public void setCancellationWindowStr(String cancellationWindowStr) {
		this.cancellationWindowStr = cancellationWindowStr;
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

	public String getRequestedQty() {
		return requestedQty;
	}

	public void setRequestedQty(String requestedQty) {
		this.requestedQty = requestedQty;
	}

	public String getEau() {
		return eau;
	}

	public void setEau(String eau) {
		this.eau = eau;
	}

	public String getTargetResale() {
		return targetResale;
	}

	public void setTargetResale(String targetResale) {
		this.targetResale = targetResale;
	}

	public String getQuoteType() {
		return quoteType;
	}

	public void setQuoteType(String quoteType) {
		this.quoteType = quoteType;
	}

	public Date getSubmissionDate() {
		return submissionDate;
	}

	public void setSubmissionDate(Date submissionDate) {
		this.submissionDate = submissionDate;
	}

	public String getYourReference() {
		return yourReference;
	}

	public void setYourReference(String yourReference) {
		this.yourReference = yourReference;
	}

	public String getEmployeeId() {
		return employeeId;
	}

	public void setEmployeeId(String employeeId) {
		this.employeeId = employeeId;
	}

	public String getSalesman() {
		return salesman;
	}

	public void setSalesman(String salesman) {
		this.salesman = salesman;
	}

	public String getTeam() {
		return team;
	}

	public void setTeam(String team) {
		this.team = team;
	}

	public String getSoldToCustomerNameStr() {
		return soldToCustomerNameStr;
	}

	public void setSoldToCustomerNameStr(String soldToCustomerNameStr) {
		this.soldToCustomerNameStr = soldToCustomerNameStr;
	}

	public String getSoldToCustomerNumber() {
		return soldToCustomerNumber;
	}

	public void setSoldToCustomerNumber(String soldToCustomerNumber) {
		this.soldToCustomerNumber = soldToCustomerNumber;
	}

	public String getCustomerType() {
		return customerType;
	}

	public void setCustomerType(String customerType) {
		this.customerType = customerType;
	}

	public String getProjectName() {
		return projectName;
	}

	public void setProjectName(String projectName) {
		this.projectName = projectName;
	}

	public String getApplication() {
		return application;
	}

	public void setApplication(String application) {
		this.application = application;
	}

	public String getEnquirySegment() {
		return enquirySegment;
	}

	public void setEnquirySegment(String enquirySegment) {
		this.enquirySegment = enquirySegment;
	}

	public String getDesignLocation() {
		return designLocation;
	}

	public void setDesignLocation(String designLocation) {
		this.designLocation = designLocation;
	}

	public String getDesignInCatStr() {
		return designInCatStr;
	}

	public void setDesignInCatStr(String designInCatStr) {
		this.designInCatStr = designInCatStr;
	}

	public Integer getDrmsNumber() {
		return drmsNumber;
	}

	public void setDrmsNumber(Integer drmsNumber) {
		this.drmsNumber = drmsNumber;
	}

	public String getEndCustomerCodeStr() {
		return endCustomerCodeStr;
	}

	public void setEndCustomerCodeStr(String endCustomerCodeStr) {
		this.endCustomerCodeStr = endCustomerCodeStr;
	}

	public String getShipToCustomerNumber() {
		return shipToCustomerNumber;
	}

	public void setShipToCustomerNumber(String shipToCustomerNumber) {
		this.shipToCustomerNumber = shipToCustomerNumber;
	}

	public String getShipToCustomerFullName() {
		return shipToCustomerFullName;
	}

	public void setShipToCustomerFullName(String shipToCustomerFullName) {
		this.shipToCustomerFullName = shipToCustomerFullName;
	}

	public String getEndCustomerCode() {
		return endCustomerCode;
	}

	public void setEndCustomerCode(String endCustomerCode) {
		this.endCustomerCode = endCustomerCode;
	}

	public String getLoaFlagStr() {
		return loaFlagStr;
	}

	public void setLoaFlagStr(String loaFlagStr) {
		this.loaFlagStr = loaFlagStr;
	}

	public String getQuoteRemarks() {
		return quoteRemarks;
	}

	public void setQuoteRemarks(String quoteRemarks) {
		this.quoteRemarks = quoteRemarks;
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

	public String getPpSchedule() {
		return ppSchedule;
	}

	public void setPpSchedule(String ppSchedule) {
		this.ppSchedule = ppSchedule;
	}

	public String getMpSchedule() {
		return mpSchedule;
	}

	public void setMpSchedule(String mpSchedule) {
		this.mpSchedule = mpSchedule;
	}

	public String getItemRemarks() {
		return itemRemarks;
	}

	public void setItemRemarks(String itemRemarks) {
		this.itemRemarks = itemRemarks;
	}

	public String getSprFlagStr() {
		return sprFlagStr;
	}

	public void setSprFlagStr(String sprFlagStr) {
		this.sprFlagStr = sprFlagStr;
	}

	public String getReasonForRefresh() {
		return reasonForRefresh;
	}

	public void setReasonForRefresh(String reasonForRefresh) {
		this.reasonForRefresh = reasonForRefresh;
	}

	public String getFormNumber() {
		return formNumber;
	}

	public void setFormNumber(String formNumber) {
		this.formNumber = formNumber;
	}

	public String getCopyToCS() {
		return copyToCS;
	}

	public void setCopyToCS(String copyToCS) {
		this.copyToCS = copyToCS;
	}

	public String getSoldToPartyChineseName() {
		return soldToPartyChineseName;
	}

	public void setSoldToPartyChineseName(String soldToPartyChineseName) {
		this.soldToPartyChineseName = soldToPartyChineseName;
	}

	public String getSystemID() {
		return systemID;
	}

	public void setSystemID(String systemID) {
		this.systemID = systemID;
	}

	public String getQcRegion() {
		return qcRegion;
	}

	public void setQcRegion(String qcRegion) {
		this.qcRegion = qcRegion;
	}

	public String getDesignRegion() {
		return designRegion;
	}

	public void setDesignRegion(String designRegion) {
		this.designRegion = designRegion;
	}

	public String getSapPartNumberStr() {
		return sapPartNumberStr;
	}

	public void setSapPartNumberStr(String sapPartNumberStr) {
		this.sapPartNumberStr = sapPartNumberStr;
	}

	public String getBmtFlagCode() {
		return bmtFlagCode;
	}

	public void setBmtFlagCode(String bmtFlagCode) {
		this.bmtFlagCode = bmtFlagCode;
	}

	public String getDrMfrNo() {
		return drMfrNo;
	}

	public void setDrMfrNo(String drMfrNo) {
		this.drMfrNo = drMfrNo;
	}

	public BigDecimal getDrCost() {
		return drCost;
	}

	public void setDrCost(BigDecimal drCost) {
		this.drCost = drCost;
	}

	public BigDecimal getDrResale() {
		return drResale;
	}

	public void setDrResale(BigDecimal drResale) {
		this.drResale = drResale;
	}

	public String getDrMfrQuoteNo() {
		return drMfrQuoteNo;
	}

	public void setDrMfrQuoteNo(String drMfrQuoteNo) {
		this.drMfrQuoteNo = drMfrQuoteNo;
	}

	public Date getDrEffectiveDate() {
		return drEffectiveDate;
	}

	public void setDrEffectiveDate(Date drEffectiveDate) {
		this.drEffectiveDate = drEffectiveDate;
	}

	public Date getBmtSQExpiryDate() {
		return bmtSQExpiryDate;
	}

	public void setBmtSQExpiryDate(Date bmtSQExpiryDate) {
		this.bmtSQExpiryDate = bmtSQExpiryDate;
	}

	public String getDrComments() {
		return drComments;
	}

	public void setDrComments(String drComments) {
		this.drComments = drComments;
	}

	public Integer getDrQuoteQty() {
		return drQuoteQty;
	}

	public void setDrQuoteQty(Integer drQuoteQty) {
		this.drQuoteQty = drQuoteQty;
	}

	public BigDecimal getDrCostAltCurrency() {
		return drCostAltCurrency;
	}

	public void setDrCostAltCurrency(BigDecimal drCostAltCurrency) {
		this.drCostAltCurrency = drCostAltCurrency;
	}

	public BigDecimal getDrResaleAltCurrency() {
		return drResaleAltCurrency;
	}

	public void setDrResaleAltCurrency(BigDecimal drResaleAltCurrency) {
		this.drResaleAltCurrency = drResaleAltCurrency;
	}

	public String getDrAltCurrency() {
		return drAltCurrency;
	}

	public void setDrAltCurrency(String drAltCurrency) {
		this.drAltCurrency = drAltCurrency;
	}

	public BigDecimal getDrExchangeRate() {
		return drExchangeRate;
	}

	public void setDrExchangeRate(BigDecimal drExchangeRate) {
		this.drExchangeRate = drExchangeRate;
	}

	public Date getLastBmtUpdateTime() {
		return lastBmtUpdateTime;
	}

	public void setLastBmtUpdateTime(Date lastBmtUpdateTime) {
		this.lastBmtUpdateTime = lastBmtUpdateTime;
	}

	public String getSalesCostPart() {
		return salesCostPart;
	}

	public void setSalesCostPart(String salesCostPart) {
		this.salesCostPart = salesCostPart;
	}

	public String getSalesCostTypeName() {
		return salesCostTypeName;
	}

	public void setSalesCostTypeName(String salesCostTypeName) {
		this.salesCostTypeName = salesCostTypeName;
	}

	public String getSalesCostStr() {
		return salesCostStr;
	}

	public void setSalesCostStr(String salesCostStr) {
		this.salesCostStr = salesCostStr;
	}

	public String getSuggestedResaleStr() {
		return suggestedResaleStr;
	}

	public void setSuggestedResaleStr(String suggestedResaleStr) {
		this.suggestedResaleStr = suggestedResaleStr;
	}

	public String getCustomerGroupId() {
		return customerGroupId;
	}

	public void setCustomerGroupId(String customerGroupId) {
		this.customerGroupId = customerGroupId;
	}

	public String getOrderQty() {
		return orderQty;
	}

	public void setOrderQty(String orderQty) {
		this.orderQty = orderQty;
	}

	public String getPricerMinsell() {
		return pricerMinsell;
	}

	public void setPricerMinsell(String pricerMinsell) {
		this.pricerMinsell = pricerMinsell;
	}

	public String getPricerSalesCost() {
		return pricerSalesCost;
	}

	public void setPricerSalesCost(String pricerSalesCost) {
		this.pricerSalesCost = pricerSalesCost;
	}

	public String getPricerSuggestedResale() {
		return pricerSuggestedResale;
	}

	public void setPricerSuggestedResale(String pricerSuggestedResale) {
		this.pricerSuggestedResale = pricerSuggestedResale;
	}

	public String getBuyCurrStr() {
		return buyCurrStr;
	}

	public void setBuyCurrStr(String buyCurrStr) {
		this.buyCurrStr = buyCurrStr;
	}

	public String getDollarLinkStr() {
		return dollarLinkStr;
	}

	public void setDollarLinkStr(String dollarLinkStr) {
		this.dollarLinkStr = dollarLinkStr;
	}

	public String getRfqCurrStr() {
		return rfqCurrStr;
	}

	public void setRfqCurrStr(String rfqCurrStr) {
		this.rfqCurrStr = rfqCurrStr;
	}

	public String getTargetResaleStr_Buy() {
		return targetResaleStr_Buy;
	}

	public void setTargetResaleStr_Buy(String targetResaleStr_Buy) {
		this.targetResaleStr_Buy = targetResaleStr_Buy;
	}

	/*public String getExRateStr_Buy() {
		return exRateStr_Buy;
	}

	public void setExRateStr_Buy(String exRateStr_Buy) {
		this.exRateStr_Buy = exRateStr_Buy;
	}

	public String getExRateStr_Rfq() {
		return exRateStr_Rfq;
	}

	public void setExRateStr_Rfq(String exRateStr_Rfq) {
		this.exRateStr_Rfq = exRateStr_Rfq;
	}*/

	public String getExRateStr() {
		return exRateStr;
	}

	public void setExRateStr(String exRateStr) {
		this.exRateStr = exRateStr;
	}

	public String getQuotedPriceStr_Rfq() {
		return quotedPriceStr_Rfq;
	}

	public void setQuotedPriceStr_Rfq(String quotedPriceStr_Rfq) {
		this.quotedPriceStr_Rfq = quotedPriceStr_Rfq;
	}

	public String getCostStr_Rfq() {
		return costStr_Rfq;
	}

	public void setCostStr_Rfq(String costStr_Rfq) {
		this.costStr_Rfq = costStr_Rfq;
	}
	
	
	
}
