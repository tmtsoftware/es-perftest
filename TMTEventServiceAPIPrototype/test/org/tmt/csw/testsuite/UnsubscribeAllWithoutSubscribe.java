package org.tmt.csw.testsuite;

import junit.framework.TestCase;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.tmt.csw.consumerone.EventConsumerOne;
import org.tmt.csw.consumertwo.EventConsumerTwo;
import org.tmt.csw.eventservice.AbstractEventService;
import org.tmt.csw.eventservice.EventService;
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
	 */
	@Test
	public void testUnsubscribeAllWithoutSubscribe() {
		System.out
				.println("******************* [14] [STARTING] UNSUBSCRIBE-ALL-CONSUMERS-WIHOUT-SUBSCRIPTION TEST ************************ ");
		// Create EventService Object
		EventService eventService = AbstractEventService.createEventService();
		EventConsumerOne consumerOne = new EventConsumerOne();
		EventConsumerTwo consumerTwo = new EventConsumerTwo();

		try {

			// unsubscribe consumer from Topic using unSubscribeAll
			consumerOne.unSubscribeAll(eventService,
					Constants.TMT_MOBI_BLUE_FILTER);
			consumerTwo.unSubscribeAll(eventService,
					Constants.TMT_MOBI_BLUE_FILTER);
			fail("Consumers were still able to unsubscribe from the Topic whereas it did not subscribed to the Topic at all");
		} catch (Exception e) {
			System.out
					.println("PASS: Exception occured while unsubscribing | Consumer tried to unsubscribe from topic without first subscribing");

		}

		System.out
				.println("######################   UNSUBSCRIBE-ALL-CONSUMERS-WIHOUT-SUBSCRIPTION TEST [END] ######################### ");

	}

}
