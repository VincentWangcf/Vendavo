package com.avnet.emasia.webquote.web.quote.cache;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeMap;

import com.avnet.emasia.webquote.entity.CostIndicator;

public class CostIndicatorCacheManager implements Serializable {

    private static final TreeMap COST_INDICATOR_CACHE = new TreeMap();

    public static CostIndicator getCostIndicator(String costIndicator) {
        return (CostIndicator) COST_INDICATOR_CACHE.get(costIndicator);
    }

    public static void putCostIndicator(CostIndicator costIndicator) {
    	COST_INDICATOR_CACHE.put(costIndicator.getName(), costIndicator);
    }
    
    public static List<String> getCostIndicator(){    	
    	Set<String> keys =  COST_INDICATOR_CACHE.keySet();
    	List<String> suggested = new ArrayList<String>();    	  
    	for (String key : keys) {
    		CostIndicator costIndicator = (CostIndicator) COST_INDICATOR_CACHE.get(key);
    		suggested.add(costIndicator.getName());
    	}    	
    	return suggested;
    }
     
    public static int getSize(){
    	return COST_INDICATOR_CACHE.size();
    }
    
    public static void clear(){
    	COST_INDICATOR_CACHE.clear();
    }

    
    static {		
    }

}
