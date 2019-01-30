insert into app_function values ((select max(id)+1 from app_function),'QuoteRateRequest');
insert into app_function_role_mapping (function_id,role_id) values((select max(id) from app_function),5);
insert into app_resource values('SCREEN', (select max(id)+1 from app_resource),'/RFQ/QuoteRateRequestUpload.jsf',(select max(id) from app_function),1,1);
