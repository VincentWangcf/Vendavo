package com.avnet.emasia.webquote.commodity.helper;

import java.util.HashMap;
import java.util.List;
import java.util.logging.Logger;

import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpSession;

import com.avnet.emasia.webquote.commodity.util.Constants;
import com.avnet.emasia.webquote.entity.ProgramPricer;

public class SessionHelper {

	private static Logger logger = Logger.getLogger(SessionHelper.class.getName());
	public SessionHelper() {
		// TODO Auto-generated constructor stub
	}
	
	public static void addSelectedRfqToSession(ProgramPricer[] selectedProgramMaterials)
	{
		logger.fine("call addSelectedRfqToSession");
		
		FacesContext facesContext = FacesContext.getCurrentInstance();
	    ExternalContext extContext =facesContext.getExternalContext();
	    HttpSession session =(HttpSession)extContext.getSession(true);
	    HashMap<Long, ProgramPricer> pmMap  = (HashMap<Long, ProgramPricer>)session.getAttribute(Constants.SESSION_STORE_TARGET_MAP);
	    if(null==pmMap)
	    {
	    	pmMap = new HashMap<Long, ProgramPricer>();
	    }
	    
	    if(null!=selectedProgramMaterials)
	    {
	        for(int i =0 ; i < selectedProgramMaterials.length ; i ++)
	        {
	        	ProgramPricer item = selectedProgramMaterials[i];
	        	ProgramPricer obj = (ProgramPricer)pmMap.get(new Long(item.getId()));
	        	if(obj==null)
	        	{
	        		pmMap.put(new Long(item.getId()), item);
	        	}
	        }
	    	session.setAttribute(Constants.SESSION_STORE_TARGET_MAP, pmMap);
	    
	    }
	}

	public static HashMap<Long, ProgramPricer> getSelectedRfqMap()
	{
		logger.fine("call getSelectedRfqMap");
		
		FacesContext facesContext = FacesContext.getCurrentInstance();
	    ExternalContext extContext =facesContext.getExternalContext();
	    HttpSession session =(HttpSession)extContext.getSession(true);
	    @SuppressWarnings("unchecked")
		HashMap<Long, ProgramPricer> pmMap = (HashMap<Long, ProgramPricer>)session.getAttribute(Constants.SESSION_STORE_TARGET_MAP);  
        return pmMap;

	}
	
	public static Integer getSelectedRfqCount()
	{
		HashMap<Long, ProgramPricer> pmMap = getSelectedRfqMap();
	    if(null==pmMap)
	    {
	    	return new Integer(0);
	    }
	    return new Integer(pmMap.size());
	}
	
	public static void removeSelectedRfqMap()
	{
		FacesContext facesContext = FacesContext.getCurrentInstance();
	    ExternalContext extContext =facesContext.getExternalContext();
	    HttpSession session =(HttpSession)extContext.getSession(true);
	    session.removeAttribute(Constants.SESSION_STORE_TARGET_MAP);
	}
	
}
