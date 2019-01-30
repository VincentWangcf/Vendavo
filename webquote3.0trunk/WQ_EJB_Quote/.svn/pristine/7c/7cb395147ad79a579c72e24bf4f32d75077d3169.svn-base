package com.avnet.emasia.webquote.quote.ejb;

import java.util.Date;
import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import javax.annotation.Resource;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.ejb.TransactionManagement;
import javax.ejb.TransactionManagementType;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.UserTransaction;
import com.avnet.emasia.webquote.entity.QuoteToSo;

@Stateless
@LocalBean
@TransactionManagement(TransactionManagementType.BEAN)
public class QuoteToSoSB {
	
	
	   
	private static final Logger LOG =Logger.getLogger(QuoteToSoPendingSB.class.getName());
	
	/*public void persist(List<QuoteToSo> quoteToSoList) {
		if (quoteToSoList != null && !quoteToSoList.isEmpty()) {
			em.persist(quoteToSoList);
		}
	}*/
	@PersistenceContext(unitName="Server_Source")
	EntityManager em;
	
	@Resource
	protected UserTransaction ut;
	
	public void persistQuoteToSosFromItemIdList(List<Long> quoteItemIds) throws Exception {
		if (quoteItemIds ==null || quoteItemIds.isEmpty()) return;
		List<Long> ids = quoteItemIds.stream().filter(id->id!=null).distinct().collect(Collectors.toList());
		System.out.println("ids size:::::::::::::::::::::::::" + ids.size());
		Date date = new Date();
		ut.setTransactionTimeout(100000000);
		ut.begin();
		for (Long id : ids) {
			em.merge(QuoteToSo.create(id, date));
		}
		ut.commit();
		 
	}
	
}
