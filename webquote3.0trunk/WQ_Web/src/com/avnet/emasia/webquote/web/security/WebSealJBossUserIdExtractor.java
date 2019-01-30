package com.avnet.emasia.webquote.web.security;

import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;

import com.avnet.emasia.webquote.utilities.common.LoginConfigCache;

/**
 * @author Lin, Tough(901518) Created on 2013-1-8
 */

public class WebSealJBossUserIdExtractor implements UserIdExtractor {

	private static final Logger LOG = Logger.getLogger(WebSealJBossUserIdExtractor.class.getName());

	@Override
	public String getUserId(HttpServletRequest request) {
		/*
		 * this is used in WebSphere
		 * 
		 * Principal principal = request.getUserPrincipal(); return
		 * principal.getName();
		 */
		String username = "";
		if (LoginConfigCache.isProduction()) {
			// Below is used in JBoss
			username = request.getHeader("iv-user");
			// get username from login page   added by Lenon 2016-08-03
			if(StringUtils.isBlank(username)) {
				username = request.getParameter("j_username");
			}
		} else {
			username = request.getParameter("j_username");
		}

		LOG.info("username is: " + username);

		return username;
	}

}