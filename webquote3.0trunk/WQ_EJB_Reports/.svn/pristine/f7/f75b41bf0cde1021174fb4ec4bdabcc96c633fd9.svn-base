package com.avnet.emasia.webquote.reports.ejb;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.Resource;
import javax.ejb.Stateless;
import javax.ejb.TransactionManagement;
import javax.ejb.TransactionManagementType;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.transaction.UserTransaction;

import com.avnet.emasia.webquote.entity.OfflineReport;
import com.avnet.emasia.webquote.reports.util.ReportsUtil;

/**
 * Session Bean implementation class UserSB
 */
@Stateless
@TransactionManagement(TransactionManagementType.BEAN) 
public class PartMasterOfflineReportSB {

    @Resource
    UserTransaction ut;
    
	@PersistenceContext(unitName="Server_Source")
	EntityManager em;
	static Logger logger = Logger.getLogger("PartMasterOfflineReportSB");
	final static String BATCH_SQL = "select a from OfflineReport a where a.employeeId=:employeeId and a.createdOn=:createdOn and a.serviceBeanId=:serviceBeanId and a.sendFlag = 0";
	
	public List<OfflineReport> findAll(){
		
		Query query = em.createQuery("select c from OfflineReport c where c.type is null and c.sendFlag = 0");
		List<OfflineReport> offlineReps = (List<OfflineReport>)query.getResultList();
		
		return offlineReps;
	}

	public List<OfflineReport> findBatchRequest(OfflineReport rpt) {

		return ReportsUtil.findBatchRequestForOffineAndPartMaster(rpt, em);
	}
	
	public long createPartMasterOfflineReportRequest(List<OfflineReport> rptList)
	{
		logger.info("call | createPartMasterOfflineReportRequest");
		try
		{
			ut.begin();
			for(OfflineReport or : rptList)
			{
				em.persist(or);
			}
			em.flush();
			ut.commit(); 	
			if(rptList!=null && rptList.size()>0)
			{
				return rptList.get(0).getId();
			}
		}
		catch(Exception e)
		{
			try 
			{
				ut.rollback();
		    }
		    catch(Exception syex) 
		    {
		    	logger.log(Level.SEVERE, "Exception occurred in roll back OfflineReport "+ syex.getMessage(), syex);
		    }
			logger.log(Level.SEVERE, "Exception occurred in OfflineReport persisting  "+ e.getMessage(), e);
		} 
		return 0l;
		
	}
	
	public void updateOfflineReport(OfflineReport offlineRpt)
	{
		try
		{
			ut.begin();
			em.merge(offlineRpt);
			em.flush();
			ut.commit(); 	
		}
		catch(Exception e)
		{
			try 
			{
				ut.rollback();
		    }
		    catch(Exception syex) 
		    {
		    	logger.log(Level.SEVERE, "Exception occurred in rollback on Update OfflineReport  "+ syex.getMessage(), syex);
		    }
			logger.log(Level.SEVERE, "Exception occurred in begin and commit in UpdateOfflineReport method while generating report"+ e.getMessage(), e);
			} 	
	}
}
