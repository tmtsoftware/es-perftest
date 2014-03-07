package org.tmt.csw.testsuite;

import java.util.Map;
import junit.framework.TestCase;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.tmt.csw.consumerone.EventConsumerOne;
import org.tmt.csw.eventservice.AbstractEventService;
import org.tmt.csw.eventservice.EventService;
import org.tmt.csw.eventservice.event.Event;
import org.tmt.csw.eventservice.exception.EventPublishException;
import org.tmt.csw.eventservice.exception.EventSubscriptionException;
import org.tmt.csw.eventservice.exception.EventUnSubscriptionException;
import org.tmt.csw.eventservice.util.Constants;
import org.tmt.csw.eventservice.callback.EventCallbackImplOne;
import org.tmt.csw.eventservice.callback.EventCallbackImplTwo;

/**
 * Unsubscribe a Callback for a consumer and Post Message--> The unregistered
 * callback should not receive the message
 */

public class UnsubscribeAndPost extends TestCase {

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
	 * 6) Unsubscribe a Callback for a consumer and Post Message--> The
	 * unregistered callback should not receive the message
	 */
	@Test
	public void testUnsubscribeAndPost() {
		System.out
				.println("******************* [6] [STARTING] UNSUBSCRIBE-AND-POST TEST ************************ ");
		// Create call-back
		EventCallbackImplOne mockEventCallbackImplOne = new EventCallbackImplOne();
		EventCallbackImplTwo mockEventCallbackImplTwo = new EventCallbackImplTwo();

		// Create EventService Object
		EventService eventService = AbstractEventService.createEventService();

		EventConsumerOne consumerOne = new EventConsumerOne();
		// Subscribe to a Topic with call backs -
		// mockEventCallbackImplOne,mockEventCallbackImplTwo
		System.out.println("Consumer Subscribes to a Topic with two callbacks");
		try {
			consumerOne.subscribe(eventService, Constants.TMT_MOBI_BLUE_FILTER,
					mockEventCallbackImplOne);
			consumerOne.subscribe(eventService, Constants.TMT_MOBI_BLUE_FILTER,
					mockEventCallbackImplTwo);
		} catch (EventSubscriptionException eSub) {
			fail("Error while Event Subscription for consumerOne");

		}

		// unsubscriber from one callback i.e mockEventCallbackImplOne
		System.out
				.println("Consumer UNSubscribes to a Topic with one callback");
		try {
			consumerOne.unSubscribe(eventService,
					Constants.TMT_MOBI_BLUE_FILTER, mockEventCallbackImplOne);
		} catch (EventUnSubscriptionException eUnsub) {
			fail("Error while Event UnSubscription");
		}
		// Create a Message (Event Object)
		Event event = testConstants.createEventObject("6",
				"unsubscriberAndPost");

		// Post a Message to a Topic
		try {
			eventService.post(Constants.TMT_MOBI_BLUE_FILTER, event);
		} catch (EventPublishException ePost) {
			fail("Error while Message Post ");
		}

		// Wait for call-back to receive message
		try {
			Thread.sleep(TestUtils.SLEEP_TIME);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		// Get the results from callback
		Event evt = mockEventCallbackImplOne.getEvt();
		// Event received by callback is NULL - consumer unsubscribed with that
		// callback
		assertNull(evt);
		System.out
				.println("Event received by callback is NULL - consumer unsubscribed with that callback");
		System.out.println("Event received by {mockEventCallbackImplOne} = ["
				+ evt + "]");

		// Get the results from second callback
		Event secondEvt = mockEventCallbackImplTwo.getEvt();
		// if the method times out, test will fail
		assertNotNull(
				"Event not received by callback - Possible reason is time out",
				secondEvt);
		System.out.println("Event received by {mockEventCallbackImplTwo} = [ "
				+ secondEvt + "]");

		Map<String, Object> secondMap = secondEvt.getPayload();
		String secondValue = (String) secondMap.get("6");
		assertEquals(
				"The sending & receiving message contents doesn't match  ",
				"unsubscriberAndPost", secondValue);
		System.out.println("Message received by {mockEventCallbackImplTwo} = ["
				+ secondValue + "]");

		// unsubscribe from callback
		try {
			consumerOne.unSubscribe(eventService,
					Constants.TMT_MOBI_BLUE_FILTER, mockEventCallbackImplTwo);
		} catch (EventUnSubscriptionException eUnsub) {
			fail("Error while Event UnSubscription");
		}

		System.out
				.println("######################   UNSUBSCRIBE-AND-POST TEST [END] ######################### ");

	}

}
