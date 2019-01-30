package com.avnet.emasia.webquote.quote.ejb;

import java.util.List;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import com.avnet.emasia.webquote.entity.Country;

/**
 * Session Bean implementation class UserSB
 */
@Stateless
@LocalBean
public class CountrySB {

	@PersistenceContext(unitName="Server_Source")
	EntityManager em;
	
	
	public List<Country> findAll(){
		
		Query query = em.createQuery("select c from Country c");
		List<Country> countries = (List<Country>)query.getResultList();
		
		return countries;
	}
		
}
