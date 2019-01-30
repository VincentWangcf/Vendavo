/***************************************************************************
 Backup Customer tables for GTS Project
 Filename max length: 30 characters
 ***************************************************************************/

SET SERVEROUTPUT ON;
SET DEFINE OFF;

DECLARE

  cTIMESTAMP_FORMAT CONSTANT VARCHAR2(10 BYTE) := 'MM';  -- Timestamp format
  cBAK_FORMAT CONSTANT VARCHAR2(20 BYTE) := 'Z_';  -- Prefix for backup table name
  cDROP_TBL_DATE CONSTANT VARCHAR2(10 BYTE) := '07';  -- Date where the table is backup
  cCOMMENT CONSTANT VARCHAR2(100 BYTE) := 'Damon: Backup app setting tables for WQ upgrade phase 1 go live';  -- Comment to add on backup tables
  
  cCURDATE VARCHAR2(20 BYTE) := TO_CHAR(current_timestamp, cTIMESTAMP_FORMAT);
  cOldTbl VARCHAR2(50 BYTE);
  cNewTbl VARCHAR2(50 BYTE);
  sqlStr VARCHAR2(2556);
  sqlStr2 VARCHAR2(500);
  
  type vCharArrayType IS VARRAY(100) of VARCHAR2(50);
  arrBuf vCharArrayType;   -- Existing table name
  arrBufNew vCharArrayType;  -- Backup table name
  
  recCnt INTEGER;  -- Record Count

  BEGIN
    -- Input existing table name to backup
 arrBuf := vCharArrayType('APP_PROPERTY', 'system_code_maintenance','Schedule_Config');
-- Input respective backup table name in same order as "arrBuf"
arrBufNew := vCharArrayType('APP_PROPERTY', 'system_code_maintenance','Schedule_Config');
    recCnt := 0;
    
    FOR i in 1 .. arrBuf.count LOOP  
      BEGIN
        cOldTbl := arrBuf(i);

        -- *** BACKUP TABLES ***
        cNewTbl := cBAK_FORMAT || cCURDATE || '_' ||arrBufNew(i);
        sqlStr := 'create table ' || cNewTbl || ' as select * from ' || cOldTbl;
        sqlStr2 := 'COMMENT ON TABLE ' || cNewTbl || ' IS ''' || cCOMMENT ||'''';
        
        -- *** DROP BACKUP TABLES ***
        --cNewTbl := cBAK_FORMAT || cDROP_TBL_DATE || '_' || arrBufNew(i);
        --sqlStr := 'drop table ' || cNewTbl;
        
        DBMS_OUTPUT.PUT_LINE('sqlStr: ' || sqlStr); 
        EXECUTE IMMEDIATE sqlStr;
        
        EXECUTE IMMEDIATE sqlStr2;  -- To add comment to table
        
        COMMIT;
        
        recCnt := recCnt + 1;
      EXCEPTION          
          WHEN OTHERS THEN  -- Other exception
            DBMS_OUTPUT.PUT_LINE('*ERROR - TBL ' || cNewTbl ||': '||SQLCODE||' -ERROR- '||SQLERRM);
      END;      
    END LOOP;
    
    DBMS_OUTPUT.PUT_LINE('Total no. of backup tables : ' || recCnt); 
  END;
