package com.avnet.emasia.webquote.cluster.dispatcher;

import java.io.IOException;
import java.io.PrintWriter;
import java.text.MessageFormat;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;

import org.primefaces.context.RequestContext;
import org.wildfly.clustering.dispatcher.CommandDispatcherFactory;
import org.wildfly.clustering.dispatcher.CommandResponse;
import org.wildfly.clustering.group.Group;
import org.wildfly.clustering.group.Node;

import com.avnet.emasia.webquote.cache.ActionIndicator;
 


@WebServlet(name = "CacheSynCommandClusterService", urlPatterns = {"/CacheSynCommandClusterService"})
public class CacheSynCommandClusterService extends HttpServlet {
	/**
	 * 
	 */
	private static final long serialVersionUID = 6892777931070066359L;
	static final Logger logger = Logger.getLogger(CacheSynCommandClusterService.class.getName());
	//REFERECE URL 
	//http://www.mastertheboss.com/jboss-server/jboss-cluster/dispatching-commands-on-wildfly-cluster
    @Resource(lookup = "java:jboss/clustering/group/web")
    private Group channelGroup;
    //jboss-deployment-structure
    @Resource(lookup = "java:jboss/clustering/dispatcher/web")
    private CommandDispatcherFactory factory;
 
    
    //private final Command<String, Node> command = new CacheSynchronizationCommand();
  
    @EJB
    CommandDispatcherBean ejb;
 
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        PrintWriter out = response.getWriter();
        String actionStr = request.getParameter(getParamKey());
        ActionIndicator actionIndicator = null;
        if(ActionIndicator.hasValue(actionStr)) {
        	actionIndicator = ActionIndicator.valueOf(actionStr);
        } else {
        	logger.log(Level.WARNING, MessageFormat.format("parameter [{0}] can not be inited to ActionIndicator", actionStr));
        	return;
        }
        try {
            //List<Node> nodes = channelGroup.getNodes();
            Map<Node, CommandResponse<String>> map = ejb.executeOnCluster(new CacheSynchronizationCommand(actionIndicator));
            for (Map.Entry<Node, CommandResponse<String>> entry : map.entrySet()) {
            	logger.log(Level.INFO, entry.getValue() + " executed on " + entry.getKey());
                out.println(entry.getValue() + " executed on " + entry.getKey());
            }
 
        } catch (Exception ex) {
        	logger.log(Level.SEVERE, null, ex);
        } finally {
        	 out.close();
        }
 
       
    }
 
  
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }
 
  
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }
    
    
    public static void doClearExRatesCache() {
    	doCallBackService(ActionIndicator.CLEAREXRATES);
    }
    
    public static void doClearAllCache() {
    	doCallBackService(ActionIndicator.CLEARALL);
    }
    
    
    
    private static void doCallBackService(ActionIndicator actionIndicator) {
    	//REFERECE URL 
    	//http://www.mastertheboss.com/jboss-server/jboss-cluster/dispatching-commands-on-wildfly-cluster
		try {
			String s = getCallBackJs(actionIndicator);
			RequestContext.getCurrentInstance().execute(s);
		} catch (Exception e) {
			logger.severe("Error in doCallBackService(), jq :" + getCallBackJs(actionIndicator) +"\n" + e.getMessage() + e.getStackTrace());
		}
    }
    
    public static String getCallBackJs(ActionIndicator actionIndicator) {
	    WebServlet anotation = CacheSynCommandClusterService.class.getAnnotation(WebServlet.class);
   		String url = anotation.urlPatterns()[0];
		FacesContext fContext = FacesContext.getCurrentInstance();
		ExternalContext extContext = fContext.getExternalContext();
		String fullPathUrl =extContext.getRequestContextPath() + url;
		String fullPathUrlWithParam =fullPathUrl + "?" + getParamKey() + "=" + actionIndicator.toString();
		String s = "$.post('"+fullPathUrlWithParam+"')";
		logger.info("actionIndicator :::" + actionIndicator.toString() + "  ,JQuery::" + s);
		return s;
   }
   
   private static String getParamKey() {
	   return  ActionIndicator.class.getSimpleName();
   }
}
