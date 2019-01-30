package com.avnet.emasia.webquote.commodity.converter;



import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;

import com.avnet.emasia.webquote.entity.PromotionItem;
import com.avnet.emasia.webquote.utilities.DateUtils;

/**
 * @author Tonmy Li
 * Created on 2013-4-10
 */

@FacesConverter("webquote.commodity.PromotionItem")
public class PromotionItemConverter implements Converter {

	


	@Override
	public Object getAsObject(FacesContext arg0, UIComponent arg1, String arg2) {

		return null;
		
	}

	@Override
	public String getAsString(FacesContext arg0, UIComponent arg1, Object arg2) {

		StringBuffer sb = new StringBuffer();
		StringBuffer returnSb = new StringBuffer();
		PromotionItem pi = (PromotionItem)arg2;
		if(pi.getDescription()!=null)
		{
			
			sb.append(pi.getMaterial().getManufacturer().getName())
			.append(", ").append(pi.getMaterial().getFullMfrPartNumber())
			.append(", ").append(pi.getDescription());
			  
			if(sb.toString().length()>55)
			{
				returnSb.append(sb.toString().substring(0, 55)).append("...");
			}
			else
			{
				returnSb.append(sb.toString());
			}
			
			returnSb.append(" (").append(DateUtils.getAnnoucementDate(pi.getLastUpdatedOn())).append(")") ;
			
		}
		
		return returnSb.toString();
	}

}
