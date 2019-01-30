package com.avnet.emasia.webquote.user.ejb;

import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import com.avnet.emasia.webquote.entity.BizUnit;
import com.avnet.emasia.webquote.entity.ExchangeRate;

/**
 * Session Bean implementation class UserSB
 */
@Stateless
@LocalBean
public class BizUnitSB {

	@PersistenceContext(unitName="Server_Source")
	EntityManager em;
	
	
	public List<BizUnit> findAll(){
		
		Query query = em.createQuery("select b from BizUnit b");
		List<BizUnit> bizUnits = (List<BizUnit>)query.getResultList();
		
		return bizUnits;
	}
	
	public BizUnit findByPk(String name){
		return em.find(BizUnit.class, name);		
	}
	/*******for bozUnit order ****/
	public LinkedHashSet<String> getAllBizUnitsByOrder() {
		Query query = em.createQuery("select b from BizUnit b where b.higherBizUnit is null");
		List<BizUnit> bizUnits = (List<BizUnit>)query.getResultList();
		LinkedHashSet<String> bizSet = new LinkedHashSet<String>();
		for(BizUnit bizUnit :bizUnits ) {
			/******record the region BY ORDER****/
			for(String bizSub :bizUnit.getSelfAndAllSubBizUnits()) {
				bizSet.add(bizSub);
			}
		}
		return bizSet;
	}
	public  BizUnit getAllowCurrenciesByName(String name) {
		
		Query query = em.createQuery("select b from BizUnit b where b.name=?1");
		query.setParameter(1, name);
		List resultList = query.getResultList();
		return (BizUnit) resultList.get(0);
	}
	public void updateCurrencybyRegion(BizUnit curr) {
		em.merge(curr);
		em.flush();

	}
}
