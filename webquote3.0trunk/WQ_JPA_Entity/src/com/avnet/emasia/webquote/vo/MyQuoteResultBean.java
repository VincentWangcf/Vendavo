package com.avnet.emasia.webquote.vo;

import java.util.Date;

/**
 * @author 906893
 * @createdOn 20130424
 */

public class MyQuoteResultBean {
	
	

	private String avnetQuotedQty;
	private String cost;
	private String mov;
	
	private String qtyIndicator;
	private String resalesMax;
	private String resalesMin;
	
	private String drmsAuthorizedGP;
	private String authorizedGPChangeReason;
	private String remarkChangeReason;
	private String designInCat;
	private String quoteType;

	private String targetMargin;
	private String resaleMargin;
	private String internalTransferComment;
	
	private String quoteNumber;
	private String formNo;
	private String readyForOrder;
	private String readyForOrderNot;
	private String mfr;
	private String quotedPn;
	private String sapPartNumber;
	private String soldToCustomerName;
	private String solToCustomerChineseName;
	private String soldToCustomerCode;
	private String endCustomer;
	private String endCustomerCode;
	private String avnetQuotePrice;
	private String pmoq;
	private String costIndicator;
	
	private String mpq;
	private String moq;
	private String leadTime;
	private String multiUsage;	 
	private String vendorDebit;
	private String vendorQuote;
	private String vendorQty;
	private String resaleIndicator;
	private String cancellWindow;
	private String reshedulingWindow;
	private String termsAndConditions;
	private String allocationFlog;
	private String avnetQCComment;
	private String internalComment;
	private String quoteStage;
	private String rfqStatus;
	private String employeeName;
	private String salesman;
	private String team;
	private String shipToParty;
	private String shipToCode;
	private String customerType;
	private String materialType;
	private String program;
	private String enquirySegment;
	private String projectName;
	private String application;
	private String designLocation;
	private String avnetRegionalDemandCreation;
	private String drms;
	private String mpSchedule;
	private String ppSchedule;
	private String loa;

	private String remarks;
	private String yourReference;
	private String bmtChecked;

	private String copyToCs;
	private String requestedPn;
	private String spr;
	private String productGroup;
	private String requiredQty;
	private String eau;
	private String targetResale;
	private String supplierDr;
	private String competitorInformation;
	private String itemRemarks;
	private String reasonForRefresh;
	private String remarksToCustomer;
	private String firstRfqCode;
	private String revertVersion;
	
	private String createdBy;
	private String quotedBy;
	private String finalQuotationPrice;
	private String referenceMargin;
	
	private String usedFlag;
	
	
	private String exRateTo;
	private String currTo;
	
	/****BMT new fields*****/
	private String bmtMfrDrNo;
	private String bmtSuggestCost;
	private String bmtSuggestResale;
	private String bmtSuggestMargin;
	private String bmtMfrQuoteNo;
	
	private String bmtComments;
	private String bmtQuoteQty;
	private String bmtSugCostAltCurrency;
	private String bmtSugResaleAltCurrency;
	private String bmtCurrency;
	private String bmtExRateAltCurrency;
	private String bmtFlag;
	private String bmtPendingAt;
	
	private String lastUpdateName;
	private String lastUpdateBmtName;
	
	
	private String designRegion;
	
	private String dpReferenceId;
	
	private String dpReferenceLineId;
	
	
	
	private String balanceUnconsumedQty;
	
	private String priceValidity;
	private Date quoteEffectiveDate;
	private Date quoteExpiryDate;
	private Date poExpiryDate;
	private Date shipmentValidity;
	private Date drExpiryDate;
	private Date sendOutTime;
	private Date uploadTime;
	private Date bmtsqEffciveDate;
	private Date bmtSQexpiryDate;
	private Date lastBmtUpdate;
	  
	//for Sales Cost Project

	private String salesCostFlag;
	
	private String salesCostTypeName;

	private String salesCost;

	private String suggestedResale;

