---Quote Number
UPDATE QUOTE_NUMBER SET NEXT_NUMBER = 0000000 ,NUMBER_PATTERN = 'JP#######' WHERE DOC_TYPE = 'QUO';
UPDATE QUOTE_NUMBER SET NEXT_NUMBER = 0000000 ,NUMBER_PATTERN = 'JF#######' WHERE DOC_TYPE = 'FRM';
----Quotation email signature
Insert into SYSTEM_CODE_MAINTENANCE (ID,CATEGORY,VALUE,DESCRIPTION,CREATED,CREATED_BY,UPDATED_BY,LAST_UPDATED,VERSION,REGION) values (3155,'EMAIL_SIGNATURE_CONTENT','Asia-Japan Quote Center',null,SYSDATE,1,1,SYSDATE,1,'JAPAN');
Insert into SYSTEM_CODE_MAINTENANCE (ID,CATEGORY,VALUE,DESCRIPTION,CREATED,CREATED_BY,UPDATED_BY,LAST_UPDATED,VERSION,REGION) values (3163,'EMAIL_SIGNATURE_EMAIL_ADDRESS','Asia-Japan.QuoteCentre@Avnet.com',null,SYSDATE,1,1,SYSDATE,1,'JAPAN');
Insert into SYSTEM_CODE_MAINTENANCE (ID,CATEGORY,VALUE,DESCRIPTION,CREATED,CREATED_BY,UPDATED_BY,LAST_UPDATED,VERSION,REGION) values (3151,'EMAIL_SIGNATURE_HOTLINE','Hotline: (03) 5792 4503',null,SYSDATE,1,1,SYSDATE,1,'JAPAN');
Insert into SYSTEM_CODE_MAINTENANCE (ID,CATEGORY,VALUE,DESCRIPTION,CREATED,CREATED_BY,UPDATED_BY,LAST_UPDATED,VERSION,REGION) values (3159,'EMAIL_SIGNATURE_NAME','Japan Quote Centre',null,SYSDATE,1,1,SYSDATE,1,'JAPAN');
Insert into SYSTEM_CODE_MAINTENANCE (ID,CATEGORY,VALUE,DESCRIPTION,CREATED,CREATED_BY,UPDATED_BY,LAST_UPDATED,VERSION,REGION) values (13,'PROGRAM_EMAIL_FROM_COMMODITY',' ',null,SYSDATE,1,1,SYSDATE,1,'JAPAN');
Insert into SYSTEM_CODE_MAINTENANCE (ID,CATEGORY,VALUE,DESCRIPTION,CREATED,CREATED_BY,UPDATED_BY,LAST_UPDATED,VERSION,REGION) values (12,'PROGRAM_EMAIL_FROM_FIRE_SALES',' ',null,SYSDATE,1,1,SYSDATE,1,'JAPAN');
Insert into SYSTEM_CODE_MAINTENANCE (ID,CATEGORY,VALUE,DESCRIPTION,CREATED,CREATED_BY,UPDATED_BY,LAST_UPDATED,VERSION,REGION) values (11,'PROGRAM_EMAIL_TEAM_COMMODITY',' ',null,SYSDATE,1,1,SYSDATE,1,'JAPAN');
Insert into SYSTEM_CODE_MAINTENANCE (ID,CATEGORY,VALUE,DESCRIPTION,CREATED,CREATED_BY,UPDATED_BY,LAST_UPDATED,VERSION,REGION) values (10,'PROGRAM_EMAIL_TEAM_FIRE_SALES',' ',null,SYSDATE,1,1,SYSDATE,1,'JAPAN');
Insert into SYSTEM_CODE_MAINTENANCE (ID,CATEGORY,VALUE,DESCRIPTION,CREATED,CREATED_BY,UPDATED_BY,LAST_UPDATED,VERSION,REGION) values (15,'PROGRAM_EMAIL_TO_COMMODITY',' ',null,SYSDATE,1,1,SYSDATE,1,'JAPAN');
Insert into SYSTEM_CODE_MAINTENANCE (ID,CATEGORY,VALUE,DESCRIPTION,CREATED,CREATED_BY,UPDATED_BY,LAST_UPDATED,VERSION,REGION) values (14,'PROGRAM_EMAIL_TO_FIRE_SALES',' ',null,SYSDATE,1,1,SYSDATE,1,'JAPAN');


