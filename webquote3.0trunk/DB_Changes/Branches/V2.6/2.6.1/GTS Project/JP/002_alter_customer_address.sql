alter table WEBQUOTE.CUSTOMER_ADDRESS  rename column "ADDRESS_NAME3" to "STREET2";
alter table WEBQUOTE.CUSTOMER_ADDRESS  rename column "ADDRESS_NAME4" to "STREET3";
alter table WEBQUOTE.CUSTOMER_ADDRESS  add  CUSTOMER_NAME3 VARCHAR2(80 CHAR);
alter table WEBQUOTE.CUSTOMER_ADDRESS  add  CUSTOMER_NAME4 VARCHAR2(80 CHAR);