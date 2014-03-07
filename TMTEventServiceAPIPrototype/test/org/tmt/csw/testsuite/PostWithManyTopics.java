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
 * Post Message with multiple consumers, multiple topics & each topic have one
 * callback --> each consumer callback with specific topic will receive message
 * 
 */
public class PostWithManyTopics extends TestCase {
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
	 * 15) Post Message with multiple consumers, multiple topics & each topic
	 * have one callback --> each consumer callback with specific topic will
	 * receive message
	 * 
	 */
	@Test
	public void testPostWithManyTopics() {
		System.out
				.println("******************* [15] [STARTING] POST-WITH-MANY-TOPICS TEST ************************ ");
		// Create call-back
		EventCallbackImplOne mockEventCallbackImplOne = new EventCallbackImplOne();
		EventCallbackImplTwo mockEventCallbackImplTwo = new EventCallbackImplTwo();
		EventCallbackImplOne mockEventCallbackImplThree = new EventCallbackImplOne();
		EventCallbackImplTwo mockEventCallbackImplFour = new EventCallbackImplTwo();

		// Create EventService Object
		EventService eventService = AbstractEventService.createEventService();

		EventConsumerOne consumerOne = new EventConsumerOne();
		// consumerOne Subscribes to different Topic with different call backs -
		// mockEventCallbackImplOne & mockEventCallbackImplTwo

		try {
			System.out
					.println("consumerOne Subscribes to a Topic [org.tmt.mobie.blue.filter] with one call back");
			consumerOne.subscribe(eventService, Constants.TMT_MOBI_BLUE_FILTER,
					mockEventCallbackImplOne);
			System.out
					.println("consumerOne Subscribes to a Topic [org.tmt.mobie.red.filter] with one call back");
			consumerOne.subscribe(eventService, Constants.TMT_MOBI_RED_FILTER,
					mockEventCallbackImplTwo);
		} catch (EventSubscriptionException eSub) {
			fail("Error while Event Subscription for consumerOne");

		}

		EventConsumerTwo consumerTwo = new EventConsumerTwo();
		// consumerTwo Subscribes to a different Topic with different call backs
		// mockEventCallbackImplThree,mockEventCallbackImplFour

		try {
			System.out
					.println("consumerTwo Subscribes to a Topic  [org.tmt.mobie.blue.filter] with one call back ");
			consumerTwo.subscribe(eventService, Constants.TMT_MOBI_BLUE_FILTER,
					mockEventCallbackImplThree);
			System.out
					.println("consumerTwo Subscribes to a Topic  [org.tmt.mobie.red.filter] with one call back");
			consumerTwo.subscribe(eventService, Constants.TMT_MOBI_RED_FILTER,
					mockEventCallbackImplFour);

		} catch (EventSubscriptionException eSub) {
			fail("Error while Event Subscription for consumerTwo ");

		}

		// Create a Message (Event Object)
		Event eventBlue = testConstants.createEventObject("100",
				"postWithManyTopics@org.tmt.mobie.blue.filter@");
		// Post a Message to a Topic [org.tmt.mobie.blue.filter]
		try {
			eventService.post(Constants.TMT_MOBI_BLUE_FILTER, eventBlue);
		} catch (EventPublishException ePost) {
			fail("Error while Message Post with topic [org.tmt.mobie.blue.filter]");
		}

		// Create a Message (Event Object)
		Event eventRed = testConstants.createEventObject("200",
				"postWithManyTopics*org.tmt.mobie.red.filter*");
		// Post a Message to a Topic [org.tmt.mobie.red.filter]
		try {
			eventService.post(Constants.TMT_MOBI_RED_FILTER, eventRed);
		} catch (EventPublishException ePost) {
			fail("Error while Message Post with topic [org.tmt.mobie.red.filter]");
		}

		// Wait for call-back to receive message
		try {
			Thread.sleep(TestUtils.SLEEP_TIME);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		// Consumer1 for topic [org.tmt.mobie.blue.filter]
		// Get the results from callback
		Event evtBlue = mockEventCallbackImplOne.getEvt();
		// if the method times out, test will fail
		assertNotNull(
				"Event not received by ConsumerOne callback - Possible reason is time out",
				evtBlue);
		System.out.println("Event received by ConsumerOne Callback = [ "
				+ evtBlue + "]");
		// otherwise, check the results (Message contents)
		Map<String, Object> mapBlue = evtBlue.getPayload();
		String valueBlue = (String) mapBlue.get("100");
		assertEquals(
				"The sending & receiving message contents doesn't match  ",
				"postWithManyTopics@org.tmt.mobie.blue.filter@", valueBlue);
		System.out.println("Message received by ConsumerOne Callback = ["
				+ valueBlue + "]");
		// //////////////////////////////////////////////////////////////////////////////////////
		// Consumer1 for topic [org.tmt.mobie.red.filter]
		// Get the results from callback
		Event evtRed = mockEventCallbackImplTwo.getEvt();
		// if the method times out, test will fail
		assertNotNull(
				"Event not received by ConsumerOne callback - Possible reason is time out",
				evtRed);
		System.out.println("Event received by ConsumerOne Callback = [ "
				+ evtRed + "]");
		// otherwise, check the results (Message contents)
		Map<String, Object> mapRed = evtRed.getPayload();
		String valueRed = (String) mapRed.get("200");
		assertEquals(
				"The sending & receiving message contents doesn't match  ",
				"postWithManyTopics*org.tmt.mobie.red.filter*", valueRed);
		System.out.println("Message received by ConsumerOne Callback = ["
				+ valueRed + "]");

		// //////////////////////////////////////////////////////////////////////////////////////
		// Consumer2 for topic [org.tmt.mobie.blue.filter]
		// Get the results from callback
		Event secondEvtBlue = mockEventCallbackImplThree.getEvt();
		assertNotNull(
				"Event not received by ConsumerTwo callback - Possible reason is time out",
				secondEvtBlue);
		System.out.println("Event received by ConsumerTwo callback = [ "
				+ secondEvtBlue + "]");

		Map<String, Object> secondMapBlue = secondEvtBlue.getPayload();
		String secondValueBlue = (String) secondMapBlue.get("100");
		assertEquals(
				"The sending & receiving message contents doesn't match  ",
				"postWithManyTopics@org.tmt.mobie.blue.filter@",
				secondValueBlue);
		System.out.println("Message received by ConsumerTwo callback = ["
				+ secondValueBlue + "]");

		// //////////////////////////////////////////////////////////////////////////////////////
		// Consumer2 for topic [org.tmt.mobie.red.filter]
		// Get the results from callback
		Event secondEvtRed = mockEventCallbackImplFour.getEvt();
		assertNotNull(
				"Event not received by ConsumerTwo callback - Possible reason is time out",
				secondEvtRed);
		System.out.println("Event received by ConsumerTwo callback = [ "
				+ secondEvtRed + "]");

		Map<String, Object> secondMapRed = secondEvtRed.getPayload();
		String secondValueRed = (String) secondMapRed.get("200");
		assertEquals(
				"The sending & receiving message contents doesn't match  ",
				"postWithManyTopics*org.tmt.mobie.red.filter*", secondValueRed);
		System.out.println("Message received by ConsumerTwo callback = ["
				+ secondValueRed + "]");

		// unsubscribe from all callbacks - cleanup
		try {
			System.out
					.println("Consumer-1 Unsubscribe from topic [org.tmt.mobie.blue.filter]");

			consumerOne.unSubscribe(eventService,
					Constants.TMT_MOBI_BLUE_FILTER, mockEventCallbackImplOne);
			System.out
					.println("Consumer-1 Unsubscribe from topic [org.tmt.mobie.red.filter]");
			consumerOne.unSubscribe(eventService,
					Constants.TMT_MOBI_RED_FILTER, mockEventCallbackImplTwo);

			System.out
					.println("Consumer-2 unsubscribe from topic [org.tmt.mobie.blue.filter]");

			consumerTwo.unSubscribe(eventService,
					Constants.TMT_MOBI_BLUE_FILTER, mockEventCallbackImplThree);
			System.out
					.println("Consumer-2 unsubscribe from topic [org.tmt.mobie.red.filter]");
			consumerTwo.unSubscribe(eventService,
					Constants.TMT_MOBI_RED_FILTER, mockEventCallbackImplFour);

		} catch (EventUnSubscriptionException eUnsub) {
			fail("Error while Event UnSubscription");
		}
		System.out
				.println("###################### POST-WITH-MANY-TOPICS TEST [END] ######################### ");

	}
}
