package org.tmt.csw.eventservice.callback;

import java.util.Iterator;
import java.util.Map;

import org.tmt.csw.eventservice.event.Event;

/**
 * Sample implementation for EventCallback interface
 * 
 * @author amit_harsola
 * 
 */
public class EventCallbackImplTwo implements EventCallback {
	private Event evt;

	public Event getEvt() {
		return this.evt;
	}

	@Override
	public void notifyEvent(Event event) {
		System.out
				.println("Event Received by EventCallbackImplTwo----------------------");

		System.out.print("Event Post Timestamp: "
				+ event.getHeaders().getPublishTimestamp());
		System.out.print(" ||  Event Create Timestamp: "
				+ event.getHeaders().getCreateTimestamp());
		System.out
				.print(" || Event Source : " + event.getHeaders().getSource());
		Map<String, Object> data = event.getPayload();
		Iterator<String> iterator = data.keySet().iterator();
		while (iterator.hasNext()) {
			String key = iterator.next();
			System.out.println("  || Event Key :" + key + "  || Event Value :"
					+ data.get(key));
		}
		System.out
				.println("------------------------------ Message END ----------------------");
		this.evt = event;

	}
}
