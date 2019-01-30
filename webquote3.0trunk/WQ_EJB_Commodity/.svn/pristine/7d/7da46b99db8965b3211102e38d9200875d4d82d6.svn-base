package com.avnet.emasia.webquote.commodity.util;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import com.avnet.emasia.webquote.utilities.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;






public class StringUtils {
	static final Logger LOG=Logger.getLogger(StringUtils.class.getSimpleName());
	
    public static final String DELIMITER_REQUEST_PART_NO = "\u03A6";
    public static final String EMPTY = "";
    
	public static double getDoubleByString(String str)
	{
		
		double returnD = -999999d;
		if(str!=null && !str.equals(""))
			returnD = Double.parseDouble(str);
		return returnD;

	}
	
	public static int getIntByString(String str)
	{
		
		int returnI = -999999;
		if(str!=null && !str.equals(""))
		{   
			double tempD = Double.parseDouble(str);
				
			returnI = (int)tempD;
		}
		return returnI;

	}
	
	public static boolean isEmpty(String obj)
	{
	   boolean returnB = true;
	   if(obj!=null)
	   {
	   	 if(!obj.equalsIgnoreCase(""))
	   	 	returnB=false;
	   }
	   return returnB;
	}
	
	public static boolean isEmptyForDL(String obj)
	{
	   boolean returnB = true;
	   if(obj!=null)
	   {
	   	 if(!obj.equalsIgnoreCase("") && !obj.equalsIgnoreCase("null"))
                 returnB=false;
	
	   }
	   return returnB;
	}
	
	public static boolean isNotEmpty(String obj)
	{
	   boolean returnB = false;
	   if(obj!=null)
	   {
	   	 if(!obj.equalsIgnoreCase(""))
	   	 	returnB=true;
	   }
	   return returnB;
	}
	public static String getOneYearBefore()
	{
		Date oneYearBefore = new Date();
    	Calendar c = Calendar.getInstance();
    	c.setTime(oneYearBefore);
    	c.add(Calendar.YEAR, -1);
    	oneYearBefore = c.getTime();
    	SimpleDateFormat bartDateFormat = new SimpleDateFormat("yyyy/MM/dd"); 
    	String oneYearBeforeStr = bartDateFormat.format(oneYearBefore);
    	return oneYearBeforeStr;
	
	}
	
	public static String getFormatSeqNum(String templateStr,int seqNum)
	{
		String formatTemplate = "0000000";
		if(templateStr!=null)
		{
			int i = templateStr.indexOf("#");
			String temp =templateStr.substring(i,templateStr.length());
			if(temp!=null)
			formatTemplate =temp.replaceAll("#","0");
		}
		DecimalFormat df = new DecimalFormat(formatTemplate);
		String returnStr = df.format(seqNum);
    	return returnStr;
	
	}
	
	public static ArrayList getListByCommaString(String str)
	{
		ArrayList returnList = null;
		if(str!=null)
		{
			returnList =new ArrayList();
		   if(str.endsWith(","))
		   	str= str.substring(0,str.length()-1);
		   if(str.startsWith(","))
		   	str= str.substring(1,str.length());
		   String[] tempStrArray = str.split(",");
		   for(int i=0; i<tempStrArray.length; i++ )
		   	returnList.add(tempStrArray[i]);

		}
	    return returnList;
	}
	
	public static boolean isNumber(String str)
	{
	   final String numberStr = "1234567890.";  
	   if(str!=null)
	   {
	   	   str = str.replaceAll("%","");
	   	   if(str!=null && str.length()>0)
	   	   {
		       for(int i = 0; i < str.length(); i++)   
		       {   
		             if(numberStr.indexOf(str.charAt(i))== -1)   
		             {   
		                     return   false;   
		             }   
		       }  
	   	   }
	   }
	   
       return true;
	}
	public static String removeNullStrForDisplay(String str)
	{
	   if(str!=null)
	   {
	   	   if(str.equalsIgnoreCase("null"))
	   	   	str="";
	   }
	   
       return str;
	}
    
	public static String numberFormatter(double dos)
	{
		java.text.DecimalFormat   formatter   =   new   java.text.DecimalFormat("########.#####");  	
	    return formatter.format(dos);
	}
	
