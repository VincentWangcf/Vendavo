CREATE TABLE PRICER_UPLOAD_SUMMARY 
(
  ID NUMBER NOT NULL 
, EMPLOYEE_ID VARCHAR2(10) 
,FILE_NAME VARCHAR2(200)
,content VARCHAR2(500)
, CREATED_ON DATE 
, BIZ_UNIT VARCHAR2(20) 
, CONSTRAINT PRICER_UPLOAD_SUMMARY_PK PRIMARY KEY 
  (
    ID 
  )
  ENABLE 
);