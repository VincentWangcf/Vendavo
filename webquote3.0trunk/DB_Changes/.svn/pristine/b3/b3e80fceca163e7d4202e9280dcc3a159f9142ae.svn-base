
--create view 
CREATE or replace VIEW EXCEL_REPORT_CONFIG_VIEW AS 
  SELECT s.*,c.BEAN_PROPERTY,r.REPORT_NAME FROM EXCEL_REPORT_SEQ s 
    LEFT JOIN EXCEL_REPORT_COLUMN c ON s.COLUMN_ID=c.ID
    LEFT JOIN EXCEL_REPORT r ON r.ID =  S.REPORT_ID;
    

--COLUMN
DECLARE 
  TYPE vc_ray IS VARRAY(5) OF VARCHAR2(50);
  heads vc_ray := vc_ray('$Link','BUY CUR','RFQ CURR','Target Resale at BUY CUR','Exchange Rate');
  propertys vc_ray := vc_ray('dollarLinkStr','buyCurrStr','rfqCurrStr','targetResaleStr_Buy','exRateStr');
  property VARCHAR2(50);
  head VARCHAR2(50);
  ct INTEGER;
BEGIN
  FOR i IN 1..heads.LIMIT() loop
    head := heads(i);
    property := propertys(i);
    SELECT count(1) INTO ct FROM EXCEL_REPORT_COLUMN WHERE BEAN_PROPERTY=property;
    --dbms_output.put_line('ct '|| ct);
    IF (ct < 1) THEN
      INSERT INTO EXCEL_REPORT_COLUMN(ID,HEADER_NAME,BEAN_PROPERTY,CELL_DATA_TYPE) VALUES 
        ((SELECT MAX(ID) + 2 FROM EXCEL_REPORT_COLUMN), head, property, '2');
    END IF;
  END loop;
end;

/

--SEQ
DECLARE 
  TYPE vc_ray IS VARRAY(5) OF VARCHAR2(50);
  heads vc_ray := vc_ray('$Link','BUY CUR','RFQ CURR','Target Resale at BUY CUR','Exchange Rate');
  propertys vc_ray := vc_ray('dollarLinkStr','buyCurrStr','rfqCurrStr','targetResaleStr_Buy','exRateStr');
  postionPrevPropertys vc_ray := vc_ray('quoteType','resaleIndicatorStr','quotedPartNumber','targetResale','targetResaleStr_Buy');
  property VARCHAR2(50);
  postionPrevProperty VARCHAR2(50);
  repo_name VARCHAR2(50) := 'QC_WORKINGPLATFORM';
  r_id NUMBER(18,0);
  c_id NUMBER(18,0);
  head VARCHAR2(50);
  seq_no_prev NUMBER(5,0);
  ct INTEGER;
  --EXCEL_REPORT_CONFIG_VIEW
BEGIN
  SELECT ID INTO r_id FROM EXCEL_REPORT WHERE REPORT_NAME=repo_name;
  
  FOR i IN 1..propertys.LIMIT() loop
    postionPrevProperty := postionPrevPropertys(i);
    property := propertys(i);
    head := heads(i);
    SELECT count(1) INTO ct FROM EXCEL_REPORT_CONFIG_VIEW 
      WHERE REPORT_ID=r_id AND BEAN_PROPERTY=property;
      
    --dbms_output.put_line('ct '|| ct);
    IF (ct < 1) THEN
      SELECT ID INTO c_id FROM EXCEL_REPORT_COLUMN WHERE BEAN_PROPERTY=property AND HEADER_NAME=head;
      SELECT SEQ_NO INTO seq_no_prev FROM EXCEL_REPORT_CONFIG_VIEW WHERE REPORT_ID=r_id AND BEAN_PROPERTY=postionPrevProperty;
      update EXCEL_REPORT_SEQ set SEQ_NO=SEQ_NO+1 where REPORT_ID=r_id AND SEQ_NO>seq_no_prev;
      INSERT INTO EXCEL_REPORT_SEQ(ID,REPORT_ID,COLUMN_ID,SEQ_NO) VALUES 
        ((SELECT MAX(ID) + 2 FROM EXCEL_REPORT_SEQ), r_id, c_id, seq_no_prev+1);
    END IF;
  END loop;
END;
/
COMMIT;
