package com.avnet.emasia.webquote.dp;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.util.Hashtable;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Startup;
import javax.ejb.Stateless;
import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.jms.Topic;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import org.apache.commons.io.FileUtils;

import com.avnet.emasia.webquote.dp.xml.responsequote.ObjectFactory;
import com.avnet.emasia.webquote.dp.xml.responsequote.ResponseQuoteType;
import com.avnet.emasia.webquote.utilities.common.SysConfigSB;

/**
 * Session Bean implementation class DpSendMessageSB
 */
//@Stateless
//@LocalBean
//@javax.ejb.Singleton
//@Startup
public class DpSendMessageSB {
static final Logger LOG= Logger.getLogger(DpSendMessageSB.class.getSimpleName());
	
	@EJB
	private SysConfigSB sysConfigSB;
	
//	@Resource(lookup = "java:/jms/broker")
	private static ConnectionFactory connectionFactory;
	
	private Destination destination; 
	
    public DpSendMessageSB() {
		
    }

    
//    @PostConstruct
    public void init(){
		
        Hashtable<String, String> properties = new Hashtable<String,String>();
        properties.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
        properties.put(Context.PROVIDER_URL, "ldap://ldap-ent-dev1.avnet.com:389/ou=jms-v8,ou=webMethods,ou=apps,dc=avnet,dc=com");        
        properties.put(Context.SECURITY_PRINCIPAL, "uid=emwcsdx,ou=wcs,ou=apps,DC=AVNET,DC=COM");
        properties.put(Context.SECURITY_AUTHENTICATION, "simple");
        properties.put(Context.SECURITY_CREDENTIALS, "$Wcsmid02");
        properties.put("com.webmethods.jms.naming.clientgroup", "admin");
        
        try {
        	
        	
            Context context = new InitialContext(properties);
            Object o = context.lookup("cn=ClusterQueueConnectionFactory");
            connectionFactory = (ConnectionFactory)context.lookup("cn=ClusterTopicConnectionFactory");
            Connection conn = connectionFactory.createConnection();
            Topic topic = (Topic)context.lookup("EmAsiaOutboundQuoteTopic");
            Session session = conn.createSession(false, Session.AUTO_ACKNOWLEDGE);
            MessageProducer producer = session.createProducer(topic);
            conn.start();
            TextMessage message = session.createTextMessage("This is an order");
            producer.send(message);

            
        } catch (Exception ex) {
        	LOG.log(Level.SEVERE, "Exception in initializing the send message sb : "+ex.getMessage(), ex);
        }
    	
/*    	
    	try {
			Connection conn = connectionFactory.createConnection();
		} catch (JMSException e) {
			// TODO Auto-generated catch block
		}*/
//        Topic topic = (Topic)context.lookup("EmAsiaOutboundQuoteTopic");
		System.out.println("XXX");
    }
    
   
    
    public void sendOutMessage(String s) throws IOException {
    	writeXmlToDisk(s);
    }
    
    private void writeXmlToDisk(String s) throws IOException{
		String outboundPath = sysConfigSB.getProperyValue("DP_OUTBOUND_PATH");
		String fileName  = sysConfigSB.getProperyValue("DP_OUTBOUND_FILE_NAME") + System.currentTimeMillis() + ".xml";
		File file = new File(outboundPath + File.separator + fileName);
		FileUtils.write(file, s, "utf-8");
	}
    

}
