package com.avnet.emasia.webquote.commodity.converter;


import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;

import com.avnet.emasia.webquote.entity.QuoteItem;
import com.avnet.emasia.webquote.quote.ejb.constant.QuoteSBConstant;
import com.avnet.emasia.webquote.utilites.resources.ResourceMB;
import com.avnet.emasia.webquote.utilites.web.util.QuoteUtil;

/**
 * @author Tonmy Li
 * Created on 2013-5-22
 */

@FacesConverter("webquote.commodity.AvnetPrice")
public class AvnetPriceConverter implements Converter {

	@Override
	public Object getAsObject(FacesContext arg0, UIComponent arg1, String arg2) {

		return null;
		
	}
	@Override
	public String getAsString(FacesContext arg0, UIComponent arg1, Object arg2) {

		StringBuffer sb = new StringBuffer();
		QuoteItem qi = (QuoteItem)arg2;
		if(QuoteSBConstant.RFQ_STATUS_AQ.equalsIgnoreCase(qi.getStatus()))
		{
			if(qi.getQuotedPrice() !=null)
			{
				sb.append(qi.convertToRfqValueWithDouble(qi.getQuotedPrice())==null?null:QuoteUtil.formatDouble(qi.convertToRfqValueWithDouble(qi.getQuotedPrice()).doubleValue()));
			}
		}
		else
		{
			sb.append(ResourceMB.getText("wq.message.progress"));
		}
        return sb.toString();
	}

}
