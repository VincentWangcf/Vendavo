



Insert into WEBQUOTE.SYSTEM_CODE_MAINTENANCE  (ID,CATEGORY,VALUE,DESCRIPTION,CREATED,CREATED_BY,UPDATED_BY,LAST_UPDATED,VERSION,REGION) values (10200,'INIT_PRICE_TYPE_MAPPING_FROM','GIS-EM-ASIA-ECO-WEB@Avnet.com',null,sysdate,1,1,sysdate,1,'AEMC');
Insert into WEBQUOTE.SYSTEM_CODE_MAINTENANCE (ID,CATEGORY,VALUE,DESCRIPTION,CREATED,CREATED_BY,UPDATED_BY,LAST_UPDATED,VERSION,REGION) 
values (10201,'INIT_PRICE_TYPE_MAPPING_TO','Lenon.Yang@Avnet.com,Tough.Lin@Avnet.com,Demon.Chen@Avnet.com,Jason.Chen22@Avnet.com,June.Guo@Avnet.com',null,sysdate,1,1,sysdate,1,'AEMC');





create or replace PROCEDURE  INIT_PRICER_TYPE_MAPPING
IS
  v_date DATE;

BEGIN
  
  v_date := sysdate+ (15/24);
  DBMS_OUTPUT.PUT_LINE('START FROM ' || v_date);
 
  DELETE FROM PRICER_TYPE_MAPPING;

  
    INSERT INTO PRICER_TYPE_MAPPING 
    (mfr, part_number, biz_unit, version, create_date, update_date, has_normal_flag, has_contract_flag, has_program_flag, has_future_pricer)
  select mfr_name, m_pn, md_bu, 1, v_date, v_date, 0, 0, 0 ,0 from  
    (
      SELECT mfr.name as mfr_name, m.full_mfr_part_number as m_pn, md.biz_unit as md_bu from material_detail md
      left join material m on md.material_id = m.id
      left join manufacturer mfr on m.manufacturer_id = mfr.id
      group by mfr.name, m.full_mfr_part_number, md.biz_unit
    );
  
  
  UPDATE PRICER_TYPE_MAPPING ptm SET has_future_pricer = 1 WHERE exists (
    SELECT * from material_detail md
    left join material m on md.material_id = m.id
    left join manufacturer mfr on m.manufacturer_id = mfr.id
    where to_char(md.quotation_effective_date, 'YYYYMMDD') > to_char(v_date, 'YYYYMMDD') 
    and m.full_mfr_part_number = ptm.part_number
    and mfr.name = ptm.mfr
    and md.biz_unit = ptm.biz_unit
    group by mfr.name, m.full_mfr_part_number, md.biz_unit 
  );
 

  v_date := sysdate+ (15/24);
  DBMS_OUTPUT.PUT_LINE('END AT ' || v_date);
  
END INIT_PRICER_TYPE_MAPPING;
/
