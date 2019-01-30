package com.avnet.emasia.webquote.rowdata;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.avnet.emasia.webquote.rowdata.converter.IntConverter;

@Retention(RetentionPolicy.RUNTIME)
@AConverter(convertBy=IntConverter.class)
@Target(ElementType.FIELD)
public @interface AIntConverter {
	String errMsg() default "Invalid numeric type ";
}
