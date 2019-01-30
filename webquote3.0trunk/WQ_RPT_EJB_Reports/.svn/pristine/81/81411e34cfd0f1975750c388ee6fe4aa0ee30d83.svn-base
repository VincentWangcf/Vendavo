package com.avnet.emasia.webquote.reports.offlinereport;

import java.text.DecimalFormat;

/**
 * @author 906893
 * @createdOn 20130424
 */
public class OfflineReportConstants {






	 

	 
	 public static final String STANDARD_DATE_TIME_FORMAT = "yyyy-MM-dd HH:mm:ss";
	 
	 public static final String DISPLAY_DATE_FORMAT = "dd/MM/yyyy";
	 
	 public static final String DISPLAY_DATE_TIME_FORMAT = "dd/MM/yyyy HH:mm:ss";
	 
	 public static final String INPUT_DATE_FORMAT = "MM/dd/yyyy";
	 
	 public final static String TEMP_DIR = "TMP_DIRECTORY";
	 


	 /**
	  * Sales Report Download
	  * @author 907110
	  *
	  * TODO To change the template for this generated type comment go to
	  * Window - Preferences - Java - Code Style - Code Templates
	  */

	 public static final String[] SALE_DOWNLOAD_TEMPLATE_HEADER_TITLE = {
		 "Used","Avnet Quote#","Form No","Valid For Ordering","Invalid For Ordering (Reason)","MFR",
        "Avnet Quoted P/N","Requested P/N","SAP Material Number", "Customer Name(Sold To Party)","Chinese Customer Name(Sold To Party)","Sold To Code","SAP Sold To Type","End Customer",
        "End Customer Code","Avnet Quoted Price","Final Quotation Price","Quote Reference Margin","PMOQ", "Cost Indicator","Quotation Effective Date", "Quote Expiry Date",
        "Shipment Validity","MPQ","MOQ","Lead Time(wks)","Multi-Usage",
        "MFR Debit#","MFR Quote#","MFR Quote Qty","Resale Indicator","Cancellation Window",
        "Rescheduling Window","Quotation T&C","Allocation Part","Avnet Quote Centre Comment","QC Comment (Avnet Internal Only)",
        "Quote Stage","RFQ Status","BMT Flag","Pending at","Salesman Name","Sales Employee Code","Team",
        "Ship To Party","Ship To Code","Customer Type","Material Type","Business Program Type",
        "Segment","Project Name","Application","Design Region","Design Location","Design In Cat",
        "DRMS Project ID","DR Expiry Date","MP Schedule","PP Schedule","LOA","Quote Type","Reference Exchange Rate","Reference Exchange Currency",
        "Remarks","Requester Reference","Copy To CS",
        "SPR","Product Group 2","Required Qty","EAU",
        "Target Resale","MFR DR#","Competitor Information","Item Remarks",
        "First RFQ Code","Revert Version","RFQ Submission Date","Quote Release Date","Quoted By","RFQ Created By" ,
        "BMT MFR DR#","BMT Suggest Resale","BMT MFR Quote #",
		 "BMT SQ Effective Date","BMT SQ Expiry Date","BMT Comments","BMT Quote Qty",
		 "BMT Suggest Resale(Non-USD)","BMT Currency","Exchange rate(Non-USD)","DP Reference ID","DP Reference Line ID"
    };

	 public static final String[] SALE_DOWNLOAD_TEMPLATE_HEADER_PARAMATERS = {
		 "usedFlag","quoteNumber","formNo","readyForOrder","readyForOrderNot","mfr", 
	         "quotedPn","requestedPn","sapPartNumber","soldToCustomerName","solToCustomerChineseName",
	         "soldToCustomerCode","sapSoldToType","endCustomer","endCustomerCode","avnetQuotePrice",
	         "finalQuotationPrice","referenceMargin","pmoq", "costIndicator","quoteEffectiveDate", "quoteExpiryDate",
	         "shipmentValidity","mpq","moq","leadTime","multiUsage",
	         "vendorDebit", "vendorQuote","vendorQty","resaleIndicator","cancellWindow",
	         "reshedulingWindow","termsAndConditions","allocationFlog","avnetQCComment","internalComment",
	         "quoteStage","rfqStatus","bmtFlag","bmtPendingAt","employeeName","salesman","team",
	         "shipToParty","shipToCode","customerType","materialType","program",
	         "enquirySegment","projectName","application","designRegion","designLocation","avnetRegionalDemandCreation",
	         "drms","drExpiryDate","mpSchedule","ppSchedule","loa","quoteType","exRateTo","currTo",
	         "remarks","yourReference","copyToCs",
	         "spr","productGroup","requiredQty","eau","targetResale",
	         "supplierDr","competitorInformation","ItemRemarks","firstRfqCode",
	         "revertVersion","uploadTime","sendOutTime","quotedBy","createdBy",
	         "bmtMfrDrNo","bmtSuggestResale","bmtMfrQuoteNo",
			 "bmtsqEffciveDate","bmtSQexpiryDate","bmtComments","bmtQuoteQty",
			 "bmtSugResaleAltCurrency","bmtCurrency","bmtExRateAltCurrency","dpReferenceId","dpReferenceLineId"
	 };

