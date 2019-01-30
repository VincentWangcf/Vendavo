create or replace PROCEDURE "PM_EMAIL_ENQUIRY_PROC" (i_forms IN VARCHAR, i_quote_item_id IN VARCHAR)
IS
  o_employee_ids VARCHAR(10240);
  v_pm_employee_ids VARCHAR(4000);
  v_forms VARCHAR(1024);
  v_form_id VARCHAR(30);
  v_team_id VARCHAR(30);
  v_biz_unit VARCHAR(30);
  v_count NUMBER;
  v_quote_item_id VARCHAR(20);
  v_quote_forms apex_application_global.vc_arr2;
  v_quote_items apex_application_global.vc_arr2;
  
BEGIN

  --i_quote_item_id := @request;
  v_forms := '';
  v_biz_unit := '';  
  o_employee_ids := '';
  v_quote_forms := apex_util.string_to_table(i_quote_item_id, ';');

  FOR i in 1..v_quote_forms.count
  LOOP
    v_quote_items := apex_util.string_to_table(v_quote_forms(i), ',');
    v_pm_employee_ids := '';
    FOR j in 1..v_quote_items.count
    LOOP
    
      IF j = 1 THEN
        v_form_id := v_quote_items(j);
        v_pm_employee_ids := v_pm_employee_ids || v_form_id || ',';
      ELSIF j = 2 THEN
        v_team_id := v_quote_items(j);
      ELSIF j = 3 THEN
        v_biz_unit := v_quote_items(j);
      ELSE
        v_quote_item_id := v_quote_items(j);    
        FOR pm_rec IN
          (
            SELECT
              u.employee_id
            FROM app_user u
            JOIN user_dataaccess_mapping udm ON udm.user_id = u.id
            JOIN data_access da ON udm.data_access_id = da.id
            JOIN user_role_mapping urm ON urm.user_id = u.id 
            JOIN role r ON urm.role_id = r.id
            LEFT JOIN team t ON da.team_id = t.name            
            LEFT JOIN manufacturer m ON da.manufacturer_id = m.id            
            LEFT JOIN material_type mt ON da.material_type_id = mt.name            
            LEFT JOIN product_group pg ON da.product_group_id = pg.id            
            LEFT JOIN program_type pt ON da.program_type_id = pt.id            
            WHERE u.active = 1
            AND r.name = 'ROLE_PM'
            AND u.default_biz_unit_id = v_biz_unit
            AND (
              m.id = (SELECT quoted_mfr_id FROM quote_item WHERE id = v_quote_item_id)
              OR
              m.id is null
            )
            AND (
              t.name = v_team_id
              OR
              t.name IS NULL
            )
            AND (
              mt.name = (SELECT material_type_id FROM quote_item WHERE id = v_quote_item_id)
              OR
              mt.name IS NULL
            )       
            AND (
              pg.id = (SELECT product_group2_id FROM quote_item WHERE id = v_quote_item_id)
              OR
              pg.id IS NULL
            )
            AND (
              pt.id = (SELECT program_type_id FROM quote_item WHERE id = v_quote_item_id)
              OR
              pt.id IS NULL
            )            
            GROUP BY (u.employee_id)
          )
        LOOP
          v_pm_employee_ids := v_pm_employee_ids || pm_rec.employee_id || ',';
        END LOOP;
        
        SELECT COUNT(*) INTO v_count FROM pm_grouping WHERE forms = i_forms AND form_id = v_form_id AND biz_unit = v_biz_unit;        
        IF v_count = 0 THEN
          INSERT INTO pm_grouping (forms, form_id, biz_unit, pm_request) VALUES (i_forms, v_form_id, v_biz_unit, v_pm_employee_ids);        
        ELSE
          UPDATE pm_grouping SET pm_request = v_pm_employee_ids WHERE forms = i_forms and form_id = v_form_id AND biz_unit = v_biz_unit;
        END IF;
        
      END IF;
          
    END LOOP;
    --o_employee_ids := o_employee_ids || ';' || v_form_id || v_pm_employee_ids;

  END LOOP;

END PM_EMAIL_ENQUIRY_PROC;