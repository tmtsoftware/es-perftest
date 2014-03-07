/**
 * 
 */
package org.tmt.csw.eventservice.event;

import java.util.HashMap;
import java.util.Map;


/**
 * Represents an Event. Event object is passed to Event Service API. It is composed of EventPayload (Hashmap) and 
 * EventHeader objects
 * 
 * @author amit_harsola 
 *
 */
public class Event {
	
	private Map<String, Object> payload;
	private EventHeader headers;
	
	public Map<String, Object> getPayload() {
		return payload;
	}
	
	public void setPayload(Map<String, Object> payload) {
		this.payload = payload;
	}
	
	public void addKeyValue(String key, Object value) {
		if (payload == null) {
			payload = new HashMap<String, Object>();
		}
		payload.put(key, value);
	}
	
	public EventHeader getHeaders() {
		return headers;
	}
	public void setHeaders(EventHeader headers) {
		this.headers = headers;
	}
}
