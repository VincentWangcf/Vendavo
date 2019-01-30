package com.avnet.emasia.webquote.utilities.util;

import java.text.DateFormat;
import java.text.ParseException;
import com.avnet.emasia.webquote.utilities.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class DateUtil {
	static final Logger LOG= Logger.getLogger(DateUtil.class.getSimpleName());
	public static final String DATE_FORMAT_YYYYMMDD = "yyyy/MM/dd";
	public static void main(String[] args){
		
		for(int i=0;i<10;i++){
			if(i==3){
				continue;
			}
			System.out.println(i);
		}
		System.out.println("01/02/2013".length());
	/*	long starttime = System.currentTimeMillis();
		for(int i=0;i<10000;i++){
			//ValidateFormatddmmyyyy("01/02/2013");
			ValidateFormat(null,"dd/mm/yyyy");
		}
		long endtime = System.currentTimeMillis();
		System.out.println(endtime -starttime);*/
		/*System.out.println(ValidateFormatddmmyyyy("01/02/2013"));
		System.out.println(ValidateFormatddmmyyyy("13/13/2013"));
		System.out.println(ValidateFormatddmmyyyy("01/02/203"));
		System.out.println(ValidateFormatddmmyyyy("2014/2/20"));*/
	}

    /** 
     *method covert String to Date
     *  
     * @param dateString ,format        
     *@return  Date 
     */  
    public final static Date string2Date(String dateString,String format) {  
        DateFormat dateFormat;  
        dateFormat = new SimpleDateFormat(format);  
        dateFormat.setLenient(false);  
        Date date = null;  
        try {  
            date = dateFormat.parse(dateString);  
        } catch (ParseException e) {  
        	LOG.log(Level.WARNING, "Exception in parsing date : "+dateString+" , Exception Message : "+e.getMessage(),"");
        }  
        return date;  
    }  
    public final static Date stringToDate(String dateString,String format ,String regular) throws ParseException {  
    	try{
    	if(!dateString.matches(regular)){
    		throw new ParseException(format,0);
    	}
    	DateFormat dateFormat= new SimpleDateFormat(format);  
    	//dateFormat.setLenient(false); 
    	dateFormat.setTimeZone(TimeZone.getTimeZone("GMT+8"));
    	Date date = dateFormat.parse(dateString);  
    	return date; 
    	}catch(Exception e){
    		throw new ParseException(format,0);
    	}
    }  
    
    /** 
     *method covert Date to String
     *  
     * @param dateString ,format        
     *@return dateString 
     */  
    public final static String dateToString(Date date,String format) { 
    	if(date == null) return null;
        DateFormat dateFormat;  
        dateFormat = new SimpleDateFormat(format);  
        dateFormat.setLenient(false);  
        String dateStr = null;
    	try {
    		dateStr = dateFormat.format(date);  
    	} catch (Exception e) {
    		LOG.log(Level.WARNING, "Exception in parsing date format: "+format+" , Exception Message : "+e.getMessage(),"");
		} 
    	return dateStr;
    }  
    
    /**
     * validate Date format(DD/MM/YYYY format of regular expressions)
     * @param dateString
     * @return boolean
     */
    public final static boolean validateFormatddmmyyyy(String dateString){
    	Pattern p = Pattern.compile("(((0[1-9]|[12][0-9]|3[01])/((0[13578]|1[02]))|((0[1-9]|[12][0-9]|30)/(0[469]|11))|"
    			+ "(0[1-9]|[1][0-9]|2[0-8])/(02))/([0-9]{3}[1-9]|[0-9]{2}[1-9][0-9]{1}|[0-9]{1}[1-9][0-9]{2}|[1-9][0-9]{3}))|"
    			+ "(29/02/(([0-9]{2})(0[48]|[2468][048]|[13579][26])|((0[48]|[2468][048]|[3579][26])00)))");
    	Matcher m = p.matcher(dateString);
    	return m.matches();
    }
    
    public final static boolean validateFormat(String dateString,String format){
    	DateFormat dateFormat = new SimpleDateFormat(format);
    	Boolean bool = true;
		try {
			dateFormat.setLenient(false);
			dateFormat.parse(dateString);
		} catch (Exception e) {
			LOG.log(Level.WARNING, "Exception in parsing date format: "+format+" , Exception Message : "+e.getMessage(),"");
			bool = false;
		}
		return bool;
    }
	 
	 /**
		 * calculate the months different between two date 
		 * 
		 * @param date1 --from date
		 * @param date2 ---to date
		 * @return
		 */
		 public static double getMonthDiff(Date date1, Date date2){      
	       double iMonth = 0;   
	       try{      
	           Calendar objCalendarDate1 = Calendar.getInstance();      
	           objCalendarDate1.setTime(date1);      
	           Calendar objCalendarDate2 = Calendar.getInstance();      
	           objCalendarDate2.setTime(date2);      
	           if (objCalendarDate2.equals(objCalendarDate1))      
	               return 0;      
	           if (objCalendarDate1.after(objCalendarDate2)){      
	               Calendar temp = objCalendarDate1;      
	               objCalendarDate1 = objCalendarDate2;      
	               objCalendarDate2 = temp;      
	           }      
	           if (objCalendarDate2.get(Calendar.YEAR) > objCalendarDate1.get(Calendar.YEAR)) {     
	               iMonth = (objCalendarDate2.get(Calendar.YEAR) - objCalendarDate1.get(Calendar.YEAR))      
	                       * 12 + objCalendarDate2.get(Calendar.MONTH)    
	                       - objCalendarDate1.get(Calendar.MONTH);
	           	   double dayDiff = (objCalendarDate2.get(Calendar.DATE) - objCalendarDate1.get(Calendar.DATE));  
	           	   iMonth = iMonth+(dayDiff/30);
	           }
	           else     
	               iMonth =-1;      
	       } catch (Exception e){ 	    	   
	    	   LOG.log(Level.WARNING, "Exception in getting month difference"+e.getMessage(),"");
	       }      
	       return iMonth;      
	  }   
}
