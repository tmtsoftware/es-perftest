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
	 */
	@Test
	public void testUnsubscribeWithoutSubscribe() {
		System.out
				.println("******************* [13] [STARTING] UNSUBSCRIBE-WITHOUT-SUBSCRIBE TEST ************************ ");
		// Create call-back
		EventCallbackImplOne mockEventCallbackImplOne = new EventCallbackImplOne();
		// Create EventService Object
		EventService eventService = AbstractEventService.createEventService();
		EventConsumerOne consumerOne = new EventConsumerOne();

		try {
			// unsubscribe from callbacks
			consumerOne.unSubscribe(eventService,
					Constants.TMT_MOBI_BLUE_FILTER, mockEventCallbackImplOne);
			fail("Consumers were still able to unsubscribe from the Topic whereas it did not subscribed to the Topic at all");

		} catch (Exception e) {
			System.out
					.println("PASS: Exception occured while unsubscribing | Consumer tried to unsubscribe to a Topic with a callback whereas it did not subscribed at all to that topic with that callback");
		}

		System.out
				.println("######################   UNSUBSCRIBE-WITHOUT-SUBSCRIBE TEST [END] ######################### ");

	}

}
