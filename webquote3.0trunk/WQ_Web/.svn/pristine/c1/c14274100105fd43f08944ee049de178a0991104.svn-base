package com.avnet.emasia.webquote.web.quote.converter;

import java.util.List;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;

import com.avnet.emasia.webquote.web.quote.vo.WorkingPlatformEmailVO;

/**
 * @author Tonmy Li
 * Created on 2013-4-10
 */

@FacesConverter("webquote.quote.MfrPartNumber")
public class MfrAndPartNumberConverter implements Converter {

	

	@Override
	public Object getAsObject(FacesContext arg0, UIComponent arg1, String arg2) {

		return null;
		
	}
	//&lt;br /&gt;
	@Override
	public String getAsString(FacesContext arg0, UIComponent arg1, Object arg2) {

		StringBuffer sb = new StringBuffer();
		WorkingPlatformEmailVO vo = (WorkingPlatformEmailVO)arg2;
		List<String> mfrs = vo.getMfrs();
		List<String> partNumbers = vo.getPartNumbers();
		if(partNumbers!=null && partNumbers.size()>0)
		{
			sb.append("<div style='width:100%'>");
			for(int i=0; i<partNumbers.size(); i++)
			{
			 	sb.append("<div style='float:left;width:100%'>").append(mfrs.get(i)+" "+partNumbers.get(i)).append("</div>");
			}
			sb.append("</div>");
		}

        return sb.toString();
	}

}