	 public static final int[] SALE_DOWNLOAD_TEMPLATE_DATE_TYPE = {
	         2,2,2,2,2,2, //mfr
	         2,2,2,2,2, //solToCustomerChineseName
	         2,2,2,2,1, //avnetQuotePrice
	         1,2,2,2,3,3, //quoteExpiryDate
	         3,0,0,2,2, //multiUsage
	         2,2,0,2,2, //cancellWindow
	         2,2,2,2,2, //internalComment
	         2,2,2,2,2,2,2, //team
	         2,2,2,2,2, //program
	         2,2,2,2,2,2, //avnetRegionalDemandCreation
	         2,3,2,2,2,2,2,2, //currTo
	         2,2,2, //copyToCs
	         2,2,2,2,2, //targetResale
	         2,2,2,2, //firstRfqCode
	         2,4,4,2,2, //createdBy
	         2,2,2, //bmtMfrQuoteNo
	         3,3,2,2, //bmtQuoteQty
	         2,2,2,2,2 //dpReferenceLineId
	 };		 
	/**
	 * CS Report Download
	 * @author 907110
	 *2, string
	 *1,float
	 *0,integer
	 *
	 * TODO To change the template for this generated type comment go to
	 * Window - Preferences - Java - Code Style - Code Templates
	 */
	 
	  public static final String[] CS_DOWNLOAD_TEMPLATE_HEADER_TITLE = {
		  "Used","Avnet Quote#","Form No","Valid For Ordering","Invalid For Ordering (Reason)","MFR",
	        "Avnet Quoted P/N","Requested P/N","SAP Material Number", "Customer Name(Sold To Party)","Chinese Customer name(Sold To Party)","Sold To Code","SAP Sold To Type","End Customer",
	        "End Customer Code","Avnet Quoted Price","Final Quotation Price","Quote Reference Margin","PMOQ", "Cost Indicator", "Quotation Effective Date","Quote Expiry Date",
	        "Shipment Validity","MPQ","MOQ","Lead Time(wks)","Multi-Usage",
	        "MFR Debit#","MFR Quote#","MFR Quote Qty","Resale Indicator","Cancellation Window",
	        "Rescheduling Window","Quotation T&C","Allocation Part","Avnet Quote Centre Comment","QC Comment (Avnet Internal Only)",
	        "Quote Stage","RFQ Status","BMT Flag","Pending at","Salesman Name","Sales Employee Code","Team",
	        "Ship To Party","Ship To Code","Customer Type","Material Type","Business Program Type",
	        "Segment","Project Name","Application","Design Region","Design Location","Design In Cat",
	        "DRMS Project ID","MP Schedule","PP Schedule","LOA","Quote Type","Reference Exchange Rate","Reference Exchange Currency",
	        "Remarks","Requester Reference","Copy To CS",
	        "SPR","Product Group 2","Required Qty","EAU",
	        "Target Resale","MFR DR#","Competitor Information","Item Remarks","First RFQ Code","Revert Version","RFQ Submission Date","Quote Release Date",
	        "Quoted By","RFQ Created By",
	        "DP Reference ID","DP Reference Line ID"
		  };
		
