package com.avnet.emasia.webquote.reports.ejb.client;

import javax.naming.*;

import com.avnet.emasia.webquote.reports.ejb.job.OfflineRptJob;
import com.avnet.emasia.webquote.reports.ejb.remote.OfflineRptRemote;

import java.util.*;


public class RemoteEJBClientForOfflineRpt {

	public static void main(String[] args) throws Exception {
        invokeOfflineRptEJB(10143071239l);
	}

	public static int invokeOfflineRptEJB(long requestId) throws NamingException {

		System.out.println("Calll invokeOfflineRptEJB start");
		int returnInt = 1; 
		final OfflineRptRemote ejb = lookupRemoteOfflineRptEJB();
		returnInt = ejb.executeTask(requestId);
		System.out.println("Calll invokeOfflineRptEJB end");
		return returnInt;
	}


	
	private static OfflineRptRemote lookupRemoteOfflineRptEJB() throws NamingException {
		final Hashtable jndiProperties = new Hashtable();
		jndiProperties.put(Context.URL_PKG_PREFIXES, "org.jboss.ejb.client.naming");
		final Context context = new InitialContext(jndiProperties);
		final String appName = "WebQuoteRptEar";
		final String moduleName = "WQ_RPT_EJB_Reports";
		final String distinctName = "";
		final String beanName = OfflineRptJob.class.getSimpleName();
		final String viewClassName = OfflineRptRemote.class.getName();
		System.out.println("Looking EJB via JNDI ");
		System.out.println("ejb:" + appName + "/" + moduleName + "/" + distinctName + "/" + beanName + "!" + viewClassName);
		return (OfflineRptRemote) context.lookup("ejb:" + appName + "/" + moduleName + "/" + distinctName + "/" + beanName + "!" + viewClassName);

	}

	
}
