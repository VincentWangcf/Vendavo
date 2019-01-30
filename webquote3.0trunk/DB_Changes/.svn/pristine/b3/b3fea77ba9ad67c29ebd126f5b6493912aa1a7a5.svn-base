create or replace PROCEDURE            "POS_SUMMARY_PROC" 
IS
  v_counter NUMBER;
  v_min NUMBER;
  v_max NUMBER;
  v_mean NUMBER;
  
  c_zero NUMBER;
  
  i_s0 VARCHAR(8);
  i_s1 VARCHAR(8);
  i_s2 VARCHAR(8);
  i_s3 VARCHAR(8);

  i_e0 VARCHAR(8);
  i_e1 VARCHAR(8);
  i_e2 VARCHAR(8);
  i_e3 VARCHAR(8);

BEGIN

  c_zero := 0;
  
  i_s0 := '20141228';
  i_s1 := '20140928';
  i_s2 := '20140628';
  i_s3 := '20140325';
  
  i_e0 := '20150328';
  i_e1 := '20141227';
  i_e2 := '20140927';
  i_e3 := '20140627';

  DELETE FROM pos_summary;
  --COMMIT;
  
  UPDATE pos set invoice_qty = -1 * invoice_qty, invoice_amount = -1 * invoice_amount where sign = '-' and invoice_qty > 0;
  --COMMIT;  
/* initial pos summary table */
  FOR cust_mat_rec IN
    (
      SELECT customer_number, customer_name, mfr, full_mfr_part_number 
      FROM pos
      WHERE billing_date >= to_date(i_s3,'YYYYMMDD')
      AND billing_date <= to_date(i_e0,'YYYYMMDD')
      GROUP BY customer_number, customer_name, mfr, full_mfr_part_number
    )
  LOOP
/*
    SELECT COUNT(*) INTO v_counter
    FROM POS_SUMMARY
    WHERE sold_to_customer_number = cust_mat_rec.customer_number
    AND customer_name = cust_mat_rec.customer_name
    AND mfr = cust_mat_rec.mfr
    AND part_number = cust_mat_rec.full_mfr_part_number;
    
    IF v_counter = c_zero THEN
*/    
      INSERT INTO pos_summary (sold_to_customer_number, customer_name, mfr, part_number)
      VALUES (cust_mat_rec.customer_number, cust_mat_rec.customer_name, cust_mat_rec.mfr, cust_mat_rec.full_mfr_part_number);
/*      
    END IF;
*/    
  END LOOP;  
  --COMMIT;

  FOR pos_rec IN
    (   
      SELECT
        customer_number
        ,customer_name
        ,mfr
        ,full_mfr_part_number
        ,SUM(INVOICE_QTY) AS "AS_TOTAL"
      FROM pos
      WHERE billing_date >= trunc(add_months(sysdate, -12),'MM')
      AND billing_date <= sysdate
      GROUP BY customer_number, mfr, full_mfr_part_number, customer_name
    )
  LOOP
    UPDATE pos_summary SET
      last_12_month_qty = pos_rec.AS_TOTAL 
    WHERE sold_to_customer_number = pos_rec.customer_number
      AND mfr = pos_rec.mfr
      AND part_number = pos_rec.full_mfr_part_number;
  END LOOP;
  --COMMIT;
  
/* insert last quarter 0 */ 
  FOR pos_rec IN
    (   
      SELECT
        customer_number
        ,customer_name
        ,mfr
        ,full_mfr_part_number
        ,SUM(INVOICE_QTY) AS "AS_TOTAL"
        ,MIN(INVOICE_AMOUNT/INVOICE_QTY) AS "AS_MIN"
        ,MAX(INVOICE_AMOUNT/INVOICE_QTY) AS "AS_MAX"
        ,AVG(INVOICE_AMOUNT/INVOICE_QTY) AS "AS_MEAN"
      FROM pos
      WHERE billing_date >= to_date(i_s0,'YYYYMMDD')
      AND billing_date <= to_date(i_e0,'YYYYMMDD')
      AND INVOICE_QTY > 0      
      GROUP BY customer_number, mfr, full_mfr_part_number, customer_name
    )
  LOOP
    UPDATE pos_summary SET
      last_0_min = pos_rec.AS_MIN, 
      last_0_max = pos_rec.AS_MAX,
      last_0_mean = pos_rec.AS_MEAN, 
      last_0_qty = pos_rec.AS_TOTAL 
    WHERE sold_to_customer_number = pos_rec.customer_number
      AND mfr = pos_rec.mfr
      AND part_number = pos_rec.full_mfr_part_number;
  END LOOP;
  --COMMIT;

