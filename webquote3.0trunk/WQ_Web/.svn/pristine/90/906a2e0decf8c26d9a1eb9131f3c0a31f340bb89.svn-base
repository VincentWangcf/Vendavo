package com.avnet.emasia.webquote.web.quote.cache;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeMap;

import com.avnet.emasia.webquote.entity.Role;

public class RoleCacheManager implements Serializable {

    private static final TreeMap<String, Role> ROLE_CACHE = new TreeMap<String, Role>();

    public static Role getRole(String role) {
        return (Role) ROLE_CACHE.get(role);
    }

    public static void putRole(Role role) {
    	ROLE_CACHE.put(role.getName(), role);
    }
    
    public static List<String> getRoleList(){
    	Set<String> keys =  ROLE_CACHE.keySet();
    	List<String> suggested = new ArrayList<String>();
    	  
    	for (String key : keys) {
   			suggested.add(key);
    	}    	
    	return suggested;
    }
    /**
     * getALLroles Meet the conditions:
     * select r from Role r where r.name != 'ROLE_SYS_ADMIN' and r.active = true 
     * @return  List<Role>
     */  
    public static List<Role>  findAllForUserAdmin(){
     	List<Role> sRoles = new ArrayList<Role>();
    	Role role = null;  
    	Set<String> keys =  ROLE_CACHE.keySet();
    	for (String key : keys) {
    		role = ROLE_CACHE.get(key);
    		if(!"ROLE_SYS_ADMIN".equals(key)&&role.getActive()){
    			sRoles.add(role);	
    		}
    	}  
    	return sRoles;
    }
    public static int getSize(){
    	return ROLE_CACHE.size();
    }
    
    public static void clear(){
    	ROLE_CACHE.clear();
    }
    
    
    static {		
    }

}
