package com.avnet.emasia.webquote.commodity.converter;


import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;

import com.avnet.emasia.webquote.utilites.web.util.QuoteUtil;
import com.avnet.emasia.webquote.vo.RfqItemVO;

/**
 * @author Tonmy Li
 * Created on 2013-5-22
 */

@FacesConverter("webquote.commodity.SuggestedResale")
public class SuggestedResaleConverter implements Converter {

	private final double DOUBLE_ZERO = 0d;
	private final String BLANK = "";
	@Override
	public Object getAsObject(FacesContext arg0, UIComponent arg1, String arg2) {

		return null;
		
	}
	@Override
	public String getAsString(FacesContext arg0, UIComponent arg1, Object arg2) {

		StringBuffer sb = new StringBuffer();
		RfqItemVO qi = (RfqItemVO)arg2;
		if(qi!=null && qi.getMinSellPrice()!=null && qi.getMinSellPrice().doubleValue()!= DOUBLE_ZERO)
		{
			sb.append(QuoteUtil.formatDouble2(qi.getMinSellPrice()));
		}
		else
		{
			sb.append(BLANK);
		}

        return sb.toString();
	}

}
