package org.tmt.addons.hornetq.latency;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import org.apache.log4j.Logger;
import org.hornetq.api.core.HornetQException;
import org.hornetq.api.core.TransportConfiguration;
import org.hornetq.api.core.client.ClientConsumer;
import org.hornetq.api.core.client.ClientMessage;
import org.hornetq.api.core.client.ClientProducer;
import org.hornetq.api.core.client.ClientSession;
import org.hornetq.api.core.client.ClientSessionFactory;
import org.hornetq.api.core.client.HornetQClient;
import org.hornetq.api.core.client.ServerLocator;
import org.hornetq.core.remoting.impl.netty.NettyConnectorFactory;
import org.hornetq.core.remoting.impl.netty.TransportConstants;
import com.persistent.bcsuite.base.PublisherBase;

public class HornetqLatencyTest extends PublisherBase {
	private static final Logger logger = Logger
			.getLogger(HornetqLatencyTest.class);
	private String serverHost;
	private String address;
	private String echoAddress;
	private String serverPort;
	private String tcpBuffer;
	private String tcpNoDelay;
	private ClientSessionFactory sendFactory;
	private ClientSessionFactory recvFactory;
	private ClientProducer producer;
	private ClientSession sendSession = null;
	private ClientSession recvSession = null;
	private ClientMessage msgSend;
	private ClientMessage msgRecv;
	private String queueName = "PQ";
	private ClientConsumer consumer;
	private String preAck;
	private int sendCnt = 0;
	private int recvCnt = 0;
	private String usePort;
	private static int sPort;
	private static int pubCounter = 0;
	private static int subCounter = 0;
	private String useNio;
	private String producerRate;
	private String latencyCaptureWindow;

	/**
	 * Initialize the Hornetq latency parameters for Publisher.
	 */
	@Override
	public void init(Map<String, String> attributes) {

		/*
		 * In case of Hornetq it needs Address-Queue pair in order to publish
		 * message on address and receive message from queue, where as queue is
		 * bind to an address.Here address is used as a topic.
		 */

		address = attributes.get("topic");
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
			serverPort = String.valueOf(sPort + pubCounter);
		}

		// All of bellowing parameters are useful for Publisher performance
		// tuning.

		tcpBuffer = attributes.get("tcp-buffer");
		tcpNoDelay = attributes.get("tcp-no-delay");
		preAck = attributes.get("pre-ack");
		useNio = attributes.get("use-nio");
		producerRate = attributes.get("producer-rate");
		latencyCaptureWindow = attributes.get("latencyCaptureWindow");
		Map<String, Object> params = new HashMap<String, Object>();

		// After setting of all parameters they are added in Map "params".

		params.put(TransportConstants.TCP_NODELAY_PROPNAME, tcpNoDelay);
		params.put(TransportConstants.TCP_SENDBUFFER_SIZE_PROPNAME, tcpBuffer);
		params.put(TransportConstants.TCP_RECEIVEBUFFER_SIZE_PROPNAME,
				tcpBuffer);
		params.put(TransportConstants.USE_NIO_PROP_NAME, useNio);
		params.put(TransportConstants.HOST_PROP_NAME, serverHost);
		params.put(TransportConstants.PORT_PROP_NAME, serverPort);

		/*
		 * Configure the Map with Hornetq server TransportConfiguration and set
		 * it using serverLocator.
		 */

		ServerLocator serverLocator = HornetQClient
				.createServerLocatorWithoutHA(new TransportConfiguration(
						NettyConnectorFactory.class.getName(), params));
		serverLocator.setPreAcknowledge(Boolean.parseBoolean(preAck));
		serverLocator.setUseGlobalPools(false);
		serverLocator.setScheduledThreadPoolMaxSize(24);
		serverLocator.setThreadPoolMaxSize(-1);
		serverLocator.setProducerMaxRate(Integer.parseInt(producerRate));

		/*
		 * Here producer is created using session & it is bind to and
		 * address/topic for message sending.
		 */
		try {
			sendFactory = serverLocator.createSessionFactory();
			sendSession = sendFactory.createSession();
			producer = sendSession.createProducer(address);
			pubCounter++;
		} catch (HornetQException e) {
			// TODO Auto-generated catch block
			logger.info("init() Exception");
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		initSubscriber(attributes);
	}

	/**
	 * Initialize the Hornetq latency parameters for Subscriber.
	 */

