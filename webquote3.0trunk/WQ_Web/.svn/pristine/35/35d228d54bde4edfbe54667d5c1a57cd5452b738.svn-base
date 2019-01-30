package com.avnet.emasia.webquote.web.quote.cache;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeMap;

public class DesignInCatCacheManager implements Serializable {

    private static final TreeMap DESIGN_IN_CAT_CACHE = new TreeMap();

    public static String getDesignInCat(String designInCat) {
        return (String) DESIGN_IN_CAT_CACHE.get(designInCat);
    }

    public static void putDesignInCat(String designInCat) {
    	DESIGN_IN_CAT_CACHE.put(designInCat, designInCat);
    }
    
    public static List<String> getDesignInCatList(){
    	Set<String> keys =  DESIGN_IN_CAT_CACHE.keySet();
    	List<String> suggested = new ArrayList<String>();
    	  
    	for (String key : keys) {
   			suggested.add(key);
    	}    	
    	return suggested;
    }
     
    public static int getSize(){
    	return DESIGN_IN_CAT_CACHE.size();
    }
    
    public static void clear(){
    	DESIGN_IN_CAT_CACHE.clear();
    }
    
    static {		
    }

}
