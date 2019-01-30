package com.avnet.emasia.webquote.web.quote.cache;

import java.io.Serializable;
import java.util.TreeMap;
import java.util.List;

import com.avnet.emasia.webquote.entity.Manufacturer;

public class MfrCacheManager implements Serializable {
	
    private static final TreeMap MFR_CACHE_BY_REGION = new TreeMap();

    public static List<Manufacturer> getMfrListByBizUnit(String bizUnit) {
        return (List<Manufacturer>) MFR_CACHE_BY_REGION.get(bizUnit);
    }

    public static void putMfrListByBizUnit(List<Manufacturer> mfrs, String bizUnit) {
        MFR_CACHE_BY_REGION.put(bizUnit, mfrs);
    }
        
    public static int getSize(){
    	return MFR_CACHE_BY_REGION.size();
    }
  
	public static Manufacturer getMfrByBizUnit(String manufacturer, String bizUnit) {
		List<Manufacturer> mfrs = getMfrListByBizUnit(bizUnit);
		for(Manufacturer mfr : mfrs){
			if(mfr.getName().equals(manufacturer))
				return mfr;
		}
		return null;
	}
	
    public static void clear(){
    	MFR_CACHE_BY_REGION.clear();
    }
	

    static { 		
    }

}
