package com.avnet.emasia.webquote.rowdata.converter;

import java.lang.reflect.Field;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.avnet.emasia.webquote.rowdata.AStringConverter;
import com.avnet.emasia.webquote.rowdata.Bean;

public class StringConverter extends DefaultConverter<AStringConverter>{
	
	private static Logger logger = Logger.getLogger(StringConverter.class.getName());
	
	@Override
	public void convert(String value, Bean bean, Field field) {
		try {
			field.set(bean, value);
		} catch (IllegalArgumentException | IllegalAccessException e) {
			logger.log(Level.SEVERE, "This should not happen. ", e);
		}
	}


}
