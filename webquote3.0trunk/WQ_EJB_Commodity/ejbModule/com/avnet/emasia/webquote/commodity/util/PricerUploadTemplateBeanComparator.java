package com.avnet.emasia.webquote.commodity.util;

import java.util.Comparator;

import com.avnet.emasia.webquote.commodity.bean.PricerUploadTemplateBean;

public 	class PricerUploadTemplateBeanComparator implements Comparator<PricerUploadTemplateBean>{
	 public  int compare(PricerUploadTemplateBean  u1, PricerUploadTemplateBean u2 ){

		 String value1 = u1.getMfr()+u1.getFullMfrPart();
		 String value2 = u2.getMfr()+u2.getFullMfrPart();
	 
	   return value1.compareTo(value2);
	 }	 
}