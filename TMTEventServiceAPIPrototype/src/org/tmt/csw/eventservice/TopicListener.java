package org.tmt.csw.eventservice;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

import org.hornetq.api.core.HornetQException;
import org.hornetq.api.core.HornetQQueueExistsException;
import org.hornetq.api.core.client.ClientConsumer;
import org.hornetq.api.core.client.ClientMessage;
import org.hornetq.api.core.client.ClientSession;
import org.hornetq.api.core.client.ClientSessionFactory;
import org.hornetq.api.core.client.MessageHandler;
import org.hornetq.api.core.client.ServerLocator;
import org.tmt.csw.eventservice.callback.EventCallback;
import org.tmt.csw.eventservice.event.Event;
import org.tmt.csw.eventservice.event.EventHeader;
import org.tmt.csw.eventservice.util.BrokerUtility;
import org.tmt.csw.eventservice.util.ByteUtils;
import org.tmt.csw.eventservice.util.ConfigPropertyLoader;

/**
 * Responsible for spawning new thread and registering/un-registering message
 * listeners , create & delete queues and receive message asynchronously on
 * behalf of consumer for the given FQN Topic
 * 
 * @author amit_harsola
 * 
 */
public class TopicListener implements Runnable, MessageHandler {

	private ClientSession session;
	private ClientConsumer consumer;
	private ServerLocator serverLocator;
	private ClientSessionFactory clientSessionFactory;
	private String consumerQueue;
	private boolean check = true;
	private boolean flag = false;
	private EventCallback eventCallback;
	private String topic;
	private boolean deleteQueue;
	private final static Logger logger = Logger.getLogger(TopicListener.class
			.getName());
	static {
		try {
			InputStream loggerProps = TopicListener.class.getClassLoader()
					.getResourceAsStream("config/logger.properties");
			if (loggerProps == null) {
				logger.info("Could not locate the logger.properties");
			}
			LogManager.getLogManager().readConfiguration(loggerProps);

		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public TopicListener(String topic, EventCallback eventCallback,
			String consumerQueue) {
		this.eventCallback = eventCallback;
		this.consumerQueue = consumerQueue;
		this.topic = topic;
	}

	@Override
	public void onMessage(ClientMessage message) {

		try {
			byte[] bMessage = new byte[message.getBodySize()];
			message.getBodyBuffer().readBytes(bMessage);

			Map<String, Object> eventData = (Map<String, Object>) ByteUtils
					.toObject(bMessage);
			Date createTimestamp = (Date) eventData.get("CREATE_TIMESTAMP");

			Event event = new Event();

			EventHeader header = new EventHeader();
			header.setCreateTimestamp(createTimestamp);
			header.setPublishTimestamp(new Date(message.getTimestamp()));
			header.setSource(message.getAddress().toString());

			eventData.remove("CREATE_TIMESTAMP");
			event.setPayload(eventData);
			event.setHeaders(header);

			eventCallback.notifyEvent(event);
		} catch (ClassNotFoundException exception) {
			// Nothing can be done
			exception.printStackTrace();
		} catch (IOException exception) {
			// Nothing can be done
			exception.printStackTrace();
		}
	}

	public String getCallback() {
		return eventCallback.getClass().getSimpleName();
	}

	/**
	 * Creates Consumer Queue, Client Consumer object , registers the message
	 * listener and spawns a new thread waiting for messages
	 * 
	 * @param createQueue
	 * @throws HornetQException
	 * @throws Exception
	 */
	public void startListening(boolean createQueue) throws HornetQException,
			Exception {

		init();
		if (createQueue) {
			try {
				boolean durable = Boolean.valueOf(ConfigPropertyLoader
						.getProperties().getProperty("durableQueue"));
				session.createQueue(topic, consumerQueue, durable);
			} catch (HornetQQueueExistsException exception) {
				// Queue exists ignore
			}
		}

		consumer = session.createConsumer(consumerQueue);
		consumer.setMessageHandler(this);
		Thread t = new Thread(this);
		t.start();
	}

	/**
	 * Triggers the active thread to stop and close the consumer session
	 */
	public void stopListening(boolean deleteQueue) {
		this.deleteQueue = deleteQueue;
		flag = false;
	}

	@Override
	public void run() {
		while (check) {
			if (!flag) {
				check = false;
			}
		}

		try {
			logger.info("Closing broker connections");
			consumer.close();
			if (deleteQueue) {
				try {
					session.deleteQueue(consumerQueue);
				} catch (HornetQException e) {
					logger.info("Another subscriber is subscribed to topic");

				}
			}
			session.close();
			clientSessionFactory.close();
			serverLocator.close();
		} catch (HornetQException exception) {
			logger.log(Level.SEVERE, exception.getMessage(), exception);
			exception.printStackTrace();
		}
	}

	/**
	 * Initializes connection to broker
	 * 
	 * @throws Exception
	 */
	private void init() throws Exception {

		serverLocator = BrokerUtility.connectToBroker(false);
		clientSessionFactory = serverLocator.createSessionFactory();
		session = clientSessionFactory.createSession();
		session.start();
		flag = true;
	}
}
