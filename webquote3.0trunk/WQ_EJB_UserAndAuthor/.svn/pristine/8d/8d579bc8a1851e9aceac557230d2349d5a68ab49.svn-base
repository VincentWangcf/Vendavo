package com.avnet.emasia.webquote.user.ejb;

import java.util.List;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TypedQuery;

import com.avnet.emasia.webquote.entity.ProgramType;

/**
 * Session Bean implementation class UserSB
 */
@Stateless
@LocalBean
public class ProgramTypeSB {

	@PersistenceContext(unitName = "Server_Source")
	EntityManager em;

	public ProgramType findByName(String name) {
		TypedQuery<ProgramType> query = em.createQuery("select p from ProgramType p where p.name= :name",ProgramType.class);
		query.setParameter("name", name);
		query.setMaxResults(1);
		List<ProgramType> list = query.getResultList();
		ProgramType programType = null;
		if(list!=null&&list.size()>0){
			programType = list.get(0);
		}
		return programType;
	}

	public List<ProgramType> findAll() {

		Query query = em.createQuery("select p from ProgramType p");
		List<ProgramType> programTypes = (List<ProgramType>) query
				.getResultList();

		return programTypes;
	}
}
