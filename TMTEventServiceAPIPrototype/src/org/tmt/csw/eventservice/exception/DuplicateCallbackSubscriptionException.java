package org.tmt.csw.eventservice.exception;

/**
 * 
 * @author amit_harsola 
 *
 */
public class DuplicateCallbackSubscriptionException extends Exception {
	
	private static final long serialVersionUID = -9128498484368193411L;

	public DuplicateCallbackSubscriptionException() {
		super();
	}

	public DuplicateCallbackSubscriptionException(String message, Throwable cause) {
		super(message, cause);
	}

	public DuplicateCallbackSubscriptionException(String message) {
		super(message);
	}

	public DuplicateCallbackSubscriptionException(Throwable cause) {
		super(cause);
	}
}
