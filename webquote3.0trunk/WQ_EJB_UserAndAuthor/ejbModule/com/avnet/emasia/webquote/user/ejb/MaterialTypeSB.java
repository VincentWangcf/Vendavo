package com.avnet.emasia.webquote.user.ejb;

import java.util.List;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import com.avnet.emasia.webquote.entity.MaterialType;

/**
 * Session Bean implementation class UserSB
 */
@Stateless
@LocalBean
public class MaterialTypeSB {

	@PersistenceContext(unitName="Server_Source")
	EntityManager em;
	
    public MaterialType findByPK(String pk){
    	return em.find(MaterialType.class, pk);
    }	
	
	public List<MaterialType> findAll(){
		
		Query query = em.createQuery("select t from MaterialType t" );
		List<MaterialType> materialTypes = (List<MaterialType>)query.getResultList();
		
		return materialTypes;
	}
	
}
