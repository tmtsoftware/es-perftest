package org.tmt.csw.eventservice.callback;

import org.tmt.csw.eventservice.event.Event;

/**
 * Callback interface used by consumers to receive messages
 * 
 * @author amit_harsola
 *
 */
public interface EventCallback {
	
	/**
	 * Callback method invoked by message listener on receipt of message 
	 * 
	 * @param event
	 */
	public void notifyEvent(Event event);
}
