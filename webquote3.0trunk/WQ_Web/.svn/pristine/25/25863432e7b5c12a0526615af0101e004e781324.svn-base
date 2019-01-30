package com.avnet.emasia.webquote.web.user;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import com.avnet.emasia.webquote.entity.AppFunction;
import com.avnet.emasia.webquote.entity.Role;
import com.avnet.emasia.webquote.exception.WebQuoteRuntimeException;
import com.avnet.emasia.webquote.user.ejb.AppFunctionSB;
import com.avnet.emasia.webquote.user.ejb.RoleSB;

/**
 * @author Lin, Tough(901518)
 * Created on 2013-1-24
 */

@FacesConverter("webquote.user.Role")
public class RoleConverter implements Converter {
	
	private RoleSB roleSB;
	
	List<Role> roles;
	
	public RoleConverter(){
		
	}
	
	
	private void initialize(){
		
		try {
			Context context = new InitialContext();
			roleSB = (RoleSB) context.lookup("java:app/WQ_EJB_UserAndAuthor/RoleSB!com.avnet.emasia.webquote.user.ejb.RoleSB");
			roles = roleSB.findAll() ;
		} catch (NamingException ne) {
			throw new WebQuoteRuntimeException(ne.getMessage(),new RuntimeException(ne));
		}
		
	}

	

	@Override
	public Object getAsObject(FacesContext arg0, UIComponent arg1, String arg2) {
		
		if(roleSB == null){
			initialize();
		}
		
		
		
		for(Role role : roles){
			if ( role.getId() == Long.parseLong(arg2)){
				return role;
			}
		}
		
		return null;
	}

	@Override
	public String getAsString(FacesContext arg0, UIComponent arg1, Object arg2) {
		
		return arg2==null ? null: arg2.toString();
	}

}
