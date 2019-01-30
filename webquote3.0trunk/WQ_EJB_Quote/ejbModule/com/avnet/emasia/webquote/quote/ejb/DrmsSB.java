package com.avnet.emasia.webquote.quote.ejb;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import com.avnet.emasia.webquote.entity.Customer;
import com.avnet.emasia.webquote.entity.DrNedaItem;

/**
 * Session Bean implementation class DrmsSB
 */
@Stateless
@LocalBean
public class DrmsSB {
	
	@PersistenceContext(unitName="Server_Source")
	EntityManager em;
	
	
	private static final Logger LOG =Logger.getLogger(DrmsSB.class.getName());


    /**
     * Default constructor. 
     * 
     */
    public DrmsSB() {
        // TODO Auto-generated constructor stub
    }
    
    public List<DrNedaItem> findMatchedDrProject(String soldToCode, String endCustomerCode, String mfr, String requiredPartNumber){
    	List<String> stages = new ArrayList<String>();
    	stages.add("5");
    	//stages.add("DL");
    	
    	List<String> itemStatus = new ArrayList<String>();
    	itemStatus.add("08");
    	itemStatus.add("09");
    	
    	if(mfr != null && mfr.length() > 3){
    		mfr = mfr.substring(0, 3);
    	}
    	
//    	requiredPartNumber = mfr + requiredPartNumber;
    	String sql = "select ni from DrProjectHeader h join h.drProjectCustomers c join h.drNedaHeaders nh join nh.drNedaItems ni where (";
    	if(soldToCode != null && endCustomerCode != null){
    		sql += "c.id.customerNumber = :soldToCode OR c.id.customerNumber = :endCustomerCode";
    	} else if(soldToCode != null){
    		sql += "c.id.customerNumber = :soldToCode";
    	} else if(endCustomerCode != null){
    		sql += "c.id.customerNumber = :endCustomerCode";
    	}
    	sql += ") and ni.supplierCode= :mfr and nh.nedaDesignStage not in :stages and ni.status not in :itemStatus and (";
    	
    	requiredPartNumber = requiredPartNumber.replaceAll("'", "''");
    	sql += "'" + requiredPartNumber.toUpperCase() + "'" + " like FUNCTION('REPLACE', ni.partMask, '*','%')  ";
    	
/*    	for(int i=0; i<requiredPartNumber.length(); i++){
    		if(i > 0)
    		{
    			sql += " OR ";
    		}

    		if(requiredPartNumber!=null)
    		{
	        	String tempStr = requiredPartNumber.substring(0, requiredPartNumber.length()-i);
	        	tempStr = tempStr.replace("'", "''");
	        	sql += " ni.partMask like '" + tempStr + "*%' ";
    		}
    	}*/
    	
    	sql += ") ";
    	Query query = em.createQuery(sql);
    	
    	if(soldToCode != null && endCustomerCode != null){
    		query.setParameter("soldToCode", soldToCode).setParameter("endCustomerCode", endCustomerCode);
    	} else if(soldToCode != null){
    		query.setParameter("soldToCode", soldToCode);
    	} else if(endCustomerCode != null){
    		query.setParameter("endCustomerCode", endCustomerCode);
    	}
    	query.setParameter("mfr", mfr);
    	query.setParameter("stages", stages);
    	query.setParameter("itemStatus", itemStatus);
//    	query.setParameter("requiredPartNumber", requiredPartNumber.toUpperCase());
    	
    	return query.getResultList();
    	
    }

    public List<DrNedaItem> findDrNedaItemsByProjectIds(List<Long> projectIds){
    	List<String> stages = new ArrayList<String>();
    	stages.add("DD");
    	stages.add("DL");
    	
    	List<String> tmpProjectIds = new ArrayList<String>();
    	for(Long projectId : projectIds){
    		tmpProjectIds.add(String.valueOf(projectId));    		
    	}
    	
    	String sql = "select ni from DrNedaItem ni where ";
    	sql += " (";

    	for(int i=0; i<projectIds.size(); i++){
    		Long projectId = projectIds.get(i);
    		if(i > 0)
    			sql += " or ";
    		sql += " ni.id.projectId = " + projectId;
    	}
    	
    	sql += ")";
    	Query query = em.createQuery(sql);
    	
    	return query.getResultList();
    	
    }


}
