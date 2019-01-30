package com.avnet.emasia.webquote.quote.vo;

import java.io.Serializable;

import com.avnet.emasia.webquote.utilities.bean.QuoteBuilderExcelAlias;

public class QuoteBuilderNonSalesCostBean implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private int lineSeq;
	
	private String errorMsg;
	
	private String formNumber;
	
	private String quoteNumber;
	
	public int getLineSeq() {
		return lineSeq;
	}


	public void setLineSeq(int lineSeq) {
		this.lineSeq = lineSeq;
	}
	

	public String getErrorMsg() {
		return errorMsg;
	}


	public void setErrorMsg(String errorMsg) {
		this.errorMsg = errorMsg;
	}


	public String getFormNumber() {
		return formNumber;
	}


	public void setFormNumber(String formNumber) {
		this.formNumber = formNumber;
	}


	public String getQuoteNumber() {
		return quoteNumber;
	}


	public void setQuoteNumber(String quoteNumber) {
		this.quoteNumber = quoteNumber;
	}

//	@QuoteBuilderExcelAlias(name="wq.label.region",mandatory="true",cellNum="1",fieldLength="-1")
//	private String region;
//
//	@QuoteBuilderExcelAlias(name="wq.label.salesEmpCode",mandatory="true",cellNum="2",fieldLength="-1")
//	private String salesEmpCode;
//
//	@QuoteBuilderExcelAlias(name="wq.label.SoldToCode",mandatory="true",cellNum="3",fieldLength="-1")
//	private String soldToCode;
//
//	@QuoteBuilderExcelAlias(name="wq.label.EndCustomerCode",mandatory="false",cellNum="4",fieldLength="-1")
//	private String endCustCode;
//
//	@QuoteBuilderExcelAlias(name="wq.label.mfr",mandatory="true",cellNum="5",fieldLength="-1")
//	private String mfr;
//
//	@QuoteBuilderExcelAlias(name="wq.label.avnetQPN",mandatory="true",cellNum="6",fieldLength="-1")
//	private String quotePartNumber;
//
//	@QuoteBuilderExcelAlias(name="wq.label.mfrQuoteQty",mandatory="false",cellNum="7",fieldLength="-1")
//	private String vendorQuoteQty;
//
//	@QuoteBuilderExcelAlias(name="wq.label.avnetQuoteQty",mandatory="false",cellNum="8",fieldLength="-1")
//	private String quotedQty;
//
//	@QuoteBuilderExcelAlias(name="wq.label.costIndicator",mandatory="true",cellNum="9",fieldLength="-1")
//	private String costIndicator;
//
//	@QuoteBuilderExcelAlias(name="wq.label.cost",mandatory="true",cellNum="10",fieldLength="-1")
//	private String cost;
//
//	@QuoteBuilderExcelAlias(name="wq.label.avnetQPrice",mandatory="true",cellNum="11",fieldLength="-1")
//	private String quotedPrice;
//
//	@QuoteBuilderExcelAlias(name="wq.label.leadTimeWK",mandatory="false",cellNum="12",fieldLength="-1")
//	private String leadTime;
//
//	@QuoteBuilderExcelAlias(name="wq.label.QuotationEffectiveDate",mandatory="false",cellNum="13",fieldLength="-1")
//	private String quotationEffectiveDate;
//
//	@QuoteBuilderExcelAlias(name="wq.label.priceValidity",mandatory="true",cellNum="14",fieldLength="-1")
//	private String priceValidity;
//
//	@QuoteBuilderExcelAlias(name="wq.label.shipmentVal",mandatory="false",cellNum="15",fieldLength="-1")
//	private String shipmentValidity;
//
//	@QuoteBuilderExcelAlias(name="wq.label.MPQ",mandatory="false",cellNum="16",fieldLength="14")
//	private String mpq;
//
//	@QuoteBuilderExcelAlias(name="wq.label.moq",mandatory="false",cellNum="17",fieldLength="14")
//	private String moq;
//
//	@QuoteBuilderExcelAlias(name="wq.label.mov",mandatory="false",cellNum="18",fieldLength="14")
//	private String mov;
//
//	@QuoteBuilderExcelAlias(name="wq.label.multiUsage",mandatory="true",cellNum="19",fieldLength="-1")
//	private String multiUsageFlag;
//
//	@QuoteBuilderExcelAlias(name="wq.label.PMOQ",mandatory="false",cellNum="20",fieldLength="-1")
//	private String pmoq;
//
//	@QuoteBuilderExcelAlias(name="wq.label.mfrDebit",mandatory="false",cellNum="21",fieldLength="-1")
//	private String vendorDebitNumber;
//
//	@QuoteBuilderExcelAlias(name="wq.label.mfrQoute",mandatory="false",cellNum="22",fieldLength="-1")
//	private String vendorQuoteNumber;
//
//	@QuoteBuilderExcelAlias(name="wq.label.resaleIndicator",mandatory="true",cellNum="23",fieldLength="-1")
//	private String resaleIndicator;
//
//	@QuoteBuilderExcelAlias(name="wq.label.qutoeTC",mandatory="false",cellNum="24",fieldLength="255")
//	private String termsAndConditions;
//
//	@QuoteBuilderExcelAlias(name="wq.label.avnetQuoteCenterComment",mandatory="false",cellNum="25",fieldLength="255")
//	private String aqcc;
//
//	@QuoteBuilderExcelAlias(name="wq.label.QCCommentsAvnetInternal",mandatory="false",cellNum="26",fieldLength="255")
//	private String internalComment;
	
	
	
	//region
	@QuoteBuilderExcelAlias(name="wq.label.region",mandatory="true",cellNum="1",fieldLength="-1")
	private String region;
	//sales Employee code
	@QuoteBuilderExcelAlias(name="wq.label.salesEmpCode",mandatory="true",cellNum="2",fieldLength="-1")
	private String salesEmpCode;
	//sold to code
	@QuoteBuilderExcelAlias(name="wq.label.SoldToCode",mandatory="true",cellNum="3",fieldLength="-1")
	private String soldToCode;
	//customer group id
	@QuoteBuilderExcelAlias(name="wq.label.customerGroupID",mandatory="false",cellNum="4",fieldLength="-1")
	private String custGroupId;
	//end customer code 
	@QuoteBuilderExcelAlias(name="wq.label.EndCustomerCode",mandatory="false",cellNum="5",fieldLength="-1")
	private String endCustCode;
	//resale Indicator
	@QuoteBuilderExcelAlias(name="wq.label.resaleIndicator",mandatory="true",cellNum="6",fieldLength="-1")
	private String resaleIndicator;
	//mfr
	@QuoteBuilderExcelAlias(name="wq.label.mfr",mandatory="true",cellNum="7",fieldLength="-1")
	private String mfr;
	//Avnet Quoted P/N
	@QuoteBuilderExcelAlias(name="wq.label.avnetQPN",mandatory="true",cellNum="8",fieldLength="-1")
	private String quotePartNumber;
	//cost indicator 
	@QuoteBuilderExcelAlias(name="wq.label.costIndicator",mandatory="true",cellNum="9",fieldLength="-1")
	private String costIndicator;
	//cost
	@QuoteBuilderExcelAlias(name="wq.label.cost",mandatory="true",cellNum="10",fieldLength="-1")
	private String cost;
	//Avnet Quoted price
	@QuoteBuilderExcelAlias(name="wq.label.avnetQPrice",mandatory="true",cellNum="11",fieldLength="-1")
	private String quotedPrice;
	//lead time
	@QuoteBuilderExcelAlias(name="wq.label.leadTimeWK",mandatory="false",cellNum="12",fieldLength="-1")
	private String leadTime;
	//quotation Effective date 
	@QuoteBuilderExcelAlias(name="wq.label.QuotationEffectiveDate",mandatory="false",cellNum="13",fieldLength="-1")
	private String quotationEffectiveDate;
	//prive Validity
	@QuoteBuilderExcelAlias(name="wq.label.priceValidity",mandatory="true",cellNum="14",fieldLength="-1")
	private String priceValidity;
	//shipment validity
	@QuoteBuilderExcelAlias(name="wq.label.shipmentVal",mandatory="false",cellNum="15",fieldLength="-1")
	private String shipmentValidity;
	//Qty Indicator
	
	@QuoteBuilderExcelAlias(name="wq.label.qtyIndicator",mandatory="true",cellNum="16",fieldLength="-1")
	private String qtyIndicator;
	//multi Usage
	@QuoteBuilderExcelAlias(name="wq.label.multiUsage",mandatory="true",cellNum="17",fieldLength="-1")
	private String multiUsageFlag;
	//Avnet Quote Qty
	@QuoteBuilderExcelAlias(name="wq.label.avnetQuoteQty",mandatory="false",cellNum="18",fieldLength="-1")
	private String quotedQty;
	//pmoq
	@QuoteBuilderExcelAlias(name="wq.label.PMOQ",mandatory="false",cellNum="19",fieldLength="-1")
	private String pmoq;
	//mpq
	@QuoteBuilderExcelAlias(name="wq.label.MPQ",mandatory="false",cellNum="20",fieldLength="14")
	private String mpq;
	//moq
	@QuoteBuilderExcelAlias(name="wq.label.moq",mandatory="false",cellNum="21",fieldLength="14")
	private String moq;
	//mov
	@QuoteBuilderExcelAlias(name="wq.label.mov",mandatory="false",cellNum="22",fieldLength="14")
	private String mov;
	//MFR Quote#
	@QuoteBuilderExcelAlias(name="wq.label.mfrQoute",mandatory="false",cellNum="23",fieldLength="-1")
	private String vendorQuoteNumber;
	//MFR debit#
	@QuoteBuilderExcelAlias(name="wq.label.mfrDebit",mandatory="false",cellNum="24",fieldLength="-1")
	private String vendorDebitNumber;
	//MFR Qoute Qty
	@QuoteBuilderExcelAlias(name="wq.label.mfrQuoteQty",mandatory="false",cellNum="25",fieldLength="-1")
	private String vendorQuoteQty;
	//Quotion T&C
	@QuoteBuilderExcelAlias(name="wq.label.qutoeTC",mandatory="false",cellNum="26",fieldLength="255")
	private String termsAndConditions;
	//Avnet Centre Comment
	@QuoteBuilderExcelAlias(name="wq.label.avnetQuoteCenterComment",mandatory="false",cellNum="27",fieldLength="255")
	private String aqcc;
	//Qc Comment (Avnet Interal Only)
	@QuoteBuilderExcelAlias(name="wq.label.QCCommentsAvnetInternal",mandatory="false",cellNum="28",fieldLength="255")
	private String internalComment;
	
	
	
	
	
	//new 
	public String getCustGroupId() {
		return custGroupId;
	}


	public void setCustGroupId(String custGroupId) {
		this.custGroupId = custGroupId;
	}

	//new 
	public String getQtyIndicator() {
		return qtyIndicator;
	}


	public void setQtyIndicator(String qtyIndicator) {
		this.qtyIndicator = qtyIndicator;
	}


	public QuoteBuilderNonSalesCostBean() {
		
	}
	
	public String getRegion() {
		return region;
	}


	public void setRegion(String region) {
		this.region = region;
	}


	public String getSalesEmpCode() {
		return salesEmpCode;
	}


	public void setSalesEmpCode(String salesEmpCode) {
		this.salesEmpCode = salesEmpCode;
	}


	public String getSoldToCode() {
		return soldToCode;
	}


	public void setSoldToCode(String soldToCode) {
		this.soldToCode = soldToCode;
	}


	public String getEndCustCode() {
		return endCustCode;
	}


	public void setEndCustCode(String endCustCode) {
		this.endCustCode = endCustCode;
	}


	public String getMfr() {
		return mfr;
	}


	public void setMfr(String mfr) {
		this.mfr = mfr;
	}


	public String getQuotePartNumber() {
		return quotePartNumber;
	}


	public void setQuotePartNumber(String quotePartNumber) {
		this.quotePartNumber = quotePartNumber;
	}


	public String getVendorQuoteQty() {
		return vendorQuoteQty;
	}


	public void setVendorQuoteQty(String vendorQuoteQty) {
		this.vendorQuoteQty = vendorQuoteQty;
	}


	public String getQuotedQty() {
		return quotedQty;
	}


	public void setQuotedQty(String quotedQty) {
		this.quotedQty = quotedQty;
	}


	public String getCostIndicator() {
		return costIndicator;
	}


	public void setCostIndicator(String costIndicator) {
		this.costIndicator = costIndicator;
	}


	public String getCost() {
		return cost;
	}


	public void setCost(String cost) {
		this.cost = cost;
	}


	public String getQuotedPrice() {
		return quotedPrice;
	}


	public void setQuotedPrice(String quotedPrice) {
		this.quotedPrice = quotedPrice;
	}


	public String getLeadTime() {
		return leadTime;
	}


	public void setLeadTime(String leadTime) {
		this.leadTime = leadTime;
	}


	public String getQuotationEffectiveDate() {
		return quotationEffectiveDate;
	}


	public void setQuotationEffectiveDate(String quotationEffectiveDate) {
		this.quotationEffectiveDate = quotationEffectiveDate;
	}


	public String getPriceValidity() {
		return priceValidity;
	}


	public void setPriceValidity(String priceValidity) {
		this.priceValidity = priceValidity;
	}


	public String getShipmentValidity() {
		return shipmentValidity;
	}


	public void setShipmentValidity(String shipmentValidity) {
		this.shipmentValidity = shipmentValidity;
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


	public String getMov() {
		return mov;
	}


	public void setMov(String mov) {
		this.mov = mov;
	}


	public String getMultiUsageFlag() {
		return multiUsageFlag;
	}


	public void setMultiUsageFlag(String multiUsageFlag) {
		this.multiUsageFlag = multiUsageFlag;
	}


	public String getPmoq() {
		return pmoq;
	}


	public void setPmoq(String pmoq) {
		this.pmoq = pmoq;
	}


	public String getVendorDebitNumber() {
		return vendorDebitNumber;
	}


	public void setVendorDebitNumber(String vendorDebitNumber) {
		this.vendorDebitNumber = vendorDebitNumber;
	}


	public String getVendorQuoteNumber() {
		return vendorQuoteNumber;
	}


	public void setVendorQuoteNumber(String vendorQuoteNumber) {
		this.vendorQuoteNumber = vendorQuoteNumber;
	}


	public String getResaleIndicator() {
		return resaleIndicator;
	}


	public void setResaleIndicator(String resaleIndicator) {
		this.resaleIndicator = resaleIndicator;
	}


	public String getTermsAndConditions() {
		return termsAndConditions;
	}


	public void setTermsAndConditions(String termsAndConditions) {
		this.termsAndConditions = termsAndConditions;
	}


	public String getAqcc() {
		return aqcc;
	}


	public void setAqcc(String aqcc) {
		this.aqcc = aqcc;
	}


	public String getInternalComment() {
		return internalComment;
	}


	public void setInternalComment(String internalComment) {
		this.internalComment = internalComment;
	}

	
}
