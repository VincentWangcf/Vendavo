package com.avnet.emasia.webquote.commodity.converter;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;

import com.avnet.emasia.webquote.entity.ProgramPricer;

/**
 * @author Tonmy Li
 * Created on 2013-4-10
 */

@FacesConverter("webquote.commodity.FullPartNumber")
public class FullPartNumberConverter implements Converter {

	


	@Override
	public Object getAsObject(FacesContext arg0, UIComponent arg1, String arg2) {

		return null;
		
	}

	@Override
	public String getAsString(FacesContext arg0, UIComponent arg1, Object arg2) {

		StringBuffer sb = new StringBuffer();
		ProgramPricer pm = (ProgramPricer)arg2;
		sb.append(pm.getMaterial().getFullMfrPartNumber());

		Integer specialItemFlag = pm.getSpecialItemFlag();
		Boolean newItemFlag = pm.getNewItemIndicator();
		if(newItemFlag!=null && newItemFlag)
		{
			sb.append("<image src='./../resources/images/new.png' width='31px'/>");
		}
		if(specialItemFlag!=null && specialItemFlag>0)
		{
			sb.append("<image src='./../resources/images/").append(specialItemFlag.intValue()).append(".gif' />");
		}
		return sb.toString();
	}

}
