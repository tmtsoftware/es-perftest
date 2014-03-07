package org.tmt.csw.eventservice.exception;

/**
 * EventService API would throw EventUnSubscriptionException while unsubscribing from a topic
 * 
 * @author amit_harsola 
 *
 */
public class EventSubscriptionException extends Exception {
	
	private static final long serialVersionUID = -9128498484368193411L;

	public EventSubscriptionException() {
		super();
	}

	public EventSubscriptionException(String message, Throwable cause) {
		super(message, cause);
	}

	public EventSubscriptionException(String message) {
		super(message);
	}

	public EventSubscriptionException(Throwable cause) {
		super(cause);
	}
}
