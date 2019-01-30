package com.avnet.emasia.webquote.user.vo;

import java.io.Serializable;
import java.text.DateFormat;
import com.avnet.emasia.webquote.utilities.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.avnet.emasia.webquote.entity.DataAccess;
import com.avnet.emasia.webquote.entity.User;

/**
 * @author Lin, Tough(901518)
 * Created on 2013-2-27
 */

public class UserSearchCriteria implements Serializable{
	
	private static final long serialVersionUID = -7311069553598137524L;
	
	private List<String> groups = new ArrayList<String>();	
	
	private List<String> bizUnits = new ArrayList<String>();	
	
	private List<String> roles = new ArrayList<String>();
	
	private String employeeId;
	private String employeeName;
	
	public UserSearchCriteria(){
		
	}
	
	@Override
	public String toString(){
		
		StringBuffer bu = new StringBuffer();
		bu.append("employeeId:[" + employeeId + "] "); 
		bu.append("employeeName:[" + employeeName + "] ");
		bu.append("bizUnits:[");
		for(String bizUnit : bizUnits){
			bu.append(bizUnit + ",");
		}
		bu.append("] ");
		bu.append("groups:[");
		for(String id : groups){
			bu.append(id + ",");
		}
		bu.append("] ");
		
		return bu.toString();
	}
	
	
	
	//Getters, Setters
	public String getEmployeeId() {
		return employeeId;
	}
	public void setEmployeeId(String employeeId) {
		this.employeeId = employeeId;
	}
	public String getEmployeeName() {
		return employeeName;
	}
	public void setEmployeeName(String employeeName) {
		this.employeeName = employeeName;
	}
	public List<String> getBizUnits() {
		return bizUnits;
	}
	public void setBizUnits(List<String> bizUnits) {
		this.bizUnits = bizUnits;
	}
	public List<String> getGroups() {
		return groups;
	}
	public void setGroups(List<String> groups) {
		this.groups = groups;
	}

	public List<String> getRoles() {
		return roles;
	}

	public void setRoles(List<String> roles) {
		this.roles = roles;
	}
	
}
