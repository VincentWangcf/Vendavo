package com.avnet.emasia.webquote.web.quote.constant;


public class QuoteConstant {
	
	public static final String DOMAIN = "emasiaweb-test.avnet.com";
    
	public static final String ACTION_COPY_QUOTE_HEADER = "CopyQuoteHeader";
	public static final String ACTION_CONTINUE_RFQ = "ContinueRfq";
	public static final String ACTION_RESUBMIT_INVALID_RFQ = "ReSubmitInvalidRfq";
	public static final String ACTION_INSTANT_PRICE_CHECK = "InstantPriceChecking";

	public static final String QUOTE_TEMPLATE_CN_LOCATION = "QuotationTemplate.xls";
	public static final String QUOTE_TEMPLATE_EN_LOCATION = "QuotationTemplateEn.xls";
	public static final String QUOTE_RATE_TEMPLATE = "Quote_Rate_Template.xlsx";
	public static final String WEB_PROMO_DOWNLOAD = "WebPromoDownload.xlsx";
	public static final String CONTRACT_PRICE_TEMPLATE_LOCATION = "ContractPriceUploadTemplate.xlsx";
	public static final String NORMAL_PRICER_TEMPLATE_LOCATION = "NormalPricerTemplate.xlsx";
	public static final String PROGRAM_PRICER_TEMPLATE_LOCATION = "ProgramPricerTemplate.xlsx";
	public static final String PENDING_LIST_TEMPLATE_LOCATION = "PendingList_Template.xlsx";
	public static final String VENDOR_REPORT_TEMPLATE_LOCATION = "VendorReport.xlsx";
	public static final String EXCHANGE_RATE_TEMPLATE_LOCATION = "ExchangeRateTemplate.xlsx";	
	public final static String TEMP_DIR = "TMP_DIRECTORY";
	public static final String WEBQUOTE2_DOMAIN ="WEBQUOTE2_DOMAIN";
	public static final String RIT_DATA_TEMPLATE="RITData_Template.xlsx";
	//WebQuote 2.2 enhancement :  fields changes. 
	public static final int QUOTE_ITEM_ROW_START = 12;
	public static final int QUOTE_TEMPLATE_HEADER_END = 9;
	public static final String AUTOCOMPLETE_SEPARATOR = " , ";
	public static final String SALES_ORG_SEPARATER = "/";
	
	public static final String DATE_FORMAT_MP_PP_SCHEUDLE = "MMM-yy";

	public static final String DEFAULT_NA = "NA";
	public static final String DEFAULT_BLANK = "";
	public static final String OPTION_YES = "Yes";
	public static final String OPTION_NO = "No";
	public static final String OPTION_ALL="*ALL";
	
	public static final String DEFAULT_SELECT = "-select-";
	public static final String DEFAULT_SELECT_EDIT = "-select or enter-";
	public static final String DEFAULT_SELECT_VALUE = " ";
	
	public static final String CUSTOMER_ENDCUSTOMER = "End Customer";
	public static final String CUSTOMER_SOLDTOCUSTOMER = "SoldTo Customer";
	public static final String CUSTOMER_SHIPTOCUSTOMER = "ShipTo Customer";
	public static final String CUSTOMER_PROSPECTIVECUSTOMER = "Prospective Customer";
	
	public static final String QUOTATION_WARNING_MISSING = " is blank, which is not allowed";
	
	public static final double MARGIN_LOWER_LIMIT = 0;
	public static final double MARGIN_UPPER_LIMIT = 30;
	
	public static final int QUOTED_QTY_UPPER_LIMIT = 250000;
	public static final String QUOTED_QTY_UPPER_LIMIT_STRING = "250,000";

	public static final String WP_WARN_MPQ = "[MPQ]";
	public static final String WP_WARN_MOQ = "[MOQ]";
	public static final String WP_WARN_MOV = "[MOV]";
	public static final String WP_WARN_LEADTIME = "[LEADTIME]";
	public static final String WP_WARN_COSTINDICATOR = "[COSTINDICATOR]";
	public static final String WP_WARN_PRICEVALIDITY = "[PRICEVALIDITY]";
	public static final String WP_WARN_COST = "[COST]";
	public static final String WP_WARN_QUOTEDPRICE = "[QUOTEDPRICE]";
	public static final String WP_WARN_MARGIN = "[MARGIN]";
	public static final String WP_WARN_QUOTEDQTY = "[QUOTEDQTY]";

	public static final String[] YES_NO = new String[] {
		OPTION_YES
		,OPTION_NO		
	};
	
