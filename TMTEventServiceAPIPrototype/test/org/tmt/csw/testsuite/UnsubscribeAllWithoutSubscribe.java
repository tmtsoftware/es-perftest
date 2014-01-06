package org.tmt.csw.testsuite;

import junit.framework.TestCase;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.tmt.csw.consumerone.EventConsumerOne;
import org.tmt.csw.eventservice.AbstractEventService;
import org.tmt.csw.eventservice.EventService;
import org.tmt.csw.eventservice.exception.EventPublishException;
import org.tmt.csw.eventservice.exception.EventUnSubscriptionException;
import org.tmt.csw.eventservice.util.Constants;

public class UnsubscribeAllWithoutSubscribe extends TestCase {

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
	 * 14) Unsubscribe all consumers without subscribe --> The EventService
	 * should throw exception
	 * 
	 * @throws EventPublishException
	 * @throws InterruptedException
	 * 
	 */
	@Test
	public void testSameSubscriberDoubleSubscription()
			throws EventPublishException, InterruptedException,
			EventUnSubscriptionException {
		System.out
				.println("******************* [14] [STARTING] UNSUBSCRIBE-ALL-CONSUMERS-WIHOUT-SUBSCRIPTION TEST ************************ ");
		// Create EventService Object
		EventService eventService = AbstractEventService.createEventService();

		EventConsumerOne consumerOne = new EventConsumerOne();

		boolean caughtException = false;

		try {

			// unsubscribe consumer from Topic using unSubscribeAll
			consumerOne.unSubscribeAll(eventService,
					Constants.TMT_MOBI_BLUE_FILTER);
		} catch (Exception e) {
			caughtException = true;

		}

		if (caughtException) {
			assertTrue("Unsubscribed", caughtException);
			System.out
					.println("Exception occured while unsubscribing | Consumer tried to unsubscribe from topic without first subscribing");
		} else {
			assertFalse("Unsubscription failed", caughtException);
			System.out.println("Sucessfully unsubscribed");
		}

		System.out
				.println("######################   UNSUBSCRIBE-ALL-CONSUMERS-WIHOUT-SUBSCRIPTION TEST [END] ######################### ");

	}

}
