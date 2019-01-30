package com.avnet.emasia.webquote.commodity.util;

import java.util.Comparator;

import com.avnet.emasia.webquote.commodity.bean.VerifyEffectiveDateResult;


public 	class VerifyEffectiveDateResultComparator implements Comparator<VerifyEffectiveDateResult>{
	 public  int compare(VerifyEffectiveDateResult  u1, VerifyEffectiveDateResult u2 ){

		 String value1 = u1.getMfr()+u1.getFullMfrPartNumber()+u1.getBizUnit()+u1.getCostIndicator();
		 String value2 = u2.getMfr()+u2.getFullMfrPartNumber()+ u2.getBizUnit()+u2.getCostIndicator();
	 
	   return value1.compareTo(value2);
	 }	 
}