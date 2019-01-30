package com.avnet.emasia.webquote.commodity.ejb;

import java.util.List;
import java.util.logging.Logger;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import com.avnet.emasia.webquote.entity.BizUnit;
import com.avnet.emasia.webquote.entity.PromotionItem;

/**
 * @author Tonmy,Li(906893)
 * @Created on 2013-01-14
 * 
 */
@Stateless
public class PromotionItemSB {

    @PersistenceContext(unitName = "Server_Source")
    private EntityManager em;
    private static Logger log = Logger.getLogger(PromotionItemSB.class.getName());
    
    String findBySeq = "select a from PromotionItem a where a.sequence=:sequence and a.bizUnit=:bizUnit";
    String findNextBySeq = "select a from PromotionItem a  where a.sequence<:sequence and a.bizUnit=:bizUnit order by a.sequence DESC";
    String findPrevBySeq = "select a from PromotionItem a  where a.sequence>:sequence and a.bizUnit=:bizUnit order by a.sequence ASC";
    String findAll = "select a from PromotionItem a where a.bizUnit=:bizUnit order by a.sequence desc";
	    		      
    //@Interceptors(AuditTrailInterceptor.class)
	public void updatePromoItem(PromotionItem promoItem)
	{
		log.fine("PromotionItemSB | updatePromoItem");
		em.merge(promoItem);
	}
    //@Interceptors(AuditTrailInterceptor.class)
	public void createPromoItem(PromotionItem promoItem)
	{
    	log.fine("PromotionItemSB | createPromoItem");
		em.persist(promoItem);
	}
    //@Interceptors(AuditTrailInterceptor.class)
	public void deletePromoItem(PromotionItem promoItem)
	{
		log.fine("PromotionItemSB | deletePromoItem");
		em.remove(em.merge(promoItem));
		em.flush();
	}
	
	/*
     * @return: return the max seq numbers
     */
	public Integer getMaxSeqNum(BizUnit bizUnit) 
	{
		log.fine("PromotionItemSB | getMaxSeqNum");
		List<PromotionItem> list = getAllPromotionItem(bizUnit); 
		if(list!=null && list.size()>0)
		{
			PromotionItem proItem = (PromotionItem)list.get(0);
			return proItem.getSequence()+1;
		}
		else
			return 1;
		
	}
	/*
     * @return: return a list of PromotionItem object
     */
	public List<PromotionItem> getAllPromotionItem(BizUnit bizUnit)
	{
		log.fine("PromotionItemSB | getAllPromotionItem");
		List<PromotionItem> list = null; 
		Query query = em.createQuery(findAll);
		query.setParameter("bizUnit", bizUnit);
		list=  query.getResultList();
		return list ;
	}
	
	/*d
     * @param: seq sequence of promotionItem
     * @return: return a promotionItem object
     */
	public PromotionItem getPromoItemBySeq(int seq, BizUnit bizUnit)
	{
		log.fine("PromotionItemSB | getPromoItemBySeq");
		Query query = em.createQuery(findBySeq);
		query.setParameter("sequence", seq);
		query.setParameter("bizUnit", bizUnit);
		return (PromotionItem)query.getSingleResult();
	}
    /*
     * @param: seq sequence of promotionItem
     * @return: return a promotionItem object
     */
	public PromotionItem getNextPromoItemBySeq(int seq, BizUnit bizUnit)
	{
		log.fine("PromotionItemSB | getNextPromoItemBySeq");
		Query query = em.createQuery(findNextBySeq);
		query.setParameter("sequence", seq);
		query.setParameter("bizUnit", bizUnit);
		List results = query.getResultList();
		if(results==null || results.size()==0)
		{
			return null;
		}
		query.setFirstResult(0).setMaxResults(1);
		return (PromotionItem)query.getSingleResult();
	}
    /*
     * @param: seq sequence of promotionItem
     * @return: return a promotionItem object
     */
	public PromotionItem getPrevPromoItemBySeq(int seq, BizUnit bizUnit)
	{
		log.fine("PromotionItemSB | getPrevPromoItemBySeq");
		Query query = em.createQuery(findPrevBySeq);
		query.setParameter("sequence", seq);
		query.setParameter("bizUnit", bizUnit);
		List results = query.getResultList();
		if(results==null || results.size()==0)
		{
			return null;
		}
		query.setFirstResult(0).setMaxResults(1);
		return (PromotionItem)query.getSingleResult();
	}
    /*
     * @return: return the total number of promotionItem
     */  
	public Integer getPromoItemNum(BizUnit bizUnit)
	{
		log.fine("PromotionItemSB | getPromoItemNum");
		int count = 0;
		List<PromotionItem> promoItemList = getAllPromotionItem(bizUnit);
		if(promoItemList!=null)
			count = promoItemList.size();
		return count;
	}
	
	
	
	/*
	 *Decline the position of the current promotionItem.
	 *@Param: seq   sequence of promotionItem
	 */
	public void downPromoItem(int seq, BizUnit bizUnit)
	{
		log.fine("PromotionItemSB downPromoItem");

		PromotionItem nextP = getNextPromoItemBySeq(seq,bizUnit);
		if(nextP==null)
			return;
		PromotionItem curP = getPromoItemBySeq(seq,bizUnit);
		int tempSep = curP.getSequence();
		curP.setSequence(nextP.getSequence());
		nextP.setSequence(tempSep);
		updatePromoItem(nextP);
		updatePromoItem(curP);
		
	}
	/*
	 *Ascend the position of the current promotionItem.
	 *@Param: seq  sequence of promotionItem
	 */
	public void upPromoItem(int seq, BizUnit bizUnit)
	{
		log.fine("PromotionItemSB upPromoItem");
		
		PromotionItem preP = getPrevPromoItemBySeq(seq,bizUnit);
		if(preP==null)
			return;
		PromotionItem curP = getPromoItemBySeq(seq,bizUnit);
		int tempSep = curP.getSequence();
		curP.setSequence(preP.getSequence());
		preP.setSequence(tempSep);
		updatePromoItem(preP);
		updatePromoItem(curP);
	}
	
}
