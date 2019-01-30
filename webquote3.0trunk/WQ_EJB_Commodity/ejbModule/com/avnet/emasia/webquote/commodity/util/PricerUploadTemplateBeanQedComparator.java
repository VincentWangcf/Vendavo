package com.avnet.emasia.webquote.commodity.util;

import java.util.Comparator;
import java.util.Date;

import com.avnet.emasia.webquote.commodity.bean.PricerUploadTemplateBean;
import com.avnet.emasia.webquote.utilities.DateUtils;
import com.avnet.emasia.webquote.utilities.util.QuoteUtil;

public 	class PricerUploadTemplateBeanQedComparator implements Comparator<PricerUploadTemplateBean>{
	 public  int compare(PricerUploadTemplateBean  u1, PricerUploadTemplateBean u2 ){
		 String value1 = u1.getPricerType() + u1.getMfr()+u1.getFullMfrPart()+u1.getCostIndicator();
		 String value2 = u2.getPricerType() +u2.getMfr()+u2.getFullMfrPart()+u2.getCostIndicator();
		 
		 Date qedToU1 = PricerUtils.getEffectiveTo( PricerUtils.getEffectiveFrom(u1.getQuotationEffectiveDate()), u1.getValidity());
		 Date qedToU2 = PricerUtils.getEffectiveTo( PricerUtils.getEffectiveFrom(u2.getQuotationEffectiveDate()), u2.getValidity());
		 
		 if(!QuoteUtil.isEmpty(u1.getQuotationEffectiveDate()) && !QuoteUtil.isEmpty(u2.getQuotationEffectiveDate())) {
			  value1 = value1+DateUtils.switchStringToDate(u1.getQuotationEffectiveDate(),"dd/mm/yyyy") + qedToU1;
			  value2 =  value2+DateUtils.switchStringToDate(u2.getQuotationEffectiveDate(),"dd/mm/yyyy") + qedToU2;
		 }
		 
		 
		 if(QuoteUtil.isEmpty(u1.getQuotationEffectiveDate()) && !QuoteUtil.isEmpty(u2.getQuotationEffectiveDate())) {
			  value1 = value1+DateUtils.addDay(u2.getQuotationEffectiveDate(), -1) + qedToU1;
			  value2 = value2+u2.getQuotationEffectiveDate()+ qedToU2;
		 }
		 
		 if(!QuoteUtil.isEmpty(u1.getQuotationEffectiveDate()) && QuoteUtil.isEmpty(u2.getQuotationEffectiveDate())) {
			 value1 = value1+u1.getQuotationEffectiveDate()+ qedToU1;
			 value2 =  value2+DateUtils.addDay(u1.getQuotationEffectiveDate(), -1)  + qedToU2;
		 }
		 if(QuoteUtil.isEmpty(u1.getQuotationEffectiveDate()) && QuoteUtil.isEmpty(u2.getQuotationEffectiveDate())) {
			 value1 = value1 + qedToU1;
			 value2 =  value2+ qedToU2;
		 }
	   return value1.compareTo(value2);
	 }	 
}