INSERT INTO MANUFACTURER VALUES('','ANC',(SELECT MAX(ID) FROM MANUFACTURER)+1,0 );
INSERT INTO MANUFACTURER VALUES('','MIC',(SELECT MAX(ID) FROM MANUFACTURER)+1,0 );
INSERT INTO MANUFACTURER VALUES('','MXM',(SELECT MAX(ID) FROM MANUFACTURER)+1,0 );
INSERT INTO MANUFACTURER VALUES('','NXP',(SELECT MAX(ID) FROM MANUFACTURER)+1,0 );
INSERT INTO MANUFACTURER VALUES('','ONU',(SELECT MAX(ID) FROM MANUFACTURER)+1,0 );
INSERT INTO MANUFACTURER VALUES('','PIT',(SELECT MAX(ID) FROM MANUFACTURER)+1,0 );
INSERT INTO MANUFACTURER VALUES('','RCC',(SELECT MAX(ID) FROM MANUFACTURER)+1,0 );
INSERT INTO MANUFACTURER VALUES('','XLX',(SELECT MAX(ID) FROM MANUFACTURER)+1,0 );
INSERT INTO MANUFACTURER VALUES('','XYZ',(SELECT MAX(ID) FROM MANUFACTURER)+1,0 );

SELECT * FROM MANUFACTURER_BIZUNIT_MAPPING

INSERT INTO MANUFACTURER_BIZUNIT_MAPPING VALUES((SELECT ID FROM MANUFACTURER WHERE NAME = 'ANC'),'JAPAN',NULL,NULL,NULL,0);
INSERT INTO MANUFACTURER_BIZUNIT_MAPPING VALUES((SELECT ID FROM MANUFACTURER WHERE NAME = 'MIC'),'JAPAN',NULL,NULL,NULL,0);
INSERT INTO MANUFACTURER_BIZUNIT_MAPPING VALUES((SELECT ID FROM MANUFACTURER WHERE NAME = 'MXM'),'JAPAN',NULL,NULL,NULL,0);
INSERT INTO MANUFACTURER_BIZUNIT_MAPPING VALUES((SELECT ID FROM MANUFACTURER WHERE NAME = 'NXP'),'JAPAN',NULL,NULL,NULL,0);
INSERT INTO MANUFACTURER_BIZUNIT_MAPPING VALUES((SELECT ID FROM MANUFACTURER WHERE NAME = 'ONU'),'JAPAN',NULL,NULL,NULL,0);
INSERT INTO MANUFACTURER_BIZUNIT_MAPPING VALUES((SELECT ID FROM MANUFACTURER WHERE NAME = 'PIT'),'JAPAN',NULL,NULL,NULL,0);
INSERT INTO MANUFACTURER_BIZUNIT_MAPPING VALUES((SELECT ID FROM MANUFACTURER WHERE NAME = 'RCC'),'JAPAN',NULL,NULL,NULL,0);
INSERT INTO MANUFACTURER_BIZUNIT_MAPPING VALUES((SELECT ID FROM MANUFACTURER WHERE NAME = 'XLX'),'JAPAN',NULL,NULL,NULL,0);
INSERT INTO MANUFACTURER_BIZUNIT_MAPPING VALUES((SELECT ID FROM MANUFACTURER WHERE NAME = 'XYZ'),'JAPAN',NULL,NULL,NULL,0);

