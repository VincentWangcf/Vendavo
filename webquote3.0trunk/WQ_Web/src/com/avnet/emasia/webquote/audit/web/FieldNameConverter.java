package com.avnet.emasia.webquote.audit.web;

import java.util.Map;
import java.util.logging.Logger;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.ejb.EJB;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import com.avnet.emasia.webquote.audit.ejb.AuditSB;
/**
 * @author Tonmy,Li(906893)
 * @Created on 2012-12-25
 */
//@FacesConverter(value="fieldNameConverter")

@ManagedBean(name = "fieldNameConverter")
@RequestScoped
public class FieldNameConverter implements Converter{
	
	static Logger logger = Logger.getLogger(FieldNameConverter.class.getName());
    @EJB
    private AuditSB auditSB ;

	@Override
	public Object getAsObject(FacesContext arg0, UIComponent arg1, String arg2) {

		@SuppressWarnings("unchecked")
		Map<String, String> fieldMap = (Map<String, String>)auditSB.createAuditFieldMappingMap();
		String returnStr = (String)fieldMap.get(arg2.trim());
		if(returnStr==null)
			returnStr=arg2.trim();
		return returnStr;
	}

	@Override
	public String getAsString(FacesContext arg0, UIComponent arg1, Object arg2) {

		@SuppressWarnings("unchecked")
		Map<String, String> fieldMap = (Map<String, String>)auditSB.createAuditFieldMappingMap();
		String returnStr = (String)fieldMap.get(arg2.toString().trim());
		if(returnStr==null)
			returnStr=arg2.toString().trim();
		return returnStr;
	}	
	


}
