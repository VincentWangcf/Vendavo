--=======-=====================QUOTE_ITEM==================================



---1.ALTER TABLE QUOTE_ITEM
--ALTER TABLE QUOTE_ITEM ADD FINAL_CURR VARCHAR2(20 BYTE) NOT NULL;
ALTER TABLE QUOTE_ITEM ADD FINAL_CURR VARCHAR2(20 BYTE) default 'USD' NOT NULL;

--AlTER TABLE QUOTE_ITEM MODIFY CURR_TO  VARCHAR2(20 BYTE) default 'USD' NOT NULL;
--AlTER TABLE QUOTE_ITEM MODIFY CURR_FROM  VARCHAR2(20 BYTE) default 'USD' NOT NULL;
AlTER TABLE QUOTE_ITEM RENAME COLUMN CURR_FROM TO BUY_CURR;
AlTER TABLE QUOTE_ITEM RENAME COLUMN CURR_TO TO RFQ_CURR;

AlTER TABLE QUOTE_ITEM RENAME COLUMN EX_RATE_TO TO EX_RATE_RFQ;
AlTER TABLE QUOTE_ITEM RENAME COLUMN EX_RATE_FROM TO EX_RATE_BUY;

ALTER TABLE QUOTE_ITEM ADD (RATE_REMARKS VARCHAR2(100) );
--no need this field
--ALTER TABLE QUOTE_ITEM ADD (EXCHANGE_RATE_TYPE VARCHAR2(20));
--ALTER TABLE QUOTE_ITEM ADD (EX_RATE_FNL);
ALTER TABLE QUOTE_ITEM ADD (EX_RATE_FNL NUMBER(12,5)); 
AlTER TABLE QUOTE_ITEM MODIFY quoted_price  NUMBER(20,10); 

--2.ALTER TABLE QUOTE

ALTER TABLE QUOTE ADD D_LINK_FLAG VARCHAR2(1 BYTE) DEFAULT '0' NOT NULL;




--=======-=====================QUOTE_ITEM_HIS==================================




--2.UPDATE RECORDS IN TABLE QUOTE_ITEM
---UPDATE QUOTE_ITEM_HIS T SET T.CURR_FROM='USD' WHERE T.CURR_FROM is null ;
----UPDATE QUOTE_ITEM_HIS T SET T.CURR_TO='USD' WHERE T.CURR_TO is null or t.CURR_TO=' ';

---COMMIT;

--1.ALTER TABLE QUOTE_ITEM
ALTER TABLE QUOTE_ITEM_HIS ADD FINAL_CURR VARCHAR2(20 BYTE) default 'USD' NOT NULL;


---AlTER TABLE QUOTE_ITEM_HIS MODIFY CURR_TO  VARCHAR2(20 BYTE) default 'USD' NOT NULL;
---AlTER TABLE QUOTE_ITEM_HIS MODIFY CURR_FROM  VARCHAR2(20 BYTE) default 'USD'  NOT NULL;
AlTER TABLE QUOTE_ITEM_HIS RENAME COLUMN CURR_FROM TO BUY_CURR;
AlTER TABLE QUOTE_ITEM_HIS RENAME COLUMN CURR_TO TO RFQ_CURR;

AlTER TABLE QUOTE_ITEM_HIS RENAME COLUMN EX_RATE_TO TO EX_RATE_RFQ;
AlTER TABLE QUOTE_ITEM_HIS RENAME COLUMN EX_RATE_FROM TO EX_RATE_BUY;

ALTER TABLE QUOTE_ITEM_HIS ADD (RATE_REMARKS VARCHAR2(100) );

ALTER TABLE QUOTE_ITEM_HIS ADD (EXCHANGE_RATE_TYPE VARCHAR2(20));
--ALTER TABLE QUOTE_ITEM_HIS ADD (EX_RATE_FNL);
ALTER TABLE QUOTE_ITEM_HIS ADD (EX_RATE_FNL NUMBER(12,5));


AlTER TABLE QUOTE_ITEM_his MODIFY quoted_price  NUMBER(20,10); 







--===Asia Webquote
----update quote_item t set t.AQCC=t.AQCC||' (Old Ref. Exc. CUR=RMB, Old Ref. Exc. Rate='||round((t.EX_RATE_RFQ*t.vat*t.HANDLING),5)||')' where 
---t.rfq_curr='RMB'  and   t.FINAL_CURR = 'USD' and t.EX_RATE_RFQ is not null 
---and t.vat is not null and t.HANDLING is not null;

--===JP Webquote
---update quote_item t set t.AQCC=t.AQCC||' (Old Ref. Exc. CUR=JPY, Old Ref. Exc. Rate='||t.EX_RATE_RFQ||')' where  t.rfq_curr='JPY'
---and   t.FINAL_CURR = 'USD' and t.EX_RATE_RFQ is not null;
--====both:
---UPDATE QUOTE_ITEM T SET T.RFQ_CURR='USD';


--3.UPDATE RECORDS IN TABLE QUOTE_ITEM

UPDATE QUOTE_ITEM  SET EX_RATE_RFQ=1 WHERE RFQ_CURR is null or RFQ_CURR=' ';
UPDATE QUOTE_ITEM SET EX_RATE_BUY=1;
UPDATE QUOTE_ITEM SET EX_RATE_FNL=EX_RATE_RFQ WHERE FINAL_QUOTATION_PRICE is not null;




UPDATE QUOTE_ITEM SET HANDLING=1  where RFQ_CURR is null or RFQ_CURR=' ';
UPDATE QUOTE_ITEM SET VAT=1  where RFQ_CURR is null or RFQ_CURR=' ';


UPDATE QUOTE_ITEM T SET T.BUY_CURR='USD';
--WHERE t.SUBMISSION_DATE <= to_date('20181028','yyyymmdd');

UPDATE QUOTE_ITEM T SET T.RFQ_CURR='USD' WHERE T.RFQ_CURR is null or t.RFQ_CURR=' ';

UPDATE QUOTE_ITEM T SET T.FINAL_CURR=T.RFQ_CURR;
--WHERE t.SUBMISSION_DATE <= to_date('20181028','yyyymmdd');


UPDATE quote_item t
SET t.TARGET_RESALE  = ROUND((t.EX_RATE_RFQ*t.TARGET_RESALE*t.HANDLING),5)
WHERE t.rfq_curr     ='RMB'
AND t.EX_RATE_RFQ   IS NOT NULL
AND t.TARGET_RESALE IS NOT NULL
AND t.HANDLING      IS NOT NULL;



AlTER TABLE QUOTE_ITEM MODIFY RFQ_CURR  VARCHAR2(20 BYTE) default 'USD' NOT NULL;
AlTER TABLE QUOTE_ITEM MODIFY BUY_CURR  VARCHAR2(20 BYTE) default 'USD' NOT NULL;


--AlTER TABLE QUOTE_ITEM_HIS MODIFY RFQ_CURR  VARCHAR2(20 BYTE) default 'USD' NOT NULL;
--AlTER TABLE QUOTE_ITEM_HIS MODIFY BUY_CURR  VARCHAR2(20 BYTE) default 'USD'  NOT NULL;