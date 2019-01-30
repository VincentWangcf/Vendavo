package com.avnet.emasia.webquote.masterData.util;

/**
 * @author Tonmy,Li(906893)
 * @Created on 2013-01-22
 * 
 */
public class Constants {

	// ecoding
	public static final String IN_ECODING = "UTF-8";
	public static final String CN_ECODING = "GBK";
	
	
	// Common
	//----------------------------------------------------
	public static final String FILE_PATH="FILE_PATH";
	public static final String BACKUP_PATH="BACKUP_PATH";
	//Bryan Start
	public static final String JOB_TEMP_OUTPUT_PATH="JOB_TEMP_OUTPUT_PATH";
	//Bryan End
	//whwong Start
	public static final String ROW_LIMIT="ROW_LIMIT";
	//whwong End
	
	public static final String DATA_FILE_POSTFIX="DATA_FILE_POSTFIX";
	public static final String CONTROL_FILE_POSTFIX="CONTROL_FILE_POSTFIX";
	
	//mail service
	public static final String MAILSERVER="MAILSERVER";
	public static final String MAIL_SEND_FROM="MAIL_SEND_FROM";
	public static final String MAIL_SEND_TO="MAIL_SEND_TO";
	
	//setting 
	public static final String ALLOW_MUTIPLE_LOAD="ALLOW_MUTIPLE_LOAD";
	public static final String BACKUP_FLAG="BACKUP_FLAG";
	
	// Tables
	//--------------------------------------------------
	public static final String FILE_NAME="FILE_NAME";
	public static final String CLASS_NAME="CLASS_NAME";
	public static final String METHODS = "METHODS";
	public static final String PARAM_TYPE = "PARAM_TYPE";
	public static final String PK_CLASS= "PK_CLASS";
	public static final String PK_CLASS_METHODS = "PK_CLASS_METHODS";
	public static final String PK_CLASS_PARAM_TYPE = "PK_CLASS_PARAM_TYPE";
	public static final String FK_CLASS = "FK_CLASS";
	public static final String FK_CLASS_METHODS = "FK_CLASS_METHODS";
	public static final String FK_CLASS_PARAM_TYPE = "FK_CLASS_PARAM_TYPE";
	public static final String FK1_CLASS = "FK1_CLASS";
	public static final String FK1_CLASS_METHODS = "FK1_CLASS_METHODS";
	public static final String FK1_CLASS_PARAM_TYPE = "FK1_CLASS_PARAM_TYPE";
	public static final String FK2_CLASS = "FK2_CLASS";
	public static final String FK2_CLASS_METHODS = "FK2_CLASS_METHODS";
	public static final String FK2_CLASS_PARAM_TYPE = "FK2_CLASS_PARAM_TYPE";
	
	public static final String TYPE_INT = "INT";
	public static final String TYPE_STR = "STRING";
	public static final String TYPE_DOUBLE = "DOUBLE";
	public static final String TYPE_LONG = "LONG";
	public static final String TYPE_DATE = "DATE";
	public static final String TYPE_INTEGER="INTEGER";
	public static final String TYPE_BOOLEAN="BOOLEAN";
	public static final String TYPE_PK = "PK";
	public static final String TYPE_NA= "NA";
	public static final String TYPE_FK= "FK";
	public static final String TYPE_FK1= "FK1";
	public static final String TYPE_FK2= "FK2";
	
	public static final String SEPARATOR ="|";
	public static final String MASTER_LOADING_ERROR_EMAIL_SUBJECT ="Master data loading error notification";
	//Bryan Start
	public static final String MASTER_EXTRACT_TO_SAP_ERROR_EMAIL_SUBJECT ="Master data extractingto SAP error notification";
	//Bryan End
	
	public static final String OPERATION_INDICATOR_UPDATE = "I";
	public static final String OPERATION_INDICATOR_DELETE = "D";
	
	public static final String INIT_PRICE_TYPE_MAPPING_ERROR_EMAIL_SUBJECT ="Call price_type_mapping procedure error notification";

}
