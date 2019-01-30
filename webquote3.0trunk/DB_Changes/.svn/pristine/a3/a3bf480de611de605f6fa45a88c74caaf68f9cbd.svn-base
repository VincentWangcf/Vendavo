
Insert into APP_MESSAGES (ID,LOCALE_ID,MESSAGE_CODE,MESSAGE) values((select max(id)+1 from APP_MESSAGES),
'en','wq.message.PriceValidityOutLimitAtRow','The value of Price Validity [{0}] is out of allowable limit at row {1}');

Insert into APP_MESSAGES (ID,LOCALE_ID,MESSAGE_CODE,MESSAGE) values((select max(id)+1 from APP_MESSAGES),
'en','wq.message.sapPNNotCorrect','SAP P/N input is incorrect');

ALTER TABLE MATERIAL DROP CONSTRAINT MATERIAL_UK1;
DROP INDEX MATERIAL_UK1;