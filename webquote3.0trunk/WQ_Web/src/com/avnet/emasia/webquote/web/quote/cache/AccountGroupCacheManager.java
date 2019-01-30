package com.avnet.emasia.webquote.web.quote.cache;

import java.io.Serializable;
import java.util.TreeMap;

public class AccountGroupCacheManager implements Serializable {

    private static final TreeMap ACCOUNT_GROUP_CACHE = new TreeMap();

    public static String getAccountGroup(String accountGroup) {
        return (String) ACCOUNT_GROUP_CACHE.get(accountGroup);
    }

    public static void putAccountGroup(String accountGroup, String description) {
    	ACCOUNT_GROUP_CACHE.put(accountGroup, description);
    }
         
    public static int getSize(){
    	return ACCOUNT_GROUP_CACHE.size();
    }

    public static void clear(){
    	ACCOUNT_GROUP_CACHE.clear();
    }
    
    
    static {		
    }

}
