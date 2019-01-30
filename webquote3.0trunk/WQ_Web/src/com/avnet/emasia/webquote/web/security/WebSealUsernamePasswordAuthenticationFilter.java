package com.avnet.emasia.webquote.web.security;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.jboss.logging.NDC;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.avnet.emasia.webquote.entity.User;
import com.avnet.emasia.webquote.utilities.common.LoginConfigCache;
import com.avnet.emasia.webquote.web.user.UserInfo;
import com.sun.faces.application.ApplicationAssociate;

/**
 * @author Lin, Tough(901518) Created on 2013-1-16
 */

public class WebSealUsernamePasswordAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

	private static final Logger LOG = Logger.getLogger(WebSealUsernamePasswordAuthenticationFilter.class.getName());

	private UserIdExtractor userIdExtractor;

	public WebSealUsernamePasswordAuthenticationFilter() {

	}

	@Override
	public String obtainUsername(HttpServletRequest request) {
		String username = userIdExtractor.getUserId(request);
		return username;
	}

	@Override
	protected String obtainPassword(HttpServletRequest request) {
		String password = "";
		if (StringUtils.isBlank(request.getHeader("iv-user"))) {
			if (LoginConfigCache.isProduction()) {
				password = request.getParameter("j_password");
			}

			if (StringUtils.isNotBlank(password)) {
				try {
					password = MD5Util.md5Encode(password);
				} catch (Exception e)
				{
					LOG.log(Level.SEVERE, "Exception in obtaining the password from request , exception message : "+e.getMessage(), e);
				}
				return password;
			}
		}
		return "password";
	}

	public UserIdExtractor getUserIdExtractor() {
		return userIdExtractor;
	}

	public void setUserIdExtractor(UserIdExtractor userIdExtractor) {
		this.userIdExtractor = userIdExtractor;
	}

	/* (non-Javadoc)
	 * @see org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter#doFilter(javax.servlet.ServletRequest, javax.servlet.ServletResponse, javax.servlet.FilterChain)
	 * Here in doFilter User id added for logging in log
	 */
	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		if(request instanceof HttpServletRequest){
			User info = UserInfo.getUser();
			NDC.push(info!=null?info.getEmployeeId():"");
			instance = ApplicationAssociate.getCurrentInstance();
			super.doFilter(request, response, chain);
			NDC.pop();
		}
	}
	
	private static ApplicationAssociate instance;

	public static ApplicationAssociate getApplicationAssociate(){
		return instance;
	}
	
	/*
	 * (non-Java doc)
	 * 
	 * attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
	 */
	@Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        return super.attemptAuthentication(request, response); 
    } 	
	
}
