package org.tmt.addons.mrg;

import java.util.Date;
import java.util.Map;
import java.util.Properties;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.DeliveryMode;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.MessageConsumer;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.jms.Topic;
import javax.jms.TopicConnection;
import javax.naming.Context;
import javax.naming.InitialContext;

import org.apache.log4j.Logger;

import com.persistent.bcsuite.base.PublisherBase;

/**
 * Class for measuring latency for each message. Contains a producer and
 * consumer and logs the time for each message to be sent to broker and received
 * back.
 * 
 */
public class MRGLatencyTopicPub extends PublisherBase {

	Properties pubProperties;
	Properties subProperties;
	ConnectionFactory pubConnectionFactory;
	ConnectionFactory subConnectionFactory;
	Context pubContext;
	Context subContext;
	Connection pubConnection = null;
	Connection subConnection = null;
	Session pubSession;
	Session subSession;
	Destination pubDestination;
	Destination subDestination;
	MessageProducer messageProducer;
	MessageConsumer messageConsumer;
	String topicToSend = null;
	private long msgSent = 0;
	private long msgRecvd = 0;
	TopicConnection topiconnection;
	Topic pubTopic;
	Topic subTopic;
	private String topicName;
	private String brokerPort;
	private String brokerHost;
	private String brokerURL;
	private int latencyCaptureWindow;
	private static Logger logger = Logger.getLogger(MRGLatencyTopicPub.class);

	/**
	 * Initialize all the objects for connection, session, producers and
	 * consumers.
	 * 
	 */
	@Override
	public void init(Map<String, String> attributes) {
		super.init(attributes);
		topicName = attributes.get("topic");
		topicToSend = topicName + "abc";
		brokerHost = attributes.get("brokerhost");
		brokerPort = attributes.get("brokerport");
		brokerURL = "'tcp://" + brokerHost + ":" + brokerPort + "'";
		String lc = attributes.get("latencyCaptureWindow");

		try {
			latencyCaptureWindow = Integer.parseInt(lc);
		} catch (Exception e) {
			logger.error("Cannot parse latencyCaptureWindow.. using default as 10");
			latencyCaptureWindow = 10;
		}

		logger.info("Using latencyCaptureWindow as [" + latencyCaptureWindow
				+ "]");
		initPub();
		initSub();
	}

	/**
	 * Initializes the publisher/producer.
	 */
	private void initPub() {
		try {
			pubProperties = new Properties();
			pubProperties.put(Context.INITIAL_CONTEXT_FACTORY,
					"org.apache.qpid.jndi.PropertiesFileInitialContextFactory");
			pubProperties.put("connectionfactory.qpidconnectionfactory",
					"amqp://guest:guest@clientid/test?brokerlist=" + brokerURL);
			pubProperties.put("topic." + topicToSend, "amq.topic");
			pubContext = new InitialContext(pubProperties);

			pubConnectionFactory = (ConnectionFactory) pubContext
					.lookup("qpidconnectionfactory");
			pubConnection = pubConnectionFactory.createConnection();
			pubConnection.start();

			pubSession = pubConnection.createSession(false,
					Session.AUTO_ACKNOWLEDGE);
			pubTopic = pubSession.createTopic(topicToSend);
			messageProducer = pubSession.createProducer(pubTopic);

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
	 * Initialize the consumer.
	 */
	private void initSub() {

		try {
			subProperties = new Properties();
			subProperties.put(Context.INITIAL_CONTEXT_FACTORY,
					"org.apache.qpid.jndi.PropertiesFileInitialContextFactory");
			subProperties.put("connectionfactory.qpidconnectionfactory",
					"amqp://guest:guest@clientid/test?brokerlist=" + brokerURL);
			subProperties.put("topic." + topicToSend, "amq.topic");
			subContext = new InitialContext(subProperties);

			subConnectionFactory = (ConnectionFactory) subContext
					.lookup("qpidconnectionfactory");
			subConnection = subConnectionFactory.createConnection();
			subConnection.start();

			subSession = subConnection.createSession(false,
					Session.AUTO_ACKNOWLEDGE);
			subTopic = subSession.createTopic(topicToSend);

			messageConsumer = subSession.createConsumer(pubTopic);
			if (messageConsumer == null) {
				System.out.println("Error Creating consumer");
				return;
			}
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

	@Override
	public void sendMessageForThroughput(byte[] message) {

	}

	/**
	 * Method to send message and consume the message. Calculated the
	 * latency/delay for each message.
	 */
	@Override
	public void sendMessageForLatency(byte[] message) {

		long sendTime = 0;
		long recvTime = 0;
		long totalTime = 0;
		setStartTime(new Date());
		int latencyCounter = 0;
		String strMsg = new String(message);
		int i = 0;
		try {
			while (canContinue()) {
				TextMessage msgToSend = pubSession.createTextMessage(strMsg);
				sendTime = System.nanoTime();
				messageProducer.send(msgToSend);
				addToStatisticsPool(getMessageLength());
				msgSent++;
				i++;

				TextMessage msgRead = null;
				msgRead = (TextMessage) messageConsumer.receive();
				if (msgRead != null) {
					recvTime = System.nanoTime();
					msgRecvd++;
					latencyCounter++;
					if (latencyCounter > latencyCaptureWindow) {
						totalTime = recvTime - sendTime;
						addLatencyStatistics(Long.toString(msgSent), totalTime);
						latencyCounter = 0;

					}
				}
			}

		} catch (Exception exp) {
			exp.printStackTrace();
		} finally {
			System.out.println("Marking task complete ");
			markTaskComplete();

		}
		setEndTime(new Date());
	}

	/**
	 * Clean up all the producer,consumer, session and connection objects
	 * created.
	 */
	public void cleanup() {

		if (subConnection != null) {
			try {
				subConnection.close();
				subContext.close();
			} catch (Exception e) {
			}
		}
		if (pubConnection != null) {
			try {
				pubConnection.close();
				pubContext.close();
			} catch (Exception e) {
			}
		}
		System.out.println("Total Message Sent = [" + msgSent + "]");
		System.out.println("Total Message Received = [" + msgRecvd + "]");
	}

}
