 package com.avnet.emasia.webquote.rowdata.converter;

import java.lang.reflect.Field;
import java.text.DateFormat;
import java.text.ParseException;
import com.avnet.emasia.webquote.utilities.SimpleDateFormat;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.lang.StringUtils;

import com.avnet.emasia.webquote.rowdata.ADateConverter;
import com.avnet.emasia.webquote.rowdata.Bean;


public class DateConverter extends DefaultConverter<ADateConverter>{
	
	private static Logger logger = Logger.getLogger(DateConverter.class.getName());
	
	@Override
	public void convert(String value, Bean bean, Field field) {
		if (StringUtils.isEmpty(value)) {
			return;
		}
		
		ADateConverter annotation = (ADateConverter) a;
		
		DateFormat formater = new SimpleDateFormat(annotation.value());

		try {
			field.set(bean, formater.parse(value));
		} catch (IllegalArgumentException | IllegalAccessException e) {
			logger.log(Level.SEVERE, "This should not happen. ", e);
			
		} catch (ParseException e) {
			logger.log(Level.INFO, "Failed to convert bean " + bean.getSeq() + ", Invalid date type on column " + field.getName() + ": " + value);
			if (annotation.errMsg().equalsIgnoreCase("Predefined")) {
				bean.putConvertError(field.getName(), "Invalid data [" + value + "] on column " + field.getName());
			} else {
				bean.putConvertError(field.getName(), annotation.errMsg());
			}
		}
	}
}
