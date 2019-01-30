package com.avnet.emasia.webquote.web.user;

import java.io.IOException;
import java.io.Serializable;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;

import com.avnet.emasia.webquote.entity.User;

@ManagedBean
@RequestScoped
public class LogoutMB implements Serializable {


	private static final long serialVersionUID = -296430706770237985L;
	
	private static final Logger LOG = Logger.getLogger(LogoutMB.class.getName());
	
	public void logout(){
		
		FacesContext context = FacesContext.getCurrentInstance();
		
		try{
			User user = UserInfo.getUser();
			LOG.info("User:" + user.getEmployeeId() + " Log out");
		}catch(Exception e){
			LOG.log(Level.SEVERE, "Exception in logout for user  , exception message : "+e.getMessage(), e);
			
		}
	
		
		try {
			context.getExternalContext().redirect(context.getExternalContext().getRequestContextPath() + "/j_spring_security_logout");
		} catch (IOException e) {
			LOG.log(Level.SEVERE, "Logout error: Can't send erequest to j_spring_security_logout while logging out : "+e.getMessage(),e);
		}
		
	}


}
