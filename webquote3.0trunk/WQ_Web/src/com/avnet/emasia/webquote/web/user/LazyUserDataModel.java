package com.avnet.emasia.webquote.web.user;

/**
 * @author Lin, Tough(901518)
 * Created on 2013-1-31
 */


import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;

import com.avnet.emasia.webquote.entity.User;
import com.avnet.emasia.webquote.user.ejb.UserSB;


public class LazyUserDataModel extends LazyDataModel<User> {
    

	private static final long serialVersionUID = -1929247359663583326L;
	
	private UserSB userSB;
    
    public LazyUserDataModel(UserSB userSB) {
        this.userSB = userSB;
    }
    
    @Override
    public User getRowData(String rowKey) {
        
        return userSB.findByEmployeeIdLazily(rowKey);
    }

    @Override
    public Object getRowKey(User user) {
        return user.getEmployeeId();
    }

    @Override
    public List<User> load(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String,Object> filters) {
    	
        
        
        String employeeId = null;
        String name = null;
        String emailAddress = null;
        String phoneNumber = null;
        Boolean active = null;


        for(Iterator<String> it = filters.keySet().iterator(); it.hasNext();) {
                String filterProperty = it.next();
                String filterValue = (String)filters.get(filterProperty);
                
                if(filterProperty.equalsIgnoreCase("employeeId")){
                	employeeId = filterValue; 
                }

                if(filterProperty.equalsIgnoreCase("name")){
                	name = filterValue; 
                }
                
                if(filterProperty.equalsIgnoreCase("phoneNumber")){
                	phoneNumber = filterValue; 
                }
                
                if(filterProperty.equalsIgnoreCase("emailAddress")){
                	emailAddress = filterValue; 
                }
                
                if(filterProperty.equalsIgnoreCase("active")){
                	try{
                		active = Boolean.parseBoolean(filterValue);
                	}catch(Exception e){
                		Logger.getLogger(LazyUserDataModel.class.getSimpleName()).log(Level.SEVERE, "exception in parsing boolean boolean , exception message : "+e.getMessage(), e);
                		active = null;
                	}
                	 
                }
                

        }
        
        List<User> data = userSB.findByPage(first, pageSize, employeeId, name, phoneNumber, emailAddress, active);
            


        //rowCount
        long dataSize = userSB.getUserCount(employeeId, name, emailAddress, phoneNumber, active);
        this.setRowCount((int) dataSize);

        return data;

    }
}
                    