package com.avnet.emasia.webquote.quote.ejb;

import java.util.List;
import java.util.logging.Logger;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TypedQuery;

import com.avnet.emasia.webquote.entity.SalesOrder;

/**
 * Session Bean implementation class SaleOrderSB
 */
@Stateless
@LocalBean
public class SalesOrderSB {

	
	@PersistenceContext(unitName="Server_Source")
	EntityManager em;

	private static final Logger LOG =Logger.getLogger(SalesOrderSB.class.getName());
	
    /**
     * Default constructor. 
     */
    public SalesOrderSB() {
        // TODO Auto-generated constructor stub
    }
    
    public List<SalesOrder> findByQuoteNumer(String quoteNumber){
    	
    	Query query = em.createQuery("select s from SalesOrder s where s.quoteItem.quoteNumber = :quoteNumber");
    	
    	query.setParameter("quoteNumber", quoteNumber);
    	
    	return query.getResultList();
    	
    }
    
    public long findCountByQuoteNumer(String quoteNumber){
    	
    	TypedQuery<Long> query = em.createQuery("select count(s) from SalesOrder s where s.quoteItem.quoteNumber = :quoteNumber", Long.class);
    	
    	query.setParameter("quoteNumber", quoteNumber);
    	
    	return query.getSingleResult();
    	
    }    
    

}
