package com.avnet.emasia.webquote.quote.ejb;

import java.util.List;
import java.util.logging.Logger;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import com.avnet.emasia.webquote.entity.Quote;

import com.avnet.emasia.webquote.entity.QuoteToSoPending;

/**
 * Session Bean implementation class QuoteSB
 */
@Stateless
@LocalBean
public class QuoteToSoPendingSB {
	
	@PersistenceContext(unitName="Server_Source")
	EntityManager em;
	   
	private static final Logger LOG =Logger.getLogger(QuoteToSoPendingSB.class.getName());
	
    /**
     * Default constructor. 
     */
    public QuoteToSoPendingSB() {
        // TODO Auto-generated constructor stub
    }
    
    public QuoteToSoPending findByPK(long id){
    	return em.find(QuoteToSoPending.class, id);
    }

    public Quote findQuoteByPK(long id){
    	return em.find(Quote.class, id);
    }

    public List<QuoteToSoPending> findQuoteToSoPendingByCustomerNumber(String customerNumber){
       	String sql = "select q from QuoteToSoPending q where q.status = :status and q.customerNumber = :customerNumber";
		Query query = em.createQuery(sql);
		query.setParameter("customerNumber", customerNumber);
		query.setParameter("status", true);
		return query.getResultList();       	    	
    }
    
    public List<QuoteToSoPending> findQuoteToSoPendingByMfrAndPartNumber(String mfr, String fullMfrPartNumber){
       	String sql = "select q from QuoteToSoPending q where q.status = :status and q.mfr = :mfr and q.fullMfrPartNumber = :fullMfrPartNumber";
		Query query = em.createQuery(sql);
		query.setParameter("mfr", mfr);
		query.setParameter("fullMfrPartNumber", fullMfrPartNumber);
		query.setParameter("status", true);
		return query.getResultList();       	    	
    }    
    
    public QuoteToSoPending updateQuoteToSoPending(QuoteToSoPending quoteToSoPending){
    	return em.merge(quoteToSoPending);
    }
}
