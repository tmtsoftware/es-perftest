package org.tmt.csw.testsuite;

import java.util.Map;
import junit.framework.TestCase;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.tmt.csw.consumerone.EventConsumerOne;
import org.tmt.csw.consumertwo.EventConsumerTwo;
import org.tmt.csw.eventservice.AbstractEventService;
import org.tmt.csw.eventservice.EventService;
import org.tmt.csw.eventservice.event.Event;
import org.tmt.csw.eventservice.exception.EventPublishException;
import org.tmt.csw.eventservice.exception.EventSubscriptionException;
import org.tmt.csw.eventservice.exception.EventUnSubscriptionException;
import org.tmt.csw.eventservice.util.Constants;
import org.tmt.csw.eventservice.callback.EventCallbackImplOne;


/**
 * Post Message with one consumer and one callback --> Consumer should
 * receive the message through callback
 */

public class PostWithOneConsumerOneCallbck extends TestCase {
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
	 * 2) Post Message with one consumer and one callback --> Consumer should
	 * receive the message through callback
	 */
	@Test
	public void testPostWithOneConsumerOneCallbck()
			throws EventPublishException, EventSubscriptionException,
			InterruptedException, EventUnSubscriptionException {
		System.out
				.println("******************* [2] [STARTING] POST-WITH-ONE-CONSUMER-ONE-CALLBACK TEST ************************ ");
		// Create call-back
		EventCallbackImplOne mockEventCallbackImplOne = new EventCallbackImplOne();

		// Create EventService Object
		EventService eventService = AbstractEventService.createEventService();

		// Subscribe to a Topic
		EventConsumerTwo consumerOne = new EventConsumerTwo();
		consumerOne.subscribe(eventService, Constants.TMT_MOBI_BLUE_FILTER,
				mockEventCallbackImplOne);

		// Create a Message (Event Object)
		Event event = testConstants.createEventObject("2",
				"postWithOneConsumerOneCallback");
		// Post a Message to a Topic
		eventService.post(Constants.TMT_MOBI_BLUE_FILTER, event);

		// Wait for call-back to receive message
		Thread.sleep(TestUtils.SLEEP_TIME);
		// Get the results from callback
		Event evt = mockEventCallbackImplOne.getEvt();
		// if the method times out, test will fail
		assertNotNull(
				"Event not received by callback - Possible reason is time out",
				evt);
		System.out.println("Event received by Consumer Callback  = [ "
				+ evt + "]");

		// otherwise, check the results (Message contents)
		Map<String, Object> map = evt.getPayload();
		String value = (String) map.get("2");
		assertEquals(
				"The sending & receiving message contents doesn't match  ",
				"postWithOneConsumerOneCallback", value);
		System.out.println("Message received by Consumer Callback = ["
				+ value + "]");

		// clean up
		consumerOne.unSubscribe(eventService, Constants.TMT_MOBI_BLUE_FILTER,mockEventCallbackImplOne);
		
		System.out
				.println("###################### POST-WITH-ONE-CONSUMER-ONE-CALLBACK TEST [END] ######################### ");

	}

}
