/*
 * Created on 2009-7-14
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.avnet.emasia.webquote.utilities.util;

/**
 * @author 906893
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class PerfLog {
	
	private long staTime=0l;
	private long endTime=0l;
	
    public PerfLog()
	{
    	this.staTime = System.currentTimeMillis();
	}
    
    public String getPerfLog()
    {
    	return " End Time["+this.getEndTime()+"] - Start Time["+this.staTime+"] = Total Time["+this.getTotalTime()+"]";
    }
    
    public String getTotalTime()
    {
       return 	String.valueOf((this.endTime-this.staTime)/1000)+" sec.";
    }
	/**
	 * @return Returns the end_time.
	 */
	public long getEndTime() {
		this.endTime = System.currentTimeMillis();
		return this.endTime;
	}
	/**
	 * @param endTime The end_time to set.
	 */
	public void setEndTime(long endTime) {
		this.endTime = endTime;
	}
	/**
	 * @return Returns the sta_time.
	 */
	public long getStaTime() {
		return staTime;
	}
	/**
	 * @param staTime The sta_time to set.
	 */
	public void setStaTime(long staTime) {
		this.staTime = staTime;
	}
}
