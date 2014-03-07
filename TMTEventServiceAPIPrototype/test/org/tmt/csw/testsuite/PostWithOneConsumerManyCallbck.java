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
 * Post Message with one consumer and multiple callbacks--> Only one callback of
 * consumer will receive message
 */

public class PostWithOneConsumerManyCallbck extends TestCase {

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
	 * 4) Post Message with one consumer and multiple callbacks--> Only one
	 * callback of consumer will receive message
	 */

	@Test
	public void testPostWithOneConsumerManyCallbck() {
		System.out
				.println("******************* [4] [STARTING] POST-WITH-ONE-CONSUMER-MANY-CALLBACK TEST ************************ ");
		// Create multiple call-backs
		EventCallbackImplOne mockEventCallbackImplOne = new EventCallbackImplOne();
		EventCallbackImplTwo mockEventCallbackImplTwo = new EventCallbackImplTwo();

		// Create EventService Object
		EventService eventService = AbstractEventService.createEventService();

		// consumerOne Subscribe to a Topic with two call backs
		// -mockEventCallbackImplOne,mockEventCallbackImplTwo
		EventConsumerOne consumerOne = new EventConsumerOne();
		System.out
				.println("consumer Subscribes to a Topic with two call backs");
		try {
			consumerOne.subscribe(eventService, Constants.TMT_MOBI_BLUE_FILTER,
					mockEventCallbackImplOne);
			consumerOne.subscribe(eventService, Constants.TMT_MOBI_BLUE_FILTER,
					mockEventCallbackImplTwo);
		} catch (EventSubscriptionException eSub) {
			fail("Error while Event Subscription for consumerOne");

		}

		// Create a Message
		Event event = testConstants.createEventObject("4",
				"postWithOneConsumerManyCallbck");

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

		// Get the results from consumer-1
		Event evt = mockEventCallbackImplOne.getEvt();
		Event secondEvt = mockEventCallbackImplTwo.getEvt();
		if (evt == null && secondEvt == null) {
			fail("None of callbacks of consumer  received event");
		}
		if (evt != null || secondEvt != null) {
			if (evt != null) {
				// Get event from callbacks
				assertNotNull(
						"Event not received by Consumer callback-1 - Possible reason is time out",
						evt);

				System.out.println("Event received by Consumer callback-1 = ["
						+ evt + "]");
				// otherwise, check the results (Message contents)
				Map<String, Object> map = evt.getPayload();
				// Map map = payloadResult.getKeyAndValues();
				String value = (String) map.get("4");
				assertEquals(
						"The sending & receiving message contents doesn't match  ",
						"postWithOneConsumerManyCallbck", value);
				System.out
						.println("Message received by Consumer callback-1 = ["
								+ value + "]");

			}
			if (secondEvt != null) {
				// Get event from callbacks
				assertNotNull(
						"Event not received by Consumer callback-2 - Possible reason is time out",
						secondEvt);
				System.out.println("Event received by Consumer callback-2 = ["
						+ secondEvt + "]");
				Map<String, Object> secondMap = secondEvt.getPayload();
				String secondValue = (String) secondMap.get("4");
				assertEquals(
						"The sending & receiving message contents doesn't match  ",
						"postWithOneConsumerManyCallbck", secondValue);
				System.out
						.println("Message received by Consumer callback-2 = ["
								+ secondValue + "]");
			}
		}

		// unsubscribe from all callbacks - cleanup
		try {
			consumerOne.unSubscribe(eventService,
					Constants.TMT_MOBI_BLUE_FILTER, mockEventCallbackImplOne);
			consumerOne.unSubscribe(eventService,
					Constants.TMT_MOBI_BLUE_FILTER, mockEventCallbackImplTwo);
		} catch (EventUnSubscriptionException eUnsub) {
			fail("Error while Event UnSubscription");
		}
		System.out
				.println("###################### POST-WITH-ONE-CONSUMER-MANY-CALLBACK TEST [END] ######################### ");
	}

}