	public static final String QTY_INDICATOR_MOQ = "MOQ";
	public static final String QTY_INDICATOR_MPQ = "MPQ";
	public static final String QTY_INDICATOR_MOV = "MOV";
	public static final String QTY_INDICATOR_EXACT = "Exact";
	public static final String QTY_INDICATOR_LIMIT = "Limit";
	public static final String[] QTY_INDICATORS = new String[] {
		"MOQ"
		,"MPQ"
		,"MOV"
		,"Exact"
		,"100%"
		,"75%"
		,"50%"		
		,"25%"
	};

	public static final String[] RFQ_STATUS_INDICATORS = new String[] {
		"QC"
		,"IT"
		,"SQ"
	};

	public static final String[] CUSTOMER_TYPE = new String[] {
		"EMS"
		,"OEM"
		,"ODM"
		,"Trader"
		,"End Customer"
		,"Others"		
	};
				
	public static final String[] TEAM = new String[] {
		"HK OEM"
		,"South OEM"
		,"North OEM"
		,"MCS-NC"
		,"MCS-SC"
		,"Channel-NC"
		,"Channel-SC"
		,"AMC-SC"
		,"AU"
		,"ASSC"
		,"BMT"
		,"YEL"
		,"AE"
		,"Others"
	};
	
	public static final String DEFAULT_COST_INDICATOR = "A-BOOK COST";
		
	public static final String CELL_YOUR_REFERENCE = "C2";
	public static final String CELL_EMPLOYEE_NAME = "C3";
	public static final String CELL_SOLD_TO_CUSTOMER_NAME = "C4";
	public static final String CELL_CUSTOMER_TYPE = "C5";
	public static final String CELL_ENQUIRY_SEGMENT = "C6";
	public static final String CELL_DESIGN_LOCATION = "C7";
	public static final String CELL_DRMS_NUMBER = "C8";
	public static final String CELL_PP_SCHEDULE = "C9";
	public static final String CELL_BOM = "C10";
	public static final String CELL_ORDER_ON_HAND = "C11";
	public static final String CELL_COPY_TO_CS = "C12";
	public static final String CELL_REMARKS = "C13";
	
	public static final String CELL_SALESMAN_CODE = "F2";
	public static final String CELL_TEAM = "F3";
	public static final String CELL_SOLD_TO_CUSTOMER_NUMBER = "F4";
	public static final String CELL_PROJECT_NAME = "F5";
	public static final String CELL_APPLICATION = "F6";
	public static final String CELL_AVNET_REGIONAL_DEMAND_CREATION = "F7";
	public static final String CELL_END_CUSTOMER = "F8";
	public static final String CELL_MP_SCHEDULE = "F9";
	public static final String CELL_LOA = "F10";
	public static final String CELL_BMT_CHECKED = "F11";
	public static final String CELL_ATTACHMENT = "F12";
	
	public static final String CELL_SPR = "B";
	public static final String CELL_MFR = "C";
	public static final String CELL_REQUESTED_PART_NUMBER = "D";
	public static final String CELL_REQUIRED_QTY = "E";
	public static final String CELL_EAU = "F";
	public static final String CELL_TARGET_RESALE = "G";
	public static final String CELL_SOLD_TO_CODE = "H";
	public static final String CELL_SHIP_TO_CODE = "I";
	
	public static final String CELL_COMPETITOR_INFORMATION = "I";
	public static final String CELL_SUPPLIER_DR_NUMBER = "J";
	public static final String CELL_ITEM_REMARKS = "K";
	
	public static final int RFQ_ITEM_START_ROW = 3;
	public static final int PRICE_BOOK_START_ROW = 2;

	public static final String CELL_PRICE_MFR = "A";
	public static final String CELL_PRICE_PRODUCT_GROUP = "B";
	public static final String CELL_PRICE_FULLMFRPART = "C";
	public static final String CELL_PRICE_DESCRIPTION = "D";
	public static final String CELL_PRICE_PRICELISTREMARK1 = "E";
	public static final String CELL_PRICE_PRICELISTREMARK2 = "F";
	public static final String CELL_PRICE_PRICELISTREMARK3 = "G";
	public static final String CELL_PRICE_PRICELISTREMARK4 = "H";
	public static final String CELL_PRICE_COST = "I";
	public static final String CELL_PRICE_MIN_SELL = "J";
	public static final String CELL_PRICE_NORM_SELL = "K";
	public static final String CELL_PRICE_LEAD_TIME = "L";
	public static final String CELL_PRICE_MPQ = "M";
	public static final String CELL_PRICE_MOQ = "N";
	public static final String CELL_PRICE_MOV = "O";
	public static final String CELL_PRICE_VALIDITY = "P";
	public static final String CELL_PRICE_SHIPMENT_VALIDITY = "Q";
	public static final String CELL_PRICE_COST_INDICATOR = "R";
	public static final String CELL_PRICE_TERMS_AND_CONDITIONS = "S";
	public static final String CELL_PRICE_PRODUCT_CATEGORY = "T";
	public static final String CELL_PRICE_VERSION = "U";
	
