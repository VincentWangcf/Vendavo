package com.avnet.emasia.webquote.cache;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap; 

import com.avnet.emasia.webquote.entity.DesignLocation;

public class DesignLocationCacheManager implements Serializable {

	private static final long serialVersionUID = -6946264926201569587L;
	private static final TreeMap DESIGN_LOCATION_CACHE = new TreeMap();
    private static final Map<String,String> DESIGN_LOCATION_MAP = new TreeMap();
    private static final Map<String,List<String>> REGION_LOCATION_MAP = new HashMap<>();
    
    private static final TreeMap DESIGN_REGION_CACHE = new TreeMap();

    public static DesignLocation getDesignLocation(String designLocation) {
        return (DesignLocation) DESIGN_LOCATION_CACHE.get(designLocation);
    }

   public static void putDesignRegion(String designRegion) {
    	DESIGN_REGION_CACHE.put(designRegion, designRegion);
    }
   
   public static String getDesignRegion(String designRegion){
	   return  (String) DESIGN_REGION_CACHE.get(designRegion);
   }
    
    public static void putDesignLocation(DesignLocation designLocation) {
    	DESIGN_LOCATION_CACHE.put(designLocation.getCode(), designLocation);
    	DESIGN_LOCATION_MAP.put(designLocation.getCode(), designLocation.getCode());

    	if(REGION_LOCATION_MAP.get(designLocation.getDesignRegion()) == null){
    		List<String> sis = new ArrayList<>();
    		sis.add(designLocation.getCode());
    		REGION_LOCATION_MAP.put(designLocation.getDesignRegion(), sis);
    	}else{
    		REGION_LOCATION_MAP.get(designLocation.getDesignRegion()).add(designLocation.getCode());
    	}
    }
    
    public static List<String> getLocationByRegion(String region) {
    	if(region == null || "".equals(region)){
    		return new ArrayList<>(DESIGN_LOCATION_MAP.keySet());
    	}
    	ArrayList<String> designLocation = new ArrayList<String>();
//    	designLocation.add("-select-");
    	List<String> cacheRegionLocation = REGION_LOCATION_MAP.get(region);
    	if(cacheRegionLocation != null && !cacheRegionLocation.isEmpty()){
    		designLocation.addAll(REGION_LOCATION_MAP.get(region));
    	}
		return designLocation;
	}
    
    
    public static List<DesignLocation> getDesignLocationList(){
    	Set<DesignLocation> entrySet =  DESIGN_LOCATION_CACHE.entrySet();
    	List<DesignLocation> suggested = new ArrayList<DesignLocation>();
    	  
    	Iterator it=entrySet.iterator();
    	while(it.hasNext()){
    		Entry e= (Entry) it.next();
    		suggested.add((DesignLocation) e.getValue());
    	
    	}
    	
    	return suggested;
    }
    
    public static Map<String,String> getDesignLocationMap(){    	
    	return DESIGN_LOCATION_MAP;
    }
     
    public static List<String> getDesignRegionList(){
    	
    	Set<String> keys=DESIGN_REGION_CACHE.keySet();
    	List<String> regionList =new ArrayList<String>();
    	for(String  key:keys){
    		regionList.add(key);
    	}
    	
    	return regionList;
    }
    
    public static void clear(){
    	DESIGN_LOCATION_CACHE.clear();
    	DESIGN_REGION_CACHE.clear();
    	DESIGN_LOCATION_MAP.clear();
    	REGION_LOCATION_MAP.clear();
    	
    }
    public static int getSize(){
    	return DESIGN_LOCATION_CACHE.size();
    }

    public static void clearDesignRegion(){
    	DESIGN_REGION_CACHE.clear();
    }
    public static int getDesignRegionSize(){
    	return DESIGN_REGION_CACHE.size();
    }
    
}
