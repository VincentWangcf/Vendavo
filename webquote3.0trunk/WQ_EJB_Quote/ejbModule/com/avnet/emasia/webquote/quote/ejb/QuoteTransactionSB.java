package com.avnet.emasia.webquote.quote.ejb;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionManagement;
import javax.ejb.TransactionManagementType;
import javax.persistence.EntityManager;
import javax.persistence.OptimisticLockException;
import javax.persistence.PersistenceContext;
import javax.transaction.HeuristicMixedException;
import javax.transaction.HeuristicRollbackException;
import javax.transaction.NotSupportedException;
import javax.transaction.RollbackException;
import javax.transaction.SystemException;
import javax.transaction.UserTransaction;

import org.apache.commons.beanutils.BeanUtils;

import com.avnet.emasia.webquote.constants.ActionEnum;
import com.avnet.emasia.webquote.entity.Quote;
import com.avnet.emasia.webquote.entity.QuoteItem;

/**
 * Session Bean implementation class QuoteSB
 */
@Stateless
@TransactionManagement(TransactionManagementType.BEAN)
public class QuoteTransactionSB {

    @Resource
    UserTransaction ut;
    
    private final static int SESSION_TIME_OUT = 36000;
    
	@PersistenceContext(unitName="Server_Source")
	EntityManager em;

	@EJB
	QuoteNumberSB quoteNumberSB;

	@EJB
	QuoteSB quoteSB;
	
	private static final Logger LOG =Logger.getLogger(QuoteTransactionSB.class.getName());


	/**
	 * Default constructor. 
	 */
	public QuoteTransactionSB() {
		// TODO Auto-generated constructor stub
	}

	public Map<String,String> updateQuoteItem(List<QuoteItem> quoteItems, ActionEnum action){
		Map<String,String> updateInfo = updateQuoteItemInTransaction(quoteItems, action);
		return updateInfo;
		
	}
	
	
    public Map<String,String> updateQuoteItemInTransaction(List<QuoteItem> quoteItems, ActionEnum action) {
    	StringBuffer lockEx = new StringBuffer();
    	StringBuffer commonEx = new StringBuffer();
    	StringBuffer success = new StringBuffer();
    	QuoteItem newItem = null;
    	for(int i = 0; i < quoteItems.size();i++){
    		QuoteItem quoteItem = quoteItems.get(i);
	    	try {
				ut.setTransactionTimeout(SESSION_TIME_OUT);
				ut.begin(); 
				quoteItem.setAction(action.name());
				newItem =em.merge(quoteItem);
				em.flush();
//				em.clear();
				ut.commit();
				success.append(quoteItem.getQuoteNumber()+",");
				//Set new Item to old one
				//BeanUtils.copyProperties(quoteItem, newItem);//fixed reset to null if value set in entity listener
//				quoteItem.setVersion(newItem.getVersion());

				// changed By Lenon 2017/03/07 Fix Ticket ICN0671813
				quoteItems.set(i, newItem);
				
				LOG.log(Level.INFO, "save quote successr==>" + quoteItem.getQuoteNumber());
			} catch (OptimisticLockException e) {
				try {
					ut.rollback();
				} catch (IllegalStateException | SecurityException
						| SystemException e1) {
					LOG.log(Level.SEVERE, "OptimisticLockException roll back Error occured when update", e);
				}
				lockEx.append(quoteItem.getQuoteNumber()+",");
				LOG.log(Level.SEVERE, "OptimisticLockException Error occured when update quote number==>" + quoteItem.getQuoteNumber(), e);
				
			} catch (Exception e) {
				try {
					ut.rollback();
				} catch (IllegalStateException | SecurityException
						| SystemException e1) {
					LOG.log(Level.SEVERE, "Exception roll back Error occured when update quote Item", e);
				}
				commonEx.append(quoteItem.getQuoteNumber()+",");
				LOG.log(Level.SEVERE, "Exception Error occured when update quote number==>" + quoteItem.getQuoteNumber(), e);
			} 
    	}		
  /* 	em.flush();
		em.clear();*/
		
		Map<String,String> returnInfo = new HashMap<String,String>();
		returnInfo.put("lockEx", null==lockEx?null:lockEx.toString());
		returnInfo.put("commonEx", null==commonEx?null:commonEx.toString());
		returnInfo.put("success", null==success?null:success.toString());
		
    	return returnInfo;
    }
    
    public boolean updateQuoteItemInTransaction1(List<QuoteItem> quoteItems) {
    	boolean updated = true;
    	try {
			ut.setTransactionTimeout(SESSION_TIME_OUT);
			ut.begin();    		
	    	for(QuoteItem quoteItem : quoteItems){
		    	try{
		    		em.merge(quoteItem);
		    	} catch (Exception ex){
		    		LOG.log(Level.SEVERE, "Exception occured for MPQ: "+quoteItem.getMpq()+", Quoted P/N: "+quoteItem.getQuotedPartNumber()+", Reason for failure: "+ex.getMessage(), ex);
		    		updated = false;
		    	}
	    	}
	    	em.flush();
        	em.clear();
			ut.commit();	    	
    	} catch (Exception e){
    		LOG.log(Level.SEVERE, "Exception occured during updating quote items, Reason for failure: "+e.getMessage());
	    	em.flush();
        	em.clear();
        	try {
        		ut.commit();
        	} catch (Exception exc){
	    		LOG.log(Level.SEVERE, "Exception occured during commit, Reason for failure: "+exc.getMessage());
        	}
    	}
    	return updated;
    }
    
    //updateQuoteSoldToCustomerName
    
    public String updateQuoteSoldToCustomerName(Quote quote){
    	String updated = "Success";
    	try {
			ut.setTransactionTimeout(SESSION_TIME_OUT);
			ut.begin();    		
		    em.merge(quote);
	    	em.flush();
        	em.clear();
			ut.commit();	    	
    	} catch (Exception e){
    		LOG.log(Level.SEVERE, e.getMessage());
    		updated = "Fail";
    	}
    	
    	return updated;
    }
}
