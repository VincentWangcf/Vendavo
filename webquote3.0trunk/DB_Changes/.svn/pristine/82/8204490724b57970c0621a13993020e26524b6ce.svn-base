SET SERVEROUTPUT ON;
DECLARE
  v_tmp_record_v CUSTOMER_ADDRESS%rowtype;
  v_count NUMBER(6);
BEGIN
  v_count:=0;
  FOR v_tmp_record_v IN
  (
    SELECT   *
      FROM customer_address t
      WHERE TRUNC(created_on) <=to_date('20161105','yyyymmdd')
      ORDER BY CUSTOMER_NUMBER ,
        LANGUAGE_CODE
  )
  LOOP
    --CUSTOMER_NUMBER,LANGUAGE_CODE
    UPDATE customer_address t
      SET t.STREET2           =v_tmp_record_v.CUSTOMER_NAME3,
        t.STREET3             =v_tmp_record_v.CUSTOMER_NAME4,
        t.CUSTOMER_NAME3      =NULL,
        t.CUSTOMER_NAME4      =NULL
      WHERE t.CUSTOMER_NUMBER =v_tmp_record_v.CUSTOMER_NUMBER
      AND t.LANGUAGE_CODE     =v_tmp_record_v.LANGUAGE_CODE;
    v_count                  :=v_count+1;
  END LOOP;
  dbms_output.put_line('--total:'||v_count);
  commit;
EXCEPTION
WHEN OTHERS THEN
  dbms_output.put_line(' Error MSG:'||sqlerrm);
  ROLLBACK;
END;
