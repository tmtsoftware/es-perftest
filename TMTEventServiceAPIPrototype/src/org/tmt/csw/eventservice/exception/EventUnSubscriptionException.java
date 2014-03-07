package org.tmt.csw.eventservice.exception;

/**
 * EventService API would throw EventSubscriptionException while subscribing to a topic
 * 
 * @author amit_harsola
 * 
 */
public class EventUnSubscriptionException extends Exception {
	
	private static final long serialVersionUID = -9128498484368193411L;

	public EventUnSubscriptionException() {
		super();
	}

	public EventUnSubscriptionException(String message, Throwable cause) {
		super(message, cause);
	}

	public EventUnSubscriptionException(String message) {
		super(message);
	}

	public EventUnSubscriptionException(Throwable cause) {
		super(cause);
	}
}
