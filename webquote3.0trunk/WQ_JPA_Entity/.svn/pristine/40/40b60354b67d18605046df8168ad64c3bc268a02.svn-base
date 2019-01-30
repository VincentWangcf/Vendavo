package com.avnet.emasia.webquote.utilities;

import java.math.BigDecimal;
import java.util.logging.Logger;

public class NumberUtils {
	static final Logger logger = Logger.getLogger(NumberUtils.class.getSimpleName());
	// if ture,then must can be convert to Double or Bigdecimal.
	public static boolean isDouble(final String str) {
		if (isStringIsEmpty(str)) {
			return false;
		}
		try {
			Double.parseDouble(str);
		} catch (Exception e) {
			return false;
		}
		return true;
	}
	
	//if true ,then must can be convert to Inter Long.
	public static boolean isInteger(final String str) {
		if (isStringIsEmpty(str)) {
			return false;
		}
		try {
			Integer.parseInt(str);
		} catch (Exception e) {
			return false;
		}
		return true;
	}

	//if true ,then must can be convert to Inter Long.
	public static boolean isBigDecimal(final String str) {
		if (isStringIsEmpty(str)) {
			return false;
		}
		try {
			BigDecimal bigDecimal = new BigDecimal(str);
			logger.config(()-> bigDecimal + " can convert to BigDecimal ");
		} catch (Exception e) {
			return false;
		}
		return true;
	}
		
	//if true ,then must can be convert to Inter Long.
	public static Integer convertToInteger(final String str) {
		Integer convertTo = null;
		if (isStringIsEmpty(str)) {
			return convertTo;
		}
		try {
			convertTo = Integer.parseInt(str);
		} catch (Exception e) {
			logger.warning(str +" can not convert to Integer.");
		} 
		return convertTo;
	}
	
	public static Double convertToDouble(final String str) {
		Double convertTo = null;
		if (isStringIsEmpty(str)) {
			return convertTo;
		}
		try {
			convertTo = Double.parseDouble(str);
		} catch (Exception e) {
			logger.warning(str +" can not convert to Double.");
		} 
		return convertTo;
	}
	
	public static BigDecimal convertToBigDecimal(final String str) {
		BigDecimal convertTo = null;
		if (isStringIsEmpty(str)) {
			return convertTo;
		}
		try {
			convertTo = new BigDecimal(str);
		} catch (Exception e) {
			logger.warning(str +" can not convert to BigDecimal.");
		} 
		return convertTo;
	}
	
	/*private static boolean withDecimalsParsing(final String str, final int beginIdx) {
		int decimalPoints = 0;
		for (int i = beginIdx; i < str.length(); i++) {
			final boolean isDecimalPoint = str.charAt(i) == '.';
			if (isDecimalPoint) {
				decimalPoints++;
			}
			if (decimalPoints > 1) {
				return false;
			}
			if (!isDecimalPoint && !Character.isDigit(str.charAt(i))) {
				return false;
			}
		}
		return true;
	}
	*/
	private static boolean isStringIsEmpty(final String str) {
		return str == null || str.length() == 0 || str.trim().length() == 0;
	}
}
