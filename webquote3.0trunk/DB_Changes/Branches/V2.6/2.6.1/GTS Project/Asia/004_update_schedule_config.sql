update schedule_config t set t.value ='setCustomerNumber|setCountry|setCustomerName1|setCustomerName2|setAddressNumber|setTelephone|setFax|setVatRegistrationNumber|setAccountGroup|setRegionCode|setCity|setBlockFlag|setDeleteFlag|setCustomerName3|setCustomerName4' where  t.type = 'CUSTOMER' and t.key='METHODS';
update schedule_config t set t.value ='STRING|STRING|STRING|STRING|STRING|STRING|STRING|STRING|STRING|STRING|STRING|STRING|BOOLEAN|STRING|STRING' where  t.type = 'CUSTOMER' and t.key='PARAM_TYPE';
update schedule_config t set t.value ='setCustomerAddress|setLanguageCode|setCustomerName1|setCustomerName2|setCustomerName3|setCustomerName4|setSearchTerm1|setSearchTerm2|setStreet|setHouseNumber|setPostalCode|setCity|setCountry|setStreet2|setStreet3' where  t.type = 'CUSTOMER_ADDRESS' and t.key='METHODS';
update schedule_config t set t.value ='STRING|PK|STRING|STRING|STRING|STRING|STRING|STRING|STRING|STRING|STRING|STRING|STRING|STRING|STRING' where  t.type = 'CUSTOMER_ADDRESS' and t.key='PARAM_TYPE';
commit;