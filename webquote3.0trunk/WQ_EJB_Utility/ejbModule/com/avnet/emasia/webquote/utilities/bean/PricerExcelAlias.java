package com.avnet.emasia.webquote.utilities.bean;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target; 

@Retention(RetentionPolicy.RUNTIME)   
@Target(ElementType.FIELD) 
public @interface PricerExcelAlias {
	String name();       //the header name of cell
	String cellNum();    //The number of the cell
	/**Include 4 mark, 
	 * 1:Normal Pricer mandatory 2:Program Pricer mandatory
	 * 3:Contract Pricer mandatory 4:Part Master mandatory 
	 * 5 Simple 6 SalesCost.
	 * for example:mandatory="true,true,true,true,true,true"
	 * */
	String mandatory(); 
	String fieldLength();// if fieldLength =-1  this field not need to verify
}
