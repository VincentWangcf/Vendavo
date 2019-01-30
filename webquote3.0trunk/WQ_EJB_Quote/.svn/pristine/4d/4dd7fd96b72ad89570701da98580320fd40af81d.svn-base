package com.avnet.emasia.webquote.quote.ejb;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@Stateless
@LocalBean
public class EntityLisenterSB {
	@PersistenceContext(unitName = "Server_Source")
	private EntityManager em;

	public EntityManager getEntityManager() {
		return em;
	}
}
