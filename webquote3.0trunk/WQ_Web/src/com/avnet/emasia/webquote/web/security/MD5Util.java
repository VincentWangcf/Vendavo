package com.avnet.emasia.webquote.web.security;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.logging.Level;
import java.util.logging.Logger;


public class MD5Util {
	static final Logger LOG = Logger.getLogger(MD5Util.class.getSimpleName());

	public static String md5Encode(String inStr) {
		MessageDigest md5 = null;
		StringBuffer hexValue = new StringBuffer();
		try {
			md5 = MessageDigest.getInstance("MD5");

			byte[] byteArray = inStr.getBytes("UTF-8");
			byte[] md5Bytes = md5.digest(byteArray);
			for (int i = 0; i < md5Bytes.length; i++) {
				int val = ((int) md5Bytes[i]) & 0xff;
				if (val < 16) {
					hexValue.append("0");
				}
				hexValue.append(Integer.toHexString(val));
			}
		} catch (Exception e) {
			LOG.log(Level.SEVERE, "Exception in getting md5Encodeding string for string :  "+inStr+" , Exception message : "+e.getMessage(), e);
			return "";
		}
		return hexValue.toString();
	}

	public static void main(String[] args) {
		// e10adc3949ba59abbe56e057f20f883e
		System.out.println(md5Encode("123456"));
	}
}
