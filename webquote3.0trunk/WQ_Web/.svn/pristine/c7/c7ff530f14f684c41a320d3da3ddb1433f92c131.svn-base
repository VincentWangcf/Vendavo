package com.avnet.emasia.webquote.web.quote.cache;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeMap;

public class EnquirySegmentCacheManager implements Serializable {

    private static final TreeMap ENQUIRY_SEGMENT_CACHE = new TreeMap();

    public static String getEnquirySegment(String enquirySegment) {
        return (String) ENQUIRY_SEGMENT_CACHE.get(enquirySegment);
    }

    public static void putEnquirySegment(String enquirySegment) {
    	ENQUIRY_SEGMENT_CACHE.put(enquirySegment, enquirySegment);
    }
    
    public static List<String> getEnquirySegmentList(){
    	Set<String> keys =  ENQUIRY_SEGMENT_CACHE.keySet();
    	List<String> suggested = new ArrayList<String>();
    	  
    	for (String key : keys) {
   			suggested.add(key);
    	}    	
    	return suggested;
    }
     
    public static int getSize(){
    	return ENQUIRY_SEGMENT_CACHE.size();
    }
    
    public static void clear(){
    	ENQUIRY_SEGMENT_CACHE.clear();
    }
    
    static {		
    }

}