INSERT INTO VALIDATION_RULE VALUES((SELECT MAX(ID) FROM VALIDATION_RULE) +1,(SELECT ID FROM MANUFACTURER WHERE NAME = 'ANC'),0,0,0,0,0,0,0,0,0,0,0,'JAPAN');
INSERT INTO VALIDATION_RULE VALUES((SELECT MAX(ID) FROM VALIDATION_RULE) +1,(SELECT ID FROM MANUFACTURER WHERE NAME = 'MIC'),0,0,0,0,0,0,0,0,0,0,0,'JAPAN');
INSERT INTO VALIDATION_RULE VALUES((SELECT MAX(ID) FROM VALIDATION_RULE) +1,(SELECT ID FROM MANUFACTURER WHERE NAME = 'MXM'),0,0,0,0,0,0,0,0,0,0,0,'JAPAN');
INSERT INTO VALIDATION_RULE VALUES((SELECT MAX(ID) FROM VALIDATION_RULE) +1,(SELECT ID FROM MANUFACTURER WHERE NAME = 'NXP'),0,0,0,0,0,0,0,0,0,0,0,'JAPAN');
INSERT INTO VALIDATION_RULE VALUES((SELECT MAX(ID) FROM VALIDATION_RULE) +1,(SELECT ID FROM MANUFACTURER WHERE NAME = 'ONU'),0,0,0,0,0,0,0,0,0,0,0,'JAPAN');
INSERT INTO VALIDATION_RULE VALUES((SELECT MAX(ID) FROM VALIDATION_RULE) +1,(SELECT ID FROM MANUFACTURER WHERE NAME = 'PIT'),0,0,0,0,0,0,0,0,0,0,0,'JAPAN');
INSERT INTO VALIDATION_RULE VALUES((SELECT MAX(ID) FROM VALIDATION_RULE) +1,(SELECT ID FROM MANUFACTURER WHERE NAME = 'RCC'),0,0,0,0,0,0,0,0,0,0,0,'JAPAN');
INSERT INTO VALIDATION_RULE VALUES((SELECT MAX(ID) FROM VALIDATION_RULE) +1,(SELECT ID FROM MANUFACTURER WHERE NAME = 'XLX'),0,0,0,0,0,0,0,0,0,0,0,'JAPAN');
INSERT INTO VALIDATION_RULE VALUES((SELECT MAX(ID) FROM VALIDATION_RULE) +1,(SELECT ID FROM MANUFACTURER WHERE NAME = 'XYZ'),0,0,0,0,0,0,0,0,0,0,0,'JAPAN');

INSERT INTO REFERENCE_MARGIN_SETTING VALUES ((SELECT ID FROM MANUFACTURER WHERE NAME = 'ANC'),'JAPAN',NULL,NULL,80,3,15,(SELECT nvl(MAX(ID),0) FROM REFERENCE_MARGIN_SETTING) +1);
INSERT INTO REFERENCE_MARGIN_SETTING VALUES ((SELECT ID FROM MANUFACTURER WHERE NAME = 'MIC'),'JAPAN',NULL,NULL,95,3,15,(SELECT MAX(ID) FROM REFERENCE_MARGIN_SETTING) +1);
INSERT INTO REFERENCE_MARGIN_SETTING VALUES ((SELECT ID FROM MANUFACTURER WHERE NAME = 'MXM'),'JAPAN',NULL,NULL,100,3,15,(SELECT MAX(ID) FROM REFERENCE_MARGIN_SETTING) +1);
INSERT INTO REFERENCE_MARGIN_SETTING VALUES ((SELECT ID FROM MANUFACTURER WHERE NAME = 'NXP'),'JAPAN',NULL,NULL,80,3,15,(SELECT MAX(ID) FROM REFERENCE_MARGIN_SETTING) +1);
INSERT INTO REFERENCE_MARGIN_SETTING VALUES ((SELECT ID FROM MANUFACTURER WHERE NAME = 'ONU'),'JAPAN',NULL,NULL,80,3,15,(SELECT MAX(ID) FROM REFERENCE_MARGIN_SETTING) +1);
INSERT INTO REFERENCE_MARGIN_SETTING VALUES ((SELECT ID FROM MANUFACTURER WHERE NAME = 'PIT'),'JAPAN',NULL,NULL,80,3,15,(SELECT MAX(ID) FROM REFERENCE_MARGIN_SETTING) +1);
INSERT INTO REFERENCE_MARGIN_SETTING VALUES ((SELECT ID FROM MANUFACTURER WHERE NAME = 'RCC'),'JAPAN',NULL,NULL,70,3,15,(SELECT MAX(ID) FROM REFERENCE_MARGIN_SETTING) +1);
INSERT INTO REFERENCE_MARGIN_SETTING VALUES ((SELECT ID FROM MANUFACTURER WHERE NAME = 'XLX'),'JAPAN',NULL,NULL,80,3,15,(SELECT MAX(ID) FROM REFERENCE_MARGIN_SETTING) +1);
INSERT INTO REFERENCE_MARGIN_SETTING VALUES ((SELECT ID FROM MANUFACTURER WHERE NAME = 'XYZ'),'JAPAN',NULL,NULL,80,3,15,(SELECT MAX(ID) FROM REFERENCE_MARGIN_SETTING) +1);

