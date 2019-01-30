package com.avnet.emasia.webquote.quote.ejb;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

import com.avnet.emasia.webquote.entity.Attachment;
import com.avnet.emasia.webquote.entity.QuoteItem;
import com.avnet.emasia.webquote.quote.ejb.constant.QuoteSBConstant;

/**
 * Session Bean implementation class QuoteSB
 */
@Stateless
@LocalBean
public class AttachmentSB {
	
	@PersistenceContext(unitName="Server_Source")
	EntityManager em;
	
	private static final Logger LOG =Logger.getLogger(AttachmentSB.class.getName());
    
    public void removeAttachment(List<Attachment> attachments){
    	
    	
    	for(Attachment attachment : attachments){
    		attachment.setQuoteItem(null);
    		attachment.setQuote(null);
    		
    		attachment = em.find(Attachment.class, attachment.getId());
    		em.remove(attachment);
    	}
    }
    
    public List<Attachment> findQCAttachment(String quoteNumber){
    	TypedQuery<Attachment> query = em.createQuery("select a from Attachment a where a.type = :type and a.quoteItem.quoteNumber = :quoteNumber", Attachment.class);
    	query.setParameter("type",  QuoteSBConstant.ATTACHMENT_TYPE_QC);
    	query.setParameter("quoteNumber", quoteNumber);
    	return query.getResultList();  	
    }
    
    public List<Attachment> findInternalTransferAttachment(String quoteNumber){
    	TypedQuery<Attachment> query = em.createQuery("select a from Attachment a where a.type = :type and a.quoteItem.quoteNumber = :quoteNumber", Attachment.class);
    	query.setParameter("type",  QuoteSBConstant.ATTACHMENT_TYPE_INTERNAL_TRANSFER);
    	query.setParameter("quoteNumber", quoteNumber);
    	return query.getResultList();  	
    }

    public List<Attachment> findRefreshQuoteAttachment(String quoteNumber){
    	TypedQuery<Attachment> query = em.createQuery("select a from Attachment a where a.type = :type and a.quoteItem.quoteNumber = :quoteNumber", Attachment.class);
    	query.setParameter("type",  QuoteSBConstant.ATTACHMENT_TYPE_REFRESH);
    	query.setParameter("quoteNumber", quoteNumber);
    	List<Attachment> returnList = query.getResultList();  	
    	if(returnList!=null && returnList.size()>0)
    	    return returnList;
    	else
    		return new ArrayList<Attachment>();
    }

    public List<Attachment> findAttachment(String quoteNumber){
    	TypedQuery<Attachment> query = em.createQuery("select a from Attachment a where a.type = :type and a.quoteItem.quoteNumber = :quoteNumber", Attachment.class);
    	query.setParameter("type",  QuoteSBConstant.ATTACHMENT_TYPE_QUOTE_ITEM);
    	query.setParameter("quoteNumber", quoteNumber);
    	return query.getResultList();  	
    }

    public List<Attachment> findFormAttachment(long quoteId){
    	TypedQuery<Attachment> query = em.createQuery("select a from Attachment a where a.type = :type and a.quote.id = :id", Attachment.class);
    	query.setParameter("type",  QuoteSBConstant.ATTACHMENT_TYPE_QUOTE);
    	query.setParameter("id", quoteId);
    	return query.getResultList();  	
    }
    
    public Attachment findById(Long id){
    	return em.find(Attachment.class, id);
    }
    
}
