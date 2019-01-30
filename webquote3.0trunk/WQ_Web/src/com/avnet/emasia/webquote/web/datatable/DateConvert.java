package com.avnet.emasia.webquote.web.datatable;

import java.text.DateFormat;
import com.avnet.emasia.webquote.utilities.SimpleDateFormat;
import java.util.Date;

public class DateConvert {
	
	public static final String EMPTY = "";
	
	public static String formatDate(Date date, String datePattern) {
		if (date != null) {
			DateFormat df = new SimpleDateFormat(datePattern);
			return df.format(date);
		}
		return EMPTY;
	}
}
