package org.tmt.csw.testsuite;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({ 
		UnsubscribeWithoutPost.class,
		UnsubscribeAllConsumerWithoutPost.class,
		UnsubscribeWithoutSubscribe.class, UnsubscribeAllWithoutSubscribe.class })

public class TestSuiteFour {

}

