package com.avnet.emasia.webquote.quote.ejb;

import java.util.List;
import java.util.logging.Logger;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import com.avnet.emasia.webquote.entity.PosSummary;

/**
 * Session Bean implementation class PosSB
 */
@Stateless
@LocalBean
public class PosSB {
	

    
	@PersistenceContext(unitName="Server_Source")
	EntityManager em;
	
	private static final Logger LOG =Logger.getLogger(PosSB.class.getName());
	
	public List<PosSummary> findPosSummary(String mfr, String partNumber, String customerName){
		
		String sql = "select ps from PosSummary ps where 1=1";
		if(customerName != null)
			sql += " and upper(ps.id.customerName) like :customerName";
		if(mfr != null)
			sql += " and upper(ps.id.mfr) like :mfr";
		if(partNumber != null)
			sql += " and upper(ps.id.partNumber) like :partNumber";
		sql += " order by ps.id.customerName";

		Query query = em.createQuery(sql);
		if(customerName != null)
			query.setParameter("customerName", "%"+customerName.toUpperCase()+"%"); 
		if(mfr != null)
			query.setParameter("mfr", "%"+mfr.toUpperCase()+"%"); 
		if(partNumber != null)
			query.setParameter("partNumber", "%"+partNumber.toUpperCase()+"%"); 
		return (List<PosSummary>)query.getResultList();
	}

	public void summarizePos() {
		Query query = em.createNativeQuery("BEGIN POS_SUMMARY_PROC(); END;");
	    Object obj = query.executeUpdate();
	    LOG.info("POS summarize result: " + obj.toString());
		
	}
}
