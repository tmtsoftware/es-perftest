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

public class SubscribeManyConsumerWithoutPost extends TestCase {

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
	 * 9) Multiple consumers subscribes with multiple callbacks without
	 * publisher posting a message --> the consumer callbacks should receive
	 * NULL
	 */

	@Test
	public void testSubscribeManyConsumerWithoutPost() {
		System.out
				.println("******************* [9] [STARTING] SUBSCRIBE-WITH-MANY-CONSUMERS-WITHOUT-POST TEST ************************ ");
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
		System.out.println("Consumer1 subscribes with 2 callbacks");
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
		// mockEventCallbackImplOne,mockEventCallbackImplTwo
		System.out.println("Consumer2 subscribes with 2 callbacks");
		try {
			consumerTwo.subscribe(eventService, Constants.TMT_MOBI_BLUE_FILTER,
					mockEventCallbackImplThree);
			consumerTwo.subscribe(eventService, Constants.TMT_MOBI_BLUE_FILTER,
					mockEventCallbackImplFour);
		} catch (EventSubscriptionException eSub) {
			fail("Error while Event Subscription for consumerTwo");

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
				.println("Event received by Consumer1 callback1 is NULL - Reason is subscription without post");
		System.out.println("Event received by {Consumer1 callback1} = [ " + evt
				+ "]");

		// Get event from Topic with second call back - mockEventCallbackImplTwo
		// Get the results from callback
		Event secondEvt = mockEventCallbackImplTwo.getEvt();
		// if the method times out, test will fail
		assertNull(secondEvt);
		System.out
				.println("Event received by Consumer1 callback2 is NULL - Reason is subscription without post");
		System.out.println("Event received by {Consumer1 callback2} = [ "
				+ secondEvt + "]");

		// Get event from Topic with third callback
		Event thirdEvt = mockEventCallbackImplThree.getEvt();
		// if the method times out, test will fail
		assertNull(thirdEvt);
		System.out
				.println("Event received by Consumer2 callback1 is NULL - Reason is subscription without post");
		System.out.println("Event received by {Consumer2 callback1} = ["
				+ thirdEvt + "]");

		// Get event from Topic with fourth call back -
		// mockEventCallbackImplFour
		Event fourthEvt = mockEventCallbackImplFour.getEvt();
		// if the method times out, test will fail
		assertNull(fourthEvt);
		System.out
				.println("Event received by Consumer2 callback2 is NULL - Reason is subscription without post");
		System.out.println("Event received by {Consumer2 callback2} = ["
				+ fourthEvt + "]");
		// unsubscribe from all callbacks - cleanup
		try {
			consumerOne.unSubscribe(eventService,
					Constants.TMT_MOBI_BLUE_FILTER, mockEventCallbackImplOne);
			consumerOne.unSubscribe(eventService,
					Constants.TMT_MOBI_BLUE_FILTER, mockEventCallbackImplTwo);
			consumerTwo.unSubscribe(eventService,
					Constants.TMT_MOBI_BLUE_FILTER, mockEventCallbackImplThree);
			consumerTwo.unSubscribe(eventService,
					Constants.TMT_MOBI_BLUE_FILTER, mockEventCallbackImplFour);
		} catch (EventUnSubscriptionException eUnsub) {
			fail("Error while Event UnSubscription");
		}
		System.out
				.println("###################### SUBSCRIBE-WITH-MANY-CONSUMERS-WITHOUT-POST TEST [END] ######################### ");

	}
}
