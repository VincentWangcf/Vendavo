package com.avnet.emasia.webquote.utilities;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;


final class PropertyUtils {

	/** The logger. */
	private static final Logger LOGGER = Logger.getLogger(PropertyUtils.class.getSimpleName());

	/** The property. */
	private static Properties property = loadProperties("configurations.properties");

	private PropertyUtils() {}
	
	/**
	 * Load properties.
	 *
	 * @param fileName the file name
	 * @return the properties
	 */
	public static Properties loadProperties(String fileName) {
		Properties prop = null;
		LOGGER.info("In PropertyUtils. Going to load configuration");
		try {
			// load a properties file
			InputStream in = Thread.currentThread().getContextClassLoader()
					.getResourceAsStream(fileName);
			prop = new Properties();
			prop.load(in);

		} catch (IOException ex) {
			LOGGER.log(Level.SEVERE, "Property File Not Found", ex);
		}
		return prop;
	}

	/**
	 * Gets the property.
	 * @param key the key
	 * @return the property
	 */
	public static String getProperty(String key) {
		return property.getProperty(key);
	}
}
