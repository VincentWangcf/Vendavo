package com.avnet.emasia.webquote.masterData.util;

import java.util.ArrayList;
import java.util.List;

public class FunctionUtils {
	static int batchSize = 100;

	public static int getBatchSize() {
		return batchSize;
	}

	public static int getTotalBatchTimes(int totalDataNum) {
		return totalDataNum / batchSize + 1;
	}

	private static int constant = 1;

	public static int getSeq() {
		return constant++;
	}

	public static String[] arrayComposite(String[] arg1, String[] arg2) {
		int l1 = arg1.length;
		int l2 = arg2.length;
		String[] rtnArr = new String[l1 + l2];
		for (int i = 0; i < arg1.length; i++) {
			rtnArr[i] = arg1[i];
		}
		for (int j = 0; j < arg2.length; j++) {
			rtnArr[l1 + j] = arg2[j];
		}
		return rtnArr;
	}

	/**
	 * Get the sequence number from the file name
	 * 
	 * @param date
	 * @param fullPath
	 * @return
	 */
	public static String parseSequence(String date, String fullPath) {
		String retVal = null;
		int index = fullPath.indexOf(date);
		if (index != -1) {
			index += 8;
			retVal = fullPath.substring(index, index + 2);
		}
		return retVal;
	}

	// split every single row of txt file to string array
	public static List splitLineData(String orgLine) {
		String[] sRet = StringUtils.splitStringToArray(orgLine, "|");
		List iRet = new ArrayList();
		for (int i = 0; i < sRet.length; i++) {
			if (i != 0)
				iRet.add(sRet[i]);
			else
				iRet.add(sRet[i]);
		}
		return iRet;
	}
	public static List splitLineData(String orgLine, String delimiter) {
		String[] sRet = StringUtils.splitStringToArray(orgLine, delimiter);
		List iRet = new ArrayList();
		for (int i = 0; i < sRet.length; i++) {
			if (i != 0)
				iRet.add(sRet[i]);
			else
				iRet.add(sRet[i]);
		}
		return iRet;
	}
	public static List<String> splitLineData(String orgLine, String delimiter, boolean delim) {
		String[] sRet = StringUtils.splitStringToArray(orgLine, delimiter, delim);
		List<String> iRet = new ArrayList<String>();
		for (int i = 0; i < sRet.length; i++) {
			if (i != 0)
				iRet.add(sRet[i]);
			else
				iRet.add(sRet[i]);
		}
		return iRet;
	}
	
	public static List<String> splitLineDataWithoutTrim(String orgLine, String delimiter, boolean delim) {
		String[] sRet = StringUtils.splitStringToArrayWithOutTrim(orgLine, delimiter, delim);
		List<String> iRet = new ArrayList<String>();
		for (int i = 0; i < sRet.length; i++) {
			if (i != 0)
				iRet.add(sRet[i]);
			else
				iRet.add(sRet[i]);
		}
		return iRet;
	}
	public static Integer[] getAllCommitBatchNum(int total, int batchSize) {
		int times = total / batchSize;
		Integer[] nums = new Integer[times];
		for (int j = 0; j < times; j++) {
			nums[j] = batchSize * (j + 1);
		}
		return nums;
	}

//	public static void main(String[] args) {
//		FunctionUtils fu = new FunctionUtils();
//		Integer[] in = fu.getAllCommitBatchNum(2906, 100);
//		for (int i = 0; i < in.length; i++) {
//			System.out.println(in[i]);
//		}
//
//	}
}
