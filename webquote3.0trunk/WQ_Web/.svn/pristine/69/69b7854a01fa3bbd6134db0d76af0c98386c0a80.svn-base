package com.avnet.emasia.webquote.web.quote.cache;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeMap;

public class ProjectNameCacheManager implements Serializable {

    private static final TreeMap PORJECT_NAME_CACHE = new TreeMap();
    private static final TreeMap PROJECT_NAME_BY_SOLD_TO_AND_END_CUSTOMER_CACHE = new TreeMap();

    public static List<String> getProjectNamesBySoldToAndEndCustomer(String soldToCustomerNumber, String endCustomerNumber) {
        return (List<String>) PROJECT_NAME_BY_SOLD_TO_AND_END_CUSTOMER_CACHE.get(soldToCustomerNumber+"_"+endCustomerNumber);
    }

    public static void putProjectNamesBySoldToAndEndCustomer(String soldToCustomerNumber, String endCustomerNumber, List<String> projectNames) {
    	PROJECT_NAME_BY_SOLD_TO_AND_END_CUSTOMER_CACHE.put(soldToCustomerNumber+"_"+endCustomerNumber, projectNames);
    }

    public static String getProjectName(String code) {
        return (String) PORJECT_NAME_CACHE.get(code);
    }

    public static void putProjectName(String code) {
		PORJECT_NAME_CACHE.put(code, code);
    }
    
    public static List<String> getAllProjectNames(){
    	Set<String> keys =  PORJECT_NAME_CACHE.keySet();
    	List<String> suggested = new ArrayList<String>();
    	  
    	for (String key : keys) {
   			suggested.add(key);
    	}
    	
    	return suggested;
    }    

    public static List<String> getMatchedProjectNames(String word){
    	Set<String> keys =  PORJECT_NAME_CACHE.keySet();
    	List<String> suggested = new ArrayList<String>();
    	  
    	for (String key : keys) {
    		if(key.contains(word)){
    			suggested.add(key);
    		}
    	}
    	
    	return suggested;
    }    

    static {		
    }

}
