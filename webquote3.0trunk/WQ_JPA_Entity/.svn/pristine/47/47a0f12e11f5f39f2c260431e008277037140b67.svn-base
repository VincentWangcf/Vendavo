package com.avnet.emasia.webquote.entity;

import com.avnet.emasia.webquote.entity.util.StringUtils;

public enum ExchangeRateType {
	COMPANY_RATE,
	CUSTOMER_FIXED_RATE;

	public static boolean hasValue(String name) {
		if (!StringUtils.isEmpty(name)) {
			try {
				ExchangeRateType.valueOf(name);
				return true;
			} catch (Exception e) {
			}
		}
		return false;
	}

	
	public static String[] exchangeRateTypeList() {
		 String[] exchangeRateTypeList = new String[] { "Company Rate", "Customer Fixed Rate" };
 		
		return exchangeRateTypeList;
	}
	
}