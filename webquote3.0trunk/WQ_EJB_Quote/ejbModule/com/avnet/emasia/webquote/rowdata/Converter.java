package com.avnet.emasia.webquote.rowdata;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.logging.Level;
import java.util.logging.Logger;

public interface Converter<A extends Annotation> {
	
	void initialize(A a);
	void convert(String sourceValue, Bean bean, Field field);

	public static <A extends Annotation> Converter<A> create(Field field) {
		
		for(Annotation a : field.getDeclaredAnnotations()) {
			Class<? extends Annotation> type = a.annotationType();
			for (Annotation aa : type.getAnnotations()) {
				if (aa.annotationType().equals(AConverter.class)) {
					Class<? extends Converter> convertBy = ((AConverter)aa).convertBy();
					Converter converter = null;
					try {
						converter = convertBy.newInstance();
					} catch (InstantiationException | IllegalAccessException e) {
						Logger logger = Logger.getLogger(Converter.class.getName());
						logger.log(Level.SEVERE, "This should not happen when initialize class " + Converter.class.getName(), e);
					}
					converter.initialize(a);
					return converter;
				}
			}
		}
		
		return null;
		

	}

	
}
