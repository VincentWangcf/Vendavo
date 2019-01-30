package com.avnet.emasia.webquote.quote.ejb;

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
import javax.persistence.TypedQuery;
import javax.transaction.UserTransaction;

import com.avnet.emasia.webquote.entity.BizUnit;
import com.avnet.emasia.webquote.entity.VendorReport;
import com.avnet.emasia.webquote.utilities.MessageFormatorUtil;

/**
 * Session Bean implementation class QuoteSB
 */
@Stateless
@TransactionManagement(TransactionManagementType.BEAN) 
public class VendorReportSB {
	private static final Logger LOGGER = Logger.getLogger(VendorReportSB.class.getName());
	
    @Resource
    UserTransaction ut;
    
	@PersistenceContext(unitName="Server_Source")
	EntityManager em;
	
    private final static int SESSION_TIME_OUT = 36000;
	
    /**
     * Default constructor. 
     */
    public VendorReportSB() {

    }
    
	public void clearVendorReportTable(String mfr, BizUnit bizunit){
		try {
			ut.setTransactionTimeout(SESSION_TIME_OUT);
			ut.begin();
		 	Query q1 = em.createQuery("DELETE FROM VendorReport v where v.mfr = :mfr and v.bizUnit = :bizunit");
	    	q1.setParameter("mfr", mfr);
	    	q1.setParameter("bizunit", bizunit);
	    	q1.executeUpdate();
	    	em.flush();
        	em.clear();
			ut.commit();
		} catch (Exception ex){
	    	em.flush();
        	em.clear();
        	try {
        		ut.commit();
        	} catch (Exception e){
        		LOGGER.log(Level.SEVERE, "Failed to clear vendor report for mfr : "+mfr+" , Business Unit : "+bizunit+" , Error Message : "+MessageFormatorUtil.getParameterizedStringFromException(ex), ex);
        	}			
        	LOGGER.log(Level.INFO, "transaction error on cleaning Vendor Reporty ");		
		}
	}
	
    public void createVendorReport(List<VendorReport> vendorReports) {
    	try {
			ut.setTransactionTimeout(SESSION_TIME_OUT);
			ut.begin();    		
	    	for(VendorReport vendorReport : vendorReports){
		    	try{
		    		em.merge(vendorReport);
		    	} catch (Exception ex){
		    		LOGGER.log(Level.SEVERE, "Failed to persist verndor report : "+vendorReport.getId()+" , for mfr : "+vendorReport.getMfr()+", business unit : "+vendorReport.getBizUnit()+", Error message : "+ex.getMessage(), ex);
		    	}
	    	}
	    	em.flush();
        	em.clear();
			ut.commit();	    	
    	} catch (Exception e){
	    	em.flush();
        	em.clear();
			try {
        		ut.commit();
        	} catch (Exception exc){
        		LOGGER.log(Level.SEVERE, "Failed to commit!"+e.getMessage(), e);
			}
    	}
    }
    
    public List<VendorReport> findVendorReportByPartNumberAndCustomer(String mfr, String partNumber, String customer, BizUnit bizUnit){
    	
    	String sql = "select v from VendorReport v where v.id > 0 ";
    	if(mfr != null)
    		sql += "AND upper(v.mfr) like :mfr ";    				
    	if(partNumber != null)
    		sql += "AND upper(v.fullMfrPartNumber) like :partNumber ";    				
    	if(customer != null)
    		sql += "AND upper(v.customer) like :customer ";		
    	if(bizUnit != null)
    		sql += "AND v.bizUnit = :bizUnit ";		

    	
    	TypedQuery<VendorReport> query = em.createQuery(sql, VendorReport.class);
    	if(mfr != null)
    		query.setParameter("mfr", "%"+mfr.toUpperCase()+"%");
    	if(partNumber != null)
    		query.setParameter("partNumber", "%"+partNumber.toUpperCase()+"%");
    	if(customer != null)
    		query.setParameter("customer", "%"+customer.toUpperCase()+"%");
    	if(bizUnit != null)
    		query.setParameter("bizUnit", bizUnit);
    	List<VendorReport> vendorReports = query.getResultList();
    	
    	return vendorReports;    	
    }
    
    
    public VendorReport findVendorReportByUK(String mfr,String sqNumber, String partNumber, String customer, BizUnit bizUnit){  	
    	String sql = "select v from VendorReport v where v.id > 0 ";
    		sql += "AND upper(v.mfr) like :mfr ";    				
    		sql += "AND upper(v.fullMfrPartNumber) like :partNumber ";  
    		sql += "AND upper(v.sqNumber) like :sqNumber ";  
    		sql += "AND upper(v.customer) like :customer ";		
    		sql += "AND v.bizUnit = :bizUnit ";		

    	
    	TypedQuery<VendorReport> query = em.createQuery(sql, VendorReport.class);
    		query.setParameter("mfr", "%"+mfr.toUpperCase()+"%");
    		query.setParameter("partNumber", "%"+partNumber.toUpperCase()+"%");
    		query.setParameter("sqNumber", "%"+sqNumber.toUpperCase()+"%");
    		query.setParameter("customer", "%"+customer.toUpperCase()+"%");
    		query.setParameter("bizUnit", bizUnit);
    	List<VendorReport> list = query.getResultList();	
    	if(list.size()>0){
    		return list.get(0);
    	}else{
    		return null;
    	}
    }

}
