select * from system_code_maintenance where category = 'UPLOAD_FILE_SIZE_LIMIT';
-- if have record, skip below
select max(id) from system_code_maintenance;
insert into system_code_maintenance (id, category, value, description, created, created_by,updated_by, last_updated, version)
values(max(id+1), 'UPLOAD_FILE_SIZE_LIMIT', 5242880, 'UPLOAD_FILE_SIZE_LIMIT', sysdate, 1, 1, sysdate,1)
