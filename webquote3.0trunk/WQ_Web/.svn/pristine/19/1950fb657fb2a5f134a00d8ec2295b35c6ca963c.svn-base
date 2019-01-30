package com.avnet.emasia.webquote.commodity.converter;


import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;

import org.apache.commons.lang.StringUtils;

import com.avnet.emasia.webquote.utilities.DateUtils;

/**
 * @author Tonmy Li
 * Created on 2013-4-10
 */

@FacesConverter("webquote.commodity.ExpiredDateConverter")
public class ExpiredDateConverter implements Converter {

	


	@Override
	public Object getAsObject(FacesContext arg0, UIComponent arg1, String arg2) {

		return null;
		
	}

	@Override
	public String getAsString(FacesContext arg0, UIComponent arg1, Object arg2) {

		String validity = (String)arg2;
		String qed = "";
		if(validity!=null)
		{
			if(DateUtils.isDate(validity))
			{
				qed=DateUtils.addDay(validity,-1);
			}
			else
			{
				if(StringUtils.isNumeric(validity))
				{
					qed=DateUtils.addDay(DateUtils.getCurrentDateStrForValidity(),(Integer.valueOf(validity).intValue()-1));
				}
			}
		}
		return qed;
	}

}
