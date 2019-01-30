
select * from system_code_maintenance where category = 'EDI_RECEIVE_PATH';
-- if have record, skip below

insert into system_code_maintenance (id, category, value, description, created, created_by,updated_by, last_updated, version)
values((select max(id) from system_code_maintenance)+1, 'EDI_RECEIVE_PATH', '/nas/extdata/GXS/outbound/webqasia/', 'EDI_RECEIVE_PATH', sysdate, 1, 1, sysdate,1);

select * from system_code_maintenance where category = 'STM_RECEIVE_PATH';
-- if have record, skip below

insert into system_code_maintenance (id, category, value, description, created, created_by,updated_by, last_updated, version)
values((select max(id) from system_code_maintenance)+1, 'STM_RECEIVE_PATH', '/app/eap62/webquote/b2bfiles/inbound/', 'STM_RECEIVE_PATH', sysdate, 1, 1, sysdate,1);


select * from system_code_maintenance where category = 'STM_SEND_PATH';
-- if have record, skip below 
insert into system_code_maintenance (id, category, value, description, created, created_by,updated_by, last_updated, version)
values((select max(id) from system_code_maintenance)+1, 'STM_SEND_PATH', '/app/eap62/webquote/b2bfiles/outbound/', 'STM_SEND_PATH', sysdate, 1, 1, sysdate,1);


select * from system_code_maintenance where category = 'STM_EDI_FTP_CONFIG';
-- if have record, skip below value host|port|username|password
--Please note in a different environment, different configuration information, because it contains host information
insert into system_code_maintenance (id, category, value, description, created, created_by,updated_by, last_updated, version)
values((select max(id) from system_code_maintenance)+1, 'STM_EDI_FTP_CONFIG', 'elko1|21|webqsftp|TBA', 'STM_EDI_FTP_CONFIG', sysdate, 1, 1, sysdate,1);


select * from system_code_maintenance where category = 'STM_FILE_BACKUP';
-- if have record, skip below 
insert into system_code_maintenance (id, category, value, description, created, created_by,updated_by, last_updated, version)
values((select max(id) from system_code_maintenance)+1, 'STM_FILE_BACKUP', '/app/eap62/webquote/b2bfiles/backup/', 'STM_FILE_BACKUP', sysdate, 1, 1, sysdate,1);

select * from system_code_maintenance where category = 'STM_EXCEPTION_PATH';
-- if have record, skip below 
insert into system_code_maintenance (id, category, value, description, created, created_by,updated_by, last_updated, version)
values((select max(id) from system_code_maintenance)+1, 'STM_EXCEPTION_PATH', '/app/eap62/webquote/b2bfiles/exception/', 'STM_EXCEPTION_PATH', sysdate, 1, 1, sysdate,1);


select * from system_code_maintenance where category = 'STM_ContactName';
-- if have record, skip below 
insert into system_code_maintenance (id, category, value, description, created, created_by,updated_by, last_updated, version)
values((select max(id) from system_code_maintenance)+1, 'STM_ContactName', 'season provide', 'STM_ContactName', sysdate, 1, 1, sysdate,1);


select * from system_code_maintenance where category = 'STM_ContactChannel';
-- if have record, skip below 
insert into system_code_maintenance (id, category, value, description, created, created_by,updated_by, last_updated, version)
values((select max(id) from system_code_maintenance)+1, 'STM_ContactChannel', 'Wait for season to provide it', 'STM_ContactChannel', sysdate, 1, 1, sysdate,1);
