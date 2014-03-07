package org.tmt.csw.eventservice.exception;

/**
 * 
 * @author amit_harsola 
 *
 */
public class EventPublishException extends Exception {
	
	private static final long serialVersionUID = -9128498484368193411L;

	public EventPublishException() {
		super();
	}

	public EventPublishException(String message, Throwable cause) {
		super(message, cause);
	}

	public EventPublishException(String message) {
		super(message);
	}

	public EventPublishException(Throwable cause) {
		super(cause);
	}
}
