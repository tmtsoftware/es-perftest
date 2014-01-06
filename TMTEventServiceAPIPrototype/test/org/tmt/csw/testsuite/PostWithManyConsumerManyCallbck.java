package org.tmt.csw.testsuite;

import java.util.Map;
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

/**
 * Post Message with multiple consumers and multiple callbacks --> Only
 * one callback of each consumer will receive message
 * 
 */
public class PostWithManyConsumerManyCallbck extends TestCase {
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
	 * 5) Post Message with multiple consumers and multiple callbacks --> Only
	 * one callback of each consumer will receive message
	 * 
	 */
	@Test
	public void testPostWithManyConsumerManyCallbck()
			throws EventPublishException, EventSubscriptionException,
			InterruptedException, EventUnSubscriptionException {
		System.out
				.println("******************* [5] [STARTING] POST-WITH-MANY-CONSUMER-MANY-CALLBACK TEST ************************ ");
		// Create call-back
		EventCallbackImplOne mockEventCallbackImplOne = new EventCallbackImplOne();
		EventCallbackImplTwo mockEventCallbackImplTwo = new EventCallbackImplTwo();
		EventCallbackImplOne mockEventCallbackImplThree = new EventCallbackImplOne();
		EventCallbackImplTwo mockEventCallbackImplFour = new EventCallbackImplTwo();

		// Create EventService Object
		EventService eventService = AbstractEventService.createEventService();

		EventConsumerOne consumerOne = new EventConsumerOne();
		// consumerOne Subscribes to a Topic with two call backs -
		// mockEventCallbackImplOne,mockEventCallbackImplTwo
		System.out.println("consumerOne Subscribes to a Topic with two call backs");
		consumerOne.subscribe(eventService, Constants.TMT_MOBI_BLUE_FILTER,
				mockEventCallbackImplOne);
		consumerOne.subscribe(eventService, Constants.TMT_MOBI_BLUE_FILTER,
				mockEventCallbackImplTwo);

		EventConsumerTwo consumerTwo = new EventConsumerTwo();
		// consumerTwo Subscribes to a Topic with two call backs -
		// mockEventCallbackImplThree,mockEventCallbackImplFour
		System.out.println("consumerTwo Subscribes to a Topic with two call backs -");
		consumerTwo.subscribe(eventService, Constants.TMT_MOBI_BLUE_FILTER,
				mockEventCallbackImplThree);
		consumerTwo.subscribe(eventService, Constants.TMT_MOBI_BLUE_FILTER,
				mockEventCallbackImplFour);

		// Create a Message (Event Object)
		Event event = testConstants.createEventObject("5",
				"postWithManyConsumerManyCallbck");
		// Post a Message to a Topic
		eventService.post(Constants.TMT_MOBI_BLUE_FILTER, event);

		// Wait for call-back to receive message
		Thread.sleep(TestUtils.SLEEP_TIME);

		// Get the results for consumerOne callbacks
		Event evt = mockEventCallbackImplOne.getEvt();
		Event secondEvt = mockEventCallbackImplTwo.getEvt();
		if (evt == null && secondEvt == null) {
			fail("None of callbacks of consumerOne  receive event");
		}
		if (evt != null || secondEvt != null) {
			if (evt != null) {
				//Get Events from callbacks
				assertNotNull(
						"Event not received by ConsumerOne callback-1 - Possible reason is time out",
						evt);

				System.out
						.println("Event received by ConsumerOne callback-1  = ["
								+ evt + "]");
				// otherwise, check the results (Message contents)
				Map<String, Object> map = evt.getPayload();
				String value = (String) map.get("5");
				assertEquals(
						"The sending & receiving message contents doesn't match  ",
						"postWithManyConsumerManyCallbck", value);
				System.out
						.println("Message received by ConsumerOne callback-1 = ["
								+ value + "]");

			}
			if (secondEvt != null) {
				//Get Events from callbacks
				assertNotNull(
						"Event not received by ConsumerOne callback-2 - Possible reason is time out",
						secondEvt);
				System.out
						.println("Event received by ConsumerOne callback-2 = ["
								+ secondEvt + "]");
				Map<String, Object> secondMap = secondEvt.getPayload();
				String secondValue = (String) secondMap.get("5");
				assertEquals(
						"The sending & receiving message contents doesn't match  ",
						"postWithManyConsumerManyCallbck", secondValue);
				System.out
						.println("Message received by ConsumerOne callback-2 = ["
								+ secondValue + "]");
			}
		}

		// Get the results for consumer-2
		// Wait for call-back to receive message
		Thread.sleep(TestUtils.SLEEP_TIME);
		Event thirdEvt = mockEventCallbackImplThree.getEvt();
		Event fourthEvt = mockEventCallbackImplFour.getEvt();
		if (thirdEvt == null && secondEvt == null) {
			fail("None of callbacks of consumer2  receive event");
		}
		if (thirdEvt != null || fourthEvt != null) {
			if (thirdEvt != null) {
				// Get event from Topic with Consumer2 Third call back -
				// mockEventCallbackImplThree
				assertNotNull(
						"Event not received by ConsumerTwo callback-1 - Possible reason is time out",
						thirdEvt);
				System.out
						.println("Event received by ConsumerTwo callback-1 = ["
								+ thirdEvt + "]");
				Map<String, Object> thirdMap = thirdEvt.getPayload();
				String thirdValue = (String) thirdMap.get("5");
				assertEquals(
						"The sending & receiving message contents doesn't match  ",
						"postWithManyConsumerManyCallbck", thirdValue);
				System.out
						.println("Message received by ConsumerTwo callback-1 = ["
								+ thirdValue + "]");

			}
			if (fourthEvt != null) {
				assertNotNull(
						// Get event from Topic with Fourth call back -
						// mockEventCallbackImplFour
						"Event not received by callback - Possible reason is time out",
						fourthEvt);
				System.out
						.println("Event received by ConsumerTwo callback-2 = ["
								+ fourthEvt + "]");
				Map<String, Object> fourthMap = fourthEvt.getPayload();
				String fourthValue = (String) fourthMap.get("5");
				assertEquals(
						"The sending & receiving message contents doesn't match  ",
						"postWithManyConsumerManyCallbck", fourthValue);
				System.out
						.println("Message received by ConsumerTwo callback-2 = ["
								+ fourthValue + "]");
			}
		}

		// unsubscribe from all callbacks - cleanup
		consumerOne.unSubscribe(eventService, Constants.TMT_MOBI_BLUE_FILTER,
				mockEventCallbackImplOne);
		consumerOne.unSubscribe(eventService, Constants.TMT_MOBI_BLUE_FILTER,
				mockEventCallbackImplTwo);

		consumerTwo.unSubscribe(eventService, Constants.TMT_MOBI_BLUE_FILTER,
				mockEventCallbackImplThree);
		consumerTwo.unSubscribe(eventService, Constants.TMT_MOBI_BLUE_FILTER,
				mockEventCallbackImplFour);

		System.out
				.println("###################### POST-WITH-MANY-CONSUMER-MANY-CALLBACK TEST [END] ######################### ");

	}
}
