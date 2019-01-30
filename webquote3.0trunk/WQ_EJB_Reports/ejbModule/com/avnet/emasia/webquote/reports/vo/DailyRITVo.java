package com.avnet.emasia.webquote.reports.vo;

import java.io.Serializable;
import java.util.List;
import com.avnet.emasia.webquote.entity.BizUnit;
import com.avnet.emasia.webquote.entity.User;

public class DailyRITVo implements Serializable
{
	/**
	 * 
	 */
	private static final long serialVersionUID = -5750301378102718774L;
	private String reportType;
	private List<User> userLst;
	private BizUnit bizUnit;
	public String getReportType()
	{
		return reportType;
	}
	public void setReportType(String reportType)
	{
		this.reportType = reportType;
	}
	public List<User> getUserLst()
	{
		return userLst;
	}
	public void setUserLst(List<User> userLst)
	{
		this.userLst = userLst;
	}
	public BizUnit getBizUnit()
	{
		return bizUnit;
	}
	public void setBizUnit(BizUnit bizUnit)
	{
		this.bizUnit = bizUnit;
	}
}
