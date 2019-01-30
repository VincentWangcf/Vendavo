
INSERT INTO APP_LABEL(ID, LOCALE_ID, MESSAGE_CODE, MESSAGE) VALUES ((select max(id)+1 from APP_LABEL), 'en', 'wq.label.buyCurrency', 'Buy Currency');
insert into app_label(id, locale_id, message_code, message) values ((select max(id) + 1 from app_label), 'en', 'wq.label.rounding', 'Rounding');
insert into app_label(id, locale_id, message_code, message) values ((select max(id) + 1 from app_label), 'en', 'wq.label.decimalPoint', 'Decimal Point');
insert into app_label(id, locale_id, message_code, message) values ((select max(id) + 1 from app_label), 'en', 'wq.label.rateRemark', 'Rate Remark');
insert into app_label(id, locale_id, message_code, message) values ((select max(id) + 1 from app_label), 'en', 'wq.label.exchangeRateType', 'Exchange Rate Type');

insert into app_messages(id, locale_id, message_code, message) values ((select max(id) + 1 from app_messages), 'en', 'wq.message.currencyFromIsnotUSD', 'Must input USD in Currency From field.');
insert into app_messages(id, locale_id, message_code, message) values ((select max(id) + 1 from app_messages), 'en', 'wq.message.roundingFieldIsnotBlankButDecimalPointIsBlank', 'Decimal Point cannot be blank if Rounding field is not blank.');
insert into app_messages(id, locale_id, message_code, message) values ((select max(id) + 1 from app_messages), 'en', 'wq.message.currencyToNotAllowed', 'The currency in Currency To column is not allowed for the selected region.');
insert into app_messages(id, locale_id, message_code, message) values ((select max(id) + 1 from app_messages), 'en', 'wq.message.customerFixedRateMustHaveValue', 'Customer Fixed Rate must have value in Sold To Code.');
insert into app_messages(id, locale_id, message_code, message) values ((select max(id) + 1 from app_messages), 'en', 'wq.message.duplicateExchangeRateTypeTocur', 'There are more than 1 Company Rate in the file with the same �To Curr�'); 
insert into app_messages(id, locale_id, message_code, message) values ((select max(id) + 1 from app_messages), 'en', 'wq.message.DecimalPointMustBeNumeric', 'Decimal point must be a numeric value from 0 to 5'); 
insert into app_messages(id, locale_id, message_code, message) values ((select max(id) + 1 from app_messages), 'en', 'wq.message.childDLinkquotes', 'Pending quote creation is not allowed for child $Link quotes.'); 
insert into app_messages(id, locale_id, message_code, message) values ((select max(id) + 1 from app_messages), 'en', 'wq.error.invalidPricerType', 'Invalid pricer type.');
insert into app_label(id, locale_id, message_code, message) values ((select max(id) + 1 from app_label), 'en', 'wq.label.buyCurInExcel', 'Buy Currency'); 

INSERT INTO EXCEL_REPORT_COLUMN (ID, HEADER_NAME, BEAN_PROPERTY, CELL_DATA_TYPE) VALUES (WQ_SEQ.NEXTVAL, 'Buy Currency', 'buyCurrency', '2');

--INSERT INTO EXCEL_REPORT_SEQ (ID, REPORT_ID, COLUMN_ID, SEQ_NO) VALUES ('10278190570', '10278190559', '10278190569', '42');
INSERT INTO EXCEL_REPORT_SEQ (ID, REPORT_ID, COLUMN_ID, SEQ_NO) VALUES (WQ_SEQ.NEXTVAL, (select id from EXCEL_REPORT where REPORT_NAME='CATALOG_MAIN'), (select id from EXCEL_REPORT_COLUMN where BEAN_PROPERTY='buyCurrency'), '42');


ALTER TABLE EXCHANGE_RATE ADD (Exchange_Rate_Type VARCHAR2(20));


 CREATE TABLE "WEBQUOTE"."EXCHANGE_RATE_HIS"   
 (
   "VALID_FROM" DATE, 
	"CURR_FROM" VARCHAR2(20 BYTE) NOT NULL ENABLE, 
	"CURR_TO" VARCHAR2(20 BYTE) NOT NULL ENABLE, 
	"EX_RATE_FROM" NUMBER(12,5), 
	"EX_RATE_TO" NUMBER(12,5), 
	"VAT" NUMBER(12,5), 
	"HANDLING" NUMBER(12,5), 
	"SOLD_TO_CODE" VARCHAR2(20 BYTE), 
	"MFR" VARCHAR2(20 BYTE), 
	"BIZ_UNIT" VARCHAR2(10 BYTE) NOT NULL ENABLE, 
	"ID" NUMBER NOT NULL ENABLE, 
	"LAST_UPDATED_BY" VARCHAR2(10 BYTE), 
	 "START_DATE" DATE,
	 "END_DATE" DATE,
	"VALID_TO" DATE NOT NULL ENABLE, 
	"ROUND_TYPE" NUMBER(1,0), 
	"SCALE" NUMBER(1,0), 
	"REMARKS" VARCHAR2(100 BYTE)); 
	
	
ALTER TABLE EXCHANGE_RATE_HIS ADD (Exchange_Rate_Type VARCHAR2(20));


CREATE TABLE STRATEGY 
(
  ID NUMBER(5) NOT NULL 
, KEY VARCHAR2(100) NOT NULL 
, STRATEGY_INTERFACE VARCHAR2(100) NOT NULL 
, STRATEGY_IMPLEMENTATION VARCHAR2(100) NOT NULL 
, CONSTRAINT STRATEGY_PK PRIMARY KEY  (ID )  ENABLE 
);
ALTER TABLE STRATEGY ADD CONSTRAINT STRATEGY_UK1 UNIQUE 
(  KEY , STRATEGY_INTERFACE) ENABLE;



INSERT INTO "WEBQUOTE"."STRATEGY" (ID, KEY, STRATEGY_INTERFACE, STRATEGY_IMPLEMENTATION) VALUES ('1', 'DEFAULT', 'com.avnet.emasia.webquote.quote.strategy.interfaces.DrmsValidationUpdateStrategy', 'com.avnet.emasia.webquote.quote.strategy.impl.DefaultDrmsValidationUpdateStrategy');
INSERT INTO "WEBQUOTE"."STRATEGY" (ID, KEY, STRATEGY_INTERFACE, STRATEGY_IMPLEMENTATION) VALUES ('2', 'CHINA', 'com.avnet.emasia.webquote.quote.strategy.interfaces.DrmsValidationUpdateStrategy', 'com.avnet.emasia.webquote.quote.strategy.impl.NoBlockDrmsValidationUpdateStrategy');
COMMIT;

delete  from exchange_rate where delete_flag ='1';


	
	