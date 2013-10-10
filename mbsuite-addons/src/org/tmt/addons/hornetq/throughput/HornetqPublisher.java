package org.tmt.addons.hornetq.throughput;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import org.apache.log4j.Logger;
import org.hornetq.api.core.HornetQException;
import org.hornetq.api.core.TransportConfiguration;
import org.hornetq.api.core.client.ClientMessage;
import org.hornetq.api.core.client.ClientProducer;
import org.hornetq.api.core.client.ClientSession;
import org.hornetq.api.core.client.ClientSessionFactory;
import org.hornetq.api.core.client.HornetQClient;
import org.hornetq.api.core.client.ServerLocator;
import org.hornetq.core.remoting.impl.netty.NettyConnectorFactory;
import org.hornetq.core.remoting.impl.netty.TransportConstants;
import com.persistent.bcsuite.base.PublisherBase;

public class HornetqPublisher extends PublisherBase {

	private static final Logger logger = Logger
			.getLogger(HornetqPublisher.class.getName());
	private String address;
	private String serverHost;
	private String serverPort;
	private String tcpBuffer;
	private String tcpNoDelay;
	private String useNio;
	private String producerRate;
	private ClientSessionFactory factory;
	private ClientProducer producer;
	private ClientSession session;
	private ClientMessage msg;
	private String preAck;
	private ServerLocator serverLocator;
	private static int counter = 0;
	private String usePort;
	private static int sPort;
	Map<String, Object> params = new HashMap<String, Object>();

	/**
	 * Initialize the Hornetq parameters for Publisher.
	 */
	@Override
	public void init(Map<String, String> attributes) {
		logger.info("HornetqPublisher Initializing");
		super.init(attributes);

		/*
		 * In case of Hornetq it needs Address-Queue pair in order to publish
		 * message on address and receive message from queue, where as queue is
		 * bind to an address.Here address is used as a topic.
		 */

		address = attributes.get("topic");
		logger.info("Topic Name from suite [" + address + "]");
		/*
		 * serverHost is the ip-address & serverPort is the port for Hornetq
		 * Publisher to connect, if usePort is "true" then for multi pub-sub
		 * mode all Publishers are going to connect to different port & if it is
		 * "false" then all threads are connected to single port.
		 */
		serverHost = attributes.get("host-ip");
		serverPort = attributes.get("host-port");
		usePort = attributes.get("use-port");
		if (usePort.equalsIgnoreCase("true")) {
			sPort = Integer.parseInt(serverPort);
			serverPort = String.valueOf(sPort + counter);
		}

		// All of bellowing parameters are useful for Publisher performance
		// tuning.
		tcpBuffer = attributes.get("tcp-buffer");
		tcpNoDelay = attributes.get("tcp-no-delay");
		preAck = attributes.get("pre-ack");
		useNio = attributes.get("use-nio");
		producerRate = attributes.get("producer-rate");

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
		serverLocator.setUseGlobalPools(false);
		serverLocator.setScheduledThreadPoolMaxSize(24);
		serverLocator.setThreadPoolMaxSize(-1);
		serverLocator.setPreAcknowledge(Boolean.parseBoolean(preAck));
		serverLocator.setProducerMaxRate(Integer.parseInt(producerRate));
		serverLocator.setProducerWindowSize(31457280);
		serverLocator.setConfirmationWindowSize(1310720);

		// serverLocator.setProducerWindowSize(31457280);
		// serverLocator.setConfirmationWindowSize(1310720);

		initializeMessagePlatform();
		logger.info("Sample Publisher initialization complete...");
	}

	/**
	 * Create a factory,session & producer.
	 */

	private void initializeMessagePlatform() {
		/*
		 * Here producer is created using session & it is bind to and
		 * address/topic for message sending.
		 */
		try {

			factory = serverLocator.createSessionFactory();
			session = factory.createSession();
			producer = session.createProducer(address);
		} catch (HornetQException e) {
			// TODO Auto-generated catch block
			logger.info("init() Exception");
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		logger.info("HornetqPublisher inited");
		counter++;
	}

	@Override
	public boolean isReadyToRun() {
		return true;
	}

	/**
	 * Sends messages to an address/topic.
	 */
	@Override
	public void sendMessageForThroughput(byte[] message) {
		// TODO Auto-generated method stub
		logger.info("sendMessageForThroughput started.");
		setStartTime(new Date());
		try {
			while (canContinue) {
				// send messages till the suite does not tell you to shutdown
				// Sends a byte[] message using byteProperty
				msg = session.createMessage(false);
				msg.putBytesProperty("prop", message);
				producer.send(msg);
				// add data to statistics pool. This will count the number of
				// messages and the total bytes sent.
				addToStatisticsPool(message.length);
				// In case you need to consider think time consider it like
				// below
				if (getThinkTimeInMillis() > 0)
					Thread.sleep(getThinkTimeInMillis());
			}
		} catch (Exception e) {
			logger.error("Exception encountered in Publisher program");
			e.printStackTrace();
		} finally {
			markTaskComplete(); // IMPORTANT !!
			setEndTime(new Date()); // IMPORTANT !!
			logger.info("sendMessageForThroughput completed.");
		}
	}

	@Override
	public void sendMessageForLatency(byte[] message) {
	}

	/**
	 * Close producer,session,factory & serverLocator
	 */
	@Override
	public void cleanup() {
		try {
			counter--;
			producer.close();
			if (counter == 0) {
				session.close();
				factory.close();
				serverLocator.close();
			}
			logger.info("Publisher closed");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.info("cleanup() Exception");
			e.printStackTrace();
		}

	}

}
