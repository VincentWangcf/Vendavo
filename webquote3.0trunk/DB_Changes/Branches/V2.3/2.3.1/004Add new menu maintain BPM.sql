insert into role values((select max(id) from role)+1,systimestamp,systimestamp,1,'ROLE_VENDOR_QUOTE_ADMIN',1,1,1,'CREATE FOR STM B2B project');

insert into app_function(id,name) values ((select max(id) from app_function)+1 ,'Vendor Quote Maintenance');

insert into app_function(id,name) values ((select max(id) from app_function)+1,'Maintain Vendor Customer');

insert into APP_FUNCTION_ROLE_MAPPING 
values((select id from app_function where name = 'Vendor Quote Maintenance'), (select id from  role where name = 'ROLE_VENDOR_QUOTE_ADMIN'));

insert into APP_FUNCTION_ROLE_MAPPING 
values((select id from app_function where name = 'Maintain Vendor Customer'), (select id from  role where name = 'ROLE_VENDOR_QUOTE_ADMIN'));


