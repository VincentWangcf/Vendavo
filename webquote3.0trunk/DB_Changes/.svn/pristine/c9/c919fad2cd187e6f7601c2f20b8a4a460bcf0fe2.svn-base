Insert into APP_MESSAGES (ID,LOCALE_ID,MESSAGE_CODE,MESSAGE) values((select max(id)+1 from APP_MESSAGES),
'en','wq.message.PriceValidityOutLimitAt','The value of Price Validity [{0}] is out of allowable limit,at');

Insert into APP_MESSAGES (ID,LOCALE_ID,MESSAGE_CODE,MESSAGE) values((select max(id)+1 from APP_MESSAGES),
'en','wq.message.PriceValidityOutLimit','The value of Price Validity is out of allowable limit.');

Insert into app_property (PROP_KEY,PROP_DESC,PROP_VALUE,PROP_RESERVED) values ('PRICE_VALIDITY_NUM_MAX','The allowable value of Price Validity number','1000','1');

Update App_Messages Set MESSAGE='The value of Price Validity [{0}] is out of allowable limit at ' where message_code='wq.message.PriceValidityOutLimitAt' and Locale_ID='en';


-----
