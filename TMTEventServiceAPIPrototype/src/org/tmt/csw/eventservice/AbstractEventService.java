/**
 * 
 */
package org.tmt.csw.eventservice;

/**
 * @author amit_harsola
 *
 */
public abstract class AbstractEventService implements EventService {

	public static EventService createEventService() {
		return new EventServiceImpl();
	}
}