		 public static final String[] CS_DOWNLOAD_TEMPLATE_HEADER_PARAMATERS = {
			 "usedFlag","quoteNumber","formNo","readyForOrder","readyForOrderNot","mfr", 
	         "quotedPn","requestedPn","sapPartNumber","soldToCustomerName","solToCustomerChineseName","soldToCustomerCode","sapSoldToType","endCustomer",
	         "endCustomerCode","avnetQuotePrice","finalQuotationPrice","referenceMargin","pmoq", "costIndicator","quoteEffectiveDate", "quoteExpiryDate",
	         "shipmentValidity","mpq","moq","leadTime","multiUsage",
	         "vendorDebit", "vendorQuote","vendorQty","resaleIndicator","cancellWindow",
	         "reshedulingWindow","termsAndConditions","allocationFlog","avnetQCComment","internalComment",
	         "quoteStage","rfqStatus","bmtFlag","bmtPendingAt","employeeName","salesman","team",
	         "shipToParty","shipToCode","customerType","materialType","program",
	         "enquirySegment","projectName","application","designRegion","designLocation","avnetRegionalDemandCreation",
	         "drms","mpSchedule","ppSchedule","loa","quoteType","exRateTo","currTo",
	         "remarks","yourReference","copyToCs",
	         "spr","productGroup","requiredQty","eau",
	         "targetResale","supplierDr","competitorInformation","ItemRemarks","firstRfqCode","revertVersion","uploadTime","sendOutTime",
	         "quotedBy","createdBy",
	         "dpReferenceId","dpReferenceLineId"
		 };
		 public static final int[] CS_DOWNLOAD_TEMPLATE_DATE_TYPE = {
			 2,2,2,2,2,2, //mfr
	         2,2,2,2,2,2,2,2, //endCustomer
	         2,1,1,2,2,2,3,3, //quoteExpiryDate
	         3,0,0,2,2,//multiUsage
	         2,2,0,2,2,//cancellWindow
	         2,2,2,2,2,//internalComment
	         2,2,2,2,2,2,2, //team
	         2,2,2,2,2,//program
	         2,2,2,2,2,2,//avnetRegionalDemandCreation
	         2,2,2,2,2,2,2,//currTo
	         2,2,2,2, //copyToCs
	         2,2,2,2,//eau
	         2,2,2,2,2,2,4,4,//sendOutTime
	         2,2,//createdBy
	         2,2//dpReferenceLineId
		 };	
		 
    	
	/**
	 * Material Management report download
	 * @author 907110
	 *
	 * TODO To change the template for this generated type comment go to
	 * Window - Preferences - Java - Code Style - Code Templates
	 */
		 public static final String[] MM_DOWNLOAD_TEMPLATE_HEADER_TITLE = {
			 "Used","Bal. Unconsumed Qty","Avnet Quote#","Quote Stage","RFQ Status","BMT Flag","Pending at","MFR","Avnet Quoted P/N","Requested P/N",
			 "SAP Material Number","Customer Name(Sold To Party)","Chinese Customer name(Sold To Party)","Sold To Code","Avnet Quoted Price","Final Quotation Price","Cost",
			 "Cost Indicator","Quotation Effective Date","PO Expiry Date","Shipment Validity","PMOQ",
			 "MPQ","MOQ","MOV","Lead Time(wks)","Multi-Usage",
			 "MFR Debit#","MFR Quote#","MFR Quote Qty","Avnet Quoted Qty","Resale Indicator",
			 "Cancellation Window","Rescheduling Window","Quotation T&C",
			 "Allocation Part","Avnet Quote Centre Comment","QC Comment (Avnet Internal Only)",
			 "Quote Expiry Date",
			 "PM Comment","Form No",
			 "Salesman Name","Sales Employee Code","Team","Quoted By","End Customer","End Customer Code",
			 "Ship To Party","Ship To Code","Material Type","Business Program Type",
			 "Design In Cat","Quote Type","Reference Exchange Rate","Reference Exchange Currency",
			 "Product Group 2",
			 "Item Remarks","RFQ Submission Date","Quote Release Date",
			 "RFQ Created By",
			 "DP Reference ID","DP Reference Line ID"

		 };
		 
