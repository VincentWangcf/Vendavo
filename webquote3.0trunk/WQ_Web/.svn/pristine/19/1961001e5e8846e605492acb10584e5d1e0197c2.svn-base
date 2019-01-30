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

import com.avnet.emasia.webquote.entity.User;
import com.avnet.emasia.webquote.exception.WebQuoteRuntimeException;
import com.avnet.emasia.webquote.user.ejb.RoleSB;
import com.avnet.emasia.webquote.user.ejb.UserSB;

/**
 * @author Lin, Tough(901518)
 * Created on 2013-1-24
 */

@FacesConverter("webquote.user.User")
public class UserConverter implements Converter {
	
	private UserSB userSB;
	
	private List<User> users;
	
	private void initialize(){
		
		try {
			Context context = new InitialContext();
			userSB = (UserSB) context.lookup("java:app/WQ_EJB_UserAndAuthor/UserSB!com.avnet.emasia.webquote.user.ejb.UserSB");
			users = userSB.findAll() ;
		} catch (NamingException ne) {
			throw new WebQuoteRuntimeException(ne.getMessage(),new RuntimeException(ne));
		}
		
	}


	@Override
	public Object getAsObject(FacesContext arg0, UIComponent arg1, String arg2) {
		if(userSB == null){
			initialize();
		}
		
		for(User user : users){
			if(user.getId() == Long.parseLong(arg2)){
				return user;
			}
		}
		
		return null;
		
	}

	@Override
	public String getAsString(FacesContext arg0, UIComponent arg1, Object arg2) {
		
		return arg2.toString();
	}

}
