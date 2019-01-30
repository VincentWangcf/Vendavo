package com.avnet.emasia.webquote.utilities.bean;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target; 

@Retention(RetentionPolicy.RUNTIME)   
@Target(ElementType.FIELD) 
public @interface QuoteBuilderExcelAlias {
	String name();       //the header name of cell
	String cellNum();    //The number of the cell
	String mandatory(); 
	String fieldLength();// if fieldLength =-1  this field not need to verify
}