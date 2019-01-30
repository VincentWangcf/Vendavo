package com.avnet.emasia.webquote.cache;

import java.io.Serializable;
import java.util.TreeMap;

import com.avnet.emasia.webquote.entity.ManufacturerDetail;

public class MfrDetailCacheManager implements Serializable {
	
	private static final long serialVersionUID = 165494105152872036L;
    private static final TreeMap MFR_DETAIL_CACHE = new TreeMap();

    public static ManufacturerDetail getMfrDetail(long manufacturerId, long productGroupId, long productCategoryId, String bizUnit) {
        return (ManufacturerDetail) MFR_DETAIL_CACHE.get(manufacturerId+"_"+productGroupId+"_"+productCategoryId+"_"+bizUnit);
    }

    public static void putMfrDetail(ManufacturerDetail mfrDetail) {    
    	if(mfrDetail!=null){
    		MFR_DETAIL_CACHE.put(mfrDetail.getManufacturer().getId()+"_"+mfrDetail.getProductGroup().getId()+"_"
    	+mfrDetail.getProductCategory().getId()+"_"+mfrDetail.getBizUnit().getName(), mfrDetail); 
    	}
    }
    
    public static TreeMap getAllMfrDetail(){
    	return MFR_DETAIL_CACHE;
    }
    
    public static int getSize(){
    	return MFR_DETAIL_CACHE.size();
    }
    
    public static void clear(){
    	MFR_DETAIL_CACHE.clear();
    }
   
    static { 		
    }

}
