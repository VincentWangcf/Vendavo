package com.avnet.emasia.webquote.commodity.converter;


import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;

import com.avnet.emasia.webquote.constants.Constants;

/**
 * @author Tonmy Li
 * Created on 2013-5-22
 */

@FacesConverter("webquote.commodity.PriceWithRoundMode")
public class PriceWithRoundModeConverter implements Converter {
	
	public DecimalFormat df5 = new DecimalFormat("#");
	{
		df5.setMaximumFractionDigits(5);
		df5.setMinimumFractionDigits(2);
		df5.setMinimumIntegerDigits(1);
		//let the round model as the same of currency convert. avoid mini diff.
		df5.setRoundingMode(RoundingMode.valueOf(Constants.ROUNDING_MODE));
	}
	@Override
	public Object getAsObject(FacesContext arg0, UIComponent arg1, String arg2) {
		if (arg2 == null) {
			return null;
		}
		return (arg2.length()==0 ||arg2.trim().length()==0) ? null : arg2.trim();
		
	}
	
	@Override
	public String getAsString(FacesContext arg0, UIComponent arg1, Object arg2) {
		
		if (arg2 == null) {
			return null;
		}
		return df5.format(arg2);
		 
	}

}
