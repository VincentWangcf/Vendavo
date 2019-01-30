delete from QUANTITY_BREAK_PRICE 
where  MATERIAL_DETAIL_ID in (
  select aa3.detail_id 
  FROM(
  SELECT  md.id as detail_id , m.full_mfr_part_number,
      mfr.name,md.QUOTATION_EFFECTIVE_DATE, md.QUOTATION_EFFECTIVE_TO,md.COST_INDICATOR,
      md.created_on,md.last_updated_on,
      ROW_NUMBER ( ) OVER ( PARTITION BY md.biz_unit, md.material_category,md.material_id,md.COST_INDICATOR,md.DISPLAY_QUOTE_EFF_DATE, md.QUOTATION_EFFECTIVE_TO order by md.created_on DESC,md.last_updated_on DESC ) AS rn1,
      md.*
    FROM material m,
      MANUFACTURER mfr,
      MATERIAL_DETAIL MD,
      (SELECT md1.biz_unit,
        md1.material_category,
        md1.material_id,
        md1.COST_INDICATOR,
        md1.DISPLAY_QUOTE_EFF_DATE
      FROM MATERIAL_DETAIL md1
      WHERE 1                         =1
      AND md1.DISPLAY_QUOTE_EFF_DATE IS NULL
      GROUP BY md1.biz_unit,  md1.material_category,  md1.material_id,  md1.COST_INDICATOR, md1.DISPLAY_QUOTE_EFF_DATE, md1.QUOTATION_EFFECTIVE_TO
      having count(*)>1
      )sub1
    WHERE 1                        =1
    AND md.DISPLAY_QUOTE_EFF_DATE IS NULL
    AND m.MANUFACTURER_ID          = mfr.id
    AND m.id                       = md.material_id
    AND sub1.material_id           = md.material_id
    AND sub1.biz_unit              = md.biz_unit
    AND sub1.material_category     = md.material_category
    AND sub1.COST_INDICATOR        = md.COST_INDICATOR
    )aa3
  WHERE aa3.rn1 >= 2
)
;
delete from MATERIAL_DETAIL 
where id in (
  select aa3.detail_id 
  FROM(
  SELECT  md.id as detail_id , m.full_mfr_part_number,
      mfr.name,md.QUOTATION_EFFECTIVE_DATE, md.QUOTATION_EFFECTIVE_TO,md.COST_INDICATOR,
      md.created_on,md.last_updated_on,
      ROW_NUMBER ( ) OVER ( PARTITION BY md.biz_unit, md.material_category,md.material_id,md.COST_INDICATOR,md.DISPLAY_QUOTE_EFF_DATE, md.QUOTATION_EFFECTIVE_TO order by md.created_on DESC,md.last_updated_on DESC ) AS rn1,
      md.*
    FROM material m,
      MANUFACTURER mfr,
      MATERIAL_DETAIL MD,
      (SELECT md1.biz_unit,
        md1.material_category,
        md1.material_id,
        md1.COST_INDICATOR,
        md1.DISPLAY_QUOTE_EFF_DATE
      FROM MATERIAL_DETAIL md1
      WHERE 1                         =1
      AND md1.DISPLAY_QUOTE_EFF_DATE IS NULL
      GROUP BY md1.biz_unit,  md1.material_category,  md1.material_id,  md1.COST_INDICATOR, md1.DISPLAY_QUOTE_EFF_DATE, md1.QUOTATION_EFFECTIVE_TO
      having count(*)>1
      )sub1
    WHERE 1                        =1
    AND md.DISPLAY_QUOTE_EFF_DATE IS NULL
    AND m.MANUFACTURER_ID          = mfr.id
    AND m.id                       = md.material_id
    AND sub1.material_id           = md.material_id
    AND sub1.biz_unit              = md.biz_unit
    AND sub1.material_category     = md.material_category
    AND sub1.COST_INDICATOR        = md.COST_INDICATOR
    )aa3
  WHERE aa3.rn1 >= 2
)
