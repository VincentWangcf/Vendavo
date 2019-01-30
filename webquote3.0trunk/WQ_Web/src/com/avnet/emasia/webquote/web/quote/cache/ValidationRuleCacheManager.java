package com.avnet.emasia.webquote.web.quote.cache;

import java.io.Serializable;
import java.util.TreeMap;
import com.avnet.emasia.webquote.web.quote.vo.ValidationRuleVO;

public class ValidationRuleCacheManager implements Serializable {
	
    private static final TreeMap VALIDATION_RULE_CACHE = new TreeMap();

    public static ValidationRuleVO getValidationRule(String region, String mfr) {
        return (ValidationRuleVO) VALIDATION_RULE_CACHE.get(region+"_"+mfr);
    }

    public static void putValidationRule(ValidationRuleVO validationRule) {
		VALIDATION_RULE_CACHE.put(validationRule.getRegion()+"_"+validationRule.getMfr(), validationRule);
    }   

    public static int getSize(){
    	return VALIDATION_RULE_CACHE.size();
    }
    
    public static void clear(){
    	VALIDATION_RULE_CACHE.clear();
    }

    static {
    }

}