	public static String numberFormatter(String dos)
	{
		java.text.DecimalFormat   formatter   =   new   java.text.DecimalFormat("########.#####");  	
		Double d = new Double(dos);
	    return formatter.format(d);
	}
	
	public static String formatEmployeeName(String str)
	{
		String returnStr = null;
		if(str!=null)
		{
		   str = str.replaceAll(",","_");
		   str = str.replaceAll(" ","");
		   returnStr = str;
		}
		return returnStr;
	}
	

	 

	public static boolean isEmployeeCode(String str)
	{
	   final String numberStr = "1234567890";  
	   if(str!=null && str.length()>0)
	   {
		       for(int i = 0; i < str.length(); i++)   
		       {   
		             if(numberStr.indexOf(str.charAt(i))== -1)   
		             {   
		                     return   false;   
		             }   
		       }  
	   }
	   
       return true;
	}
	

	
	public static String isInListForValidationString(String str,ArrayList aList)
	{
            String returnStr = null;
	    	if(str!=null)
			{
		       for(int i=0; i<aList.size(); i++)
		       {
		       	    String tempStr =(String)aList.get(i);
		         	if(tempStr!=null && str.equalsIgnoreCase(tempStr))
		         	{
		         		returnStr = tempStr;	
		         		break;
		         	}
		         	
		       }
			}
	    	return returnStr;	
	
	}
	
	public static String isInListForValidationArray(String str,String[] aList)
	{
            String returnStr = null;
	    	if(str!=null)
			{
		       for(int i=0; i<aList.length; i++)
		       {
		       	    String tempString =(String)aList[i];
		         	if(tempString!=null && tempString.equalsIgnoreCase(str))
		         	{
		         		returnStr = tempString;	
		         		break;
		         	}
		         	
		       }
			}
	    	return returnStr;	
	
	}
	public static String isYesOrNoValidation(String str)
	{
            String returnStr = null;
	    	if(str!=null)
			{
	         	if(str.equalsIgnoreCase("Yes") || str.equalsIgnoreCase("Y"))
	         	{
	         		returnStr = "Yes";	
	         	}
	         	else if(str.equalsIgnoreCase("No") || str.equalsIgnoreCase("N"))
	         	{
	         		returnStr = "No";	
	         	}
	         		
			}
	    	return returnStr;	
	
	}
	
 

    public static String sqlcommaFilter(String str)
    {
       if(str!=null)
       {
       	    if(str.indexOf("'")!=-1)
       	    {
       	    	str=str.replaceAll("'","''");	
       	    }
       }
       
       return str;
    }
    public static String getUpperCaseStr(String str)
    {
       if(str!=null)
       {
       	    return str.toUpperCase();
       }
       
       return str;
    }
    

    
    public static String removeCommaFilter(String str)
    {
       if(str!=null)
       {
       	    if(str.indexOf(",")!=-1)
       	    {
       	    	str=str.replaceAll(","," ");	
       	    }
       	    if(str.indexOf(".")!=-1)
    	    {
    	    	str=str.replaceAll("\\.", " ") ;	
    	    }
       	    
	       	if(str.indexOf("/")!=-1)
	 	    {
	 	    	str=str.replaceAll("/", " ") ;	
	 	    }
	       	if(str.indexOf("\\")!=-1)
    	    {
    	    	str=str.replaceAll("\\\\"," ") ;	
    	    }
	       	if(str.indexOf("'")!=-1)
    	    {
    	    	str=str.replaceAll("'"," ") ;	
    	    }
	       	if(str.indexOf(":")!=-1)
    	    {
    	    	str=str.replaceAll(":"," ") ;	
    	    }
       }
       
       return str;
    }
    
    
    public static String removeSpaceOfStr(String str)
    {
       if(str!=null)
       {
       	    str = str.trim();
       }
       return str;
    }
//    public static String getContentFilter(String str)
//	{
//    	String returnS = "";
//		if(str!=null)
//		{
//			str.replaceAll(""","/"");
//		}
//		return returnS;
//	}
    
