package com.avnet.emasia.webquote.web.security;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.access.SecurityConfig;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.FilterInvocation;
import org.springframework.security.web.access.intercept.FilterInvocationSecurityMetadataSource;
import org.springframework.security.web.util.AntPathRequestMatcher;
import org.springframework.security.web.util.RequestMatcher;
import org.springframework.stereotype.Service;

import com.avnet.emasia.webquote.exception.WebQuoteRuntimeException;
import com.avnet.emasia.webquote.user.ejb.ApplicationSB;
import com.avnet.emasia.webquote.user.ejb.ApplicationSBListener;
import com.avnet.emasia.webquote.user.ejb.UserSB;

/**
 * @author Lin, Tough(901518) Created on 2013-1-15
 */

public class JdbcFilterInvocationSecurityMetadataSource implements
		FilterInvocationSecurityMetadataSource, ApplicationSBListener {

	private static Map<String, Collection<ConfigAttribute>> resourceMap = null;
	
	ApplicationSB applicationSB = null;

	public JdbcFilterInvocationSecurityMetadataSource() {
		try {
			Context context = new InitialContext();
			applicationSB = (ApplicationSB) context.lookup("java:app/WQ_EJB_UserAndAuthor/ApplicationSB!com.avnet.emasia.webquote.user.ejb.ApplicationSB");
			applicationSB.addApplicationSBListener(this);
		} catch (NamingException ne) {
			throw new WebQuoteRuntimeException(ne.getMessage(), new RuntimeException(ne));
		}

		loadResourceDefine();
	}
	
	@Override
	public void applicationSBChanged() {
		loadResourceDefine();
		
	}	

	private void loadResourceDefine() {
		
		Logger.getLogger(getClass().getName()).log(Level.SEVERE, "load reource map");

		String username = "";
		String sql = "";

		resourceMap = new LinkedHashMap<String, Collection<ConfigAttribute>>();
		/*
		 * for (String auth : query) { ConfigAttribute ca = new
		 * SecurityConfig(auth);
		 * 
		 * List<String> query1 = session .createSQLQuery(
		 * "select b.resource_string " +
		 * "from Pub_Authorities_Resources a, Pub_Resources b, " +
		 * "Pub_authorities c where a.resource_id = b.resource_id " +
		 * "and a.authority_id=c.authority_id and c.Authority_name='" + auth +
		 * "'").list();
		 * 
		 * for (String res : query1) { String url = res;
		 * 
		 * 
		 * åˆ¤æ–­èµ„æº�æ–‡ä»¶å’Œæ�ƒé™�çš„å¯¹åº”å…³ç³»ï¼Œå¦‚æžœå·²ç»�å­˜åœ¨ç›¸å…³çš„èµ„æº�urlï¼Œåˆ™è¦�é€šè¿‡è¯¥urlä¸ºkeyæ��å�–å‡ºæ�ƒé™�é›†å�ˆï¼Œå°†æ�ƒé™�å¢žåŠ åˆ°æ�ƒé™�é›†å�ˆä¸­ã€‚ sparta
		 * 
		 * if (resourceMap.containsKey(url)) {
		 * 
		 * Collection<ConfigAttribute> value = resourceMap.get(url);
		 * value.add(ca); resourceMap.put(url, value); } else {
		 * Collection<ConfigAttribute> atts = new ArrayList<ConfigAttribute>();
		 * atts.add(ca); resourceMap.put(url, atts); }
		 * 
		 * }
		 * 
		 * }
		 */
		
		LinkedHashMap<String, List<String>> screenRoles = applicationSB.getScreenRoles();
		
		Set<String> screens = screenRoles.keySet();
		
		for(String screen : screens){
			List<String> roles = screenRoles.get(screen);
			Collection<ConfigAttribute> atts = new ArrayList<ConfigAttribute>();
			for(String role : roles){
				ConfigAttribute ca = new SecurityConfig(role);
				atts.add(ca);
			}
			
			resourceMap.put(screen, atts);
		}
		
/*		Collection<ConfigAttribute> atts = new ArrayList<ConfigAttribute>();
		ConfigAttribute ca = new SecurityConfig("ROLE_SALES");
		atts.add(ca);
		resourceMap.put("/sales/**", atts);
		atts = new ArrayList<ConfigAttribute>();
		ca = new SecurityConfig("ROLE_ADMIN");
		atts.add(ca);
		resourceMap.put("/admin/**", atts);*/
	}

	@Override
	public Collection<ConfigAttribute> getAllConfigAttributes() {

		return null;
	}

	// æ ¹æ�®URLï¼Œæ‰¾åˆ°ç›¸å…³çš„æ�ƒé™�é…�ç½®ã€‚
	@Override
	public Collection<ConfigAttribute> getAttributes(Object object)
			throws IllegalArgumentException {

		// object æ˜¯ä¸€ä¸ªURLï¼Œè¢«ç”¨æˆ·è¯·æ±‚çš„urlã€‚

		String url = ((FilterInvocation) object).getRequestUrl();

		/*
		 * int firstQuestionMarkIndex = url.indexOf("?");
		 * 
		 * if (firstQuestionMarkIndex != -1) { url = url.substring(0,
		 * firstQuestionMarkIndex); }
		 */

		Iterator<String> ite = resourceMap.keySet().iterator();

		while (ite.hasNext()) {
			String resURL = ite.next();
			RequestMatcher urlMatcher = new AntPathRequestMatcher(resURL);

			if (urlMatcher
					.matches(((FilterInvocation) object).getHttpRequest())) {

				return resourceMap.get(resURL);
			}
		}

		return null;
	}

	@Override
	public boolean supports(Class<?> arg0) {

		return true;
	}

}
