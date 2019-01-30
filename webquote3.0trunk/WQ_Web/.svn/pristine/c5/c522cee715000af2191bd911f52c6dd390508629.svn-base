package com.avnet.emasia.webquote.web.quote.cache;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeMap;

public class ApplicationCacheManager implements Serializable {

    private static final TreeMap APPLICATION_CACHE = new TreeMap();

    public static String getApplication(String application) {
        return (String) APPLICATION_CACHE.get(application);
    }

    public static void putApplication(String application) {
    	APPLICATION_CACHE.put(application, application);
    }
    
    public static List<String> getApplicationList(){
    	Set<String> keys =  APPLICATION_CACHE.keySet();
    	List<String> suggested = new ArrayList<String>();
    	  
    	for (String key : keys) {
   			suggested.add(key);
    	}    	
    	return suggested;
    }
     
    public static int getSize(){
    	return APPLICATION_CACHE.size();
    }
    
    public static void clear(){
    	APPLICATION_CACHE.clear();
    }
    
    static {		
    }

}
