package com.avnet.emasia.webquote.cluster.dispatcher;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.wildfly.clustering.dispatcher.Command;
import org.wildfly.clustering.group.Node;

import com.avnet.emasia.webquote.cache.ActionIndicator;
import com.avnet.emasia.webquote.cache.ICacheUtil;
import com.avnet.emasia.webquote.utilites.web.common.CacheUtil;
 
public class CacheSynchronizationCommand implements Command<String, Node> {
	/**
	 * 
	 */
	private static final long serialVersionUID = -7181690731370724551L;
	private ActionIndicator actionIndicator;
	private static ICacheUtil cacheUtilEntity;
	private static CacheUtil cacheUtilWQ;
	static final Logger logger = Logger.getLogger(CacheSynchronizationCommand.class.getName());
	static Context context;
	static {
		try {
			context = new InitialContext();
			cacheUtilEntity = ((ICacheUtil) context.lookup("java:global/WebQuote_jpEar/WQ_JPA_Entity/CacheUtil!com.avnet.emasia.webquote.cache.ICacheUtil"));
			cacheUtilWQ = ((CacheUtil) context.lookup("java:global/WebQuote_jpEar/WQ_Web/CacheUtil!com.avnet.emasia.webquote.utilites.web.common.CacheUtil"));
			logger.info("context .... JAPAN.." );
		} catch (NamingException e) {
			try {
				cacheUtilEntity = ((ICacheUtil) context.lookup("java:global/WebQuoteEar/WQ_JPA_Entity/CacheUtil!com.avnet.emasia.webquote.cache.ICacheUtil"));
				cacheUtilWQ = ((CacheUtil) context.lookup("java:global/WebQuoteEar/WQ_Web/CacheUtil!com.avnet.emasia.webquote.utilites.web.common.CacheUtil"));
				logger.info("context .... ASIA.." );
			}catch (NamingException ex) {
				logger.log(Level.SEVERE, "This shoud not happen" , ex);
			}
		}			
	}
	
	public CacheSynchronizationCommand(ActionIndicator actionIndicator) {
		this.actionIndicator = actionIndicator;
	}
    @Override
    public String execute(Node node) {
    	cacheUtilEntity.clear(actionIndicator);
    	cacheUtilWQ.clear(actionIndicator);
    	logger.info(node.getName() + " done.");
    	return node.getName();
    }
}

