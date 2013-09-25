package org.tmt.addons.mrg;

import java.util.Date;
import java.util.Map;
import java.util.Properties;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.jms.MessageConsumer;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.jms.Topic;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.apache.log4j.Logger;

import com.persistent.bcsuite.base.SubscriberBase;

/**
 * Consumer class that consumes/reads the messages sent by producer.
 * 
 */
public class MRGTopicReceiver extends SubscriberBase {
	Properties properties;
	private ConnectionFactory connectionFactory;
	private Context context;
	Connection connection = null;
	Session session;
	Topic topic;
	MessageConsumer messageConsumer;
	private long msgReceived = 0;
	private String topicName = null;
	private String brokerHost;
	private String brokerPort;
	private static Logger logger = Logger.getLogger(MRGTopicReceiver.class);

	/**
	 * Initialize all the objects for connection, session, producers and
	 * consumers.
	 * 
	 */
	@Override
	public void init(Map<String, String> attributes) {
		topicName = attributes.get("topic");
		brokerHost = attributes.get("brokerhost");
		brokerPort = attributes.get("brokerport");
		String brokerURL = "'tcp://" + brokerHost + ":" + brokerPort + "'";
		logger.debug("Using Broker " + brokerURL);
		logger.info("Using topic as [" + topicName + "]");
		properties = new Properties();
		properties.put(Context.INITIAL_CONTEXT_FACTORY,
				"org.apache.qpid.jndi.PropertiesFileInitialContextFactory");
		properties.put("connectionfactory.qpidconnectionfactory",
				"amqp://guest:guest@clientid/test?brokerlist=" + brokerURL);
		properties.put("topic." + topicName, "amq.topic");

		try {
			context = new InitialContext(properties);
			connectionFactory = (ConnectionFactory) context
					.lookup("qpidconnectionfactory");
			connection = connectionFactory.createConnection();
			connection.start();
			session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
			logger.debug("Created receiver for Topic : " + topicName);
			topic = session.createTopic(topicName);
			messageConsumer = session.createConsumer(topic);
		} catch (JMSException e) {
			e.printStackTrace();
		} catch (NamingException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Method for consuming the messages produced by Producer
	 */
	@Override
	public void read() {

		TextMessage message;
		try {
			message = session.createTextMessage();
			while (true) {
				message = (TextMessage) messageConsumer.receive();

				if (message != null) {
					addToStatisticsPool(null, message.getText().length(),
							new Date());
					msgReceived++;
				}
			}
		} catch (JMSException e) {// If consumer closed catch exception and do
									// nothing.
		}

	}

	/**
	 * Clean up all the consumer, session and connection objects created.
	 */
	@Override
	public void shutdown() {
		markTaskComplete();
		logger.info("Number of Messages recevied = [" + msgReceived + "]");
		if (connection != null) {
			try {
				messageConsumer.close();
				session.close();
				connection.close();
				context.close();
			} catch (NamingException e) {
				e.printStackTrace();
			} catch (JMSException e) {
				e.printStackTrace();
			}
		}
	}
}
