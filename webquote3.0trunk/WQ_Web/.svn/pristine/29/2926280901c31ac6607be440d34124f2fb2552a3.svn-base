package com.avnet.emasia.webquote.web.quote.cache;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeMap;

import com.avnet.emasia.webquote.entity.BizUnit;

public class BizUnitCacheManager implements Serializable {

    private static final TreeMap BIZ_UNIT_CACHE = new TreeMap();

    public static BizUnit getBizUnit(String bizUnit) {
        return (BizUnit) BIZ_UNIT_CACHE.get(bizUnit);
    }

    public static void putBizUnit(BizUnit bizUnit) {
    	BIZ_UNIT_CACHE.put(bizUnit.getName(), bizUnit);
    }
    
    public static List<String> getBizUnitList(){
    	Set<String> keys =  BIZ_UNIT_CACHE.keySet();
    	List<String> suggested = new ArrayList<String>();
    	  
    	for (String key : keys) {
   			suggested.add(key);
    	}    	
    	return suggested;
    }
     
    public static int getSize(){
    	return BIZ_UNIT_CACHE.size();
    }
    
    public static void clear(){
    	BIZ_UNIT_CACHE.clear();
    }
    
    static {		
    }

}
