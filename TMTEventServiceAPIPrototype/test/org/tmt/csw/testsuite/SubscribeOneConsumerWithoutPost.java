package org.tmt.csw.testsuite;

import junit.framework.TestCase;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.tmt.csw.consumerone.EventConsumerOne;
import org.tmt.csw.eventservice.AbstractEventService;
import org.tmt.csw.eventservice.EventService;
import org.tmt.csw.eventservice.event.Event;
import org.tmt.csw.eventservice.exception.EventSubscriptionException;
import org.tmt.csw.eventservice.exception.EventUnSubscriptionException;
import org.tmt.csw.eventservice.util.Constants;
import org.tmt.csw.eventservice.callback.EventCallbackImplOne;

public class SubscribeOneConsumerWithoutPost extends TestCase {

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
	 * 8) one consumer subscribe with a callback without publisher posting any
	 * message --> the consumer callback should not receive message
	 */

	@Test
	public void testSubscribeOneConsumerWithoutPost() {
		System.out
				.println("******************* [8] [STARTING] SUBSCRIBE-WITH-ONE-CONSUMER-WITHOUT-POST TEST ************************ ");
		// Create call-back
		EventCallbackImplOne mockEventCallbackImplOne = new EventCallbackImplOne();

		// Create EventService Object
		EventService eventService = AbstractEventService.createEventService();
		// Subscribe to a Topic with a call back - mockEventCallbackImplOne
		System.out.println("Consumer subscribes to a topic with a callback");
		EventConsumerOne consumerOne = new EventConsumerOne();
		try {
			consumerOne.subscribe(eventService, Constants.TMT_MOBI_BLUE_FILTER,
					mockEventCallbackImplOne);
		} catch (EventSubscriptionException eSub) {
			fail("Error while Event Subscription for consumerOne");

		}

		// Wait for call-back to receive message
		try {
			Thread.sleep(TestUtils.SLEEP_TIME);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		// Get the results from callback
		Event evt = mockEventCallbackImplOne.getEvt();
		assertNull(evt);
		System.out
				.println("Event received by callback is NULL - Reason is subscription without post");
		System.out.println("Event received by callback = [" + evt + "]");

		// unsubscribe from all callbacks - cleanup
		try {
			consumerOne.unSubscribe(eventService,
					Constants.TMT_MOBI_BLUE_FILTER, mockEventCallbackImplOne);
		} catch (EventUnSubscriptionException eUnsub) {
			fail("Error while Event UnSubscription");
		}
		System.out
				.println("###################### SUBSCRIBE-WITH-ONE-CONSUMER-WITHOUT-POST TEST [END] ######################### ");

	}
}
