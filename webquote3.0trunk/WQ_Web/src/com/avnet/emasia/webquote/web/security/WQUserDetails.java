package com.avnet.emasia.webquote.web.security;

import java.util.Collection;

import org.springframework.security.core.GrantedAuthority;

import com.avnet.emasia.webquote.entity.User;


/**
 * @author Lin, Tough(901518)
 * Created on 2013-1-8
 */

public class WQUserDetails extends org.springframework.security.core.userdetails.User {

	
	private static final long serialVersionUID = 6188844830640053267L;
	
	private User user;
	
	
	public WQUserDetails(String username, String password, boolean enabled, boolean accountNonExpired, boolean credentialsNonExpired, boolean accountNonLocked, Collection<? extends GrantedAuthority> authorities, User user){
		super(username, password, enabled, accountNonExpired, credentialsNonExpired, accountNonLocked, authorities);
		this.user = user;
	}

	public WQUserDetails(String username, String password, Collection<? extends GrantedAuthority> authorities, User user){
		super(username, password, authorities);
		this.user = user;
	}
	
	public User getUser() {
		return user;
	}


	public void setUser(User user) {
		this.user = user;
	}
	
	
	
}
