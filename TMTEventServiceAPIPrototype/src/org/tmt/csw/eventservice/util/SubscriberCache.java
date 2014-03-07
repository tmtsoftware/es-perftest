package org.tmt.csw.eventservice.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.tmt.csw.eventservice.TopicListener;

/**
 * Singleton class that holds cache of topic, queue and listener mappings
 * 
 * @author amit_harsola
 * 
 */
public class SubscriberCache {

	private static SubscriberCache self = new SubscriberCache();
	private static Map<String, List<QueueCallBackMapping>> subscriberCacheMap = new HashMap<String, List<QueueCallBackMapping>>();

	/**
	 * Returns instance of cache
	 * 
	 * @return
	 */
	public static SubscriberCache getInstance() {
		return self;
	}

	/**
	 * Adds combination of topic, queue and listener to the cache
	 * 
	 * @param topic
	 * @param consumerQueue
	 * @param callback
	 * @param listener
	 */
	public synchronized void addCallbackToCache(String topic, String consumerQueue,
			String callback, TopicListener listener) {
		if (!subscriberCacheMap.containsKey(topic)) {
			QueueCallBackMapping mapping = new QueueCallBackMapping();
			mapping.setQueueName(consumerQueue);
			mapping.addCallback(callback, listener);
			List<QueueCallBackMapping> listofQueues = new ArrayList<QueueCallBackMapping>();
			listofQueues.add(mapping);
			subscriberCacheMap.put(topic, listofQueues);
		} else {
			List<QueueCallBackMapping> listofQueues = subscriberCacheMap
					.get(topic);
			boolean foundQueue = false;
			for (Iterator<QueueCallBackMapping> iterator = listofQueues
					.iterator(); iterator.hasNext();) {
				QueueCallBackMapping queueCallBackMapping = iterator.next();

				if (queueCallBackMapping.getQueueName().equals(consumerQueue)) {
					queueCallBackMapping.addCallback(callback, listener);
					foundQueue = true;
				}
			}

			if (!foundQueue) {
				QueueCallBackMapping mapping = new QueueCallBackMapping();
				mapping.setQueueName(consumerQueue);
				mapping.addCallback(callback, listener);
                listofQueues = subscriberCacheMap.get(topic);
                listofQueues.add(mapping);


				
			}
		}

	}

	/**
	 * Removes listener from the cache for the given topic
	 * 
	 * @param topic
	 */
	public synchronized void removeListenerFromCache(String topic) {
		if (subscriberCacheMap.containsKey(topic)) {
			subscriberCacheMap.remove(topic);
		}
	}

	/**
	 * Removes listener from the cache for the given topic
	 * 
	 * @param topic
	 * @param callback
	 */
	public synchronized void removeCallbackFromCache(String topic, String consumerQueue,
			String callback) {
		if (subscriberCacheMap.containsKey(topic)) {
			List<QueueCallBackMapping> listofQueues = subscriberCacheMap
					.get(topic);
			Object[] queues = listofQueues.toArray();

			for (int i = 0; i < queues.length; i++) {

				QueueCallBackMapping queueCallBackMapping = (QueueCallBackMapping) queues[i];

				if (queueCallBackMapping.getQueueName().equals(consumerQueue)) {
					queueCallBackMapping.removeCallback(callback);

					if (queueCallBackMapping.getMap().size() == 0) {
						listofQueues.remove(i);
						if (listofQueues.size() == 0) {
							subscriberCacheMap.remove(topic);
						}
					}
				}
			}
		}
	}

	/**
	 * Removes listener from the cache for the given topic
	 * 
	 * @param topic
	 * @param callback
	 */
	public synchronized void removeAllCallbacksFromCache(String topic, String consumerQueue) {
		if (subscriberCacheMap.containsKey(topic)) {
			List<QueueCallBackMapping> listofQueues = subscriberCacheMap
					.get(topic);
			Object[] queues = listofQueues.toArray();

			for (int i = 0; i < queues.length; i++) {

				QueueCallBackMapping queueCallBackMapping = (QueueCallBackMapping) queues[i];

				if (queueCallBackMapping.getQueueName().equals(consumerQueue)) {
					queueCallBackMapping.getMap().clear();
					listofQueues.remove(i);
					if (listofQueues.size() == 0) {
						subscriberCacheMap.remove(topic);
					}
				}
			}
		}
	}

	/**
	 * Provides the listener instance for the given topic
	 * 
	 * @param topic
	 * @param callback
	 * @return TopicListener
	 */
	public synchronized TopicListener getCallbackListener(String topic,
			String consumerQueue, String callback) {
		if (subscriberCacheMap.containsKey(topic)) {
			List<QueueCallBackMapping> listofQueues = subscriberCacheMap
					.get(topic);

			for (Iterator<QueueCallBackMapping> iterator = listofQueues
					.iterator(); iterator.hasNext();) {

				QueueCallBackMapping queueCallBackMapping = iterator.next();

				if (queueCallBackMapping.getQueueName().equals(consumerQueue)
						&& queueCallBackMapping.getMap().containsKey(callback)) {
					return queueCallBackMapping.getCallbackListener(callback);
				}
			}
		}

		return null;
	}

	/**
	 * Provides the listener instance for the given topic
	 * 
	 * @param topic
	 * @param consumerQueue
	 * @return Collection<TopicListener>
	 */
	public synchronized Collection<TopicListener> getAllCallbackListeners(String topic,
			String consumerQueue) {
		if (subscriberCacheMap.containsKey(topic)) {

			List<QueueCallBackMapping> listofQueues = subscriberCacheMap
					.get(topic);

			for (Iterator<QueueCallBackMapping> iterator = listofQueues
					.iterator(); iterator.hasNext();) {
				QueueCallBackMapping queueCallBackMapping = iterator.next();

				if (queueCallBackMapping.getQueueName().equals(consumerQueue)) {
					return queueCallBackMapping.getMap().values();
				}
			}
		}

		return null;
	}
}
