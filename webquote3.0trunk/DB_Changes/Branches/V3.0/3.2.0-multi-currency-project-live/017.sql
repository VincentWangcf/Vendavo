Insert into APP_MESSAGES (ID,LOCALE_ID,MESSAGE_CODE,MESSAGE) values((select max(id)+1 from APP_MESSAGES),
'en','wq.message.addMfrDetailExists','The input [{0}], [{1}] & [{2}] already exists.');


----Need to update QUOTE_ITEM_BMT_VIEW

EX_RATE_TO  ---->EX_RATE_RFQ
CURR_TO    ----->RFQ_CURR