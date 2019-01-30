alter table DP_RFQ modify  SOLD_TO_CUSTOMERNAME varchar2(256);
alter table QUOTE modify  SOLD_TO_CUSTOMER_NAME varchar2(256);
alter table QUOTE modify  SOLD_TO_CUSTOMER_NAME_CHINESE varchar2(256);
alter table QUOTE_ITEM modify  SOLD_TO_CUSTOMER_NAME varchar2(256);