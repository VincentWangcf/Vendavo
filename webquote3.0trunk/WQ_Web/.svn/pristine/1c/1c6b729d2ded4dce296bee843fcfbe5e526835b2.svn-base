package com.avnet.emasia.webquote.ejb;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.Resource;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.JMSConnectionFactory;
import javax.jms.JMSContext;
import javax.jms.JMSDestinationDefinition;
import javax.jms.JMSException;
import javax.jms.Queue;
import javax.jms.Session;

import com.avnet.emasia.webquote.entity.QuoteItem;
import com.avnet.emasia.webquote.quote.ejb.SAPWebServiceSB;

@JMSDestinationDefinition(name = "java:/queue/CreateSapQuoteQueue", interfaceName = "javax.jms.Queue", destinationName = "CreateSapQuoteQueue")
@Stateless
@LocalBean
public class SapMesgProducerSB {
	private static final Logger LOG = Logger.getLogger(SAPWebServiceSB.class.getName());
	@Inject
	@JMSConnectionFactory("java:/ConnectionFactory")
	private JMSContext context;

	@Resource(lookup = "java:/queue/CreateSapQuoteQueue")
	private Queue queue;

	@Resource(lookup = "java:/ConnectionFactory")
	ConnectionFactory connectionFactory;

	public void sendQuoteToQueue(List<Long> quoteItems) {
		Connection connection = null;
		try {
			connection = connectionFactory.createConnection();
			Session session = connection.createSession();
			ArrayList<Long> list = new ArrayList<>();
			list.addAll(quoteItems);
			// context = connectionFactory.createContext();
			context.createProducer().send(queue, session.createObjectMessage(list));

		} catch (JMSException e2) {
			LOG.log(Level.SEVERE, "sendQuoteToQueue failed", e2);
		} finally {
			if (connection != null) {
				try {
					connection.close();
				} catch (JMSException e) {
					LOG.log(Level.SEVERE, " connection close failed", e);
				}
			}
		}

	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void groupListForSendQuoteToQueue(List<QuoteItem> quoteItems) {
		List<Long> qItems = new ArrayList();
		for (QuoteItem q : quoteItems) {
			qItems.add(q.getId());
		}
		int listSize = qItems.size();
		int toIndex = 100;
		Map map = new ConcurrentHashMap();
		int keyToken = 0;
		for (int i = 0; i < qItems.size(); i += 100) {
			if (i + 100 > listSize) {
				toIndex = listSize - i;
			}
			List<Long> newList = qItems.subList(i, i + toIndex);
			map.put("group" + keyToken, newList);
			keyToken++;
		}

		// The map to call senfQuoteToQueue
		for (Object object : map.keySet()) {

			List<Long> list = (List<Long>) map.get(object);
			sendQuoteToQueue(list);
			// for(QuoteItem q :list){
			// List<Long> qItems = new ArrayList();
			// qItems.add(q.getId());
			// sendQuoteToQueue(qItems);
			// }
			// qItems.clear();
			// sendQuoteToQueue(list);
		}
		// return map;
	}
}
