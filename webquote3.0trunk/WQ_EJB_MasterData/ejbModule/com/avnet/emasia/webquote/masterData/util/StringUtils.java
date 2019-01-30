package com.avnet.emasia.webquote.masterData.util;



import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.avnet.emasia.webquote.utilities.DateUtils;


/**
 * @author Tonmy,Li(906893)
 * @Created on 2013-01-22
 * 
 */
abstract public class StringUtils {

	private static Logger logger = Logger.getLogger("StringUtils");
	private final static Pattern PATTERN = Pattern.compile("[0-9]*");

	public static List<String> splitStringToList(String source, String delimiter) {
		StringTokenizer st = new StringTokenizer(source, delimiter, false);
		List<String> list = new ArrayList<String>();
		while (st.hasMoreElements()) {
			list.add(st.nextToken().trim());
		}
		return list;
	}
	
	public static String[] splitStringToArray(String source, String delimiter) {
		StringTokenizer st = new StringTokenizer(source, delimiter, false);
		List list = new ArrayList();
		while (st.hasMoreElements()) {
			list.add(st.nextToken().trim());
		}
		return (String[]) list.toArray(new String[list.size()]);
	}
	public static String[] splitStringToArray(String source, String delimiter, boolean delim) {
		StringTokenizer st = new StringTokenizer(source, delimiter, delim);
		List list = new ArrayList();
		while (st.hasMoreElements()) {
			list.add(st.nextToken().trim());
		}
		
		if(((String)list.get(list.size()-1)).equals(delimiter))
			list.add("");
		logger.log(Level.FINE, "Method call splitStringToArray... ");
		List myStringList = new ArrayList();
		
		for(int i=0; i < list.size(); i++)
		{
			if(((String)list.get(i)).equals(delimiter) && ((String)list.get(i+1)).equals(delimiter) && i < list.size()-1)
			{
				myStringList.add("");
			}
			else if(!((String)list.get(i)).equals(delimiter))
			{
				myStringList.add(list.get(i));
			}
		}
		return (String[]) myStringList.toArray(new String[myStringList.size()]);
	}
	
	public static String[] splitStringToArrayWithOutTrim(String source, String delimiter, boolean delim) {
		StringTokenizer st = new StringTokenizer(source, delimiter, delim);
		List list = new ArrayList();
		int j = 0;
		
		// don't trim the sap part number
		while (st.hasMoreElements()) {
			if(j==0)
			{
				list.add(st.nextToken());
			}
			else
			{
				list.add(st.nextToken().trim());
			}
			j++;
		}
		
		if(((String)list.get(list.size()-1)).equals(delimiter))
			list.add("");
		
		List myStringList = new ArrayList();
		
		logger.log(Level.FINE, "Method call splitStringToArrayWithOutTrim : delimiter : " + delimiter);
		
		for(int i=0; i < list.size(); i++)
		{
			if(((String)list.get(i)).equals(delimiter) && ((String)list.get(i+1)).equals(delimiter) && i < list.size()-1)
			{
				myStringList.add("");
			}
			else if(!((String)list.get(i)).equals(delimiter))
			{
				myStringList.add(list.get(i));
			}
		}
		return (String[]) myStringList.toArray(new String[myStringList.size()]);
	}

	// replace all replstr1 string in allstr to replstr2
	public static String replaceAll(String allstr, String replstr1,
			String replstr2) {
		StringBuffer newStringBuff = new StringBuffer();
		String str1Upper = replstr1.toUpperCase();
		int str1Length = replstr1.length();
		int currentPos = 0;
		int pos = 0;
		while ((pos = allstr.toUpperCase().indexOf(str1Upper, currentPos)) != -1) {
			newStringBuff.append(allstr.substring(currentPos, pos));
			newStringBuff.append(replstr2);
			currentPos = pos + str1Length;
		}
		newStringBuff.append(allstr.substring(currentPos));
		return newStringBuff.toString();
	}

	// format to length by 0
	public static String zeroFormat(int iValue, int prmLength) {
		String sValue = String.valueOf(iValue);
		int iLength = sValue.length();
		for (int i = iLength; i < prmLength; i++) {
			sValue = 0 + sValue;
		}
		return sValue;
	}

	public static String nvl(String org) {
		return null == org ? "" : org.trim();
	}

	public static String convertToStrNull(String org) {
		return null == org || "".equals(org) ? "NULL" : "'" + org.trim() + "'";
	}

	public static String convertToStrBlank(String org) {
		return null == org || "".equals(org) ? "''" : org.trim();
	}

	public static String convertDate(java.util.Date org) {
		return null == org ? null : DateUtils.formateDate2String(org,
				"yyyyMMdd");
	}

	public static String[] convertStringList2StringArr(List orgList) {
		String[] tmp = new String[orgList.size()];
		for (int i = 0; i < orgList.size(); i++) {
			tmp[i] = (String) orgList.get(i);
		}
		return tmp;
	}
	public static String trim(String str) {
		if (str == null || "".equals(str)) {
			return null;
		}
		return str.trim();
	}
	
	/*
	 * @param str: input string
	 * @result: if the input paramter is numeric string. reture true, otherwise return false; 
	 */
	public static boolean isNumeric(String str)
	{
		if(str != null && str.length() > 0)
		{
			Matcher isNum = PATTERN.matcher(str);
			if(isNum.matches())
			{
				return true;
			}
		}
		return false;
	}
	
	public static void main(String[] args){
		String g = "000008098hd08 |dfdfdf  |fdfdfdfd ";
		String[] str = splitStringToArrayWithOutTrim(g,"|",true);
		for(String s :str)
		{
		System.out.println("|"+s+"|");
		}
	}
}
