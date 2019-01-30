package com.avnet.emasia.webquote.masterData.dao;

import java.util.List;

import javax.transaction.HeuristicMixedException;
import javax.transaction.HeuristicRollbackException;
import javax.transaction.NotSupportedException;
import javax.transaction.RollbackException;
import javax.transaction.SystemException;

import com.avnet.emasia.webquote.entity.PricerDeleted;
//import com.avnet.emasia.webquote.masterData.dto.SapInterfacePricer;

public interface PricerDeletedDAO {
	public void batchStartUpdate() throws IllegalStateException, SecurityException, SystemException;

	public void batchEndUpdate() throws IllegalStateException, SecurityException, SystemException;
	
	public List<PricerDeleted> getProcessingData();
	
	public void bulkInsertFromPricer(String deletedBy, String nativeSQLWhereCondition) throws IllegalStateException, SecurityException, SystemException;
	
	//public List<SapInterfacePricer> getProcessingData();
	public long getMaxIDByRowLimit();
	
	public long getRowLimit();
	
	public void setRowLimit(long rowLimit);
}
