package com.avnet.emasia.webquote.web.quote.job;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

import javax.ejb.Asynchronous;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.Timer;
import javax.faces.bean.ManagedProperty;

import org.jboss.as.server.CurrentServiceContainer;
import org.jboss.ejb3.annotation.TransactionTimeout;
import org.jboss.logmanager.Level;
import org.jboss.msc.service.ServiceController;

import com.avnet.emasia.webquote.dp.EJBCommonSB;
import com.avnet.emasia.webquote.exception.AppException;
import com.avnet.emasia.webquote.exception.CommonConstants;
import com.avnet.emasia.webquote.exception.WebQuoteRuntimeException;
import com.avnet.emasia.webquote.quote.ejb.ManufacturerSB;
import com.avnet.emasia.webquote.quote.ejb.SAPWebServiceSB;
import com.avnet.emasia.webquote.utilities.MessageFormatorUtil;
import com.avnet.emasia.webquote.utilities.schedule.IScheduleTask;
import com.avnet.emasia.webquote.webservice.zwqrmtosap.ZquoteBlklst;

@Stateless
public class DailySendMFRAndRegiontoSapTask implements IScheduleTask {

	private static final Logger LOG = Logger.getLogger(DailySendMFRAndRegiontoSapTask.class.getName());
	@EJB
	private ManufacturerSB manufacturerSB;
	@EJB
	private SAPWebServiceSB sapWebServiceSB;
	
	@EJB
	private EJBCommonSB ejbCommonSB;

	@Override
	@Asynchronous
	@TransactionTimeout(value = 20000, unit = TimeUnit.SECONDS)
	public void executeTask(Timer timer) {
		LOG.info("Daily send the mapping of MFR and region to SAP job beginning...");
		try {
					//the webserive to send the mapping of MFR and region to SAP.The webservice will be triggered to send the SAP Daily.
					List<ZquoteBlklst> list = manufacturerSB.findAllManufacturerMappingForWebservice();
					sapWebServiceSB.sendMfrAndRegion2SAP(list);

		} catch (Exception e) {
			LOG.log(Level.SEVERE, "Exception in executing timer service , timer: "+timer.getInfo().toString()+", exception message : "+e.getMessage(), e);
		}

		LOG.info("Daily send the mapping of MFR and region to SAP job ended");
	}
	
	

}
