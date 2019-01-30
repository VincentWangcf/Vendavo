package com.avnet.emasia.webquote.reports.ejb;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import com.avnet.emasia.webquote.entity.OfflineReport;
import com.avnet.emasia.webquote.utilities.MessageFormatorUtil;

/**
 * Session Bean implementation class UserSB
 */
@Stateless
public class OfflineReportSB {

    
	@PersistenceContext(unitName="Server_Source")
	EntityManager em;
	static Logger logger = Logger.getLogger("OfflineReportSB");
	final static String BATCH_SQL = "select a from OfflineReport a where a.employeeId=:employeeId and a.createdOn=:createdOn and a.serviceBeanId=:serviceBeanId and a.sendFlag = 0";
	
	public List<OfflineReport> findAll(){
		
		Query query = em.createQuery("select c from OfflineReport c where c.type is null and c.sendFlag = 0");
		List<OfflineReport> offlineReps = (List<OfflineReport>)query.getResultList();
		
		return offlineReps;
	}

	public List<OfflineReport> findBatchRequest(OfflineReport rpt){
		
		Query query = em.createQuery(BATCH_SQL);
		query.setParameter("employeeId", rpt.getEmployeeId());
		query.setParameter("createdOn", rpt.getCreatedOn());
		query.setParameter("serviceBeanId", rpt.getServiceBeanId());
		List<OfflineReport> offlineReps = (List<OfflineReport>)query.getResultList();
		
		return offlineReps;
	}
	
	public long createOfflineReportRequest(List<OfflineReport> rptList)
	{
		//logger.info("call | createOfflineReportRequest");
		OfflineReport temp = null;
		try
		{
			for(OfflineReport or : rptList)
			{
				temp = or;
				em.persist(or);
			}
			em.flush();
			if(rptList!=null && rptList.size()>0)
			{
				return rptList.get(0).getId();
			}
		}
		catch(Exception e)
		{
			logger.log(Level.SEVERE, "Exception in offline report, Offline report id"+temp.getId()+" , business unit : "+temp.getBizUnit()+" , Exception Message : "+MessageFormatorUtil.getParameterizedStringFromException(e),e);
		} 
		return 0l;
		
	}
	
	public void updateOfflineReport(OfflineReport offlineRpt)
	{
		try
		{
			em.merge(offlineRpt);
			em.flush();
		}
		catch(Exception e)
		{
			logger.log(Level.SEVERE, "Exception in offline report, Offline report id"+offlineRpt.getId()+" , business unit : "+offlineRpt.getBizUnit()+" , Exception Message : "+MessageFormatorUtil.getParameterizedStringFromException(e),e);
		}    			
	}
	
	
	 public OfflineReport findById(long id)
	 {
	    	return em.find(OfflineReport.class, id);
	 }
}