		 public static final String[] MM_DOWNLOAD_TEMPLATE_HEADER_PARAMATERS = {
			 "usedFlag","balanceUnconsumedQty","quoteNumber","quoteStage","rfqStatus","bmtFlag","bmtPendingAt","mfr","quotedPn","requestedPn",
			 "sapPartNumber","soldToCustomerName","solToCustomerChineseName","soldToCustomerCode","avnetQuotePrice","finalQuotationPrice","cost",
			 "costIndicator","quoteEffectiveDate","poExpiryDate","shipmentValidity","pmoq",
			 "mpq","moq","mov","leadTime","multiUsage",
			 "vendorDebit","vendorQuote","vendorQty","avnetQuotedQty","resaleIndicator",
			 "cancellWindow","reshedulingWindow","termsAndConditions",
			 "allocationFlog","avnetQCComment","internalComment","quoteExpiryDate","internalTransferComment","formNo",
			 "employeeName","salesman","team","quotedBy","endCustomer","endCustomerCode",
			 "shipToParty","shipToCode","materialType","program",
			 "designInCat","quoteType","exRateTo","currTo",
			 "productGroup",
			 "ItemRemarks","uploadTime","sendOutTime",
			 "createdBy",
			 "dpReferenceId","dpReferenceLineId"
		 };
		 public static final int[] MM_DOWNLOAD_TEMPLATE_DATE_TYPE = {
			 2,2,2,2,2,2,2,2,2,2, //requestedPn
	         2,2,2,2,1,1,1, //cost
	         2,3,3,3,2, //pmoq
	         0,0,0,2,2, //multiUsage
	         2,2,0,0,2, //resaleIndicator
	         2,2,2, //termsAndConditions
	         2,2,2,3,2,2, //formNo
	         2,2,2,2,2,2, //endCustomerCode
	         2,2,2,2, //program
	         2,2,2,2, //currTo
	         2, //productGroup
	         2,4,4, //sendOutTime
	         2, //createdBy
	         2,2 //dpReferenceLineId
		 };	
		
			/**
			 * Production Management report download
			 * @author 907110
			 *
			 * TODO To change the template for this generated type comment go to
			 * Window - Preferences - Java - Code Style - Code Templates
			 */
				
				 public static final String[] PM_DOWNLOAD_TEMPLATE_HEADER_TITLE = {
					 "Avnet Quote#","Form No","Quote Stage","RFQ Status","BMT Flag","Pending at","MFR",
					 "Requested P/N","Avnet Quoted P/N","SAP Material Number", "Customer Name(Sold To Party)","Chinese Customer name(Sold To Party)",
					 "Sold To Code","SAP Sold To Type","Customer Type","End Customer","End Customer Code",
					 "Avnet Quoted Price","Final Quotation Price","Cost","Quote Margin","Quote Reference Margin",
					 "Cost Indicator","Price Validity","Quotation Effective Date", "Quote Expiry Date","PO Expiry Date",// for furture.					 
					 "Shipment Validity","DRMS Project ID","MFR DR#","DRMS Authorized GP%", "Reasons for Authorized GP% Change",					 
					 "Remarks of Reason","DR Expiry Date","Segment","Project Name","Application",	"Design Region",				 
					 "Design Location","Design In Cat","MFR Debit#","MFR Quote#","MFR Quote Qty",					 
					 "Avnet Quoted Qty","Resale Indicator","Avnet Quote Centre Comment","QC Comment (Avnet Internal Only)","PM Comment","PMOQ",					 
					 "MPQ","MOQ","MOV","Lead Time(wks)","Multi-Usage",					 
					 "Qty Indicator","Resale Max%","Resale Min%","Cancellation Window","Rescheduling Window",					 
					 "Quotation T&C","Allocation Part","Salesman Name","Sales Employee Code","Team",							 
					 "Ship To Party","Ship To Code","Material Type","Business Program Type","PP Schedule",						 
					 "MP Schedule","LOA","Quote Type","Reference Exchange Rate","Reference Exchange Currency","Remarks","Requester Reference",					 					 
					 "Copy To CS","SPR","Product Group 2",
					 "Required Qty","EAU","Target Resale","Competitor Information","Item Remarks",							 
					 "Reason For Refresh","First RFQ Code","Revert Version","RFQ Submission Date","Quote Release Date","Quoted By",					 
					 "RFQ Created By" ,
					 "BMT MFR DR#","BMT Suggest Cost","BMT Suggest Resale","BMT Suggest Margin","BMT MFR Quote #",
					 "BMT SQ Effective Date","BMT SQ Expiry Date","BMT Comments","BMT Quote Qty","BMT Suggest Cost (Non-USD)",
					 "BMT Suggest Resale(Non-USD)","BMT Currency","Exchange rate(Non-USD)","DP Reference ID","DP Reference Line ID"
				 };
		 
