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
 * Post Message with multiple consumers (each with one callback)--> All
 * consumers should receive the message through their callbacks
 * 
 * @throws EventUnSubscriptionException
 */

public class PostWithManyConsumer extends TestCase {
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
	 * 3) Post Message with multiple consumers (each with one callback)--> All
	 * consumers should receive the message through their callbacks
	 * 
	 * @throws EventUnSubscriptionException
	 */

	@Test
	public void testPostWithManyConsumer() throws EventPublishException,
			EventSubscriptionException, InterruptedException,
			EventUnSubscriptionException {
		System.out
				.println("******************* [3] [STARTING] POST-WITH-MANY-CONSUMER TEST ************************ ");
		// Create call-back
		EventCallbackImplOne mockEventCallbackImplOne = new EventCallbackImplOne();
		EventCallbackImplTwo mockEventCallbackImplTwo = new EventCallbackImplTwo();

		// Create EventService Object
		EventService eventService = AbstractEventService.createEventService();

		EventConsumerOne consumerOne = new EventConsumerOne();
		// ConsumerOne Subscribe to a Topic with a call back -mockEventCallbackImplOne
		System.out.println("ConsumerOne Subscribe to a Topic with a call back");
		consumerOne.subscribe(eventService, Constants.TMT_MOBI_BLUE_FILTER,
				mockEventCallbackImplOne);

		EventConsumerTwo consumerTwo = new EventConsumerTwo();
		//ConsumerTwo Subscribe to a Topic with call back - mockEventCallbackImplTwo
		System.out.println("ConsumerTwo Subscribe to a Topic with call back");
		consumerTwo.subscribe(eventService, Constants.TMT_MOBI_BLUE_FILTER,
				mockEventCallbackImplTwo);

		// Create a Message (Event Object)
		Event event = testConstants.createEventObject("3",
				"postWithManyConsumer");
		// Post a Message to a Topic
		eventService.post(Constants.TMT_MOBI_BLUE_FILTER, event);

		// Wait for call-back to receive message
		Thread.sleep(TestUtils.SLEEP_TIME);
		// Get the results from callback
		Event evt = mockEventCallbackImplOne.getEvt();
		// if the method times out, test will fail
		assertNotNull(
				"Event not received by ConsumerOne callback - Possible reason is time out",
				evt);
		System.out.println("Event received by ConsumerOne Callback = [ "
				+ evt + "]");
		// otherwise, check the results (Message contents)
		Map<String, Object> map = evt.getPayload();
		String value = (String) map.get("3");
		assertEquals(
				"The sending & receiving message contents doesn't match  ",
				"postWithManyConsumer", value);
		System.out.println("Message received by ConsumerOne Callback = ["
				+ value + "]");

		// //////////////////////////////////////////////////////////////////////////////////////
		// Get event from Topic with call back - mockEventCallbackImplTwo
		Event secondEvt = mockEventCallbackImplTwo.getEvt();
		assertNotNull(
				"Event not received by ConsumerTwo callback - Possible reason is time out",
				secondEvt);
		System.out.println("Event received by ConsumerTwo callback = [ "
				+ secondEvt + "]");

		Map<String, Object> secondMap = secondEvt.getPayload();
		String secondValue = (String) secondMap.get("3");
		assertEquals(
				"The sending & receiving message contents doesn't match  ",
				"postWithManyConsumer", secondValue);
		System.out.println("Message received by ConsumerTwo callback = ["
				+ secondValue + "]");

		// cleanup
		consumerOne.unSubscribe(eventService, Constants.TMT_MOBI_BLUE_FILTER,
				mockEventCallbackImplOne);
		consumerTwo.unSubscribe(eventService, Constants.TMT_MOBI_BLUE_FILTER,
				mockEventCallbackImplTwo);

		System.out
				.println("###################### POST-WITH-MANY-CONSUMER TEST [END] ######################### ");
	}

}
