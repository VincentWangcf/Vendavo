package com.avnet.emasia.webquote.cluster.dispatcher;

import java.util.Map;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.ejb.EJB;
  
import javax.ejb.Singleton;
import javax.ejb.Startup;
 
import org.wildfly.clustering.dispatcher.Command;
import org.wildfly.clustering.dispatcher.CommandDispatcher;
import org.wildfly.clustering.dispatcher.CommandDispatcherFactory;
import org.wildfly.clustering.dispatcher.CommandResponse;
import org.wildfly.clustering.group.Node;
 
@Singleton
@Startup
public class CommandDispatcherBean  {
    @EJB
    private CommandDispatcherFactory factory;
    private CommandDispatcher<Node> dispatcher;
 
    @PostConstruct
    public void init() {
        this.dispatcher = this.factory.createCommandDispatcher("CommandDispatcher", this.factory.getGroup().getLocalNode());
    }
 
    @PreDestroy
    public void destroy() {
        this.close();
    }
 
    public <R> CommandResponse<R> executeOnNode(Command<R, Node> command, Node node) throws Exception {
        return this.dispatcher.executeOnNode(command, node);
    }
    
    public <R> Map<Node, CommandResponse<R>> executeOnCluster(Command<R, Node> command, Node... excludedNodes) throws Exception  {
        return this.dispatcher.executeOnCluster(command, excludedNodes);
    }
 
    public void close() {
        this.dispatcher.close();
    }
}
