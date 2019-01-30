package com.avnet.emasia.webquote.utilities.common;

import java.io.Serializable;
import java.util.Calendar;
import java.util.TimeZone;

public class OSTimeZone implements Serializable {
	static {		
    }
	private static TimeZone osTimeZone = null;
	
	public static TimeZone getOsTimeZone() {
		if(null==osTimeZone) {
			osTimeZone = Calendar.getInstance().getTimeZone();
		}
		return osTimeZone;
	}
	public static void setOsTimeZone(TimeZone timeZone) {
		osTimeZone = timeZone;
	}
	


}
