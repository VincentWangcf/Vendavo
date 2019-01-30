
delete from SYSTEM_CODE_MAINTENANCE where CATEGORY = 'DRMS_VALIDATION_IMPL';
Insert into SYSTEM_CODE_MAINTENANCE (ID,CATEGORY,VALUE,DESCRIPTION,CREATED,CREATED_BY,UPDATED_BY,LAST_UPDATED,VERSION,REGION) values ((select max(id) + 1 from SYSTEM_CODE_MAINTENANCE),'DRMS_VALIDATION_IMPL','com.avnet.emasia.webquote.quote.strategy.impl.DefaultDrmsValidationUpdateStrategy',null,sysdate,1,1,sysdate,1,'DEFAULT');
Insert into  SYSTEM_CODE_MAINTENANCE (ID,CATEGORY,VALUE,DESCRIPTION,CREATED,CREATED_BY,UPDATED_BY,LAST_UPDATED,VERSION,REGION) values ((select max(id) + 1 from SYSTEM_CODE_MAINTENANCE),'DRMS_VALIDATION_IMPL','com.avnet.emasia.webquote.quote.strategy.impl.NoBlockDrmsValidationUpdateStrategy',null,sysdate,1,1,sysdate,1,'CHINA');