	private String customerGroupId;

	//JP Multi Currency Project (by whwong)
	private String resaleRange;
	private String quotedPriceRFQCur;
	private String targetResaleBuyCur;
	private String mqsCurExc;
	private String mqsExcRate;
	private String finalCurr;
	private String mqsExcRateFnl;
	private String dLinkFlagStr;
	private String rateRemarks;
	private String buyCurr;

	private String salesCostRFQCur;
	private String suggestedResaleRFQCur;

	
	public String getBuyCurr() {
		return buyCurr;
	}
	public void setBuyCurr(String buyCurr) {
		this.buyCurr = buyCurr;
	}
	public String getMqsExcRateFnl() {
		return mqsExcRateFnl;
	}
	public void setMqsExcRateFnl(String mqsExcRateFnl) {
		this.mqsExcRateFnl = mqsExcRateFnl;
	}
	public String getDLinkFlagStr() {
		return dLinkFlagStr;
	}
	public void setDLinkFlagStr(String dLinkFlagStr) {
		this.dLinkFlagStr = dLinkFlagStr;
	}
	public String getRateRemarks() {
		return rateRemarks;
	}
	public void setRateRemarks(String rateRemarks) {
		this.rateRemarks = rateRemarks;
	}
	public String getFinalCurr() {
		return finalCurr;
	}
	public void setFinalCurr(String finalCurr) {
		this.finalCurr = finalCurr;
	}
	public String getMqsCurExc() {
		return mqsCurExc;
	}
	public void setMqsCurExc(String mqsCurExc) {
		this.mqsCurExc = mqsCurExc;
	}
	public String getMqsExcRate() {
		return mqsExcRate;
	}
	public void setMqsExcRate(String mqsExcRate) {
		this.mqsExcRate = mqsExcRate;
	}
	public String getResaleRange() {
		return resaleRange;
	}
	public void setResaleRange(String resaleRange) {
		this.resaleRange = resaleRange;
	}
	public String getQuotedPriceRFQCur() {
		return quotedPriceRFQCur;
	}
	public void setQuotedPriceRFQCur(String quotedPriceRFQCur) {
		this.quotedPriceRFQCur = quotedPriceRFQCur;
	}
	public String getTargetResaleBuyCur() {
		return targetResaleBuyCur;
	}
	public void setTargetResaleBuyCur(String targetResaleBuyCur) {
		this.targetResaleBuyCur = targetResaleBuyCur;
	}

	public String getSalesCostRFQCur() {
		return salesCostRFQCur;
	}
	public void setSalesCostRFQCur(String salesCostRFQCur) {
		this.salesCostRFQCur = salesCostRFQCur;
	}
	public String getSuggestedResaleRFQCur() {
		return suggestedResaleRFQCur;
	}
	public void setSuggestedResaleRFQCur(String suggestedResaleRFQCur) {
		this.suggestedResaleRFQCur = suggestedResaleRFQCur;
	}

	
	public String getDrmsAuthorizedGP()
	{
		return drmsAuthorizedGP;
	}
	public void setDrmsAuthorizedGP(String drmsAuthorizedGP)
	{
		this.drmsAuthorizedGP = drmsAuthorizedGP;
	}
	public String getAuthorizedGPChangeReason()
	{
		return authorizedGPChangeReason;
	}
	public void setAuthorizedGPChangeReason(String authorizedGPChangeReason)
	{
		this.authorizedGPChangeReason = authorizedGPChangeReason;
	}
	public String getRemarkChangeReason()
	{
		return remarkChangeReason;
	}
	public void setRemarkChangeReason(String remarkChangeReason)
	{
		this.remarkChangeReason = remarkChangeReason;
	}
	
