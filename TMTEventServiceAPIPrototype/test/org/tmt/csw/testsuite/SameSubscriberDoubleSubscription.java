package org.tmt.csw.testsuite;

import junit.framework.TestCase;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.tmt.csw.consumerone.EventConsumerOne;
import org.tmt.csw.eventservice.AbstractEventService;
import org.tmt.csw.eventservice.EventService;
import org.tmt.csw.eventservice.callback.EventCallbackImplOne;
import org.tmt.csw.eventservice.exception.EventSubscriptionException;
import org.tmt.csw.eventservice.exception.EventUnSubscriptionException;
import org.tmt.csw.eventservice.util.Constants;

public class SameSubscriberDoubleSubscription extends TestCase {

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
	 * 10) Subscribe same instance of subscriber again to same topic --> The
	 * EventService should throw exception
	 * 
	 */
	@Test
	public void testSameSubscriberDoubleSubscription() {
		System.out
				.println("******************* [10] [STARTING] SAME-SUBSCRIBER-DOUBLE-SUBSCRIPTION TEST ************************ ");
		// Create call-back
		EventCallbackImplOne mockEventCallbackImplOne = new EventCallbackImplOne();

		// Create EventService Object
		EventService eventService = AbstractEventService.createEventService();
		EventConsumerOne consumerOne = new EventConsumerOne();

		// Subscribe to a Topic with call back -
		// mockEventCallbackImplOne
		System.out.println("Consumer1 subscribes to Topic with a callback");
		try {
			consumerOne.subscribe(eventService, Constants.TMT_MOBI_BLUE_FILTER,
					mockEventCallbackImplOne);
		} catch (EventSubscriptionException e1) {
			fail("Error while subscribing");
		}

		try {
			// Again Subscribe to a Topic with same call back -
			// mockEventCallbackImplOne
			System.out
					.println("Consumer1 again Subscribes to a Topic with same callback mockEventCallbackImplOne ");
			consumerOne.subscribe(eventService, Constants.TMT_MOBI_BLUE_FILTER,
					mockEventCallbackImplOne);
			fail("FAIL: Consumer was able to Subscribe again ");

		} catch (EventSubscriptionException e) {
			System.out
					.println("PASS: Exception occured while subscribing | Subscriber trying to subscribe to the topic again with same callback");

		}

		try {
			consumerOne.unSubscribe(eventService,
					Constants.TMT_MOBI_BLUE_FILTER, mockEventCallbackImplOne);
		} catch (EventUnSubscriptionException eUnsub) {
			fail("Error while Event UnSubscription");
		}

		System.out
				.println("######################   SAME-SUBSCRIBER-DOUBLE-SUBSCRIPTION TEST [END] ######################### ");

	}

}
