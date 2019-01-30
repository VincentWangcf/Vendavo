create or replace PROCEDURE QUOTE_RESPONSE_DATA_PATCH AS 
pendingBy varchar2(20);updatePendingBy varchar2(20);last_updated_by varchar2(20);last_out_date date;last_updated_time date;old_status varchar2(20);old_stage varchar2(20);bak_table_name varchar2(40);bak_script varchar2(1000);update_script varchar2(1000);rb varchar2(10);
BEGIN
 DBMS_OUTPUT.PUT_LINE('data patch start: ' || sysdate); 
  select to_char(sysdate,'yyyyMMdd_HHmmss') into bak_table_name from dual;
  bak_script := 'create table qrth_' || bak_table_name || '_bak as SELECT * FROM QUOTE_RESPONSE_TIME_HISTORY'; 
  --where CREATED_TIME BETWEEN to_date(''2016-07-08 20:00'',''yyyy-MM-dd hh24:mi'') and to_date(''2016-07-19 20:00'',''yyyy-MM-dd hh24:mi'')
  DBMS_OUTPUT.PUT_LINE('back data script: ' || bak_script);  
  EXECUTE immediate bak_script;  
  DBMS_OUTPUT.PUT_LINE('back data end: ' || sysdate);
  delete FROM QUOTE_RESPONSE_TIME_HISTORY;
  commit;
 -- DBMS_OUTPUT.PUT_LINE('delete data');
  FOR quoteNumber_src IN (SELECT /*+ PARALLEL(quote_item_his 4) */ distinct(quote_number)  FROM quote_item_his) 
  LOOP
    --DBMS_OUTPUT.PUT_LINE('quote_number: ' || quoteNumber_src.quote_number);
    for quote_item_src in (select /*+ PARALLEL(quote_item_his 4) */ * from quote_item_his where quote_number = quoteNumber_src.quote_number and last_updated_by is not null order by version asc)
     loop
    -- DBMS_OUTPUT.PUT_LINE('quote_item_src: ' || quote_item_src.version || ' status: ' || quote_item_src.status);
      begin
        select status,stage into old_status,old_stage from quote_item_his where id = quote_item_src.id and version = (quote_item_src.version-1) and rownum=1;    
        EXCEPTION WHEN NO_DATA_FOUND THEN null;
        --DBMS_OUTPUT.PUT_LINE('not found old records: '|| quote_item_src.id || ' v: '||quote_item_src.version);
      end;
       -- DBMS_OUTPUT.PUT_LINE(' old_status: ' || old_status);
             --todo: insert if version =1
              if quote_item_src.version != 1 then              
              --  DBMS_OUTPUT.PUT_LINE('old_status - '|| old_status);
                if (old_status is null or old_status <> 'BQ') and quote_item_src.status = 'BQ' then pendingBy := 'BMT'; end if;              
                if (old_status is null or old_status != 'RBQ') and quote_item_src.status = 'RBQ'  then pendingBy := 'QC';updatePendingBy := 'BMT';rb := 'yes'; end if;
                if (old_status is null or old_status != 'RBIT') and quote_item_src.status = 'RBIT'  then pendingBy := 'QC';updatePendingBy := 'BMT';rb := 'yes';  end if;
                if (old_status is null or old_status != 'BIT') and quote_item_src.status = 'BIT'  then 
                  if(old_status = 'SQ') then updatePendingBy := 'SQ'; else updatePendingBy := 'QC';end if;
                  pendingBy := 'BMT';
                end if;           
                if (old_status is null or old_status != 'QC') and quote_item_src.status = 'QC'  then 
                  if old_status = 'IT' then updatePendingBy := 'PM';end if;
                  pendingBy := 'QC';
                end if;
                if (old_status is null or old_status != 'IT') and quote_item_src.status = 'IT'  then
                  if(old_status = 'SQ') then updatePendingBy := 'SQ'; else updatePendingBy := 'QC';end if;
                  pendingBy := 'PM';
                end if;
                if (old_status is null or old_status != 'RIT') and quote_item_src.status = 'RIT'  then  updatePendingBy := 'PM'; pendingBy := 'QC'; end if;
                if old_status != 'SQ' and quote_item_src.status = 'SQ' then pendingBy := 'SQ';updatePendingBy := 'QC'; end if;
                
                if ((old_stage is null or old_stage != 'FINISH') and quote_item_src.stage = 'FINISH') or ((old_stage is null or old_stage != 'INVALID') and quote_item_src.stage = 'INVALID') then 
                  if old_status = 'SQ' then
                    updatePendingBy := 'SQ'; 
                    else
                    updatePendingBy := 'QC'; 
                  end if;
                end if;                
              end if;
             
              if quote_item_src.version = 1 then
                if quote_item_src.status = 'BQ' then pendingBy := 'BMT'; end if;
                if quote_item_src.status = 'QC' then pendingBy := 'QC'; end if;
                if quote_item_src.status = 'RBQ' then pendingBy := 'QC'; end if;               
                if quote_item_src.status = 'IT' then pendingBy := 'PM'; end if;                 
              end if;
              --DBMS_OUTPUT.PUT_LINE('quote_item_src.status:'||quote_item_src.status||' - '||old_status||' pendingBy: '||pendingBy);
              if(pendingBy is not null) then
               insert /*+ PARALLEL(QUOTE_RESPONSE_TIME_HISTORY 4) */ into quote_response_time_history (id,created_by,created_time,last_in_date,last_updated_by,last_updated_time,pending_by,quote_item_id,version) 
               values (WQ_SEQ.NEXTVAL,quote_item_src.last_updated_by,quote_item_src.last_updated_on,quote_item_src.last_updated_on,quote_item_src.last_updated_by,quote_item_src.last_updated_on,pendingBy,quote_item_src.id,1);
               commit;
              --DBMS_OUTPUT.PUT_LINE(quote_item_src.last_updated_on|| '-' || quote_item_src.quote_number || ' v: ' || quote_item_src.version || ' - ' || quote_item_src.status || ' - ' || old_status ||' - '||pendingBy || '-' || updatePendingBy);
              end if;
              if(updatePendingBy is not null) then              
                if rb = 'yes' then update_script := 'update /*+ PARALLEL(QUOTE_RESPONSE_TIME_HISTORY 4) */ QUOTE_RESPONSE_TIME_HISTORY set VERSION = (VERSION+1),LAST_UPDATED_BY = '''||quote_item_src.last_updated_by||''','||' LAST_OUT_DATE = to_date('''||quote_item_src.last_updated_on||''',''yyyy/MM/dd HH24:MI:SS''),LAST_UPDATED_TIME = to_date('''||quote_item_src.last_updated_on||''',''yyyy/MM/dd HH24:MI:SS'') where id = (select id from (select ID from QUOTE_RESPONSE_TIME_HISTORY where LAST_OUT_DATE is null and PENDING_BY = '''||updatePendingBy||''' and QUOTE_ITEM_ID = '||quote_item_src.id||' order by LAST_OUT_DATE DESC) where rownum = 1)';
                  else update_script := 'update /*+ PARALLEL(QUOTE_RESPONSE_TIME_HISTORY 4) */ QUOTE_RESPONSE_TIME_HISTORY set VERSION = (VERSION+1),LAST_UPDATED_BY = '''||quote_item_src.last_updated_by||''','||' LAST_OUT_DATE = to_date('''||quote_item_src.last_updated_on||''',''yyyy/MM/dd HH24:MI:SS''),LAST_UPDATED_TIME = to_date('''||quote_item_src.last_updated_on||''',''yyyy/MM/dd HH24:MI:SS'') where id = (select id from (select ID from QUOTE_RESPONSE_TIME_HISTORY where PENDING_BY = '''||updatePendingBy||''' and QUOTE_ITEM_ID = '||quote_item_src.id||' order by LAST_OUT_DATE DESC) where rownum = 1)';                  
                end if;
                if update_script is not null then
                  --DBMS_OUTPUT.PUT_LINE(update_script);
                  EXECUTE immediate update_script;
                  commit;
                end if;
              end if; 
              commit;
              --DBMS_OUTPUT.PUT_LINE(quote_item_src.quote_number || ' v: ' || quote_item_src.version || ' - ' || quote_item_src.status || ' - ' || old_status ||' - '||pendingBy);
         old_status := null;
         pendingBy := null;
         old_stage := null;
         last_updated_by := null;
         last_out_date := null;
         last_updated_time := null;
         updatePendingBy := null;
         update_script :=null;
         rb := null;
     end loop;
    END LOOP;
    DBMS_OUTPUT.PUT_LINE('data patch end: ' || sysdate); 
END QUOTE_RESPONSE_DATA_PATCH;