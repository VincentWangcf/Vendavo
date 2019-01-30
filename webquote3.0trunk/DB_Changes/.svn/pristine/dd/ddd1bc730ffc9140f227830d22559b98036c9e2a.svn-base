INSERT INTO ROLE (ID, CREATED_ON,LAST_UPDATED_ON, VERSION, NAME,CREATED_BY,LAST_UDPATED_BY, ACTIVE, DESCRIPTION)
  VALUES ((select max(id) + 1 from role),sysdate+ (15/24), sysdate+ (15/24),  1, 'ROLE_BMT', 1,1,1, 'CREAT FROM BMT PROJECT' );

INSERT INTO APP_FUNCTION_ROLE_MAPPING VALUES ((SELECT ID FROM APP_FUNCTION WHERE NAME='Index Page'),(select ID from ROLE where NAME = 'ROLE_BMT') );



insert into app_function values ( (select max(id) from app_function)+1,'My Quote Search - BMT');
Insert into APP_RESOURCE (RESOURCE_TYPE,ID,PATH,APP_FUNCTION_ID,PATH_ORDER,VERSION) values ('SCREEN',(select max(id) from APP_RESOURCE)+1,'/RFQ/MyQuoteListForBMT.jsf',(SELECT ID FROM APP_FUNCTION WHERE NAME='My Quote Search - BMT'),1,1);
INSERT INTO APP_FUNCTION_ROLE_MAPPING VALUES ((SELECT ID FROM APP_FUNCTION WHERE NAME='My Quote Search - BMT'),(select ID from ROLE where NAME = 'ROLE_BMT') );
 

insert into app_function values ( (select max(id) from app_function)+1,'BMT Working Platform');
Insert into APP_RESOURCE (RESOURCE_TYPE,ID,PATH,APP_FUNCTION_ID,PATH_ORDER,VERSION) values ('SCREEN',(select max(id) from APP_RESOURCE)+1,'/BMTPlatform/BMTWorkingPlatform.jsf',(SELECT ID FROM APP_FUNCTION WHERE NAME='BMT Working Platform'),1,1);
INSERT INTO APP_FUNCTION_ROLE_MAPPING VALUES ((SELECT ID FROM APP_FUNCTION WHERE NAME='BMT Working Platform'),(select ID from ROLE where NAME = 'ROLE_BMT') ); 
  
insert into app_function(id,name) values((select max(id) +1 from app_function),'AuditLogReport');
insert into app_function_role_mapping (function_id,role_id) values((select max(id) from app_function),1);
insert into app_resource values('SCREEN', (select max(id)+1 from app_resource),'/Report/AuditLogReport.jsf',(select max(id) from app_function),1,1);

