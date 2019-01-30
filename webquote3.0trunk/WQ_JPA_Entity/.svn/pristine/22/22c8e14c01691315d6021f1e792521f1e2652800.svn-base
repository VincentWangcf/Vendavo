package com.avnet.emasia.webquote.strategy;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.avnet.emasia.webquote.entity.Strategy;

 
public class StrategyFactory implements Serializable{
	/**
	 * 
	 */
	private static final Logger LOG = Logger.getLogger(StrategyFactory.class.getName());
	private static final long serialVersionUID = 50458959260212625L;
	private static final StrategyFactory factory = new StrategyFactory();
	private final Map<String,Object> map = new HashMap<String,Object>();
	private final Map<String,Object> instanceMap = new ConcurrentHashMap<String,Object>();
	
	private StrategyFactory() {
	}
	
	public Object getStrategy(Class<? extends Serializable> strategyInterface, String key, ClassLoader loader) {
		return initAndGetStrategy(strategyInterface, key, loader);
	}
	
	public Object getStrategy(Class<? extends Serializable> strategyInterface, String key) {
		return initAndGetStrategy(strategyInterface, key , null);
	}
	
	private Object initAndGetStrategy(Class<? extends Serializable> strategyInterface, String key, ClassLoader loader) {
		String impletionClassName = (String)map.getOrDefault(strategyInterface.getName() + "|" + key,
				map.get(strategyInterface.getName() + "|DEFAULT" ));
		String iplNameKey = impletionClassName +"|"+strategyInterface.getName();
		return instanceMap.computeIfAbsent(iplNameKey, k ->  {
			Object o2 = null;
			try {
				if(loader != null) 
					o2 = Class.forName(impletionClassName, true, loader).newInstance();
				else 
					o2 = Class.forName(impletionClassName).newInstance();
				
			} catch (Exception e) {
				LOG.log(Level.SEVERE,impletionClassName + " init failed! classloader:" + loader, e);
				throw new RuntimeException(e);
			}
			return o2;
		});
	}
	/*public synchronized void initMap(Map<?, ?> cacheMap) {
		cacheMap.forEach((k, v)->map.put((String)k, (Object)v));
	}*/
	public synchronized void initMap(List<Strategy> strategies) {
		map.clear();
		strategies.stream().forEach(strategy -> map.put(
				strategy.getStrategyInterface() + "|" + strategy.getKey(), 
				strategy.getStrategyImplementation()));
	}
	
	public static StrategyFactory getSingletonFactory() {
		return factory;
	}
	
}
