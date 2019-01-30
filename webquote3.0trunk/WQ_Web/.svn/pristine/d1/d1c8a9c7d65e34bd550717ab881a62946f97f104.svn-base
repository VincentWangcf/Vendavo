package com.avnet.emasia.webquote.web.quote.converter;

import java.util.List;
import java.util.logging.Logger;

import javax.ejb.EJB;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.jboss.logmanager.Level;

import com.avnet.emasia.webquote.entity.User;
import com.avnet.emasia.webquote.user.ejb.UserSB;
import com.avnet.emasia.webquote.web.quote.vo.WorkingPlatformEmailVO;

/**
 * @author Tonmy Li
 * Created on 2013-4-10
 */

@FacesConverter("webquote.quote.UserAndRole")
public class UserAndRoleConverter implements Converter {
 static final Logger LOG=Logger.getLogger(UserAndRoleConverter.class.getSimpleName());
	@EJB
	transient UserSB userSB;

	public UserSB getUserSB(){
		try {
			if(userSB == null){
				Context context = new InitialContext();		
				userSB = (UserSB) context.lookup("java:app/WQ_EJB_UserAndAuthor/UserSB!com.avnet.emasia.webquote.user.ejb.UserSB");
				context = null;
			}
		} catch (NamingException e) {
			LOG.log(Level.SEVERE, "Exception in getting UserSB object, exception message : "+e.getMessage(), e);
		}			
		return userSB;
	}
	
	@Override
	public Object getAsObject(FacesContext arg0, UIComponent arg1, String arg2) {

		return null;
		
	}
	//&lt;br /&gt;
	@Override
	public String getAsString(FacesContext arg0, UIComponent arg1, Object arg2) {

		StringBuffer sb = new StringBuffer();
		List<String> ids = (List<String>)arg2;

		if(ids != null && ids.size() > 0){
			List<User> vos = getUserSB().findByEmployeeIds(ids);
			
			if(vos != null)
			{
				sb.append("<div style='height:40px;overflow:auto;'>");
				for(User user : vos)
				{
				 	sb.append("<div style='float:left;width:90%;font-size:11px;'>").append(user.getName() + "  ["+user.getRoleName()+"],").append("</div>");
				}
				sb.append("</div>");
			}
		}

        return sb.toString();
	}

}
