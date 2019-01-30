--[5.16.2 SAP Interface to WQ]

-->5.16.2.4: New Config Data this schedule file import function

insert into schedule_config (ID, TYPE, KEY, VALUE) values ((select max(ID)+1 from schedule_config), 
'SCP_BAL_QTY', 'FILE_NAME' , 'WQ_ZINM_QTY');


--5.16.2.5: New Config Data this schedule file import function
Insert into SCHEDULE_CONFIG (ID,TYPE,KEY,VALUE) values ((select max(id)+1 from schedule_config),'SAP_DPA_CUST','CLASS_NAME','com.avnet.emasia.webquote.entity.SapDpaCust');
Insert into SCHEDULE_CONFIG (ID,TYPE,KEY,VALUE) values ((select max(id)+1 from schedule_config),'SAP_DPA_CUST','FILE_NAME','WQ_CUSTGRP');
Insert into SCHEDULE_CONFIG (ID,TYPE,KEY,VALUE) values ((select max(id)+1 from schedule_config),'SAP_DPA_CUST','FK_CLASS','com.avnet.emasia.webquote.entity.BizUnit');
Insert into SCHEDULE_CONFIG (ID,TYPE,KEY,VALUE) values ((select max(id)+1 from schedule_config),'SAP_DPA_CUST','FK_CLASS_METHODS','setBizUnit');
Insert into SCHEDULE_CONFIG (ID,TYPE,KEY,VALUE) values ((select max(id)+1 from schedule_config),'SAP_DPA_CUST','FK_CLASS_PARAM_TYPE','STRING');
Insert into SCHEDULE_CONFIG (ID,TYPE,KEY,VALUE) values ((select max(id)+1 from schedule_config),'SAP_DPA_CUST','METHODS','setCustGroupId|setSalesOrg|setSoldToCustNumber|setShipToCustNumber|setEndCustNumber|setName|setName||');
Insert into SCHEDULE_CONFIG (ID,TYPE,KEY,VALUE) values ((select max(id)+1 from schedule_config),'SAP_DPA_CUST','PARAM_TYPE','STRING|STRING|STRING|STRING|STRING|FK|STRING|NA|NA');
