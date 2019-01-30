package com.avnet.emasia.webquote.web.edi.job;

import java.util.logging.Logger;
import javax.ejb.Asynchronous;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.Timer;
import com.avnet.emasia.webquote.edi.TIInfoExchangeSB;
import com.avnet.emasia.webquote.utilities.schedule.IScheduleTask;

@Stateless
public class DeleteOverTimeDataTask implements IScheduleTask {

	private static final Logger LOG = Logger.getLogger(DeleteOverTimeDataTask.class.getName());
    @EJB
    private TIInfoExchangeSB tISB;
   
	@Override
	@Asynchronous   
	public void executeTask(Timer timer) {
		LOG.info("TI project DeleteOverTimeDataTask job beginning...");
		try{
			tISB.deleteOverTimeData();
		} catch (Exception e) {
			LOG.severe(e.toString());
		}
		LOG.info("TI project DeleteOverTimeDataTask job ended");
	}

	
	


}
