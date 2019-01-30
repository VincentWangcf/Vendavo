package com.avnet.emasia.webquote.reports.vo;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class QuoteDailyOptVo implements Serializable
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1446276722825246312L;
	private int seq;
	private String userPmName;
	private Date sentOutFrom;
	private Date sentOutTo;
	private String quantity;
	private List<String> stage = new ArrayList<String>();
	private List<String> status = new ArrayList<String>();
	private List<String> pageStage = new ArrayList<String>();

	public int getSeq()
	{
		return seq;
	}

	public String getQuantity()
	{
		return quantity;
	}

	public void setQuantity(String quantity)
	{
		this.quantity = quantity;
	}

	public List<String> getStatus()
	{
		return status;
	}


	public void setStatus(List<String> status)
	{
		this.status = status;
	}


	public List<String> getStage()
	{
		return stage;
	}

	public void setStage(List<String> stage)
	{
		this.stage = stage;
	}


	public void setSeq(int seq)
	{
		this.seq = seq;
	}

	public String getUserPmName()
	{
		return userPmName;
	}

	public void setUserPmName(String userPmName)
	{
		this.userPmName = userPmName;
	}

	public Date getSentOutFrom()
	{
		return sentOutFrom;
	}

	public void setSentOutFrom(Date sentOutFrom)
	{
		this.sentOutFrom = sentOutFrom;
	}

	public Date getSentOutTo()
	{
		return sentOutTo;
	}

	public void setSentOutTo(Date sentOutTo)
	{
		this.sentOutTo = sentOutTo;
	}

	public List<String> getPageStage()
	{
		return pageStage;
	}

	public void setPageStage(List<String> pageStage)
	{
		this.pageStage = pageStage;
	}

}
