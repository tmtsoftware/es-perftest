package org.tmt.csw.testsuite;

import junit.framework.TestCase;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.tmt.csw.consumerone.EventConsumerOne;
import org.tmt.csw.consumertwo.EventConsumerTwo;
import org.tmt.csw.eventservice.AbstractEventService;
import org.tmt.csw.eventservice.EventService;
import org.tmt.csw.eventservice.event.Event;
import org.tmt.csw.eventservice.exception.EventSubscriptionException;
import org.tmt.csw.eventservice.exception.EventUnSubscriptionException;
import org.tmt.csw.eventservice.util.Constants;
import org.tmt.csw.eventservice.callback.EventCallbackImplOne;
import org.tmt.csw.eventservice.callback.EventCallbackImplTwo;

public class UnsubscribeAllConsumerWithoutPost extends TestCase {

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
	 * 12) Consumer Unsubscribes from Topic with all callbacks whereas publisher
	 * had not posted any message--> The unregistered callbacks should receive
	 * the NULL
	 */
	@Test
	public void testUnsubscribeAllWithoutPost() {
		System.out
				.println("******************* [12] [STARTING] UNSUBSCRIBE-ALL-WITHOUT-POST TEST ************************ ");
		// Create call-back
		EventCallbackImplOne mockEventCallbackImplOne = new EventCallbackImplOne();
		EventCallbackImplTwo mockEventCallbackImplTwo = new EventCallbackImplTwo();
		EventCallbackImplOne mockEventCallbackImplThree = new EventCallbackImplOne();
		EventCallbackImplTwo mockEventCallbackImplFour = new EventCallbackImplTwo();

		// Create EventService Object
		EventService eventService = AbstractEventService.createEventService();

		EventConsumerOne consumerOne = new EventConsumerOne();
		// Subscribe to a Topic with call backs -
		// mockEventCallbackImplOne,mockEventCallbackImplTwo
		System.out
				.println("Consumer1 Subscribe to a Topic with two call backs");
		try {
			consumerOne.subscribe(eventService, Constants.TMT_MOBI_BLUE_FILTER,
					mockEventCallbackImplOne);
			consumerOne.subscribe(eventService, Constants.TMT_MOBI_BLUE_FILTER,
					mockEventCallbackImplTwo);
		} catch (EventSubscriptionException eSub) {
			fail("Error while Event Subscription for consumerOne");

		}

		EventConsumerTwo consumerTwo = new EventConsumerTwo();
		// Subscribe to a Topic using second consumer with call backs -
		// mockEventCallbackImplThree,mockEventCallbackImplFour
		System.out
				.println("Consumer2 Subscribe to a Topic with two call backs");
		try {
			consumerTwo.subscribe(eventService, Constants.TMT_MOBI_BLUE_FILTER,
					mockEventCallbackImplThree);
			consumerTwo.subscribe(eventService, Constants.TMT_MOBI_BLUE_FILTER,
					mockEventCallbackImplFour);
		} catch (EventSubscriptionException eSub) {
			fail("Error while Event Subscription for consumerTwo");

		}

		// Both Consumers unsubscribe from all callbacks
		System.out.println("Consumer:1 unsubscribeAll--");
		try {
			consumerOne.unSubscribeAll(eventService,
					Constants.TMT_MOBI_BLUE_FILTER);
		} catch (EventUnSubscriptionException eUnsub) {
			fail("Error while Event UnSubscription");
		}
		System.out.println("Consumer:2 unsubscribeAll--");
		try {
			consumerTwo.unSubscribeAll(eventService,
					Constants.TMT_MOBI_BLUE_FILTER);
		} catch (EventUnSubscriptionException eUnsub) {
			fail("Error while Event UnSubscription");
		}

		// Wait for call-back to receive message
		try {
			Thread.sleep(TestUtils.SLEEP_TIME);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		// Get the results from callback
		Event evt = mockEventCallbackImplOne.getEvt();
		// if the method times out, test will fail
		assertNull(evt);
		System.out
				.println("Event received by Consumer1 callback1 is NULL - Reason:  Consumer had unsubscribed from Topic with callback1 and no messages were posted");
		System.out.println("Event received by {Consumer1 callback1} = [" + evt
				+ ']');

		// Get the result from second callback
		Event secondEvt = mockEventCallbackImplTwo.getEvt();
		// if the method times out, test will fail
		assertNull(secondEvt);
		System.out
				.println("Event received by Consumer1 callback2 is NULL - Reason:  Consumer had unsubscribed from Topic with callback2 and no messages were posted");

		System.out.println("Event received by {Consumer1 callback2} = ["
				+ secondEvt + "]");

		// Get the result from third callback

		Event thirdEvt = mockEventCallbackImplTwo.getEvt();
		// if the method times out, test will fail
		assertNull(thirdEvt);
		System.out
				.println("Event received by Consumer2 callback1 is NULL - Reason:  Consumer had unsubscribed from Topic with callback1 and no messages were posted");
		System.out.println("Event received by {Consumer2 callback1} = ["
				+ thirdEvt + "]");

		// Get the result from fourth callback
		Event fourthEvt = mockEventCallbackImplTwo.getEvt();
		// if the method times out, test will fail
		assertNull(fourthEvt);
		System.out
				.println("Event received by Consumer2 callback2 is NULL - Reason:  Consumer had unsubscribed from Topic with callback1 and no messages were posted");
		System.out.println("Event received by {Consumer2 callback2} = ["
				+ fourthEvt + "]");

		System.out
				.println("######################  UNSUBSCRIBE-ALL-WITHOUT-POST TEST [END] ######################### ");

	}

}
