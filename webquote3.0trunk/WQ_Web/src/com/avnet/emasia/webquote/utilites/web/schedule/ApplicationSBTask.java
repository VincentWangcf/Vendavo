package com.avnet.emasia.webquote.utilites.web.schedule;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ejb.Asynchronous;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.Timer;

import com.avnet.emasia.webquote.user.ejb.ApplicationSB;
import com.avnet.emasia.webquote.utilities.schedule.IScheduleTask;

/**
 * @author 906893
 * @createdOn 20130702
 */
@Stateless
public class ApplicationSBTask implements IScheduleTask{

	private static final Logger LOGGER = Logger.getLogger(ApplicationSBTask.class.getName());

	@EJB
	ApplicationSB applicationSB;

	@Override
	@Asynchronous
	public void executeTask(Timer timer) {
		LOGGER.info("ApplicationSBTask job beginning...");

		try {
			applicationSB.loadFunctionRole();
		} catch (Exception e) {
			LOGGER.log(Level.SEVERE, "Exception occured for timer:"+timer.getInfo().toString()+"ApplicationSBTask error:" + e.getMessage(), e);
		}

		LOGGER.info("ApplicationSBTask job ended");
	}

}
