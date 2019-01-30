package com.avnet.emasia.webquote.commodity.util;

import java.util.Comparator;

import com.avnet.emasia.webquote.entity.NormalPricer;

public 	class MaterialDetailComparator implements Comparator<NormalPricer>{
	 public  int compare(NormalPricer  u1, NormalPricer u2 ){

		 String value1 = u1.getMaterial().getManufacturer().getName()+u1.getMaterial().getFullMfrPartNumber()+
				 u1.getBizUnitBean().getName()+u1.getCostIndicator().getName();
		 String value2 = u2.getMaterial().getManufacturer().getName()+u2.getMaterial().getFullMfrPartNumber()+
				 u2.getBizUnitBean().getName()+u2.getCostIndicator().getName();
	 
	   return value1.compareTo(value2);
	 }	 
}
