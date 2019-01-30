package com.avnet.emasia.webquote.web.quote.cache;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeMap;

public class QtyIndicatorCacheManager implements Serializable {

    private static final TreeMap QTY_INDICATOR_CACHE = new TreeMap();

    public static String getQtyIndicator(String qtyIndicator) {
        return (String) QTY_INDICATOR_CACHE.get(qtyIndicator);
    }

    public static void putQtyIndicator(String qtyIndicator) {
    	QTY_INDICATOR_CACHE.put(qtyIndicator, qtyIndicator);
    }
    
    public static List<String> getQtyIndicatorList(){
    	Set<String> keys =  QTY_INDICATOR_CACHE.keySet();
    	List<String> suggested = new ArrayList<String>();
    	  
    	for (String key : keys) {
   			suggested.add((String) QTY_INDICATOR_CACHE.get(key));
    	}    	
    	return suggested;
    }
     
    public static int getSize(){
    	return QTY_INDICATOR_CACHE.size();
    }
    
    public static void clear(){
    	QTY_INDICATOR_CACHE.clear();
    }

    static {		
    }

}
