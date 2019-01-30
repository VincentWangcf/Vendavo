package com.avnet.emasia.webquote.rowdata;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.avnet.emasia.webquote.rowdata.converter.BigDecimalConverter;

@Retention(RetentionPolicy.RUNTIME)
@AConverter(convertBy=BigDecimalConverter.class)
@Target(ElementType.FIELD)
public @interface ABigDecimalConverter {
	String errMsg() default "Predefined";
}
