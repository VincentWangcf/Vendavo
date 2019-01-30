package com.avnet.emasia.webquote.web.user;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpSession;

import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.primefaces.context.RequestContext;

import com.avnet.emasia.webquote.entity.BizUnit;
import com.avnet.emasia.webquote.entity.Group;
import com.avnet.emasia.webquote.entity.Role;
import com.avnet.emasia.webquote.entity.User;
import com.avnet.emasia.webquote.user.ejb.GroupSB;
import com.avnet.emasia.webquote.user.ejb.RoleSB;
import com.avnet.emasia.webquote.user.ejb.UserSB;
import com.avnet.emasia.webquote.user.vo.UserSearchCriteria;

@ManagedBean
@SessionScoped
public class ResumeSessionMB implements Serializable {


	private static final long serialVersionUID = 8578493247515686157L;
	
	private static final Logger LOG = Logger.getLogger(ResumeSessionMB.class.getName());
	
	public void resumeSession(){
		HttpSession session = (HttpSession)FacesContext.getCurrentInstance().getExternalContext().getSession(false);
		LOG.info("Session resumed: " + UserInfo.getUser().getEmployeeId());
		RequestContext context = RequestContext.getCurrentInstance();
		context.addCallbackParam("sessionResumed", "1");
	}
	
}
