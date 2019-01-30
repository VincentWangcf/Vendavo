package com.avnet.emasia.webquote.commodity.converter;



import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;

import com.avnet.emasia.webquote.entity.Announcement;
import com.avnet.emasia.webquote.utilities.DateUtils;

/**
 * @author Tonmy Li
 * Created on 2013-4-10
 */

@FacesConverter("webquote.commodity.AnnouncementMaint")
public class AnnouncementMaintConverter implements Converter {

	


	@Override
	public Object getAsObject(FacesContext arg0, UIComponent arg1, String arg2) {

		return null;
		
	}

	@Override
	public String getAsString(FacesContext arg0, UIComponent arg1, Object arg2) {

		StringBuffer sb = new StringBuffer();
		StringBuffer sb2 = new StringBuffer();
		Announcement anno = (Announcement)arg2;
		if(anno.getDescription()!=null)
		{
			if(anno.getDescription().length()>40)
			{
				sb.append(anno.getDescription().substring(0, 40)).append("...");
			}
			else
			{
				sb.append(anno.getDescription());
			}
			sb.append(" (").append(DateUtils.getAnnoucementDate(anno.getLastUpdatedOn())).append(")") ;
			
			if(anno.getUrl()!=null)
			{
				sb2.append("<a href='").append(anno.getUrl()).append("' target='_blank' >").append(sb.toString()).append("</a>");
				
				return sb2.toString();
			}
		}
		
		return sb.toString();
	}

}
