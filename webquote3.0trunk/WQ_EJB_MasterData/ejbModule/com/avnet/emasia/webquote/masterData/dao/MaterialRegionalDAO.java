package com.avnet.emasia.webquote.masterData.dao;

import java.util.List;

import javax.transaction.HeuristicMixedException;
import javax.transaction.HeuristicRollbackException;
import javax.transaction.NotSupportedException;
import javax.transaction.RollbackException;
import javax.transaction.SystemException;

import com.avnet.emasia.webquote.entity.MaterialRegional;

public interface MaterialRegionalDAO {
	public void batchStartUpdate() throws IllegalStateException, SecurityException, SystemException;

	public void batchEndUpdate() throws IllegalStateException, SecurityException, SystemException;
	
	public List<MaterialRegional> getProcessingData();
	
}
