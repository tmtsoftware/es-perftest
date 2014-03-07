package org.tmt.csw.testsuite;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({ SubscribeOneConsumerWithoutPost.class,
		SubscribeManyConsumerWithoutPost.class,
		SameSubscriberDoubleSubscription.class
		 })


public class TestSuiteThree {

}

