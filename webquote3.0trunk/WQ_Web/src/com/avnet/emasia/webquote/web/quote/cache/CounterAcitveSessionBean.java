package com.avnet.emasia.webquote.web.quote.cache;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Calendar;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ejb.EJB;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

import org.picketbox.util.StringUtil;
import org.springframework.security.core.context.SecurityContextImpl;

import com.avnet.emasia.webquote.entity.ActiveSession;
import com.avnet.emasia.webquote.entity.User;
import com.avnet.emasia.webquote.user.ejb.ActiveSessionSB;
import com.avnet.emasia.webquote.web.security.WQUserDetails;
import com.avnet.emasia.webquote.web.security.WebSealAuthenticationToken;
import com.avnet.emasia.webquote.web.user.UserInfo;


public class CounterAcitveSessionBean implements HttpSessionListener {
	
	/*@PersistenceContext(unitName="Server_Source")
	EntityManager em;*/
	
	@EJB 
	ActiveSessionSB activeSessionSB;
	
	
	
	private static final Logger LOG = Logger.getLogger(CounterAcitveSessionBean.class.getName());
	private static int totalActiveSessions;
	 
	 
	 
	 public static int getTotalActiveSession(){
		return totalActiveSessions;
		 
	  }
	 

	@Override
	public void sessionCreated(HttpSessionEvent se) {
		HttpSession session = se.getSession();
		/*try{
		 //String remoteAddress = ((RequestContextListener)RequestContextHolder.currentRequestAttributes()).getRequest().getRemoteAddr();
		// LOG.info("sessionCreated 1- remoteAddress is " +  remoteAddress);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			
		}*/
		User user = UserInfo.getUser();
		String employeeId = "000001";
		if(null!=user) {
			employeeId = user.getEmployeeId();
		}
		//LOG.info("sessionCreated 1 - ==>> " +user.getName()+"  " +  user.getEmployeeId());
		totalActiveSessions++;
		//if(null!=user){
		Calendar cal = Calendar.getInstance();
		Date date = cal.getTime();
		
		ActiveSession activeSession = new ActiveSession();
		activeSession.setSessionId(extractSessionId(session.getId()));
		activeSession.setLoginTime(date);
		activeSession.setUserId(employeeId);
		activeSession.setActive(1);
		String currentThreadName = Thread.currentThread().getName();
		
		
		if(!StringUtil.isNullOrEmpty(currentThreadName)) {
			currentThreadName = currentThreadName.substring(currentThreadName.indexOf("/")+1,currentThreadName.length());
			activeSession.setClientInfo(currentThreadName);
		}
		

		try {
			String hostname = InetAddress.getLocalHost().getHostName();
			activeSession.setSeverName(hostname);
		} catch (UnknownHostException e) {
			LOG.log(Level.SEVERE, "Exception in creating session , Exception message : "+e.getMessage(), e);
		}
		
		
		activeSessionSB.createActiveSession(activeSession);
	
		LOG.info("sessionCreated - new Session Id ==>> " +session.getId() +" add one session into counter,totalActiveSessions is " +  totalActiveSessions);
		
	}

	@Override
	public void sessionDestroyed(HttpSessionEvent se) {
		
		
		
		HttpSession session = se.getSession();
		Calendar cal = Calendar.getInstance();
		Date date = cal.getTime();
		try{
			
			SecurityContextImpl s  =(SecurityContextImpl)session.getAttribute("SPRING_SECURITY_CONTEXT");
//			server env use this
			WebSealAuthenticationToken u = (WebSealAuthenticationToken)s.getAuthentication();
//			windows env use this
//			UsernamePasswordAuthenticationToken u = (UsernamePasswordAuthenticationToken)s.getAuthentication();
			String userName = ((WQUserDetails)u.getPrincipal()).getUsername();
			if(userName!=null){
				LOG.log(Level.INFO, "User {0} session destroyed.", userName);
			}
		}catch(Exception e){
			LOG.log(Level.WARNING, "error occured when get user name in session listener");
		}
		
		activeSessionSB.updateSession(date, extractSessionId(session.getId()));
		
		if(totalActiveSessions>0){
			totalActiveSessions--;
			
		}
		
		LOG.info("sessionDestroyed  deduct one Session ID is========= ==>> " +session.getId()+" from counter,totalActiveSessions is " +  totalActiveSessions);
		
	}
	
	private String extractSessionId(String sessionId) {
		int endPos = sessionId.indexOf('.');
		String newSessionId = sessionId;
		if(endPos>0) {
			newSessionId = sessionId.substring(0,endPos);
		}
		return newSessionId;
	}
	
	

}
