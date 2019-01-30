package com.avnet.emasia.webquote.web.quote.cache;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

import com.avnet.emasia.webquote.entity.BizUnit;
import com.avnet.emasia.webquote.entity.User;
@Deprecated
public class UserCacheManager implements Serializable {

    private static final TreeMap USER_CACHE = new TreeMap();
    private static final TreeMap EMAIL_ADDRESS_CACHE = new TreeMap();

    public static User getUser(String employeeId) {
        return (User) USER_CACHE.get(employeeId);
    }

    public static void putUser(User user) {
		USER_CACHE.put(user.getEmployeeId(), user);
    }
    
    public static int getSize(){
    	return EMAIL_ADDRESS_CACHE.size();
    }

    public static void putEmailAddress(User user) {
    	BizUnit bizUnit = user.getDefaultBizUnit();
    	List<User> users = (List<User>) EMAIL_ADDRESS_CACHE.get(bizUnit.getName());
    	if(users == null)
    		users = new ArrayList<User>();
    	users.add(user);
    	EMAIL_ADDRESS_CACHE.put(bizUnit.getName(), users);
    }

    public static List<User> getEmailAddress(BizUnit bizUnit) {
    	return (List<User>) EMAIL_ADDRESS_CACHE.get(bizUnit.getName());
    }
    
    public static void clear(){
    	USER_CACHE.clear();
    	EMAIL_ADDRESS_CACHE.clear();
    }

    static {		
    }

}
