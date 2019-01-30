package com.avnet.emasia.webquote.cluster.dispatcher;

import javax.ejb.Startup;
import javax.annotation.Resource;

import javax.ejb.Singleton;

 
import org.wildfly.clustering.dispatcher.CommandDispatcher;
import org.wildfly.clustering.dispatcher.CommandDispatcherFactory;
import org.wildfly.clustering.group.Group;

@Singleton
@Startup
public class CommandDispatcherFactoryBean implements CommandDispatcherFactory {

	@Resource(lookup = "java:jboss/clustering/dispatcher/web")
	private CommandDispatcherFactory factory;

	public <C> CommandDispatcher<C> createCommandDispatcher(Object service, C context) {
		//this.factory.getGroup().
		return this.factory.createCommandDispatcher(service, context);
	}

	@Override
	public Group getGroup() {
		return this.factory.getGroup();
	}
}
