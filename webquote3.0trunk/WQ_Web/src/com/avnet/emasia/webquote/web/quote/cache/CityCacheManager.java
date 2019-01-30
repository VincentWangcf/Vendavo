package com.avnet.emasia.webquote.web.quote.cache;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

import com.avnet.emasia.webquote.entity.City;

public class CityCacheManager implements Serializable {

    private static final TreeMap CITY_CACHE = new TreeMap();

    public static List<City> getCitiesByCountry(String countryId) {
        return (List<City>) CITY_CACHE.get(countryId);
    }

    public static void putCitiesByCountry(String countryId, List<City> cities) {
        CITY_CACHE.put(countryId, cities);
    }
    
    public static void putCity(City city) {
    	List<City> list = new ArrayList<City>();
    	if(CITY_CACHE.containsKey(city.getCountry().getId())){
    		list = (List<City>) CITY_CACHE.get(city.getCountry().getId());
    	} else {
    		list = new ArrayList<City>();
    	}
    	list.add(city);
		CITY_CACHE.put(city.getCountry().getId(), list);
    }
    
    public static void clear(){
    	CITY_CACHE.clear();
    }
    
    static {		
    }

}
