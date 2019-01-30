package com.avnet.emasia.webquote.web.quote.job;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Logger;

import org.jboss.as.server.ServerEnvironment;
import org.jboss.msc.inject.Injector;
import org.jboss.msc.service.Service;
import org.jboss.msc.service.ServiceName;
import org.jboss.msc.service.StartContext;
import org.jboss.msc.service.StartException;
import org.jboss.msc.service.StopContext;
import org.jboss.msc.value.InjectedValue;

import com.avnet.emasia.webquote.exception.CommonConstants;
import com.avnet.emasia.webquote.utilities.MessageFormatorUtil;
import com.avnet.emasia.webquote.utilities.schedule.HATimerService;

/**
 * TODO:removed this class�� 041863
 *
 */
public class EnvironmentService implements Service<String>{
	
	private static final Logger LOGGER = Logger.getLogger(EnvironmentService.class
			.getName());
	
//    public static final ServiceName SINGLETON_SERVICE_NAME = ServiceName.JBOSS.append("quickstart", "fh-webquote-prod", "singleton");
    public static final ServiceName SINGLETON_SERVICE_NAME = HATimerService.SINGLETON_SERVICE_NAME;

    
	/**
     * A flag whether the service is started.
     */
    private final AtomicBoolean started = new AtomicBoolean(false);

    private String nodeName;

    private final InjectedValue<ServerEnvironment> env = new InjectedValue<ServerEnvironment>();

    public Injector<ServerEnvironment> getEnvInjector() {
        return this.env;
    }


	public EnvironmentService() {
		// TODO Auto-generated constructor stub
	}


	 /**
     * @return the name of the server node
     */
    public String getValue() throws IllegalStateException, IllegalArgumentException {
        if (!started.get()) {
            throw new IllegalStateException("The service '"+this.getClass().getName()+"' is not ready!");
        }
        return this.nodeName;
    }

    public void start(StartContext arg0) throws StartException {
        if (!started.compareAndSet(false, true)) {
            throw new StartException("The service is still started!");
        }
        LOGGER.info("Start service '" + this.getClass().getName() + "'");
        this.nodeName = this.env.getValue().getNodeName();
    }


    public void stop(StopContext arg0) {
        if (!started.compareAndSet(true, false)) {
        	LOGGER.warning("The service '" + this.getClass().getName() + "' is not active!");
        } else {
        	LOGGER.info("Stop service '" + this.getClass().getName() + "'");
        }
    }

}
