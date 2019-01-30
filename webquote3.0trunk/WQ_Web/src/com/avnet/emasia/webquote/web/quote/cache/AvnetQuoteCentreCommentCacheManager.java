package com.avnet.emasia.webquote.web.quote.cache;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeMap;

import com.avnet.emasia.webquote.web.quote.vo.AvnetQuoteCentreCommentVO;

public class AvnetQuoteCentreCommentCacheManager implements Serializable {

    private static final TreeMap AVNET_QUOTE_CENTRE_COMMENT_CACHE = new TreeMap();

    public static AvnetQuoteCentreCommentVO getAvnetQuoteCentreComment(String avnetQuoteCentreComment) {
        return (AvnetQuoteCentreCommentVO) AVNET_QUOTE_CENTRE_COMMENT_CACHE.get(avnetQuoteCentreComment);
    }

    public static void putAvnetQuoteCentreComment(AvnetQuoteCentreCommentVO avnetQuoteCentreComment) {
    	AVNET_QUOTE_CENTRE_COMMENT_CACHE.put(avnetQuoteCentreComment.getName(), avnetQuoteCentreComment);
    }
    
    public static List<AvnetQuoteCentreCommentVO> getAvnetQuoteCentreCommentList(){
    	Set<String> keys =  AVNET_QUOTE_CENTRE_COMMENT_CACHE.keySet();
    	List<AvnetQuoteCentreCommentVO> suggested = new ArrayList<AvnetQuoteCentreCommentVO>();
    	  
    	for (String key : keys) {
   			suggested.add((AvnetQuoteCentreCommentVO) AVNET_QUOTE_CENTRE_COMMENT_CACHE.get(key));
    	}    	
    	return suggested;
    }
     
    public static int getSize(){
    	return AVNET_QUOTE_CENTRE_COMMENT_CACHE.size();
    }
    
    public static void clear(){
    	AVNET_QUOTE_CENTRE_COMMENT_CACHE.clear();
    }
    
    static {		
    }

}
