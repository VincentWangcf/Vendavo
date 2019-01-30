package com.avnet.emasia.webquote.web.security;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;

import com.avnet.emasia.webquote.utilities.common.LoginConfigCache;

/**
 * @author Lin, Tough(901518)
 * Created on 2013-1-8
 */

public class WQLogoutSuccessHandler implements LogoutSuccessHandler{
	
	public WQLogoutSuccessHandler(){

	}
	
	@Override
	public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response, Authentication arg2) throws IOException, ServletException {
		if(LoginConfigCache.isProduction()){			
			String logoutUrl = LoginConfigCache.getLogoutUrl();
			response.sendRedirect(logoutUrl);
		}else{
			response.sendRedirect("login.jsp");			
		}
	}
	
}
