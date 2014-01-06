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
import org.tmt.csw.eventservice.exception.EventPublishException;
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
	 * 10) Unsubscribe a Callback for a consumer without posting a message-->
	 * The unregistered callback should receive the NULL
	 */
	@Test
	public void testUnsubscribeOneConsumerWithoutPost()
			throws EventPublishException, EventSubscriptionException,
			InterruptedException, EventUnSubscriptionException {

		System.out
				.println("******************* [10] [STARTING] UNSUBSCRIBE-ONE-CONSUMER-WITHOUT-POST TEST ************************ ");
		// Create call-back
		EventCallbackImplOne mockEventCallbackImplOne = new EventCallbackImplOne();
		EventCallbackImplTwo mockEventCallbackImplTwo = new EventCallbackImplTwo();

		// Create EventService Object
		EventService eventService = AbstractEventService.createEventService();

		EventConsumerOne consumerOne = new EventConsumerOne();
		// Subscribe to a Topic with call backs -
		// mockEventCallbackImplOne,mockEventCallbackImplTwo
		System.out.println("Consumer Subscribe to a Topic with two call backs ");
		consumerOne.subscribe(eventService, Constants.TMT_MOBI_BLUE_FILTER,
				mockEventCallbackImplOne);
		consumerOne.subscribe(eventService, Constants.TMT_MOBI_BLUE_FILTER,
				mockEventCallbackImplTwo);

		// Consumer unsubscribes from one callback i.e mockEventCallbackImplOne
		consumerOne.unSubscribe(eventService, Constants.TMT_MOBI_BLUE_FILTER,
				mockEventCallbackImplOne);

		// Wait for call-back to receive message
		Thread.sleep(TestUtils.SLEEP_TIME);
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
		consumerOne.unSubscribe(eventService, Constants.TMT_MOBI_BLUE_FILTER,
				mockEventCallbackImplTwo);

		System.out
				.println("######################  UNSUBSCRIBE-ONE-CONSUMER-WITHOUT-POST TEST [END] ######################### ");

	}

}
