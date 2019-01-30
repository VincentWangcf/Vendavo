package com.avnet.emasia.webquote.web.quote.cache;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeMap;

import com.avnet.emasia.webquote.web.quote.vo.TermsAndConditionsVO;

public class TermsAndConditionsCacheManager implements Serializable {

    private static final TreeMap TERMS_AND_CONDITIONS_CACHE = new TreeMap();

    public static TermsAndConditionsVO getTermsAndConditions(String termsAndConditions) {
        return (TermsAndConditionsVO) TERMS_AND_CONDITIONS_CACHE.get(termsAndConditions);
    }

    public static void putTermsAndConditions(TermsAndConditionsVO termsAndConditions) {
    	TERMS_AND_CONDITIONS_CACHE.put(termsAndConditions.getName(), termsAndConditions);
    }
    
    public static List<TermsAndConditionsVO> getTermsAndConditionsList(){
    	Set<String> keys =  TERMS_AND_CONDITIONS_CACHE.keySet();
    	List<TermsAndConditionsVO> suggested = new ArrayList<TermsAndConditionsVO>();
    	  
    	for (String key : keys) {
   			suggested.add((TermsAndConditionsVO) TERMS_AND_CONDITIONS_CACHE.get(key));
    	}    	
    	return suggested;
    }
     
    public static int getSize(){
    	return TERMS_AND_CONDITIONS_CACHE.size();
    }
    
    public static void clear(){
    	TERMS_AND_CONDITIONS_CACHE.clear();
    }

    
    static {		
    }

}
