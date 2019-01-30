package com.avnet.emasia.webquote.quote.strategy.factory;

import org.apache.commons.lang.StringUtils;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * <p>Bean Factory</p>
 * @author Lenon.Yang(043044)
 * @version 1.0
 * @date 2016-11-30
 *
 */
public final class BeanFactory {
	private static BeanFactory factory = null;
	private static final Logger LOG = Logger.getLogger(BeanFactory
			.class.getName());
	private BeanFactory() {
	}


	public static BeanFactory getInstance() {
		if (factory == null) {
			factory = new BeanFactory();
		}
		return factory;
	}

	public Object getBean(String className) {
		Object obj = null;
		if (StringUtils.isNotBlank(className)) {
//			try {
				try {
					obj = Class.forName(className).newInstance();
			} catch (Exception e) {
				LOG.log(Level.SEVERE,"Exception occurs in Bean getting :: "+className+""+ e.getMessage(), e);
				}
//			} catch (Exception e) {
//				e.printStackTrace();
//			}
		}
		return obj;
	}
	
	public Object getBean(Class<?> clazz) {
		Object obj = null;
//		try {
			try {
				obj = clazz.getClass().newInstance();
		} catch (Exception e) {
				
				LOG.log(Level.SEVERE,"Exception occurs on getBean  " + e.getMessage(), e);
			}
//		} catch (Exception e) {
//			e.printStackTrace();
//		} 
		return obj;
	}

}
