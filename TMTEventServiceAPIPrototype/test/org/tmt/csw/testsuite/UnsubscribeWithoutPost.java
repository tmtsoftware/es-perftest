package org.tmt.csw.testsuite;

import junit.framework.TestCase;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.tmt.csw.consumerone.EventConsumerOne;
import org.tmt.csw.eventservice.AbstractEventService;
import org.tmt.csw.eventservice.EventService;
import org.tmt.csw.eventservice.callback.EventCallbackImplOne;
import org.tmt.csw.eventservice.callback.EventCallbackImplTwo;
import org.tmt.csw.eventservice.event.Event;
import org.tmt.csw.eventservice.exception.EventSubscriptionException;
import org.tmt.csw.eventservice.exception.EventUnSubscriptionException;
import org.tmt.csw.eventservice.util.Constants;

public class UnsubscribeWithoutPost extends TestCase {

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
	 * 11) Unsubscribe a Callback for a consumer without posting a message-->
	 * The unregistered callback should receive the NULL
	 */
	@Test
	public void testUnsubscribeOneConsumerWithoutPost() {
		System.out
				.println("******************* [11] [STARTING] UNSUBSCRIBE-ONE-CONSUMER-WITHOUT-POST TEST ************************ ");
		// Create call-back
		EventCallbackImplOne mockEventCallbackImplOne = new EventCallbackImplOne();
		EventCallbackImplTwo mockEventCallbackImplTwo = new EventCallbackImplTwo();

		// Create EventService Object
		EventService eventService = AbstractEventService.createEventService();

		EventConsumerOne consumerOne = new EventConsumerOne();
		// Subscribe to a Topic with call backs -
		// mockEventCallbackImplOne,mockEventCallbackImplTwo
		System.out
				.println("Consumer Subscribe to a Topic with two call backs ");
		try {
			consumerOne.subscribe(eventService, Constants.TMT_MOBI_BLUE_FILTER,
					mockEventCallbackImplOne);
			consumerOne.subscribe(eventService, Constants.TMT_MOBI_BLUE_FILTER,
					mockEventCallbackImplTwo);
		} catch (EventSubscriptionException eSub) {
			fail("Error while Event Subscription for consumerOne");

		}

		// Consumer unsubscribes from one callback i.e mockEventCallbackImplOne
		try {
			consumerOne.unSubscribe(eventService,
					Constants.TMT_MOBI_BLUE_FILTER, mockEventCallbackImplOne);
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
				.println("Event received by Consumer callback is NULL - Reason : No messages were posted by publisher and Consumer also unsubscribed from the topic with that callback");
		System.out.println("Event received by {mockEventCallbackImplOne} = ["
				+ evt + "]");

		// Get event from second callback
		Event secondEvt = mockEventCallbackImplTwo.getEvt();
		// if the method times out, test will fail
		assertNull(secondEvt);
		System.out
				.println("Event received by Consumer callback is NULL - Reason : No messages were posted by publisher and Consumer also unsubscribed from the topic with that callback");
		System.out.println("Event received by {mockEventCallbackImplTwo} = ["
				+ secondEvt + "]");

		// unsubscribe from callbacks - cleanup
		try {
			consumerOne.unSubscribe(eventService,
					Constants.TMT_MOBI_BLUE_FILTER, mockEventCallbackImplTwo);
		} catch (EventUnSubscriptionException eUnsub) {
			fail("Error while Event UnSubscription");
		}

		System.out
				.println("######################  UNSUBSCRIBE-ONE-CONSUMER-WITHOUT-POST TEST [END] ######################### ");

	}

}
