delete from schedule_config where type='MATERIAL'

Insert into schedule_config (ID,TYPE,KEY,VALUE) values (127,'MATERIAL','PARAM_TYPE','STRING|NA|BOOLEAN|NA|STRING|STRING|NA|STRING|NA|STRING');
Insert into schedule_config (ID,TYPE,KEY,VALUE) values (125,'MATERIAL','CLASS_NAME','com.avnet.emasia.webquote.entity.SapMaterial');
Insert into schedule_config (ID,TYPE,KEY,VALUE) values (124,'MATERIAL','FILE_NAME','WQ_MMR_GEN_');
Insert into schedule_config (ID,TYPE,KEY,VALUE) values (250,'MATERIAL','TABLES','MATERIAL');
Insert into schedule_config (ID,TYPE,KEY,VALUE) values (251,'MATERIAL','ADDI_JOB_SWITCH','Y');
Insert into schedule_config (ID,TYPE,KEY,VALUE) values (126,'MATERIAL','METHODS','setSapPartNumber|NA|setDeletionFlag|NA|setMfr|setPartNumber|NA|setSapProductCategory|NA|setSpecialMaterial');

Insert into EXPORT_TABLE (ID,TYPE,KEY,VALUE) values (258,'SPECIAL_CUSTOMER','CLASS_NAME','com.avnet.emasia.webquote.entity.SpecialCustomer');
Insert into EXPORT_TABLE (ID,TYPE,KEY,VALUE) values (259,'SPECIAL_CUSTOMER','FILE_NAME','WQ_SPEC_CUST_');
Insert into EXPORT_TABLE (ID,TYPE,KEY,VALUE) values (260,'SPECIAL_CUSTOMER','PARAM_TYPE','STRING|STRING|DATE|DATE');
Insert into EXPORT_TABLE (ID,TYPE,KEY,VALUE) values (261,'SPECIAL_CUSTOMER','METHODS','setSoldToCustomerNumber|setEndCustomerNumber|setValidTo|setValidFrom');
