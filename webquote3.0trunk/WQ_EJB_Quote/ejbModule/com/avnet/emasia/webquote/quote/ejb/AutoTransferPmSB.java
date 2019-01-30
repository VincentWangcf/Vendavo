package com.avnet.emasia.webquote.quote.ejb;

import java.util.List;
import java.util.logging.Logger;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import com.avnet.emasia.webquote.entity.AutoTransferPm;


/**
 * Session Bean implementation class QuoteSB
 */
@Stateless
@LocalBean
public class AutoTransferPmSB {
	
	@PersistenceContext(unitName="Server_Source")
	EntityManager em;
	
	private static final Logger LOG =Logger.getLogger(AutoTransferPmSB.class.getName());
	
    /**
     * Default constructor. 
     */
    public AutoTransferPmSB() {

    }
    
    public void createAutoTransferPm(AutoTransferPm autoTransferPm) {
    	em.merge(autoTransferPm);
    }
    
    public List<AutoTransferPm> findVendorReportByPartNumber(String manufacturer, String soldToCustomerNumber, String fullMfrPartNumber, String bizUnit){
    	List<AutoTransferPm> autoTransferPms = null;
    	String sql = null;
    	
		sql = "select a from AutoTransferPm a where a.manufacturer = :manufacturer AND a.soldToCustomerNumber = :soldToCustomerNumber AND a.fullMfrPartNumber = :fullMfrPartNumber AND a.bizUnit =:bizUnit ";  
		autoTransferPms = em.createQuery(sql)
		.setParameter("manufacturer", manufacturer)
		.setParameter("soldToCustomerNumber", soldToCustomerNumber)
		.setParameter("fullMfrPartNumber", fullMfrPartNumber)
		.setParameter("bizUnit", bizUnit)
		.getResultList();
		if(autoTransferPms != null && autoTransferPms.size() > 0)
			return autoTransferPms;
		
		sql = "select a from AutoTransferPm a where a.manufacturer = :manufacturer AND a.soldToCustomerNumber = :soldToCustomerNumber AND a.fullMfrPartNumber is NULL AND a.bizUnit =:bizUnit ";
		autoTransferPms = em.createQuery(sql)
		.setParameter("manufacturer", manufacturer)
		.setParameter("soldToCustomerNumber", soldToCustomerNumber)
		.setParameter("bizUnit", bizUnit)
		.getResultList();
		if(autoTransferPms != null && autoTransferPms.size() > 0)
			return autoTransferPms;			

		sql = "select a from AutoTransferPm a where a.manufacturer = :manufacturer AND a.soldToCustomerNumber is NULL AND a.fullMfrPartNumber = :fullMfrPartNumber AND a.bizUnit =:bizUnit ";
		autoTransferPms = em.createQuery(sql)
		.setParameter("manufacturer", manufacturer)
		.setParameter("fullMfrPartNumber", fullMfrPartNumber)
		.setParameter("bizUnit", bizUnit)
		.getResultList();
		if(autoTransferPms != null && autoTransferPms.size() > 0)
			return autoTransferPms;
		
		sql = "select a from AutoTransferPm a where a.manufacturer is NULL AND a.soldToCustomerNumber = :soldToCustomerNumber AND a.fullMfrPartNumber = :fullMfrPartNumber AND a.bizUnit =:bizUnit ";
		autoTransferPms = em.createQuery(sql)
		.setParameter("soldToCustomerNumber", soldToCustomerNumber)
		.setParameter("fullMfrPartNumber", fullMfrPartNumber)
		.setParameter("bizUnit", bizUnit)
		.getResultList();
		if(autoTransferPms != null && autoTransferPms.size() > 0)
			return autoTransferPms;

		sql = "select a from AutoTransferPm a where a.manufacturer = :manufacturer AND a.soldToCustomerNumber is NULL AND a.fullMfrPartNumber is NULL AND a.bizUnit =:bizUnit ";
		autoTransferPms = em.createQuery(sql)
		.setParameter("manufacturer", manufacturer)
		.setParameter("bizUnit", bizUnit)
		.getResultList();
		if(autoTransferPms != null && autoTransferPms.size() > 0)
			return autoTransferPms;

		sql = "select a from AutoTransferPm a where a.manufacturer is NULL AND a.soldToCustomerNumber = :soldToCustomerNumber AND a.fullMfrPartNumber is NULL AND a.bizUnit =:bizUnit ";
		autoTransferPms = em.createQuery(sql)
		.setParameter("soldToCustomerNumber", soldToCustomerNumber)
		.setParameter("bizUnit", bizUnit)
		.getResultList();
		if(autoTransferPms != null && autoTransferPms.size() > 0)
			return autoTransferPms;

		sql = "select a from AutoTransferPm a where a.manufacturer is NULL AND a.soldToCustomerNumber is NULL AND a.fullMfrPartNumber = :fullMfrPartNumber AND a.bizUnit =:bizUnit ";
		autoTransferPms = em.createQuery(sql)
		.setParameter("fullMfrPartNumber", fullMfrPartNumber)
		.setParameter("bizUnit", bizUnit)
		.getResultList();
		if(autoTransferPms != null && autoTransferPms.size() > 0)
			return autoTransferPms;

    	return null;
    }
    

}