	public static final int DEFAULT_AUTOCOMPLETE_FIRST_RESULT = 0;
	public static final int DEFAULT_AUTOCOMPLETE_MAX_RESULTS = 30;

	public static final int CUSTOMER_NAME_LENGTH = 35;
	 
	public static final String PARA_MFR ="mfr";
	public static final String PARA_FULL_MFR_PART="fmp";

	public static final int ALERT_NUMBER_OF_RECIPIENT = 8;

	public static final String PART_SEPARATOR = " || ";
	public static final String PART_SPLIT_SEPARATOR = " \\|\\| ";
	
	public static final String MESSAGE_MISSING_MANDATORY_FIELD_FOR_SPR = "For SPR Item, Mandatory Field(s) is/are EMPTY :";
	public static final String MESSAGE_MISSING_MANDATORY_FIELD = "Mandatory Field(s) is/are EMPTY :";
	public static final String MESSAGE_INVALID_MFR_FIELD = "Found invalid MFR :";
	public static final String MESSAGE_FOLLOWING_RFQ_NOT_LOAD = ", and the following RFQs will be not loaded. ";
	public static final String MESSAGE_SAVE_DRAFT_SUCCESSFUL = "Draft saved successfully.";
	public static final String MESSAGE_INVALID_FORMAT = "Invalid Format :";
	public static final String MESSAGE_INVALID_TARGET_PRICE = "The price value must be in between 0 - 999,999,999.99999";
	public static final String MESSAGE_INCORRECT_MATCH_DRMS = "Found the invalid DRMS Project Id:";
	public static final String MESSAGE_USER_NOT_MAPPING = "It doesn't exist or is not your mapping sales";
	public static final String MESSAGE_USER_NOT_EXIST = "It doesn't exist or is not a sales";
	public static final String MESSAGE_DRMS_NOT_EXIST = "Found the invalid DRMS number:";
	public static final String MESSAGE_EAU_SMALLER_THAN_REQUIRED_QTY = "Found the EAU is smaller than Required Qty. Please adjust it.";
	public static final String MESSAGE_REQUIRED_QTY_CANNOT_EQUALS_TO_ZERO = "The required Qty can't be 0, Please adjust it";
	public static final String MESSAGE_REQUIRED_PART_EXCEED_LENGTH = "The length of Requested P/N is larger than 40 chars. Please adjust it.";
	
	public static final String MESSAGE_SPR_VALIDATION = "The SPR is/are removed because a target price is greater than or equal to a suggested price.";
	
	public final static String MESSAGE_SEARCH_CRITERIA = "All search criteria are empty";	
	public static final String MESSAGE_SAVED = "Save is completed :";
	public static final String MESSAGE_NOT_SAVED = "Quote is not saved";
	public static final String UPLOAD_SUCCESSFUL = "Upload Successfully";
	public static final String UPLOAD_FAILED = "Upload is Failed";

	public static final int MAX_PENDING_COL_NUMBER = 94 + 5;
	
	//report type
	public final static String DAILY_RIT_REPORT = "DAILY_RIT_REPORT";
	public final static String OUTSTANDING_IT_REPORT = "OUTSTANDING_IT_DRMS_REPORT";
	public final static String SALES_QUOTATION_PERFORMANCE = "SALES_QUOTATION_PERFORMANCE";
	public final static String[] REPORT_TYPE_ARR = { "DAILY_RIT_REPORT",
		"OUTSTANDING_IT_DRMS_REPORT","SALES_QUOTATION_PERFORMANCE" };
	public final static String ROLE_SALE_GM = "ROLE_SALES_GM";
	public final static String ROLE_SALE_DIRECTOR = "ROLE_SALES_DIRECTOR";
	public final static String ROLE_SALES = "ROLE_SALES";	
	public final static String ROLE_SALES_MANAGER = "ROLE_SALES_MANAGER";
	public final static String ROLE_INSIDE_SALES = "ROLE_INSIDE_SALES";
	public final static String RECEIVE_ALL_SALE_AUTHORISE = "901424";
	
	public final static String ALLOW_CHARACTER = "@ABCDEFGHIJKLMNOPQRSTUVWXYZ[\\]^_abcdefghijklmnopqrstuvwxyz{}!\"$%&'()+,-./0123456789< =>?";
	
