package com.avnet.emasia.webquote.web.quote.cache;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.avnet.emasia.webquote.entity.Country;

public class CountryCacheManager implements Serializable {

    private static List<Country> countryList = new ArrayList<Country>();

    public static List<Country> getCountries(){
    	return countryList;
    }
    
    public static void setCountries(List<Country> countries) {
    	countryList = countries;
    }
    
    public static Country getCountry(String code){
    	if(countryList != null){
	    	for(Country country : countryList){
	    		if(country.getId().equals(code)){
	    			return country;
	    		}
	    	}
    	}
    	return null;
    }
    
    public static void clear(){
    	countryList.clear();
    }
        
    static {		
    }

}
