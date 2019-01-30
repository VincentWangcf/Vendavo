package com.avnet.emasia.webquote.masterData.dao;

import java.util.List;

import com.avnet.emasia.webquote.entity.SapInterfaceBatches;
import com.avnet.emasia.webquote.masterData.exception.CheckedException;

public interface SapInterfaceBatchesDAO {
	public Long createBatch(String createdBy, String processID) throws CheckedException;

	public void updateBatchEnd(Long Id, Integer processStatus) throws CheckedException;
	
	public List<SapInterfaceBatches> findById(Long Id);
}
