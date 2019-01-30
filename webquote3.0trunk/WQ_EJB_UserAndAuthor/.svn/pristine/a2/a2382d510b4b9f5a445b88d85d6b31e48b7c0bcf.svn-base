package com.avnet.emasia.webquote.user.ejb;

import java.util.List;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import com.avnet.emasia.webquote.entity.Manufacturer;

/**
 * Session Bean implementation class UserSB
 */
@Stateless
@LocalBean
public class MfrSB {

	@PersistenceContext(unitName="Server_Source")
	EntityManager em;
	
	
	public List<Manufacturer> findAll(){
		
		Query query = em.createQuery("select m from Manufacturer m");
		List<Manufacturer> mfrs = (List<Manufacturer>)query.getResultList();
		
		return mfrs;
	}
	
}
