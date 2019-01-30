//package com.avnet.emasia.webquote.reports.mdb;
//
//import java.util.logging.Logger;
//
//import javax.ejb.ActivationConfigProperty;
//import javax.ejb.MessageDriven;
//import javax.jms.JMSException;
//import javax.jms.MapMessage;
//import javax.jms.Message;
//import javax.jms.MessageListener;
//
// 
//import javax.naming.Context;
//import java.util.Hashtable;
//import javax.naming.InitialContext;
//
//import com.avnet.emasia.webquote.reports.ejb.job.OfflineRptJob;
//import com.avnet.emasia.webquote.reports.ejb.remote.OfflineRptRemote;
// 
///**
// * The MessageDriven annotation declares this class to be a message driven bean.
// * With EJB 3.0, you add a set of properties to the MDB via the
// * ActivationConfigProperty annotation to indicate how to configure this MDB. In
// * this case we set the destinationType to be a Queue (point to point) and the
// * destination to queue/MyQueue. These are the standard properties you'll need
// * to set on JMS-based, point-to-point (queue)-based MDB's.
// */
//@MessageDriven(activationConfig = {
//		@ActivationConfigProperty(
//		propertyName = "destinationType", propertyValue = "javax.jms.Queue"),
//		@ActivationConfigProperty(
//		propertyName = "destination", propertyValue = "java:jboss/exported/queue/offlineRptQueue") })
//public class OfflineRptMdb implements MessageListener {
//   
//	/**
//     * I can refer to other Session Beans as demonstrated here.
//     */
//   /* @EJB
//    private AccountInventory bean;*/
//	private static final Logger LOG =Logger.getLogger(OfflineRptMdb.class.getName());
//    /**
//     * When a message comes in, my onMessage is executed by the container. In
//     * this case, I expect a MapMessage (this of this as a hash table). The
//     * implementation is simple, grab the information out of the map and send a
//     * message to a Stateless Session Bean (AccountInventoryBean) to add a
//     * charge.
//     */
//    public void onMessage(final Message message) 
//    {
//        final MapMessage mm = (MapMessage) message;
//        LOG.info("call onMessage");
//        try 
//        {
//            final long requestId = mm.getLong("requestId");
//            int returnResult = invokeOnBean(requestId);
//            LOG.info("result:"+returnResult);
//
//        } 
//        catch (JMSException e) 
//        {
//            throw new RuntimeException(
//                    "Problem getting properties out of message");
//        }
//    }
//    
//    public int invokeOnBean(long requestId) 
//    {        
//    	LOG.info("invokeOnBean requestId:"+requestId);
//    	int returnInt =0 ;
//    	try 
//    	{            
//    		final Hashtable props = new Hashtable();                       
//    		props.put(Context.URL_PKG_PREFIXES, "org.jboss.ejb.client.naming");                      
//    		final Context context = new javax.naming.InitialContext(props);             
//    		final String appName = "WebQuoteRptEar";
//    		final String moduleName = "WQ_EJB_Reports";
//    		final String distinctName = "";
//    		final String beanName = OfflineRptJob.class.getSimpleName();
//    		final String viewClassName = OfflineRptRemote.class.getName();
//    		System.out.println("Looking EJB via JNDI ");
//    		System.out.println("ejb:" + appName + "/" + moduleName + "/" + distinctName + "/" + beanName + "!" + viewClassName);
//    		final OfflineRptRemote bean = (OfflineRptRemote) context.lookup("ejb:" + appName + "/" + moduleName + "/" + distinctName + "/" + beanName + "!" + viewClassName);
//    		returnInt = bean.executeTask(requestId);
//    		System.out.println("Calll invokeOfflineRptEJB end");    
//    		return returnInt;
//		} 
//    	catch (Exception e) 
//		{            
//			throw new RuntimeException(e);        
//	    }
//	} 
//    
//    
//
//}