package com.avnet.emasia.webquote.web.security;

/**
 * @author Lin, Tough(901518)
 * Created on 2013-4-15
 */

import java.util.Collection;

import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

public class WebSealAuthenticationToken extends AbstractAuthenticationToken {

	private static final long serialVersionUID = 586507054416937106L;

	private Object principal;

    private Object credentials;

    public WebSealAuthenticationToken(Object aPrincipal, Object aCredentials, Collection<GrantedAuthority> anAuthorities) {
        super(anAuthorities);
        this.principal = aPrincipal;
        this.credentials = aCredentials;
        setAuthenticated(true);
    }


    public Object getCredentials() {
        return this.credentials;
    }

    public Object getPrincipal() {
        return this.principal;
    }



}
