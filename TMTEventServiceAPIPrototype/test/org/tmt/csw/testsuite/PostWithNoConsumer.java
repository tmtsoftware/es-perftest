package org.tmt.csw.testsuite;

import junit.framework.TestCase;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.tmt.csw.eventservice.AbstractEventService;
import org.tmt.csw.eventservice.EventService;
import org.tmt.csw.eventservice.event.Event;
import org.tmt.csw.eventservice.util.Constants;

/**
 * Post Message to a Topic with no consumers subscribed --> Message should not
 * be available to consumers on subscription
 */

public class PostWithNoConsumer extends TestCase {
	public TestUtils testConstants;

	@Before
	protected void setUp() throws Exception {
		super.setUp();
		testConstants = new TestUtils();
	}

	@After
	protected void tearDown() throws Exception {
		super.tearDown();
		testConstants = null;

	}

	/**
	 * Post Message to a Topic with no consumers subscribed --> Message should
	 * not be available to consumers on subscription
	 */

	@Test
	public void testPostWithNoConsumer(){
		System.out
				.println("******************* [1] [STARTING] POST-WITH-NO-CONSUMER TEST ************************ ");
		// Create EventService Object
		EventService eventService = AbstractEventService.createEventService();

		// Create a Message (Event Object)
		Event event = testConstants
				.createEventObject("1", "postWithNoConsumer");

		try {
			// Post a Message to a Topic
			eventService.post(Constants.TMT_MOBI_BLUE_FILTER, event);
		} catch (Exception exception) {
			fail("message could not be posted to the Topic");
		}

		System.out.println("Message posted successfully to the Topic");
		System.out
				.println("###################### POST-WITH-NO-CONSUMER TEST [END] ######################### ");

	}

}