		        public static final String[] PM_DOWNLOAD_TEMPLATE_HEADER_PARAMATERS = {
					 "quoteNumber","formNo","quoteStage","rfqStatus","bmtFlag","bmtPendingAt","mfr",
					 "requestedPn","quotedPn","sapPartNumber","soldToCustomerName","solToCustomerChineseName", 
					 "soldToCustomerCode","sapSoldToType","customerType","endCustomer","endCustomerCode",
					 "avnetQuotePrice","finalQuotationPrice","cost","resaleMargin","referenceMargin", 
					 "costIndicator", "priceValidity","quoteEffectiveDate","quoteExpiryDate","poExpiryDate", //for future					 					 
					 "shipmentValidity","drms","supplierDr", "drmsAuthorizedGP", "authorizedGPChangeReason",
					 "remarkChangeReason","drExpiryDate","enquirySegment","projectName","application","designRegion",
					 "designLocation","avnetRegionalDemandCreation","vendorDebit","vendorQuote","vendorQty",					 
					 "avnetQuotedQty","resaleIndicator","avnetQCComment","internalComment","internalTransferComment","pmoq",					 
					 "mpq","moq","mov","leadTime","multiUsage",					 
					 "qtyIndicator","resalesMax","resalesMin","cancellWindow","reshedulingWindow",
					 "termsAndConditions","allocationFlog","employeeName","salesman","team",					 					 
					 "shipToParty","shipToCode","materialType","program","ppSchedule",					 					 
					 "mpSchedule","loa","quoteType","exRateTo","currTo","remarks","yourReference",					 
					 "copyToCs","spr","productGroup",
					 "requiredQty","eau","targetResale","competitorInformation","ItemRemarks",					 
					 "reasonForRefresh","firstRfqCode","revertVersion","uploadTime","sendOutTime","quotedBy",					 
					 "createdBy" ,
					 "bmtMfrDrNo","bmtSuggestCost","bmtSuggestResale","bmtSuggestMargin","bmtMfrQuoteNo",
					 "bmtsqEffciveDate","bmtSQexpiryDate","bmtComments","bmtQuoteQty","bmtSugCostAltCurrency",
					 "bmtSugResaleAltCurrency","bmtCurrency","bmtExRateAltCurrency","dpReferenceId","dpReferenceLineId"
				 };
				 

				
				 public static final int[] PM_DOWNLOAD_TEMPLATE_DATE_TYPE = {
					 2,2,2,2,2,2,2, //mfr
					 2,2,2,2,2, //solToCustomerChineseName
			         0,2,2,2,0, //endCustomerCode
			         1,1,1,2,2, //referenceMargin
					 2,2,3,3,3, //poExpiryDate
			         3,2,2,2,2, //authorizedGPChangeReason
			         2,3,2,2,2,2,  //designRegion
					 2,2,2,2,0, //vendorQty
			         0,2,2,2,2,2, //pmoq
			         0,0,0,2,2, //multiUsage
					 2,1,1,0,0, //reshedulingWindow
			         2,2,2,2,2, //team
			         2,2,2,2,2, //ppSchedule
					 2,2,2,2,2,2,2, //yourReference
			         2,2,2, //productGroup
			         0,0,1,2,2, //ItemRemarks
					 2,2,2,4,4,2,//quotedBy
			         2, //createdBy
			         2,2,2,2,2, //bmtMfrQuoteNo
			         3,3,2,2,2, //bmtSugCostAltCurrency
			         2,2,2,2,2 //dpReferenceLineId
				 };
				 
				 
				 /**
					 * BMT report download
					 * @author 916138
					 *
					 * TODO To change the template for this generated type comment go to
					 * Window - Preferences - Java - Code Style - Code Templates
					 */
						
