package com.avnet.emasia.webquote.web.user;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import com.avnet.emasia.webquote.entity.BizUnit;
import com.avnet.emasia.webquote.entity.Group;
import com.avnet.emasia.webquote.entity.User;
import com.avnet.emasia.webquote.user.ejb.GroupSB;
import com.avnet.emasia.webquote.user.ejb.UserSB;
import com.avnet.emasia.webquote.user.vo.UserSearchCriteria;

@ManagedBean
@SessionScoped
public class MyProfileMB implements Serializable {

	private static final long serialVersionUID = -804084060206999402L;

	private static final Logger LOG = Logger.getLogger(MyProfileMB.class.getName());

	private User user;
	

	@PostConstruct
	public void initialize() {
		
		user = UserInfo.getUser();
		
	}


	public User getUser() {
		return user;
	}


	public void setUser(User user) {
		this.user = user;
	}
	
}