package com.avnet.emasia.webquote.user.ejb;

import java.util.List;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TypedQuery;

import com.avnet.emasia.webquote.entity.AppFunction;

/**
 * Session Bean implementation class RoleSB
 */
@Stateless
@LocalBean
public class AppFunctionSB {

	@PersistenceContext(unitName="Server_Source")
	EntityManager em;
	

	
	public List<AppFunction> findAll(){
		
		TypedQuery<AppFunction> query = em.createQuery("select r from AppFunction r", AppFunction.class);
		List<AppFunction> roles = query.getResultList();

		return roles;
	}

}
