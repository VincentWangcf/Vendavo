package com.avnet.emasia.webquote.commodity.converter;


import java.util.logging.Logger;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;

import com.avnet.emasia.webquote.commodity.util.StringUtils;
import com.avnet.emasia.webquote.entity.ProgramPricer;
import com.avnet.emasia.webquote.entity.QtyBreakPricer;

/**
 * @author Tonmy Li
 * Created on 2013-5-22
 */

@FacesConverter("webquote.commodity.AvailableSellQty")
public class AvailableSellQtyConverter implements Converter {

	
    private final static String PLUS = "+";
    private static Logger logger = Logger.getLogger(AvailableSellQtyConverter.class.getName());

   
	@Override
	public Object getAsObject(FacesContext arg0, UIComponent arg1, String arg2) {

		return null;
		
	}
	@Override
	public String getAsString(FacesContext arg0, UIComponent arg1, Object arg2) {
		StringBuffer sb = new StringBuffer();
		QtyBreakPricer qbpricer = (QtyBreakPricer) arg2;
		Integer availableSellToQty = qbpricer.getAvailableToSellQty();
		if (availableSellToQty != null) {
			if (qbpricer instanceof ProgramPricer) {
				ProgramPricer pm = (ProgramPricer) qbpricer;
				if (pm.getAvailableToSellMoreFlag()) {
					sb.append(StringUtils.thousandFormat(availableSellToQty)).append(PLUS);
				} else {
					sb.append(StringUtils.thousandFormat(availableSellToQty));
				}
			} else {
				sb.append(StringUtils.thousandFormat(availableSellToQty));
			}

		} else {
			return "";
		}
        return sb.toString();
	}

}
