package com.avnet.emasia.webquote.web.quote.cache;

import java.io.Serializable;
import java.util.List;
import java.util.TreeMap;

import com.avnet.emasia.webquote.web.quote.vo.PosStatisticVO;

public class PosCacheManager implements Serializable {

    private static final TreeMap POS_CACHE = new TreeMap();

    public static List<PosStatisticVO> getPosHistory(String partNumber) {
        return (List<PosStatisticVO>) POS_CACHE.get(partNumber);
    }

    public static void putPosHistory(String partNumber, List<PosStatisticVO> posList) {
    	POS_CACHE.put(partNumber, posList);
    }
    
    public static boolean containsKey(String partNumber){
    	return POS_CACHE.containsKey(partNumber);
    }
    
    static {
    }

}
