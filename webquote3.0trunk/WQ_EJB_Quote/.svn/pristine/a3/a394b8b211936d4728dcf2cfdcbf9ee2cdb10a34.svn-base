package com.avnet.emasia.webquote.quote.ejb;

import java.util.List;
import java.util.logging.Logger;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.LockModeType;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import com.avnet.emasia.webquote.entity.BizUnit;
import com.avnet.emasia.webquote.entity.QuoteNumber;

/**
 * Session Bean implementation class QuoteNumberSB
 */
@Stateless
@LocalBean
public class QuoteNumberSB {
	
	@PersistenceContext(unitName="Server_Source")
	EntityManager em;

	private static final Logger LOG =Logger.getLogger(QuoteNumberSB.class.getName());

    /**
     * Default constructor. 
     */
    public QuoteNumberSB() {
        
    }
    


    
    public String nextNumber(QuoteNumber quoteNumber){
    	
    	int number = quoteNumber.getNextNumber();
    	
    	String pattern = quoteNumber.getNumberPattern();
    	
    	String sNumber = String.valueOf(++number);
    	
    	int numberLength = String.valueOf(number).length();
    	int patternNumberLength = pattern.length() - pattern.indexOf("#");
    	
    	if(numberLength < patternNumberLength){
    		for(;numberLength < patternNumberLength ; numberLength++){
    			sNumber = "0" + sNumber;
    		}
    	}    	
    	
    	quoteNumber.setNextNumber(number);
    	return pattern.substring(0, pattern.indexOf("#")) + sNumber;
    }
    

	public QuoteNumber lockNumber(String type, BizUnit bizUnit) {
    	QuoteNumber quoteNumber =(QuoteNumber) em.createQuery("select q from QuoteNumber q where q.id.docType = :type and q.id.bizUnit = :bizUnit")
    	.setParameter("bizUnit", bizUnit.getName())
    	.setParameter("type", type)
    	.getSingleResult();
    	
    	em.lock(quoteNumber, LockModeType.PESSIMISTIC_WRITE);
    	
    	return quoteNumber;
    }
	
	

	public QuoteNumber lockDPNumber(String type) {
    	QuoteNumber quoteNumber =(QuoteNumber) em.createQuery("select q from QuoteNumber q where q.id.docType = :type ")
    	.setParameter("type", type).getSingleResult();
    	em.lock(quoteNumber, LockModeType.PESSIMISTIC_WRITE);
    	return quoteNumber;
    }
	
	public List<QuoteNumber> findQuoteNumberListByType(String type) {
		Query q = em.createQuery("select q from QuoteNumber q where q.id.docType = :type ");
    	q.setParameter("type", type);
    	return (List<QuoteNumber>)q.getResultList();
    }

	
    
}
