package org.tmt.csw.eventservice;

import org.tmt.csw.eventservice.callback.EventCallback;
import org.tmt.csw.eventservice.event.Event;
import org.tmt.csw.eventservice.exception.EventPublishException;
import org.tmt.csw.eventservice.exception.EventSubscriptionException;
import org.tmt.csw.eventservice.exception.EventUnSubscriptionException;

/**
 * Event Service API used by all publishers and consumers to publish messages and subscribe to topics
 * 
 * @author amit_harsola
 *
 */
public interface EventService {
	
	/**
	 * It accepts the FQN topic and the Event to the published to the topic. It connects to the broker using Core 
	 * API and publishes event. The Event object is transformed to HashMap before  publishing to topic
	 * 
	 * @param topic
	 * @param event
	 * @throws EventPublishException
	 */
	public void post(String topic, Event event) throws EventPublishException;

	/**
	 * Any consumer desiring to subscribe to a topic uses the API passing the FQN Topic name and the Callback to be 
	 * used for receiving the messages. It would spawn a new thread, connect to broker, create a queue (if required), 
	 * register message listener with broker and also register the callback with message listener 
	 * 
	 * @param topic
	 * @param callback
	 * @throws EventSubscriptionException
	 */
	public void subscribe(String topic, EventCallback callback) throws EventSubscriptionException;
	
	/**
	 * Consumers can unsubscribe to a topic by passing the FQN topic name and callback class used for receiving the message. 
	 * If there multiple callbacks registered against a topic for the same consumer, it would just unregister the callback. 
	 * If there are no other callbacks, it would unregister the message listener as well
	 * 
	 * @param topic
	 * @param callback
	 * @throws EventUnSubscriptionException
	 */
	public void unSubscribe(String topic, EventCallback callback) throws EventUnSubscriptionException;
	
	/**
	 * It unregisters all the callbacks for the given topic and consumer and also message listener from the queue. 
	 * Once everything is unsubscribed, queue is also deleted
	 * 
	 * @param topic
	 * @throws EventUnSubscriptionException
	 */
	public void unSubscribeAll(String topic) throws EventUnSubscriptionException;	
}
