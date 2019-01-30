create table dr_project_header_bak as select * from dr_project_header;
create table dr_project_customer_bak as select * from dr_project_customer;
create table dr_neda_header_bak as select * from dr_neda_header;
create table dr_neda_item_bak as select * from dr_neda_item;

create table system_code_maintenance_bak as select * from system_code_maintenance;

select count(*) from dr_project_header;
select count(*) from dr_project_header_bak;
select count(*) from dr_project_customer;
select count(*) from dr_project_customer_bak;
select count(*) from dr_neda_header;
select count(*) from dr_neda_header_bak;
select count(*) from dr_neda_item;
select count(*) from dr_neda_item_bak;


declare 
i number(2);
begin
for i in 1..45 loop
delete from dr_project_customer where rownum < 10000;
end loop;
end;

declare 
i number(2);
begin
for i in 1..88 loop
delete from dr_neda_item where rownum < 10000;
end loop;
end;

declare 
i number(2);
begin
for i in 1..88 loop
delete from dr_neda_header where rownum < 10000;
end loop;
end;

declare 
i number(2);
begin
for i in 1..88 loop
delete from dr_project_header where rownum < 10000;
end loop;
end;



ALTER TABLE DR_NEDA_HEADER ADD (PRODUCTION_DATE DATE );
ALTER TABLE DR_NEDA_ITEM ADD (SUPPLIER_CODE VARCHAR2(3) );
ALTER TABLE DR_NEDA_ITEM ADD (SUPPLIER_NAME VARCHAR2(30) );
ALTER TABLE DR_NEDA_ITEM ADD (APPLICATION VARCHAR2(10) );
ALTER TABLE DR_NEDA_ITEM ADD (APPLICATION_DESCRIPTION VARCHAR2(80) );
ALTER TABLE DR_NEDA_ITEM ADD (FAE_ID VARCHAR2(12) );
ALTER TABLE DR_NEDA_ITEM ADD (FAE_NAME VARCHAR2(30) );
ALTER TABLE DR_NEDA_ITEM ADD (STATUS VARCHAR2(2) );
ALTER TABLE DR_NEDA_ITEM ADD (DR_NUMBER VARCHAR2(60) );
ALTER TABLE DR_NEDA_ITEM ADD (CREATION_DATE DATE );



ALTER TABLE DR_PROJECT_HEADER DROP COLUMN PRODUCTION_DATE;
ALTER TABLE DR_PROJECT_HEADER DROP COLUMN APPLICATION ;
ALTER TABLE DR_PROJECT_HEADER DROP COLUMN APPLICATION_DESCRIPTION ;
ALTER TABLE DR_NEDA_HEADER DROP COLUMN SUPPLIER_CODE ;
ALTER TABLE DR_NEDA_HEADER DROP COLUMN SUPPLIER_NAME ;
ALTER TABLE DR_NEDA_HEADER DROP COLUMN DR_NUMBER ;
ALTER TABLE DR_NEDA_HEADER DROP COLUMN FAE_ID ;
ALTER TABLE DR_NEDA_HEADER DROP COLUMN FAE_NAME ;
ALTER TABLE DR_NEDA_HEADER DROP COLUMN CREATION_DATE;



ALTER TABLE DR_PROJECT_CUSTOMER DROP CONSTRAINT DR_PROJECT_CUSTOMER_PK1;

ALTER TABLE DR_PROJECT_CUSTOMER MODIFY (CUSTOMER_TYPE NULL);

ALTER TABLE DR_PROJECT_CUSTOMER ADD CONSTRAINT DR_PROJECT_CUSTOMER_PK1 PRIMARY KEY 
(
  PROJECT_ID 
, CUSTOMER_NUMBER 
, SALES_ORG 
);
ALTER TABLE DR_PROJECT_CUSTOMER DROP CONSTRAINT DR_PROJECT_CUSTOMER_FK3;





update schedule_config t set t.value ='setProjectId|setGroupProjectId|setProjectName|setProjectDescription|setName|setSalesOrgDescription' where  t.type = 'PROJECT_HEADER' and t.key='METHODS';
update schedule_config t set t.value ='LONG|INTEGER|STRING|STRING|FK|STRING' where  t.type = 'PROJECT_HEADER' and t.key='PARAM_TYPE';
update schedule_config t set t.value ='setProjectId|setNedaId|setProductionDate|setNedaDesignStage|setAssemblyQty' where  t.type = 'NEDA_HEADER' and t.key='METHODS';
update schedule_config t set t.value ='PK|PK|DATE|STRING|INTEGER' where  t.type = 'NEDA_HEADER' and t.key='PARAM_TYPE';
update schedule_config t set t.value ='setProjectId|setNedaId|setNedaLineNumber|setCreationDate|setSupplierCode|setSupplierName|setApplication|setApplicationDescription|setDrNumber|setPartMask|setMA|setCoreId|setQtyPerBoard|setAnnualQty|setSuggestedPrice|setCurrency|setFaeId|setFaeName|setStatus' where  t.type = 'NEDA_ITEM' and t.key='METHODS';
update schedule_config t set t.value ='PK|PK|PK|DATE|STRING|STRING|STRING|STRING|STRING|STRING|STRING|STRING|INTEGER|INTEGER|DOUBLE|STRING|STRING|STRING|STRING' where  t.type = 'NEDA_ITEM' and t.key='PARAM_TYPE';

update schedule_config t set t.value ='PK|PK|PK|STRING|STRING|STRING' where  t.type = 'PROJECT_CUSTOMER' and t.key='PARAM_TYPE';
update schedule_config t set t.value ='LONG|STRING|STRING|STRING' where  t.type = 'PROJECT_CUSTOMER' and t.key='PK_CLASS_PARAM_TYPE';
commit;
