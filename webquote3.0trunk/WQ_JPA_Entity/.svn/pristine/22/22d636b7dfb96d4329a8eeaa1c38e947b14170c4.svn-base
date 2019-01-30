package com.avnet.emasia.webquote.vo;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;


public class OfflineReportParam implements Serializable {
	
	private static final Logger LOG = Logger.getLogger(OfflineReportParam.class.getName());

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private String remoteEjbClass;

	private byte[] criteriaBeanValue;
	
	private String reportName;
	
	private String employeeId;
	
	//added for download offline function need the records on the WP page only, by Damonchen@20180312
	private Map<String,Object> filtersMap;
	
	/***some time the result list has been populated and with a small count of item 
	 * so we can use the result directly (the count of catalog search result 
	 * has no more than 100) and the search criteria is confirmed by step1.
	 * ****/
	private byte[] resultBeanValue;

	
 	public Map<String, Object> getFiltersMap() {
		return filtersMap;
	}

	public void setFiltersMap(Map<String, Object> filtersMap) {
		this.filtersMap = filtersMap;
	}

	public String getRemoteEjbClass() {
		return remoteEjbClass;
	}

	public void setRemoteEjbClass(String remoteEjbClass) {
		this.remoteEjbClass = remoteEjbClass;
	}

	public byte[] getCriteriaBeanValue() {
		return criteriaBeanValue;
	}

	public void setCriteriaBeanValue(byte[] criteriaBeanValue) {
		this.criteriaBeanValue = criteriaBeanValue;
	}
	
	public String getReportName() {
		return reportName;
	}

	public void setReportName(String reportName) {
		this.reportName = reportName;
	}
	
	public String getEmployeeId() {
		return employeeId;
	}

	public void setEmployeeId(String employeeId) {
		this.employeeId = employeeId;
	}

	public void setCriteriaBeanValue(Object obj) {
		setCriteriaBeanValue(transfer(obj));
	}
	
	private byte[] getResultBeanValue() {
		return resultBeanValue;
	}

	private void setResultBeanValue(byte[] resultBeanValue) {
		this.resultBeanValue = resultBeanValue;
	}
	
	public void setResultBean(Object obj) {
		this.setResultBeanValue(transfer(obj));
	}
	
	public Object getResultBean() {
		return reverse(this.getResultBeanValue());
	}
	
	private byte[] transfer(Object obj) {
		try {
			ByteArrayOutputStream bo = new ByteArrayOutputStream();
			ObjectOutputStream oo = new ObjectOutputStream(bo);
			oo.writeObject(obj);
			return bo.toByteArray();
		} catch (IOException e) {
			LOG.log(Level.SEVERE, "exception in converting object to raw data , Exception message : "+e.getMessage(), e);
		}
		return new byte[0];
	}
	
	public Object getCriteriaBean() {
		return reverse(this.getCriteriaBeanValue());
	}
	
	private Object reverse(byte[] criteriaBeanValue) {
		try {
			ByteArrayInputStream bi = new ByteArrayInputStream(criteriaBeanValue);
			ObjectInputStream oi = new ObjectInputStream(bi);
			return (oi.readObject());
		} catch (ClassNotFoundException | IOException e1) {
			LOG.log(Level.SEVERE,
					"Exception occoured while reading from inputstream in reverse Method" + e1.getMessage(), e1);
		}

		return null;

	}
	
}
