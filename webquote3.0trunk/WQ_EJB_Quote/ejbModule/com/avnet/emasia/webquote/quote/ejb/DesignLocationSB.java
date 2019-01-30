package com.avnet.emasia.webquote.quote.ejb;

import java.util.List;
import java.util.logging.Logger;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TypedQuery;

import com.avnet.emasia.webquote.entity.City;
import com.avnet.emasia.webquote.entity.Country;
import com.avnet.emasia.webquote.entity.Customer;
import com.avnet.emasia.webquote.entity.DesignLocation;
import com.avnet.emasia.webquote.entity.Quote;
import com.avnet.emasia.webquote.entity.QuoteItem;
import com.avnet.emasia.webquote.entity.User;

/**
 * Session Bean implementation class UserSB
 */
@Stateless
@LocalBean
public class DesignLocationSB {
	
	private static final Logger LOG =Logger.getLogger(DesignLocationSB.class.getName());

	@PersistenceContext(unitName="Server_Source")
	EntityManager em;
	
    public City findByPK(String pk){
    	return em.find(City.class, pk);
    }	
	
	public List<String> findAllRegion(){
		TypedQuery<String> query = em.createQuery("select distinct c.designRegion from DesignLocation c order by c.name",String.class);
		List<String> regions = query.getResultList();
		return regions;
	}
	
	public List<DesignLocation> findAllDesignLocation(String region) {
		TypedQuery<DesignLocation> query = em.createQuery("select distinct c from DesignLocation c order by c.name", DesignLocation.class);
		List<DesignLocation> results = query.getResultList();
		return results;
	}
	
	public List<String> findLocationByRegion(String region) {
		TypedQuery<String> query = em.createQuery("select distinct c.code from DesignLocation c where c.designRegion = :designRegion order by c.code", String.class);
		query.setParameter("designRegion", region);
		List<String> results = query.getResultList();
		return results;
	}
	
	//Added by Damon@20151224
	public List<DesignLocation> findAllDesignLocation() {
		TypedQuery<DesignLocation> query = em.createQuery("select distinct c from DesignLocation c order by c.name", DesignLocation.class);
		List<DesignLocation> results = query.getResultList();
		return results;
	}
	
	public DesignLocation findDesignLocationPK(String k){
		 return em.find(DesignLocation.class, k);
	}
	

	
	
}
