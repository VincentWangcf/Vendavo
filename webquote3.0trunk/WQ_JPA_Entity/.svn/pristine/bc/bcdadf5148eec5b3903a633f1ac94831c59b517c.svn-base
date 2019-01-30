package com.avnet.emasia.webquote.entity.util;

import java.text.NumberFormat;

/**
 * @author Lin, Tough(901518)
 * Created on 2013-4-21
 */

public class StringUtils {
	
	public static boolean isEmpty(String s){
		if(s == null || s.equalsIgnoreCase("")){
			return true;
		}else{
			return false;
		}
	}

	public static String formatNumber(double d){
		NumberFormat num = NumberFormat.getPercentInstance(); 
		num.setMaximumFractionDigits(2); 
		return num.format(d);
	}
}
