package com.avnet.emasia.webquote.stm.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


@Retention(RetentionPolicy.RUNTIME)   
@Target(ElementType.FIELD) 
public @interface TransAlias {
	String xmlFieldName();
	String uiFieldName();
	String fieldType();   
	String fieldLength();
	boolean sadaMandatory(); 
	boolean sgaMandatory();   
}
