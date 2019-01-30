package com.avnet.emasia.webquote.commodity.util;

import java.util.Comparator;

import com.avnet.emasia.webquote.commodity.bean.PricerUploadTemplateBean;

public 	class PricerTypeAndUkComparator implements Comparator<PricerUploadTemplateBean>{
		 public  int compare(PricerUploadTemplateBean  u1, PricerUploadTemplateBean u2 ){

			 String value1 = u1.getPricerType() + u1.getMfr()+u1.getFullMfrPart()+u1.getCostIndicator();
			 String value2 =  u2.getPricerType() +u2.getMfr()+u2.getFullMfrPart()+u2.getCostIndicator();
		 
		   return value1.compareTo(value2);
		 }	
}