	public String getReferenceMargin() {
		return referenceMargin;
	}
	public void setReferenceMargin(String referenceMargin) {
		this.referenceMargin = referenceMargin;
	}
	public String getResaleMargin() {
		return resaleMargin;
	}
	public void setResaleMargin(String resaleMargin) {
		this.resaleMargin = resaleMargin;
	}
	public String getTargetMargin() {
		return targetMargin;
	}
	public void setTargetMargin(String targetMargin) {
		this.targetMargin = targetMargin;
	}
	public String getFinalQuotationPrice() {
		return finalQuotationPrice;
	}
	public void setFinalQuotationPrice(String finalQuotationPrice) {
		this.finalQuotationPrice = finalQuotationPrice;
	}
	
	public String getEndCustomerCode() {
		return endCustomerCode;
	}
	public void setEndCustomerCode(String endCustomerCode) {
		this.endCustomerCode = endCustomerCode;
	}
	public String getAvnetQuotePrice() {
		return avnetQuotePrice;
	}
	public void setAvnetQuotePrice(String avnetQuotePrice) {
		this.avnetQuotePrice = avnetQuotePrice;
	}
	public String getPmoq() {
		return pmoq;
	}
	public void setPmoq(String pmoq) {
		this.pmoq = pmoq;
	}
	public String getCostIndicator() {
		return costIndicator;
	}
	public void setCostIndicator(String costIndicator) {
		this.costIndicator = costIndicator;
	}
	
	public String getEndCustomer() {
		return endCustomer;
	}
	public void setEndCustomer(String endCustomer) {
		this.endCustomer = endCustomer;
	}
	public String getQuoteNumber() {
		return quoteNumber;
	}
	public void setQuoteNumber(String quoteNumber) {
		this.quoteNumber = quoteNumber;
	}
	public String getFormNo() {
		return formNo;
	}
	public void setFormNo(String formNo) {
		this.formNo = formNo;
	}
	public String getReadyForOrder() {
		return readyForOrder;
	}
	public void setReadyForOrder(String readyForOrder) {
		this.readyForOrder = readyForOrder;
	}
	public String getReadyForOrderNot() {
		return readyForOrderNot;
	}
	public void setReadyForOrderNot(String readyForOrderNot) {
		this.readyForOrderNot = readyForOrderNot;
	}
	public String getMfr() {
		return mfr;
	}
	public void setMfr(String mfr) {
		this.mfr = mfr;
	}
	public String getQuotedPn() {
		return quotedPn;
	}
	public void setQuotedPn(String quotedPn) {
		this.quotedPn = quotedPn;
	}
	public String getSapPartNumber() {
		return sapPartNumber;
	}
	public void setSapPartNumber(String sapPartNumber) {
		this.sapPartNumber = sapPartNumber;
	}
	public String getSoldToCustomerName() {
		return soldToCustomerName;
	}
	public void setSoldToCustomerName(String soldToCustomerName) {
		this.soldToCustomerName = soldToCustomerName;
	}
	
	public String getSolToCustomerChineseName()
	{
		return solToCustomerChineseName;
	}
	public void setSolToCustomerChineseName(String solToCustomerChineseName)
	{
		this.solToCustomerChineseName = solToCustomerChineseName;
	}
	public String getSoldToCustomerCode() {
		return soldToCustomerCode;
	}
	public void setSoldToCustomerCode(String soldToCustomerCode) {
		this.soldToCustomerCode = soldToCustomerCode;
	}
	
