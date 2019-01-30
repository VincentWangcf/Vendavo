Insert into app_function (ID,NAME) values ((select  max(id) from app_function )+1,'RfqSubmission Create Prospective Customer Button');


INSERT INTO app_resource (RESOURCE_TYPE,ID,PATH,APP_FUNCTION_ID,PATH_ORDER,VERSION)
  VALUES('ACTION',
    (SELECT MAX(id) FROM app_resource )+1,'RfqSubmission.createProspectiveCustomer.buttons',
    (SELECT t.id FROM app_function t
    WHERE t.name = 'RfqSubmission Create Prospective Customer Button'),1,1);
	
insert into APP_FUNCTION_ROLE_MAPPING 
values ((select id from app_function where name = 'RfqSubmission Create Prospective Customer Button'),
(select id from role where name = 'ROLE_QC_PRICING'));

insert into APP_FUNCTION_ROLE_MAPPING 
values ((select id from app_function where name = 'RfqSubmission Create Prospective Customer Button'),
(select id from role where name = 'ROLE_CS'));

insert into APP_FUNCTION_ROLE_MAPPING 
values ((select id from app_function where name = 'RfqSubmission Create Prospective Customer Button'),
(select id from role where name = 'ROLE_QC_OPERATION'));

insert into APP_FUNCTION_ROLE_MAPPING 
values ((select id from app_function where name = 'RfqSubmission Create Prospective Customer Button'),
(select id from role where name = 'ROLE_INSIDE_SALES'));

insert into APP_FUNCTION_ROLE_MAPPING 
values ((select id from app_function where name = 'RfqSubmission Create Prospective Customer Button'),
(select id from role where name = 'ROLE_SALES'));

insert into APP_FUNCTION_ROLE_MAPPING 
values ((select id from app_function where name = 'RfqSubmission Create Prospective Customer Button'),
(select id from role where name = 'ROLE_SALES_MANAGER'));

insert into APP_FUNCTION_ROLE_MAPPING 
values ((select id from app_function where name = 'RfqSubmission Create Prospective Customer Button'),
(select id from role where name = 'ROLE_SALES_DIRECTOR'));

insert into APP_FUNCTION_ROLE_MAPPING 
values ((select id from app_function where name = 'RfqSubmission Create Prospective Customer Button'),
(select id from role where name = 'ROLE_SALES_GM'));

insert into APP_FUNCTION_ROLE_MAPPING 
values ((select id from app_function where name = 'RfqSubmission Create Prospective Customer Button'),
(select id from role where name = 'ROLE_CS_MANAGER'));

insert into APP_FUNCTION_ROLE_MAPPING 
values ((select id from app_function where name = 'RfqSubmission Create Prospective Customer Button'),
(select id from role where name = 'ROLE_QCP_MANAGER'));

insert into APP_FUNCTION_ROLE_MAPPING 
values ((select id from app_function where name = 'RfqSubmission Create Prospective Customer Button'),
(select id from role where name = 'ROLE_QCO_MANAGER'));

insert into APP_FUNCTION_ROLE_MAPPING 
values ((select id from app_function where name = 'RfqSubmission Create Prospective Customer Button'),
(select id from role where name = 'ROLE_QC_Director'));


Insert into app_function (ID,NAME) values ((select  max(id) from app_function )+1,'WebPromo Create Prospective Customer Button');

INSERT INTO app_resource (RESOURCE_TYPE,ID,PATH,APP_FUNCTION_ID,PATH_ORDER,VERSION)
  VALUES('ACTION',
    (SELECT MAX(id) FROM app_resource )+1,'WebPromo.createProspectiveCustomer.buttons',
    (SELECT t.id FROM app_function t
    WHERE t.name = 'WebPromo Create Prospective Customer Button'),1,1);
	

insert into APP_FUNCTION_ROLE_MAPPING 
values ((select id from app_function where name = 'WebPromo Create Prospective Customer Button'),
(select id from role where name = 'ROLE_QC_PRICING'));

insert into APP_FUNCTION_ROLE_MAPPING 
values ((select id from app_function where name = 'WebPromo Create Prospective Customer Button'),
(select id from role where name = 'ROLE_PM'));

insert into APP_FUNCTION_ROLE_MAPPING 
values ((select id from app_function where name = 'WebPromo Create Prospective Customer Button'),
(select id from role where name = 'ROLE_CS'));

insert into APP_FUNCTION_ROLE_MAPPING 
values ((select id from app_function where name = 'WebPromo Create Prospective Customer Button'),
(select id from role where name = 'ROLE_QC_OPERATION'));

insert into APP_FUNCTION_ROLE_MAPPING 
values ((select id from app_function where name = 'WebPromo Create Prospective Customer Button'),
(select id from role where name = 'ROLE_INSIDE_SALES'));

insert into APP_FUNCTION_ROLE_MAPPING 
values ((select id from app_function where name = 'WebPromo Create Prospective Customer Button'),
(select id from role where name = 'ROLE_SALES'));

insert into APP_FUNCTION_ROLE_MAPPING 
values ((select id from app_function where name = 'WebPromo Create Prospective Customer Button'),
(select id from role where name = 'ROLE_SALES_MANAGER'));

insert into APP_FUNCTION_ROLE_MAPPING 
values ((select id from app_function where name = 'WebPromo Create Prospective Customer Button'),
(select id from role where name = 'ROLE_SALES_DIRECTOR'));

insert into APP_FUNCTION_ROLE_MAPPING 
values ((select id from app_function where name = 'WebPromo Create Prospective Customer Button'),
(select id from role where name = 'ROLE_SALES_GM'));

insert into APP_FUNCTION_ROLE_MAPPING 
values ((select id from app_function where name = 'WebPromo Create Prospective Customer Button'),
(select id from role where name = 'ROLE_CS_MANAGER'));

insert into APP_FUNCTION_ROLE_MAPPING 
values ((select id from app_function where name = 'WebPromo Create Prospective Customer Button'),
(select id from role where name = 'ROLE_PM_SPM'));

insert into APP_FUNCTION_ROLE_MAPPING 
values ((select id from app_function where name = 'WebPromo Create Prospective Customer Button'),
(select id from role where name = 'ROLE_PM_BUM'));

insert into APP_FUNCTION_ROLE_MAPPING 
values ((select id from app_function where name = 'WebPromo Create Prospective Customer Button'),
(select id from role where name = 'ROLE_PM_MD'));

insert into APP_FUNCTION_ROLE_MAPPING 
values ((select id from app_function where name = 'WebPromo Create Prospective Customer Button'),
(select id from role where name = 'ROLE_CM'));

insert into APP_FUNCTION_ROLE_MAPPING 
values ((select id from app_function where name = 'WebPromo Create Prospective Customer Button'),
(select id from role where name = 'ROLE_QCP_MANAGER'));

insert into APP_FUNCTION_ROLE_MAPPING 
values ((select id from app_function where name = 'WebPromo Create Prospective Customer Button'),
(select id from role where name = 'ROLE_QCO_MANAGER'));

insert into APP_FUNCTION_ROLE_MAPPING 
values ((select id from app_function where name = 'WebPromo Create Prospective Customer Button'),
(select id from role where name = 'ROLE_QC_Director'));




