package com.avnet.emasia.webquote.utilites.web.common;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
//import java.util.List;
import java.util.TreeMap;
import java.util.logging.Logger;

import javax.cache.integration.CacheLoader;
import javax.cache.integration.CacheLoaderException;
import javax.ejb.EJB;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.TypedQuery;

import com.avnet.emasia.webquote.entity.BizUnit;
import com.avnet.emasia.webquote.entity.City;
import com.avnet.emasia.webquote.entity.Country;
import com.avnet.emasia.webquote.entity.LocaleMaster;
import com.avnet.emasia.webquote.entity.Role;
import com.avnet.emasia.webquote.quote.ejb.CountrySB;
import com.avnet.emasia.webquote.web.quote.cache.BizUnitCacheManager;
import com.avnet.emasia.webquote.web.quote.cache.CityCacheManager;


//public class SimpleCacheLoader implements CacheLoader<Integer, List<String>> {
public class CommonCacheLoader<K, V> implements CacheLoader<K, List<V>> {
	private static final Logger LOG = Logger.getLogger(CommonCacheLoader.class.getName());
	
	private EntityManager em;
	
	public EntityManager getEm() {
		return em;
	}
	
	public void setEm(EntityManager em) {
		this.em = em;
	}

	public CommonCacheLoader(EntityManager em){
		this.em = em;
	}

	@Override
	public Map loadAll(Iterable keys) throws CacheLoaderException {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public List<V> load(K key) throws CacheLoaderException {
		LOG.info("=========================================");
		LOG.info("CommonCache - Key: " + key + " - Load data from DB...");
		List<Country> countries = new ArrayList<Country>();

		String k = (String)key;
		
		if(k.equals(Constants.COUNTRY)) {
			Query query = em.createQuery("select c from Country c");
			countries = (List<Country>)query.getResultList();
			
			return (List<V>)countries;
			
		}else if(k.equals(Constants.LOCALE_MASTER)) {
			Query query = em.createQuery("SELECT e FROM LocaleMaster e");
			List<LocaleMaster> localMaster = (List<LocaleMaster>)query.getResultList();
			
			return (List<V>)localMaster;
		}
		
		return null;

	}
	
//	private final TreeMap cityMap = new TreeMap();
//	private final TreeMap bizUnitMap = new TreeMap();
	
//	private void putCity(City city) {
//    	List<City> list = new ArrayList<City>();
//    	if(cityMap.containsKey(city.getCountry().getId())){
//    		list = (List<City>) cityMap.get(city.getCountry().getId());
//    	} else {
//    		list = new ArrayList<City>();
//    	}
//    	list.add(city);
//    	cityMap.put(city.getCountry().getId(), list);
//    }



}