	private void initSubscriber(Map<String, String> attributes) {
		/*
		 * In case of Hornetq it needs Address-Queue pair in order to publish
		 * message on address and receive message from queue, where as queue is
		 * bind to an address.Here address is used as a topic.
		 */
		echoAddress = attributes.get("topic");
		echoAddress = echoAddress + "-echo";

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
			serverPort = String.valueOf(sPort + subCounter);
		}
		// All of bellowing parameters are useful for Publisher performance
		// tuning.
		tcpBuffer = attributes.get("tcp-buffer");
		tcpNoDelay = attributes.get("tcp-no-delay");
		preAck = attributes.get("pre-ack");
		useNio = attributes.get("use-nio");
		Map<String, Object> params = new HashMap<String, Object>();
		// After setting of all parameters they are added in Map "params".
		params.put(TransportConstants.TCP_NODELAY_PROPNAME, tcpNoDelay);
		params.put(TransportConstants.TCP_SENDBUFFER_SIZE_PROPNAME, tcpBuffer);
		params.put(TransportConstants.TCP_RECEIVEBUFFER_SIZE_PROPNAME,
				tcpBuffer);
		params.put(TransportConstants.HOST_PROP_NAME, serverHost);
		params.put(TransportConstants.PORT_PROP_NAME, serverPort);
		params.put(TransportConstants.USE_NIO_PROP_NAME, useNio);

		/*
		 * Configure the Map with Hornetq server TransportConfiguration and set
		 * it using serverLocator.
		 */
		ServerLocator serverLocator = HornetQClient
				.createServerLocatorWithoutHA(new TransportConfiguration(
						NettyConnectorFactory.class.getName(), params));
		serverLocator.setPreAcknowledge(Boolean.parseBoolean(preAck));
		serverLocator.setConsumerWindowSize(-1);

		/*
		 * Here consumer is created with queue & it is bind to and address/topic
		 * for message receiving.
		 */

		try {
			recvFactory = serverLocator.createSessionFactory();
			recvSession = recvFactory.createSession();
			String s = queueName + "_" + UUID.randomUUID().toString();
			recvSession.createTemporaryQueue(address, s);
			consumer = recvSession.createConsumer(s);
			recvSession.start();
			subCounter++;
		} catch (Exception e) {
			logger.info("init() Exception");
			e.printStackTrace();
		}
	}

	@Override
	public void sendMessageForThroughput(byte[] message) {
		// TODO Auto-generated method stub
	}

	@Override
	public boolean isReadyToRun() {
		// TODO Auto-generated method stub
		return true;
	}

	/**
	 * Send & Receive message for latency determination.
	 */

	@Override
	public void sendMessageForLatency(byte[] message) {

		/*
		 * In first stage producer sends message and consumer receives it , in
		 * second stage consumer echos received message back to producer
		 */

		setStartTime(new Date());
		long sendTime = 0;
		long recvTime = 0;
		String mstr = new String(message);
		int mcounter = 0;
		try {
			while (canContinue) {
				try {
					mcounter++;
					// Send the message. Now the message contains a unique
					// identifier which is used for calculating latency.
					msgSend = sendSession.createMessage(false);
					msgSend.putStringProperty("prop", mstr);
					sendTime = System.nanoTime();
					producer.send(msgSend);
					sendCnt++;
					// add data to statistics pool. This will count the number
					// of messages and the total bytes sent.
					addToStatisticsPool(message.length);
					// In case you need to consider think time, consider it like
					// below
					if (getThinkTimeInMillis() > 0)
						try {
							Thread.sleep(getThinkTimeInMillis());
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
				} catch (HornetQException e) {
					// TODO Auto-generated catch block
					// logger.info("sendMessage() Exception");
					e.printStackTrace();
				}
				try {
					msgRecv = consumer.receive(1);
					recvTime = System.nanoTime();
					if (msgRecv == null) {
						continue;
					}
					recvCnt++;
					if (latencyCaptureWindow == null) {
						latencyCaptureWindow = String.valueOf(10);
					}
					if (mcounter > Integer.parseInt(latencyCaptureWindow)) {
						addLatencyStatistics(String.valueOf(mcounter++),
								recvTime - sendTime);
						mcounter = 0;
					}
				} catch (HornetQException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		} catch (Exception e) {
			logger.error("Exception encountered in Echo-Send-Receive program");
			e.printStackTrace();
		} finally {
			logger.info("sendCnt=[" + sendCnt + "], recvCnt=[" + recvCnt + "]");
			markTaskComplete(); // IMPORTANT !!
			setEndTime(new Date()); // IMPORTANT !!
		}
	}

	/**
	 * Close producer,session,factory & serverLocator for Publisher & Subscriber
	 */
	@Override
	public void cleanup() {
		try {
			pubCounter--;
			subCounter--;
			producer.close();
			consumer.close();
			sendSession.close();
			recvSession.close();
			sendFactory.close();
			recvFactory.close();
		} catch (Exception ex) {
			logger.error("cleanup thread caught exception: " + ex);
			ex.printStackTrace();
			System.exit(1);
		}
	}

}
