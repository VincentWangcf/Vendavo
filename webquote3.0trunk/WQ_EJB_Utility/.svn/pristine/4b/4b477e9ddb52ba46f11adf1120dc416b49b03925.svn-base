package com.avnet.emasia.webquote.utilities.schedule;

import java.util.logging.Logger;

import javax.naming.InitialContext;

import org.jboss.as.server.CurrentServiceContainer;
import org.jboss.msc.service.ServiceController;
import org.jboss.msc.service.ServiceController.State;
import org.jgroups.JChannel;
import org.jgroups.Message;

import com.avnet.emasia.webquote.exception.CommonConstants;
import com.avnet.emasia.webquote.exception.WebQuoteException;
import com.avnet.emasia.webquote.utilities.common.SysConfigSB;

public class ClusterTimerService {
	private static final Logger LOGGER = Logger.getLogger(ClusterTimerService.class.getSimpleName());

	public static void updateTaskService() throws WebQuoteException {
		try {
			ServiceController<?> service = CurrentServiceContainer.getServiceContainer()
					.getService(HATimerService.SINGLETON_SERVICE_NAME);
			if (service == null || !State.UP.equals(service.getState()) || service.getValue() == null) {
				String data = (service == null ? "service == null" : service.getState() + "service.getValue()");
				throw new WebQuoteException(CommonConstants.COMMON_UPDATE_TASKSERVICE_NOT_INSTALLEDSERVICE,new Object[]{data});
			}
			LOGGER.info("Service value : " + service.getValue());
			// if
			// (service.getValue().toString().equals(System.getProperty(HATimerService.JBOSS_NODE_NAME)))
			// {
			// InitialContext ic = new InitialContext();
			// SchedulerSB scheduler = (SchedulerSB)
			// ic.lookup(HATimerService.JNDI_SCHEDULERSB);
			// scheduler.restartTimers();
			// LOGGER.info("Update Task: timer restarted. the HA singleton
			// service on current node|" + service.getValue().toString());
			// } else {
			InitialContext ic = new InitialContext();
			SysConfigSB sysconfigSB = (SysConfigSB) ic.lookup(HATimerService.getSysConfigSBJndi());
			JChannel channel = new JChannel();
			String clusterName = sysconfigSB.getProperyValue(HATimerService.CLUSTER_NAME);
			channel.connect(clusterName);
			// eventLoop()
			channel.send(new Message(null, null, service.getValue()));
			channel.close();
			LOGGER.info("Update Task: cluster message has sent to: " + clusterName);
			// }
		} catch (Exception ex) {
			throw new WebQuoteException(ex);
		}
	}
}
