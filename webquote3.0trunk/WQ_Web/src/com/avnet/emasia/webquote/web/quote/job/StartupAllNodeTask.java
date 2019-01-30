package com.avnet.emasia.webquote.web.quote.job;

import javax.annotation.PostConstruct;
import javax.ejb.Startup;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import com.avnet.emasia.webquote.utilities.schedule.HATimerService;
import com.avnet.emasia.webquote.utilities.schedule.SchedulerSB;

import java.util.logging.Logger;

@Startup
public class StartupAllNodeTask {
	private static final Logger LOGGER = Logger
			.getLogger(StartupAllNodeTask.class.getName());

	@PostConstruct
	protected void startup() {
		LOGGER.info("StartupAllNodeTask will be initialized!");
		try {
			InitialContext ic = new InitialContext();
			SchedulerSB scheduler = (SchedulerSB) ic
					.lookup(HATimerService.getSchedulerSBJndi());
			scheduler.startAllNodeTimer();

		} catch (Exception e) {
			LOGGER.severe("StartupAllNodeTask init faild");
			
		}
	}

}