				 public static final String[] BMT_DOWNLOAD_TEMPLATE_HEADER_TITLE = {
					 "Avnet Quote#","Form No","Quote Stage","RFQ Status","BMT Flag","Pending at","MFR",
					 "Requested P/N","Avnet Quoted P/N","SAP Material Number", "Customer Name(Sold To Party)","Chinese Customer name(Sold To Party)",
					 "Sold To Code","SAP Sold To Type","Customer Type","End Customer","End Customer Code",
					 "Avnet Quoted Price","Final Quotation Price","Cost","Quote Margin","Quote Reference Margin",
					 "Cost Indicator","Price Validity","Quotation Effective Date", "Quote Expiry Date","PO Expiry Date",// for furture.					 
					 "Shipment Validity","DRMS Project ID","MFR DR#","DRMS Authorized GP%", "Reasons for Authorized GP% Change",					 
					 "Remarks of Reason","DR Expiry Date","Segment","Project Name","Application",	"Design Region",				 
					 "Design Location","Design In Cat","MFR Debit#","MFR Quote#","MFR Quote Qty",					 
					 "Avnet Quoted Qty","Resale Indicator","Avnet Quote Centre Comment","QC Comment (Avnet Internal Only)","PM Comment","PMOQ",					 
					 "MPQ","MOQ","MOV","Lead Time(wks)","Multi-Usage",					 
					 "Qty Indicator","Resale Max%","Resale Min%","Cancellation Window","Rescheduling Window",					 
					 "Quotation T&C","Allocation Part","Salesman Name","Sales Employee Code","Team",							 
					 "Ship To Party","Ship To Code","Material Type","Business Program Type","PP Schedule",						 
					 "MP Schedule","LOA","Quote Type","Reference Exchange Rate","Reference Exchange Currency","Remarks","Requester Reference",					 					 
					 "Copy To CS","SPR","Product Group 2",
					 "Required Qty","EAU","Target Resale","Competitor Information","Item Remarks",							 
					 "Reason For Refresh","First RFQ Code","Revert Version","RFQ Submission Date","Quote Release Date","Quoted By",					 
					 "RFQ Created By" ,"Last Update BMT" ,"Last Update By" ,
					 "BMT MFR DR#","BMT Suggest Cost","BMT Suggest Resale","BMT Suggest Margin","BMT MFR Quote #",
					 "BMT SQ Effective Date","BMT SQ Expiry Date","BMT Comments","BMT Quote Qty","BMT Suggest Cost (Non-USD)",
					 "BMT Suggest Resale(Non-USD)","BMT Currency","Exchange rate(Non-USD)","DP Reference ID","DP Reference Line ID"
				 };
		 
