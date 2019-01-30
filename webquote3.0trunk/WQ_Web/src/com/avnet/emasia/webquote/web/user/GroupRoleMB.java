package com.avnet.emasia.webquote.web.user;

import java.util.List;

import com.avnet.emasia.webquote.entity.User;

public class GroupRoleMB {

	private List<User> users;
	
	private String employeeId;
	
	public List<User> getUsers() {
		return users;
	}

	public void setUsers(List<User> users) {
		this.users = users;
	}

	public String getEmployeeId() {
		return employeeId;
	}

	public void setEmployeeId(String employeeId) {
		this.employeeId = employeeId;
	}
}
