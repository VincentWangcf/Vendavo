package com.avnet.emasia.webquote.web.quote.converter;

import java.util.List;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;

import com.avnet.emasia.webquote.entity.QuoteItem;

/**
 * @author Tonmy Li
 * Created on 2013-4-10
 */

@FacesConverter("webquote.quote.EndCustomerNumber")
public class EndCustomerNumberConverter implements Converter {

	

	@Override
	public Object getAsObject(FacesContext arg0, UIComponent arg1, String arg2) {

		return null;
		
	}
	//&lt;br /&gt;
	@Override
	public String getAsString(FacesContext arg0, UIComponent arg1, Object arg2) {

		StringBuffer sb = new StringBuffer();
		List<QuoteItem> list = (List<QuoteItem>)arg2;
		if(list!=null && list.size()>0)
		{
			sb.append("<div style='width:100%'>");
			for(QuoteItem o : list)
			{
			 	sb.append("<div style='float:left;width:100%'>").append(o.getEndCustomerFullName()==null?"":o.getEndCustomerFullName()).append("</div>");
			}
			sb.append("</div>");
		}

        return sb.toString();
	}

}
