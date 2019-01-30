package com.avnet.emasia.webquote.user.ejb;

import java.util.List;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import com.avnet.emasia.webquote.entity.Plant;

/**
 * Session Bean implementation class PlantSB
 */
@Stateless
@LocalBean
public class PlantSB {

	@PersistenceContext(unitName="Server_Source")
	EntityManager em;
	
	
	public List<Plant> findAll(){
		
		Query query = em.createQuery("select p from Plant p order by p.name");
		List<Plant> plants = (List<Plant>)query.getResultList();
		
		return plants;
	}
	
	public Plant findByPk(String name){
		return em.find(Plant.class, name);		
	}
	
}
