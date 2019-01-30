package com.avnet.emasia.webquote.web.security;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextImpl;

import com.avnet.emasia.webquote.entity.Role;
import com.avnet.emasia.webquote.exception.CommonConstants;
import com.avnet.emasia.webquote.exception.WebQuoteRuntimeException;
import com.avnet.emasia.webquote.user.ejb.UserSB;

/**
 * @author Lin, Tough(901518)
 * Created on 2013-4-15
 */

public class SetupSecurityContextFilter implements javax.servlet.Filter{
	
	private UserIdExtractor userIdExtractor;
	
	private UserSB userSB;
	
	private static final Logger LOG =Logger.getLogger(SetupSecurityContextFilter.class.getName());	
	
	public SetupSecurityContextFilter(){
		try {
			Context context = new InitialContext();
			userSB = (UserSB) context.lookup("java:app/WQ_EJB_UserAndAuthor/UserSB!com.avnet.emasia.webquote.user.ejb.UserSB");
		} catch (NamingException ne) {
			throw new WebQuoteRuntimeException(""+ne.getMessage(),new RuntimeException());
		}		
	}
	
	@Override
	public void destroy() {		
	}


	@Override
	public void doFilter(ServletRequest request, ServletResponse response,
			FilterChain chain) throws IOException, ServletException {
		
		Authentication authentication  = SecurityContextHolder.getContext().getAuthentication();
		if(authentication != null && ! authentication.getName().equalsIgnoreCase("anonymousUser")){
			chain.doFilter(request, response);
			return;
		}
		
		String username = userIdExtractor.getUserId((HttpServletRequest)request);
		
		if(username == null){
			chain.doFilter(request, response);
			return;
		}
		
		com.avnet.emasia.webquote.entity.User user = userSB.findByEmployeeIdWithAllRelation(username);
		
		if(((HttpServletRequest)request).getRequestURI().contains("Non-authorized")){
			chain.doFilter(request, response);
			return;
		}
		
		if(user == null || user.getActive() == false){
			LOG.info("Invalid user: " + username + " tried to access WebQuote.");

			((HttpServletResponse)response).sendRedirect(((HttpServletRequest)request).getContextPath() + "/Non-authorized.jsf");
			
			return;
		}
		
		Collection<GrantedAuthority> auths = new ArrayList<GrantedAuthority>();
		
		List<Role> roles = user.getRoles();
		
		StringBuffer sb = new StringBuffer();
		sb.append("[");
		for(Role role : roles){
			GrantedAuthority authority = new SimpleGrantedAuthority(role.getName());
			auths.add(authority);
			sb.append(role.getName());
		}
		sb.append("]");
		
		String password = "password";
		
		//
		WebSealAuthenticationToken webSealAuthenticationToken= new WebSealAuthenticationToken(new WQUserDetails(username, password, auths, user), "password", auths);
		webSealAuthenticationToken.setDetails(user);			
		SecurityContext context = new SecurityContextImpl();
		context.setAuthentication(webSealAuthenticationToken);
		SecurityContextHolder.setContext(context);
		
		try{
			user.setLastLoginDate(new Date());
			userSB.save(user);
		}catch(Exception e){
			LOG.log(Level.WARNING, "Save user " + user.getEmployeeId() + " failed. Exception message: "+e.getMessage());
		}
		
		
		LOG.info("User: " + username + " logined with authority " + sb.toString());
		
		chain.doFilter(request, response);
		
	}


	@Override
	public void init(FilterConfig arg0) throws ServletException {
		
	}


	public void setUserIdExtractor(UserIdExtractor userIdExtractor) {
		this.userIdExtractor = userIdExtractor;
	}



}
