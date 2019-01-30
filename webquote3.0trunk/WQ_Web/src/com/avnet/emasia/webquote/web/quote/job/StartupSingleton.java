package com.avnet.emasia.webquote.web.quote.job;

import java.util.Collection;
import java.util.EnumSet;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.apache.poi.ss.formula.functions.T;
//import org.jboss.as.clustering.singleton.SingletonService;
import org.jboss.as.server.CurrentServiceContainer;
import org.jboss.logmanager.Level;
import org.jboss.msc.service.AbstractServiceListener;
import org.jboss.msc.service.ServiceController;
import org.jboss.msc.service.ServiceController.Transition;
import org.jboss.msc.service.ServiceListener;

import com.avnet.emasia.webquote.exception.WebQuoteRuntimeException;


/**
 * A Singleton EJB to create the SingletonService during startup.
 * 
 * @author <a href="mailto:wfink@redhat.com">Wolf-Dieter Fink</a>
 */
//@Singleton
//@Startup
public class StartupSingleton {
  private static final Logger LOGGER = Logger.getLogger(StartupSingleton.class
			.getName());
  /**
   * Create the Service and wait until it is started.<br/>
   * Will log a message if the service will not start in 10sec. 
   */
  @PostConstruct
  protected void startup() {
    LOGGER.info("StartupSingleton will be initialized!");

   /* EnvironmentService service = new EnvironmentService();
    SingletonService<String> singleton = new SingletonService<String>(service, EnvironmentService.SINGLETON_SERVICE_NAME);
    // if there is a node where the Singleton should deployed the election policy might set,
    // otherwise the JGroups coordinator will start it
    //singleton.setElectionPolicy(new PreferredSingletonElectionPolicy(new NamePreference("node2/cluster"), new SimpleSingletonElectionPolicy()));
    ServiceController<String> controller = singleton.build(CurrentServiceContainer.getServiceContainer())
        .addDependency(ServerEnvironmentService.SERVICE_NAME, ServerEnvironment.class, service.getEnvInjector())
        .install();

    controller.setMode(ServiceController.Mode.ACTIVE);*/
    try {
     // wait(controller, EnumSet.of(ServiceController.State.DOWN, ServiceController.State.STARTING), ServiceController.State.UP);
      LOGGER.info("StartupSingleton has started the Service");
    } catch (IllegalStateException e) {
      LOGGER.log(Level.WARNING,"Singleton Service "+EnvironmentService.SINGLETON_SERVICE_NAME+" not started, are you sure to start in a cluster (HA) environment?");
    }
  }

  /**
   * Remove the service during undeploy or shutdown
   */
  @PreDestroy
  protected void destroy() {
    LOGGER.info("StartupSingleton will be removed!");
    ServiceController<?> controller = CurrentServiceContainer.getServiceContainer().getRequiredService(EnvironmentService.SINGLETON_SERVICE_NAME);
    controller.setMode(ServiceController.Mode.REMOVE);
    try {
      wait(controller, EnumSet.of(ServiceController.State.UP, ServiceController.State.STOPPING, ServiceController.State.DOWN), ServiceController.State.REMOVED);
    } catch (IllegalStateException e) {
      LOGGER.log(Level.WARNING,"Singleton Service "+EnvironmentService.SINGLETON_SERVICE_NAME+" has not be stopped correctly!");
    }
  }

  private static <T> void wait(ServiceController<T> controller, Collection<ServiceController.State> expectedStates, ServiceController.State targetState) {
    if (controller.getState() != targetState) {
      ServiceListener<T> listener = new NotifyingServiceListener<T>();
      controller.addListener(listener);
      try {
        synchronized (controller) {
          int maxRetry = 2;
          while (expectedStates.contains(controller.getState()) && maxRetry > 0) {
            LOGGER.info("Service controller state is "+controller.getState()+", waiting for transition to "+targetState);
            controller.wait(5000);
            maxRetry--;
          }
        }
      } catch (InterruptedException e) {
        LOGGER.log(Level.WARNING,"Wait on startup is interrupted!");
        Thread.currentThread().interrupt();
      }
      controller.removeListener(listener);
      ServiceController.State state = controller.getState();
      LOGGER.info("Service controller state is now "+state);
      if (state != targetState) {
        throw new WebQuoteRuntimeException(String.format("Failed to wait for state to transition to %s.  Current state is %s", targetState, state), controller.getStartException());
      }
    }
  }

  private static class NotifyingServiceListener<T> extends AbstractServiceListener<T> {
    @Override
    public void transition(ServiceController<? extends T> controller, Transition transition) {
      synchronized (controller) {
        controller.notify();
      }
    }
  }
}
