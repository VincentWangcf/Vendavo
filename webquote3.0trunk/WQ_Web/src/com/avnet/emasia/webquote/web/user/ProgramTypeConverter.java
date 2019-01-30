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

import com.avnet.emasia.webquote.entity.ProgramType;
import com.avnet.emasia.webquote.entity.Role;
import com.avnet.emasia.webquote.exception.WebQuoteRuntimeException;
import com.avnet.emasia.webquote.user.ejb.AppFunctionSB;
import com.avnet.emasia.webquote.user.ejb.ProgramTypeSB;

/**
 * @author Lin, Tough(901518)
 * Created on 2013-1-24
 */

@FacesConverter("webquote.user.ProgramType")
public class ProgramTypeConverter implements Converter {
	
	private ProgramTypeSB programTypeSB; 
	
	private List<ProgramType> programTypes;
	
	private void initialize(){
		
		try {
			Context context = new InitialContext();
			programTypeSB = (ProgramTypeSB) context.lookup("java:app/WQ_EJB_UserAndAuthor/ProgramTypeSB!com.avnet.emasia.webquote.user.ejb.ProgramTypeSB");
			programTypes = programTypeSB.findAll();
			
		} catch (NamingException ne) {
			throw new WebQuoteRuntimeException(ne.getMessage(), new RuntimeException(ne));
		}
		
	}

	@Override
	public Object getAsObject(FacesContext arg0, UIComponent arg1, String arg2) {
		
		if(programTypeSB == null){
			initialize();
		}
		
		for(ProgramType programType : programTypes){
			if (programType.getId() == Long.parseLong(arg2)){
				return programType;
			}
		}
		
		return null;
	
	}

	@Override
	public String getAsString(FacesContext arg0, UIComponent arg1, Object arg2) {
		
		return arg2.toString();
	}

}