	public String getMpq() {
		return mpq;
	}
	public void setMpq(String mpq) {
		this.mpq = mpq;
	}
	public String getMoq() {
		return moq;
	}
	public void setMoq(String moq) {
		this.moq = moq;
	}
	public String getLeadTime() {
		return leadTime;
	}
	public void setLeadTime(String leadTime) {
		this.leadTime = leadTime;
	}
	public String getMultiUsage() {
		return multiUsage;
	}
	public void setMultiUsage(String multiUsage) {
		this.multiUsage = multiUsage;
	}
	public String getVendorDebit() {
		return vendorDebit;
	}
	public void setVendorDebit(String vendorDebit) {
		this.vendorDebit = vendorDebit;
	}
	public String getVendorQuote() {
		return vendorQuote;
	}
	public void setVendorQuote(String vendorQuote) {
		this.vendorQuote = vendorQuote;
	}
	public String getVendorQty() {
		return vendorQty;
	}
	public void setVendorQty(String vendorQty) {
		this.vendorQty = vendorQty;
	}
	public String getResaleIndicator() {
		return resaleIndicator;
	}
	public void setResaleIndicator(String resaleIndicator) {
		this.resaleIndicator = resaleIndicator;
	}
	public String getCancellWindow() {
		return cancellWindow;
	}
	public void setCancellWindow(String cancellWindow) {
		this.cancellWindow = cancellWindow;
	}
	public String getReshedulingWindow() {
		return reshedulingWindow;
	}
	public void setReshedulingWindow(String reshedulingWindow) {
		this.reshedulingWindow = reshedulingWindow;
	}
	public String getTermsAndConditions() {
		return termsAndConditions;
	}
	public void setTermsAndConditions(String termsAndConditions) {
		this.termsAndConditions = termsAndConditions;
	}
	public String getAllocationFlog() {
		return allocationFlog;
	}
	public void setAllocationFlog(String allocationFlog) {
		this.allocationFlog = allocationFlog;
	}
	public String getAvnetQCComment() {
		return avnetQCComment;
	}
	public void setAvnetQCComment(String avnetQCComment) {
		this.avnetQCComment = avnetQCComment;
	}
	public String getInternalComment() {
		return internalComment;
	}
	public void setInternalComment(String internalComment) {
		this.internalComment = internalComment;
	}
	public String getQuoteStage() {
		return quoteStage;
	}
	public void setQuoteStage(String quoteStage) {
		this.quoteStage = quoteStage;
	}
	public String getRfqStatus() {
		return rfqStatus;
	}
	public void setRfqStatus(String rfqStatus) {
		this.rfqStatus = rfqStatus;
	}
	public String getEmployeeName() {
		return employeeName;
	}
	public void setEmployeeName(String employeeName) {
		this.employeeName = employeeName;
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
	public String getShipToParty() {
		return shipToParty;
	}
	public void setShipToParty(String shipToParty) {
		this.shipToParty = shipToParty;
	}
	public String getShipToCode() {
		return shipToCode;
	}
	public void setShipToCode(String shipToCode) {
		this.shipToCode = shipToCode;
	}
	public String getCustomerType() {
		return customerType;
	}
	public void setCustomerType(String customerType) {
		this.customerType = customerType;
	}
	public String getMaterialType() {
		return materialType;
	}
	public void setMaterialType(String materialType) {
		this.materialType = materialType;
	}
	public String getProgram() {
		return program;
	}
	public void setProgram(String program) {
		this.program = program;
	}
	public String getEnquirySegment() {
		return enquirySegment;
	}
	public void setEnquirySegment(String enquirySegment) {
		this.enquirySegment = enquirySegment;
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
	public String getDesignLocation() {
		return designLocation;
	}
	public void setDesignLocation(String designLocation) {
		this.designLocation = designLocation;
	}
	public String getAvnetRegionalDemandCreation() {
		return avnetRegionalDemandCreation;
	}
	public void setAvnetRegionalDemandCreation(String avnetRegionalDemandCreation) {
		this.avnetRegionalDemandCreation = avnetRegionalDemandCreation;
	}
	public String getDrms() {
		return drms;
	}
	public void setDrms(String drms) {
		this.drms = drms;
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
	public String getLoa() {
		return loa;
	}
	public void setLoa(String loa) {
		this.loa = loa;
	}

	public String getRemarks() {
		return remarks;
	}
	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}
	public String getYourReference() {
		return yourReference;
	}
	public void setYourReference(String yourReference) {
		this.yourReference = yourReference;
	}
	public String getBmtChecked() {
		return bmtChecked;
	}
	public void setBmtChecked(String bmtChecked) {
		this.bmtChecked = bmtChecked;
	}

	public String getCopyToCs() {
		return copyToCs;
	}
	public void setCopyToCs(String copyToCs) {
		this.copyToCs = copyToCs;
	}
	public String getRequestedPn() {
		return requestedPn;
	}
	public void setRequestedPn(String requestedPn) {
		this.requestedPn = requestedPn;
	}
	public String getSpr() {
		return spr;
	}
	public void setSpr(String spr) {
		this.spr = spr;
	}
	public String getProductGroup() {
		return productGroup;
	}
	public void setProductGroup(String productGroup) {
		this.productGroup = productGroup;
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
		this.eau = eau;
	}
	public String getTargetResale() {
		return targetResale;
	}
	public void setTargetResale(String targetResale) {
		this.targetResale = targetResale;
	}
	public String getSupplierDr() {
		return supplierDr;
	}
	public void setSupplierDr(String supplierDr) {
		this.supplierDr = supplierDr;
	}
	public String getCompetitorInformation() {
		return competitorInformation;
	}
	public void setCompetitorInformation(String competitorInformation) {
		this.competitorInformation = competitorInformation;
	}
	public String getItemRemarks() {
		return itemRemarks;
	}
	public void setItemRemarks(String itemRemarks) {
		this.itemRemarks = itemRemarks;
	}
	public String getReasonForRefresh() {
		return reasonForRefresh;
	}
	public void setReasonForRefresh(String reasonForRefresh) {
		this.reasonForRefresh = reasonForRefresh;
	}
	public String getRemarksToCustomer() {
		return remarksToCustomer;
	}
	public void setRemarksToCustomer(String remarksToCustomer) {
		this.remarksToCustomer = remarksToCustomer;
	}
	public String getFirstRfqCode() {
		return firstRfqCode;
	}
	public void setFirstRfqCode(String firstRfqCode) {
		this.firstRfqCode = firstRfqCode;
	}
	public String getRevertVersion() {
		return revertVersion;
	}
	public void setRevertVersion(String revertVersion) {
		this.revertVersion = revertVersion;
	}
	
	
	public String getCreatedBy() {
		return createdBy;
	}
	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}
	public String getQuotedBy() {
		return quotedBy;
	}
	public void setQuotedBy(String quotedBy) {
		this.quotedBy = quotedBy;
	}
	
	private String sapSoldToType;

	public String getSapSoldToType() {
		return sapSoldToType;
	}
	public void setSapSoldToType(String sapSoldToType) {
		this.sapSoldToType = sapSoldToType;
	}
	public String getDesignInCat() {
		return designInCat;
	}
	public void setDesignInCat(String designInCat) {
		this.designInCat = designInCat;
	}
	public String getQuoteType() {
		return quoteType;
	}
	public void setQuoteType(String quoteType) {
		this.quoteType = quoteType;
	}
	public String getExRateTo() {
		return exRateTo;
	}
	public void setExRateTo(String exRateTo) {
		this.exRateTo = exRateTo;
	}
	public String getCurrTo() {
		return currTo;
	}
	public void setCurrTo(String currTo) {
		this.currTo = currTo;
	}
	public String getUsedFlag() {
		return usedFlag;
	}
	public void setUsedFlag(String usedFlag) {
		this.usedFlag = usedFlag;
	}
	
	
	public void setAvnetQuotedQty(String avnetQuotedQty) {
		this.avnetQuotedQty = avnetQuotedQty;
	}
	public String getCost() {
		return cost;
	}
	public void setCost(String cost) {
		this.cost = cost;
	}
	public String getMov() {
		return mov;
	}
	public void setMov(String mov) {
		this.mov = mov;
	}
	
	public String getQtyIndicator() {
		return qtyIndicator;
	}
	public void setQtyIndicator(String qtyIndicator) {
		this.qtyIndicator = qtyIndicator;
	}
	public String getResalesMax() {
		return resalesMax;
	}
	public void setResalesMax(String resalesMax) {
		this.resalesMax = resalesMax;
	}
	public String getResalesMin() {
		return resalesMin;
	}
	public void setResalesMin(String resalesMin) {
		this.resalesMin = resalesMin;
	}
	public String getInternalTransferComment() {
		return internalTransferComment;
	}
	public void setInternalTransferComment(String internalTransferComment) {
		this.internalTransferComment = internalTransferComment;
	}
	public String getBmtMfrDrNo() {
		return bmtMfrDrNo;
	}
	public void setBmtMfrDrNo(String bmtMfrDrNo) {
		this.bmtMfrDrNo = bmtMfrDrNo;
	}
	public String getBmtSuggestCost() {
		return bmtSuggestCost;
	}
	public void setBmtSuggestCost(String bmtSuggestCost) {
		this.bmtSuggestCost = bmtSuggestCost;
	}
	public String getBmtSuggestResale() {
		return bmtSuggestResale;
	}
	public void setBmtSuggestResale(String bmtSuggestResale) {
		this.bmtSuggestResale = bmtSuggestResale;
	}
	public String getBmtSuggestMargin() {
		return bmtSuggestMargin;
	}
	public void setBmtSuggestMargin(String bmtSuggestMargin) {
		this.bmtSuggestMargin = bmtSuggestMargin;
	}
	public String getBmtMfrQuoteNo() {
		return bmtMfrQuoteNo;
	}
	public void setBmtMfrQuoteNo(String bmtMfrQuoteNo) {
		this.bmtMfrQuoteNo = bmtMfrQuoteNo;
	}
	
	public String getBmtComments() {
		return bmtComments;
	}
	public void setBmtComments(String bmtComments) {
		this.bmtComments = bmtComments;
	}
	public String getBmtQuoteQty() {
		return bmtQuoteQty;
	}
	public void setBmtQuoteQty(String bmtQuoteQty) {
		this.bmtQuoteQty = bmtQuoteQty;
	}
	public String getBmtSugCostAltCurrency() {
		return bmtSugCostAltCurrency;
	}
	public void setBmtSugCostAltCurrency(String bmtSugCostAltCurrency) {
		this.bmtSugCostAltCurrency = bmtSugCostAltCurrency;
	}
	public String getBmtSugResaleAltCurrency() {
		return bmtSugResaleAltCurrency;
	}
	public void setBmtSugResaleAltCurrency(String bmtSugResaleAltCurrency) {
		this.bmtSugResaleAltCurrency = bmtSugResaleAltCurrency;
	}
	public String getBmtCurrency() {
		return bmtCurrency;
	}
	public void setBmtCurrency(String bmtCurrency) {
		this.bmtCurrency = bmtCurrency;
	}
	public String getBmtExRateAltCurrency() {
		return bmtExRateAltCurrency;
	}
	public void setBmtExRateAltCurrency(String bmtExRateAltCurrency) {
		this.bmtExRateAltCurrency = bmtExRateAltCurrency;
	}
	public String getBmtFlag() {
		return bmtFlag;
	}
	public void setBmtFlag(String bmtFlag) {
		this.bmtFlag = bmtFlag;
	}
	public String getBmtPendingAt() {
		return bmtPendingAt;
	}
	public void setBmtPendingAt(String bmtPendingAt) {
		this.bmtPendingAt = bmtPendingAt;
	}
	public String getDesignRegion() {
		return designRegion;
	}
	public void setDesignRegion(String designRegion) {
		this.designRegion = designRegion;
	}
	public String getLastUpdateName() {
		return lastUpdateName;
	}
	public void setLastUpdateName(String lastUpdateName) {
		this.lastUpdateName = lastUpdateName;
	}
	public String getLastUpdateBmtName() {
		return lastUpdateBmtName;
	}
	public void setLastUpdateBmtName(String lastUpdateBmtName) {
		this.lastUpdateBmtName = lastUpdateBmtName;
	}
	
	public String getDpReferenceId() {
		return dpReferenceId;
	}
	
	public void setDpReferenceId(String dpReferenceId) {
		this.dpReferenceId = dpReferenceId;
	}
	public String getDpReferenceLineId() {
		return dpReferenceLineId;
	}
	public void setDpReferenceLineId(String dpReferenceLineId) {
		this.dpReferenceLineId = dpReferenceLineId;
	}
	public String getBalanceUnconsumedQty() {
		return balanceUnconsumedQty;
	}
	public void setBalanceUnconsumedQty(String balanceUnconsumedQty) {
		this.balanceUnconsumedQty = balanceUnconsumedQty;
	}
	
	public String getPriceValidity() {
		return priceValidity;
	}
	public void setPriceValidity(String priceValidity) {
		this.priceValidity = priceValidity;
	}
	public Date getQuoteEffectiveDate() {
		return quoteEffectiveDate;
	}
	public void setQuoteEffectiveDate(Date quoteEffectiveDate) {
		this.quoteEffectiveDate = quoteEffectiveDate;
	}
	public Date getQuoteExpiryDate() {
		return quoteExpiryDate;
	}
	public void setQuoteExpiryDate(Date quoteExpiryDate) {
		this.quoteExpiryDate = quoteExpiryDate;
	}
	public Date getPoExpiryDate() {
		return poExpiryDate;
	}
	public void setPoExpiryDate(Date poExpiryDate) {
		this.poExpiryDate = poExpiryDate;
	}
	public Date getShipmentValidity() {
		return shipmentValidity;
	}
	public void setShipmentValidity(Date shipmentValidity) {
		this.shipmentValidity = shipmentValidity;
	}
	public Date getDrExpiryDate() {
		return drExpiryDate;
	}
	public void setDrExpiryDate(Date drExpiryDate) {
		this.drExpiryDate = drExpiryDate;
	}
	public Date getSendOutTime() {
		return sendOutTime;
	}
	public void setSendOutTime(Date sendOutTime) {
		this.sendOutTime = sendOutTime;
	}
	public Date getUploadTime() {
		return uploadTime;
	}
	public void setUploadTime(Date uploadTime) {
		this.uploadTime = uploadTime;
	}
	public Date getBmtsqEffciveDate() {
		return bmtsqEffciveDate;
	}
	public void setBmtsqEffciveDate(Date bmtsqEffciveDate) {
		this.bmtsqEffciveDate = bmtsqEffciveDate;
	}
	public Date getBmtSQexpiryDate() {
		return bmtSQexpiryDate;
	}
	public void setBmtSQexpiryDate(Date bmtSQexpiryDate) {
		this.bmtSQexpiryDate = bmtSQexpiryDate;
	}
	public Date getLastBmtUpdate() {
		return lastBmtUpdate;
	}
	public void setLastBmtUpdate(Date lastBmtUpdate) {
		this.lastBmtUpdate = lastBmtUpdate;
	}
	public String getAvnetQuotedQty() {
		return avnetQuotedQty;
	}
	public String getSalesCostFlag() {
		return salesCostFlag;
	}
	public void setSalesCostFlag(String salesCostFlag) {
		this.salesCostFlag = salesCostFlag;
	}
	public String getSalesCostTypeName() {
		return salesCostTypeName;
	}
	public void setSalesCostTypeName(String salesCostTypeName) {
		this.salesCostTypeName = salesCostTypeName;
	}
	public String getSalesCost() {
		return salesCost;
	}
	public void setSalesCost(String salesCost) {
		this.salesCost = salesCost;
	}
	public String getSuggestedResale() {
		return suggestedResale;
	}
	public void setSuggestedResale(String suggestedResale) {
		this.suggestedResale = suggestedResale;
	}
	public String getCustomerGroupId() {
		return customerGroupId;
	}
	public void setCustomerGroupId(String customerGroupId) {
		this.customerGroupId = customerGroupId;
	}
	
	
	
	
	
	
	
}
