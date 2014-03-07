package org.tmt.csw.eventservice;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

import org.hornetq.api.core.HornetQException;
import org.hornetq.api.core.client.ClientMessage;
import org.hornetq.api.core.client.ClientProducer;
import org.hornetq.api.core.client.ClientSession;
import org.hornetq.api.core.client.ClientSessionFactory;
import org.hornetq.api.core.client.ServerLocator;
import org.tmt.csw.eventservice.callback.EventCallback;
import org.tmt.csw.eventservice.event.Event;
import org.tmt.csw.eventservice.exception.EventPublishException;
import org.tmt.csw.eventservice.exception.EventSubscriptionException;
import org.tmt.csw.eventservice.exception.EventUnSubscriptionException;
import org.tmt.csw.eventservice.util.BrokerUtility;
import org.tmt.csw.eventservice.util.ByteUtils;
import org.tmt.csw.eventservice.util.SubscriberCache;

/**
 * Implementation class for EventService API
 * 
 * @author amit_harsola
 * 
 */
public class EventServiceImpl extends AbstractEventService {

	private final static Logger logger = Logger
			.getLogger(EventServiceImpl.class.getName());
	static {
		try {
			InputStream loggerProps = EventServiceImpl.class.getClassLoader()
					.getResourceAsStream("config/logger.properties");
			if (loggerProps == null) {
				System.out.println("Could not locate the logger.properties");
			}
			LogManager.getLogManager().readConfiguration(loggerProps);

		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void post(String topic, Event event) throws EventPublishException {
		ClientSessionFactory factory = null;
		ClientSession session = null;
		ClientProducer producer = null;
		ServerLocator serverLocator = null;

		try {
			serverLocator = BrokerUtility.connectToBroker(true);
			factory = serverLocator.createSessionFactory();
			session = factory.createSession();
			producer = session.createProducer(topic);
			ClientMessage message = session.createMessage(false);

			session.start();

			event.addKeyValue("CREATE_TIMESTAMP", event.getHeaders()
					.getCreateTimestamp());

			message.getBodyBuffer().writeBytes(
					ByteUtils.toByteArray(event.getPayload()));
			producer.send(message);

		} catch (HornetQException hqException) {
			logger.log(Level.SEVERE,
					"Event could not be posted " + hqException.toString(),
					hqException);
			throw new EventPublishException("Event could not be posted",
					hqException);
		} catch (Exception exception) {
			logger.log(Level.SEVERE,
					"Unable to create Session " + exception.toString());
			throw new EventPublishException("Unable to create Session",
					exception);
		} finally {
			// Job Done. Close everything
			try {
				if (producer != null) {
					producer.close();
				}

				if (session != null) {
					session.close();
				}

				if (factory != null) {
					factory.close();
				}
				serverLocator.close();

			} catch (HornetQException hornetQException) {
				logger.log(Level.SEVERE,
						"Error while closing the broker connection"
								+ hornetQException.toString(),
						hornetQException);
				throw new EventPublishException(
						"Error while closing the broker connection",
						hornetQException);
			}
		}
	}

	@Override
	public void subscribe(String topic, EventCallback callback)
			throws EventSubscriptionException {
		try {

			Throwable t = new Throwable();
			StackTraceElement[] stackTraceElements = t.getStackTrace();
			// Consumer Queue name
			String consumerQueue = stackTraceElements[1].getClassName();
			consumerQueue = topic
					+ "-"
					+ consumerQueue
							.substring(consumerQueue.lastIndexOf(".") + 1);
			String callbackName = callback.getClass().getSimpleName();

			SubscriberCache cache = SubscriberCache.getInstance();
			TopicListener listener = cache.getCallbackListener(topic,
					consumerQueue, callbackName);

			if (listener != null) {
				throw new EventSubscriptionException(
						"Consumer again trying to subscribe " + callbackName
								+ " to topic " + topic);
			}

			listener = new TopicListener(topic, callback, consumerQueue);
			listener.startListening();
			cache.addCallbackToCache(topic, consumerQueue, callbackName,
					listener);

		} catch (Exception exception) {
			logger.log(Level.SEVERE, "Error while subscribing to topic"
					+ exception.toString());
			throw new EventSubscriptionException(
					"Error while subscribing to topic", exception);
		}
	}

	@Override
	public void unSubscribe(String topic, EventCallback callback)
			throws EventUnSubscriptionException {

		Throwable t = new Throwable();
		StackTraceElement[] stackTraceElements = t.getStackTrace();
		// Consumer Queue name
		String consumerQueue = stackTraceElements[1].getClassName();
		consumerQueue = topic + "-"
				+ consumerQueue.substring(consumerQueue.lastIndexOf(".") + 1);
		String callbackName = callback.getClass().getSimpleName();

		SubscriberCache cache = SubscriberCache.getInstance();
		TopicListener listener = cache.getCallbackListener(topic,
				consumerQueue, callbackName);
		logger.info("Invoking stop on listener object: "+listener);
		if (listener != null) {
			logger.info("unSubscribing from topic");
			listener.stopListening();
			cache.removeCallbackFromCache(topic, consumerQueue, callbackName);
		} else {
			logger.log(Level.SEVERE, "Consumer has not subscribed callback "
					+ callbackName + " to topic " + topic);
			throw new EventUnSubscriptionException(
					"Consumer has not subscribed callback " + callbackName
							+ " to topic " + topic);
		}
	}

	@Override
	public void unSubscribeAll(String topic)
			throws EventUnSubscriptionException {
		Throwable t = new Throwable();
		StackTraceElement[] stackTraceElements = t.getStackTrace();
		// Consumer Queue name
		String consumerQueue = stackTraceElements[1].getClassName();
		consumerQueue = topic + "-"
				+ consumerQueue.substring(consumerQueue.lastIndexOf(".") + 1);

		SubscriberCache cache = SubscriberCache.getInstance();
		Collection<TopicListener> listeners = cache.getAllCallbackListeners(
				topic, consumerQueue);

		if (listeners != null && listeners.size() != 0) {
			for (Iterator<TopicListener> iterator = listeners.iterator(); iterator
					.hasNext();) {
				TopicListener listener = iterator.next();
				listener.stopListening();
			}

			cache.removeAllCallbacksFromCache(topic, consumerQueue);

		} else {
			logger.log(Level.SEVERE, "Consumer has not subscribed to topic "
					+ topic);
			throw new EventUnSubscriptionException(
					"Consumer has not subscribed to topic " + topic);
		}
	}
}
