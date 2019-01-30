insert into APP_FUNCTION (ID, NAME) values ((select max(id) + 1 from APP_FUNCTION), 'Cost Extraction');

INSERT INTO app_function_role_mapping (function_id, role_id) VALUES ((SELECT id FROM app_function WHERE name = 'Cost Extraction'), (SELECT id FROM role WHERE name ='ROLE_CENTRAL_MM' ));
INSERT INTO app_function_role_mapping (function_id, role_id) VALUES ((SELECT id FROM app_function WHERE name = 'Cost Extraction'), (SELECT id FROM role WHERE name ='ROLE_CENTRAL_MM_DIRECTOR' ));
INSERT INTO app_function_role_mapping (function_id, role_id) VALUES ((SELECT id FROM app_function WHERE name = 'Cost Extraction'), (SELECT id FROM role WHERE name ='ROLE_CENTRAL_MM_MANAGER' ));
INSERT INTO app_function_role_mapping (function_id, role_id) VALUES ((SELECT id FROM app_function WHERE name = 'Cost Extraction'), (SELECT id FROM role WHERE name ='ROLE_CENTRAL_PRICING' ));
INSERT INTO app_function_role_mapping (function_id, role_id) VALUES ((SELECT id FROM app_function WHERE name = 'Cost Extraction'), (SELECT id FROM role WHERE name ='ROLE_CM' ));
INSERT INTO app_function_role_mapping (function_id, role_id) VALUES ((SELECT id FROM app_function WHERE name = 'Cost Extraction'), (SELECT id FROM role WHERE name ='ROLE_CM_MANAGER' ));
INSERT INTO app_function_role_mapping (function_id, role_id) VALUES ((SELECT id FROM app_function WHERE name = 'Cost Extraction'), (SELECT id FROM role WHERE name ='ROLE_MM' ));
INSERT INTO app_function_role_mapping (function_id, role_id) VALUES ((SELECT id FROM app_function WHERE name = 'Cost Extraction'), (SELECT id FROM role WHERE name ='ROLE_MM_DIRECTOR' ));
INSERT INTO app_function_role_mapping (function_id, role_id) VALUES ((SELECT id FROM app_function WHERE name = 'Cost Extraction'), (SELECT id FROM role WHERE name ='ROLE_MM_MANAGER' ));
INSERT INTO app_function_role_mapping (function_id, role_id) VALUES ((SELECT id FROM app_function WHERE name = 'Cost Extraction'), (SELECT id FROM role WHERE name ='ROLE_PM' ));
INSERT INTO app_function_role_mapping (function_id, role_id) VALUES ((SELECT id FROM app_function WHERE name = 'Cost Extraction'), (SELECT id FROM role WHERE name ='ROLE_PM_BUM' ));
INSERT INTO app_function_role_mapping (function_id, role_id) VALUES ((SELECT id FROM app_function WHERE name = 'Cost Extraction'), (SELECT id FROM role WHERE name ='ROLE_PM_MD' ));
INSERT INTO app_function_role_mapping (function_id, role_id) VALUES ((SELECT id FROM app_function WHERE name = 'Cost Extraction'), (SELECT id FROM role WHERE name ='ROLE_PM_SPM' ));
INSERT INTO app_function_role_mapping (function_id, role_id) VALUES ((SELECT id FROM app_function WHERE name = 'Cost Extraction'), (SELECT id FROM role WHERE name ='ROLE_QCO_MANAGER' ));
INSERT INTO app_function_role_mapping (function_id, role_id) VALUES ((SELECT id FROM app_function WHERE name = 'Cost Extraction'), (SELECT id FROM role WHERE name ='ROLE_QCP_MANAGER' ));
INSERT INTO app_function_role_mapping (function_id, role_id) VALUES ((SELECT id FROM app_function WHERE name = 'Cost Extraction'), (SELECT id FROM role WHERE name ='ROLE_QC_Director' ));
INSERT INTO app_function_role_mapping (function_id, role_id) VALUES ((SELECT id FROM app_function WHERE name = 'Cost Extraction'), (SELECT id FROM role WHERE name ='ROLE_QC_OPERATION' ));
INSERT INTO app_function_role_mapping (function_id, role_id) VALUES ((SELECT id FROM app_function WHERE name = 'Cost Extraction'), (SELECT id FROM role WHERE name ='ROLE_QC_PRICING' ));


insert into app_label(id, locale_id, message_code, message) values ((select max(id) + 1 from app_label), 'en', 'wq.label.costExtraction', 'Cost Extraction');


insert into app_messages(id, locale_id, message_code, message) values ((select max(id) + 1 from app_messages), 'en', 'wq.message.costExtract.selectRegion', 'Please select Region');
insert into app_messages(id, locale_id, message_code, message) values ((select max(id) + 1 from app_messages), 'en', 'wq.message.costExtract.selectCostSource', 'Please select Cost Source');
insert into app_messages(id, locale_id, message_code, message) values ((select max(id) + 1 from app_messages), 'en', 'wq.message.costExtract.selectMfr', 'Please select MFR');
insert into app_messages(id, locale_id, message_code, message) values ((select max(id) + 1 from app_messages), 'en', 'wq.message.costExtract.selectMfrMax', 'The MFR max selection is {0}');
insert into app_messages(id, locale_id, message_code, message) values ((select max(id) + 1 from app_messages), 'en', 'wq.message.costExtract.costComparisonInProgress', 'You have a report in process. Please wait for your report from email before submitting new request.');
insert into app_messages(id, locale_id, message_code, message) values ((select max(id) + 1 from app_messages), 'en', 'wq.message.costExtract.dateOverRange', 'Date range must not more than {0}.');
insert into app_messages(id, locale_id, message_code, message) values ((select max(id) + 1 from app_messages), 'en', 'wq.message.costExtract.quoteEffectiveDateFromRequired', 'Quote Effective Date From is required');


Insert into EXCEL_REPORT (ID,TEMPLATE_NAME,REPORT_NAME,SHEET_NO) values ((select max(id) + 1 from EXCEL_REPORT),'cost_extraction_info_template.xlsx','COST_EXTRACTION_INFO',0);
Insert into EXCEL_REPORT (ID,TEMPLATE_NAME,REPORT_NAME,SHEET_NO) values ((select max(id) + 1 from EXCEL_REPORT),'cost_extraction_comparison_template.xlsx','COST_EXTRACTION_COMPARISON',0);

CREATE TABLE "WEBQUOTE"."COST_COMPARISON_TEMP" 
   (	"ROW_NO" NUMBER(38,0), 
	"MFR" VARCHAR2(20), 
	"PART_NO" VARCHAR2(40), 
	"USER_ID" VARCHAR2(20)
     );
	 
CREATE INDEX COST_COMPARISON_TEMP_INDEX1 ON COST_COMPARISON_TEMP (MFR, PART_NO);
CREATE INDEX COST_COMPARISON_TEMP_INDEX2 ON COST_COMPARISON_TEMP (USER_ID);
CREATE INDEX COST_COMPARISON_TEMP_INDEX3 ON COST_COMPARISON_TEMP (ROW_NO);