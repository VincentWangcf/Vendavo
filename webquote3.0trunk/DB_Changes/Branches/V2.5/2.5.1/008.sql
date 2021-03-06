delete from APP_PROPERTY where prop_key in( 'WEBPROMO_LINKAGE_PATH','WEBPROMO_ITEM_LINKAGE_PATH');
insert into APP_PROPERTY values('WEBPROMO_LINKAGE_PATH','WEBPROMO_LINKAGE_PATH','/webquote/WebPromo/WebPromo.jsf',1)
insert into APP_PROPERTY values('WEBPROMO_ITEM_LINKAGE_PATH','WEBPROMO_ITEM_LINKAGE_PATH','/webquote/WebPromo/WebPromo.jsf?',1)


delete from SYSTEM_CODE_MAINTENANCE where CATEGORY = 'RFQ_SUBMISSION_TEMPLATE';
Insert into SYSTEM_CODE_MAINTENANCE (ID,CATEGORY,VALUE,DESCRIPTION,CREATED,CREATED_BY,UPDATED_BY,LAST_UPDATED,VERSION,REGION) values (10203,'RFQ_SUBMISSION_TEMPLATE','RFQ_Submission_Template_AEMC_V1.5.xlsx',null,sysdate,1,1,sysdate,1,'AEMC');
Insert into  SYSTEM_CODE_MAINTENANCE (ID,CATEGORY,VALUE,DESCRIPTION,CREATED,CREATED_BY,UPDATED_BY,LAST_UPDATED,VERSION,REGION) values (10204,'RFQ_SUBMISSION_TEMPLATE','RFQ_Submission_Template_AMC_V1.5.xlsx',null,sysdate,1,1,sysdate,1,'AMC');
Insert into SYSTEM_CODE_MAINTENANCE (ID,CATEGORY,VALUE,DESCRIPTION,CREATED,CREATED_BY,UPDATED_BY,LAST_UPDATED,VERSION,REGION) values (10205,'RFQ_SUBMISSION_TEMPLATE','RFQ_Submission_Template_ASEAN_V1.5.xlsx',null,sysdate,1,1,sysdate,1,'ASEAN');
Insert into SYSTEM_CODE_MAINTENANCE (ID,CATEGORY,VALUE,DESCRIPTION,CREATED,CREATED_BY,UPDATED_BY,LAST_UPDATED,VERSION,REGION) values (10206,'RFQ_SUBMISSION_TEMPLATE','RFQ_Submission_Template_INDIA_V1.5.xlsx',null,sysdate,1,1,sysdate,1,'INDIA');
Insert into SYSTEM_CODE_MAINTENANCE (ID,CATEGORY,VALUE,DESCRIPTION,CREATED,CREATED_BY,UPDATED_BY,LAST_UPDATED,VERSION,REGION) values (10207,'RFQ_SUBMISSION_TEMPLATE','RFQ_Submission_Template_TW_V1.5.xlsx',null,sysdate,1,1,sysdate,1,'TW');
Insert into SYSTEM_CODE_MAINTENANCE (ID,CATEGORY,VALUE,DESCRIPTION,CREATED,CREATED_BY,UPDATED_BY,LAST_UPDATED,VERSION,REGION) values (10208,'RFQ_SUBMISSION_TEMPLATE','RFQ_Submission_Template_KOREA_V1.5.xlsx',null,sysdate,1,1,sysdate,1,'KOREA');
Insert into SYSTEM_CODE_MAINTENANCE (ID,CATEGORY,VALUE,DESCRIPTION,CREATED,CREATED_BY,UPDATED_BY,LAST_UPDATED,VERSION,REGION) values (10209,'RFQ_SUBMISSION_TEMPLATE','RFQ_Submission_Template_ANZ_V1.5.xlsx',null,sysdate,1,1,sysdate,1,'ANZ');




update APP_PROPERTY set PROP_VALUE = 'https://emasiaweb.avnet.com/webquote' where prop_key = 'WEBQUOTE2_DOMAIN';
delete from APP_PROPERTY where prop_key in( 'RPT_APP_NAME','JMS_QUEUE_NAME');
insert into APP_PROPERTY values('RPT_APP_NAME','offline report','WebQuoteRptEar',1);
insert into APP_PROPERTY values('JMS_QUEUE_NAME','JMS QUUEUEName','/queue/offlineRptQueue',1)