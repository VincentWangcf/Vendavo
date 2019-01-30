package com.avnet.emasia.webquote.web.security;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.apache.commons.lang.StringUtils;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.GrantedAuthorityImpl;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import com.avnet.emasia.webquote.entity.Role;
import com.avnet.emasia.webquote.exception.CommonConstants;
import com.avnet.emasia.webquote.exception.WebQuoteRuntimeException;
import com.avnet.emasia.webquote.user.ejb.UserSB;
import com.avnet.emasia.webquote.utilities.common.LoginConfigCache;


/**
 * @author Lin, Tough(901518) Created on 2013-1-15
 */

public class UserDetailsServiceImpl implements UserDetailsService {

	private static final Logger LOG = Logger.getLogger(UserDetailsServiceImpl.class.getName());

	private UserSB userSB;

	public UserDetailsServiceImpl() {
		try {
			Context context = new InitialContext();
			userSB = (UserSB) context.lookup("java:app/WQ_EJB_UserAndAuthor/UserSB!com.avnet.emasia.webquote.user.ejb.UserSB");
		} catch (NamingException ne) {
			throw new WebQuoteRuntimeException(ne.getMessage(), new RuntimeException(ne));
		}
	}

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		
		com.avnet.emasia.webquote.entity.User user = userSB.findByEmployeeIdWithAllRelation(username);
		
		if(user == null || user.getActive() == false ){
			/*LOG.info("Invalid user: " + username + " tried to access WebQuote.");*/
			throw new UsernameNotFoundException(username + " can nnot be found");
		}
		
		Collection<GrantedAuthority> auths = new ArrayList<GrantedAuthority>();
		
		List<Role> roles = user.getRoles();
		for(Role role : roles){
			GrantedAuthority authority = new GrantedAuthorityImpl(role.getName());
			auths.add(authority);
		}
		
		String password = "password";
		if(StringUtils.isNotBlank(user.getPassword()) && LoginConfigCache.isProduction()) {
			password = user.getPassword();
		}
		

		LOG.info("User: " + username + " logged in.");

		
		return new WQUserDetails(username, password, auths, user);

	}

}
