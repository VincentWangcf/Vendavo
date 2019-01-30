package com.avnet.emasia.webquote.stm.ejb;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.Resource;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.ejb.TransactionManagement;
import javax.ejb.TransactionManagementType;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.transaction.HeuristicMixedException;
import javax.transaction.HeuristicRollbackException;
import javax.transaction.NotSupportedException;
import javax.transaction.RollbackException;
import javax.transaction.SystemException;
import javax.transaction.UserTransaction;

import com.avnet.emasia.webquote.entity.User;
import com.avnet.emasia.webquote.entity.VendorBpmCustomer;
import com.avnet.emasia.webquote.stm.constant.StmConstant;
import com.avnet.emasia.webquote.stm.dto.BpmSearchCriteria;
import com.avnet.emasia.webquote.utilities.MessageFormatorUtil;
import com.avnet.emasia.webquote.utilities.util.QuoteUtil;

@Stateless
@LocalBean
@TransactionManagement(TransactionManagementType.BEAN)
public class BpmMaintainSB {
	
	@PersistenceContext(unitName = "Server_Source")
	private EntityManager em;

	
	@Resource
	private UserTransaction ut;
	
	
	private static Logger logger = Logger.getLogger(BpmMaintainSB.class.getName());


	public List<String> autoCompleteBpmCode(String key) {
    	TypedQuery<VendorBpmCustomer> query = em.createQuery("select m from VendorBpmCustomer m where"
    	+ " m.bpmCode like :bpmCode", VendorBpmCustomer.class);
    	query.setFirstResult(0).setMaxResults(10)
    	.setParameter("bpmCode", "%"+key + "%");
    	List<VendorBpmCustomer> list = query.getResultList();
    	List<String> result = new ArrayList<String>();
    	for(VendorBpmCustomer bean:list){
    		String s = bean.getBpmCode() + StmConstant.PART_SEPARATOR +bean.getShortname() + StmConstant.PART_SEPARATOR +bean.getCtry();
    		if (! result.contains(s)){
    			result.add(s);
    		}
    	}  	
		return result;
	}
	
	public boolean deleteAndSaveBpmToDB(List<VendorBpmCustomer> bpmCustomers){
		Boolean bool = false;
		try {
			ut.setTransactionTimeout(100000);
			ut.begin();
			
			//before save into DB, to do remove all firstly
			logger.info("remove all BPM records from DB!");
			Query delete = em.createQuery("DELETE FROM VendorBpmCustomer");
			delete.executeUpdate();
			em.flush();
			em.clear();
			logger.info("Finish remove all BPM records from DB!");
			
			logger.info("Begin save Bpm  to Db!");
			for(VendorBpmCustomer bean:bpmCustomers){
				em.persist(bean);
			}
			ut.commit();
			bool = true;
		
		} catch (NotSupportedException | SystemException |SecurityException |IllegalStateException |RollbackException |HeuristicMixedException | HeuristicRollbackException e) {
			logger.log(Level.SEVERE, "Error occured while Removing all and Uploading files for Vendor BPM Customer List, Reason for failed: "+MessageFormatorUtil.getParameterizedStringFromException(e),e);
		}
		
		logger.info("End Save Bpm  to Db!");
		return bool;
	}

	public List<VendorBpmCustomer> search(BpmSearchCriteria criteria) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<VendorBpmCustomer> sql = cb.createQuery(VendorBpmCustomer.class);
		Root<VendorBpmCustomer> vendorBpmCustomer = sql.from(VendorBpmCustomer.class);
		List<Predicate> predicates = new ArrayList<Predicate>();
		
		//distributorCode
		if(!QuoteUtil.isEmpty(criteria.getBmpCode())){
			predicates.add(cb.equal(vendorBpmCustomer.get("bpmCode"), criteria.getBmpCode()));
		}
		
		//distributorName
		if(!QuoteUtil.isEmpty(criteria.getShortName())){
			predicates.add(cb.like(cb.upper(vendorBpmCustomer.<String>get("shortname")), "%"+criteria.getShortName().toUpperCase()+"%"));
		}
	    
	    
		sql.select(vendorBpmCustomer);
		if(predicates.size()>0) { // if no search criteria
			sql.where(predicates.toArray(new Predicate[]{}));
		}
		sql.orderBy(cb.desc(vendorBpmCustomer.<Date>get("bpmCode")));
    	
    	TypedQuery<VendorBpmCustomer> query = em.createQuery(sql);
    	List<VendorBpmCustomer> bpmCustomers = query.getResultList();
    	return bpmCustomers;
	}

	public List<String> autoCompleteBpmName(String key) {
    	TypedQuery<VendorBpmCustomer> query = em.createQuery("select m from VendorBpmCustomer m where "
        + "UPPER(m.shortname) like :shortname", VendorBpmCustomer.class);
        	query.setFirstResult(0).setMaxResults(10)
        	.setParameter("shortname", "%"+(key==null ? "":key.toUpperCase()) + "%");
        	List<VendorBpmCustomer> list = query.getResultList();
        	List<String> result = new ArrayList<String>();
        	for(VendorBpmCustomer bean:list){
        		String s = bean.getBpmCode() +StmConstant.PART_SEPARATOR +bean.getShortname() + StmConstant.PART_SEPARATOR +bean.getCtry();
        		if (! result.contains(s)){
        			result.add(s);
        		}
        	}  	
    		return result;
	}
	
	public VendorBpmCustomer findByBpmCode(String bpmCode){
		String sql = "select m from VendorBpmCustomer m where m.bpmCode = ?1";
		TypedQuery<VendorBpmCustomer>  query = em.createQuery(sql, VendorBpmCustomer.class);
		query.setParameter(1, bpmCode);
		query.setMaxResults(1);
		
		List<VendorBpmCustomer> results = query.getResultList();
		
		if (results.size() !=0){
			return results.get(0);
		}else{
			return null;
		}
	}
}
