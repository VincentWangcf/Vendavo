insert into app_function(id,name) values ((select  max(id) from app_function )+1,'version page') ;

insert into APP_FUNCTION_ROLE_MAPPING 
values ((select id from app_function where name = 'version page'),(select id from role where name = 'ROLE_SYS_ADMIN'))
