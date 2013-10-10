package org.tmt.addons.hornetq.throughput;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import org.apache.log4j.Logger;
import org.hornetq.api.core.HornetQException;
import org.hornetq.api.core.TransportConfiguration;
import org.hornetq.api.core.client.ClientConsumer;
import org.hornetq.api.core.client.ClientMessage;
import org.hornetq.api.core.client.ClientSession;
import org.hornetq.api.core.client.ClientSessionFactory;
import org.hornetq.api.core.client.HornetQClient;
import org.hornetq.api.core.client.ServerLocator;
import org.hornetq.core.remoting.impl.netty.NettyConnectorFactory;
import org.hornetq.core.remoting.impl.netty.TransportConstants;
import com.persistent.bcsuite.base.SubscriberBase;

public class HornetqSubscriber extends SubscriberBase {
	private static final Logger logger = Logger
			.getLogger(HornetqSubscriber.class.getName());
	private String queueName = "Q";
	private String address;
	private String usePort;
	private String serverHost;
	private String serverPort;
	private String tcpBuffer;
	private String tcpNoDelay;
	private String useNio;
	private ClientSessionFactory factory;
	private ClientConsumer consumer;
	private ClientSession session;
	private ClientMessage msg = null;
	private String preAck;
	private ServerLocator serverLocator;
	private static int counter = 0;
	private static int sPort;
	Map<String, Object> params = new HashMap<String, Object>();

	@Override
	public void init(Map<String, String> attributes) {
		logger.info("HornetqSubscriber Initializing");
		/*
		 * In case of Hornetq it needs Address-Queue pair in order to publish
		 * message on address and receive message from queue, where as queue is
		 * bind to an address.Here address is used as a topic.
		 */
		address = attributes.get("topic");
		logger.info("Topic Name from suite [" + address + "]");

		/*
		 * serverHost is the ip-address & serverPort is the port for Hornetq
		 * Subscriber to connect, if usePort is "true" then for multi pub-sub
		 * mode all Subscribers are going to connect to different port & if it
		 * is "false" then all threads are connected to single port.
		 */

		serverHost = attributes.get("host-ip");
		serverPort = attributes.get("host-port");
		usePort = attributes.get("use-port");
		if (usePort.equalsIgnoreCase("true")) {
			sPort = Integer.parseInt(serverPort);
			serverPort = String.valueOf(sPort + counter);
		}

		// All of bellowing parameters are useful for Subscriber performance
		// tuning.
		tcpBuffer = attributes.get("tcp-buffer");
		tcpNoDelay = attributes.get("tcp-no-delay");
		useNio = attributes.get("use-nio");
		preAck = attributes.get("pre-ack");

		// After setting of all parameters they are added in Map "params".
		params.put(TransportConstants.TCP_NODELAY_PROPNAME, tcpNoDelay);
		params.put(TransportConstants.TCP_SENDBUFFER_SIZE_PROPNAME, tcpBuffer);
		params.put(TransportConstants.TCP_RECEIVEBUFFER_SIZE_PROPNAME,
				tcpBuffer);
		params.put(TransportConstants.USE_NIO_PROP_NAME, useNio);
		logger.info("Subscriber connecting to server at IP [" + serverHost
				+ "] and port [" + serverPort + "]");
		params.put(TransportConstants.HOST_PROP_NAME, serverHost);
		params.put(TransportConstants.PORT_PROP_NAME, serverPort);

		/*
		 * Configure the Map with Hornetq server TransportConfiguration and set
		 * it using serverLocator.
		 */

		serverLocator = HornetQClient
				.createServerLocatorWithoutHA(new TransportConfiguration(
						NettyConnectorFactory.class.getName(), params));
		serverLocator.setPreAcknowledge(Boolean.parseBoolean(preAck));
		serverLocator.setConsumerWindowSize(-1);

		/*
		 * Here consumer is created with queue & it is bind to and address/topic
		 * for message receiving.
		 */

		try {
			factory = serverLocator.createSessionFactory();
			session = factory.createSession();
			String s = queueName + "_" + UUID.randomUUID().toString();
			logger.info("Created random queue = " + s);
			session.createTemporaryQueue(address, s);
			consumer = session.createConsumer(s);
		} catch (Exception e) {
			logger.info("init() Exception");
			e.printStackTrace();
		}
		logger.info("HornetqSubscriber inited");
		counter++;
	}

	/**
	 * Reads messages from an queue which is binded to an address/topic.
	 */
	@Override
	public void read() {
		// Send the message
		// Return back the messageId of the message
		// Dont do any other logic in this method.
		try {
			session.start();
			logger.info("READY!!!");
			while (!consumer.isClosed()) {
				msg = consumer.receive();
				if (msg == null) {
					continue;
				}
				byte[] byteMessage = msg.getBytesProperty("prop");
				String message = new String(byteMessage);
				Date recdOn = new Date();
				addToStatisticsPool(null, message.length(), recdOn);
			}
		} catch (HornetQException e) {
			// TODO Auto-generated catch block
			logger.info("read() Exception");
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			markTaskComplete();
		}
	}

	/**
	 * Close consumer,session,factory & serverLocator
	 */
	@Override
	public void shutdown() {
		try {
			counter--;
			consumer.close();
			if (counter == 0) {
				session.close();
				factory.close();
				serverLocator.close();
			}
			logger.info("Subscriber closed");
		} catch (Exception e) {
			// TODO: handle exception
			logger.info("shutdown() Exception");
			e.printStackTrace();
		}
	}

}