	public final static String ALL = "ALL";
	
	public final static String BID_TO_BUY = "Bid-To-Buy";
	public final static String BID_TO_BID = "Bid-To-Bid";
	
	public final static String RFQ_SUBMISSION_DRMS_VALIDATION = "RFQ_SUBMISSION_DRMS_VALIDATION";
	
	public final static String RFQ_SUBMITTION_ALLOW_CREATE_PROSPECTIVE_CUSTOMER = "RfqSubmission.createProspectiveCustomer.buttons";

	public final static String WEBPROMO_ALLOW_CREATE_PROSPECTIVE_CUSTOMER = "WebPromo.createProspectiveCustomer.buttons";
	
	public final static String TEMPLATE_PATH = "TEMPLATE_PATH";

	//Off Line Report Name
	public final static String MYQUOTE_MM = "MYQUOTE_MM";
	public final static String MYQUOTE_QC = "MYQUOTE_QC";
	public final static String MYQUOTE_SALE = "MYQUOTE_SALE";
	public final static String MYQUOTE_CS = "MYQUOTE_CS";
	public final static String MYQUOTE_PM = "MYQUOTE_PM";
	public final static String MYQUOTE_BMT = "MYQUOTE_BMT";
	
	public final static String PART_MASTER = "PART_MASTER";
	
	public final static String QC_WORKINGPLATFORM = "QC_WORKINGPLATFORM";
	
	public final static String CATALOG_MAINTENANCE = "CATALOG_MAINTENANCE";
	
	public static final String CATALOG_MAIN_REPORT = "CATALOG_MAIN";

	
	public static final String[] COST_SOURCE = new String[] { "Normal Pricer", "Contract Pricer", "Simple Pricer",
			"Quote Record"

	};
	
	
	//sales cost constant
	public static final String[] SALES_COST_PART = new String[] { "ALL", "Yes", "No" };
	public  static final String INDICATOR_SUBSTR = "-";

	public static final String SALES_COST_TYPE_NONSC="NonSC";
	public static final String SALES_COST_TYPE_ZINC="ZINC";
	public static final String SALES_COST_TYPE_ZIND="ZIND";
	public static final String SALES_COST = "Sales Cost";

	public static final String NON_SALES_COST = "Non-Sales Cost";

	public static final String NON_SALES_COST_TYPE = "NonSC";

	
	public static final String SUCCESS_SALES_COST_FILE = "QB_ReportSalesCost";

	public static final String SUCCESS_NON_SALES_COST_FILE = "QB_ReportNonSalesCost";

	public static final String FAIL_SALES_COST_FILE = "QB_FailReportSalesCost";

	public static final String FAIL_NON_SALES_COST_FILE = "QB_FailReportNonSalesCost";

	public static final String SALES_COST_TEMPLATE = "WQ-QB_SalesCost_Template.xlsx";

	public static final String NON_SALES_COST_TEMPLATE = "WQ-QB_NonSalesCost_Template.xlsx";
	
	public static final String[] ROLE_QC = new String[] { "ROLE_QC_Director", "ROLE_QC_OPERATION", "ROLE_QC_PRICING"};
	
	public static final String[] ROLE_CENTRAL = new String[] { "ROLE_CENTRAL_MM", "ROLE_CENTRAL_MM_DIRECTOR", "ROLE_CENTRAL_MM_MANAGER"};

	/*The header line number */
	public final static int CATALOG_SEARCH_TEMPLATE_HEADER_ROW_NUMBER = 1;
	public final static int CATALOG_RESULT_TEMPLATE_HEADER_ROW_NUMBER = 1;
	public final static String CATALOG_SEARCH_RESULT = "CATALOG_SEARCH_RESULT";
	
	public final static int SHOPPING_CART_MAX_ITEMS=300;
	
	public final static String PRICER_ENQUIRY ="PRICER_ENQUIRY";
	
	public final static String COST_EXTRACTION_INFO ="COST_EXTRACTION_INFO";
	public final static String COST_EXTRACTION_COMPARISON ="COST_EXTRACTION_COMPARISON";

	public final static String[] REGION_NOT_DISPLAY = {"ASIA"};

	
	public final static String PRICE_VALIDITY_NUM_MAX ="PRICE_VALIDITY_NUM_MAX";
	
	public static final String QUOTE_STAGE_DRAFT = "DRAFT";
	public static final String QUOTE_STAGE_PENDING = "PENDING";
	public static final String QUOTE_STAGE_FINISH = "FINISH";
	public static final String QUOTE_STAGE_INVALID = "INVALID";
	public static final String QUOTE_STATE_PROG_DRAFT = "PROG_DRAFT";


}
