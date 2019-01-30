package com.avnet.emasia.webquote.utilities.common;

import java.io.Serializable;

import org.apache.commons.lang.StringUtils;

public class LoginConfigCache implements Serializable {
	private static final long serialVersionUID = 2437825793594699831L;
	private static boolean isProduction = true;
	private static String logoutUrl = "https://emasiaweb.avnet.com/webquote";

	public static boolean isProduction() {
		return isProduction;
	}

	public static void setLoginType(String type) {
		if(StringUtils.startsWith(System.getProperty("os.name"), "Windows")){
			isProduction = false;	
		}else{
			isProduction = "PROD".equals(type);
		}
	}

	public static void setLogoutUrl(String url) {
		logoutUrl = url;
	}

	public static String getLogoutUrl() {
		return logoutUrl;
	}
}
