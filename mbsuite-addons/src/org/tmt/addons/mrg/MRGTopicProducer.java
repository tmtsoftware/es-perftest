package org.tmt.addons.mrg;

import java.util.Date;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.DeliveryMode;
import javax.jms.JMSException;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.jms.Topic;
import javax.naming.Context;
import javax.naming.InitialContext;

import org.apache.log4j.Logger;

import com.persistent.bcsuite.base.PublisherBase;

/**
 * Producer class that publishes messages to the receiver.
 * 
 */
public class MRGTopicProducer extends PublisherBase {
	Properties properties;
	ConnectionFactory connectionFactory;
	Context context;
	Connection connection = null;
	Session session;
	MessageProducer messageProducer;
	private long msgSent = 0;
	private String topicName = null;
	private static Logger logger = Logger.getLogger(MRGTopicProducer.class);
	private Topic topic;
	private String brokerHost;
	private String brokerPort;
	private int throttlingFactor = 1000;

	/**
	 * Initialize all the objects for connection, session, producers and
	 * consumers.
	 * 
	 */
	@Override
	public void init(Map<String, String> attributes) {
		super.init(attributes);
		topicName = attributes.get("topic");
		brokerHost = attributes.get("brokerhost");
		brokerPort = attributes.get("brokerport");
		String brokerURL = "'tcp://" + brokerHost + ":" + brokerPort + "'";
		logger.debug("Using Broker " + brokerURL);
		logger.info("Using topic as [" + topicName + "]");
		String tf = attributes.get("throttlingFactor");

		try {
			throttlingFactor = Integer.parseInt(tf);
			logger.debug("Throttling factor " + throttlingFactor);
		} catch (Exception e) {
			logger.error("Cannot parse throttling factor.. setting default as 1000");
			throttlingFactor = 1000;
		}
		try {
			properties = new Properties();
			properties.put(Context.INITIAL_CONTEXT_FACTORY,
					"org.apache.qpid.jndi.PropertiesFileInitialContextFactory");
			properties.put("connectionfactory.qpidconnectionfactory",
					"amqp://guest:guest@clientid/test?brokerlist=" + brokerURL);
			properties.put("topic." + topicName, "amq.topic");

			context = new InitialContext(properties);

			connectionFactory = (ConnectionFactory) context
					.lookup("qpidconnectionfactory");
			connection = connectionFactory.createConnection();
			connection.start();

			session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

			logger.info("Created publisher for Topic : " + topicName);
			topic = session.createTopic(topicName);
			messageProducer = session.createProducer(topic);

			if (messageProducer == null) {
				System.out.println("Error Creating producer");
				return;
			}
			messageProducer.setDeliveryMode(DeliveryMode.NON_PERSISTENT);

		} catch (JMSException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Checks whether all the consumers for the producer are matched.
	 */
	@Override
	public boolean isReadyToRun() {
		return true;
	}

	/**
	 * Sends the message to the receiver.
	 */
	@Override
	public void sendMessageForThroughput(byte[] message) {
		setStartTime(new Date());
		try {
			TextMessage strMsg = session.createTextMessage(new String(message));
			if (throttlingFactor > 0) {// For throttled output send 1
										// message/millisecond
				while (canContinue()) {
					long beforeSend = System.currentTimeMillis();
					for (int i = 0; i < throttlingFactor && canContinue(); i++) {
						messageProducer.send(strMsg);
						addToStatisticsPool(getMessageLength());
						msgSent++;
					}
					long afterSend = System.currentTimeMillis();
					long timeLeftInMillis = 100 - (afterSend - beforeSend);

					if (timeLeftInMillis > 0) {
						TimeUnit.MILLISECONDS.sleep(timeLeftInMillis);
					}
				}

			} else {
				while (canContinue()) {
					messageProducer.send(strMsg);
					addToStatisticsPool(getMessageLength());
					msgSent++;

				}
			}
		} catch (Exception exp) {
			exp.printStackTrace();
		}
		setEndTime(new Date());
		logger.info("Producer Sleeping for 5 seconds");
		try {
			Thread.sleep(5000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		markTaskComplete();

	}

	@Override
	public void sendMessageForLatency(byte[] message) {

	}

	/**
	 * Clean up all the producer, session and connection objects created.
	 */
	public void cleanup() {
		if (connection != null) {
			try {
				messageProducer.close();
				session.close();
				connection.close();
				context.close();
			} catch (Exception e) {
			}
		}
		logger.info("Total Message Sent = [" + msgSent + "]");
	}

}
