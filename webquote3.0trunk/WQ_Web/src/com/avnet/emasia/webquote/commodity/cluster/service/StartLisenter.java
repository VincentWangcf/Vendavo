package com.avnet.emasia.webquote.commodity.cluster.service;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

import org.jboss.logging.Logger;

import com.avnet.emasia.webquote.utilities.schedule.ClusterMessageReceiver;

@WebListener
public class StartLisenter implements ServletContextListener {
	private static final Logger LOGGER = Logger.getLogger(StartLisenter.class.getName());

	@Override
	public void contextInitialized(ServletContextEvent sce) {
		try {
			System.setProperty("org.apache.el.parser.COERCE_TO_ZERO", "false");

			LOGGER.info("************ StartLisenter for install Cluster message service ******************");
			if (System.getProperty("jboss.server.base.dir").endsWith("standalone")) {
				LOGGER.info("-------------- run on Standalone, install will be stop --------------------------");
			} else {
				new ClusterMessageReceiver().start();
				LOGGER.info("-------------------- Start WebQuote Cluster Message Reveiver -------------------------------");
			}
			LOGGER.info("******************************************************************************************");
		} catch (Exception ex) {
			LOGGER.error("--start chat receiver when web start---" + ex.getMessage(),ex);
			
		}
	}

	@Override
	public void contextDestroyed(ServletContextEvent sce) {

	}

}
