package com.avnet.emasia.webquote.utilities.schedule;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.jgroups.JChannel;
import org.jgroups.Message;
import org.jgroups.ReceiverAdapter;
import org.jgroups.View;

import com.avnet.emasia.webquote.exception.WebQuoteException;
import com.avnet.emasia.webquote.utilities.common.SysConfigSB;

public class ClusterMessageReceiver extends ReceiverAdapter {

	private static final Logger LOGGER = Logger.getLogger(ClusterMessageReceiver.class.getSimpleName());
//	public static final String WEBQUOTE_CLUSTER_MESSAGE = "WEBQUOTE_CLUSTER_MESSAGE";
	private String nodeName;

	@Override
	public void viewAccepted(View newView) {
		LOGGER.info("** view: " + newView);
	}

	@Override
	public void receive(Message msg) {
		try {
			LOGGER.info(nodeName + " receive cluster message from server : " + msg.getSrc() + " node: " + msg.getObject());
			// is HA timer service node
			InitialContext ic = new InitialContext();
			SchedulerSB scheduler = (SchedulerSB) ic.lookup(HATimerService.getSchedulerSBJndi());
			if (msg.getObject().toString().equals(nodeName)) {
				scheduler.restartTimers();
				LOGGER.info(nodeName + " restartTimers : " + msg.getSrc() + " node: " + msg.getObject());
			} else {
				LOGGER.info("----HA Timer Service not on node: ------: " + nodeName + msg.getSrc() + ": " + msg.getObject());
				//update all node task
				scheduler.updateAllNodeTimer();
				LOGGER.info(nodeName + " updateAllNodeTimer : " + msg.getSrc() + " node: " + msg.getObject());

			}		
			
		} catch (Exception e) {
			LOGGER.log(Level.SEVERE, "----receive message error------: " + nodeName + " receive message: " + msg.getSrc() + ": " + msg.getObject()+" , Exception message : "+e.getMessage(),e);
		}
	}

	/**
	 * test method
	 * @throws WebQuoteException 
	 */
	public void start() throws WebQuoteException {
		InitialContext ic;
		try {
			ic = new InitialContext();
		SysConfigSB sysconfigSB = (SysConfigSB) ic.lookup(HATimerService.getSysConfigSBJndi());
		String clusterName = sysconfigSB.getProperyValue(HATimerService.CLUSTER_NAME);
		LOGGER.info("----connect to cluster name ------: " + clusterName);
//		String props="UDP(mcast_addr=228.1.2.3;mcast_port=45566;ip_ttl=32):" +  
//		        "PING(timeout=3000;num_initial_members=6):" +  
//		        "FD(timeout=5000):" +  
//		        "VERIFY_SUSPECT(timeout=1500):" +  
//		        "pbcast.STABLE(desired_avg_gossip=10000):" +  
//		        "pbcast.NAKACK(gc_lag=10;retransmit_timeout=3000):" +  
//		        "UNICAST(timeout=5000;min_wait_time=2000):" +  
//		        "FRAG:" +  
//		        "pbcast.GMS(initial_mbrs_timeout=4000;join_timeout=5000;" +  
//		        "join_retry_timeout=2000;shun=false;print_local_addr=false)";  
		String props="UDP(mcast_addr=228.1.2.3;mcast_port=45566;ip_ttl=32):" +  
				"PING(timeout=3000;num_initial_members=6):" +  
				"FD(timeout=5000):" +  
				"VERIFY_SUSPECT(timeout=1500):" +  
				"pbcast.STABLE(desired_avg_gossip=10000):" +  
				"pbcast.NAKACK(gc_lag=10;retransmit_timeout=3000):" +  
				"UNICAST(timeout=5000;min_wait_time=2000):" +  
				"FRAG:" +  
				"pbcast.GMS(initial_mbrs_timeout=4000;join_timeout=5000;" +  
				"join_retry_timeout=2000;shun=false;print_local_addr=false)";  
		nodeName = System.getProperty(HATimerService.JBOSS_NODE_NAME);
		JChannel channel = new JChannel();
		channel.setReceiver(this);
		channel.connect(clusterName);
		} catch (Exception e) {
			throw new WebQuoteException(e);
		}
	}
}