/* insert last quarter 1 */ 
  FOR pos_rec IN
    (   
      SELECT
        customer_number
        ,customer_name
        ,mfr
        ,full_mfr_part_number
        ,SUM(INVOICE_QTY) AS "AS_TOTAL"
        ,MIN(INVOICE_AMOUNT/INVOICE_QTY) AS "AS_MIN"
        ,MAX(INVOICE_AMOUNT/INVOICE_QTY) AS "AS_MAX"
        ,AVG(INVOICE_AMOUNT/INVOICE_QTY) AS "AS_MEAN"
      FROM pos
      WHERE billing_date >= to_date(i_s1,'YYYYMMDD')
      AND billing_date <= to_date(i_e1,'YYYYMMDD')
      AND INVOICE_QTY > 0      
      GROUP BY customer_number, mfr, full_mfr_part_number, customer_name
    )
  LOOP
    UPDATE pos_summary SET
      last_1_min = pos_rec.AS_MIN, 
      last_1_max = pos_rec.AS_MAX,
      last_1_mean = pos_rec.AS_MEAN, 
      last_1_qty = pos_rec.AS_TOTAL 
    WHERE sold_to_customer_number = pos_rec.customer_number
      AND mfr = pos_rec.mfr
      AND part_number = pos_rec.full_mfr_part_number;
  END LOOP;
  --COMMIT;
   
/* insert last quarter 2 */ 
  FOR pos_rec IN
    (   
      SELECT
        customer_number
        ,customer_name
        ,mfr
        ,full_mfr_part_number
        ,SUM(INVOICE_QTY) AS "AS_TOTAL"
        ,MIN(INVOICE_AMOUNT/INVOICE_QTY) AS "AS_MIN"
        ,MAX(INVOICE_AMOUNT/INVOICE_QTY) AS "AS_MAX"
        ,AVG(INVOICE_AMOUNT/INVOICE_QTY) AS "AS_MEAN"
      FROM pos
      WHERE billing_date >= to_date(i_s2,'YYYYMMDD')
      AND billing_date <= to_date(i_e2,'YYYYMMDD')
      AND INVOICE_QTY > 0      
      GROUP BY customer_number, mfr, full_mfr_part_number, customer_name
    )
  LOOP
    UPDATE pos_summary SET
      last_2_min = pos_rec.AS_MIN, 
      last_2_max = pos_rec.AS_MAX,
      last_2_mean = pos_rec.AS_MEAN, 
      last_2_qty = pos_rec.AS_TOTAL 
    WHERE sold_to_customer_number = pos_rec.customer_number
      AND mfr = pos_rec.mfr
      AND part_number = pos_rec.full_mfr_part_number;
  END LOOP;
  --COMMIT;
  
/* insert last quarter 3 */ 
  FOR pos_rec IN
    (   
      SELECT
        customer_number
        ,customer_name
        ,mfr
        ,full_mfr_part_number
        ,SUM(INVOICE_QTY) AS "AS_TOTAL"
        ,MIN(INVOICE_AMOUNT/INVOICE_QTY) AS "AS_MIN"
        ,MAX(INVOICE_AMOUNT/INVOICE_QTY) AS "AS_MAX"
        ,AVG(INVOICE_AMOUNT/INVOICE_QTY) AS "AS_MEAN"
      FROM pos
      WHERE billing_date >= to_date(i_s3,'YYYYMMDD')
      AND billing_date <= to_date(i_e3,'YYYYMMDD')
      AND INVOICE_QTY > 0      
      GROUP BY customer_number, mfr, full_mfr_part_number, customer_name
    )
  LOOP
    UPDATE pos_summary SET
      last_3_min = pos_rec.AS_MIN, 
      last_3_max = pos_rec.AS_MAX,
      last_3_mean = pos_rec.AS_MEAN, 
      last_3_qty = pos_rec.AS_TOTAL 
    WHERE sold_to_customer_number = pos_rec.customer_number
      AND mfr = pos_rec.mfr
      AND part_number = pos_rec.full_mfr_part_number;
  END LOOP;
  --COMMIT;
  
END POS_SUMMARY_PROC;