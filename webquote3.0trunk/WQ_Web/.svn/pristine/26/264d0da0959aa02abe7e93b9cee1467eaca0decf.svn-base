package com.avnet.emasia.webquote.web.quote.cache;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import java.util.TreeMap;

import com.avnet.emasia.webquote.entity.BizUnit;
import com.avnet.emasia.webquote.entity.SalesOrg;

public class SalesOrgCacheManager implements Serializable {

    private static final TreeMap SALES_ORG_CACHE = new TreeMap();

    public static List<SalesOrg> getSalesOrgByBizUnit(String bizUnitCode) {
        return (List<SalesOrg>) SALES_ORG_CACHE.get(bizUnitCode);
    }

    public static void putSalesOrg(SalesOrg salesOrg) {
    	List<BizUnit> bizUnits = salesOrg.getBizUnits();
    	if(bizUnits != null){
    		for(BizUnit bizUnit : bizUnits){
    			List<SalesOrg> salesOrgs = null;
    			if(SALES_ORG_CACHE.containsKey(bizUnit.getName())){
    				salesOrgs = (List<SalesOrg>) SALES_ORG_CACHE.get(bizUnit.getName());    				
    			} else {
    				salesOrgs = new ArrayList<SalesOrg>();    				
    			}
    			salesOrgs.add(salesOrg);
    			SALES_ORG_CACHE.put(bizUnit.getName(), salesOrgs);    			
    		}
    	}
    }
    
    public static List<String> getSalesOrgCodesByBizUnit(String bizUnitCode){
    	List<SalesOrg> salesOrgs = getSalesOrgByBizUnit(bizUnitCode);
    	List<String> suggested = new ArrayList<String>();
    	if(salesOrgs != null){  
	    	for (SalesOrg salesOrg : salesOrgs) {
	   			suggested.add(salesOrg.getName());
	    	}
    	}
    	return suggested;
    }
      
    public static int getSize(){
    	return SALES_ORG_CACHE.size();
    }    
    
    public static void clear(){
    	SALES_ORG_CACHE.clear();
    }
    
    static {
    }
}
