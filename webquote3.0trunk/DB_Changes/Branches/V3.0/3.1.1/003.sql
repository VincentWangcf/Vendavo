Insert into APP_MESSAGES (ID,LOCALE_ID,MESSAGE_CODE,MESSAGE) values((select max(id)+1 from APP_MESSAGES),
'en','wq.message.CanNotCreateQBQuote','Can not create Quote builder quote');
