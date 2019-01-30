package com.avnet.emasia.webquote.utilites.web.common;

import java.util.List;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;

import com.avnet.emasia.webquote.entity.Role;
import com.avnet.emasia.webquote.entity.User;
import com.avnet.emasia.webquote.user.ejb.ApplicationSB;
import com.avnet.emasia.webquote.web.security.WQUserDetails;
import com.avnet.emasia.webquote.web.user.UserInfo;

import org.springframework.security.core.context.SecurityContextHolder;

@ManagedBean
@SessionScoped
public class DockingPageMB implements java.io.Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -2905529308934038440L;
	private User user;
	private final static String WEB_PROMOTE = "/WebPromo/CatalogPage.jsf";
	private final static String MY_QUOTE_QC_OPR = "/RFQ/MyQuoteListForQC.jsf";
	private final static String MY_QUOTE_MM = "/RFQ/MyQuoteListForMM.jsf";
	private final static String MY_PROFILE = "/UserMgmt/ViewMyProfile.jsf";
	private final static String WORKING_PLATFORM = "/WorkingPlatform/WorkingPlatformLayout.jsf";
//	private final static String WEBQUOTE = "/webquote";
	private static String contextRoot;

	@EJB
	private ApplicationSB applicationSB;

	public void setUser(User user) {
		this.user = user;
	}

	public User getUser() {
		return user;
	}

	public String getUrl() {

		user = UserInfo.getUser();
		List<Role> roleLst = user.getRoles();
		String roleName = roleLst.get(0).getName();
		
		if (contextRoot == null){
			contextRoot = FacesContext.getCurrentInstance().getExternalContext().getRequestContextPath();
			System.out.println(contextRoot);
		}

		if (roleName.contains("ROLE_SALES") || roleName.contains("ROLE_INSIDE_SALES") || roleName.contains("ROLE_SALES_MANAGER") || roleName.contains("ROLE_SALES_GM") || roleName.contains("ROLE_SALES_DIRECTOR")) {
			return contextRoot + WEB_PROMOTE;

		} else if (roleName.contains("ROLE_QC_PRICING") || roleName.contains("ROLE_QCP_MANAGER")) {

			return contextRoot + WORKING_PLATFORM;

		} else if (roleName.contains("ROLE_PM") ||  roleName.contains("ROLE_PM_SPM") || roleName.contains("ROLE_PM_BUM") || roleName.contains("ROLE_PM_MD")) {
			return contextRoot + WEB_PROMOTE;

		} else if (roleName.contains("ROLE_CS") || roleName.contains("ROLE_CS_MANAGER")) {
			return contextRoot + WEB_PROMOTE;
			
		} else if (roleName.contains("ROLE_CM") || roleName.contains("ROLE_CM_MANAGER")) {
			return contextRoot + MY_QUOTE_QC_OPR;			

		} else if (roleName.contains("ROLE_MM") || roleName.contains("ROLE_MM_MANAGER") || roleName.contains("ROLE_MM_DIRECTOR")) {
			return contextRoot + MY_QUOTE_MM;

		} else if (roleName.contains("ROLE_QC_OPERATION") || roleName.contains("ROLE_QCO_MANAGER") || roleName.contains("ROLE_QC_Director")) {
			return contextRoot + MY_QUOTE_QC_OPR;

		} else if (roleName.contains("ROLE_USER_ADMIN")) {
			return contextRoot + MY_PROFILE;

		} else if (roleName.contains("ROLE_SYS_ADMIN")) {
			return contextRoot + MY_PROFILE;

		} else if (roleName.contains("ROLE_WEBPROMO_ADMIN")) {
			return contextRoot + WEB_PROMOTE;
			
		}

		return contextRoot + MY_PROFILE;

	}

}