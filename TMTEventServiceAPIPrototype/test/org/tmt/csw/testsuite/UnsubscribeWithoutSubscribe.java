package org.tmt.csw.testsuite;

import junit.framework.TestCase;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.tmt.csw.consumerone.EventConsumerOne;
import org.tmt.csw.eventservice.AbstractEventService;
import org.tmt.csw.eventservice.EventService;
import org.tmt.csw.eventservice.util.Constants;
import org.tmt.csw.eventservice.callback.EventCallbackImplOne;
import org.tmt.csw.eventservice.exception.EventPublishException;
import org.tmt.csw.eventservice.exception.EventUnSubscriptionException;

public class UnsubscribeWithoutSubscribe extends TestCase {

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
	 * 13) Unsubscribe a Callback for a consumer without subscription --> The
	 * EventService should throw exception
	 * 
	 * @throws EventPublishException
	 * @throws InterruptedException
	 * 
	 */
	@Test
	public void testUnsubscribeWithoutSubscribe() throws EventPublishException,
			InterruptedException, EventUnSubscriptionException {
		System.out
				.println("******************* [13] [STARTING] UNSUBSCRIBE-WITHOUT-SUBSCRIBE TEST ************************ ");
		// Create call-back
		EventCallbackImplOne mockEventCallbackImplOne = new EventCallbackImplOne();
		// Create EventService Object
		EventService eventService = AbstractEventService.createEventService();
		EventConsumerOne consumerOne = new EventConsumerOne();

		boolean caughtException = false;

		try {
			// unsubscribe from callbacks
			consumerOne.unSubscribe(eventService,
					Constants.TMT_MOBI_BLUE_FILTER, mockEventCallbackImplOne);
		} catch (Exception e) {
			caughtException = true;
		}

		if (caughtException) {
			assertTrue("Unsubscribed", caughtException);
			System.out
					.println("Exception occured while unsubscribing | Consumer tried to unsubscribe to a Topic with a callback whereas it did not subscribed at all to that topic with that callback");
		} else {
			assertFalse("Unsubscription failed", caughtException);
			System.out.println("Sucessfully unsubscribed");
		}

		System.out
				.println("######################   UNSUBSCRIBE-WITHOUT-SUBSCRIBE TEST [END] ######################### ");

	}

}
