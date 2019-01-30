package com.avnet.emasia.webquote.stm.ejb;

import java.util.List;
import java.util.logging.Logger;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import com.avnet.emasia.webquote.entity.BizUnit;
import com.avnet.emasia.webquote.entity.SubregionRecipientMapping;
import com.avnet.emasia.webquote.entity.SubregionTeamMapping;


@Stateless
@LocalBean
public class SubregionTeamMappingSB {
	@PersistenceContext(unitName = "Server_Source")
	EntityManager em;
	
	private static Logger logger = Logger.getLogger(MfrRequestInfoSB.class.getName());

	public List<SubregionTeamMapping> findAllTeams(BizUnit bu) {
		String sql = "select r from SubregionTeamMapping r where r.bizUnit = :bizUnit order by r.subRegion";
		Query query = em.createQuery(sql, SubregionTeamMapping.class);
		query.setParameter("bizUnit", bu);	
		List<SubregionTeamMapping> teams = query.getResultList();
		return teams;
		
	}
	
	

}