		        public static final String[] BMT_DOWNLOAD_TEMPLATE_HEADER_PARAMATERS = {
					 "quoteNumber","formNo","quoteStage","rfqStatus","bmtFlag","bmtPendingAt","mfr",
					 "requestedPn","quotedPn","sapPartNumber","soldToCustomerName","solToCustomerChineseName", 
					 "soldToCustomerCode","sapSoldToType","customerType","endCustomer","endCustomerCode",
					 "avnetQuotePrice","finalQuotationPrice","cost","resaleMargin","referenceMargin", 
					 "costIndicator", "priceValidity","quoteEffectiveDate","quoteExpiryDate","poExpiryDate", //for future					 					 
					 "shipmentValidity","drms","supplierDr", "drmsAuthorizedGP", "authorizedGPChangeReason",
					 "remarkChangeReason","drExpiryDate","enquirySegment","projectName","application","designRegion",
					 "designLocation","avnetRegionalDemandCreation","vendorDebit","vendorQuote","vendorQty",					 
					 "avnetQuotedQty","resaleIndicator","avnetQCComment","internalComment","internalTransferComment","pmoq",					 
					 "mpq","moq","mov","leadTime","multiUsage",					 
					 "qtyIndicator","resalesMax","resalesMin","cancellWindow","reshedulingWindow",
					 "termsAndConditions","allocationFlog","employeeName","salesman","team",					 					 
					 "shipToParty","shipToCode","materialType","program","ppSchedule",					 					 
					 "mpSchedule","loa","quoteType","exRateTo","currTo","remarks","yourReference",					 
					 "copyToCs","spr","productGroup",
					 "requiredQty","eau","targetResale","competitorInformation","ItemRemarks",					 
					 "reasonForRefresh","firstRfqCode","revertVersion","uploadTime","sendOutTime","quotedBy",					 
					 "createdBy" ,"lastBmtUpdate" ,"lastUpdateName" ,
					 "bmtMfrDrNo","bmtSuggestCost","bmtSuggestResale","bmtSuggestMargin","bmtMfrQuoteNo",
					 "bmtsqEffciveDate","bmtSQexpiryDate","bmtComments","bmtQuoteQty","bmtSugCostAltCurrency",
					 "bmtSugResaleAltCurrency","bmtCurrency","bmtExRateAltCurrency","dpReferenceId","dpReferenceLineId"
				 };
				 

				
				 public static final int[] BMT_DOWNLOAD_TEMPLATE_DATE_TYPE = {
					 2,2,2,2,2,2,2, //mfr
					 2,2,2,2,2, //solToCustomerChineseName
			         0,2,2,2,0, //endCustomerCode
			         1,1,1,2,2, //referenceMargin
					 2,2,3,3,3, //poExpiryDate
			         3,2,2,2,2, //authorizedGPChangeReason
			         2,3,2,2,2,2, //designRegion
					 2,2,2,2,0, //vendorQty
			         0,2,2,2,2,2, //pmoq
			         0,0,0,2,2,//multiUsage
					 2,1,1,0,0, //reshedulingWindow
			         2,2,2,2,2, //team
			         2,2,2,2,2, //ppSchedule
					 2,2,2,2,2,2,2, //yourReference
			         2,2,2, //productGroup
			         0,0,1,2,2, //ItemRemarks
					 2,2,2,4,4,2, //quotedBy
			         2,3,2, //lastUpdateName
			         2,2,2,2,2, //bmtMfrQuoteNo
			         3,3,2,2,2, //bmtSugCostAltCurrency
			         2,2,2,2,2 //dpReferenceLineId
				 };
				 
				
				 /**
				  * Quote Centre Report Template download 
				  */
				 public static final String[] QC_DOWNLOAD_TEMPLATE_HEADER_TITLE = {
					 "Used","Bal. Unconsumed Qty","Avnet Quote#","Form No","Quote Stage","RFQ Status","BMT Flag","Pending at","MFR",
					 "Avnet Quoted P/N","SAP Material Number", "Customer Name(Sold To Party)","Chinese Customer name(Sold To Party)","Sold To Code","SAP Sold To Type","End Customer",
					 "End Customer Code","Avnet Quoted Price","Final Quotation Price","Cost","Quote Margin","Quote Reference Margin","Cost Indicator", "Price Validity","Quotation Effective Date",
					 "Quote Expiry Date","PO Expiry Date","Shipment Validity","PMOQ","MPQ","MOQ",
					 "MOV","Lead Time(wks)","Multi-Usage","Qty Indicator","MFR Debit#",
					 "MFR Quote#","MFR Quote Qty","Avnet Quoted Qty","Resale Indicator","Resale Max%","Resale Min%",
					 "Cancellation Window","Rescheduling Window","Quotation T&C","Allocation Part","Avnet Quote Centre Comment",
					 "QC Comment (Avnet Internal Only)","PM Comment","Valid For Ordering","Invalid For Ordering (Reason)","Salesman Name",
					 "Sales Employee Code","Team","Ship To Party","Ship To Code","Customer Type",
					 "Material Type","Business Program Type","Segment","Project Name","Application","Design Region",
					 "Design Location","Design In Cat","DRMS Project ID","DRMS Authorized GP%", "Reasons for Authorized GP% Change","Remarks of Reason","DR Expiry Date","MP Schedule","PP Schedule",
					 "LOA","Quote Type","Reference Exchange Rate","Reference Exchange Currency","Remarks","Requester Reference",
					 "Copy To CS","Requested P/N","SPR","Product Group 2",
					 "Required Qty","EAU","Target Resale","Target Price Margin","MFR DR#","Competitor Information",
					 "Item Remarks","Reason For Refresh","First RFQ Code","Revert Version",
					 "RFQ Submission Date","Quote Release Date","Quoted By","RFQ Created By" ,
					 "BMT MFR DR#","BMT Suggest Cost","BMT Suggest Resale","BMT Suggest Margin","BMT MFR Quote #",
					 "BMT SQ Effective Date","BMT SQ Expiry Date","BMT Comments","BMT Quote Qty","BMT Suggest Cost (Non-USD)",
					 "BMT Suggest Resale(Non-USD)","BMT Currency","Exchange rate(Non-USD)","Last Update BMT" ,"Last Update By","DP Reference ID","DP Reference Line ID"
				 };

