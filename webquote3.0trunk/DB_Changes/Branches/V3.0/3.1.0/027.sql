insert into APP_FUNCTION_ROLE_MAPPING
values ((select id from app_function where name = 'QuoteBuilder'), (select id from role where name ='ROLE_QC_OPERATION' ));

insert into APP_FUNCTION_ROLE_MAPPING
values ((select id from app_function where name = 'QuoteBuilder'), (select id from role where name ='ROLE_PM' ));

insert into APP_FUNCTION_ROLE_MAPPING
values ((select id from app_function where name = 'QuoteBuilder'), (select id from role where name ='ROLE_QCO_MANAGER' ));

insert into APP_FUNCTION_ROLE_MAPPING
values ((select id from app_function where name = 'QuoteBuilder'), (select id from role where name ='ROLE_QC_Director' ));
COMMIT;




insert into APP_FUNCTION_ROLE_MAPPING
values ((select id from app_function where name = 'QuoteBuilder'), (select id from role where name ='ROLE_PM_BUM' ));
COMMIT;
insert into APP_FUNCTION_ROLE_MAPPING
values ((select id from app_function where name = 'QuoteBuilder'), (select id from role where name ='ROLE_PM_MD' ));
COMMIT;
insert into APP_FUNCTION_ROLE_MAPPING
values ((select id from app_function where name = 'QuoteBuilder'), (select id from role where name ='ROLE_PM_SPM' ));
COMMIT;
insert into APP_FUNCTION_ROLE_MAPPING
values ((select id from app_function where name = 'QuoteBuilder'), (select id from role where name ='ROLE_QC_PRICING' ));
COMMIT;
insert into APP_FUNCTION_ROLE_MAPPING
values ((select id from app_function where name = 'QuoteBuilder'), (select id from role where name ='ROLE_QCP_MANAGER' ));
COMMIT;