    /**
     *  @inputString the string user inputs
     * 	@delimiter the delimiter is propose to devise
     * 	@isSupportEmpty whether the devision should be filted when comes to a empty string
     */
    public static List getListByDelimiter(String inputString,String delimiter,boolean isSupportEmpty){
        //check whether the input is empty or not
        if(inputString==null || "".equals(inputString)){
            return null;
        }
        int index = inputString.indexOf(delimiter);
        if(index<=0){//single one
            List list = new ArrayList();
            list.add(inputString);
            return list;
        }else{//multiple
            String[] limitedArray = inputString.split(delimiter);
            List list = new ArrayList();
            
//          need to decide whether those empty string should be returned
            if(isSupportEmpty){
                int size = limitedArray.length;
                for( int i=0;i<size;i++ ){
                    String temp = limitedArray[i];
                    if(temp.length()>0)
                        list.add(temp);
                }
            }else{
                list.add(limitedArray);
            }
            return list;
        }
        
    }
    
    /**
     * isSupportEmpty is true when default
     * @param inputString
     * @param delimiter
     * @return
     */
    public static List getListByDelimiter(String inputString,String delimiter){
        return getListByDelimiter(inputString,delimiter,true);
    }
    
    /**
     * isSupportEmpty is true and delimiter is ; when default
     * @param inputString
     * @return
     */
    public static List getListByDelimiter(String inputString){
        return getListByDelimiter(inputString,DELIMITER_REQUEST_PART_NO,true);
    }
    
    
    public static String getCombinedKey(String str1 , String str2)
	{
       StringBuffer returnStr = new StringBuffer();
       if(str1!=null && !str1.equalsIgnoreCase(""))
       {
       	   returnStr.append(str1.trim());
       	   if(str2!=null && !str2.equalsIgnoreCase(""))
           {
       	      returnStr.append(str2.trim());
           }
       }
       return returnStr.toString();
	}
    public static String getCombinedKey4(String str1 , String str2 , String str3 , String str4)
	{
       StringBuffer returnStr = new StringBuffer();
       String tempStr=null;
       String nullStr="_";
       if(str1!=null && !str1.equalsIgnoreCase(""))
       	tempStr = str1.trim();
       else
       	tempStr = nullStr;
       returnStr.append(tempStr);
       
       	   if(str2!=null && !str2.equalsIgnoreCase(""))
       		tempStr = str2.trim();
           else
           	tempStr = nullStr;
       	   returnStr.append(tempStr);
       	      
	       	   if(str3!=null && !str3.equalsIgnoreCase(""))
	       	    tempStr = str3.trim();
	           else
	           	tempStr = nullStr;
	       	   returnStr.append(tempStr);
	       	       
		       	   if(str4!=null && !str4.equalsIgnoreCase(""))
		       	    tempStr = str4.trim();
		           else
		           	tempStr = nullStr;
		       	   returnStr.append(tempStr);

       return returnStr.toString();
	}
    
    public static String thousandFormat(int mpq)
    {
         String returnStr = "";
   	 try{
    		 returnStr =Constants.THOUSAND_FORMAT.format(mpq);
         }
         catch(Exception e)
         {
        	 LOG.log(Level.WARNING,"Exception occuring in method thousandFormat " + e.getMessage(), e);
         }
         return returnStr;
    }
    
	public static void printList(String str , List<String> list)
	{
		StringBuffer sb = new StringBuffer();
		for(String stra:  list)
		{
			sb.append(stra).append(" | ");
			
		}
		System.out.println(str +": "+ sb.toString());
	}
	
	public static String zeroFormat(int iValue, int prmLength) {
		String sValue = String.valueOf(iValue);
		int iLength = sValue.length();
		for (int i = iLength; i < prmLength; i++) {
			sValue = 0 + sValue;
		}
		return sValue;
	}
	
	public static String trim(String obj) {
		if (obj != null) {
			obj = obj.trim();
		}
		return obj;
	}
	
	public static String toUpperCase(String obj) {
		if (obj != null) {
			obj = obj.toUpperCase();
		}
		return obj;
	}
	
	/**
	 * Return localized number format as String
	 * 
	 * @param locale
	 * @param number
	 * @return String number
	 
	public static String localizedNumberFormat(int number, Locale locale) {

		return NumberFormat.getNumberInstance(locale).format(number);

	}*/
}
