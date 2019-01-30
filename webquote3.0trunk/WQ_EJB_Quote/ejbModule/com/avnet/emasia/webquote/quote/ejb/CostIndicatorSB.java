package com.avnet.emasia.webquote.quote.ejb;

import java.util.List;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TypedQuery;

import com.avnet.emasia.webquote.entity.CostIndicator;
import com.avnet.emasia.webquote.entity.ManufacturerDetail;

/**
 * Session Bean implementation class UserSB
 */
@Stateless
@LocalBean
public class CostIndicatorSB {

	@PersistenceContext(unitName="Server_Source")
	EntityManager em;
	
	
//	public List<String> findAllNameList(){
//		Query query = em.createQuery("select c.name from CostIndicator c ");
//		
//		return query.getResultList();
//	}
	public List<CostIndicator> findAll(){
		
		Query query = em.createQuery("select c from CostIndicator c");
		List<CostIndicator> costIndicators = (List<CostIndicator>)query.getResultList();
		
		return costIndicators;
	}
	
	public List<CostIndicator> findAllActive(){
		
		Query query = em.createQuery("select c from CostIndicator c where c.status = true");
		List<CostIndicator> costIndicators = (List<CostIndicator>)query.getResultList();
		
		return costIndicators;
	}
	
	public void saveCostIndicator(CostIndicator costIndicator) {
		em.persist(costIndicator);
    	em.flush();
	}
	
	public void updateCostIndicator(CostIndicator costIndicator) {
		em.merge(costIndicator);
    	em.flush();
	}
	
	public CostIndicator findCostIndicator(String name){
		TypedQuery<CostIndicator> query = em.createQuery("select r from CostIndicator r where r.name = :name", CostIndicator.class);
		query.setParameter("name", name);
		List<CostIndicator> costIndicatorRules = query.getResultList();
		if(costIndicatorRules.size() ==0){
			return null;
		}else{
			return costIndicatorRules.get(0);
		}
		
		
	}
	
}
