package com.avnet.emasia.webquote.stm.ejb;

import java.util.List;
import java.util.logging.Logger;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import com.avnet.emasia.webquote.entity.BizUnit;
import com.avnet.emasia.webquote.entity.Quote;
import com.avnet.emasia.webquote.entity.SubregionRecipientMapping;

@Stateless
@LocalBean
public class SubregionRecipientMappingSB {
	
	@PersistenceContext(unitName = "Server_Source")
	EntityManager em;
	
	private static Logger logger = Logger.getLogger(SubregionRecipientMappingSB.class.getName());

	public List<SubregionRecipientMapping> findAllRecipients(BizUnit bu) {
		String sql = "select r from SubregionRecipientMapping r where r.bizUnit = :bizUnit order by r.subRegion";
		Query query = em.createQuery(sql, SubregionRecipientMapping.class);
		query.setParameter("bizUnit", bu);	
		List<SubregionRecipientMapping> recipients = query.getResultList();
		return recipients;
	}

}
