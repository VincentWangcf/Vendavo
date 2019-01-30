package com.avnet.emasia.webquote.web.quote.cache;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import java.util.TreeMap;

import com.avnet.emasia.webquote.entity.BizUnit;
import com.avnet.emasia.webquote.entity.Plant;
import com.avnet.emasia.webquote.entity.SalesOrg;

public class PlantCacheManager implements Serializable {

    private static final TreeMap PLANT_CACHE = new TreeMap();

    public static List<Plant> getPlantByBizUnit(String bizUnitCode) {
        return (List<Plant>) PLANT_CACHE.get(bizUnitCode);
    }

    public static void putPlant(Plant plant) {
    	List<SalesOrg> salesOrgs = plant.getSalesOrgs();
    	if(salesOrgs != null){
    		for(SalesOrg salesOrg : salesOrgs){
    			List<BizUnit> bizUnits = salesOrg.getBizUnits();   		
        		for(BizUnit bizUnit : bizUnits){
        			List<Plant> plants = null;
	    			if(PLANT_CACHE.containsKey(bizUnit.getName())){
	    				plants = (List<Plant>) PLANT_CACHE.get(bizUnit.getName());    				
	    			} else {
	    				plants = new ArrayList<Plant>();    				
	    			}
	    			plants.add(plant);
	    			PLANT_CACHE.put(bizUnit.getName(), plants);  
        		}
    		}
    	}
    }
    
    public static List<String> getPlantCodesByBizUnit(String bizUnitCode){
    	List<Plant> plants = getPlantByBizUnit(bizUnitCode);
    	List<String> suggested = new ArrayList<String>();
    	if(plants != null){  
	    	for (Plant plant : plants) {
	   			suggested.add(plant.getName());
	    	}
    	}
    	return suggested;
    }
      
    public static int getSize(){
    	return PLANT_CACHE.size();
    }    
    
    public static void clear(){
    	PLANT_CACHE.clear();
    }

    
    static {
    }
}
