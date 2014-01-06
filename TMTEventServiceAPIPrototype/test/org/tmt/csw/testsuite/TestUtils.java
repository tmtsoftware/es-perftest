package org.tmt.csw.testsuite;

import java.util.Calendar;
import org.tmt.csw.eventservice.event.Event;
import org.tmt.csw.eventservice.event.EventHeader;

public class TestUtils {

	public static final long SLEEP_TIME = 3000;

	public Event createEventObject(String key, String value) {

		Event event = new Event();
		event.addKeyValue(key, value);
		event.setPayload(event.getPayload());
		EventHeader headers = new EventHeader();
		headers.setCreateTimestamp(Calendar.getInstance().getTime());
		event.setHeaders(headers);
		return event;

	}
}
