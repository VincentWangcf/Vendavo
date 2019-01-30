package com.avnet.emasia.webquote.masterData.dao;

import java.util.List;

import javax.transaction.HeuristicMixedException;
import javax.transaction.HeuristicRollbackException;
import javax.transaction.NotSupportedException;
import javax.transaction.RollbackException;
import javax.transaction.SystemException;

import com.avnet.emasia.webquote.entity.SalesCostPricer;
//import com.avnet.emasia.webquote.masterData.dto.SapInterfacePricer;

public interface SalesCostPricerDAO {
	public void batchStartUpdate() throws IllegalStateException, SecurityException, SystemException;

	public void batchEndUpdate() throws IllegalStateException, SecurityException, SystemException;
	
	public List<SalesCostPricer> getProcessingData();
	
	//public List<SapInterfacePricer> getProcessingData();
	public void updateQtyControl(String salesCostType, String bizUnit, String manufacturerName, String fullMfrPartNumber, long qtyControl ) throws IllegalStateException, SecurityException, SystemException;
	
	public long getMaxIDByRowLimit();
	
	public long getRowLimit();
	
	public void setRowLimit(long rowLimit);
}
