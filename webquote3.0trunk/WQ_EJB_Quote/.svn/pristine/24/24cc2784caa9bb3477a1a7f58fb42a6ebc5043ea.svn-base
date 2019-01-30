package com.avnet.emasia.webquote.rowdata.converter;

import java.lang.reflect.Field;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.lang.StringUtils;

import com.avnet.emasia.webquote.rowdata.AIntConverter;
import com.avnet.emasia.webquote.rowdata.Bean;

public class IntConverter extends DefaultConverter<AIntConverter> {
	
	private static Logger logger = Logger.getLogger(IntConverter.class.getName());
	
	

	@Override
	public void convert(String value, Bean bean, Field field) {
		if (StringUtils.isEmpty(value)) {
			return;
		}

		try {
			field.set(bean, Integer.parseInt(value));
		} catch (NumberFormatException e) {
			logger.log(Level.INFO, "Failed to convert bean " + bean.getSeq() + ", Invalid Number on column " + field.getName() + ": " + value);
			AIntConverter annotation = (AIntConverter) a;
			if (annotation.errMsg().equalsIgnoreCase("Predefined")) {
				bean.putConvertError(field.getName(), "Invalid data [" + value + "] on column " + field.getName());
			} else {
				bean.putConvertError(field.getName(), annotation.errMsg());
			}
		} catch (IllegalArgumentException | IllegalAccessException e) {
			logger.log(Level.SEVERE, "This should not happen. ", e);
		}
	}

}
