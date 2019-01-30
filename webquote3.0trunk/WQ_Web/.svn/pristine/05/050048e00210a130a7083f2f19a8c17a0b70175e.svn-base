package com.avnet.emasia.webquote.web.quote.cache;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeMap;

public class QuoteTypeCacheManager implements Serializable {

    private static final TreeMap QUOTE_TYPE_CACHE = new TreeMap();

    public static String getQuoteType(String quoteType) {
        return (String) QUOTE_TYPE_CACHE.get(quoteType);
    }

    public static void putQuoteType(String quoteType) {
    	QUOTE_TYPE_CACHE.put(quoteType, quoteType);
    }
    
    public static List<String> getQuoteTypeList(){
    	Set<String> keys =  QUOTE_TYPE_CACHE.keySet();
    	List<String> suggested = new ArrayList<String>();
    	  
    	for (String key : keys) {
   			suggested.add(key);
    	}    	
    	return suggested;
    }
     
    public static int getSize(){
    	return QUOTE_TYPE_CACHE.size();
    }

    public static void clear(){
    	QUOTE_TYPE_CACHE.clear();
    }
    
    static {		
    }

}
