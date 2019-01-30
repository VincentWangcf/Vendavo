package com.avnet.emasia.webquote.masterData.util;


import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.lang.StringUtils;

/**
 * @author Tonmy,Li(906893)
 * @Created on 2013-01-22
 * 
 */
public final class EncodeUtil {


    private static Logger logger = Logger.getLogger("MaterialLoadingJob");
	private final static String GBK = "GBK";
	private final static String GB2312 = "GB2312";
	private final static String ISO = "ISO8859-1";
	private final static String UTF8 = "utf-8";

	/**
	 * 
	 * @param text
	 *            String
	 * @return boolean
	 */
	public static boolean isSimpleChinese(String text) {
		if (StringUtils.isEmpty(text))
			return false;

		byte[] bytes = text.getBytes();
		if (bytes.length < 2)
			return false;
		byte aa = (byte) 0xB0;
		byte bb = (byte) 0xF7;
		byte cc = (byte) 0xA1;
		byte dd = (byte) 0xFE;
		if (bytes[0] >= aa && bytes[0] <= bb) {
			if (bytes[1] < cc || bytes[1] > dd)
				return false;
		}
		return false;
	}

	/**
	 * 
	 * 
	 * @param text
	 *            String
	 * @return boolean
	 */
	public static boolean isTraditionalChinese(String text) {
		if (StringUtils.isEmpty(text))
			return false;

		byte[] bytes = text.getBytes();
		if (bytes.length < 2)
			return false;

		byte aa = (byte) 0xB0;
		byte bb = (byte) 0xF7;
		byte cc = (byte) 0xA1;
		byte dd = (byte) 0xFE;
		if (bytes[0] >= aa && bytes[0] <= bb) {
			if (bytes[1] < cc || bytes[1] > dd)
				return true;
		}
		return false;
	}

	/**
	 * 
	 * @param encoding
	 *            String
	 * @return String
	 */

	/**
	 * 
	 * @param strUtfContent
	 *            String
	 * @return String
	 */

	/**
	 * 
	 * @param strUtfContent
	 *            String
	 * @return String
	 */
	public static String utfTransfer2(String content) {
		if (StringUtils.isEmpty(content))
			return "";

		try {
			StringBuffer result = new StringBuffer();
			String temp = new String(content.getBytes(GB2312), ISO);
			for (int k = 0; k < temp.length(); k++) {
				result.append("%" + Integer.toHexString(temp.charAt(k)));
			}
			return result.toString();
		} catch (Exception e) {
			logger.log(Level.WARNING, "Error occured in utfTransfer2 for content: "+content+", Reason for failure: "+e.getMessage(), e); 
			return content;
		}
	}

	/**
	 * ISO to GBK
	 * 
	 * @param content
	 *            String
	 * @return String
	 */
	public static String iso2gbk(String content) {
		try {
			return new String(content.getBytes(ISO), GBK);
		} catch (Exception e) {
			logger.log(Level.WARNING, "Error occured for content: "+content+", Reason for failure: "+e.getMessage(), e); 
		}
		return content;
	}

	public static String utf2gbk(String encoding) {
		try {
			return new String(encoding.getBytes(UTF8), GBK);
		} catch (Exception e) {
			logger.log(Level.WARNING, "Error occured in iso2gbk in method for value: "+encoding+" ::: "+e.getMessage(), e); 
			return encoding;
		}
	}

	public static String gbk2utf(String encoding) {
		try {
			return new String(encoding.getBytes(GBK), UTF8);
		} catch (Exception e) {
			logger.log(Level.WARNING, "Error occured in gbk2utf  in method for value: "+encoding+" "+e.getMessage(), e); 
			return encoding;
		}
	}

	public static String togbk(String encoding) {
		return togbk("", encoding);
	}

	public static String toutf(String encoding) {
		return toutf("", encoding);
	}

	/**
	 * 
	 * @param content
	 *            String
	 * @param encoding
	 *            String
	 * @return String
	 */
	public static String toutf(String content, String encoding) {
		try {
			if (!StringUtils.isEmpty(content))
				return new String(encoding.getBytes(content), UTF8);
			else
				return new String(encoding.getBytes(), UTF8);
		} catch (Exception e) {
			logger.log(Level.WARNING, "Error occured in toutf in method for value: "+encoding+" "+e.getMessage(), e);
			return content;
		}
	}

	/**
	 * 
	 * @param content
	 *            String
	 * @param encoding
	 *            String
	 * @return String
	 */
	public static String togbk(String content, String encoding) {
		try {
			if (!StringUtils.isEmpty(content))
				return new String(encoding.getBytes(content), GBK);
			else
				return new String(encoding.getBytes(), GBK);
		} catch (Exception e) {
			logger.log(Level.WARNING, "Error occured in togbk in method for value: "+encoding+" and content :: "+content+" "+e.getMessage(), e);
			return content;
		}
	}

	public static String encode(String content, String charset) {
		try {
			if (!StringUtils.isEmpty(content)) {
				return new String(content.getBytes(content), charset);
			} else {
				return null;
			}
		} catch (Exception e) {
			logger.log(Level.WARNING, "Error occured in encode  in method for charset: "+charset+" and content :: "+content+" "+e.getMessage(), e);
			return content;
		}
	}

	/**
	 * 
	 * @param content
	 *            String
	 * @return String
	 */
	public static String latin2Gbk(String content) {
		try {
			return new String(content.getBytes("latin1"), GBK);
		} catch (Exception e) {
			logger.log(Level.WARNING, "Error occured in latin2Gbk  in method content :: "+content+" "+e.getMessage(), e);
			return content;
		}
	}

	public static String encoder(String content, String enc) {
		try {
			return URLEncoder.encode(content, enc);
		} catch (Exception e) {
			logger.log(Level.WARNING, "Error occured in encoder in method content :: "+content+" "+e.getMessage(), e);
			return content;
		}
	}
	public static String decodeURL(String url) {
		try {
			return URLDecoder.decode(url, GBK);
		} catch (Exception e) {
			logger.log(Level.WARNING, "Error occured in decodeURL  in method :: "+url+" "+e.getMessage(), e);
            return url ;
		}	
	}
	
}
