package org.tmt.csw.testsuite;

import junit.framework.TestCase;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.tmt.csw.consumerone.EventConsumerOne;
import org.tmt.csw.eventservice.AbstractEventService;
import org.tmt.csw.eventservice.EventService;
import org.tmt.csw.eventservice.callback.EventCallbackImplOne;
import org.tmt.csw.eventservice.exception.EventPublishException;
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
	 * 12) Subscribe same instance of subscriber again to same topic --> The
	 * EventService should throw exception
	 * 
	 * @throws EventPublishException
	 * @throws InterruptedException
	 * 
	 */
	@Test
	public void testSameSubscriberDoubleSubscription()
			throws EventPublishException, InterruptedException,
			EventUnSubscriptionException {
		System.out
				.println("******************* [12] [STARTING] SAME-SUBSCRIBER-DOUBLE-SUBSCRIPTION TEST ************************ ");
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

		boolean caughtException = false;

		try {
			// Again Subscribe to a Topic with same call back -
			// mockEventCallbackImplOne
			System.out
					.println("Consumer1 again Subscribes to a Topic with same callback mockEventCallbackImplOne ");
			consumerOne.subscribe(eventService, Constants.TMT_MOBI_BLUE_FILTER,
					mockEventCallbackImplOne);

		} catch (Exception e) {
			caughtException = true;

		}

		if (caughtException) {
			assertTrue("Subscribed", caughtException);
			System.out
					.println("Exception occured while subscribing | Subscriber trying to subscribe to the topic again with same callback");
		} else {
			assertFalse("Subscription failed", caughtException);
			System.out.println("Consumer Sucessfully Subscribed");
		}

		consumerOne.unSubscribe(eventService, Constants.TMT_MOBI_BLUE_FILTER,
				mockEventCallbackImplOne);

		System.out
				.println("######################   SAME-SUBSCRIBER-DOUBLE-SUBSCRIPTION TEST [END] ######################### ");

	}

}