				 public static final String[] QC_DOWNLOAD_TEMPLATE_HEADER_PARAMATERS = {
					 "usedFlag","balanceUnconsumedQty","quoteNumber","formNo","quoteStage","rfqStatus","bmtFlag","bmtPendingAt","mfr", 
					 "quotedPn","sapPartNumber","soldToCustomerName","solToCustomerChineseName","soldToCustomerCode","sapSoldToType","endCustomer",
					 "endCustomerCode","avnetQuotePrice","finalQuotationPrice","cost","resaleMargin","referenceMargin","costIndicator", "priceValidity","quoteEffectiveDate",
					 "quoteExpiryDate","poExpiryDate","shipmentValidity","pmoq","mpq","moq",
					 "mov","leadTime","multiUsage","qtyIndicator","vendorDebit",
					 "vendorQuote","vendorQty","avnetQuotedQty","resaleIndicator","resalesMax","resalesMin",
					 "cancellWindow","reshedulingWindow","termsAndConditions","allocationFlog","avnetQCComment",
					 "internalComment","internalTransferComment","readyForOrder","readyForOrderNot","employeeName",
					 "salesman","team","shipToParty","shipToCode","customerType",
					 "materialType","program","enquirySegment","projectName","application","designRegion",
					 "designLocation","avnetRegionalDemandCreation","drms","drmsAuthorizedGP", "authorizedGPChangeReason","remarkChangeReason","drExpiryDate","mpSchedule","ppSchedule",
					 "loa","quoteType","exRateTo","currTo","remarks","yourReference",
					 "copyToCs","requestedPn","spr","productGroup",
					 "requiredQty","eau","targetResale","targetMargin","supplierDr","competitorInformation",
					 "ItemRemarks","reasonForRefresh", "firstRfqCode","revertVersion",
					 "uploadTime","sendOutTime","quotedBy","createdBy"  ,
					 "bmtMfrDrNo","bmtSuggestCost","bmtSuggestResale","bmtSuggestMargin","bmtMfrQuoteNo",
					 "bmtsqEffciveDate","bmtSQexpiryDate","bmtComments","bmtQuoteQty","bmtSugCostAltCurrency",
					 "bmtSugResaleAltCurrency","bmtCurrency","bmtExRateAltCurrency","lastBmtUpdate" ,"lastUpdateName","dpReferenceId","dpReferenceLineId"
				 };
				 
				 public static final int[] QC_DOWNLOAD_TEMPLATE_DATE_TYPE = {
					 2,2,2,2,2,2,2,2,2, //mfr
					 2,2,2,2,2,2,2, //endCustomer
			         2,1,1,1,2,2,2,2,3, //quoteEffectiveDate
			         3,3,3,2,0,0, //moq
			         0,2,2,2,2, //vendorDebit
			         2,0,2,2,1,1, //resalesMin
			         2,2,2,2,2, //avnetQCComment
			         2,2,2,2,2, //employeeName
			         2,2,2,2,2, //customerType
			         2,2,2,2,2,2, //designRegion
			         2,2,2,2,2,2,3,2,2, //ppSchedule
			         2,2,2,2,2,2, //yourReference
			         2,2,2,2, //productGroup
			         2,2,2,2,2,2, //competitorInformation
			         2,2,2,2,//revertVersion
			         4,4,2,2, //createdBy
			         2,2,2,2,2, //bmtMfrQuoteNo
			         3,3,2,2,2, //bmtSugCostAltCurrency
			         2,2,2,3,2,2,2 //dpReferenceLineId
				 };
				 
			
		
		public static final String BLANK_SIGN ="";
		public static final String SEMICOLON_SIGN ="";
		public static final String NULL_SIGN ="null";
		public static final String NO_SIGN ="NO";

		public static final String EXCEL_EXTENTION_XLS ="xls";
		public static final String EXCEL_EXTENTION_XLSX ="xlsx";
		
		
		public static final String EMAIL_ADDRESS_SUFFIX="@avnet.com";
		

		public static final String UNDER_LINE_SIGN= "_";
		
		public static final String CALL_FOR_SQ="Call for Special Quote";
		public static final DecimalFormat THOUSAND_FORMAT = new DecimalFormat("###,###");

		public static final String YES="YES";
		public static final String NO="NO";
		public static final String PLUS_SIGN ="+";
		public static final String MINUS_SIGN ="-";
		public static final String USER_FULL_NAME= "userfullname";
		
		public static final String OFFLINE_RPT_MODE_TENMINS ="TENMINS";
		public static final String OFFLINE_RPT_WEB_PROMO_SERVICE_BEAN_ID ="excelGenForWebProFacade";
		
		 /**
		  * Quote Centre Report Template download 
		  */
		 public static final String[] QC_PART_MASTER_DOWNLOAD_TEMPLATE_HEADER_TITLE = {
			 "MFR","PART NUMBER"
		 };
		 
		 public static final String[] QC_PART_MASTER_DOWNLOAD_TEMPLATE_HEADER_PARAMATERS = {
			 "mfr","partNumber"
		 };
		 
		 public static final int[] QC_PART_MASTER_DOWNLOAD_TEMPLATE_DATE_TYPE = {
			 2,2
		 };

}
