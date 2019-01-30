update exchange_rate set EXCHANGE_RATE_TYPE ='CUSTOMER_FIXED_RATE' where SOLD_TO_CODE is not null;
update  exchange_rate set EXCHANGE_RATE_TYPE ='COMPANY_RATE' where SOLD_TO_CODE is  null;

---below sql added by dmaon

UPDATE APP_LABEL T SET T.MESSAGE='RFQ CUR' WHERE T.LOCALE_ID='en' AND T.MESSAGE_CODE='wq.label.rfqCurr';

COMMIT;