#!/bin/bash
export CLASSPATH=$CLASSPATH:lib/hornetq-commons.jar:lib/hornetq-core-client.jar:lib/jnp-client.jar:lib/netty.jar:lib/TMTEventServicePrototype.jar:/config/configuration.properties

java -cp $CLASSPATH org.tmt.csw.consumerone.EventConsumerOne

