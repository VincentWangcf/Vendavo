package com.avnet.emasia.webquote.web.security;

import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

/**
 * @author Lin, Tough(901518)
 * Created on 2013-1-8
 * @deprecated
 */

public class PageUserIdExtractor implements UserIdExtractor{

	private static final Logger LOG =Logger.getLogger(WebSealJBossUserIdExtractor.class.getName());
	
	@Override
	public String getUserId(HttpServletRequest request) {
		
		String userName = request.getParameter("j_username");

		return userName;

	}

}