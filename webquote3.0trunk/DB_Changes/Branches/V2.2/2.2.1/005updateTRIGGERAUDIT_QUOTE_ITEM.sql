create or replace TRIGGER AUDIT_QUOTE_ITEM 
BEFORE INSERT OR UPDATE OF QUOTED_MFR_ID,QUOTED_PART_NUMBER,QUOTE_NUMBER,COST,COST_INDICATOR,LEAD_TIME,MOQ,MOV,MPQ,PMOQ,QTY_INDICATOR,QUOTED_QTY,RESALE_INDICATOR,SHIPMENT_VALIDITY,STATUS,MIN_SELL_PRICE,NORM_SELL_PRICE,PRICE_VALIDITY,STAGE,FINAL_QUOTATION_PRICE,QUOTED_PRICE ON QUOTE_ITEM 
FOR EACH ROW 
BEGIN
    IF (:new.PMOQ = '0+') AND :new.MOQ is not null THEN 
      :new.PMOQ := concat(:new.MOQ, '+');
    END IF;
    INSERT INTO AUDIT_QUOTE_ITEM VALUES
    (
      :new.QUOTE_NUMBER
      ,SYSDATE
      ,:old.STAGE
      ,:new.STAGE
      ,:old.STATUS
      ,:new.STATUS
      ,:old.QUOTED_MFR_ID
      ,:new.QUOTED_MFR_ID
      ,:old.QUOTED_PART_NUMBER
      ,:new.QUOTED_PART_NUMBER
      ,:old.QUOTED_QTY
      ,:new.QUOTED_QTY
      ,:old.PMOQ
      ,:new.PMOQ
      ,:old.QTY_INDICATOR
      ,:new.QTY_INDICATOR
      ,:old.COST_INDICATOR
      ,:new.COST_INDICATOR
      ,:old.COST
      ,:new.COST
      ,:old.LEAD_TIME
      ,:new.LEAD_TIME
      ,:old.MPQ
      ,:new.MPQ
      ,:old.MOQ
      ,:new.MOQ
      ,:old.MOV
      ,:new.MOV
      ,:old.PRICE_VALIDITY
      ,:new.PRICE_VALIDITY
      ,:old.SHIPMENT_VALIDITY
      ,:new.SHIPMENT_VALIDITY
      ,:new.LAST_UPDATED_BY
      ,:new.QUOTE_ID
      ,:new.ID
      ,:old.resale_indicator
      ,:new.resale_indicator
      ,(SELECT form_number FROM quote WHERE id = :new.QUOTE_ID)
      ,(SELECT biz_unit FROM quote WHERE id = :new.QUOTE_ID)
      ,:old.quoted_price
      ,:new.quoted_price
      ,NVL2(:old.QUOTED_MFR_ID, (SELECT name FROM manufacturer WHERE id = :old.QUOTED_MFR_ID), :old.QUOTED_MFR_ID)
      ,NVL2(:new.QUOTED_MFR_ID, (SELECT name FROM manufacturer WHERE id = :new.QUOTED_MFR_ID), :new.QUOTED_MFR_ID)
      ,NVL2(:new.LAST_UPDATED_BY, (SELECT name FROM app_user WHERE EMPLOYEE_ID = :new.LAST_UPDATED_BY), :new.LAST_UPDATED_BY)
      ,NVL2(:new.LAST_UPDATED_BY, (SELECT employee_id FROM app_user WHERE EMPLOYEE_ID = :new.LAST_UPDATED_BY), :new.LAST_UPDATED_BY)      
    );
END;