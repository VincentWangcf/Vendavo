package com.avnet.emasia.webquote.rowdata;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.avnet.emasia.webquote.rowdata.converter.DoubleConverter;

@Retention(RetentionPolicy.RUNTIME)
@AConverter(convertBy=DoubleConverter.class)
@Target(ElementType.FIELD)
public @interface ADoubleConverter {
	String errMsg() default "Invalid numeric type ";
}