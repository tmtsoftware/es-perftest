package org.tmt.csw.eventservice.util;

import java.util.HashMap;
import java.util.Map;

import org.tmt.csw.eventservice.TopicListener;

/**
 * 
 * @author amit_harsola
 *
 */
public class QueueCallBackMapping {
	private String queueName;
	
	// Stores callback to listener mapping
	private Map<String, TopicListener> map  = new HashMap<String, TopicListener>();

	public String getQueueName() {
		return queueName;
	}

	public void setQueueName(String queueName) {
		this.queueName = queueName;
	}

	public Map<String, TopicListener> getMap() {
		return map;
	}

	public void addCallback(String callback, TopicListener listener) {
		if (!map.containsKey(callback)) {
			map.put(callback, listener);
		}
	}
	
	public TopicListener getCallbackListener(String callback) {
		if (map.containsKey(callback)) {
			return map.get(callback);
		}
		
		return null;
	}
	
	public void removeCallback(String callback) {
		if (map.containsKey(callback)) {
			map.remove(callback);
		}		
	}
}
