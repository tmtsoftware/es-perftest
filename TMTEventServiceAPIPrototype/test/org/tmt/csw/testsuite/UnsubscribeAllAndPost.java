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
import org.tmt.csw.eventservice.exception.EventPublishException;
import org.tmt.csw.eventservice.exception.EventSubscriptionException;
import org.tmt.csw.eventservice.exception.EventUnSubscriptionException;
import org.tmt.csw.eventservice.util.Constants;
import org.tmt.csw.eventservice.callback.EventCallbackImplOne;
import org.tmt.csw.eventservice.callback.EventCallbackImplTwo;

public class UnsubscribeAllAndPost extends TestCase {
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
	 * 7) Unsubscribe all consumer callbacks and Post Message --> Consumer
	 * should not receive message
	 */
	@Test
	public void testUnsubscribeAllAndPost()
			throws EventUnSubscriptionException, EventPublishException,
			EventSubscriptionException, InterruptedException {
		System.out
				.println("******************* [7] [STARTING] UNSUBSCRIBE-ALL-AND-POST TEST ************************ ");
		// Create call-back
		EventCallbackImplOne mockEventCallbackImplOne = new EventCallbackImplOne();
		EventCallbackImplTwo mockEventCallbackImplTwo = new EventCallbackImplTwo();
		EventCallbackImplOne mockEventCallbackImplThree = new EventCallbackImplOne();
		EventCallbackImplTwo mockEventCallbackImplFour = new EventCallbackImplTwo();

		// Create EventService Object
		EventService eventService = AbstractEventService.createEventService();

		EventConsumerOne consumerOne = new EventConsumerOne();
		//Consumer1 subscribes to a Topic with 2 callbacks
		System.out.println("Consumer1 subscribes to a Topic with 2 callbacks");
		consumerOne.subscribe(eventService, Constants.TMT_MOBI_BLUE_FILTER,
				mockEventCallbackImplOne);
		consumerOne.subscribe(eventService, Constants.TMT_MOBI_BLUE_FILTER,
				mockEventCallbackImplTwo);

		EventConsumerTwo consumerTwo = new EventConsumerTwo();
		// Subscribe to a Topic using second consumer with call backs -
		// mockEventCallbackImplThree,mockEventCallbackImplFour
		System.out.println("Consumer2 subscribes to a Topic with 2 callbacks");
		consumerTwo.subscribe(eventService, Constants.TMT_MOBI_BLUE_FILTER,
				mockEventCallbackImplThree);
		consumerTwo.subscribe(eventService, Constants.TMT_MOBI_BLUE_FILTER,
				mockEventCallbackImplFour);

		// unsubscribe from all callbacks
		System.out.println("Consumer1 unsubscribeAll--");
		consumerOne
				.unSubscribeAll(eventService, Constants.TMT_MOBI_BLUE_FILTER);
		System.out.println("Consumer:2 unsubscribeAll--");
		consumerTwo
				.unSubscribeAll(eventService, Constants.TMT_MOBI_BLUE_FILTER);

		// Create a Message (Event Object)
		Event event = testConstants.createEventObject("7",
				"unsubscriberAllAndPost");
		// Post a Message to a Topic after both consumers unsubscribed from the topic
		eventService.post(Constants.TMT_MOBI_BLUE_FILTER, event);

		// Wait for call-back to receive message
		Thread.sleep(TestUtils.SLEEP_TIME);
		// Get the results from callback
		Event evt = mockEventCallbackImplOne.getEvt();
		// if the method times out, test will fail
		assertNull(evt);
		System.out
				.println("Event received by Consumer1 callback1 is NULL - Consumer had unsubscribed from Topic with callback1");
		System.out.println("Event received by {Consumer1 callback1} = ["
				+ evt + "]");
		

		// Get the result from second callback
		Event secondEvt = mockEventCallbackImplTwo.getEvt();
		assertNull(secondEvt);
		System.out
		.println("Event received by Consumer1 callback2 is NULL - Consumer had unsubscribed from Topic ");
		System.out.println("Event received by {Consumer1 callback2} = ["
				+ secondEvt + "]");

		// Get the result from third callback
		Event thirdEvt = mockEventCallbackImplThree.getEvt();
		assertNull(thirdEvt);
		System.out
		.println("Event received by Consumer2 callback1 is NULL - Consumer had unsubscribed from Topic ");
		System.out.println("Event received by {Consumer2 callback1} = ["
		+ evt + "]");

		// Get the result from fourth callback
		Event fourthEvt = mockEventCallbackImplFour.getEvt();
		assertNull(fourthEvt);
		System.out
		.println("Event received by Consumer2 callback2 is NULL - Consumer had unsubscribed from Topic ");
		System.out.println("Event received by {Consumer2 callback2} = ["
		+ fourthEvt + "]");

		System.out
				.println("###################### UNSUBSCRIBE-ALL-AND-POST TEST [END] ######################### ");

	}
}
