
INSERT INTO SYSTEM_CODE_MAINTENANCE VALUES(
(select (max(id) +1) as newId from SYSTEM_CODE_MAINTENANCE),
'EXCEPTION_REPORT_AVNET_RECEIVER','WEBQUOTE-WEB-DEV@avnet.com','Please split the email box use ;',sysdate,1,1,sysdate,1,'AEMC')
;
--INSERT INTO SYSTEM_CODE_MAINTENANCE VALUES(
--(select (max(id) +1) as newId from SYSTEM_CODE_MAINTENANCE),
--'EXCEPTION_REPORT_ST_RECEIVER','june.guo@avnet.com;andy.hu@avnet.com','Please split the email box use ;',sysdate,1,1,sysdate,1,'AEMC')
--;
/****
For B2B recipients, you may include myself (Aron.Ong@Avnet.com) for test environment; 
please put EM-ASIA-IT-SUPPORT@Avnet.com for production environment.
****/
INSERT INTO SYSTEM_CODE_MAINTENANCE VALUES(
(select (max(id) +1) as newId from SYSTEM_CODE_MAINTENANCE),
'EXCEPTION_REPORT_B2B_RECEIVER','Aron.Ong@Avnet.com','Please split the email box use ;',sysdate,1,1,sysdate,1,'AEMC')
