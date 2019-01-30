package com.avnet.emasia.webquote.quote.ejb;

import java.util.List;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import com.avnet.emasia.webquote.entity.City;
import com.avnet.emasia.webquote.entity.Country;
import com.avnet.emasia.webquote.entity.Customer;

/**
 * Session Bean implementation class UserSB
 */
@Stateless
@LocalBean
public class CitySB {

	@PersistenceContext(unitName="Server_Source")
	EntityManager em;
	
    public City findByPK(String pk){
    	return em.find(City.class, pk);
    }	
	
	public List<City> findAll(){
		
		Query query = em.createQuery("select c from City c order by c.name");
		List<City> countries = (List<City>)query.getResultList();
		
		return countries;
	}
	
	public List<City> findByCountry(Country country){
		Query query = em.createQuery("select c from City c where c.country = :country order by c.name");
		query.setParameter("country", country);
		List<City> cities = query.getResultList();
		
		return cities;		
	}
	
}
