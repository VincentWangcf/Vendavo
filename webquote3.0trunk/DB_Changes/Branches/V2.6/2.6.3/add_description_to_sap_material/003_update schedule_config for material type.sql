UPDATE schedule_config t
  SET t.value ='setSapPartNumber|NA|setDeletionFlag|NA|setMfr|setPartNumber|setDescription|setSapProductCategory|NA|setSpecialMaterial'
  WHERE t.type='MATERIAL'
  AND t.key   ='METHODS';
  
  UPDATE schedule_config t
  SET t.value ='STRING|NA|BOOLEAN|NA|STRING|STRING|STRING|STRING|NA|STRING'
  WHERE t.type='MATERIAL'
  AND t.key   ='PARAM_TYPE';