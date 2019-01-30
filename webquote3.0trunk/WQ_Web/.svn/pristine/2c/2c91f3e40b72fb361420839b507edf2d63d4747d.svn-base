package com.avnet.emasia.webquote.web.quote.cache;

import java.io.Serializable;
import java.util.List;
import java.util.TreeMap;

import com.avnet.emasia.webquote.entity.QuoteItem;

public class QuotationHistoryCacheManager implements Serializable {

    private static final TreeMap QUOTATION_HISTORY_CACHE = new TreeMap();

    public static List<QuoteItem> getQuotationHistory(String partNumber) {
        return (List<QuoteItem>) QUOTATION_HISTORY_CACHE.get(partNumber);
    }

    public static void putQuotationHistory(String partNumber, List<QuoteItem> posList) {
    	QUOTATION_HISTORY_CACHE.put(partNumber, posList);
    }
    
    public static boolean containsKey(String partNumber){
    	return QUOTATION_HISTORY_CACHE.containsKey(partNumber);
    }
    
    static {
    }

}
