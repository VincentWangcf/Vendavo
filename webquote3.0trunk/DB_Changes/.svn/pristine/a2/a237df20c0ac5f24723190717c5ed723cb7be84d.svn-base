/**if SPECIAL_MATERIAL no exist ,please run this sql  */
alter table MATERIAL add (SPECIAL_MATERIAL varchar2(4));

update schedule_config set value = 'STRING|NA|BOOLEAN|NA|STRING|STRING|NA|STRING|NA|STRING' where type ='MATERIAL' and key = 'PARAM_TYPE'
update schedule_config set value = 'setSapPartNumber|NA|setDeletionFlag|NA|setMfr|setPartNumber|NA|setSapProductCategory|NA|setSpecialMaterial' where type ='MATERIAL' and key = 'METHODS'
