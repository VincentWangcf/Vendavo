package com.avnet.emasia.webquote.user.ejb;

import java.util.List;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import com.avnet.emasia.webquote.entity.SalesOrg;

/**
 * Session Bean implementation class SalesOrgSB
 */
@Stateless
@LocalBean
public class SalesOrgSB {

	@PersistenceContext(unitName="Server_Source")
	EntityManager em;
	
	
	public List<SalesOrg> findAll(){
		
		Query query = em.createQuery("select s from SalesOrg s order by s.name");
		List<SalesOrg> salesOrgs = (List<SalesOrg>)query.getResultList();
		
		return salesOrgs;
	}
	
	public SalesOrg findByPk(String name){
		return em.find(SalesOrg.class, name);		
	}
	
}
