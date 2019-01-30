package com.avnet.emasia.webquote.utilities.common;

/**
 * the constant class which define so many common constant.
 * 
 * @author 914975
 */
public class Constants {

	// indicate the email is text email or html email.
	public static enum MAIL_TYPE {
		TEXTMAIL, HTMLMAIL
	};
	
	public static final String EMAIL_JNDI = "java:jboss/mail/Default";
	
	public static final String QUOTE_STAGE_DRAFT = "DRAFT";
	public static final String QUOTE_STAGE_PENDING = "PENDING";
	public static final String QUOTE_STAGE_FINISH = "FINISH";
	public static final String QUOTE_STAGE_INVALID = "INVALID";
	public static final String QUOTE_STATE_PROG_DRAFT = "PROG_DRAFT";

	public static final String RFQ_STATUS_AQ = "AQ";
	public static final String RFQ_STATUS_IT = "IT";
	public static final String RFQ_STATUS_RIT = "RIT";
	public static final String RFQ_STATUS_SQ = "SQ";
	public static final String RFQ_STATUS_QC = "QC";
	public static final String RFQ_STATUS_DQ = "DQ";
	
	public static final String BACKLOG_SEARCH_RPT_ROLE_TYPE_QC="QC";
	public static final String BACKLOG_SEARCH_RPT_ROLE_TYPE_SALES="SALES";
	public static final String BACKLOG_SEARCH_RPT_ROLE_TYPE_MGR="SALES_MANAGER";
	/** The Constant USER_SELECTED_LANGUAGE_KEY. */
	public static final String USER_SELECTED_LANGUAGE_KEY="selectedLanguage";
	/** The Constant JAPANESE_LOCALE_STRING. */
	public final static String JAPANESE_LOCALE_STRING="ja";
	public static final Long JAPANESE_LOCALE_ID = 2L;
	/** The Constant ENGLISH_LOCALE_STRING. */
	public final static String ENGLISH_LOCALE_STRING="en";
	public static final Long ENGLISH_LOCALE_ID = 1L;

}