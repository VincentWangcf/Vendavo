AlTER TABLE  RESTRICTED_CUSTOMER_MAPPING  ADD END_CUSTOMER_CODE  VARCHAR2(10 BYTE);
AlTER TABLE RESTRICTED_CUSTOMER_MAPPING ADD CREATED_ON  TIMESTAMP (6) default sysdate not null;
AlTER TABLE  RESTRICTED_CUSTOMER_MAPPING ADD LAST_UPDATED_ON TIMESTAMP (6)  default sysdate not null;
AlTER TABLE  RESTRICTED_CUSTOMER_MAPPING  ADD CREATED_BY  VARCHAR2(20 BYTE);
AlTER TABLE  RESTRICTED_CUSTOMER_MAPPING ADD LAST_UPDATED_BY VARCHAR2(20 BYTE); 
AlTER TABLE  RESTRICTED_CUSTOMER_MAPPING Drop column COST_INDICATOR;




Insert into APP_MESSAGES (ID,LOCALE_ID,MESSAGE_CODE,MESSAGE) values((select max(id)+1 from APP_MESSAGES),
'en','wq.message.restrictedCustomerFails','Sold To Code or End Customer Code fails the Restricted Customer checking at row [{0}]');

Insert into APP_MESSAGES (ID,LOCALE_ID,MESSAGE_CODE,MESSAGE) values((select max(id)+1 from APP_MESSAGES),
'en','wq.message.restrictedCustomerFailsWithoutRow','Sold To Code or End Customer Code fails the Restricted Customer checking');
commit;