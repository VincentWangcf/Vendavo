package com.avnet.emasia.webquote.utilities.schedule;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Level;

import javax.naming.InitialContext;
import javax.naming.NameClassPair;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;

import org.apache.commons.lang.StringUtils;
import org.jboss.as.server.ServerEnvironment;
import org.jboss.logging.Logger;
import org.jboss.msc.service.Service;
import org.jboss.msc.service.ServiceName;
import org.jboss.msc.service.StartContext;
import org.jboss.msc.service.StartException;
import org.jboss.msc.service.StopContext;
import org.jboss.msc.value.InjectedValue;

import com.avnet.emasia.webquote.exception.CommonConstants;
import com.avnet.emasia.webquote.exception.WebQuoteRuntimeException;
import com.avnet.emasia.webquote.utilities.MessageFormatorUtil;

/**
 * A service to start schedule-timer as HASingleton timer in a clustered
 * environment. The service will ensure that the timer is initialized only once
 * in a cluster.
 */
public class HATimerService implements Service<String> {

	private static final Logger LOGGER = Logger.getLogger(HATimerService.class.getName());
	public static final ServiceName SINGLETON_SERVICE_NAME = ServiceName.JBOSS.append("avnet", "webquote", "singleton", "timer");
	public static String jndiSchedulerSB = "";
	public static String jndiSysConfigSB = "";
	public static final String CLUSTER_NAME = "CLUSTER_NAME";
	public static final String JBOSS_NODE_NAME = "jboss.node.name";
	
	public static void initJndiName() {
		try {
			LOGGER.info("******************Init JDND Start*********************");
			NamingEnumeration<NameClassPair> namingEnumeration = new InitialContext().list("java:global/");
			if(namingEnumeration != null) {
				String earName = namingEnumeration.next().getName();
				LOGGER.info("///////////////////////earName===" + earName);
				jndiSchedulerSB = "java:global/" + earName + "/WQ_EJB_Utility/SchedulerSB";
				jndiSysConfigSB = "java:global/" + earName + "/WQ_EJB_Utility/SysConfigSB";
			}
			LOGGER.info("/////////JNDI_SCHEDULERSB==========" + jndiSchedulerSB);
			LOGGER.info("/////////JNDI_SYSCONFIGSB==========" + jndiSysConfigSB);
			LOGGER.info("******************Init JDND End*********************");
		} catch (Exception e) {
			LOGGER.error("///////////////////////initJndiName() method NamingException exception===" + e.getMessage());
		}
	}
	
	public static String getSchedulerSBJndi() {
		if(StringUtils.isBlank(jndiSchedulerSB)) {
			initJndiName();
		}
		return jndiSchedulerSB;
	}
	
	public static String getSysConfigSBJndi() {
		if(StringUtils.isBlank(jndiSysConfigSB)) {
			initJndiName();
		}
		return jndiSysConfigSB;
	}
	
	/**
	 * A flag whether the service is started.
	 */
	private final AtomicBoolean started = new AtomicBoolean(false);

	private String nodeName;
	final InjectedValue<ServerEnvironment> env = new InjectedValue<ServerEnvironment>();

	/**
	 * @return the name of the server node
	 */
	@Override
	public String getValue() throws IllegalStateException, IllegalArgumentException {
		if (!started.get()) {
			throw new WebQuoteRuntimeException(CommonConstants.COMMON_THE_SERVICE_IS_NOT_READY, new Object[]{this.getClass().getName()});
		}
		return this.nodeName;
	}

	@Override
	public void start(StartContext arg0) throws StartException {
		if (!started.compareAndSet(false, true)) {
			throw new StartException(CommonConstants.COMMON_THE_SERVICE_IS_STILL_STARTED);
		}
		LOGGER.info("----Start HASingleton timer service " + this.getClass().getName());
		try {
			this.nodeName = this.env.getValue().getNodeName();
			LOGGER.info("----Start HASingleton timer service on " + this.nodeName);			
			InitialContext ic = new InitialContext();
			SchedulerSB scheduler = (SchedulerSB) ic.lookup(HATimerService.getSchedulerSBJndi());
			scheduler.start();
		} catch (Exception e) {
			throw new StartException("Could not initialize timer", e);
		}
	}

	@Override
	public void stop(StopContext arg0)  {
		if (!started.compareAndSet(true, false)) {
			LOGGER.info("--------The service is not active! " + this.getClass().getName());
		} else {
			LOGGER.info("");
			LOGGER.info("------------Stop HASingleton timer service !" + this.getClass().getName());
			LOGGER.info("");
			try {
				InitialContext ic = new InitialContext();
				SchedulerSB scheduler = (SchedulerSB) ic.lookup(HATimerService.getSchedulerSBJndi());
				scheduler.stopAll();
				LOGGER.info("------------Stop HASingleton timer service successful.");
			} catch (Exception e) {
				LOGGER.error("-----------Could not stop timer " + e.getMessage());
			}
		}
	}
}
