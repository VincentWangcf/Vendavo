package com.avnet.emasia.webquote.web.quote.cache;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.TreeMap;
import java.util.List;
import java.util.Set;

import com.avnet.emasia.webquote.entity.Material;
import com.avnet.emasia.webquote.utilites.web.util.QuoteUtil;
import com.avnet.emasia.webquote.web.quote.vo.MaterialVO;

public class MaterialCacheManager implements Serializable {

    private static final TreeMap MATERIAL_CACHE = new TreeMap();
    private static final TreeMap MFR_CACHE = new TreeMap();
    private static final TreeMap SUGGEST_PART_CACHE = new TreeMap();

    public static List<Material> getSuggestMaterial(String mfr, String materialNumber){
    	return (List<Material>) SUGGEST_PART_CACHE.get(mfr+"_"+materialNumber);
    }

    public static void putSuggestMaterial(String mfr, String materialNumber, List<Material> materials){
    	SUGGEST_PART_CACHE.put(mfr+"_"+materialNumber, materials);
    }
    
    
    
    public static MaterialVO getMaterial(String materialNumber) {
        return (MaterialVO) MATERIAL_CACHE.get(materialNumber);
    }

    public static void putMaterial(MaterialVO material) {
        MATERIAL_CACHE.put(material.getFullMfrPartNumber(), material);
        if(material.getMfr()!= null && material.getMfr().toUpperCase().matches("[A-Z]{3}") && !MFR_CACHE.containsKey(material.getMfr().toUpperCase())){
        	MFR_CACHE.put(material.getMfr().toUpperCase(), material.getMfr().toUpperCase());
        }
    }

    public static List<String> getMfrList(){
    	Set<String> keys =  MFR_CACHE.keySet();
    	List<String> suggested = new ArrayList<String>();
    	  
    	for (String key : keys) {
   			suggested.add(key);
    	}
    	return suggested;
    }

    public static List<String> getMatchedMaterialNumberList(String word){
    	Set<String> keys =  MATERIAL_CACHE.keySet();
    	List<String> suggested = new ArrayList<String>();
    	  
    	for (String key : keys) {
    		if(key.contains(word.toUpperCase()))
    			suggested.add(key);
    	}
    	return suggested;
    }

    public static List<String> getMatchedMaterialNumberList(String word, String mfr){
    	Set<String> keys =  MATERIAL_CACHE.keySet();
    	List<String> suggested = new ArrayList<String>();
    	  
    	for (String key : keys) {
    		if(key.contains(word.toUpperCase())){
    			MaterialVO material = (MaterialVO) MATERIAL_CACHE.get(key);
    			if(material.getMfr() != null && material.getMfr().equals(mfr)){
    				suggested.add(key + ", " + material.getMpq() + ", " + material.getMoq() + ", " + material.getLeadTime());
    			}
    		}
    	}
    	return suggested;
    }

	public static List<MaterialVO> getSearchedMaterialNameList(String materialNumberSearch) {
		return getSearchedMaterialNameList(null, materialNumberSearch);
	}
	
	public static List<MaterialVO> getSearchedMaterialNameList(String mfrSearch, String materialNumberSearch) {
    	Set<String> keys =  MATERIAL_CACHE.keySet();
    	List<MaterialVO> suggested = new ArrayList<MaterialVO>();
    	  
    	for (String key : keys) {
    		if(QuoteUtil.isMatch(materialNumberSearch, key)){
   				MaterialVO material = getMaterial(key); 
   				if(mfrSearch == null || material.getMfr() != null && material.getMfr().equals(mfrSearch))
    				suggested.add(material);
    		}
    	}
    	
    	return suggested;
	}    
    
    static {
    }

}
