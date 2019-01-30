insert into app_function values((select max(id) +1 from app_function),'Part Master Enquiry');
insert into APP_FUNCTION_ROLE_MAPPING
values ((select id from app_function where name = 'Part Master Enquiry'), (select id from role where name ='ROLE_QC_OPERATION' ));



