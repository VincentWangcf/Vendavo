package com.avnet.emasia.webquote.web.reports.vo;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.ParseException;
import com.avnet.emasia.webquote.utilities.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

import org.apache.commons.lang.time.DateUtils;

public class QuoteDailyReportVo  implements Serializable{
	

	/**
	 * 
	 */
	private static final long serialVersionUID = -3108816655027586376L;
	private String qcPricer;
	private Map<String,Integer> dateMap;
	private Date firstDate;
	private int col1;
	private int col2;
	private int col3;
	private int col4;
	private int col5;
	private int col6;
	private int col7;
	private int total;
	
	private boolean isTotalRow = false;	
	
	DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
	
	
	
	public boolean isTotalRow()
	{
		return isTotalRow;
	}
	public void setIsTotalRow(boolean isTotalRow)
	{
		this.isTotalRow = isTotalRow;
	}
	public void setCol1(int col1)
	{
		this.col1 = col1;
	}
	public void setCol2(int col2)
	{
		this.col2 = col2;
	}
	public void setCol3(int col3)
	{
		this.col3 = col3;
	}
	public void setCol4(int col4)
	{
		this.col4 = col4;
	}
	public void setCol5(int col5)
	{
		this.col5 = col5;
	}
	public void setCol6(int col6)
	{
		this.col6 = col6;
	}
	public void setCol7(int col7)
	{
		this.col7 = col7;
	}

	public void setTotal(int total)
	{
		this.total = total;
	}
	   
	public String getQcPricer() {
		return qcPricer;
	}
	public void setQcPricer(String qcPricer) {
		this.qcPricer = qcPricer;
	}
	public Map<String, Integer> getDateMap() {
		return dateMap;
	}
	public void setDateMap(Map<String, Integer> dateMap) {
		this.dateMap = dateMap;
	}

	public String getQuoteDetails()
	{
		String quoteDetails="";
		Map<String, Integer> map = dateMap;
		for (Map.Entry<String, Integer> entry : dateMap.entrySet())
		{
			quoteDetails += "[" + entry.getKey() + ":" + entry.getValue() + "]\n";
		}
		return quoteDetails;
	}
	
	public Date getFirstDate() {
		return firstDate;
	}
	public void setFirstDate(Date firstDate) {
		this.firstDate = firstDate;
	}
	
	public int getCol1() throws ParseException {
		if (isTotalRow)
		{
			return col1;
		}

		for (Map.Entry<String,Integer> entry : dateMap.entrySet()) {
		    String key = entry.getKey();
		    Integer value = entry.getValue();
		    Date date = formatter.parse(key);
		    if(date.getTime() == firstDate.getTime())
			{
				col1 = value;
			}
		}
		return col1;
	}
	public int getCol2() throws ParseException {
		if (isTotalRow)
		{
			return col2;
		}

		for (Map.Entry<String,Integer> entry : dateMap.entrySet()) {
		    String key = entry.getKey();
		    Integer value = entry.getValue();
		    Date date = formatter.parse(key);
		    
		    if(date.getTime() == DateUtils.addDays(firstDate, 1).getTime())
			{
				col2 = value;
			}
		}
		return col2;
	}
	public int getCol3() throws ParseException {
		if (isTotalRow)
		{
			return col3;
		}
		
		for (Map.Entry<String,Integer> entry : dateMap.entrySet()) {
		    String key = entry.getKey();
		    Integer value = entry.getValue();
		    Date date = formatter.parse(key);
		    
		    if(date.getTime() == DateUtils.addDays(firstDate, 2).getTime())
			{
				col3 = value;
			}
		}
		return col3;
	}
	public int getCol4() throws ParseException {
		if (isTotalRow)
		{
			return col4;
		}
		
		for (Map.Entry<String,Integer> entry : dateMap.entrySet()) {
		    String key = entry.getKey();
		    Integer value = entry.getValue();
		    Date date = formatter.parse(key);
		    
		    if(date.getTime() == DateUtils.addDays(firstDate, 3).getTime())
			{
				col4 = value;
			}
		}
		return col4;
	}
	public int getCol5() throws ParseException {
		if (isTotalRow)
		{
			return col5;
		}
		
		for (Map.Entry<String,Integer> entry : dateMap.entrySet()) {
		    String key = entry.getKey();
		    Integer value = entry.getValue();
		    Date date = formatter.parse(key);
		    
		    if(date.getTime() == DateUtils.addDays(firstDate, 4).getTime())
			{
				col5 = value;
			}
		}
		return col5;
	}
	public int getCol6() throws ParseException {
		if (isTotalRow)
		{
			return col6;
		}
		
		for (Map.Entry<String,Integer> entry : dateMap.entrySet()) {
		    String key = entry.getKey();
		    Integer value = entry.getValue();
		    Date date = formatter.parse(key);
		    
		    if(date.getTime() == DateUtils.addDays(firstDate, 5).getTime())
			{
				col6 = value;
			}
		}
		return col6;
	}
	public int getCol7() throws ParseException {
		if (isTotalRow)
		{
			return col7;
		}
		
		for (Map.Entry<String,Integer> entry : dateMap.entrySet()) {
		    String key = entry.getKey();
		    Integer value = entry.getValue();
		    Date date = formatter.parse(key);
		    
		    if(date.getTime() == DateUtils.addDays(firstDate, 6).getTime())
			{
				col7 = value;
			}
		}
		return col7;
	}
	
	public int getTotal() {
		if (isTotalRow)
		{
			return total;
		}
		
		total = col1+col2+col3+col4+col5+col6+col7;
		return total;
	}
	
}