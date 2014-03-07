package org.tmt.csw.testsuite;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({ PostWithNoConsumer.class, PostWithOneConsumerOneCallbck.class,
		PostWithManyConsumer.class, PostWithOneConsumerManyCallbck.class,
		PostWithManyConsumerManyCallbck.class })


public class TestSuiteOne {

}

