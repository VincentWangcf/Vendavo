CREATE TABLE PRICER_OFFLINE 
(
  ID NUMBER NOT NULL 
, ACTION VARCHAR2(100) NOT NULL 
, PRICER_TYPE VARCHAR2(20) NOT NULL 
, EMPLOYEE_ID VARCHAR2(10) 
,SEND_FLAG VARCHAR2(1)
, EMPLOYEE_NAME VARCHAR2(50) 
,FILE_NAME VARCHAR2(200) 
, CREATED_ON DATE 
, LAST_UPDATED_ON DATE 
, BIZ_UNIT VARCHAR2(20) 
, CONSTRAINT PRICER_OFFLINE_PK PRIMARY KEY 
  (
    ID 
  )
  ENABLE 
);


select * from system_code_maintenance where category = 'PRICER_UPLOAD_OFFLINE';
-- if have record, skip below
select max(id) from system_code_maintenance;
insert into system_code_maintenance (id, category, value, description, created, created_by,updated_by, last_updated, version)
values(max(id+1), 'PRICER_UPLOAD_OFFLINE', '/app/eap62/webquote/sapfiles/tempfile/', 'PRICER_UPLOAD_OFFLINE', sysdate, 1, 1, sysdate,1)