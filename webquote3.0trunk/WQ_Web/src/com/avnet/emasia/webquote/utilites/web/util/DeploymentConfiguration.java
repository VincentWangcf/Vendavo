package com.avnet.emasia.webquote.utilites.web.util;

import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.Singleton;
import javax.ejb.Startup;

import com.avnet.emasia.webquote.utilities.common.SysConfigSB;
import com.avnet.emasia.webquote.web.quote.constant.QuoteConstant;

/**
 * @author punit.singh
 * This class is Singleton class
 */
@Singleton
@Startup
public class DeploymentConfiguration {
	
	/**
	 * A Logger object is used to log messages for a specific Class  
	 */
	private static final Logger LOGGER = Logger.getLogger(DeploymentConfiguration.class.getName());
	/**
	 * Blank String initialized with blank
	 */
	public static String configPath = "";
	
	
	
	@EJB
	private SysConfigSB sysConfigSB;
	
	 
	/**
	 * This method MUST be invoked before the class is put into service
	 */
	@PostConstruct
	public void readConfig() {
		try {

			//Look for APP_PROPERTY till not populate 
			do {
				Thread.sleep(2000);

			} while (sysConfigSB.getProperties().size() == 0);

			configPath = sysConfigSB.getProperyValue(QuoteConstant.TEMPLATE_PATH);

			//configPath = System.getProperty(serverConfigPath);
			// logic if directory does not exist
			final String path = configPath;
			final String directoryName = path;
			final File directory = new File(directoryName);
			if (!directory.exists()) {
				directory.mkdir();
			}
		} catch (SecurityException e) {
			LOGGER.log(Level.SEVERE, "Server config path (jboss.server.config.dir)does not exist : " + e.getMessage(),
					e);
		} catch (InterruptedException e) {
			LOGGER.log(Level.SEVERE, "Server Interrupted message : " + e.getMessage(),
					e);
		}
	}


	
	
	
	
}
