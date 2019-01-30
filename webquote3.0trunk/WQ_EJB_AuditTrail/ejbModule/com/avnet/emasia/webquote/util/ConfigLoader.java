package com.avnet.emasia.webquote.util;

/**
 * @author Tonmy,Li(906893)
 * @Created on 2012-12-25
 * 
 */
import java.io.IOException;
import java.util.Properties;

abstract public class ConfigLoader {

	private static ResourceLoader loader = ResourceLoader.getInstance();
	public static String getPropertiesConfig(String properties, String key) throws IOException {
		Properties prop = null;
		try {
			prop = loader.getProp(properties);
		} 
		catch (IOException e) 
		{
			throw e;
		}
		return prop.getProperty(key);
	}

}
