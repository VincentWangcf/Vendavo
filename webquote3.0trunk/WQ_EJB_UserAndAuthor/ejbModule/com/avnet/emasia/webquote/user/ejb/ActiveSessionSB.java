package com.avnet.emasia.webquote.user.ejb;

import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.Resource;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.transaction.UserTransaction;

import com.avnet.emasia.webquote.entity.ActiveSession;
import com.avnet.emasia.webquote.entity.Country;
import com.avnet.emasia.webquote.entity.Quote;

@Stateless
@LocalBean
public class ActiveSessionSB {
	
	
	//@Resource
   // UserTransaction ut;
    
	@PersistenceContext(unitName="Server_Source")
	EntityManager em;
	
    private final static int SESSION_TIME_OUT = 36000;
	private static final Logger LOG =Logger.getLogger(ActiveSessionSB.class.getName());
	
	
	public List<ActiveSession> findActiveSession() {
		
		String sql = "select q from ActiveSession q where q.active=:active order by q.userId,q.loginTime ";
		Query query = em.createQuery(sql, ActiveSession.class);
		query.setParameter("active", 1);
		List<ActiveSession> sessionLst = query.getResultList();
		return sessionLst;
		
	}
	
	public void clearSession(){
		
    	/*Query q1 = em.createQuery("UPDATE  ActiveSession v set v.active = :newActive where v.active=:oldActive");
    	q1.setParameter("newActive", 1);
    	q1.setParameter("oldActive", 0);*/
		Query q1 = em.createQuery("DELETE FROM ActiveSession");
		try {
	    	q1.executeUpdate();
	    	em.flush();
        	em.clear();
			//ut.commit();
		} catch (Exception ex){
			LOG.log(Level.SEVERE, "Exception Occoured while updating query in clearSession method:" + ex.getMessage(),ex);
	    	em.flush();
        	em.clear();
        	/*try {
        		ut.commit();
        	} catch (Exception e){
        		LOG.log(Level.SEVERE, "clear ActiveSession failed!");
        	}			*/
			LOG.log(Level.INFO, "transaction error on clear ActiveSession ");		
		}
	}
	
	public void updateSession(Date logoutTime,String sessionID){
    	Query q1 = em.createQuery("UPDATE ActiveSession v set v.logoutTime =:logoutTime,v.active=:active where v.sessionId=:sessionId");
    	q1.setParameter("logoutTime", logoutTime);
    	q1.setParameter("active", 0);
    	q1.setParameter("sessionId", sessionID);
		try {
			q1.executeUpdate();
	    	em.flush();
        	em.clear();
		} catch (Exception ex){
			LOG.log(Level.SEVERE, "update session failed for session id : "+sessionID+" , Message : "+ex.getMessage(),ex);
	    	em.flush();
        	em.clear();
        	/*try {
        		ut.commit();
        	} catch (Exception e){
        		LOG.log(Level.SEVERE, "update ActiveSession logout time failed!");
        	}			*/
		}
	}
	
	public void createActiveSession(ActiveSession activeSession){
		em.persist(activeSession);
    	em.flush();
    	em.clear();
        	
	}
	
	

}
