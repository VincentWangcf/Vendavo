package com.avnet.emasia.webquote.masterData.util;


import java.io.UnsupportedEncodingException;

/**
 * @author Tonmy,Li(906893)
 * @Created on 2013-01-22
 * 
 */

import java.net.URLEncoder;
import java.util.logging.Level;
import java.util.logging.Logger;


abstract public class CharacterUtils {
	 
	private static Logger logger = Logger.getLogger(CharacterUtils.class.getName());


	public static final String GBK = "GBK";

	public static final String ISO_8859_1 = "ISO-8859-1";

	public static String encString(String prmStr, String encode) {
		try {
			return URLEncoder.encode(prmStr, encode);
		} catch (Exception e) {
			logger.log(Level.WARNING, "Error occured in encString for prm string: "+prmStr+", encode String"+encode+", "
					+ "Reason for failure"+e.getMessage(), e); 
			return "";
		}
	}

	// check if chinese
	public static boolean isChinese(char character, String decode) {
		if (null == decode)
			decode = GBK;
		Character.UnicodeBlock block = Character.UnicodeBlock.of(character);
		if (block.equals(Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS)
				&& decode.equals(GBK)) {
			return true;
		} else if (block.equals(Character.UnicodeBlock.LATIN_1_SUPPLEMENT)
				&& decode.equals(ISO_8859_1)) {
			return true;
		} else {
			return false;
		}
	}

	public static String toUnicodeHexString(char character) {
		return Integer.toHexString((int) character);
	}

	public static char toChineseCharacter(String hexString) {
		char chinese = 0;
		if (hexString.length() == 6 && hexString.startsWith("\\u")) {
			chinese = (char) (Integer.decode("0x" + hexString.substring(2))
					.intValue());
		} else if (hexString.length() == 6 && hexString.startsWith("0x")) {
			chinese = (char) (Integer.decode(hexString).intValue());
		} else if (hexString.length() == 4) {
			chinese = (char) (Integer.decode("0x" + hexString).intValue());
		}
		return chinese;
	}

	public static String handleChineseToUnicdoe(String content, String decode) {
		if (content == null)
			return "";
		char[] dataArray = content.toCharArray();
		StringBuffer result = new StringBuffer();
		int begin = 0;
		int end = 0;
		int i = 0;
		for (; i < dataArray.length; i++) {
			if (isChinese(dataArray[i], decode)) {
				end = i;
				if (begin < end) {
					result.append(new String(dataArray, begin, end - begin));
					begin = end;
				}
				// replace chinese to unicode
				result.append("\\u").append(toUnicodeHexString(dataArray[i]));
				begin++;
			}
		}
		if (begin > 0 && begin < i) {
			result.append(new String(dataArray, begin, i - begin));
		} else if (begin == end && begin == 0) {
			result.append(dataArray);
		}
		return result.toString();
	}

	// encode pattern : "ISO-8859-1" "gb2312"
	public static String conv2GB(String prmStr, String oldEncode,
			String newEncode) {
		try {
			byte[] b = prmStr.getBytes(oldEncode);
			return new String(b, newEncode);
		} catch (Exception e) {
			logger.log(Level.WARNING, "Error occured in encString for prm string: "+prmStr+", old encode string"+oldEncode+", new encode string: "+newEncode
					+ "Reason for failure"+e.getMessage(), e);
			return "";
		}
	}

	// Returns hex String representation of byte b
	public static String byteToHex(byte b) {
		char hexDigit[] = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
				'a', 'b', 'c', 'd', 'e', 'f' };
		char[] array = { hexDigit[(b >> 4) & 0x0f], hexDigit[b & 0x0f] };
		return new String(array);
	}

	// Returns hex String representation of char c
	public static String charToHex(char c) {
		byte hi = (byte) (c >>> 8);
		byte lo = (byte) (c & 0xff);
		return byteToHex(hi) + byteToHex(lo);
	}

	public static String native2UTF8(String nativeStr) {
		if (nativeStr == null)
			return nativeStr;
		StringBuffer converted = new StringBuffer();
		try {
			String utfQuery = new String(nativeStr.getBytes("UTF-8"), "UTF-8");
			for (int i = 0; i < nativeStr.length(); i++) {
				char c = nativeStr.charAt(i);
				if (c > 128)
					converted.append("\\u" + charToHex(utfQuery.charAt(i)));
				else
					converted.append(c);
			}
		} catch (Exception e) {
			// ignore
			logger.log(Level.WARNING,"Error Occured in native2UTF8 method :: for input String :: "+nativeStr+"" + e.getMessage(), e);
		}
		return converted.toString();
	}

	public static String utf82Native(String utf8String) {
		if (utf8String == null)
			return null;
		int i = 0;
		boolean backSlash = false, utfStart = false;
		StringBuffer outPut = new StringBuffer();
		while (i < utf8String.length()) {
			char c = utf8String.charAt(i);
			if (c == 'u' && backSlash) {
				utfStart = true;
			} else
				utfStart = false;

			if (c == '\\') {
				backSlash = true;
			} else
				backSlash = false;

			if (utfStart) {
				StringBuffer buf = new StringBuffer();
				int j = i + 1;
				for (; (j < i + 1 + 4) && j < utf8String.length(); j++) {
					char d = utf8String.charAt(j);
					if ((d >= '0' && d <= '9') || (d >= 'A' && d <= 'F')
							|| (d >= 'a' && d <= 'f'))
						buf.append(d);
					else
						break;
				}
				String utfStr = buf.toString();
				i = j;
				utfStart = false;
				char ch = (char) Integer.parseInt(utfStr, 16);
				outPut.setCharAt(outPut.length() - 1, ch);
			} else {
				i++;
				outPut.append(c);
			}
		}
		return outPut.toString();
	}

	public static void main(String[] args) {
	}
}
