package com.avnet.emasia.webquote.web.quote.cache;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeMap;

public class CurrencyCacheManager implements Serializable {

    private static final TreeMap CURRENCY_CACHE = new TreeMap();

    public static String getCurrency(String code) {
        return (String) CURRENCY_CACHE.get(code);
    }

    public static void putCurrency(String code, String description) {
		CURRENCY_CACHE.put(code, description);
    }
    
    public static List<String> getAllCurrencyCode(){
    	Set<String> keys =  CURRENCY_CACHE.keySet();
    	List<String> suggested = new ArrayList<String>();
    	  
    	for (String key : keys) {
   			suggested.add(key);
    	}
    	
    	return suggested;
    }    
    
    static {		
    }

}
