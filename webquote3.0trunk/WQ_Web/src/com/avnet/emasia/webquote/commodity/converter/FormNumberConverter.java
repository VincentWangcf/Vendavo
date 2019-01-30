package com.avnet.emasia.webquote.commodity.converter;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;

import com.avnet.emasia.webquote.entity.Quote;



/**
 * @author Tonmy Li
 * Created on 2013-4-10
 */

@FacesConverter("webquote.rfq.FormNumber")
public class FormNumberConverter implements Converter {

	


	@Override
	public Object getAsObject(FacesContext arg0, UIComponent arg1, String arg2) {

		return null;
		
	}

	@Override
	public String getAsString(FacesContext arg0, UIComponent arg1, Object arg2) {

		StringBuffer sb = new StringBuffer();
		Quote quote = (Quote)arg2;

		if(quote.getFormNumber()!=null && quote.getFormNumber().startsWith("DF"))
		{
			sb.append("");
		}
		else
		{
			sb.append(quote.getFormNumber());
		}
		return sb.toString();
	}

}
