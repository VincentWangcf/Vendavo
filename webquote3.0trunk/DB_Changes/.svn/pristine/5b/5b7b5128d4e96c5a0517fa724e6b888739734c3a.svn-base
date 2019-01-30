ALTER TABLE MATERIAL  ADD (PACKAGE_TYPE VARCHAR(100),PACKAGING_TYPE VARCHAR(100));
insert into APP_LABEL(ID,LOCALE_ID,MESSAGE_CODE,MESSAGE) 
  values((select max(id)+1 from APP_LABEL),'en','wq.label.packageType','Package Type');
insert into APP_LABEL(ID,LOCALE_ID,MESSAGE_CODE,MESSAGE) 
  values((select max(id)+1 from APP_LABEL),'en','wq.label.packagingType','Packaging Type');
 commit;
 